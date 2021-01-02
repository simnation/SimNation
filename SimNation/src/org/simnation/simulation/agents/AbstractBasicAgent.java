/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable and used JSimpleSim as technical
 * backbone for concurrent discrete event simulation. This software is published as open source and licensed under GNU
 * GPLv3. Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simnation.simulation.agents;

import org.simplesim.core.messaging.Message;
import org.simplesim.core.messaging.RoutedMessage;
import org.simplesim.core.scheduling.HeapEventQueue;
import org.simplesim.core.scheduling.Time;
import org.simplesim.model.RoutingAgent;
import org.simplesim.model.State;

/**
 * Base class for all agents of the simnation model, providing common functionality and variables
 *
 * @param S type of the agent state containing all state variables
 * @param E event type
 */
public abstract class AbstractBasicAgent<S extends State, E extends Enum<E>> extends RoutingAgent<S, E> {

	/**
	 * Exception to be thrown if an unknown event occurs is returned from the event queue
	 */
	@SuppressWarnings("serial")
	public static class UnhandledEventType extends RuntimeException {
		public UnhandledEventType(String message) {
			super(message);
		}
	}

	/**
	 * Exception to be thrown if an incoming message cannot be handled
	 */
	@SuppressWarnings("serial")
	public static class UnhandledMessageType extends RuntimeException {
		public UnhandledMessageType(String message) {
			super(message);
		}
	}

	public AbstractBasicAgent(S state) {
		super(new HeapEventQueue<E>(),state);
	}

	@Override
	protected Time doEvent(Time time) {
		// process messages
		while (getInport().hasMessages()) handleMessage(getInport().poll());
		// process events
		while (getEventQueue().getMin().equals(time)) handleEvent(getEventQueue().dequeue(),time);
		executeStrategy(time);
		return getTimeOfNextEvent();
	}

	/**
	 * Handles the content of a due message.
	 *
	 * @param msg the next message to be handled by the agent
	 */
	protected abstract void handleMessage(Message<?> msg);

	/**
	 * Handles a due event.
	 *
	 * @param <E>   the type of the event
	 * @param event the event as such (containing also additional information)
	 * @param time  the time stamp of the event
	 */
	protected abstract void handleEvent(E event, Time time);

	/**
	 * Executes the agent's strategy thus implements the agent's behavior
	 *
	 * @param time the current simulation time
	 */
	protected abstract void executeStrategy(Time time);

	/**
	 * Sends a message via the agent's outport.
	 *
	 * @param msg the message
	 */
	protected final void sendMessage(Message<?> msg) {
		getOutport().write(msg);
	}
	
	/**
	 * Sends a message via the agent's outport.
	 *
	 * @param src sender of the message
	 * @param dst receiver of the message
	 * @param content the content of the message
	 * 
	 */
	protected final void sendMessage(int[] src,int[] dst, Object content) {
		sendMessage(new RoutedMessage(src,dst,content));
	}

	public void enqueueEvent(E event, Time time) {
		getEventQueue().enqueue(event,time);
	}

}
