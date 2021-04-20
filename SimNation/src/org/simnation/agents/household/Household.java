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

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.simnation.agents.AbstractBasicAgent;
import org.simnation.agents.business.Demand;
import org.simnation.agents.common.Batch;
import org.simnation.context.needs.Need;
import org.simnation.context.needs.Need.URGENCY;
import org.simnation.model.Limits;
import org.simplesim.core.messaging.RoutedMessage;
import org.simplesim.core.scheduling.Time;

/**
 * Agent of a household
 *
 */
public final class Household extends AbstractBasicAgent<HouseholdState, Household.EVENT> {

	private final HouseholdStrategy strategy;

	private static final String AGENT_NAME="Household";

	public Household(HouseholdDBS dbs) {
		super(dbs.convertToState());
		strategy=new HouseholdStrategy(this);

		enqueueEvent(EVENT.testEvent,Time.HOUR);

		/*
		 * ToDo (0. Set need events according to stock) 0. Set revolving events 1. get
		 * firm sending pre-defined batches to market 2. get household to send
		 * pre-defined demands 3. evaluate market functionality
		 * 
		 * 
		 */

		/*
		 * for (final NeedDefinition nd : Root.getInstance().getNeedSet().asList()) { /*
		 * // calculate need base of family and save it to the agent's state final Need
		 * need=nd.calculateNeed(family,getState().getTime());
		 * getState().setNeed(nd,need); // set initial events base on the previously
		 * calculated needs
		 * addEvent(getActivationEvent(nd),need.getActivationTime(dbs.getNeedLevel(nd)))
		 * ;
		 * addEvent(getFrustrationEvent(nd),need.getFrustrationTime(dbs.getNeedLevel(nd)
		 * ));
		 * 
		 * }
		 */

	}

	/*
	 * Does event handling of agent
	 */
	@Override
	protected void handleEvent(EVENT event, Time time) {
		if (isActivationEvent(event)) processNeedActivation(getNeedDefinition(event));
		else if (isFrustrationEvent(event)) processNeedFrustration(getNeedDefinition(event));
		else switch (event) { // all other events are handled here...
		case testEvent:
			log(time,"test event");
			enqueueEvent(EVENT.testEvent,time.add(Time.DAY));
			break;
		/*
		 * case START_PERIOD: planBudget();
		 * addEvent(EVENT.START_PERIOD,EVENT.START_PERIOD.period()); break; case
		 * START_DAY: processMessages();
		 * addEvent(EVENT.START_DAY,EVENT.START_DAY.period()); break; case JOB_SEARCH:
		 * jobSearch(); break;
		 */
		default: // error: event type not known - this should never happen!
			throw new UnhandledEventType(event,this);
		}
	}

	/**
	 * @param needDefinition
	 */
	private void processNeedFrustration(Need needDefinition) {
		// TODO Auto-generated method stub

	}

	/**
	 * @param needDefinition
	 */
	private void processNeedActivation(Need needDefinition) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void handleMessage(RoutedMessage msg) {
		if (msg.getContent().getClass()==Demand.class) {
			Batch batch=(Batch) ((Demand<?>) msg.getContent()).getItem().getType();
			getState().getInventory().get(batch.getType()).merge(batch);
		} else throw new UnhandledMessageType(msg,this);
	}

	// calculates budgets for all needs; budget is always >= 0
	/*
	 * private void planBudget() { // Assuming that income was paid the day before,
	 * the overall budget equals the actual cash double
	 * totalBudget=getState().getCash().getValue();
	 * getState().setTotalBudget(totalBudget); // 1.) calc budgets of ALL needs
	 * based on predicted consumption and last period's average price for (final
	 * Need iter : Root.getContext().getNeeds().get()) { // amount equals family's
	 * consumption rate divided by the good's saturation ability final double
	 * x=getState().getNeed(iter).getConsumptionRate()/(iter.getSatisfier().
	 * getSaturation()*iter.getPeriod().getTicks()); // get an educated best guess
	 * of the price at the local market final double p=2.5f; //
	 * Model.getPubInfo().getMarketInfo(getAddress().getRegion(),iter.getSatisfier()
	 * ).getTrendPrice(); // planned budget equals amount by price in relation to
	 * the budget period getState().setBudget(iter,(float)
	 * (Time.BUDGET_PERIOD*x*p));
	 * log("Budget for "+iter.getName()+" is "+getState().getBudget(iter)+" GE"); }
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
	 * 
	 * private void jobSearch() { // putSuppliesToB2CMarket();
	 * 
	 * }
	 * 
	 * // activate a need if deactivated before by setting its activation and
	 * frustration event // if there is already a frustration event in queue,
	 * nothing has to be done! private void activateNeed(Need nd) { if
	 * (isEventQueued(getFrustrationEvent(nd))==false) {
	 * addEvent(getActivationEvent(nd),getState().getNeed(nd).getActivationTime(0));
	 * addEvent(getFrustrationEvent(nd),getState().getNeed(nd).getFrustrationTime(0)
	 * ); } }
	 * 
	 * private void deactivateNeed(Need nd) { dequeueEvent(getActivationEvent(nd));
	 * dequeueEvent(getFrustrationEvent(nd)); }
	 * 
	 * // takes suitable batch from cart if available, else return null private
	 * Batch getBatchFromCart(Need nt) { final ListIterator<Batch>
	 * iter=getState().getCart().listIterator(); while (iter.hasNext()) { final
	 * Batch result=iter.next(); if
	 * (result.getProduct().getGood()==nt.getSatisfier()) { iter.remove(); return
	 * result; } } return null; }
	 * 
	 */

	private HouseholdStrategy getStrategy() { return strategy; }

	@Override
	public String getName() { return AGENT_NAME; }

	/*
	 * static section
	 */
	enum EVENT {
		/*
		 * 21 needs altogether, do not change without changing Limits.MAX_NEEDSET_SIZE
		 */
		// activation events
		activateNeed_00, activateNeed_01, activateNeed_02, activateNeed_03, activateNeed_04, activateNeed_05,
		activateNeed_06, activateNeed_07, activateNeed_08, activateNeed_09, activateNeed_10, activateNeed_11,
		activateNeed_12, activateNeed_13, activateNeed_14, activateNeed_15, activateNeed_16, activateNeed_17,
		activateNeed_18, activateNeed_19, activateNeed_20,
		// frustration events
		frustrateNeed_00, frustrateNeed_01, frustrateNeed_02, frustrateNeed_03, frustrateNeed_04, frustrateNeed_05,
		frustrateNeed_06, frustrateNeed_07, frustrateNeed_08, frustrateNeed_09, frustrateNeed_10, frustrateNeed_11,
		frustrateNeed_12, frustrateNeed_13, frustrateNeed_14, frustrateNeed_15, frustrateNeed_16, frustrateNeed_17,
		frustrateNeed_18, frustrateNeed_19, frustrateNeed_20,
		// other events
		testEvent, startPeriod(), START_DAY(), JOB_SEARCH();

	}

	/* maps each urgency level to its corresponding subset of need definitions */
	private static final EnumMap<URGENCY, List<Need>> urgencyMap=new EnumMap<>(URGENCY.class);

	/* maps each event to its specific need definition */
	private static final Map<EVENT, Need> mapEvent2Need=new EnumMap<>(EVENT.class);
	private static final Map<Need, EVENT> mapNeed2ActivationEvent=new HashMap<>();
	private static final Map<Need, EVENT> mapNeed2FrustrationEvent=new HashMap<>();

	public static void initNeedMap(Collection<Need> needSet) {
		urgencyMap.clear();
		mapEvent2Need.clear();
		mapNeed2ActivationEvent.clear();
		mapNeed2FrustrationEvent.clear();
		for (URGENCY u : URGENCY.values()) urgencyMap.put(u,new ArrayList<Need>());
		int index=0; // init mappings
		for (Need nd : needSet) {
			// map activation events
			final EVENT activation=EVENT.values()[index];
			mapEvent2Need.put(activation,nd);
			mapNeed2ActivationEvent.put(nd,activation);
			// map frustration events
			final EVENT frustration=EVENT.values()[index+Limits.MAX_NEEDSET_SIZE];
			mapEvent2Need.put(frustration,nd);
			mapNeed2FrustrationEvent.put(nd,frustration);
			// map need urgency levels
			urgencyMap.get(nd.getUrgency()).add(nd);
			index++;
		}
	}

	private static EVENT getActivationEvent(Need nd) {
		return mapNeed2ActivationEvent.get(nd);
	}

	private static EVENT getFrustrationEvent(Need nd) {
		return mapNeed2FrustrationEvent.get(nd);
	}

	private static Need getNeedDefinition(EVENT event) {
		return mapEvent2Need.get(event);
	}

	private static boolean isActivationEvent(EVENT event) {
		return event.ordinal()<Limits.MAX_NEEDSET_SIZE;
	}

	private static boolean isFrustrationEvent(EVENT event) {
		return event.ordinal()>=Limits.MAX_NEEDSET_SIZE&&event.ordinal()<2*Limits.MAX_NEEDSET_SIZE;
	}

}
