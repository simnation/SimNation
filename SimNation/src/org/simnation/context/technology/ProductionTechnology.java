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
package org.simnation.context.technology;

import org.simnation.context.technology.Precursor;
import org.simplesim.core.scheduling.Time;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Convert;

@Entity
@Embeddable
public class ProductionTechnology {

	public interface IProductionFunction {

		/**
		 * Calculates the output of a production function based on the available input.
		 *
		 * @param good  the good to be produced
		 * @param input available amount of input factors, corresponds to
		 *              {@link Good#getPrecursors()}
		 * @return the amount of output being produced
		 * @see Precursor
		 */
		double calcOutput(Good good, int input[]);

	}

	public enum ProductionFunctionType {

		/**
		 * The Leontief production function is based on the formula<br>
		 * <i>output=min(input[0]/alpha[0],...,input[n]/alpha[n])</i><br>
		 * and is used in the case of complete inelasticity of input factors.
		 */
		LEONTIEF("Leontief", (good, input) -> {
			double result=input[0]/good.getPrecursor(0).getAlpha();
			for (int i=1; i<good.getPrecursorCount(); i++)
				result=Math.min(result,input[i]/good.getPrecursor(i).getAlpha());
			return result;
		}),
		/**
		 * The Cobb-Douglas production function is based on the formula<br>
		 * <i>output=input[0]^alpha[0]*...*input[n]^alpha[n]</i><br>
		 * and is used if input factors can be substituted for each other, but not at a
		 * constant rate
		 */
		COBB_DOUGLAS("Cobb-Douglas", (good, input) -> {
			double result=Math.pow(input[0],good.getPrecursor(0).getAlpha());
			for (int i=1; i<good.getPrecursorCount(); i++) result*=Math.pow(input[i],good.getPrecursor(i).getAlpha());
			return result;
		}),
		/**
		 * The perfect substitution production function is based on the formula<br>
		 * <i>output=input[0]*alpha[0]+...+input[n]*alpha[n]</i><br>
		 * and is used if input factors can be substituted freely for each other
		 */
		PERFECT_SUBSTITUTION("Perfect Substitution", (good, input) -> {
			double result=input[0]*good.getPrecursor(0).getAlpha();
			for (int i=1; i<good.getPrecursorCount(); i++) result+=input[i]*good.getPrecursor(i).getAlpha();
			return result;
		});

		private final IProductionFunction pfd;
		private final String name;

		ProductionFunctionType(String name, IProductionFunction pf) {
			this.name=name;
			pfd=pf;
		}

		public IProductionFunction getProductionFunctionDefinition() { return pfd; }

		@Override
		public String toString() {
			return name;
		}

	}

	private Good machine=null; // which machine is used for this technology?
	private int defaultCapacity; // output capacity of good PER UNIT of this good's machine
	@Convert(converter=org.simnation.persistence.JPATimeConverter.class)
	private Time defaultMakespan; // how long does it take to make a unit?
	private double defaultManhours; // how many manhours of labor does one unit need?
	@Convert(converter=org.simnation.persistence.JPAProductionFunctionTypeConverter.class)
	private ProductionFunctionType pft;

	public int getDefaultCapacity() { return defaultCapacity; }

	public void setDefaultCapacity(int value) { defaultCapacity=value; }

	public Time getDefaultMakespan() { return defaultMakespan; }

	public void setDefaultMakespan(Time value) { defaultMakespan=value; }

	public double getDefaultManhours() { return defaultManhours; }

	public void setDefaultManhours(double value) { defaultManhours=value; }

	public ProductionFunctionType getProductionFunction() { return pft; }

	public void setProductionFunction(ProductionFunctionType value) { pft=value; }

	public Good getMachine() { return machine; }

	public void setMachine(Good value) { machine=value; }

	/**
	 * Calculates the the output based on the production function and its alpha
	 * parameters as defined in the precursor set, no additional effects are taken
	 * into account.
	 * 
	 * @param good  the good being produced
	 * @param input the amounts of input factors with their position corresponding
	 *              to the precursor set (first input defines amount of first
	 *              precursor)
	 * @return output of the good after the production process
	 */
	public double calcDefaultOutput(Good good, int[] input) {
		return getProductionFunction().getProductionFunctionDefinition().calcOutput(good,input);
	}

}
