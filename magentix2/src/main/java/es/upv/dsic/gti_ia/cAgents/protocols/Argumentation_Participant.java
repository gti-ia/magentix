package es.upv.dsic.gti_ia.cAgents.protocols;

import es.upv.dsic.gti_ia.cAgents.ActionState;
import es.upv.dsic.gti_ia.cAgents.BeginState;
import es.upv.dsic.gti_ia.cAgents.BeginStateMethod;
import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CFactory;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.FinalState;
import es.upv.dsic.gti_ia.cAgents.FinalStateMethod;
import es.upv.dsic.gti_ia.cAgents.ReceiveState;
import es.upv.dsic.gti_ia.cAgents.ReceiveStateMethod;
import es.upv.dsic.gti_ia.cAgents.SendState;
import es.upv.dsic.gti_ia.cAgents.SendStateMethod;
import es.upv.dsic.gti_ia.cAgents.WaitState;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_CONTRACTNET_Participant.BEGIN_Method;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_CONTRACTNET_Participant.DO_TASK_Method;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_CONTRACTNET_Participant.FINAL_Method;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_CONTRACTNET_Participant.RECEIVE_ACCEPT_Method;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_CONTRACTNET_Participant.RECEIVE_REJECT_Method;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_CONTRACTNET_Participant.RECEIVE_SOLICIT_Method;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_CONTRACTNET_Participant.SEND_FAILURE_Method;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_CONTRACTNET_Participant.SEND_INFO_Method;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_CONTRACTNET_Participant.SEND_NOT_UNDERSTOOD_Method;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_CONTRACTNET_Participant.SEND_PROPOSAL_Method;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_CONTRACTNET_Participant.SEND_REFUSE_Method;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.MessageFilter;

public abstract class Argumentation_Participant {

	private final String OPENDIALOGUE="OPENDIALOGUE";
	private final String ENTERDIALOGUE="ENTERDIALOGUE";
	private final String LEAVEDIALOGUE="LEAVEDIALOGUE";
	private final String FINISHDIALOGUE="FINISHDIALOGUE";
	private final String WITHDRAWDIALOGUE="WITHDRAWDIALOGUE";
	private final String PROPOSE="PROPOSE";
	private final String WHY="WHY";
	private final String NOCOMMIT="NOCOMMIT";
	private final String ASSERT="ASSERT";
	private final String ACCEPT="ACCEPT";
	private final String ATTACK="ATTACK";
	private final String RETRACT="RETRACT";
	private final String DIE="DIE";
	
	private final String LOCUTION="locution";
	
	
	protected void doBegin(CProcessor myProcessor, ACLMessage msg) {
		myProcessor.getInternalData().put("InitialMessage", msg); //TODO ??
	}
	
	class Begin_Method implements BeginStateMethod {
		public String run(CProcessor myProcessor, ACLMessage msg) {
			doBegin(myProcessor, msg);
			return "OPEN";
		};
	}
	
	
	class Open_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			
			if(messageReceived.getHeaderValue(LOCUTION).equalsIgnoreCase(DIE))
				return "DIE";
			else if(messageReceived.getHeaderValue(LOCUTION).equalsIgnoreCase(OPENDIALOGUE))
				return "ENTER";
			else return "OPEN";
		};
	}
	
	
	/**
	 * Evaluates if the agent can enter in the dialogue offering a solution. If it can't, it does a withdraw dialogue.
	 * @param myProcessor
	 * @param msg
	 * @return
	 */
	protected abstract boolean doEnterDialogue(CProcessor myProcessor, ACLMessage msg);
	
	
	class Enter_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			boolean enterDialogue=doEnterDialogue(myProcessor, messageReceived);
			if(enterDialogue)
				return "PROPOSE";
			else return "OPEN";
		};
	}
	
	/**
	 * Proposes a position to defend in the dialogue. If it can't, it does a withdraw dialogue.
	 * @param myProcessor
	 * @param msg
	 * @return
	 */
	protected abstract boolean doPropose(CProcessor myProcessor, ACLMessage msg);
	
	
	class Propose_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			boolean propose=doPropose(myProcessor, messageReceived);
			if(propose)
				return "CENTRAL";
			else return "OPEN";
		};
	}
	
	
	protected abstract boolean doFinishDialogue(CProcessor myProcessor, ACLMessage msg);
	
	
	class Central_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			if(messageReceived.getHeaderValue(LOCUTION).equalsIgnoreCase(WHY))
				return "ASSERT"; // TODO no esta clar este canvi, evaluar abans si podré fer l'assert?
			else if(messageReceived.getHeaderValue(LOCUTION).equalsIgnoreCase(FINISHDIALOGUE)){
				doFinishDialogue(myProcessor,messageReceived);
				return "OPEN";
			}
			else return "OPEN";//TODO per fer
		};
	}
	
	//TODO seguisc a partir d'ací
	
	
	protected abstract boolean doWhy(CProcessor myProcessor, ACLMessage msg);
	
	protected abstract boolean doAssert(CProcessor myProcessor, ACLMessage msg);
	
	class Three_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			myProcessor.getInternalData().put("solicitMessage", messageReceived);
			
			if(messageReceived.getHeaderValue(LOCUTION).equalsIgnoreCase(WHY)){
				doAssert(myProcessor, messageReceived);
				return "4";
			}
			else if(messageReceived.getHeaderValue(LOCUTION).equalsIgnoreCase(DIE)){
				
				return "final";
			}
			else{
				boolean why=doWhy(myProcessor, messageReceived);
				if(why)
					return "6";
				else return "3";
			}
		}
	}
	
	
	protected abstract boolean doAttack(CProcessor myProcessor, ACLMessage msg);
	
	protected abstract boolean doRetractAssert(CProcessor myProcessor, ACLMessage msg);
	
	
	
	class Four_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			myProcessor.getInternalData().put("solicitMessage", messageReceived);
			
			if(messageReceived.getHeaderValue(LOCUTION).equalsIgnoreCase(ATTACK)){
				boolean attack=doAttack(myProcessor, messageReceived);
				if(attack)
					return "5";
				else{
					doRetractAssert(myProcessor, messageReceived);
					return "3";
				}
					
			}
			else{ //accept
				
				return "3";
			}
		}
	}
	
	
	protected abstract boolean doRetractAttack(CProcessor myProcessor, ACLMessage msg);
	
	
	class Five_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			myProcessor.getInternalData().put("solicitMessage", messageReceived);
			
			if(messageReceived.getHeaderValue(LOCUTION).equalsIgnoreCase(ATTACK)){
				boolean attack=doAttack(myProcessor, messageReceived);
				if(attack)
					return "5";
				else{
					doRetractAttack(myProcessor, messageReceived);
					return "4";
				}
					
			}
			else{ //accept
				
				return "4"; //??? 
			}
		}
	}
	
	
	
	class Six_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			myProcessor.getInternalData().put("solicitMessage", messageReceived);
			
			if(messageReceived.getHeaderValue(LOCUTION).equalsIgnoreCase(ASSERT)){
				boolean attack=doAttack(myProcessor, messageReceived);
				if(attack)
					return "7";
				else{
					
					//TODO
					return "3";
				}
					
			}
			else{ //no_commit
				
				return "3";
			}
		}
	}
	
	
	
	class Seven_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			myProcessor.getInternalData().put("solicitMessage", messageReceived);
			
			if(messageReceived.getHeaderValue(LOCUTION).equalsIgnoreCase(ATTACK)){
				boolean attack=doAttack(myProcessor, messageReceived);
				if(attack)
					return "7";
				else{
					doRetractAttack(myProcessor, messageReceived);
					return "6";
				}
					
			}
			else{ //accept
				
				return "6"; //??? 
			}
		}
	}
	
	
	/**
	 * Method executed when the conversation ends
	 * @param myProcessor the CProcessor managing the conversation
	 * @param messageToSend final message
	 */
	protected void doFinal(CProcessor myProcessor, ACLMessage messageToSend) {
		messageToSend = myProcessor.getLastSentMessage();
	}
	
	
	class FINAL_Method implements FinalStateMethod {
		public void run(CProcessor myProcessor, ACLMessage messageToSend) {
			doFinal(myProcessor, messageToSend);
		}
	}
	
	
	
	
	
	
	
	/**
	 * Creates a new argumentation participant CFactory
	 * @param name factory's name
	 * @param filter message filter
	 * @param template first message to send
	 * @param availableConversations maximum number of conversation this CFactory can manage simultaneously
	 * @param myAgent agent owner of this CFactory
	 * @param timeout for waiting after sending the proposal
	 * @return a new argumentation participant factory
	 */
	public CFactory newFactory(String name, MessageFilter filter,
			ACLMessage template, int availableConversations, CAgent myAgent, int timeout) {

		// Create factory

		if (filter == null) {
			filter = new MessageFilter("performative = CFP"); // falta AND
																	// protocol
																	// =
																	// fipa-contract-net;
		}
		
		if (template == null){
			template = new ACLMessage(ACLMessage.PROPOSE);
		}
		CFactory theFactory = new CFactory(name, filter,
				availableConversations, myAgent);

		// Processor template setup

		CProcessor processor = theFactory.cProcessorTemplate();

		// BEGIN State

		BeginState BEGIN = (BeginState) processor.getState("BEGIN");
		BEGIN.setMethod(new Begin_Method());
		
		// WAIT_FOR_SOLICIT State
		WaitState OPEN_DIALOGUE = new WaitState("OPEN_DIALOGUE", timeout);
		processor.registerState(OPEN_DIALOGUE);
		processor.addTransition(BEGIN, OPEN_DIALOGUE);
		
		// RECEIVE_SOLICIT State

		ReceiveState ONE = new ReceiveState("1");
		ONE.setMethod(new One_Method());
		filter = new MessageFilter("performative = CFP");
//		RECEIVE_SOLICIT.setAcceptFilter(filter);
		processor.registerState(ONE);
		processor.addTransition(OPEN_DIALOGUE,ONE);

		// SEND_PROPOSAL State

		ReceiveState TWO = new ReceiveState("2");

		TWO.setMethod(new Two_Method());
//		template.setProtocol("fipa-contract-net");
//		template.setPerformative(ACLMessage.PROPOSE);
//		SEND_PROPOSAL.setMessageTemplate(template);
		processor.registerState(TWO);
		processor.addTransition(ONE, TWO);
		
		processor.addTransition(TWO, ONE);
		
		// SEND_REFUSE State

		SendState SEND_REFUSE = new SendState("SEND_REFUSE");

		SEND_REFUSE.setMethod(new SEND_REFUSE_Method());
		template.setProtocol("fipa-contract-net");
		template.setPerformative(ACLMessage.REFUSE);
		SEND_REFUSE.setMessageTemplate(template);
		processor.registerState(SEND_REFUSE);
		processor.addTransition(WAIT_FOR_SOLICIT, SEND_REFUSE);
		
		// SEND_NOT_UNDERSTOOD State

		SendState SEND_NOT_UNDERSTOOD = new SendState("SEND_NOT_UNDERSTOOD");

		SEND_NOT_UNDERSTOOD.setMethod(new SEND_NOT_UNDERSTOOD_Method());
		template.setProtocol("fipa-contract-net");
		template.setPerformative(ACLMessage.NOT_UNDERSTOOD);
		SEND_NOT_UNDERSTOOD.setMessageTemplate(template);
		processor.registerState(SEND_NOT_UNDERSTOOD);
		processor.addTransition(WAIT_FOR_SOLICIT, SEND_NOT_UNDERSTOOD);

		// WAIT_FOR_ACCEPT State
		WaitState WAIT_FOR_ACCEPT = new WaitState("WAIT_FOR_ACCEPT", timeout);
		processor.registerState(WAIT_FOR_ACCEPT);
		processor.addTransition(SEND_PROPOSAL, WAIT_FOR_ACCEPT);

		// RECEIVE_ACCEPT State

		ReceiveState RECEIVE_ACCEPT = new ReceiveState(
				"RECEIVE_ACCEPT");
		RECEIVE_ACCEPT.setMethod(new RECEIVE_ACCEPT_Method());
		filter = new MessageFilter("performative = ACCEPT-PROPOSAL");
		RECEIVE_ACCEPT.setAcceptFilter(filter);
		processor.registerState(RECEIVE_ACCEPT);
		processor.addTransition(WAIT_FOR_ACCEPT,
				RECEIVE_ACCEPT);

		// RECEIVE_REJECT State

		ReceiveState RECEIVE_REJECT = new ReceiveState("RECEIVE_REJECT");
		RECEIVE_REJECT.setMethod(new RECEIVE_REJECT_Method());
		filter = new MessageFilter("performative = REJECT-PROPOSAL");
		RECEIVE_REJECT.setAcceptFilter(filter);
		processor.registerState(RECEIVE_REJECT);
		processor.addTransition(WAIT_FOR_ACCEPT,
				RECEIVE_REJECT);

		// DO_TASK State

		ActionState DO_TASK = new ActionState("DO_TASK");
		DO_TASK.setMethod(new DO_TASK_Method());
		processor.registerState(DO_TASK);
		processor.addTransition(RECEIVE_ACCEPT, DO_TASK);
		
		// SEND_INFO State

		SendState SEND_INFO = new SendState("SEND_INFORM");

		SEND_INFO.setMethod(new SEND_INFO_Method());
		template.setProtocol("fipa-contract-net");
		template.setPerformative(ACLMessage.INFORM);
		SEND_INFO.setMessageTemplate(template);
		processor.registerState(SEND_INFO);
		processor.addTransition(DO_TASK, SEND_INFO);
		
		// SEND_INFO State

		SendState SEND_FAILURE = new SendState("SEND_FAILURE");

		SEND_FAILURE.setMethod(new SEND_FAILURE_Method());
		template.setProtocol("fipa-contract-net");
		template.setPerformative(ACLMessage.FAILURE);
		SEND_FAILURE.setMessageTemplate(template);
		processor.registerState(SEND_FAILURE);
		processor.addTransition(DO_TASK, SEND_FAILURE);		

		// FINAL State

		FinalState FINAL = new FinalState("FINAL");

		FINAL.setMethod(new FINAL_Method());

		processor.registerState(FINAL);
		processor.addTransition(SEND_FAILURE, FINAL);			
		processor.addTransition(SEND_INFO, FINAL);
		processor.addTransition(RECEIVE_REJECT, FINAL);
		processor.addTransition(SEND_NOT_UNDERSTOOD, FINAL);
				
		return theFactory;
	}
	
	
	
	
	
	
	
	
}
