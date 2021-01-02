package org.simnation.simulation.agents.testagent;

import static org.simnation.simulation.agents.AbstractBasicAgent.Type.TESTAGENT;

import org.simnation.simulation.agents.AbstractBasicAgent;


/**
 * Base class of any test agent
 */
@SuppressWarnings("serial")
public abstract class TestAgent extends AbstractBasicAgent<TestAgentState> {

	public TestAgent(short region) {
		super(TESTAGENT,region);
	}

	protected TestAgentState createState() {
		return(new TestAgentState()); // creates empty agent state
	}
	
}
