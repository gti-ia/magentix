package requestFactory;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.CFactory;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_REQUEST_Participant;

class SallyClass extends CAgent {

	public SallyClass(AgentID aid) throws Exception {
		super(aid);
	}

	protected void execution(CProcessor myProcessor, ACLMessage welcomeMessage) {

		System.out.println(myProcessor.getMyAgent().getName()
				+ ": the welcome message is " + welcomeMessage.getContent());

		// Each agent's conversation is carried out by a CProcessor.
		// CProcessors are created by the CFactories in response
		// to messages that start the agent's activity in a conversation

		// An easy way to create CFactories is to create them from the 
		// predefined factories of package es.upv.dsi.gri_ia.cAgents.protocols
		// Another option, not shown in this example, is that the agent
		// designs her own factory and, therefore, a new interaction protocol

		// In this example the agent is going to act as the participant in
		// REQUESt protocol defined by FIPA.
		// In order to do so, she has to extend the class FIPA_REQUEST_Participant
		// implementing the method that receives the request (doRequest),
		// the method that carries out the request (doAction) and
		// the method that generates the answer (doInform)

		class myFIPA_REQUEST extends FIPA_REQUEST_Participant {

			@Override
			protected String doAction(CProcessor myProcessor) {
				System.out.println("Nada que hacer en la acción, solo retornar el siguiente estado");
				return "INFORM";
			}

			@Override
			protected void doInform(CProcessor myProcessor, ACLMessage response) {
				response.setContent("Yes, my number is 666 456 855");
				System.out.println(myProcessor.getMyAgent().getName()
						+ ": I send the answer to " + myProcessor.getLastReceivedMessage().getSender().name);
				
			}

			@Override
			protected String doReceiveRequest(CProcessor myProcessor,
					ACLMessage request) {
				System.out.println("Siempre aceptamos peticiones");
				return "AGREE";
			}
		}

		// The agent creates the CFactory that manages every message which its
		// performative is set to REQUEST and protocol set to REQUEST. In this
		// example the CFactory gets the name "TALK", we don't add any
		// additional message acceptance criterion other than the required
		// by the REQUEST protocol (null) and we limit the number of simultaneous
		// processors to 1, i.e. the requests will be attended one after another.
		
		CFactory talk = new myFIPA_REQUEST().newFactory("TALK", null,
				0, myProcessor.getMyAgent());

		// Finally the factory is setup to answer to incoming messages that
		// can start the participation of the agent in a new conversation
		this.addFactoryAsParticipant(talk);
	}

	protected void finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {
	}
}