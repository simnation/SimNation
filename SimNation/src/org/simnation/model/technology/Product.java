/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable
 * and used JSimpleSim as technical backbone for concurrent discrete event
 * simulation.
 *
 * This software is published as open source and licensed under GNU GPLv3.
 *
 * Contributors: - Rene Kuhlemann - development and initial implementation
 *
 */
package org.simnation.model.technology;

/**
 * A product is a concrete specialized implementation of a good and extended by additional characteristics chosen by its
 * manufacturer. This is necessary to enable every manufacturer to apply an own marketing strategy and to differentiate
 * in the market of that good.
 *
 * 
 */

public class Product {

	private final Good good; // reference to the corresponding good
	private final int packageSize; // size of one unit product 
	private int totalOutput=0; // for experience curve effect
	
	public Product(Good good,int ps) {
		this.good=good;
		packageSize=ps;
	}

	public Good getGood() {
		return good;
	}

	public int getPackageSize() {
		return packageSize;
	}

	public int getTotalOutput() {
		return totalOutput;
	} // accumulate total output for experience curve effects

	public void addOutput(int output) {
		totalOutput+=output;
	}

}