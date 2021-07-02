/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable 
 * and used JSimpleSim as technical backbone for concurrent discrete event simulation.
 * 
 * This software is published as open source and licensed under GNU GPLv3.
 * 
 * Contributors:
 * 	- Rene Kuhlemann - development and initial implementation
 * 
 */
package org.simnation.agents.firm.common;

import org.simnation.agents.common.Batch;
import org.simnation.context.technology.Good;

/**
 * Storage represent the storage of a good and provides additional statistic
 * functionality.
 * <p>
 * The storage contains a {@code Batch} of a {@code Good}. Additional batches
 * are merged with the storage batch, yielding averaged price and quality.
 * Statistics support order quantity forecast by exponential smoothing and
 * safety stock calculation.
 * <p>
 * There is optional inventory management provided based on a
 * time-quantity-policy: Reorder or production happen in a constant lead-time. A
 * new stock level is calculated based on the actual demand during the
 * lead-time. Forecasting is done by a recursive algorithm based on exponential
 * smoothing.
 * 
 * @see Good
 * @see Batch
 * 
 */
public class Storage {

	private final Batch batch; // stored items
	private final WarehouseStatistics stat=new WarehouseStatistics();
	
	public Storage(Good good) {
		batch=new Batch(good);
	}

	/**
	 * Removes a quantity from the inventory and returns it as a new batch
	 * <p>
	 * Note: The quantity is limited by the overall batch size in stock.
	 * 
	 * @param quantity the requested quantity
	 * @return the new batch containing the quantity
	 */
	public Batch removeFromStock(long quantity) {
		if (getStockLevel()>=quantity) return batch.split(stat.update(quantity,false)); // fulfillment
		else return batch.split(stat.update(getStockLevel(),true)); // miss
	}

	/**
	 * Add batch to inventory.
	 * <p>
	 * Note: Statistics should be reset after restocking.
	 * 
	 * @param item the new batch added to the inventory
	 */
	public void addToStock(Batch item) {
		if (!batch.isSameGoodAs(item))
			throw new IllegalArgumentException("Storage.addToStock(): Tried to add a batch of a different good!");
		batch.merge(item);
	}

	/**
	 * Estimates the reorder volume based stock level forecast and current stock level.
	 * <p>
	 * Note: Reset statistics to synchronize with order cycle 
	 * 
	 * @param sl the desired service level (0<sl<=1)
	 * @return reorder volume for next service period
	 */
	public long calcReorderVolume(float sl) {
		final long reorderVolume=stat.forecastStockLevel(sl)-getStockLevel();
		if (reorderVolume<0) return 0;
		return reorderVolume;
	}

	public Good getGood() { return batch.getType(); }

	public boolean isEmpty() { return batch.isEmpty(); }

	public long getStockLevel() { return batch.getQuantity(); }

	public long getStockValue() { return batch.getValue(); }
	
}
