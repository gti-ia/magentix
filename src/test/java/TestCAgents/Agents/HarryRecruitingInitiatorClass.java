package TestCAgents.Agents;

import java.util.concurrent.CountDownLatch;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.MessageFilter;
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
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_RECRUITING_Initiator;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_REQUEST_Initiator;

/**
 * Initiator factory class for the test Recruiting
 * 
 * @author Javier Jorge - jjorge@dsic.upv.es
 */

public class HarryRecruitingInitiatorClass extends CAgent {

	// Variables for testing
	public String informMsg;
	public String refuseMsg;
	private CountDownLatch finished;
	private CountDownLatch ready;
	public String agreeMsg;
	public String receivedMsgFromProxy;
	public String failureNoMatchMsg;

	/**
	 * @return the failureNoMatchMsg
	 */
	public String getFailureNoMatchMsg() {
		return failureNoMatchMsg;
	}

	/**
	 * @param failureNoMatchMsg
	 *            the failureNoMatchMsg to set
	 */
	public void setFailureNoMatchMsg(String failureNoMatchMsg) {
		this.failureNoMatchMsg = failureNoMatchMsg;
	}

	/**
	 * @return the failureProxyMsg
	 */
	public String getFailureProxyMsg() {
		return failureProxyMsg;
	}

	/**
	 * @param failureProxyMsg
	 *            the failureProxyMsg to set
	 */
	public void setFailureProxyMsg(String failureProxyMsg) {
		this.failureProxyMsg = failureProxyMsg;
	}

	public String failureProxyMsg;

	public HarryRecruitingInitiatorClass(AgentID aid, CountDownLatch finished, CountDownLatch ready)
			throws Exception {
		super(aid);
		
		this.finished = finished;
		this.ready = ready;
		informMsg = "";
	}

	protected void execution(CProcessor myProcessor, ACLMessage welcomeMessage) {

		ACLMessage msg;

		logger.info(myProcessor.getMyAgent().getName()
				+ ": the welcome message is " + welcomeMessage.getContent());

		class myFIPA_RECRUITING extends FIPA_RECRUITING_Initiator {

			protected void doBegin(CProcessor myProcessor, ACLMessage msg) {
				myProcessor.getInternalData().put("InitialMessage", msg);
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
				logger.info("Ok Sally :(");
				refuseMsg = msg.getContent();
			}

			/**
			 * Method executed when the initiator receives an agree message
			 * 
			 * @param myProcessor
			 *            the CProcessor managing the conversation
			 * @param msg
			 *            agree message
			 */
			protected void doReceiveAgree(CProcessor myProcessor, ACLMessage msg) {
				logger.info("I'm waiting for response...");
				agreeMsg = msg.getContent();

			}

			/**
			 * Method executed when the initiator receives a proxy failure
			 * message
			 * 
			 * @param myProcessor
			 *            the CProcessor managing the conversation
			 * @param msg
			 *            proxy failure message
			 */
			protected void doReceiveFailureProxy(CProcessor myProcessor,
					ACLMessage msg) {
				failureProxyMsg = msg.getContent();
				logger.info("Proxy action failed");
			}

			/**
			 * Method executed when the initiator receives a no match message
			 * 
			 * @param mmyProcessor
			 *            the CProcessor managing the conversation
			 * @param msg
			 *            no match message
			 */
			protected void doReceiveFailureNoMatch(CProcessor myProcessor,
					ACLMessage msg) {
				logger.info("No agent match found");
				failureNoMatchMsg = msg.getContent();
			}

			/**
			 * Method executed when the initiator receives an inform message
			 * 
			 * @param myProcessor
			 *            the CProcessor managing the conversation
			 * @param msg
			 *            inform message
			 */
			protected void doReceiveInform(CProcessor myProcessor,
					ACLMessage msg) {
				informMsg = msg.getContent();
				logger.info("Proxy worked");
			}

			/**
			 * Method executed when the conversation ends
			 * 
			 * @param myProcessor
			 *            the CProcessor managing the conversation
			 * @param messageToSend
			 *            final message
			 */
			protected void doFinalRecruitingInitiator(CProcessor myProcessor,
					ACLMessage messageToSend) {
				messageToSend = myProcessor.getLastSentMessage();

				myProcessor.getMyAgent().Shutdown();
			}

			@Override
			protected void setProxyMessage(CProcessor myProcessor,
					ACLMessage messageToSend) {
				messageToSend.setProtocol("fipa-recruiting");
				messageToSend.setPerformative(ACLMessage.PROPOSE);
				messageToSend.setReceiver(new AgentID("Sally"));
				messageToSend.setSender(myProcessor.getMyAgent().getAid());
				messageToSend.setContent("Is anyone there?");

			}

		}

		msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setReceiver(new AgentID("Sally"));
		msg.setContent("May you give me your phone number?");
		msg.setProtocol("fipa-recruiting");
		msg.setSender(getAid());

		CFactory recruiting = new myFIPA_RECRUITING().newFactory("RECRUITING",
				null, msg, 1, this, 0);

		this.addFactoryAsInitiator(recruiting);
		
		ready.countDown();
		try {
			ready.await();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}

		this.startSyncConversation("RECRUITING");

	}

	protected void finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {
		finished.countDown();
	}
	

	/**
	 * @return the finished
	 */
	public CountDownLatch getFinished() {
		return finished;
	}

	/**
	 * @param finished the finished to set
	 */
	public void setFinished(CountDownLatch finished) {
		this.finished = finished;
	}

	/**
	 * @return the ready
	 */
	public CountDownLatch getReady() {
		return ready;
	}

	/**
	 * @param ready the ready to set
	 */
	public void setReady(CountDownLatch ready) {
		this.ready = ready;
	}

}
