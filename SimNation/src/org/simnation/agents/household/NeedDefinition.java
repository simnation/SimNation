/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable
 * and used JSimpleSim as technical backbone for concurrent discrete event
 * simulation.
 *
 * This software is published as open source and licensed under GNU GPLv3.
 *
 * Contributors: - Rene Kuhlemann - development and initial implementation
 *
 */
package org.simnation.agents.household;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

import org.simnation.context.technology.Good;
import org.simnation.zzz_old.NeedSet;

/**
 * Main class to define the need system of the model.
 * <p>
 * A {@code Need} is the basic driver of the household's motivation system. The
 * aim of a {@code Household} is to satisfy its needs the best way possible.
 * This class defines all attributes of a need itself.
 * <p>
 * <b>Theoretical background</b><br>
 * A need is the very basic reason for a household to consume. There are three
 * categories of a need's urgency according to the ERG-Theory (Alderfer). If the
 * desire of a household reaches the <i>activation level</i>, the household
 * starts to look for a product satisfying the need. By consumption the desire
 * can be decreased, down to saturation level. Otherwise, if the need cannot be
 * satisfied, the desire increases even further, until it finally reaches the
 * <i>regression level</i>. Then the agent stops trying to satisfy the need and
 * resigns. <br>
 * As a result of a regression, the ERG level drops and the household focuses on
 * satisficing all needs of the next lower level. If the level of the
 * unfulfilled need is existential, the agent dies and is eliminated from the
 * model.Households will try to persuade the needs of the next higher ERG level,
 * if the needs of the current level are completely satisfied. <br>
 * The desired <i>stock</i> is the standard amount of a product a household
 * tries to acquire on the market. The desired <i>quality</i> of products
 * increases with the households satisfaction level (as a result, the demanded
 * quality level rises with what an increasing income). <br>
 * Acquired ERG level, fulfillment of desired stock and quality enable to
 * calculate an overall contentment of a household. That in turn may lead the
 * way to model political dynamics within the agent population
 * <p>
 * Note: Each needs should be instantiated only ONCE (just like {@code Good}),
 * so identity can be used!
 * 
 * @see NeedSet
 * @see Good
 * @see InstantNeed
 */
@PersistenceCapable
public class NeedDefinition {

	/**
	 * Needs can be satisfied instantly on consumption (e.g. food) or by
	 * availability for a period of time (e.g. housing). A periodical need is
	 * satisfied by a good that is a service.
	 */
	public enum DURATION {
		INSTANTLY, PERIODICALLY;
	}

	/**
	 * Needs can occur continuously (thirst) or randomly (medical support).
	 */
	public enum INCIDENCE {
		CONTINUOUSLY, RANDOMLY;
	}

	/**
	 * Needs have to be assigned to an urgency level:
	 * <ul>
	 * <li>EXISTENTIAL: without satisficing <u>all</u> existential needs, an
	 * individual dies eventually
	 * <li>BASIC: basic needs are not existential but crucial to be pursued after
	 * existence needs have been met
	 * <li>CONVENIENCE: needs that add to the overall content level of an individual
	 * <li>LUXURY: surplus needs addressing an individual's social status
	 * </ul>
	 * Needs are organized as a hierarchy where a new level can only be reached if
	 * <u>all</u> needs of the lower hierarchy level are satisfied (according to the
	 * theories by Maslow/Alderfer).
	 */
	public enum URGENCY {
		EXISTENTIAL, BASIC, CONVENIENCE, LUXURY;
	}

	@PrimaryKey
	private String name;
	
	/** link to good that can satisfy this need */
	@Column(name="GOOD_FK")
	private Good satisfier=null;

	/** the household's buy-ahead period */
	private int activationDays; // equals the need's planning period

	/**
	 * when does the household enter the resignation process? Must be longer than
	 * activation time!
	 */
	private int frustrationDays; // days after which the agent enters regression
	// each day one try!

	/**
	 * consumption of the good satisfying this need (in good units) PER DAY and PER
	 * HOUSEHOLD MEMBER
	 */
	private int dailyConsumptionAdult;
	
	private int dailyConsumptionChild;
	
	/** urgency level of this need */
	private URGENCY urgency;

	/** incidence of this need */
	private INCIDENCE incidence;

	/** duration of this need */
	private DURATION duration;
	
	/** the need event assigned to this need - this is a (1:1) relation */
	@NotPersistent
	private Household.EVENT event;
	
	/** caching the hash code */
	@NotPersistent
	private int hash=0;
	
	
	public int getIndex() {return event.ordinal(); }
	
	void setEvent(Household.EVENT value) { event=value; }
	
	public Household.EVENT getEvent() { return event; }

	public int getActivationDays() { return activationDays; }

	public int getDailyConsumptionAdult() { return dailyConsumptionAdult; }

	public int getDailyConsumptionChild() { return dailyConsumptionChild; }

	public DURATION getDuration() { return duration; }

	public int getFrustrationDays() { return frustrationDays; }

	public INCIDENCE getIncidence() { return incidence; }

	public String getName() { return name; }

	public Good getSatisfier() { return satisfier; }
	
	public URGENCY getUrgency() { return urgency; }
	
	public boolean isInstantNeed() { return getDuration()==DURATION.INSTANTLY; }

	public void setActivationDays(int value) { activationDays=value; }

	public void setDailyConsumptionAdult(int value) { dailyConsumptionAdult=value; }

	public void setDailyConsumptionChild(int dailyConsumptionChild) { this.dailyConsumptionChild = dailyConsumptionChild; }

	public void setDuration(DURATION value) { duration=value; }

	public void setFrustrationDays(int value) { frustrationDays=value; }

	public void setIncidence(INCIDENCE value) { incidence=value; }

	public void setName(String value) { name=value; hash=name.hashCode(); }

	public void setSatisfier(Good value) { satisfier=value; }

	public void setUrgency(URGENCY value) { urgency=value; }

	@Override
	public boolean equals(Object that) {
		return this==that;
	}

	@Override
	public int hashCode() {
		return hash;
	}

	@Override
	public String toString() {
		return getName()+" "+getSatisfier().toString();
	}

}
