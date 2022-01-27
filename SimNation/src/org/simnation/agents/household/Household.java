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
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.simnation.agents.AbstractBasicAgent;
import org.simnation.agents.business.Demand;
import org.simnation.agents.common.Batch;
import org.simnation.agents.household.NeedDefinition.URGENCY;
import org.simnation.agents.market.GoodsMarketB2C;
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
	
	public Household(HouseholdDBS dbs) {
		super(new HouseholdState(Model.getInstance().getNeedSet()));
		dbs.convertToState(getState());
		strategy=new HouseholdStrategy(this);
		// init Stock and enqueue all need events
		// Time.ZERO may be substituted by deterministic RNG
		// saving initial saturation values in database may be omitted by RNG
		// a need not queued here will never be called!
		for (NeedDefinition nd : Model.getInstance().getNeedSet()) {
			getState().setSaturation(nd,0);
			enqueueEvent(nd.getEvent(),Time.ZERO);
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
			strategy.planBudget();
			enqueueEvent(EVENT.planBudget,time.add(Time.months(1)));
			break;
		default: // error: event type not known - this should never happen!
			throw new UnhandledEventType(event,this);
		}
	}

	/**
	 * @param i
	 */
	private void sendDemand(Good good, long amount) {
	//	Good good=Model.getInstance().getNeedSet().iterator().next().getSatisfier();
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
		final NeedDefinition nd=getNeedDefinition(event);
		final long dailyConsumption=getDailyConsumption(nd);
		final Batch stock=getState().getStock(nd);
		
		long saturation=getState().getSaturation(nd)+stock.consume();
		if (saturation>=0) {
			saturation-=nd.getActivationDays()*dailyConsumption;
			getState().setSaturation(nd,(int) saturation);
			enqueueEvent(nd.getEvent(),time.add(nd.getActivationDays()*Time.TICKS_PER_DAY));
		} else { // saturation is negative 
			saturation-=dailyConsumption;
			getState().setSaturation(nd,(int) saturation);
			enqueueEvent(nd.getEvent(),time.add(Time.TICKS_PER_DAY));
			if ((-saturation/dailyConsumption)>=nd.getFrustrationDays()) startRegression(nd);
		}
		// issue order here, skip on regression
	}

	/**
	 * @param nd
	 */
	private void startRegression(NeedDefinition nd) { // TODO Auto-generated method stub
		log("Regression called.");
		/*
		 * existential: death
		 * basic: regression to existential
		 * luxury: regression to basic
		 * 
		 * new variable to indicate household's level
		 */
	 }

	@Override
	protected void handleMessage(RoutedMessage msg) {
		if (msg.getContent().getClass()==Demand.class) {
			final Demand<Good> demand=msg.getContent();
			final Batch batch=(Batch) demand.getItem();
			getState().addToStock(batch.getType(),batch); // add delivery to stock
			getState().getMoney().merge(demand.getMoney()); // take back change money
			demand.setItem(null); 							// item used, prevent memory leak
		} else throw new UnhandledMessageType(msg,this);
	}

	private HouseholdStrategy getStrategy() { return strategy; }
	
	URGENCY getUrgency() { return URGENCY.values()[getState().getUrgencyLevel()]; }
	
	boolean isNeedDisabled(NeedDefinition nd) { return getState().getBudget(nd.getIndex())==NEED_DISABLED; }
	
	void disableNeed(NeedDefinition nd) { getState().setBudget(nd,NEED_DISABLED); }
	
	GoodsMarketB2C getGoodsMarket() { return ((Domain) getParent()).getGoodsMarket(); }
	
	long getDailyConsumption(NeedDefinition nd) {
		return nd.getDailyConsumptionAdult()*getState().getAdults()
				+nd.getDailyConsumptionChild()*getState().getChildren();
	}
	
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

	private static final int NEED_DISABLED=Integer.MIN_VALUE;
	public static final int MAX_NEEDS=EVENT.activation_event_limit.ordinal();

	/* map each urgency level to its corresponding subset of need definitions */
	private static final EnumMap<URGENCY, Set<NeedDefinition>> mapUrgency2NeedDefinition=new EnumMap<>(URGENCY.class);

	/* map activation event to need definition */
	private static final Map<EVENT, NeedDefinition> mapEvent2NeedDefinition=new EnumMap<>(EVENT.class);
	
	/* map satisficing good to corresponding need activation event */
	private static final Map<Good, EVENT> mapSatisfier2Event=new IdentityHashMap<>();

	public static void initNeedMap(Collection<NeedDefinition> needSet) {
		int index=0; // init mappings
		mapUrgency2NeedDefinition.clear();
		mapEvent2NeedDefinition.clear();
		mapSatisfier2Event.clear();
		for (URGENCY urgency : URGENCY.values()) mapUrgency2NeedDefinition.put(urgency,new HashSet<NeedDefinition>());
		for (NeedDefinition nd : needSet) {
			// map need urgency level
			mapUrgency2NeedDefinition.get(nd.getUrgency()).add(nd);
			// bi-map activation event to need definition
			EVENT activation=EVENT.values()[index];
			mapEvent2NeedDefinition.put(activation,nd);
			nd.setEvent(activation);
			// map consumable to activation event
			Good satisfier=nd.getSatisfier();
			if (mapSatisfier2Event.containsKey(satisfier)) throw new UniqueConstraintViolationException(
					satisfier.getName()+" is satfisfier for more than one need!");
			mapSatisfier2Event.put(satisfier,activation);
			index++;
			if (index>=MAX_NEEDS) throw new IndexOutOfBoundsException("Need set contains too many need definitions!");
		}
	}
	
	Set<NeedDefinition> getUrgencySet(URGENCY urgency) { return mapUrgency2NeedDefinition.get(urgency); }

	private static EVENT getEvent(Good satisfier) {
		return mapSatisfier2Event.get(satisfier);
	}

	private static NeedDefinition getNeedDefinition(EVENT event) {
		return mapEvent2NeedDefinition.get(event);
	}

	private static boolean isNeedActivationEvent(EVENT event) {
		return event.ordinal()<MAX_NEEDS;
	}

}
