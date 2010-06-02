package es.upv.dsic.gti_ia.organization;

import java.net.URI;
import java.util.*;

import org.apache.log4j.Logger;
import org.mindswap.owl.EntityFactory;
import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owls.OWLSFactory;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.process.execution.ProcessExecutionEngine;
import org.mindswap.owls.service.Service;
import org.mindswap.query.ValueMap;

import es.upv.dsic.gti_ia.architecture.*;
import es.upv.dsic.gti_ia.architecture.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;

/**
 * SF agent is responsible for managing all the request messages from other
 * entities SF agent follows a FIPA-Request protocol
 */
public class SF extends QueueAgent {

	private Monitor mon = new Monitor();

	Configuration configuration = Configuration.getConfiguration();

	
	private static SF sf = null;
	private String SFServiceDesciptionLocation = configuration.getSFServiceDesciptionLocation();

	static Logger logger = Logger.getLogger(SF.class);
	// create a kb
	OWLKnowledgeBase kb = OWLFactory.createKB();
	OWLKnowledgeBase kbaux = OWLFactory.createKB();

	// Debug
	//private final Boolean DEBUG = true;

	// URI where the SF service descriptions are located

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
	private final URI SF_REMOVEPROVIDER_GROUNDING = URI.create(OWL_S_SF_SERVICES
			+ "RemoveProviderGrounding.owl");
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
	 * @param agent a new Agent ID
	  * @return SFagent SF
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
	 * Returns an instance of the agents SF, the agentID of the agent is AgentID("SF")
	  * @return SF agent SF
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

	}

	/**
	 * Change the URL where the owl's document is
	 * located.
	 * @param SFUrl ej. http://localhost:8080/sfservices/SFservices/owl/owls/
	 */
	public void setSFServiceDesciptionLocation(String SFUrl) {
		this.SFServiceDesciptionLocation = SFUrl;
	}

	/**
	 * get the URL where the owl's document is
	 * located.
	 * @param SFUrl ej. http://localhost:8080/sfservices/SFservices/owl/owls/
	 */
	public String getSFServiceDesciptionLocation() {
		return this.SFServiceDesciptionLocation;
	}

	/**
	 * Initial registration of the SF service profile
	 */
	public void RegisterSFServiceProfiles() {

		// create an execution engine
		ProcessExecutionEngine exec = OWLSFactory.createExecutionEngine();

		try {

			// REGISTER SF SERVICES PROFILES
			Service RegisterProfileService = kb
					.readService(SF_REGISTERPROCESS_PROCESS);
			// get the process for the server
			Process RegisterProfileProcess = RegisterProfileService
					.getProcess();

			for (int k = 0; k < SFServicesProfiles.length; k++) {

				// initialize the input values to be empty
				ValueMap values = new ValueMap();
				values.setDataValue(RegisterProfileProcess
						.getInput("RegisterProfileInputServiceProfile"),
						SFServicesProfiles[k].toString());
				values.setDataValue(RegisterProfileProcess
						.getInput("RegisterProfileInputServiceGoal"),
						SFServicesGoals[k].toString());

				logger
						.info("[SF]Executing... "
								+ values.getValues().toString());
				values = exec.execute(RegisterProfileProcess, values);

				logger.info("[SF]Values obtained... :" + values.toString());

			}// for k
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}// end RegisterSFServiceProfiles

	/**
	 * Initial registration of the SF service process
	 * 
	 * 	 */
	public void RegisterSFServiceProcess() {
		// create an execution engine
		ProcessExecutionEngine exec = OWLSFactory.createExecutionEngine();

		try {
			// REGISTER SF SERVICES PROCESS
			Service RegisterProcessService = kb
					.readService(SF_REGISTERPROCESS_PROCESS);
			// get the process
			Process RegisterProcessProcess = RegisterProcessService
					.getProcess();

			for (int k = 0; k < SFServicesProcess.length; k++) {

				// initialize the input values to be empty
				ValueMap values = new ValueMap();
				values.setDataValue(RegisterProcessProcess
						.getInput("RegisterProcessInputServiceID"),
						SFServicesIDs[k].toString());
				values.setDataValue(RegisterProcessProcess
						.getInput("RegisterProcessInputServiceModel"),
						SFServicesProcess[k].toString());
				values.setDataValue(RegisterProcessProcess
						.getInput("RegisterProcessInputServiceGrounding"),
						SFServicesGrounding[k].toString());
				values.setDataValue(RegisterProcessProcess
						.getInput("RegisterProcessInputProviderID"),
						SFServicesProviderID[k].toString());

				logger
						.info("[SF]Executing... "
								+ values.getValues().toString());
				values = exec.execute(RegisterProcessProcess, values);

				logger.info("[SF]Values obtained... :" + values.toString());

			}// for k
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}// end RegisterSFServiceProcess

	/**
	 * Manages the messages for the SF services
	 */
	public class SFResponder extends FIPARequestResponder {

		public SFResponder(es.upv.dsic.gti_ia.architecture.QueueAgent agent) {
			super(agent, new MessageTemplate(InteractionProtocol.FIPA_REQUEST));

		}// SFResponder

		/**
		 * Receives the messages and takes the message content. Analyzes the
		 * message content and gets the service process and input parameters to
		 * invoke the service. After the service invocation, the SF gets the
		 * answer and sends it to the requester agent.
		 * 
		 * @param msg ACLMessage
		 * @throws RuntimeException
		 */
		protected ACLMessage prepareResponse(ACLMessage msg) {

			ACLMessage response = msg.createReply();
			if (msg != null) {

				try {

					// read msg content
					StringTokenizer Tok = new StringTokenizer(msg.getContent());

					// read in the service description
					String token_process = Tok.nextElement().toString();

					logger.info("[SF]Doc OWL-S: " + token_process);
					Service aService = kb.readService(token_process);

					// get the process for the server
					Process aProcess = aService.getProcess();

					// System.out.println("resultado de la comparacion
					// "+token_process.equals(SFServicesProcess[4].toString()));
					if (token_process.equals(SFServicesProcess[0].toString())
							|| token_process.equals(SFServicesProcess[1]
									.toString())
							|| token_process.equals(SFServicesProcess[2]
									.toString())
							|| token_process.equals(SFServicesProcess[3]
									.toString())
							|| token_process.equals(SFServicesProcess[4]
									.toString())
							|| token_process.equals(SFServicesProcess[5]
									.toString())
							|| token_process.equals(SFServicesProcess[6]
									.toString())
							|| token_process.equals(SFServicesProcess[7]
									.toString())
							|| token_process.equals(SFServicesProcess[8]
									.toString())
							|| token_process.equals(SFServicesProcess[9]
									.toString())) {

						logger.info("AGREE");
						response
								.setPerformative(es.upv.dsic.gti_ia.core.ACLMessage.AGREE);
						response.setContent(aProcess.getLocalName() + "=Agree");

					} else {

						logger.info("REFUSE");
						response
								.setPerformative(es.upv.dsic.gti_ia.core.ACLMessage.REFUSE);
						response
								.setContent(aProcess.getLocalName() + "=Refuse");
					}

				} catch (Exception e) {

					logger.info("EXCEPTION");
					System.out.println(e);
					e.printStackTrace();
					throw new RuntimeException(e.getMessage());

				}

			} else {

				logger.info("NOTUNDERSTOOD");
				response
						.setPerformative(es.upv.dsic.gti_ia.core.ACLMessage.NOT_UNDERSTOOD);
				response.setContent("NotUnderstood");
			}

			logger.info("[SF]Sending First message:" + response);

			return (response);

		} // end prepareResponse

		/**
		 * This callback happens if the SF sent a positive reply to the original
		 * request (i.e. an AGREE) if the SF has agreed to supply the service,
		 * the SF has to inform the other agent that what they have asked is now
		 * complete (or if it failed)
		 * 
		 * @param inmsg  Message messages sent by the initiator
		 * @param outmsg Message which we will send to the initiator with the notification 
		 
		 * @throws RuntimeException
		 */
		protected ACLMessage prepareResultNotification(ACLMessage inmsg,
				ACLMessage outmsg) {

			ACLMessage msg = inmsg.createReply();

			// create an execution engine
			ProcessExecutionEngine exec = OWLSFactory.createExecutionEngine();

			// read msg content
			StringTokenizer Tok = new StringTokenizer(inmsg.getContent());

			// read in the service description
			String token_process = Tok.nextElement().toString();

			logger.info("[SF]Doc OWL-S: " + token_process);

			try {
				Service aService = kb.readService(token_process);

				// get the process for the server
				Process aProcess = aService.getProcess();
				// initialize the input values to be empty
				ValueMap values = new ValueMap();

				// get the input values
				for (int i = 0; i < aProcess.getInputs().size(); i++)
					values.setValue(aProcess.getInputs().inputAt(i),
							EntityFactory.createDataValue(""));
				while (Tok.hasMoreElements()) {
					String token = Tok.nextElement().toString();
					for (int i = 0; i < aProcess.getInputs().size(); i++) {
						String paramName = aProcess.getInputs().inputAt(i)
								.getLocalName().toLowerCase();
						if (paramName.equalsIgnoreCase(token.split("=")[0]
								.toLowerCase())) {
							if (token.split("=").length >= 2)
								values.setValue(
										aProcess.getInputs().inputAt(i),
										EntityFactory.createDataValue(token
												.split("=")[1]));
							else
								values.setValue(
										aProcess.getInputs().inputAt(i),
										EntityFactory.createDataValue(""));
						}
						if (aProcess.getInputs().inputAt(i).toString()
								.contains("AgentID")) {

							values.setValue(aProcess.getInputs().inputAt(i),
									EntityFactory.createDataValue(inmsg
											.getSender().toString()));
						}
					}
				}// end while

				// execute the service
				logger
						.info("[SF]Executing... "
								+ values.getValues().toString());
				values = exec.execute(aProcess, values);

				logger.info("[SF]Values obtained... ");

				logger.info("[SF]Creating inform message to send...");

				msg.setPerformative(es.upv.dsic.gti_ia.core.ACLMessage.INFORM);

				logger.info("[SF]Before set message content...");
				msg.setContent(aProcess.getLocalName() + "="
						+ values.toString());

			} catch (Exception e) {

			}
			return (msg);
		} // end prepareResultNotification

	}// end class SFResponder

	/**
	 * Starts the SF agent and registers all the SF services (process, profile,
	 * grounding)
	 */
	protected void execute() {
		// RegisterOMSServiceProfiles();
		// RegisterOMSServiceProcess();
		logger.info("Agent SF active");

		SFResponder responder = new SFResponder(this);

		this.addTask(responder);
		mon.waiting();

	}// end execute

} // end SF Agent
