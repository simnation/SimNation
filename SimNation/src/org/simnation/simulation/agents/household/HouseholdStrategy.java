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
package org.simnation.simulation.agents.household;

import org.simnation.simulation.agents.AgentStrategy;
import org.simplesim.core.scheduling.Time;

/**
 * Strategy of a household
 *
 */

public final class HouseholdStrategy implements AgentStrategy {
	
	private final Household household;
	private final float[] budget;
	private long totalBudget;


	public HouseholdStrategy(Household parent) {
		household=parent;
		
	}

	
	/* (non-Javadoc)
	 * @see org.simnation.simulation.agents.AgentStrategy#executeStrategy()
	 */
	public void execute(Time time) {
		// TODO Auto-generated method stub
		
	}
	
	private HouseholdState getState() {
		return household.getState();
	}
	
}
