/**
 * 
 */
package org.simnation.simulation.model;

/**stores information of national relevance 
 * @author Rene Kuhlemann
 *
 */
final class NationalInformation {
	
	private final float expectation;
	
	NationalInformation() {
		expectation=1.0f;
	}

	float getFutureExpectation() {
		return(expectation);
	}

}
