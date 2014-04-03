package TestCAgents.Agents;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.MessageFilter;

import es.upv.dsic.gti_ia.cAgents.*;

/**
 * Propose participant factory class for the test 
 * of the example myfirstCProcessorFactories 
 * 
 * @author David Fernández - dfernandez@dsic.upv.es
 */

public class OtherParticipantClass extends CAgent {

	//Variables for testing
	public String receivedMsg;
	public boolean notAcceptedMessageState;
	public int mode;
	
	public OtherParticipantClass(AgentID aid) throws Exception {
		super(aid);
		
		receivedMsg = "";
	}

	protected void execution(CProcessor myProcessor, ACLMessage welcomeMessage) {

		MessageFilter filter;
		ACLMessage template;

		// We create a factory in order to manage propositions

		filter = new MessageFilter("performative = PROPOSE");

		CFactory talk = new CFactory("RCV", filter, 1,
				this);

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
				// to the next state which will send the answer.
				return "WAIT";
			};
		}
		
		BEGIN.setMethod(new BEGIN_Method());
		
		talk.cProcessorTemplate().registerState(new WaitState("WAIT", 0));
		talk.cProcessorTemplate().addTransition("BEGIN", "WAIT");
		
		class GETMESSAGE_Method implements ReceiveStateMethod {
			public String run(CProcessor myProcessor, ACLMessage messageReceived) {
				System.out.println("Getting message");
				receivedMsg = messageReceived.getPerformative()+": "+messageReceived.getContent();
				if(mode==0){
					return "AGREE";
				}
				return "REFUSE";
				
			}
		}
		
		ReceiveState GETMESSAGE = new ReceiveState("GETMESSAGE");
		GETMESSAGE.setMethod(new GETMESSAGE_Method());
		filter = new MessageFilter("performative = PROPOSE");
		GETMESSAGE.setAcceptFilter(filter);
		talk.cProcessorTemplate().registerState(GETMESSAGE);
		talk.cProcessorTemplate().addTransition("WAIT", "GETMESSAGE");
		
		
		///////////////////////////////////////////////////////////////////////////////
		// REFUSE state

		SendState REFUSE = new SendState("REFUSE");

		class REFUSE_Method implements SendStateMethod {
			public String run(CProcessor myProcessor, ACLMessage messageToSend) {

				messageToSend.setSender(myProcessor.getMyAgent().getAid());
				messageToSend.setReceiver(myProcessor.getLastReceivedMessage().getSender());
				messageToSend.setContent("Maybe someday...");
				
				return "FINAL";
			}
		}
		
		REFUSE.setMethod(new REFUSE_Method());
		
		template = new ACLMessage(ACLMessage.REFUSE);
		REFUSE.setMessageTemplate(template);

		talk.cProcessorTemplate().registerState(REFUSE);
		talk.cProcessorTemplate().addTransition(GETMESSAGE, REFUSE);
				
				
		///////////////////////////////////////////////////////////////////////////////
		// AGREE state
		
		SendState AGREE = new SendState("AGREE");
		
		class AGREE_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
		
			messageToSend.setSender(myProcessor.getMyAgent().getAid());
			messageToSend.setReceiver(myProcessor.getLastReceivedMessage().getSender());
			messageToSend.setContent("OK");
			logger.error("INICIANDO CONVERSACION CON HARRY");
			myProcessor.getMyAgent().startSyncConversation("TALK");
			
			return "FINAL";
			}
		}
		
		AGREE.setMethod(new AGREE_Method());
		
		template = new ACLMessage(ACLMessage.AGREE);
		AGREE.setMessageTemplate(template);
		
		talk.cProcessorTemplate().registerState(AGREE);
		talk.cProcessorTemplate().addTransition(GETMESSAGE, AGREE);

		///////////////////////////////////////////////////////////////////////////////
		// FINAL state

		FinalState FINAL = new FinalState("FINAL");

		class FINAL_Method implements FinalStateMethod {
			public void run(CProcessor myProcessor, ACLMessage messageToSend) {
				messageToSend.setContent("Done");
				//myProcessor.getMyAgent().Shutdown();
			}
		}
		FINAL.setMethod(new FINAL_Method());

		talk.cProcessorTemplate().registerState(FINAL);
		talk.cProcessorTemplate().addTransition("AGREE", "FINAL");

		// The template processor is ready. We activate the factory
		// as participant. Every message that arrives to the agent
		// with the performative set to PURPOSE will make the factory
		// TALK to create a processor in order to manage the conversation.
		this.addFactoryAsParticipant(talk);
		
		
		//INITIATOR PART
		/*
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 */
		
		MessageFilter filter_I;
		
		// We create a factory in order to send a propose and wait for the answer

		filter_I = new MessageFilter("performative = PROPOSE");
		
		CFactory talk_I = new CFactory("TALK", filter_I, 1,
				myProcessor.getMyAgent());

		// A CProcessor always starts in the predefined state BEGIN.
		// We have to associate this state with a method that will be
		// executed at the beginning of the conversation.

		///////////////////////////////////////////////////////////////////////////////
		// BEGIN state

		BeginState BEGIN_I = (BeginState) talk.cProcessorTemplate().getState(
				"BEGIN_I");

		class BEGIN_I_Method implements BeginStateMethod {
			public String run(CProcessor myProcessor, ACLMessage msg) {
				// In this example there is nothing more to do than continue
				// to the next state which will send the message.
				return "PURPOSE_I";
			};
		}
		BEGIN_I.setMethod(new BEGIN_I_Method());

		///////////////////////////////////////////////////////////////////////////////
		// PURPOSE state

		SendState PURPOSE_I = new SendState("PURPOSE_I");

		class PURPOSE_I_Method implements SendStateMethod {
			public String run(CProcessor myProcessor, ACLMessage messageToSend) {
				messageToSend.setPerformative(ACLMessage.PROPOSE);
				messageToSend.setReceiver(new AgentID("Harry"));
				messageToSend.setSender(myProcessor.getMyAgent().getAid());
				messageToSend.setContent("Will you come with me to a movie?");
				System.out.println(myProcessor.getMyAgent().getName() + " : I tell " + messageToSend.getReceiver().name + " "
						+ messageToSend.getPerformative() + " " + messageToSend.getContent());

				return "WAIT_I";
			}
		}
		PURPOSE_I.setMethod(new PURPOSE_I_Method());

		talk.cProcessorTemplate().registerState(PURPOSE_I);
		talk.cProcessorTemplate().addTransition("BEGIN_I", "PURPOSE_I");

		///////////////////////////////////////////////////////////////////////////////
		// WAIT State

		talk.cProcessorTemplate().registerState(new WaitState("WAIT_I", 0));
		talk.cProcessorTemplate().addTransition("PURPOSE_I", "WAIT_I");

		///////////////////////////////////////////////////////////////////////////////
		// RECEIVE State

		ReceiveState RECEIVE_I = new ReceiveState("RECEIVE_I");

		class RECEIVE_I_Method implements ReceiveStateMethod {
			public String run(CProcessor myProcessor, ACLMessage messageReceived) {
				receivedMsg=messageReceived.getPerformative()+": "+messageReceived.getContent();
				return "FINAL_I";
			}
		}
		
		RECEIVE_I.setAcceptFilter(null); // null -> accept any message
		RECEIVE_I.setMethod(new RECEIVE_I_Method());
		talk.cProcessorTemplate().registerState(RECEIVE_I);
		talk.cProcessorTemplate().addTransition("WAIT_I", "RECEIVE_I");

		///////////////////////////////////////////////////////////////////////////////
		// FINAL state

		FinalState FINAL_I = new FinalState("FINAL_I");

		class FINAL_I_Method implements FinalStateMethod {
			public void run(CProcessor myProcessor, ACLMessage messageToReturn) {
				messageToReturn.copyFromAsTemplate(myProcessor
						.getLastReceivedMessage());
				myProcessor.ShutdownAgent();
			}
		}
		FINAL_I.setMethod(new FINAL_I_Method());

		talk.cProcessorTemplate().registerState(FINAL_I);
		talk.cProcessorTemplate().addTransition(RECEIVE_I, FINAL_I);
		talk.cProcessorTemplate().addTransition("PURPOSE_I", "FINAL_I");

		///////////////////////////////////////////////////////////////////////////////
		
		// The template processor is ready. We add the factory, in this case as a initiator one

		this.addFactoryAsInitiator(talk);

	}

	protected void finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {

		System.out.println(finalizeMessage.getContent());
	}

	/**
	 * @return the mode
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * @param mode the mode to set
	 */
	public void setMode(int mode) {
		this.mode = mode;
	}
}