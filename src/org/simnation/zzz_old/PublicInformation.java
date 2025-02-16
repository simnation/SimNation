/**
 * 
 */
package org.simnation.zzz_old;

import java.util.ArrayList;
import java.util.List;

import org.simnation.agents.common.RegionalInformation;
import org.simnation.agents.market.MarketInfo;
import org.simnation.context.technology.Good;
import org.simnation.core.Address;



/**
 * This class offers public information (such as statistics) for agents without messaging. 
 * Thus, all data of the informer is read-only! 
 * 
 * @author Rene Kuhlemann
 *
 */
public final class PublicInformation {
    
   // private final NationalInformation ni=new NationalInformation();
    private final List<RegionalInformation> ri=new ArrayList<RegionalInformation>();
    
        
    public float getFutureExpectation() {
    	return 0.7f;
        //return(ni.getFutureExpectation());
    }
    
    public MarketInfo getMarketInfo(int region, Good key) {
        return(ri.get(region).getMarketInfo(key));        
    }
    
	public Address getGoodsMarketAddr(int node) {
		return(ri.get(node).getGoodsMarketAddr());
	}

	public Address getLaborMarketAddr(int node) {
		return(ri.get(node).getLaborMarketAddr());
	}
    
    
    
    
//************** private part ***********************************    
    
    // for initialization
   
    void setRegionalInformation(Address b2c, Address lm) {
    	//assert b2c.getRegion()==lm.getRegion();    	
        ri.add(new RegionalInformation(b2c,lm));
    }
    

}
