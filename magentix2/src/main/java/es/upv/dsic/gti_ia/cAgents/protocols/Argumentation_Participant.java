package es.upv.dsic.gti_ia.cAgents.protocols;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;

import com.hp.hpl.jena.graph.query.regexptrees.Nothing;

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

public abstract class Argumentation_Participant {

	private final String OPENDIALOGUE="OPENDIALOGUE";
	private final String ENTERDIALOGUE="ENTERDIALOGUE";
	private final String LEAVEDIALOGUE="LEAVEDIALOGUE";
	private final String FINISHDIALOGUE="FINISHDIALOGUE";
	private final String WITHDRAWDIALOGUE="WITHDRAWDIALOGUE";
	private final String GETALLPOSITIONS="GETALLPOSITIONS";
	private final String PROPOSE="PROPOSE";
	private final String WHY="WHY";
	private final String NOCOMMIT="NOCOMMIT";
	private final String ASSERTS="ASSERT";
	private final String ACCEPTS="ACCEPT";
	private final String ATTACKS="ATTACK";
	//private final String RETRACT="RETRACT";
	private final String DIE="DIE";
	
	private final String LOCUTION="locution";
	
	private String currentWhyAgentID;
	
	
	protected void doBegin(CProcessor myProcessor, ACLMessage msg) {
		myProcessor.getInternalData().put("InitialMessage", msg); //TODO ??
	}
	
	class Begin_Method implements BeginStateMethod {
		public String run(CProcessor myProcessor, ACLMessage msg) {
			doBegin(myProcessor, msg);
			return "WAIT_OPEN";
		};
	}
	
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
	 * Evaluates if the agent can enter in the dialogue offering a solution. If it can't, it does a withdraw dialogue.
	 * @param myProcessor
	 * @param msg
	 * @return
	 */
	protected abstract boolean doEnterDialogue(CProcessor myProcessor, ACLMessage msg);
	
	
	class Enter_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage msg) {
			boolean enterDialogue=doEnterDialogue(myProcessor, msg);
			System.out.println("************* message "+msg.getHeaderValue("locution")+ " receiver: "+msg.getReceiver().name);
			if(enterDialogue)
				return "PROPOSE";
			else return "WAIT_OPEN";
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
			if(propose) return "WAIT_CENTRAL";
			else return "WAIT_OPEN";
		};
	}
	
	
	protected abstract boolean doFinishDialogue(CProcessor myProcessor, ACLMessage msg);
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
	
	
	protected abstract void doSendPosition(CProcessor myProcessor, ACLMessage msg);
	
	
	class Send_Position_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage msg) {
			doSendPosition(myProcessor, msg);
			return "WAIT_SOLUTION";
		};
	}
	
	
	protected abstract void doSolution(CProcessor myProcessor, ACLMessage msg);
	
	
	class Solution_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doSolution(myProcessor,messageReceived);
			return "WAIT_OPEN";
		};
	}
	
	
	
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
			else return "WAIT_CENTRAL"; //TODO no tiene porqué...
		};
	}
	
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
	
	protected abstract boolean doGetPositions(CProcessor myProcessor, ACLMessage msg);
	
	class Get_Positions_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			if(messageReceived.getHeaderValue(LOCUTION).equalsIgnoreCase(FINISHDIALOGUE)){
				return "SEND_POSITION";
			}
			else if(messageReceived.getHeaderValue(LOCUTION).equalsIgnoreCase(GETALLPOSITIONS)){
				System.out.println("++++++++++++++++++++++ locution in getPositionsMethod="+messageReceived.getHeaderValue(LOCUTION));
				ACLMessage msg2=new ACLMessage();
				copyMessages(msg2, messageReceived);
				boolean positions=doGetPositions(myProcessor, msg2);
				if(positions) return "WHY";
				else return "WAIT_CENTRAL";
			}
			else return "WAIT_CENTRAL"; //TODO with the filter, this should not happen
			
		};
	}
	
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
			else return "WAIT_CENTRAL"; //TODO should not happen
		};
	}
	
	
	class Wait_Assert_Timeout_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			return "WAIT_CENTRAL";
		}
	}
	
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
	
	
	
	class Attack2_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			if(messageReceived.getHeaderValue(LOCUTION).equalsIgnoreCase(ATTACKS))
				return "ATTACK"; 
			else if(messageReceived.getHeaderValue(LOCUTION).equalsIgnoreCase(NOCOMMIT)){
				return "WAIT_CENTRAL";
			}
			else return "WAIT_CENTRAL"; //TODO no tiene porqué...
		};
	}
	
//	ACLMessage msgToSend;
//	String nextState;
	
	
	
	class NotAcceptedMessagesState2 extends NotAcceptedMessagesState {
		
		String nextState;
		
		@Override
		protected int run(CProcessor myProcessor, ACLMessage exceptionMessage, String next) {
			
			myProcessor.logger.info("\nThis is NOT_ACCEPTED_MESSAGES_STATE\n prevState: "+next+
			" locutionProcesor: "+exceptionMessage.getHeaderValue(LOCUTION));
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
				return NotAcceptedMessagesState.IGNORE; //TODO cuidao!
			}
						
		}
	
		@Override
		protected String getNext(CProcessor myProcessor, String previousState) {
			myProcessor.logger.info("\nNextStateSuposat: "+nextState+" previousState: "+previousState);
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
		
//		@Override
//		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
//		
//			myProcessor.logger.info("\nThis is NOT_ACCEPTED_MESSAGES_STATE\nPrevious state: "+
//			myProcessor.getPreviousState()+" locutionProcesor: "+myProcessor.getLastReceivedMessage().getHeaderValue(LOCUTION)+
//			"\nlocutionMsg: "+messageReceived.getHeaderValue(LOCUTION));
//			if(myProcessor.getLastReceivedMessage().getHeaderValue(LOCUTION).equalsIgnoreCase(FINISHDIALOGUE)){
//				
//				String prevState=myProcessor.getPreviousState();
//				if(prevState.equalsIgnoreCase("WAIT_OPEN")){
//					return "WAIT_OPEN";
//				}
//				else{
//					return "SEND_POSITION";
//				}
//			}
//			else if(myProcessor.getLastReceivedMessage().getHeaderValue(LOCUTION).equalsIgnoreCase(DIE)){
//				return "DIE";
//			}
//			else{
////				msgToSend=new ACLMessage();
////				msgToSend.setPerformative(myProcessor.getLastReceivedMessage().getPerformative());
////				msgToSend.setSender(myProcessor.getLastReceivedMessage().getSender());
////				msgToSend.setContent(myProcessor.getLastReceivedMessage().getContent());
////				msgToSend.setReceiver(myProcessor.getLastReceivedMessage().getReceiver());
////				
////				nextState=myProcessor.getPreviousState();
////				
////				return "SEND_NOT_ACCEPTED";
//				
//				return myProcessor.getPreviousState();
//			}
//			
//		}
	
	
//	class SEND_NOT_ACCEPTED_Method implements SendStateMethod{
//		@Override
//		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
//		
//			copyMessages(messageToSend, msgToSend);
//			return nextState;
//			
//		}
//	}
	
	
	
	protected abstract void doDie(CProcessor myProcessor);
	
	
	class Die_Method implements FinalStateMethod {
		public void run(CProcessor myProcessor, ACLMessage messageToSend) {
			doDie(myProcessor);
		}
	}
	
	
//	class ManageMessages_Method implements ReceiveStateMethod {
//		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
//			if(messageReceived.getHeaderValue("locution").equalsIgnoreCase(FINISHDIALOGUE)){
//				//if(currentPosition!=null)
//				return "SEND_POSITION";
//			}
//			else
//				myProcessor
//		};
//	}
	
	/**
	 * Copies all contents of the msg2 to the msg1
	 * @param msg
	 * @param msg2
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
	 * @param filter message filter
	 * @param template first message to send
	 * @param availableConversations maximum number of conversation this CFactory can manage simultaneously
	 * @param myAgent agent owner of this CFactory
	 * @param timeout for waiting after sending the proposal
	 * @return a new argumentation participant factory
	 */
	public CFactory newFactory(String name, MessageFilter filter,
			ACLMessage template, int availableConversations, CAgent myAgent, long stdTimeout, long randTimeout) {

		// Create factory

		filter = new MessageFilter("performative = INFORM AND locution = "+OPENDIALOGUE);
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
		//filter = new MessageFilter("performative = INFORM AND (locution = "+WHY+" OR locution = "+FINISHDIALOGUE+" OR locution = "+ACCEPTS+")");
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
		//filter = new MessageFilter("performative = INFORM AND (locution = "+GETALLPOSITIONS+" OR locution = "+FINISHDIALOGUE+")");
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
//		processor.addTransition(DEFEND, NO_COMMIT);
//		processor.addTransition(ASSERT, NO_COMMIT);
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
	
//		SendState ACCEPT = new SendState("ACCEPT");
//		ACCEPT.setMethod(new Accept_Method());
//		processor.registerState(ACCEPT);
//		processor.addTransition(ATTACK, ACCEPT);
//		
//		processor.addTransition(ACCEPT, WAIT_CENTRAL);
	
		
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
		
//		SendState SEND_NOT_ACCEPTED = new SendState("SEND_NOT_ACCEPTED");
//		SEND_NOT_ACCEPTED.setMethod(new SEND_NOT_ACCEPTED_Method());
//		theFactory.cProcessorTemplate().registerState(SEND_NOT_ACCEPTED); 
////		processor.addTransition(NOT_ACCEPTED_MESSAGES_STATE,SEND_NOT_ACCEPTED);//TODO??
//		processor.addTransition(SEND_NOT_ACCEPTED, WAIT_OPEN);
//		processor.addTransition(SEND_NOT_ACCEPTED, WAIT_POSITIONS);
//		processor.addTransition(SEND_NOT_ACCEPTED, WAIT_ATTACK2);
//		processor.addTransition(SEND_NOT_ACCEPTED, WAIT_CENTRAL);
//		processor.addTransition(SEND_NOT_ACCEPTED, WAIT_SOLUTION);
//		processor.addTransition(SEND_NOT_ACCEPTED, WAIT_WAIT_ASSERT);
//		processor.addTransition(SEND_NOT_ACCEPTED, WAIT_WAIT_ATTACK);
		
		return theFactory;
	}
	
	
	
	
	
	
	
	
}
