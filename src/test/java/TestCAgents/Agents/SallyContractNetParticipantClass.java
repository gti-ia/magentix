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
	public String timeOutMsg;

	public SallyContractNetParticipantClass(AgentID aid, CountDownLatch finished)
			throws Exception {
		super(aid);
		this.finished = finished;
		acceptRequests = false;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getMode() {
		return this.mode;
	}

	protected void execution(CProcessor myProcessor, ACLMessage welcomeMessage) {

		logger.info(myProcessor.getMyAgent().getName()
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
				timeOutMsg = msg.getPerformative();
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
				System.out.println("SALIENDO....");
				messageToSend = myProcessor.getLastSentMessage();
				myProcessor.getMyAgent().Shutdown();
			}

			@Override
			protected String doReceiveSolicit(CProcessor myProcessor,
					ACLMessage msg) {
				logger.info("ME LLEGA UNA PETICION");
				proposal = msg.getContent();
				
				if (mode == PROPOSE) {
					return "SEND_PROPOSAL";
				} else {
					refuseMsg = "refuse";
					return "SEND_REFUSE";
				}

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

				if (FAIL) {
					logger.info("Error");
					receiveFailure = "Error";
					return "SEND_FAILURE";
				} else{
					return "SEND_INFORM";
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

		MessageFilter filter = null;
		ACLMessage template = null;
		int availableConversations = 1;
		int timeout = 10000;
		CFactory contractnet = new myFIPA_CONTRACTNET().newFactory(
				"CONTRACTNET", filter, template, availableConversations,
				myProcessor.getMyAgent(), timeout);

		this.addFactoryAsParticipant(contractnet);
		
	}

	protected void finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {
		
		finished.countDown();
	}
}
