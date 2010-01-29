package Thomas_Example;

import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.ProfileDescription;
import es.upv.dsic.gti_ia.organization.SFProxy;

public class AgentAnnouncement extends QueueAgent {

    public AgentAnnouncement(AgentID aid) throws Exception {

	super(aid);

    }

    // We create the class that will make us the agent proxy oms,facilitates
    // access to the methods of the OMS
    OMSProxy OMSservices = new OMSProxy();

    // We create the class that will make us the agent proxy sf,facilitates
    // access to the methods of the SF
    SFProxy SFservices = new SFProxy();

    ProfileDescription profile = new ProfileDescription(
	    "http://localhost:8080/SearchCheapHotel/owl/owls/SearchCheapHotelProfile.owl",
	    "SearchCheapHotel");

    public void execute() {

	logger.info("Executing, I'm " + getName());
	String result;

	try {
	    result = OMSservices.acquireRole(this, "member", "virtual");
	    logger.info("[AgentAnnoucement]Acquire Role result: " + result + "\n");
	    SFservices.registerProfile(this, profile);
	    logger.info("[AgentAnnoucement]The operation register Profile return: "
		    + profile.getServiceID() + "\n");
	} catch (Exception e) {
	    logger.error(e.getMessage());

	}
    }
}
