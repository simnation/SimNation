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
package org.simnation.agents.household;

import org.simnation.agents.common.Batch;
import org.simnation.context.needs.NeedDefinition;
import org.simplesim.core.scheduling.Time;

/**
 * Specifies a household's need
 */
public interface NeedState {

	/**
	 * Consume a batch to satisfy this need.
	 * <p>
	 * Recalculates frustration time and satisfaction
	 * 
	 * @param nd the need's definition
	 * @param state the household's state
	 * @param batch the batch to consume 
	 */
	void satisfice(NeedDefinition nd, HouseholdState state, Batch batch);

	/**
	 * @return
	 */
	Time getActivationTime();
	
	/**
	 * @param actualTime
	 * @return
	 */
	default boolean isActivated(Time actualTime) {
		return getActivationTime().getTicks()<actualTime.getTicks();
	}
	
	/**
	 * @param actualTime
	 * @return
	 */
	default boolean isFrustrated(Time actualTime, NeedDefinition nd) {
		final long delta=actualTime.getTicks()-getActivationTime().getTicks();
		return delta>nd.getFrustrationDays()*Time.TICKS_PER_DAY;
	}
	

	/**
	 * @return
	 */
	float getSatisfaction();

}