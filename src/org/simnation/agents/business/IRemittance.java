/**
 * 
 */
package org.simnation.agents.business;

import org.simnation.core
.Address;

/**Generalized money transfer 
 * 
 * @author Rene Kuhlemann
 *
 */
public interface IRemittance {
    
    public static final double IMMEDIATELY=0;
    
    double getAmount();
    double getDueDate();
    Address getCreditor();
    Address getDeptor();

}
