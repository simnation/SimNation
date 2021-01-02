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
package org.simnation.model.technology;

import org.simplesim.core.scheduling.Time;

/**
 * An investment good is a good that is not for consumption or further processing, but essential for the production process itself.
 * 
 *
 */
public class InvestmentGood extends Good {
	
	private Time apr; // asset depreciation time, 0 means infinite (like land)
	
	public InvestmentGood(String name) {
		super(name);
	}

	public Time getDepreciationTime() {
		return apr;
	}

	public void setDepreciationTime(Time apr) {
		this.apr = apr;
	}

}
