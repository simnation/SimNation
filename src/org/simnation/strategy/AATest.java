/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 * 
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simnation.strategy;

import java.text.NumberFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.simnation.strategy.AspirationAdaptation.Action;
import org.simnation.strategy.AspirationAdaptation.GoalVariable;
import org.simnation.strategy.AspirationAdaptation.STRATEGY;

/**
 * Testing the aspiration adaptation strategy by a three-parameter optimization
 * of a two-objective problem.
 * <p>
 * The Fonseca-Fleming Problem is a two-objective problem that can be used with
 * a variable number of parameters.
 * 
 * @see <a href=
 *      "http://www.mathlayer.com/support/benchmark-problems-fonseca-fleming.html">Fonseca-Fleming
 *      Problem </a>
 * @see <a href=
 *      "https://www.al-roomi.org/benchmarks/multi-objective/unconstrained-list/321-fonseca-fleming-s-function-fon">Fonseca-Fleming
 *      Problem </a>
 
 */
public class AATest {

	private static final NumberFormat nf2=NumberFormat.getInstance();
	private static final NumberFormat nf6=NumberFormat.getInstance();
	private static final int dim=2; // number of objectives
	private static final double a=1/Math.sqrt(3);
	
	private final AspirationAdaptation aat;	
	private final GoalVariable g[]=new GoalVariable[dim]; // vector of goal variables
	double x[]=new double[3]; // parameter vector
	
	final Chart chart=new Chart();

		
	public AATest() {
		g[0]=new GoalVariable() {
			public double getLimit() { return 1d; }
			public double getValue() { return f1(x); }
			public String toString() { return "f1="+nf6.format(getValue()); }
		};
		g[1]=new GoalVariable() {
			public double getLimit() { return 1d; }
			public double getValue() { return f2(x); }
			public String toString() { return "f2="+nf6.format(getValue()); }
		};

		final List<Action> actionList= new ArrayList<>();
		actionList.add(() -> x[0]+=0.01d);
		actionList.add(() -> x[0]-=0.01d);
		actionList.add(() -> x[1]+=0.01d);
		actionList.add(() -> x[1]-=0.01d);
		actionList.add(() -> x[2]+=0.01d);
		actionList.add(() -> x[2]-=0.01d);
		
		aat=new AspirationAdaptation(g,STRATEGY.PRIO);
		
		x[0]=0.8; x[1]=0.5; x[2]=0.31;
		
		final double oldG[]=new double[dim];	// old goal values
		for (int i=0; i<dim; i++) oldG[i]=g[i].getValue();
		
		for (Action action : actionList) {
			final int influence[]=new int[dim];
			final double oldX[]=x.clone();	// save old parameters
			action.doAction();
			for (int i=0; i<dim; i++) influence[i]=(int) Math.signum(g[i].getValue()-oldG[i]);
			aat.addAction(action,influence);
			x=oldX;
		}
	}

	private void run() {
		for (int i=0; i<1000; i++) {
			aat.decideAction().doAction();
			System.out.println("x="+nf2.format(x[0])+"  "+"y="+nf2.format(x[1])+"  "+"z="+nf2.format(x[2])+"  "+g[0].toString()+"   "+g[1].toString());
			chart.update(g[0].getValue(),g[1].getValue());
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {		
		nf2.setMaximumFractionDigits(2);
		nf2.setMinimumFractionDigits(2);
		nf6.setMaximumFractionDigits(6);
		nf6.setMinimumFractionDigits(6);
		final AATest test=new AATest();
		test.run();
	}
	
	public double f1(double x[]) {
		return Math.exp(-Math.pow(x[0]-a,2)-Math.pow(x[1]-a,2)-Math.pow(x[2]-a,2)); 
	}
	
	public double f2(double x[]) {
		return Math.exp(-Math.pow(x[0]+a,2)-Math.pow(x[1]+a,2)-Math.pow(x[2]+a,2)); 
	}

}
