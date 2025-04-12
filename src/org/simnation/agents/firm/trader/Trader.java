/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable
 * and used JSimpleSim as technical backbone for concurrent discrete event simulation.
 * 
 * This software is published as open source and licensed under GNU GPLv3.
 * 
 * Contributors:
 * 	- Rene Kuhlemann - development and initial implementation
 * 
 */
package org.simnation.agents.firm.trader;

import java.util.HashMap;
import java.util.Map;

import org.simnation.agents.AbstractBasicAgent;
import org.simnation.agents.business.Demand;
import org.simnation.agents.business.Supply;
import org.simnation.agents.market.GoodsMarketB2C;
import org.simnation.common.Batch;
import org.simnation.common.statistics.ExponentialSmoothingStatistics;
import org.simnation.common.statistics.Statistics;
import org.simnation.context.technology.Good;
import org.simnation.model.Model;
import org.simplesim.core.messaging.RoutingMessage;
import org.simplesim.core.scheduling.Time;

/**
 * Represents an enterprise that buys a single product at the B2B-market and
 * sells it to the local B2C-markets. Traders fulfill an important logistic and
 * allocation function.
 *
 */

public final class Trader extends AbstractBasicAgent<TraderState, Trader.EVENT> {

	private static final Time TRADER_OFFSET=new Time(1); // be the first agent to start
	private static final Time TRADER_PERIOD=Time.DAY;  // deliver daily to market

	enum EVENT {
		supplyMarket, orderStock
	}

	/*
	 * PLAN_LOGISTICS, BOOKING_INVOKED, DAY_STARTED, WEEK_ENDED, STOCKUP_TRIGGERED,
	 * PROCUREMNT_TRIGGERED, ASPIRATION_ADAPTATION_TRIGGERED,
	 * ACCOUNTING_PERIOD_ENDED, MACHINE_LIFETIME_ENDED;
	 */

	private final Map<int[], Statistics> salesVolume=new HashMap<>();
	private final Map<int[], Statistics> salesTurnover=new HashMap<>();

	public Trader(TraderDBS dbs) {
		super(new TraderState());
		dbs.convertToState(getState());
		for (GoodsMarketB2C market : Model.getInstance().getB2CMarketSet()) {
			salesVolume.put(market.getAddress(),new ExponentialSmoothingStatistics());
			salesTurnover.put(market.getAddress(),new ExponentialSmoothingStatistics());
		}
		enqueueEvent(EVENT.supplyMarket,TRADER_OFFSET); // start simulation with posting offers to market
	}

	@Override
	protected void handleEvent(EVENT event, Time time) {
		switch (event) {
		case orderStock:
			//long amount=getState().getStorage().calcReorderVolume(0.95f);
			// place order or schedule production
			log("\t reorder event");
			enqueueEvent(EVENT.orderStock,time.add(Time.MONTH));
			break;
		case supplyMarket:
			log("\t supply market event");
			sendSupplyToMarket();
			enqueueEvent(EVENT.supplyMarket,time.add(TRADER_PERIOD));
			break;
		default:
			throw new UnhandledEventType(event,this);
		}

	}

	@Override
	protected void handleMessage(RoutingMessage msg) {
		if (msg.getContent().getClass()==Demand.class) {

		} else if (msg.getContent().getClass()==Supply.class) {
			final int[] market=msg.getSource();
			final Supply<?> supply=(Supply<?>) msg.getContent();
			log("\t received returned supply of "+supply.toString());
			salesVolume.get(market).update(supply.getQuantitySold());
			getState().getStorage().addToStock((Batch) supply.getItem());
			salesTurnover.get(market).update(supply.getMoney().getValue());
			getState().getMoney().merge(supply.getMoney());
			log("\t money is now at "+getState().getMoney().toString());
		} else throw new UnhandledMessageType(msg,this);
	}

	private void sendSupplyToMarket() {
		// send trading good to all markets
		for (GoodsMarketB2C market : Model.getInstance().getB2CMarketSet()) {
			final Statistics statistics=salesVolume.get(market.getAddress());
			// estimate delivery volume as average sales volume plus standard deviation
			double quantity=statistics.getAVG()+Math.sqrt(statistics.getVAR());
			// if there are no statistics yet, deliver an equal share of the current stock to each market
			if (quantity==0) quantity=getState().getStorage().getStockLevel()/Model.getInstance().getRegionSet().size();
			final Batch batch=getState().getStorage().removeFromStock((long) quantity);
			// supply price is the actual value plus a margin (cost plus approach)
			final double price=batch.getPrice()*getState().getMargin();
			// send supply to market
			final Supply<Good> supply=new Supply<>(getAddress(),batch,price);
			final RoutingMessage msg=new RoutingMessage(getAddress(),market.getAddress(),supply);
			sendMessage(msg);
			//log("\t"+stat.toString());
			log("\t send supply to market: "+supply.toString());
		}
	}

	@Override
	public String getName() { return "Trader"; }

}