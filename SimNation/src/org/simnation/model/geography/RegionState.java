package org.simnation.model.geography;

import java.util.List;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

import org.simnation.simulation.agents.enterprise.EnterpriseState;
import org.simnation.simulation.agents.household.HouseholdState;
import org.simplesim.model.State;;

@PersistenceCapable
public class RegionState implements State {

	@PrimaryKey
	private int id; // only used as database index !
	private String name;
	private double area=0;
	private double latitude=-1; // in radians!!!
	private double longitude=-1;

	private List<HouseholdState> householdStateList; // for persistence
	private List<EnterpriseState> firmStateList; // for persistence

	/*
	 * insert marketStates here
	 */

	public double getArea() {
		return area;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public String getName() {
		return name;
	}

	public void setArea(double value) {
		area=value;
	}

	public void setLatitude(double value) {
		latitude=value;
	}

	public void setLongitude(double value) {
		longitude=value;
	}

	public void setName(String value) {
		name=value;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id=id;
	}

	public List<HouseholdState> getHouseholdStateList() {
		return householdStateList;
	}

	public void setHouseholdStateList(List<HouseholdState> value) {
		householdStateList=value;
	}

	public List<EnterpriseState> getFirmStateList() {
		return firmStateList;
	}

	public void setFirmStateList(List<EnterpriseState> value) {
		firmStateList=value;
	}

}
