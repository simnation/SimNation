/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable and used JSimpleSim as technical
 * backbone for concurrent discrete event simulation. This software is published as open source and licensed under GNU
 * GPLv3. Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simnation.simulation.agents.market;

import java.util.Iterator;
import java.util.List;

import org.simnation.simulation.business.Demand;
import org.simnation.simulation.business.Supply;

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
	public void doMarketClearing(Market<T> market, List<Demand<T>> demand, List<Supply<T>> supply) {
		if (supply.isEmpty()||demand.isEmpty()) return;
		demand.sort((o1, o2) -> -o1.compareTo(o2)); // sort demand with descending price
		supply.sort(null); // sort supply with ascending price
		double price=findEquilibriumPrice(demand,supply);
		tradeItems(market,demand,supply,price);
	}

	/**
	 * @param market
	 * @param demand
	 * @param supply
	 * @param price
	 */
	private void tradeItems(Market<T> market, List<Demand<T>> demand, List<Supply<T>> supply, double price) {
		Iterator<Demand<T>> diter=demand.iterator();
		Iterator<Supply<T>> siter=supply.iterator();
		Demand<T> ask=diter.next(); 	// check for empty list done before
		Supply<T> bid=siter.next(); 	// check for empty list done before
		while (ask.getMaxPrice()>=bid.getPrice()) {
			if (bid.getQuantity()>ask.getQuantity()) {
				market.trade(ask,bid,ask.getQuantity(),price);
				if (!diter.hasNext()) break; // demand completely satisfied --> exit
				ask=diter.next();
			} else if (bid.getQuantity()<ask.getQuantity()) {
				market.trade(ask,bid,bid.getQuantity(),price);
				if (!siter.hasNext()) break; // supply completely sold --> exit
				bid=siter.next();
			} else { // askQty==bidQty
				market.trade(ask,bid,ask.getQuantity(),price);
				if (diter.hasNext()&&siter.hasNext()) {
					ask=diter.next();
					bid=siter.next();
				} else break; // supply and demand simultaneously cleared - this is rare!
			}
		}
	}
	
	private double findEquilibriumPrice(List<Demand<T>> demand, List<Supply<T>> supply) {
		Iterator<Demand<T>> diter=demand.iterator();
		Iterator<Supply<T>> siter=supply.iterator();
		Demand<T> ask=diter.next(); 	// check for empty list done before
		Supply<T> bid=siter.next(); 	// check for empty list done before
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

	/*private double findEquilibriumPrice(List<Demand<T>> demand, List<Supply<T>> supply) {
		int si=0, di=0;
		double bidPrice=0, askPrice=0;
		long bidQty=supply.get(0).getQuantity();
		long askQty=demand.get(0).getQuantity();
		while (demand.get(di).getMaxPrice()>=supply.get(si).getPrice()) {
			askPrice=demand.get(di).getMaxPrice();
			bidPrice=supply.get(si).getPrice();
			if (bidQty>askQty) {
				bidQty-=askQty;
				di++;
				if (di==demand.size()) break; // demand completely satisfied --> exit
				askQty=demand.get(di).getQuantity();
			} else if (bidQty<askQty) {
				askQty-=bidQty;
				si++;
				if (si==supply.size()) break; // supply completely sold --> exit
				bidQty=supply.get(si).getQuantity();
			} else { // askQty==bidQty
				di++;
				si++;
				if ((di==demand.size())||(si==supply.size())) break; // no more elements in demand or supply list -->
																		// exit
				askQty=demand.get(di).getQuantity();
				bidQty=supply.get(si).getQuantity();
			}
		}
		return (bidPrice+askPrice)/2.0d;// eq. price is average of last deal's bid and ask price
	}*/

}
