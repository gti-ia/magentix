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

	public SallyRecruitingParticipantClass(AgentID aid, CountDownLatch finished) throws Exception {
		super(aid);
		this.finished = finished;
		acceptRequests = false;// False until the CFactory gets to the
								// doReceiveRequestMethod
	}

	protected void execution(CProcessor myProcessor, ACLMessage welcomeMessage) {

		System.out.println(myProcessor.getMyAgent().getName()
				+ ": the welcome message is " + welcomeMessage.getContent());

		// Each agent's conversation is carried out by a CProcessor.
		// CProcessors are created by the CFactories in response
		// to messages that start the agent's activity in a conversation

		// An easy way to create CFactories is to create them from the
		// predefined factories of package es.upv.dsi.gri_ia.cAgents.protocols
		// Another option, not shown in this example, is that the agent
		// designs her own factory and, therefore, a new interaction protocol

		// In this example the agent is going to act as the participant in
		// REQUESt protocol defined by FIPA.
		// In order to do so, she has to extend the class
		// FIPA_REQUEST_Participant
		// implementing the method that receives the request (doRequest),
		// the method that carries out the request (doAction) and
		// the method that generates the answer (doInform)

		class myFIPA_RECRUITING extends FIPA_RECRUITING_Participant {

			private int modeLocate;

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
			 * Method executed when the participant receive a message to proxy
			 * 
			 * @param myProcessor
			 *            the CProcessor managing the conversation
			 * @param msg
			 *            proxy message
			 * @return next conversation state
			 */
			// protected abstract String doReceiveProxy(CProcessor myProcessor,
			// ACLMessage msg);

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
				//messageToSend.setReceiver(messageToSend.getSender());
				//messageToSend.setSender(myProcessor.getMyAgent().getAid());
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
				//messageToSend.setReceiver(messageToSend.getSender());
				//messageToSend.setSender(myProcessor.getMyAgent().getAid());
				agreeMsg = "ok, let me see...";
				messageToSend.setContent(agreeMsg);
			}

			// /**
			// * Locate agents to recruit
			// * @param myProcessor the CProcessor managing the conversation
			// * @param proxyMessage proxy message sent by the initiator
			// * @return next conversation state
			// */
			// protected abstract ArrayList<AgentID> doLocateAgents(CProcessor
			// myProcessor, ACLMessage proxyMessage);
			//
			// class LOCATE_AGENTS_Method implements ActionStateMethod{
			// @Override
			// public String run(CProcessor myProcessor) {
			// ACLMessage proxyMessage = (ACLMessage)
			// myProcessor.getInternalData().get("proxyMessage");
			// ArrayList<AgentID> locatedAgents = doLocateAgents(myProcessor,
			// proxyMessage);
			// if(locatedAgents.size() == 0)
			// return "FAILURE_NO_MATCH";
			// else{
			// myProcessor.getInternalData().put("locatedAgents",
			// locatedAgents);
			// return "START_SUB_PROTOCOL";
			// }
			// }
			// }

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
			// protected abstract boolean resultOfSubProtocol(CProcessor
			// myProcessor, ACLMessage subProtocolMessageResult);

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
				//Para acabar, Sally se apaga
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
				ArrayList<AgentID> al =  new ArrayList<AgentID>();
				if(modeLocate == 0){//Locate
					//proxyMessage.setContent("TALK");
					al.add(new AgentID("other"));
				}
				return al;
				
			}

			@Override
			protected boolean resultOfSubProtocol(CProcessor myProcessor,
					ACLMessage subProtocolMessageResult) {
				// TODO Auto-generated method stub
				return false;
			}

			/**
			 * @return the modeLocate
			 */
			public int getModeLocate() {
				return modeLocate;
			}

			/**
			 * @param modeLocate the modeLocate to set
			 */
			public void setModeLocate(int modeLocate) {
				this.modeLocate = modeLocate;
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

		// REVISAR FILTER => null, template no se usa, eliminar del constructor?
		CFactory recruiting = new myFIPA_RECRUITING().newFactory("RECRUITING",
				null, null, 0, myProcessor.getMyAgent());
		// .newFactory("TALK", null,0, myProcessor.getMyAgent());

		// Finally the factory is setup to answer to incoming messages that
		// can start the participation of the agent in a new conversation
		this.addFactoryAsParticipant(recruiting);
		
		MessageFilter filter;
		
		// We create a factory in order to send a propose and wait for the answer

		filter = new MessageFilter("performative = PROPOSE");
		
		CFactory talk = new CFactory("Is anyone there?", filter, 1,
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
				messageToSend.setPerformative(ACLMessage.PROPOSE);
				messageToSend.setReceiver(new AgentID("other"));
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
				receivedMsgFromOther=messageReceived.getPerformative()+": "+messageReceived.getContent();
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
				//myProcessor.ShutdownAgent();
			}
		}
		FINAL.setMethod(new FINAL_Method());

		talk.cProcessorTemplate().registerState(FINAL);
		talk.cProcessorTemplate().addTransition(RECEIVE, FINAL);
		talk.cProcessorTemplate().addTransition("PURPOSE", "FINAL");

		///////////////////////////////////////////////////////////////////////////////
		
		// The template processor is ready. We add the factory, in this case as a initiator one

		this.addFactoryAsInitiator(talk);
		

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
