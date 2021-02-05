/**
 * 
 */
package org.simnation.agents.business;

import org.simnation.core.Address;

/**
 * Represents the invoice of a delivery with all relevant information for payment
 * 
 * @author Rene Kuhlemann
 */
public final class Invoice {

    private final Address creditor;
    private final double amount_payable;
    private final int due_date;
    private boolean paid=false;

    public Invoice(Address creditor, double amount, int due) {
        this.creditor=creditor;
        this.amount_payable=amount;
        this.due_date=due;
    }

    public Address getCreditor() {
        return(creditor);
    }

    public double getAmountPayable() {
        return(amount_payable);
    }

    public double getDueDate() {
        return(due_date);
    }

    public boolean isPayed() {
        return(paid);
    }

    public void markAsPayed() {
        paid=true;
    }

}
