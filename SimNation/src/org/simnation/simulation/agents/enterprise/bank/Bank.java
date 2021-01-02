package org.simnation.simulation.agents.enterprise.bank;


import org.simnation.core.AgentType;
import org.simnation.simulation.agents.enterprise.Enterprise;
import org.simnation.simulation.scenario.BankStateReader;


/**
 * @author Rene Kuhlemann
 *
 */
public final class Bank extends Enterprise<BankState> {
	
    private static final long serialVersionUID = 4019727098897397688L;
    
    private static final AgentType TYPE=AgentType.BANK;

    public Bank(BankStateReader bsr,short region) throws Exception {
		super(TYPE,region,bsr.getEnterpriseStateReader());

	}

	@Override
	protected BankState createState() {
		return(new BankState());
		
	}

	@Override
	protected void deltaInternal() {
	
	}
	
}
