package es.upv.dsic.gti_ia.magentix2;

import es.upv.dsic.gti_ia.fipa.ACLMessage;
import es.upv.dsic.gti_ia.fipa.AgentID;
import es.upv.dsic.gti_ia.proto.Monitor;
import es.upv.dsic.gti_ia.proto.FIPARequestInitiator;
import es.upv.dsic.gti_ia.proto.FIPANames.InteractionProtocol;
import java.util.ArrayList;

public class OMSService {

	private String configuration;
	private String conection;
	private Monitor adv = new Monitor();
	private String value = "";
	private int Quantity;
	private ArrayList<String> list = new ArrayList<String>();

	public  OMSService(String OMSServiceDesciptionLocation) {

		this.configuration = OMSServiceDesciptionLocation;
	}

	/**
	 * Asigna el tipo de resultado de la salida, true o false
	 * 
	 * @param valor
	 */
	public void setValor(String value) {
		this.value = value;
	}

	
	public void addElementToList(String element)
	{
		this.list.add(element);
	}
	
	public ArrayList<String> getList()
	{
		return this.list;
		
	}
	public void setQuantity(int Quantity)
	{
		this.Quantity = Quantity;
	}
	
	public int getQuantity()
	{
		return this.Quantity;
	}
	
	public String LeaveRole(QueueAgent agente, String AgentID, String RoleID,
			String UnitID) {
		// suggestedServiceCalls[2]=configuration+"RegisterNormProcess.owl
		// NormID=norma1
		// normContent=FORBIDDEN_Member_REQUEST_acquireRole_MESSAGE(CONTENT(ROLE_'Payee'))";

		String call = configuration + "LeaveRoleProcess.owl AgentID=" + AgentID
				+ " RoleID=" + RoleID + " UnitID=" + UnitID;
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agente.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS", "qpid", "localhost", ""));

		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");
		// send(requestMsg);

		// crear un fil en el fipa request initiator
		agente.setTask(new TestAgentClient(agente, requestMsg, this));

		this.adv.waiting();

		return this.value;

	}

	public ArrayList<String> InformAgentRole(QueueAgent agente, String AgentID) {
		// suggestedServiceCalls[2]=configuration+"RegisterNormProcess.owl
		// NormID=norma1
		// normContent=FORBIDDEN_Member_REQUEST_acquireRole_MESSAGE(CONTENT(ROLE_'Payee'))";

		this.list.clear();
		
		String call = configuration + "InformAgentRoleProcess.owl RequestedAgentID="
				+ AgentID;
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agente.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS", "qpid", "localhost", ""));

		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");
		// send(requestMsg);

		// crear un fil en el fipa request initiator
		agente.setTask(new TestAgentClient(agente, requestMsg, this));

		this.adv.waiting();

		return this.list;

	}

	public ArrayList<String> InformMembers(QueueAgent agente, String RoleID,
			String UnitID) {
		// suggestedServiceCalls[2]=configuration+"RegisterNormProcess.owl
		// NormID=norma1
		// normContent=FORBIDDEN_Member_REQUEST_acquireRole_MESSAGE(CONTENT(ROLE_'Payee'))";

		this.list.clear();
		
		String call = configuration + "InformMembersProcess.owl RoleID="
				+ RoleID + " UnitID=" + UnitID;
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agente.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS", "qpid", "localhost", ""));

		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");
		// send(requestMsg);

		// crear un fil en el fipa request initiator
		agente.setTask(new TestAgentClient(agente, requestMsg, this));

		this.adv.waiting();
		
		System.out.println("Lista vale: "+ this.list.size());

		return this.list;

	}

	public ArrayList<String> InformRoleNorms(QueueAgent agente, String RoleID) {
		// suggestedServiceCalls[2]=configuration+"RegisterNormProcess.owl
		// NormID=norma1
		// normContent=FORBIDDEN_Member_REQUEST_acquireRole_MESSAGE(CONTENT(ROLE_'Payee'))";

		this.list.clear();
		
		String call = configuration + "InformRoleNormsProcess.owl RoleID="
				+ RoleID;
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agente.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS", "qpid", "localhost", ""));

		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");
		// send(requestMsg);

		// crear un fil en el fipa request initiator
		agente.setTask(new TestAgentClient(agente, requestMsg, this));

		this.adv.waiting();

		return this.list;

	}

	public ArrayList<String> InformRoleProfiles(QueueAgent agente, String UnitID) {
		// suggestedServiceCalls[2]=configuration+"RegisterNormProcess.owl
		// NormID=norma1
		// normContent=FORBIDDEN_Member_REQUEST_acquireRole_MESSAGE(CONTENT(ROLE_'Payee'))";

		this.list.clear();
		
		String call = configuration + "InformRoleProfilesProcess.owl UnitID="
				+ UnitID;
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agente.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS", "qpid", "localhost", ""));

		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");
		// send(requestMsg);

		// crear un fil en el fipa request initiator
		agente.setTask(new TestAgentClient(agente, requestMsg, this));

		this.adv.waiting();

		return this.list;

	}

	public ArrayList<String> InformUnit(QueueAgent agente, String UnitID) {
		// suggestedServiceCalls[2]=configuration+"RegisterNormProcess.owl
		// NormID=norma1
		// normContent=FORBIDDEN_Member_REQUEST_acquireRole_MESSAGE(CONTENT(ROLE_'Payee'))";

		this.list.clear();
		
		String call = configuration + "InformUnitProcess.owl UnitID=" + UnitID;
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agente.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS", "qpid", "localhost", ""));

		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");
		// send(requestMsg);

		// crear un fil en el fipa request initiator
		agente.setTask(new TestAgentClient(agente, requestMsg, this));

		this.adv.waiting();

		return this.list;

	}

	public ArrayList<String> InformUnitRoles(QueueAgent agente, String UnitID) {
		// suggestedServiceCalls[2]=configuration+"RegisterNormProcess.owl
		// NormID=norma1
		// normContent=FORBIDDEN_Member_REQUEST_acquireRole_MESSAGE(CONTENT(ROLE_'Payee'))";

		this.list.clear();
		
		String call = configuration + "InformUnitRolesProcess.owl UnitID="
				+ UnitID;
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agente.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS", "qpid", "localhost", ""));

		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");
		// send(requestMsg);

		// crear un fil en el fipa request initiator
		agente.setTask(new TestAgentClient(agente, requestMsg, this));

		this.adv.waiting();

		return this.list;

	}

	public int QuantityMembers(QueueAgent agente, String RoleID, String UnitID) {
		// suggestedServiceCalls[2]=configuration+"RegisterNormProcess.owl
		// NormID=norma1
		// normContent=FORBIDDEN_Member_REQUEST_acquireRole_MESSAGE(CONTENT(ROLE_'Payee'))";

		String call = configuration + "QuantityMembersProcess.owl RoleID="
				+ RoleID + " UnitID=" + UnitID;
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agente.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS", "qpid", "localhost", ""));

		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");
		// send(requestMsg);

		// crear un fil en el fipa request initiator
		agente.setTask(new TestAgentClient(agente, requestMsg, this));

		this.adv.waiting();

		return this.Quantity;

	}

	public String RegisterNorm(QueueAgent agente, String NormID,
			String NormContent) {
		// suggestedServiceCalls[2]=configuration+"RegisterNormProcess.owl
		// NormID=norma1
		// normContent=FORBIDDEN_Member_REQUEST_acquireRole_MESSAGE(CONTENT(ROLE_'Payee'))";

		String call = configuration + "RegisterNormProcess.owl NormID="
				+ NormID + " NormContent=" + NormContent;
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agente.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS", "qpid", "localhost", ""));

		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");
		// send(requestMsg);

		// crear un fil en el fipa request initiator
		agente.setTask(new TestAgentClient(agente, requestMsg, this));

		this.adv.waiting();

		return this.value;

	}

	public String RegisterRole(QueueAgent agente,
			String RegisterRoleInputRoleID, String UnitID,
			String Accessibility, String Position, String Visibility,
			String Inheritance) {
		// suggestedServiceCalls[2]=configuration+"RegisterNormProcess.owl
		// NormID=norma1
		// normContent=FORBIDDEN_Member_REQUEST_acquireRole_MESSAGE(CONTENT(ROLE_'Payee'))";

		String call = configuration
				+ "RegisterRoleProcess.owl RegisterRoleInputRoleID="
				+ RegisterRoleInputRoleID + " UnitID=" + UnitID
				+ " Accessibility=" + Accessibility + " Position=" + Position
				+ " Visibility=" + Visibility + " Inheritance=" + Inheritance;
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agente.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS", "qpid", "localhost", ""));

		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");
		// send(requestMsg);

		// crear un fil en el fipa request initiator
		agente.setTask(new TestAgentClient(agente, requestMsg, this));

		this.adv.waiting();

		return this.value;

	}

	public String RegisterUnit(QueueAgent agente, String UnitID, String Type,
			String Goal, String ParentUnitID) {
		// suggestedServiceCalls[2]=configuration+"RegisterNormProcess.owl
		// NormID=norma1
		// normContent=FORBIDDEN_Member_REQUEST_acquireRole_MESSAGE(CONTENT(ROLE_'Payee'))";

		String call = configuration + "RegisterUnitProcess.owl  UnitID="
				+ UnitID + " Type=" + Type + " Goal=" + Goal + " ParentUnitID="
				+ ParentUnitID;
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agente.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS", "qpid", "localhost", ""));

		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");
		// send(requestMsg);

		// crear un fil en el fipa request initiator
		agente.setTask(new TestAgentClient(agente, requestMsg, this));

		this.adv.waiting();

		return this.value;

	}

	public String DeregisterNorm(QueueAgent agente, String NormID) {
		// suggestedServiceCalls[2]=configuration+"RegisterNormProcess.owl
		// NormID=norma1
		// normContent=FORBIDDEN_Member_REQUEST_acquireRole_MESSAGE(CONTENT(ROLE_'Payee'))";

		String call = configuration + "DeregisterNormProcess.owl  NormID="
				+ NormID;
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agente.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS", "qpid", "localhost", ""));

		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");
		// send(requestMsg);

		// crear un fil en el fipa request initiator
		agente.setTask(new TestAgentClient(agente, requestMsg, this));

		this.adv.waiting();

		return this.value;

	}

	public String DeregisterRole(QueueAgent agente, String RoleID, String UnitID) {
		// suggestedServiceCalls[2]=configuration+"RegisterNormProcess.owl
		// NormID=norma1
		// normContent=FORBIDDEN_Member_REQUEST_acquireRole_MESSAGE(CONTENT(ROLE_'Payee'))";

		String call = configuration + "DeregisterRoleProcess.owl  RoleID="
				+ RoleID + " UnitID=" + UnitID;
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agente.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS", "qpid", "localhost", ""));

		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");
		// send(requestMsg);

		// crear un fil en el fipa request initiator
		agente.setTask(new TestAgentClient(agente, requestMsg, this));

		this.adv.waiting();

		return this.value;

	}

	public String DeregisterUnit(QueueAgent agente, String UnitID) {
		// suggestedServiceCalls[2]=configuration+"RegisterNormProcess.owl
		// NormID=norma1
		// normContent=FORBIDDEN_Member_REQUEST_acquireRole_MESSAGE(CONTENT(ROLE_'Payee'))";

		String call = configuration + "DeregisterNormProcess.owl  UnitID="
				+ UnitID;
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agente.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS", "qpid", "localhost", ""));

		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");
		// send(requestMsg);

		// crear un fil en el fipa request initiator
		agente.setTask(new TestAgentClient(agente, requestMsg, this));

		this.adv.waiting();

		return this.value;

	}

	public String Expulse(QueueAgent agente, String AgentID, String RoleID,
			String UnitID) {
		// suggestedServiceCalls[2]=configuration+"RegisterNormProcess.owl
		// NormID=norma1
		// normContent=FORBIDDEN_Member_REQUEST_acquireRole_MESSAGE(CONTENT(ROLE_'Payee'))";

		String call = configuration + "ExpulseProcess.owl AgentID=" + AgentID
				+ " RoleID=" + RoleID + " UnitID=" + UnitID;
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agente.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS", "qpid", "localhost", ""));

		System.out.println("Destinatario del mensaje: "
				+ requestMsg.getReceiver().toString());

		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");
		// send(requestMsg);

		// crear un fil en el fipa request initiator
		agente.setTask(new TestAgentClient(agente, requestMsg, this));

		this.adv.waiting();

		return this.value;

	}

	/**
	 * Registra un agente en la organizacion
	 * 
	 * @param agente
	 *            agente a registrar
	 * @param descripcion
	 *            inidica que rol y en que organizacion entrara el agente
	 * @return
	 */
	public String AcquireRole(QueueAgent agente, String RoleID, String UnitID) {
		// montar string de conexion
		// Enviamos el mensaje

		String call = configuration + "AcquireRoleProcess.owl RoleID=" + RoleID
				+ " UnitID=" + UnitID;
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agente.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS", "qpid", "localhost", ""));

		System.out.println("Destinatario del mensaje: "
				+ requestMsg.getReceiver().toString());

		System.out.println("[QueryAgent]Sms to send: " + requestMsg.toString());
		System.out.println("[QueryAgent]Sending... ");
		// send(requestMsg);

		// crear un fil en el fipa request initiator
		agente.setTask(new TestAgentClient(agente, requestMsg, this));

		this.adv.waiting();

		return this.value;
		// registar el agente en la plataforma

	}

	/**
	 * TestAgentClient handles the messages received from the SF
	 */
	static class TestAgentClient extends FIPARequestInitiator {
		QueueAgent agent;
		OMSService oms;
		String[] elements;

		protected TestAgentClient(QueueAgent agent, ACLMessage msg,
				OMSService oms) {
			super(agent, msg);
			this.agent = agent;
			this.oms = oms;

		}

		protected void handleAgree(ACLMessage msg) {
			System.out.println(myAgent.getName() + ": OOH! "
					+ msg.getSender().getLocalName()
					+ " Has agreed to excute the service!");
		}

		protected void handleRefuse(ACLMessage msg) {
			System.out.println(myAgent.getName() + ": Oh no! "
					+ msg.getSender().getLocalName()
					+ " has rejected my proposal.");
			this.oms.setValor(myAgent.getName() + ": Oh no! "
					+ msg.getSender().getLocalName()
					+ " has rejected my proposal.");
			this.oms.adv.advise();
		}

		protected void handleInform(ACLMessage msg) {
			System.out.println(myAgent.getName() + ":"
					+ msg.getSender().getLocalName()
					+ " has informed me of the status of my request."
					+ " They said : " + msg.getContent());

			String patron = msg.getContent().substring(0,
					msg.getContent().indexOf("="));

			if (patron.equals("InformUnitProcess")) {
				String arg;
				String argAux;
				// Desglosar el UnitType, UnitGoal, y ParentID
				// ParentID

				arg = msg.getContent().substring(
						msg.getContent().indexOf("ParentID") + 9,
						msg.getContent().indexOf(","));
				this.oms.addElementToList(arg);
				argAux = msg.getContent().substring(
						msg.getContent().indexOf("UnitGoal"),
						msg.getContent().length());
				arg = argAux.substring(argAux.indexOf("UnitGoal") + 9, argAux
						.indexOf(","));
				this.oms.addElementToList(arg);
				argAux = argAux.substring(argAux.indexOf("UnitType"), argAux
						.length());
				arg = argAux.substring(argAux.indexOf("UnitType") + 9, argAux
						.indexOf("}"));
				this.oms.addElementToList(arg);

			}

			//sacamos el Status
			String status = "";

			
			int n =  msg.getContent().indexOf(",") - msg.getContent().indexOf("Status");
			
			
			
			
			if (n>0)
			{
			status = msg.getContent().substring(
					msg.getContent().indexOf("Status") + 7,
					msg.getContent().indexOf(","));
			}
			else
			{
				status = msg.getContent().substring(
						msg.getContent().indexOf("Status") + 7,
						msg.getContent().indexOf("}"));	
				
			}

			
			

			if (patron.equals("InformAgentRoleProcess")
					|| patron.equals("InformMembersProcess")
					|| patron.equals("InformRoleNormsProcess")
					|| patron.equals("InformRoleProfilesProcess")
					|| patron.equals("InformUnitRolesProcess")) {

				// recorrer el vector
				String argAux;

				
				//para los servicios informativos
			

					// diferenciamos entre los que tienen tuplas y los que no
					// son tuplas

					if (patron.equals("InformAgentRoleProcess")
							|| patron.equals("InformMembersProcess")) {
						
						if (!status.equals("Ok")) {
							this.oms.addElementToList("EMPTY");
						}
						else
						{
						String arg3 = msg.getContent().substring(
								msg.getContent().indexOf(",")+1,
								msg.getContent().length());
						arg3 = arg3.substring(arg3.indexOf("("), arg3
								.indexOf("]"));

						elements = arg3.split(",");

						int paridad = 0;

						for (String e : elements) {
							if ((paridad % 2) == 0)// es par
							{
								argAux = e.substring(e.indexOf("(")+1, e.length());

							} else {
								argAux = e.substring(0, e.indexOf(")"));
							}
							this.oms.addElementToList(argAux);
							paridad++;
						}
						}
					}
					else
					{
						if (!status.equals("Ok")) {

							this.oms.addElementToList("EMPTY");
						}
						else
						{
						String arg3 = msg.getContent().substring(
								msg.getContent().indexOf("[") + 1,
								msg.getContent().indexOf("]"));
						

						elements = arg3.split(",");

					

						for (String e : elements) {
							this.oms.addElementToList(e);
					
						}
						}
						
					}
				}

			

			String ErrorValue = msg.getContent();
			
			 n =  msg.getContent().indexOf(",") - msg.getContent().indexOf("ErrorValue");
			
			
			
			
			if (n>0)
			{
				ErrorValue = msg.getContent().substring(
					msg.getContent().indexOf("ErrorValue") + 11,
					msg.getContent().indexOf(","));
			}
			else
			{
				ErrorValue = msg.getContent().substring(
						msg.getContent().indexOf("ErrorValue") + 11,
						msg.getContent().indexOf("}"));	
				
			}

			


			// si ha salido bien despierto al agente
			if (status.equals("Ok")) {

				if (patron.equals("QuantityMembersProcess")) {
					
					String quantity  = msg.getContent().substring(msg.getContent().indexOf("Quantity=") + 9,
							msg.getContent().indexOf("}"));	
					this.oms.setQuantity(Integer.parseInt(quantity));
				}

				this.oms.setValor(status);
			} else {
				// vemos que tipo de error
				this.oms.setValor(status + " " + ErrorValue);
			}


			this.oms.adv.advise();

		}

		protected void handleNotUnderstood(ACLMessage msg) {
			System.out.println(myAgent.getName() + ":"
					+ msg.getSender().getLocalName()
					+ " has indicated that they didn't understand.");
			this.oms.setValor(myAgent.getName() + ":"
					+ msg.getSender().getLocalName()
					+ " has indicated that they didn't understand.");
			this.oms.adv.advise();
		}

		protected void handleOutOfSequence(ACLMessage msg) {
			System.out.println(myAgent.getName() + ":"
					+ msg.getSender().getLocalName()
					+ " has send me a message which i wasn't"
					+ " expecting in this conversation");
			this.oms.setValor(myAgent.getName() + ":"
					+ msg.getSender().getLocalName()
					+ " has send me a message which i wasn't"
					+ " expecting in this conversation");
			this.oms.adv.advise();
		}
	}

}
