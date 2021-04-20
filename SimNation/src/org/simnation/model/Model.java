/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable and used JSimpleSim as technical
 * backbone for concurrent discrete event simulation. This software is published as open source and licensed under GNU
 * GPLv3. Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simnation.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.simnation.agents.firm.trader.Trader;
import org.simnation.agents.firm.trader.TraderDBS;
import org.simnation.agents.household.Household;
import org.simnation.agents.household.HouseholdDBS;
import org.simnation.agents.market.GoodsMarketB2C;
import org.simnation.context.geography.Region;
import org.simnation.context.needs.Need;
import org.simnation.context.technology.Good;
import org.simnation.persistence.DataAccessObject;
import org.simnation.persistence.Persistable;
import org.simplesim.model.RoutingDomain;

/**
 * The Model is the top-level domain of the economy and thus designed as a singleton.
 * <p>
 * Parts of the model level are:
 * <ul>
 * <li> B2B goods market, including investment goods ({@code GoodsMarketB2B})
 * <li> money market ({@code MoneyMarket})
 * <li> credit market ({@code CreditMarket})
 * <li> regions / domains ({@code Domain})
 * </ul><p>
 * It also yields public information available for all agents (read-only):
 * <ul>
 * <li> value chain ({@code GoodSet})
 * <li> need hierarchy ({@code NeedSet})
 * <li> geographical information ({@code RegionSet})
 * <li> statistical information
 * </ul>
 * 
 */
public final class Model extends RoutingDomain implements Persistable {

	private static Model instance=null;
	
	/** set of all regions */
	private final Set<Region> regions=new HashSet<>();
	
	/** set of all goods representing the value chain */
	private final Set<Good> goods=new HashSet<>();
	
	/** set of all resources, acting as source nodes of value chain graph */
	private final Set<Good> resources=new HashSet<>();
	
	/** set of all consumable goods, acting as sink nodes of value chain graph */
	private final Set<Good> consumables=new HashSet<>();
	
	/** set of all needs */
	private final Set<Need> needs=new HashSet<>();

	
	
	private final Set<GoodsMarketB2C> b2c=new HashSet<>();
	
	// Singleton
	private Model() {
		super();
		setAsRootDomain();
	}
	
	public static Model getInstance() {
		if (instance==null) instance=new Model();
		return instance;
	}
	
	

	public Set<Good> getGoodSet() {
		return goods;
	}

	public Set<Need> getNeedSet() {
		return needs;
	}
	
	public Set<Good> getConsumableSet() {
		return consumables;
	}
	
	public Set<Good> getResourceSet() {
		return resources;
	}

	public Set<Region> getRegionSet() { return regions; }
	
	public Set<GoodsMarketB2C> getConsumableMarket() {
		return b2c;
	}
	
	@Override
	public void load(DataAccessObject dao) throws Exception {
		// load regions
		regions.addAll(dao.load(Region.class));
		// load goods, set up value chain
		goods.addAll(dao.load(Good.class));
		for (Good good : goods) if (good.isResource()) resources.add(good);
		// load needs
		needs.addAll(dao.load(Need.class));
		for (Need need : needs) consumables.add(need.getSatisfier());
		// init household's need hierarchy and event tables
		Household.initNeedMap(needs);
		
		for (Region region : getRegionSet()) {
			Domain domain=addEntity(new Domain(region)); // also inits regional markets
			domain.addEntity(domain.getGoodsMarket()); // has to be added externally
			b2c.add(domain.getGoodsMarket());
			Collection<HouseholdDBS> households=dao.load(HouseholdDBS.class,region);
			for (HouseholdDBS dbs : households) domain.addEntity(new Household(dbs));
			Collection<TraderDBS> traders=dao.load(TraderDBS.class,region);
			for (TraderDBS dbs : traders) domain.addEntity(new Trader(dbs));
			
		}
	}
	
	@Override
	public void save(DataAccessObject dao) throws Exception {
		dao.store(regions);
		dao.store(goods);
		dao.store(needs);
	}
	
	public String getName() {
		return "root";
	}

}
