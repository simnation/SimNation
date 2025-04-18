/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable and used JSimpleSim as technical
 * backbone for concurrent discrete event simulation. This software is published as open source and licensed under GNU
 * GPLv3. Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simnation.agents.market;

import java.util.Set;

import org.simnation.agents.business.Demand;
import org.simnation.agents.business.Supply;
import org.simnation.common.Batch;
import org.simnation.context.technology.Good;

/**
 *
 *
 */
public final class GoodsMarketB2C extends Market<Good> {

	/**
	 * @param strat
	 * @param segments
	 * @param offset
	 * @param p
	 */
	public GoodsMarketB2C(Set<Good> segments) {
		super(segments,new SimpleDoubleAuctionStrategy<>());
	}

	@Override
	long trade(Demand<Good> demand, Supply<Good> supply, long amount, double price) {
		long cost=Math.round(price*amount);
		long quantity=amount;
		if (cost>demand.getMoney().getValue()) { // insufficient funds
			quantity=(long) (demand.getMoney().getValue()/price); // round off
			cost=Math.round(price*quantity);
		}
		log("\t market price: $"+Double.toString(price)+", demand: "+amount+",affordable: "+quantity+", cost: $"+cost);
		Batch batch=((Batch) supply.getItem()).split(quantity);
		batch.setValue(cost); // set to actual trading value --> the price is what others pay for it.
		if (demand.getItem()==null) demand.setItem(batch);
		else((Batch) demand.getItem()).merge(batch);
		supply.getMoney().merge(demand.getMoney().split(cost));
		return quantity;
	}
	
	@Override
	public String getName() { return "GoodsMarketB2C"; }

}
