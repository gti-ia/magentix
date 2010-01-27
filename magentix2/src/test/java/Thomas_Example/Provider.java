package Thomas_Example;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import org.mindswap.owls.OWLSFactory;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.process.execution.ProcessExecutionEngine;
import org.mindswap.query.ValueMap;

import es.upv.dsic.gti_ia.cAgents.ActionState;
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
import es.upv.dsic.gti_ia.cAgents.RequestResponderFactory;
import es.upv.dsic.gti_ia.cAgents.SendState;
import es.upv.dsic.gti_ia.cAgents.WaitState;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.Configuration;
import es.upv.dsic.gti_ia.organization.Oracle;
import es.upv.dsic.gti_ia.organization.ProcessDescription;

public class Provider extends CAgent{

	private Oracle oracle;
	ProcessDescription processDescription = new ProcessDescription(
		    "http://localhost:8080/SearchCheapHotel/owl/owls/SearchCheapHotelProcess.owl",
		    "SearchCheapHotel");
	
	String configuration;
	Configuration c;
	String SFServiceDesciptionLocation;
	
	
	
	public Provider(AgentID aid) throws Exception {
		super(aid);
		c = Configuration.getConfiguration();
		configuration = c.getOMSServiceDesciptionLocation();
		SFServiceDesciptionLocation = c.getSFServiceDesciptionLocation();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void setFactories() {
		String parentConversationId = "C"+this.hashCode()+System.currentTimeMillis();
		ACLMessage template = new ACLMessage(ACLMessage.NOT_UNDERSTOOD); //the template has no use in this example
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
		
		//SSS activate Search Service
		SendState1 SSS = new SendState1("SSS");
		sendTemplate = new ACLMessage(ACLMessage.REQUEST);
		sendTemplate.setHeader("start", "searchService");
		sendTemplate.setContent("Search Service");
		sendTemplate.setSender(getAid());
		sendTemplate.setReceiver(getAid());
		sendTemplate.setConversationId("C"+sendTemplate.hashCode()+System.currentTimeMillis());
		SSS.setMessageTemplate(sendTemplate);
		parentProcessor.registerState(SSS);
		parentProcessor.addTransition("RAR1", "SSS");
		
		//WSS
		parentProcessor.registerState(new WaitState("WSS",10000));
		parentProcessor.addTransition("SSS", "WSS");
		
		//RWSS
		GenericReceiveState RWSS = new GenericReceiveState("RWSS");
		receiveFilter = new ACLMessage(ACLMessage.INFORM);
		receiveFilter.setHeader("purpose", "waitMessage");
		RWSS.setAcceptFilter(receiveFilter);
		parentProcessor.registerState(RWSS);
		parentProcessor.addTransition("WSS", "RWSS");
		parentProcessor.addTransition("RWSS", "WSS");
		
		//RSS1
		GenericReceiveState RSS1 = new GenericReceiveState("RSS1");
		receiveFilter = new ACLMessage(ACLMessage.INFORM);
		receiveFilter.setHeader("inform", "done");
		RSS1.setAcceptFilter(receiveFilter);
		parentProcessor.registerState(RSS1);
		parentProcessor.addTransition("WSS", "RSS1");
		
		//RSS2
		GenericReceiveState RSS2 = new GenericReceiveState("RSS2");
		receiveFilter = new ACLMessage(ACLMessage.FAILURE);
		RSS2.setAcceptFilter(receiveFilter);
		parentProcessor.registerState(RSS2);
		parentProcessor.addTransition("WSS", "RSS2");
		
		//Create and attach search service request conversation
		template = new ACLMessage(ACLMessage.REQUEST);
		template.setHeader("start", "searchService");
		
		String serviceGoal = "SearchCheapHotel";
		sendMessage = new ACLMessage(ACLMessage.REQUEST);
		sendMessage.setProtocol("fipa-request");
		call = SFServiceDesciptionLocation
			+ "SearchServiceProcess.owl SearchServiceInputServicePurpose="
			+ serviceGoal;
		sendMessage.setContent(call);
		sendMessage.setReceiver(new AgentID("SF"));

		RequestInitiatorFactory SSfactory = new RequestInitiatorFactory("SSfactory", template, sendMessage);		
		
		//change state R5 and F
		ReceiveState2 R5 = new ReceiveState2("R5");
		receiveFilter = new ACLMessage(ACLMessage.INFORM);
		receiveFilter.setHeader("inform", "done");
		R5.setAcceptFilter(receiveFilter);
		try {
			SSfactory.changeState(R5);
			SSfactory.changeState(new FinalState1("F"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		parentFactory.addChild(SSfactory);
		this.addFactory(SSfactory);
		
		//SGP activate Get Profile
		SendState1 SGP = new SendState1("SGP");
		sendTemplate = new ACLMessage(ACLMessage.REQUEST);
		sendTemplate.setHeader("start", "getProfile");
		sendTemplate.setContent("Get Profile");
		sendTemplate.setSender(getAid());
		sendTemplate.setReceiver(getAid());
		sendTemplate.setConversationId("C"+sendTemplate.hashCode()+System.currentTimeMillis());
		SGP.setMessageTemplate(sendTemplate);
		parentProcessor.registerState(SGP);
		parentProcessor.addTransition("RSS1", "SGP");
		
		//WGP
		parentProcessor.registerState(new WaitState("WGP",10000));
		parentProcessor.addTransition("SGP", "WGP");
		
		//RWGP
		GenericReceiveState RWGP = new GenericReceiveState("RWGP");
		receiveFilter = new ACLMessage(ACLMessage.INFORM);
		receiveFilter.setHeader("purpose", "waitMessage");
		RWGP.setAcceptFilter(receiveFilter);
		parentProcessor.registerState(RWGP);
		parentProcessor.addTransition("WGP", "RWGP");
		parentProcessor.addTransition("RWGP", "WGP");
		
		//RGP1
		GenericReceiveState RGP1 = new GenericReceiveState("RGP1");
		receiveFilter = new ACLMessage(ACLMessage.INFORM);
		receiveFilter.setHeader("inform", "done");
		RGP1.setAcceptFilter(receiveFilter);
		parentProcessor.registerState(RGP1);
		parentProcessor.addTransition("WGP", "RGP1");
		
		//RGP2
		GenericReceiveState RGP2 = new GenericReceiveState("RGP2");
		receiveFilter = new ACLMessage(ACLMessage.FAILURE);
		RGP2.setAcceptFilter(receiveFilter);
		parentProcessor.registerState(RGP2);
		parentProcessor.addTransition("WGP", "RGP2");
		
		//Create and attach get profile request conversation
		template = new ACLMessage(ACLMessage.REQUEST);
		template.setHeader("start", "getProfile");
		
		RequestInitiatorFactory GPfactory = new RequestInitiatorFactory("GPfactory", template, new ACLMessage(ACLMessage.REQUEST));
		
		receiveFilter = new ACLMessage(ACLMessage.INFORM);
		receiveFilter.setHeader("inform", "done");
		R5.setAcceptFilter(receiveFilter);
		try {
			GPfactory.changeState(R5);
			GPfactory.changeState(new FinalState1("F"));
			GPfactory.changeState(new GPSendState("S"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		parentFactory.addChild(GPfactory);
		this.addFactory(GPfactory);
		
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
		parentProcessor.addTransition("RGP1", "SAR_2");
		
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
		
		RequestInitiatorFactory ARfactory_2 = new RequestInitiatorFactory("ARfactory_2", template, new ACLMessage(ACLMessage.REQUEST));
		
		try {
			ARfactory_2.changeState(new FinalState1("F"));
			ARfactory_2.changeState(new AR_2SendState("S"));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		parentFactory.addChild(ARfactory_2);
		this.addFactory(ARfactory_2);
		
		//SRP activate Register Process
		SendState1 SRP = new SendState1("SRP");
		sendTemplate = new ACLMessage(ACLMessage.REQUEST);
		sendTemplate.setHeader("start", "registerProcess");
		sendTemplate.setContent("Register Process");
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
		template.setHeader("start", "registerProcess");
		
		RequestInitiatorFactory RPfactory = new RequestInitiatorFactory("RPfactory", template, new ACLMessage(ACLMessage.REQUEST));
		
		receiveFilter = new ACLMessage(ACLMessage.INFORM);
		receiveFilter.setHeader("inform", "done");
		R5.setAcceptFilter(receiveFilter);
		try {
			RPfactory.changeState(R5);
			RPfactory.changeState(new FinalState1("F"));
			RPfactory.changeState(new RPSendState("S"));
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		parentFactory.addChild(RPfactory);
		this.addFactory(RPfactory);
		
		//Third Acquire Role
		//SAR_3 activate Acquire Role
		SendState1 SAR_3 = new SendState1("SAR_3");
		sendTemplate = new ACLMessage(ACLMessage.REQUEST);
		sendTemplate.setHeader("start", "acquireRole_3");
		sendTemplate.setContent("Acquire Role");
		sendTemplate.setSender(getAid());
		sendTemplate.setReceiver(getAid());
		sendTemplate.setConversationId("C"+sendTemplate.hashCode()+System.currentTimeMillis());
		SAR_3.setMessageTemplate(sendTemplate);
		parentProcessor.registerState(SAR_3);
		parentProcessor.addTransition("RRP1", "SAR_3");
		
		//WAR_3
		parentProcessor.registerState(new WaitState("WAR_3",10000));
		parentProcessor.addTransition("SAR_3", "WAR_3");
		
		//RWRP
		GenericReceiveState RWAR_3 = new GenericReceiveState("RWAR_3");
		receiveFilter = new ACLMessage(ACLMessage.INFORM);
		receiveFilter.setHeader("purpose", "waitMessage");
		RWAR_3.setAcceptFilter(receiveFilter);
		parentProcessor.registerState(RWAR_3);
		parentProcessor.addTransition("WAR_3", "RWAR_3");
		parentProcessor.addTransition("RWAR_3", "WAR_3");
		
		//RAR1_3
		GenericReceiveState RAR1_3 = new GenericReceiveState("RAR1_3");
		receiveFilter = new ACLMessage(ACLMessage.INFORM);
		receiveFilter.setHeader("inform", "done");
		RAR1_3.setAcceptFilter(receiveFilter);
		parentProcessor.registerState(RAR1_3);
		parentProcessor.addTransition("WAR_3", "RAR1_3");
		
		//RAR2_3
		GenericReceiveState RAR2_3 = new GenericReceiveState("RAR2_3");
		receiveFilter = new ACLMessage(ACLMessage.FAILURE);
		RAR2_3.setAcceptFilter(receiveFilter);
		parentProcessor.registerState(RAR2_3);
		parentProcessor.addTransition("WAR_3", "RAR2_3");
		
		//Create and attach acquire role request conversation
		template = new ACLMessage(ACLMessage.REQUEST);
		template.setHeader("start", "acquireRole_3");
				
		sendMessage = new ACLMessage(ACLMessage.REQUEST);
		sendMessage.setProtocol("fipa-request");
		RoleID = "payee";
		UnitID = "travelagency";
		call = configuration + "AcquireRoleProcess.owl RoleID=" + RoleID + " UnitID="+ UnitID;
		sendMessage.setContent(call);
		sendMessage.setReceiver(new AgentID("OMS"));
		
		RequestInitiatorFactory ARfactory_3 = new RequestInitiatorFactory("AcquireRole_3", template, sendMessage);
		
		try {
			ARfactory_3.changeState(new FinalState1("F"));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		parentFactory.addChild(ARfactory_3);
		this.addFactory(ARfactory_3);
				
		//exception states
		parentProcessor.registerState(new GenericCancelState());
		parentProcessor.registerState(new GenericNotAcceptedMessagesState());
		parentProcessor.registerState(new GenericSendingErrorsState());
		parentProcessor.registerState(new GenericTerminatedFatherState());
		
		//FINAL STATE
		parentProcessor.registerState(new GenericFinalState("F"));
		parentProcessor.addTransition("RAR2", "F");
		parentProcessor.addTransition("RSS2", "F");
		parentProcessor.addTransition("RGP2", "F");
		parentProcessor.addTransition("RAR2_2", "F");
		parentProcessor.addTransition("RRP2", "F");
		parentProcessor.addTransition("RAR2_3", "F");
		parentProcessor.addTransition("RAR1_3", "F");
		
		//attach factory to agent
		this.addStartingFactory(parentFactory, parentConversationId);
		
		//responder factory
		ACLMessage responderTemplate = new ACLMessage(ACLMessage.REQUEST);
		responderTemplate.setProtocol("fipa-request");
		ResponderReceiveState responderState = new ResponderReceiveState("R");
		ACLMessage responderFilter = new ACLMessage(ACLMessage.REQUEST);
		responderState.setAcceptFilter(responderFilter);
		CProcessorFactory responderFactory = new RequestResponderFactory("responderFactory", responderTemplate, 1000, 
				responderState, new ResponderActionState("A"));
		this.addFactory(responderFactory);
	}
	
	class ReceiveState2 extends ReceiveState{

		public ReceiveState2(String n) {
			super(n);
		}

		@Override
		protected String run(CProcessor myProcessor, ACLMessage msg) {
			String next = "F";
			
			String[] agen;
			
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

			// si ejecutamos el registerProcess

			if (patron.equals("RegisterProcessProcess")) {
				if (arg2.equals("1")) {
					myProcessor.getParent().internalData.put("setImplementationID", arg1);
					myProcessor.getParent().internalData.put("salidaString", arg1);
				} else {
					myProcessor.getParent().internalData.put("salidaString", arg1);
				}

			}

			// si ejecutamos el GetProfile
			if (patron.equals("GetProfileProcess")) {
				arg2 = msg.getContent().substring(
						msg.getContent().indexOf(",") + 1,
						msg.getContent().length());
				arg2 = arg2.substring(arg2.indexOf("=") + 1, arg2.indexOf(","));

				if (arg2.equals("1"))// ha ido bien
				{
					myProcessor.getParent().internalData.put("URLProfile", arg1);
				} else {
					myProcessor.getParent().internalData.put("URLProfile", arg1);
				}

			}

			// si ejecutamos el DeregisterProfile
			if (patron.equals("DeregisterProfileProcess")) {

				if (arg2.equals("1"))// ha ido bien
				{
					// elimino del arrayList
					// this.sf.agent.getArraySFAgentDescription().remove(
					// this.sf.descripcion);
					myProcessor.getParent().internalData.put("salidaString", arg2);
				} else // ha ido mal
				{
					myProcessor.getParent().internalData.put("salidaString", arg2);
				}

			}

			// si ejecutamos el GetProcess
			if (patron.equals("GetProcessProcess")) {

				agen = null;
				if (arg2.equals("0")) {

					myProcessor.getParent().internalData.put("salidaString", arg2);
				} else {
					agen = arg1.split(",");
					Hashtable<AgentID, String> agentes = new Hashtable<AgentID, String>();
					for (String a : agen) {
						// sacamos el url process
						String arg_aux = a.substring(arg1.indexOf(" ") + 1, arg1
								.length());

						arg1 = a.substring(0, arg1.indexOf(" "));
						arg1 = arg1.substring(arg1.indexOf("-") + 1, arg1.length());

						// tenemos que controlar si existe 0, 1 o mas
						// proveedores.

						if (!arg1.equals("null"))// si existe algun provideer
						{

							// a�adimos tantos agentes proveedores como nos
							// devuelva
							// this.agentes.add(new AgentID(arg1));
							agentes.put(new AgentID(arg1), arg_aux);
						}
					}
					myProcessor.getParent().internalData.put("agents", agentes);
				}
			}

			// si ejecutamos el searchService
			if (patron.equals("SearchServiceProcess")) {

				agen = null;

				if (arg2.equals("1")) {

					// this.sf.addIDSearchService(arg2);
					// } else {
					agen = arg1.split(",");
					ArrayList<String> results = new ArrayList<String>();

					for (String a : agen) {
						a = a.substring(0, arg1.indexOf(" "));
						results.add(a);
					}
					myProcessor.getParent().internalData.put("results", results);
				} 
				else
					myProcessor.getParent().internalData.put("salidaString", arg1);
			}

			// solo si ejecutamos el registerProfile
			if (patron.equals("RegisterProfileProcess")) {
				if (arg1.equals("1")) {
					// para guardar nuestros ID para poder modificar
					// posteriormente nuestro servicio
					myProcessor.getParent().internalData.put("profileDescription", arg2);
					myProcessor.getParent().internalData.put("salidaString", arg2);
				} else {
					myProcessor.getParent().internalData.put("salidaString", arg2);
				}

			}

			// Si ejecutamos el ModifyProfile

			if (patron.equals("ModifyProfileProcess")) {
				if (arg2.equals("1"))// ha hido todo bien
				{
					myProcessor.getParent().internalData.put("salidaString", arg2);
				} else if (arg2.equals("0"))// existen profile ligados a este
				// process, por tanto no puede
				// modificar-lo
				{
					myProcessor.getParent().internalData.put("salidaString", arg2);
				} else// el id del servicio no es valido
				{
					myProcessor.getParent().internalData.put("salidaString", arg1);
				}

			}

			// Si ejecutamos el ModifyProcess
			if (patron.equals("ModifyProcessProcess")) {
				myProcessor.getParent().internalData.put("salidaString", arg2);
			}

			if (patron.equals("RemoveProvider")) {
				myProcessor.getParent().internalData.put("salidaString", arg2);
			}

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
	
	class GPSendState extends SendState{

		public GPSendState(String n) {
			super(n);
		}

		@Override
		protected ACLMessage run(CProcessor myProcessor, ACLMessage lastReceivedMessage) {
			
			ArrayList<String> results = (ArrayList<String>) myProcessor.getParent().internalData.get("results");
			String serviceID = results.get(0);
			
			Configuration c;
			c = Configuration.getConfiguration();
			ACLMessage sendMessage = new ACLMessage(ACLMessage.REQUEST);
			sendMessage.setProtocol("fipa-request");
			String SFServiceDesciptionLocation = c.getSFServiceDesciptionLocation();
			String call = SFServiceDesciptionLocation
			+ "GetProfileProcess.owl GetProfileInputServiceID=" + serviceID;
			sendMessage.setContent(call);
			sendMessage.setSender(myProcessor.getMyAgent().getAid());
			sendMessage.setConversationId(myProcessor.getConversationID());
			sendMessage.setReceiver(new AgentID("SF"));
			
			return sendMessage;
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
			System.out.println("Next GP: "+next);
			return next;
		}
	}
	
	class AR_2SendState extends SendState{

		public AR_2SendState(String n) {
			super(n);
		}

		@Override
		protected ACLMessage run(CProcessor myProcessor, ACLMessage lastReceivedMessage) {
			
			Oracle oracle = null
			;
			URL profile;
		    try {
		    	profile = new URL((String) myProcessor.getParent().internalData.get("URLProfile"));
		    	oracle = new Oracle(profile);
	
		    } catch (MalformedURLException e) {
		    	logger.error("ERROR: Profile URL Malformed!");
		    	e.printStackTrace();
		    }
		    
		    Configuration c;
		    c = Configuration.getConfiguration();
			String configuration = c.getOMSServiceDesciptionLocation();
			String UnitID = "travelagency";
			//String RoleID = oracle.getClientList().get(0);
			String RoleID = "provider";
		    
		    String call = configuration + "AcquireRoleProcess.owl RoleID=" + RoleID + " UnitID="
			+ UnitID;
		    
			//System.out.println("Llamada acquireRole2: "+call);
			ACLMessage sendMessage = new ACLMessage(ACLMessage.REQUEST);
			sendMessage.setProtocol("fipa-request");
			sendMessage.setContent(call);
			sendMessage.setReceiver(new AgentID("OMS"));
			sendMessage.setSender(myProcessor.getMyAgent().getAid());
			sendMessage.setConversationId(myProcessor.getConversationID());
					
			return sendMessage;
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
	
	class RPSendState extends SendState{

		public RPSendState(String n) {
			super(n);
		}

		@Override
		protected ACLMessage run(CProcessor myProcessor, ACLMessage lastReceivedMessage) {
			
			ArrayList<String> results = (ArrayList<String>) myProcessor.getParent().internalData.get("results");
			
			processDescription.setProfileID(results.get(0));	
			
			String call = SFServiceDesciptionLocation
			+ "RegisterProcessProcess.owl"
			+ " RegisterProcessInputServiceID="
			+ processDescription.getProfileID()
			+ " RegisterProcessInputServiceModel="
			+ processDescription.getServiceModel();
			
			ACLMessage sendMessage = new ACLMessage(ACLMessage.REQUEST);
			sendMessage.setProtocol("fipa-request");			
			sendMessage.setContent(call);
			sendMessage.setReceiver(new AgentID("SF"));
			
			sendMessage.setSender(myProcessor.getMyAgent().getAid());
			sendMessage.setConversationId(myProcessor.getConversationID());
					
			return sendMessage;
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
	
	class ResponderReceiveState extends ReceiveState{

		public ResponderReceiveState(String n) {
			super(n);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected String run(CProcessor myProcessor, ACLMessage msg) {
			String next= "";
			if (msg != null) {
		    	try {
		    		Process aProcess = processDescription.getProcess(msg);
		    		System.out.println("AGREE");
		    		myProcessor.internalData.put("inmsg", msg);
		    		next = "S3";
		    	} catch (Exception e) {
		    		System.out.println("EXCEPTION");
		    		System.out.println(e);
		    		next = "S2";
		    	}

		    } else {
		    	System.out.println("NOTUNDERSTOOD");
		    	next = "S1";
		    }
		    return next;
		}	
	}
	
	class ResponderActionState extends ActionState{

		public ResponderActionState(String n) {
			super(n);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected String run(CProcessor myProcessor) {

			String next = "";
		    ACLMessage inmsg = (ACLMessage) myProcessor.internalData.get("inmsg");
		    ACLMessage msg = inmsg.createReply();		
		    
		    // create an execution engine
		    ProcessExecutionEngine exec = OWLSFactory.createExecutionEngine();
		    
		    try {
				Process aProcess = processDescription.getProcess(inmsg);
	
				
				// initialize the input values to be empty
				ValueMap values = new ValueMap();
				
				values = processDescription.getServiceRequestValues(inmsg);
				
				
				System.out.println("[Provider]Executing... " + values.getValues().toString());
				values = exec.execute(aProcess, values);
	
				System.out.println("[Provider]Values obtained... :" + values.toString());
	
				System.out.println("[Provider]Creating inform message to send...");
	
				msg.setPerformative(ACLMessage.INFORM);
	
				
				msg.setContent(aProcess.getLocalName() + "=" + values.toString());
				System.out.println("[Provider]Before set message content..."+msg.getContent());
				myProcessor.internalData.put("outmsg", msg);
				next = "S5";

		    } catch (Exception e) {

				System.out.println("EXCEPTION");
				System.out.println(e);
				e.printStackTrace();
				next = "S4";
		    }
		    return next;
		} // end prepareResultNotification
	}
}

