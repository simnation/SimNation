package org.simnation.agents.firm.trader;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;

import org.simnation.agents.business.Money;
import org.simnation.agents.common.Batch;
import org.simnation.agents.common.DatabaseState;
import org.simnation.context.geography.Region;
import org.simnation.context.technology.Good;

@PersistenceCapable
@DatastoreIdentity(strategy=IdGeneratorStrategy.IDENTITY)
public class TraderDBS implements DatabaseState<TraderState> {

	@Column(name="Region_FK")
	private Region region;
	@Column(name="Good_FK")
	private Good good;
	private long value;
	private float quality;
	private long stock;
	private long cash;

	public Region getRegion() { return region; }

	public void setRegion(Region value) { region=value; }

	public long getCash() { return cash; }

	public void setCash(long value) { cash=value; }

	@Override
	public void convertToDBS(TraderState state) { // TODO Auto-generated method stub
	}

	@Override
	public TraderState convertToState() {
		return convertToState(new TraderState(getGood(),new Money(getCash())));		
	}

	@Override
	public TraderState convertToState(TraderState state) { 
		final Batch batch=new Batch(getGood(),getStock(),getValue(),getQuality());
		state.getStorage().addToStock(batch);
		state.setMargin(1.2f); // 20% margin
		state.setServiceLevel(0.95f); // 95% service level
		return state;
	}

	public long getStock() { return stock; }

	public void setStock(long stock) { this.stock=stock; }

	public Good getGood() { return good; }

	public void setGood(Good good) { this.good=good; }

	public float getQuality() { return quality; }

	public void setQuality(float quality) { this.quality=quality; }

	public long getValue() { return value; }

	public void setValue(long value) { this.value=value; }

}