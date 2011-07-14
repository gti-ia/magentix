package MyService_example;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;

import java.util.Hashtable;

import es.upv.dsic.gti_ia.architecture.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CFactory;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_REQUEST_Initiator;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.Oracle;
import es.upv.dsic.gti_ia.organization.SFProxy;

public class CAgentClient extends CAgent {

	// We create the class that will make us the agent proxy oms, facilitates
	// access to the methods of the OMS
	OMSProxy omsProxy = new OMSProxy(this);

	// We create the class that will make us the agent proxy sf, facilitates
	// access to the methods of the SF
	SFProxy sfProxy = new SFProxy(this);

	ArrayList<String> results = new ArrayList<String>();
	Hashtable<AgentID, String> agents = new Hashtable<AgentID, String>();
	Hashtable<String, String> service_response = new Hashtable<String, String>();
	private Oracle oracle;
	String URLProfile;
	String URLProcess;

	public String result;

	/**
	 * Constructor
	 * 
	 * @param aid
	 * @throws Exception
	 */
	public CAgentClient(AgentID aid) throws Exception {

		super(aid);

	}

	// ***************************************** Methods
	// **************************************************

	/**
	 * Acquire Role
	 */
	public void scenario1() {

		// Enter in virtual organization.
		result = omsProxy.acquireRole("member", "virtual");
		System.out.println("[ClientAgent]Acquire Role member in virtual return: "+ result + "\n");

		// Finding agents that offered MyService service...
		do {
			results = sfProxy.searchService("MyService");
		} while (results.size() == 0);

		URLProfile = sfProxy.getProfile(results.get(0));

		System.out.println("[ClientAgent] getProfile: " + URLProfile);
		URL profile;
		try {
			profile = new URL(URLProfile);
			oracle = new Oracle(profile);

		} catch (MalformedURLException e) {
			this.logger.error("ERROR: Profile URL Malformed!");
			e.printStackTrace();
		}

		System.out.println("[ClientAgent] acquireRole "+ oracle.getClientList().get(0)+ " "+ oracle.getClientUnitList().get(0)+ ":"
				+ omsProxy.acquireRole(oracle.getClientList().get(0), oracle.getClientUnitList().get(0)));

		agents = sfProxy.getProcess(results.get(0));

		System.out.println("[ClientAgent]agents that offered Sum service: "+ agents.size() + "\n");

	}

	// Exit
	public void scenario3() {
		System.out.println(" leave role : "	+ omsProxy.leaveRole(oracle.getClientList().get(0), oracle.getClientUnitList().get(0)));
		System.out.println(" leave role : " + omsProxy.leaveRole("member", "virtual"));
	}

	// Execute service MyService.
	public void scenario2() {

		ArrayList<String> arg = new ArrayList<String>();

		String input = "[5,2,5,4,25,5]";
		
		String arguments = " " + oracle.getInputs().get(0) + "=" + input;
		
		Enumeration<AgentID> agents1 = agents.keys();

		AgentID agentToSend = agents1.nextElement();

		URLProcess = agents.get(agentToSend);

		// build the message to service provider
		String call = URLProcess + arguments;

		// ACLMessage
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(this.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(agentToSend);

		// Initialization protocol / conversation request.
		THOMASCAgentRequest protocol = new THOMASCAgentRequest();
		CFactory talk = protocol.newFactory("THOMASRequest", null, requestMsg, 1,
				this, 0);
		this.addFactoryAsInitiator(talk);
		this.startSyncConversation(talk.getName());
		this.removeFactory(talk.getName());

	}

	/**
	 * This class handles the messages received from the provider.
	 */
	class THOMASCAgentRequest extends FIPA_REQUEST_Initiator {

		protected void doInform(CProcessor myProcessor, ACLMessage msg) {
			System.out.println("The result of the service is: "	+ msg.getContent());
					//msg.getContent().substring(msg.getContent().indexOf("MyServiceOutputResult") + 22));
		}

		protected void doAgree(CProcessor myProcessor, ACLMessage msg) {
			System.out.println(myProcessor.getMyAgent().getName() + ": OOH! "
					+ msg.getSender().getLocalName()
					+ " Has agreed to excute the service!");
		}

		protected void doRefuse(CProcessor myProcessor, ACLMessage msg) {
			System.out.println(myProcessor.getMyAgent().getName() + ": Oh no! "
					+ msg.getSender().getLocalName()
					+ " has rejected my proposal.");

		}

		protected void doNotUnderstood(CProcessor myProcessor, ACLMessage msg) {
			System.out.println(myProcessor.getMyAgent().getName() + ":"
					+ msg.getSender().getLocalName()
					+ " has indicated that they didn't understand.");

		}

		protected void doFailure(CProcessor myProcessor, ACLMessage msg) {
			System.out.println(myProcessor.getMyAgent().getName() + ":"
					+ msg.getSender().getLocalName()
					+ " has send me a message which i wasn't"
					+ " expecting in this conversation");

		}
	}

	@Override
	protected void finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void execution(CProcessor firstProcessor,
			ACLMessage welcomeMessage) {

		logger.info("Executing, I'm " + this.getName());
		this.scenario1();
		this.scenario2();
		this.scenario3();

	}

}
