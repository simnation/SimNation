package org.simnation.agents.firm.common;

import org.simnation.agents.business.ContractEmployment;
import org.simnation.agents.business.IRemittance;
import org.simnation.agents.business.Transfer;
import org.simnation.agents.firm.Enterprise;
import org.simnation.common.Labor;
import org.simnation.core.Address;
import org.simnation.core.AgentType;
import org.simnation.core.Message;
import org.simnation.core.SkillDefinition;
import org.simnation.core.Tools;
import org.simnation.simulation.model.Time;


/**Takes responsibility for the complete management of the {@link Staff}
 * This means, {@link Staffing} takes account of the payroll, payment of wages and manages the
 * available amount of man hours (recruiting efforts and lay-offs).
 * 
 * @author Rene Kuhlemann
 *
 */

public final class Staffing {
    
    private final Enterprise<?> parent;
    private float workload=1;
    private double total_wt=0;
	private double timestamp;
	
	public Staffing(Enterprise<?> enterprise) {
		parent=enterprise;
        timestamp=parent.getState().getTime();
	}
	
	/** Tries to allocate the requested amount of working power for the given production time
	 * accprding to staffing restrictions
	 * 
	 * @param labor - requested amount of labor 
	 * @param tprod - production time
	 * @return available amount of labor
	 */
	public Labor requestLabor(int labor, int tprod, SkillDefinition skill) {
	    Staff staff=parent.getState().getStaff(skill);
	    // add used manhours up to now to statistics and set timestamp to current time
	    total_wt+=(parent.getState().getTime()-timestamp)*staff.getUsedManhours();
	    timestamp=parent.getState().getTime();
	    // unit of labot here is MINUTES, in Labor.class it is HOURS!
	    // calc if ordered amount of labor is available within in working time
	    double working_days=tprod/Time.DAY;
	    double requested_wh=labor/Time.HOUR;
	    // calc needed manhours PER DAY (since man_hours a normalized to ONE DAY, see Staff)
	    // only calculate in WHOLE man hours!!!
	    int mh_per_day=Tools.roundUp(requested_wh/working_days);
	    if (mh_per_day>staff.getAvailableManhours()) mh_per_day=staff.getAvailableManhours();
		staff.blockManhours(mh_per_day);
		return(new Labor(mh_per_day,staff.getAvgWage(),skill,staff.getAvgQualification()));
	}
	
	// deallocate unneeded labor resources
	public void returnLabor(Labor labor) {
	    parent.getState().getStaff(labor.getSkill()).freeManhours(labor.utilize());
	}
	
	// update workload, reset statistics and pay wages
	public void closeWorkingPeriod() {
	    double hired_mh=Time.DAYS_PER_WEEK*parent.getState().getStaff().getHiredManhours();
	    double overtime=total_wt-hired_mh;
        if (overtime<0) overtime=0;
        workload=(float)(total_wt/hired_mh);
        payWages(overtime);
	    timestamp=parent.getState().getTime();	    
	    total_wt=0;   
	}

	/**
	 * Pays all wages to Households and cause the accounting transactions. 
	 */
	private void payWages(double overtime) {
	    for(SkillDefinition skill : SkillDefinition.values()) {
	        for(ContractEmployment contract : parent.getState().getStaff(skill).getPayroll()) {
	            double wage=contract.getWage()*contract.getWorkingTime();
	            // assemble the workers address
	            Address dest=new Address(AgentType.HOUSEHOLD,parent.getAddress().getRegion(),contract.getHouseholdId());
	            IRemittance transfer=new Transfer(parent.getAddress(),dest,wage,Transfer.IMMEDIATELY);
	            Message<IRemittance> msg=new Message<IRemittance>(parent.getAddress(),parent.getState().getAccounting().getBankAddress(),transfer);
	            parent.sendMessage(msg);
	        }
	    }
		/*parent.getState().getAccounting().createAccountingTransaction(
				FinanceReport.PROFIT_AND_LOSS_ACCOUNTS.WAGES,
				FinanceReport.BALANCE_SHEET_ACCOUNTS.BANK,  
				(double)this.available_mh * (double)this.regular_wage		 //regular wages
			  +	(double)overtim_hours 			* (double)this.overtime_wage);	 //overtime wages
		*/
	}

	
	/**
	 * hires new worker by sending an advertisement to labor market. The advertisement has the form of an 
	 * {@link Inquiry} of {@link Labor}. 
	 * <br><b>Important:</b> The labor market supplies the producer without long waiting time.
	 * This assumption has been made as labor is not superposable. The demanded Labor will be needed 
	 * by production when current production cycle ends and the next one starts.
	 * So all replies from labor market has to be arrive during current production time. 
	 * As each day nor more than one production can start the labor has to be supplied at the same
	 * day when the demand has been sent.   
	 */
	/*
	private void advertiseLabor() {
		IPort outPort = parent.getOutPort(PortNames.SingleOut(parent.getName()));
		// address of labor market in the node of enterprise 
		Address labor_market =  
			new Address(Address.AGENT_TYPES.LABOR_MARKET, parent.getAddress().getNode(), 0);
		Inquiry<Labor> demand = new Inquiry<Labor>(
				parent.getAddress(),
				parent.getState().getLabor(), 	
				(int) (this.getExpectationHours() / parent.getState().getLabor().getRegularWorkingHours()), 
				0,0);
		
		Message<Inquiry<Labor>> advertisement = 
			new Message<Inquiry<Labor>>(parent.getAddress(), labor_market, demand);
		if(demand.getQuantity() > 0)
			parent.getState().getOutgoing_labor_demands().add(advertisement);
		//outPort.write(advertisement);
	}*/
	/**
	 * If any answer of last advertisement arrived, 
	 * this method checks the incoming labor in and make it available for production.  
	 * 
	 * @param labor
	 * @param quantity
	 * @param WorkersAdress
	 */
	/*
	public void hireNewLabor(Labor labor, int quantity ,Address WorkersAdress){
		if( this.list_of_wages.containsKey(WorkersAdress))
			this.list_of_wages.get(WorkersAdress).add(	
					new Labor(	labor.getRegularWorkingHours(), 
									labor.getQualification(), 
									labor.getHourlyWage() * (double)labor.getRegularWorkingHours() ));
		else
			this.list_of_wages.put(WorkersAdress, new Labor(labor.getRegularWorkingHours(), 0, labor.getHourlyWage()));
		this.hired_man_hours = labor.getRegularWorkingHours()* quantity;
	
		
	}*/
	
	public float getWorkLoad() {
	    return(workload);
	}
	
}
