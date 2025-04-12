/**
 * 
 */
package org.simnation.agents.business;

import org.simnation.core.Citizen;
import org.simnation.common.Labor;
import org.simnation.core.Address;

/**
 * Contains all important conditions and information of a working contract
 * 
 * @author Martin Sanski
 */
public final class ContractEmployment extends Contract<Labor> {

    // Employee's data:
    private final Citizen employee; // the employee's personal parameters

    public ContractEmployment(Address employer, Address employee, Citizen citizen, int from,
            int to, Labor labor) {
        super(employer,employee,labor,from,to);
        this.employee=citizen;
    }

    public Citizen getEmployee() {
        return(employee);
    }

}
