package org.simnation.persistence;

import javax.jdo.annotations.PersistenceCapable;


/**
 * Contains the initial state and info of a regional goods market
 * 
 * @author Rene Kuhlemann
 */
@PersistenceCapable
public class B2CMarketDBS {

   

    private int suppliers; // the number of suppliers (=competitors)
        
    private float avgPrice;
    private float avgQuality; // to calculate average quality of traded items
    private float avgPackageSize; // to calculate average package size
    // private int open_supply;
    // private int open_demand;
    private float trendPrice=0;
    private float trendQuality=0;
    private double trendVolume=0;
  
    public B2CMarketDBS(float price, float quality, float pckg, int supp) {
    	avgPrice=price;
    	avgQuality=quality;
    	avgPackageSize=pckg;
    	suppliers=supp;    	
    }

    
    
    
    public int getSupplierCount() {
        return(suppliers);
    }

    
    public float getAveragePrice() {
        return(avgPrice);
    }

    public float getAverageQuality() {
        return(avgQuality);
    }

    public float getAveragePackageSize() {
        return(avgPackageSize);
    }

    public float getTrendPrice() {
        return(trendPrice);
    }

    public float getTrendQuality() {
        return(trendQuality);
    }

    public double getTrendVolume() {
        return(trendVolume);
    }

}
