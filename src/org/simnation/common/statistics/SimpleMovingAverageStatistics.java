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
 * Calculates average and variance by a simple moving window.
 * 
 * @see <a href="https://fanf2.user.srcf.net/hermes/doc/antiforgery/stats.pdf">
 *      Reference for incremental calculation of mean and variance</a>
 * @see <a href="https://jonisalonen.com/2014/efficient-and-accurate-rolling-standard-deviation">Algorithm implementation</a>
 * 
 */
public final class SimpleMovingAverageStatistics implements Statistics {

	private final double buffer[];		// circular buffer
	private final int sampleSize;
	private int head, tail;				// circular buffer indices
	private volatile double average;	
	private volatile double sumVar;		// sum for variance calculation
	private volatile double lastValue;	// last value of this time series

	public SimpleMovingAverageStatistics(int size) {
		sampleSize=size;
		buffer=new double[size+1];
		reset(0);
	}

	public SimpleMovingAverageStatistics() { this(5); }

	@Override
	public void update(double value) {
		final double oldAvg=average;
		if (size()<sampleSize) {
			final double diff=value-oldAvg;
			average+=diff/(size()+1);
			sumVar+=diff*(value-average);
		} else {
			final double oldVal=removeFromTail();
			final double diff=value-oldVal;
			average+=diff/sampleSize;
			sumVar+=diff*(value-average+oldVal-oldAvg);		
		}
		addToHead(value);
	}

	@Override
	public void reset(double avg) {
		head=tail=0;
		average=avg;
		sumVar=0;
	}

	@Override
	public double getAVG() { return average; }

	@Override
	public double getVAR() { return sumVar/size(); }
	
	@Override
	public double getLastValue() { return lastValue; }
	
	private void addToHead(double value) {
		buffer[head]=value;
		head=(head+1)%buffer.length;
	}
	
	private double removeFromTail() {
		final double result=buffer[tail];
		tail=(tail+1)%buffer.length;
		return result;
	}

	private int size() {
		int diff=head-tail;
		if (diff<0) diff+=buffer.length;
		return diff;
	}

	@Override
	public String toString() {
		return "[avg="+getAVG()+", var="+getVAR()+"]";
	}

}
