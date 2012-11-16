package es.upv.dsic.gti_ia.cAgents.protocols;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;

import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Argument;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Position;
import es.upv.dsic.gti_ia.cAgents.BeginState;
import es.upv.dsic.gti_ia.cAgents.BeginStateMethod;
import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CFactory;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.FinalState;
import es.upv.dsic.gti_ia.cAgents.FinalStateMethod;
import es.upv.dsic.gti_ia.cAgents.NotAcceptedMessagesState;
import es.upv.dsic.gti_ia.cAgents.ReceiveState;
import es.upv.dsic.gti_ia.cAgents.ReceiveStateMethod;
import es.upv.dsic.gti_ia.cAgents.SendState;
import es.upv.dsic.gti_ia.cAgents.SendStateMethod;
import es.upv.dsic.gti_ia.cAgents.WaitState;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.MessageFilter;

/**
 * Abstract class that defines the argumentation participant protocol to be followed
 * by the CAgents.
 * @author Jaume Jordan
 *
 */
public abstract class Argumentation_Participant {

	private final String OPENDIALOGUE="OPENDIALOGUE";
	private final String FINISHDIALOGUE="FINISHDIALOGUE";
	private final String GETALLPOSITIONS="GETALLPOSITIONS";
	private final String WHY="WHY";
	private final String NOCOMMIT="NOCOMMIT";
	private final String ASSERTS="ASSERT";
	private final String ACCEPTS="ACCEPT";
	private final String ATTACKS="ATTACK";
	private final String DIE="DIE";
	private final String LOCUTION="locution";
	
	private String currentWhyAgentID;
	
	/**
	 * Begin method executed to begin the conversation
	 * @param myProcessor {@link CProcessor} that manage the conversation
	 * @param msg initial message
	 */
	protected void doBegin(CProcessor myProcessor, ACLMessage msg) {
		myProcessor.getInternalData().put("InitialMessage", msg);
	}
	
	class Begin_Method implements BeginStateMethod {
		public String run(CProcessor myProcessor, ACLMessage msg) {
			doBegin(myProcessor, msg);
			return "WAIT_OPEN";
		};
	}
	
	/**
	 * Takes the domain-case to solve and the dialogue ID from the {@link ACLMessage} given
	 * @param myProcessor {@link CProcessor} that manage the conversation
	 * @param msg {@link ACLMessage} with the domain-case to solve and the dialogue ID
	 */
	protected abstract void doOpenDialogue(CProcessor myProcessor, ACLMessage msg);
	
	class Open_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			
			if(messageReceived.getHeaderValue(LOCUTION).equalsIgnoreCase(DIE))
				return "DIE";
			else if(messageReceived.getHeaderValue(LOCUTION).equalsIgnoreCase(OPENDIALOGUE)){
				doOpenDialogue(myProcessor,messageReceived);
				return "ENTER";
			}
			else return "WAIT_OPEN";
		};
	}
	
	
	/**
	 * Evaluates if the agent can enter in the dialogue offering a solution. If it can not, it does a withdraw dialogue.
	 * @param myProcessor {@link CProcessor} that manage the conversation
	 * @param msg {@link ACLMessage} to send to Commitment Store, with locution ENTERDIALOGUE or WITHDRAWDIALOGUE
	 * @return <code>true</code> if it makes an ENTERDIALOGUE, <code>false</code> if it makes a WITHDRAWDIALOGUE
	 */
	protected abstract boolean doEnterDialogue(CProcessor myProcessor, ACLMessage msg);
	
	
	class Enter_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage msg) {
			boolean enterDialogue=doEnterDialogue(myProcessor, msg);
			if(enterDialogue)
				return "PROPOSE";
			else return "WAIT_OPEN";
		};
	}
	
	
	/**
	 * Proposes a {@link Position} to defend in the dialogue. If it can not, it does a withdraw dialogue.
	 * @param myProcessor {@link CProcessor} that manage the conversation
	 * @param msg {@link ACLMessage} to send with the {@link Position} to propose (ADDPOSITION) or WITHDRAWDIALOGUE
	 * @return <code>true</code> if it makes an ADDPOSITION, <code>false</code> if it makes a WITHDRAWDIALOGUE
	 */
	protected abstract boolean doPropose(CProcessor myProcessor, ACLMessage msg);
	
	
	class Propose_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			boolean propose=doPropose(myProcessor, messageReceived);
			if(propose) return "WAIT_CENTRAL";
			else return "WAIT_OPEN";
		};
	}
	
	/**
	 * Actions to be executed when the dialogue has to finish
	 * @param myProcessor {@link CProcessor} that manage the conversation
	 * @param msg {@link ACLMessage} received with the locution FINISHDIALOGUE
	 */
	protected abstract void doFinishDialogue(CProcessor myProcessor, ACLMessage msg);
	
	/**
	 * Actions to perform when the position of the agent has been accepted.
	 * @param myProcessor {@link CProcessor} that manage the conversation
	 * @param messageReceived {@link ACLMessage} with the locution ACCEPT
	 */
	protected abstract void doMyPositionAccepted(CProcessor myProcessor, ACLMessage messageReceived);
	
	class Central_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			if(messageReceived.getHeaderValue(LOCUTION).equalsIgnoreCase(WHY)){
				currentWhyAgentID=messageReceived.getSender().name;
				return "ASSERT";
			}
			else if(messageReceived.getHeaderValue(LOCUTION).equalsIgnoreCase(FINISHDIALOGUE)){
				doFinishDialogue(myProcessor,messageReceived);
				return "SEND_POSITION";
			}
			else if(messageReceived.getHeaderValue(LOCUTION).equalsIgnoreCase(ACCEPTS)){
				doMyPositionAccepted(myProcessor,messageReceived);
				return "WAIT_CENTRAL";
			}
			else return "CENTRAL_TIMEOUT";
		};
	}
	
	/**
	 * Sends an {@link ACLMessage} with the {@link Position} defended by the agent 
	 * @param myProcessor {@link CProcessor} that manage the conversation
	 * @param msg an {@link ACLMessage} to send with the {@link Position} defended by the agent 
	 */
	protected abstract void doSendPosition(CProcessor myProcessor, ACLMessage msg);
	
	
	class Send_Position_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage msg) {
			doSendPosition(myProcessor, msg);
			return "WAIT_SOLUTION";
		};
	}
	
	/**
	 * Actions to perform when the final solution to the current problem to solve arrives in an {@link ACLMessage}
	 * @param myProcessor {@link CProcessor} that manage the conversation
	 * @param msg an {@link ACLMessage} received with the solution to the current problem
	 */
	protected abstract void doSolution(CProcessor myProcessor, ACLMessage msg);
	
	
	class Solution_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doSolution(myProcessor,messageReceived);
			return "WAIT_OPEN";
		};
	}
	
	/**
	 * Try to assert a support argument to respond to the WHY received previously.
	 * @param myProcessor {@link CProcessor} that manage the conversation
	 * @param msg an {@link ACLMessage} to send with an {@link Argument} and locution ASSERT, a locution NOCOMMIT, or a locution NOTHING
	 * @param whyAgentID agent identifier that has made the WHY
	 * @return A {@link String} describing if it makes an ASSERT, NOCOMMIT or, WAIT_CENTRAL
	 */
	protected abstract String doAssert(CProcessor myProcessor, ACLMessage msg, String whyAgentID);
	
	
	class Assert_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage msg) {
			String asserts=doAssert(myProcessor, msg, currentWhyAgentID);
			if(asserts.equalsIgnoreCase(ASSERTS))
				return "WAIT_WAIT_ATTACK";
			else if(asserts.equalsIgnoreCase(NOCOMMIT)){
				doNoCommit(myProcessor, msg);
				return "PROPOSE";
			}
			else return "WAIT_CENTRAL"; //only happens if I have responded the other agent before
		};
	}
	
	
	class Wait_Attack_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			if(messageReceived.getHeaderValue(LOCUTION).equalsIgnoreCase(ACCEPTS)){
				doMyPositionAccepted(myProcessor, messageReceived);
				return "WAIT_CENTRAL"; 
			}
			else if(messageReceived.getHeaderValue(LOCUTION).equalsIgnoreCase(ATTACKS)){
				return "DEFEND";
			}
			else return "WAIT_CENTRAL"; //should not happen
		};
	}
	
	/**
	 * Actions to perform to generate an attack argument against an attack or assert received
	 * @param myProcessor {@link CProcessor} that manage the conversation
	 * @param msgToSend {@link ACLMessage} to send with the attack argument or a NOCOMMIT
	 * @param msgReceived {@link ACLMessage} received with an attack or assert
	 * @param defending indicates if it is defending its position or attacking another agent position
	 * @return <code>true</code> if an attack argument has been generated
	 */
	protected abstract boolean doAttack(CProcessor myProcessor, ACLMessage msgToSend, ACLMessage msgReceived, boolean defending);
	
	class Defend_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage msg) {
			boolean attack=doAttack(myProcessor, msg, myProcessor.getLastReceivedMessage(), true);
			if(attack) return "WAIT_WAIT_ATTACK";
			else{
				doNoCommit(myProcessor, msg);
				return "PROPOSE"; 
			}
		};
	}
	
	/**
	 * Creates an {@link ACLMessage} to send with the locution NOCOMMIT
	 * @param myProcessor {@link CProcessor} that manage the conversation
	 * @param msg {@link ACLMessage} to send with the locution NOCOMMIT
	 */
	protected abstract void doNoCommit(CProcessor myProcessor, ACLMessage msg);
	
	class No_Commit_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage msg) {
			doNoCommit(myProcessor, msg);
			return "PROPOSE";
		};
	}
	
	class Central_Timeout_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			return "QUERY_POSITIONS";
		}
	}
	
	/**
	 * Creates a message to send to the Commitment Store with locution GETALLPOSITIONS to obtain all the positions of the dialogue
	 * @param myProcessor {@link CProcessor} that manage the conversation
	 * @param msg {@link ACLMessage} to send to the Commitment Store with locution GETALLPOSITIONS
	 */
	protected abstract void doQueryPositions(CProcessor myProcessor, ACLMessage msg);
	
	class Query_Positions_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage msg) {
			doQueryPositions(myProcessor, msg);
			return "WAIT_POSITIONS"; 
		};
	}
	
	
	class Positions_Timeout_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			return "WAIT_CENTRAL";
		}
	}
	
	/**
	 * Get the positions of the agents in the dialogue sent by the Commitment Store as an object
	 * in the {@link ACLMessage} parameter
	 * @param myProcessor {@link CProcessor} that manage the conversation
	 * @param msg {@link ACLMessage} with locution GETALLPOSITIONS and the positions of other agents in the dialogue
	 */
	protected abstract void doGetPositions(CProcessor myProcessor, ACLMessage msgReceived);
	
	class Get_Positions_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			if(messageReceived.getHeaderValue(LOCUTION).equalsIgnoreCase(FINISHDIALOGUE)){
				return "SEND_POSITION";
			}
			else if(messageReceived.getHeaderValue(LOCUTION).equalsIgnoreCase(GETALLPOSITIONS)){
				ACLMessage msg2=new ACLMessage();
				copyMessages(msg2, messageReceived);
				doGetPositions(myProcessor, msg2);
				return "WHY";
			}
			else return "WAIT_CENTRAL"; //should not happen
			
		};
	}
	
	/**
	 * Choose a {@link Position} to send a WHY message if it can, or NOTHING
	 * @param myProcessor {@link CProcessor} that manage the conversation
	 * @param msg {@link ACLMessage} to send a WHY, or NOTHING if there is not any {@link Position} to ask
	 * @return code>true</code> if it makes a WHY, <code>false</code> if it makes a NOTHING
	 */
	protected abstract boolean doWhy(CProcessor myProcessor, ACLMessage msg);
	
	class Why_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage msg) {
			boolean why=doWhy(myProcessor, msg);
			if(why) return "WAIT_WAIT_ASSERT";
			else return "WAIT_CENTRAL"; 
		};
	}
	
	class Wait_Assert_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			if(messageReceived.getHeaderValue(LOCUTION).equalsIgnoreCase(ASSERTS))
				return "ATTACK"; 
			else if(messageReceived.getHeaderValue(LOCUTION).equalsIgnoreCase(NOCOMMIT)){
				return "WAIT_CENTRAL";
			}
			else return "WAIT_CENTRAL"; // should not happen
		};
	}
	
	class Wait_Assert_Timeout_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			return "WAIT_CENTRAL";
		}
	}
	
	/**
	 * Actions to perform and send an {@link ACLMessage} accepting the other agent's position or argument
	 * @param myProcessor {@link CProcessor} that manage the conversation
	 * @param msgToSend {@link ACLMessage} accepting the other agent's position or argument
	 */
	protected abstract void doAccept(CProcessor myProcessor, ACLMessage msgToSend);
	
	class Attack_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage msgToSend) {
			boolean attack=doAttack(myProcessor, msgToSend, myProcessor.getLastReceivedMessage(), false);
			if(attack) return "WAIT_ATTACK2";
			else{
				doAccept(myProcessor, msgToSend);
				return "WAIT_CENTRAL"; 
			}
		};
	}
	
	/**
	 * Actions to perform after other's no commit.
	 * @param myProcessor {@link CProcessor} that manage the conversation
	 * @param msgReceived {@link ACLMessage} received with a NO COMMIT
	 */
	protected abstract void doOtherNoCommit(CProcessor myProcessor, ACLMessage msgReceived);
	
	class Attack2_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			if(messageReceived.getHeaderValue(LOCUTION).equalsIgnoreCase(ATTACKS))
				return "ATTACK"; 
			else if(messageReceived.getHeaderValue(LOCUTION).equalsIgnoreCase(NOCOMMIT)){
				doOtherNoCommit(myProcessor,messageReceived);
				return "WAIT_CENTRAL";
			}
			else return "WAIT_CENTRAL"; // should not happen
		};
	}
	
	/**
	 * This class manages the unexpected messages that arrive to an state.
	 * The messages with locutions FINISHDIALOGUE and DIE are not expected in any
	 * state, this is because we prefer to manage and change the behaviour of the
	 * agent in any WAIT state where these messages can be received.
	 * 
	 *
	 */
	class NotAcceptedMessagesState2 extends NotAcceptedMessagesState {
		
		String nextState;
		
		@Override
		protected int run(ACLMessage exceptionMessage, String next) {
			
			if(exceptionMessage.getHeaderValue(LOCUTION).equalsIgnoreCase(FINISHDIALOGUE)){
				nextState="FINISH";
				return NotAcceptedMessagesState.IGNORE;
			}
			else if(exceptionMessage.getHeaderValue(LOCUTION).equalsIgnoreCase(DIE)){
				nextState="DIE";
				return NotAcceptedMessagesState.IGNORE;
			}
			else{
				nextState="SAME";
				return NotAcceptedMessagesState.IGNORE;
			}
						
		}
	
		@Override
		protected String getNext(String previousState) {
			
			if(nextState.equalsIgnoreCase("FINISH")){
				if(previousState.equalsIgnoreCase("WAIT_OPEN")){
					return "WAIT_OPEN";
				}
				else{
					return "SEND_POSITION";
				}
			}
			else if(nextState.equalsIgnoreCase("DIE")){
				return "DIE";
			}
			else{
				return previousState;
			}
			
		}
		
		
	}
		
	/**
	 * Actions to perform when the message with locution DIE is received.
	 * @param myProcessor {@link CProcessor} that manage the conversation
	 */
	protected abstract void doDie(CProcessor myProcessor);
	
	
	class Die_Method implements FinalStateMethod {
		public void run(CProcessor myProcessor, ACLMessage messageToSend) {
			doDie(myProcessor);
		}
	}
	
	
	/**
	 * Copies all contents from the msg2 to the msg
	 * @param msg {@link ACLMessage}
	 * @param msg2 {@link ACLMessage}
	 */
	private void copyMessages(ACLMessage msg, ACLMessage msg2){
		msg.setSender(msg2.getSender());
		Iterator<AgentID> iterReceivers=msg2.getReceiverList().iterator();
		while(iterReceivers.hasNext()){
			msg.addReceiver(iterReceivers.next());
		}
		msg.setConversationId(msg2.getConversationId());
		msg.setHeader("locution", msg2.getHeaderValue("locution"));
		msg.setPerformative(msg2.getPerformative());
		if(msg2.getContentObject()!=null)
			try {
				msg.setContentObject((Serializable) msg2.getContentObject());
			} catch (IOException e) {
				
				e.printStackTrace();
			}
	}
	
	
	/**
	 * Creates a new argumentation participant CFactory
	 * @param name factory's name
	 * @param availableConversations maximum number of conversation this {@link CFactory} can manage simultaneously
	 * @param myAgent agent owner of this {@link CFactory}
	 * @param stdTimeout standard timeout to wait in wait states
	 * @param randTimeout random timeout to wait in some states of the dialogue
	 * @return a new argumentation participant factory
	 */
	public CFactory newFactory(String name, int availableConversations, CAgent myAgent, 
			long stdTimeout, long randTimeout) {

		MessageFilter filter = new MessageFilter("performative = INFORM AND locution = "+OPENDIALOGUE);

		// Create factory
		CFactory theFactory = new CFactory(name, filter,
				availableConversations, myAgent);

		// Processor template setup
		CProcessor processor = theFactory.cProcessorTemplate();

		// BEGIN State
		BeginState BEGIN = (BeginState) processor.getState("BEGIN");
		BEGIN.setMethod(new Begin_Method());
		
		
		WaitState WAIT_OPEN = new WaitState("WAIT_OPEN", 0);
		processor.registerState(WAIT_OPEN);
		processor.addTransition(BEGIN, WAIT_OPEN);
		
		ReceiveState OPEN = new ReceiveState("OPEN");
		OPEN.setMethod(new Open_Method());
		filter = new MessageFilter("performative = INFORM AND (locution = "+OPENDIALOGUE+" OR locution = "+DIE+")");
		OPEN.setAcceptFilter(filter);
		processor.registerState(OPEN);
		processor.addTransition(WAIT_OPEN,OPEN);		

		
		FinalState DIE = new FinalState("DIE");
		DIE.setMethod(new Die_Method());
		processor.registerState(DIE);
		processor.addTransition(OPEN, DIE);
		
		
		SendState ENTER = new SendState("ENTER");
		ENTER.setMethod(new Enter_Method());
		processor.registerState(ENTER);
		processor.addTransition(OPEN, ENTER);
		
		processor.addTransition(ENTER, WAIT_OPEN);
		
		
		SendState PROPOSE = new SendState("PROPOSE");
		PROPOSE.setMethod(new Propose_Method());
		processor.registerState(PROPOSE);
		processor.addTransition(ENTER, PROPOSE);
		
		processor.addTransition(PROPOSE, WAIT_OPEN);
		
		
		WaitState WAIT_CENTRAL = new WaitState("WAIT_CENTRAL", randTimeout);
		processor.registerState(WAIT_CENTRAL);
		processor.addTransition(PROPOSE, WAIT_CENTRAL);
		
		ReceiveState CENTRAL = new ReceiveState("CENTRAL");
		CENTRAL.setMethod(new Central_Method());
		filter = new MessageFilter("performative = INFORM AND (locution = "+WHY+" OR locution = "+ACCEPTS+")");
		CENTRAL.setAcceptFilter(filter);
		processor.registerState(CENTRAL);
		processor.addTransition(WAIT_CENTRAL,CENTRAL);
		
		
		ReceiveState CENTRAL_TIMEOUT = new ReceiveState("CENTRAL_TIMEOUT");
		CENTRAL_TIMEOUT.setMethod(new Central_Timeout_Method());
		filter = new MessageFilter("performative = INFORM AND purpose = waitMessage");
		CENTRAL_TIMEOUT.setAcceptFilter(filter);
		processor.registerState(CENTRAL_TIMEOUT);
		processor.addTransition(WAIT_CENTRAL, CENTRAL_TIMEOUT);
		
		
		SendState QUERY_POSITIONS = new SendState("QUERY_POSITIONS");
		QUERY_POSITIONS.setMethod(new Query_Positions_Method());
		processor.registerState(QUERY_POSITIONS);
		processor.addTransition(CENTRAL_TIMEOUT, QUERY_POSITIONS);
		
		WaitState WAIT_POSITIONS = new WaitState("WAIT_POSITIONS", stdTimeout);
		processor.registerState(WAIT_POSITIONS);
		processor.addTransition(QUERY_POSITIONS, WAIT_POSITIONS);
		
		ReceiveState GET_POSITIONS = new ReceiveState("GET_POSITIONS");
		GET_POSITIONS.setMethod(new Get_Positions_Method());
		filter = new MessageFilter("performative = INFORM AND locution = "+GETALLPOSITIONS);
		GET_POSITIONS.setAcceptFilter(filter);
		processor.registerState(GET_POSITIONS);
		processor.addTransition(WAIT_POSITIONS,GET_POSITIONS);
		
		processor.addTransition(GET_POSITIONS, WAIT_CENTRAL);
		
		
		ReceiveState POSITIONS_TIMEOUT = new ReceiveState("POSITIONS_TIMEOUT");
		POSITIONS_TIMEOUT.setMethod(new Positions_Timeout_Method());
		filter = new MessageFilter("performative = INFORM AND purpose = waitMessage");
		POSITIONS_TIMEOUT.setAcceptFilter(filter);
		processor.registerState(POSITIONS_TIMEOUT);
		processor.addTransition(WAIT_POSITIONS, POSITIONS_TIMEOUT);
		
		processor.addTransition(POSITIONS_TIMEOUT, WAIT_CENTRAL);
		
		
		SendState ASSERT = new SendState("ASSERT");
		ASSERT.setMethod(new Assert_Method());
		processor.registerState(ASSERT);
		processor.addTransition(CENTRAL, ASSERT);
		
		processor.addTransition(ASSERT, WAIT_CENTRAL);
		processor.addTransition(ASSERT, PROPOSE);
		
		
		WaitState WAIT_WAIT_ATTACK = new WaitState("WAIT_WAIT_ATTACK", stdTimeout);
		processor.registerState(WAIT_WAIT_ATTACK);
		processor.addTransition(ASSERT, WAIT_WAIT_ATTACK);
		
		ReceiveState WAIT_ATTACK = new ReceiveState("WAIT_ATTACK");
		WAIT_ATTACK.setMethod(new Wait_Attack_Method());
		filter = new MessageFilter("performative = INFORM AND (locution = "+ATTACKS+" OR locution = "+ACCEPTS+")");
		WAIT_ATTACK.setAcceptFilter(filter);
		processor.registerState(WAIT_ATTACK);
		processor.addTransition(WAIT_WAIT_ATTACK,WAIT_ATTACK);
		
		processor.addTransition(WAIT_ATTACK,WAIT_CENTRAL);
		
		
		SendState DEFEND = new SendState("DEFEND");
		DEFEND.setMethod(new Defend_Method());
		processor.registerState(DEFEND);
		processor.addTransition(WAIT_ATTACK, DEFEND);
		
		processor.addTransition(DEFEND, WAIT_WAIT_ATTACK);
		processor.addTransition(DEFEND, PROPOSE);
		
		SendState NO_COMMIT = new SendState("NO_COMMIT");
		NO_COMMIT.setMethod(new No_Commit_Method());
		processor.registerState(NO_COMMIT);
		processor.addTransition(CENTRAL, NO_COMMIT);
		
		processor.addTransition(NO_COMMIT, PROPOSE);
		
		
		SendState WHY = new SendState("WHY");
		WHY.setMethod(new Why_Method());
		processor.registerState(WHY);
		processor.addTransition(GET_POSITIONS,WHY);
		
		processor.addTransition(WHY, WAIT_CENTRAL);
		
		
		WaitState WAIT_WAIT_ASSERT = new WaitState("WAIT_WAIT_ASSERT", stdTimeout);
		processor.registerState(WAIT_WAIT_ASSERT);
		processor.addTransition(WHY, WAIT_WAIT_ASSERT);
		
		ReceiveState WAIT_ASSERT = new ReceiveState("WAIT_ASSERT");
		WAIT_ASSERT.setMethod(new Wait_Assert_Method());
		filter = new MessageFilter("performative = INFORM AND (locution = "+ASSERTS+" OR locution = "+NOCOMMIT+")");
		WAIT_ASSERT.setAcceptFilter(filter);
		processor.registerState(WAIT_ASSERT);
		processor.addTransition(WAIT_WAIT_ASSERT,WAIT_ASSERT);
		
		processor.addTransition(WAIT_ASSERT,WAIT_CENTRAL);
		
		
		ReceiveState WAIT_ASSERT_TIMEOUT = new ReceiveState("WAIT_ASSERT_TIMEOUT");
		WAIT_ASSERT_TIMEOUT.setMethod(new Wait_Assert_Timeout_Method());
		filter = new MessageFilter("performative = INFORM AND purpose = waitMessage");
		WAIT_ASSERT_TIMEOUT.setAcceptFilter(filter);
		processor.registerState(WAIT_ASSERT_TIMEOUT);
		processor.addTransition(WAIT_WAIT_ASSERT, WAIT_ASSERT_TIMEOUT);
		
		processor.addTransition(WAIT_ASSERT_TIMEOUT, WAIT_CENTRAL);
		
		SendState ATTACK = new SendState("ATTACK");
		ATTACK.setMethod(new Attack_Method());
		processor.registerState(ATTACK);
		processor.addTransition(WAIT_ASSERT, ATTACK);
		
		processor.addTransition(ATTACK, WAIT_CENTRAL);
	
		
		WaitState WAIT_ATTACK2 = new WaitState("WAIT_ATTACK2", stdTimeout);
		processor.registerState(WAIT_ATTACK2);
		processor.addTransition(ATTACK, WAIT_ATTACK2);
		
		ReceiveState ATTACK2 = new ReceiveState("ATTACK2");
		ATTACK2.setMethod(new Attack2_Method());
		filter = new MessageFilter("performative = INFORM AND (locution = "+ATTACKS+" OR locution = "+NOCOMMIT+")");
		ATTACK2.setAcceptFilter(filter);
		processor.registerState(ATTACK2);
		processor.addTransition(WAIT_ATTACK2,ATTACK2);
		
		processor.addTransition(ATTACK2,WAIT_CENTRAL);
		processor.addTransition(ATTACK2,ATTACK);
		
		
		SendState SEND_POSITION = new SendState("SEND_POSITION");
		SEND_POSITION.setMethod(new Send_Position_Method());
		processor.registerState(SEND_POSITION);
		processor.addTransition(CENTRAL, SEND_POSITION);
		processor.addTransition(GET_POSITIONS, SEND_POSITION);
		
		
		WaitState WAIT_SOLUTION = new WaitState("WAIT_SOLUTION", 0);
		processor.registerState(WAIT_SOLUTION);
		processor.addTransition(SEND_POSITION, WAIT_SOLUTION);
		
		ReceiveState SOLUTION = new ReceiveState("SOLUTION");
		SOLUTION.setMethod(new Solution_Method());
		filter = new MessageFilter("performative = INFORM AND locution = "+"SOLUTION");
		SOLUTION.setAcceptFilter(filter);
		processor.registerState(SOLUTION);
		processor.addTransition(WAIT_SOLUTION,SOLUTION);
		
		processor.addTransition(SOLUTION,WAIT_OPEN);
		
		NotAcceptedMessagesState2 NOT_ACCEPTED_MESSAGES_STATE = new NotAcceptedMessagesState2();
		theFactory.cProcessorTemplate().registerState(NOT_ACCEPTED_MESSAGES_STATE); 
		
		
		return theFactory;
	}
	
}
