package TestCAgents.Agents;

import es.upv.dsic.gti_ia.cAgents.BeginState;
import es.upv.dsic.gti_ia.cAgents.BeginStateMethod;
import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.CFactory;
import es.upv.dsic.gti_ia.cAgents.FinalState;
import es.upv.dsic.gti_ia.cAgents.FinalStateMethod;
import es.upv.dsic.gti_ia.cAgents.ReceiveState;
import es.upv.dsic.gti_ia.cAgents.ReceiveStateMethod;
import es.upv.dsic.gti_ia.cAgents.SendState;
import es.upv.dsic.gti_ia.cAgents.SendStateMethod;
import es.upv.dsic.gti_ia.cAgents.WaitState;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.MessageFilter;

/**
 * Propose initiator factory class for the test of the example myfirstCProcessorFactories 
 * 
 * @author David Fernández - dfernandez@dsic.upv.es
 * @author Jose Manuel Mejias Rodriguez - jmejias@dsic.upv.es
 */

public class OtherInitiatorClass extends CAgent {
	
	//Variables for testing
	public String receivedMsg;
	public int msgPerformative;
	
	public OtherInitiatorClass(AgentID aid) throws Exception {
		super(aid);
		
		receivedMsg = "";
		msgPerformative = ACLMessage.PROPOSE; //Performative set to Propose by default
	}

	protected void execution(CProcessor myProcessor, ACLMessage welcomeMessage) {

		MessageFilter filter;
		
		// We create a factory in order to send a propose and wait for the answer

		filter = new MessageFilter("performative = PROPOSE");
		
		CFactory talk = new CFactory("TALK", filter, 1,
				myProcessor.getMyAgent());

		// A CProcessor always starts in the predefined state BEGIN.
		// We have to associate this state with a method that will be
		// executed at the beginning of the conversation.

		///////////////////////////////////////////////////////////////////////////////
		// BEGIN state

		BeginState BEGIN = (BeginState) talk.cProcessorTemplate().getState(
				"BEGIN");

		class BEGIN_Method implements BeginStateMethod {
			public String run(CProcessor myProcessor, ACLMessage msg) {
				// In this example there is nothing more to do than continue
				// to the next state which will send the message.
				return "PURPOSE";
			};
		}
		BEGIN.setMethod(new BEGIN_Method());

		///////////////////////////////////////////////////////////////////////////////
		// PURPOSE state

		SendState PURPOSE = new SendState("PURPOSE");

		class PURPOSE_Method implements SendStateMethod {
			public String run(CProcessor myProcessor, ACLMessage messageToSend) {
				messageToSend.setPerformative(msgPerformative);
				messageToSend.setReceiver(new AgentID("Sally"));
				messageToSend.setSender(myProcessor.getMyAgent().getAid());
				messageToSend.setContent("Will you come with me to a movie?");
				System.out.println(myProcessor.getMyAgent().getName() + " : I tell " + messageToSend.getReceiver().name + " "
						+ messageToSend.getPerformative() + " " + messageToSend.getContent());

				return "WAIT";
			}
		}
		PURPOSE.setMethod(new PURPOSE_Method());

		talk.cProcessorTemplate().registerState(PURPOSE);
		talk.cProcessorTemplate().addTransition("BEGIN", "PURPOSE");

		///////////////////////////////////////////////////////////////////////////////
		// WAIT State

		talk.cProcessorTemplate().registerState(new WaitState("WAIT", 0));
		talk.cProcessorTemplate().addTransition("PURPOSE", "WAIT");

		///////////////////////////////////////////////////////////////////////////////
		// RECEIVE State

		ReceiveState RECEIVE = new ReceiveState("RECEIVE");

		class RECEIVE_Method implements ReceiveStateMethod {
			public String run(CProcessor myProcessor, ACLMessage messageReceived) {
				receivedMsg=messageReceived.getPerformative()+": "+messageReceived.getContent();
				return "FINAL";
			}
		}
		
		RECEIVE.setAcceptFilter(null); // null -> accept any message
		RECEIVE.setMethod(new RECEIVE_Method());
		talk.cProcessorTemplate().registerState(RECEIVE);
		talk.cProcessorTemplate().addTransition("WAIT", "RECEIVE");

		///////////////////////////////////////////////////////////////////////////////
		// FINAL state

		FinalState FINAL = new FinalState("FINAL");

		class FINAL_Method implements FinalStateMethod {
			public void run(CProcessor myProcessor, ACLMessage messageToReturn) {
				messageToReturn.copyFromAsTemplate(myProcessor
						.getLastReceivedMessage());
				myProcessor.ShutdownAgent();
			}
		}
		FINAL.setMethod(new FINAL_Method());

		talk.cProcessorTemplate().registerState(FINAL);
		talk.cProcessorTemplate().addTransition(RECEIVE, FINAL);
		talk.cProcessorTemplate().addTransition("PURPOSE", "FINAL");

		///////////////////////////////////////////////////////////////////////////////
		
		// The template processor is ready. We add the factory, in this case as a initiator one

		this.addFactoryAsInitiator(talk);

		// Finally Harry starts the conversation.
		//this.startSyncConversation("TALK");

		//System.out.println(this.getAid().name + " : Sally tell me "
			//	+ response.getPerformative() + " " + response.getContent());
	}

	protected void finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {

		System.out.println(finalizeMessage.getContent());
	}
}