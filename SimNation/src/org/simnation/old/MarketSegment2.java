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
package org.simnation.old;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.simnation.model.technology.Batch;
import org.simnation.model.technology.Good;
import org.simnation.simulation.business.Demand;
import org.simnation.simulation.business.Tradable;
import org.simnation.simulation.business.Supply;

/**
 * Provides generic functionality for market clearing. There is a list of {@link Supply} and {@link Demand} as well as a list of
 * subscribers for the {@link MarketInfo}. Methods to handle supply and demand are moved to the derived classes
 *
 * @param <T> - type characterizing market segments, e.g. Good, SkillSet
 *
 */
public abstract class MarketSegment2<T> {

	final List<Demand<T>> demand=new ArrayList<>();
	final List<Supply<T>> supply=new ArrayList<>();
	
	// statistics
	private int soldUnits=0; // number of sold item units on the market
	private long turnover=0; // market turnover in money units
	private int deals=0; // number of deals
	private int soldPackages=0; // to calculate average package size
	private double qualitySum=0; // to calculate average quality of traded items

	public void reset() {
		soldUnits=0;
		turnover=0;
		deals=0;
		soldPackages=0;
		qualitySum=0;
	}

	void addDeal(Supply<T> deal) {
		Tradable<T> item=deal.getBatch();
		soldUnits+=item.getTotalVolume();
		turnover+=item.getTotalValue();
		soldPackages+=item.getQuantity();
		qualitySum+=item.getQuality()*item.getTotalVolume();
		deals++;
	}

	public boolean hasSupply() {
		return !supply.isEmpty();
	}

	public void addSupply(Supply<T> s) {
		supply.add(s);
	}

	public void removeSupply(Supply<T> s) {
		supply.remove(s);
	}

	public boolean hasDemand() {
		return !demand.isEmpty();
	}

	public void addDemand(Demand<T> d) {
		demand.add(d);
	}

	public void removeDemand(Demand<T> d) {
		demand.remove(d);
	}

	/**
	 * implements the double auction algorithm
	 * 
	 * Tries to satisfy all posted demands by the markets supply NOTES: - gives always the cheapest possible supply
	 * satisfying the minimum quality criterion - first come, first served policy for demands - splits delivery over
	 * several orders if necessary
	 *
	 * @param parent - market agent containing this market segment
	 */
	public void clearMarket(Market<T,?> parent) {
		demand.sort((o1, o2) -> -o1.compareTo(o2)); // sort demand in descending order
		supply.sort(null); // sort supply in ascending order
		
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
	
	private List<Supply<Good>> getSupplyInRange(double limit) {
		int index=0;
		if (supply.isEmpty()||supply.get(0).getBatch().getPricePerUnit()>limit) return supply.subList(0,0);
		while (index<supply.size()&&supply.get(index).getBatch().getPricePerUnit()<=limit)
			index++;
		if (index==supply.size()) return supply;
		else return supply.subList(0,index+1);
	}



	public void clearDemandList() {
		demand.clear(); // erase any demand left at the end of business
	}

}
