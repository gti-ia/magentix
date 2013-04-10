package TestThomas;

/**
 * In this class the agent James is represented. 
 * Functions:
 *  -	Acquire role student inside the unit school.
 *  -	Search service addition, product and square.
 *  -	Request the execution of the addition service.
 *  -	Request the execution of the product service.
 *  -	Execute service Square.
 */

import java.util.ArrayList;
import java.util.HashMap;

import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CFactory;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_REQUEST_Initiator;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.Oracle;
import es.upv.dsic.gti_ia.organization.Provider;
import es.upv.dsic.gti_ia.organization.SFProxy;
import es.upv.dsic.gti_ia.organization.ServiceTools;
import es.upv.dsic.gti_ia.organization.exception.THOMASException;

public class James extends CAgent {

	OMSProxy omsProxy = new OMSProxy(this);
	SFProxy sfProxy = new SFProxy(this);
	ServiceTools st = new ServiceTools();

	String requestResult = "";
	String message;

	public James(AgentID aid) throws Exception {
		super(aid);
	}

	public String getMessage()
	{
		return message;
	}
	protected void execution(CProcessor myProcessor, ACLMessage welcomeMessage) {

		try {

			// James has to resolve the equation (5 * (3 + 4))^2
			String resultEquation="";

			System.out.println("[" + this.getName() + "]" + " I want to resolve the equation (5 * (3 + 4))^2");

			String result = omsProxy.acquireRole("student", "school");
			logger.info("[" + this.getName() + "] Result acquire role student: " + result);
			System.out.println("[" + this.getName() + "]" + " student role (school) acquired");

			// ---------------------------------------------------------------------
			// ---Searching for the Addition service----------------------
			// ---------------------------------------------------------------------

			ArrayList<String> searchInputs = new ArrayList<String>();
			ArrayList<String> searchOutputs = new ArrayList<String>();
			ArrayList<String> searchKeywords = new ArrayList<String>();

			searchInputs.add(ServiceTools.OntologicalTypesConstants.DOUBLE);
			searchInputs.add(ServiceTools.OntologicalTypesConstants.DOUBLE);

			searchOutputs.add(ServiceTools.OntologicalTypesConstants.DOUBLE);

			searchKeywords.add("addition");

			ArrayList<ArrayList<String>> foundServices;

			do {
				// Waiting for services
				try {
					Thread.sleep(2 * 1000);
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
				foundServices = sfProxy.searchService(searchInputs, searchOutputs, searchKeywords);

			} while (foundServices.isEmpty());

			// -----------------------------------------------------------------------------
			// ---Requesting the execution of the Addition service-------------
			// -----------------------------------------------------------------------------

			//get the first service found because it is the most suitable
			String serviceOWLS = sfProxy.getService(foundServices.get(0).get(0));

			Oracle oracle = new Oracle(serviceOWLS);

			//get service inputs
			ArrayList<String> serviceInputs = oracle.getOwlsProfileInputs();

			//put the service inputs values
			HashMap<String, String> agentInputs = new HashMap<String, String>();

			for (String input : serviceInputs) {
				if (input.equalsIgnoreCase("x"))
					agentInputs.put(input, "3");
				else if (input.equalsIgnoreCase("y"))
					agentInputs.put(input, "4");
				else
					agentInputs.put(input, "0");
			}
			
			
			//agents or organizations providers
			ArrayList<Provider> providers = oracle.getProviders();
			//web services providers
			ArrayList<String> providersGroundingWSDL = oracle.getProvidersGroundingWSDL();
			
			if(!providers.isEmpty()){
				System.out.println("[" + this.getName() + "]" + " Requesting Addition Service (3+4)");

				// Building the ACL message
				ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
				msg.setReceiver(new AgentID(providers.get(0).getEntityID()));
				msg.setProtocol("fipa-request");
				msg.setSender(getAid());

				String content = st.buildServiceContent(oracle.getServiceName(), agentInputs);

				// ACL message content is formed by XML format with service name and
				// inputs
				msg.setContent(content);

				this.send_request(msg);

				ServiceTools st = new ServiceTools();
				HashMap<String, String> outputs = new HashMap<String, String>();
				st.extractServiceContent(requestResult, outputs);
				resultEquation = outputs.get("Result");
			}
			else if(!providersGroundingWSDL.isEmpty()){
				System.out.println("[" + this.getName() + "]" + " Executing Addition Service (3+4)");

				HashMap<String, Object> resultExecution = st.executeWebService(providersGroundingWSDL.get(0), agentInputs);

				Double resultDouble = (Double) resultExecution.get("Result");
				resultEquation = resultDouble.toString();
			}
			else{//no providers for this service
				System.out.println("[" + this.getName() + "]" + " No providers found for Addition Service (3+4)");
			}

			

			

			// ---------------------------------------------------------------------
			// ---------Searching for the Product service----------------------
			// ---------------------------------------------------------------------

			searchKeywords.clear();
			searchKeywords.add("multiplies");

			foundServices.clear();

			do {
				// Waiting for services
				try {
					Thread.sleep(2 * 1000);
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
				foundServices = sfProxy.searchService(searchInputs, searchOutputs, searchKeywords);

			} while (foundServices.isEmpty());

			// -----------------------------------------------------------------------------
			// --Requesting the execution of the Product service-------------
			// -----------------------------------------------------------------------------

			//get the first service found because it is the most suitable
			serviceOWLS = sfProxy.getService(foundServices.get(0).get(0));

			oracle = null;
			oracle = new Oracle(serviceOWLS);

			//get service inputs
			serviceInputs = oracle.getOwlsProfileInputs();

			agentInputs.clear();
			agentInputs = new HashMap<String, String>();
			for (String input : serviceInputs) {
				if (input.equalsIgnoreCase("x"))
					agentInputs.put(input, "5");
				else if (input.equalsIgnoreCase("y"))
					agentInputs.put(input, resultEquation);
				else
					agentInputs.put(input, "0");
			}
			
			//agents or organizations providers
			providers = oracle.getProviders();
			
			//web services providers
			providersGroundingWSDL = oracle.getProvidersGroundingWSDL();
			
			if(!providers.isEmpty()){
				System.out.println("[" + this.getName() + "]" + " Requesting Product Service (5*7)");
				
				ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
				msg.setReceiver(new AgentID(providers.get(0).getEntityID()));
				msg.setProtocol("fipa-request");
				msg.setSender(getAid());

				String content = st.buildServiceContent(oracle.getServiceName(), agentInputs);
				msg.setContent(content);

				this.send_request(msg);

				HashMap<String, String> outputs = new HashMap<String, String>();
				st.extractServiceContent(requestResult, outputs);
				resultEquation = outputs.get("Result");
			}
			else if(!providersGroundingWSDL.isEmpty()){
				System.out.println("[" + this.getName() + "]" + " Executing Product Service (5*7)");

				HashMap<String, Object> resultExecution = st.executeWebService(providersGroundingWSDL.get(0), agentInputs);

				Double resultDouble = (Double) resultExecution.get("Result");
				resultEquation = resultDouble.toString();
			}
			else{//no providers for this service
				System.out.println("[" + this.getName() + "]" + " No providers found for Product Service (5*7)");
			}

			

			// ---------------------------------------------------------------------
			// -------Searching for the Square service----------------------
			// ---------------------------------------------------------------------

			searchInputs.clear();
			searchInputs.add(ServiceTools.OntologicalTypesConstants.DOUBLE);
			searchKeywords.clear();
			searchKeywords.add("squares");

			foundServices.clear();

			do {
				// Waiting for services
				try {
					Thread.sleep(2 * 1000);
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
				foundServices = sfProxy.searchService(searchInputs, searchOutputs, searchKeywords);

			} while (foundServices.isEmpty());

			// ---------------------------------------------------
			// --------------Executing Square service ----------------
			// ---------------------------------------------------

			//get the first service found because it is the most suitable
			serviceOWLS = sfProxy.getService(foundServices.get(0).get(0));

			oracle = null;
			oracle = new Oracle(serviceOWLS);

			//get service inputs
			serviceInputs = oracle.getOwlsProfileInputs();

			agentInputs = new HashMap<String, String>();
			for (String input : serviceInputs) {
				agentInputs.put(input, resultEquation);
			}
			
			//agents or organizations providers
			providers = oracle.getProviders();
			//web services providers
			providersGroundingWSDL = oracle.getProvidersGroundingWSDL();
			
			if(!providers.isEmpty()){
				System.out.println("[" + this.getName() + "]" + " Requesting Square Service (35^2)");
				
				ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
				msg.setReceiver(new AgentID(providers.get(0).getEntityID()));
				msg.setProtocol("fipa-request");
				msg.setSender(getAid());

				String content = st.buildServiceContent(oracle.getServiceName(), agentInputs);
				msg.setContent(content);

				this.send_request(msg);

				HashMap<String, String> outputs = new HashMap<String, String>();
				st.extractServiceContent(requestResult, outputs);
				resultEquation = outputs.get("Result");
				
				
			}
			else if(!providersGroundingWSDL.isEmpty()){
				
				System.out.println("[" + this.getName() + "]" + " Executing Square Service (35^2)");

				HashMap<String, Object> resultExecution = st.executeWebService(providersGroundingWSDL.get(0), agentInputs);

				Double resultDouble = (Double) resultExecution.get("Result");
				resultEquation = resultDouble.toString();
				
			}
			else{//no providers for this service
				System.out.println("[" + this.getName() + "]" + " No providers found for Square Service (35^2)");
			}
			
			
			String finalResult= resultEquation;

			logger.info("\n\n[" + this.getName() + "] Final result: " + finalResult + "\n\n");
			System.out.println("\n\n[" + this.getName() + "] Final result: " + finalResult + "\n\n");

			// send a request to the InitiatorAgent to notify that the Example
			// is ended
			String finishContent = "<inform>" + "<content>" + "EXAMPLE ENDED" + "</content>" + "</inform>";
			ACLMessage msgFinish = new ACLMessage(ACLMessage.REQUEST);
			msgFinish.setHeader("EXAMPLEENDED", "EXAMPLEENDED");
			msgFinish.setReceiver(new AgentID("InitiatorAgent"));
			msgFinish.setProtocol("fipa-request");
			msgFinish.setSender(getAid());
			msgFinish.setContent(finishContent);

			this.send_request(msgFinish);

			myProcessor.ShutdownAgent();

		} catch (THOMASException e) {

			e.printStackTrace();
			message="ERROR";
		}

	}

	/**
	 * This method creates a new FIPA REQUEST protocol in order to communicate
	 * with the service provider.
	 * 
	 * @param msg
	 */
	private void send_request(ACLMessage msg) {
		CFactory talk = new myFIPA_REQUEST().newFactory("TALK", null, msg, 1, this, 0);

		// The factory is setup to answer start conversation requests from the
		// agent
		// using the REQUEST protocol.

		this.addFactoryAsInitiator(talk);

		// finally the new conversation starts. Because it is synchronous,
		// the current interaction halts until the new conversation ends.
		this.startSyncConversation("TALK");

		this.removeFactory("TALK");

	}

	/**
	 * This class implements the FIPA REQUEST protocol, the method doInform will
	 * be overloaded
	 * 
	 * @author joabelfa
	 * 
	 */
	class myFIPA_REQUEST extends FIPA_REQUEST_Initiator {
		protected void doInform(CProcessor myProcessor, ACLMessage msg) {
			System.out.println("[" + myProcessor.getMyAgent().getName() + "] " + msg.getSender().name
					+ " informs me \n" + msg.getContent());
			requestResult = msg.getContent();
		}

	}

	@Override
	protected void finalize(CProcessor firstProcessor, ACLMessage finalizeMessage) {
		message="OK";
	}

}
