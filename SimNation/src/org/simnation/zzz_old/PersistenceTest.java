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
package org.simnation.zzz_old;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;

import org.simnation.context.technology.Good;
import org.simnation.context.technology.IProductionFunction;
import org.simnation.context.technology.Precursor;
import org.simnation.context.technology.ProductionTechnology;
import org.simnation.context.technology.Good.GOOD_TYPE;
import org.simnation.context.technology.Good.ResourceType;
import org.simnation.context.technology.ProductionTechnology.ProductionFunctionType;


/**
 * @author Rene Kuhlemann
 *
 */
public final class PersistenceTest {

	private static final String PERSISTENCE_UNIT_NAME="Database";
	
	final List<Good> goodSet=new ArrayList<>();
	
	private void addGood(String name) {
		final Good good=new Good(name);
		good.setGoodType(GOOD_TYPE.RESOURCE);
		good.setResourceType(ResourceType.AGRICULTURE);
		good.setUnit("pc.");
		goodSet.add(good);
		
	}
	
	private Good getGood(String name) {
		for (Good item : goodSet) if (item.getName().equals(name)) return item;
		return null;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try { new File("d:\\simulation\\data\\test.mv.db").delete();
		} catch (Exception exception) { }
		final PersistenceTest test=new PersistenceTest();
		test.addGood("Machine");
		test.addGood("Grain");
		test.addGood("Food");
		
		test.getGood("Food").addPrecursor(test.getGood("Grain"),3.247);
		test.getGood("Food").setMachine(test.getGood("Machine"));
		test.getGood("Machine").setMachine(test.getGood("Machine"));
		
		final ProductionTechnology technology=new ProductionTechnology();
		technology.setDefaultCapacity(45);
		technology.setDefaultMakespan(345);
		technology.setDefaultManhours(56.78);
		technology.setDefaultWorkers(4);
		technology.setProductionFunction(ProductionFunctionType.COBB_DOUGLAS);
		test.getGood("Grain").setProductionTechnology(technology);
		technology.setProductionFunction(ProductionFunctionType.PERFECT_SUBSTITUTION);
		test.getGood("Machine").setProductionTechnology(technology);
		test.getGood("Food").setProductionTechnology(technology);
		
		final Map<String,String> properties = new HashMap<>();
		properties.put("javax.jdo.PersistenceManagerFactoryClass","org.datanucleus.api.jdo.JDOPersistenceManagerFactory");
		properties.put("javax.jdo.option.ConnectionDriverName","org.h2.Driver");
		properties.put("javax.jdo.option.ConnectionURL","jdbc:h2:file:d:\\simulation\\data\\test");
		properties.put("javax.jdo.option.ConnectionUserName","");
		properties.put("javax.jdo.option.ConnectionPassword","");
		final PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory(properties,PERSISTENCE_UNIT_NAME);
		final PersistenceManager pm = pmf.getPersistenceManager();
		final Transaction tx=pm.currentTransaction();
		try
		{
		    tx.begin();
		    for (Good item : test.goodSet) {
		    	pm.makePersistent(item);
		    }
		    tx.commit();
		}
		finally
		{
		    if (tx.isActive())
		    {
		        tx.rollback();
		    }
		    pm.close();
		}
		// TODO Auto-generated method stub

	}

}
