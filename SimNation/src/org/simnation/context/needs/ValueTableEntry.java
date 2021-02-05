/**
 * 
 */
package org.simnation.context.needs;

import java.io.Serializable;

/**
 * Contains values for consumption rate, activation and frustration level. These are saved in a table with a
 * citizen's age as primary key. The age is capped by the size of the table.
 *
 * NeedValueTable and its entries must be {@link Serializable} to be saved as a BLOB by JDO
 */
// @PersistenceCapable
public class ValueTableEntry implements Serializable {

	private static final long serialVersionUID=-6375357899447798155L;

	private int age;
	private double activation;
	private double frustration;
	private double consumption;

	public int getAge() {
		return age;
	}

	public void setAge(int value) {
		age=value;
	}

	public double getActivation() {
		return activation;
	}

	public void setActivation(double a) {
		activation=a;
	}

	public double getFrustration() {
		return frustration;
	}

	public void setFrustration(double f) {
		frustration=f;
	}

	public double getConsumption() {
		return consumption;
	}

	public void setConsumption(double c) {
		consumption=c;
	}

}