package TestCAgents.Agents;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CFactory;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_CONTRACTNET_Initiator;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.MessageFilter;

public class HarrySendingErrorsClass extends CAgent {

	// Variables for testing
	public String informMsg;
	private CountDownLatch finished;
	public String acceptMsg;
	public String refuseMsg;
	private int mode;
	public String rejectMsg;
	public String receiveFailure;
	public String notUnderstood;
	public int received = 0;
	public boolean first = true;

	public HarrySendingErrorsClass(AgentID aid, CountDownLatch finished)
			throws Exception {
		super(aid);
		this.finished = finished;
		informMsg = "";
	}

	protected void execution(CProcessor myProcessor, ACLMessage welcomeMessage) {

		ACLMessage msg;

		class mySending_Error extends SENDING_ERROR_PROTOCOL_Custom {

			/**
			 * Method executed at the beginning of the conversation
			 * 
			 * @param myProcessor
			 *            the CProcessor managing the conversation
			 * @param msg
			 *            first message to send in the conversation
			 */
			protected void doBegin(CProcessor myProcessor, ACLMessage msg) {
				myProcessor.getInternalData().put("InitialMessage", msg);
			}

			/**
			 * Method executed when the initiator calls for proposals
			 * 
			 * @param myProcessor
			 *            the CProcessor managing the conversation
			 * @param messageToSend
			 *            Message to send
			 */
			protected void doSolicitProposals(CProcessor myProcessor,
					ACLMessage messageToSend) {

				if (first) {
					messageToSend.setProtocol("fipa-contract-net");
					messageToSend.setPerformative(ACLMessage.CFP);
					messageToSend.setSender(myProcessor.getMyAgent().getAid());

					int participants = (Integer) myProcessor.getInternalData()
							.get("participants");

					messageToSend
							.setContent("May you give me your phone number?");
					for (int i = 1; i < participants; ++i) {
						messageToSend.addReceiver(new AgentID("Sally" + i));
					}
					informMsg = "cfp";
				} else {
					myProcessor.getMyAgent().Shutdown();
				}

			}

			/**
			 * Method executed when the initiator receives a not-understood
			 * message
			 * 
			 * @param myProcessor
			 *            the CProcessor managing the conversation
			 * @param msg
			 *            not-understood message
			 */
			protected void doReceiveNotUnderstood(CProcessor myProcessor,
					ACLMessage msg) {
				notUnderstood = msg.getPerformative();
			}

			/**
			 * Method executed when the initiator receives a refuse message
			 * 
			 * @param myProcessor
			 *            the CProcessor managing the conversation
			 * @param msg
			 *            refuse message
			 */
			protected void doReceiveRefuse(CProcessor myProcessor,
					ACLMessage msg) {
				refuseMsg = msg.getContent();
			}

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
				informMsg = "timeout";
			}

			//
			// /**
			// * Evaluate proposals. Each accepted proposal has to be added to
			// the acceptances list.
			// * Each rejected proposal has to be added to the rejected list
			// * @param myProcessor the CProcessor managing the conversation
			// * @param proposes Proposals
			// * @param acceptances Accepted proposals
			// * @param rejections Rejected proposals
			// */
			// protected abstract void doEvaluateProposals(CProcessor
			// myProcessor,
			// ArrayList<ACLMessage> proposes, ArrayList<ACLMessage>
			// acceptances, ArrayList<ACLMessage> rejections);
			//
			//

			/**
			 * Method executed when the initiator receives a failure
			 * 
			 * @param myProcessor
			 *            the CProcessor managing the conversation
			 * @param msg
			 *            the failure message
			 */
			protected void doReceiveFailure(CProcessor myProcessor,
					ACLMessage msg) {
				receiveFailure = msg.getContent();
			}

			//
			// /**
			// * Method executed when the initiator receives a inform
			// * @param myProcessor the CProcessor managing the conversation
			// * @param msg inform message
			// */
			// protected abstract void doReceiveInform(CProcessor myProcessor,
			// ACLMessage msg); // Method
			// // to
			// // implement

			/**
			 * Method executed when the initiator ends the conversation
			 * 
			 * @param myProcessor
			 *            the CProcessor managing the conversation
			 * @param messageToSend
			 *            final message of this conversation
			 */
			protected void doFinal(CProcessor myProcessor,
					ACLMessage messageToSend) {
				// messageToSend = myProcessor.getLastSentMessage();
				// informMsg = messageToSend.getContent();
				logger.info(myProcessor.getMyAgent().getName()
						+ " says Goodbye");
				myProcessor.getMyAgent().Shutdown();
			}

			@Override
			protected void doEvaluateProposals(CProcessor myProcessor,
					ArrayList<ACLMessage> proposes,
					ArrayList<ACLMessage> acceptances,
					ArrayList<ACLMessage> rejections) {
				informMsg = "evaluateProposals";

				if (proposes.size() != 0) {

					if (((HarrySendingErrorsClass) myProcessor.getMyAgent())
							.getMode() == 0) {

						ACLMessage messageToSend = new ACLMessage(
								ACLMessage.ACCEPT_PROPOSAL);
						messageToSend.setProtocol("fipa-contract-net");
						for (ACLMessage msg : proposes) {
							logger.error("RCV = > " + msg.getSender());
							messageToSend.addReceiver(msg.getSender());
							messageToSend.setSender(myProcessor.getMyAgent()
									.getAid());
							acceptMsg = "OK";
							messageToSend.setContent(acceptMsg);
							acceptances.add(messageToSend);
						}

					} else {
						// System.out.println(proposes.get(0).getContent());
						ACLMessage messageToSend = new ACLMessage(
								ACLMessage.REJECT_PROPOSAL);
						messageToSend.setProtocol("fipa-contract-net");
						for (ACLMessage msg : proposes) {
							logger.error("RCV = > " + msg.getSender());
							messageToSend.addReceiver(msg.getSender());
							messageToSend.setSender(myProcessor.getMyAgent()
									.getAid());
							rejectMsg = "NO,THANKS";
							messageToSend.setContent(refuseMsg);
							rejections.add(messageToSend);
						}

					}
				}
			}

			@Override
			protected void doReceiveInform(CProcessor myProcessor,
					ACLMessage msg) {

				informMsg = msg.getContent();
			}

		}

		msg = new ACLMessage(ACLMessage.CFP);// REVISAR

		msg.setProtocol("fipa-contract-net");
		msg.setSender(getAid());

		MessageFilter filter = null;
		ACLMessage template = msg;
		int availableConversations = 1;
		int participants = 4;// ?
		long deadline = 1000;
		int timeout = 0;
		CFactory contractnet = new mySending_Error().newFactory("CONTRACTNET",
				filter, template, availableConversations,
				myProcessor.getMyAgent(), participants, deadline, timeout);
		// .newFactory("TALK", null , msg,1, this, 0);
		// REVISAR EL FILTER Y EL TEMPLATE Y PARTICIPANTS

		// The factory is setup to answer start conversation requests from the
		// agent
		// using the REQUEST protocol.

		this.addFactoryAsInitiator(contractnet);

		// finally the new conversation starts. Because it is synchronous,
		// the current interaction halts until the new conversation ends.
		// myProcessor.createSyncConversation(msg);
		this.startSyncConversation("CONTRACTNET");

		myProcessor.ShutdownAgent();
	}

	protected void finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {
		finished.countDown();
	}

	public void setMode(int mode) {
		this.mode = mode;

	}

	public int getMode() {
		return this.mode;

	}
}
