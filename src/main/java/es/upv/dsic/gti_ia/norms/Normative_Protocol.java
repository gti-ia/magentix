package es.upv.dsic.gti_ia.norms;


import java.io.IOException;
import java.util.ArrayList;

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
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.MessageFilter;



public abstract class Normative_Protocol{

	private ArrayList<String> messageContent = null;
	private String jasonAgentName = "";
	private ArrayList<String> result = null;

	public CFactory newFactory(String name, MessageFilter filter,
			int availableConversations, CAgent myAgent, ArrayList<String> messageContent, String jasonAgentName, ArrayList<String> result) {
		
			this.messageContent = messageContent; 
			this.jasonAgentName = jasonAgentName;
			this.result = result;
			//----------------------------wait STATE----------------------------------
			WaitState WAIT = new WaitState("WAIT", 0);




			//------------------------------------------------------------------------
			//-----------------------Initiator CFactory definition------------------
			//------------------------------------------------------------------------

			CFactory theFactory = new CFactory(name, filter,
					availableConversations, myAgent);

			//----------------------------BEGIN STATE----------------------------------
			BeginState BEGIN = (BeginState) theFactory.cProcessorTemplate().getState("BEGIN");
			BEGIN.setMethod(new BEGIN_Method());


			//----------------------------QUERY STATE----------------------------------
			SendState QUERY = new SendState("QUERY");
			QUERY.setMethod(new QUERY_Method());
			theFactory.cProcessorTemplate().registerState(QUERY);
			theFactory.cProcessorTemplate().addTransition(BEGIN, QUERY);

			//----------------------------WAIT STATE----------------------------------
			theFactory.cProcessorTemplate().registerState(WAIT);
			theFactory.cProcessorTemplate().addTransition(QUERY, WAIT);


			//----------------------------RECEIVE STATE----------------------------------
			ReceiveState RECEIVE = new ReceiveState("RECEIVE");
			RECEIVE.setAcceptFilter(null); // null -> accept any message
			RECEIVE.setMethod(new RECEIVE_Method());
			theFactory.cProcessorTemplate().registerState(RECEIVE);
			theFactory.cProcessorTemplate().addTransition(WAIT, RECEIVE);



			//----------------------------FINAL STATE----------------------------------
			FinalState FINAL = new FinalState("FINAL");
			FINAL.setMethod(new FINAL_Method());
			theFactory.cProcessorTemplate().registerState(FINAL);
			theFactory.cProcessorTemplate().addTransition(RECEIVE, FINAL);



			return theFactory;

	}

	//------------------------------------------------------------------------
	//-----------------------CFactory implementation--------------------------
	//------------------------------------------------------------------------


	//	class BEGIN_MANAGER_Method implements BeginStateMethod {
	//
	//		public String run(CProcessor myProcessor, ACLMessage msg) {
	//
	//
	//			return "WAIT";
	//		};
	//
	//	}


	protected void doBegin(CProcessor myProcessor, ACLMessage msg){
	}
	class BEGIN_Method implements BeginStateMethod {

		public String run(CProcessor myProcessor, ACLMessage msg) {

			doBegin(myProcessor,msg);
			return "QUERY";
		};

	}

	protected void doQuery(CProcessor myProcessor, ACLMessage msg){
		
	}
	class QUERY_Method implements SendStateMethod {
		int n=0;

		public String run(CProcessor myProcessor, ACLMessage messageToSend) {

			doQuery(myProcessor,messageToSend);
			String state = "WAIT";

			//Sends a message with rules.


			messageToSend.setPerformative(ACLMessage.QUERY_REF);
			messageToSend.setLanguage("AgentSpeak");
			messageToSend.setSender(myProcessor.getMyAgent().getAid());
			messageToSend.setReceiver(new AgentID(jasonAgentName));
			messageToSend.setReplyWith(myProcessor.getConversationID());


			

			try {
				messageToSend.setContentObject(messageContent);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//messageToSend.setContent("allocateRole(participant, virtual,_,AgentName)");


			System.out.println("Voy a enviar un mensaje al agente Jason");


			return state;
		}

	}

	protected void doReceive(CProcessor myProcessor, ACLMessage msg){
	}

	class RECEIVE_Method implements ReceiveStateMethod {
		int n=0;

		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			
			
			String state = "FINAL";
			System.out.println("Header: "+ messageReceived.getHeaderValue("activatedNorm"));
			System.out.println("Resultado: "+ messageReceived.getContent());
			result.add(messageReceived.getHeaderValue("activatedNorm"));
			doReceive(myProcessor,messageReceived);
			return state;
		}

	}

	protected void doFinal(CProcessor myProcessor, ACLMessage msg){
	}

	class FINAL_Method implements FinalStateMethod {
		public void run(CProcessor myProcessor, ACLMessage responseMessage) {
			//myProcessor.ShutdownAgent();
			doFinal(myProcessor,responseMessage);
		}

	}



}
