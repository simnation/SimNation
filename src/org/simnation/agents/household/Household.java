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
import org.simnation.agents.household.Need.URGENCY;
import org.simnation.common.Batch;
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
	

	public Household(HouseholdDTO dto) {
		super(new HouseholdState());
		dto.convertDTO2State(getState());	// setup state
		for (Need need : Model.getInstance().getNeeds()) { // setup need level and events
			final double x=dto.getNeedLevel(need.getIndex());
			getState().setNeedLevel(need,(int) (getActivationLevel(need)*x));
			enqueueEvent(need.getEvent(),new Time((long) (x*Time.days(need.getActivationDays()))));
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
	private void sendDemand(Need nd, int amount, double price) {
		final float quality=0;
		final Money money=getState().getMoney().split((long) (amount*price)+1); // round up
		final Demand<Good> demand=new Demand<>(getAddress(),nd.getSatisfier(),amount,price,quality,money);
		sendMessage(new RoutingMessage(getAddress(),((Domain) getParent()).getGoodsMarket().getAddress(),demand));
		log("\t sent demand to market: "+demand.toString());
	}

	private void processNeedActivationEvent(EVENT event, Time time) {
		final Need need=mapEvent2Need(event);
		
		// check for disabled needs
		if (getState().getBudget(need)==0) { // need disabled, try again next month
			enqueueEvent(event,getState().getBudgetPeriodStart().add(BUDGET_PERIOD.getTicks()+1));
			return;
		}
		
		// cycle for active needs
		final int nl=getState().getNeedLevel(need);
		final int al=getActivationLevel(need);

		if (nl==0) { // normal cycle, need fully satisfied
			getState().setNeedLevel(need,al);
			enqueueEvent(need.getEvent(),time.add(Time.days(need.getActivationDays())));
		} 
		else if (nl<getActivationLevel(need)) { // normal cycle, need partially satisfied
			getState().setNeedLevel(need,al);			
			enqueueEvent(need.getEvent(),time.add(((al-nl)*Time.days(need.getActivationDays()))/al));
		} 
		else if (nl<=getFrustrationLevel(need)) { // frustration phase
			getState().increaseNeedLevel(need,getConsumption(need,1));	// increase by consumption of one day
			enqueueEvent(need.getEvent(),time.add(Time.DAY));// try again the next day
		} 
		else { // regression
			// modify urgency level
		} 
		sendDemand(need,getState().getNeedLevel(need),calcPricing(need,time));
	}

	private int getFrustrationLevel(Need need) { return getConsumption(need,need.getActivationDays()+need.getFrustrationDays()); }

	private int getActivationLevel(Need need) { return getConsumption(need,need.getActivationDays()); }

	/**
	 * @param nd
	 */
	private void startRegression(Need nd) { // TODO Auto-generated method stub
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
			if (batch!=null) // reduce need level by consumption
				getState().decreaseNeedLevel(mapConsumable2Need(batch.getType()),(int) batch.consume());
			getState().getMoney().merge(demand.getMoney()); // take back change money
			demand.setItem(null); 							// item used, prevent memory leak
		} 
		else throw new UnhandledMessageType(msg,this);
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
	private double calcPricing(Need nd, Time time) {
		// calc expected price as monthly budget divided by monthly consumption
		final double expectedPrice=(double) getState().getBudget(nd)/getConsumption(nd,Time.DAYS_PER_MONTH);
		// calc urgency factor as remaining consumption divided by consumption per activation period
		final double eUrg=(double) getState().getNeedLevel(nd)/getConsumption(nd,nd.getActivationDays());
		// calc internal security factor as ratio of remaining money vs. remaining time
		final double moneyRatio=(double) getState().getMoney().getValue()/getState().getTotalBudget();
		final double remainingTicks=(getState().getBudgetPeriodStart().getTicks()+Time.TICKS_PER_MONTH-time.getTicks());
		final double timeRatio=remainingTicks/Time.TICKS_PER_MONTH;
		final double eInt=moneyRatio/timeRatio;
		// set external security factor as economic growth forecast
		final double eExt=Model.getInstance().getEconomicGrowth();
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
		getState().setBudgetPeriodStart(time);
		long total=getState().getMoney().getValue();
		getState().setTotalBudget(total);
		for (URGENCY urgency : URGENCY.values()) { // traverse need hierarchy from bottom to top
			for (Need need : getUrgencySet(urgency)) { // traverse all needs of a level
				if (total>0) {
					// calc with local market pricing
					final double price=getDomain().getGoodsMarket().getPrice(need.getSatisfier());
					int budget=(int) (price*getConsumption(need,Time.DAYS_PER_MONTH));
					// fall back: without a valid market price calc with equal budget share for all needs 
					if (price<=0) budget=(int) (getState().getTotalBudget()/Model.getInstance().getNeeds().size());
					if (total<budget) budget=(int) total; // adjust if out of budget 
					total-=budget;
					getState().setBudget(need,budget);
					setUrgency(urgency); // save highest urgency level possible to achieve
				} 
				else { // insufficient funds, disable need for this budget period
					getState().setBudget(need,0);
					getState().setNeedLevel(need,0);
				}
			}
		}
		enqueueEvent(EVENT.planBudget,time.add(BUDGET_PERIOD));
	}

	private URGENCY getUrgency() { return URGENCY.values()[getState().getUrgencyLevel()]; }

	private void setUrgency(URGENCY value) { getState().setUrgencyLevel(value.ordinal()); }
	
	private int getConsumption(Need nd, int days) {
		final int consumption=nd.getDailyConsumptionAdult()*getState().getAdults()
				+nd.getDailyConsumptionChild()*getState().getChildren();
		// if (nd.getType()==TYPE.FIXED) return consumption;
		return consumption*days;
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

	public static final int MAX_NEEDS=EVENT.activation_event_limit.ordinal();

	/* map each urgency level to its corresponding subset of need definitions */
	private static final EnumMap<URGENCY, Set<Need>> mappingUrgency2Need=new EnumMap<>(URGENCY.class);

	/* map activation event to need definition */
	private static final Map<EVENT, Need> mappingEvent2Need=new EnumMap<>(EVENT.class);

	/* map satisficing good to corresponding need activation event */
	private static final Map<Good, Need> mappingConsumable2Need=new IdentityHashMap<>();

	/**
	 * This method has to be called AFTER initializing the model context and BEFORT starting a simulation run.
	 * 
	 * @param needSet the actual set of needs
	 */
	public static void initNeedMap(Collection<Need> needSet) {
		if (needSet.size()>=MAX_NEEDS) throw new IndexOutOfBoundsException("Need set contains too many need definitions!");
		mappingUrgency2Need.clear();
		mappingEvent2Need.clear();
		mappingConsumable2Need.clear();
		for (URGENCY urgency : URGENCY.values()) mappingUrgency2Need.put(urgency,new HashSet<Need>());
		int index=0; // init mappings
		for (Need need : needSet) {
			// map need urgency level
			mappingUrgency2Need.get(need.getUrgency()).add(need);
			// bi-map activation event to need definition
			final EVENT activation=EVENT.values()[index];
			mappingEvent2Need.put(activation,need);
			need.setEvent(activation);
			// map consumable to activation event
			final Good satisfier=need.getSatisfier();
			if (mappingConsumable2Need.containsKey(satisfier)) throw new UniqueConstraintViolationException(
					satisfier.getName()+" is satfisfier for more than one need!");
			mappingConsumable2Need.put(satisfier,need);
			index++;
		}
	}

	static Set<Need> getUrgencySet(URGENCY urgency) { return mappingUrgency2Need.get(urgency); }

	private static Need mapConsumable2Need(Good good) {
		return mappingConsumable2Need.get(good);
	}

	private static Need mapEvent2Need(EVENT event) {
		return mappingEvent2Need.get(event);
	}

	private static boolean isNeedActivationEvent(EVENT event) {
		return event.ordinal()<MAX_NEEDS;
	}

}
