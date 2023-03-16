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

import org.simnation.agents.household.NeedDefinition;
import org.simnation.context.technology.ProductionTechnology.IProductionFunction;
import org.simnation.model.Limits;
import org.simnation.zzz_old.GoodSet;
import org.simplesim.core.scheduling.Time;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;

/**
 * A good is a node of a value chain and contains all its characteristics,
 * precursors and productionparameters. In Simnation, there are two types of
 * goods: Storable goods and services.
 * <p>
 * <b>Storable goods</b> can be either durable (having a depreciation time,
 * example: machinery, furniture) or non-durable (having a depreciation time of
 * zero, example: food). <b>Services</b> are non-storable, they offer a certain
 * capacity over a given time (example: communication, logistics, medical
 * services). Here, the depreciation time is the period of service availability.
 * <p>
 * Goods are made from precursors (production as transformation process). A good
 * that has no precursors is a <b>resource</b>. A good that satisfies a
 * {@link NeedDefinition} is a <b>consumable</b>. Thus, the value chain
 * resembles a production network with resources as <i>source</i> and
 * consumables as <i>sink</i> of the network.
 * <p>
 * A {@link Machine} is a special good to transform other goods. Thus, a machine
 * to produce other machines introduces a self-replicating element into the
 * value chain.
 * <p>
 * Note:
 * <ul>
 * <li>There is a 1:1 relationship between needs and goods: Every need has to be
 * satisfied by one (and only one) good.
 * <li>Services can be seen as an option to use a given capacity (wholly or
 * partially).
 * <li>A consumable that is also a precursor leads to competing demands
 * (example: corn as food and as a precursor for biofuel)
 * </ul>
 *
 */
@Entity
public class Good {


	public enum ResourceType {
		INFINITE, LIMITED, POPULATION;
	}

	@Id
	private String name; // name as primary key
	private String unit;
	private boolean service; // is it a service?
	@Convert(converter=org.simnation.persistence.JDOTimeConverter.class)
	private Time depreciationTime=Time.ZERO; // depreciation or service availability time

	@OneToMany(fetch=FetchType.EAGER,cascade=CascadeType.ALL)
	@JoinColumn(name="PRECURSOR_FK")
	//@ElementCollection
	private List<Precursor> precursors=new ArrayList<>();
	
	//@Persistent(embeddedElement="true",defaultFetchGroup="true")
	@Transient
	private ProductionTechnology technology; // contains all technical specifications of this good


	// raw material
	// private RESOURCE_TYPE resourceType;
	// private long stock;
	// private double gamma;

	// general --> calculated values, not in scenario description!
	// values serve as initial values to start the simulation
	@Transient
	private double initialOutput; // inital global amount
	@Transient
	private double initialValue; // initial price

	@Transient
	private final List<Good> successorList=new ArrayList<>(); // links to the successors

	@Transient
	int hash=0; // caching the hash value

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

	public double getDailyOutput() { return initialOutput; }

	public double getOutput() { return initialOutput; }

	public ProductionTechnology getTechology() { return technology; }

	public String getName() { return name; }

	public Precursor getPrecursor(Good good) {
		return getPrecursors().get(getPrecursorIndexOf(good));
	}

	public Precursor getPrecursor(int index) {
		return getPrecursors().get(index);
	}

	public List<Precursor> getPrecursors() { return precursors; }

	public List<Good> getPrecursorGoods() {
		final List<Good> result=new ArrayList<>(getPrecursorCount());
		for (final Precursor prec : precursors) result.add(prec.getGood());
		return result;
	}

	public Time getDepreciationTime() { return depreciationTime; }

	int getPrecursorIndexOf(Good good) {
		for (int i=0; i<getPrecursorCount(); i++) if (getPrecursors().get(i).getGood().equals(good)) return i;
		return -1;
	}

	public int getPrecursorCount() { return precursors.size(); }

	public Good getSuccessor(int index) {
		return successorList.get(index);
	}

	public List<Good> getSuccessors() { return Collections.unmodifiableList(successorList); }

	public int getSuccessorsTotal() { return successorList.size(); }

	public String getUnit() { return unit; }

	public double getValue() { return initialValue; }

	public boolean hasPrecursors() {
		return !getPrecursors().isEmpty();
	}

	public boolean isManufactured() { return hasPrecursors(); }

	public boolean isResource() { return !hasPrecursors(); }

	public boolean isService() { return service; }

	void setDailyOutput(double value) { initialOutput=value; }

	public void setDepreciationTime(Time depreciationTime) { this.depreciationTime=depreciationTime; }

	public void setName(String value) { name=value; hash=name.hashCode(); }

	public void setPrecursor(int index, Precursor value) {
		getPrecursors().set(index,value);
	}

	public void setPrecursorAlpha(Good good, double value) {
		setPrecursorAlpha(getPrecursorIndexOf(good),value);
	}

	public void setPrecursorAlpha(int index, double value) {
		getPrecursor(index).setAlpha(value);
	}

	public void setPrecursors(List<Precursor> precursors) { this.precursors=precursors; }

	void setOutputValue(double v) { initialValue=v; }

	public void setProductionTechnology(ProductionTechnology value) { technology=value; }

	public void setService(boolean value) { service=value; }

	public void setUnit(String value) { unit=value; }

	// unties good from its connections to others good in chain
	public void untie() {
		for (final Precursor precursor : getPrecursors()) precursor.getGood().successorList.remove(this);
		for (final Good successor : successorList) successor.deletePrecursor(this);
	}

	@Override
	public int hashCode() {
		return hash;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object that) {
		return this==that;
		/*
		 * if (this==that) return true; if (that==null||!(that instanceof Good)) return
		 * false; return name.equals(((Good) that).name);
		 */
	}

}
