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

import java.util.HashSet;
import java.util.Set;

import org.simnation.agents.firm.trader.TraderDBS;
import org.simnation.agents.household.HouseholdDBS;
import org.simnation.context.geography.Region;
import org.simnation.context.needs.Need;
import org.simnation.context.needs.Need.DURATION;
import org.simnation.context.needs.Need.INCIDENCE;
import org.simnation.context.needs.Need.URGENCY;
import org.simnation.context.population.Citizen;
import org.simnation.context.technology.Good;
import org.simnation.persistence.DataAccessObject;
import org.simplesim.core.scheduling.Time;

/**
 * Class to set up new database structure using the JDO data model.
 */
public class InitDatabase {

	private final Set<Need> needs=new HashSet<>();
	private final Set<Good> goods=new HashSet<>();
	private final Set<Region> regions=new HashSet<>();
	private final Set<HouseholdDBS> households=new HashSet<>();
	private final Set<TraderDBS> traders=new HashSet<>();

	private Good pizza;
	private Need nutrition;
	private Region domain;

	public static void main(String[] args) {
		DataAccessObject dao=new DataAccessObject("Scenario");
		InitDatabase id=new InitDatabase();
		try {
			id.populateRegionSet();
			dao.store(id.regions);

			id.populateGoodSet();
			id.populateNeedSet();
			id.nutrition.setSatisfier(id.pizza);
			dao.store(id.goods);
			dao.store(id.needs);

			id.households.add(id.generateHousehold());
			dao.store(id.households);
			id.traders.add(id.generateTrader());
			dao.store(id.traders);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}

	}

	private HouseholdDBS generateHousehold() {
		HouseholdDBS hh=new HouseholdDBS();
		hh.setRegion(domain);
		hh.getFamily().add(Citizen.generateRandom());
		hh.getFamily().add(Citizen.generateRandom());
		hh.setCash(10000);
		int stock[]=new int[1];
		stock[0]=34;
		hh.setStock(stock);
		return hh;
	}

	private TraderDBS generateTrader() {
		TraderDBS tr=new TraderDBS();
		tr.setRegion(domain);
		tr.setGood(pizza);
		tr.setStock(5000);
		tr.setValue(350*tr.getStock());
		tr.setQuality(0.43f);
		tr.setCash(100000);
		return tr;
	}

	private void populateRegionSet() {
		domain=new Region();
		domain.setName("Bavaria");
		domain.setCity("Munich");
		domain.setArea(10.24d);
		domain.setLatitude(0);
		domain.setLongitude(0);
		regions.add(domain);
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

		pizza.addPrecursor(flour,0.2);
		pizza.addPrecursor(vegetables,0.15);
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
