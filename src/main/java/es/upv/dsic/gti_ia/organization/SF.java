package es.upv.dsic.gti_ia.organization;

import java.util.HashMap;

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
	
	ServiceTools st=new ServiceTools();

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

		sfServicesURLs.put("RegisterService", SFServiceDescriptionLocation+"RegisterService?wsdl");
		sfServicesURLs.put("DeregisterService", SFServiceDescriptionLocation+"DeregisterService?wsdl");
		sfServicesURLs.put("GetService", SFServiceDescriptionLocation+"GetService?wsdl");
		sfServicesURLs.put("SearchService", SFServiceDescriptionLocation+"SearchService?wsdl");
		sfServicesURLs.put("RemoveProvider", SFServiceDescriptionLocation+"RemoveProvider?wsdl");

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

					HashMap<String,String> inputs=new HashMap<String, String>();
					
					String serviceName=st.extractServiceContent(myProcessor.getLastReceivedMessage().getContent(),inputs);
					String serviceWSDLURL=sfServicesURLs.get(serviceName);
					HashMap<String,Object> result=st.executeWebService(serviceWSDLURL, inputs);
					
					logger.info("[SF]Values obtained... ");

					logger.info("[SF]Creating inform message to send...");

					next = "INFORM";

					logger.info("[SF]Before set message content...");
					
					String resultContent=(String)result.get("Result");
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
			protected String doReceiveRequest(CProcessor myProcessor,
					ACLMessage request) {
				String next = "";
				ACLMessage msg = request;

				if (msg != null) {

					try {
						
						
						HashMap<String,String> inputs=new HashMap<String, String>();
						String serviceName = st.extractServiceContent(msg.getContent(), inputs);

						logger.info("[SF]Service Name: " + serviceName);


						if (sfServicesURLs.containsKey(serviceName)) //if (sfServicesURLs.containsKey(serviceName))
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

