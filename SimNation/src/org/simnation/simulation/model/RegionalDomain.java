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
package org.simnation.simulation.model;


import org.simnation.simulation.agents.market.GoodsMarketB2C;
import org.simplesim.model.RoutingDomain;



public final class RegionalDomain extends RoutingDomain {

	private final GoodsMarketB2C gm;
	// add regional LaborMarket
	
	public RegionalDomain() {
		super();
		gm=new GoodsMarketB2C(RootDomain.getInstance().getGoodDefinitionSet().getConsumables());
	}

	/**
	 * @return the gm
	 */
	public GoodsMarketB2C getGoodsMarket() {
		return gm;
	}
	
}
