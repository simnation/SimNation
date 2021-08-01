package org.simnation.model;

import org.simplesim.core.scheduling.Time;

public final class Limits {
	
	public static final int MAX_HOUSEHOLD_AGENTS=500000;
	public static final int MAX_ENTERPRISE_AGENTS=10000;
	public static final int MAX_BANK_AGENTS=100;
	
	public static final int MAX_REGIONAL_POPULATION=5000000;
	public static final int MAX_REGIONAL_AREA=100000; // km^2
	
	// Citizen generation limits
	public static final int MAX_CHILDREN=20;
	public static final long MAX_AGE=100*Time.TICKS_PER_YEAR;
	public static final int MAX_SKILL=100;
	public static final float INTROVERSION=0.0f;
	public static final float EXTRAVERSION=2.0f;
	
	
	public static final int NUMBER_OF_TRIES=5; // number of activation events before regression starts
	
	// coords of the business system in the real world
	private static final float WORKING_DAYS=5;
	private static final float DEFAULT_WORKING_TIME_PER_DAY=8*Time.TICKS_PER_HOUR;
	private static final float MAX_WORKING_TIME_PER_DAY=14*Time.TICKS_PER_HOUR;
	
	// coords of the business system in the simulation model
	// divide 5 working days equally on 7 working days, it comes out to the same in the simulation model
	public static final int DAYS_PER_WORKING_PERIOD=Time.DAYS_PER_WEEK;
	public static final int WORKING_PERIOD=DAYS_PER_WORKING_PERIOD*Time.TICKS_PER_DAY;
	public static final int MIN_PRODUCTION_PERIOD=Time.TICKS_PER_DAY;
	public static final float MAX_WORKLOAD=MAX_WORKING_TIME_PER_DAY/DEFAULT_WORKING_TIME_PER_DAY;
	public static final float DEFAULT_WORKING_TIME=WORKING_DAYS*DEFAULT_WORKING_TIME_PER_DAY/WORKING_PERIOD;
	public static final float MAX_WORKING_TIME=WORKING_DAYS*MAX_WORKING_TIME_PER_DAY/WORKING_PERIOD;
	
	public static final long LEGAL_AGE=18*Time.TICKS_PER_YEAR;

	public static final int DAYS_PER_BUDGET_PERIOD=Time.DAYS_PER_WEEK;
	public static final int BUDGET_PERIOD=DAYS_PER_BUDGET_PERIOD*Time.TICKS_PER_DAY;
	
	public static final int CHANGING=-1; // changing period length, needed for EventType

	
	// Limits of good and need context
	public static final int MAX_RESOURCESET_SIZE=3*7; // should be divisible by 3
	public static final int MAX_GOODSET_SIZE=50;	// to limit complexity of value chain
	public static final int MAX_PRODUCTION_DEPTH=6; // to limit complexity of value chain
	public static final int MAX_PRECURSORS=4;
		
	// *** Resources ***
	public static final int MAX_RESOURCE_STOCK=Integer.MAX_VALUE;
	public static final double MAX_GAMMA=Double.MAX_VALUE;
	public static final double MIN_GAMMA=0;
	
	// *** Goods ***
	public static final double MAX_PRECURSOR_AMOUNT=10000;
	
	
}
