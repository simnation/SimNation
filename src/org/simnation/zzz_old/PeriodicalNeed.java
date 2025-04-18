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
package org.simnation.zzz_old;

import org.simnation.agents.household.Need;
import org.simnation.common.Batch;
import org.simplesim.core.scheduling.Time;

/**
 * Specifies a periodical need of a household.
 * <p>
 * There two kinds of needs in this model: instant and periodical needs.
 * Periodical needs have a constant activation time. So fewer good units do NOT
 * lead to a shorter saturation time but to a lower saturation level and thus to
 * a lower satisfaction level. Usually, all needs above the existential level
 * (see urgency) are periodic needs (housing, communication, entertainment).
 * 
 * 
 */
public final class PeriodicalNeed implements NeedState {

	// set internal time stamp if need couldn't be satisfied, set to INFINITY if not
	// save frustration time here to be able to switch between activation and frustration phase
	// if need not satisfied set new activation event and frustration time
	// time between frustration and actual time hints to actual urgency

	private Time activationTime; // at this time the satisficing good runs out
	private float satisfaction=1.0f; // instant need satisfaction always 100%

	public PeriodicalNeed(Time actTime) {
		activationTime=actTime;
	}

	public PeriodicalNeed() {
		this(Time.ZERO);
	}

	@Override
	public void satisfice(Need nd, long dailyConsumption, Batch batch) {
		satisfaction=(float) batch.consume()/dailyConsumption;
		activationTime=activationTime.add(nd.getActivationDays()*Time.TICKS_PER_DAY);
	}

	@Override
	public Time getActivationTime() { return activationTime; }

	@Override
	public float getSatisfaction() { return satisfaction; }

}
