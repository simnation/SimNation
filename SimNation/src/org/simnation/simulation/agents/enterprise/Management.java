/**
 * 
 */
package org.simnation.simulation.agents.enterprise;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.simnation.core.Tools;


/**
 * @author rene
 *
 */
public class Management<I extends Enum<I> & Management.InstrumentVariable, G extends Enum<G> & Management.GoalVariable> {
	
	public interface GoalVariable {
		public String getName();
		public float getMin();
		public float getMax();
		public float getStep();
		public float getLimit();
		public int getPriority();		
	}
	
	public interface InstrumentVariable {
		public String getName();
	}
	
	private static final int NO_ACTION=0;
	private final ArrayList<EnumMap<G,Float>> influenceScheme;
	private final ArrayList<EnumMap<I,Float>> actionList;
	private final EnumMap<G,Float> a0;
	private EnumMap<G,Float> g0,g1;
	private int lastAction;
		
	public Management(Class<I> classInstruments,Class<G> classGoals,EnumMap<G,Float> initialGoals) {
		// init goal(t) and goal(t-1)
		g1=new EnumMap<G,Float>(g0=initialGoals);
		// calculate inital aspiration level
		a0=new EnumMap<G,Float>(classGoals);
		for (G iter : initialGoals.keySet()) a0.put(iter,Tools.roundOff(initialGoals.get(iter)/iter.getStep())*iter.getStep());
		// init action list and influence scheme
		influenceScheme=new ArrayList<EnumMap<G,Float>>();
		actionList=new ArrayList<EnumMap<I,Float>>();
		// init action list with a "no changes" action that has no influence on goals
		EnumMap<I,Float> no_action=new EnumMap<I,Float>(classInstruments);  
		EnumMap<G,Float> no_influence= new EnumMap<G,Float>(classGoals);
		for (I iter : no_action.keySet()) no_action.put(iter,0.0f);
		for (G iter : no_influence.keySet()) no_influence.put(iter,0.0f);
		addAction(no_action,no_influence);
		lastAction=NO_ACTION;
	}
		
	public void addAction(EnumMap<I,Float> action,EnumMap<G,Float> influence) {
		actionList.add(action);
		influenceScheme.add(influence);		
	}
	
	public EnumMap<I,Float> decideAction(EnumMap<G,Float> result) {
		correctInfluenceScheme(result);
		constructExpectedFeasibleSet(result);
		return null;	
	}
	
	private void constructExpectedFeasibleSet(EnumMap<G,Float> result) {
		List<G> priority_list=new LinkedList<G>(); // contains urgency order
		List<G> retreat_list=new LinkedList<G>();
		EnumMap<G,Float> a=new EnumMap<G,Float>(result);
		final Map<EnumMap<G,Float>,EnumMap<I,Float>> feasibleSet=new IdentityHashMap<EnumMap<G,Float>,EnumMap<I,Float>>(actionList.size());
		// calculate inital aspiration level
		for (G iter : result.keySet()) a.put(iter,Tools.roundOff(result.get(iter)/iter.getStep())*iter.getStep());
		
		for (G iter : result.keySet()) {
			// add iter to priority list if it is below its critical limit and has not yet reached its maximum value
			// otherwise add iter to retreat but only if it has not yet reached its minimum value
			if (result.get(iter)<=iter.getLimit()) {	
				if (result.get(iter)<iter.getMax()) priority_list.add(iter);
				else retreat_list.add(iter);				
			} else { 
				if (result.get(iter)>=iter.getMin()) retreat_list.add(iter);
				else priority_list.add(iter);
			}  
		} // append not urgent goals to the end of prio list 
		priority_list.addAll(retreat_list); 
		
		
		
	}
	
	private void correctInfluenceScheme(EnumMap<G,Float> result) {
		g0=g1;
		g1=result;
		for (G iter : g1.keySet()) {
			// calc ratio of actual and last value of goal variable
			float q=(g1.get(iter)/g0.get(iter))-1.0f;
			// set influence to ratio of actual relative increase to planned relativ increase (=step width) 
			influenceScheme.get(lastAction).put(iter,q/iter.getStep());
		}
	}
	
}
