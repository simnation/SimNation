package org.simnation.persistence;

import java.sql.DriverManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

/**
 * Data Access Object serving as bridge to store the simulation scenario in a database. Uses DataNucleus as JDO
 * implementation and is used in the editor (store) and simulator (load) likewise.
 *
 *
 */
public final class DataAccessObject {

	private final PersistenceManager pm;

	public DataAccessObject(PersistenceManager value) {
		pm=value;
	}

	public void close() {
		if (pm.currentTransaction().isActive()) pm.currentTransaction().rollback();
		pm.close();
	}

	public <T> List<T> load(Class<T> clazz) {
		return query("SELECT FROM "+clazz.getName());
	}
	
	public <T> List<T> load(Class<T> clazz,int region) {
		return query("SELECT FROM "+clazz.getName()+" WHERE region.id=="+region);
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> query(String jdoql) {
		final Query query=pm.newQuery(jdoql);
		return (List<T>) query.execute();
	}

	/**
	 * Returns the number of elements of the specific class within a region. 
	 * 
	 * @param clazz - respective class, i.e. name of table
	 * @param region - region, has to be a column in the class's table!
	 * @return number of elements
	 */
	public <T> int count(Class<T> clazz,int region) {
		final Query query=pm.newQuery("SELECT count(region) FROM "+clazz.getName()+" WHERE region.id=="+region);
		final long result=(Long) query.execute();
		return (int) result;
	}

	public void store(List<?> list) throws Exception {
		pm.currentTransaction().begin();
		pm.makePersistentAll(list);
		pm.currentTransaction().commit();
	}

}
