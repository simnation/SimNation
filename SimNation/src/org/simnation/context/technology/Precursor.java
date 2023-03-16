/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable 
 * and used JSimpleSim as technical backbone for concurrent discrete event simulation.
 * 
 * This software is published as open source and licensed under GNU GPLv3.
 * 
 * Contributors:
 * 	- Rene Kuhlemann - development and initial implementation
 * 
 */
package org.simnation.context.technology;

import org.hibernate.annotations.Parent;
import org.simnation.context.technology.Good;
import org.simnation.context.technology.ProductionTechnology.IProductionFunction;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;

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
@Entity
public class Precursor {

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	private int index;

	@ManyToOne
	@JoinColumn(name="GOOD_FK")
	private Good good;
	private double alpha;

	public Precursor() { }
	
	public Precursor(Good p, double a) {
		good=p;
		alpha=a;
	}

	public Good getGood() { return good; }

	public double getAlpha() { return alpha; }

	public void setGood(Good value) { good=value; }

	public void setAlpha(double value) { alpha=value; }

	public int getIndex() { return index; }

	public void setIndex(int index) { this.index=index; }


}
