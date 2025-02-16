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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.simnation.persistence.DataAccessObject;
import org.simnation.persistence.Persistable;

/**
 * The GoodSet contains the model's value chain.
 * 
 * Resources as source and Consuables as sink of the value chain.
 *
 */
public final class GoodSet implements Persistable {

	/** list of all goods representing the value chain */
	private final Set<Good> goods=new HashSet<>();
	
	/** list of all resources, acting as source nodes of a graph */
	private final Set<Good> resources=new HashSet<>();
	
	public void add(Good good) {
		goods.add(good);
		if (good.isResource()) resources.add(good);
	}

	public Set<Good> get() {
		return goods;
	}

	public void clear() {
		goods.clear();
		resources.clear();
	}

	public boolean exists(Good good) {
		return goods.contains(good);
	}

	public Set<Good> getResources() {
		return resources;
	}

	public int size() {
		return goods.size();
	}

	@Override
	public void load(DataAccessObject dao) throws Exception {
		clear(); // clear all lists
		final Collection<Good> list=dao.load(Good.class);
		for (Good good : list) add(good);
	}

	@Override
	public void save(DataAccessObject dao) throws Exception {
		dao.save(goods);
	}

	/**
	 * Creates a sorted version of the value chain. Each good can be produced based on the 
	 * predecessing goods in the list. So, resources are first elements of the list whereas
	 * the last goods are the most complex to produce and have the most added value.
	 * 
	 * @return sorted version of the value chain
	 * @throws Exception
	 
	public List<Good> asSortedList() throws Exception {
		final List<Good> nodes=new LinkedList<Good>(vc); // copy of the original value chain list
		final List<Good> predecessors=new ArrayList<Good>(vc.size()); // list of sorted elements, initially empty
		final List<Good> temp=new ArrayList<Good>();
		for (final Good iter : nodes)
			if (iter.isResource()) predecessors.add(iter);
		nodes.removeAll(predecessors);
		for (int depth=1; nodes.isEmpty()==false; depth++) {
			temp.clear();
			if (depth>Limits.MAX_PRODUCTION_DEPTH) throw new Exception(
					"Value chain exceeds maximum production depth of "+Limits.MAX_PRODUCTION_DEPTH+"! Are there cycles in the value chain?");
			for (final Good good : nodes)
				if (predecessors.containsAll(good.getPrecursorGoods())) temp.add(good);
			predecessors.addAll(temp);
			nodes.removeAll(temp);
		}
		return predecessors;
	} */

}
