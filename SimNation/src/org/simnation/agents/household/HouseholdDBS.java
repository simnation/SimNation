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

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Serialized;

import org.simnation.agents.business.Money;
import org.simnation.agents.common.DatabaseState;
import org.simnation.context.geography.Region;

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
	private int adults, children;
	private long cash;

	@Serialized
	private int stock[]; // grade of initial need satisfaction

	public Money getMoney() { return new Money(cash); }

	public long getCash() { return cash; }

	public void setCash(long cash) { this.cash=cash; }

	public void setStock(int index, int amount) {
		stock[index]=amount;
	}

	public void setStock(int value[]) { stock=value; }

	public int[] getStock() { return stock; }

	public Region getRegion() { return region; }

	public void setRegion(Region region) { this.region=region; }

	@Override
	public void convertToDBS(HouseholdState state) { // TODO Auto-generated method stub
	}

	@Override
	public HouseholdState convertToState() {
		final HouseholdState state=new HouseholdState();
		state.adults=getAdults();
		state.children=getChildren();
		state.money=new Money(getCash());
		return state;
	}

	public int getAdults() { return adults; }

	public void setAdults(int adults) { this.adults=adults; }

	public int getChildren() { return children; }

	public void setChildren(int children) { this.children=children; }

}
