/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 * 
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simnation.zzz_old;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class implements the aspiration adaptation algorithm.
 * <p>
 * Aspiration adaptation is a heuristic algorithm for multi-goal optimization.
 * <ul>
 * <li>There is a given set of goal variables that have to be <u>maximized</u>.
 * <li>There is a given set of actions that influence these goal variables.
 * <li>The algorithm evaluates how an action influences the goal variables by
 * means of an influence scheme.
 * <li>The algorithm aims the goal variables to achieve certain <i>aspiration
 * levels</i>.
 * <li>The urgency of the goals varies according to a given urgency order and
 * depending on their actual aspiration level.
 * </ul>
 * The algorithm returns the action supporting the most urgent goal with the
 * least negative and most positive effects on all goals
 * <p>
 *
 * @see <a href=
 *      "https://www.sciencedirect.com/science/article/abs/pii/S0022249697912050">Aspiration
 *      Adaptation Theory</a>
 * @see <a href="https://www.jstor.org/stable/40748622?seq=1">First publication
 *      (as of 1962, German)</a>
 *
 */
public class AspirationAdaptation {

	/** abstraction layer for the goal variables / objectives */
	public interface GoalVariable {
		// aspiration scale
		float getMin(); // minimum value of aspiration level

		float getMax(); // maximum value of aspiration level

		float getStep(); // step size of aspiration adaptation

		float getLimit(); // when the limit is reached, the goal variable no longer is urgent

		double getValue(); // current value of the goal variable  

		default boolean isUrgent() { return getValue()<getLimit(); }
	}

	/** abstraction layer, has to be implemented by the callers actions */
	public interface Action {
		void doAction();
	}

	/**
	 * the aspiration level maps the continuous goal variables to a discrete scale
	 */
	private class AspirationLevel {

		private final double a[]; // vector

		/* constructors */
		AspirationLevel(int size) { a=new double[size]; }

		/* utility functions */
		double get(int index) { return a[index]; }

		void set(int index, double value) { a[index]=value; }

		void dec(int index) { a[index]-=goal[index].getStep(); }

		void inc(int index) { a[index]+=goal[index].getStep(); }

		/** Does this aspiration level contain the other one? */
		boolean contains(AspirationLevel other) {
			for (int i=0; i<a.length; i++) if (other.a[i]>a[i]) return false;
			return true;
		}

		@Override
		public int hashCode() {
			int hash=Double.hashCode(a[0]);
			for (int i=1; i<a.length; i++) hash=hash*71^Double.hashCode(a[i]);
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			// checks for same class, null and same array length omitted because of private visibility
			for (int i=0; i<a.length; i++) if (a[i]!=((AspirationLevel) obj).a[i]) return false;
			return true;
		}
	}

	private final GoalVariable[] goal; // constant array of goal variables
	
	/** mapping actions to their presumed influence on the goal variables
	 * 	note: deliberately chose float precision to avoid spurious precision
	 * */
	private final Map<Action, float[]> influenceScheme=new IdentityHashMap<>();

	/**
	 * mapping of feasible aspiration levels to the corresponding actions by which
	 * they can be reached
	 */
	private final Map<AspirationLevel, List<Action>> expectedFeasibleSet=new HashMap<>();

	private final int[] urgencyOrder; // permutation vector
	private final double[] g0; // vector of last goal variable values, g0=g(t-1)
//	private final AspirationLevel a0; // (current) aspiration level

	private Action lastAction=null;
	private final int dim; // number of goal variables

	/**
	 * Constructor of aspiration adaptation strategy
	 * <p>
	 * Note: Goal variables have to be ordered with descending priority. The
	 * priority cannot be changed later on.
	 *
	 * @param goals array of goal variables
	 */
	public AspirationAdaptation(GoalVariable goals[]) {
		// init goal and action lists
		goal=goals;
		dim=goals.length; // number of goals determines dimension of other schemes
		urgencyOrder=new int[dim];

		// init goal variable values and aspiration level;
		g0=new double[dim];
	//	a0=new AspirationLevel(dim);
		for (int index=0; index<dim; index++) {
			g0[index]=goal[index].getValue(); // set g(t-1)
		//	a0.set(index,g2a(g0[index],goal[index])); // set initial aspiration level
		}
	}

	/**
	 * Adds a new action with estimated influences to the list of actions.
	 * <p>
	 * As initial influence, use the {#code getStep()} method of the goal variable with an appropriate sign.
	 * The sign indicates the supposed change of the goal variable depending on the action.
	 * <p>
	 * Best educated guessing of the influence is okay, since the influence is adapted later on. Do not fill 
	 * the influence with zeros since this may lead to a dead lock.
	 * 
	 * @param action    the action
	 * @param influence estimated influence of the action on the goal variables
	 * @return the action for further usage, {@code null} if an error occurred
	 */
	public Action addAction(Action action, float influence[]) {
		if (influence.length!=dim) return null;
		influenceScheme.put(action,influence);
		return action;
	}

	/**
	 * Heuristic to find the best action to increase goal variables.
	 * 
	 * @return the best action to improve goals
	 */
	public Action decideAction() {
		updateInfluenceScheme();	// correct influence scheme
		constructFeasibleSet();		// construct set of feasible aspiration levels and set current aspiration level
		adaptAspirationLevel();		// aspiration adaptation process
		lastAction=selectBestAction(expectedFeasibleSet.get(a0));
		return lastAction;
	}

	/**
	 * Finds the best action to realize the given aspiration level.
	 * <p>
	 * An aspiration level might be assigned to several actions. The action list is
	 * narrowed by the following criteria: (1) select the actions with the least
	 * negative influences (2) of this subset, select the action with the most
	 * positive influences
	 *
	 */
	private Action selectBestAction(List<Action> actionList) {
		// list actions with the least negative influences
		int best=Integer.MAX_VALUE;
		for (final Action action : actionList)
			best=Math.min(best,countInfluence(influenceScheme.get(action),INFLUENCE.negative));
		for (final Iterator<Action> iter=actionList.iterator(); iter.hasNext();) {
			if (countInfluence(influenceScheme.get(iter.next()),INFLUENCE.negative)>best) iter.remove();
		}
		// of this subset, select actions with most positive influences
		best=0;
		for (final Action action : actionList)
			best=Math.max(best,countInfluence(influenceScheme.get(action),INFLUENCE.positive));
		for (final Iterator<Action> iter=actionList.iterator(); iter.hasNext();) {
			if (countInfluence(influenceScheme.get(iter.next()),INFLUENCE.positive)<best) iter.remove();
		}
		// return first element, since all remaining elements are tantamount
		return actionList.get(0);
	}

	/**
	 * Maximizes the aspiration level within the boundaries of the feasible set.
	 * <p>
	 * Tries to maximize the aspiration level of the most urgent goal variable. Then
	 * the AL of the second most urgent is maximized and so on. Result should be the
	 * optimal AL that can be reached by the given set of actions.
	 * <p>
	 * In contrast to the idea of Selten, the aspiration adaptation starts near the current
	 * goal and not from the last aspiration level. The implementation of an appropriate 
	 * urgency order was omitted by Selten, so the way of aspiration adaptation remains miraculous...
	 * 
	 */
	private void adaptAspirationLevel() {
		final AspirationLevel a=new AspirationLevel(dim);
		for (int i=0; i<dim; i++) a.set(i,g2a(g0[i],goal[i])); // set initial aspiration level
		if (!isAspirationLevelFeasible(a)) throw new IllegalStateException();
		updateUrgencyOrder(a);
		
		// finde passenden fal
		for (int i=0; i<dim; i++) {
			a.inc(urgencyOrder[i]);
			if (!isAspirationLevelFeasible(a)) 
				// finde bestmöglichen AL nach urgency order
				// starte mit höchster prio, wenn nicht möglich, nächste prio
				// wenn auch nicht möglich, verringere retreat variable und beginne von vorne
				
				// result: finde bestmögliches set of actions 
		}
		
		// if necessary: adapt the aspiration level downward until it is within the feasible set
		while (!isAspirationLevelFeasible()) {
			a0.dec(getRereatVariable());
			updateUrgencyOrder();
		}
		// adapt the aspiration level upward to the best possible position within the feasible set
		while (isAspirationLevelFeasible()) {
			a0.inc(urgencyOrder[index]);
		}
		a0.dec(urgencyOrder[index]);
	}

	/**
	 * Generates a permutation of goal indices according to the current aspiration
	 * level and sorted by descending urgency.
	 * <p>
	 * The new urgency order contains the indices of the goal variable sorted by
	 * descending urgency, the last element indicating the retreat variable.
	 *
	 */
	private void updateUrgencyOrder(AspirationLevel a) {
		int index=0;
		// first pass: below the limit, goals with lower indices are more urgent
		for (int i=0; i<dim; i++) if (a.get(i)<=goal[i].getLimit()) urgencyOrder[index++]=i;
		// second pass: if the goal limit is reached, goals with higher indices are more urgent
		for (int i=dim-1; i>=0; i--) if (a.get(i)>goal[i].getLimit()) urgencyOrder[index++]=i;
	}

	/**
	 * Constructs a set of aspiration levels that can be reached by the available
	 * actions.
	 */
	private void constructFeasibleSet() {
		final double[] aLow=new double[dim]; // lower aspiration bound
		final double[] aMid=new double[dim]; // mid value
		final double[] aHi=new double[dim];  // higher aspiration bound

		// calculate feasible aspiration levels
		// yielding aLow<g0<aHi
		for (int i=0; i<dim; i++) {
			final double a=g2a(g0[i],goal[i]); // calc the next aspiration level lower than the actual goal variable		
			if (a<goal[i].getMax()) aHi[i]=a+goal[i].getStep();  // construct upper bound
			else aHi[i]=a;
			aLow[i]=aMid[i]=a;  // construct lower bound and save aspiration level to aMid
			if (a==g0[i]&&a>goal[i].getMin()) aLow[i]-=goal[i].getStep(); // adjust downward in rare cases of a==g0
		}

		// assign feasible aspiration levels to all actions according to the influence scheme
		expectedFeasibleSet.clear();
		for (final Action action : influenceScheme.keySet()) {
			final AspirationLevel fal=new AspirationLevel(dim); // feasible aspiration level
			final float influence[]=influenceScheme.get(action);
			for (int index=0; index<dim; index++) {
				if (influence[index]<0) fal.set(index,aLow[index]);
				else if (influence[index]>0) fal.set(index,aHi[index]);
				else fal.set(index,aMid[index]);
			}
			List<Action> actionList=expectedFeasibleSet.get(fal);
			if (actionList==null) { // yet, there is no action for this aspiration level
				actionList=new LinkedList<>();
				expectedFeasibleSet.put(fal,actionList);
			}
			actionList.add(action);
		}
	}

	/**
	 * Updates the influence of the last action in the influence scheme and saves
	 * the current values of the goal variables.
	 */
	private void updateInfluenceScheme() {
		if (lastAction==null) return; // nothing to do on first iteration
		final float influence[]=influenceScheme.get(lastAction);
		for (int index=0; index<dim; index++) {
			final double g=goal[index].getValue();
			influence[index]=(float) (g-g0[index]); // update influence
			g0[index]=g; // save value for next iteration
		}
	}

	/**
	 * Is there at least one element of the feasible set that contains the actual
	 * aspiration level?
	 */
	private boolean isAspirationLevelFeasible(AspirationLevel a0) {
		for (final AspirationLevel element : expectedFeasibleSet.keySet()) {
			if (element.contains(a0)) return true;
		}
		return false;
	}

	private int getRereatVariable() { return urgencyOrder[dim-1]; }

	/**
	 * converts a value to the closest aspiration level of a goal variable within
	 * the allowed range
	 */
	private static double g2a(double value, GoalVariable g) {
		final double result=Math.floor(value/g.getStep())*g.getStep();
		if (result<g.getMin()) return g.getMin();
		return result;
	}

	/** converts a value a goal variable to its closest aspiration level */
	private static double g2a(GoalVariable g) {
		return g2a(g.getValue(),g);
	}

}
