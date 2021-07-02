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
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.simnation.agents.business.Money;
import org.simnation.agents.common.Batch;
import org.simnation.context.population.Citizen;
import org.simnation.context.technology.Good;
import org.simnation.model.Model;
import org.simplesim.model.State;

public class HouseholdState implements State {

	private final Map<Good,Batch> stock=new IdentityHashMap<>();
	private final HouseholdNeed[] need=null;
	List<Citizen> family;
	Money money;
	
	public HouseholdState() {
	}
	
	public Map<Good,Batch> getInventory() {
		return stock;
	}
	
	public Money getMoney() {
		return money;
	}
	
	public Citizen getFamilyMember(int index) {
		return getFamily().get(index);
	}
	
	public List<Citizen> getFamily() {
		return family;
	}
	
	public HouseholdNeed getNeed(int index) {
		return need[index];
	}
	
}