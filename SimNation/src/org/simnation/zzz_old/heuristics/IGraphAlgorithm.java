/**
 * 
 */
package org.simnation.editor.heuristics;


import org.simnation.model.geography.DistanceMatrix;

/**
 * All graph algorithms calculating a {@link DistanceMatrix} have to implement this interface
 * 
 * @author Rene
 *
 */
public interface IGraphAlgorithm {
	
	public DistanceMatrix calcDistanceMatrix();

}
