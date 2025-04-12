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
package org.simnation.persistence;

import org.simplesim.model.State;

/**
 * Interface to be implemented from all DBS classes
 */
public interface DataTransferObject<X extends State> {

	/**
	 * Converts the agents state into the database state
	 * @param state
	 */
	void convertToDBS(X state);

	/**
	 * Converts the database state into the the actual agent state using the provided state.
	 *
	 * @param state a provided state to be overwritten by the database state data.
	 *
	 * @return the agent state
	 */
	X convertToState(X state);

}
