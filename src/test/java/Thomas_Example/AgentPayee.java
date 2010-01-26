package Thomas_Example;

import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.OMSProxy;

public class AgentPayee extends QueueAgent {

    
    private OMSProxy omsProxy = new OMSProxy();
	    
	    
    public AgentPayee(AgentID aid) throws Exception {

	super(aid);

    }
    
    
    public void execute(){
		DOMConfigurator.configure("configuration/loggin.xml");
		logger.info("Executing, I'm " + getName());
		
		
		this.escenario2();
	
    }
	
	public void escenario2() {
		try {
		    omsProxy.acquireRole(this,"member", "virtual");
		    omsProxy.acquireRole(this, "payee", "travelagency");
		    omsProxy.registerNorm(this, "norma1",
			    "FORBIDDEN_Member_REQUEST_acquireRole_MESSAGE(CONTENT(ROLE_'Payee'))");
		} catch (Exception e) {
		    logger.error(e.getMessage());
		}

	    }
    
}
