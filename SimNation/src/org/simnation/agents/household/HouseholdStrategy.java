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
package org.simnation.agents.household;

import org.simnation.agents.AgentStrategy;
import org.simnation.agents.household.NeedDefinition.URGENCY;
import org.simnation.agents.market.MarketStatistics;
import org.simnation.model.Model;
import org.simplesim.core.scheduling.Time;

/**
 * Strategy of a household
 *
 */

public final class HouseholdStrategy implements AgentStrategy {

	private final Household household;
	private Time startBudgetPeriod;

	public HouseholdStrategy(Household parent) {
		household=parent;

	}

	/**
	 * Plans budget for a one month period
	 */
	public void planBudget(Time time) {
		startBudgetPeriod=time;
		long total=getState().getMoney().getValue();
		getState().setTotalBudget(total);
		for (URGENCY urgency : URGENCY.values()) { // traverse need hierarchy from bottom to top
			for (NeedDefinition nd : household.getUrgencySet(urgency)) { // traverse all needs of a level
				if (total>0) {
					int budget;
					final MarketStatistics ms=household.getDomain().getGoodsMarket()
							.getMarketStatistics(nd.getSatisfier());
					if (ms!=null) budget=(int) (ms.getPrice()*household.getDailyConsumption(nd)*Time.DAYS_PER_MONTH);
					else budget=(int) (getState().getTotalBudget()/Model.getInstance().getNeedCount());
					if (total<budget) budget=(int) total; // adjust if out of budget 
					total-=budget;
					getState().setBudget(nd,budget);
				}
				else getState().setBudget(nd,0);
			}
		}
	}

	public float calcPricing(NeedDefinition nd, Time time) {
		// calc expected price as monthly budget divided by monthly consumption 
		final double monthlyConsumption=household.getDailyConsumption(nd)*Time.DAYS_PER_MONTH;
		final double expectedPrice=getState().getBudget(nd)/monthlyConsumption;
		// calc urgency factor as missing consumption divided by consumption per activation period 
		final double consumption=household.getDailyConsumption(nd)*nd.getActivationDays();
		final double eUrg=(consumption-getState().getSaturation(nd))/consumption;
		// calc internal security factor as ratio of remaining money vs. remaining time  
		final double moneyRatio=((double) getState().getMoney().getValue()/getState().getTotalBudget());
		final double timeRatio=(Time.TICKS_PER_MONTH-(time.getTicks()-startBudgetPeriod.getTicks()))/(double) Time.TICKS_PER_MONTH;
		final double eInt=moneyRatio/timeRatio;
		// set external security factor as economic growth forecast
		final double eExt=1d;
		// set personal security factor to an individual constant representing the agent's personality trait
		final double ePers=1d;
		// calc modifying factor as geometric mean of the four factors above
		return (float) (expectedPrice*Math.pow(eUrg*eInt*eExt*ePers,4));
	}

	private HouseholdState getState() { return household.getState(); }

	// calculates budgets for all needs; budget is always >= 0
	/*
	 * // 2.) activate need according to available budget, start with existence
	 * level first, then go up the hierarchy boolean outOfBudget=false; for (final
	 * URGENCY urgency : URGENCY.values()) if (!outOfBudget) // if there is still
	 * enough budget left plan budget for each need type within urgency // level for
	 * (final Need iter : urgencyMap.get(urgency)) { // is there enough budget left
	 * for current need? if (totalBudget>=getState().getBudget(iter)) {
	 * totalBudget-=getState().getBudget(iter); activateNeed(iter); } else { // no:
	 * resume planning in current urgency level, then stop outOfBudget=true;
	 * deactivateNeed(iter); getState().setBudget(iter,0); } } // if out of budget
	 * before, completely skip needs of lower urgency levels! else for (final Need
	 * iter : urgencyMap.get(urgency)) { deactivateNeed(iter);
	 * getState().setBudget(iter,0); } }
	 * 
	 * // triggered by any need activation event // adds and re-queues events //
	 * starts buying private void processNeedActivation(Need nd) { // has a suitable
	 * batch been delivered, yet? final HouseholdNeed need=getState().getNeed(nd);
	 * final Batch batch=getBatchFromCart(nd); // decision by case analysis if
	 * (batch==null) { // no suitable supply found sendDemand(nd); if
	 * (need.calcTriesLeft(getEventTimeReverse(getFrustrationEvent(nd))-getTime())>
	 * 1) // if there are tries left, add the next activation event
	 * addEvent(getActivationEvent(nd),need.getTimeBetweenTries()); } else { // oh,
	 * there is a suitable good in the cart, so schedule new events based on its
	 * saturation ability
	 * addEvent(getActivationEvent(nd),need.getActivationTime(batch.
	 * getSaturationAbility()));
	 * requeueEvent(getFrustrationEvent(nd),need.getFrustrationTime(batch.
	 * getSaturationAbility())); } }
	 * 
	 * // triggered by any need frustration event, puts agent into regression phase
	 * and deactivates need private void processNeedFrustration(Need nd) { double
	 * saturation=0; final HouseholdNeed need=getState().getNeed(nd); final Batch
	 * batch=getBatchFromCart(nd); if (batch!=null)
	 * saturation=batch.getSaturationAbility(); if (saturation<=0) {
	 * getState().setBudget(nd,0); if (nd.getUrgency()==URGENCY.EXISTENTIAL)
	 * log("Existential need "+nd.getName()+" frustrated --> household dies!"); }
	 * else { // oh, there is a suitable good in the cart, so schedule new events
	 * based on its saturation ability
	 * addEvent(getActivationEvent(nd),need.getActivationTime(saturation));
	 * addEvent(getFrustrationEvent(nd),need.getFrustrationTime(saturation)); } }
	 * 
	 * // send demand to regional market // calculates needType corresponding good,
	 * maximal affordable price and minimal required quality // needs ohne
	 * ausreichendes Budget (=Nullbudget) einbeziehen oder Rechenleistung sparen? //
	 * idee: personality private void sendDemand(Need nt) { final int
	 * amount=calcAmount(nt); final float maxPrice=calcMaxPrice(nt); final float
	 * minQuality=getMinQualtity(nt); final int
	 * validTime=getTime()+Time.TICKS_PER_DAY; final Demand<Good> demand=new
	 * Demand<>(getAddress(),nt.getSatisfier(),validTime,amount,maxPrice,minQuality,
	 * getState().getCash()); sendMessage(new
	 * Message<Demand<Good>>(getAddress(),getB2CMarketAddr(),demand)); }
	 * 
	 * private float calcMaxPrice(final Need nd) { final HouseholdNeed
	 * need=getState().getNeed(nd); final int
	 * deltaT=getEventTimeReverse(getFrustrationEvent(nd))-getTime(); final int
	 * remainingPeriod=Time.BUDGET_PERIOD-getTime()%Time.BUDGET_PERIOD; final double
	 * internalExp=getState().getPersonality(); // internal const final double
	 * externalExp=Root.getPubInfo().getFutureExpectation(); // external "const"
	 * final double
	 * personalExp=getState().getCash().getValue()/getState().getTotalBudget()*(Time
	 * .BUDGET_PERIOD/remainingPeriod); final double
	 * overallExp=lambda*personalExp+mu*externalExp+(1-lambda-mu)*internalExp; //
	 * max price is based on previously planned budget share final double
	 * priceExp=getState().getBudget(nd)*nd.getSatisfier().getSaturation()/(Time.
	 * BUDGET_PERIOD*need.getConsumptionRate()); return (float)
	 * (priceExp*overallExp*need.calcUrgency(deltaT)); }
	 * 
	 * private float getMinQualtity(Need nt) { // 0: reiner Entscheid �ber
	 * Marktpreise return 0; }
	 * 
	 * // used for price mark-up private double calcUrgency(Need nd, Time time) { if
	 * (getFrustrationTime()==Time.INFINITY) return 1.0d; final double
	 * activityPeriod=nd.getFrustrationTime()-nd.getActivationTime(); final double
	 * actualPeriod=getFrustrationTime().getTicks()-time.getTicks(); return
	 * (activityPeriod/actualPeriod); }
	 * 
	 * private int calcAmount(Need nd) { final HouseholdNeed
	 * need=getState().getNeed(nd); final double
	 * amount=need.calcAmount(getEventTimeReverse(getFrustrationEvent(nd))-getTime()
	 * ); return (int) Math.round(amount/nd.getSatisfier().getSaturation()); }
	 */

}
