/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable and used JSimpleSim as technical
 * backbone for concurrent discrete event simulation. This software is published as open source and licensed under GNU
 * GPLv3. Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simnation.agents.market;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.simnation.agents.AbstractBasicAgent;
import org.simnation.agents.business.Demand;
import org.simnation.agents.business.Supply;
import org.simplesim.core.messaging.RoutingMessage;
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

	/**
	 * Price-volume-combination at a given point of time
	 */
	static class PriceVolumeDataPoint {

		final private double price;
		private long volume=0;

		PriceVolumeDataPoint(double p) { price=p; }

		void addVolume(long value) { volume+=value; }

		double getPrice() { return price; }

		long getVolume() { return volume; }

	}

	protected enum Event {
		initMarket, clearMarket
	}
	
	private static final Time MARKET_OFFSET=new Time(2);	// be the second agent to start 
	private static final Time MARKET_TIME=new Time(0,12,0); // be the second agent to start 
	private static final Time MARKET_PERIOD=Time.DAY;		// do market clearing every 12 hours



	private static final PriceVolumeDataPoint INITIAL_PVDP=new PriceVolumeDataPoint(Double.NaN);

	private final MarketStrategy<T> strategy; // market clearing strategy
	private final Map<T, PriceVolumeDataPoint> statistics=new ConcurrentHashMap<>(); // lock when changing data 	

	public Market(Set<T> segSet, MarketStrategy<T> strat) {
		super(new MarketState<>(segSet));
		for (T marketSegment : segSet) statistics.put(marketSegment,INITIAL_PVDP);
		strategy=strat;
		enqueueEvent(Event.initMarket,MARKET_OFFSET);
	}

	public Set<T> getMarketSegments() { return Collections.unmodifiableSet(statistics.keySet()); }

	public double getLastPrice(T segment) { return statistics.get(segment).getPrice(); }

	public double getAverageSupplyPrice(T segment) {
		if (getState().getSupplyList(segment).isEmpty()) return Double.NaN;
		double sum=0;
		for (Supply<T> supply : getState().getSupplyList(segment)) sum+=supply.getPrice();
		return sum/getState().getSupplyList(segment).size();
	}

	public double getLastVolume(T segment) { return statistics.get(segment).getVolume(); }

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simnation.simulation.agents.AbstractBasicAgent#handleMessage(org.
	 * simplesim.core.messaging.Message)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void handleMessage(RoutingMessage msg) {
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
		if (event==Event.initMarket) enqueueEvent(Event.clearMarket,MARKET_TIME);
		else {
			doMarketClearing();
			enqueueEvent(Event.clearMarket,time.add(MARKET_PERIOD));
		}
	}

	private void doMarketClearing() {
		for (T segment : getMarketSegments()) {
			final List<Demand<T>> demandList=getState().getDemandList(segment);
			final List<Supply<T>> supplyList=getState().getSupplyList(segment);
			log("\tsupply list size="+supplyList.size());
			log("\tdemand list size="+demandList.size());
			final PriceVolumeDataPoint pvdp=strategy.doMarketClearing(this,demandList,supplyList);
			// update market statistics if there was some trade (=new data point)
			if (pvdp!=null) statistics.put(segment,pvdp); // this alteration should be thread save
			// return unmatched demand and supply to agents
			for (Demand<T> item : demandList) sendMessage(getAddress(),item.getAddr(),item);
			demandList.clear();
			for (Supply<T> item : supplyList) sendMessage(getAddress(),item.getAddr(),item);
			supplyList.clear();
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
