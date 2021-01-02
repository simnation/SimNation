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
package org.simnation.simulation.business;

import org.simnation.model.technology.Batch;
import org.simplesim.core.messaging.RoutedMessage;

public final class Delivery extends RoutedMessage {

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
