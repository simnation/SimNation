package org.simnation.agents.firm;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

import org.simnation.agents.business.AbstractAccounting;
import org.simnation.agents.business.Invoice;
import org.simnation.agents.business.Money;
import org.simnation.agents.firm.common.Staff;
import org.simnation.agents.firm.common.Staffing;
import org.simnation.agents.firm.common.Warehouse;
import org.simnation.context.population.SkillDefinition;
import org.simplesim.core.scheduling.Time;
import org.simplesim.model.State;

/**
 * Stores common variables of all enterprises, i.e. manufacturers, service providers, banks 
 * 
 * @author Rene Kuhlemann
 *
 */

public class EnterpriseState implements State {
	
	
	private final EnumMap<SkillDefinition,Staff> staff=new EnumMap<>(SkillDefinition.class);
	private final List<Invoice> payable=new LinkedList<>();;
	private final List<Invoice> receivable=new LinkedList<>();;
	private final Accounting accounting=new Accounting();
	private final Warehouse warehouse=new Warehouse();
	private Staffing staffing;
	private Money cash;
	
	// company steering parameters
	
	private float service_level=0.8f;
	private float min_quality=0.2f;
	private float max_price_factor=1.0f; // relatively to price in last period!!! 1.1 means 10% more
	private float credit_period=5*Time.DAY.getTicks();
    private float max_workload=1.2f;
    private float regular_wage=10;
    private float overtime_wage=15;
    private float min_qualification=0.2f;
    private float profit_margin=0.05f;  // based on cost plus approach  
	
	public EnterpriseState() {
        // for (SkillSet skill : SkillSet.values()) staff.put(skill,new Staff());
	}
	
	public final Accounting getAccounting() {
        return accounting;
    }
    
    public final Staffing getStaffing() {
        return staffing;
    }

    
    
    /*public final Staff getStaff(SkillSet skill) {
        return(staff.getValue(skill));
    }
    */
    
    public final List<Invoice> getPayables() {
        return(payable);
    }
    
    public final List<Invoice> getReceivables() {
        return(receivable);
    }

    public final float getMinQualification() {
        return min_qualification;
    }

    public final float getMaxWorkload() {
        return max_workload;
    }

    public final float getRegularWage() {
        return regular_wage;
    }

    public final float getOvertimeWage() {
        return overtime_wage;
    }

    public final float getCreditPeriod() {
        return credit_period;
    }

    public float getProfitMargin() {
        return profit_margin;
    }
    
    public float getServiceLevel() {
        return service_level;
    }
    
    
    public void setProfitMargin(float profit_margin) {
        this.profit_margin = profit_margin;
    }

    public final void setCreditPeriod(float creditPeriod) {
        credit_period = creditPeriod;
    }

    public final void setMinQualification(float qualification) {
        this.min_qualification = qualification;
    }

    public final void setMaxWorkload(float wl) {
        max_workload = wl;
    }

    public final void setRegularWage(float wage) {
        regular_wage = wage;
    }

    public final void setOvertimeWage(float wage) {
        overtime_wage = wage;
    }

    final void setStaffing(Staffing staffing) {
        this.staffing = staffing;
    }

    final void setCredit_period(float creditPeriod) {
        credit_period = creditPeriod;
    }

    final void setMax_workload(float maxWorkload) {
        max_workload = maxWorkload;
    }

    final void setRegular_wage(float regularWage) {
        regular_wage = regularWage;
    }

    final void setOvertime_wage(float overtimeWage) {
        overtime_wage = overtimeWage;
    }

    final void setMin_qualification(float minQualification) {
        min_qualification = minQualification;
    }

    final void setProfit_margin(float profitMargin) {
        profit_margin = profitMargin;
    }
    
    final void setServiceLevel(float value) {
        service_level=value;
    }

    public void setMinQuality(float min_quality) {
        this.min_quality = min_quality;
    }

    public float getMinQuality() {
        return min_quality;
    }

    public void setMaxPriceFactor(float max_price) {
        this.max_price_factor = max_price;
    }

    public float getMaxPriceFactor() {
        return max_price_factor;
    }

	public Money getCash() {
		return cash;
	}

	public void setCash(Money cash) {
		this.cash = cash;
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}

}
