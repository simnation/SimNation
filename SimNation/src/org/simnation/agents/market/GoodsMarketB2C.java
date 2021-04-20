/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable and used JSimpleSim as technical
 * backbone for concurrent discrete event simulation. This software is published as open source and licensed under GNU
 * GPLv3. Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simnation.agents.market;

import java.util.List;
import java.util.Set;

import org.simnation.agents.business.Demand;
import org.simnation.agents.business.Supply;
import org.simnation.agents.common.Batch;
import org.simnation.context.technology.Good;
import org.simplesim.core.scheduling.Time;

/**
 *
 *
 */
public final class GoodsMarketB2C extends Market<Good> {
	
	private static final String AGENT_NAME="GoodsMarketB2C";


	public static final Time MARKET_OFFSET=new Time(0,0,0,12,0,0);
	public static final Time MARKET_PERIOD=Time.DAY;

	/**
	 * @param strat
	 * @param segments
	 * @param offset
	 * @param p
	 */
	public GoodsMarketB2C(Set<Good> segments) {
		super(segments,MARKET_OFFSET,MARKET_PERIOD,new SimpleDoubleAuctionStrategy<>());
	}

	/*
	 * (non-Javadoc)
	 * @see org.simnation.simulation.agents.market.Market#trade(org.simnation.simulation.business.Supply,
	 * org.simnation.simulation.business.Demand, int, float)
	 */
	@Override
	void trade(Demand<Good> d, Supply<Good> s, long amount, double price) {
		Batch batch=((Batch) s.getItem()).split(amount);
		if (d.getItem()==null) d.setItem(batch);
		else((Batch) d.getItem()).merge(batch);
		long cost=Math.round(price*amount);
		s.getMoney().merge(d.getMoney().split(cost));
	}
	
	public String getName() {
		return AGENT_NAME;
	}

}
