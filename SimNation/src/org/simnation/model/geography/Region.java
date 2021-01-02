package org.simnation.model.geography;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

import org.simnation.core.AbstractJavaBean;
import org.simnation.simulation.agents.enterprise.EnterpriseState;
import org.simnation.simulation.agents.household.HouseholdState;;

@PersistenceCapable
public class Region extends AbstractJavaBean  {

	public enum PROPERTY implements IPropertyName {
		REGION("region"),
		CITY("city"),
		POPULATION("population"),
		AREA("area"),
		LATITUDE("latitude"),
		LONGITUDE("longitude");

		private String name;

		public String getName() {
			return name;
		}

		PROPERTY(String n) {
			name=n;
		}

	}

	@PrimaryKey
	private int id; // only used as database index !
	private String name;
	private String city;
	private int population=0;
	private double area=0;
	private double latitude=NOT_INITIALIZED; // in radians!!!
	private double longitude=NOT_INITIALIZED;
	
	private Collection<HouseholdState> householdStates; // for persistence
	private Collection<EnterpriseState> firmStates; // for persistence

	@NotPersistent
	static final double NOT_INITIALIZED=-1.0;
	private static final double EARTH_RADIUS=6371.0;

	private final List<Region> neighbor_list=new ArrayList<Region>();
	private final List<Double> distance_list=new ArrayList<Double>();

	public Region(String region) {
		this.name=region;
	}

	public Region() {
	}

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

	public void setArea(double value) {
		firePropertyChange(PROPERTY.AREA.getName(),area,area=value);
	}

	public void setCity(String value) {
		firePropertyChange(PROPERTY.CITY.getName(),city,city=value);
	}

	public void setLatitude(double value) {
		firePropertyChange(PROPERTY.LATITUDE.getName(),latitude,latitude=Math.toRadians(value));
	}

	public void setLongitude(double value) {
		firePropertyChange(PROPERTY.LONGITUDE.getName(),longitude,longitude=Math.toRadians(value));
	}

	public void setPopulation(int value) {
		firePropertyChange(PROPERTY.POPULATION.getName(),population,population=value);
	}

	public void setName(String value) {
		firePropertyChange(PROPERTY.REGION.getName(),name,name=value);
	}

	public String toString() {
		final String neighbors="";
		for (final Region iter : neighbor_list)
			neighbors.concat(String.format("# %s(%g km) ",iter.city,getDistanceToNeighbor(iter)));
		return city+" ["+longitude+"|"+latitude+"] "+neighbors;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id=id;
	}

}
