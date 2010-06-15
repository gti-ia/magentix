package contractNetFactory;

import java.util.ArrayList;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.CProcessorFactory;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_CONTRACTNET_Initiator;

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
		// CONTRACT_NET protocol defined by FIPA.
		// In order to do so, she has to extend the class FIPA_REQUEST_Initiator
		// implementing the method that evaluates the proposals (doEvaluateProposals)
		// and the method that receives the results of the accepted proposal (doInform)
		
		class myFIPA_CONTRACTNET extends FIPA_CONTRACTNET_Initiator {

			@Override
			protected void doEvaluateProposals(CProcessor myProcessor,
					ArrayList<ACLMessage> proposes,
					ArrayList<ACLMessage> acceptances,
					ArrayList<ACLMessage> rejections) {
				int min = 1000;
				int index = -1;
				for(int i=0; i < proposes.size(); i++){
					if(Integer.valueOf(proposes.get(i).getContent()) < min){
						min = Integer.valueOf(proposes.get(i).getContent());
						index = i;
					}
				}
				
				for(int i=0; i < proposes.size(); i++){
					if(i == index){ // accept the cheaper proposal
						System.out.println("I accept "+proposes.get(i).getSender()+"'s proposal");
						ACLMessage accept = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
						accept.setContent("I accept your proposal");
						accept.setReceiver(proposes.get(i).getSender());
						accept.setSender(getAid());
						accept.setProtocol("fipa-contract-net");
						acceptances.add(accept);
					}
					else{ // reject the rest
						System.out.println("I reject "+proposes.get(i).getSender()+"'s proposal");
						ACLMessage reject = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
						reject.setContent("I don't like your proposal, I reject it");
						reject.setReceiver(proposes.get(1).getSender());
						reject.setSender(getAid());
						reject.setProtocol("fipa-contract-net");
						rejections.add(reject);
					}
				}
			}

			@Override
			protected void doReceiveInform(CProcessor myProcessor,
					ACLMessage msg) {
				// receive accepted proposal result
				System.out.println("Result: "+msg.getContent());
				
			}
			
		}
		
		// In order to start a conversation the agent creates a message
		// that can be accepted by one of its initiator factories.

		msg = new ACLMessage(ACLMessage.CFP);
		msg.addReceiver(new AgentID("Sally"));
		msg.addReceiver(new AgentID("Sally2"));
		msg.setContent("How much do you want to spend tomorrow in the dinner?");

		// The agent creates the CFactory that creates processors that initiate
		// CONTRACT_NET protocol conversations. In this
		// example the CFactory gets the name "TALK", we don't add any
		// additional message acceptance criterion other than the required
		// by the CONTRACT_NET protocol (null) and we do not limit the number of simultaneous
		// processors (value 0)
		
		CProcessorFactory talk = new myFIPA_CONTRACTNET().newFactory("TALK", null, msg,
				1, myProcessor.getMyAgent(), 2, 2000, 2000);

		// The factory is setup to answer start conversation requests from the agent
		// using the CONTRACT_NET protocol.

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