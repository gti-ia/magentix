package es.upv.dsic.gti_ia.magentix2;

import es.upv.dsic.gti_ia.fipa.ACLMessage;
import es.upv.dsic.gti_ia.fipa.AgentID;
import es.upv.dsic.gti_ia.proto.FIPARequestInitiator;
import es.upv.dsic.gti_ia.proto.FIPANames.InteractionProtocol;
import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * This class gives us the support to accede to the services of the OMS 
 * @author jbellver
 *
 */
public class OMSService {

	private String configuration;
	private String value = "";
	private int Quantity;
	private ArrayList<String> list = new ArrayList<String>();
	static Logger logger = Logger.getLogger(OMSService.class);

	
	/**
	 * This class gives us the support to accede to the services of the OMS
	 * @param OMSServiceDesciptionLocation  The URL where the owl's document  is located.
	 */
	public  OMSService(String OMSServiceDesciptionLocation) {

		this.configuration = OMSServiceDesciptionLocation;
	}

	/**
	 * 
	 */
	public  OMSService() {

		this.configuration = "http://localhost:8080/omsservices/OMSservices/owl/owls/";
	}


	private void setValor(String value) {
		this.value = value;
	}

	
	private void addElementToList(String element)
	{
		this.list.add(element);
	}
	
	private ArrayList<String> getList()
	{
		return this.list;
		
	}
	
	private void setQuantity(int Quantity)
	{
		this.Quantity = Quantity;
	}
	
	private int getQuantity()
	{
		return this.Quantity;
	}
	

	/**
	 * Leave role agent inside the organization
	 * @param agent
	 * @param AgentID
	 * @param RoleID
	 * @param UnitID
	 * @return String Status ErrorValue
	 */
	public String LeaveRole(QueueAgent agent, String AgentID, String RoleID,
			String UnitID) {

		String call = configuration + "LeaveRoleProcess.owl AgentID=" + AgentID
				+ " RoleID=" + RoleID + " UnitID=" + UnitID;
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agent.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS", "qpid", "localhost", ""));

		logger.debug("[QueryAgent]Sms to send: " + requestMsg.toString());
		logger.debug("[QueryAgent]Sending... ");


		TestAgentClient test = new TestAgentClient(agent, requestMsg, this);

		do {
			test.action();
		} while (!test.finished());
	

		return this.value;

	}
	/**
	 * Inform agent role inside the organization
	 * @param agent
	 * @param AgentID
	 * @return ArrayList RoleUnitList
	 */
	public ArrayList<String> InformAgentRole(QueueAgent agent, String AgentID) {


		this.list.clear();
		
		String call = configuration + "InformAgentRoleProcess.owl RequestedAgentID="
				+ AgentID;
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agent.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS", "qpid", "localhost", ""));

		logger.debug("[QueryAgent]Sms to send: " + requestMsg.toString());
		logger.debug("[QueryAgent]Sending... ");

		TestAgentClient test = new TestAgentClient(agent, requestMsg, this);

		do {
			test.action();
		} while (!test.finished());

		return this.list;

	}
	/**
	 * Inform of the members of the organization with one veer role and a unit
	 * @param agent
	 * @param RoleID
	 * @param UnitID
	 * @return ArrayList EntityRoleList
	 */
	public ArrayList<String> InformMembers(QueueAgent agent, String RoleID,
			String UnitID) {

		this.list.clear();
		
		String call = configuration + "InformMembersProcess.owl RoleID="
				+ RoleID + " UnitID=" + UnitID;
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agent.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS", "qpid", "localhost", ""));

		logger.debug("[QueryAgent]Sms to send: " + requestMsg.toString());
		logger.debug("[QueryAgent]Sending... ");

		TestAgentClient test = new TestAgentClient(agent, requestMsg, this);

		do {
			test.action();
		} while (!test.finished());

		return this.list;

	}

	/**
	 * Inform norm list
	 * @param agent
	 * @param RoleID
	 * @return ArrayList NormList
	 */
	public ArrayList<String> InformRoleNorms(QueueAgent agent, String RoleID) {

		this.list.clear();
		
		String call = configuration + "InformRoleNormsProcess.owl RoleID="
				+ RoleID;
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agent.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS", "qpid", "localhost", ""));

		logger.debug("[QueryAgent]Sms to send: " + requestMsg.toString());
		logger.debug("[QueryAgent]Sending... ");

		TestAgentClient test = new TestAgentClient(agent, requestMsg, this);

		do {
			test.action();
		} while (!test.finished());

		return this.list;

	}

	/**
	 * Inform role profiles
	 * @param agent
	 * @param UnitID
	 * @return ArrayList ProfileList
	 */
	public ArrayList<String> InformRoleProfiles(QueueAgent agent, String UnitID) {

		this.list.clear();
		
		String call = configuration + "InformRoleProfilesProcess.owl UnitID="
				+ UnitID;
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agent.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS", "qpid", "localhost", ""));

		logger.debug("[QueryAgent]Sms to send: " + requestMsg.toString());
		logger.debug("[QueryAgent]Sending... ");

		TestAgentClient test = new TestAgentClient(agent, requestMsg, this);

		do {
			test.action();
		} while (!test.finished());

		return this.list;

	}

	/**
	 * Inform unit
	 * @param agent
	 * @param UnitID
	 * @return ArrayList UnitType UnitGoal ParentID
	 */
	public ArrayList<String> InformUnit(QueueAgent agent, String UnitID) {

		this.list.clear();
		
		String call = configuration + "InformUnitProcess.owl UnitID=" + UnitID;
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agent.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS", "qpid", "localhost", ""));

		logger.debug("[QueryAgent]Sms to send: " + requestMsg.toString());
		logger.debug("[QueryAgent]Sending... ");

		TestAgentClient test = new TestAgentClient(agent, requestMsg, this);

		do {
			test.action();
		} while (!test.finished());

		return this.list;

	}
/**
 * Inform unit roles
 * @param agent
 * @param UnitID
 * @return ArrayList RoleList
 */
	public ArrayList<String> InformUnitRoles(QueueAgent agent, String UnitID) {

		this.list.clear();
		
		String call = configuration + "InformUnitRolesProcess.owl UnitID="
				+ UnitID;
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agent.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS", "qpid", "localhost", ""));

		logger.debug("[QueryAgent]Sms to send: " + requestMsg.toString());
		logger.debug("[QueryAgent]Sending... ");

		TestAgentClient test = new TestAgentClient(agent, requestMsg, this);

		do {
			test.action();
		} while (!test.finished());

		return this.list;

	}
	/**
	 * Agents quantity in the organization with a role and a unit 
	 * @param agent
	 * @param RoleID
	 * @param UnitID
	 * @return int Quantity
	 */
	public int QuantityMembers(QueueAgent agent, String RoleID, String UnitID) {


		String call = configuration + "QuantityMembersProcess.owl RoleID="
				+ RoleID + " UnitID=" + UnitID;
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agent.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS", "qpid", "localhost", ""));

		logger.debug("[QueryAgent]Sms to send: " + requestMsg.toString());
		logger.debug("[QueryAgent]Sending... ");

		TestAgentClient test = new TestAgentClient(agent, requestMsg, this);

		do {
			test.action();
		} while (!test.finished());

		return this.Quantity;

	}

	/**
	 * Register a new norm in the organization
	 * @param agent
	 * @param NormID
	 * @param NormContent
	 * @return String Status ErrorValue
	 */
	public String RegisterNorm(QueueAgent agent, String NormID,
			String NormContent) {


		String call = configuration + "RegisterNormProcess.owl NormID="
				+ NormID + " NormContent=" + NormContent;
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agent.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS", "qpid", "localhost", ""));

		logger.debug("[QueryAgent]Sms to send: " + requestMsg.toString());
		logger.debug("[QueryAgent]Sending... ");

		TestAgentClient test = new TestAgentClient(agent, requestMsg, this);

		do {
			test.action();
		} while (!test.finished());

		return this.value;

	}
	/**
	 * Register a new role in the organization
	 * @param agent
	 * @param RegisterRoleInputRoleID
	 * @param UnitID
	 * @param Accessibility
	 * @param Position
	 * @param Visibility
	 * @param Inheritance
	 * @return String Status ErroValue
	 */
	public String RegisterRole(QueueAgent agent,
			String RegisterRoleInputRoleID, String UnitID,
			String Accessibility, String Position, String Visibility,
			String Inheritance) {


		String call = configuration
				+ "RegisterRoleProcess.owl RoleID="
				+ RegisterRoleInputRoleID + " UnitID=" + UnitID
				+ " Accessibility=" + Accessibility + " Position=" + Position
				+ " Visibility=" + Visibility + " Inheritance=" + Inheritance;
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agent.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS", "qpid", "localhost", ""));

		logger.debug("[QueryAgent]Sms to send: " + requestMsg.toString());
		logger.debug("[QueryAgent]Sending... ");

		TestAgentClient test = new TestAgentClient(agent, requestMsg, this);

		do {
			test.action();
		} while (!test.finished());

		return this.value;

	}
	/**
	 * Register a new unit in the organization
	 * @param agent
	 * @param UnitID
	 * @param Type
	 * @param Goal
	 * @param ParentUnitID
	 * @return String Status ErrorValue
	 */
	public String RegisterUnit(QueueAgent agent, String UnitID, String Type,
			String Goal, String ParentUnitID) {


		String call = configuration + "RegisterUnitProcess.owl  UnitID="
				+ UnitID + " Type=" + Type + " Goal=" + Goal + " ParentUnitID="
				+ ParentUnitID;
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agent.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS", "qpid", "localhost", ""));

		logger.debug("[QueryAgent]Sms to send: " + requestMsg.toString());
		logger.debug("[QueryAgent]Sending... ");

		TestAgentClient test = new TestAgentClient(agent, requestMsg, this);

		do {
			test.action();
		} while (!test.finished());

		return this.value;

	}
	/**
	 * Deregister norm
	 * @param agent
	 * @param NormID
	 * @return String Status ErrorValue
	 */
	public String DeregisterNorm(QueueAgent agent, String NormID) {


		String call = configuration + "DeregisterNormProcess.owl  NormID="
				+ NormID;
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agent.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS", "qpid", "localhost", ""));

		logger.debug("[QueryAgent]Sms to send: " + requestMsg.toString());
		logger.debug("[QueryAgent]Sending... ");

		TestAgentClient test = new TestAgentClient(agent, requestMsg, this);

		do {
			test.action();
		} while (!test.finished());

		return this.value;

	}
	/**
	 * Deregister role
	 * @param agent
	 * @param RoleID
	 * @param UnitID
	 * @return String Status ErrorValue
	 */
	public String DeregisterRole(QueueAgent agent, String RoleID, String UnitID) {


		String call = configuration + "DeregisterRoleProcess.owl  RoleID="
				+ RoleID + " UnitID=" + UnitID;
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agent.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS", "qpid", "localhost", ""));

		logger.debug("[QueryAgent]Sms to send: " + requestMsg.toString());
		logger.debug("[QueryAgent]Sending... ");

		TestAgentClient test = new TestAgentClient(agent, requestMsg, this);

		do {
			test.action();
		} while (!test.finished());

		return this.value;

	}
	/**
	 * Deregister unit
	 * @param agent
	 * @param UnitID
	 * @return String Status ErrorValue
	 */
	public String DeregisterUnit(QueueAgent agent, String UnitID) {


		String call = configuration + "DeregisterNormProcess.owl  UnitID="
				+ UnitID;
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agent.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS", "qpid", "localhost", ""));

		logger.debug("[QueryAgent]Sms to send: " + requestMsg.toString());
		logger.debug("[QueryAgent]Sending... ");

		TestAgentClient test = new TestAgentClient(agent, requestMsg, this);

		do {
			test.action();
		} while (!test.finished());

		return this.value;

	}

	/**
	 * Expulse an agent of the organization
	 * @param agent
	 * @param AgentID
	 * @param RoleID
	 * @param UnitID
	 * @return String Status ErrorValue
	 */
	public String Expulse(QueueAgent agent, String AgentID, String RoleID,
			String UnitID) {


		String call = configuration + "ExpulseProcess.owl AgentID=" + AgentID
				+ " RoleID=" + RoleID + " UnitID=" + UnitID;
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agent.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS", "qpid", "localhost", ""));


		logger.debug("[QueryAgent]Sms to send: " + requestMsg.toString());
		logger.debug("[QueryAgent]Sending... ");

		TestAgentClient test = new TestAgentClient(agent, requestMsg, this);

		do {
			test.action();
		} while (!test.finished());

		return this.value;

	}

	/**
	 * Register a new agent in the organization
	 * 
	 * @param agente
	 * @param descripcion
	 * @return String Status ErrorValue
	 */
	public String AcquireRole(QueueAgent agent, String RoleID, String UnitID) {
		// montar string de conexion
		// Enviamos el mensaje

		String call = configuration + "AcquireRoleProcess.owl RoleID=" + RoleID
				+ " UnitID=" + UnitID;
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agent.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS", "qpid", "localhost", ""));


		logger.debug("[QueryAgent]Sms to send: " + requestMsg.toString());
		logger.debug("[QueryAgent]Sending... ");

		TestAgentClient test = new TestAgentClient(agent, requestMsg, this);
		
		do {
			test.action();
		} while (!test.finished());
		//bloquea el agente
		//this.monitor.waiting();

		return this.value;


	}

	/**
	 * TestAgentClient handles the messages received from the OMS
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
			logger.debug(myAgent.getName() + ": OOH! "
					+ msg.getSender().getLocalName()
					+ " Has agreed to excute the service!");
		}

		protected void handleRefuse(ACLMessage msg) {
			logger.debug(myAgent.getName() + ": Oh no! "
					+ msg.getSender().getLocalName()
					+ " has rejected my proposal.");
			this.oms.setValor(myAgent.getName() + ": Oh no! "
					+ msg.getSender().getLocalName()
					+ " has rejected my proposal.");

		}

		protected void handleInform(ACLMessage msg) {
			logger.debug(myAgent.getName() + ":"
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


			//this.oms.monitor.advise();

		}

		protected void handleNotUnderstood(ACLMessage msg) {
			logger.info(myAgent.getName() + ":"
					+ msg.getSender().getLocalName()
					+ " has indicated that they didn't understand.");
			this.oms.setValor(myAgent.getName() + ":"
					+ msg.getSender().getLocalName()
					+ " has indicated that they didn't understand.");
		
		}

		protected void handleOutOfSequence(ACLMessage msg) {
			logger.info(myAgent.getName() + ":"
					+ msg.getSender().getLocalName()
					+ " has send me a message which i wasn't"
					+ " expecting in this conversation");
			this.oms.setValor(myAgent.getName() + ":"
					+ msg.getSender().getLocalName()
					+ " has send me a message which i wasn't"
					+ " expecting in this conversation");
	
		}
	}

}
