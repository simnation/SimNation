/*
 * SimNation is a framework to simulate economic systems using a scalable and
 * highly configurable multi-agent approach.
 *
 * This software is published as open source and licensed under GNU GPLv3.
 *
 * Contributors: - Rene Kuhlemann - development and initial implementation
 *
 */
package org.simnation.zzz_old;

import java.util.List;

/**
 * Abstraction layer for persistence of all data of the simulation (snapshot)
 *
 * @param T data transfer object specific for the DAO implementation (this could
 *          be an {@code State} or other data in form of a POJO)
 *
 */
public interface IDataAccessObject<T> {

	T load();

	List<T> loadAll();

	void save(T obj);

	void saveAll(List<T> list);

}
