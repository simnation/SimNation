/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable 
 * and used JSimpleSim as technical backbone for concurrent discrete event simulation.
 * 
 * This software is published as open source and licensed under GNU GPLv3.
 * 
 * Contributors:
 * 	- Rene Kuhlemann - development and initial implementation
 * 
 */
package org.simnation.model.population;

public enum SkillDefinition {
	/**
	 * Specifies the available skills within the model.
	 * <p>
	 * Any {@link Citizen} has got a set of skills to be used when working. The skill set
	 * is composed of a basic skill (general education or IQ) and several applied skills (qualifications).
	 * The basic skill can be improved by education, applied skills are acquired in a "learning by doing" 
	 * approach. The basic skill determines how fast any applied skill can be improved. The kind of skill 
	 * being increased depends on the current job of the citizen. Each job should influence at least one 
	 * applied skill.
	 */
    GENERAL, PRODUCTION, SERVICE, FINANCE, ADMINISTRATION, MANAGEMENT;
    
    public static final Integer BASIC_SKILL_LEVEL=0;

    public static int length() { return(values().length); }

}