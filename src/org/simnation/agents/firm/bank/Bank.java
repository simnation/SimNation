package org.simnation.agents.firm.bank;


import org.simnation.agents.firm.Enterprise;
import org.simnation.core.AgentType;
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
