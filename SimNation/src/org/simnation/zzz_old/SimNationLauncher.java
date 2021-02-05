/*
 * SimNation is a framework to simulate economic systems based on multi-agent technology.
 *
 * This software is published as open source and licensed under the terms of GNU
 * GPLv3.
 *
 * Contributors: - Rene Kuhlemann - development and initial implementation
 *
 */
package org.simnation.zzz_old;
/*
 * 
 * LICENCE: JAMESLIC
 */

import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.Registry;
import org.jamesii.core.simulation.launch.DirectLauncher;
import org.jamesii.core.util.logging.ApplicationLogger;
import org.simnation.core.Time;
import org.simnation.model.Model;
import org.simnation.model.persistence.DataAccessObject;
import org.simnation.model.persistence.DataAccessObject.DB_TYPE;
import org.simnation.simulation.model.Root;

/**
 * 
 */
public class SimNationLauncher extends DirectLauncher {

	/**
	 * Creates and launches the simulation
	 * 
	 * @param args - command line parameters
	 */
	public static void main(String args[]) {
		final SimNationLauncher launcher=new SimNationLauncher();

		// std parameter parsing, any non std parameters have to be parsed before
		// a call to parseArgs (and have to be thereby removed from or set to an
		// empty string in the list of args)
		launcher.stdParseArgs(args);

		// set run time of simulation
		launcher.setStopTime(5*Time.DAY);

		// load scenario and create an instance of the model
		final Model model=launcher.loadScenario("E:\\git\\SimNation\\scenario","HouseholdTest","","",true);
		if (model!=null) {
			launcher.createSimulation(model);
			launcher.executeModel();
		}
		System.out.println("Finished!");
	}

	private Model loadScenario(String path,String database,String user,String pwd,boolean logging) {
		DataAccessObject dao=null;
		Model model=null;

		try {
			dao=new DataAccessObject(DB_TYPE.H2,path,database);
			Root.getInstance().load(dao);
			model=new Model(dao);
		}
		catch (final Exception e) {
			System.err.println("Model could not be initialized!");
			e.printStackTrace();
		}
		finally {
			try {
				if (dao!=null) dao.close();
			}
			catch (final Exception e) {
				System.err.println("Could not close scenario database!");
				e.printStackTrace();
			}

		}

		return model;
	}

	/**
	 * Print the default arguments plus the model specific ones!
	 */
	@Override
	public String extArgsToString() {
		return "";
	}

	/**
	 * Checks whether the given parameter is known, if known the parameter is interpreted and the function returns true,
	 * otherwise it will return the inherited function's implementation return code
	 *
	 * @return true if the parameter was handled, false otherwise
	 */
	@Override
	public boolean handleParameter(String param,String value) {
		return super.handleParameter(param,value);
	}

}
