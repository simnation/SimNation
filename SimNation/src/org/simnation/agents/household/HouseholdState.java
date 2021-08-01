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

import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.simnation.agents.business.Money;
import org.simnation.agents.common.Batch;
import org.simnation.context.technology.Good;
import org.simplesim.model.State;

public class HouseholdState implements State {

	int adults, children;
	Money money;

	public Money getMoney() { return money; }

	public int getAdults() { return adults; }

	public int getChildren() { return children; }


}