package es.upv.dsic.gti_ia.organization;

import es.upv.dsic.gti_ia.architecture.FIPARequestInitiator;
import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.architecture.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;

import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * This class gives us the support to accede to the services of the OMS
 * 
 * @author Joan Bellver Faus 
 * 
 */
public class OMSProxy {

	private String configuration;
	private String value = "";
	private int Quantity;
	private ArrayList<String> list = new ArrayList<String>();
	static Logger logger = Logger.getLogger(OMSProxy.class);

	Configuration c = Configuration.getConfiguration();

	/**
	 * This class gives us the support to accede to the services of the OMS
	 * 
	 * @param OMSServiceDesciptionLocation
	 *            The URL where the owl's document is located.
	 */
	public OMSProxy(String OMSServiceDesciptionLocation) {

		this.configuration = OMSServiceDesciptionLocation;
	}

	/**
	 * This class gives us the support to accede to the services of the OMS
	 * 
	 *
	 */
	public OMSProxy() {

		this.configuration = c.getOMSServiceDesciptionLocation();
	}

	private void setValor(String value) {
		this.value = value;
	}

	private void addElementToList(String element) {
		this.list.add(element);
	}

	private void setQuantity(int Quantity) {
		this.Quantity = Quantity;
	}

	private void sendInform(QueueAgent agent, String call) {
		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agent.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID("OMS"));

		logger.info("[QueryAgent]Sms to send: " + requestMsg.getContent());
		logger.info("[QueryAgent]Sending... ");

		TestAgentClient test = new TestAgentClient(agent, requestMsg, this);

		do {
			test.action();
		} while (!test.finished());
		
	}

	/**
	 * Leave role agent inside the organization
	 * 
	 * @param agent
	 * @param AgentID
	 * @param RoleID
	 * @param UnitID
	 * @return String Status ErrorValue
	 */
	public String LeaveRole(QueueAgent agent, String AgentID, String RoleID,
			String UnitID) throws Exception{

		String call = configuration + "LeaveRoleProcess.owl AgentID=" + AgentID
				+ " RoleID=" + RoleID + " UnitID=" + UnitID;

		this.sendInform(agent, call);
		
		if (!this.value.equals("Ok"))
		{
			throw new Exception("Leave Role "+ this.value);
		}
		else
			return this.value;

	}

	/**
	 * Inform agent role inside the organization
	 * 
	 * @param agent
	 * @param AgentID
	 * @return ArrayList RoleUnitList
	 * @throws Exception 
	 */
	public ArrayList<String> InformAgentRole(QueueAgent agent, String AgentID) throws Exception {

		this.list.clear();

		String call = configuration
				+ "InformAgentRoleProcess.owl RequestedAgentID=" + AgentID;
		this.sendInform(agent, call);
         
		if (!this.value.equals("Ok"))
		{
			throw new Exception("Leave Role "+ this.value);
		}
		else
			return this.list;

	}

	/**
	 * Inform of the members of the organization with one veer role and a unit
	 * 
	 * @param agent
	 * @param RoleID
	 * @param UnitID
	 * @return ArrayList EntityRoleList
	 * @throws Exception 
	 */
	public ArrayList<String> InformMembers(QueueAgent agent, String RoleID,
			String UnitID) throws Exception {

		this.list.clear();

		String call = configuration + "InformMembersProcess.owl RoleID="
				+ RoleID + " UnitID=" + UnitID;
		this.sendInform(agent, call);

		if (!this.value.equals("Ok"))
		{
			throw new Exception("Leave Role "+ this.value);
		}
		else
		return this.list;

	}

	/**
	 * Inform norm list
	 * 
	 * @param agent
	 * @param RoleID
	 * @return ArrayList NormList
	 * @throws Exception 
	 */
	public ArrayList<String> InformRoleNorms(QueueAgent agent, String RoleID) throws Exception {

		this.list.clear();

		String call = configuration + "InformRoleNormsProcess.owl RoleID="
				+ RoleID;
		this.sendInform(agent, call);
		
		if (!this.value.equals("Ok"))
		{
			throw new Exception("Leave Role "+ this.value);
		}
		else
		return this.list;

	}

	/**
	 * Inform role profiles
	 * 
	 * @param agent
	 * @param UnitID
	 * @return ArrayList ProfileList
	 * @throws Exception 
	 */
	public ArrayList<String> InformRoleProfiles(QueueAgent agent, String UnitID) throws Exception {

		this.list.clear();

		String call = configuration + "InformRoleProfilesProcess.owl UnitID="
				+ UnitID;
		this.sendInform(agent, call);
		
		if (!this.value.equals("Ok"))
		{
			throw new Exception("Leave Role "+ this.value);
		}
		else
		return this.list;

	}

	/**
	 * Inform unit
	 * 
	 * @param agent
	 * @param UnitID
	 * @return ArrayList UnitType UnitGoal ParentID
	 * @throws Exception 
	 */
	public ArrayList<String> InformUnit(QueueAgent agent, String UnitID) throws Exception {

		this.list.clear();

		String call = configuration + "InformUnitProcess.owl UnitID=" + UnitID;
		this.sendInform(agent, call);
		
		if (!this.value.equals("Ok"))
		{
			throw new Exception("Leave Role "+ this.value);
		}
		else
		return this.list;

	}

	/**
	 * Inform unit roles
	 * 
	 * @param agent
	 * @param UnitID
	 * @return ArrayList RoleList
	 * @throws Exception 
	 */
	public ArrayList<String> InformUnitRoles(QueueAgent agent, String UnitID) throws Exception {

		this.list.clear();

		String call = configuration + "InformUnitRolesProcess.owl UnitID="
				+ UnitID;
		this.sendInform(agent, call);
		if (!this.value.equals("Ok"))
		{
			throw new Exception("Leave Role "+ this.value);
		}
		else
		return this.list;

	}

	/**
	 * Agents quantity in the organization with a role and a unit
	 * 
	 * @param agent
	 * @param RoleID
	 * @param UnitID
	 * @return int Quantity
	 * @throws Exception 
	 */
	public int QuantityMembers(QueueAgent agent, String RoleID, String UnitID) throws Exception {

		String call = configuration + "QuantityMembersProcess.owl RoleID="
				+ RoleID + " UnitID=" + UnitID;
		this.sendInform(agent, call);
		if (!this.value.equals("Ok"))
		{
			throw new Exception("Leave Role "+ this.value);
		}
		else
		return this.Quantity;

	}

	/**
	 * Register a new norm in the organization
	 * 
	 * @param agent
	 * @param NormID
	 * @param NormContent
	 * @return String Status ErrorValue
	 * @throws Exception 
	 */
	public String RegisterNorm(QueueAgent agent, String NormID,
			String NormContent) throws Exception {

		String call = configuration + "RegisterNormProcess.owl NormID="
				+ NormID + " NormContent=" + NormContent;
		this.sendInform(agent, call);
		if (!this.value.equals("Ok"))
		{
			throw new Exception("Leave Role "+ this.value);
		}
		else
		return this.value;

	}

	/**
	 * Register a new role in the organization
	 * 
	 * @param agent
	 * @param RegisterRoleInputRoleID
	 * @param UnitID
	 * @param Accessibility
	 * @param Position
	 * @param Visibility
	 * @param Inheritance
	 * @return String Status ErroValue
	 * @throws Exception 
	 */
	public String RegisterRole(QueueAgent agent,
			String RegisterRoleInputRoleID, String UnitID,
			String Accessibility, String Position, String Visibility,
			String Inheritance) throws Exception {

		String call = configuration + "RegisterRoleProcess.owl RoleID="
				+ RegisterRoleInputRoleID + " UnitID=" + UnitID
				+ " Accessibility=" + Accessibility + " Position=" + Position
				+ " Visibility=" + Visibility + " Inheritance=" + Inheritance;
		this.sendInform(agent, call);
		if (!this.value.equals("Ok"))
		{
			throw new Exception("Leave Role "+ this.value);
		}
		else
		return this.value;

	}

	/**
	 * Register a new unit in the organization
	 * 
	 * @param agent
	 * @param UnitID
	 * @param Type
	 * @param Goal
	 * @param ParentUnitID
	 * @return String Status ErrorValue
	 * @throws Exception 
	 */
	public String RegisterUnit(QueueAgent agent, String UnitID, String Type,
			String Goal, String ParentUnitID) throws Exception {

		String call = configuration + "RegisterUnitProcess.owl  UnitID="
				+ UnitID + " Type=" + Type + " Goal=" + Goal + " ParentUnitID="
				+ ParentUnitID;
		this.sendInform(agent, call);
		if (!this.value.equals("Ok"))
		{
			throw new Exception("Leave Role "+ this.value);
		}
		else
		return this.value;

	}

	/**
	 * Deregister norm
	 * 
	 * @param agent
	 * @param NormID
	 * @return String Status ErrorValue
	 * @throws Exception 
	 */
	public String DeregisterNorm(QueueAgent agent, String NormID) throws Exception {

		String call = configuration + "DeregisterNormProcess.owl  NormID="
				+ NormID;
		this.sendInform(agent, call);
		if (!this.value.equals("Ok"))
		{
			throw new Exception("Leave Role "+ this.value);
		}
		else
		return this.value;

	}

	/**
	 * Deregister role
	 * 
	 * @param agent
	 * @param RoleID
	 * @param UnitID
	 * @return String Status ErrorValue
	 * @throws Exception 
	 */
	public String DeregisterRole(QueueAgent agent, String RoleID, String UnitID) throws Exception {

		String call = configuration + "DeregisterRoleProcess.owl  RoleID="
				+ RoleID + " UnitID=" + UnitID;
		this.sendInform(agent, call);
		if (!this.value.equals("Ok"))
		{
			throw new Exception("Leave Role "+ this.value);
		}
		else
		return this.value;

	}

	/**
	 * Deregister unit
	 * 
	 * @param agent
	 * @param UnitID
	 * @return String Status ErrorValue
	 * @throws Exception 
	 */
	public String DeregisterUnit(QueueAgent agent, String UnitID) throws Exception {

		String call = configuration + "DeregisterNormProcess.owl  UnitID="
				+ UnitID;
		this.sendInform(agent, call);
		if (!this.value.equals("Ok"))
		{
			throw new Exception("Leave Role "+ this.value);
		}
		else
		return this.value;

	}

	/**
	 * Expulse an agent of the organization
	 * 
	 * @param agent
	 * @param AgentID
	 * @param RoleID
	 * @param UnitID
	 * @return String Status ErrorValue
	 * @throws Exception 
	 */
	public String Expulse(QueueAgent agent, String AgentID, String RoleID,
			String UnitID) throws Exception {

		String call = configuration + "ExpulseProcess.owl AgentID=" + AgentID
				+ " RoleID=" + RoleID + " UnitID=" + UnitID;
		this.sendInform(agent, call);
		if (!this.value.equals("Ok"))
		{
			throw new Exception("Leave Role "+ this.value);
		}
		else
		return this.value;

	}

	/**
	 * Register a new agent in the organization
	 * 
	 * @param agente
	 * @param descripcion
	 * @return String Status ErrorValue
	 * @throws Exception 
	 */
	public String AcquireRole(QueueAgent agent, String RoleID, String UnitID) throws Exception {
		// montar string de conexion
		// Enviamos el mensaje

		String call = configuration + "AcquireRoleProcess.owl RoleID=" + RoleID
				+ " UnitID=" + UnitID;
		this.sendInform(agent, call);
		if (!this.value.equals("Ok"))
		{
			throw new Exception("Leave Role "+ this.value);
		}
		else
		return this.value;

	}

	/**
	 * TestAgentClient handles the messages received from the OMS
	 */
	static class TestAgentClient extends FIPARequestInitiator {
		QueueAgent agent;
		OMSProxy oms;
		String[] elements;

		protected TestAgentClient(QueueAgent agent, ACLMessage msg,
				OMSProxy oms) {
			super(agent, msg);
			this.agent = agent;
			this.oms = oms;

		}

		protected void handleAgree(ACLMessage msg) {
			logger.info(myAgent.getName() + ": OOH! "
					+ msg.getSender().getLocalName()
					+ " Has agreed to excute the service!");

		}

		protected void handleRefuse(ACLMessage msg) {
			logger.info(myAgent.getName() + ": Oh no! "
					+ msg.getSender().getLocalName()
					+ " has rejected my proposal.");
			this.oms.setValor(myAgent.getName() + ": Oh no! "
					+ msg.getSender().getLocalName()
					+ " has rejected my proposal.");

		}

		protected void handleInform(ACLMessage msg) {
			logger.info(myAgent.getName() + ":"
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

			// sacamos el Status
			String status = "";

			int n = msg.getContent().indexOf(",")
					- msg.getContent().indexOf("Status");

			if (n > 0) {
				status = msg.getContent().substring(
						msg.getContent().indexOf("Status") + 7,
						msg.getContent().indexOf(","));
			} else {
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

				// para los servicios informativos

				// diferenciamos entre los que tienen tuplas y los que no
				// son tuplas

				if (patron.equals("InformAgentRoleProcess")
						|| patron.equals("InformMembersProcess")) {

					if (!status.equals("Ok")) {
						this.oms.addElementToList("EMPTY");
					} else {
						String arg3 = msg.getContent().substring(
								msg.getContent().indexOf(",") + 1,
								msg.getContent().length());
						arg3 = arg3.substring(arg3.indexOf("("), arg3
								.indexOf("]"));

						elements = arg3.split(",");

						int paridad = 0;

						for (String e : elements) {
							if ((paridad % 2) == 0)// es par
							{
								argAux = e.substring(e.indexOf("(") + 1, e
										.length());

							} else {
								argAux = e.substring(0, e.indexOf(")"));
							}
							this.oms.addElementToList(argAux);
							paridad++;
						}
					}
				} else {
					if (!status.equals("Ok")) {

						this.oms.addElementToList("EMPTY");
					} else {
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

			n = msg.getContent().indexOf(",")
					- msg.getContent().indexOf("ErrorValue");

			if (n > 0) {
				ErrorValue = msg.getContent().substring(
						msg.getContent().indexOf("ErrorValue") + 11,
						msg.getContent().indexOf(","));
			} else {
				ErrorValue = msg.getContent().substring(
						msg.getContent().indexOf("ErrorValue") + 11,
						msg.getContent().indexOf("}"));

			}

			
			if (status.equals("Ok")) {

				if (patron.equals("QuantityMembersProcess")) {

					String quantity = msg.getContent().substring(
							msg.getContent().indexOf("Quantity=") + 9,
							msg.getContent().indexOf("}"));
					this.oms.setQuantity(Integer.parseInt(quantity));
				}

				this.oms.setValor(status);
			} else {
				// vemos que tipo de error
				this.oms.setValor(status + " " + ErrorValue);
			}

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
