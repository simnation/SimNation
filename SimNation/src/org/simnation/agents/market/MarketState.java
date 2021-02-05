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

import org.simnation.agents.business.Demand;
import org.simnation.agents.business.Supply;
import org.simplesim.model.State;


/**
 * Basic market state contains all general data all markets have
 * 
 * @param <T> - type characterizing market segments, e.g. Good, SkillSet
 */

public class MarketState<T> implements State {

	
	private final Map<T,List<Demand<T>>> demandList=new IdentityHashMap<>();
	private final Map<T,List<Supply<T>>> supplyList=new IdentityHashMap<>();
	
	public MarketState(List<T> segmentList) {
		for (T segement : segmentList) {
			demandList.put(segement,new ArrayList<Demand<T>>());
			supplyList.put(segement,new ArrayList<Supply<T>>());
		}
		
	}

public List<Demand<T>> getDemandList(T segment) {
	return demandList.get(segment);
}

public List<Supply<T>> getSupplyList(T segment) {
	return supplyList.get(segment);
}


}
