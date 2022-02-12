/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable and used JSimpleSim as technical
 * backbone for concurrent discrete event simulation. This software is published as open source and licensed under GNU
 * GPLv3. Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simnation.agents.market;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.simnation.agents.AbstractBasicAgent;
import org.simnation.agents.business.Demand;
import org.simnation.agents.business.Supply;
import org.simnation.agents.market.MarketStrategy.PriceVolumeDataPoint;
import org.simnation.agents.math.ExponentialSmoothingStatistics;
import org.simnation.agents.math.Statistics;
import org.simplesim.core.messaging.RoutedMessage;
import org.simplesim.core.scheduling.Time;

/**
 * Template class for any market type in SimNation. A market can either be a
 * supply market (like a supermarket), a demand market (anything with a bidding
 * process) or a stock market (traditional market clearing mechanism). The type
 * of item that is traded on this market is indicated by the parameter T.
 * Methods that differ for different market types (supply, demand or stock
 * market) are encapsulated by the {@code MarketStrategy }.
 *
 * @param <T> - type of traded items (characterizing market segments, e.g. Good,
 *            SkillSet)
 */
public abstract class Market<T> extends AbstractBasicAgent<MarketState<T>, Market.Event> {

	protected enum Event {
		clearMarket
	}

	private final MarketStrategy<T> strategy; // market clearing strategy
	private final Map<T, Statistics> priceForcast=new HashMap<>(); 	// price statistics
	private final Map<T, Statistics> volumeForcast=new HashMap<>(); // volume statistics
	private final Time period; // how often this market is cleared

	public Market(Set<T> segSet, MarketStrategy<T> strat, Time offset, Time p) {
		super(new MarketState<>(segSet));
		for (T marketSegment : segSet) {
			priceForcast.put(marketSegment,new ExponentialSmoothingStatistics());
			volumeForcast.put(marketSegment,new ExponentialSmoothingStatistics());
		}
		strategy=strat;
		period=p;
		enqueueEvent(Event.clearMarket,offset);
	}

	public Set<T> getMarketSegments() { return Collections.unmodifiableSet(priceForcast.keySet()); }
	
	public double getPriceAverage(T segment) { return priceForcast.get(segment).getAverage(); }
	
	public double getPriceVariance(T segment) { return priceForcast.get(segment).getVariance(); }
	
	public double getVolumeAverage(T segment) { return volumeForcast.get(segment).getAverage(); }
		
	public double getVolumeVariance(T segment) { return volumeForcast.get(segment).getVariance(); }
	

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simnation.simulation.agents.AbstractBasicAgent#handleMessage(org.
	 * simplesim.core.messaging.Message)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void handleMessage(final RoutedMessage msg) {
		if (msg.getContent().getClass()==Supply.class) addSupply((Supply<T>) msg.getContent());
		else if (msg.getContent().getClass()==Demand.class) addDemand((Demand<T>) msg.getContent());
		else throw new UnhandledMessageType(msg,this);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.simnation.simulation.agents.AbstractBasicAgent#handleEvent(java.lang.
	 * Enum, org.simplesim.core.scheduling.Time)
	 */
	@Override
	protected void handleEvent(Event event, Time time) {
		switch (event) { // all other events are handled here...
		case clearMarket:
			log("\t market clearing event");
			doMarketClearing();
			enqueueEvent(Event.clearMarket,time.add(period));
			break;
		default: // error: event type not known - this should never happen!
			throw new UnhandledEventType(event,this);
		}

	}

	private void doMarketClearing() {
		for (T segment : getMarketSegments()) {
			final List<Demand<T>> demandList=getState().getDemandList(segment);
			final List<Supply<T>> supplyList=getState().getSupplyList(segment);
			final PriceVolumeDataPoint pvdp=strategy.doMarketClearing(this,demandList,supplyList);
			// return unmatched demand and supply to agents
			for (Supply<T> item : supplyList) sendMessage(getAddress(),item.getAddr(),item);
			for (Demand<T> item : demandList) sendMessage(getAddress(),item.getAddr(),item);
			supplyList.clear();
			demandList.clear();
			// update market statistics
			// maintain old price with a volume of 0 if demand and supply did not match.
			if (pvdp==null) volumeForcast.get(segment).update(0); 
			else { // add new data point to time series. 
				volumeForcast.get(segment).update(pvdp.getVolume());
				priceForcast.get(segment).update(pvdp.getPrice());
			}
		}
	}

	private void addDemand(Demand<T> demand) {
		getState().getDemandList(demand.getMarketSegment()).add(demand);
	}

	private void addSupply(Supply<T> supply) {
		getState().getSupplyList(supply.getMarketSegment()).add(supply);
	}

	/**
	 * Does the actual exchange of item vs. money, called by the strategy.
	 * <p>
	 * This method only does the actual trading operation taking the specifics of
	 * <T> into account. Amount and price have to be matched to budget (available
	 * money) BEFORE!
	 * 
	 * @return the actual trading volume (may be less than amount)
	 */
	abstract long trade(Demand<T> d, Supply<T> s, long amount, double price);

	public MarketStrategy<T> getStrategy() { return strategy; }

}
