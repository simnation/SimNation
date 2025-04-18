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

import org.simnation.agents.business.Money;
import org.simnation.context.geography.Region;
import org.simnation.persistence.DataTransferObject;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

/**
 * Saves the {@link HouseholdState} in a form that can directly be made
 * persistent by a database. The encapsulated data is converted during the
 * initialization process of the agent's constructor (e.g. need level -->
 * events)
 *
 */
@Entity
public class HouseholdDTO implements DataTransferObject<HouseholdState> {

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	private int index;

	@OneToOne
	@JoinColumn(name="REGION_FK")
	private Region region=null; // the household's parent region
	private int adults, children;
	private long cash;
	private float extraversion; // the households affinity to risk, [safe;risky] --> [0.5;1.5]

	private float needLevel[]; // initial degree of need satisfaction [0;1]

	
	@Override
	public void convertToState(HouseholdState state) {
		state.adults=getAdults();
		state.children=getChildren();
		state.money=new Money(getCash());
		state.extraversion=getExtraversion();
	}
	
	@Override
	public void convertToDTO(HouseholdState state) { 
		adults=state.getAdults();
		children=state.getChildren();
		cash=state.getMoney().getValue();
		extraversion=state.getExtraversion();
	 }

	public Money getMoney() { return new Money(cash); }

	public long getCash() { return cash; }

	public void setCash(long cash) { this.cash=cash; }

	public void setNeedLevel(float[] value) { needLevel=value; }

	public void setNeedLevel(int index, float value) { needLevel[index]=value; }

	public float[] getNeedLevel() { return needLevel; }

	public float getNeedLevel(int index) { return needLevel[index]; }

	public Region getRegion() { return region; }

	public void setRegion(Region region) { this.region=region; }

	public int getAdults() { return adults; }

	public void setAdults(int adults) { this.adults=adults; }

	public int getChildren() { return children; }

	public void setChildren(int children) { this.children=children; }

	public int getIndex() { return index; }

	public void setIndex(int index) { this.index=index; }

	public float getExtraversion() { return extraversion; }

	public void setExtraversion(float extraversion) { this.extraversion=extraversion; }

}
