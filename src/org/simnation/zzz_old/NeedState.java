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
package org.simnation.zzz_old;

import org.simnation.agents.household.NeedDefinition;
import org.simnation.common.Batch;
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
	 * @param dc the household's daily consumption of the need's satisfier
	 * @param batch the batch to consume 
	 */
	public void satisfice(NeedDefinition nd, long dc, Batch batch);
		
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