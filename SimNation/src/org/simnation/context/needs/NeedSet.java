package org.simnation.context.needs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.simnation.model.Limits;
import org.simnation.persistence.DataAccessObject;
import org.simnation.persistence.Persistable;

/**
 * Assigns every need type a table containing all characteristic values concerning the need Need types and tables have
 * to be defined in the simulation scenario
 */
public final class NeedSet implements Persistable {

	private final List<Need> list=new ArrayList<>(Limits.MAX_NEEDSET_SIZE);
	private final Map<String, Need> map=new HashMap<>((4*Limits.MAX_NEEDSET_SIZE)/3);

	public boolean add(Need nd) {
		if ((exists(nd.getName()))||(size()>=Limits.MAX_NEEDSET_SIZE)) return false;
		list.add(nd); // add new type
		map.put(nd.getName(),nd);// for referencing by name
		return true;
	}

	public List<Need> asList() {
		return Collections.unmodifiableList(list);
	}

	public void clear() {
		list.clear();
		map.clear();
	}

	public boolean remove(String text) {
		final Need nd=get(text);
		if (nd==null) return false;
		return remove(nd);
	}

	public boolean remove(Need nd) {
		if (!list.remove(nd)) return false;
		map.remove(nd.getName());
		return true;
	}

	public boolean exists(String name) {
		return map.containsKey(name);
	}

	public Need get(String name) {
		return map.get(name);
	}

	public int size() {
		return list.size();
	}
	
	public Iterator<Need> iterator() {
		return list.iterator();
	}

	@Override
	public void load(DataAccessObject dao) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save(DataAccessObject dao) {
		// TODO Auto-generated method stub
		
	}

}
