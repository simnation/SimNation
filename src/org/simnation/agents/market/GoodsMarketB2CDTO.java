package org.simnation.agents.market;

import java.util.Map;

import org.simnation.context.geography.Region;
import org.simnation.context.technology.Good;
import org.simnation.persistence.DataTransferObject;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class GoodsMarketB2CDTO implements DataTransferObject<MarketState<Good>>{
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	private int index;
	
	@OneToOne
	@JoinColumn(name="REGION_FK")
	private Region region; // the market's parent region

    @Embedded
    @ElementCollection
    @CollectionTable(name="PRICE_VOLUME_MAP")
    private Map<Good,PriceVolumeDataPoint> pvMap;


	@Override
	public void convertDTO2State(MarketState<Good> state) { 
		for (Good segment : state.getMarketSegments()) {
			state.setMarketData(segment,pvMap.get(segment));
		}
	 }

	@Override
	public void convertState2DTO(MarketState<Good> state) { 
		for (Good segment : state.getMarketSegments()) {
			final MarketData md=state.getMarketData(segment);
			final PriceVolumeDataPoint pvdp=new PriceVolumeDataPoint();
			pvdp.setPriceAVG(md.getPrice());
			pvdp.setPriceVAR(md.getPriceVAR());
			pvdp.setVolumeAVG(md.getVolume());
			pvdp.setVolumeVAR(md.getVolumeVAR());
			pvMap.put(segment,pvdp);
		}
	 }
	
	public void setPriceVolumeMap(Map<Good,PriceVolumeDataPoint> map) { pvMap=map; }

}
