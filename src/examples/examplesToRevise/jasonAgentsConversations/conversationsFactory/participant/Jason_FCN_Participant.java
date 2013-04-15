package jasonAgentsConversations.conversationsFactory.participant;

import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_CONTRACTNET_Participant;
import es.upv.dsic.gti_ia.core.ACLMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;


import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.core.AgentID;
import jason.asSemantics.*;
import jason.asSyntax.*;
import jasonAgentsConversations.agent.ConvMagentixAgArch;

class Jason_FCN_Participant extends FIPA_CONTRACTNET_Participant {

	//private String AgName;
	private AgentID AgID;
	private AgentID Sender = null; //the I must send my messages
	private String JasonConversationID;
	protected TransitionSystem ts; 
	public int mutex = 0;
	
	public int TimeOut ;
	
	public Monitor   myMonitor;
	public Semaphore mySem = new Semaphore(0,true);
	//block of results
	public String answerToProposal = "propose"; //it can change to "refuse" or "notUnderstood"
	public String myProposal = "";
	public String infoToSend = "";
	public boolean taskDone=true;
	
	
	public Jason_FCN_Participant(String agName2, String sagentConversationID,
			TransitionSystem ts2, AgentID aid, int TO) {

		//AgName = agName2;
		JasonConversationID = sagentConversationID;
		ts = ts2;
		AgID = aid;
		TimeOut = TO;
	
	}
	
	public void setConversationID(String sagentConversationID){
		JasonConversationID = sagentConversationID;
	}

	
	@Override
	protected String doReceiveSolicit(CProcessor myProcessor,
			ACLMessage msg) {
		// accept all the solicits
		String result = "";
		Sender = myProcessor.getLastReceivedMessage().getSender();
		//StringTokenizer content = new StringTokenizer(myProcessor.getLastReceivedMessage().getContent(),"-");
		//String SenderAgName = content.nextToken();
		String newMsgContent = "callforproposal"+"("+Sender.name+","+msg.getContent()+","+JasonConversationID+")[source(self)]";
		//ts.getAg().getLogger().info("HE RECIBIDO SOLICITUD: "+msg.toString());
		List<Literal> allpercep = new ArrayList<Literal>();
		
		allpercep.add(Literal.parseLiteral(newMsgContent));
		((ConvMagentixAgArch)ts.getUserAgArch()).setPerception(allpercep);
		
	
		try {
			mySem.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if (answerToProposal=="propose"){
			result = "SEND_PROPOSAL";
		}else if (answerToProposal=="refuse"){
			result = "SEND_REFUSE";
		}else if (answerToProposal=="notUnderstood"){
			result = "SEND_NOT_UNDERSTOOD";
		}
		//ts.getAg().getLogger().info("HE RECIBIDO SOLICITUD Y RESPUESTA ES : "+answerToProposal);	
		return result;
	}
	
	@Override
	protected void doSendFailure(CProcessor myProcessor,
			ACLMessage messageToSend){
		super.doSendFailure(myProcessor, messageToSend);
	}
	
	
	
	@Override
	protected void doSendInfo(CProcessor myProcessor,
			ACLMessage messageToSend) {
		
		messageToSend.setSender(AgID);
		messageToSend.setReceiver(myProcessor.getLastReceivedMessage().getSender());
		messageToSend.setContent("I'm "+AgID+": "+infoToSend);
		messageToSend.setPerformative(ACLMessage.INFORM);
		messageToSend.setProtocol("fipa-contract-net");		
	}

	@Override
	protected void doSendProposal(CProcessor myProcessor,
			ACLMessage messageToSend) {

/*at this point the field "myProposal" has already a value that was set in
 * the "makeProposal" steep 
 */
		//ts.getAg().getLogger().info("PROPUESTA DE "+AgID+": "+myProposal);
		messageToSend.setSender(AgID);
		messageToSend.setReceiver(Sender);
		messageToSend.setContent(String.valueOf(myProposal));
		messageToSend.setPerformative(ACLMessage.PROPOSE);
		messageToSend.setProtocol("fipa-contract-net");
	}

	@Override
	protected String doTask(CProcessor myProcessor,
			ACLMessage solicitMessage) {
		String result = ""; 
		
		List<Literal> allpercep = new ArrayList<Literal>();
		allpercep.add(Literal.parseLiteral("timetodotask("+Sender.name+","+JasonConversationID+")[source(self)]"));
		((ConvMagentixAgArch)ts.getUserAgArch()).setPerception(allpercep);

		try {
			mySem.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(infoToSend);
		if (taskDone){ 
			result = "SEND_INFORM";}
		else {
			result = "SEND_FAILURE";}
		return result;
	}

}