package org.simnation.agents.centralbank;

import org.simnation.simulation.scenario.DataAccessObject;

import static org.simnation.agents.AbstractBasicAgent.Type.CENTRAL_BANK;
import static org.simnation.simulation.model.Time.YEAR;

import org.simnation.agents.AbstractBasicAgent;

/**Central bank
 * 
 * @author Rene Kuhlemann
 *
 */
@SuppressWarnings("serial")
public final class CentralBank extends AbstractBasicAgent<CentralBankState> {
	
	private enum EVENT implements AbstractBasicAgent.EventType {
		
		DO_NOTHING("Wait!",100*YEAR,100*YEAR);
		
		private String name;
	    private int offset,period;

	    EVENT(String n,int ofs, int per) { name=n; offset=ofs; period=per; }
	    public int offset() { return(offset); }
	    public int period() { return(period); }
	    public String toString() { return(name); }

	}
	/*public enum MM_SEGMENT implements Marketable<MM_SEGMENT> {
	        OPEN_MARKET_OPERATION, MARGINAL_LENDING_FACILITY, REDISOUNT_QUOTA;
	        public MM_SEGMENT getSegment() { return(this); }   
	}*/

	public CentralBank(DataAccessObject scn) {
		super(CENTRAL_BANK);
		addEvent(EVENT.DO_NOTHING,EVENT.DO_NOTHING.offset());
	}
	
	
	public CentralBankState createState(){
		return(new CentralBankState());
	}

	@Override
	protected void deltaInternal() {
	    dequeueCurrentEvent();
	    addEvent(EVENT.DO_NOTHING,EVENT.DO_NOTHING.period());
	}

}
