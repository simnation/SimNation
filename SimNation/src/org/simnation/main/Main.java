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

import org.simnation.model.Model;
import org.simnation.persistence.DataAccessObject;
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
		// build model
		try {
			final DataAccessObject dao=new DataAccessObject("Simulation");
			Model.getInstance().load(dao);
			dao.close();
		} catch (Exception exception) {
			exception.printStackTrace();
			System.exit(3);
		}
		// start simulation
		final ForwardingStrategy fs=new RoutedMessageForwarding(Model.getInstance());
		final Simulator simulator=new SequentialDESimulator(Model.getInstance(),fs);
		simulator.runSimulation(Time.months(1));
	}

}
