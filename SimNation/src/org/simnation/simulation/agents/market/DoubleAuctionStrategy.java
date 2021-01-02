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
package org.simnation.simulation.agents.market;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.simnation.model.technology.Batch;
import org.simnation.model.technology.Good;
import org.simnation.old.GoodsMarketB2C;
import org.simnation.simulation.agents.market.MarketStrategy;
import org.simnation.simulation.business.Demand;
import org.simnation.simulation.business.Supply;

/**
 * 
 *
 */
public class DoubleAuctionStrategy<T> implements MarketStrategy<T> {

	 /* (non-Javadoc)
	 * @see org.simnation.simulation.agents.market.MarketStrategy#doMarketClearing(org.simnation.simulation.agents.market.Market, java.util.List, java.util.List)
	 */
	public void doMarketClearing(Market<T> market, List<Demand<T>> demand, List<Supply<T>> supply) {
		demand.sort((o1, o2) -> -o1.compareTo(o2)); // sort demand with descending price
		supply.sort(null); // sort supply with ascending price
		
		
		
		// find the equilibirium price
		

		// iterate over the complete list of demands
		Iterator<Demand<T>> diter=demand.iterator(); 
		while (diter.hasNext()&&!supply.isEmpty()) {
			// find all supplies within the given price limit
			final Demand<T> inquiry=diter.next();
			int openVolume=inquiry.getQuantity(); // get the total amount of the demanded item
			// iterate over all supplied batches with price below max price (while we still have supply and open demand)
			final List<Supply<Good>> suitableSupply=getSupplyInRange(inquiry.getMaxPrice());
			for (final Iterator<Supply<Good>> siter=suitableSupply.iterator(); siter.hasNext()&&openVolume>0;) {
				final Supply<Good> offer=siter.next();
				final Batch item=(Batch) offer.getBatch();
				// consider only offers with at least the given minimum quality
				// --> this is also the cheapest offer since the supply list is sorted by price!
				if (item.getQuality()>=inquiry.getMinQuality()) {
					// how many units of this product would we need? always round up to be sure!
					int amount=Tools.roundUp(openVolume/item.getPackageSize());
					// how many units can we afford? always round off to be sure!
					final int affordableAmount=Tools.roundOff(inquiry.getMoney().getValue()/item.getPrice());
					// correct units to be transferred if necessary
					if (affordableAmount<amount) amount=affordableAmount;
					// is there enough offered to satisfy the demand?
					if (amount<=item.getQuantity()) { // yes!
						openVolume=0;
						sendDelivery((GoodsMarketB2C) parent,inquiry,offer,amount);
						diter.remove(); // delete processed demand (even if it could not be fully satisfied)
					} else { // no!
						openVolume-=item.getTotalVolume();
						sendDelivery((GoodsMarketB2C) parent,inquiry,offer,item.getQuantity());
						siter.remove(); // delete empty supply
					}
				}
			}
		}	
	}

}
