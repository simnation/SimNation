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

import java.util.IdentityHashMap;
import java.util.Map;

import org.simnation.agents.AbstractBasicAgent;
import org.simnation.agents.business.Demand;
import org.simnation.agents.business.Supply;
import org.simnation.agents.common.Batch;
import org.simnation.agents.firm.common.WarehouseStatistics;
import org.simnation.agents.market.GoodsMarketB2C;
import org.simnation.agents.market.Market;
import org.simnation.context.technology.Good;
import org.simnation.model.Model;
import org.simplesim.core.messaging.RoutedMessage;
import org.simplesim.core.scheduling.Time;

/**
 * Represents an enterprise that buys a single product at the B2B-market and
 * sells it to the local B2C-markets. Traders fulfill an important logistic and
 * allocation function.
 *
 */

public final class Trader extends AbstractBasicAgent<TraderState, Trader.EVENT> {

	enum EVENT {
		supplyMarket, orderStock
	}

	/*
	 * PLAN_LOGISTICS, BOOKING_INVOKED, DAY_STARTED, WEEK_ENDED, STOCKUP_TRIGGERED,
	 * PROCUREMNT_TRIGGERED, ASPIRATION_ADAPTATION_TRIGGERED,
	 * ACCOUNTING_PERIOD_ENDED, MACHINE_LIFETIME_ENDED;
	 */

	private final Map<int[],WarehouseStatistics> marketStatistics=new IdentityHashMap<>();
	
	public Trader(TraderDBS dbs) {
		super(dbs.convertToState());
		for (Market<?> market : Model.getInstance().getConsumableMarket()) 
			marketStatistics.put(market.getAddress(),new WarehouseStatistics());
		enqueueEvent(EVENT.supplyMarket,new Time(0,6,0));
	}

	@Override
	protected void handleEvent(EVENT event, Time time) {
		switch (event) {
		case orderStock:
			//long amount=getState().getStorage().calcReorderVolume(0.95f);
			// place order or schedule production
			log("\t reorder event");
			enqueueEvent(EVENT.orderStock,Time.MONTH);
			break;
		case supplyMarket:
			log("\t supply market event");
			sendSupplyToMarket();
			enqueueEvent(EVENT.supplyMarket,time.add(Time.DAY));
			break;
		default:
			throw new UnhandledEventType(event,this);
		}

	}

	@Override
	protected void handleMessage(RoutedMessage msg) { 
		if (msg.getContent().getClass()==Demand.class) {
			
		} else if (msg.getContent().getClass()==Supply.class) {
			int[] market=msg.getSource();
			Supply<?> supply=(Supply<?>) msg.getContent();
			log("\t received returned supply of "+supply.toString());
			if (supply.getQuantity()!=0) { // some items returned
				marketStatistics.get(market).update(supply.getQuantitySold(),false);
				getState().getStorage().addToStock((Batch) supply.getItem());
			} else marketStatistics.get(market).update(supply.getQuantitySold(),true); // sold out
			getState().getMoney().merge(supply.getMoney());
			log("\t money is now at "+getState().getMoney().toString());
		} else throw new UnhandledMessageType(msg,this);
	}

	private void sendSupplyToMarket() { 
		// send trading good to all markets
		for (GoodsMarketB2C market : Model.getInstance().getConsumableMarket()) {
			// estimate delivery volume based recent statistics
			WarehouseStatistics stat=marketStatistics.get(market.getAddress());
			long marketQuantity=stat.forecastDelivery(getState().getServiceLevel());
			// if there are no statistics yet, deliver an equal share of the current stock to each market
			if (marketQuantity==0) marketQuantity=getState().getStorage().getStockLevel()/marketStatistics.size();
			Batch batch=getState().getStorage().removeFromStock(marketQuantity);
			// supply price is the actual value plus a margin (cost plus approach)
			double price=batch.getPrice()*getState().getMargin();
			// send supply to market
			Supply<Good> supply=new Supply<>(getAddress(),batch,price);
			RoutedMessage msg=new RoutedMessage(getAddress(),market.getAddress(),supply);
			sendMessage(msg);
			log("\t"+stat.toString());
			log("\t send supply to market: "+supply.toString());
		}
	 }

	public String getName() {
		return "Trader";
	}

}
