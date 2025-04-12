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
package org.simnation.zzz_old;

import org.simnation.common.Batch;
import org.simnation.context.technology.Good;
import org.simplesim.core.scheduling.Time;

public final class Consumable extends Product {

	private static final Time NO_ADVERTISING=Time.ZERO;
	private static final double ADVERTISING_HALFLIFE=7*Time.TICKS_PER_DAY;
	private static final double tau=Math.log(2.0)*ADVERTISING_HALFLIFE;
	private static final float WEIGHT_CUTOFF=0.01f; // makes about 45 days of advertizing efficacy
	public static final float MIN_ATTENTION=0; // limits for perception values
	public static final float MAX_ATTENTION=100;

	private float attention;
	private Time lastAdvertisingTime;

	public Consumable(Good good, int psize, float advValue, Time advTime) {
		super(good,psize);
		lastAdvertisingTime=advTime;
		attention=(advValue<MIN_ATTENTION) ? MIN_ATTENTION : advValue;
		attention=(advValue>MAX_ATTENTION) ? MAX_ATTENTION : advValue;
	}

	public Consumable(Good good, int psize) {
		this(good,psize,0,NO_ADVERTISING);
	}

	/**
	 * Calculates the product's perception based on advertising efforts, time and
	 * quality
	 *
	 * @param quality - the actual quality, e.g. of a {@link Batch}
	 * @return the consumer's perception
	 */
	public float getPerception(Time time, float quality) {
		if (lastAdvertisingTime.equals(NO_ADVERTISING)) return quality; // no advertising effect --> return quality
		final double weight=Math.exp((lastAdvertisingTime.getTicks()-time.getTicks())/tau); // = exp(-dT/tau)
		if (weight<WEIGHT_CUTOFF) { // advertising effect diminishes
			lastAdvertisingTime=NO_ADVERTISING;
			attention=MIN_ATTENTION;
			return quality;
		}
		return (float) (quality+(weight*(attention-quality))); // =weight*attention+(1-weight)*quality
	}

	public void advertise(float advValue, Time time) {
		attention=getPerception(time,0)+advValue; // calc residue from last advertising campaign
		if (attention>MAX_ATTENTION) attention=MAX_ATTENTION;
		lastAdvertisingTime=time;
	}

}