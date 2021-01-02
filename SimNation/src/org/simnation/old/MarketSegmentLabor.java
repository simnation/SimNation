package org.simnation.old;

// java
import java.util.LinkedList;

import org.simnation.core.Labor;
import org.simnation.core.Message;
import org.simnation.core.SkillDefinition;
import org.simnation.simulation.business.Demand;
import org.simnation.simulation.business.Tradable;
import org.simnation.simulation.business.Money;
import org.simnation.simulation.business.PaymentInfo;
import org.simnation.simulation.business.Supply;
import org.simnation.model.technology.Batch;
import org.simnation.model.technology.Good;

/**
 * Represents a segment of the local {@link GoodsMarketB2C} and offers a specialized clearing
 * mechanism
 * 
 * @author Rene Kuhlemann
 * @param <S> - segment within the market of the traded entitiy (SkillSet/type of Good)
 * @param <T> - classification of traded entity (Good/Credit/Labor) - must be {@link Tradable}!
 */
public final class MarketSegmentLabor extends MarketSegment2<SkillDefinition> {

	

    /**
     * Tries to satisfy all posted demands by the markets supply NOTES: - gives always the cheapest
     * possible supply satisfying the minimum quality criterion - first come, first served policy
     * for demands - splits delivery over several orders if necessary
     * 
     * @param agent - market agent containing this market segment
     */
    
	
    /* (non-Javadoc)
	 * @see org.simnation.simulation.agents.market.MarketSegment#clearMarket(org.simnation.simulation.agents.market.Market)
	 */
	public void clearMarket(Market<SkillDefinition,?> parent) {
		// TODO Auto-generated method stub
		
	}

}
