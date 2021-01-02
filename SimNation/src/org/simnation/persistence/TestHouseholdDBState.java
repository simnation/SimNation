package org.simnation.persistence;

import javax.jdo.annotations.PersistenceCapable;

import org.simnation.core.Time;
import org.simnation.model.geography.Region;
import org.simnation.simulation.agents.household.HouseholdDBS;

//@PersistenceCapable
public class TestHouseholdDBState extends HouseholdDBS implements TestAgentDBState {

	private Region region=null;

	@Override
	public Time getDeltaT() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDeltaT(Time deltaT) {
		// TODO Auto-generated method stub

	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region=region;
	}

}
