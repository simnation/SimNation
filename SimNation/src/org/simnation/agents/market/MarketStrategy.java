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

import java.util.List;

import org.simnation.agents.business.Demand;
import org.simnation.agents.business.Supply;

/**
 * Strategy interface for various market clearing algorithms.
 */
interface MarketStrategy<T> {
	
	static class PriceVolumeDataPoint { 
		
		final private double price;
		private long volume=0;
		
		PriceVolumeDataPoint(double p) { price=p; }
		
		void addVolume(long value) { volume+=value; }

		double getPrice() { return price; }

		long getVolume() { return volume; }
		
	}
	
	/**
	 * Settles demand and supply, calculating market price and traded volume.
	 * 
	 * @param market
	 * @param demand
	 * @param supply
	 * @param statistics
	 */
	PriceVolumeDataPoint doMarketClearing(Market<T> market, List<Demand<T>> demand, List<Supply<T>> supply);

}
