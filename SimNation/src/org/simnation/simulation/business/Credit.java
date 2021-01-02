/**
 * 
 */
package org.simnation.simulation.business;

import org.simnation.simulation.agents.market.MarketSegment2;
import org.simnation.simulation.business.Credit.CreditSet;


/**Credit contract
 * 
 * @author Rene
 *
 */
public final class Credit extends Tradable<CreditSet> {
	
	public static enum CreditSet {
		
		BOND;

	    private static final int LENGTH=values().length;

	    public static int length() { return(LENGTH); }
	    public CreditSet getMarketSegment() { return(this); }

	}
	
	
	private final int start_time=0,end_time=0;

	//Enum der Finanzierungsinstrumente wie bei Skillset?
	
	public Credit(int amount, float rate, float risk) {
		super(amount,rate,1-risk);
	}
	
	public MarketSegment2<CreditSet> getMarketSegment() {
		return null; //CreditSet.BOND;
	}

	public int getPackageSize() { return(1); }

	

}