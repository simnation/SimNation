/**
 * 
 */
package org.simnation.common;

import java.util.ArrayList;
import java.util.List;

import org.simnation.context.technology.Good;
import org.simnation.core.Limits;


/**Provides an amount of precursors ({@link Batch}) and working power ({@link Labor}) as input 
 * of the production process
 * 
 * @author Rene Kuhlemann
 *
 */

public final class MachineJob {

	List<Batch> precursors;
	Labor labor;
		
	public MachineJob(Labor labor) {
        this.precursors=new ArrayList<Batch>(Limits.MAX_PRECURSORS);
        this.labor=labor;
    }
	
	public MachineJob() { this(null); }

	public Labor getLabor() { return(labor); }
	
	// set, if labor is not initialized, else add
	public void addLabor(Labor labor) {
	    if (labor==null) this.labor=labor;
	    else this.labor.merge(labor);
	}
	
	public Labor removeLabor(int amount) {
	    assert(labor.getAmount()>=amount);
	    return(labor.split(amount));
	}

	// set, if batch is not initialized, else add
	public void addBatch(Batch b) {
	    Batch batch=getBatch(b.getType());
		if (batch==null) precursors.add(b.unpack());
		else batch.merge(b.unpack());
	}

	public Batch removeBatch(Good type, int amount){
	    return(getBatch(type).split(amount));
	}

	public Batch getBatch(Good type) {
	    for (Batch iter : precursors) {
	        // a good is only initialized ONCE by the value chain, so == should be ok!
	        if (iter.getType()==type) return(iter);
	    } return(null);
	}

	public int countPrecursors() {
	    return(precursors.size());
	}

	@Override
	public String toString() {
		return("MachineJob[factors="+precursors+"][man_hours="+labor+"]");
	}

}
