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
 *
 */
public class TestStatistics {

	private static final double ts[]= { 71, 34, -2, 17, 65, 103, 53, 78, 7, 0, 75, 34, 23, 22, 81 };
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// final Statistics stat=new SimpleMovingAverageStatistics();
		final Statistics stat=new ExponentialSmoothingStatistics();
		
		
		for (double value : ts) {
			stat.update(value);
			System.out.println(stat.toString());
		}
		
	}

}
