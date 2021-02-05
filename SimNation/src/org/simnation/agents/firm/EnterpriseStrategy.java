package org.simnation.agents.firm;

import org.simnation.agents.AgentStrategy;
import org.simnation.agents.firm.Management.GoalVariable;
import org.simnation.agents.firm.Management.InstrumentVariable;
import org.simplesim.core.scheduling.Time;

public class EnterpriseStrategy implements AgentStrategy {

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

		@Override
		public String getName() {
			return name;
		}

		@Override
		public float getMin() {
			return min;
		}

		@Override
		public float getMax() {
			return max;
		}

		@Override
		public float getStep() {
			return step;
		}

		@Override
		public float getLimit() {
			return limit;
		}

		@Override
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

		@Override
		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	@Override
	public void execute(Time time) {
		// TODO Auto-generated method stub
		
	}

	

}
