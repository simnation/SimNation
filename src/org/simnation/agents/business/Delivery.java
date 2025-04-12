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
package org.simnation.agents.business;

import org.simnation.common.Batch;
import org.simplesim.core.messaging.RoutingMessage;

public final class Delivery extends RoutingMessage {

	private final Money money;
	private final Batch batch;
	// add more content here
	
    
    public Delivery(int[] src, int[] dst, Money m, Batch b) {
        super(src,dst);
        money=m;
        batch=b; 
    }


	/**
	 * @return the money
	 */
	public Money getMoney() {
		return money;
	}


	/**
	 * @return the batch
	 */
	public Batch getBatch() {
		return batch;
	}
    
}
