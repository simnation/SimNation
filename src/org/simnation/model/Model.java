/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable and used JSimpleSim as technical
 * backbone for concurrent discrete event simulation. This software is published as open source and licensed under GNU
 * GPLv3. Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simnation.model;

import java.util.HashSet;
import java.util.Set;

import org.simnation.agents.firm.trader.Trader;
import org.simnation.agents.firm.trader.TraderDBS;
import org.simnation.agents.household.Household;
import org.simnation.agents.household.HouseholdDTO;
import org.simnation.agents.household.Need;
import org.simnation.agents.market.GoodsMarketB2C;
import org.simnation.context.geography.Region;
import org.simnation.context.technology.Good;
import org.simnation.persistence.DataAccessObject;
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
public final class Model extends RoutingDomain {

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

	/** set of all regional market */
	private final Set<GoodsMarketB2C> b2c=new HashSet<>();
	//private final Set<LaborMarket> lm=new HashSet<>();
	
	private double economicGrowth=1.0d;
	
	// Singleton
	private Model() {
		setAsRootDomain();
	}

	public static Model getInstance() {
		if (instance==null) instance=new Model();
		return instance;
	}

	public Set<Good> getGoods() { return goods; }

	public Set<Need> getNeeds() { return needs; }

	public Set<Good> getConsumables() { return consumables; }

	public Set<Good> getResources() { return resources; }

	public Set<Region> getRegions() { return regions; }

	public Set<GoodsMarketB2C> getB2CMarketSet() { return b2c; }

	public void load(DataAccessObject dao) throws Exception {
		// set up value chain
		goods.addAll(dao.load(Good.class));
		for (Good good : getGoods()) if (good.isResource()) resources.add(good);
		// set up need system
		needs.addAll(dao.load(Need.class)); // load needs
		for (Need need : getNeeds()) consumables.add(need.getSatisfier());
		Household.initNeedMap(getNeeds()); // init household's need hierarchy and event tables
		// set up geography
		regions.addAll(dao.load(Region.class));
		
		for (Region region : getRegions()) {
			final GoodsMarketB2C gm=new GoodsMarketB2C(getConsumables());
			b2c.add(gm);
			// final LaborMarket lm=new LaborMarket(SkillSet.values());
			final Domain domain=new Domain(region,gm); // adding market entities
			Model.getInstance().addEntity(domain); // add domain to model
			domain.addEntity(gm);
			// adding households and companies externally
			for (HouseholdDTO dto : dao.load(HouseholdDTO.class,region)) domain.addEntity(new Household(dto));
			for (TraderDBS dbs : dao.load(TraderDBS.class,region)) domain.addEntity(new Trader(dbs));
		}

	}

	public void save(DataAccessObject dao) throws Exception {
		dao.save(regions);
		dao.save(goods);
		dao.save(needs);
	}

	@Override
	public String getName() { return "root"; }

	public double getEconomicGrowth() {
		return economicGrowth;
	}

	public void setEconomicGrowth(double economicGrowth) {
		this.economicGrowth = economicGrowth;
	}

}
