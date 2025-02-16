package org.simnation.agents.firm.common;

// java
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.simnation.agents.business.ContractEmployment;
import org.simnation.core.Citizen;
import org.simnation.core.Tools;
import org.simnation.core.Time;

/**Represents the staff of a company having one specific skill
 * 
 * @author Rene Kuhlemann
 *
 */

public final class Staff {
    
 // only calculate in WHOLE man hours!!!
    
    private final Map<Citizen,ContractEmployment> payroll;
    private double qualification_sum=0;   // qualification sum of staff
    private double wage_sum=0;            // wage sum of staff PER HOUR
    private int hired_mh=0;            // hired man-hours PER DAY 
    private int used_mh=0;             // man-hours currently in use PER DAY
                                          // ACOUNTS FOR MAX_WORKLOAD PER DAY!!!
        
    public Staff() {
        payroll=new HashMap<Citizen,ContractEmployment>();
    }
    
    public void addContract(ContractEmployment contract) {
        payroll.put(contract.getEmployee(),contract);
        qualification_sum+=contract.getSkill();
        wage_sum+=contract.getWage();
        hired_mh+=contract.getWorkingTime();
    }
    
    public void removeContracts(float time) {
        // lay off workers to save on working hours
    }
    
    public int getHiredManhours() { return(hired_mh); }
    
    public int getUsedManhours() { return(used_mh); } 
    
    // absolute limit of the staff's manhours capacity
    public int getAvailableManhours() {
        return(Tools.roundOff(Time.MAX_WORKLOAD*hired_mh)-used_mh);
    }
    
    public float getWorkLoad() { 
        return((float)used_mh/hired_mh); 
    }
    
    public float getAvgQualification() {
        return((float)(qualification_sum/payroll.size()));
    }
    
    public float getAvgWage() {
        return((float)(wage_sum/payroll.size()));
    }
    
    public double getDailyWageCosts() {
        return(hired_mh*wage_sum/payroll.size()); 
    }
    
    public Collection<ContractEmployment> getPayroll() {
        return(payroll.values());
    }
    
    public void blockManhours(int mh) {
        used_mh+=mh;
    }
    
    public void freeManhours(int mh) {
        assert(used_mh>=mh);
        used_mh-=mh;
    }
    
}
