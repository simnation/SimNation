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
package org.simnation.agents.market;

import jakarta.persistence.Embeddable;

/**
 * 
 */
@Embeddable
public class PriceVolumeDataPoint {
	
	private double priceAVG,priceVAR,volumeAVG,volumeVAR;

	public double getPriceAVG() { return priceAVG; }

	public void setPriceAVG(double value) { priceAVG=value; }

	public double getPriceVAR() { return priceVAR; }

	public void setPriceVAR(double value) { priceVAR=value; }

	public double getVolumeAVG() { return volumeAVG; }

	public void setVolumeAVG(double value) { volumeAVG=value; }

	public double getVolumeVAR() { return volumeVAR; }

	public void setVolumeVAR(double value) { volumeVAR=value; }

}
