package TestCAgents.Agents;

import java.util.concurrent.CountDownLatch;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.MessageFilter;

import es.upv.dsic.gti_ia.cAgents.*;

/**
 * 
 * @author Javier Jorge - jjorge@dsic.upv.es
 */

public class OtherParticipantClass extends CAgent {

	// Variables for testing
	public String receivedMsg;
	public boolean notAcceptedMessageState;
	public int mode;
	private CountDownLatch ready;

	public OtherParticipantClass(AgentID aid, CountDownLatch ready) throws Exception {
		super(aid);
		this.ready = ready;
		receivedMsg = "";
	}

	protected void execution(CProcessor myProcessor, ACLMessage welcomeMessage) {

		MessageFilter filter;
		ACLMessage template;

		filter = new MessageFilter("performative = PROPOSE");

		CFactory talk = new CFactory("RCV", filter, 1, this);

		// /////////////////////////////////////////////////////////////////////////////
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
				logger.info("Getting message");
				receivedMsg = messageReceived.getPerformative() + ": "
						+ messageReceived.getContent();
				if (mode == 0) {
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

		// /////////////////////////////////////////////////////////////////////////////
		// REFUSE state

		SendState REFUSE = new SendState("REFUSE");

		class REFUSE_Method implements SendStateMethod {
			public String run(CProcessor myProcessor, ACLMessage messageToSend) {

				messageToSend.setSender(myProcessor.getMyAgent().getAid());
				messageToSend.setReceiver(myProcessor.getLastReceivedMessage()
						.getSender());
				messageToSend.setContent("Maybe someday...");

				return "FINAL";
			}
		}

		REFUSE.setMethod(new REFUSE_Method());

		template = new ACLMessage(ACLMessage.REFUSE);
		REFUSE.setMessageTemplate(template);

		talk.cProcessorTemplate().registerState(REFUSE);
		talk.cProcessorTemplate().addTransition(GETMESSAGE, REFUSE);

		// /////////////////////////////////////////////////////////////////////////////
		// AGREE state

		SendState AGREE = new SendState("AGREE");

		class AGREE_Method implements SendStateMethod {
			public String run(CProcessor myProcessor, ACLMessage messageToSend) {

				messageToSend.setSender(myProcessor.getMyAgent().getAid());
				messageToSend.setReceiver(myProcessor.getLastReceivedMessage()
						.getSender());
				messageToSend.setContent("OK");
				//logger.error("INICIANDO CONVERSACION CON HARRY");
				//myProcessor.getMyAgent().startSyncConversation("TALK");

				return "FINAL";
			}
		}

		AGREE.setMethod(new AGREE_Method());

		template = new ACLMessage(ACLMessage.AGREE);
		AGREE.setMessageTemplate(template);

		talk.cProcessorTemplate().registerState(AGREE);
		talk.cProcessorTemplate().addTransition(GETMESSAGE, AGREE);

		// /////////////////////////////////////////////////////////////////////////////
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
		talk.cProcessorTemplate().addTransition("AGREE", "FINAL");

		this.addFactoryAsParticipant(talk);
		
		ready.countDown();
		try {
			ready.await();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}

	}

	protected void finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {

		logger.info(finalizeMessage.getContent());
	}

	/**
	 * @return the mode
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * @param mode
	 *            the mode to set
	 */
	public void setMode(int mode) {
		this.mode = mode;
	}
}