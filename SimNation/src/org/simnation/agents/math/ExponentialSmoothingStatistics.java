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
package org.simnation.agents.math;

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

	private double average=0;	// weighted average 
	private double variance=0;	// weighted variance
	private float alpha;		// exponential smoothing factor, has to be between 0 and 1.

	
	public ExponentialSmoothingStatistics(float esf) { alpha=esf; }
	
	public ExponentialSmoothingStatistics() { this(0.2f); }
	
	@Override
	public double update(double value) {
		final double diff=value-average;
		final double incr=alpha*diff;
		average+=incr; // update average
		variance=(1.0f-alpha)*(variance+diff*incr); // calculation according to the above paper
		return value;
	}

	@Override
	public void reset() {
		average=variance=0;
	}

	@Override
	public double getAverage() { return average; }

	@Override
	public double getVariance() { return variance; }

	public float getSmoothingFactor() { return alpha; }

	/**
	 * Sets the factor used for exponential smoothing.
	 * 
	 * @param data.alpha the smoothing factor, must be between 0 and 1 
	 **/
	public void setSmoothingFactor(float value) { alpha=value; }

	@Override
	public String toString() {
		return "[avg="+average+", std="+Math.sqrt(variance)+"]";
	}

}
