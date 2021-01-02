package org.simnation.model;

import org.simnation.model.geography.RegionSet;
import org.simnation.model.needs.NeedDefinitionSet;
import org.simnation.model.technology.ValueChain;
import org.simnation.persistence.DBSavable;
import org.simnation.persistence.DataAccessObject;

/**
 * Containing all model data shared by editor and simulation. Specific implementations are derived from this class.
 *
 *
 * @author Rene
 *
 */
public class BasicContext implements DBSavable {

	// private static final Preferences preference=new Preferences();
	private final RegionSet regions=new RegionSet();
	private final ValueChain goods=new ValueChain();
	private final NeedDefinitionSet needs=new NeedDefinitionSet();
	
	// private static final ResourceSet resources=new ResourceSet();
	
	public ValueChain getGoods() {
		return goods;
	}

	public NeedDefinitionSet getNeeds() {
		return needs;
	}

	public RegionSet getRegions() {
		return regions;
	}

	public void save(DataAccessObject dao) throws Exception {
		getRegions().save(dao);
		getGoods().save(dao);
		getNeeds().save(dao);
	}

	public void load(DataAccessObject dao) {
		getRegions().load(dao);
		getGoods().load(dao);
		getNeeds().load(dao);
	}

}
