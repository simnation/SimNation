/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable and used JSimpleSim as technical
 * backbone for concurrent discrete event simulation. This software is published as open source and licensed under GNU
 * GPLv3. Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simnation.agents.common;

import org.simnation.agents.business.Money;
import org.simnation.agents.business.Tradable;
import org.simnation.context.technology.Good;

/**
 * Represents a certain amount of a {@code Good}.
 * <p>
 * A batch has a value and can be split and merged. It is traded on B2C- and
 * B2B-markets.
 * <p>
 * Note: This class is thread-safe
 * <p>
 *
 * @see Money
 */
public class Batch implements Tradable<Good>, Mergable<Batch> {

	private final Good good;
	private volatile long quantity; // in good units
	private volatile long value; // in money units
	private volatile float quality;

	public Batch(Good g, long a, long v, float q) {
		if (a<0) throw new IllegalArgumentException("Batch(): initial quantity negative!");
		good=g;
		quantity=a;
		value=v;
		quality=q;
	}

	/**
	 * Standard constructor initializing a batch with zero values.
	 */
	public Batch(Good g) {
		this(g,0,0,0);
	}

	@Override
	public Batch split(long amount) {
		if (amount<0||amount>getQuantity())
			throw new IllegalArgumentException("Batch.split(): value too large or negative!");
		final long otherValue=amount*getValue()/getQuantity(); // integer division
		value-=otherValue; // ensure sum equals the old value, i.e. there is no arithmetic loss 
		quantity-=amount;
		return new Batch(good,amount,otherValue,quality);
	}

	@Override
	public long merge(Batch other) {
		final double sum=getQuantity()+other.getQuantity();
		quality=(float) ((getQuantity()*getQuality()+other.getQuantity()*other.getQuality())/sum);
		quantity+=other.consume();
		value+=other.getValue();
		other.value=0;
		return quantity;
	}

	public long consume() {
		final long result=getQuantity();
		quantity=0;
		return result;
	}

	public boolean isSameGoodAs(Batch other) {
		return getType().equals(other.getType());
	}

	public boolean isEmpty() { return getQuantity()==0; }

	public void setValue(long newValue) { value=newValue; }

	@Override
	public long getQuantity() { return quantity; }

	@Override
	public float getQuality() { return quality; }

	@Override
	public Good getType() { return good; }

	@Override
	public long getValue() { return value; }

	@Override
	public String toString() {
		return "["+getQuantity()+" U of "+good.getName()+", value: "+getValue()+" $, price: "+getPrice()+" $/U]";
	}

}
