package jasonAgentsConversations.conversationsFactory.participant;

import jason.asSemantics.TransitionSystem;
import jason.asSyntax.Literal;
import jasonAgentsConversations.agent.ConvMagentixAgArch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;


import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_REQUEST_Participant;
import es.upv.dsic.gti_ia.cAgents.protocols.Protocol_Template;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;


public class Jason_Fipa_Request_Participant extends FIPA_REQUEST_Participant {

	//private String AgName;
	//private AgentID AgID;
	private String JasonConversationID;
	protected TransitionSystem Ts; 
	
	public int TimeOut ;
	
	public Semaphore Protocol_Semaphore = new Semaphore(0,true);
	
	public String Initiator ;
	public String RequestResult = "";
	public String TaskDesition = "";
	public String TaskResult = "" ;
	public String Task = "" ;
	
	public Jason_Fipa_Request_Participant(String sagName, String sagentConversationID,
			TransitionSystem ts, AgentID aid, int iTO) {
		//AgName = sagName;
		JasonConversationID = sagentConversationID;
		Ts = ts;
		//AgID = aid;
		TimeOut = iTO;
		
	}
	
	public void setConversationID(String sagentConversationID){
		JasonConversationID = sagentConversationID;
	}
	
	
	@Override
	protected String doReceiveRequest(CProcessor myProcessor, ACLMessage request) {

		List<Literal> allperc = new ArrayList<Literal>();
		Task = request.getContent();
		String percept = "request("+request.getSender().name+","+Task+","+JasonConversationID+")[source(self)]";
		allperc.add(Literal.parseLiteral(percept));
		((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allperc);
		
		try {
			Protocol_Semaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		String result = null; 
		if (RequestResult==Protocol_Template.AGREE_STEEP){
			result = "AGREE";
		}else 
		if (RequestResult==Protocol_Template.REFUSE_STEEP){
			result = "REFUSE";
		}else 
		if (RequestResult==Protocol_Template.NOT_UNDERSTOOD_STEEP){
			result = "NOT_UNDERSTOOD";
		} 

	  return result;
	}

	@Override
	protected String doAction(CProcessor myProcessor) {
		List<Literal> allperc = new ArrayList<Literal>();
		String percept = "timetodotask("+Task+","+JasonConversationID+")[source(self)]";
		allperc.add(Literal.parseLiteral(percept));
		((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allperc);
		
		try {
			Protocol_Semaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		String result = null; 
		if (TaskDesition==Protocol_Template.INFORM_STEEP){
			result = "INFORM";
		}else 
		if (TaskDesition==Protocol_Template.FAILURE_STEEP){
			result = "FAILURE";
		}
			
	  return result;
	}

	@Override
	protected void doInform(CProcessor myProcessor, ACLMessage response) {
		response.setProtocol("fipa-request");
		response.setPerformative(ACLMessage.INFORM);
		response.setContent(TaskResult);
	}
}
