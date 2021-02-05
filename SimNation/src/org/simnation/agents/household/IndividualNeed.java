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
package org.simnation.agents.household;

import org.simnation.agents.common.Batch;
import org.simplesim.core.scheduling.Time;

/**
 * Specifies an individual need of a household.
 *
 * A need is the very basic reason for a household to consume. There are three
 * categories of a need's urgency according to the ERG-Theory (Alderfer). If the
 * desire of a household reaches the <i>activation level</i>, the household starts to
 * look for a product satisfying the need. By consumption the desire can be
 * decreased, down to saturation level. Otherwise, if the need cannot be
 * satisfied, the desire increases even further, until it finally reaches the
 * <i>regression level</i>. Then the agent stops trying to satisfy the need and
 * resigns.
 * <p> As a result of a regression, the ERG level drops and the household focuses on 
 * satisficing all needs of the next lower level. If the level of the unfulfilled need is 
 * existential, the agent dies and is eliminated from the model.Households will try to persuade the needs of the next higher ERG level, if the needs of the
 * current level are completely satisfied.
 * <p>
 * The desired <i>stock</i> is the standard amount of a product a household tries to 
 * acquire on the market. The desired <i>quality</i> of products increases with the 
 * households satisfaction level (as a result, the demanded quality level rises with
 * what an increasing income).
 * <p>
 * Acquired ERG level, fulfillment of desired stock and quality enable to calculate an overall 
 * contentment of a household. That in turn may lead the way to model political dynamics within the agent population
 *
 */
public final class IndividualNeed { 
	
	// set internal time stamp if need couldn't be satisfied, set to INFINITY if not
	// save frustration time here to be able to switch between activation and frustration phase
	// if need not satisfied set new activation event and frustration time
	// time between frustration and actual time hints to actual urgency 
	
	private Time frustrationTime=Time.INFINITY; // at this time the satisficing good runs out
	private double cr; // consumption rate of household
	private long stock; // stock of the satisficing good
	private float quality;  // quality of the satisficing good in stock

	
	public void addToStock(Batch batch) {
		double sum=getQuantity()+batch.getQuantity();
		// average quality and price
		double avgQuality=(getQuality()*getQuantity()+batch.getQuality()*batch.getQuantity())/sum;
		setQuality((float) avgQuality);
		setQuantity((int) sum);
		batch.consume();
	}
	
	public void setFrustrationTime(Time value) {
		frustrationTime=value;
	}

	public void setConsumptionRate(double value) {
		cr=value;
	}

	public float getQuality() {
		return quality;
	}
	public void setQuantity(int value) {
		stock=value;
	}

	public void setQuality(float value) {
		quality=value;
	}

	public long getQuantity() {
		return stock;
	}

	public Time getFrustrationTime() {
		return frustrationTime;
	}

	public double getConsumptionRate() {
		return cr;
	}

}
