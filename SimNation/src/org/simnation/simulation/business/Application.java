/**
 * 
 */
package org.simnation.simulation.business;

import org.simnation.core.Citizen;
import org.simnation.core.Address;

/**
 * Representing an application of a citizen for the local labor market
 * 
 * @author Rene Kuhlemann
 */

public final class Application {

    private final Citizen applicant;
    private final Address sender;
    private final int wh_per_week;
    private final float wage;

    public Application(Address sender, Citizen candidate, int wh, float wage) {
        this.sender=sender;
        this.applicant=candidate;
        this.wh_per_week=wh;
        this.wage=wage;
    }

    public int getWorkingHours() {
        return(wh_per_week);
    }

    public Citizen getApplicant() {
        return(applicant);
    }

    public float getWage() {
        return(wage);
    }

    public Address getSender() {
        return(sender);
    }

}
