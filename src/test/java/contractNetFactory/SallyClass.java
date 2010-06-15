package contractNetFactory;

import java.util.Random;

import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.CProcessorFactory;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_CONTRACTNET_Participant;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;

class SallyClass extends CAgent {

	public SallyClass(AgentID aid) throws Exception {
		super(aid);
	}

	protected void Initialize(CProcessor myProcessor, ACLMessage welcomeMessage) {

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
		// CONTRACT protocol defined by FIPA.
		// In order to do so, she has to extend the class FIPA_CONTRACTNET_Participant
		// implementing the method that receives solicit (doSolicit),
		// the method that carries out the proposal if it is accepted (doTask),
		// the method that informs of the result of the task (doSendInfo) and
		// the method that sends the proposal (doSendProposal)

		class myFIPA_CONTRACTNET extends FIPA_CONTRACTNET_Participant {

			@Override
			protected String doReceiveSolicit(CProcessor myProcessor,
					ACLMessage msg) {
				// accept all the solicits
				return "SEND_PROPOSAL";
			}

			@Override
			protected void doSendInfo(CProcessor myProcessor,
					ACLMessage messageToSend) {
				messageToSend.setSender(getAid());
				messageToSend.setReceiver(myProcessor.getLastReceivedMessage().getSender());
				messageToSend.setContent("I'm "+getAid()+". Ok. See you tomorrow!");
				messageToSend.setPerformative(ACLMessage.INFORM);
				messageToSend.setProtocol("fipa-contract-net");				
			}

			@Override
			protected void doSendProposal(CProcessor myProcessor,
					ACLMessage messageToSend) {
				Random rand = new Random(System.currentTimeMillis());
				int x = rand.nextInt(100);				
				messageToSend.setSender(getAid());
				messageToSend.setReceiver(myProcessor.getLastReceivedMessage().getSender());
				messageToSend.setContent(String.valueOf(x));
				messageToSend.setPerformative(ACLMessage.PROPOSE);
				messageToSend.setProtocol("fipa-contract-net");						
			}

			@Override
			protected String doTask(CProcessor myProcessor,
					ACLMessage solicitMessage) {
				// no action to take, just inform
				System.out.println("I'm "+getName()+" my proposal was accepted");
				return "SEND_INFORM";
			}

		}

		// The agent creates the CFactory that manages every message which its
		// performative is set to CFP and protocol set to CONTRACTNET. In this
		// example the CFactory gets the name "TALK", we don't add any
		// additional message acceptance criterion other than the required
		// by the CONTRACTNET protocol (null) and we limit the number of simultaneous
		// processors to 1, i.e. the requests will be attended one after another.

		CProcessorFactory talk = new myFIPA_CONTRACTNET().newFactory("TALK", null, 
				null, 1, myProcessor.getMyAgent(), 0);
		
		// Finally the factory is setup to answer to incoming messages that
		// can start the participation of the agent in a new conversation

		this.addFactoryAsParticipant(talk);
	}

	protected void Finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {
	}
}