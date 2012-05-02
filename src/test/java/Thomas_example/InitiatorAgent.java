package Thomas_example;

/**
 * In this class the agent initiator is represented. 
 * Functions:
 *  -	Acquire role participant inside the unit virtual.
 * 	-	Initialize the organization. This organization is formed by two units (calculator and school) 
 * 		and two roles (operator and student)
 */

import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CFactory;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_REQUEST_Participant;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.SFProxy;
import es.upv.dsic.gti_ia.organization.THOMASException;

public class InitiatorAgent extends CAgent {

	OMSProxy omsProxy = new OMSProxy(this);
	SFProxy sfProxy = new SFProxy(this);
	String message;

	public InitiatorAgent(AgentID aid) throws Exception {
		super(aid);
	}
	
	
	public String getMessage()
	{
		return message;
	}

	protected void execution(CProcessor firstProcessor, ACLMessage welcomeMessage) {

		try {
			System.out.println("[" + this.getName() + "]" + " Initializing Example");

			String result = omsProxy.acquireRole("participant", "virtual");
			logger.info("[" + this.getName() + "] Result acquire role participant: " + result);

			this.initialize_scenario();

			CFactory initiatorTalk = new myFIPA_REQUEST_Participant().newFactory("INITIATOR_TALK", null, 0,
					firstProcessor.getMyAgent());

			this.addFactoryAsParticipant(initiatorTalk);

		} catch (THOMASException e) {
			e.printStackTrace();
			message ="ERROR";
		}

	}

	/**
	 * The agent initializes the scenario registering the unit and roles needed
	 */
	public void initialize_scenario() {
		try {

			String result = omsProxy.registerUnit("calculator", "team", "virtual", "creator");
			logger.info("[" + this.getName() + "] Result register unit calculator: " + result);

			result = omsProxy.registerUnit("school", "flat", "virtual", "creator");
			logger.info("[" + this.getName() + "] Result register unit school: " + result);

			result = omsProxy.registerRole("operation", "calculator", "external", "public", "member");
			logger.info("[" + this.getName() + "] Result register role operation: " + result);

			result = omsProxy.registerRole("student", "school", "external", "public", "member");
			logger.info("[" + this.getName() + "] Result register role student: " + result);

			System.out.println("[" + this.getName() + "] " + "Scenario initialized");

		} catch (THOMASException e) {
			System.out.println("[" + this.getName() + "] " + e.getContent());
			message = "ERROR";
		}

	}

	@Override
	protected void finalize(CProcessor firstProcessor, ACLMessage finalizeMessage) {
		System.out.println("[" + firstProcessor.getMyAgent().getName() + "] End execution");

	}

	class myFIPA_REQUEST_Participant extends FIPA_REQUEST_Participant {

		@Override
		protected String doReceiveRequest(CProcessor myProcessor, ACLMessage request) {
			String next = "";
			if (request.getHeaderValue("EXAMPLEENDED") != null) {
				next = "AGREE";
			} else {
				logger.info("REFUSE");
				next = "REFUSE";
			}

			return next;
		}

		@Override
		protected String doAction(CProcessor myProcessor) {
			// deallocate roles and deregister services

			try {
				omsProxy.deallocateRole("operation", "calculator", "AdditionAgent");
				omsProxy.deallocateRole("operation", "calculator", "ProductAgent");
				omsProxy.deallocateRole("student", "school", "JamesAgent");

				omsProxy.deregisterRole("operation", "calculator");
				omsProxy.deregisterRole("student", "school");
				omsProxy.deregisterUnit("calculator");
				omsProxy.deregisterUnit("school");

				sfProxy.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile");
				sfProxy.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile");
				sfProxy.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl#AdditionProfile");

				omsProxy.leaveRole("participant", "virtual");

				// send an INFORM message to AdditionAgent and ProductAgent with
				// header and content EXAMPLEENDED to finish them

				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				msg.setSender(getAid());
				msg.addReceiver(new AgentID("AdditionAgent"));
				msg.addReceiver(new AgentID("ProductAgent"));
				msg.setHeader("EXAMPLEENDED", "EXAMPLEENDED");
				msg.setContent("EXAMPLEENDED");
				send(msg);

				System.out.println("[" + myProcessor.getMyAgent().getName() + "]"
						+ " Example Ended. Roles, Units and Services deregistered");
				
				message = "OK";

			} catch (THOMASException e) {

				e.printStackTrace();
				
				message = "ERROR";
			}

			return "INFORM";
		}

		@Override
		protected void doInform(CProcessor myProcessor, ACLMessage response) {
			ACLMessage lastReceivedMessage = myProcessor.getLastReceivedMessage();
			response.setContent(lastReceivedMessage.getContent());
			if (lastReceivedMessage.getHeaderValue("EXAMPLEENDED") != null)
				response.setHeader("EXAMPLEENDED", "EXAMPLEENDED");

		}

		@Override
		protected void doFinal(CProcessor myProcessor, ACLMessage messageToSend) {
			ACLMessage msg = myProcessor.getLastReceivedMessage();
			if (msg.getHeaderValue("EXAMPLEENDED") != null)
				myProcessor.ShutdownAgent();
		}

	}

}
