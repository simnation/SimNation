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

import org.simnation.common.Batch;
import org.simnation.context.technology.Good;
import org.simplesim.core.scheduling.Time;

public class Service extends Batch {

	private final Time period;
	
	public Service(Good good,int amount,int price,float quality, Time time) {
		super(good,amount,price,quality);
		period=time;
	}

	public Time getPeriod() {
		return period;
	}

}