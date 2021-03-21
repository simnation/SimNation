package org.simnation.context.geography;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.simnation.agents.household.HouseholdDBS;

@PersistenceCapable
public class Region {

	@Persistent(primaryKey="true",valueStrategy=IdGeneratorStrategy.IDENTITY)
	private int index;


	private String name="Berlin";
	private String city="City";
	private double area=20.5;
	private double latitude=NOT_INITIALIZED; // in radians!!!
	private double longitude=NOT_INITIALIZED;
	
	@Element(column="REGION_FK")
	private Set<HouseholdDBS> householdList=new HashSet<>();
	
	
	static final double NOT_INITIALIZED=-1.0;
	private static final double EARTH_RADIUS=6371.0;

	@NotPersistent
	private int population=1000;
	@NotPersistent
	private final List<Region> neighbor_list=new ArrayList<>();
	@NotPersistent
	private final List<Double> distance_list=new ArrayList<>();
	
	
	public void addNeighbor(Region dest) {
		final double distance=this.distanceTo(dest);
		distance_list.add(distance);
		dest.distance_list.add(distance);
		neighbor_list.add(dest);
		dest.neighbor_list.add(this);
	}

	/*public void check(SanityCheck sc) {
		if (latitude==NOT_INITIALIZED&&longitude==NOT_INITIALIZED) {
			sc.addInfo("Region "+getName()+" is not initialized");
			sc.fail();
		} else if (latitude<0||latitude>2*Math.PI||longitude<0||longitude>2*Math.PI) {
			sc.addInfo("Region "+getName()+" has invalid coordinates");
			sc.fail();
		}
	}*/

	public void disposeNeighbors() {
		neighbor_list.clear();
		distance_list.clear();
	}

	public double distanceTo(double lng,double lat) {
		assert latitude!=NOT_INITIALIZED&&longitude!=NOT_INITIALIZED&&lat!=NOT_INITIALIZED&&lng!=NOT_INITIALIZED;
		final double temp=Math.sin(latitude)*Math.sin(lat)+Math.cos(latitude)*Math.cos(lat)*Math.cos(longitude-lng);
		return Math.acos(temp)*EARTH_RADIUS;
	}

	public double distanceTo(Region dest) {
		return distanceTo(dest.longitude,dest.latitude);
	}

	public double getArea() {
		return area;
	}

	public String getCity() {
		return city;
	}

	public List<Double> getDistanceList() {
		return distance_list;
	}

	// returns distance to neighbor or POSITIVE_INFINITY if region is not a neighbor
	public double getDistanceToNeighbor(Region region) {
		final int index=neighbor_list.indexOf(region);
		if (index==-1) return Double.POSITIVE_INFINITY;
		else return distance_list.get(index);
	}

	public int getEnterpriseCount() {
		return 0;
	}

	public double getLatitude() {
		return Math.toDegrees(latitude);
	}

	public double getLongitude() {
		return Math.toDegrees(longitude);
	}

	public List<Region> getNeighborList() {
		return neighbor_list;
	}

	public int getPopulation() {
		return population;
	}

	public String getName() {
		return name;
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setArea(double value) {
		area=value;
	}

	public void setCity(String value) {
		city=value;
	}

	public void setLatitude(double value) {
		latitude=Math.toRadians(value);
	}

	public void setLongitude(double value) {
		longitude=Math.toRadians(value);
	}

	public void setPopulation(int value) {
		population=value;
	}

	public void setName(String value) {
		name=value;
	}

	@Override
	public String toString() {
		final String neighbors="";
		for (final Region iter : neighbor_list)
			neighbors.concat(String.format("# %s(%g km) ",iter.city,getDistanceToNeighbor(iter)));
		return city+" ["+longitude+"|"+latitude+"] "+neighbors;
	}

	/**
	 * @return the householdList
	 */
	public Set<HouseholdDBS> getHouseholdList() {
		return householdList;
	}

	/**
	 * @param householdList the householdList to set
	 */
	public void setHouseholdList(Set<HouseholdDBS> householdList) {
		this.householdList = householdList;
	}

}
