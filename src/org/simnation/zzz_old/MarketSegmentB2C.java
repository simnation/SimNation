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
package org.simnation.zzz_old;

// java
import java.util.Iterator;
import java.util.List;

import org.simnation.agents.business.Demand;
import org.simnation.agents.business.Money;
import org.simnation.agents.business.Supply;
import org.simnation.common.Batch;
import org.simnation.context.technology.Good;

/**
 * Represents a segment of the local {@link GoodsMarketB2C} and offers a specialized clearing mechanism
 *
 * 
 */
public final class MarketSegmentB2C extends MarketSegment2<Good> {

	/**
	 * Tries to satisfy all posted demands by the markets supply NOTES: - gives always the cheapest possible supply
	 * satisfying the minimum quality criterion - first come, first served policy for demands - splits delivery over
	 * several orders if necessary
	 *
	 * @param agent - market agent containing this market segment
	 */
	public void clearMarket(Market<Good,?> parent) {
		// iterate over the complete list of demands
		for (final Iterator<Demand<Good>> diter=demand.iterator(); diter.hasNext()&&!supply.isEmpty();) {
			// find all supplies within the given price limit
			final Demand<Good> inquiry=diter.next();
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

	// returns supply in pricing rang
	private List<Supply<Good>> getSupplyInRange(double limit) {
		int index=0;
		if (supply.isEmpty()||supply.get(0).getBatch().getPricePerUnit()>limit) return supply.subList(0,0);
		while (index<supply.size()&&supply.get(index).getBatch().getPricePerUnit()<=limit)
			index++;
		if (index==supply.size()) return supply;
		else return supply.subList(0,index+1);
	}

	/**
	 * Sends supply to the demanding household
	 *
	 * @param parent - the market agent instance
	 * @param demand - demand reacted to (contains also the households address)
	 * @param offer - {@link Supply} containing {@link Batch} and change {@link Money}
	 * @param units - amount of delivery in packageSize units!!!
	 */
	private void sendDelivery(final GoodsMarketB2C parent,Demand<Good> demand,Supply<Good> offer,int units) {
		// transfer money to the supplier's cash deposit
		final int value=units*offer.getBatch().getMaxPrice();
		parent.transferMoney(offer.getSupplier(),demand.getMoney(),value);
		// send the delivery
		final Batch item=((Batch) offer.getBatch()).split(units);
		final Supply<Good> delivery=new Supply<Good>(offer.getSupplier(),item);
		final Message<Supply<Good>> msg=new Message<Supply<Good>>(parent.getAddress(),demand.getInquirer(),delivery);
		parent.sendMessage(msg);
		parent.updateMarketInfo(delivery);
	}

}
