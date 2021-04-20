/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable and used JSimpleSim as technical
 * backbone for concurrent discrete event simulation. This software is published as open source and licensed under GNU
 * GPLv3. Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simnation.agents.business;

/**
 * General supply class, contains the {@code Tradable<T>} item and a {@code Money} placeholder for direct payment.
 * <p>
 * Note: This class is thread-safe.
 * <p>
 * Note: Implementations of the parameter T have to be singletons, so {@code IdentityHashMap} can be used.
 * <p>
 */
public final class Supply<T> implements Comparable<Supply<T>> {

	private final int[] supplier;
	private final double price; // selling price <> batch price
	private final Tradable<T> item; // item offered
	private volatile Money money=null; // optional: cash payment - only for cash-based trades

	/**
	 * Constructor for supply sent to a market
	 *
	 * @param batch
	 * @param order
	 * @param payment
	 */
	public Supply(int[] addr, Tradable<T> t, double p) {
		supplier=addr;
		item=t;
		price=p;
	}

	public Tradable<T> getItem() {
		return item;
	}

	public T getMarketSegmentSelector() {
		return getItem().getType();
	}

	public double getPrice() {
		return price;
	}

	public long getQuantity() {
		return getItem().getQuantity();
	}

	public int[] getAddr() {
		return supplier;
	}

	public Money getMoney() {
		return money;
	}

	public void setMoney(Money value) {
		money=value;
	}

	@Override
	public String toString() {
		return getItem().toString();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Supply<T> other) {
		return getItem().compareTo(other.getItem());
	}

}
