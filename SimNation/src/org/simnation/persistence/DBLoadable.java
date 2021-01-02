package org.simnation.persistence;


//Marker interface: Can be loaded from a scenario database

public interface DBLoadable {
	
	public void load(DataAccessObject dao);

}
