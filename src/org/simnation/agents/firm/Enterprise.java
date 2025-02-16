package org.simnation.agents.firm;

import org.simnation.agents.AbstractBasicAgent;
import org.simnation.agents.business.Money;
import org.simnation.agents.common.Batch;
import org.simnation.agents.firm.Accounting.ACCOUNT;
import org.simnation.agents.firm.Management.GoalVariable;
import org.simnation.agents.firm.Management.InstrumentVariable;
import org.simnation.agents.firm.common.Warehouse;
import org.simnation.context.technology.Good;

public abstract class Enterprise<S extends EnterpriseState,E extends Enum<E>> extends AbstractBasicAgent<S,E> {


	public Enterprise(S state) {
		super(state);
		initCash(dbs.getMoney());
	}

	public void initCash(int cash) {
		getState().setCash(new Money(cash));
		getAccounting().initAccount(ACCOUNT.CASH,cash);
	}

	public void initInventory(Good good,int amount,int price,float quality) {
		final Batch batch=new Batch(good.getStandardProduct(),amount,price,quality);
		getWarehouse().putOnStock(batch);
		getAccounting().initAccount(ACCOUNT.INVENTORIES,batch.getValue());
	}

	public Warehouse getWarehouse() {
		return getState().getWarehouse();
	}

	public Accounting getAccounting() {
		return getState().getAccounting();
	}

	public S getState() {
		return super.getState();
	}

}
