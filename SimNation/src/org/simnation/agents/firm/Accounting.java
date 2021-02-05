package org.simnation.agents.firm;

import org.simnation.agents.business.AbstractAccounting;


public final class Accounting extends AbstractAccounting<Accounting.ACCOUNT> {
	
	public enum ACCOUNT implements AbstractAccounting.Bookable {
		// ********** balance sheet according to IFRS **********
		// non-current assets
		GOODWILL("Intangible assets"), // Goodwill
		PPE("Property, plant and equipment"), // Machinery (Sachanlagen)
		ASSETS("Non-current financial assets"), // Real estate and other long-term investments

		// current assets
		INVENTORIES("Inventories"), // Inventory stocks
		RECEIVABLES("Accounts receivable"), // Trade receivables and Other receivables
		CASH("Cash"), // Bank account

		// capital and reserves
		EQUITY("Issued capital"), // Equity
		RETAINED("Reserves"), // Retained earnings

		// liabilities
		PAYABLES("Accounts payable"), // offene Rechnungen
		LOANS("Loans"), // Bankkredite
		BONDS("Bonds"), // Anleihen
		PROVISIONS("Provisions"), // Rueckstellungen
		
		// *** specific banking accounts may be added here....

		// ********** income statement according to IFRS **********
		// *** (total expenditure format = nature of expense method) ***
		// income
		REVENUE("Revenue"),
		OTHER_REVENUE("Other revenue"),
		// expenses
		INV_CHANGES("Changes in inventories"),
		PRECURSORS("Raw materials and consumables used"),
		LABOR("Employee benefits expense"),
		DEPRECIATION("Depreciation and amortization"),
		OTHER_EXPENSES("Other expenses"),
		// financial activities
		INV_INCOME("Finance income"),
		INV_EXPENSES("Finance expenses"),
		TAX("Income tax expenses");

		private final String name;

		ACCOUNT(String n) {
			name=n;
		}
		
		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return getName();
		}


	}
	
	public Accounting() {
		super(ACCOUNT.class);
	}

}
