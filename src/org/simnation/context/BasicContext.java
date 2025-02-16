package org.simnation.context;

import org.simnation.context.geography.RegionSet;
import org.simnation.context.needs.NeedSet;
import org.simnation.context.technology.GoodSet;
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
	private final GoodSet goods=new GoodSet();
	private final NeedSet needs=new NeedSet();
	
	// private static final ResourceSet resources=new ResourceSet();
	
	public GoodSet getGoods() {
		return goods;
	}

	public NeedSet getNeeds() {
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
