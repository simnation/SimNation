/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable and used JSimpleSim as technical
 * backbone for concurrent discrete event simulation. This software is published as open source and licensed under GNU
 * GPLv3. Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simnation.simulation.model;

import org.simnation.model.needs.NeedDefinitionSet;
import org.simnation.model.technology.ValueChain;
import org.simplesim.model.RoutingDomain;

public final class RootDomain extends RoutingDomain {

	private static RootDomain instance=null;
	private final ValueChain vc=new ValueChain();
	private final NeedDefinitionSet nd=new NeedDefinitionSet();
	
	// Singleton
	private RootDomain() {
		super();
		setAsRootDomain();
	}
	
	public static RootDomain getInstance() {
		if (instance==null) instance=new RootDomain();
		return instance;
	}
	
	

	/**
	 * @return the vc
	 */
	public ValueChain getGoodDefinitionSet() {
		return vc;
	}

	/**
	 * @return the nd
	 */
	public NeedDefinitionSet getNeedDefinitionSet() {
		return nd;
	}

}
