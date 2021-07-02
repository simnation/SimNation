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

/**
 * Provides evaluation of order data and simple forecasting.
 * <p>
 * Calculates average and variance by exponential smoothing.
 * 
 * @see <a href="https://fanf2.user.srcf.net/hermes/doc/antiforgery/stats.pdf">
 *      Reference for exponentially-weighted mean and variance</a>
 * 
 * @see <a href="https://en.wikipedia.org/wiki/Safety_stock"> Reference for
 *      safety stock calculation</a>
 * 
 */
public class WarehouseStatistics {

	private double average; // demand average 
	private double variance; // demand variance
	private int count; // total requests
	private int misses; // out-of-stock requests
	private float alpha=0.1f; // exponential smoothing factor

	public WarehouseStatistics() {
		reset();
	}

	/**
	 * Estimates the necessary stock level for a period based on previous demand,
	 * variance and service level.
	 * <p>
	 * Calculation is done for a full order cycle. Deviating from usual SCM
	 * formulas, period length refers to number of orders, not time. Presuming there
	 * is an equal amount of orders per period, this comes out to the same.
	 * <p>
	 * Note: Reset statistics to synchronize with order cycle
	 * 
	 * @param sl the desired service level (0<sl<=1)
	 * @return estimated stock level for next service period
	 */
	public long forecastStockLevel(float sl) {
		// calc safety stock for a full period based on service level and variance of previous demands
		double safetyStock=getSafetyFactor(sl)*Math.sqrt(getVariance()*count);
		// return estimated demand volume of next period plus safety stock
		return Math.round(count*getAverage()+safetyStock);
	}

	/**
	 * Estimates the volume of the next delivery to a market based on previous
	 * demand, variance and safety factor
	 * 
	 * @param sl the desired service level (0<sl<=1)
	 * @return estimated volume of outbound delivery
	 */
	public long forecastDelivery(float sl) {
		// calc safety stock for a single order based on service level and variance of previous demands
		double safetyStock=getSafetyFactor(sl)*Math.sqrt(getVariance());
		// return estimated demand volume plus safety stock
		return Math.round(getAverage()+safetyStock);
	}

	/**
	 * Update statistics with a new data point
	 * 
	 * @param quantity order volume taken from stock (must not be greater than stock
	 *                 level!)
	 * @param miss     failed fulfillment?
	 */
	public long update(long quantity, boolean miss) {
		if (count!=0) { // see reference in class documentation for algorithm 
			final double diff=quantity-average;
			final double incr=alpha*diff;
			average=average+incr; // update mean
			variance=(1-alpha)*(variance+diff*incr); // recursive calculation based on Welford's method
		} else average=quantity;
		count++;
		if (miss) misses++;
		return quantity;
	}

	/**
	 * Resets all statistics and begin a new time series
	 **/
	public void reset() {
		average=variance=0;
		misses=count=0;
	}

	public double getAverage() { return average; }

	public double getVariance() { return variance; }

	/**
	 * Calculate the service reliability.
	 * <p>
	 * The reliability is the actual service level.
	 */
	public double getReliability() {
		if (count==0) return Double.NaN;
		return (double) (count-misses)/count;
	}

	public float getSmoothingFactor() { return alpha; }

	public void setSmoothingFactor(float value) { alpha=value; }

	@Override
	public String toString() {
		return "Statistics | avg: "+average+", std: "+Math.sqrt(variance)+", reliability: "+getReliability();
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
