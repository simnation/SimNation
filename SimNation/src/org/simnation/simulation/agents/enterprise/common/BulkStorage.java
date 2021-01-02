package org.simnation.simulation.agents.enterprise.common;

import java.util.LinkedList;
import java.util.List;

import org.simnation.model.technology.Batch;
import org.simnation.model.technology.Good;


/**
 * Storage with only on container for all (merged) batches, like a silo
 *
 * @author Martin Sanski and Rene Kuhlemann
 *
 */
public final class BulkStorage extends Storage {

	private final Batch batch; // type of stored items

	public BulkStorage(Good good,int start) {
		super(start);
		batch=new Batch(good.getStandardProduct());
	}

	public BulkStorage(Good good) {
		this(good,0);
	}

	public Batch removeFromStock(int request) {
		// if there is not enough on stock return what we have and count a miss;
		if (batch.getQuantity()<request) {
			updateStatistic(request,false);
			return batch.split(batch.getQuantity());
		} // otherwise decrease stock by requested quantity
		else {
			updateStatistic(request,true);
			return batch.split(request);
		}
	}

	public void putOnStock(Batch item) {
		assert batch.isSameGoodAs(item);
		batch.merge(item.unpack());
	}

	public List<Batch> getStockOnHand() {
		final List<Batch> list=new LinkedList<Batch>();
		list.add(batch);
		return list;
	}

	public int getTotalStockLevel() {
		return batch.getQuantity();
	}

	public float getAverageValue() {
		return batch.getPrice();
	}

	public int getBatchesOnStock() {
		return 1;
	}

	public Good getGood() {
		return batch.getProduct().getGood();
	}

	@Override
	public String toString() {
		return "Batch toString to be implemented";
	}

}
