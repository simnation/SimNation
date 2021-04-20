package org.simnation.agents.firm.trader;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.simnation.agents.business.Money;
import org.simnation.agents.common.Batch;
import org.simnation.agents.common.DatabaseState;
import org.simnation.agents.household.HouseholdState;
import org.simnation.context.geography.Region;
import org.simnation.context.technology.Good;

@PersistenceCapable
@DatastoreIdentity(strategy=IdGeneratorStrategy.IDENTITY)
public class TraderDBS implements DatabaseState<TraderState> {
	
	@Column(name="Region_FK")
	private Region region;
	@Column(name="Good_FK")
	private Good good;
	private float price;
	private float quality;
	private long stock;
	private long cash;
	
	
	public Region getRegion() {
		return region;
	}

	public void setRegion(Region value) {
		region=value;
	}

	public long getCash() {
		return cash;
	}

	public void setCash(long value) {
		cash = value;
	}

	@Override
	public void convertToDBS(TraderState state) { // TODO Auto-generated method stub
	 }

	@Override
	public TraderState convertToState() { 
		final TraderState state=new TraderState(getGood(),new Money(getCash()));
		final Batch batch=new Batch(getGood(),getStock(),getPrice(),getQuality());
		state.getStorage().restock(batch);
		state.setMargin(1.2f); // 20% margin
		state.setServiceLevel(0.95f); // 95% service level
		return state; 
	}

	public long getStock() { return stock; }

	public void setStock(long stock) { this.stock = stock; }

	public Good getGood() { return good; }

	public void setGood(Good good) { this.good = good; }

	public float getQuality() { return quality; }

	public void setQuality(float quality) { this.quality = quality; }

	public float getPrice() { return price; }

	public void setPrice(float price) { this.price = price; }

}