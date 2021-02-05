package org.simnation.context.geography;

import java.util.ArrayList;
import java.util.List;

import org.simnation.persistence.DataAccessObject;
import org.simnation.persistence.Persistable;

public final class RegionSet implements Persistable {

	private final List<Region> list=new ArrayList<Region>();
	private DistanceMatrix matrix=null;
	
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


	public void setDistanceMatrix(DistanceMatrix m) {
		matrix=m;
	}
	
	public DistanceMatrix getDistanceMatrix() {
		return matrix;
	}

	public void delete() {

	}

	public void rename(String name) {

	}


	public boolean exists(String name) {
		return get(name)!=null;
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
