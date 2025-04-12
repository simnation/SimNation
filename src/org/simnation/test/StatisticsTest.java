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
package org.simnation.test;

import org.simnation.statistics.WarehouseStatistics;

/**
 *
 */
public class StatisticsTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		WarehouseStatistics stat=new WarehouseStatistics();
		stat.setSmoothingFactor(0.2f);
		for (int i=0; i<100; i++) {
			long r=Math.round(Math.random()*100);
			stat.update(r,false);
			System.out.println("order: "+r+" forcast: "+stat.forecastDelivery(0.8f)+" | "+stat.toString());
		}
		System.out.println("forecast: "+stat.forecastStockLevel(0.95f));
		// TODO Auto-generated method stub

	}

}
