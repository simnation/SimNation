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
package org.simnation.main;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;

import org.simnation.agents.household.HouseholdDBS;
import org.simnation.context.geography.Region;
import org.simnation.context.needs.Need;
import org.simnation.context.technology.Good;
import org.simplesim.core.scheduling.Time;

/**
 * Class to set up new database structure using the JDO data model.
 */
public class InitDatabase {
	
	private static HouseholdDBS generateHousehold() {
		HouseholdDBS hh=new HouseholdDBS();
		hh.setCash(10000);
		int stock[]=new int[1];
		stock[0]=2;
		hh.setStock(stock);
		return hh;
	}
	
	private static Region generateRegion() {
		Region region = new Region();
	    region.setName("Bavaria");
	    region.setCity("Munich");
	    region.setArea(10.24d);
	    region.setLatitude(0);
	    region.setLongitude(0);
	    return region;
	}
	
	private static Good generateGoods() {
		Good flour=new Good();
		flour.setName("Flour");
		flour.setUnit("g");
		flour.setService(false);
		flour.setNeed(null);
		
		Good vegetables=new Good();
		vegetables.setName("Vegetables");
		vegetables.setUnit("g");
		vegetables.setService(false);
		vegetables.setNeed(null);
		
		Good pizza=new Good();
		pizza.setName("Pizza");
		pizza.setUnit("pc.");
		pizza.setService(false);
		
		pizza.addPrecursor(flour, 150);
		pizza.addPrecursor(vegetables, 100);
		return pizza;
	}

	/**
	 * @return
	 */
	private static Need generateNeed() {
		Need need=new Need();
		need.setName("Nutrition");
		need.setUnit("kJ");
		need.setActivationTime(Time.DAY);
		need.setFrustrationTime(new Time(3*Time.TICKS_PER_DAY));
		need.setConsumptionRate(1600d/Time.TICKS_PER_DAY);
		return need;
	}

	public static void main(String[] args) {
		PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory("simnation-database");
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx=pm.currentTransaction();
		try
		{
		    tx.begin();
		    Good pizza=generateGoods();
		    Need nutrition=generateNeed();
		    pizza.setNeed(nutrition);
		    nutrition.setSatisfier(pizza);
		    
		    pm.makePersistent(pizza);   
		    pm.makePersistent(nutrition);   
		    
		    
		    Region region=generateRegion();
		    region.getHouseholdList().add(generateHousehold());
		    pm.makePersistent(region);
		    
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

	}

}
