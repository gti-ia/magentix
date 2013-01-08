package myFirstCProcessorFactories;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.MessageFilter;

import es.upv.dsic.gti_ia.cAgents.*;


class SallyClass extends CAgent {

	public SallyClass(AgentID aid) throws Exception {
		super(aid);
	}

	protected void execution(CProcessor myProcessor, ACLMessage welcomeMessage) {

		MessageFilter filter;
		ACLMessage template;

		// We create a factory in order to manage propositions

		filter = new MessageFilter("performative = PROPOSE");

		CFactory talk = new CFactory("TALK", filter, 1,
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
				return "REFUSE";
			}
		}
		
		ReceiveState GETMESSAGE = new ReceiveState("GETMESSAGE");
		GETMESSAGE.setMethod(new GETMESSAGE_Method());
		filter = new MessageFilter("performative = PROPOSE");
		GETMESSAGE.setAcceptFilter(filter);
		talk.cProcessorTemplate().registerState(GETMESSAGE);
		talk.cProcessorTemplate().addTransition("WAIT", "GETMESSAGE");
		
		class NOT_ACCEPTED_MESSAGES_STATE_Method implements ActionStateMethod {
			@Override
			public String run(CProcessor myProcessor) {
				System.out.println("This is NOT_ACCEPTED_MESSAGES_STATE");
				return "REFUSE";
			}
		}
		
		ActionState NOT_ACCEPTED_MESSAGES_STATE = new ActionState("NOT_ACCEPTED_MESSAGES_STATE");
		NOT_ACCEPTED_MESSAGES_STATE.setMethod(new NOT_ACCEPTED_MESSAGES_STATE_Method());
		talk.cProcessorTemplate().registerState(NOT_ACCEPTED_MESSAGES_STATE);

		///////////////////////////////////////////////////////////////////////////////
		// REFUSE state

		SendState REFUSE = new SendState("REFUSE");

		class REFUSE_Method implements SendStateMethod {
			public String run(CProcessor myProcessor, ACLMessage messageToSend) {

				messageToSend.setSender(myProcessor.getMyAgent().getAid());
				messageToSend.setReceiver(myProcessor.getLastReceivedMessage().getSender());
				messageToSend.setContent("Maybe someday");
				return "FINAL";
			}
		}
		REFUSE.setMethod(new REFUSE_Method());
		
		template = new ACLMessage(ACLMessage.REFUSE);
		REFUSE.setMessageTemplate(template);

		talk.cProcessorTemplate().registerState(REFUSE);
		talk.cProcessorTemplate().addTransition(GETMESSAGE, REFUSE);

		///////////////////////////////////////////////////////////////////////////////
		// FINAL state

		FinalState FINAL = new FinalState("FINAL");

		class FINAL_Method implements FinalStateMethod {
			public void run(CProcessor myProcessor, ACLMessage messageToSend) {
				messageToSend.setContent("Done");
				myProcessor.getMyAgent().Shutdown();
			}
		}
		FINAL.setMethod(new FINAL_Method());

		talk.cProcessorTemplate().registerState(FINAL);
		talk.cProcessorTemplate().addTransition("REFUSE", "FINAL");

		// The template processor is ready. We activate the factory
		// as participant. Every message that arrives to the agent
		// with the performative set to PURPOSE will make the factory
		// TALK to create a processor in order to manage the conversation.
		this.addFactoryAsParticipant(talk);

	}

	protected void finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {

		System.out.println(finalizeMessage.getContent());
	}
}