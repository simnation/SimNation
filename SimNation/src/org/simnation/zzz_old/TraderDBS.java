package org.simnation.zzz_old;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.PersistenceCapable;

import org.simnation.agents.firm.EnterpriseDBS;
import org.simnation.context.technology.Good;

import javax.jdo.annotations.InheritanceStrategy;

/**
 * Saves the {@link ManufactuerState} in a form that can directly be made persistent by a database. The encapsulated
 * data is converted during the initialization process of the agent's constructor
 *
 * @author Rene
 *
 */
@PersistenceCapable
@Inheritance(strategy=InheritanceStrategy.NEW_TABLE)
public class TraderDBS extends EnterpriseDBS {

	@Column(name="good")
	private Good good; // reference to the corresponding good
	private int pckgSize;
	private int initAmount;
	private int initPrice;
	private float initQuality;
	private int initMoney; 

	public Good getGood() {
		return good;
	}

	public void setGood(Good good) {
		this.good=good;
	}

	public int getPackageSize() {
		return pckgSize;
	}

	public void setPackageSize(int packageSize) {
		pckgSize=packageSize;
	}

	public int getInitAmount() {
		return initAmount;
	}

	public void setInitAmount(int initAmount) {
		this.initAmount=initAmount;
	}

	public int getInitPrice() {
		return initPrice;
	}

	public void setInitPrice(int initPrice) {
		this.initPrice=initPrice;
	}

	public float getInitQuality() {
		return initQuality;
	}

	public void setInitQuality(float initQuality) {
		this.initQuality=initQuality;
	}

	public int getInitMoney() {
		return initMoney;
	}

	public void setInitMoney(int initMoney) {
		this.initMoney = initMoney;
	}



}
