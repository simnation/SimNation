package org.simnation.zzz_old;

import static org.simnation.core.Time.DAY;
import static org.simnation.core.Time.HOUR;

import org.simnation.agents.AbstractBasicAgent;
import org.simnation.agents.business.Demand;
import org.simnation.agents.business.Money;
import org.simnation.agents.business.Supply;
import org.simnation.agents.common.Command;
import org.simnation.agents.common.Command.COMMAND;
import org.simnation.context.technology.Good;
import org.simnation.core.Address;
import org.simnation.core.AgentType;
import org.simnation.simulation.model.Root;

/**
 * Goods market for consumables directly sold to the households of each region. Enterprises sell their goods at the
 * local B2C market that is designed like a conventional grocery store: products of a certain price, package size and
 * quality are offered. Households issue their demand in respect of desired amount, max unit price and max overall
 * expense to the market agent that returns the best suited product in exchange for money.
 *
 * @author Rene Kuhlemann
 */

public final class GoodsMarketB2C extends Market<Good,GoodsMarketB2CState<Good>> {
	
	private static final AgentType TYPE=AgentType.GOODS_MARKET_B2C;

	private static final long serialVersionUID=2308361350028231503L;

	private enum EVENT implements AbstractBasicAgent.EventType {

		INVOKE_MARKET("Market invoked",2*HOUR,4*HOUR),
		TRIGGER_EOB("End of business triggered",DAY,DAY);

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

	public GoodsMarketB2C(int region) throws Exception {
		super(TYPE,region);
		for (final Good good : Root.getContext().getGoods().getConsumables())
			getState().setMarketSegment(good,new MarketSegmentB2C());
		addEvent(EVENT.INVOKE_MARKET,EVENT.INVOKE_MARKET.offset());
		addEvent(EVENT.TRIGGER_EOB,EVENT.TRIGGER_EOB.offset());

		
	}

	protected void handleEvent(final EventType e) {
		final EVENT event=(EVENT) e;
		switch (event) {
			case INVOKE_MARKET:
				processMessages();
				clearMarket(Root.getContext().getGoods().get());
				addEvent(EVENT.INVOKE_MARKET,EVENT.INVOKE_MARKET.period());
				break;
			case TRIGGER_EOB:
				clearDemands(Root.getContext().getGoods().get());
				// updateMarketInfo();
				addEvent(EVENT.TRIGGER_EOB,EVENT.TRIGGER_EOB.period());
				break;
			default:
				super.handleEvent(e);
		}
	}



	protected void handleCommand(final Command<?> command) {
		if (command.getCommand()==COMMAND.B2C_SUBSCRIBE) {
			final Money money=(Money) command.getDetail();
			getState().getDeposit().put(command.getAddress(),money);
			log("b2c market subscription received from "+command.getAddress().toString());
		} else if (command.getCommand()==COMMAND.B2C_UNSUBSCRIBE) {
			getState().getDeposit().remove(command.getAddress());
		} else super.handleCommand(command);
	}

	public void transferMoney(Address addr,Money cash,int amount) {
		getState().getDeposit().get(addr).transfer(cash,amount);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see simnation.agents.market.Market#addDemand(simnation.business.Demand)
	 */
	void addDemand(Demand<Good> demand) {
		getState().getMarketSegment(demand.getMarketSegmentSelector()).addDemand(demand);
		log("received demand: "+demand.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simnation.agents.market.Market#addSupply(simnation.business.Supply)
	 */
	void addSupply(Supply<Good> supply) {
		getState().getMarketSegment(supply.getMarketSegmentSelector()).addSupply(supply);
		log("received supply: "+supply.toString());
	}

	protected GoodsMarketB2CState<Good> createState() {
		return new GoodsMarketB2CState<Good>();
	}



}
