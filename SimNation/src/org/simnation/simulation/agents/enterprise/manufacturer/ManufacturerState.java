/**
 *
 */
package org.simnation.simulation.agents.enterprise.manufacturer;

import org.simnation.core.Machine;
import org.simnation.simulation.agents.enterprise.EnterpriseState;
import org.simnation.simulation.agents.enterprise.common.Procurement;
import org.simnation.simulation.agents.enterprise.common.Production;
import org.simnation.simulation.agents.enterprise.common.Sales;

/**
 *
 * @author Martin Sanski
 */

public final class ManufacturerState extends EnterpriseState {

	private static final long serialVersionUID=-6238445156521521666L;

	private Machine machine;
	private Procurement procurement;
	private Production production;
	private Sales sales;
	private int tprod;

	public ManufacturerState() {

	}

	public Machine getMachine() {
		return machine;
	}

	public Procurement getProcurement() {
		return procurement;
	}

	public Production getProduction() {
		return production;
	}

	public Sales getSales() {
		return sales;
	}

	public void setMachine(Machine machine) {
		this.machine=machine;
	}

	public void setProcurement(Procurement procurement) {
		this.procurement=procurement;
	}

	public void setProduction(Production production) {
		this.production=production;
	}

	public void setSales(Sales sales) {
		this.sales=sales;
	}

	public void setProductionTime(int tprod) {
		this.tprod=tprod;
	}

	public int getProductionTime() {
		return tprod;
	}

}
