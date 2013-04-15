package jasonAgentsConversations.conversationsFactory.participant;

import jason.asSemantics.TransitionSystem;
import jason.asSyntax.Literal;
import jason.asSyntax.LiteralImpl;
import jasonAgentsConversations.agent.ConvMagentixAgArch;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.Semaphore;


import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_RECRUITING_Participant;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;


public class Jason_Fipa_Recruiting_Participant extends
		FIPA_RECRUITING_Participant {


	private String AgName;
	//private AgentID AgID;
	private String JasonConversationID;
	protected TransitionSystem Ts; 
	public int TimeOut ;
	
	public Semaphore Protocol_Semaphore = new Semaphore(0,true);
	
	public LiteralImpl MsgProxyContent = new LiteralImpl("");
	public boolean ProxyAcceptance = false; 
	public ArrayList<AgentID> TargetAgents = new ArrayList<AgentID>();
	public Literal Condition = new LiteralImpl("");
	public int TargetAgentsMaxNumber = 0;
	public String InfoToSend = "";
	public String FinalResult = "";
	
	public Jason_Fipa_Recruiting_Participant(String sagName, String sagentConversationID,
			TransitionSystem ts, AgentID aid, int iTO) {
		AgName = sagName;
		JasonConversationID = sagentConversationID;
		Ts = ts;
		//AgID = aid;
		TimeOut = iTO;
		
	}
	
	
	@Override
	protected String doReceiveProxy(CProcessor myProcessor, ACLMessage msg) {
		StringTokenizer msgContent = new StringTokenizer(msg.getContent(),",");
		LiteralImpl tmpcond = new LiteralImpl(LiteralImpl.parseLiteral( msgContent.nextToken()));
		Condition = tmpcond;
		TargetAgentsMaxNumber = Integer.parseInt(msgContent.nextToken());
		TimeOut = Integer.parseInt(msgContent.nextToken());
		List<Literal> allperc = new ArrayList<Literal>();
		String percept = "receiveproxy("+msg.getSender().name+","+tmpcond.toString()+","+(TimeOut)+","+JasonConversationID+")[source(self)]";
		allperc.add(Literal.parseLiteral(percept));
		((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allperc);
		try 
		{
			Protocol_Semaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String result;
		if (ProxyAcceptance) {result = ACLMessage.getPerformative(ACLMessage.AGREE);}
		else result = ACLMessage.getPerformative(ACLMessage.REFUSE);
		return result;
	}

	@Override
	protected ArrayList<AgentID> doLocateAgents(CProcessor myProcessor,
			ACLMessage proxyMessage) {
		List<Literal> allperc = new ArrayList<Literal>();
		String percept = "timetolocateagents("+AgName+","+ Condition+","+JasonConversationID+")[source(self)]";
		allperc.add(Literal.parseLiteral(percept));
		((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allperc);

		try 
		{
			Protocol_Semaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		int i = 0; ArrayList<AgentID> result = new ArrayList<AgentID>();
		while (i<TargetAgentsMaxNumber&&i<TargetAgents.size()){
			result.add(TargetAgents.get(i));
			i++;
		}
		return result;
		
	}

	@Override
	protected boolean resultOfSubProtocol(CProcessor myProcessor,
			ACLMessage subProtocolMessageResult) {
		// TODO Auto-generated method stub
		
		//fipa-recruiting
		return true;
	}
	
	protected void doInform(CProcessor myProcessor,ACLMessage messageToSend){
		List<Literal> allperc = new ArrayList<Literal>();
		String percept = "infotosend("+AgName+","+JasonConversationID+")[source(self)]";
		allperc.add(Literal.parseLiteral(percept));
		((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allperc);
		try 
		{
			Protocol_Semaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		messageToSend.setContent(InfoToSend);
		FinalResult="INFORM";
	}
	
	protected void doFinalRecruitingParticipant(CProcessor myProcessor, ACLMessage messageToSend) {
		super.doFinalRecruitingParticipant(myProcessor, messageToSend);
		List<Literal> allperc = new ArrayList<Literal>();
		String percept = "conversationended("+AgName+","+"\""+FinalResult+"\""+","+JasonConversationID+")[source(self)]";
		allperc.add(Literal.parseLiteral(percept));
		((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allperc);
	}

	protected void doRefuse(CProcessor myProcessor,
			ACLMessage messageToSend){
		FinalResult="REFUSE";
	}
	
	protected void doFailureNoMatch(CProcessor myProcessor,ACLMessage messageToSend){
		FinalResult="FAILURE NOT MATCH";
	}
	
	protected void doFailureProxy(CProcessor myProcessor,ACLMessage messageToSend){
		FinalResult="FAILURE PROXY";
	}
	
}
