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
package org.simnation.model;


import org.simnation.agents.market.GoodsMarketB2C;
import org.simnation.context.geography.Region;
import org.simplesim.model.RoutingDomain;



/**
 * The domain represents a regional economy with its markets.
 * <p>
 * Parts of the domain level are:
 * <ul>
 * <li> B2C goods market ({@code GoodsMarketB2C})
 * <li> labor market ({@code LaborMarket})
 * <li> households ({@code Household})
 * <li> firms
 * </ul>
 */
public final class Domain extends RoutingDomain {

	private final Region region; // the region represented by this domain
	private final GoodsMarketB2C gm;
	//private final LaborMarket lm;
	
	public Domain(Region r) {
		super();
		region=r;
		gm=new GoodsMarketB2C(Model.getInstance().getConsumableSet());
		// add labor market here
	}

	public GoodsMarketB2C getGoodsMarket() {
		return gm;
	}

	public Region getRegion() { return region; }
	
	public String getName() {
		return region.getName();
	}
	
}
