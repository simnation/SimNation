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
package org.simnation.common.statistics;

import org.simnation.context.Limits;

/**
 * Calculates mean and variance by exponential smoothing.
 * 
 * Uses EWAM and EWVM (Exponentially Weighted Average/Variance Method) to calculate mean and variance of a time series. 
 * The exponential smoothing factor determines the elasticity to changes. The higher the factor, the higher the impact of new
 * values. The method is implemented as efficient online algorithm.
 * 
 * 
 * @see <a href="https://fanf2.user.srcf.net/hermes/doc/antiforgery/stats.pdf">
 *       <i>Incremental calculation of weighted mean and variance</i> by Tony Finch (2009)</a>
 * 
 */
public final class ExponentialSmoothingStatistics implements Statistics {

	private volatile double average;	// weighted average 
	private volatile double variance;	// weighted variance
	private volatile double lastValue;	// last value of this time series
	private final float alpha;	// exponential smoothing factor, has to be between 0 and 1.
	
	public ExponentialSmoothingStatistics(float esf) { 	
		reset(0);
		alpha=esf; 
	}
	
	public ExponentialSmoothingStatistics() { this(Limits.DEFAULT_SMOOTHING_FACTOR); }
	
	@Override
	public void update(double value) {
		final double diff=value-average;
		final double incr=alpha*diff;
		average=average+incr; // update average
		variance=(1.0f-alpha)*(variance+diff*incr); // calculation according to the above paper
		lastValue=value;
	}

	@Override
	public void reset(double avg) {
		average=lastValue=avg;
		variance=0;
	}
	
	@Override
	public double getLastValue() { return lastValue; }

	@Override
	public double getAVG() { return average; }

	@Override
	public double getVAR() { return variance; }

	public float getSmoothingFactor() { return alpha; }

	@Override
	public String toString() {
		return "[avg="+average+", std="+Math.sqrt(variance)+"]";
	}

}
