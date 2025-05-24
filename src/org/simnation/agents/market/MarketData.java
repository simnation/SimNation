/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable and used JSimpleSim as technical
 * backbone for concurrent discrete event simulation. This software is published as open source and licensed under GNU
 * GPLv3. Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simnation.agents.market;

import org.simnation.common.Statistics;

/**
 * Class for providing general market information.
 * <p>
 * All public methods may be subject to concurrent read in a multi-threaded environment.
 * They can be used by various agents to conclude their decisions.
 * <p>
 * {@code update} and {@code reset} should only be used by a market agent itself after market clearing.
 *
 */
public final class MarketData {
	
	private final Statistics price=new Statistics();
	private final Statistics volume=new Statistics();
	
	
	public double getPrice() { return price.getAVG(); }
	
	public double getLastPrice() { return price.getLastValue(); }
	
	public double getPriceSTD() { return price.getSTD(); }
	
	public double getPriceVAR() { return price.getVAR(); }
	
	
	public double getVolume() { return volume.getAVG(); }
	
	public double getLastVolume() { return volume.getLastValue(); }
	
	public double getVolumeSTD() { return volume.getSTD(); }
	
	public double getVolumeVAR() { return volume.getVAR(); }
	
	
	public double getTurnover() { return getPrice()*getVolume(); }
	
	/* part of restricted view */
	
	void update(double p, double v) { 
		price.update(p);
		volume.update(v); 
	}
	
	void setValues(PriceVolumeDataPoint pvdp) {
		price.reset(pvdp.getPriceAVG(),pvdp.getPriceVAR());
		volume.reset(pvdp.getVolumeAVG(),pvdp.getVolumeVAR());
	}

}
