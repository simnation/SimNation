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
 * @see <a href=
 *      "https://fanf2.user.srcf.net/hermes/doc/antiforgery/stats.pdf">Reference
 *      for exponentially-weighted mean and variance</a>
 * 
 */
public class Storage {

	private final Batch batch; // stored items
	private double average; // demand average 
	private double variance; // demand variance
	private int count; // total requests
	private int misses; // out-of-stock requests
	private float alpha=0.1f; // exponential smoothing factor

	public Storage(Good good) {
		batch=new Batch(good);
		resetStatistics();
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
		return batch.split(updateStatistics(quantity));
	}

	/**
	 * Restocks the inventory.
	 * <p>
	 * Note: Statistics should be reset after restocking.
	 * 
	 * @param item the new batch added to the inventory
	 */
	public void restock(Batch item) {
		if (!batch.isSameGoodAs(item))
			throw new IllegalArgumentException("Tried to store a batch of a different good!");
		batch.merge(item);
	}

	/**
	 * Estimates the reorder volume based on previous demand, safety stock and current stock level.
	 * <p>
	 * Note: Reset statistics to synchronize with order cycle 
	 * 
	 * @param sl the desired service level (0<sl<=1)
	 * @return reorder volume for next service period
	 */
	public long calcReorderVolume(float sl) {
		// calc safety stock based on service level and variance of previous demands
		double safetyStock=getSafetyFactor(sl)*Math.sqrt(getDemandVariance()*count);
		// add estimated demand volume of next period
		long targetQuantity=Math.round(count*getDemandAverage()+safetyStock);
		// consider remaining quantity in stock
		if (targetQuantity<getStockLevel()) return targetQuantity-getStockLevel();
		return 0; // stock sufficient, no reorder
	}

	public Good getGood() { return batch.getType(); }

	public boolean isEmpty() { return batch.isEmpty(); }

	/**
	 * Returns the actual service level of this inventory
	 * 
	 * @return the current service level
	 */
	public float getServiceLevel() {
		if (count==0) return 0;
		return (count-misses)/count;
	}

	public long getStockLevel() { return batch.getQuantity(); }

	public double getAverageValue() { return batch.getPrice(); }

	public double getTotalValue() { return batch.getTotalValue(); }

	public float getSmoothingFactor() { return alpha; }

	/**
	 * Sets the factor for exponential smoothing.
	 * 
	 * @param value new value of smoothing factor
	 */
	public void setSmoothingFactor(float value) { alpha=value; }

	public double getDemandAverage() { return average; }

	public double getDemandVariance() { return variance; }

	public void resetStatistics() {
		average=variance=0;
		misses=count=0;
	}

	private long updateStatistics(long orderedQuantity) {
		// do statistics
		if (count==0) average=orderedQuantity;
		else { // see reference in class documentation for algorithm 
			final double diff=orderedQuantity-average;
			final double incr=alpha*diff;
			average=average+incr; // update mean
			variance=(1-alpha)*(variance+diff*incr); // recursive calculation based on Welford's method
		}
		// determine maximum quantity
		count++;
		if (orderedQuantity<=getStockLevel()) return orderedQuantity;
		misses++; // record stock-out
		return getStockLevel();
	}

	/**
	 * Returns the inverse of the standardized normal distribution for a given value
	 * (sl=desired service level).
	 * 
	 */
	private static float getSafetyFactor(float sl) {
		if (sl<0.51f) return 0; // default value for service level below 51%
		if (sl>=1.00f) return MAX_SAFETY_FACTOR; // for sl>=100% return max safety factor 
		int index=(int) ((sl-0.51f)*100.0f);
		return SAFETY_FACTOR[index];
	}

	private static final float MAX_SAFETY_FACTOR=3.5f;

	/**
	 * Inverse of the standardized normal distribution from 51% to 100% (equals
	 * NORMSINVERS from LibreOffice)
	 */
	private static final float[] SAFETY_FACTOR= { 0.025069f, 0.050154f, 0.075270f, 0.100434f, 0.125661f, 0.150969f,
			0.176374f, 0.201893f, 0.227545f, 0.253347f, 0.279319f, 0.305481f, 0.331853f, 0.358459f, 0.385320f,
			0.412463f, 0.439913f, 0.467699f, 0.495850f, 0.524401f, 0.553385f, 0.582842f, 0.612813f, 0.643345f,
			0.674490f, 0.706303f, 0.738847f, 0.772193f, 0.806421f, 0.841621f, 0.877896f, 0.915365f, 0.954165f,
			0.994458f, 1.036433f, 1.080319f, 1.126391f, 1.174987f, 1.226528f, 1.281552f, 1.340755f, 1.405072f,
			1.475791f, 1.554774f, 1.644854f, 1.750686f, 1.880794f, 2.053749f, 2.326348f, 2.575829f };
}
