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
package org.simnation.agents.common;

/**
 * Marks types that have merge and split functionality
 *
 * @see Batch
 * @see Money
 */
public interface Mergable<T extends Mergable<T>> {
	
	public T split(long amount);
	
	public long merge(T other);

}
