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

import org.apache.commons.math3.util.FastMath;
import org.simnation.agents.AbstractBasicAgent;
import org.simnation.agents.business.Demand;
import org.simnation.agents.business.Money;
import org.simnation.agents.common.Batch;
import org.simnation.agents.household.NeedDefinition.URGENCY;
import org.simnation.agents.market.MarketStatistics;
import org.simnation.context.technology.Good;
import org.simnation.model.Domain;
import org.simnation.model.Model;
import org.simplesim.core.messaging.RoutingMessage;
import org.simplesim.core.scheduling.Time;

/**
 * Agent of a household
 *
 */
public final class Household extends AbstractBasicAgent<HouseholdState, Household.EVENT> {

	private static final Time BUDGET_OFFSET=new Time(3); // be the third agent to start
	private static final Time BUDGET_PERIOD=Time.MONTH;	 // monthly budget planning
	
	
	public Household(HouseholdDBS dbs) {
		super(new HouseholdState(Model.getInstance().getNeedSet()));
		dbs.convertToState(getState());
		// init Stock and enqueue all need events
		// Time.ZERO may be substituted by deterministic RNG
		// saving initial saturation values in database may be omitted by RNG
		// a need not queued here will never be called!
		for (NeedDefinition nd : Model.getInstance().getNeedSet()) {
			getState().setSaturation(nd,0);
			enqueueEvent(nd.getEvent(),new Time(Time.hours(6)));
		}
		enqueueEvent(EVENT.planBudget,BUDGET_OFFSET);
	}

	/*
	 * Does event handling of agent
	 */
	@Override
	protected void handleEvent(EVENT event, Time time) {
		if (isNeedActivationEvent(event)) processNeedActivationEvent(event,time);
		else switch (event) { // all other events are handled here...
		case planBudget: planBudget(time); break;
		case applyForJob:
			break;
		default: // error: event type not known - this should never happen!
			throw new UnhandledEventType(event,this);
		}
	}

	/**
	 * @param i
	 */
	private void sendDemand(NeedDefinition nd, int amount, double price) {
		final float quality=getState().getStock(nd).getQuality();
		final Money money=getState().getMoney().split((long) (amount*price)+1); // round up
		final Demand<Good> demand=new Demand<>(getAddress(),nd.getSatisfier(),amount,price,quality,money);
		sendMessage(new RoutingMessage(getAddress(),((Domain) getParent()).getGoodsMarket().getAddress(),demand));
		log("\t send demand to market: "+demand.toString());
	}

	/**
	 * @param needDefinition
	 */
	private void processNeedActivationEvent(EVENT event, Time time) {
		final NeedDefinition nd=getNeedDefinition(event);
		final long dailyConsumption=getDailyConsumption(nd);
		log("\t need activation: "+nd.getName());
		int saturation=getState().getSaturation(nd)+(int) getState().getStock(nd).consume();
		if (saturation>=0) {
			saturation-=nd.getActivationDays()*dailyConsumption;
			getState().setSaturation(nd,saturation);
			enqueueEvent(nd.getEvent(),time.add(Time.days(nd.getActivationDays())));
		} else { // saturation is negative 
			saturation-=dailyConsumption;
			getState().setSaturation(nd,saturation);
			enqueueEvent(nd.getEvent(),time.add(Time.DAY));
		}
		if (-saturation/dailyConsumption>=nd.getFrustrationDays()) startRegression(nd);
		else sendDemand(nd,-saturation,calcPricing(nd,time));
	}

	/**
	 * @param nd
	 */
	private void startRegression(NeedDefinition nd) { // TODO Auto-generated method stub
		log("\t Regression called.");
		/*
		 * existential: death basic: regression to existential luxury: regression to
		 * basic
		 * 
		 * new variable to indicate household's level
		 */
	}

	@Override
	protected void handleMessage(RoutingMessage msg) {
		if (msg.getContent().getClass()==Demand.class) {
			final Demand<Good> demand=msg.getContent();
			final Batch batch=(Batch) demand.getItem();
			if (batch!=null) log("got "+batch.getQuantity()+" U of "+batch.getType().getName());
			if (batch!=null) getState().addToStock(batch.getType(),batch); // add delivery to stock
			getState().getMoney().merge(demand.getMoney()); // take back change money
			demand.setItem(null); 							// item used, prevent memory leak
		} else throw new UnhandledMessageType(msg,this);
	}

	/**
	 * Calculates the current demand price taking into account following influences:
	 * - the need's current urgency (as relative stock depletion)
	 * - remaining time and money of the budget period (as relative cash depletion)
	 * - internal security factor (i.e. household's readiness to assume risk)
	 * - external security factor (i.e. national economy forecast)
	 * 
	 * @return the current demand price
	 */
	private double calcPricing(NeedDefinition nd, Time time) {
		final double dailyConsumption=getDailyConsumption(nd);
		// calc expected price as monthly budget divided by monthly consumption
		final double expectedPrice=getState().getBudget(nd)/(dailyConsumption*Time.DAYS_PER_MONTH);
		// calc urgency factor as remaining consumption divided by consumption per activation period
		final double consumption=dailyConsumption*nd.getActivationDays();
		final double eUrg=(consumption-getState().getSaturation(nd))/consumption;
		// calc internal security factor as ratio of remaining money vs. remaining time
		final double moneyRatio=((double) getState().getMoney().getValue())/getState().getTotalBudget();
		final double remainingTicks=(getState().getBudgetPeriodStart().getTicks()+Time.TICKS_PER_MONTH-time.getTicks());
		final double timeRatio=remainingTicks/Time.TICKS_PER_MONTH;
		final double eInt=moneyRatio/timeRatio;
		// set external security factor as economic growth forecast
		final double eExt=1;
		// set personal security factor to an individual constant representing the agent's personality trait
		final double ePers=getState().getExtraversion(); // [0.5;1.5]
		// calc modifying factor as geometric mean of the four factors above
		final double modifier=FastMath.pow(eUrg*eInt*eExt*ePers,0.25); //x^0.25=(x^0.5)^0.5 
		return expectedPrice*modifier; // price multiplied with percental change modifiers
	}

	/**
	 * Plans budget for a one month period
	 */
	private void planBudget(Time time) {
		log("\t plan budget");
		getState().setBudgetPeriodStart(time);
		long total=getState().getMoney().getValue();
		getState().setTotalBudget(total);
		for (URGENCY urgency : URGENCY.values()) { // traverse need hierarchy from bottom to top
			for (NeedDefinition nd : getUrgencySet(urgency)) { // traverse all needs of a level
				if (total>0) {
					int budget;
					// calc with local market pricing
					final double price=getDomain().getGoodsMarket().getLastPrice(nd.getSatisfier());
					log("Price="+price+" for "+nd.getSatisfier().getName());
					if (price>0) budget=(int) (price*getMonthlyConsumption(nd));
					// fall back: without a market price calc with equal budgets for all needs 
					else budget=(int) (getState().getTotalBudget()/Model.getInstance().getNeedSet().size());
					if (total<budget) budget=(int) total; // adjust if out of budget 
					total-=budget;
					getState().setBudget(nd,budget);
				} else getState().setBudget(nd,0);
			}
		}
		enqueueEvent(EVENT.planBudget,time.add(BUDGET_PERIOD));
	}

	private URGENCY getUrgency() { return URGENCY.values()[getState().getUrgencyLevel()]; }

	private boolean isNeedDisabled(NeedDefinition nd) { return getState().getBudget(nd)==NEED_DISABLED; }

	private void disableNeed(NeedDefinition nd) { getState().setBudget(nd,NEED_DISABLED); }

	private int getDailyConsumption(NeedDefinition nd) {
		return nd.getDailyConsumptionAdult()*getState().getAdults()
				+nd.getDailyConsumptionChild()*getState().getChildren();
	}
	
	private int getMonthlyConsumption(NeedDefinition nd) {
		return getDailyConsumption(nd)*Time.DAYS_PER_MONTH;
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
		planBudget, applyForJob;

	}

	private static final int NEED_DISABLED=Integer.MIN_VALUE;
	public static final int MAX_NEEDS=EVENT.activation_event_limit.ordinal();

	/* map each urgency level to its corresponding subset of need definitions */
	private static final EnumMap<URGENCY, Set<NeedDefinition>> mapUrgency2NeedDefinition=new EnumMap<>(URGENCY.class);

	/* map activation event to need definition */
	private static final Map<EVENT, NeedDefinition> mapEvent2NeedDefinition=new EnumMap<>(EVENT.class);

	/* map satisficing good to corresponding need activation event */
	private static final Map<Good, EVENT> mapSatisfier2Event=new IdentityHashMap<>();

	/**
	 * This method has to be called AFTER initializing the model context and BEFORT starting a simulation run.
	 * 
	 * @param needSet the actual set of needs
	 */
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
			final EVENT activation=EVENT.values()[index];
			mapEvent2NeedDefinition.put(activation,nd);
			nd.setEvent(activation);
			// map consumable to activation event
			final Good satisfier=nd.getSatisfier();
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
