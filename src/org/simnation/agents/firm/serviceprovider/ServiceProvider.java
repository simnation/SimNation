package org.simnation.agents.firm.serviceprovider;



import org.simnation.agents.firm.Enterprise;
import org.simnation.core.AgentType;
import org.simnation.simulation.scenario.ServiceProviderStateReader;



/**
 * @author Rene Kuhlemann
 *
 */
public final class ServiceProvider extends Enterprise<ServiceProviderState> {
	
    private static final long serialVersionUID = 4019727098897397688L;
    
    private static final AgentType TYPE=AgentType.SERVICE_PROVIDER;

    public ServiceProvider(ServiceProviderStateReader spsr,short region) throws Exception {	
		super(TYPE,region,spsr.getEnterpriseStateReader());

	}

	protected ServiceProviderState createState() {
		return(new ServiceProviderState());	
	}

	@Override
	protected void deltaInternal() {
	
	}
	
}
