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

import org.simnation.context.technology.Good;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;

/**
 * A need is the basic driver of the household's motivation system. The aim of a
 * {@link Household} is to satisfy its needs the best way possible. This class
 * defines all attributes of a need itself.
 * <p>
 * <u>Theoretical background</u><br>
 * A need is the very basic reason for a household to consume. According to the
 * concept of Maslow (pyramid of needs) and Alderfer (ERG-Theory), needs form a
 * hierarchy. In this hierarchy, needs are organized in categories of the same
 * urgency level. In the simnation model there are four ugrnency levels.
 * <p>
 * Navigating the need hierarchy works as follows: If the desire of a need
 * reaches the <i>activation level</i>, the household starts to look for a
 * product that satisfices this need. By consumption the desire can be decreased
 * down to the <i>saturation level</i>. Otherwise, if the need cannot be
 * satisfied, the desire increases even further, stepping up the willingness to
 * pay. If this does not lead to satisficing the need, the desire finally
 * reaches the <i>regression level</i>. In this case the agent stops to pursue
 * the need and resigns.
 * <p>
 * As a result of a regression, the ERG level drops and the household focuses on
 * satisficing all needs of the next lower level. If the level of the
 * unfulfilled need is existential, the agent dies and is eliminated from the
 * model.Households will try to persue the needs of the next higher ERG level,
 * if the needs of the current level are completely satisfied.
 * <p>
 * <u>Future features</u><br>
 * <ul>
 * <li>Let the desired <i>quality</i> of products increase with the households
 * satisfaction level (as a result, the demanded quality level rises with rising
 * income).</li>
 * <li>Calculate an overall contentment of a household from statisfaction level
 * and quality. This in turn may lead to model political dynamics within the
 * agent population.</li>
 * </ul>
 * 
 * @see Household
 * @see Good
 */

@Entity
public class Need {

	/**
	 * Needs have to be assigned to an urgency level:
	 * <ul>
	 * <li>EXISTENTIAL: without satisficing <u>all</u> existential needs, an
	 * individual dies eventually
	 * <li>BASIC: basic needs are not existential but crucial to be pursued after
	 * existence needs have been met
	 * <li>COMFORTABLE: needs that add to the overall content level of an individual
	 * <li>SELF_ACTUALIZATION: surplus needs addressing an individual's social status
	 * </ul>
	 * Needs are organized as a hierarchy where a new level can only be reached if
	 * <u>all</u> needs of the lower hierarchy level are satisfied (according to the
	 * theories by Maslow/Alderfer).
	 */
	public enum URGENCY {	EXISTENTIAL, BASIC, COMFORTABLE, SELF_ACTUALIZATION; }
	
	public enum TYPE {	FIXED, LINEAR; }
	
	@Id
	private String name;

	/** link to good that can satisfy this need */
	@OneToOne
	@JoinColumn(name="GOOD_FK")
	private Good satisfier=null;

	/** the household's buy-ahead period */
	private int activationDays; // equals the need's planning period

	/**
	 * when does the household enter the resignation process? Must be longer than
	 * activation time!
	 */
	private int regressionDays; // days after which the agent enters regression
	// each day one try!

	/** consumption (in good units) PER DAY and PER HOUSEHOLD MEMBER */
	private int dailyConsumptionAdult;

	private int dailyConsumptionChild;

	/** urgency level of this need */
	private URGENCY urgency;

	/** the need event assigned to this need - this is a (1:1) relation */
	@Transient
	private Household.EVENT event;

	/** caching the hash code */
	@Transient
	private int hash=0;
	
	/** is consumption fixed or linear per day? */
	private TYPE type;

	public int getIndex() { return event.ordinal(); }

	void setEvent(Household.EVENT value) { event=value; }

	public Household.EVENT getEvent() { return event; }

	public int getActivationDays() { return activationDays; }

	public int getDailyConsumptionAdult() { return dailyConsumptionAdult; }

	public int getDailyConsumptionChild() { return dailyConsumptionChild; }

	public int getFrustrationDays() { return regressionDays; }

	public String getName() { return name; }

	public Good getSatisfier() { return satisfier; }

	public URGENCY getUrgency() { return urgency; }
	
	public TYPE getType() { return type; }

	public void setType(TYPE value) { type = value; 	}
	
	public void setActivationDays(int value) { activationDays=value; }

	public void setDailyConsumptionAdult(int value) { dailyConsumptionAdult=value; }

	public void setDailyConsumptionChild(int value) { dailyConsumptionChild=value; }

	public void setFrustrationDays(int value) { regressionDays=value; }

	public void setName(String value) { name=value; }

	public void setSatisfier(Good value) { satisfier=value; }

	public void setUrgency(URGENCY value) { urgency=value; }

	@Override
	public boolean equals(Object that) {
		if (this==that) return true;
		if (that==null || !(that instanceof Need)) return false;
		return name.equals(((Need) that).name);
	}

	@Override
	public int hashCode() {
		if (hash==0) hash=name.hashCode() + 42;
		return hash;
	}

	@Override
	public String toString() { return getName() + " " + getSatisfier().toString(); }

}
