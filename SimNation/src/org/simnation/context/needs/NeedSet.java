package org.simnation.context.needs;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.simnation.agents.household.Household;
import org.simnation.context.technology.Good;
import org.simnation.persistence.DataAccessObject;
import org.simnation.persistence.Persistable;

/**
 * Assigns every need type a table containing all characteristic values concerning the need Need types and tables have
 * to be defined in the simulation scenario
 * 
 *  This class defines which needs can be pursued.   
 */
public final class NeedSet implements Persistable {

	private final Set<Need> needs=new HashSet<>();

	/** list of all consumable goods, acting as sink node of a graph */
	private final Set<Good> consumables=new HashSet<>();
	

	public void add(Need need) {
		needs.add(need); // add new type
		consumables.add(need.getSatisfier());
	}

	public Set<Need> get() {
		return needs;
	}

	public void clear() {
		needs.clear();
		consumables.clear();
	}

	public boolean remove(Need nd) {
		return needs.remove(nd);
	}

	public boolean exists(Need need) {
		return needs.contains(need);
	}

	public int size() {
		return needs.size();
	}
	
	/**
	 * Return a {@code Set} of all consumable goods.
	 * <p>
	 * A {@code Need} can be satisfied by the consumption of a {@code Good}. Consumables are the set of goods that can serve to satisfice needs.
	 * 
	 * @return the set of consumable goods
	 */
	public Set<Good> getConsumables() {
		return consumables;
	}

	@Override
	public void load(DataAccessObject dao) throws Exception {
		clear();
		final Collection<Need> list=dao.load(Need.class);
		for (Need need : list) add(need);
		Household.initNeedMap(needs);
	}
	
	@Override
	public void save(DataAccessObject dao) throws Exception {
		dao.store(needs);
	}

}
