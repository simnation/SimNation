/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable
 * and used JSimpleSim as technical backbone for concurrent discrete event
 * simulation.
 *
 * This software is published as open source and licensed under GNU GPLv3.
 *
 * Contributors: - Rene Kuhlemann - development and initial implementation
 *
 */
package org.simnation.zzz_old;

import javax.jdo.annotations.PersistenceCapable;

import org.simnation.context.technology.Good;

@PersistenceCapable
public class Resource extends Good {

	public Resource(String name) {
		super(name);
	}

}