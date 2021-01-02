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

import org.simnation.simulation.business.Demand;
import org.simnation.simulation.business.Supply;

/**
 * Provides generic functionality for market clearing and market statistics. 
 * <p>
 * There is a list of {@link Supply} and {@link Demand} as well as a list of
 * subscribers for the {@link MarketInfo}. Methods to handle supply and demand are moved to the derived classes
 *
 * @param <T> - type characterizing market segments, e.g. Good, SkillSet
 *
 */
public class MarketSegment {

}
