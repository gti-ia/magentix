package es.upv.dsic.gti_ia.organization;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import ThomasNOMindswap.Oracle;
import ThomasNOMindswap.ServiceClient;
import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CFactory;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_REQUEST_Participant;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;


/**
 * SF agent is responsible for managing all the request messages from other
 * entities SF agent follows a FIPA-Request protocol
 */
public class SF extends CAgent {

	Configuration configuration = Configuration.getConfiguration();


	private static SF sf = null;
	private String SFServiceDesciptionLocation = configuration.getSFServiceDesciptionLocation();

	private static HashMap<String, String> sfServicesURLs=new HashMap<String, String>();
	static Logger logger = Logger.getLogger(SF.class);
	// create a kb
//	OWLKnowledgeBase kb = OWLFactory.createKB();
//	OWLKnowledgeBase kbaux = OWLFactory.createKB();

	// Debug
	//private final Boolean DEBUG = true;

	private final URI OWL_S_SF_SERVICES = URI
	.create(SFServiceDesciptionLocation);

	// URI of each SF services description parameters are located
	private final URI SF_ADDPROVIDER_PROFILE = URI.create(OWL_S_SF_SERVICES
			+ "AddProviderProfile.owl");
	private final URI SF_ADDPROVIDER_PROCESS = URI.create(OWL_S_SF_SERVICES
			+ "AddProviderProcess.owl");
	private final URI SF_ADDPROVIDER_GROUNDING = URI.create(OWL_S_SF_SERVICES
			+ "AddProviderGrounding.owl");
	private final URI SF_ADDPROVIDER_ID = URI.create(OWL_S_SF_SERVICES
			+ "AddProviderProfile.owl#AddProviderProfile");
	private final URI SF_ADDPROVIDER_GOAL = URI.create("AddProvider");
	private final URI SF_ADDPROVIDER_PROVIDER = URI.create("Provider");

	private final URI SF_REMOVEPROVIDER_PROFILE = URI.create(OWL_S_SF_SERVICES
			+ "RemoveProviderProfile.owl");
	private final URI SF_REMOVEPROVIDER_PROCESS = URI.create(OWL_S_SF_SERVICES
			+ "RemoveProviderProcess.owl");
	private final URI SF_REMOVEPROVIDER_GROUNDING = URI
	.create(OWL_S_SF_SERVICES + "RemoveProviderGrounding.owl");
	private final URI SF_REMOVEPROVIDER_ID = URI.create(OWL_S_SF_SERVICES
			+ "RemoveProviderProfile.owl#RemoveProviderProfile");
	private final URI SF_REMOVEPROVIDER_GOAL = URI.create("RemoveProvider");
	private final URI SF_REMOVEPROVIDER_PROVIDER = URI.create("Provider");

	private final URI SF_REGISTERPROFILE_PROFILE = URI.create(OWL_S_SF_SERVICES
			+ "RegisterProfileProfile.owl");
	private final URI SF_REGISTERPROFILE_PROCESS = URI.create(OWL_S_SF_SERVICES
			+ "RegisterProfileProcess.owl");
	private final URI SF_REGISTERPROFILE_GROUNDING = URI
	.create(OWL_S_SF_SERVICES + "RegisterProfileGrounding.owl");
	private final URI SF_REGISTERPROFILE_ID = URI.create(OWL_S_SF_SERVICES
			+ "RegisterProfileProfile.owl#RegisterProfileProfile");
	private final URI SF_REGISTERPROFILE_GOAL = URI.create("RegisterProfile");
	private final URI SF_REGISTERPROFILE_PROVIDER = URI.create("Provider");

	private final URI SF_DEREGISTERPROFILE_PROFILE = URI
	.create(OWL_S_SF_SERVICES + "DeregisterProfileProfile.owl");
	private final URI SF_DEREGISTERPROFILE_PROCESS = URI
	.create(OWL_S_SF_SERVICES + "DeregisterProfileProcess.owl");
	private final URI SF_DEREGISTERPROFILE_GROUNDING = URI
	.create(OWL_S_SF_SERVICES + "DeregisterProfileGrounding.owl");
	private final URI SF_DEREGISTERPROFILE_ID = URI.create(OWL_S_SF_SERVICES
			+ "DeregisterProfileProfile.owl#DeregisterProfileProfile");
	private final URI SF_DEREGISTERPROFILE_GOAL = URI
	.create("DeregisterProfile");
	private final URI SF_DEREGISTERPROFILE_PROVIDER = URI.create("Provider");

	private final URI SF_REGISTERPROCESS_PROFILE = URI.create(OWL_S_SF_SERVICES
			+ "RegisterProcessProfile.owl");
	private final URI SF_REGISTERPROCESS_PROCESS = URI.create(OWL_S_SF_SERVICES
			+ "RegisterProcessProcess.owl");
	private final URI SF_REGISTERPROCESS_GROUNDING = URI
	.create(OWL_S_SF_SERVICES + "RegisterProcessGrounding.owl");
	private final URI SF_REGISTERPROCESS_ID = URI.create(OWL_S_SF_SERVICES
			+ "RegisterProcessProfile.owl#RegisterProcessProfile");
	private final URI SF_REGISTERPROCESS_GOAL = URI.create("RegisterProcess");
	private final URI SF_REGISTERPROCESS_PROVIDER = URI.create("Provider");

	private final URI SF_GETPROCESS_PROFILE = URI.create(OWL_S_SF_SERVICES
			+ "GetProcessProfile.owl");
	private final URI SF_GETPROCESS_PROCESS = URI.create(OWL_S_SF_SERVICES
			+ "GetProcessProcess.owl");
	private final URI SF_GETPROCESS_GROUNDING = URI.create(OWL_S_SF_SERVICES
			+ "GetProcessGrounding.owl");
	private final URI SF_GETPROCESS_ID = URI.create(OWL_S_SF_SERVICES
			+ "GetProcessProfile.owl#GetProcessProfile");
	private final URI SF_GETPROCESS_GOAL = URI.create("GetProcess");
	private final URI SF_GETPROCESS_PROVIDER = URI.create("Provider");

	private final URI SF_GETPROFILE_PROFILE = URI.create(OWL_S_SF_SERVICES
			+ "GetProfileProfile.owl");
	private final URI SF_GETPROFILE_PROCESS = URI.create(OWL_S_SF_SERVICES
			+ "GetProfileProcess.owl");
	private final URI SF_GETPROFILE_GROUNDING = URI.create(OWL_S_SF_SERVICES
			+ "GetProfileGrounding.owl");
	private final URI SF_GETPROFILE_ID = URI.create(OWL_S_SF_SERVICES
			+ "GetProfileProfile.owl#GetProfileProfile.owl");
	private final URI SF_GETPROFILE_GOAL = URI.create("GetProfile");
	private final URI SF_GETPROFILE_PROVIDER = URI.create("Provider");

	private final URI SF_MODIFYPROCESS_PROFILE = URI.create(OWL_S_SF_SERVICES
			+ "ModifyProcessProfile.owl");
	private final URI SF_MODIFYPROCESS_PROCESS = URI.create(OWL_S_SF_SERVICES
			+ "ModifyProcessProcess.owl");
	private final URI SF_MODIFYPROCESS_GROUNDING = URI.create(OWL_S_SF_SERVICES
			+ "ModifyProcessGrounding.owl");
	private final URI SF_MODIFYPROCESS_ID = URI.create(OWL_S_SF_SERVICES
			+ "ModifyProcessProfile.owl#ModifyProcessProfile.owl");
	private final URI SF_MODIFYPROCESS_GOAL = URI.create("ModifyProcess");
	private final URI SF_MODIFYPROCESS_PROVIDER = URI.create("Provider");

	private final URI SF_MODIFYPROFILE_PROFILE = URI.create(OWL_S_SF_SERVICES
			+ "ModifyProfileProfile.owl");
	private final URI SF_MODIFYPROFILE_PROCESS = URI.create(OWL_S_SF_SERVICES
			+ "ModifyProfileProcess.owl");
	private final URI SF_MODIFYPROFILE_GROUNDING = URI.create(OWL_S_SF_SERVICES
			+ "ModifyProfileGrounding.owl");
	private final URI SF_MODIFYPROFILE_ID = URI.create(OWL_S_SF_SERVICES
			+ "ModifyProfileProfile.owl#ModifyProfileProfile");
	private final URI SF_MODIFYPROFILE_GOAL = URI.create("ModifyProfile");
	private final URI SF_MODIFYPROFILE_PROVIDER = URI.create("Provider");

	private final URI SF_SEARCHSERVICE_PROFILE = URI.create(OWL_S_SF_SERVICES
			+ "SearchServiceProfile.owl");
	private final URI SF_SEARCHSERVICE_PROCESS = URI.create(OWL_S_SF_SERVICES
			+ "SearchServiceProcess.owl");
	private final URI SF_SEARCHSERVICE_GROUNDING = URI.create(OWL_S_SF_SERVICES
			+ "SearchServiceGrounding.owl");
	private final URI SF_SEARCHSERVICE_ID = URI.create(OWL_S_SF_SERVICES
			+ "SearchServiceProfile.owl#SearchServiceProfile");
	private final URI SF_SEARCHSERVICE_GOAL = URI.create("SearchService");
	private final URI SF_SEARCHSERVICE_PROVIDER = URI.create("Provider");

	// array with the SF processes related with registration
	URI[] SFInitServices = { SF_REGISTERPROFILE_PROCESS,
			SF_REGISTERPROCESS_PROCESS };
	// array with SF service profiles
	URI[] SFServicesProfiles = { SF_ADDPROVIDER_PROFILE,
			SF_REMOVEPROVIDER_PROFILE, SF_REGISTERPROFILE_PROFILE,
			SF_DEREGISTERPROFILE_PROFILE, SF_REGISTERPROCESS_PROFILE,
			SF_GETPROCESS_PROFILE, SF_GETPROFILE_PROFILE,
			SF_MODIFYPROCESS_PROFILE, SF_MODIFYPROFILE_PROFILE,
			SF_SEARCHSERVICE_PROFILE };
	// array with SF service goals
	URI[] SFServicesGoals = { SF_ADDPROVIDER_GOAL, SF_REMOVEPROVIDER_GOAL,
			SF_REGISTERPROFILE_GOAL, SF_DEREGISTERPROFILE_GOAL,
			SF_REGISTERPROCESS_GOAL, SF_GETPROCESS_GOAL, SF_GETPROFILE_GOAL,
			SF_MODIFYPROCESS_GOAL, SF_MODIFYPROFILE_GOAL, SF_SEARCHSERVICE_GOAL };
	// array with SF service ID
	URI[] SFServicesIDs = { SF_ADDPROVIDER_ID, SF_REMOVEPROVIDER_ID,
			SF_REGISTERPROFILE_ID, SF_DEREGISTERPROFILE_ID,
			SF_REGISTERPROCESS_ID, SF_GETPROCESS_ID, SF_GETPROFILE_ID,
			SF_MODIFYPROCESS_ID, SF_MODIFYPROFILE_ID, SF_SEARCHSERVICE_ID };
	// array with SF service processes
	URI[] SFServicesProcess = { SF_ADDPROVIDER_PROCESS,
			SF_REMOVEPROVIDER_PROCESS, SF_REGISTERPROFILE_PROCESS,
			SF_DEREGISTERPROFILE_PROCESS, SF_REGISTERPROCESS_PROCESS,
			SF_GETPROCESS_PROCESS, SF_GETPROFILE_PROCESS,
			SF_MODIFYPROCESS_PROCESS, SF_MODIFYPROFILE_PROCESS,
			SF_SEARCHSERVICE_PROCESS };
	// array with SF service grounding
	URI[] SFServicesGrounding = { SF_ADDPROVIDER_GROUNDING,
			SF_REMOVEPROVIDER_GROUNDING, SF_REGISTERPROFILE_GROUNDING,
			SF_DEREGISTERPROFILE_GROUNDING, SF_REGISTERPROCESS_GROUNDING,
			SF_GETPROCESS_GROUNDING, SF_GETPROFILE_GROUNDING,
			SF_MODIFYPROCESS_GROUNDING, SF_MODIFYPROFILE_GROUNDING,
			SF_SEARCHSERVICE_GROUNDING };
	// array with SF service provider IDs
	URI[] SFServicesProviderID = { SF_ADDPROVIDER_PROVIDER,
			SF_REMOVEPROVIDER_PROVIDER, SF_REGISTERPROFILE_PROVIDER,
			SF_DEREGISTERPROFILE_PROVIDER, SF_REGISTERPROCESS_PROVIDER,
			SF_GETPROCESS_PROVIDER, SF_GETPROFILE_PROVIDER,
			SF_MODIFYPROCESS_PROVIDER, SF_MODIFYPROFILE_PROVIDER,
			SF_SEARCHSERVICE_PROVIDER };




	/**
	 * Returns an instance of the agents SF
	 * @param agent
	 * @return sf
	 */
	static public SF getSF(AgentID agent)
	{
		if (sf == null)
			try
		{
				sf = new SF(agent);
		}catch(Exception e){logger.error(e);}
		return sf;		
	}

	/**
	 *  Returns an instance of the agents SF
	 * @return sf
	 */
	static public SF getSF()
	{
		if (sf == null)
			try
		{
				sf = new SF(new AgentID("SF"));
		}catch(Exception e){logger.error(e);}
		return sf;


	}


	/**
	 * Initial registration of the SF service profiles
	 * 
	 * @param
	 * @throws RuntimeException
	 */

	private SF(AgentID aid) throws Exception {

		super(aid);
		
		//TODO get from some configuration file...
		
		sfServicesURLs.put("RegisterService", "http://localhost:8080/sfservices/SFservices/owl/owls/RegisterService.owl");
		sfServicesURLs.put("DeregisterService", "http://localhost:8080/sfservices/SFservices/owl/owls/DeregisterService.owl");
		sfServicesURLs.put("GetService", "http://localhost:8080/sfservices/SFservices/owl/owls/GetService.owl");
		sfServicesURLs.put("SearchService", "http://localhost:8080/sfservices/SFservices/owl/owls/SearchService.owl");
		sfServicesURLs.put("RemoveProvider", "http://localhost:8080/sfservices/SFservices/owl/owls/RemoveProvider.owl");

	}

	/**
	 * Change the URL where the owl's document is
	 * located.
	 * @param SFUrl
	 */
	public void setSFServiceDesciptionLocation(String SFUrl) {
		this.SFServiceDesciptionLocation = SFUrl;
	}

	/**
	 * get the URL where the owl's document is
	 * located.
	 * @param SFUrl
	 */
	public String getSFServiceDesciptionLocation() {
		return this.SFServiceDesciptionLocation;
	}

	/**
	 * Initial registration of the SF service profile
	 */
	public void RegisterSFServiceProfiles() {

		try {

			// REGISTER SF SERVICES PROFILES
			ThomasNOMindswap.Oracle oracle = new Oracle();
			oracle.setURLProcess(SF_REGISTERPROCESS_PROCESS.toString());
		
			for (int k = 0; k < SFServicesProfiles.length; k++) {
				//construct params list with the value of the parameters ordered...
				ArrayList<String> params = new ArrayList<String>();
				params.add(this.getName());
				params.add(SFServicesProfiles[k].toString());
				params.add(SFServicesGoals[k].toString());
				
				logger.info("[SF]Executing... ");
				
				ServiceClient serviceClient = new ServiceClient();
			    ArrayList<String> results = serviceClient.invoke(SF_REGISTERPROCESS_PROCESS.toString(), params);
			    
			    Iterator<String> iterRes=results.iterator();
			    String resStr="";
			    while(iterRes.hasNext()){
			    	String res=iterRes.next();
			    	resStr+=res+" ";
			    }
			    
				logger.info("[SF]Values obtained... :" + resStr);

			}// for k
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}// end RegisterSFServiceProfiles

	private static String executeWithJavaX(String inputParams){
		
		StringTokenizer tokenInputParams = new StringTokenizer(inputParams, "--");
		String serviceURL=sfServicesURLs.get(tokenInputParams.nextToken().trim());
		Oracle oracle = new Oracle();
		oracle.setURLProcess(serviceURL);
		
		ArrayList<String> processInputs=oracle.getProcessInputs();
		
		HashMap<String,String> paramsComplete=new HashMap<String, String>();
		Iterator<String> iterProcessInputs=processInputs.iterator();
		while(iterProcessInputs.hasNext()){
			String in=iterProcessInputs.next().toLowerCase();
			//initialize the inputs
			paramsComplete.put(in, "");
		}
		
		
		while(tokenInputParams.hasMoreTokens()){
			String inputToken=tokenInputParams.nextToken().trim();
			StringTokenizer anInputToken=new StringTokenizer(inputToken, "=");
			String in=anInputToken.nextToken().toLowerCase().trim();
			String value="";
			if(anInputToken.hasMoreTokens())
				value=anInputToken.nextToken().trim();
			if(paramsComplete.get(in)!=null){
				paramsComplete.put(in, value);
				System.out.println("inputParamName: "+in+" value: "+value);
			}
		}
		
		
		//construct params list with the value of the parameters ordered...
		ArrayList<String> params = new ArrayList<String>();
		Iterator<String> iterInputs=processInputs.iterator();
		while(iterInputs.hasNext()){
			String input=iterInputs.next().toLowerCase();
			params.add(paramsComplete.get(input));
			//System.out.println("inputParamValue: "+paramsComplete.get(input));
		}
		
		ServiceClient serviceClient = new ServiceClient();
	    ArrayList<String> results = serviceClient.invoke(serviceURL, params);
	    
	    //String process_localName="SearchServiceProcess"; //TODO no estic segur si es això...
	    //String resultStr=process_localName+ "=" + "{";
		String resultStr=serviceURL+"=" + "{";
		for(int i=0;i<results.size();i++){
			resultStr+=serviceURL+"#"+results.get(i);
			if(i!=results.size()-1){
				resultStr+=", ";
			}
			else{
				resultStr+=" }";
			}
		}
		
		
		return resultStr;
	}
	
	@Override
	protected void finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {
	}

	@Override
	protected void execution(CProcessor firstProcessor,
			ACLMessage welcomeMessage) {

		class myFIPA_REQUEST extends FIPA_REQUEST_Participant {

			@Override
			protected String doAction(CProcessor myProcessor) {
				String next = "";

				try{
				//TODO bad use of constructor
//				ProcessDescription processDescription= new ProcessDescription("", "");
//				String processURL=processDescription.getProcessURL(myProcessor.getLastReceivedMessage());
//				//extract process' local name
//				String process_localName = processDescription.getProcessLocalName(myProcessor.getLastReceivedMessage());
//				
//				logger.info("[SF]Doc OWL-S: " + processURL);
				
				
				/**
				StringTokenizer Tok = new StringTokenizer(myProcessor.getLastReceivedMessage().getContent());
				Tok.nextElement().toString();
				
				Map<String,String> paramsComplete=new HashMap<String, String>();
				while(Tok.hasMoreElements()){
					String paramComplete=Tok.nextElement().toString();
					String param=paramComplete.split("=")[0].toLowerCase();
					String value="";
					if(paramComplete.split("=").length>1)
						value=paramComplete.split("=")[1];
					paramsComplete.put(param, value);
					//System.out.println("[Provider]Value: " + param);
				}
				**/
				
				String resultStr=executeWithJavaX(myProcessor.getLastReceivedMessage().getContent());
				
				/*****
				
				ThomasNOMindswap.Oracle oracle = new Oracle();
				oracle.setURLProcess(processURL);
				
				ArrayList<String> processInputs=oracle.getProcessInputs();
				
				//construct params list with the value of the parameters ordered...
				ArrayList<String> params = new ArrayList<String>();
				Iterator<String> iterInputs=processInputs.iterator();
				while(iterInputs.hasNext()){
					String input=iterInputs.next().toLowerCase();
					if(input.contains("agentid"))
						params.add(myProcessor.getLastReceivedMessage().getSender().name.replace('~', '@'));
					else 
						params.add(paramsComplete.get(input));
				}
				
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
				
				****/
				
				
//				// create an execution engine
//				ProcessExecutionEngine exec = OWLSFactory.createExecutionEngine();
//
//				// read msg content
//				StringTokenizer Tok = new StringTokenizer(myProcessor.getLastReceivedMessage().getContent());
//
//				// read in the service description
//				String token_process = Tok.nextElement().toString();
//
//				logger.info("[SF]Doc OWL-S: " + token_process);
////
////				try {
//					Service aService = kb.readService(token_process);
//
//					// get the process for the server
//					Process aProcess = aService.getProcess();
//					// initialize the input values to be empty
//					ValueMap values = new ValueMap();
//
//					// get the input values
//					for (int i = 0; i < aProcess.getInputs().size(); i++)
//						values.setValue(aProcess.getInputs().inputAt(i),
//								EntityFactory.createDataValue(""));
//					while (Tok.hasMoreElements()) {
//						String token = Tok.nextElement().toString();
//						for (int i = 0; i < aProcess.getInputs().size(); i++) {
//							String paramName = aProcess.getInputs().inputAt(i).getLocalName().toLowerCase();
//							if (paramName.equalsIgnoreCase(token.split("=")[0].toLowerCase())) {
//								if (token.split("=").length >= 2)
//									values.setValue(aProcess.getInputs().inputAt(i),EntityFactory.createDataValue(token.split("=")[1]));
//								else
//									values.setValue(aProcess.getInputs().inputAt(i),EntityFactory.createDataValue(""));
//							}
//							if (aProcess.getInputs().inputAt(i).toString().contains("AgentID")) {
//								values.setValue(aProcess.getInputs().inputAt(i),EntityFactory.createDataValue(
//										myProcessor.getLastReceivedMessage().getSender().name.replace('~', '@')));
//							}
//						}
//					}// end while

					// execute the service
//					logger.info("[SF]Executing... "+ values.getValues().toString());
//					values = exec.execute(aProcess, values);

					logger.info("[SF]Values obtained... ");

					logger.info("[SF]Creating inform message to send...");

					next = "INFORM";

					logger.info("[SF]Before set message content...");
//					myProcessor.getLastReceivedMessage().setContent(aProcess.getLocalName() + "="
//							+ values.toString());
					myProcessor.getLastReceivedMessage().setContent(resultStr);

				} catch (Exception e) {
					next = "FAILURE";
				}

				return next;
			}

			@Override
			protected void doInform(CProcessor myProcessor, ACLMessage response) {
				ACLMessage lastReceivedMessage = myProcessor.getLastReceivedMessage();
				response.setContent(lastReceivedMessage.getContent());				
			}

			@Override
			protected String doReceiveRequest(CProcessor myProcessor,
					ACLMessage request) {
				String next = "";
				ACLMessage msg = request;

				if (msg != null) {

					try {

						// read msg content
						StringTokenizer Tok = new StringTokenizer(msg.getContent());

						// read in the service description
						String token_process = Tok.nextElement().toString();

						logger.info("[SF]Doc OWL-S: " + token_process);
						//Service aService = kb.readService(token_process);

						// get the process for the server
						//Process aProcess = aService.getProcess();

						// System.out.println("resultado de la comparacion
						// "+token_process.equals(SFServicesProcess[4].toString()));
						if (token_process.equals(SFServicesProcess[0].toString())
								|| token_process.equals(SFServicesProcess[1].toString())
								|| token_process.equals(SFServicesProcess[2].toString())
								|| token_process.equals(SFServicesProcess[3].toString())
								|| token_process.equals(SFServicesProcess[4].toString())
								|| token_process.equals(SFServicesProcess[5].toString())
								|| token_process.equals(SFServicesProcess[6].toString())
								|| token_process.equals(SFServicesProcess[7].toString())
								|| token_process.equals(SFServicesProcess[8].toString())
								|| token_process.equals(SFServicesProcess[9].toString())) {

							logger.info("AGREE");
							next = "AGREE";

						} else {

							logger.info("REFUSE");
							next = "REFUSE";
						}

					} catch (Exception e) {

						logger.info("EXCEPTION");
						System.out.println(e);
						e.printStackTrace();
						throw new RuntimeException(e.getMessage());

					}

				} else {

					logger.info("NOTUNDERSTOOD");
					next = "NOT_UNDERSTOOD";
				}

				logger.info("[SF]Sending First message:" + next);

				return next;
			}
		}

		CFactory talk = new myFIPA_REQUEST().newFactory("TALK", null,
				1, firstProcessor.getMyAgent());

		// Finally the factory is setup to answer to incoming messages that
		// can start the participation of the agent in a new conversation
		this.addFactoryAsParticipant(talk);

	}

} // end SF Agent

