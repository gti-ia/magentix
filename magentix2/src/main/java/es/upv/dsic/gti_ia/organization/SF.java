package es.upv.dsic.gti_ia.organization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

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
	private String SFServiceDescriptionLocation = configuration.getSFServiceDescriptionLocation();

	private static HashMap<String, String> sfServicesURLs=new HashMap<String, String>();
	static Logger logger = Logger.getLogger(SF.class);
	
	String separatorToken=" ";

	// Debug
	//private final Boolean DEBUG = true;



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
	public void setSFServiceDescriptionLocation(String SFUrl) {
		this.SFServiceDescriptionLocation = SFUrl;
	}

	/**
	 * get the URL where the owl's document is
	 * located.
	 * @param SFUrl
	 */
	public String getSFServiceDescriptionLocation() {
		return this.SFServiceDescriptionLocation;
	}

	
//	private static String executeWithJavaX(String inputParams){
//
//		//http://localhost:8080/omsservices/OMSservices/owl/owls/AcquireRole.owl RoleID=miembro2 UnitID=plana2
//		//
//		StringTokenizer tokenInputParams = new StringTokenizer(inputParams, separatorToken);
//		String serviceURL=sfServicesURLs.get(tokenInputParams.nextToken().trim());
//		Oracle oracle = new Oracle();
//		oracle.setURLProcess(serviceURL);
//
//		ArrayList<String> processInputs=oracle.getProcessInputs();
//
//		HashMap<String,String> paramsComplete=new HashMap<String, String>();
//		Iterator<String> iterProcessInputs=processInputs.iterator();
//		while(iterProcessInputs.hasNext()){
//			String in=iterProcessInputs.next().toLowerCase();
//			//initialize the inputs
//			paramsComplete.put(in, "");
//		}
//
//
//		while(tokenInputParams.hasMoreTokens()){
//			String inputToken=tokenInputParams.nextToken().trim();
//			StringTokenizer anInputToken=new StringTokenizer(inputToken, "=");
//			String in=anInputToken.nextToken().toLowerCase().trim();
//			String value="";
//			if(anInputToken.hasMoreTokens())
//				value=anInputToken.nextToken().trim();
//			if(paramsComplete.get(in)!=null){
//				paramsComplete.put(in, value);
//				System.out.println("inputParamName: "+in+" value: "+value);
//			}
//		}
//
//
//		//construct params list with the value of the parameters ordered...
//		ArrayList<String> params = new ArrayList<String>();
//		Iterator<String> iterInputs=processInputs.iterator();
//		while(iterInputs.hasNext()){
//			String input=iterInputs.next().toLowerCase();
//			params.add(paramsComplete.get(input));
//			//System.out.println("inputParamValue: "+paramsComplete.get(input));
//		}
//
//		ServiceClient serviceClient = new ServiceClient();
//		ArrayList<String> results = serviceClient.invoke(serviceURL, params);
//
//		//String process_localName="SearchServiceProcess"; //TODO no estic segur si es això...
//		//String resultStr=process_localName+ "=" + "{";
//		String resultStr=serviceURL+"=" + "{";
//		for(int i=0;i<results.size();i++){
//			resultStr+=serviceURL+"#"+results.get(i);
//			if(i!=results.size()-1){
//				resultStr+=", ";
//			}
//			else{
//				resultStr+=" }";
//			}
//		}
//
//
//		return resultStr;
//	}

	private String executeWithJavaX(String inputParams){

		//http://localhost:8080/omsservices/OMSservices/owl/owls/AcquireRole.owl RoleID=miembro2 UnitID=plana2
		//
		StringTokenizer tokenInputParams = new StringTokenizer(inputParams, separatorToken);
		String serviceURL=tokenInputParams.nextToken().trim();
		//String serviceURL=sfServicesURLs.get(tokenInputParams.nextToken().trim());
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



					String resultStr=executeWithJavaX(myProcessor.getLastReceivedMessage().getContent());


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
						StringTokenizer msgContentTok = new StringTokenizer(msg.getContent(), separatorToken);

						String serviceName = msgContentTok.nextToken().trim();

						logger.info("[SF]Doc OWL-S: " + serviceName);


						if (sfServicesURLs.containsValue(serviceName)) //if (sfServicesURLs.containsKey(serviceName))
						{

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

