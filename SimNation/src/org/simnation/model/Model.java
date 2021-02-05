/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable and used JSimpleSim as technical
 * backbone for concurrent discrete event simulation. This software is published as open source and licensed under GNU
 * GPLv3. Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simnation.model;

import org.simnation.context.needs.NeedSet;
import org.simnation.context.technology.GoodSet;
import org.simplesim.model.RoutingDomain;

public final class Model extends RoutingDomain {

	private static Model instance=null;
	private final GoodSet vc=new GoodSet();
	private final NeedSet nd=new NeedSet();
	
	// Singleton
	private Model() {
		super();
		setAsRootDomain();
	}
	
	public static Model getInstance() {
		if (instance==null) instance=new Model();
		return instance;
	}
	
	

	/**
	 * @return the vc
	 */
	public GoodSet getGoodDefinitionSet() {
		return vc;
	}

	/**
	 * @return the nd
	 */
	public NeedSet getNeedDefinitionSet() {
		return nd;
	}

}
