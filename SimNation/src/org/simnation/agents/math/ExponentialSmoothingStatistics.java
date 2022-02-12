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
 * Calculates average and variance by exponential smoothing.
 * 
 * @see <a href="https://fanf2.user.srcf.net/hermes/doc/antiforgery/stats.pdf">
 *      Reference for exponentially-weighted mean and variance</a>
 * 
 */
public final class ExponentialSmoothingStatistics implements Statistics {

	private double average=0;	// demand average 
	private double variance=0;	// demand variance
	private float alpha;		// exponential smoothing factor

	
	public ExponentialSmoothingStatistics(float esf) { alpha=esf; }
	
	public ExponentialSmoothingStatistics() { this(0.2f); }
	
	/**
	 * {@inheritDoc}
	 */
	public double update(double value) {
		final double diff=value-average;
		final double incr=alpha*diff;
		average=average+incr; // update average
		variance=(1-alpha)*(variance+diff*incr); // recursive calculation based on Welford's method
		return value;
	}

	/**
	 * {@inheritDoc}
	 **/
	public void reset() {
		average=variance=0;
	}

	/**
	 * {@inheritDoc}
	 **/
	public double getAverage() { return average; }

	/**
	 * {@inheritDoc}
	 **/
	public double getVariance() { return variance; }

	public float getSmoothingFactor() { return alpha; }

	/**
	 * Sets the factor used for exponential smoothing.
	 * 
	 * @param alpha the smoothing factor, must be between 0 and 1 
	 **/
	public void setSmoothingFactor(float value) { alpha=value; }

	@Override
	public String toString() {
		return "[avg="+average+", std="+Math.sqrt(variance)+"]";
	}

}
