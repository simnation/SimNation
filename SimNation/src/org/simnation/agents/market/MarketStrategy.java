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

import java.util.List;

import org.simnation.agents.business.Demand;
import org.simnation.agents.business.Supply;

/**
 * 
 *
 */
public interface MarketStrategy<T> {
	
	void doMarketClearing(Market<T> market, List<Demand<T>> demand, List<Supply<T>> supply);

}
