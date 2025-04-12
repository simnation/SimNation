package org.simnation.zzz_old;

/**
 * Labor market
 *
 *
 * @author Rene Kuhlemann
 *
 */

import static org.simnation.core.Time.DAY;
import static org.simnation.core.Time.HOUR;

import java.util.Arrays;

import org.simnation.agents.AbstractBasicAgent;
import org.simnation.agents.business.Demand;
import org.simnation.agents.business.Supply;
import org.simnation.common.Command;
import org.simnation.core.AgentType;
import org.simnation.core.SkillDefinition;

public class LaborMarket extends Market<SkillDefinition,LaborMarketState<SkillDefinition>> {

	private static final long serialVersionUID=8084835854462786199L;
	
	private static final AgentType TYPE=AgentType.LABOR_MARKET;

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

	public LaborMarket(short region) {
		super(TYPE,region);
		for (final SkillDefinition skill : SkillDefinition.values())
			getState().setMarketSegment(skill,new MarketSegmentLabor());
		addEvent(EVENT.INVOKE_MARKET,EVENT.INVOKE_MARKET.offset());
		addEvent(EVENT.TRIGGER_EOB,EVENT.TRIGGER_EOB.offset());
		// addEvent(SEND_MARKETINFO,SEND_MARKETINFO.offset());
	}

	protected void handleEvent(final EventType e) {
		final EVENT event=(EVENT) e;
		switch (event) {
			case INVOKE_MARKET:
				processMessages();
				clearMarket(Arrays.asList(SkillDefinition.values()));
				addEvent(EVENT.INVOKE_MARKET,EVENT.INVOKE_MARKET.period());
				break;
			case TRIGGER_EOB:
				clearDemands(Arrays.asList(SkillDefinition.values()));
				// updateMarketInfo();
				addEvent(EVENT.TRIGGER_EOB,EVENT.TRIGGER_EOB.period());
				break;
			default:
				super.handleEvent(e);
		}
	}

	protected void handleCommand(final Command<?> command) {
		super.handleCommand(command);
	}

	void addDemand(Demand<SkillDefinition> demand) {
		// TODO Auto-generated method stub

	}

	void addSupply(Supply<SkillDefinition> supply) {
		// TODO Auto-generated method stub

	}

	protected LaborMarketState<SkillDefinition> createState() {
		return new LaborMarketState<SkillDefinition>();
	}

}
