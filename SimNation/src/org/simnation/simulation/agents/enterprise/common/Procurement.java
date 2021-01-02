package org.simnation.simulation.agents.enterprise.common;
// java

import org.simnation.core.Message;
import org.simnation.model.technology.Batch;
// simnation
import org.simnation.simulation.agents.enterprise.Enterprise;
import org.simnation.simulation.agents.enterprise.EnterpriseState;
import org.simnation.simulation.agents.market.GoodsMarketB2B;
import org.simnation.simulation.business.DemandMtS;
import org.simnation.simulation.business.Offer;
import org.simnation.simulation.business.SupplyMtO;
import org.simnation.simulation.model.Good;

/**Is responsible for the inventory management of the input warehouse. Tries to maintain a
 * delivery reliability given by the service level by ordering products at the national goods 
 * market (B2B). Inventory management follows an (r,S)-policy (constant control intervals with
 * variable order amount).  
 * 
 * @author Martin Sanski und Rene Kuhlemann
 *
 */

public final class Procurement {
    
    //private static final double DEFAULT_LEADTIME=3*Time.DAY;
	
	private final Enterprise<?> parent;

	public Procurement(Enterprise<?> enterprise) {
		parent=enterprise;
	}

	public void checkInventory() {
	    EnterpriseState state=parent.getState();
	    for (Storage storage : state.getWarehouse().getInputStorages()) {
	        if (storage.reachedReorderLevel()) {
	            int amount=storage.calcOrderVolume(state.getServiceLevel(),state.getTime());
	            // willingness to pay is derived by price of current stock and a correction factor
	            // a factor of 1.1 means the price per unit can be max 10% higher than actual stock price
	            float max_price=state.getMaxPriceFactor()*storage.getAverageValue();
	            // commission demand...
				DemandMtS<Good> demand=new DemandMtS<Good>(parent.getAddress(),storage.getGood(),
				        amount,max_price,state.getMinQuality(),state.getTime());
				// ...and send it to the goods market (B2B)
				Message<DemandMtS<Good>> msg=new Message<DemandMtS<Good>>(parent.getAddress(),
				        GoodsMarketB2B.getStaticAddress(),demand);
				parent.sendMessage(msg);
				storage.resetStatistics(state.getTime());
			}
		}		
	}

    public void handleDelivery(SupplyMtO<Batch> delivery) {
        // TODO Auto-generated method stub
        
    }
    
    public void handleOffer(Offer<Good> offer) {
        // TODO Auto-generated method stub
        
    }
    
    

}
