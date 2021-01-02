package org.simnation.old;

// James

import static org.simnation.core.AgentType.GOODS_MARKET_B2C;
import static org.simnation.core.AgentType.LABOR_MARKET;
import static org.simnation.core.AgentType.REGIONAL_NODE;

import java.util.ArrayList;

import model.devscore.ports.IPort;
import model.devscore.ports.Port;

import org.simnation.core.Address;
import org.simnation.core.AgentType;
import org.simnation.core.Message;
// SimNation
import org.simnation.core.Time;
import org.simnation.simulation.agents.AbstractBasicAgent;

// Java

/**
 * The regional node routes messages to the nations or the regional objects.
 *
 * @author Rene Kuhlemann
 */

final class RegionalNode extends AbstractBasicAgent<RegionalNodeState> {

	private enum EVENT implements EventType {
		INVOKE_REGION("National node invoked",1*Time.HOUR,2*Time.HOUR);

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

	private static final long serialVersionUID=-4439230393757871959L;

	private static final AgentType TYPE=REGIONAL_NODE;

	private final IPort b2c_market_port,labor_market_port;
	private final ArrayList<IPort> household=new ArrayList<>();
	private final ArrayList<IPort> enterprise=new ArrayList<>();

	public RegionalNode(int region) throws Exception {
		super(TYPE,region);
		log("init region id "+region);
		// getState().setAddress(new Address(AgentType.REGIONAL_NODE,region,Address.NOID));
		// use standard outport for communication with nation node and
		// add all other necessary ports
		b2c_market_port=newOutport(GOODS_MARKET_B2C);
		labor_market_port=newOutport(LABOR_MARKET);

		// Household and Enterprise ports were added before by the enclosing model

		// initial event to start
		addEvent(EVENT.INVOKE_REGION,EVENT.INVOKE_REGION.offset());
	}

	protected RegionalNodeState createState() {
		return new RegionalNodeState();
	}

	protected void deltaInternal() {
		requeueCurrentEvent(EVENT.INVOKE_REGION.period());
	}

	@SuppressWarnings("serial")
	void addHouseholdPorts(int count) {
		household.clear();
		household.ensureCapacity(count);
		for (int i=0; i<count; i++) {
			final int id=i;
			final Port port= new Port(null,Message.class) {
				@Override 		// save memory for lots of redundant strings by these classes
				public String getName() {
					return "toHH"+id;
				}	
			};
			addOutPort(port);
			household.add(port);
		}			
	}

	@SuppressWarnings("serial")
	void addEnterprisePorts(int count) {
		enterprise.clear();
		enterprise.ensureCapacity(count);
		for (int i=0; i<count; i++) {
			final int id=i;
			final Port port= new Port(null,Message.class) {
				@Override 		// save memory for lots of redundant strings by these classes
				public String getName() {
					return "toEP"+id;
				}	
			};
			addOutPort(port);
			enterprise.add(port);
		}			
	}

	IPort getOutport(Address addr) {
		if (addr.getRegion()!=getAddress().getRegion()) return getOutport(); // message to another node
		switch (addr.getAgentType()) { // message to this region
			case GOODS_MARKET_B2C:
				return b2c_market_port;
			case LABOR_MARKET:
				return labor_market_port;
			case HOUSEHOLD:
				return household.get(addr.getId());	
			case SERVICE_PROVIDER:
			case TRADER:
			case MANUFACTURER:
			case BANK:
				return enterprise.get(addr.getId());
			default:
				return null;
		}
	}

	/*
	@Override
	protected void lambda() {
		while (getState().getIncomingMessageList().isEmpty()==false) {
			final Message<?> msg=getState().getIncomingMessageList().remove(0);
			final IPort outport=getOutport(msg.getDest());
			// check if valid port could be found
			if (outport==null) throw new InvalidMessageDestinationException(msg);
			// this.log("Message FROM "+msg.getSrc().toString()+" TO "+msg.getDest().toString()+" diverted");
			outport.write(msg);
		}
	}*/
	
	
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
	

	private IPort newOutport(AgentType type) {
		final Port port=new Port("to"+type.getTag(),Message.class);
		addOutPort(port);
		return port;
	}

}
