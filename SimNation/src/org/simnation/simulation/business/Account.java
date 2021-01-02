/**
 * 
 */
package org.simnation.simulation.business;

/**Represents a book keeping account, provides basic booking functionality and ensures
 * a closed system of money flow
 * 
 * @author Rene Kuhlemann
 * 
 */

public final class Account {
	
	private long balance;
	
	public Account(final long amount) {
		balance=amount;
	}
	
	public Account() { this(0); }
	
	public long getBalance() {
		return(balance);
	}
	
	public void bookTo(final Account dest, final long amount) {
	    this.balance-=amount;
	    dest.balance+=amount;
	}
			
}
