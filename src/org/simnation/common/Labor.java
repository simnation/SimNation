package org.simnation.common;

import org.simnation.agents.business.Tradable;
import org.simnation.core.SkillDefinition;

/**
 * Container of a certain amount of working power, similar to {@link Batch}
 *
 * @author Rene Kuhlemann
 *
 */
public final class Labor implements Tradable<SkillDefinition> {

	private final SkillDefinition skill; // specific skill of labor package (e.g. PRODUCTION)
	private int workingHours;
	private int wage;
	private float qualification;

	// amount = working hours per WORKING_PERIOD (=WEEK)
	// price = wage per HOUR
	// quality= qualification in that very skill

	public Labor(SkillDefinition s,int wh,int w,float q) {
		workingHours=wh;
		wage=w;
		qualification=q;
		skill=s;
	}

	public Labor(Labor lab) {
		this(lab.skill,lab.getAmount(),lab.getPrice(),lab.getQuality());
	}

	public Labor(SkillDefinition s) {
		this(s,0,0,0);
	}

	public Labor merge(Labor labor) {
		assert labor.getAmount()>=0;
		final int sum=getAmount()+labor.getAmount();
		qualification=(getAmount()*getQuality()+labor.getAmount()*labor.getQuality())/sum;
		wage=Math.round((getAmount()*getPrice()+labor.getAmount()*labor.getPrice())/sum);
		workingHours=sum;
		labor.utilize();
		return this;
	}

	public Labor split(int hours) {
		assert hours>0;
		assert getAmount()>=hours;
		workingHours-=hours;
		return new Labor(getType(),hours,getPrice(),getQuality());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simnation.simulation.business.Marketable#utitlize()
	 */
	public int utilize() {
		final int wh=getAmount();
		workingHours=0;
		return wh;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simnation.simulation.business.Marketable#getPackageSize()
	 */
	public int getPackageSize() {
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simnation.simulation.business.Marketable#getType()
	 */
	public SkillDefinition getType() {
		return skill;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simnation.simulation.business.Marketable#getPrice()
	 */
	public int getPrice() {
		return wage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simnation.simulation.business.Marketable#getQuality()
	 */
	public float getQuality() {
		return qualification;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simnation.simulation.business.Marketable#getAmount()
	 */
	public int getAmount() {
		return workingHours;
	}
}
