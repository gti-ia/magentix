package Thomas_Example;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
import es.upv.dsic.gti_ia.cAgents.ReceiveState;
import es.upv.dsic.gti_ia.cAgents.RequestInitiatorFactory;
import es.upv.dsic.gti_ia.cAgents.SendState;
import es.upv.dsic.gti_ia.cAgents.WaitState;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.Configuration;
import es.upv.dsic.gti_ia.organization.ProfileDescription;

public class Announcement extends CAgent{

	public Announcement(AgentID aid) throws Exception {
		super(aid);
		// TODO Auto-generated constructor stub
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
		
		ACLMessage sendMessage = new ACLMessage(ACLMessage.REQUEST);
		sendMessage.setProtocol("fipa-request");
		String RoleID = "member";
		String UnitID = "virtual";
		Configuration c;
		c = Configuration.getConfiguration();
		String SFServiceDesciptionLocation = c.getSFServiceDesciptionLocation();
		String configuration = c.getOMSServiceDesciptionLocation();
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
		template.setHeader("start", "acquireRole");
		
		sendMessage = new ACLMessage(ACLMessage.REQUEST);
		sendMessage.setProtocol("fipa-request");
		RoleID = "provider";
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
		
		//SRP activate Register Profile
		SendState1 SRP = new SendState1("SRP");
		sendTemplate = new ACLMessage(ACLMessage.REQUEST);
		sendTemplate.setHeader("start", "registerProfile");
		sendTemplate.setContent("Register Profile");
		sendTemplate.setSender(getAid());
		sendTemplate.setReceiver(getAid());
		sendTemplate.setConversationId("C"+sendTemplate.hashCode()+System.currentTimeMillis());
		SRP.setMessageTemplate(sendTemplate);
		parentProcessor.registerState(SRP);
		parentProcessor.addTransition("RAR1_2", "SRP");
		
		//WRP
		parentProcessor.registerState(new WaitState("WRP",10000));
		parentProcessor.addTransition("SRP", "WRP");
		
		//RWRP
		GenericReceiveState RWRP = new GenericReceiveState("RWRP");
		receiveFilter = new ACLMessage(ACLMessage.INFORM);
		receiveFilter.setHeader("purpose", "waitMessage");
		RWRP.setAcceptFilter(receiveFilter);
		parentProcessor.registerState(RWRP);
		parentProcessor.addTransition("WRP", "RWRP");
		parentProcessor.addTransition("RWRP", "WRP");
		
		//RRP1
		GenericReceiveState RRP1 = new GenericReceiveState("RRP1");
		receiveFilter = new ACLMessage(ACLMessage.INFORM);
		receiveFilter.setHeader("inform", "done");
		RRP1.setAcceptFilter(receiveFilter);
		parentProcessor.registerState(RRP1);
		parentProcessor.addTransition("WRP", "RRP1");
		
		//RRP2
		GenericReceiveState RRP2 = new GenericReceiveState("RRP2");
		receiveFilter = new ACLMessage(ACLMessage.FAILURE);
		RRP2.setAcceptFilter(receiveFilter);
		parentProcessor.registerState(RRP2);
		parentProcessor.addTransition("WRP", "RRP2");
		
		//Create and attach get profile request conversation
		template = new ACLMessage(ACLMessage.REQUEST);
		template.setHeader("start", "registerPRofile");
		
		ProfileDescription profile = new ProfileDescription(
			    "http://localhost:8080/SearchCheapHotel/owl/owls/SearchCheapHotelProfile.owl",
			    "SearchCheapHotel");
		
		call = SFServiceDesciptionLocation
		+ "RegisterProfileProcess.owl "
		+ "RegisterProfileInputServiceGoal= "
		+ " RegisterProfileInputServiceProfile="
		+ profile.getServiceProfile();
		
		sendMessage = new ACLMessage(ACLMessage.REQUEST);
		sendMessage.setProtocol("fipa-request");
		sendMessage.setContent(call);
		sendMessage.setReceiver(new AgentID("SF"));
		
		RequestInitiatorFactory RPfactory = new RequestInitiatorFactory("RPfactory", template, sendMessage);
		
		receiveFilter = new ACLMessage(ACLMessage.INFORM);
		receiveFilter.setHeader("inform", "done");
		RPReceiveState R5 = new RPReceiveState("R5");
		R5.setAcceptFilter(receiveFilter);
		try {
			RPfactory.changeState(R5);
			RPfactory.changeState(new FinalState1("F"));
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		parentFactory.addChild(RPfactory);
		this.addFactory(RPfactory);
				
		//exception states
		parentProcessor.registerState(new GenericCancelState());
		parentProcessor.registerState(new GenericNotAcceptedMessagesState());
		parentProcessor.registerState(new GenericSendingErrorsState());
		parentProcessor.registerState(new GenericTerminatedFatherState());
		
		//FINAL STATE
		parentProcessor.registerState(new GenericFinalState("F"));
		parentProcessor.addTransition("RAR2", "F");
		parentProcessor.addTransition("RAR2_2", "F");
		parentProcessor.addTransition("RRP1", "F");
		parentProcessor.addTransition("RRP2", "F");
				
		//attach factory to agent
		this.addStartingFactory(parentFactory, parentConversationId);
	}
	
	class RPReceiveState extends ReceiveState{

		public RPReceiveState(String n) {
			super(n);
		}

		@Override
		protected String run(CProcessor myProcessor, ACLMessage msg) {
			String next = "F";
			
			// Sacamos el patron
			String patron = msg.getContent().substring(0,
					msg.getContent().indexOf("="));
			String arg1 = "Check the error log file";

			// primer argumento si es un DeregisterProfileProcess no sacaremos
			// el arg1
			if (!patron.equals("DeregisterProfileProcess")
					&& !patron.equals("ModifyProfileProcess")
					&& !patron.equals("ModifyProcessProcess")
					&& !patron.equals("RemoveProviderProcess")) {
				arg1 = msg.getContent().substring(
						msg.getContent().indexOf("=") + 1,
						msg.getContent().length());
				arg1 = arg1.substring(arg1.indexOf("=") + 1, arg1.indexOf(","));
			}

			// segundo argumento
			String arg2 = msg.getContent();
			arg2 = arg2.substring((arg2.lastIndexOf("=")) + 1, arg2.length() - 1);

			
			if (arg1.equals("1")) {
				// para guardar nuestros ID para poder modificar
				// posteriormente nuestro servicio
				System.out.println("funciono RegisterProfile");
				myProcessor.getParent().internalData.put("profileDescription", arg2);
				myProcessor.getParent().internalData.put("salidaString", arg2);
			} else {
				myProcessor.getParent().internalData.put("salidaString", arg2);
			}
			return next;
		}
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
