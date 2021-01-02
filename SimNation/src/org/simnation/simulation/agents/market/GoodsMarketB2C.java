/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable and used JSimpleSim as technical
 * backbone for concurrent discrete event simulation. This software is published as open source and licensed under GNU
 * GPLv3. Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simnation.simulation.agents.market;

import java.util.List;

import org.simnation.model.technology.Batch;
import org.simnation.model.technology.Good;
import org.simnation.simulation.business.Demand;
import org.simnation.simulation.business.Supply;
import org.simplesim.core.scheduling.Time;

/**
 *
 *
 */
public final class GoodsMarketB2C extends Market<Good> {

	public static final Time MARKET_OFFSET=new Time(0,0,0,12,0,0);
	public static final Time MARKET_PERIOD=Time.DAY;

	/**
	 * @param strat
	 * @param segList
	 * @param offset
	 * @param p
	 */
	public GoodsMarketB2C(List<Good> segList) {
		super(segList,MARKET_OFFSET,MARKET_PERIOD,new SimpleDoubleAuctionStrategy<>());
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

}
