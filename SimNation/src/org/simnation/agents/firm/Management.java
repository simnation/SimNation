/**
 * 
 */
package org.simnation.agents.firm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * This class implements the aspiration adaptation algorithm.
 * <p>
 * Aspiration adaptation is a heuristic algorithm for multi-goal decision.
 * <ul>
 * <li>There is a set of goal variables which have to be <i>maximized</i>.
 * <li>There is also a set of actions that influence the goal variables.
 * <li>The influence can vary and is monitored by this algorithm with an influence scheme.
 * <li>The goal variables aim to achieve certain <i>aspiration levels</i>.
 * <li>The urgency order of the goals may vary depending on their actual aspiration level.
 * The algorithm return the action supporting the most urgent with least negative and most positive effects on goals
 * 
 * </ul>
 * 
 *  (see references for more details).
 * 
 *  
 *  
 *  
 *  
 *  @see <a href="https://www.sciencedirect.com/science/article/abs/pii/S0022249697912050">Aspiration Adaptation Theory</a>
 *  @see <a href="https://www.jstor.org/stable/40748622?seq=1">First publication (as of 1962, German)</a>
 *
 */
public class Management {
	
	public interface GoalVariable {
		// aspiration scale
		public float getMin(); // minimum value of aspiration level
		public float getMax(); // maximum value of aspiration level
		public float getStep(); // step size of aspiration adaptation
		// aspiration limits and priority to compute urgency order
		public float getLimit(); // limit for aspiration adaptation scheme
		// current value of the goal variable to correct influence scheme
		public double getValue(); // current value of the goal variable  
	
		public default boolean isBelowLimit() {
			return getLimit()<getValue();
		}
		
	}
	
	public interface Action {
	
		public abstract void doAction();
	
	}
	
	public enum INFLUENCE { negative, none, positive }

	private class AspirationLevel {
		
		private final float a[];
	
		
		AspirationLevel(float init[]) { a=init; }
		
		AspirationLevel(int size) { this(new float[size]); }
		
		float get(int index) { return a[index]; }
		
		void set(int index, float value) { a[index]=value; }
		
		void retreat(int index) {
			a[index]-=goalList.get(index).getStep();
		}
				
		boolean contains(AspirationLevel other) {
			for (int i=0; i<a.length; i++) if (other.a[i]>a[i]) return false;
			return true;
		}
		
		@Override
		public int hashCode() {
			int hash=Float.hashCode(a[0]);
			for (int i=1; i<a.length;i++) hash+=Float.hashCode(a[i])*71;
			return hash;
		}
		
		@Override
		public boolean equals(Object obj) {
			// checks for same class, null and same array length omitted because of private visibility
			for (int i=0; i<a.length; i++) if (a[i]!=((AspirationLevel) obj).a[i]) return false;
			return true;
		}
	}

	private final List<GoalVariable> goalList;
	private final Map<Action,INFLUENCE[]> influenceScheme=new IdentityHashMap<>();
	private final Map<AspirationLevel,List<Action>> expectedFeasibleSet=new HashMap<>();
	
	private final int[] urgencyOrder;
	private final double[] g1; // vector of last goal variable values, g1=g(t-1)
	private final AspirationLevel aspirationLevel; // current aspiration level 
	
	private Action lastAction;
	private final int dim; // number of goal variables

	
	/**
	 * Constructor of aspiration adaptation strategy
	 * <p>
	 * Note: Goal variables have to be ordered with descending priority. The priority cannot be changed later on. 
	 * 
	 * @param goals list of goal variables 
	 */
	public Management(List<GoalVariable> goals, float[] al) {	
		// init goal and action lists
		goalList=new ArrayList<>(goals); // copy goal variables
		dim=goalList.size();
		urgencyOrder=new int[dim];
		
		if (al!=null) aspirationLevel=new AspirationLevel(al.clone());
		else {
			aspirationLevel=new AspirationLevel(dim);
			for (int index=0; index<dim; index++)
				aspirationLevel.set(index,round(goalList.get(index).getValue(),goalList.get(index).getStep())); // initial aspiration level
		}
		
		g1=new double[dim]; 
		final INFLUENCE[] noInfluence=new INFLUENCE[dim];
		
		// initial values of goal variables as g(t-1)=g(t)
		for (int index=0; index<dim; index++) {
			g1[index]=goalList.get(index).getValue(); // init values of goal variables
			noInfluence[index]=INFLUENCE.none; // create neutral "no action" inflúence
		}
		
		// add a neutral "no action" element with zero influence to the action set
		lastAction=addAction(new Action() { public void doAction() {}},noInfluence);
	}
	
	/**
	 * Constructor of aspiration adaptation strategy
	 * <p>
	 * Sets the initial aspiration level just below the values of the goal variables
	 * Note: Goal variables have to be ordered with descending priority. The priority cannot be changed later on. 
	 * 
	 * @param goals list of goal variables 
	 */
	public Management(List<GoalVariable> goals) {
		this(goals,null);
	}
	
	public Action addAction(Action action,INFLUENCE influence[]) {
		if (influence.length!=dim) return null;
		influenceScheme.put(action,influence);
		return action;
	}
		
 
	/**
	 * Generates a permutation of goal indices sorted by descending urgency.
	 * <p>
	 * The new urgency order contains the indexes of the goal variable sorted with descending urgency, the last element
	 * indicating the retreat variable
	 * 
	 * @param a an adaptation level
	 */
	private void updateUrgencyOrder() {
		int index=0;
		// goals with adaptation levels below the limit are more urgent than the ones above
		for (int i=0; i<dim; i++) {
			if (aspirationLevel.get(i)<=goalList.get(i).getLimit()) urgencyOrder[index++]=i;
		}
		for (int i=0; i<dim; i++) {
			if (aspirationLevel.get(i)>goalList.get(i).getLimit()) urgencyOrder[index++]=i;
		}
	}

	
	public Action decideAction() {
		updateInfluenceScheme();		// correct influence scheme
		constructExpectedFeasibleSet();	// construct set of feasible aspiration levels
		adjustAspirationLevel();		// adjust aspiration level to be contained in the comprehensive hull
		maximizeAspirationLevel();		// upward aspiration adaptation
		lastAction=selectBestAction(expectedFeasibleSet.get(aspirationLevel));
		return lastAction; 
	}
	
	/**
	 * 
	 */
	private void maximizeAspirationLevel() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 
	 */
	private void adjustAspirationLevel() {
		// TODO Auto-generated method stub
		
	}
	
	
	
	private boolean isAspirationLevelFeasible() {
		for (AspirationLevel entry : expectedFeasibleSet.keySet()) {
			if (!entry.contains(aspirationLevel)) return false;
		}
		return true;
	}

	/**
	 * 
	 */
	private Action selectBestAction(List<Action> actionList) {
		
		int best=Integer.MAX_VALUE;
		for (Action action : actionList) best=Math.min(best,
				countInfluence(influenceScheme.get(action),INFLUENCE.negative));
		for (Iterator<Action> iter=actionList.iterator(); iter.hasNext();) {
		    if (countInfluence(influenceScheme.get(iter.next()),INFLUENCE.negative)>best)
		    	iter.remove();
		}
		
		best=0;
		for (Action action : actionList) best=Math.max(best,
				countInfluence(influenceScheme.get(action),INFLUENCE.positive));
		for (Iterator<Action> iter=actionList.iterator(); iter.hasNext();) {
		    if (countInfluence(influenceScheme.get(iter.next()),INFLUENCE.positive)<best)
		    	iter.remove();
		}
		
		return actionList.get(0);	
	}
	
	private int countInfluence(INFLUENCE[] influence,INFLUENCE value) {
		int counter=0;
		for (INFLUENCE item : influence) {
			if (item==value) counter++;
		}
		return counter;
	}

	
	private void constructExpectedFeasibleSet() {
		final float[] aLow=new float[dim]; // lower aspiration bound
		final float[] aMid=new float[dim]; // mid value
		final float[] aHi=new float[dim];  // higher aspiration bound

		// calculate feasible aspiration levels 
		for (int i=0; i<dim; i++) { 
			final float step=goalList.get(i).getStep();
			final float a=round(g1[i],step);
			if (a<goalList.get(i).getMax()) aHi[i]=a+step;
			else aHi[i]=a;
			if ((a==g1[i])&&(a>goalList.get(i).getMin())) aLow[i]=a-step;
			else aLow[i]=a;
			aMid[i]=a;
		}
		// assign feasible aspirations form influence scheme to actions
		expectedFeasibleSet.clear();
		for (Action action : influenceScheme.keySet()) {
			AspirationLevel fal=new AspirationLevel(dim); // feasible aspiration level
			for (int index=0; index<dim; index++) {
				switch(influenceScheme.get(action)[index]) {
				case negative : fal.set(index,aLow[index]); break;
				case positive : fal.set(index,aHi[index]); break;
				default:		fal.set(index,aMid[index]);
				}
			}
			List<Action> actionList=expectedFeasibleSet.get(fal);
			if (actionList==null) {
				actionList=new LinkedList<>();
				expectedFeasibleSet.put(fal,actionList);
			}
			actionList.add(action);
		}
	}
	
	/**
	 * Updates the influence of the last action in the influence scheme and saves the current values of the goal variables.
	 */
	private void updateInfluenceScheme() {
		final INFLUENCE[] influenceRow=influenceScheme.get(lastAction);
		for (int index=0; index<dim; index++) {
			final double g=goalList.get(index).getValue();
			if (g<g1[index]) influenceRow[index]=INFLUENCE.negative;
			else if (g>g1[index]) influenceRow[index]=INFLUENCE.positive;
			else influenceRow[index]=INFLUENCE.none;
			g1[index]=g; // save for next round
		}
	}
	
	private static float round(double value, float step) {
		return ((float) Math.floor(value/step))*step;
	}
	

}
