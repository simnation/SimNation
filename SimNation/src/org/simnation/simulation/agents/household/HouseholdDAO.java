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
package org.simnation.simulation.agents.household;

import java.util.List;

import javax.jdo.PersistenceManagerFactory;

import org.simnation.persistence.IDataAccessObject;

/**
 * Makes the {@code HouseholdState} accessible via the persistence layer used.
 *
 * @see HouseholdState
 *
 */
public final class HouseholdDAO implements IDataAccessObject<HouseholdDBS> {

	private final PersistenceManagerFactory pmf;

	private int region=0;

	public HouseholdDAO(PersistenceManagerFactory factory) {
		pmf=factory;
	}

	@Override
	public HouseholdDBS load() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<HouseholdDBS> loadAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save(HouseholdDBS obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveAll(List<HouseholdDBS> list) {
		// TODO Auto-generated method stub

	}

	public int getRegion() {
		return region;
	}

	public void selectRegion(int reg) {
		region=reg;
	}

}
