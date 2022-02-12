/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable and used JSimpleSim as technical
 * backbone for concurrent discrete event simulation. This software is published as open source and licensed under GNU
 * GPLv3. Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simnation.agents.market;

import java.util.Iterator;
import java.util.List;

import org.simnation.agents.business.Demand;
import org.simnation.agents.business.Supply;

/**
 * Implementation of a simple double auction strategy only considering pricing.
 * <p>
 * Supply and demand are organized in sorted lists ordered by price (supply ascending, demand descending). In a first step, 
 * the equilibrium price is determined as price with the highest market turnover. Second, distinct portions of item are traded
 * by the market's {@code trade} method.
 *
 */
public class SimpleDoubleAuctionStrategy<T> implements MarketStrategy<T> {

	/*
	 * (non-Javadoc)
	 * @see
	 * org.simnation.simulation.agents.market.MarketStrategy#doMarketClearing(org.simnation.simulation.agents.market.
	 * Market, java.util.List, java.util.List)
	 */
	@Override
	public PriceVolumeDataPoint doMarketClearing(Market<T> market, List<Demand<T>> demand, List<Supply<T>> supply) {
		if (supply.isEmpty()||demand.isEmpty()) return null; // check for empty lists
		demand.sort((o1, o2) -> -o1.compareTo(o2)); // sort demand with descending price
		supply.sort(null); // sort supply with ascending price
		final double price=findEquilibriumPrice(demand,supply);
		final PriceVolumeDataPoint result=new PriceVolumeDataPoint(price); 
		final Iterator<Demand<T>> diter=demand.iterator();
		final Iterator<Supply<T>> siter=supply.iterator();
		Demand<T> ask=diter.next(); 	// check for empty list done before
		Supply<T> bid=siter.next(); 	// check for empty list done before
		while (ask.getMaxPrice()>=bid.getPrice()) {
			if (bid.getQuantity()>ask.getQuantity()) {
				result.addVolume(market.trade(ask,bid,ask.getQuantity(),price));
				if (!diter.hasNext()) break; // demand completely satisfied --> exit
				ask=diter.next();
			} else if (bid.getQuantity()<ask.getQuantity()) {
				result.addVolume(market.trade(ask,bid,bid.getQuantity(),price));
				if (!siter.hasNext()) break; // supply completely sold --> exit
				bid=siter.next();
			} else { // askQty==bidQty
				result.addVolume(market.trade(ask,bid,ask.getQuantity(),price));
				if (diter.hasNext()&&siter.hasNext()) {
					ask=diter.next();
					bid=siter.next();
				} else break; // supply and demand simultaneously cleared - this is rare!
			}
		}
		return result;
	}
	
	/**
	 * Calculates the market's actual equilibrium price.
	 * <p>
	 * In economics, the equilibrium price is the intersection of supply and demand curve. In this implementation,
	 * supply and demand are <i>discrete</i> functions. So, finding the equilibrium price and quantity has to be done by iteration.
	 * <p>
	 * Note: The equilibrium price is calculated as average of the last supply and demand price.
	 * 
	 * 
	 * @param market the market
	 * @param demand list of the market's demands
	 * @param supply list of the market's supplies
	 * @return the equilibrium price
	 */
	private double findEquilibriumPrice(List<Demand<T>> demand, List<Supply<T>> supply) {
		final Iterator<Demand<T>> diter=demand.iterator();
		final Iterator<Supply<T>> siter=supply.iterator();
		Demand<?> ask=diter.next(); 	// check for empty list done before
		Supply<?> bid=siter.next(); 	// check for empty list done before
		long askQty=ask.getQuantity();
		long bidQty=bid.getQuantity();
		
		while (ask.getMaxPrice()>=bid.getPrice()) {
			if (bidQty>askQty) {
				bidQty-=askQty;
				if (!diter.hasNext()) break; // demand completely satisfied --> exit
				ask=diter.next();
				askQty=ask.getQuantity();
			} else if (bidQty<askQty) {
				askQty-=bidQty;
				if (!siter.hasNext()) break; // supply completely sold --> exit
				bid=siter.next();
				bidQty=bid.getQuantity();
			} else { // askQty==bidQty
				if (diter.hasNext()&&siter.hasNext()) {
					ask=diter.next();
					bid=siter.next();
					askQty=ask.getQuantity();
					bidQty=bid.getQuantity();
				} else break; // supply and demand simultaneously cleared - this is rare!
			}
		}
		return (bid.getPrice()+ask.getMaxPrice())/2.0d;// eq. price is average of last deal's bid and ask price
	}

}
