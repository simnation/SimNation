/**
 * 
 */
package org.simnation.simulation.business;

import org.simnation.core.Address;

/**
 * Abstract class model for any kind of contract. Specific contracts (e.g.
 * {@link ContractEmployment}) have to be derived from this generic class!
 * 
 * @author Rene Kuhlemann
 */

public abstract class Contract<T> {

    private final Address creditor, debtor;
    private final T type;
    private final int start_time, end_time;

    public Contract(Address debtor, Address creditor, T item, int start, int end) {
        this.creditor=creditor;
        this.debtor=debtor;
        this.type=item;
        this.start_time=start;
        this.end_time=end;
    }

    public Address getDebtor() {
        return(debtor);
    }

    public Address getCreditor() {
        return(creditor);
    }

    public T getContractItem() {
        return(type);
    }

    public int getStartTime() {
        return(start_time);
    }

    public int getEndTime() {
        return(end_time);
    }

    public int getDuration() {
        return(end_time-start_time);
    }

}
