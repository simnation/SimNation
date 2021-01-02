package org.simnation.simulation.agents.enterprise;

import org.simnation.core.AgentType;
import org.simnation.persistence.EnterpriseDBS;
import org.simnation.model.technology.Batch;
import org.simnation.model.technology.Good;
import org.simnation.simulation.agents.AbstractBasicAgent;
import org.simnation.simulation.agents.enterprise.Accounting.ACCOUNT;
import org.simnation.simulation.agents.enterprise.Management.GoalVariable;
import org.simnation.simulation.agents.enterprise.Management.InstrumentVariable;
import org.simnation.simulation.agents.enterprise.common.Warehouse;
import org.simnation.simulation.business.Money;

public abstract class Enterprise<S extends EnterpriseState> extends AbstractBasicAgent<S> {

	private static final long serialVersionUID=-3494700033588007022L;

	protected enum EnterpriseGoals implements GoalVariable {
		G1("rentability",-1.0f,Float.POSITIVE_INFINITY,0.01f,0.06f),
		G2("equity ratio",0.0f,1.0f,0.1f,0.30f),
		G3("market share",0.0f,1.0f,0.05f,0.70f);

		private String name;
		private float min,max,step,limit;

		EnterpriseGoals(String name,float min,float max,float step,float limit) {
			this.name=name;
			this.min=min;
			this.max=max;
			this.step=step;
			this.limit=limit;
		}

		public String getName() {
			return name;
		}

		public float getMin() {
			return min;
		}

		public float getMax() {
			return max;
		}

		public float getStep() {
			return step;
		}

		public float getLimit() {
			return limit;
		}

		public int getPriority() {
			return ordinal();
		}

		@Override
		public String toString() {
			return name;
		}
	}

	protected enum EnterpriseInstruments implements InstrumentVariable {
		X1("price"),
		X2("quality"),
		X3("marketing"),
		X4("workload");

		private String name;

		private EnterpriseInstruments(String name) {
			this.name=name;
		}

		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	public Enterprise(EnterpriseDBS dbs,AgentType agentType,int region,int id) throws Exception {
		super(agentType,region,id);
		initCash(dbs.getCash());
	}

	public void initCash(int cash) {
		getState().setCash(new Money(cash));
		getAccounting().initAccount(ACCOUNT.CASH,cash);
	}

	public void initInventory(Good good,int amount,int price,float quality) {
		final Batch batch=new Batch(good.getStandardProduct(),amount,price,quality);
		getWarehouse().putOnStock(batch);
		getAccounting().initAccount(ACCOUNT.INVENTORIES,batch.getTotalValue());
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
