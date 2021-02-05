/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable 
 * and used JSimpleSim as technical backbone for concurrent discrete event simulation.
 * 
 * This software is published as open source and licensed under GNU GPLv3.
 * 
 * Contributors:
 * 	- Rene Kuhlemann - development and initial implementation
 * 
 */
package org.simnation.main;

import org.simnation.agents.household.Household;
import org.simnation.agents.household.HouseholdState;
import org.simnation.model.Model;
import org.simplesim.core.messaging.ForwardingStrategy;
import org.simplesim.core.messaging.RoutedMessageForwarding;
import org.simplesim.core.scheduling.Time;
import org.simplesim.simulator.SequentialDESimulator;
import org.simplesim.simulator.Simulator;

/**
 * 
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Model.getInstance().addEntity(new Household(generateHouseholdState()));
		ForwardingStrategy fs=new RoutedMessageForwarding(Model.getInstance());
		Simulator simulator=new SequentialDESimulator(Model.getInstance(),fs);
		simulator.runSimulation(Time.MONTH);
	}
	
	private static HouseholdState generateHouseholdState() {
		HouseholdState state=new HouseholdState();
	
		return state;
	}

}
