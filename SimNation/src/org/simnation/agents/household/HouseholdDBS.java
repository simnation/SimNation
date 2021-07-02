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

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Serialized;

import org.simnation.agents.business.Money;
import org.simnation.agents.common.Batch;
import org.simnation.agents.common.DatabaseState;
import org.simnation.context.geography.Region;
import org.simnation.context.population.Citizen;
import org.simplesim.model.State;

/**
 * Saves the {@link HouseholdState} in a form that can directly be made
 * persistent by a database. The encapsulated data is converted during the
 * initialization process of the agent's constructor (e.g. need level -->
 * events)
 *
 */
@PersistenceCapable
@DatastoreIdentity(strategy=IdGeneratorStrategy.IDENTITY)
public class HouseholdDBS implements DatabaseState<HouseholdState> {

	@Column(name="REGION_FK")
	private Region region=null; // the households parent region
	private long cash;
	@Serialized
	private List<Citizen> family=new ArrayList<>();
	@Serialized
	private int stock[]; // grade of initial need satisfaction

	
	public Money getMoney() {
		return new Money(cash);
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

	
	public void setStock(int index, int amount) {
		stock[index]=amount;
	}

	public void setStock(int value[]) {
		stock=value;
	}
	
	public int[] getStock() {
		return stock;
	}

	public Region getRegion() { return region; }

	public void setRegion(Region region) { this.region = region; }
	

	@Override
	public void convertToDBS(HouseholdState state) { // TODO Auto-generated method stub
	 }

	@Override
	public HouseholdState convertToState() { 
		final HouseholdState state=new HouseholdState();
		state.family=getFamily();
		state.money=new Money(getCash());
		return state;
	}

}
