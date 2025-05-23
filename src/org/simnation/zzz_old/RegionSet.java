package org.simnation.zzz_old;

import java.util.HashSet;
import java.util.Set;

import org.simnation.persistence.DataAccessObject;
import org.simnation.persistence.Persistable;

public final class RegionSet implements Persistable {

	private final Set<RegionData> regions=new HashSet<>();
	private DistanceMatrix matrix=null;

	public void add(RegionData region) {
		regions.add(region);
	}

	public Set<RegionData> get() {
		return regions;
	}

	public RegionData get(String name) {
		for (final RegionData region : regions) if (region.getName().equalsIgnoreCase(name)) return region;
		return null;
	}

	public int getTotal() { return regions.size(); }

	public void clear() {
		for (final RegionData region : regions) region.disposeNeighbors();
		regions.clear();
		matrix=null;
	}

	public void setDistanceMatrix(DistanceMatrix m) { matrix=m; }

	public DistanceMatrix getDistanceMatrix() { return matrix; }

	public void delete() {

	}

	public void rename(String name) {

	}

	public boolean exists(String name) {
		return get(name)!=null;
	}

	@Override
	public void load(DataAccessObject dao) throws Exception {
		clear();
		regions.addAll(dao.load(RegionData.class));
	}

	@Override
	public void save(DataAccessObject dao) throws Exception {
		dao.save(regions);
	}

}
