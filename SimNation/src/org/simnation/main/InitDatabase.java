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
import org.simnation.context.geography.RegionSet;
import org.simnation.context.needs.Need;
import org.simnation.context.needs.Need.DURATION;
import org.simnation.context.needs.Need.INCIDENCE;
import org.simnation.context.needs.Need.URGENCY;
import org.simnation.context.needs.NeedSet;
import org.simnation.context.population.Citizen;
import org.simnation.context.technology.Good;
import org.simnation.context.technology.GoodSet;
import org.simnation.persistence.DataAccessObject;
import org.simplesim.core.scheduling.Time;

/**
 * Class to set up new database structure using the JDO data model.
 */
public class InitDatabase {
	
	private final NeedSet needs=new NeedSet();
	private final GoodSet goods=new GoodSet();
	private final RegionSet regions=new RegionSet();
	
	private Good pizza;
	private Need nutrition;
	
	public static void main(String[] args) {
		DataAccessObject dao=new DataAccessObject("simnation-database");
		InitDatabase id=new InitDatabase();
		try
		{
			id.populateRegionSet();
			id.regions.get(0).getHouseholdList().add(id.generateHousehold());
			id.regions.save(dao);
			
			id.populateGoodSet();
			id.populateNeedSet();
			id.nutrition.setSatisfier(id.pizza);
			
			id.goods.save(dao);
			id.needs.save(dao);
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
		    dao.close();
		}
	
	}

	private HouseholdDBS generateHousehold() {
		HouseholdDBS hh=new HouseholdDBS();
		hh.getFamily().add(Citizen.generateRandom());
		hh.getFamily().add(Citizen.generateRandom());
		hh.setCash(10000);
		int stock[]=new int[1];
		stock[0]=34;
		hh.setStock(stock);
		return hh;
	}
	
	private void populateRegionSet() {
		Region region = new Region();
	    region.setName("Bavaria");
	    region.setCity("Munich");
	    region.setArea(10.24d);
	    region.setLatitude(0);
	    region.setLongitude(0);
	    regions.add(region);
	}
	
	private void populateGoodSet() throws Exception {
		Good flour=new Good();
		flour.setName("Flour");
		flour.setUnit("kg");
		flour.setService(false);
		goods.add(flour);
		
		Good vegetables=new Good();
		vegetables.setName("Vegetables");
		vegetables.setUnit("kg");
		vegetables.setService(false);
		goods.add(vegetables);
		
		pizza=new Good();
		pizza.setName("Pizza");
		pizza.setUnit("pc.");
		pizza.setService(false);
		
		pizza.addPrecursor(flour, 0.2);
		pizza.addPrecursor(vegetables, 0.15);
		goods.add(pizza);
	}

	/**
	 * @return
	 */
	private void populateNeedSet() {
		nutrition=new Need();
		nutrition.setName("Nutrition");
		nutrition.setUnit("kJ");
		nutrition.setActivationTime(Time.DAY);
		nutrition.setFrustrationTime(new Time(3*Time.TICKS_PER_DAY));
		nutrition.setSaturation(2400);
		nutrition.setConsumption(9200);
		nutrition.setIncidence(INCIDENCE.CONTINUOUSLY);
		nutrition.setUrgency(URGENCY.EXISTENTIAL);
		nutrition.setDuration(DURATION.INSTANTLY);
		needs.add(nutrition);
	}

}
