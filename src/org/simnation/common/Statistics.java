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
package org.simnation.common;

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
public final class Statistics {

	private volatile double average;	// weighted average 
	private volatile double variance;	// weighted variance
	private volatile double lastValue;	// last value of this time series
	private static final float alpha=Limits.DEFAULT_SMOOTHING_FACTOR;	// exponential smoothing factor, has to be between 0 and 1.
	
	public Statistics(double avg, double var) { 	
		reset(avg,var);
	}
	
	public Statistics(double avg) { 	
		reset(avg,0);
	}
	
	public Statistics() { this(0,0); }
	
	public void update(double value) {
		final double diff=value-average;
		final double incr=alpha*diff;
		average=average+incr; // update average
		variance=(1.0f-alpha)*(variance+diff*incr); // calculation according to the above paper
		lastValue=value;
	}

	public void reset(double avg, double var) {
		average=lastValue=avg;
		variance=var;
	}
	
	public double getLastValue() { return lastValue; }

	public double getAVG() { return average; }

	public double getVAR() { return variance; }
	
	public double getSTD() { return Math.sqrt(variance); }

	public float getSmoothingFactor() { return alpha; }

	public String toString() {
		return "[avg="+average+", std="+Math.sqrt(variance)+"]";
	}

}
