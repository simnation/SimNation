package org.simnation.persistence;

import org.simnation.core.Time;

interface TestAgentDBState {

	// delta t to take data snap shots during simulation
	public Time getDeltaT();

	public void setDeltaT(Time deltaT);

}
