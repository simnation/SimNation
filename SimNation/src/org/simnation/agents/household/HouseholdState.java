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

import org.simnation.agents.business.Money;
import org.simnation.agents.common.Batch;
import org.simnation.context.population.Citizen;
import org.simplesim.model.State;

public class HouseholdState implements State {

	private final IndividualNeed[] need;
	private final List<Citizen> family;
	private final Money cash;
	
	public HouseholdState(HouseholdDBS dbs) {
		int stock[]=dbs.getStock();
		need=new IndividualNeed[stock.length];
		
		// fill in need here
		
		
		
		family=new ArrayList<>(); //dbs.getFamily();
		cash=dbs.getMoney();
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
	
	public IndividualNeed getNeed(int index) {
		return need[index];
	}
	
}