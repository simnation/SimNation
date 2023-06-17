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

import java.util.Random;

import org.simnation.agents.business.Money;
import org.simnation.agents.common.DatabaseState;
import org.simnation.context.geography.Region;
import org.simnation.model.Model;

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
public class HouseholdDBS implements DatabaseState<HouseholdState> {
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	private int index;

	@OneToOne
	@JoinColumn(name="REGION_FK")
	private Region region=null; // the household's parent region
	private int adults, children;
	private long cash;
	private float extraversion; // the households affinity to risk, [safe;risky] --> [0.5;1.5]

	private int needSatisfaction[]; // initial need satisfaction

	@Override
	public void convertToDBS(HouseholdState state) { // TODO Auto-generated method stub
	}

	@Override
	public HouseholdState convertToState(HouseholdState state) {
		state.adults=getAdults();
		state.children=getChildren();
		state.urgencyLevel=0;
		state.money=new Money(getCash());
		state.extraversion=getExtraversion();
		return state;
	}
	
	@Override
	public void generateDBS(Region reg, Random rng) { 
		region=reg;
		extraversion=rng.nextFloat()+0.5f; // [0.5; 1.5]
		adults=1;
		if (rng.nextBoolean()) adults++; // [1; 2]
		children=rng.nextInt(5);
		cash=10000+rng.nextInt(10000);
		int index=0;
		needSatisfaction=new int[Model.getInstance().getNeedCount()];
		for (NeedDefinition nd : Model.getInstance().getNeedSet()) {
			int c=nd.getDailyConsumptionAdult()*adults+nd.getDailyConsumptionChild()*children;
			needSatisfaction[index]=rng.nextInt(c*nd.getActivationDays());
			index++;
		}
	 }

	public Money getMoney() { return new Money(cash); }

	public long getCash() { return cash; }

	public void setCash(long cash) { this.cash=cash; }

	public void setNeedSatisfaction(int[] value) { needSatisfaction=value; }

	public void setNeedSatisfaction(int index, int value) { needSatisfaction[index]=value; }

	public int[] getNeedSatisfaction() { return needSatisfaction; }

	public int getNeedSatisfaction(int index) { return needSatisfaction[index]; }

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
