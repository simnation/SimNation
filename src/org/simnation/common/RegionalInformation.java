/**
 *
 */
package org.simnation.common;

import java.util.IdentityHashMap;
import java.util.Map;

import org.simnation.agents.market.MarketInfo;
import org.simnation.context.technology.Good;
import org.simnation.core.Address;
import org.simnation.simulation.model.Root;

/**
 * Contains all information of a region that is publicly available
 *
 * @author Rene Kuhlemann
 *
 */
public final class RegionalInformation {

	private final Map<Good,MarketInfo> mi=new IdentityHashMap<Good,MarketInfo>();
	private final Address b2c_market,labor_market;

	public RegionalInformation(Address b2c,Address lm) {
		b2c_market=b2c;
		labor_market=lm;
		for (final Good good : Root.getContext().getGoods().getConsumables())
			mi.put(good,new MarketInfo());
	}

	public MarketInfo getMarketInfo(Good key) {
		return mi.get(key);
	}

	public Address getGoodsMarketAddr() {
		return b2c_market;
	}

	public Address getLaborMarketAddr() {
		return labor_market;
	}

	/*
	 * public MarketInfo getMarketInfo(SkillSet key) { return(mi.get(key)); }
	 */

}
