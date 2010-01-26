package Thomas_Example;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.CProcessorFactory;
import es.upv.dsic.gti_ia.cAgents.FinalState;
import es.upv.dsic.gti_ia.cAgents.GenericBeginState;
import es.upv.dsic.gti_ia.cAgents.GenericCancelState;
import es.upv.dsic.gti_ia.cAgents.GenericFinalState;
import es.upv.dsic.gti_ia.cAgents.GenericNotAcceptedMessagesState;
import es.upv.dsic.gti_ia.cAgents.GenericReceiveState;
import es.upv.dsic.gti_ia.cAgents.GenericSendingErrorsState;
import es.upv.dsic.gti_ia.cAgents.GenericTerminatedFatherState;
import es.upv.dsic.gti_ia.cAgents.RequestInitiatorFactory;
import es.upv.dsic.gti_ia.cAgents.SendState;
import es.upv.dsic.gti_ia.cAgents.WaitState;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.Configuration;

public class Payee extends CAgent{

	Configuration c;	
	String SFServiceDesciptionLocation;
	String configuration;
	
	public Payee(AgentID aid) throws Exception {
		super(aid);
		DOMConfigurator.configure("configuration/loggin.xml");
		c = Configuration.getConfiguration();
		SFServiceDesciptionLocation = c.getSFServiceDesciptionLocation();
		configuration = c.getOMSServiceDesciptionLocation();
	}

	@Override
	protected void setFactories() {
		String parentConversationId = "C"+this.hashCode()+System.currentTimeMillis();
		ACLMessage template = new ACLMessage(ACLMessage.REQUEST); //the template has no use in this example
		CProcessorFactory parentFactory = new CProcessorFactory("parentFactory", template, 1);
		CProcessor parentProcessor = parentFactory.getCProcessor();
		
		//B
		parentProcessor.registerFirstState(new GenericBeginState("B"));
		
		//SAR activate Acquire Role
		SendState1 SAR = new SendState1("SAR");
		ACLMessage sendTemplate = new ACLMessage(ACLMessage.REQUEST);
		sendTemplate.setHeader("start", "acquireRole");
		sendTemplate.setContent("Acquire Role");
		sendTemplate.setSender(getAid());
		sendTemplate.setReceiver(getAid());
		sendTemplate.setConversationId("C"+sendTemplate.hashCode()+System.currentTimeMillis());
		SAR.setMessageTemplate(sendTemplate);
		parentProcessor.registerState(SAR);
		parentProcessor.addTransition("B", "SAR");
		
		//WAR
		parentProcessor.registerState(new WaitState("WAR",10000));
		parentProcessor.addTransition("SAR", "WAR");
		
		//RWAR
		GenericReceiveState RWAR = new GenericReceiveState("RWAR");
		ACLMessage receiveFilter = new ACLMessage(ACLMessage.INFORM);
		receiveFilter.setHeader("purpose", "waitMessage");
		RWAR.setAcceptFilter(receiveFilter);
		parentProcessor.registerState(RWAR);
		parentProcessor.addTransition("WAR", "RWAR");
		parentProcessor.addTransition("RWAR", "WAR");
		
		//RAR1
		GenericReceiveState RAR1 = new GenericReceiveState("RAR1");
		receiveFilter = new ACLMessage(ACLMessage.INFORM);
		receiveFilter.setHeader("inform", "done");
		RAR1.setAcceptFilter(receiveFilter);
		parentProcessor.registerState(RAR1);
		parentProcessor.addTransition("WAR", "RAR1");
		
		//RAR2
		GenericReceiveState RAR2 = new GenericReceiveState("RAR2");
		receiveFilter = new ACLMessage(ACLMessage.FAILURE);
		RAR2.setAcceptFilter(receiveFilter);
		parentProcessor.registerState(RAR2);
		parentProcessor.addTransition("WAR", "RAR2");
		
		//Create and attach acquire role request conversation
		template = new ACLMessage(ACLMessage.REQUEST);
		template.setHeader("start", "acquireRole");
		
		//mensaje que se enviara durante el send del request
		ACLMessage sendMessage = new ACLMessage(ACLMessage.REQUEST);
		sendMessage.setProtocol("fipa-request");
		String RoleID = "member";
		String UnitID = "virtual";
		String call = configuration + "AcquireRoleProcess.owl RoleID=" + RoleID + " UnitID="+ UnitID;
		sendMessage.setContent(call);
		sendMessage.setReceiver(new AgentID("OMS"));

		RequestInitiatorFactory ARfactory = new RequestInitiatorFactory("ARfactory", template, sendMessage);
		
		//change final state
		try {
			ARfactory.changeState(new FinalState1("F"));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		parentFactory.addChild(ARfactory);
		this.addFactory(ARfactory);
		
		//Second Acquire Role
		//SAR_2 activate Acquire Role
		SendState1 SAR_2 = new SendState1("SAR_2");
		sendTemplate = new ACLMessage(ACLMessage.REQUEST);
		sendTemplate.setHeader("start", "acquireRole_2");
		sendTemplate.setContent("Acquire Role");
		sendTemplate.setSender(getAid());
		sendTemplate.setReceiver(getAid());
		sendTemplate.setConversationId("C"+sendTemplate.hashCode()+System.currentTimeMillis());
		SAR_2.setMessageTemplate(sendTemplate);
		parentProcessor.registerState(SAR_2);
		parentProcessor.addTransition("RAR1", "SAR_2");
		
		//WAR_2
		parentProcessor.registerState(new WaitState("WAR_2",10000));
		parentProcessor.addTransition("SAR_2", "WAR_2");
		
		//RWAR
		GenericReceiveState RWAR_2 = new GenericReceiveState("RWAR_2");
		receiveFilter = new ACLMessage(ACLMessage.INFORM);
		receiveFilter.setHeader("purpose", "waitMessage");
		RWAR_2.setAcceptFilter(receiveFilter);
		parentProcessor.registerState(RWAR_2);
		parentProcessor.addTransition("WAR_2", "RWAR_2");
		parentProcessor.addTransition("RWAR_2", "WAR_2");
		
		//RAR1_2
		GenericReceiveState RAR1_2 = new GenericReceiveState("RAR1_2");
		receiveFilter = new ACLMessage(ACLMessage.INFORM);
		receiveFilter.setHeader("inform", "done");
		RAR1_2.setAcceptFilter(receiveFilter);
		parentProcessor.registerState(RAR1_2);
		parentProcessor.addTransition("WAR_2", "RAR1_2");
		
		//RAR2_2
		GenericReceiveState RAR2_2 = new GenericReceiveState("RAR2_2");
		receiveFilter = new ACLMessage(ACLMessage.FAILURE);
		RAR2_2.setAcceptFilter(receiveFilter);
		parentProcessor.registerState(RAR2_2);
		parentProcessor.addTransition("WAR_2", "RAR2_2");
		
		//Create and attach acquire role request conversation
		template = new ACLMessage(ACLMessage.REQUEST);
		template.setHeader("start", "acquireRole_2");
		
		//mensaje que se enviara durante el send del request
		sendMessage = new ACLMessage(ACLMessage.REQUEST);
		sendMessage.setProtocol("fipa-request");
		RoleID = "payee";
		UnitID = "travelagency";
		call = configuration + "AcquireRoleProcess.owl RoleID=" + RoleID + " UnitID="+ UnitID;
		sendMessage.setContent(call);
		sendMessage.setReceiver(new AgentID("OMS"));

		RequestInitiatorFactory AR_2factory = new RequestInitiatorFactory("ARfactory", template, sendMessage);
		
		//change final state
		try {
			AR_2factory.changeState(new FinalState1("F"));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		parentFactory.addChild(AR_2factory);
		this.addFactory(AR_2factory);
		
		//SRN activate Register Norm
		SendState1 SRN = new SendState1("SRN");
		sendTemplate = new ACLMessage(ACLMessage.REQUEST);
		sendTemplate.setHeader("start", "registerNorm");
		sendTemplate.setContent("Register Norm");
		sendTemplate.setSender(getAid());
		sendTemplate.setReceiver(getAid());
		sendTemplate.setConversationId("C"+sendTemplate.hashCode()+System.currentTimeMillis());
		SRN.setMessageTemplate(sendTemplate);
		parentProcessor.registerState(SRN);
		parentProcessor.addTransition("RAR1_2", "SRN");
		
		//WRN
		parentProcessor.registerState(new WaitState("WRN",10000));
		parentProcessor.addTransition("SRN", "WRN");
		
		//RWRN
		GenericReceiveState RWRN = new GenericReceiveState("RWRN");
		receiveFilter = new ACLMessage(ACLMessage.INFORM);
		receiveFilter.setHeader("purpose", "waitMessage");
		RWRN.setAcceptFilter(receiveFilter);
		parentProcessor.registerState(RWRN);
		parentProcessor.addTransition("WRN", "RWRN");
		parentProcessor.addTransition("RWRN", "WRN");
		
		//RRN1
		GenericReceiveState RRN1 = new GenericReceiveState("RRN1");
		receiveFilter = new ACLMessage(ACLMessage.INFORM);
		receiveFilter.setHeader("inform", "done");
		RRN1.setAcceptFilter(receiveFilter);
		parentProcessor.registerState(RRN1);
		parentProcessor.addTransition("WRN", "RRN1");
		
		//RRN2
		GenericReceiveState RRN2 = new GenericReceiveState("RRN2");
		receiveFilter = new ACLMessage(ACLMessage.FAILURE);
		RRN2.setAcceptFilter(receiveFilter);
		parentProcessor.registerState(RRN2);
		parentProcessor.addTransition("WRN", "RRN2");
		
		//Create and attach get profile request conversation
		template = new ACLMessage(ACLMessage.REQUEST);
		template.setHeader("start", "registerNorm");
		
		//create message to send in the request protocol
		String NormID = "norma1";
		String NormContent = "FORBIDDEN_Member_REQUEST_acquireRole_MESSAGE(CONTENT(ROLE_'Payee'))";
		call = configuration + "RegisterNormProcess.owl NormID=" + NormID + " NormContent="+ NormContent;
		sendMessage = new ACLMessage(ACLMessage.REQUEST);
		sendMessage.setProtocol("fipa-request");
		sendMessage.setContent(call);
		sendMessage.setReceiver(new AgentID("OMS"));
		
		RequestInitiatorFactory RNfactory = new RequestInitiatorFactory("RNfactory", template, sendMessage);
		
		try {
			RNfactory.changeState(new FinalState1("F"));
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		parentFactory.addChild(RNfactory);
		this.addFactory(RNfactory);
				
		//exception states
		parentProcessor.registerState(new GenericCancelState());
		parentProcessor.registerState(new GenericNotAcceptedMessagesState());
		parentProcessor.registerState(new GenericSendingErrorsState());
		parentProcessor.registerState(new GenericTerminatedFatherState());
		
		//FINAL STATE
		parentProcessor.registerState(new GenericFinalState("F"));
		parentProcessor.addTransition("RAR2", "F");
		parentProcessor.addTransition("RAR2_2", "F");
		parentProcessor.addTransition("RRN1", "F");
		parentProcessor.addTransition("RRN2", "F");
				
		//attach factory to agent
		this.addStartingFactory(parentFactory, parentConversationId);
	}
	
	class SendState1 extends SendState{

		public SendState1(String n) {
			super(n);
		}

		@Override
		protected ACLMessage run(CProcessor myProcessor, ACLMessage lastReceivedMessage) {
			return this.messageTemplate;
		}

		@Override
		protected String getNext(CProcessor myProcessor,
				ACLMessage lastReceivedMessage) {
			String next = "";
			Set<String> transitions = new HashSet<String>();
			transitions = myProcessor.getTransitionTable().getTransitions(this.getName());
			Iterator<String> it = transitions.iterator();
			if (it.hasNext()) {
		        // Get element
		        next = it.next();
		    }
			return next;
		}
	}
	
	class FinalState1 extends FinalState{

		public FinalState1(String n) {
			super(n);
		}

		@Override
		protected ACLMessage run(CProcessor myProcessor) {
			ACLMessage finalMessage = new ACLMessage(ACLMessage.INFORM);
			finalMessage.setHeader("inform", "done");
			finalMessage.setSender(myProcessor.getMyAgent().getAid());
			finalMessage.setReceiver(myProcessor.getMyAgent().getAid());
			finalMessage.setConversationId(myProcessor.getParent().getConversationID());
			return finalMessage;
		}

	}
}