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

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import org.simplesim.core.scheduling.Time;

/**
 *  Converter class used by JDO to persist {@code Time} class in database
 */
@Converter
public class JPATimeConverter implements AttributeConverter<Time,Long> {

	@Override
	public Long convertToDatabaseColumn(Time time) {
		return time.getTicks();
	}

	@Override
	public Time convertToEntityAttribute(Long value) {
		return new Time(value);
	}

}
