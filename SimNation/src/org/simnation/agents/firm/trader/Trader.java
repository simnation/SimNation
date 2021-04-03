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
import org.simnation.agents.business.Money;
import org.simnation.agents.business.Supply;
import org.simnation.agents.common.Batch;
import org.simnation.agents.common.Command;
import org.simnation.agents.common.Command.COMMAND;
import org.simnation.agents.firm.Enterprise;
import org.simnation.agents.firm.common.BulkStorage;
import org.simnation.agents.firm.common.Inventory;
import org.simnation.agents.market.MarketInfo;
import org.simnation.context.technology.Good;
import org.simnation.core.AgentType;
import org.simnation.core.Message;
import org.simnation.core.Time;
import org.simnation.model.persistence.TraderDBS;
import org.simnation.simulation.model.Root;
import org.simplesim.core.messaging.RoutedMessage;


/**
 * Represents an enterprise that buy a single product at the B2B-market and sells it to the local B2C-markets. So, it
 * has an important logistic and allocation function.
 *
 */

public final class Trader extends AbstractBasicAgent<TraderState,Trader.EVENT> {


	 enum EVENT {

		PLAN_LOGISTICS,
		BOOKING_INVOKED,
		DAY_STARTED,
		WEEK_ENDED,
		STOCKUP_TRIGGERED;

		/*
		 * PROCUREMNT_TRIGGERED, ASPIRATION_ADAPTATION_TRIGGERED, ACCOUNTING_PERIOD_ENDED, MACHINE_LIFETIME_ENDED;
		 */

	}

	public Trader(TraderDBS dbs,int region,int id) throws Exception {
		super(dbs,TYPE,region,id);
		final Good good=dbs.getGood();

		// init other departments
		// getState().setProcurement(new Procurement(this));
		
		// getState().setSales(new Sales(this));
		getWarehouse().setOutputStorage(new BulkStorage(good));
		initInventory(good,dbs.getInitAmount(),dbs.getInitPrice(),dbs.getInitQuality());
		/*
		 * // subscribe to relevant B2B market list to get market info sendMessage(new
		 * Message<Command<Address>>(getAddress(),GoodsMarketB2B.getStaticAddress(), new
		 * Command<Address>(COMMAND.SUBSCRIBE,getAddress())));
		 */
		// subscribe to local b2c market
		final Command<Money> subscription=new Command<Money>(COMMAND.B2C_SUBSCRIBE,getAddress(),getState().getCash());
		final Message<Command<Money>> msg=new Message<>(getAddress(),getB2CMarketAddr(),subscription);
		sendMessage(msg);

		addEvent(EVENT.DAY_STARTED,EVENT.DAY_STARTED.offset());

	}
	
	protected void handleEvent(final EVENT event) {
		switch (event) {			
			case STOCKUP_TRIGGERED:
				final Inventory store=getState().getStorage();
				final int plan=store.calcOrderVolume(getState().getServiceLevel(),getState().getTime());
				getState().getProduction().order(plan);
				// tprod ist f�r ein Gut konstant -> in State abspeichern!!!
				// production finished event?
				// reorderlevelreached --> reschedule STOCKUP-event to immediately
				addEvent(EVENT.STOCKUP_TRIGGERED,store.getReorderPeriodLength());
				break;
			case DAY_STARTED:
				sendBatchToMarket(100);
				//processMessages();
				addEvent(EVENT.DAY_STARTED,EVENT.DAY_STARTED.offset());
				break;

			default:
				super.handleEvent(event);
		}

	}
	
	private void sendBatchToMarket(int amount) {
		final Batch batch=getWarehouse().removeFromStock(amount);
		final Supply<Good> supply=new Supply<Good>(getAddress(),batch);
		final Message<?> msg=new Message<Supply<Good>>(getAddress(),Root.getPubInfo().getGoodsMarketAddr(getRegion()),supply);
		log("Sending supply to market "+supply.toString());
		sendMessage(msg);		
	}

	protected void handleMessage(final Message<?> msg) {
			if (msg.getContent().getClass()==MarketInfo.class) getState().getSales().handleMarketInfo((MarketInfo) msg.getContent());
			/*
			 * } else if (msg.getContent().getClass()==Demand.class) {
			 * getState().getSales().handleInquiry((Demand<Good>) msg.getContent()); } else if
			 * (msg.getContent().getClass()==Offer.class) { getState().getProcurement().handleOffer((Offer<Good>)
			 * msg.getContent()); } else if (msg.getContent().getClass()==Order.class) {
			 * getState().getSales().handleOrder((Order<Batch>) msg.getContent()); } else if
			 * (msg.getContent().getClass()==Supply.class) { getState().getProcurement().handleDelivery((Supply<Good>)
			 * msg.getContent());
			 */
			else super.handleMessage(msg);
		
	}

	@Override
	protected void handleMessage(RoutedMessage msg) { // TODO Auto-generated method stub
	 }

	@Override
	protected void handleEvent(EVENT event, Time time) {
		// TODO Auto-generated method stub
		
	}

}
