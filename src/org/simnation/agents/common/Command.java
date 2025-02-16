package org.simnation.agents.common;

import org.simnation.core.Address;

public final class Command<T> {
    
    public enum COMMAND { 
    	B2C_SUBSCRIBE,		// enterprise subscribes to b2c-market and sends its money object for direct cash transfer 
    	B2C_UNSUBSCRIBE
    }
    
    private final COMMAND command;
    private final Address address;
    private final T detail;
    
    public Command(COMMAND com, Address addr, T d) {
    	command=com;
    	address=addr;
        detail=d;        
    }
    
    public Command(COMMAND com, Address addr) {
    	this(com,addr,null);
    }
    
    
    
    public COMMAND getCommand() {
        return(command);
    }

    public T getDetail() {
        return(detail);
    }

	public Address getAddress() {
		return address;
	}

}
