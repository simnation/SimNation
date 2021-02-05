package org.simnation.persistence;

import javax.jdo.annotations.PersistenceCapable;

import org.simnation.agents.household.HouseholdDBS;
import org.simnation.context.geography.Region;
import org.simnation.core.Time;

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
