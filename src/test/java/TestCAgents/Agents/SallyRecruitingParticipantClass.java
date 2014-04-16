package TestCAgents.Agents;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.MessageFilter;
import es.upv.dsic.gti_ia.cAgents.ActionStateMethod;
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
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_RECRUITING_Participant;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_REQUEST_Participant;

public class SallyRecruitingParticipantClass extends CAgent {

	// Variables for testing
	public boolean acceptRequests;
	public String refuseMsg;
	private int mode;
	private CountDownLatch finished;
	public String agreeMsg;
	private String receivedMsgFromOther;
	public boolean resultOfSubprotocol;
	private int modeLocate;
	public String informMsg;
	public String failureMsg;
	public String failureNoMatchMsg;

	/**
	 * @return the modeLocate
	 */
	public int getModeLocate() {
		return modeLocate;
	}

	/**
	 * @param modeLocate
	 *            the modeLocate to set
	 */
	public void setModeLocate(int modeLocate) {
		this.modeLocate = modeLocate;
	}

	/**
	 * @return the failureMsg
	 */
	public String getFailureMsg() {
		return failureMsg;
	}

	/**
	 * @param failureMsg
	 *            the failureMsg to set
	 */
	public void setFailureMsg(String failureMsg) {
		this.failureMsg = failureMsg;
	}

	public SallyRecruitingParticipantClass(AgentID aid, CountDownLatch finished)
			throws Exception {
		super(aid);
		this.finished = finished;
		acceptRequests = false;// False until the CFactory gets to the
								// doReceiveRequestMethod
	}

	protected void execution(CProcessor myProcessor, ACLMessage welcomeMessage) {

		logger.info(myProcessor.getMyAgent().getName()
				+ ": the welcome message is " + welcomeMessage.getContent());

		class myFIPA_RECRUITING extends FIPA_RECRUITING_Participant {

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
			 * Sets the refuse message to a proxy action
			 * 
			 * @param myProcessor
			 *            the CProcessor managing the conversation
			 * @param messageToSend
			 *            refuse message
			 */
			protected void doRefuse(CProcessor myProcessor,
					ACLMessage messageToSend) {
				messageToSend.setProtocol("fipa-recruiting");
				messageToSend.setPerformative(ACLMessage.REFUSE);
				// messageToSend.setReceiver(messageToSend.getSender());
				// messageToSend.setSender(myProcessor.getMyAgent().getAid());
				refuseMsg = "Nup";
				messageToSend.setContent(refuseMsg);

			}

			/**
			 * Sets the agree message to a proxy action
			 * 
			 * @param myProcessor
			 *            the CProcessor managing the conversation
			 * @param messageToSend
			 *            agree message
			 */
			protected void doAgree(CProcessor myProcessor,
					ACLMessage messageToSend) {
				messageToSend.setProtocol("fipa-recruiting");
				messageToSend.setPerformative(ACLMessage.AGREE);
				// messageToSend.setReceiver(messageToSend.getSender());
				// messageToSend.setSender(myProcessor.getMyAgent().getAid());
				agreeMsg = "ok, let me see...";
				messageToSend.setContent(agreeMsg);
			}

			/**
			 * Method to execute when there is no agents to recruit
			 * 
			 * @param myProcessor
			 *            the CProcessor managing the conversation
			 * @param messageToSend
			 *            no match message
			 */
			protected void doFailureNoMatch(CProcessor myProcessor,
					ACLMessage messageToSend) {
				failureNoMatchMsg = "Agent not found";
				messageToSend.setContent(failureNoMatchMsg);
			}

			/**
			 * Returns the result of a proxy action
			 * 
			 * @param myProcessor
			 *            the CProcessor managing the conversation
			 * @param subProtocolMessageResult
			 *            result of the subprotocol
			 * @return next conversation message
			 */

			/**
			 * Sets the failure message
			 * 
			 * @param myProcessor
			 *            the CProcessor managing the conversation
			 * @param messageToSend
			 *            failure message
			 */
			protected void doFailureProxy(CProcessor myProcessor,
					ACLMessage messageToSend) {

				failureMsg = "SubProtocol failed :(";
				messageToSend.setContent(failureMsg);
			}

			/**
			 * Sets the inform message
			 * 
			 * @param myProcessor
			 *            the CProcessor managing the conversation
			 * @param messageToSend
			 *            inform message
			 */
			protected void doInform(CProcessor myProcessor,
					ACLMessage messageToSend) {
				informMsg = "Done (by other)";
				messageToSend.setContent(informMsg);
			}

			/**
			 * End of the conversation
			 * 
			 * @param myProcessor
			 *            the CProcessor managing the conversation
			 * @param messageToSend
			 *            final message
			 */
			protected void doFinalRecruitingParticipant(CProcessor myProcessor,
					ACLMessage messageToSend) {
				messageToSend = myProcessor.getLastSentMessage();
				// Para acabar, Sally se apaga
				myProcessor.getMyAgent().Shutdown();
			}

			@Override
			protected String doReceiveProxy(CProcessor myProcessor,
					ACLMessage msg) {
				if (mode == 0) {// Agree mode

					return "AGREE";
				} else {
					// Refuse mode

					return "REFUSE";

				}

			}

			@Override
			protected ArrayList<AgentID> doLocateAgents(CProcessor myProcessor,
					ACLMessage proxyMessage) {
				ArrayList<AgentID> al = new ArrayList<AgentID>();
				if (modeLocate == 0) {// Locate

					al.add(new AgentID("other"));
				}
				return al;

			}

			@Override
			protected boolean resultOfSubProtocol(CProcessor myProcessor,
					ACLMessage subProtocolMessageResult) {
				if (resultOfSubprotocol) {

					subProtocolMessageResult.setContent("Success :)");
				} else {
					informMsg = "SubProtocol failed :(";
					subProtocolMessageResult.setContent(informMsg);
				}
				return resultOfSubprotocol;
			}

		}

		CFactory recruiting = new myFIPA_RECRUITING().newFactory("RECRUITING",
				null, null, 0, myProcessor.getMyAgent());

		this.addFactoryAsParticipant(recruiting);

		MessageFilter filter;

		// We create a factory in order to send a propose and wait for the
		// answer

		filter = new MessageFilter("performative = PROPOSE");

		CFactory talk = new CFactory("Is anyone there?", filter, 1,
				myProcessor.getMyAgent());

		// A CProcessor always starts in the predefined state BEGIN.
		// We have to associate this state with a method that will be
		// executed at the beginning of the conversation.

		// /////////////////////////////////////////////////////////////////////////////
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

		// /////////////////////////////////////////////////////////////////////////////
		// PURPOSE state

		SendState PURPOSE = new SendState("PURPOSE");

		class PURPOSE_Method implements SendStateMethod {
			public String run(CProcessor myProcessor, ACLMessage messageToSend) {
				messageToSend.setPerformative(ACLMessage.PROPOSE);
				messageToSend.setReceiver(new AgentID("other"));
				messageToSend.setSender(myProcessor.getMyAgent().getAid());
				messageToSend.setContent("Will you come with me to a movie?");
				logger.info(myProcessor.getMyAgent().getName()
						+ " : I tell " + messageToSend.getReceiver().name + " "
						+ messageToSend.getPerformative() + " "
						+ messageToSend.getContent());

				return "WAIT";
			}
		}
		PURPOSE.setMethod(new PURPOSE_Method());

		talk.cProcessorTemplate().registerState(PURPOSE);
		talk.cProcessorTemplate().addTransition("BEGIN", "PURPOSE");

		// /////////////////////////////////////////////////////////////////////////////
		// WAIT State

		talk.cProcessorTemplate().registerState(new WaitState("WAIT", 0));
		talk.cProcessorTemplate().addTransition("PURPOSE", "WAIT");

		// /////////////////////////////////////////////////////////////////////////////
		// RECEIVE State

		ReceiveState RECEIVE = new ReceiveState("RECEIVE");

		class RECEIVE_Method implements ReceiveStateMethod {

			public String run(CProcessor myProcessor, ACLMessage messageReceived) {
				receivedMsgFromOther = messageReceived.getPerformative() + ": "
						+ messageReceived.getContent();
				return "FINAL";
			}
		}

		RECEIVE.setAcceptFilter(null); // null -> accept any message
		RECEIVE.setMethod(new RECEIVE_Method());
		talk.cProcessorTemplate().registerState(RECEIVE);
		talk.cProcessorTemplate().addTransition("WAIT", "RECEIVE");

		// /////////////////////////////////////////////////////////////////////////////
		// FINAL state

		FinalState FINAL = new FinalState("FINAL");

		class FINAL_Method implements FinalStateMethod {
			public void run(CProcessor myProcessor, ACLMessage messageToReturn) {
				messageToReturn.copyFromAsTemplate(myProcessor
						.getLastReceivedMessage());
				// myProcessor.ShutdownAgent();
			}
		}
		FINAL.setMethod(new FINAL_Method());

		talk.cProcessorTemplate().registerState(FINAL);
		talk.cProcessorTemplate().addTransition(RECEIVE, FINAL);
		talk.cProcessorTemplate().addTransition("PURPOSE", "FINAL");

		// /////////////////////////////////////////////////////////////////////////////

		// The template processor is ready. We add the factory, in this case as
		// a initiator one

		this.addFactoryAsInitiator(talk);

	}

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
	 * @return the informMsg
	 */
	public String getInformMsg() {
		return informMsg;
	}

	/**
	 * @param informMsg
	 *            the informMsg to set
	 */
	public void setInformMsg(String informMsg) {
		this.informMsg = informMsg;
	}

	/**
	 * @return the resultOfSubprotocol
	 */
	public boolean isResultOfSubprotocol() {
		return resultOfSubprotocol;
	}

	/**
	 * @param resultOfSubprotocol
	 *            the resultOfSubprotocol to set
	 */
	public void setResultOfSubprotocol(int resultOfSubprotocol) {
		this.resultOfSubprotocol = resultOfSubprotocol == 0;
	}

	protected void finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {
		finished.countDown();
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
