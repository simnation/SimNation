package org.simnation.agents.firm.trader;

import org.simnation.agents.AbstractBasicAgent;
import org.simnation.agents.business.Money;
import org.simnation.agents.business.Supply;
import org.simnation.agents.common.Batch;
import org.simnation.agents.common.Command;
import org.simnation.agents.common.Command.COMMAND;
import org.simnation.agents.firm.Enterprise;
import org.simnation.agents.firm.common.BulkStorage;
import org.simnation.agents.firm.common.Storage;
import org.simnation.agents.market.MarketInfo;
import org.simnation.context.technology.Good;
import org.simnation.core.AgentType;
import org.simnation.core.Message;
import org.simnation.core.Time;
import org.simnation.model.persistence.TraderDBS;
import org.simnation.simulation.model.Root;


/**
 * Represents an enterprise that buy a single product at the B2B-market and sells it to the local B2C-markets. So, it
 * has an important logistic and allocation function.
 *
 * @author Rene Kuhlemann
 *
 */

public final class Trader extends Enterprise<TraderState> {

	private static final long serialVersionUID=4019727098897397688L;

	private static final AgentType TYPE=AgentType.TRADER;

	private enum EVENT implements AbstractBasicAgent.EventType {

		PLAN_LOGISTICS("Plan logistic and send deliveries to local markets",0,0),
		BOOKING_INVOKED("Check payments",Time.DAY,Time.DAY),
		DAY_STARTED("New day started",8*Time.HOUR,Time.DAY),
		WEEK_ENDED("Working week ended",0,Time.WEEK),
		STOCKUP_TRIGGERED("Reorder period",0,Time.CHANGING);

		/*
		 * PROCUREMNT_TRIGGERED, ASPIRATION_ADAPTATION_TRIGGERED, ACCOUNTING_PERIOD_ENDED, MACHINE_LIFETIME_ENDED;
		 */

		private String name;
		private int offset,period;

		EVENT(String n,int ofs,int per) {
			name=n;
			offset=ofs;
			period=per;
		}

		public int offset() {
			return offset;
		}

		public int period() {
			return period;
		}

		public String toString() {
			return name;
		}

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
				final Storage store=getState().getStorage();
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


	protected TraderState createState() {
		return new TraderState();
	}

}
