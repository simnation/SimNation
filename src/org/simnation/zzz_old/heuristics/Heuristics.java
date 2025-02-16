package org.simnation.editor.heuristics;

import java.util.List;

import org.simnation.model.technology.AgeDistribution;
import org.simnation.model.technology.Good;

public class Heuristics {

	public void estimateInitialAmount(AgeDistribution ad) {
		for (final Good good : list)
			good.setDailyOutput(0);
		for (final Good good : consumable)
			good.calcOutputPerDay(good.getNeed().estimateConsumptionPerDay(ad));
	}

	public void estimateInitialPrice(double wage,double margin) {
		final List<Good> save_list=generateGoodHierarchy();
		for (final Good good : save_list)
			good.calcOutputValue(wage,margin);
	}
	
	
}
