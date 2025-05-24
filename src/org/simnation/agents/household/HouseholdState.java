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
import org.simnation.agents.household.Need.URGENCY;
import org.simnation.model.Model;
import org.simplesim.core.scheduling.Time;
import org.simplesim.model.State;

public class HouseholdState implements State {

	// set during initialization
	int adults, children;
	
	Money money;
	float extraversion;

	// set at during simulation
	private Time budgetPeriodStart;
	private long totalBudget;
	private final int budget[]; // budgets per need
	private final int needLevel[]; // saturation
	private int urgencyLevel;


	public HouseholdState() {
		budget=new int[Model.getInstance().getNeeds().size()];
		needLevel=new int[Model.getInstance().getNeeds().size()];
	}

	Money getMoney() { return money; }

	int getAdults() { return adults; }

	int getChildren() { return children; }

	float getExtraversion() { return extraversion; }

	int getUrgencyLevel() { return urgencyLevel; }

	void setUrgencyLevel(int value) { urgencyLevel=value; }

	long getTotalBudget() { return totalBudget; }

	void setTotalBudget(long value) { totalBudget=value; }

	int getBudget(Need nd) { return budget[nd.getIndex()]; }

	void setBudget(Need nd, int value) { budget[nd.getIndex()]=value; }

	int getNeedLevel(Need nd) { return needLevel[nd.getIndex()]; }

	void setNeedLevel(Need nd, int value) { needLevel[nd.getIndex()]=value; }
	
	void decreaseNeedLevel(Need nd, int value) { needLevel[nd.getIndex()]-=value; }
	
	void increaseNeedLevel(Need nd, int value) { needLevel[nd.getIndex()]+=value; }
	
	Time getBudgetPeriodStart() { return budgetPeriodStart; }

	void setBudgetPeriodStart(Time value) { budgetPeriodStart = value; }

}