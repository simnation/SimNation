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
package org.simnation.agents.market;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.simnation.agents.business.Demand;
import org.simnation.agents.business.Money;
import org.simnation.agents.business.Supply;
import org.simnation.common.Batch;
import org.simnation.context.technology.Good;

/**
 * Test the market clearing mechanism
 */
public class TestMarketClearing {

	class TestMarket extends Market<Good> {

		public TestMarket(Set<Good> segments) { super(segments, null); }

		private final MarketStrategy<Good> ms = new SimpleDoubleAuctionStrategy<>();
		private final List<Demand<Good>> dl = new ArrayList<>();
		private final List<Supply<Good>> sl = new ArrayList<>();

		@Override
		long trade(Demand<Good> demand, Supply<Good> supply, long amount, double price) {
			long cost = Math.round(price * amount);
			long quantity = amount;
			if (cost > demand.getMoney().getValue()) { // insufficient funds
				quantity = (long) Math.floor(demand.getMoney().getValue() / price); // round off
				cost = Math.round(price * quantity);
			}
			log("\t market price: $" + Double.toString(price) + ", demand: " + amount + ",affordable: " + quantity
					+ ", cost: $" + cost);
			Batch batch = ((Batch) supply.getItem()).split(quantity);
			batch.setValue(cost); // set to actual trading value --> the price is what others pay for it.
			if (demand.getItem() == null)
				demand.setItem(batch);
			else
				((Batch) demand.getItem()).merge(batch);
			supply.getMoney().merge(demand.getMoney().split(cost));
			return quantity;
		}

		void doMarketClearing() {
			final PriceVolumeDataPoint pvd = ms.doMarketClearing(this, dl, sl);
			if (pvd != null)
				log("\t Market cleared. Units: " + pvd.volume() + ", price: " + Double.toString(pvd.price())
						+ " turnover: " + Double.toString(pvd.price() * pvd.volume()));
			dl.clear();
			sl.clear();
		}

		void addDemand(Demand<Good> demand) { dl.add(demand); }

		void addSupply(Supply<Good> supply) { sl.add(supply); }

	}

	private final Good good = new Good();
	private final Set<Good> goods = new HashSet<>();
	private final TestMarket market;

	TestMarketClearing() {
		good.setName("Gold");
		good.setUnit("kg");
		good.setService(false);
		goods.add(good);
		market = new TestMarket(goods);
	}

	void test() {
		populateSupplyList();
		populateDemandList();
		market.doMarketClearing();
	}

	private void populateDemandList() { 
		for (int i=0; i<5; i++) {
			Money money=new Money(3000);
			int amount=100;
			double price=50+10*i;
			Demand<Good> demand=new Demand<>(new int[0],good,amount,price,0,money);
			market.addDemand(demand);
		}
	}

	private void populateSupplyList() { 
		for (int i=0; i<5; i++) {
			int quantity=100;
			double price=48+10*i;
			Batch batch=new Batch(good,quantity,(long) (quantity*price),0);
			Supply<Good> supply=new Supply<>(new int[0],batch,price);
			market.addSupply(supply);
		}
		
		
	}

	public static void main(String[] args) {
		TestMarketClearing tmc = new TestMarketClearing();
		tmc.test();

	}

}
