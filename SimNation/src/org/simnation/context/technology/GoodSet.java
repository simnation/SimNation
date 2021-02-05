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
package org.simnation.context.technology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.simnation.model.Limits;
import org.simnation.persistence.DataAccessObject;
import org.simnation.persistence.IDataAccessObject;
import org.simnation.persistence.Persistable;

/**
 * 
 *
 */
public final class GoodSet implements Persistable {

	/** list of all goods representing the value chain */
	private final List<Good> vc=new ArrayList<>();
	
	/** list of all resources, acting as sources node of a graph */
	private final List<Good> resources=new ArrayList<>();
	
	/** list of all consumable goods, acting as sink node of a graph */
	private final List<Good> consumables=new ArrayList<>();
	
	/** lookup table assigning names to goods */
	private final Map<String,Good> map=new HashMap<>();

	public void add(Good good) throws Exception {
		if (exists(good.getName())) throw new Exception("Good "+good.getName()+" is defined twice!");
		if (vc.size()>=Limits.MAX_GOODSET_SIZE)
			throw new Exception("There is currently a limit of "+Limits.MAX_GOODSET_SIZE+" goods to ensure the simulation's stability");
		vc.add(good);
		map.put(good.getName(),good);
		if (good.isResource()) resources.add(good);
		if (good.isConsumable()) {
			good.getNeed().setSatisfier(good);
			consumables.add(good);
		}
	}

	public List<Good> asList() {
		return vc;
	}

	public void clear() {
		vc.clear();
		resources.clear();
		consumables.clear();
		map.clear();
	}

	/*public void delete() {
		getActive().untie();
		list.remove(getActive());
		map.remove(getActive().getName());
	}*/

	public boolean exists(String name) {
		return map.containsKey(name);
	}

	public Good get(int index) {
		return vc.get(index);
	}

	public Good get(String name) {
		return map.get(name);
	}

	public List<Good> getConsumables() {
		return consumables;
	}

	public List<Good> getResources() {
		return resources;
	}

	public int countGoods() {
		return vc.size();
	}

	public int countConsumables() {
		return consumables.size();
	}

	public int countResources() {
		return resources.size();
	}

	public void load(IDataAccessObject<Good> dao) throws Exception {
		clear();
		final List<Good> list=dao.loadAll();
		for (Good good : list) add(good);
	}

	/*public void rename(String text) throws Exception {
		if (exists(text)) throw new Exception("Good with name "+text+" already exists!");
		map.remove(getActive().getName());
		getActive().setName(text);
		map.put(text,getActive());
	}*/

	public void save(IDataAccessObject<Good> dao) throws Exception {
		dao.saveAll(vc);
	}

	@Override
	public void load(DataAccessObject dao) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save(DataAccessObject dao) {
		// TODO Auto-generated method stub
		
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
