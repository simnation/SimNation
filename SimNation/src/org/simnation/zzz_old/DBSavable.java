package org.simnation.zzz_old;



// Marker interface: Can be stored into scenario database

public interface DBSavable {
	
	public void save(DataAccessObject dao) throws Exception;
	
}