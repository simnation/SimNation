/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable and used JSimpleSim as technical
 * backbone for concurrent discrete event simulation. This software is published as open source and licensed under GNU
 * GPLv3. Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simnation.agents.business;

import org.simnation.agents.common.Batch;
import org.simnation.context.technology.Good;

/**
 * Represents a demand for a {@code Good} that can be traded in form of a {@code Batch}.
 * <p>
 * Note: Implementations of the parameter T have to be singletons, so {@code IdentityHashMap} can be used.
 * <p>
 * Note: This class is thread-safe.
 *
 * @param <T> - type characterizing market segments
 * @see Good
 * @see Batch
 */
public final class Demand<T> implements Comparable<Demand<?>> {

	private final int[] addr;
	private final T marketSegment; // market segment
	private final int minQuantity; // minimum amount
	private final float minQuality; // minimum quality - may be zero
	private final double maxPrice; // maximum price
	private final Money money; // cash payment in advance - only for cash-based trades
	private Tradable<T> item=null;

	/**
	 * Constructor for a cash-based trade (cash'n carry)
	 *
	 * @param addr   - address of the inquiring household
	 * @param ms	 - market segment (e.g. an instance of {@link Good})
	 * @param amount - minQuantity
	 * @param maxPrice  - maxPrice
	 * @param qual   - minQuality
	 * @param cash   - payment, should at least be equal amount by price, can be {@code null}.
	 */
	public Demand(int[] addr, T ms, int amount, float p, float qual, Money cash) {
		this.addr=addr;
		marketSegment=ms;
		minQuantity=amount;
		minQuality=qual;
		maxPrice=p;
		money=cash;
	}

	/**
	 * Constructor for a contract-based trade (e.g. services)
	 *
	 * @param addr   - address of the inquiring household
	 * @param ms     - market segment (e.g. an instance of {@link Good})
	 * @param amount - minQuantity
	 * @param qual   - minQuality
	 * @param cost   - maxCost
	 */
	public Demand(int[] a, T ms, int amount, long cost, float qual) {
		this(a,ms,amount,cost,qual,null);
	}

	public int[] getAddr() {
		return addr;
	}

	public int getQuantity() {
		return minQuantity;
	}

	public float getMinQuality() {
		return minQuality;
	}

	public T getMarketSegment() {
		return marketSegment;
	}

	public Money getMoney() {
		return money;
	}

	public double getMaxPrice() {
		return maxPrice;
	}

	public Tradable<T> getItem() {
		return item;
	}

	public void setItem(Tradable<T> item) {
		this.item=item;
	}

	@Override
	public String toString() {
		return "["+getQuantity()+"U of "+getMarketSegment()+" for $"+getMaxPrice()+"] cash: "+getMoney().toString();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Demand<?> other) {
		if (this.getMaxPrice()<other.getMaxPrice()) return -1;
		else if (this.getMaxPrice()>other.getMaxPrice()) return 1;
		return 0;
	}

}
