/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable and used JSimpleSim as technical
 * backbone for concurrent discrete event simulation. This software is published as open source and licensed under GNU
 * GPLv3. Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simnation.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.simnation.agents.firm.trader.Trader;
import org.simnation.agents.firm.trader.TraderDBS;
import org.simnation.agents.household.Household;
import org.simnation.agents.household.HouseholdDBS;
import org.simnation.agents.household.NeedDefinition;
import org.simnation.agents.market.GoodsMarketB2C;
import org.simnation.context.geography.Region;
import org.simnation.context.technology.Good;
import org.simnation.persistence.DataAccessObject;
import org.simnation.persistence.Persistable;
import org.simplesim.model.RoutingDomain;

/**
 * The Model is the top-level domain of the economy and thus designed as a
 * singleton.
 * <p>
 * Parts of the model level are:
 * <ul>
 * <li>B2B goods market, including investment goods ({@code GoodsMarketB2B})
 * <li>money market ({@code MoneyMarket})
 * <li>credit market ({@code CreditMarket})
 * <li>regions / domains ({@code Domain})
 * </ul>
 * <p>
 * It also yields public information available for all agents (read-only):
 * <ul>
 * <li>value chain
 * <li>need hierarchy
 * <li>geographical information
 * <li>statistical information
 * </ul>
 * 
 */
public final class Model extends RoutingDomain implements Persistable {

	private static Model instance=null;

	/** set of all regions */
	private Set<Region> regions=Collections.emptySet();

	/** set of all goods representing the value chain */
	private Set<Good> goods=Collections.emptySet();

	/** set of all resources, acting as source nodes of value chain graph */
	private Set<Good> resources=Collections.emptySet();

	/** set of all consumable goods, acting as sink nodes of value chain graph */
	private Set<Good> consumables=Collections.emptySet();

	/** set of all needs */
	private Set<NeedDefinition> needs=Collections.emptySet();
	
	/** set of all needs */
	private Set<GoodsMarketB2C> b2cMarkets=Collections.emptySet();
	

	// Singleton
	private Model() {
		setAsRootDomain();
	}

	public static Model getInstance() {
		if (instance==null) instance=new Model();
		return instance;
	}

	public Set<Good> getGoodSet() { return goods; }

	public Set<NeedDefinition> getNeedSet() { return needs; }

	public Set<Good> getConsumableSet() { return consumables; }

	public Set<Good> getResourceSet() { return resources; }

	public Set<Region> getRegionSet() { return regions; }
	
	public Set<GoodsMarketB2C> getB2CMarketSet() { return b2cMarkets; }

	public int getGoodCount() { return goods.size(); }
	
	public int getNeedCount() { return needs.size(); }
	
	public int getConsumableCount() { return consumables.size(); }
	
	public int getResourceCount() { return resources.size(); }
	
	public int getRegionCount() { return regions.size(); }

	@Override
	public void load(DataAccessObject dao) throws Exception {
		regions=Collections.unmodifiableSet(new HashSet<>(dao.load(Region.class))); // load regions
		goods=Collections.unmodifiableSet(new HashSet<>(dao.load(Good.class))); // load goods, set up value chain
		needs=Collections.unmodifiableSet(new HashSet<>(dao.load(NeedDefinition.class))); // load needs
		// sort out resource as source of value chain
		final Set<Good> resourceSet=new HashSet<>();
		resources=Collections.unmodifiableSet(resourceSet);
		for (Good good : getGoodSet()) if (good.isResource()) resourceSet.add(good);
		// sort out consumables as sink of value chain
		final Set<Good> consumableSet=new HashSet<>();
		consumables=Collections.unmodifiableSet(consumableSet);
		for (NeedDefinition nd : getNeedSet()) {
			System.out.println(nd.toString());
			consumableSet.add(nd.getSatisfier());
		}
		System.out.println(getConsumableSet().toString());
		// init household's need hierarchy and event tables
		Household.initNeedMap(getNeedSet());
		final Set<GoodsMarketB2C> b2c=new HashSet<>();
		b2cMarkets=Collections.unmodifiableSet(b2c);
		//Set<LaborMarket> lm=new HashSet<>();
		for (Region region : getRegionSet()) {
			final GoodsMarketB2C gm=new GoodsMarketB2C(getConsumableSet());
			b2c.add(gm);
			// final LaborMarket lm=new LaborMarket(SkillSet.values());
			final Domain domain=new Domain(region,gm); // adding market entities  
			domain.addToDomain(Model.getInstance()); // add domain to model
			gm.addToDomain(domain);
			// adding households and companies externally
			for (HouseholdDBS dbs : dao.load(HouseholdDBS.class,region)) new Household(dbs).addToDomain(domain);
			for (TraderDBS dbs : dao.load(TraderDBS.class,region)) new Trader(dbs).addToDomain(domain);
		}
		
	}

	@Override
	public void save(DataAccessObject dao) throws Exception {
		dao.store(regions);
		dao.store(goods);
		dao.store(needs);
	}

	@Override
	public String getName() { return "root"; }

}
