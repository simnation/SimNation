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
package org.simnation.agents.market;

import java.util.ArrayList;
// java
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.simnation.agents.business.Demand;
import org.simnation.agents.business.Supply;
import org.simplesim.model.State;

/**
 * Basic market state contains all general data all markets have
 * 
 * @param <T> - type characterizing market segments, e.g. Good, SkillSet
 */

public class MarketState<T> implements State {

	private final Map<T, List<Demand<T>>> demandMap = new IdentityHashMap<>();
	private final Map<T, List<Supply<T>>> supplyMap = new IdentityHashMap<>();

	private final Map<T, MarketData> statistics = new IdentityHashMap<>();

	public MarketState(Set<T> segmentList) {
		for (T segment : segmentList) {
			demandMap.put(segment, new ArrayList<Demand<T>>());
			supplyMap.put(segment, new ArrayList<Supply<T>>());
			statistics.put(segment, new MarketData());
		}
	}

	MarketData getMarketData(T segment) { return statistics.get(segment); }

	List<Demand<T>> getDemand(T segment) { return demandMap.get(segment); }

	List<Supply<T>> getSupply(T segment) { return supplyMap.get(segment); }

}
