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

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import org.simnation.agents.AbstractBasicAgent;
import org.simnation.agents.business.Demand;
import org.simnation.agents.common.Batch;
import org.simnation.context.needs.NeedDefinition;
import org.simnation.context.needs.NeedDefinition.URGENCY;
import org.simnation.context.technology.Good;
import org.simnation.model.Domain;
import org.simnation.model.Model;
import org.simplesim.core.messaging.RoutedMessage;
import org.simplesim.core.scheduling.Time;

/**
 * Agent of a household
 *
 */
public final class Household extends AbstractBasicAgent<HouseholdState, Household.EVENT> {

	private final HouseholdStrategy strategy;
	private final Map<EVENT, NeedState> mapEvent2NeedState=new EnumMap<>(EVENT.class);

	public Household(HouseholdDBS dbs) {
		super(dbs.convertToState());
		strategy=new HouseholdStrategy(this);
		for (NeedDefinition nd : Model.getInstance().getNeedSet()) {
			if (nd.isInstantNeed()) mapEvent2NeedState.put(getEvent(nd),new InstantNeed());
			else mapEvent2NeedState.put(getEvent(nd),new PeriodicalNeed());
		}
	}

	/*
	 * Does event handling of agent
	 */
	@Override
	protected void handleEvent(EVENT event, Time time) {
		if (isNeedActivationEvent(event)) processNeedActivationEvent(event,time);

		/*
		 * on activation event: - issue market demand - use time reserve - if there is
		 * no reserve, set frustration event on supply - recalculate new time reserve on
		 * frustration event: - check for new time reserve - calculate new price limit -
		 * trigger regression
		 * 
		 */
		else switch (event) { // all other events are handled here...
		case planBudget:
			log("\t plan budget");
			enqueueEvent(EVENT.planBudget,time.add(Time.months(1)));
			break;
		default: // error: event type not known - this should never happen!
			throw new UnhandledEventType(event,this);
		}
	}

	/**
	 * @param i
	 */
	private void sendDemand() {
		Good good=Model.getInstance().getNeedSet().iterator().next().getSatisfier();
		int q=(int) (Math.random()*200);
		Demand<Good> demand=new Demand<>(getAddress(),good,q,10.5f,0,getState().getMoney().split(1000));
		RoutedMessage msg=new RoutedMessage(getAddress(),((Domain) getParent()).getGoodsMarket().getAddress(),demand);
		sendMessage(msg);
		log("\t send demand to market: "+demand.toString());

	}

	/**
	 * @param needDefinition
	 */
	private void processNeedActivationEvent(EVENT event, Time time) {
		NeedDefinition nd=getNeedDefinition(event);
		NeedState ns=getNeedState(event);
		if (ns.isActivated(time)) strategy.buySatisfier(nd);
		else if (ns.isFrustrated(time,nd)) log(nd.getName()+" is frustrated.");
		enqueueEvent(event,ns.getActivationTime());
	}

	@Override
	protected void handleMessage(RoutedMessage msg) {
		if (msg.getContent().getClass()==Demand.class) {
			/*
			 * TASK: refill saturation level of corresponding need, take back change money
			 */
			Demand<Good> demand=msg.getContent();
			Batch batch=(Batch) demand.getItem();
			if (batch!=null) {
				log("\t received batch: "+batch.toString()+" and "+demand.getMoney().toString());
				final EVENT key=getEvent(batch.getType());
				NeedDefinition nd=getNeedDefinition(key);
				NeedState need=getNeedState(key);
				need.satisfice(nd,getState(),batch);
			} else log("\t received no supply "+demand.getMoney().toString());
			getState().getMoney().merge(demand.getMoney());
			log("\t total money: "+getState().getMoney().toString());
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
	public String getName() { return "Household"; }

	/*
	 * static section
	 */
	enum EVENT {
		// activation events
		activateNeed_00, activateNeed_01, activateNeed_02, activateNeed_03, activateNeed_04, activateNeed_05,
		activateNeed_06, activateNeed_07, activateNeed_08, activateNeed_09, activateNeed_10, activateNeed_11,

		activation_event_limit, // marker event, do not change position or delete!

		// other events
		planBudget, startPeriod(), START_DAY(), JOB_SEARCH();

	}

	public static final int MAX_NEEDS=EVENT.activation_event_limit.ordinal();

	/* map each urgency level to its corresponding subset of need definitions */
	private static final EnumMap<URGENCY, Set<NeedDefinition>> mapUrgency=new EnumMap<>(URGENCY.class);

	/* bi-map activation event to need definition */
	private static final Map<EVENT, NeedDefinition> mapEvent2NeedDefinition=new EnumMap<>(EVENT.class);
	private static final Map<NeedDefinition, EVENT> mapNeedDefinition2Event=new IdentityHashMap<>();

	/* map satisficing good to corresponding need activation event */
	private static final Map<Good, EVENT> mapSatisfier2Event=new IdentityHashMap<>();

	public static void initNeedMap(Collection<NeedDefinition> needSet) {
		mapUrgency.clear();
		mapEvent2NeedDefinition.clear();
		mapNeedDefinition2Event.clear();
		mapSatisfier2Event.clear();
		for (URGENCY u : URGENCY.values()) mapUrgency.put(u,new HashSet<NeedDefinition>());
		int index=0; // init mappings
		for (NeedDefinition nd : needSet) {
			// map need urgency level
			mapUrgency.get(nd.getUrgency()).add(nd);
			// bi-map activation event to need definition
			EVENT activation=EVENT.values()[index];
			mapEvent2NeedDefinition.put(activation,nd);
			mapNeedDefinition2Event.put(nd,activation);
			// map consumable to activation event
			Good satisfier=nd.getSatisfier();
			if (mapSatisfier2Event.containsKey(satisfier)) throw new UniqueConstraintViolationException(
					satisfier.getName()+" is satfisfier for more than one need!");
			mapSatisfier2Event.put(satisfier,activation);
			index++;
			if (index>=MAX_NEEDS) throw new IndexOutOfBoundsException("Need set contains too many need definitions!");
		}
	}

	private NeedState getNeedState(EVENT event) {
		return mapEvent2NeedState.get(event);

	}

	private static EVENT getEvent(Good satisfier) {
		return mapSatisfier2Event.get(satisfier);
	}

	private static EVENT getEvent(NeedDefinition nd) {
		return mapNeedDefinition2Event.get(nd);
	}

	private static NeedDefinition getNeedDefinition(EVENT event) {
		return mapEvent2NeedDefinition.get(event);
	}

	private static boolean isNeedActivationEvent(EVENT event) {
		return event.ordinal()<MAX_NEEDS;
	}

}
