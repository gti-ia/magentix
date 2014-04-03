package TestCAgents.Agents;

import java.util.concurrent.CountDownLatch;

import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CFactory;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_CONTRACTNET_Participant;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.MessageFilter;

public class SallyContractNetParticipantClass extends CAgent {

	// Variables for testing
	public boolean acceptRequests;
	public final int PROPOSE = 0;
	public final int REFUSE = 1;
	public int mode = PROPOSE;

	public String proposal;
	public String informMsg;
	private CountDownLatch finished;
	public String refuseMsg;
	public String rejectMsg;
	public boolean FAIL = false;
	public String receiveFailure;
	public String notUnderstood;
	public String acceptMsg;

	public SallyContractNetParticipantClass(AgentID aid, CountDownLatch finished)
			throws Exception {
		super(aid);
		this.finished = finished;
		acceptRequests = false;// False until the CFactory gets to the
								// doReceiveRequestMethod
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getMode() {
		return this.mode;
	}

	protected void execution(CProcessor myProcessor, ACLMessage welcomeMessage) {

		System.out.println(myProcessor.getMyAgent().getName()
				+ ": the welcome message is " + welcomeMessage.getContent());


		class myFIPA_CONTRACTNET extends FIPA_CONTRACTNET_Participant {

			

			/**
			 * Method executed at the beginning of the conversation
			 * 
			 * @param myProcessor
			 *            the CProcessor managing the conversation
			 * @param msg
			 *            first message assigned to this conversation
			 */
			protected void doBegin(CProcessor myProcessor, ACLMessage msg) {
				myProcessor.getInternalData().put("InitialMessage", msg);
			}

			// /**
			// * Method executed when the participant receives a call for
			// * proposals
			// *
			// * @param myProcessor
			// * the CProcessor managing the conversation
			// * @param msg
			// * call for proposals message
			// * @return next state of this conversation
			// */
			// protected abstract String doReceiveSolicit(CProcessor
			// myProcessor,
			// ACLMessage msg);
			//
			// /**
			// * Method executed when the participant sends a proposal
			// *
			// * @param myProcessor
			// * the CProcessor managing the conversation
			// * @param messageToSend
			// * proposal message
			// */
			// protected abstract void doSendProposal(CProcessor myProcessor,
			// ACLMessage messageToSend);

			/**
			 * Method executed when the timeout is reached while the initiator
			 * was waiting for proposals
			 * 
			 * @param myProcessor
			 *            the CProcessor managing the conversation
			 * @param msg
			 *            timeout message
			 */
			protected void doTimeout(CProcessor myProcessor, ACLMessage msg) {
			}

			/**
			 * Method executed when the initiator accepts participant's proposal
			 * 
			 * @param myProcessor
			 *            the CProcessor managing the conversation
			 * @param msg
			 *            accept message
			 */
			protected void doReceiveAccept(CProcessor myProcessor,
					ACLMessage msg) {
				acceptMsg = msg.getContent();
			}

			/**
			 * Method executed when the initiator rejects participant's proposal
			 * 
			 * @param myProcessor
			 *            the CProcessor managing the conversation
			 * @param msg
			 *            reject message
			 */
			protected void doReceiveReject(CProcessor myProcessor,
					ACLMessage msg) {
				
				rejectMsg = msg.getContent();
				
			}

			// /**
			// * Perform the proposal's task
			// *
			// * @param myProcessor
			// * the CProcessor managing the conversation
			// * @param solicitMessage
			// * the first message assigned to this conversation
			// * containing the solicit of the initiator agent
			// * @return next conversation state
			// */
			// protected abstract String doTask(CProcessor myProcessor,
			// ACLMessage solicitMessage);

			/**
			 * Method executed when the task failed
			 * 
			 * @param myProcessor
			 *            the CProcessor managing the conversation
			 * @param messageToSend
			 *            fail message
			 */
			protected void doSendFailure(CProcessor myProcessor,
					ACLMessage messageToSend) {
				ACLMessage aux = (ACLMessage) myProcessor.getInternalData()
						.get("InitialMessage");
				messageToSend.copyFromAsTemplate(aux);
				messageToSend.setProtocol("fipa-contract-net");
				messageToSend.setPerformative(ACLMessage.FAILURE);
				messageToSend.setReceiver(myProcessor.getLastReceivedMessage()
						.getSender());
				messageToSend.setSender(myProcessor.getMyAgent().getAid());
				messageToSend.setContent("Error");
			}

			// /**
			// * Method executed when the task succeeded
			// *
			// * @param myProcessor
			// * the CProcessor managing the conversation
			// * @param messageToSend
			// * inform message
			// */
			// protected abstract void doSendInfo(CProcessor myProcessor,
			// ACLMessage messageToSend);

			/**
			 * Method executed when the conversation ends
			 * 
			 * @param myProcessor
			 *            the CProcessor managing the conversation
			 * @param messageToSend
			 *            final message
			 */
			protected void doFinal(CProcessor myProcessor,
					ACLMessage messageToSend) {
				messageToSend = myProcessor.getLastSentMessage();
				myProcessor.getMyAgent().Shutdown();
			}

			@Override
			protected String doReceiveSolicit(CProcessor myProcessor,
					ACLMessage msg) {
				System.out.println("ME LLEGA UNA PETICION");
				proposal = msg.getContent();
				if (mode == PROPOSE) {
					return "SEND_PROPOSAL";
				} else {
					refuseMsg = "refuse";
					return "SEND_REFUSE";
				}
				// DETERMINAR YO QUE PASO A
				// return null;
			}

			@Override
			protected void doSendProposal(CProcessor myProcessor,
					ACLMessage messageToSend) {

				messageToSend.setProtocol("fipa-contract-net");
				messageToSend.setPerformative(ACLMessage.PROPOSE);
				messageToSend.setReceiver(myProcessor.getLastReceivedMessage()
						.getSender());
				messageToSend.setSender(myProcessor.getMyAgent().getAid());
				messageToSend.setContent("OK");
			}

			@Override
			protected String doTask(CProcessor myProcessor,
					ACLMessage solicitMessage) {
				logger.info(myProcessor.getMyAgent().getName()
						+ " carry on the task...");
				try {
					Thread.sleep(6 * 1000);
				} catch (InterruptedException e) {

				}
				
				boolean failure = FAIL;
				boolean done = true;

				if (failure) {
					System.out.println("Error");
					receiveFailure = "Error";
					return "SEND_FAILURE";
				} else if (done) {
					return "SEND_INFORM";
				} else {// RESULTS??
					return "SEND_INFORM"; //
				}
			}

			@Override
			protected void doSendInfo(CProcessor myProcessor,
					ACLMessage messageToSend) {

				messageToSend.setProtocol("fipa-contract-net");
				messageToSend.setPerformative(ACLMessage.INFORM);
				messageToSend.setReceiver(myProcessor.getLastReceivedMessage()
						.getSender());
				messageToSend.setSender(myProcessor.getMyAgent().getAid());
				messageToSend.setContent("COMPLETE");
				informMsg = "COMPLETE";

			}

		}

		// The agent creates the CFactory that manages every message which its
		// performative is set to REQUEST and protocol set to REQUEST. In this
		// example the CFactory gets the name "TALK", we don't add any
		// additional message acceptance criterion other than the required
		// by the REQUEST protocol (null) and we limit the number of
		// simultaneous
		// processors to 1, i.e. the requests will be attended one after
		// another.

		MessageFilter filter = null;
		ACLMessage template = null;
		int availableConversations = 1;
		int timeout = 0;
		CFactory contractnet = new myFIPA_CONTRACTNET().newFactory(
				"CONTRACTNET", filter, template, availableConversations,
				myProcessor.getMyAgent(), timeout);
		// .newFactory("CONTRACTNET", null, 0, myProcessor.getMyAgent());

		// Finally the factory is setup to answer to incoming messages that
		// can start the participation of the agent in a new conversation
		this.addFactoryAsParticipant(contractnet);

	}

	protected void finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {
		finished.countDown();
	}
}
