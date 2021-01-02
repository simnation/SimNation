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
package org.simnation.model.needs;

import static org.simnation.model.Limits.NUMBER_OF_TRIES;

import java.util.List;

import org.simnation.model.technology.Batch;
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
public final class Need { 
	
	// set internal time stamp if need couldn't be satisfied, set to INFINITY if not
	// Different types of need for service, consumable, etc?
	// save frustration time here, not in queue, easier to look up
	// if need not satisifed set new activation event and frustration time
	// time between furstration and actual time hints to actual urgendcy 
	
	private static final int INFINITY=Integer.MAX_VALUE;
	
	private int frustrationTime=INFINITY; // at this time the satisficing good runs out
	private double cr; // consumption rate of household
	private int stock; // stock of the satisficing good
	private int quality;  // quality of the satisficing good in stock

	/**
	 *
	 * @param activationLevel  from this time on the need becomes urgent
	 * @param frustrationLevel at this the agent resigns, and in case of an
	 *                         existential need, dies.
	 * @param consumptionRate  defines how fast the need recharges, can also be seen
	 *                         as burn rate [U/t]
	 * @param stock         defines the desired stock
	 * @param quality          defines the desired minimum quality of products that
	 *                         satisfy this need
	 */
	public Need(int initQuantity, int initQuality) {
		stock=initQuantity;
		quality=initQuality;
	}

	/**
	 * Estimates the time the need is actively persuaded.
	 *
	 * @return time between activation and frustration event
	 */
	public int getActivityPeriod() {
		return (int) Math.round(calcActivityPeriod());
	}

	/**
	 * Estimates the time between the various tries during the activity period.
	 *
	 * @return time between one try activation event and another during activity
	 *         period
	 */
	public int getTimeBetweenTries() {
		return (int) Math.round(calcActivityPeriod()/NUMBER_OF_TRIES);
	}

	/**
	 * Calculates how many tries are left within the activity period.
	 *
	 * @return numerber of tries before the agent goes into frustration mode.
	 */
	public int calcTriesLeft(Time deltaT) {
		return (int) Math.floor((NUMBER_OF_TRIES*deltaT.getTicks())/calcActivityPeriod());
	}

	public double calcUrgency(NeedDefinition nd, Time time) {
		if (getFrustrationTime()==INFINITY) return 1.0d;
		final double activityPeriod=nd.getFrustrationTime()-nd.getActivationTime();
		final double actualPeriod=getFrustrationTime()-time.getTicks();
		return (activityPeriod/actualPeriod);
	}
	
	public void addToStock(Batch batch) {
		final int sum=getQuantity()+batch.getTotalVolume();
		// average quality and price
		final float avgQuality=(getQuality()*getQuantity()+batch.getQuality()*batch.getTotalVolume())/sum;
		setQuality(Math.round(avgQuality));
		setQuantity(sum);
		batch.utilize();
	}

	public double calcAmount(Time deltaT) {
		return ((getFrustrationTime()-deltaT.getTicks())+getTimeBetweenTries())*consumption;
	}

	public void setFrustrationTime(int time) {
		frustrationTime=time;
	}

	public void setConsumptionRate(double value) {
		cr=value;
	}

	// How long is the time period between initial activation and frustration event?
	private double calcActivityPeriod() {
		return (frustration-activation)/consumption;
	}

	@Override
	public String toString() {
		return "toString in Need to be done";
	}

	public int getQuality() {
		return quality;
	}
	public void setQuantity(int quantity) {
		this.stock=quantity;
	}

	public void setQuality(int quality) {
		this.quality=quality;
	}

	public int getQuantity() {
		return stock;
	}

	public int getFrustrationTime() {
		return frustrationTime;
	}

	public double getConsumptionRate() {
		return cr;
	}

}
