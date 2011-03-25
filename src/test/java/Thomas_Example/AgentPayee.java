package Thomas_Example;

import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.OMSProxy;

public class AgentPayee extends QueueAgent {

    private OMSProxy omsProxy = new OMSProxy(this);

    public AgentPayee(AgentID aid) throws Exception {

	super(aid);

    }

    public void execute() {
	DOMConfigurator.configure("configuration/loggin.xml");
	System.out.println("Executing, I'm " + getName());

	this.escenario2();

    }

    public void escenario2() {

	System.out.println("[AgentPayee] Acquire Role member in virtual: "+omsProxy.acquireRole( "member", "virtual"));
	System.out.println("[AgentPayee] Acquire Role payee in travelagency: "+omsProxy.acquireRole("payee", "travelagency"));
	System.out.println("[AgentPayee] Register norm: "+omsProxy.registerNorm( "norma1",
		    "FORBIDDEN_Member_REQUEST_acquireRole_MESSAGE(CONTENT(ROLE_'Payee'))"));


    }

}
