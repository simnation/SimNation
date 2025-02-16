package org.simnation.agents.business;

import java.util.EnumMap;

/**
 * Provides general functionality of an accounting department. Can be extended to suit needs of dedicated business
 * (companies, banks, etc), central bank or government by providing an enum containing all account types
 *
 * @author Rene Kuhlemann
 *
 */

public abstract class AbstractAccounting<A extends Enum<A> & AbstractAccounting.Bookable> {

	public interface Bookable { // marker interface for all account custodies

		public String getName();
	}

	private final EnumMap<A,Account> accountMap;

	public AbstractAccounting(Class<A> clazz) {
		accountMap=new EnumMap<>(clazz);
	}

	// init new account or add value if account already exists
	public void initAccount(A key,long value) {
		final Account acc=new Account(value);
		final Account dest=accountMap.get(key);
		if (dest==null) accountMap.put(key,acc);
		else acc.bookTo(dest,value);
	}

	public void doBooking(A src,A dest,long amount) {
		accountMap.get(src).bookTo(accountMap.get(dest),amount);
	}

	public long getBalance(A key) {
		return accountMap.get(key).getBalance();
	}

}
