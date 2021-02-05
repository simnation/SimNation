/**
 * 
 */
package org.simnation.agents.government;


import org.simnation.simulation.scenario.DataAccessObject;

import static org.simnation.agents.AbstractBasicAgent.Type.GOVERNMENT;
import static org.simnation.simulation.model.Time.YEAR;

import org.simnation.agents.AbstractBasicAgent;



/**
 * @author rene
 *
 */
@SuppressWarnings("serial")
public final class Government extends AbstractBasicAgent<GovernmentState> {
	
	private enum EVENT implements AbstractBasicAgent.EventType {
		
		DO_NOTHING("Wait!",100*YEAR,100*YEAR);
		
		private String name;
	    private int offset,period;

	    EVENT(String n,int ofs, int per) { name=n; offset=ofs; period=per; }
	    public int offset() { return(offset); }
	    public int period() { return(period); }
	    public String toString() { return(name); }

	}

	public Government(DataAccessObject scn) {
		super(GOVERNMENT);
		addEvent(EVENT.DO_NOTHING,EVENT.DO_NOTHING.offset());
	}
	
	
	public GovernmentState createState(){
		return(new GovernmentState());
	}
	
	@Override
	protected void deltaInternal() {
	    dequeueCurrentEvent();
	    addEvent(EVENT.DO_NOTHING,EVENT.DO_NOTHING.period());
	}

}
