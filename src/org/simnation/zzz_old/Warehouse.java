package org.simnation.zzz_old;

import java.util.ArrayList;
import java.util.List;

import org.simnation.agents.common.Batch;
import org.simnation.agents.firm.manufacturer.Manufacturer;
import org.simnation.context.technology.Good;
import org.simnation.core.Limits;
import org.simnation.core.Product;

/**
 * A warehouse is a building to store {@link Batch}es of {@link Product}s. It is used by an {@link Enterprise} 
 * to ensure delivery ability.<p> 
 * {@link Manufacturer}: Precursors and outgoing products are stored in the same warehouse, each product is stored 
 * in a separate shelf, first slot (index=0) is the outgoing product. <p>
 * {@link Trader}: Buy and sells only ONE good, so there is only one {@link Storage} and there are convenience methods
 * implemented for the trader agent to access the first slot directly. <p>
 * So, a warehouse serves to buffer over-production and to realize a certain delivery reliability. On the other hand, it enables
 * to build up a safety stock to assure a continuous flow of goods.
 *
 *
 */
public final class Warehouse {

	private final List<Storage> inventory=new ArrayList<Storage>(Limits.MAX_PRECURSORS+1);
	private final static int OUTPUT_STORAGE_INDEX=0;

	public void addInputStorage(Storage storage) {
		if (inventory.isEmpty()) inventory.add(null); // index 0 i reserved for output
		inventory.add(storage);
	}

	public void setOutputStorage(Storage storage) {
		if (inventory.isEmpty()) inventory.add(storage);
		else inventory.set(OUTPUT_STORAGE_INDEX,storage);
	}

	public boolean isStorageInstalled(Good good) {
		return get(good)!=null;
	}

	public boolean isOnStock(Good good,int minQuantity) {
		return get(good).isOnStock(minQuantity);
	}
	
	public boolean isOnStock(int minQuantity) {
		return getOutputStorage().isOnStock(minQuantity);
	}

	public int getQuantityOnStock(Good good) {
		return get(good).getTotalStockLevel();
	}
	
	public int getQuantityOnStock() {
		return getOutputStorage().getTotalStockLevel();
	}

	public int getItemsOnStock(Good good) {
		return get(good).getBatchesOnStock();
	}
	
	public int getItemsOnStock() {
		return getOutputStorage().getBatchesOnStock();
	}

	public double getValueOnStock(Good good) {
		return get(good).getValueOnStock();
	}
	
	public double getValueOnStock() {
		return getOutputStorage().getValueOnStock();
	}

	/**
	 * Removes the passed number of packages of required good from stock. If there are not enough product on stock the
	 * returning value will contain the maximum quantity of the stored product and leave an empty shelf.
	 *
	 * @param data.good
	 * @param quantity
	 * @return Product containing number of removed items.
	 */
	public Batch removeFromStock(Batch batch,int quantity) {
		return removeFromStock(batch.getType(),quantity);
	}

	public Batch removeFromStock(Good good,int quantity) {
		return get(good).removeFromStock(quantity);
	}
	
	public Batch removeFromStock(int quantity) {
		return getOutputStorage().removeFromStock(quantity);
	}

	public void putOnStock(Batch item) {
		get(item.getType()).putOnStock(item);
	}

	public float getActualServiceLevel(Good good) {
		return get(good).getActualServiceLevel();
	}
	
	public float getActualServiceLevel() {
		return getOutputStorage().getActualServiceLevel();
	}

	public void resetStatistics(Good good,int time) {
		get(good).resetStatistics(time);
	}
	
	public void resetStatistics(int time) {
		getOutputStorage().resetStatistics(time);
	}

	public Storage getOutputStorage() {
		return inventory.get(OUTPUT_STORAGE_INDEX);
	}

	private Storage get(Good good) {
		for (final Storage iter : inventory)
			// a good is only initialized ONCE by the value chain, so == should be ok!
			if (iter.getGood()==good) return iter;
		return null;
	}

	@Override
	public String toString() {
		return "Warehouse [Shelfs= "+inventory+"]";
	}

}
