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

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import org.simnation.agents.business.Money;
import org.simnation.agents.common.Batch;
import org.simnation.context.technology.Good;
import org.simnation.model.Model;
import org.simplesim.model.State;

public class HouseholdState implements State {

	int adults, children;
	int urgencyLevel;
	Money money;
	
	private long totalBudget;
	private final int budget[];
	private final int saturationLevel[];
	private final Map<Good,Batch> stock=new IdentityHashMap<>(8);


	/**
	 * @param size
	 */
	public HouseholdState(Set<NeedDefinition> needSet) { 
		budget=new int[needSet.size()];
		saturationLevel=new int[needSet.size()];
		for (NeedDefinition nd : needSet) {
			stock.put(nd.getSatisfier(),new Batch(nd.getSatisfier()));
			setSaturation(nd,0);
		}
	}

	public Money getMoney() { return money; }

	public int getAdults() { return adults; }

	public int getChildren() { return children; }
	
	public int getUrgencyLevel() { return urgencyLevel; }
	
	public void setUrgencyLevel(int value) { urgencyLevel=value; }
	
	public long getTotalBudget() { return totalBudget; }

	public void setTotalBudget(long value) { totalBudget=value; }

	public int getBudget(int index) { return budget[index]; }

	public void setBudget(NeedDefinition nd, int value) { budget[nd.getIndex()]=value; }

	public Batch getStock(NeedDefinition nd) { return stock.get(nd.getSatisfier()); }

	public void addToStock(Good good, Batch batch) { stock.get(good).merge(batch); }
	
	public int getSaturation(NeedDefinition nd) { return saturationLevel[nd.getIndex()]; }

	public void setSaturation(NeedDefinition nd, int value) { saturationLevel[nd.getIndex()]=value; }
	
	public void addToSaturation(NeedDefinition nd, int value) { saturationLevel[nd.getIndex()]+=value; }	
	
}