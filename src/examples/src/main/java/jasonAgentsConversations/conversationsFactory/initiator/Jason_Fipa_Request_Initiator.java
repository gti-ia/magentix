package jasonAgentsConversations.conversationsFactory.initiator;

import jason.asSemantics.TransitionSystem;
import jason.asSyntax.Literal;
import jasonAgentsConversations.agent.ConvMagentixAgArch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;


import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_REQUEST_Initiator;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;


public class Jason_Fipa_Request_Initiator extends FIPA_REQUEST_Initiator {

	//private String AgName;
	private String JasonConversationID;
	protected TransitionSystem Ts; 
	//private AgentID AgID;
	public int TimeOut ;
	
	public Semaphore Protocol_Semaphore = new Semaphore(0,true);
	
	public String initialMsg = "";
	public String requestMsg = "";
	public String Participant;
	public String myAcceptances;
	public String myRejections;

	
	public Jason_Fipa_Request_Initiator(String sagName, String sagentConversationID,
			TransitionSystem ts, AgentID aid, int iTO) {
		//AgName = sagName;
		JasonConversationID = sagentConversationID;
		Ts = ts;
		//AgID = aid;
		TimeOut = iTO;
		
	}
	
	@Override
	protected void doBegin(CProcessor myProcessor,
			ACLMessage messageToSend) {
		messageToSend.setContent(initialMsg);
		super.doBegin(myProcessor, messageToSend);

	}
	
	@Override
	protected void doRequest(CProcessor myProcessor,
			ACLMessage messageToSend) {
		try {
			Protocol_Semaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		messageToSend.setContent(requestMsg);
		messageToSend.setProtocol("fipa-request");
		messageToSend.setPerformative(ACLMessage.REQUEST);
		messageToSend.setReceiver(new AgentID("frequest_participant"));
		messageToSend.setSender(myProcessor.getMyAgent().getAid() );
		
	}
	
	@Override
	protected void doInform(CProcessor myProcessor, ACLMessage msg) {
		// Updating task done in agent beliefs

		List<Literal> allperc = new ArrayList<Literal>();
		String percept = "taskdonesuccessfully("+Participant+","+JasonConversationID+")[source(self)]";
		allperc.add(Literal.parseLiteral(percept));
		((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allperc);
		try {
			Protocol_Semaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		msg.setProtocol("fipa-request");
		msg.setPerformative(ACLMessage.REQUEST);
	}
	
	@Override	
	protected void doFinal(CProcessor myProcessor, ACLMessage messageToSend){
		List<Literal> allperc = new ArrayList<Literal>();
		String percept = "conversationended("+JasonConversationID+")[source(self)]";
		allperc.add(Literal.parseLiteral(percept));
		((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allperc);
		messageToSend.setProtocol("fipa-request");
		messageToSend.setPerformative(ACLMessage.REQUEST);
		super.doFinal(myProcessor, messageToSend);
	}

}
