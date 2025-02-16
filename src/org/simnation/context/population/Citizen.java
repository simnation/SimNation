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
package org.simnation.context.population;

import java.io.Serializable;
import java.util.EnumMap;

import org.simnation.model.Limits;
import org.simplesim.core.scheduling.Time;

/**
 * Represents all basic characteristics of a {@code Citizen} as an atomic
 * element of the simulation
 *
 */
public class Citizen implements Serializable {

	private static final long serialVersionUID = 3669592658113927776L;
	
	
	/**
	 * negative date of birth, equals the age at the begin of the simulation
	 * (simTime=0)
	 */
	private long dob;
	/** skills in various categories */
	private final EnumMap<SkillDefinition, Integer> skills=new EnumMap<>(SkillDefinition.class);
	/** personality factor: 0=introversion <= personality <= extraversion=2 */
	private float personality;

	public Citizen(long age, float pers) {
		dob=-age;
		personality=pers;
		for (final SkillDefinition skill : SkillDefinition.values())
			skills.put(skill,SkillDefinition.BASIC_SKILL_LEVEL);
	}

	public Time getAge(Time now) {
		return new Time(dob+now.getTicks());
	}

	public float getPersonality() {
		return personality;
	}

	public int getSkill(SkillDefinition skill) {
		return skills.get(skill);
	}

	public boolean isAdult(Time now) {
		return ((now.getTicks()-dob)>=Limits.LEGAL_AGE);
	}

	public void learn(SkillDefinition skill, int increment) {
		int value=getSkill(skill)+increment;
		//if (value>Limits.MAX_SKILL) value=Limits.MAX_SKILL;
		skills.put(skill,value);
	}
	
	public void setSkill(SkillDefinition skill, int value) {
		skills.put(skill,value);
	}
	
	public static Citizen generateRandom() {
		long age=Math.round(Math.random()*Limits.MAX_AGE);
		Citizen result=new Citizen(age,(float) Math.random()*Limits.EXTRAVERSION);
		return result;
	}

}
