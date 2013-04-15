package jasonAgentsConversations.conversationsFactory.initiator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;


import jason.asSemantics.TransitionSystem;
import jason.asSyntax.Literal;
import jason.asSyntax.LiteralImpl;
import jasonAgentsConversations.agent.ConvMagentixAgArch;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_RECRUITING_Initiator;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;


public class Jason_Fipa_Recruiting_Initiator extends FIPA_RECRUITING_Initiator {

	//private String AgName;
	private String JasonConversationID;
	protected TransitionSystem Ts; 
	//private AgentID AgID;
	public int TimeOut ;
	
	public Semaphore Protocol_Semaphore = new Semaphore(0,true);
	
	public String initialMsg = "";
	public String Participant;
	LiteralImpl Condition = new LiteralImpl("");
	public int ParticipantsNumber = 0;
	public String ConversationResult = "";
	
	public Jason_Fipa_Recruiting_Initiator(String sagName, String sagentConversationID,
			TransitionSystem ts, AgentID aid, int iTO) {
		//AgName = sagName;
		JasonConversationID = sagentConversationID;
		Ts = ts;
		//AgID = aid;
		TimeOut = iTO;
	}	
	
	
	@Override
	protected void setProxyMessage(CProcessor myProcessor,
			ACLMessage messageToSend) {
		messageToSend.setContent(Condition.toString().trim()+","+ParticipantsNumber+","+TimeOut);
		messageToSend.setPerformative(ACLMessage.INFORM);
		messageToSend.setReceiver(new AgentID(Participant));
	}
	
	@Override
	protected void doReceiveRefuse(CProcessor myProcessor, ACLMessage msg) {
		ConversationResult = "REFUSE";
		List<Literal> allperc = new ArrayList<Literal>();
		String percept = "conversationresult(\""+ConversationResult+"\","+JasonConversationID+")[source(self)]";
		allperc.add(Literal.parseLiteral(percept));
		((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allperc);
	}

	@Override	
	protected void doReceiveFailureNoMatch(CProcessor myProcessor, ACLMessage msg) {
		ConversationResult = "NO AGENT MATCH FOUND!";
		List<Literal> allperc = new ArrayList<Literal>();
		String percept = "conversationresult(\""+ConversationResult+"\","+JasonConversationID+")[source(self)]";
		allperc.add(Literal.parseLiteral(percept));
		((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allperc);
		Ts.getAg().getLogger().info("Receive failure not match... "+msg.getSender().getLocalName());
	}
	
	@Override
	protected void doReceiveFailureProxy(CProcessor myProcessor, ACLMessage msg) {
		ConversationResult = "PROXY ACTION FAILED!";
		List<Literal> allperc = new ArrayList<Literal>();
		String percept = "conversationresult(\""+ConversationResult+"\","+JasonConversationID+")[source(self)]";
		allperc.add(Literal.parseLiteral(percept));
		((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allperc);
		Ts.getAg().getLogger().info("Receive failure proxy... "+msg.getSender().getLocalName());
	}
	
	@Override
	protected void doReceiveInform(CProcessor myProcessor, ACLMessage msg) {
		//msg has the information of 
		List<Literal> allperc = new ArrayList<Literal>();
		String percept = "receiveinform("+Participant+","+JasonConversationID+")[source(self)]";
		allperc.add(Literal.parseLiteral(percept));
		((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allperc);

		try 
		{
			Protocol_Semaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ConversationResult = "PROXY WORKED!";
		allperc = new ArrayList<Literal>();
		percept = "conversationresult(\""+ConversationResult+"\","+JasonConversationID+")[source(self)]";
		allperc.add(Literal.parseLiteral(percept));
		((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allperc);
		
	}
	
	@Override	
	protected void doFinalRecruitingInitiator(CProcessor myProcessor, ACLMessage messageToSend) {
		super.doFinalRecruitingInitiator(myProcessor, messageToSend);
		
		List<Literal> allperc = new ArrayList<Literal>();
		String percept = "conversationended("+Participant+","+JasonConversationID+")[source(self)]";
		allperc.add(Literal.parseLiteral(percept));
		((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allperc);
		
	}
	
}
