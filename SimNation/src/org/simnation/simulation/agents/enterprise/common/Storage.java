package org.simnation.simulation.agents.enterprise.common;

import java.util.List;

import org.simnation.core.Product;
import org.simnation.core.Time;
import org.simnation.core.Tools;
import org.simnation.model.technology.Batch;
import org.simnation.model.technology.Good;

/**
 * A shelf is a storage rack placed in warehouses. Each shelf contains only one type of {@link Good} but can store
 * several {@link Batch}es of {@link Product}s. The storage is implemented as strategy pattern to enable different
 * implementations, e.g. a First In - First Out" rack like a queue or a "First Expire - First Out" rack for food.
 *
 * @author Martin Sanski and Rene Kuhlemann
 *
 */
public abstract class Storage {

	private double sum; // sum of amounts taken from inventory yet
	private double sqr; // sum of squares to calc variance
	private int time; // starting time of statistic period
	private int reorder_period; // how long is an order supposed to last?
	private int safety_stock; // level of safety stock
	private int max_inventory; // maximum inventory level
	private int count; // counts each request
	private int misses; // counts each non-deliverable request
	private float actual_sl=0;

	public abstract Good getGood();

	public abstract Batch removeFromStock(int quantity);

	public abstract void putOnStock(Batch item);

	public abstract int getBatchesOnStock();

	public abstract int getTotalStockLevel(); // total amount in good units

	public abstract float getAverageValue();

	public abstract List<Batch> getStockOnHand();

	protected Storage(int start) {
		setReorderPeriodLength(14*Time.DAY);
		resetStatistics(start);
	}

	public int calcOrderVolume(float service_level,double actual_time) {
		// calculate the safety stock to provide the service level during the leadtime
		safety_stock=Tools.roundUp(calcSafetyStock(service_level,reorder_period/Time.DAY));
		// calc gap between max inventory and safety stock based on off-rate in last period
		final int gap=Tools.roundUp(reorder_period/(actual_time-time)*getVolumeAverage()); // =L+leadtime
		max_inventory=safety_stock+gap;
		// volume to order is the difference between maximum and actual inventory level
		return max_inventory-getTotalStockLevel();
	}

	public void resetStatistics(int start) {
		if (count!=0) actual_sl=(float) (count-misses)/count;
		time=start;
		sum=0;
		sqr=0;
		misses=0;
		count=0;
	}

	protected void updateStatistic(double amount,boolean hit) {
		if (hit==false) misses++;
		count++;
		sum+=amount;
		sqr+=amount*amount;
	}

	public double getVolumeAverage() {
		if (count==0) return 0;
		return sum/count;
	}

	public double getVolumeVariance() {
		if (count==0) return 0;
		final double avg=getVolumeAverage();
		return sqr/count-avg*avg; // Verschiebungssatz, Grundgesamtheit bekannt
	}

	public double getValueOnStock() {
		return (double) getAverageValue()*getTotalStockLevel();
	}

	// based on last period!!!
	public float getActualServiceLevel() {
		return actual_sl;
	}

	public boolean reachedReorderLevel() {
		return getTotalStockLevel()<safety_stock;
	}

	public boolean isEmpty() {
		return getTotalStockLevel()==0;
	}

	public boolean isOnStock(int quantity) {
		return getTotalStockLevel()>=quantity;
	}

	public boolean storesGood(Good good) {
		return getGood()==good;
	}

	public void setReorderPeriodLength(int reorder_period) {
		this.reorder_period=reorder_period;
	}

	public int getReorderPeriodLength() {
		return reorder_period;
	}

	private double calcSafetyStock(float service_level,double reorder_days) {
		return getSafetyFactor(service_level)*Math.sqrt(getVolumeVariance()*reorder_days);
	}

	/**
	 * Returns the inverse of the standardized normal distribution (NORMSINVERS in Excel) for a given value (sl=desired
	 * service level).
	 * 
	 * @author Martin Sanski
	 *
	 */
	private static final float[] service_level= {
			0.510000f,0.520000f,0.530000f,0.540000f,0.550000f,0.560000f,0.570000f,0.580000f,0.590000f,0.600000f,
			0.610000f,0.620000f,0.630000f,0.640000f,0.650000f,0.660000f,0.670000f,0.680000f,0.690000f,0.700000f,
			0.710000f,0.720000f,0.730000f,0.740000f,0.750000f,0.760000f,0.770000f,0.780000f,0.790000f,0.800000f,
			0.810000f,0.820000f,0.830000f,0.840000f,0.850000f,0.860000f,0.870000f,0.880000f,0.890000f,0.900000f,
			0.910000f,0.920000f,0.930000f,0.940000f,0.950000f,0.960000f,0.970000f,0.980000f,0.990000f,
			0.995000f,1.000000f};
	private static final float[] safety_factor= {
			0.025069f,0.050154f,0.075270f,0.100434f,0.125661f,0.150969f,0.176374f,0.201893f,0.227545f,0.253347f,
			0.279319f,0.305481f,0.331853f,0.358459f,0.385320f,0.412463f,0.439913f,0.467699f,0.495850f,0.524401f,
			0.553385f,0.582842f,0.612813f,0.643345f,0.674490f,0.706303f,0.738847f,0.772193f,0.806421f,0.841621f,
			0.877896f,0.915365f,0.954165f,0.994458f,1.036433f,1.080319f,1.126391f,1.174987f,1.226528f,1.281552f,
			1.340755f,1.405072f,1.475791f,1.554774f,1.644854f,1.750686f,1.880794f,2.053749f,2.326348f,
			2.575829f,3.500000f};

	private static float getSafetyFactor(float sl) {
		float result=0; // sl<0.51
		if (sl>=1.00f) result=3.5f; // max safety factor 
		else if (sl>0.95f) {	// fine tuning for upper service levels 0.99, 0.995f, etc.
			for (int i=service_level.length-1; (service_level[i]>0.95f)&&(i>=0); i--)
				if (sl>=service_level[i]) {
					result=safety_factor[i];
					break;
				}
		} else if (sl>=0.51f) result=safety_factor[Tools.roundOff((sl-0.51d)*100)];
		return result;
	}

}
