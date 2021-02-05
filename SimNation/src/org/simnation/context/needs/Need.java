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
import javax.jdo.annotations.Convert;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.simnation.persistence.JDOTimeConverter;

import org.simnation.context.technology.Good;
import org.simplesim.core.scheduling.Time;

/**
 * Basic class to define all available needs within the model. Each type is only
 * instantiated ONCE (just like @link Good), so IdentityHashMap and = can be
 * used!
 */
@PersistenceCapable
//@DatastoreIdentity(strategy=IdGeneratorStrategy.IDENTITY)
public class Need {

	/**
	 * Most needs can be satisfied instantly on consumption. However, services are
	 * bought for a period of time.
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
	private String unit;
	@Column(name="GOOD_ID")
	private Good satisfier=null;
	private double consumptionRate; // consumption of satisfier per time unit
	@Persistent(converter=org.simnation.persistence.JDOTimeConverter.class)
	private Time activationTime; // how often is this need to be checked?
	@Persistent(converter=org.simnation.persistence.JDOTimeConverter.class)
	private Time frustrationTime; // after which time the agent resigns from follow up the need?
	private URGENCY urgency=URGENCY.BASIC;
	private INCIDENCE incidence=INCIDENCE.CONTINUOUSLY;
	private DURATION duration=DURATION.INSTANTLY;
	
	
	public Time getActivationTime() {
		return activationTime;
	}

	public double getConsumptionRate() {
		return consumptionRate;
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

	public void setConsumptionRate(double vaue) {
		consumptionRate=vaue;
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
	
	/*add other general need functionality here
	delete current Need class and use stock of HouseholdState instead
	rename NeedDefinition to Need and the Set as well
*/
}
