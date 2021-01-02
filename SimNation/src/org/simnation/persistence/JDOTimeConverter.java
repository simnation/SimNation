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
package org.simnation.persistence;

import javax.jdo.AttributeConverter;

import org.simplesim.core.scheduling.Time;

/**
 *  *
 */
public class JDOTimeConverter implements AttributeConverter<Time,Long> {

	public Long convertToDatastore(Time time) {
		return time.getTicks();
	}

	public Time convertToAttribute(int value) {
		return new Time(value);
	}
	
	public Time convertToAttribute(Long value) {
		return new Time(value);
	}

}
