/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable and used JSimpleSim as technical
 * backbone for concurrent discrete event simulation. This software is published as open source and licensed under GNU
 * GPLv3. Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simnation.agents.business;

import java.util.concurrent.atomic.AtomicLong;

import org.simnation.common.Mergable;

/**
 * Represents an amount of cash.
 * <p>
 * Money is handled on an integer base, so fast and simple primitive calculation
 * can be used. Real world data has to be scaled accordingly. One way could
 * be to work on a cent base, thus multiplying real world values by 100.
 * <p>
 * For a closed money cycle, money may only be created in two occasions:
 * <ol>
 * <li>when loading the database states during the model initialization
 * <li>during the simulation run by the central bank agent (influences inflation
 * rate)
 * </ol>
 * Never ever create money during a simulation run by any other agent than the
 * central bank!
 * <p>
 * Note: This class is thread-safe
 */
public final class Money implements Mergable<Money> {

	private final AtomicLong value;

	public Money(long val) {
		if (val<0) throw new IllegalArgumentException("Money(): initial value negative!");
		value=new AtomicLong(val);
	}

	public Money() {
		this(0);
	}

	public long getValue() { return value.get(); }

	@Override
	public Money split(long amount) {
		if (amount<0||amount>getValue())
			throw new IllegalArgumentException("Money.split(): value too large or negative!");
		value.addAndGet(-amount);
		return new Money(amount);
	}

	@Override
	public long merge(Money other) {
		return value.addAndGet(other.value.getAndSet(0));
	}

	public void transfer(Money other, long amount) {
		merge(other.split(amount));
	}

	@Override
	public String toString() {
		return Long.toUnsignedString(getValue())+" $";
	}

}
