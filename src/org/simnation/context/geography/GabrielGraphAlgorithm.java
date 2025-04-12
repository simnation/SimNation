package org.simnation.context.geography;

import java.util.List;

import org.simnation.model.geography.DistanceMatrix;
import org.simnation.model.geography.Region;

public class GabrielGraphAlgorithm  {

	private final List<RegionData> graph;
	
	public GabrielGraphAlgorithm(List<RegionData> list) {
		graph=list;
	}

	private boolean isNeighbor(RegionData from,RegionData to) {
		final double lng=0.5*(from.getLongitude()+to.getLongitude()); // calc the circle's mid point
		final double lat=0.5*(from.getLatitude()+to.getLatitude());
		final double r=0.5*from.distanceTo(to); // radius of circle equals half of the distance

		for (final RegionData reg : graph) {
			if (reg==from||reg==to) continue; // ignore "from" and "to"
			if (reg.distanceTo(lng,lat)<r) return false; // is any other point within the circle?
		}
		return true;
	}

	// calc DistanceMatrix by Floyd-Warshall-Algorithm
	public DistanceMatrix calcDistanceMatrix() {
		final DistanceMatrix matrix=new DistanceMatrix(graph.size());
		// calc neighbors for all points
		for (int i=0; i<graph.size(); i++) {
			for (int j=i+1; j<graph.size(); j++)
				if (isNeighbor(graph.get(i),graph.get(j))) graph.get(i).addNeighbor(graph.get(j));
		}
		// fill matrix with distances to neighbors in graph and infinity everywhere else
		for (int i=0; i<graph.size(); i++)
			for (int j=i+1; j<graph.size(); j++)
				matrix.put(i,j,graph.get(i).getDistanceToNeighbor(graph.get(j)));
		// calc shortest distances by Floyd-Warshall-Algorithm
		for (int k=0; k<graph.size(); k++) {
			for (int i=0; i<graph.size(); i++)
				for (int j=i+1; j<graph.size(); j++) {
					final double d=matrix.get(i,k)+matrix.get(k,j);
					if (matrix.get(i,j)>d) matrix.put(i,j,d);
				}
		}
		return matrix;
	}

}
