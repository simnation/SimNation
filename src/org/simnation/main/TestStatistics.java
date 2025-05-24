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
package org.simnation.main;

import org.simnation.common.Statistics;

/**
 *
 */
public class TestStatistics {

	private static final double ts[]= { 25, 25, 25, 25, 26, 24, 26, 24, 26, 24, 26, 24, 26, 24, 26, 24, 26, 24, 26 };
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// final Statistics stat=new SimpleMovingAverageStatistics();
		final Statistics stat=new Statistics();
		stat.reset(25,0);
		
		for (double value : ts) {
			stat.update(value);
			System.out.println(stat.toString());
		}
		
	}

}
