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
package org.simnation.simulation.agents.household;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.simplesim.core.messaging.Message;
import org.simplesim.core.scheduling.Time;
import org.simplesim.model.AbstractAgent;
import org.simnation.model.needs.Need;
import org.simnation.model.needs.NeedDefinition;
import org.simnation.model.needs.NeedDefinition.URGENCY;
import org.simnation.model.technology.Batch;
import org.simnation.model.technology.Good;
import org.simnation.simulation.agents.AbstractBasicAgent;
import org.simnation.simulation.business.Demand;
import org.simnation.simulation.business.Money;
import org.simnation.simulation.business.Supply;


/**
 * Agent of a household
 *
 */
public final class Household extends AbstractBasicAgent<HouseholdState,Household.EVENT> {
	
	private final HouseholdStrategy strategy;

	private static final String AGENT_NAME="Household";
	//private static final int NUMBER_OF_NEEDS=Limits.MAX_NEEDSET_SIZE;
	private static final double lambda=0.33f;
	private static final double mu=0.33f;


	public Household(HouseholdDBS dbs) {
		super(new HouseholdState());
		strategy=new HouseholdStrategy(this);
		
		getState().setFamily(dbs.getFamily());
		getState().setCash(new Money(dbs.getCash()));
		
		for (final NeedDefinition nd : Root.getInstance().getNeedSet().asList()) {
		/*	// calculate need base of family and save it to the agent's state
			final Need need=nd.calculateNeed(family,getState().getTime());
			getState().setNeed(nd,need);
			// set initial events base on the previously calculated needs
			addEvent(getActivationEvent(nd),need.getActivationTime(dbs.getNeedLevel(nd)));
			addEvent(getFrustrationEvent(nd),need.getFrustrationTime(dbs.getNeedLevel(nd)));
		*/
		}
		enqueueEvent(EVENT.START_PERIOD,Time.ZERO);
	}
	
	public Household(HouseholdState state) {
		super(state);
		strategy=new HouseholdStrategy(this);
		
	
	}


	protected void executeStrategy(Time time) {
		strategy.execute(time);
	}

	/*
	 * Does event handling of agent
	 */
	protected void handleEvent(EVENT event, Time time) {	
		if (isActivationEvent(event)) processNeedActivation(getNeedDefinition(event));
		else if (isFrustrationEvent(event)) processNeedFrustration(getNeedDefinition(event));
		else switch (event) { // all other events are handled here...
			case ACTIVATE_HOUSEHOLD:
				break;
			case START_PERIOD:
				planBudget();
				addEvent(EVENT.START_PERIOD,EVENT.START_PERIOD.period());
				break;
			case START_DAY:
				processMessages();
				addEvent(EVENT.START_DAY,EVENT.START_DAY.period());
				break;
			case JOB_SEARCH:
				jobSearch();
				break;
			default: // error: event type not known - this should never happen!
				super.handleEvent(event);
		}
	}

	protected void handleMessage(final Message<?> msg) {
		if (msg.getContent().getClass()==Supply.class) {
			final Supply<Good> supply=msg.getContent();
			getState().getCart().add((Batch) supply.getBatch());
		} else super.handleMessage(msg);
	}

	// calculates budgets for all needs; budget is always >= 0
	private void planBudget() {
		// Assuming that income was paid the day before, the overall budget equals the actual cash
		double totalBudget=getState().getCash().getValue();
		getState().setTotalBudget(totalBudget);
		// 1.) calc budgets of ALL needs based on predicted consumption and last period's average price
		for (final NeedDefinition iter : Root.getContext().getNeeds().asList()) {
			// amount equals family's consumption rate divided by the good's saturation ability
			final double x=getState().getNeed(iter).getConsumptionRate()/(iter.getSatisfier().getSaturation()*iter.getPeriod().getTicks());
			// get an educated best guess of the price at the local market
			final double p=2.5f; // Model.getPubInfo().getMarketInfo(getAddress().getRegion(),iter.getSatisfier()).getTrendPrice();
			// planned budget equals amount by price in relation to the budget period
			getState().setBudget(iter,(float) (Time.BUDGET_PERIOD*x*p));
			log("Budget for "+iter.getName()+" is "+getState().getBudget(iter)+" GE");
		}
		// 2.) activate need according to available budget, start with existence level first, then go up the hierarchy
		boolean outOfBudget=false;
		for (final URGENCY urgency : URGENCY.values())
			if (!outOfBudget) // if there is still enough budget left plan budget for each need type within urgency
			// level
			for (final NeedDefinition iter : urgencyMap.get(urgency)) {
				// is there enough budget left for current need?
				if (totalBudget>=getState().getBudget(iter)) {
					totalBudget-=getState().getBudget(iter);
					activateNeed(iter);
				} else { // no: resume planning in current urgency level, then stop
					outOfBudget=true;
					deactivateNeed(iter);
					getState().setBudget(iter,0);
				}
			} // if out of budget before, completely skip needs of lower urgency levels!
			else for (final NeedDefinition iter : urgencyMap.get(urgency)) {
				deactivateNeed(iter);
				getState().setBudget(iter,0);
			}
	}

	// triggered by any need activation event
	// adds and re-queues events
	// starts buying
	private void processNeedActivation(NeedDefinition nd) {
		// has a suitable batch been delivered, yet?
		final Need need=getState().getNeed(nd);
		final Batch batch=getBatchFromCart(nd);
		// decision by case analysis
		if (batch==null) { // no suitable supply found
			sendDemand(nd);
			if (need.calcTriesLeft(getEventTimeReverse(getFrustrationEvent(nd))-getTime())>1)
			// if there are tries left, add the next activation event
				addEvent(getActivationEvent(nd),need.getTimeBetweenTries());
		} else { // oh, there is a suitable good in the cart, so schedule new events based on its saturation ability
			addEvent(getActivationEvent(nd),need.getActivationTime(batch.getSaturationAbility()));
			requeueEvent(getFrustrationEvent(nd),need.getFrustrationTime(batch.getSaturationAbility()));
		}
	}

	// triggered by any need frustration event, puts agent into regression phase and deactivates need
	private void processNeedFrustration(NeedDefinition nd) {
		double saturation=0;
		final Need need=getState().getNeed(nd);
		final Batch batch=getBatchFromCart(nd);
		if (batch!=null) saturation=batch.getSaturationAbility();
		if (saturation<=0) {
			getState().setBudget(nd,0);
			if (nd.getUrgency()==URGENCY.EXISTENTIAL) log("Existential need "+nd.getName()+" frustrated --> household dies!");
		} else { // oh, there is a suitable good in the cart, so schedule new events based on its saturation ability
			addEvent(getActivationEvent(nd),need.getActivationTime(saturation));
			addEvent(getFrustrationEvent(nd),need.getFrustrationTime(saturation));
		}
	}

	// send demand to regional market
	// calculates needType corresponding good, maximal affordable price and minimal required quality
	// needs ohne ausreichendes Budget (=Nullbudget) einbeziehen oder Rechenleistung sparen?
	// idee: personality
	private void sendDemand(NeedDefinition nt) {
		final int amount=calcAmount(nt);
		final float maxPrice=calcMaxPrice(nt);
		final float minQuality=getMinQualtity(nt);
		final int validTime=getTime()+Time.TICKS_PER_DAY;
		final Demand<Good> demand=new Demand<>(getAddress(),nt.getSatisfier(),validTime,amount,maxPrice,minQuality,getState().getCash());
		sendMessage(new Message<Demand<Good>>(getAddress(),getB2CMarketAddr(),demand));
	}

	private float calcMaxPrice(final NeedDefinition nd) {
		final Need need=getState().getNeed(nd);
		final int deltaT=getEventTimeReverse(getFrustrationEvent(nd))-getTime();
		final int remainingPeriod=Time.BUDGET_PERIOD-getTime()%Time.BUDGET_PERIOD;
		final double internalExp=getState().getPersonality(); // internal const
		final double externalExp=Root.getPubInfo().getFutureExpectation(); // external "const"
		final double personalExp=getState().getCash().getValue()/getState().getTotalBudget()*(Time.BUDGET_PERIOD/remainingPeriod);
		final double overallExp=lambda*personalExp+mu*externalExp+(1-lambda-mu)*internalExp;
		// max price is based on previously planned budget share
		final double priceExp=getState().getBudget(nd)*nd.getSatisfier().getSaturation()/(Time.BUDGET_PERIOD*need.getConsumptionRate());
		return (float) (priceExp*overallExp*need.calcUrgency(deltaT));
	}

	private float getMinQualtity(NeedDefinition nt) {
		// 0: reiner Entscheid �ber Marktpreise
		return 0;
	}

	private int calcAmount(NeedDefinition nd) {
		final Need need=getState().getNeed(nd);
		final double amount=need.calcAmount(getEventTimeReverse(getFrustrationEvent(nd))-getTime());
		return (int) Math.round(amount/nd.getSatisfier().getSaturation());
	}

	private void jobSearch() {
		// putSuppliesToB2CMarket();

	}

	// activate a need if deactivated before by setting its activation and frustration event
	// if there is already a frustration event in queue, nothing has to be done!
	private void activateNeed(NeedDefinition nd) {
		if (isEventQueued(getFrustrationEvent(nd))==false) {
			addEvent(getActivationEvent(nd),getState().getNeed(nd).getActivationTime(0));
			addEvent(getFrustrationEvent(nd),getState().getNeed(nd).getFrustrationTime(0));
		}
	}

	private void deactivateNeed(NeedDefinition nd) {
		dequeueEvent(getActivationEvent(nd));
		dequeueEvent(getFrustrationEvent(nd));
	}

	// takes suitable batch from cart if available, else return null
	private Batch getBatchFromCart(NeedDefinition nt) {
		final ListIterator<Batch> iter=getState().getCart().listIterator();
		while (iter.hasNext()) {
			final Batch result=iter.next();
			if (result.getProduct().getGood()==nt.getSatisfier()) {
				iter.remove();
				return result;
			}
		}
		return null;
	}
	
	public String getName() {
		return AGENT_NAME;
	}

	
	/* 
	 * static section
	 */	
	enum EVENT {
		NEED_ACTIVATION_00("Need_00 activated"),
		NEED_ACTIVATION_01("Need_01 activated"),
		NEED_ACTIVATION_02("Need_02 activated"),
		NEED_ACTIVATION_03("Need_03 activated"),
		NEED_ACTIVATION_04("Need_04 activated"),
		NEED_ACTIVATION_05("Need_05 activated"),
		NEED_ACTIVATION_06("Need_06 activated"),
		NEED_ACTIVATION_07("Need_07 activated"),
		NEED_ACTIVATION_08("Need_08 activated"),
		NEED_ACTIVATION_09("Need_09 activated"),
		NEED_ACTIVATION_10("Need_10 activated"),
		NEED_ACTIVATION_11("Need_11 activated"),
		NEED_ACTIVATION_12("Need_12 activated"),
		NEED_ACTIVATION_13("Need_13 activated"),
		NEED_ACTIVATION_14("Need_14 activated"),
		NEED_ACTIVATION_15("Need_15 activated"),
		NEED_ACTIVATION_16("Need_16 activated"),
		NEED_ACTIVATION_17("Need_17 activated"),
		NEED_ACTIVATION_18("Need_18 activated"),
		NEED_ACTIVATION_19("Need_19 activated"),
		NEED_ACTIVATION_20("Need_20 activated"),
		/* 21 needs altogether, do not change without changing Limits.MAX_NEEDSET_SIZE */
		START_PERIOD("New budget period started"),
		START_DAY("New day started"),
		JOB_SEARCH("Job search started");

		private final String name;

		EVENT(String n) { name=n; }

		public String getName() { return name; }
	}

	
	/* maps each urgency level to its corresponding subset of need definitions */
	private static final EnumMap<URGENCY,List<NeedDefinition>> urgencyMap=new EnumMap<>(URGENCY.class);

	/* maps each event to its specific need definition */
	private static final Map<EVENT,NeedDefinition> mapEvent2Need=new EnumMap<>(EVENT.class);
	private static final Map<NeedDefinition,EVENT> mapNeed2Event=new HashMap<>();
	
	public static void initNeedMap(Collection<NeedDefinition> needSet) {
		for (URGENCY u : URGENCY.values())
			urgencyMap.put(u,new ArrayList<NeedDefinition>());
		// init mapping EVENT-->NeedDefinition and URGENCY-->NeedDefinition
		int index=0;
		for (NeedDefinition nd : needSet) {
			final EVENT activation=EVENT.values()[index];
			mapEvent2Need.put(activation,nd);
			mapNeed2Event.put(nd,activation);
			urgencyMap.get(nd.getUrgency()).add(nd);
			index++;
		}
	}

	private static EVENT getActivationEvent(NeedDefinition nd) {
		return mapNeed2Event.get(nd); 
	}

	private static NeedDefinition getNeedDefinition(EVENT event) {
		return mapEvent2Need.get(event);
	}
	
}
