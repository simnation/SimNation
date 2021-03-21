package org.simnation.persistence;

import java.util.List;
import java.util.Set;

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
	
	public DataAccessObject(String persistenceUnitName) {
		pm=JDOHelper.getPersistenceManagerFactory(persistenceUnitName).getPersistenceManager();
	}
	
	public void close() {
		if (pm.currentTransaction().isActive()) pm.currentTransaction().rollback();
		pm.close();
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> load(Class<T> clazz) throws Exception {
		final Query<T> query=pm.newQuery("SELECT FROM "+clazz.getName());
		final List<T> result=query.executeList();
		query.closeAll();
		return result;
	}
	
	public void store(Set<?> set) throws Exception {
		pm.currentTransaction().begin();
		pm.makePersistentAll(set);
		pm.currentTransaction().commit();
	}


	public void store(List<?> list) throws Exception {
		pm.currentTransaction().begin();
		pm.makePersistentAll(list);
		pm.currentTransaction().commit();
	}

}
