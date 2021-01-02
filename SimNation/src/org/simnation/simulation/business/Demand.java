/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable and used JSimpleSim as technical
 * backbone for concurrent discrete event simulation. This software is published as open source and licensed under GNU
 * GPLv3. Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simnation.simulation.business;

import org.simnation.model.technology.Batch;
import org.simnation.model.technology.Good;

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
public class Demand<T> implements Comparable<Demand<T>> {

	private final int[] addr;
	private final T ms; // market segment
	private final int minQuantity; // minimum amount
	private final float minQuality; // minimum quality - may be zero
	private final float maxPrice; // maximum price
	private final Money money; // cash payment in advance - only for cash-based trades
	private Tradable<T> item=null;

	/**
	 * Constructor for a cash-based trade (cash'n carry)
	 *
	 * @param addr   - address of the inquiring household
	 * @param ms     - market segment (e.g. an instance of {@link Good})
	 * @param amount - minQuantity
	 * @param qual   - minQuality
	 * @param cost   - maxCost
	 * @param cash   - payment, should at least be equal to maxCost !
	 */
	public Demand(int[] a, T type, int amount, float price, float qual, Money cash) {
		addr=a;
		ms=type;
		minQuantity=amount;
		minQuality=qual;
		maxPrice=price;
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

	public float getMaxPrice() {
		return maxPrice;
	}

	public T getMarketSegmentSelector() {
		return ms;
	}

	public long getMaxCost() {
		if (getMoney()!=null) return getMoney().getValue();
		return (long) (getQuantity()*getMaxPrice());
	}

	public Money getMoney() {
		return money;
	}

	@Override
	public String toString() {
		return "["+getMarketSegmentSelector()+": "+getQuantity()+"U|"+getMaxPrice()+"$]";
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Demand<T> other) {
		if (this.getMaxPrice()<other.getMaxPrice()) return -1;
		else if (this.getMaxPrice()>other.getMaxPrice()) return 1;
		return 0;
	}

	public Tradable<T> getItem() {
		return item;
	}

	public void setItem(Tradable<T> item) {
		this.item=item;
	}

}
