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
	private final GoodsMarketB2C goodsMarket;
	//private final LaborMarket lm;
	
	public Domain(Region r,GoodsMarketB2C gm) {
		super();
		region=r;
		goodsMarket=addEntity(gm);
		// add labor market here
	}

	public GoodsMarketB2C getGoodsMarket() { return goodsMarket; }

	public Region getRegion() { return region; }
	
	/**
	 * Returns the index of this domain.
	 * 
	 * @return index of this domain within the first level of the model tree 
	 */
	public int getDomainIndex() {
		return getDomainIndex(getAddress());
	}
	
	/**
	 * Returns the domain index of the given address.
	 * 
	 * @param addr the address
	 * @return domain index of the entity with the given address within the model tree 
	 */
	public static int getDomainIndex(int[] addr) {
		return addr[0];
	}
	
	public String getName() {
		return region.getName();
	}
	
}
