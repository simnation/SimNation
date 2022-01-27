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
package org.simnation.agents.market;

/**
 * Contains relevant data of the last market clearing
 */
public final class MarketStatistics {
	
	private final double price;
	private long volume=0;
	
	MarketStatistics(double p) {
		price=p;
	}
	
	public double getPrice() { return price; }
	
	public long getVolume() { return volume; }
	
	public long getTurnover() { return Math.round(getPrice()*getVolume()); }
	
	public void addVolume(long value) { volume+=value; }

}
