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
package org.simnation.context.needs;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.simnation.agents.household.HouseholdNeed;
import org.simnation.context.technology.Good;
import org.simplesim.core.scheduling.Time;

/**
 * Main class to define the need system of the model.
 * <p>
 * A {@code Need} is the basic driver of the household's motivation system. The aim of a {@code Household} is to
 * satisfy its needs the best way possible. This class defines all attributes of a need itself.
 *  <p>
 *  Note: Each needs should be instantiated only ONCE (just like {@code Good}), so identity can be used!
 *  
 *  @see NeedSet
 *  @see Good
 *  @see HouseholdNeed
 */
@PersistenceCapable
public class Need {

	/**
	 * Needs can be satisfied instantly on consumption (e.g. food) or by availability for a period of time (e.g. housing).
	 * A periodical need is satisfied by a good that is a service.
	 */
	public enum DURATION { INSTANTLY, PERIODICALLY; }

	/**
	 * Needs can occur continuously (thirst) or randomly (medical support).
	 */
	public enum INCIDENCE {	CONTINUOUSLY, RANDOMLY;	}

	/**
	 * Needs have to be assigned to an urgency level:
	 * <ul>
	 * <li> EXISTENTIAL: without satisficing <u>all</u> existential needs, an individual dies eventually
	 * <li> BASIC: basic needs are not existential but crucial to be pursued after existence needs have been met
	 * <li> CONVENIENCE: needs that add to the overall content level of an individual
	 * <li> LUXURY: surplus needs addressing an individual's social status
	 * </ul>
	 * Needs are organized as a hierarchy where a new level can only be reached if <u>all</u> needs of the
	 * lower hierarchy level are satisfied (according to the theories by Maslow/Alderfer).
	 */
	public enum URGENCY { EXISTENTIAL, BASIC, CONVENIENCE, LUXURY;	}

	@PrimaryKey
	private String name;
	
	/** unit the consumption is measured with */
	private String unit;
	
	/** link to good that can satisfy this need */
	@Column(name="GOOD_FK")
	private Good satisfier=null;
	
	/** saturation ability per one unit satisfying good */
	private double saturation;  // this should be part of a Good or Product class
	
	/** how often a household pursues this need? */
	@Persistent(converter=org.simnation.persistence.JDOTimeConverter.class)
	private Time activationTime; // how often is this need to be checked?
	
	/** when does the household enters resignation process? Must be longer than activation time! */
	@Persistent(converter=org.simnation.persistence.JDOTimeConverter.class)
	private Time frustrationTime; // after which time the agent resigns from follow up the need?
	
	/** consumption of one household member per activation period */
	private double consumption;
	
	/** urgency level of this need */
	private URGENCY urgency;
	
	/** incidence of this need */
	private INCIDENCE incidence;
	
	/** duration of this need */
	private DURATION duration;
	
	
	public Time getActivationTime() {
		return activationTime;
	}

	public double getConsumption() {
		return consumption;
	}

	public DURATION getDuration() {
		return duration;
	}

	public Time getFrustrationTime() {
		return frustrationTime;
	}

	public INCIDENCE getIncidence() {
		return incidence;
	}

	public String getName() {
		return name;
	}

	public Good getSatisfier() {
		return satisfier;
	}

	public String getUnit() {
		return unit;
	}

	public URGENCY getUrgency() {
		return urgency;
	}

	public void setActivationTime(Time vaue) {
		activationTime=vaue;
	}

	public void setConsumption(double vaue) {
		consumption=vaue;
	}

	public void setDuration(DURATION value) {
		duration=value;
	}

	public void setFrustrationTime(Time value) {
		frustrationTime=value;
	}

	public void setIncidence(INCIDENCE value) {
		incidence=value;
	}

	public void setName(String value) {
		name=value;
	}

	public void setSatisfier(Good value) {
		satisfier=value;
	}

	public void setUnit(String value) {
		unit=value;
	}

	public void setUrgency(URGENCY value) {
		urgency=value;
	}

	@Override
	public boolean equals(Object that) {
		return (this==that);
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	@Override
	public String toString() {
		return getName();
	}

	/**
	 * @return the saturation
	 */
	public double getSaturation() {
		return saturation;
	}

	/**
	 * @param saturation the saturation to set
	 */
	public void setSaturation(double saturation) {
		this.saturation = saturation;
	}
	
	/*add other general need functionality here
	delete current Need class and use stock of HouseholdState instead
	rename NeedDefinition to Need and the Set as well
*/
}
