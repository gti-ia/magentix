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
import es.upv.dsic.gti_ia.organization.SFProxy;
import es.upv.dsic.gti_ia.organization.ServiceTools;
import es.upv.dsic.gti_ia.organization.THOMASException;

public class James extends CAgent {

	OMSProxy omsProxy = new OMSProxy(this);
	SFProxy sfProxy = new SFProxy(this);
	ServiceTools st = new ServiceTools();
	
	


	public James(AgentID aid) throws Exception {
		super(aid);
	}

	protected void execution(CProcessor myProcessor, ACLMessage welcomeMessage) {

		try {


			omsProxy.acquireRole("student","school");

			ArrayList <String> search_inputs = new ArrayList <String>();
			ArrayList <String> outputs = new ArrayList <String>();
			ArrayList <String> keywords = new ArrayList <String>();

			search_inputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
			search_inputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");

			ArrayList<ArrayList<String>> resApp;
			
			
			//---------------------------------------------------------------------
			//---------------------Searching for the services----------------------
			//---------------------------------------------------------------------
			do{
				//Waiting for services
				try {
					Thread.sleep(5*1000);
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
				resApp = sfProxy.searchService(search_inputs, outputs, keywords);

			}while(resApp.size() != 3);


			//-----------------------------------------------------------------------------
			//-------------------Requesting the execution of the first service-------------
			//-----------------------------------------------------------------------------
			
			String service = sfProxy.getService(resApp.get(0).get(0));
			
			Oracle oracle = new Oracle(service);
			
			ArrayList<Provider> providers = oracle.getProviders();

			ArrayList<String> service_inputs = oracle.getInputs();

			HashMap<String,String> agent_inputs = new HashMap<String,String>();
			
			for(String input : service_inputs)
			{
				agent_inputs.put(input,"4");
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

			//-----------------------------------------------------------------------------
			//-------------------Requesting the execution of the second service-------------
			//-----------------------------------------------------------------------------


			service = sfProxy.getService(resApp.get(1).get(0));

			oracle = null;
			oracle = new Oracle(service);

			providers.clear();
			providers = oracle.getProviders();

			service_inputs.clear();
			service_inputs = oracle.getInputs();

			agent_inputs.clear();
			agent_inputs = new HashMap<String,String>();
			for(String input : service_inputs)
			{
				agent_inputs.put(input,"3");
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


			//---------------------------------------------------
			//--------------Executing service square--------------------
			//---------------------------------------------------


			service = sfProxy.getService(resApp.get(2).get(0));
			
			
			oracle = null;
			oracle = new Oracle(service);

			ArrayList<String> providersGrounding = oracle.getProvidersGroundingWSDL();

			service_inputs = oracle.getInputs();

			agent_inputs.clear();

			agent_inputs = new HashMap<String,String>();
			for(String input : service_inputs)
			{
				agent_inputs.put(input,"2");
			}


			HashMap<String,Object> result=st.executeWebService(providersGrounding.get(0), agent_inputs);

			Double resultContent=(Double)result.get("Result");

			System.out.println("Square service result: "+ resultContent);
			myProcessor.ShutdownAgent();




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
		}
	}



	@Override
	protected void finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {


	}

}
