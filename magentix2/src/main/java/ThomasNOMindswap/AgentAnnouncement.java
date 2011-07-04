package ThomasNOMindswap;

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
    OMSProxy OMSservices = new OMSProxy(this);

    // We create the class that will make us the agent proxy sf,facilitates
    // access to the methods of the SF
    SFProxy SFservices = new SFProxy(this);

    ProfileDescription profile = new ProfileDescription(
	    "http://localhost:8080/SearchCheapHotel/owl/owls/SearchCheapHotelProfile.owl",
	    "SearchCheapHotel");

    public void execute() {

    System.out.println("Executing, I'm " + getName());
	String result;


	    result = OMSservices.acquireRole("member", "virtual");
	    System.out.println("[AgentAnnoucement] Acquire Role member in virtual: "+ result);
	
	    SFservices.registerProfile(profile);
	    
	    System.out.println("[AgentAnnoucement]The operation register Profile return: "
		    + profile.getServiceID() + "\n");

    }
}
