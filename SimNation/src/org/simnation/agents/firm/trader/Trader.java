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

import org.simnation.agents.AbstractBasicAgent;
import org.simnation.agents.business.Demand;
import org.simnation.agents.business.Supply;
import org.simnation.agents.common.Batch;
import org.simnation.agents.market.GoodsMarketB2C;
import org.simnation.context.technology.Good;
import org.simnation.model.Model;
import org.simplesim.core.messaging.RoutedMessage;
import org.simplesim.core.scheduling.Time;

/**
 * Represents an enterprise that buy a single product at the B2B-market and
 * sells it to the local B2C-markets. So, it has an important logistic and
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

	public Trader(TraderDBS dbs) {
		super(dbs.convertToState());
		enqueueEvent(EVENT.supplyMarket,Time.HOUR);

	}

	@Override
	protected void handleEvent(EVENT event, Time time) {
		switch (event) {
		case orderStock:
			//long amount=getState().getStorage().calcReorderVolume(0.95f);
			// place order or schedule production
			log(time,"reorder event");
			enqueueEvent(EVENT.orderStock,Time.MONTH);
			break;
		case supplyMarket:
			log(time,"supply market event");
			sendSupplyToMarket();
			enqueueEvent(EVENT.supplyMarket,time.add(time.DAY));
			break;
		default:
			throw new UnhandledEventType(event,this);
		}

	}

	private void sendSupplyToMarket() { 
		for (GoodsMarketB2C market : Model.getInstance().getConsumableMarket()) {
			Batch batch=getState().getStorage().removeFromStock(100);
			log(Time.ZERO,"send to market: "+batch.toString());
			double price=getState().getMargin()*batch.getPrice();
			Supply<Good> supply=new Supply<>(getAddress(),batch,price);
			RoutedMessage msg=new RoutedMessage(getAddress(),market.getAddress(),supply);
			sendMessage(msg);
		}
		
	 }
	
	private Good getMarketSegment() {
		return getState().getStorage().getGood();
	}

	/*
	 * private void sendBatchToMarket(int amount) { final Batch
	 * batch=getWarehouse().removeFromStock(amount); final Supply<Good> supply=new
	 * Supply<Good>(getAddress(),batch); final Message<?> msg=new
	 * Message<Supply<Good>>(getAddress(),Root.getPubInfo().getGoodsMarketAddr(
	 * getRegion()),supply); log("Sending supply to market "+supply.toString());
	 * sendMessage(msg); }
	 */

	@Override
	protected void handleMessage(RoutedMessage msg) { // TODO Auto-generated method stub
		if (msg.getContent().getClass()==Demand.class) {
			
		} else if (msg.getContent().getClass()==Supply.class) {
			Batch batch=(Batch) ((Supply) msg.getContent()).getItem();
			log(Time.ZERO,"reveived batch of "+batch.toString());
			getState().getStorage().restock(batch);
		} else throw new UnhandledMessageType(msg,this);
	}
	
	public String getName() {
		return "Trader";
	}

}
