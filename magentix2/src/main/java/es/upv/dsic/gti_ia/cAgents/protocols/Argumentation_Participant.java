package es.upv.dsic.gti_ia.cAgents.protocols;

import java.io.IOException;
import java.io.Serializable;

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
import es.upv.dsic.gti_ia.core.ACLMessage;
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
			else return "WITHDRAW_DIALOGUE";
		};
	}
	
	protected abstract void doWithdrawDialogue(CProcessor myProcessor, ACLMessage msg);
	
	class Withdraw_Dialogue_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage msg) {
			doWithdrawDialogue(myProcessor, msg);
			return "WAIT_OPEN";
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
				return "WAIT_CENTRAL";
			else return "WITHDRAW_DIALOGUE";
		};
	}
	
	
	protected abstract boolean doFinishDialogue(CProcessor myProcessor, ACLMessage msg);
	protected abstract void doMyPositionAccepted(CProcessor myProcessor, ACLMessage msg);
	
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
			else if(asserts.equalsIgnoreCase(NOCOMMIT))
				return "NO_COMMIT";
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
	
	protected abstract boolean doAttack(CProcessor myProcessor, ACLMessage msg);
	
	class Defend_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage msg) {
			boolean attack=doAttack(myProcessor, msg);
			if(attack) return "WAIT_WAIT_ATTACK";
			else return "NO_COMMIT"; 
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
	
	
	class Attack_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage msg) {
			boolean attack=doAttack(myProcessor, msg);
			if(attack) return "WAIT_ATTACK2";
			else return "ACCEPT"; 
		};
	}
	
	protected abstract void doAccept(CProcessor myProcessor, ACLMessage msg);
	
	class Accept_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage msg) {
			doAccept(myProcessor, msg);
			return "WAIT_CENTRAL";
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
	
	
	
	
	
	
	protected abstract void doDie(CProcessor myProcessor);
	
	
	class Die_Method implements FinalStateMethod {
		public void run(CProcessor myProcessor, ACLMessage messageToSend) {
			doDie(myProcessor);
		}
	}
	
	
	class Ronya_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			System.out.println("----------- " +messageReceived.getHeaderValue("locution"));
			return "WAIT_SOLUTION";
		};
	}
	
	/**
	 * Copies all contents of the msg2 to the msg1
	 * @param msg
	 * @param msg2
	 */
	private void copyMessages(ACLMessage msg, ACLMessage msg2){
		msg.setSender(msg2.getSender());
		msg.setReceiver(msg2.getReceiver());
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
		
		SendState PROPOSE = new SendState("PROPOSE");
		PROPOSE.setMethod(new Propose_Method());
		processor.registerState(PROPOSE);
		processor.addTransition(ENTER, PROPOSE);
		
		SendState WITHDRAW_DIALOGUE = new SendState("WITHDRAW_DIALOGUE");
		WITHDRAW_DIALOGUE.setMethod(new Withdraw_Dialogue_Method());
		processor.registerState(WITHDRAW_DIALOGUE);
		processor.addTransition(ENTER, WITHDRAW_DIALOGUE);
		processor.addTransition(PROPOSE, WITHDRAW_DIALOGUE);
		
		processor.addTransition(WITHDRAW_DIALOGUE, WAIT_OPEN);
		
		
		WaitState WAIT_CENTRAL = new WaitState("WAIT_CENTRAL", randTimeout);
		processor.registerState(WAIT_CENTRAL);
		processor.addTransition(PROPOSE, WAIT_CENTRAL);
		
		ReceiveState CENTRAL = new ReceiveState("CENTRAL");
		CENTRAL.setMethod(new Central_Method());
		filter = new MessageFilter("performative = INFORM AND (locution = "+WHY+" OR locution = "+FINISHDIALOGUE+" OR locution = "+ACCEPTS+")");
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
		filter = new MessageFilter("performative = INFORM AND (locution = "+GETALLPOSITIONS+" OR locution = "+FINISHDIALOGUE+")");
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
		
		
		/*ReceiveState NOT_ACCEPTED_MESSAGES_STATE = new ReceiveState("NOT_ACCEPTED_MESSAGES_STATE");
		NOT_ACCEPTED_MESSAGES_STATE.setMethod(new Ronya_Method());
		processor.registerState(NOT_ACCEPTED_MESSAGES_STATE);
		//processor.addTransition(WAIT_SOLUTION,NOT_ACCEPTED_MESSAGES_STATE);
		processor.addTransition(NOT_ACCEPTED_MESSAGES_STATE,WAIT_SOLUTION);*/
		
		ReceiveState RONYOS = new ReceiveState("RONYOS");
		RONYOS.setMethod(new Ronya_Method());
		filter = new MessageFilter("performative = INFORM AND locution = "+"GETALLPOSITIONS");
		RONYOS.setAcceptFilter(filter);
		processor.registerState(RONYOS);
		

		SendState ASSERT = new SendState("ASSERT");
		ASSERT.setMethod(new Assert_Method());
		processor.registerState(ASSERT);
		processor.addTransition(CENTRAL, ASSERT);
		
		processor.addTransition(ASSERT, WAIT_CENTRAL);
		
		
		
		
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
		
		
		SendState NO_COMMIT = new SendState("NO_COMMIT");
		NO_COMMIT.setMethod(new No_Commit_Method());
		processor.registerState(NO_COMMIT);
		processor.addTransition(DEFEND, NO_COMMIT);
		processor.addTransition(ASSERT, NO_COMMIT);
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
		
		
		
		SendState ATTACK = new SendState("ATTACK");
		ATTACK.setMethod(new Attack_Method());
		processor.registerState(ATTACK);
		processor.addTransition(WAIT_ASSERT, ATTACK);
		
	
		SendState ACCEPT = new SendState("ACCEPT");
		ACCEPT.setMethod(new Accept_Method());
		processor.registerState(ACCEPT);
		processor.addTransition(ATTACK, ACCEPT);
		
		processor.addTransition(ACCEPT, WAIT_CENTRAL);
	
		
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
		processor.addTransition(ATTACK2,WAIT_ATTACK2);
		
		
		SendState SEND_POSITION = new SendState("SEND_POSITION");
		SEND_POSITION.setMethod(new Send_Position_Method());
		processor.registerState(SEND_POSITION);
		processor.addTransition(CENTRAL, SEND_POSITION);
		processor.addTransition(GET_POSITIONS, SEND_POSITION);
		
		
		WaitState WAIT_SOLUTION = new WaitState("WAIT_SOLUTION", 0);
		processor.registerState(WAIT_SOLUTION);
		processor.addTransition(SEND_POSITION, WAIT_SOLUTION);
		
		processor.addTransition(WAIT_SOLUTION,RONYOS); //TODO 
		processor.addTransition(RONYOS,WAIT_SOLUTION); //TODO
		
		ReceiveState SOLUTION = new ReceiveState("SOLUTION");
		SOLUTION.setMethod(new Solution_Method());
		filter = new MessageFilter("performative = INFORM AND locution = "+"SOLUTION");
		SOLUTION.setAcceptFilter(filter);
		processor.registerState(SOLUTION);
		processor.addTransition(WAIT_SOLUTION,SOLUTION);
		
		processor.addTransition(SOLUTION,WAIT_OPEN);
		
		
		return theFactory;
	}
	
	
	
	
	
	
	
	
}
