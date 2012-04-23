package es.upv.dsic.gti_ia.organization;

import java.util.ArrayList;
import java.util.HashMap;
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
 * entities. It follows a FIPA-Request protocol
 */
public class SF extends CAgent {

	Configuration configuration = Configuration.getConfiguration();

	private static SF sf = null;
	private String SFServiceDescriptionLocation = configuration.getSFServiceDescriptionLocation();

	private static HashMap<String, Integer> sfServicesURLs = new HashMap<String, Integer>();
	static Logger logger = Logger.getLogger(SF.class);

	ServiceTools st = new ServiceTools();
	SFinterface sfInterface=new SFinterface();

	/**
	 * Returns an instance of the agents SF
	 * 
	 * @param agent
	 * @return sf
	 */
	static public SF getSF(AgentID agent) {
		if (sf == null)
			try {
				sf = new SF(agent);
			} catch (Exception e) {
				logger.error(e);
			}
			return sf;
	}

	/**
	 * Returns an instance of the agents SF
	 * 
	 * @return sf
	 */
	static public SF getSF() {
		if (sf == null)
			try {
				sf = new SF(new AgentID("SF"));
			} catch (Exception e) {
				logger.error(e);
			}
			return sf;

	}

	/**
	 * Initial registration of the SF service profiles
	 * 
	 * @param aid
	 * @throws RuntimeException
	 */

	private SF(AgentID aid) throws Exception {

		super(aid);

		//		sfServicesURLs.put("RegisterService", SFServiceDescriptionLocation + "RegisterService?wsdl");
		//		sfServicesURLs.put("DeregisterService", SFServiceDescriptionLocation + "DeregisterService?wsdl");
		//		sfServicesURLs.put("GetService", SFServiceDescriptionLocation + "GetService?wsdl");
		//		sfServicesURLs.put("SearchService", SFServiceDescriptionLocation + "SearchService?wsdl");
		//		sfServicesURLs.put("RemoveProvider", SFServiceDescriptionLocation + "RemoveProvider?wsdl");


		sfServicesURLs.put("RegisterService", 1);
		sfServicesURLs.put("DeregisterService",2);
		sfServicesURLs.put("GetService", 3);
		sfServicesURLs.put("SearchService", 4);
		sfServicesURLs.put("RemoveProvider", 5);
	}

	/**
	 * Change the URL where the OWL-S document is located.
	 * 
	 * @param SFUrl
	 */
	public void setSFServiceDescriptionLocation(String SFUrl) {
		this.SFServiceDescriptionLocation = SFUrl;
	}

	/**
	 * get the URL where the OWL-S document is located.
	 * 
	 * @param SFUrl
	 */
	public String getSFServiceDescriptionLocation() {
		return this.SFServiceDescriptionLocation;
	}

	@Override
	protected void finalize(CProcessor firstProcessor, ACLMessage finalizeMessage) {
	}

	@Override
	protected void execution(CProcessor firstProcessor, ACLMessage welcomeMessage) {

		class myFIPA_REQUEST extends FIPA_REQUEST_Participant {

			@Override
			protected String doAction(CProcessor myProcessor) {
				String next = "";

				try {

					HashMap<String, String> inputs = new HashMap<String, String>();

					// Extract the service name and inputs of the request
					String serviceName = st.extractServiceContent(myProcessor.getLastReceivedMessage().getContent(),
							inputs);

					String resultContent = "";
					switch(sfServicesURLs.get(serviceName))
					{
					case 1: //Register service  
						resultContent = sfInterface.registerService(inputs.get("ServiceURL"));
						break;
					case 2: //De-register service
						resultContent = sfInterface.deregisterService(inputs.get("ServiceProfile"));
						break;
					case 3: //Get service
						resultContent = sfInterface.getService(inputs.get("ServiceProfile"));
						break;
					case 4: //Search service

						ArrayList<String> inputsService=new ArrayList<String>();
						ArrayList<String> outputsService=new ArrayList<String>();
						ArrayList<String> keywordsService=new ArrayList<String>();

						StringTokenizer tokInputs=new StringTokenizer(inputs.get("Inputs"), "|");
						while(tokInputs.hasMoreTokens()){
							String in=tokInputs.nextToken().trim();
							inputsService.add(in);
							logger.info("\t\t"+in);
						}


						logger.info("\tOutputs:");
						StringTokenizer tokOutputs=new StringTokenizer(inputs.get("Outputs"), "|");
						while(tokOutputs.hasMoreTokens()){
							String out=tokOutputs.nextToken().trim();
							outputsService.add(out);

							logger.info("\t\t"+out);
						}


						logger.info("\tKeywords:");
						StringTokenizer tokKeywords=new StringTokenizer(inputs.get("Keywords"), "|");
						while(tokKeywords.hasMoreTokens()){
							String key=tokKeywords.nextToken().trim();
							keywordsService.add(key);

							logger.info("\t\t"+key);
						}

						resultContent = sfInterface.searchService(inputsService, outputsService,keywordsService);

						break;
					case 5: //Remove provider

						String serviceProfile=inputs.get("ServiceProfile").trim();
						String providerID=inputs.get("ProviderID").trim();

						resultContent = sfInterface.removeProvider(serviceProfile, providerID);
						break;

					}

					// get the SF service WSDL URL
					//	String serviceWSDLURL = sfServicesURLs.get(serviceName);
					// execute the SF service Requested
					//	HashMap<String, Object> result = st.executeWebService(serviceWSDLURL, inputs);

					logger.info("[SF] Values obtained... ");

					logger.info("[SF] Creating inform message to send...");

					next = "INFORM";

					// get the result and put it in the response
					//String resultContent = (String) result.get("Result");
					myProcessor.getLastReceivedMessage().setContent(resultContent);

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
			protected String doReceiveRequest(CProcessor myProcessor, ACLMessage request) {
				String next = "";
				ACLMessage msg = request;

				if (msg != null) {

					try {

						// extract the data of the request to check if the
						// requested service is one of the SF services
						HashMap<String, String> inputs = new HashMap<String, String>();
						String serviceName = st.extractServiceContent(msg.getContent(), inputs);

						logger.info("[SF] Service Name: " + serviceName);

						if (sfServicesURLs.containsKey(serviceName)) {
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

				logger.info("[SF] Sending First message:" + next);

				return next;
			}
		}

		CFactory talk = new myFIPA_REQUEST().newFactory("TALK", null, 1, firstProcessor.getMyAgent());

		// Finally the factory is setup to answer to incoming messages that
		// can start the participation of the agent in a new conversation
		this.addFactoryAsParticipant(talk);

	}

} // end SF Agent

