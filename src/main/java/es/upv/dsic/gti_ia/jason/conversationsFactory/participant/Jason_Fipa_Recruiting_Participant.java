package es.upv.dsic.gti_ia.jason.conversationsFactory.participant;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import es.upv.dsic.gti_ia.cAgents.ActionState;
import es.upv.dsic.gti_ia.cAgents.ActionStateMethod;
import es.upv.dsic.gti_ia.cAgents.BeginState;
import es.upv.dsic.gti_ia.cAgents.BeginStateMethod;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.FinalState;
import es.upv.dsic.gti_ia.cAgents.FinalStateMethod;
import es.upv.dsic.gti_ia.cAgents.ReceiveState;
import es.upv.dsic.gti_ia.cAgents.ReceiveStateMethod;
import es.upv.dsic.gti_ia.cAgents.SendState;
import es.upv.dsic.gti_ia.cAgents.SendStateMethod;
import es.upv.dsic.gti_ia.cAgents.WaitState;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.MessageFilter;
import jason.asSemantics.TransitionSystem;
import jason.asSyntax.Literal;
import jason.asSyntax.LiteralImpl;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvCFactory;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvCProcessor;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvJasonAgent;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvMagentixAgArch;
import es.upv.dsic.gti_ia.jason.conversationsFactory.Conversation;
import es.upv.dsic.gti_ia.jason.conversationsFactory.FRCConversation;

/**
 * This class represents a template for a Fipa Recruiting Protocol from the participant 
 * perspective for being used in the Conversations Factory from Jason agents.
 * @author Bexy Alfonso Espinosa
 */

public class Jason_Fipa_Recruiting_Participant  {


	protected TransitionSystem Ts; 
	
	public Jason_Fipa_Recruiting_Participant(TransitionSystem ts) {
		Ts = ts;
	}
	
	  	/**
		 * Method executed at the beginning of the conversation
		 * @param myProcessor the CProcessor managing the conversation
		 * @param msg first message assigned to this conversation
		 */
		protected void doBegin(CProcessor myProcessor, ACLMessage msg) {
			myProcessor.getInternalData().put("InitialMessage", msg);
		}
		
		class BEGIN_Method implements BeginStateMethod {
			public String run(CProcessor myProcessor, ACLMessage msg) {
				doBegin(myProcessor, msg);
				return "WAIT_FOR_PROXY_MESSAGE";
			};
		}
	
		/**
		 * Method executed when the participant receive a message to proxy
		 * @param myProcessor the CProcessor managing the conversation
		 * @param msg proxy message
		 * @return next conversation state
		 */
		protected String doReceiveProxy(ConvCProcessor myProcessor, ACLMessage msg) {
			Conversation conv =  myProcessor.getConversation();
			String factName = conv.factoryName;
			FRCConversation newConv = new FRCConversation(conv.jasonConvID, conv.internalConvID, "" ,0,
					myProcessor.getMyAgent().getAid(),conv.initiator,factName);
			((ConvCFactory)myProcessor.getMyFactory()).UpdateConv(newConv, myProcessor);
			StringTokenizer msgContent = new StringTokenizer(msg.getContent(),",");
			String tmp =  msgContent.nextToken();
			LiteralImpl tmpcond = new LiteralImpl(LiteralImpl.parseLiteral(tmp));
			newConv.Condition = tmpcond;
			newConv.TargetAgentsMaxNumber = Integer.parseInt(msgContent.nextToken());
			newConv.timeOut = Integer.parseInt(msgContent.nextToken());
			newConv.jasonConvID = msgContent.nextToken();
			newConv.initiator = new AgentID(msg.getSender().name);
			List<Literal> allperc = new ArrayList<Literal>();
			String percept = "receiveproxy("+msg.getSender().name+","+tmpcond.toString()+","+(newConv.timeOut)+","+newConv.jasonConvID+")[source(self)]";
			allperc.add(Literal.parseLiteral(percept));
			((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allperc);
			
			newConv.aquire_semaphore();

			String result;
			if (newConv.ProxyAcceptance) {result = ACLMessage.getPerformative(ACLMessage.AGREE);}
			else result = ACLMessage.getPerformative(ACLMessage.REFUSE);
			return result;
		}

		class RECEIVE_PROXY_Method implements ReceiveStateMethod {
			public String run(CProcessor myProcessor, ACLMessage messageReceived) {
				myProcessor.getInternalData().put("proxyMessage", messageReceived);
				return doReceiveProxy((ConvCProcessor) myProcessor, messageReceived);
			}
		}
	

		/**
		 * Sets the refuse message to a proxy action
		 * @param myProcessor the CProcessor managing the conversation
		 * @param messageToSend refuse message
		 */
		protected void doRefuse(ConvCProcessor myProcessor,
				ACLMessage messageToSend){
			FRCConversation conv =  (FRCConversation) myProcessor.getConversation();
			conv.FinalResult="REFUSE";
			messageToSend.setReceiver(conv.initiator);
		}
		
		class REFUSE_Method implements SendStateMethod {
			public String run(CProcessor myProcessor, ACLMessage messageToSend) {
				messageToSend.setProtocol("fipa-recruiting");
				messageToSend.setSender(myProcessor.getMyAgent().getAid());
				messageToSend.setPerformative(ACLMessage.REFUSE);
				doRefuse((ConvCProcessor) myProcessor, messageToSend);
				return "FINAL_RECRUITING_PARTICIPANT";
			}
		}

		
		/**
		 * Sets the agree message to a proxy action
		 * @param myProcessor the CProcessor managing the conversation
		 * @param messageToSend agree message
		 */
		protected void doAgree(ConvCProcessor myProcessor,ACLMessage messageToSend){
			FRCConversation conv =  (FRCConversation) myProcessor.getConversation();
			messageToSend.setReceiver(conv.initiator);
		}
		
		class AGREE_Method implements SendStateMethod {
			public String run(CProcessor myProcessor, ACLMessage messageToSend) {
				messageToSend.setProtocol("fipa-recruiting");
				messageToSend.setSender(myProcessor.getMyAgent().getAid());
				messageToSend.setPerformative(ACLMessage.AGREE);
				doAgree((ConvCProcessor) myProcessor, messageToSend);
				return "LOCATE_AGENTS";
			}
		}
		
		
		/**
		 * Locate agents to recruit
		 * @param myProcessor the CProcessor managing the conversation
		 * @param proxyMessage proxy message sent by the initiator
		 * @return next conversation state
		 */
		
		protected ArrayList<AgentID> doLocateAgents(ConvCProcessor myProcessor,
				ACLMessage proxyMessage) {
			FRCConversation conv =  (FRCConversation) myProcessor.getConversation();
			List<Literal> allperc = new ArrayList<Literal>();
			String percept = "timetolocateagents("+myProcessor.getMyAgent().getName()+","+ conv.Condition+","+conv.jasonConvID+")[source(self)]";
			allperc.add(Literal.parseLiteral(percept));
			((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allperc);

			conv.aquire_semaphore();

			int i = 0; ArrayList<AgentID> result = new ArrayList<AgentID>();
			while (i<conv.TargetAgentsMaxNumber&&i<conv.TargetAgents.size()){
				result.add(conv.TargetAgents.get(i));
				i++;
			}
			return result;
		}
		
		class LOCATE_AGENTS_Method implements ActionStateMethod{
			public String run(CProcessor myProcessor) {
				ACLMessage proxyMessage = (ACLMessage) myProcessor.getInternalData().get("proxyMessage");
				ArrayList<AgentID> locatedAgents = doLocateAgents((ConvCProcessor) myProcessor, proxyMessage);
				if(locatedAgents.size() == 0)
					return "FAILURE_NO_MATCH";
				else{
					myProcessor.getInternalData().put("locatedAgents", locatedAgents);
					return "START_SUB_PROTOCOL";
				}
			}		
		}

		/**
		 * Method to execute when there is no agents to recruit
		 * @param myProcessor the CProcessor managing the conversation
		 * @param messageToSend no match message
		 */
		protected void doFailureNoMatch(ConvCProcessor myProcessor,ACLMessage messageToSend){
			FRCConversation conv =  (FRCConversation) myProcessor.getConversation();
			conv.FinalResult="FAILURE NOT MATCH";
			messageToSend.setReceiver(conv.initiator);
		}
		
		class FAILURE_NO_MATCH_Method implements SendStateMethod {
			public String run(CProcessor myProcessor, ACLMessage messageToSend) {
				messageToSend.setProtocol("fipa-recruiting");
				messageToSend.setSender(myProcessor.getMyAgent().getAid());
				messageToSend.setPerformative(ACLMessage.FAILURE);
				messageToSend.setHeader("reason", "no-match");
				doFailureNoMatch((ConvCProcessor) myProcessor, messageToSend);  
				return "FINAL_RECRUITING_PARTICIPANT";
			}
		}

		
		/**
		 * Returns the result of a proxy action
		 * @param myProcessor the CProcessor managing the conversation
		 * @param subProtocolMessageResult result of the subprotocol
		 * @return next conversation message
		 */
		protected boolean resultOfSubProtocol(CProcessor myProcessor,
				ACLMessage subProtocolMessageResult) {
			// TODO Auto-generated method stub
			return true;
		}			
		class START_SUB_PROTOCOL_Method implements ActionStateMethod{
			@SuppressWarnings("unchecked")
			@Override
			public String run(CProcessor myProcessor) {
				ACLMessage initialMessage = (ACLMessage) myProcessor.getInternalData().get("proxyMessage");
				ArrayList<AgentID> locatedAgents = (ArrayList<AgentID>) myProcessor.getInternalData().get("locatedAgents");
				for(int i=0; i < locatedAgents.size(); i++){
					initialMessage.setReceiver(locatedAgents.get(i));
					initialMessage.setReplyTo(initialMessage.getSender());
					ACLMessage subProtocolMessageResult = myProcessor.createSyncConversation(initialMessage);
					if(!resultOfSubProtocol(myProcessor, subProtocolMessageResult))
						return "FAILURE_PROXY";
				}
				return "INFORM";
			}		
		}


		/**
		 * Sets the failure message
		 * @param myProcessor the CProcessor managing the conversation
		 * @param messageToSend failure message
		 */
		protected void doFailureProxy(ConvCProcessor myProcessor,ACLMessage messageToSend){
			FRCConversation conv =  (FRCConversation) myProcessor.getConversation();
			conv.FinalResult="FAILURE PROXY";
			messageToSend.setReceiver(conv.initiator);
		}
		
		class FAILURE_PROXY_Method implements SendStateMethod {
			public String run(CProcessor myProcessor, ACLMessage messageToSend) {
				messageToSend.setProtocol("fipa-recruiting");
				messageToSend.setSender(myProcessor.getMyAgent().getAid());
				messageToSend.setPerformative(ACLMessage.FAILURE);
				messageToSend.setHeader("reason", "proxy");
				doFailureProxy((ConvCProcessor) myProcessor, messageToSend);
				return "FINAL_RECRUITING_PARTICIPANT";
			}
		}

		/**
		 * Sets the inform message
		 * @param myProcessor the CProcessor managing the conversation
		 * @param messageToSend inform message
		 */
		protected void doInform(ConvCProcessor myProcessor,ACLMessage messageToSend){
			FRCConversation conv = (FRCConversation)myProcessor.getConversation();
			List<Literal> allperc = new ArrayList<Literal>();
			String percept = "infotosend("+myProcessor.getMyAgent().getName()+","+conv.jasonConvID+")[source(self)]";
			allperc.add(Literal.parseLiteral(percept));
			((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allperc);
			
			conv.aquire_semaphore();
			
			messageToSend.setContent(conv.InfoToSend);
			messageToSend.setReceiver(conv.initiator);
			conv.FinalResult="INFORM";
		}
		
		class INFORM_Method implements SendStateMethod {
			public String run(CProcessor myProcessor, ACLMessage messageToSend) {
				messageToSend.setProtocol("fipa-recruiting");
				messageToSend.setSender(myProcessor.getMyAgent().getAid());
				messageToSend.setPerformative(ACLMessage.INFORM);
				doInform((ConvCProcessor) myProcessor, messageToSend);
				return "FINAL_RECRUITING_PARTICIPANT";
			}
		}
		
		/**
		 * End of the conversation
		 * @param myProcessor the CProcessor managing the conversation
		 * @param messageToSend final message
		 */
		protected void doFinalRecruitingParticipant(ConvCProcessor myProcessor, ACLMessage messageToSend) {
			FRCConversation conv = (FRCConversation)myProcessor.getConversation();
			messageToSend = myProcessor.getLastSentMessage();
			List<Literal> allperc = new ArrayList<Literal>();
			String percept = "conversationended("+myProcessor.getMyAgent().getName()+","+"\""+conv.FinalResult+"\""+","+conv.jasonConvID+")[source(self)]";
			allperc.add(Literal.parseLiteral(percept));
			((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allperc);
			
			myProcessor.getMyAgent().removeFactory(conv.factoryName);

		}

		class FINAL_RECRUITING_PARTICIPANT_Method implements FinalStateMethod {
			public void run(CProcessor myProcessor, ACLMessage messageToSend) {
				doFinalRecruitingParticipant((ConvCProcessor) myProcessor, messageToSend);
			}
		}
		
	/**
	 * Creates a new participant fipa recruiting CFactory
	 * @param name factory's name
	 * @param filter message filter
	 * @param template first message to send
	 * @param availableConversations maximum number of conversation this CFactory can manage simultaneously
	 * @param myAgent agent owner of this CFactory
	 * @return a new fipa recruiting participant factory
	 */
	public ConvCFactory newFactory(String name, MessageFilter filter, ACLMessage template,
			int availableConversations, ConvJasonAgent myAgent) {
		
		// Create factory
		long timeout = 0;
		if (filter == null) {
			filter = new MessageFilter("protocol = fipa-recruiting");
		}
		ConvCFactory theFactory = new ConvCFactory(name, filter,
				availableConversations, myAgent);
		
		// Processor template setup

		ConvCProcessor processor = theFactory.cProcessorTemplate();
		
		// BEGIN State

		BeginState BEGIN = (BeginState) processor.getState("BEGIN");
		BEGIN.setMethod(new BEGIN_Method());
		
		// WAIT_FOR_PROXY_MESSAGE State

		WaitState WAIT_FOR_PROXY_MESSAGE = new WaitState("WAIT_FOR_PROXY_MESSAGE", timeout);
		processor.registerState(WAIT_FOR_PROXY_MESSAGE);
		processor.addTransition(BEGIN, WAIT_FOR_PROXY_MESSAGE);
		
		// RECEIVE_PROXY State
		
		ReceiveState RECEIVE_PROXY = new ReceiveState("RECEIVE_PROXY");
		RECEIVE_PROXY.setMethod(new RECEIVE_PROXY_Method());
		filter = new MessageFilter("protocol = fipa-recruiting");
		RECEIVE_PROXY.setAcceptFilter(filter);
		processor.registerState(RECEIVE_PROXY);
		processor.addTransition(WAIT_FOR_PROXY_MESSAGE, RECEIVE_PROXY);
		
		// REFUSE State

		SendState REFUSE = new SendState("REFUSE");
		REFUSE.setMethod(new REFUSE_Method());
		template = new ACLMessage(ACLMessage.REFUSE);
		REFUSE.setMessageTemplate(template);
		processor.registerState(REFUSE);
		processor.addTransition(RECEIVE_PROXY, REFUSE);
		
		// AGREE State

		SendState AGREE = new SendState("AGREE");
		AGREE.setMethod(new AGREE_Method());
		template = new ACLMessage(ACLMessage.AGREE);
		AGREE.setMessageTemplate(template);
		processor.registerState(AGREE);
		processor.addTransition(RECEIVE_PROXY, AGREE);
		
		// LOCATE_AGENTS State
		
		ActionState LOCATE_AGENTS = new ActionState("LOCATE_AGENTS");
		LOCATE_AGENTS.setMethod(new LOCATE_AGENTS_Method());
		processor.registerState(LOCATE_AGENTS);
		processor.addTransition(AGREE, LOCATE_AGENTS);
		
		// FAILURE_NO_MATCH State

		SendState FAILURE_NO_MATCH = new SendState("FAILURE_NO_MATCH");
		FAILURE_NO_MATCH.setMethod(new FAILURE_NO_MATCH_Method());
		template = new ACLMessage(ACLMessage.FAILURE);
		FAILURE_NO_MATCH.setMessageTemplate(template);
		processor.registerState(FAILURE_NO_MATCH);
		processor.addTransition(LOCATE_AGENTS, FAILURE_NO_MATCH);
		
		// START_SUB_PROTOCOL State
		
		ActionState START_SUB_PROTOCOL = new ActionState("START_SUB_PROTOCOL");
		START_SUB_PROTOCOL.setMethod(new START_SUB_PROTOCOL_Method());
		processor.registerState(START_SUB_PROTOCOL);
		processor.addTransition(LOCATE_AGENTS, START_SUB_PROTOCOL);
		
		// INFORM State

		SendState INFORM = new SendState("INFORM");
		INFORM.setMethod(new INFORM_Method());
		template = new ACLMessage(ACLMessage.INFORM);
		INFORM.setMessageTemplate(template);
		processor.registerState(INFORM);
		processor.addTransition(START_SUB_PROTOCOL, INFORM);
		
		// FAILURE_PROXY State

		SendState FAILURE_PROXY = new SendState("FAILURE_PROXY");
		FAILURE_PROXY.setMethod(new FAILURE_PROXY_Method());
		template = new ACLMessage(ACLMessage.FAILURE);
		FAILURE_PROXY.setMessageTemplate(template);
		processor.registerState(FAILURE_PROXY);
		processor.addTransition(START_SUB_PROTOCOL, FAILURE_PROXY);
		
		// FINAL State

		FinalState FINAL_RECRUITING_PARTICIPANT = new FinalState("FINAL_RECRUITING_PARTICIPANT");

		FINAL_RECRUITING_PARTICIPANT.setMethod(new FINAL_RECRUITING_PARTICIPANT_Method());
		processor.registerState(FINAL_RECRUITING_PARTICIPANT);
		
		processor.addTransition(REFUSE, FINAL_RECRUITING_PARTICIPANT);
		processor.addTransition(FAILURE_PROXY, FINAL_RECRUITING_PARTICIPANT);
		processor.addTransition(FAILURE_NO_MATCH, FINAL_RECRUITING_PARTICIPANT);
		processor.addTransition(INFORM, FINAL_RECRUITING_PARTICIPANT);				
		
		return theFactory;
	}
}
