/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable 
 * and used JSimpleSim as technical backbone for concurrent discrete event simulation.
 * 
 * This software is published as open source and licensed under GNU GPLv3.
 * 
 * Contributors:
 * 	- Rene Kuhlemann - development and initial implementation
 * 
 */
package org.simnation.persistence;

/**
 * Interface to mark classes that can be made persistent by using a given data access object.
 * <p>
 * Note: This serves as an abstraction layer between model and persistence functionality.   
 */
public interface Persistable {
	
	void load(DataAccessObject dao);
	
	void save(DataAccessObject dao);
	
}
