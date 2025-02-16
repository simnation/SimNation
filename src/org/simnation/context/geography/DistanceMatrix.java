package org.simnation.context.geography;

public final class DistanceMatrix {

	private final double[][] matrix;

	public DistanceMatrix(int size) {
		matrix=new double[size][];
		for (int i=0; i<size; i++)
			matrix[i]=new double[i+1];
	}

	public void put(int x,int y,double value) {
		int m;
		if (y>x) {
			m=x;
			x=y;
			y=m;
		}
		matrix[x][y]=value;
	}

	public double get(int x,int y) {
		int m;
		if (y>x) {
			m=x;
			x=y;
			y=m;
		}
		return matrix[x][y];
	}
		
}