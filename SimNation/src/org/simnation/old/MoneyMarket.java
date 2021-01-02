package org.simnation.old;

/**Money market
 * 
 * 
 * @author Rene Kuhlemann
 *
 */



import org.simnation.core.AgentType;
import org.simnation.simulation.business.Application;




public class MoneyMarket extends Market<MoneyMarketState,MM_SEGMENT,Application> {
    
    private static final AgentType TYPE=AgentType.MONEY_MARKET;
	
    
	public MoneyMarket() {
	    super(TYPE);
        addEvent(INVOKE_MARKET,INVOKE_MARKET.offset());
        addEvent(SEND_MARKETINFO,SEND_MARKETINFO.offset());
	}

	protected MoneyMarketState createState() {
		return(new MoneyMarketState());
	}

	protected void deltaInternal() {
        EventType event=dequeueCurrentEvent();
        switch(event) {
	        case SEND_MARKETINFO:
	            /*for(SkillSet skill : SkillSet.values()) 
	                getState().getMarketSegment(skill).broadcastMarketInfo(this);*/
	            addEvent(SEND_MARKETINFO,SEND_MARKETINFO.period());
	            break;
	        case INVOKE_MARKET:
	            processMessages();
	            addEvent(INVOKE_MARKET,INVOKE_MARKET.period());
	            break;
	        default: throw new UnhandledEventTypeException(this,event);
	    }
	}

}
