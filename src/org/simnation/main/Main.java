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
import org.simplesim.core.messaging.MessageForwardingStrategy;
import org.simplesim.core.messaging.RecursiveMessageForwarding;
import org.simplesim.core.messaging.RoutingMessageForwarding;
import org.simplesim.core.scheduling.EventQueue;
import org.simplesim.core.scheduling.HeapEventQueue;
import org.simplesim.core.scheduling.Time;
import org.simplesim.model.Agent;
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
		final MessageForwardingStrategy fs=new RecursiveMessageForwarding();
		final EventQueue<Agent> eq=new HeapEventQueue<>();
		final Simulator simulator=new SequentialDESimulator(Model.getInstance(),eq,fs);
		simulator.runSimulation(Time.MONTH);
	}

}
