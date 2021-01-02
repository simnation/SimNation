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

import org.simnation.model.population.Citizen;
import org.simnation.model.technology.Batch;
import org.simnation.simulation.business.Money;
import org.simnation.simulation.model.Root;
import org.simnation.simulation.model.RootDomain;
import org.simplesim.model.State;

public class HouseholdState implements State {

	private final Batch[] stock; // stores the household's stock, may also contain Service 
	private List<Citizen> family;
	private Money cash;
	
	public HouseholdState() {
		stock=new Batch[RootDomain.getInstance().getNeedDefinitionSet().size()];
	}

	public Money getCash() {
		return cash;
	}
	
	public Citizen getFamilyMember(int index) {
		return getFamily().get(index);
	}
	
	public List<Citizen> getFamily() {
		return family;
	}
	
	public Batch getStock(int index) {
		return stock[index];
	}
	
	void setCash(Money cash) {
		this.cash=cash;
	}

	void setFamily(List<Citizen> value) {
		family=value;
	}

}