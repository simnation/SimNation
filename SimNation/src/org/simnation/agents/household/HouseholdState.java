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
import org.simplesim.core.scheduling.Time;
import org.simplesim.model.State;

public class HouseholdState implements State {

	// set during initialization
	int adults, children;
	int urgencyLevel;
	Money money;
	float extraversion;

	// set at any time
	private Time startBudgetPeriod;
	private long totalBudget;
	private final int budget[];
	private final int saturationLevel[];
	private final Map<Good, Batch> stock=new IdentityHashMap<>(9);

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
	
	public double getExtraversion() { return extraversion; }

	public int getUrgencyLevel() { return urgencyLevel; }

	public void setUrgencyLevel(int value) { urgencyLevel=value; }

	public long getTotalBudget() { return totalBudget; }

	public void setTotalBudget(long value) { totalBudget=value; }

	public int getBudget(NeedDefinition nd) { return budget[nd.getIndex()]; }

	public void setBudget(NeedDefinition nd, int value) { budget[nd.getIndex()]=value; }

	public Batch getStock(NeedDefinition nd) { return stock.get(nd.getSatisfier()); }

	public void addToStock(Good good, Batch batch) { stock.get(good).merge(batch); }

	public int getSaturation(NeedDefinition nd) { return saturationLevel[nd.getIndex()]; }

	public void setSaturation(NeedDefinition nd, int value) { saturationLevel[nd.getIndex()]=value; }

	public void addToSaturation(NeedDefinition nd, int value) { saturationLevel[nd.getIndex()]+=value; }

	public Time getStartBudgetPeriod() { return startBudgetPeriod; }

	public void setStartBudgetPeriod(Time startBudgetPeriod) { this.startBudgetPeriod = startBudgetPeriod; }

}