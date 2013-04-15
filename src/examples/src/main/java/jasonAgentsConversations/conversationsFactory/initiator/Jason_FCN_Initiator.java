package jasonAgentsConversations.conversationsFactory.initiator;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.Semaphore;


import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_CONTRACTNET_Initiator;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import jason.asSemantics.*;
import jason.asSyntax.*;
import jasonAgentsConversations.agent.ConvMagentixAgArch;

class Jason_FCN_Initiator extends FIPA_CONTRACTNET_Initiator {
	//private String AgName;
	private AgentID AgID;
	private String JasonConversationID;
	protected TransitionSystem ts; 
	public int mutex = 0;
	
	public int TimeOut ;
	
	public Semaphore mySem = new Semaphore(0,true);
	
	public String myAcceptances;
	public String myRejections;

	
	public Jason_FCN_Initiator(String agName2, String sagentConversationID,
			TransitionSystem ts2, AgentID aid, int TO) {
		//AgName = agName2;
		JasonConversationID = sagentConversationID;
		ts = ts2;
		AgID = aid;
		TimeOut = TO;
		
	}


	@Override
	protected void doEvaluateProposals(CProcessor myProcessor,
			ArrayList<ACLMessage> proposes,
			ArrayList<ACLMessage> acceptances,
			ArrayList<ACLMessage> rejections) {

	
		String tmpproposal;
		String tmpsender;
		String percept ;
		List<Literal> allperc = new ArrayList<Literal>();
		
		//ts.getAg().getLogger().info("PROPUESTAS: "+proposes.size());
		for (ACLMessage msg : proposes){
			tmpproposal =  msg.getContent();
			tmpsender = msg.getSender().name;
			percept = "proposal("  +tmpproposal+ "," +tmpsender+ "," + JasonConversationID+")[source(self)]" ;
			//ts.getAg().getLogger().info(percept);
			allperc.add(Literal.parseLiteral(percept));
		}
		percept = "proposalsevaluationtime("+JasonConversationID+")[source(self)]";
		//ts.getAg().getLogger().info(percept);
		allperc.add(Literal.parseLiteral(percept));
		
		((ConvMagentixAgArch)ts.getUserAgArch()).setPerception(allperc);
		
		
		try {
			mySem.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		/*
		 * When this lien is reached it is because the time
		 * has expired or because the monitor was released and 
		 * consequently the accepted and rejected proposals have
		 * been updated. At this point the process go on with
		 * the current values of "acceptances" and "rejections"
		 */
			
		StringTokenizer argtokenacc = new StringTokenizer(
					myAcceptances.substring(1, myAcceptances.length()-1),",");// 8,participan1,9,participant2,...
	
		StringTokenizer argtokenrej = new StringTokenizer(
				myRejections.substring(1, myRejections.length()-1),",");// 8,participan1),(9,participant2),(...

			String prop;
			String sender;
			while (argtokenacc.hasMoreTokens()) {
				//propu = argtokenacc.nextToken(); // 8,participan1
				//propu = propu.substring(1, propu.length() - 1);/
				//elem = new StringTokenizer(argtokenacc,","); // 8,participan1
				//Integer.parseInt(elem.nextToken()); // 8
				prop = argtokenacc.nextToken().trim(); //8
				sender = argtokenacc.nextToken().trim(); // participan1
				int index = 0;
				while (index<proposes.size()&&(!prop.equals(proposes.get(index).getContent()))&&(!sender.equals(proposes.get(index).getSender().name))){
					index++;
				}
				ACLMessage accept = new ACLMessage(
						ACLMessage.ACCEPT_PROPOSAL);
				accept.setContent("I accept your proposal");
				accept.setReceiver(proposes.get(index).getSender());
				accept.setSender(AgID);
				accept.setProtocol("fipa-contract-net");
				acceptances.add(accept);
			}

			//String reje;
			while (argtokenrej.hasMoreTokens()) {
				//reje = argtokenrej.nextToken();// 8,participan1
				//reje = reje.substring(1, reje.length() - 1);
				//elem = new StringTokenizer(reje,",");
				//Integer.parseInt(elem.nextToken()); // 8

				prop = argtokenrej.nextToken().trim(); //8
				sender = argtokenrej.nextToken().trim(); // participan1
				int index = 0;
				while (index<proposes.size()&&(!prop.equals(proposes.get(index).getContent()))&&(!sender.equals(proposes.get(index).getSender().name))){
					index++;
				}
				ACLMessage reject = new ACLMessage(
						ACLMessage.REJECT_PROPOSAL);
				reject.setContent("rejected");
				reject.setReceiver(proposes.get(index).getSender());
				reject.setSender(AgID);
				reject.setProtocol("fipa-contract-net");
				rejections.add(reject);
			}
	

		for (int i = 0; i < acceptances.size(); i++) {
			System.out.println("I accept "
					+ acceptances.get(i).getReceiver()
					+ "'s proposal");
		}

		for (int i = 0; i < rejections.size(); i++) {

			System.out.println("I reject "
					+ rejections.get(i).getReceiver()
					+ "'s proposal");
		}
	}
	

	@Override
	protected void doReceiveFailure(CProcessor myProcessor, ACLMessage msg) {

		List<Literal> allperc = new ArrayList<Literal>();
		String tmpsender = myProcessor.getLastReceivedMessage().getSender().name;
		
		String percept = "taskNotDone("+tmpsender+","+JasonConversationID+")[source(self)]";
		allperc.add(Literal.parseLiteral(percept));
		((ConvMagentixAgArch)ts.getUserAgArch()).setPerception(allperc);
	}
	
	@Override
	protected void doReceiveInform(CProcessor myProcessor,
			ACLMessage msg) {

		List<Literal> allperc = new ArrayList<Literal>();
		
		String tmpsender = myProcessor.getLastReceivedMessage().getSender().name;
		String percept = "taskDone("+tmpsender+","+JasonConversationID+")[source(self)]";
		allperc.add(Literal.parseLiteral(percept));
		((ConvMagentixAgArch)ts.getUserAgArch()).setPerception(allperc);
		System.out.println("Result: " + msg.getContent());
	}
	
	@Override
	protected void doFinal(CProcessor myProcessor, ACLMessage messageToSend) {
		super.doFinal(myProcessor, messageToSend);
		List<Literal> allperc = new ArrayList<Literal>();
		
		String percept = "resultsreceived("+JasonConversationID+")[source(self)]";
		allperc.add(Literal.parseLiteral(percept));
		((ConvMagentixAgArch)ts.getUserAgArch()).setPerception(allperc);
		
		try {
			mySem.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		percept = "conversationended("+JasonConversationID+")[source(self)]";
		allperc.clear();
		allperc.add(Literal.parseLiteral(percept));
		((ConvMagentixAgArch)ts.getUserAgArch()).setPerception(allperc);
	}
	
	

}