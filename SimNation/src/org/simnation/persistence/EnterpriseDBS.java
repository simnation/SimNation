package org.simnation.persistence;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.simnation.model.geography.Region;

@PersistenceCapable
public class EnterpriseDBS {
	
	@Persistent(primaryKey="true",valueStrategy=IdGeneratorStrategy.NATIVE)
	private int id;
	@Column(name="region",jdbcType="INTEGER",targetMember="id")
	private Region region;
	private int cash;
	
	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
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
