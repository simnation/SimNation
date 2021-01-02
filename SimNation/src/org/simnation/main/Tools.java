/**
 * 
 */
package org.simnation.main;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.List;

/**
 * Some static utility functions to facility readability
 * 
 * @author Rene Kuhlemann
 *
 */
public final class Tools {
	
	private static final DecimalFormat df=new DecimalFormat();
			
	static {	
		DecimalFormatSymbols dfs=DecimalFormatSymbols.getInstance();
		dfs.setGroupingSeparator(',');
		dfs.setDecimalSeparator('.');
		dfs.setCurrencySymbol("MU");
		df.setDecimalFormatSymbols(dfs);
		df.setMinimumFractionDigits(0);
		df.setMaximumFractionDigits(4);
	}
		
    public static <E extends Enum<E>> E convertStringToEnum(String value,E e[]) {             
        for(E iter : e) if (iter.toString().equals(value)) return(iter);
        throw new RuntimeException("Unknown enum value:"+value.toUpperCase());
    }
    
    public static String[] convertEnumToString(Enum<?>[] e) {
    	String[] result=new String[e.length];
		for (int index=0; index<e.length; index++) result[index]=e[index].toString();
    	return result;
	}
    
    public static <E extends Enum<E>> E[] castEnum(Enum<?>[] e) {
        @SuppressWarnings("unchecked")
        E[] result = (E[]) e;
        return result;
    }
    
    public static String[] convertListToString(List<?> c) {
    	String[] result=new String[c.size()];
		for (int i=0; i<c.size(); i++) result[i]=c.get(i).toString();
    	return result;
	}
    
    public static void out(String text) {
    	System.out.println(text);
    }
    
	public static String format(double d) { return(df.format(d)); }
	
	public static String percentage(double d) {
		NumberFormat nf = NumberFormat.getPercentInstance();
		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(2);
		return(nf.format(d)); 
	}
		
	public static boolean isLowerCase(String str) {
		return str.toLowerCase().equals(str);
	}
	
	public static int roundUp(double x) { 
		Integer result=roundOff(x); // do not round up if x is already an integer!!!
		if (result.doubleValue()==x) return(result);
		else return(result+1); 
	}

	public static int roundOff(double x) { return((int)x); }

	public static double maximal(double act, double max) { if (act>max) return(max); else return(act); }
	public static double minimal(double act, double min) { if (act<min) return(min); else return(act); }
	public static int maximal(int act, int max) { if (act>max) return(max); else return(act); }
	public static int minimal(int act, int min) { if (act<min) return(min); else return(act); }
	public static double exp(double a,double x) { return(Math.exp(x*Math.log(a))); }
	public static double log(double x,double b) { return(Math.log(x)/Math.log(b)); }

	private static final int ODD_PRIME_NUMBER = 37;
	private static int firstTerm(int seed){ return(ODD_PRIME_NUMBER*seed); }

	public static int hash(int seed, boolean aBoolean) { return(firstTerm(seed)+(aBoolean ? 1 : 0)); }
	public static int hash(int seed, char aChar) { return(firstTerm(seed)+(int)aChar); }
	public static int hash(int seed, int aInt) { return(firstTerm(seed)+aInt); }
	public static int hash(int seed, long aLong) { return(firstTerm(seed)+(int)(aLong^(aLong>>>32))); }
	public static int hash(int seed, float aFloat) { return(hash(seed,Float.floatToIntBits(aFloat))); }
	public static int hash(int seed, double aDouble) { return hash(seed,Double.doubleToLongBits(aDouble)); }
	public static int hash(int seed, String aString) { return hash(seed,aString.hashCode()); }

	/**
	 * <code>obj</code> is a possibly-null object field, and possibly an array.
	 *
	 * If <code>obj</code> is an array, then each element may be a primitive
	 * or a possibly-null object.
	 */
	public static int hash(int seed, Object obj) {
		int result=seed;
		if (obj==null) result=hash(result,0);
		else if (obj.getClass().isArray()) {
			for (int index=0;index<Array.getLength(obj);index++) {
				Object item=Array.get(obj,index);
				result=hash(result,item);
			}
		} else result=hash(result,obj.hashCode());
		return result;
	}    
}
