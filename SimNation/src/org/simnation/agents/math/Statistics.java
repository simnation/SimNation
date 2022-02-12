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
 * Forecasts provide an extrapolation of a time series  
 */
public interface Statistics {
	
	/**
	 * Adds a new data point to the time series
	 * 
	 * @param value		the data point
	 * @return the value for further usage
	 */
	double update(double value);

	/**
	 * Resets all statistics and starts a new time series
	 **/
	void reset();
	
	/**
	 * Returns the calculated average of the time series
	 * 
	 * @return the average 
	 **/
	double getAverage();
	
	/**
	 * Returns the calculated variance (squared standard deviation) of the time series
	 * 
	 * @return the variance
	 **/
	double getVariance();

}
