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


/**
 * Initiator factory class for the test ContractNet protocol
 * 
 * @author Javier Jorge - jjorge@dsic.upv.es
 */

public class HarryContractNetInitiatorClass extends CAgent {

	// Variables for testing
	public String informMsg;
	private CountDownLatch finished;
	public String acceptMsg;
	public String refuseMsg;
	private int mode;
	public String rejectMsg;
	public String receiveFailure;
	public String notUnderstood;
	public String timeOutMsg;
	
	public final int ACCEPT = 0;
	public final int REJECT = 1;
	public final int REFUSE = 1;
	public final int PASS = 2;

	/**
	 * @return the timeOutMsg
	 */
	public String getTimeOutMsg() {
		return timeOutMsg;
	}

	/**
	 * @param timeOutMsg
	 *            the timeOutMsg to set
	 */
	public void setTimeOutMsg(String timeOutMsg) {
		this.timeOutMsg = timeOutMsg;
	}

	public HarryContractNetInitiatorClass(AgentID aid, CountDownLatch finished)
			throws Exception {
		super(aid);
		this.finished = finished;
		informMsg = "";
	}

	protected void execution(CProcessor myProcessor, ACLMessage welcomeMessage) {

		ACLMessage msg;

		class myFIPA_CONTRACTNET extends FIPA_CONTRACTNET_Initiator {

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
				messageToSend.setProtocol("fipa-contract-net");
				messageToSend.setPerformative(ACLMessage.CFP);
				messageToSend.setSender(myProcessor.getMyAgent().getAid());
				informMsg = "cfp";
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

					if (((HarryContractNetInitiatorClass) myProcessor
							.getMyAgent()).getMode() != PASS) {

						if (((HarryContractNetInitiatorClass) myProcessor
								.getMyAgent()).getMode() == ACCEPT) {
							
							ACLMessage messageToSend = new ACLMessage(
									ACLMessage.ACCEPT_PROPOSAL);
							messageToSend.setProtocol("fipa-contract-net");
							messageToSend.setReceiver(proposes.get(0)
									.getSender());
							messageToSend.setSender(myProcessor.getMyAgent()
									.getAid());
							acceptMsg = "OK";
							messageToSend.setContent(acceptMsg);

							acceptances.add(messageToSend);
						} else {

							ACLMessage messageToSend = new ACLMessage(
									ACLMessage.REJECT_PROPOSAL);
							messageToSend.setProtocol("fipa-contract-net");
							messageToSend.setReceiver(proposes.get(0)
									.getSender());
							messageToSend.setSender(myProcessor.getMyAgent()
									.getAid());
							rejectMsg = "NO,THANKS";

							messageToSend.setContent(rejectMsg);

							rejections.add(messageToSend);

						}
					} else {
						timeOutMsg = "mmmm...no";
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
		msg.setReceiver(new AgentID("Sally"));
		msg.setContent("May you give me your phone number?");
		msg.setProtocol("fipa-contract-net");
		msg.setSender(getAid());

		MessageFilter filter = null;
		ACLMessage template = msg;
		int availableConversations = 1;
		int participants = 2;// ?
		long deadline = 1000;
		int timeout = 0;
		CFactory contractnet = new myFIPA_CONTRACTNET().newFactory(
				"CONTRACTNET", filter, template, availableConversations,
				myProcessor.getMyAgent(), participants, deadline, timeout);
		this.addFactoryAsInitiator(contractnet);

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
