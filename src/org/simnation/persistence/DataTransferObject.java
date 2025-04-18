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
 * A DTO stores all data to make the agent state and event queue persistent.
 */
public interface DataTransferObject<S extends State> {
	
	void convertToState(S state);
	
	void convertToDTO(S state);

}
