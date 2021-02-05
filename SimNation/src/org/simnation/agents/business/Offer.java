/**
 *
 */
package org.simnation.agents.business;

import org.simnation.core.Address;

/**
 * Offer to a given inquiry in a bidding process or an offered batch at the goods market
 *
 * @author Rene Kuhlemann
 */
public final class Offer<S> {

	private final Address offerer;
	private final Demand<S> demand; // if it will be a contract or service
	private final Tradable<S> item; // if it is something "made to stock"

	public Offer(Address offerer,Demand<S> inquiry) {
		this.demand=inquiry;
		this.offerer=offerer;
		item=null;
	}

	public Offer(Address offerer,Tradable<S> item) {
		this.offerer=offerer;
		this.item=item;
		this.demand=null;
	}

	public Demand<S> getDemand() {
		return demand;
	}

	public Address getOfferer() {
		return offerer;
	}

	public Tradable<S> getItem() {
		return item;
	}
	
	public boolean containsItem() {
		return item!=null;
	}

}
