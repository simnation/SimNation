/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 * 
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simnation.strategy;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.simnation.strategy.AspirationAdaptation.Action;
import org.simnation.strategy.AspirationAdaptation.GoalVariable;

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
 */
public class AATest {

	private static final NumberFormat nf2=NumberFormat.getInstance();
	private static final NumberFormat nf6=NumberFormat.getInstance();
	private static final int dim=2; // number of objectives
	private static final double a=1/Math.sqrt(3);
	
	private final AspirationAdaptation aat;	
	private final GoalVariable g[]=new GoalVariable[dim]; // vector of goal variables
	double x[]=new double[3]; // parameter vector
	
	public AATest() {
		g[0]=new GoalVariable() {
			public float getMin() { return 0; }
			public float getMax() { return 1; }
			public float getStep() { return 0.0001f; }
			public float getLimit() { return 0.995f; }
			public double getValue() { return Math.exp(-Math.pow(x[0]-a,2)-Math.pow(x[1]-a,2)-Math.pow(x[2]-a,2)); }
			public String toString() { return "f1="+nf6.format(getValue()); }
		};
		g[1]=new GoalVariable() {
			public float getMin() { return 0; }
			public float getMax() { return 1; }
			public float getStep() { return 0.00001f; }
			public float getLimit() { return 0.995f; }
			public double getValue() { return Math.exp(-Math.pow(x[0]+a,2)-Math.pow(x[1]+a,2)-Math.pow(x[2]+a,2)); }
			public String toString() { return "f2="+nf6.format(getValue()); }
		};

		final List<Action> actionList= new ArrayList<>();
		actionList.add(() -> x[0]+=0.1d);
		actionList.add(() -> x[0]-=0.1d);
		actionList.add(() -> x[1]+=0.1d);
		actionList.add(() -> x[1]-=0.1d);
		actionList.add(() -> x[2]+=0.1d);
		actionList.add(() -> x[2]-=0.1d);
		
		aat=new AspirationAdaptation(g);
		
		x[0]=2d; x[1]=-1.3d; x[2]=-0.7d;
		
		final double oldG[]=new double[dim];	// old goal values
		for (int i=0; i<dim; i++) oldG[i]=g[i].getValue();
		
		for (Action action : actionList) {
			final float influence[]=new float[dim];
			final double oldX[]=x.clone();	// save old parameters
			action.doAction();
			for (int i=0; i<dim; i++) influence[i]=(float) (g[i].getValue()-oldG[i]);
			aat.addAction(action,influence);
			x=oldX;
		}
	}

	private void run() {
		for (int i=0; i<100; i++) {
			aat.decideAction().doAction();
			System.out.println("x="+nf2.format(x[0])+"  "+"y="+nf2.format(x[1])+"  "+"z="+nf2.format(x[2])+"  "+g[0].toString()+"   "+g[1].toString());
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

}
