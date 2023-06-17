/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 * 
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simnation.strategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This class implements a variation of the aspiration adaptation algorithm.
 * <p>
 * Aspiration adaptation is a heuristic algorithm for multi-goal optimization by
 * Reinhard Selten.
 * <p>
 * <u>Propositions:</u>
 * <ul>
 * <li>There is a given set of <i>goal variables</i> that have to be
 * <i>maximized</i>.
 * <li>There is a given set of <i>actions</i> that influence the goal variables.
 * <li>Goal variables have certain limits, below these limits they become
 * <i>urgent</i>.
 * <li>An <i>urgency order</i> is formed for each combination of goal values to
 * prioritize goals.
 * <li>The algorithm evaluates how an action influences the goal variables by
 * means of an <i>influence scheme</i>.
 * </ul>
 * The algorithm returns the action supporting the most urgent goal with the
 * most positive effects on all other goals. If there is no such action, it
 * return the action with the least negative impact.
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

	public enum STRATEGY {
		PRIO, SUM, RANDOM, OVERALL
	}

	/** abstraction layer for the goal variables / objectives */
	public interface GoalVariable {

		/**
		 * When the limit is reached, the goal variable no longer is urgent. The limit
		 * has to be a positive value.
		 */
		double getLimit();

		/** The current value of the goal variable. */
		double getValue();

		default boolean isUrgent() { return getValue()<=getLimit(); }

		/**
		 * The scale of the goal variable is divided in discrete steps. A best educated
		 * guess of the step size is 1% of the limit.
		 */
		default double getStep() { return getLimit()/100.0d; }

	}

	/** abstraction layer, has to be implemented by the callers actions */
	public interface Action {
		void doAction();
	}

	private final GoalVariable[] goal; // constant array of goal variables

	/**
	 * mapping actions to their presumed influence on the goal variables The
	 * influence is measured as number of discrete steps. In other words: How many
	 * percent of the limit does an action change?
	 */
	private final Map<Action, int[]> influenceScheme=new IdentityHashMap<>();

	private final int[] urgencyOrder; // permutation vector
	private final double[] g0; // vector of last goal variable values, g0=g(t-1)

	private Action lastAction=null;
	private int prioritizedGoal; // currently prioritized goal, prio=dim if no goal could be found 
	private final STRATEGY strategy;
	private final int dim; // number of goal variables

	/**
	 * Constructor of aspiration adaptation strategy
	 * <p>
	 * Note: Goal variables have to be ordered with descending priority. The
	 * priority cannot be changed later on.
	 *
	 * @param goals array of goal variables
	 */
	public AspirationAdaptation(GoalVariable goals[], STRATEGY s) {
		// init goals and action lists
		goal=goals;
		dim=goals.length; // number of goals determines dimension of other schemes
		strategy=s;
		urgencyOrder=new int[dim];
		prioritizedGoal=dim;

		// init goal variable values;
		g0=new double[dim];
		for (int index=0; index<dim; index++) g0[index]=goal[index].getValue();
	}

	public AspirationAdaptation(GoalVariable goals[]) { this(goals,STRATEGY.SUM); }

	/**
	 * Adds a new action with estimated influences to the list of actions.
	 * <p>
	 * Use values -1, 0 and 1 to initialize the influence scheme with a best
	 * educated guess of an action's effect.
	 * <p>
	 * Best educated guessing of the influence is okay, since the influence is
	 * adapted later on. Filling the influence scheme only with zeros may lead to a
	 * longer "warm-up" time of the algorithm.
	 * 
	 * @param action    the action
	 * @param influence estimated influence of the action on the goal variables
	 * @return the action for further usage, {@code null} if an error occurred
	 */
	public Action addAction(Action action, int influence[]) {
		if (influence.length!=dim) return null;
		influenceScheme.put(action,influence);
		return action;
	}

	public Action decideAction() {
		if (lastAction!=null) updateInfluenceScheme();
		updateUrgencyOrder();
		lastAction=findBestAction(); // find best action according to selected strategy
		return lastAction;
	}

	private Action findBestAction() {
		final List<Action> list=createCandiateList();
		Action result=null;
		if (!list.isEmpty()) switch (strategy) {
		case SUM:
			result=selectBestSum(list);
			break;
		case PRIO:
			result=selectBestPrio(list);
			break;
		case RANDOM:
			result=list.get(ThreadLocalRandom.current().nextInt(list.size()));
			break;
		case OVERALL: // use fallback
			break;
		}
		// fallback: select least bad action according to the sum of influences
		if (result==null) result=selectBestSum(influenceScheme.keySet());
		return result;
	}

	private Action selectBestSum(Collection<Action> list) {
		int best=Integer.MIN_VALUE;
		Action result=null;
		for (final Action action : list) {
			final int sum=sumInfluence(action);
			if (sum>best) {
				best=sum;
				result=action;
			}
		}
		return result;
	}

	private Action selectBestPrio(Collection<Action> list) {
		int best=Integer.MIN_VALUE;
		Action result=null;
		for (final Action action : list) {
			final int influence=influenceScheme.get(action)[prioritizedGoal];
			if (influence>best) {
				best=influence;
				result=action;
			}
		}
		return result;
	}

	/**
	 * converts a value to the closest aspiration level of a goal variable within
	 * the allowed range
	 */
	private List<Action> createCandiateList() {
		final List<Action> result=new ArrayList<>();
		// find all actions that should improve the prioritized goal,
		// decrease priority if there are no suitable actions.
		for (final int prio : urgencyOrder) {
			for (final Action action : influenceScheme.keySet()) {
				// add all actions with positive influence on the prioritized goal to the result set
				if (influenceScheme.get(action)[prio]>0) result.add(action);
			}
			prioritizedGoal=prio;
			if (!result.isEmpty()) return result;
		}
		// result set is empty, no action with positive influence on any goal variable was found.
		prioritizedGoal=dim;
		return result;
	}

	/**
	 * Generates a permutation of goal indices according to the current aspiration
	 * level and sorted by descending urgency.
	 * <p>
	 * The new urgency order contains the indices of the goal variable sorted by
	 * descending urgency, the last element indicating the retreat variable.
	 *
	 */
	private void updateUrgencyOrder() {
		int index=0;
		// first pass: below the limit, goals with lower indices are more urgent
		for (int i=0; i<dim; i++) if (goal[i].isUrgent()) urgencyOrder[index++]=i;
		// second pass: if the goal limit is reached, goals with higher indices are more urgent
		for (int i=dim-1; i>=0; i--) if (!goal[i].isUrgent()) urgencyOrder[index++]=i;
	}

	/**
	 * Updates the influence of the last action in the influence scheme and saves
	 * the current values of the goal variables.
	 */
	private void updateInfluenceScheme() {
		final int influence[]=influenceScheme.get(lastAction);
		for (int index=0; index<dim; index++) // assess change in number of discrete steps
			influence[index]=(int) ((goal[index].getValue()-g0[index])/goal[index].getStep()); // update influence
	}

	private int sumInfluence(Action action) {
		final int[] influence=influenceScheme.get(action);
		int result=0;
		for (int i=0; i<dim; i++) result+=influence[i];
		return result;
	}

}
