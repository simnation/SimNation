/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable and used JSimpleSim as technical
 * backbone for concurrent discrete event simulation. This software is published as open source and licensed under GNU
 * GPLv3. Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simnation.agents.market;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.simnation.agents.AbstractBasicAgent;
import org.simnation.agents.business.Demand;
import org.simnation.agents.business.Supply;
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
		MARKET_CLEARING
	}

	private final MarketStrategy<T> strategy; // market clearing strategy
	private final Map<T, MarketStatistics> statistics=new ConcurrentHashMap<>(); // enables concurrent access from agents
	private final Time period; // how often market is cleared

	public Market(Set<T> segSet, Time offset, Time p, MarketStrategy<T> strat) {
		super(new MarketState<>(segSet));
		for (T item : segSet) statistics.put(item,null);
		strategy=strat;
		period=p;
		enqueueEvent(Event.MARKET_CLEARING,offset);
	}

	public Set<T> getMarketSegments() { return statistics.keySet(); }
	
	public MarketStatistics getMarketStatistics(T segment) { return statistics.get(segment); }

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
		case MARKET_CLEARING:
			log("\t market clearing event");
			doMarketClearing();
			enqueueEvent(Event.MARKET_CLEARING,time.add(period));
			break;
		default: // error: event type not known - this should never happen!
			throw new UnhandledEventType(event,this);
		}

	}

	private void doMarketClearing() {
		for (T segment : getMarketSegments()) {
			List<Demand<T>> demandList=getState().getDemandList(segment);
			List<Supply<T>> supplyList=getState().getSupplyList(segment);
			final MarketStatistics ms=strategy.doMarketClearing(this,demandList,supplyList);
			if (ms!=null) statistics.put(segment,ms); // update statistics, keep old value if there was no trading
			for (Supply<T> item : supplyList) sendMessage(getAddress(),item.getAddr(),item);
			supplyList.clear();
			for (Demand<T> item : demandList) sendMessage(getAddress(),item.getAddr(),item);
			demandList.clear();
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
