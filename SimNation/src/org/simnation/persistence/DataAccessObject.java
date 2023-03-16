package org.simnation.persistence;

import java.util.Collection;

import org.simnation.context.geography.Region;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

/**
 * Data Access Object serving as bridge to store the simulation scenario in a
 * database. Uses DataNucleus as JDO implementation and is used in the editor
 * (store) and simulator (load) likewise.
 *
 */
public final class DataAccessObject {

	private final EntityManager pm;

	public DataAccessObject(EntityManager value) {
		pm=value;

		//pm.getFetchPlan().setFetchSize(javax.jdo.FetchPlan.FETCH_SIZE_OPTIMAL);
		//pm.getFetchPlan().setMaxFetchDepth(-1);
	}

	public DataAccessObject(String pmfName) {
		this(Persistence.createEntityManagerFactory(pmfName).createEntityManager());
	}

	/**
	 * Closes the database connection and all queries.
	 */
	public void close() {
		if (pm.getTransaction().isActive()) pm.getTransaction().rollback();
		pm.close();
	}

	/**
	 * Loads a complete database table of class objects without further filtering.
	 * <p>
	 * The returned {@code Collection} is a <i>attached</i> to the database, the
	 * caller has to clone it for further usage!
	 * 
	 * @param <T>    class type
	 * @param clazz  class type
	 * @param region index of the assigned region
	 * @return collection of class objects from persistence store
	 * @throws Exception JPA or IO exception
	 */
	public <T> Collection<T> load(Class<T> clazz) throws Exception {
		final CriteriaQuery<T> cq=pm.getCriteriaBuilder().createQuery(clazz);
		return pm.createQuery(cq.select(cq.from(clazz))).getResultList();
	}

	/**
	 * Loads a set of class objects filtered by the assigned region.
	 * <p>
	 * The returned {@code Collection} is a <i>attached</i> to the database, the
	 * caller has to clone it for further usage!
	 * 
	 * @param <T>    class type
	 * @param clazz  class type
	 * @param region index of the assigned region
	 * @return collection of class objects from persistence store
	 * @throws Exception JPA or IO exception
	 */
	public <T> Collection<T> load(Class<T> clazz, Region region) throws Exception {
		final CriteriaBuilder cb=pm.getCriteriaBuilder();
		final CriteriaQuery<T> cq=cb.createQuery(clazz);
		final Root<T> root=cq.from(clazz);
		cq.select(root).where(cb.equal(root.get("region"),region));
		return pm.createQuery(cq).getResultList();
	}

	public <T> void store(Collection<T> set) throws Exception {
		pm.getTransaction().begin();
		for (T item : set) pm.persist(item);
		pm.getTransaction().commit();
	}

}
