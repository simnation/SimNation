package org.simnation.old;

// simnation

import java.util.IdentityHashMap;
import java.util.Map;

import org.simnation.core.Address;
import org.simnation.simulation.business.Money;

/**
 * Regional goods market to supply households with consumables
 *
 * @author Rene Kuhlemann
 */

public final class GoodsMarketB2CState<T> extends MarketState<T> {

	private static final long serialVersionUID=-3591446446369750890L;

	private final Map<Address,Money> deposit=new IdentityHashMap<Address,Money>();

	public Map<Address,Money> getDeposit() {
		return deposit;
	}

}
