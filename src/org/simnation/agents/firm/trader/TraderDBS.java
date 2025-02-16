package org.simnation.agents.firm.trader;


import java.util.Random;

import org.simnation.agents.business.Money;
import org.simnation.agents.common.Batch;
import org.simnation.agents.common.DatabaseState;
import org.simnation.agents.firm.common.Storage;
import org.simnation.context.geography.Region;
import org.simnation.context.technology.Good;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class TraderDBS implements DatabaseState<TraderState> {
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	private int index;

	@OneToOne
	@JoinColumn(name="Region_FK")
	private Region region;
	
	@OneToOne
	@JoinColumn(name="Good_FK")
	private Good good;
	
	private long stockValue;
	private float stockQuality;
	private long stockQuantity;
	private long cash;

	public Region getRegion() { return region; }

	public void setRegion(Region value) { region=value; }

	public long getCash() { return cash; }

	public void setCash(long value) { cash=value; }

	@Override
	public void convertToDBS(TraderState state) { // TODO Auto-generated method stub
	}

	@Override
	public TraderState convertToState(TraderState state) {
		state.money=new Money(getCash());
		state.storage=new Storage(getGood());
		state.getStorage().addToStock(new Batch(getGood(),getStock(),getValue(),getQuality()));
		state.setMargin(1.2f); // 20% margin
		state.setServiceLevel(0.95f); // 95% service level
		return state;
	}

	public long getStock() { return stockQuantity; }

	public void setStock(long stock) { this.stockQuantity=stock; }

	public Good getGood() { return good; }

	public void setGood(Good good) { this.good=good; }

	public float getQuality() { return stockQuality; }

	public void setQuality(float quality) { this.stockQuality=quality; }

	public long getValue() { return stockValue; }

	public void setValue(long value) { this.stockValue=value; }
	
	public int getIndex() { return index; }

	public void setIndex(int index) { this.index=index; }

	@Override
	public void generateDBS(Region region, Random rnd) { // TODO Auto-generated method stub
	 }

}