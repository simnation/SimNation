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
 * Simple statistics implementation that always provides the last data point with zero variance.
 * 
 */
public final class LastValueStatistics implements Statistics {

	private double lastValue=0;	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double update(double value) {
		return lastValue=value;
	}

	/**
	 * {@inheritDoc}
	 **/
	@Override
	public void reset() {
	}

	/**
	 * {@inheritDoc}
	 **/
	@Override
	public double getAverage() { return lastValue; }

	/**
	 * {@inheritDoc}
	 **/
	@Override
	public double getVariance() { return 0; }

	@Override
	public String toString() {
		return "[avg="+getAverage()+", std="+Math.sqrt(getVariance())+"]";
	}

}
