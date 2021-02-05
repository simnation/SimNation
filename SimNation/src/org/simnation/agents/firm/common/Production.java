/**
 * 
 */
package org.simnation.agents.firm.common;

import org.simnation.agents.common.Batch;
import org.simnation.agents.common.Labor;
import org.simnation.agents.common.MachineJob;
import org.simnation.agents.firm.manufacturer.Forecast;
import org.simnation.agents.firm.manufacturer.Manufacturer;
import org.simnation.agents.firm.manufacturer.ProductionPlan;
import org.simnation.core.Machine;
import org.simnation.core.SkillDefinition;
import org.simnation.core.Tools;
import org.simnation.simulation.model.Good;



/**Represents the production department and is responsible for the planning and execution of the
 * production process.
 * 
 * 
 * @author Martin Sanski
 *
 */

public final class Production {
	
    private final Manufacturer parent;   

	public Production(Manufacturer manufacturer) {
	    parent=manufacturer;
	}
	
	/**Sets up the production of the planned output.   
	 * 
	 * @param planned_output - previously planned output (should be a multiple of the product's package size) 
	 * @return the quantity that will be produced;  
	 */
	public int order(int planned_output) {
		assert(planned_output>=0);
		Machine machine=parent.getState().getMachine();
		ProductionPlan plan=new ProductionPlan(machine.getGood());
	    //System.out.println(prod.getName()+" ordered: "+planned_output_quantity);
		if (machine.isReady()) {
			// remove last production if this still not happened. 
			storeOutput();
			
			// 1. calc output to be produced taking care of the machine's capacity limit
			plan.setOutput(Tools.maximal(planned_output,Tools.roundOff(machine.getCapacity()*parent.getState().getProductionTime())));			
			// 2. estimate amount of input factors for production
			estimateInputFactors(plan);
			// 3. order inputs based on this estimation
			MachineJob input=orderInput(plan);
			// 4. estimate output based on actually deliverable input factors
			plan.setOutput(estimateOutput(input));
			// 5. correct amount of input factor according to the new output estimation 
			estimateInputFactors(plan);
			// 6. return surplus input factors to their stocks, if necessary 
			removeExcess(input,plan);
			// 7. charge machine and start production.
			machine.startProduction(input);			
			return(plan.getOutput());
		}
		else return(0);
	}

	/**Estimates quantities for each input factor and returns a input factor combination which 
     * should be sufficient to realize the planned output level. Planned output and production schould
     * be reasonable and feasable - there is no plausibility check!   
     *  
     * @param plan - preliminary {@link ProductionPlan} 
     */
    private void estimateInputFactors(ProductionPlan plan) {    
        Good good=parent.getState().getMachine().getGood();
        
        // 1. estimate inverse of production function for planned output
        double x=parent.getState().getMachine().getForecast().estimateInput(plan.getOutput());
        // 2. estimate amount of labor (round up)...
        plan.setLabor(Tools.roundUp(x*good.getLabor()));
        // 3. ...and all necessary precursors (round up) 
        // based on estimated inverse of production function
        for (int i=0; i<good.getPrecursorCount(); i++) {
            plan.setAmount(good.getPrecursor(i),Tools.roundUp(x*good.getPrecursorAmount(i)));
        }
    }
    
    /**Orders the necessary precursors from the {@link Warehouse} and
     * requests the needed amount of labor from {@link Staffing}.
     * Returns a {@link MachineJob} with all allocated input factors.      
     * 
     * @param plan - ordered input factor combination
     * @return available combination of input factors
     */
    private  MachineJob orderInput(ProductionPlan plan) {
        MachineJob delivery=new MachineJob();
        Good good=parent.getState().getMachine().getGood();
        // 1. request needed man hours from staffing (production time is known there)
        delivery.addLabor(parent.getState().getStaffing().requestLabor(plan.getLabor(),
                parent.getState().getProductionTime(),SkillDefinition.PRODUCTION));
        // 2. remove requested inputs from warehouse if on stock
        for(int i=0;i<good.getPrecursorCount();i++) {
            Good iter=good.getPrecursor(i);
            delivery.addBatch(parent.getState().getWarehouse().removeFromStock(iter,plan.getAmount(iter)));
        }
        return(delivery);
    }
      
    /**Estimates the maximum output based on the available input
     * 
     * @param input - available input factors
     * @return estimation of the maximum output that can be produced
     */
    private int estimateOutput(MachineJob input) {
        Forecast forecast=parent.getState().getMachine().getForecast();
        Good output=parent.getState().getMachine().getGood();
        
        // 1. check output based on available working time
        double result=forecast.estimateOutput((double)input.getLabor().getWorkingHours()/output.getLabor());
        // 2. additionally, check for feasible output based on delivered precursors
        for (int i=0; i<output.getPrecursorCount(); i++) {
            Batch batch=input.getBatch(output.getPrecursor(i));
            double estimation=forecast.estimateOutput((double)batch.getQuantity()/output.getPrecursorAmount(i));
            if (estimation<result) result=estimation;
        }  // return the least output value as upper limit for production
        return(Tools.roundOff(result));
    }
    	
	/**Calculates the excess of unused input factors from the available input 
	 * @param delivery - input factors to be spent by the production process, to be corrected if necessary
	 * @param plan - the final production plan
	 */
	private MachineJob removeExcess(MachineJob delivery, ProductionPlan plan) {
	    // 1. return surplus labor
	    Labor surplus=delivery.getLabor().split(delivery.getLabor().getWorkingHours()-plan.getLabor());
	    parent.getState().getStaffing().returnLabor(surplus);
        // 2. return surplus precursors
        for (int i=0;i<parent.getState().getGood().getPrecursorCount();i++) {
            Good iter=parent.getState().getGood().getPrecursor(i);
            Batch batch=delivery.removeBatch(iter,delivery.getBatch(iter).getQuantity()-plan.getAmount(iter));
            parent.getState().getWarehouse().putOnStock(batch);
        }        
        return(delivery);
    }
	
	/**Stores the output of the last production cycle in the output warehouse.
     */
    public void storeOutput() {
        assert(parent.getState().getMachine().isReady());
        Batch output=parent.getState().getMachine().removeOutput();
        parent.getState().getWarehouse().putOnStock(output);             
    }
		
}
