/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable and used JSimpleSim as technical
 * backbone for concurrent discrete event simulation. This software is published as open source and licensed under GNU
 * GPLv3. Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simnation.agents.market;

import java.util.List;

import org.simnation.agents.AbstractBasicAgent;
import org.simnation.agents.business.Demand;
import org.simnation.agents.business.Supply;
import org.simplesim.core.messaging.Message;
import org.simplesim.core.scheduling.Time;

/**
 * Template class for any market type in SimNation. A market can either be a supply market (like a supermarket), a
 * demand market (anything with a bidding process) or a stock market (traditional market clearing mechanism). The type
 * of item that is traded on this market is indicated by the parameter T. Methods that differ for different market types
 * (supply, demand or stock market) are encapsulated by the {@code MarketStrategy }.
 *
 * @param <T> - type of traded items (characterizing market segments, e.g. Good, SkillSet)
 */
public abstract class Market<T> extends AbstractBasicAgent<MarketState<T>, Market.Event> {

	protected enum Event {
		MARKET_CLEARING
	}

	private MarketStrategy<T> strategy; // market clearing strategy
	private final List<T> segmentList; // list of market segment selectors
	private final Time period; // how often market is cleared

	public Market(List<T> segList, Time offset, Time p, MarketStrategy<T> strat) {
		super(new MarketState<>(segList));
		segmentList=segList;
		strategy=strat;
		period=p;
		enqueueEvent(Event.MARKET_CLEARING,offset);
	}

	/*
	 * (non-Javadoc)
	 * @see org.simnation.simulation.agents.AbstractBasicAgent#handleMessage(org.simplesim.core.messaging.Message)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void handleMessage(final Message<?> msg) {
		if (msg.getContent().getClass()==Supply.class) addSupply((Supply<T>) msg.getContent());
		else if (msg.getContent().getClass()==Demand.class) addDemand((Demand<T>) msg.getContent());
		else throw new UnhandledMessageType(msg.toString());
	}

	/*
	 * (non-Javadoc)
	 * @see org.simnation.simulation.agents.AbstractBasicAgent#handleEvent(java.lang.Enum,
	 * org.simplesim.core.scheduling.Time)
	 */
	@Override
	protected void handleEvent(Event event, Time time) {
		switch (event) { // all other events are handled here...
		case MARKET_CLEARING:
			enqueueEvent(Event.MARKET_CLEARING,period);
			break;
		default: // error: event type not known - this should never happen!
			throw new UnhandledEventType(event.toString());
		}

	}

	/*
	 * (non-Javadoc)
	 * @see org.simnation.simulation.agents.AbstractBasicAgent#executeStrategy(org.simplesim.core.scheduling.Time)
	 */
	@Override
	protected void executeStrategy(Time time) {
		for (T segment : segmentList) {
			List<Demand<T>> demandList=getState().getDemandList(segment);
			List<Supply<T>> supplyList=getState().getSupplyList(segment);
			getStrategy().doMarketClearing(this,demandList,supplyList);

			for (Supply<T> item : supplyList) sendMessage(this.getAddress(),item.getAddr(),item);
			supplyList.clear();
			for (Demand<T> item : demandList) sendMessage(this.getAddress(),item.getAddr(),item);
			demandList.clear();
		}
	}

	private void addDemand(Demand<T> demand) {
		getState().getDemandList(demand.getMarketSegmentSelector()).add(demand);
	}

	private void addSupply(Supply<T> supply) {
		getState().getSupplyList(supply.getMarketSegmentSelector()).add(supply);
	}

	/*
	 * Does the actual exchange of item vs. money with the specifics of <T> called by the strategy
	 */
	abstract void trade(Demand<T> d, Supply<T> s, long amount, double price);

	public MarketStrategy<T> getStrategy() {
		return strategy;
	}

	public void setStrategy(MarketStrategy<T> value) {
		strategy=value;
	}

}
