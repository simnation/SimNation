package org.simnation.zzz_old;

import org.simnation.simulation.model.Good;

import static org.simnation.simulation.model.Time.DAY;
import static org.simnation.simulation.model.Time.HOUR;

import org.simnation.agents.AbstractBasicAgent;
import org.simnation.agents.business.Demand;
import org.simnation.agents.business.Supply;
import org.simnation.agents.common.Batch;
/**National goods market for trading between enterprises
 * 
 * @author Rene Kuhlemann
 *
 */
import org.simnation.core.*;


@SuppressWarnings("serial")
public final class GoodsMarketB2B extends Market<GoodsMarketB2BState,Good,Batch> {

	private enum EVENT implements AbstractBasicAgent.EventType {
		
		INVOKE_MARKET("Market invoked",2*HOUR,4*HOUR),
	    TRIGGER_EOB("End of business triggered",DAY,DAY);	    
		
		private String name;
	    private int offset,period;

	    EVENT(String n,int ofs, int per) { name=n; offset=ofs; period=per; }
	    public int offset() { return(offset); }
	    public int period() { return(period); }
	    public String toString() { return(name); }

	}

	private static final AgentType TYPE=AgentType.GOODS_MARKET_B2B;
	
	public GoodsMarketB2B() {
	    super(TYPE);
	    addEvent(EVENT.INVOKE_MARKET,EVENT.INVOKE_MARKET.offset());
	    addEvent(EVENT.TRIGGER_EOB,EVENT.TRIGGER_EOB.offset());
	}

	protected GoodsMarketB2BState createState() {
		return(new GoodsMarketB2BState());
	}

	@Override
	protected void deltaInternal() {
        EVENT event=dequeueCurrentEvent();
        switch(event) {
	        case TRIGGER_EOB:
	            for(Good good : getState().getTradedItems()) 
	            //    getState().getMarketSegment(good).broadcastMarketInfo(this);
	            addEvent(EVENT.TRIGGER_EOB,EVENT.TRIGGER_EOB.period());
	            break;
	        case INVOKE_MARKET:
	            processMessages();
	            addEvent(EVENT.INVOKE_MARKET,EVENT.INVOKE_MARKET.period());
	            break;
	        default: throw new UnhandledEventTypeException(this,event);
	    }
	}

	@Override
	void addDemand(Demand<Good> demand) {
		// TODO Auto-generated method stub
		
	}

	@Override
	void addSupply(Supply<Good, Batch> supply) {
		// TODO Auto-generated method stub
		
	}
	
}
