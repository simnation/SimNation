/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable 
 * and used JSimpleSim as technical backbone for concurrent discrete event simulation.
 * 
 * This software is published as open source and licensed under GNU GPLv3.
 * 
 * Contributors:
 * 	- Rene Kuhlemann - development and initial implementation
 * 
 */
package org.simnation.agents.business;

import org.simnation.agents.common.Batch;
import org.simnation.agents.common.Labor;

/**
 * This interface marks any object type that can be traded on a {@code Market}.
 * <p>
 * This interface encapsulates tradable objects to provide all necessary information in a uniform way. 
 * Its methods are used by various market agents for market clearing and by the {@code Supply} wrapper.
 * <p>
 * Price is defined as money units per item units, the overall value equals price by quantity. Quality offers a way to model product differentiation.
 * 
 * @param <T> - type traded on a market, defining the market segment (e.g. Good, Skill, Credit)
 *
 * @see Demand
 * @see Supply
 * @see Market
 * @see Batch
 * @see Labor
 * @see Credit
 *
 */

public interface Tradable<T> extends Comparable<Tradable<T>> {

	/**
	 * @return the type / market segment of this Marketable
	 */
	T getType();
	
	/**
	 * @return stock quality
	 */
	float getQuality();
	
	/**
	 * @return quantity in number of packages
	 */
	long getQuantity();

	/**
	 * @return price per package
	 */
	double getPrice();

	/**
	 * @return total monetary value of the transaction
	 */
	default public double getTotalValue() {
		return ((double)getPrice())*getQuantity();
	}
	
	/**
	 * @return comparison by price per unit
	 */
	default int compareTo(Tradable<T> other) {
		if (this.getPrice()<other.getPrice()) return -1;
		else if (this.getPrice()>other.getPrice()) return 1;
		return 0;
	}
	
}