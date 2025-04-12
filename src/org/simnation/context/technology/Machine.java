/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable
 * and used JSimpleSim as technical backbone for concurrent discrete event
 * simulation.
 *
 * This software is published as open source and licensed under GNU GPLv3.
 *
 * Contributors: - Rene Kuhlemann - development and initial implementation
 *
 */
package org.simnation.context.technology;


import org.simplesim.core.scheduling.Time;

import javax.jdo.annotations.PersistenceCapable;

import org.simnation.agents.firm.manufacturer.Forecast;
import org.simnation.common.Batch;
import org.simnation.common.MachineJob;
import org.simnation.zzz_old.Product;


/**
 * Transforms input factors into a defined output.
 * 
 * A machine is a good to produce a good. In contrast to most other goods it is not a good to consume or for further processing,
 * but an investment good. A  machine producing a machine
 * introduces a self-replicating element into the value chain.
 * 
 * First, a draft of a {@link Product} has to be passed to the machine to serve as a prototype.
 * 
 * This draft defines the {@link Good} type of the machine output. 
 * The {@link Good} types of inputs and the production function to quantify the output 
 * are stored in the singleton {@link PrototypeSet} as {@link Prototype}.
 *  
 * 
 * 
 * {@see Good}
 * {@see Product}
 * {@see Batch}
 *
 */
@PersistenceCapable
public class Machine {

    private double capacity;  // capacity of the machine measured UNITS per MINUTE
    private boolean ready;          // is the machine free?
	
    
	private Forecast initForecast() {
	    /*double x[]=new double[Forecast.HISTORY_LENGTH];
	    double y[]=new double[Forecast.HISTORY_LENGTH];
	    double input[]=new double[prod.getGood().getPrecursorsTotal()+1];
	    double step=(double)capacity/(double)x.length;
	    for (int i=0; i<Forecast.HISTORY_LENGTH; i++) {
	        double f=i;
	        x[i]=f*step;
	        input[0]=f*prod.getGood().getManhours().getTicks();
	        // USE PREDUCTION FUNCTION HERE !!!!
	        // TO DO !!!
	        for (int j=0; j<prod.getGood().getPrecursorsTotal(); j++) {
	            input[j+1]=f*prod.getGood().getPrecursor(j).getAlpha();
	        }
	        y[i]=type.getProductionFunction().calcMaxOutput(input);
	    }
	    return(new Forecast(x,y)); */
	    return null;
	}
		

	/**
     * Starts next production cycle if the machine is free.<br>
     * Machine inputs will be completely consumed to produce the maximum output possible.
     * To minimize excess, the {@link Production} department should only offer an adequate amount
     * of precursors and labor. The machine has to be queried to remove the output of the last
     * production cycle, otherwise the machine cannot start!  
     *  
     * @param mi - {@link MachineJob} for production
     * @return busy time of the machine
     */
	public float startProduction(MachineJob mi) {
		/*float weight=1;
		float quality=0;
		double input[]=new double[prod.getGood().getPrecursorCount()+1]; 
		// man hours for production
		input[0]=mi.getLabor().getWorkingHours();
		// all other input factors
		for(int i=0; i<prod.getGood().getPrecursorCount(); i++) {
		    input[i+1]=mi.getBatch(prod.getGood().getPrecursor(i)).getAmount();
		    float q=prod.getGood().getQualityWeight(i);
		    quality+=q*mi.getBatch(prod.getGood().getPrecursor(i)).getQuality();
		    weight-=q;
		}
		assert(weight>=0);
		quality+=weight*mi.getLabor().getQualification();
		output=new Batch(prod,(int)(type.getProductionFunction().calcMaxOutput(input)/prod.getPackageSize()),0,quality);
		ready=false;
		return(prod.getGood().getMakespan());
		*/
		return 0;
	}

	/**Returns a draft of produced products if the actual production cycle is ready.
	 * Otherwise a null value will be returned. 
	 * <b> The produced product can removed only one time! 
	 * 	After this method call the product is no longer available.</b> 
	 *   
	 * @return draft of the actual produced products
	 */
	public Batch removeOutput() {
		/*if (this.isReady()) {
		    Batch result=output;
		    output=null;
			return(result);
		} else return(null);
		*/
		return null;
	}
	
	public void finishProduction() {
	    ready=true;
	}
	
	/**
	 * checks if enough time has been elapsed to complete the actual production cycle. 
	 * 
	 * @return true if last production cycle has been completed.
	 */
	public boolean isReady(){
		return(ready);
	}
		
	// Getters and Setters:
	public double getCapacity(){
		return(capacity);
	}
	
	public Good getGood() {
	    return(prod.getGood());
	}
	
	public int getPackageSize() {
	    return(prod.getPackageSize());
	}
	
	public Time getMakespan() {
	    // can be adapted to economies of scale later on
	    return(prod.getGood().getProductionTime());
	}

	// a new package size means a new product!!! package size is final!
	// a new product needs new advertising, so initial advertising is zero!
	public void setPackageSize(int x) {
	    prod=new Product(prod.getGood(),x);
	}

	
	public Product getProduct() {
		return prod;
	}
	
}
