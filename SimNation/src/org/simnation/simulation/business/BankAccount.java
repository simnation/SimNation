/**
 * 
 */
package org.simnation.simulation.business;

import org.simnation.core.Address;

/**Represents a bank account and offers all basic account functions 
 *  
 * @author Rene Kuhlemann
 *
 */
public final class BankAccount {
    
    private int balance;
    private int limit;
    private final Address owner;
    
    public BankAccount(Address owner, int amount) {
        this.owner=owner;
        balance=amount;
        limit=0;
    }
    
    public int getMaxAvail() {
        return(balance-limit);
    }
    
    public Money withdraw(int amount) {
        assert(amount>0);
        int max=getMaxAvail();
        if (max==0) return(null);
        if (amount>max) {
            balance-=max;
            return(new Money(max));
        } else {
            balance-=amount;
            return(new Money(amount));
        }
    }
    
    public boolean transfer(BankAccount dest, int amount) {
        if ((getMaxAvail()==0)||(amount>getMaxAvail())) return(false);
        this.balance-=amount;
        dest.balance+=amount;
        return(true);
    }
    
    public void deposit(Money money) {
        balance+=money.spend();
    }
    
    public Address getAddress() {
        return(owner);
    }

    public double getBalance() {
        return(balance);
    }

    public float getLimit() {
        return(limit);
    }

    public void setLimit(int l) {
        this.limit=l;
    }

}
