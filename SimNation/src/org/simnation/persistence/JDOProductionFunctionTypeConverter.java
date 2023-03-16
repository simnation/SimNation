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

import org.simnation.context.technology.ProductionTechnology.ProductionFunctionType;

import jakarta.persistence.Converter;

@Converter
public final class JDOProductionFunctionTypeConverter implements AttributeConverter<ProductionFunctionType, String> {

	@Override
	public String convertToDatabaseColumn(ProductionFunctionType pfd) {
		return pfd.toString();
	}

	@Override
	public ProductionFunctionType convertToEntityAttribute(String name) {
		for (final ProductionFunctionType element : ProductionFunctionType.values())
			if (element.toString().equals(name)) return element;
		return null;
	}

}