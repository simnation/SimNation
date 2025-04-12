package org.simnation.agents.firm;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.simnation.context.geography.RegionData;

@PersistenceCapable
public class EnterpriseDBS {
	
	@Persistent(primaryKey="true",valueStrategy=IdGeneratorStrategy.NATIVE)
	private int id;
	@Column(name="region",jdbcType="INTEGER",targetMember="id")
	private RegionData region;
	private int cash;
	
	public RegionData getRegion() {
		return region;
	}

	public void setRegion(RegionData region) {
		this.region=region;
	}

	
	public int getId() {
		return id;
	}

	
	public void setId(int id) {
		this.id=id;
	}

	public int getCash() {
		return cash;
	}

	public void setCash(int cash) {
		this.cash = cash;
	}

}
