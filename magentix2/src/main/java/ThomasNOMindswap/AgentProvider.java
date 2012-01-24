package ThomasNOMindswap;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.architecture.FIPARequestResponder;
import es.upv.dsic.gti_ia.architecture.MessageTemplate;
import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.architecture.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.Oracle;
import es.upv.dsic.gti_ia.organization.ProcessDescription;
import es.upv.dsic.gti_ia.organization.SFProxy;
import es.upv.dsic.gti_ia.organization.ServiceClient;

public class AgentProvider extends QueueAgent {

    private OMSProxy omsProxy = new OMSProxy(this);

    private SFProxy sfProxy = new SFProxy(this);

    private ArrayList<String> results = new ArrayList<String>();

    private Oracle oracle;

    ProcessDescription processDescription = new ProcessDescription(
	    "http://localhost:8080/SearchCheapHotel/owl/owls/SearchCheapHotelProcess.owl",
	    "SearchCheapHotel");

    public AgentProvider(AgentID aid) throws Exception {

    	super(aid);

    }

    public void escenario1() {


    	System.out.println("[AgentProvider] Acquire Role member in virtual: "+ omsProxy.acquireRole( "member", "virtual"));

    }



//    public void escenario3() {
//
//	
//
//	    results = sfProxy.searchService("SearchCheapHotel");
//
//	    if (results.size() == 0) {
//		System.out.println("profiles are not similar to SearchCheapHotel");
//	    } else {
//		// cogemos el primero por ejemplo
//		String URLProfile = sfProxy.getProfile(results.get(0));
//
//		URL profile;
//		try {
//		    profile = new URL(URLProfile);
//		    oracle = new Oracle(profile);
//
//		} catch (MalformedURLException e) {
//		    logger.error("ERROR: Profile URL Malformed!");
//		    e.printStackTrace();
//		}
//
//	
//		 System.out.println("[AgentProvider] Acquire Role "+oracle.getProviderList().get(0)+" in "+oracle.getProviderUnitList().get(0)+ " :"+omsProxy.acquireRole(oracle.getProviderList().get(0), oracle.getProviderUnitList().get(0)));
//
//	    }
//
//
//    }

//    public void escenario4() {
//
//
//	    processDescription.setProfileID(results.get(0));
//
//	    System.out.println("[AgentProvider] RegisterProcess: "+ sfProxy.registerProcess(processDescription));
//
//
//
//    }

    public void escenario5(){
	try{
	System.out.println("[AgentProvider] Acquire Role payee in travelagency: "+ omsProxy.acquireRole("payee", "travelagency"));
	
	}catch(Exception e)
	{
	    logger.error(e.getMessage());
	}
    }
    public void escenario6() {
	// Rol responder
	Responder responder = new Responder(this);

	this.addTask(responder);


    }

    public void execute() {

	DOMConfigurator.configure("configuration/loggin.xml");
	logger.info("Executing, I'm " + getName());
	

	this.escenario1();
//	this.escenario3();
//	this.escenario4();
	this.escenario5();
	this.escenario6();

	// when we do not have to create more roles we await the expiration
	// of the other roles

	es.upv.dsic.gti_ia.architecture.Monitor mon = new es.upv.dsic.gti_ia.architecture.Monitor();
	mon.waiting();

    }

    /**
     * Manages the messages for the  agent provider services
     */
    public class Responder extends FIPARequestResponder {



	public Responder(QueueAgent agent) {
	    super(agent, new MessageTemplate(InteractionProtocol.FIPA_REQUEST));

	}// SFResponder

	/**
	 * Receives the messages and takes the message content. Analyzes the
	 * message content and gets the service process and input parameters to
	 * invoke the service. After the service invocation, the agent provider gets the
	 * answer and sends it to the requester agent.
	 * 
	 * @param
	 * @throws RuntimeException
	 */
	protected ACLMessage prepareResponse(ACLMessage msg) {

	    ACLMessage response = msg.createReply();
	    if (msg != null) {

		try {


		    String processLocalName = processDescription.getProcessLocalName(msg);
		    System.out.println("[Provider] AGREE");
		    response.setPerformative(ACLMessage.AGREE);
		    response.setContent(processLocalName + "=Agree");
		    System.out.println("[Provider] "+response.getContent());


		} catch (Exception e) {

		    System.out.println("EXCEPTION");
		    System.out.println(e);
		    e.printStackTrace();
		    throw new RuntimeException(e.getMessage());

		}

	    } else {

		System.out.println("NOTUNDERSTOOD");
		response.setPerformative(ACLMessage.NOT_UNDERSTOOD);
		response.setContent("NotUnderstood");
	    }

	    System.out.println("[Provider]Sending First message:" + response);

	    return (response);

	} // end prepareResponse

	/**
	 * This callback happens if the SF sent a positive reply to the original
	 * request (i.e. an AGREE) if the SF has agreed to supply the service,
	 * the agent provider has to inform the other agent that what they have asked is now
	 * complete (or if it failed)
	 * 
	 * @param inmsg
	 * @param outmsg
	 * @throws RuntimeException
	 */
	protected ACLMessage prepareResultNotification(ACLMessage inmsg, ACLMessage outmsg) {

		ACLMessage msg = inmsg.createReply();
	   
		try {
	    
		
		// read in the service description
		String processURL = processDescription.getProcessURL(inmsg);
		
		//extract process' local name
		String process_localName = processDescription.getProcessLocalName(inmsg);
	    
		System.out.println("[Provider]Executing: " + processURL + "\n localName="+process_localName);
		System.out.println("[Provider]Executing: " + inmsg.getContent());
		
		// read msg content
		List<String> params= processDescription.getServiceRequestValuesList(inmsg);
		
		
		
	    // create an execution engine
//	    ProcessExecutionEngine exec = OWLSFactory.createExecutionEngine();

	    
//		Process aProcess = processDescription.getProcess(inmsg);
//
//		
//		// initialize the input values to be empty
//		ValueMap values = new ValueMap();
//		
//		values = processDescription.getServiceRequestValues(inmsg);
//		
//		
//		System.out.println("[Provider]Executing... " + values.getValues().toString());
//		values = exec.execute(aProcess, values);
//
//		System.out.println("[Provider]Values obtained... :" + values.toString());
//
		System.out.println("[Provider]Creating inform message to send...");

		msg.setPerformative(ACLMessage.INFORM);

		
	    ServiceClient serviceClient = new ServiceClient();
	    ArrayList<String> results = serviceClient.invoke(processURL, params);
	    
		String resultStr=process_localName+ "=" + "{";
		for(int i=0;i<results.size();i++){
			resultStr+=processURL+"#"+results.get(i);
			if(i!=results.size()-1){
				resultStr+=", ";
			}
			else{
				resultStr+=" }";
			}
		}
		
		
		
	    System.out.println("[Provider]Before set message content...");
//	    System.out.println("[Provider] "+aProcess.getLocalName() + "=" + values.toString());
//		msg.setContent(aProcess.getLocalName() + "=" + values.toString());
	    
	    System.out.println("[Provider] "+resultStr);
		msg.setContent(resultStr);
	    	
	    	
	    //[Provider] SearchCheapHotelProcess={http://localhost:8080/SearchCheapHotel/owl/owls/SearchCheapHotelProcess.owl#SearchCheapHotelOutputHotel=Hotel Puerta de Valencia, http://localhost:8080/SearchCheapHotel/owl/owls/SearchCheapHotelProcess.owl#SearchCheapHotelOutputHotelCompany=NH}
	    //[Provider] SearchCheapHotelProcess=SearchCheapHotelOutputHotelCompany=NH SearchCheapHotelOutputHotel=Hotel Puerta de Valencia

	    } catch (Exception e) {

		System.out.println("EXCEPTION");
		System.out.println(e);
		e.printStackTrace();
		msg.setPerformative(ACLMessage.FAILURE);
	    }
	    return (msg);
	} // end prepareResultNotification

    }// end class SFResponder

}
