package org.simnation.persistence;



// Marker interface: Can be stored into scenario database

public interface DBSavable {
	
	public void save(DataAccessObject dao) throws Exception;
	
}