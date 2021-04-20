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
package org.simnation.agents.common;

import org.simplesim.model.State;

/**
 * Interface to be implemented from all DBS classes
 */
public interface DatabaseState<X extends State> {
	
	/**
	 * Converts the agents state into the database state
	 * @param state
	 */
	void convertToDBS(X state);
	
	/**
	 * Converts the database state into the the actual agent state.
	 * 
	 * @return the agent state
	 */
	X convertToState();

}
