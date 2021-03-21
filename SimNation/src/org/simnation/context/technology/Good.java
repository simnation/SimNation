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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.simnation.agents.household.HouseholdNeed;
import org.simnation.context.needs.Need;
import org.simnation.context.technology.ProductionTechnology.IProductionFunction;
import org.simnation.model.Limits;

/**
 * A good is a node of a value chain and contains all its characteristics and
 * connected elements.
 *
 * There are various types of goods. First, a good can either be storable or
 * non-storable. Storable goods can either be durable (having a depreciation
 * time) or non-durable. Last, any good can be used to satisfy a {@link HouseholdNeed} or
 * as a {@link Precursor} for the production of other goods.
 *
 * Some types of goods have a special characteristic, modeled as a derived
 * class. A good that...
 * <ul>
 * <li>...is non-storable is a {@link Service} in this implementation.
 * <li>...has no precursors is a {@link Resource}. It is a <i>source</i> of the
 * value chain network.
 * <li>...satisfies a {@link HouseholdNeed} is a consumable (see {@link #isConsumable()}). It is a <i>sink</i>
 * of the value chain network. Note that a consumable that is also a precursor leads to competing demands
 * <li>...produces other goods is a {@link Machine}. Note that machines to
 * produce other machines introduce a self-replicating element into the value
 * chain.
 * </ul>
 *
 */
@PersistenceCapable
//@DatastoreIdentity(strategy=IdGeneratorStrategy.IDENTITY)
//@Inheritance(strategy=InheritanceStrategy.SUBCLASS_TABLE)
public class Good {

	/**
	 * Represents a precursor as part of a value chain.
	 * <p>
	 * Any good may have one or more precursors its production depends on.
	 * Precursors are goods coupled with a parameter (alpha). This parameter is used
	 * to characterize the production function further.
	 * 
	 * @see IProductionFunction
	 * @see ProductionTechnology
	 *
	 */
	@PersistenceCapable(embeddedOnly="true")
	public static class Precursor {

		@Persistent(column="precursor")
		private Good good;
		private double alpha;

		Precursor(Good p, double a) {
			good=p;
			alpha=a;
		}

		public Good getGood() {
			return good;
		}

		public double getAlpha() {
			return alpha;
		}

		public void setGood(Good precursor) {
			good=precursor;
		}

		public void setAlpha(double value) {
			alpha=value;
		}

	}

	public enum ResourceType {
		INFINITE, LIMITED, POPULATION;
	}

	@PrimaryKey
	private String name; // name as primary key
	private String unit; 
	private boolean service; // is it a service?

	// @Embedded
	@NotPersistent
	private ProductionTechnology technology; // contains all technical specifications of this good

	@Persistent(embeddedElement="true")
	//@Embedded
	@Join(column="good")
	private List<Precursor> precursors=new ArrayList<>();

	// raw material
	// private RESOURCE_TYPE resourceType;
	// private long stock;
	// private double gamma;
	
	//@Column(name="satisfier")
	//private Need need;
	
	// general --> calculated values, not in scenario description!
	// values serve as initial values to start the simulation
	@NotPersistent
	private double output; // inital global amount
	@NotPersistent
	private double value; // initial price
	
	@NotPersistent
	private final List<Good> successorList=new ArrayList<>(); // links to the successors


	public void addPrecursor(Good good, double value) {
		assert getPrecursorCount()<Limits.MAX_PRECURSORS;
		if (good!=null) {
			precursors.add(new Precursor(good,value));
			good.addSuccessor(this);
		}
	}

	void addSuccessor(Good succ) {
		successorList.add(succ);
	}

	public void deletePrecursor(Good good) {
		deletePrecursor(getPrecursorIndexOf(good));
	}

	public void deletePrecursor(int index) {
		precursors.get(index).getGood().successorList.remove(this);
		precursors.remove(index);
	}

	public void deleteSuccessor(Good good) {
		successorList.remove(good);
	}

	public void deleteSuccessor(int index) {
		successorList.remove(index);
	}

	/**
	 * Calculates the necessary output of this good to be generated per day
	 *
	 * Goes DOWN the technology tree for each new request, because total amount can
	 * be a sum of various sources, e.g. if the good is a consumable AND a precursor
	 *
	 * @param amount : output to be generated daily
	 *
	 *               void calcOutputPerDay(double amount) { final double[]
	 *               input=getProductionFunction().getTerm().calcInput(precursors,amount);
	 *               for (int index=0; index<getPrecursorsTotal(); index++)
	 *               getPrecursor(index).getGood().calcOutputPerDay(input[index]);
	 *               setDailyOutput(getDailyOutput()+amount); }
	 */

	/**
	 * Estimates initial value of the given (previously generated) amount of good
	 *
	 * ASSUMES that the values of ALL precursors are already known and up to date!
	 * Initially the caller has to iterate through a {@link generateGoodHierarchy},
	 * similar to the saving process of the {@link GoodSet}.
	 *
	 *
	 * void calcOutputValue(double wage,double margin) { double val=0; final
	 * double[]
	 * input=getProductionFunction().getTerm().calcInput(precursors,output); //
	 * first calc cost of all necessary precursors for (int index=0;
	 * index<getPrecursorsTotal(); index++)
	 * val+=input[index]*getPrecursor(index).getGood().value; // value now contains
	 * precursor cost, let's add labor cost and a margin // how many working hours
	 * are needed to produce daily output? final double
	 * labor=(output*manhours.getTicks())/(workers*Time.HOUR); // working time in
	 * hours val+=labor*wage; // add labor cost setOutputValue(val*(1+margin)); //
	 * add margin --> "cost plus" approach }
	 */

	public double getDailyOutput() {
		return output;
	}



	public double getOutput() {
		return output;
	}

	public ProductionTechnology getTechology() {
		return technology;
	}

	public String getName() {
		return name;
	}

	public Precursor getPrecursor(Good good) {
		return getPrecursors().get(getPrecursorIndexOf(good));
	}

	public Precursor getPrecursor(int index) {
		return getPrecursors().get(index);
	}

	public List<Precursor> getPrecursors() {
		return precursors;
	}

	public List<Good> getPrecursorGoods() {
		final List<Good> result=new ArrayList<>(getPrecursorCount());
		for (final Precursor prec : precursors) result.add(prec.getGood());
		return result;
	}

	int getPrecursorIndexOf(Good good) {
		for (int i=0; i<getPrecursorCount(); i++) if (getPrecursors().get(i).getGood().equals(good)) return i;
		return -1;
	}

	public int getPrecursorCount() {
		return precursors.size();
	}

	public Good getSuccessor(int index) {
		return successorList.get(index);
	}

	public List<Good> getSuccessors() {
		return Collections.unmodifiableList(successorList);
	}

	public int getSuccessorsTotal() {
		return successorList.size();
	}

	public String getUnit() {
		return unit;
	}

	public double getValue() {
		return value;
	}

	public boolean hasPrecursors() {
		return !getPrecursors().isEmpty();
	}

	public boolean isManufactured() {
		return hasPrecursors();
	}

	public boolean isResource() {
		return !hasPrecursors();
	}

	void setDailyOutput(double value) {
		output=value;
	}

	public void setName(String value) {
		name=value;
	}

	public void setPrecursor(int index, Precursor value) {
		getPrecursors().set(index,value);
	}

	public void setPrecursorAlpha(Good good, double value) {
		setPrecursorAlpha(getPrecursorIndexOf(good),value);
	}

	public void setPrecursorAlpha(int index, double value) {
		getPrecursor(index).setAlpha(value);
	}
	
	public void setPrecursors(List<Precursor> precursors) {
		this.precursors=precursors;
	}

	void setOutputValue(double v) {
		value=v;
	}

	public void setProductionTechnology(ProductionTechnology value) {
		technology=value;
	}

	public void setUnit(String value) {
		unit=value;
	}

	// unties good from its connections to others good in chain
	public void untie() {
		for (final Precursor precursor : getPrecursors()) precursor.getGood().successorList.remove(this);
		for (final Good successor : successorList) successor.deletePrecursor(this);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		return name+"[has "+getPrecursorCount()+" precursors]";
	}

	@Override
	public boolean equals(Object that) {
		if (this==that) return true;
		if ((that==null)||!(that instanceof Good)) return false;
		return name.equals(((Good) that).name);
	}

	public boolean isService() {
		return service;
	}

	public void setService(boolean value) {
		service = value;
	}

}
