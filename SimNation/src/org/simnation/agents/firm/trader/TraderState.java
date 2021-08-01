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
package org.simnation.agents.firm.trader;

import org.simnation.agents.business.Money;
import org.simnation.agents.firm.common.Storage;
import org.simnation.context.technology.Good;
import org.simplesim.model.State;

/**
 * 
 */
public final class TraderState implements State {

	private final Storage storage;
	private final Money money;
	private float margin; // price mark-up factor, always >1.0f
	private float serviceLevel; // service level for inventory management 0.5<sl<1.0

	public TraderState(Good good, Money m) {
		storage=new Storage(good);
		money=m;
	}

	public Storage getStorage() { return storage; }

	public float getMargin() { return margin; }

	public void setMargin(float value) { margin=value; }

	public Money getMoney() { return money; }

	public float getServiceLevel() { return serviceLevel; }

	public void setServiceLevel(float value) { serviceLevel=value; }

}
