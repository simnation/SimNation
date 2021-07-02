/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable and used JSimpleSim as technical
 * backbone for concurrent discrete event simulation. This software is published as open source and licensed under GNU
 * GPLv3. Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simnation.agents.common;

import java.util.concurrent.atomic.AtomicLong;

import org.simnation.agents.business.Tradable;
import org.simnation.context.technology.Good;

/**
 * Represents a certain amount of a {@code Good}.
 * <p>
 * A batch has a value and can be split and merged. It is traded on B2C- and B2B-markets.
 * <p>
 * Note: This class is thread-safe
 * <p>
 * @see Money
 *  */
public class Batch_copy implements Tradable<Good>, Mergable<Batch_copy> {

	private final Good good;
	private final AtomicLong quantity;
	private volatile long value; // in money units
	private volatile float quality;

	public Batch_copy(Good g, long a, long value v, float q) {
		if (a<0) throw new IllegalArgumentException("Batch constructor: initial quantity negative!");
		good=g;
		quantity=new AtomicLong(a);
		value=v;
		//price=p;
		quality=q;
	}

	/**
	 * Standard constructor initializing a batch with zero values. 
	 */
	public Batch_copy(Good good) {
		this(good,0,0,0);
	}

	@Override
	public long getQuantity() {
		return quantity.get();
	}

	@Override
	public Batch_copy split(long amount) {
		if ((amount<0)||(amount>getQuantity()))
			throw new IllegalArgumentException("Batch.split(): value too large or negative!");
		quantity.getAndAdd(-amount);
		return new Batch_copy(good,amount,price,quality);
	}

	@Override
	public long merge(Batch_copy other) {
		final double sum=this.getQuantity()+other.getQuantity();
		price=(float) (((this.getQuantity()*this.getPrice())+(other.getQuantity()*other.getPrice()))/sum);
		quality=(float) (((this.getQuantity()*this.getQuality())+(other.getQuantity()*other.getQuality()))/sum);
		return quantity.getAndAdd(other.consume());
	}

	public long consume() {
		return quantity.getAndSet(0);
	}

	public boolean isSameGoodAs(Batch_copy other) {
		return getType().equals(other.getType());
	}

	public boolean isEmpty() {
		return getQuantity()==0;
	}

	@Override
	public double getPrice() {
		return price;
	}
	
	public void setPrice(double value) {
		price=value;
	}

	@Override
	public float getQuality() {
		return quality;
	}

	@Override
	public Good getType() {
		return good;
	}

	@Override
	public String toString() {
		return "["+getQuantity()+"U of "+good.getName()+" value "+getPrice()+"$/U]";
	}

}
