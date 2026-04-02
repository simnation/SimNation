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
package org.simnation.environment;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.util.FastMath;
import org.simnation.common.Batch;
import org.simnation.environment.technology.Machine;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
 * {@link Need} is a <b>consumable</b>. Thus, the value chain resembles a
 * production network with resources as <i>source</i> and consumables as
 * <i>sink</i> of the network.
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

	/**
	 * Represents a precursor as part of the value chain.
	 * <p>
	 * Every good may have one or more precursors for its production. Precursors are
	 * goods coupled with a weight. This parameter can be used to characterize the
	 * production function further.<br>
	 * {@code Link} is encapsulated in this class and should not accessed from outside.
	 *
	 * @see Good.ProductionFunction
	 *
	 */
	@Entity
	static class Link {

		@Id
		@GeneratedValue(strategy=GenerationType.SEQUENCE)
		private int index;

		@ManyToOne(optional=false)
		@JoinColumn(name="source_fk")
		private Good source;

		@ManyToOne(optional=false)
		@JoinColumn(name="target_fk")
		private Good target;
		private double alpha;

		public Link() {}

		public Link(Good s, Good t, double a) {
			source=s;
			target=t;
			alpha=a;
		}

		public int getIndex() { return index; }

		public void setIndex(int index) { this.index=index; }

		public Good getSource() { return source; }

		public void setSource(Good source) { this.source=source; }

		public Good getTarget() { return target; }

		public void setTarget(Good target) { this.target=target; }

		public double getAlpha() { return alpha; }

		public void setAlpha(double alpha) { this.alpha=alpha; }

	}

	public enum ProductionFunction {

		PERFECT_SUBSTITUTION {
			/**
			 * The perfect substitution production function is based on the formula<br>
			 * <i>output=input[0]*alpha[0]+...+input[n]*alpha[n]</i><br>
			 * and is used if input factors can be substituted freely for each other
			 */
			@Override
			public double calculateOutput(Good good, Map<Good, Batch> input) {
				double result=0;
				for (Link link : good.precursors) {
					final Batch batch=input.get(link.getSource());
					if (batch!=null) result+=batch.getQuantity()*link.getAlpha();
				}
				return result;
			}
		},

		LEONTIEF {
			/**
			 * The Leontief production function is based on the formula<br>
			 * <i>output=min(input[0]*alpha[0],...,input[n-1]*alpha[n-1])</i><br>
			 * and is used in the case of complete inelasticity of input factors.
			 */
			@Override
			public double calculateOutput(Good good, Map<Good, Batch> input) {
				double result=0;
				for (Link link : good.precursors) {
					Batch batch=input.get(link.getSource());
					if (batch!=null) result=FastMath.min(result,batch.getQuantity()*link.getAlpha());
				}
				return result;
			}
		},

		COBB_DOUGLAS {
			/**
			 * The Cobb-Douglas production function is based on the formula<br>
			 * <i>output=input[0]^alpha[0]*...*input[n-1]^alpha[n-1]</i><br>
			 * and is used if input factors can be substituted for each other, but not at a
			 * constant rate
			 */
			@Override
			public double calculateOutput(Good good, Map<Good, Batch> input) {
				double result=1.0d;
				for (Link link : good.precursors) {
					final Batch batch=input.get(link.getSource());
					if (batch!=null) result*=FastMath.pow(batch.getQuantity(),link.getAlpha());
				}
				return result;
			}
		},

		GEOMETRIC_MEAN {
			/**
			 * The geometric mean is a special case of a Cobb-Douglas function where all
			 * exponents are equal and sum up to 1.
			 * <i>output=sqrt(input[0]*...*input[n-1],n)</i><br>
			 * This production function is independent from all alpha value.
			 */
			@Override
			public double calculateOutput(Good good, Map<Good, Batch> input) {
				double result=1.0d;
				for (Link link : good.precursors) {
					Batch batch=input.get(link.getSource());
					if (batch!=null) result*=batch.getQuantity();

				}
				return FastMath.pow(result,1.0d/good.getPrecursorCount());
			}
		};

		/**
		 * Implements various production functions to calculate output of the
		 * transformation process
		 * 
		 * @param good  the output good to produce
		 * @param input batches of input products
		 * @return amount of output
		 */
		public abstract double calculateOutput(Good good, Map<Good, Batch> input);

	}

	public enum ResourceType {
		INFINITE, LIMITED, POPULATION;
	}

	@Id
	private int index; // primary key
	private String name;
	private String unit;
	private boolean service; // is it a service?

	@OneToMany(mappedBy="target", cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<Link> precursors=new HashSet<>();

	@OneToMany(mappedBy="source", cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<Link> successors=new HashSet<>();

	@Enumerated(EnumType.STRING)
	private ProductionFunction pf; // contains all technical specifications of this good

//	@Convert(converter = org.simnation.persistence.JPATimeConverter.class)
//	private Time depreciationTime = Time.ZERO; // depreciation or service availability time

	// raw material
	// private RESOURCE_TYPE resourceType;
	// private long stock;
	// private double gamma;

	@Transient
	private int hash=0; // caching the hash value

	public void addPrecursor(Good good, double value) {
		if (good==null) throw new NullPointerException("Precursor to add may not be null.");
		if (getPrecursorCount()>=Limits.MAX_PRECURSORS) 
			throw new IndexOutOfBoundsException("Good "+name+" has too many precursors.");
		Link link=getLink(good);
		if (link==null) // new precursor good
		{
			link=new Link(good,this,value);
			this.precursors.add(link);
			good.successors.add(link);
		}
		else link.setAlpha(value); // precursor is already there, just update alpha value
	}

	public void deletePrecursor(Good good) {
		final Link link=getLink(good);
		if (link==null) return;
		link.getSource().successors.remove(link);
		precursors.remove(link);
	}

	private Link getLink(Good precursor) {
		for (Link link : precursors)
			if (link.getSource().equals(precursor)) return link;
		return null;
	}

	public Set<Good> getPrecursors() {
		final Set<Good> result=new HashSet<>(getPrecursorCount());
		for (Link link : precursors)
			result.add(link.getSource());
		return result;
	}

	public Set<Good> getSuccessors() {
		final Set<Good> result=new HashSet<>(getSuccessorCount());
		for (Link link : successors)
			result.add(link.getTarget());
		return result;
	}

	// unlinks good from its connections to others good in chain
	public void unlink() {
		for (Link link : precursors)
			link.getSource().successors.remove(link);
		for (Link link : successors)
			link.getTarget().precursors.remove(link);
		precursors.clear();
		successors.clear();
	}

	public int getPrecursorCount() { return precursors.size(); }

	public int getSuccessorCount() { return successors.size(); }

	public boolean hasPrecursors() { return precursors.isEmpty(); }

	public boolean isManufactured() { return hasPrecursors(); }

	public boolean isResource() { return !hasPrecursors(); }

	public boolean isService() { return service; }

// getters and setters
	
	public int getIndex() { return index; }

	public void setIndex(int index) { this.index=index; }

	public ProductionFunction getProductionFunction() { return pf; }

	public String getName() { return name; }

	// public Time getDepreciationTime() { return depreciationTime; }

	public String getUnit() { return unit; }

	// public void setDepreciationTime(Time depreciationTime) {
	// this.depreciationTime = depreciationTime; }

	public void setName(String value) { name=value; }

	public void setProductionFunction(ProductionFunction value) { pf=value; }

	public void setService(boolean value) { service=value; }

	public void setUnit(String value) { unit=value; }

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

	@Override
	public int hashCode() {
		if (hash==0) hash=name.hashCode()+37;
		return hash;
	}

	@Override
	public String toString() { return name; }

	@Override
	public boolean equals(Object that) {
		if (this==that) return true;
		if (that==null || !(that instanceof Good)) return false;
		return name.equals(((Good) that).name);
	}

}
