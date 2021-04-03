package org.simnation.agents.firm.manufacturer;

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
import org.simnation.context.technology.Precursor;
import org.simnation.core.AgentType;
import org.simnation.core.Machine;
import org.simnation.core.Message;
import org.simnation.core.Time;
import org.simnation.persistence.ManufacturerDBS;
import org.simnation.simulation.model.Root;

/**
 * Represents an enterprise that produces a single product and sells it a the B2B-market
 *
 * @author Martin Sanski, Rene Kuhlemann
 *
 */

public final class Manufacturer extends Enterprise<ManufacturerState> {

	private static final long serialVersionUID=1865170090576509272L;

	private enum EVENT implements AbstractBasicAgent.EventType {

		PRODUCTION_FINISHED("Production finished",0,0),
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

	private static final AgentType TYPE=AgentType.MANUFACTURER;

	public Manufacturer(ManufacturerDBS dbs,int region,int id) throws Exception {
		super(dbs,TYPE,region,id);
		final Good good=dbs.getGood();
		// init machine - HAS TO BE DONE FIRST, since other departments depend on it!
		getState().setMachine(new Machine(good,dbs.getPackageSize(),dbs.getCapacity()));
		// init other departments
		// getState().setProcurement(new Procurement(this));
		// getState().setProduction(new Production(this));
		// getState().setSales(new Sales(this));
		// init storage for output good and precursors
		getState().getWarehouse().setOutputStorage(new BulkStorage(good));
		for (final Precursor precursor : good.getPrecursors()) {
			getState().getWarehouse().addInputStorage(new BulkStorage(precursor.getGood()));
		}
		final Batch batch=new Batch(getState().getMachine().getProduct(),dbs.getInitAmount(),dbs.getInitPrice(),dbs.getInitQuality());
		getState().getWarehouse().putOnStock(batch);
		/*
		 * // calc production time
		 * getState().setProductionTime(Tools.minimal(Tools.roundUp(getState().getMachine().getMakespan
		 * ()),Time.MIN_PRODUCTION_PERIOD));
		 * 
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
			case PRODUCTION_FINISHED:
				getState().getProduction().storeOutput();
				final int output=getState().getWarehouse().getOutputStorage().calcOrderVolume(getState().getServiceLevel(),getState().getTime());
				getState().getProduction().order(output);
				addEvent(EVENT.PRODUCTION_FINISHED,getState().getProductionTime());
				break;
			case STOCKUP_TRIGGERED:
				final Inventory store=getState().getWarehouse().getOutputStorage();
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
		final Batch batch=getStorage().removeFromStock(amount);
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

	Good getGood() {
		return getState().getMachine().getGood();
	}
	
	private Inventory getStorage() {
		return getState().getWarehouse().getOutputStorage();
	}
	
	protected ManufacturerState createState() {
		return new ManufacturerState();
	}



}
