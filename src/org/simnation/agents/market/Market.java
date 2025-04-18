/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable and used JSimpleSim as technical
 * backbone for concurrent discrete event simulation. This software is published as open source and licensed under GNU
 * GPLv3. Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simnation.agents.market;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.simnation.agents.AbstractBasicAgent;
import org.simnation.agents.business.Demand;
import org.simnation.agents.business.Supply;
import org.simnation.context.technology.Good;
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

	protected enum Event {
		initMarket, clearMarket
	}

	record PriceVolumeDataPoint(double price, long volume) {}

	private static final Time MARKET_OFFSET=new Time(2);	// be the second agent to start
	private static final Time MARKET_TIME=new Time(0,12,0); // be the second agent to start
	private static final Time MARKET_PERIOD=Time.DAY;		// do market clearing every 12 hours

	private final MarketStrategy<T> strategy; // market clearing strategy
	private final Set<T> marketSegments;

	public Market(Set<T> ms, MarketStrategy<T> strat) {
		super(new MarketState<T>(ms));
		strategy=strat;
		marketSegments=ms;
		enqueueEvent(Event.initMarket,MARKET_OFFSET);
	}
	
	public MarketData getMarketData(T segment) { return getState().getMarketData(segment); }

	@SuppressWarnings("unchecked")
	@Override
	protected void handleMessage(RoutingMessage msg) {
		if (msg.getContent().getClass()==Supply.class)
			addSupply((Supply<T>) msg.getContent());
		else if (msg.getContent().getClass()==Demand.class)
			addDemand((Demand<T>) msg.getContent());
		else
			throw new UnhandledMessageType(msg,this);
	}

	@Override
	protected void handleEvent(Event event, Time time) {
		if (event==Event.initMarket)
			enqueueEvent(Event.clearMarket,MARKET_TIME);
		else {
			doMarketClearing();
			enqueueEvent(Event.clearMarket,time.add(MARKET_PERIOD));
		}
	}

	private void doMarketClearing() {
		for (T segment : marketSegments) {
			final List<Demand<T>> demandList=getState().getDemand(segment);
			final List<Supply<T>> supplyList=getState().getSupply(segment);
			log("\tsupply list size="+supplyList.size());
			log("\tdemand list size="+demandList.size());
			final PriceVolumeDataPoint pvd=strategy.doMarketClearing(this,demandList,supplyList);
			// update market statistics if there was some trade (=new data point)
			if (pvd != null)
				getMarketData(segment).update(pvd.price(), pvd.volume()); // this alteration should be thread save
			// return unmatched demand and supply to agents
			for (Demand<T> item : demandList)
				sendMessage(getAddress(),item.getAddr(),item);
			demandList.clear();
			for (Supply<T> item : supplyList)
				sendMessage(getAddress(),item.getAddr(),item);
			supplyList.clear();
		}
	}

	private void addDemand(Demand<T> demand) {
		getState().getDemand(demand.getMarketSegment()).add(demand);
	}

	private void addSupply(Supply<T> supply) {
		getState().getSupply(supply.getMarketSegment()).add(supply);
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
	
	public double getPrice(T segment) { return getState().getMarketData(segment).getPrice(); }

}
