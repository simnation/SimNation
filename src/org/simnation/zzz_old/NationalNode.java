package org.simnation.zzz_old;

// James
// Simnation
import static org.simnation.core.Address.NATION_NODE;
import static org.simnation.core.AgentType.CAPITAL_MARKET;
import static org.simnation.core.AgentType.CENTRAL_BANK;
import static org.simnation.core.AgentType.CREDIT_MARKET;
import static org.simnation.core.AgentType.GOODS_MARKET_B2B;
import static org.simnation.core.AgentType.GOVERNMENT;
import static org.simnation.core.AgentType.MONEY_MARKET;
import static org.simnation.core.AgentType.REGIONAL_NODE;



// Java
import java.util.ArrayList;
import java.util.EnumMap;

import model.devscore.ports.IPort;

import org.simnation.agents.AbstractBasicAgent;
import org.simnation.core.Address;
import org.simnation.core.AgentType;
import org.simnation.core.Message;
import org.simnation.core.Time;

final class NationalNode extends AbstractBasicAgent<NationalNodeState> {

	private static final long serialVersionUID=-3303568674840129850L;

	private enum EVENT implements EventType {
		INVOKE_NATION("National node invoked",1*Time.HOUR,2*Time.HOUR);

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
	
	private static final AgentType TYPE=AgentType.NATIONAL_NODE;

	// class variables not in state, because the state is not initialized by the scenario
	private final ArrayList<IPort> region;
	private final EnumMap<AgentType,IPort> outport;

	public NationalNode(int regions) {
		super(TYPE);
		getState().setAddress(new Address(AgentType.NATIONAL_NODE,NATION_NODE,Address.NOID));
		region=new ArrayList<IPort>(regions);
		outport=new EnumMap<AgentType,IPort>(AgentType.class);

		// remove default outport and set up outports to all national institutions and markets...
		removeOutPort(getOutport());
		addOutport(GOVERNMENT,NATION_NODE);
		addOutport(CENTRAL_BANK,NATION_NODE);
		addOutport(GOODS_MARKET_B2B,NATION_NODE);
		addOutport(MONEY_MARKET,NATION_NODE);
		addOutport(CAPITAL_MARKET,NATION_NODE);
		addOutport(CREDIT_MARKET,NATION_NODE);

		// set up one communication channels to each region
		for (int i=0; i<regions; i++)
			addOutport(REGIONAL_NODE,i);
		addEvent(EVENT.INVOKE_NATION,EVENT.INVOKE_NATION.offset());
	}

	private void addOutport(AgentType type,int node) {
		if (node==NATION_NODE) outport.put(type,addOutPort("to_"+type.toString(),Message.class));
		else region.add(node,addOutPort("to_"+type.toString()+node,Message.class));
	}

	protected NationalNodeState createState() {
		return new NationalNodeState();
	}

	protected final void deltaInternal() {
		requeueCurrentEvent(EVENT.INVOKE_NATION.period());
	}

	IPort getOutport(Address addr) {
		final int node=addr.getRegion();
		if (node==NATION_NODE) return outport.get(addr.getAgentType());
		if (node<0||node>=region.size()) throw new IndexOutOfBoundsException("Illegal node index: "+node);
		return region.get(node);
	}

	/*
	 * Diverts messages to the right agent within this region or to the nation node, thus overrides default
	 * implementation in class {@link Agent}
	 */
	@Override
	protected final void lambda() {
		while (hasMessage()) {
			final Message<?> msg=getMessage();
			final IPort outport=getOutport(msg.getDest());
			// check if a valid port could be found
			if (outport==null) throw new RuntimeException(Time.toString(getTime())+getAddress().toString()+
					" could not find destination"+msg.getDest().toString()); 
			outport.write(msg); 
		}
	}

}
