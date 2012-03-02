package Thomas_example;


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
import es.upv.dsic.gti_ia.organization.ResponseParser;
import es.upv.dsic.gti_ia.organization.SFProxy;
import es.upv.dsic.gti_ia.organization.ServiceTools;
import es.upv.dsic.gti_ia.organization.THOMASException;

public class James extends CAgent {

	OMSProxy omsProxy = new OMSProxy(this);
	SFProxy sfProxy = new SFProxy(this);
	ServiceTools st = new ServiceTools();
	
	String requestResult="";


	public James(AgentID aid) throws Exception {
		super(aid);
	}

	protected void execution(CProcessor myProcessor, ACLMessage welcomeMessage) {

		try {

			//James has to resolve the equation (5 * (3 + 4))^2

			String result = omsProxy.acquireRole("student","school");
			logger.info("["+this.getName()+"] Result acquire role student: "+result);

			
			//---------------------------------------------------------------------
			//---------------------Searching for the Addition service----------------------
			//---------------------------------------------------------------------
			
			ArrayList <String> searchInputs = new ArrayList <String>();
			ArrayList <String> searchOutputs = new ArrayList <String>();
			ArrayList <String> searchKeywords = new ArrayList <String>();

			searchInputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
			searchInputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
			
			searchOutputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
			
			searchKeywords.add("addition");
			
			ArrayList<ArrayList<String>> foundServices;
			
			do{
				//Waiting for services
				try {
					Thread.sleep(2*1000);
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
				foundServices = sfProxy.searchService(searchInputs, searchOutputs, searchKeywords);

			}while(foundServices.isEmpty());


			//-----------------------------------------------------------------------------
			//-------------------Requesting the execution of the addition service-------------
			//-----------------------------------------------------------------------------
			
			String serviceOWLS = sfProxy.getService(foundServices.get(0).get(0));
			
			Oracle oracle = new Oracle(serviceOWLS);
			
			ArrayList<Provider> providers = oracle.getProviders();

			ArrayList<String> service_inputs = oracle.getInputs();

			HashMap<String,String> agent_inputs = new HashMap<String,String>();
			
			for(String input : service_inputs)
			{
				if(input.equalsIgnoreCase("x"))
					agent_inputs.put(input,"3");
				else if(input.equalsIgnoreCase("y"))
					agent_inputs.put(input,"4");
				else 
					agent_inputs.put(input,"0");
			}


			//Building the ACL message
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			msg.setReceiver(new AgentID(providers.get(0).getEntityID()));
			msg.setProtocol("fipa-request");
			msg.setSender(getAid());

			String content = st.buildServiceContent(oracle.getServiceName(), agent_inputs);

			//ACL message content is formed by XML format with service name and inputs
			msg.setContent(content);

			this.send_request(msg);
			
			
			ResponseParser rp=new ResponseParser();
			rp.parseResponse(requestResult);
			String resultEquation=rp.getKeyAndValueList().get("Result");

			//---------------------------------------------------------------------
			//---------------------Searching for the Product service----------------------
			//---------------------------------------------------------------------
			
			searchKeywords.clear();
			searchKeywords.add("multiplies");
			
			foundServices.clear();
			
			
			do{
				//Waiting for services
				try {
					Thread.sleep(2*1000);
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
				foundServices = sfProxy.searchService(searchInputs, searchOutputs, searchKeywords);

			}while(foundServices.isEmpty());
			
			//-----------------------------------------------------------------------------
			//-------------------Requesting the execution of the Product service-------------
			//-----------------------------------------------------------------------------


			serviceOWLS = sfProxy.getService(foundServices.get(0).get(0));

			oracle = null;
			oracle = new Oracle(serviceOWLS);

			providers.clear();
			providers = oracle.getProviders();

			service_inputs.clear();
			service_inputs = oracle.getInputs();

			agent_inputs.clear();
			agent_inputs = new HashMap<String,String>();
			for(String input : service_inputs)
			{
				if(input.equalsIgnoreCase("x"))
					agent_inputs.put(input,"5");
				else if(input.equalsIgnoreCase("y"))
					agent_inputs.put(input,resultEquation);
				else 
					agent_inputs.put(input,"0");
			}


			msg=null;

			msg = new ACLMessage(ACLMessage.REQUEST);
			msg.setReceiver(new AgentID(providers.get(0).getEntityID()));
			msg.setProtocol("fipa-request");
			msg.setSender(getAid());

			content = "";
			content = st.buildServiceContent(oracle.getServiceName(), agent_inputs);
			msg.setContent(content);
			
			this.send_request(msg);

			
			rp=new ResponseParser();
			rp.parseResponse(requestResult);
			resultEquation=rp.getKeyAndValueList().get("Result");
			
			//---------------------------------------------------------------------
			//---------------------Searching for the Square service----------------------
			//---------------------------------------------------------------------
			
			searchInputs.clear();
			searchInputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
			searchKeywords.clear();
			searchKeywords.add("squares");
			
			foundServices.clear();
			
			do{
				//Waiting for services
				try {
					Thread.sleep(2*1000);
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
				foundServices = sfProxy.searchService(searchInputs, searchOutputs, searchKeywords);

			}while(foundServices.isEmpty());
			
			
			//---------------------------------------------------
			//--------------Executing service square--------------------
			//---------------------------------------------------


			serviceOWLS = sfProxy.getService(foundServices.get(0).get(0));
			
			
			oracle = null;
			oracle = new Oracle(serviceOWLS);

			ArrayList<String> providersGrounding = oracle.getProvidersGroundingWSDL();

			service_inputs = oracle.getInputs();

			agent_inputs.clear();

			agent_inputs = new HashMap<String,String>();
			for(String input : service_inputs)
			{
				agent_inputs.put(input,resultEquation);
			}


			HashMap<String,Object> resultExecution=st.executeWebService(providersGrounding.get(0), agent_inputs);

			Double resultContent=(Double)resultExecution.get("Result");

			logger.info("\n\n["+this.getName()+"] Final result: "+resultContent+"\n\n");
			
			
			
			String finishContent="<inform>"+
					"<content>"+"EXAMPLE ENDED"+"</content>"+
					"</inform>";
			ACLMessage msgFinish=new ACLMessage(ACLMessage.REQUEST);
			msgFinish.setHeader("EXAMPLEENDED", "EXAMPLEENDED");
			msgFinish.setReceiver(new AgentID("InitiatorAgent"));
			msgFinish.setProtocol("fipa-request");
			msgFinish.setSender(getAid());
			msgFinish.setContent(finishContent);
			
			this.send_request(msgFinish);
			
			
		} catch (THOMASException e) {

			e.printStackTrace();
		}


	}

	/**
	 * This method creates a new FIPA REQUEST protocol in order to communicate with the 
	 * service provider.
	 * @param msg
	 */
	private void send_request(ACLMessage msg)
	{
		CFactory talk = new myFIPA_REQUEST().newFactory("TALK", null , msg,1, this, 0);

		// The factory is setup to answer start conversation requests from the agent
		// using the REQUEST protocol.

		this.addFactoryAsInitiator(talk);

		// finally the new conversation starts. Because it is synchronous, 
		// the current interaction halts until the new conversation ends.
		//myProcessor.createSyncConversation(msg);
		this.startSyncConversation("TALK");

		this.removeFactory("TALK");


	}


	/**
	 * This class implements the FIPA REQUEST protocol, the method doInform will be overloaded
	 * @author joabelfa
	 *
	 */
	class myFIPA_REQUEST extends FIPA_REQUEST_Initiator {
		protected void doInform(CProcessor myProcessor, ACLMessage msg) {
			System.out.println(myProcessor.getMyAgent().getName() + ": "
					+ msg.getSender().name + " informs me \n"
					+ msg.getContent());
			requestResult=msg.getContent();
		}
		
//		@Override
//		protected void doFinal(CProcessor myProcessor, ACLMessage messageToSend){
//			ACLMessage msg=myProcessor.getLastReceivedMessage();
//			if(msg.getHeaderValue("EXAMPLEENDED")!=null)
//				myProcessor.ShutdownAgent();
//			else
//				messageToSend=myProcessor.getLastSentMessage();
//		}
	}



	@Override
	protected void finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {


	}

}
