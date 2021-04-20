package org.simnation.persistence;

import java.util.Collection;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.simnation.context.geography.Region;

/**
 * Data Access Object serving as bridge to store the simulation scenario in a
 * database. Uses DataNucleus as JDO implementation and is used in the editor
 * (store) and simulator (load) likewise.
 *
 *
 */
public final class DataAccessObject {

	private final PersistenceManager pm;

	public DataAccessObject(PersistenceManager value) {
		pm=value;
		pm.getFetchPlan().setFetchSize(javax.jdo.FetchPlan.FETCH_SIZE_OPTIMAL);
		pm.getFetchPlan().setMaxFetchDepth(-1);
		
	}

	public DataAccessObject(String pmfName) {
		this(JDOHelper.getPersistenceManagerFactory(pmfName).getPersistenceManager());
	}

	/**
	 * Closes the database connection and all queries.
	 */
	public void close() {
		if (pm.currentTransaction().isActive()) pm.currentTransaction().rollback();
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
	 * @throws Exception JDO or IO exception
	 */
	@SuppressWarnings("unchecked")
	public <T> Collection<T> load(Class<T> clazz) throws Exception {
		return (Collection<T>) pm.newQuery(clazz).execute();
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
	 * @throws Exception JDO or IO exception
	 */
	@SuppressWarnings("unchecked")
	public <T> Collection<T> load(Class<T> clazz, Region region) throws Exception {
		final Query<T> query=pm.newQuery(clazz);
		query.setFilter("this.region==regionSelector");
		query.declareParameters(Region.class.getName()+" regionSelector");
		return (Collection<T>) query.execute(region);
	}

	public void store(Collection<?> item) throws Exception {
		pm.currentTransaction().begin();
		pm.makePersistentAll(item);
		pm.currentTransaction().commit();
	}

}
