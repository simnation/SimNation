/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable
 * and used JSimpleSim as technical backbone for concurrent discrete event
 * simulation.
 *
 * This software is published as open source and licensed under GNU GPLv3.
 *
 * Contributors: - Rene Kuhlemann - development and initial implementation
 *
 */
package org.simnation.agents.household;

import org.simnation.agents.common.Batch;
import org.simnation.context.needs.NeedDefinition;
import org.simplesim.core.scheduling.Time;

/**
 * Specifies an instant need of a household.
 * <p>
 * There two kinds of needs in this model: instant and periodical needs. Instant
 * needs have a constant consumption rate (defined as good units per time). So
 * fewer good units lead to a shorter saturation time. The agent has to look for
 * a new batch of the satisficing good sooner. If there is no batch available,
 * the need is frustrated, which should lead to elevated efforts. If frustration
 * lasts for a longer time, frustration changes to regression. Usually, all need
 * of the existential level (see urgency) are instant needs (hunger, thirst,
 * medical support).
 * 
 */
public final class InstantNeed implements NeedState {

	// set internal time stamp if need couldn't be satisfied, set to INFINITY if not
	// save frustration time here to be able to switch between activation and frustration phase
	// if need not satisfied set new activation event and frustration time
	// time between frustration and actual time hints to actual urgency

	private Time activationTime; // at this time the satisficing good runs out

	public InstantNeed(Time actTime) {
		activationTime=actTime;
	}

	public InstantNeed() {
		this(Time.ZERO);
	}

	@Override
	public void satisfice(NeedDefinition nd, HouseholdState state, Batch batch) {
		long consumption=nd.getDailyConsumptionAdult()*state.getAdults()
				+nd.getDailyConsumptionChild()*state.getChildren();
		long deltaT=(batch.consume()*Time.TICKS_PER_DAY)/consumption;
		activationTime=activationTime.add(deltaT);
	}

	@Override
	public Time getActivationTime() { return activationTime; }

	@Override
	public float getSatisfaction() {
		return 1.0f; // instant need satisfaction always 100%
	}

}
