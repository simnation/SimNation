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
package org.simnation.model.technology;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.jdo.annotations.Convert;
import javax.jdo.annotations.Embedded;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.simnation.model.Limits;
import org.simnation.model.needs.Need;
import org.simnation.model.needs.NeedDefinition;
import org.simnation.model.technology.ProductionTechnology.IProductionFunction;
import org.simplesim.core.scheduling.Time;

/**
 * A good is a node of a value chain and contains all its characteristics and
 * connected elements.
 *
 * There are various types of goods. First, a good can either be storable or
 * non-storable. Storable goods can either be durable (having a depreciation
 * time) or non-durable. Last, any good can be used to satisfy a {@link Need} or
 * as a {@link Precursor} for the production of other goods.
 *
 * Some types of goods have a special characteristic, modeled as a derived
 * class. A good that...
 * <ul>
 * <li>...is non-storable is a {@link Service} in this implementation.
 * <li>...has no precursors is a {@link Resource}. It is a <i>source</i> of the
 * value chain network.
 * <li>...satisfies a {@link Need} is a consumable (see {@link #isConsumable()}). It is a <i>sink</i>
 * of the value chain network. Note that a consumable that is also a precursor leads to competing demands
 * <li>...produces other goods is a {@link Machine}. Note that machines to
 * produce other machines introduce a self-replicating element into the value
 * chain.
 * </ul>
 *
 */
@PersistenceCapable
@Inheritance(strategy=InheritanceStrategy.SUBCLASS_TABLE)
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

		@Persistent(column="precuror")
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

	@PrimaryKey
	private String name; // name as primary key
	private String unit;

	@Embedded
	private ProductionTechnology technology;

	@Persistent(embeddedElement="true")
	@Join(column="good")
	private List<Precursor> precursors;

	// general --> calculated values, not in scenario description!
	// values serve as initial values to start the simulation
	private double output; // inital global amount
	private double value; // initial price

	// raw material
	// private RESOURCE_TYPE resourceType;
	// private long stock;
	// private double gamma;
	
	@Persistent(mappedBy="satisfier")
	private NeedDefinition need;
	@Convert(org.simnation.persistence.JDOTimeConverter.class)
	private Time depreciationTime; // depreciation time, 0 means this is a non-durable good
	@Convert(org.simnation.persistence.JDOTimeConverter.class)
	private Time productionTime; // period of time for production
	private boolean service; // is it a service?

	@NotPersistent
	private final List<Good> successor_list=new ArrayList<>(); // links to the successors

	public enum RESOURCE_TYPE {
		INFINITE, LIMITED, POPULATION;
	}

	public Good(String name) {
		this.name=name;
	}

	public void addPrecursor(Good good, double value) {
		assert getPrecursorCount()<Limits.MAX_PRECURSORS;
		if (good!=null) {
			precursors.add(new Precursor(good,value));
			good.addSuccessor(this);
		}
	}

	void addSuccessor(Good succ) {
		successor_list.add(succ);
	}

	public void deletePrecursor(Good good) {
		deletePrecursor(getPrecursorIndexOf(good));
	}

	public void deletePrecursor(int index) {
		precursors.get(index).getGood().successor_list.remove(this);
		precursors.remove(index);
	}

	public void deleteSuccessor(Good good) {
		successor_list.remove(good);
	}

	public void deleteSuccessor(int index) {
		successor_list.remove(index);
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
	 * similar to the saving process of the {@link ValueChain}.
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

	public Time getDepreciationTime() {
		return depreciationTime;
	}

	public Time getProductionTime() {
		return productionTime;
	}

	public double getOutput() {
		return output;
	}

	public ProductionTechnology getTechology() {
		return technology;
	}
	
	public boolean isConsumable() {
		return need!=null;
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
	
	public NeedDefinition getNeed() {
		return need;
	}

	public void setNeed(NeedDefinition value) {
		need=value;
	}


	public int getPrecursorCount() {
		return precursors.size();
	}

	public Good getSuccessor(int index) {
		return successor_list.get(index);
	}

	public List<Good> getSuccessors() {
		return Collections.unmodifiableList(successor_list);
	}

	public int getSuccessorsTotal() {
		return successor_list.size();
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

	public boolean isDurable() {
		return !getDepreciationTime().equals(Time.ZERO);
	}

	void setDailyOutput(double value) {
		output=value;
	}

	public void setDepreciationTime(Time value) {
		depreciationTime=value;
	}

	public void setProductionTime(Time value) {
		productionTime=value;
	}

	void setName(String value) {
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
		for (final Precursor precursor : getPrecursors()) precursor.getGood().successor_list.remove(this);
		for (final Good successor : successor_list) successor.deletePrecursor(this);
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

	public void setService(boolean service) {
		this.service = service;
	}

}
