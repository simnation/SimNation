/**
 *
 */
package org.simnation.agents.firm.manufacturer;

import org.simnation.agents.firm.EnterpriseState;
import org.simnation.agents.firm.common.Procurement;
import org.simnation.agents.firm.common.Production;
import org.simnation.agents.firm.common.Sales;
import org.simnation.core.Machine;

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
