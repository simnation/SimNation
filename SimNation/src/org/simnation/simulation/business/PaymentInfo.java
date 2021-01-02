/**
 * 
 */
package org.simnation.simulation.business;

/**
 * Represents general payment information: When is it due? Payment by cash? Recurring?
 * 
 * @author Rene Kuhlemann
 */
public final class PaymentInfo {

    private final Invoice invoice;
    private final int frequency;
    private final int period;
    private final Money money;

    public PaymentInfo(Invoice invoice, int period, int freq, Money money) {
        this.invoice=invoice;
        this.period=period;
        this.frequency=freq;
        this.money=money;
    }

    /**
     * Constructor for recurring payment {@link Contract}
     * 
     * @param invoice - invoice with initial due date and additional payment information
     * @param period - time between payments
     * @param freq - how often after due date?
     */
    public PaymentInfo(Invoice invoice, int period, int freq) {
        this(invoice,period,freq,null);
    }

    /**
     * Constructor for a single date of payment {@link Invoice}
     * 
     * @param invoice - invoice with initial due date and additional payment information
     */
    public PaymentInfo(Invoice invoice) {
        this(invoice,0,1,null);
    }

    /**
     * Constructor immediate and single payment by cash {@link Money} Note: this only returns the
     * change, since the money was paid in advance in the order process!
     * 
     * @param money - money RETURNED to customer
     */
    public PaymentInfo(Money money) {
        this(null,0,1,money);
    }

    public Invoice getInvoice() {
        return(invoice);
    }

    public int getFrequency() {
        return(frequency);
    }

    public int getPeriod() {
        return(period);
    }

    public Money getMoney() {
        return(money);
    }

    public boolean isCashPayment() {
        return(money!=null);
    }

    public boolean isInvoicePayment() {
        return((invoice!=null)&&(frequency==1));
    }

    public boolean isRecurringPayment() {
        return((invoice!=null)&&(frequency>1));
    }

}
