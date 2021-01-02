package org.simnation.old;

import org.simnation.simulation.business.Tradable;


/**
 * Provides the supplier of a market with some statistical information. With help of this information {@link Enterprise}s
 * and {@link Household}s can react to the actual market situation.
 * 
 * @author Martin Sanski
 */
public final class MarketInfo {

    private static final float SMOOTHING=0.5f;

    // internal counter    
    private int dealsCounter=0; // number of deals
    
    // aggregators
    private int totalAmount=0; // amount of sold [pckgsize] units on the market
    private int totalVolume=0;  // number of sold item units on the market
    private double totalValue=0; // market turnover since last market information
    private double aggrQuality=0; // aggregated quality by volume to calc weighted quality
    
    // averages    
    private float avgPrice;
    private float avgQuality; // to calculate average quality of traded items
    private float avgPckgSize; // to calculate average package size
    
    // counters
    private int timeStamp=0; // time at which the following information has been collected
    private int suppliers=0; // the number of suppliers (=competitors)
    private int deals=0;
    
    // trends
    private float trendPrice=0;
    private float trendQuality=0;
    private double trendVolume=0;
    
    // private int open_supply;
    // private int open_demand;
  
    public MarketInfo(float price, float quality, float pckg, int supp) {
    	avgPrice=price;
    	avgQuality=quality;
    	avgPckgSize=pckg;
    	suppliers=supp;    	
    }
    
    public MarketInfo() {
    	this(100,0.1f,1.0f,1);
    }
    
    /**Update is done by {@link MarketSegment2} on each deal
     * 
     * @param m
     */
    void update(final Tradable<?> m) { 
    	totalAmount+=m.getQuantity();
    	totalVolume+=m.getTotalVolume();
    	totalValue+=m.getTotalValue();
    	aggrQuality+=(m.getQuality()*m.getTotalVolume());
    	dealsCounter++;
    }
    

    /** Update is done by {@link Market} agent regularly on market clearing
     *  
     * @param ts
     * @param supp
     */
    void update(int ts, int supp) {
    	timeStamp=ts;
    	suppliers=supp;
    	deals=dealsCounter;
        avgQuality=(float) (aggrQuality/totalVolume);
        avgPckgSize=(float) (totalVolume/totalAmount);
        avgPrice=(float) (totalValue/totalVolume);
        
    	if (trendPrice==0) {
            trendPrice=avgPrice;
            trendQuality=avgQuality;
            trendVolume=totalValue;
        } else { // do exponential smoothing to generate some basic trend information
            trendPrice=SMOOTHING*avgPrice+(1-SMOOTHING)*trendPrice;
            trendQuality=SMOOTHING*avgQuality+(1-SMOOTHING)*trendQuality;
            trendVolume=SMOOTHING*totalValue+(1-SMOOTHING)*trendVolume;
        }
    	reset();
    }
    
    private void reset() {
    	totalAmount=0;
        totalVolume=0;
        totalValue=0;
        aggrQuality=0;
        dealsCounter=0;
    }
    
    /*
     * public void setOpenDemand(int count) { this.open_demand=count; } public void setOpenSupply(int count) {
     * this.open_supply=count; } public int getOpenSupply() { return open_supply; } public int getOpenDemand() { return
     * open_demand; }
     */

    public int getTimeStamp() {
        return(timeStamp);
    }

    public double getSalesValue() {
        return(totalValue);
    }
    
    public int getSalesVolume() {
        return(totalVolume);
    }

    public int getSupplierCount() {
        return(suppliers);
    }

    public int getDeals() {
        return(deals);
    }

    public int getSoldUnits() {
        return(totalAmount);
    }

    public float getAveragePrice() {
        return(avgPrice);
    }

    public float getAverageQuality() {
        return(avgQuality);
    }

    public float getAveragePackageSize() {
        return(avgPckgSize);
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
