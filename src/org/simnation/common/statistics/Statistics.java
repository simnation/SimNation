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

/**
 * Provides time series statistics 
 */
public interface Statistics {
	
	/**
	 * Adds a new data point to the time series
	 * 
	 * @param value		the data point
	 */
	void update(double value);

	/**
	 * Resets all statistics and starts a new time series
	 * 
	 * @param avg		initial value of the average
	 **/
	void reset(double avg);
	
	/**
	 * Returns the calculated average of the time series
	 * 
	 * @return the average 
	 **/
	double getAVG();
	
	/**
	 * Returns the calculated variance of the time series
	 * 
	 * @return the variance
	 **/
	double getVAR();
	
	/**
	 * Returns the calculated standard deviation of the time series
	 * 
	 * @return the standard deviation
	 **/
	default double getSTD() { return Math.sqrt(getVAR()); }
	
	
	/**
	 * Returns the last value of the underlying time series
	 * 
	 * @return the last value
	 **/
	double getLastValue();
	

}
