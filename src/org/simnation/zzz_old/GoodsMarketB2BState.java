package org.simnation.zzz_old;

// java
import java.util.IdentityHashMap;
import java.util.Iterator;

import org.simnation.common.Batch;
import org.simnation.simulation.model.Good;
import org.simnation.simulation.model.ValueChain;
import org.simnation.simulation.scenario.GoodsMarketB2BStateReader;
import org.simnation.simulation.scenario.MarketStateReader;
// simnation




/**National goods market for trading between enterprises
 * 
 * @author Rene Kuhlemann
 *
 */

@SuppressWarnings("serial")
public final class GoodsMarketB2BState extends MarketState<GoodsMarketB2BStateReader<?>,Good,Batch> {

	public GoodsMarketB2BState() {
	    super(new IdentityHashMap<Good,MarketSegment2<Good,Batch>>());
	    Iterator<Good> iter=GoodSet.getInstance().getAllGoods();
	    while(iter.hasNext()) {
	        Good good=iter.next();
	        //setMarketSegment(good,new MarketSegment<Good,Batch>());
	    }
	}



	@Override
	public void load(GoodsMarketB2BStateReader<?> reader) throws Exception {
		// TODO Auto-generated method stub
		
	}
			
}
