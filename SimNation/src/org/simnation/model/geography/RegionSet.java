package org.simnation.model.geography;

import java.util.ArrayList;
import java.util.List;

import org.simnation.core.SanityCheck;
import org.simnation.model.IContextSet;
import org.simnation.persistence.DBSavable;
import org.simnation.persistence.DataAccessObject;

public class RegionSet implements IContextSet<Region>, DBSavable {

	private final List<Region> list=new ArrayList<Region>();
	private DistanceMatrix matrix=null;
	private static Region active;

	public void add(Region region) {
		list.add(region);
	}

	public List<Region> asList() {
		return list;
	}

	public Region get(int index) {
		return list.get(index);
	}

	public Region get(String name) {
		for (final Region region : list)
			if (region.getName().equalsIgnoreCase(name)) return region;
		return null;
	}

	public int getTotal() {
		return list.size();
	}

	public void clear() {
		for (final Region region : list)
			region.disposeNeighbors();
		list.clear();
		matrix=null;
	}

	public String[] getComboList() {
		return new String[0];
	}

	public void setDistanceMatrix(DistanceMatrix m) {
		matrix=m;
	}
	
	public DistanceMatrix getDistanceMatrix() {
		return matrix;
	}

	public void save(DataAccessObject dao) throws Exception {
		for (int i=0; i<list.size();i++) list.get(i).setId(i);
		dao.store(list);
	}

	public void load(DataAccessObject dao) {
		clear();
		final List<Region> result=dao.load(Region.class);
		list.addAll(result);
	}

	public void activate(Region element) {
		active=element;
	}

	public void delete() {

	}

	public void rename(String name) {

	}

	public Region getActive() {
		return active;
	}

	public boolean exists(String name) {
		return get(name)!=null;
	}

	/* (non-Javadoc)
	 * @see org.simnation.model.IContextSet#check(org.simnation.core.SanityCheck)
	 */
	public void check(SanityCheck sc) {
		// TODO Auto-generated method stub
		
	}

}
