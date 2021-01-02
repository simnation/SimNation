/**
 * 
 */
package org.simnation.simulation.agents.enterprise.manufacturer;


import org.simnation.model.technology.Good;

/**Contains a preliminary production plan. When planning has finished, this can be used to order input factors 
 * 
 * @author Rene Kuhlemann
 *
 */
public final class ProductionPlan {
    
    private final Good[] precursor;         // precursors
    private final int[] amount;             // amount of each precursor
    private int labor=0;                    // amount of work needed IN MINUTES
    private int output=0;                   // output to be produced
    
    public ProductionPlan(Good good) {
    	final int count=good.getPrecursorCount();
        precursor=good.getPrecursorGoods().toArray(new Good[count]);
        amount=new int[count];
        for (int i=0;i<count;i++) amount[i]=0;
    }
    
    private int getIndex(Good good) {
        for (int i=0;i<precursor.length;i++) {
            if (precursor[i]==good) return(i);
        }
        throw new UnsupportedOperationException("ProductionPlan: unknown precursor passed!");
    }
    
    public int getAmount(Good good) { return(amount[getIndex(good)]); }
    
    public void setAmount(Good good,int amount) { this.amount[getIndex(good)]=amount; }

    public int getLabor() { return(labor); }

    public int getOutput() { return(output); }

    public void setLabor(int labor) { this.labor = labor; }

    public void setOutput(int output) { this.output = output; }
    
}
