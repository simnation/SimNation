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
package org.simnation.simulation.agents.household;

import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Serialized;

import org.simnation.model.population.Citizen;

/**
 * Saves the {@link HouseholdState} in a form that can directly be made
 * persistent by a database. The encapsulated data is converted during the
 * initialization process of the agent's constructor (e.g. need level -->
 * events)
 *
 */
@PersistenceCapable
public class HouseholdDBS {

	@Persistent(primaryKey="true", valueStrategy=IdGeneratorStrategy.NATIVE)
	private int id;
	private int region;
	private long cash;
	@Serialized
	private List<Citizen> family;
	@Serialized
	private float needLevel[]; // grade of initial need satisfaction

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id=id;
	}

	public int getRegion() {
		return region;
	}

	public void setRegion(int region) {
		this.region=region;
	}

	public long getCash() {
		return cash;
	}

	public void setCash(long cash) {
		this.cash=cash;
	}

	public List<Citizen> getFamily() {
		return family;
	}

	public void setFamily(List<Citizen> value) {
		family=value;
	}

	public float[] getNeedLevel() {
		return needLevel;
	}

	public void setNeedLevel(float[] needLevel) {
		this.needLevel=needLevel;
	}

}
