/**
 * 
 */
package org.simnation.agents.firm.manufacturer;

/**
 * Calculates a function value for x based on polynomial interpolation of a given set of points
 * 
 * Uses the algorithm of Neville.
 * 
 * @author Rene Kuhlemann
 *
 */
public final class Forecast {
    
    public static final int HISTORY_LENGTH=5; // number of stored base points
    
    private final double x[];    // queue x-values ==> input of production function
    private final double y[];    // queue y-values ==> output of production function
    private int index;           // index of next available slot
    
    
    // idea: always keep an additional point (0/0) at x[HISTORY_LENGTH]
    // --> enforces restriction "with no input there is no output"
    public Forecast(double x[], double y[]) {
        assert(x.length==y.length);
        assert(HISTORY_LENGTH==x.length); 
        // init array with some pre-calculated value pairs
        this.x=x;
        this.y=y;
        /*this.x=new double[HISTORY_LENGTH+1];
        this.y=new double[HISTORY_LENGTH+1];
        System.arraycopy(x,0,this.x,0,HISTORY_LENGTH);
        System.arraycopy(y,0,this.y,0,HISTORY_LENGTH);
        this.x[HISTORY_LENGTH]=0;
        this.y[HISTORY_LENGTH]=0; */
        index=0;
    }

    public void addPoint(int input, int output) {
        int i;
        // is there already a value stored for input? --> would cause error in Neville's algorithm 
        for (i=0; i<HISTORY_LENGTH; i++) if (x[i]==input) break;
        // no, so only overwrite oldest value pair with actual values
        if (i>=HISTORY_LENGTH) {
            x[index]=input;
            y[index]=output;
            index++;
            if (index>=HISTORY_LENGTH) index-=HISTORY_LENGTH; // wrap-around
        } // yes, so only update corresponding y-value 
        else y[i]=output;
    }
    
    public double estimateOutput(double t) {
        // store y-values in temporary buffer
        double z[]=y.clone();
        // calculate function value for t according to Neville's algorithm
        for (int j=1; j<HISTORY_LENGTH; j++)
            for (int i=HISTORY_LENGTH-1; i>=j; i--) {
                z[i]=((t-x[i-j])*z[i]-(t-x[i])*z[i-1])/(x[i]-x[i-j]);
            }
        return(z[HISTORY_LENGTH-1]);
    }
    
    public double estimateInput(double t) {
        // store x-values in temporary buffer
        double z[]=x.clone();
        // calculate INVERSE function value for t according to Neville's algorithm
        for (int j=1; j<HISTORY_LENGTH; j++)
            for (int i=HISTORY_LENGTH-1; i>=j; i--) {
                z[i]=((t-y[i-j])*z[i]-(t-y[i])*z[i-1])/(y[i]-y[i-j]);
            }
        return(z[HISTORY_LENGTH-1]);
    }

}
