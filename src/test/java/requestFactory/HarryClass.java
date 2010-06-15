package requestFactory;

import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.CProcessorFactory;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_REQUEST_Initiator;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;

class HarryClass extends CAgent {

	public HarryClass(AgentID aid) throws Exception {
		super(aid);
	}

	protected void Initialize(CProcessor myProcessor, ACLMessage welcomeMessage) {

		ACLMessage msg;

		System.out.println(myProcessor.getMyAgent().getName()
				+ ": the welcome message is " + welcomeMessage.getContent());
		
		// Each agent's conversation is carried out by a CProcessor.
		// CProcessors are created by the CFactories in response
		// to messages that start the agent's activity in a conversation

		// An easy way to create CFactories is to create them from the 
		// predefined factories of package es.upv.dsi.gri_ia.cAgents.protocols
		// Another option, not shown in this example, is that the agent
		// designs her own factory and, therefore, a new interaction protocol

		// In this example the agent is going to act as the initiator in the
		// REQUEST protocol defined by FIPA.
		// In order to do so, she has to extend the class FIPA_REQUEST_Initiator
		// implementing the method that receives results of the request (doInform)
		
		class myFIPA_REQUEST extends FIPA_REQUEST_Initiator {
			protected void doInform(CProcessor myProcessor, ACLMessage msg) {
				System.out.println(myProcessor.getMyAgent().getName() + ": "
						+ msg.getSender().name + " informs me "
						+ msg.getContent());
			}
		}
		
		// In order to start a conversation the agent creates a message
		// that can be accepted by one of its initiator factories.

		msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setReceiver(new AgentID("Sally"));
		msg.setContent("May you give me your phone number?");
		
		// The agent creates the CFactory that creates processors that initiate
		// REQUEST protocol conversations. In this
		// example the CFactory gets the name "TALK", we don't add any
		// additional message acceptance criterion other than the required
		// by the REQUEST protocol (null) and we do not limit the number of simultaneous
		// processors (value 0)
		
		CProcessorFactory talk = new myFIPA_REQUEST().newFactory("TALK", null, msg,
				1, myProcessor.getMyAgent(), 0);

		// The factory is setup to answer start conversation requests from the agent
		// using the REQUEST protocol.

		this.addFactoryAsInitiator(talk);

		// finally the new conversation starts. Because it is synchronous, 
		// the current interaction halts until the new conversation ends.
		myProcessor.createSyncConversation(msg);

		myProcessor.ShutdownAgent();
	}

	protected void Finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {
	}
}