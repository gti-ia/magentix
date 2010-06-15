package es.upv.dsic.gti_ia.organization;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import es.upv.dsic.gti_ia.architecture.FIPARequestInitiator;
import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.architecture.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;

/**
 * This class gives us the support to accede to the services of the OMS. The OMS
 * provides a group of services for registering or deregistering structural
 * components, specific roles, norms and units. It also offers services for
 * reporting on these components.
 * 
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
	private String[] elements;

	Configuration c;

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
	 * This class gives us the support to accede to the services of the OMS,
	 * checked that the data contained in the file settings.xml is the URL
	 * OMSServiceDescriptionLocation is not empty and is the correct path.
	 * 
	 * 
	 * 
	 */
	public OMSProxy() {

		c = Configuration.getConfiguration();
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
		this.value = "";
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
	 * Requests to leave a role
	 * 
	 * @param agent
	 *            is a QueueAgent, this agent implemented the communication
	 *            protocol
	 * @param AgentID
	 *            entity,this agent is protocol://name@host:port
	 *            ej.qpid://clientagent2@localhost:8080 , we can extract this
	 *            inform with the method getAid().toString() for example.
	 * @param RoleID
	 *            represent all required functionality needed in order to
	 *            achieve the unit goal.
	 * @param UnitID
	 *            organizational units (OUs), which represent groups of entities
	 *            (agents or other units)
	 * @return String Status ErrorValue
	 */
	public String leaveRole(QueueAgent agent, String AgentID, String RoleID,
			String UnitID) throws Exception {

		String call = configuration + "LeaveRoleProcess.owl AgentID=" + AgentID
				+ " RoleID=" + RoleID + " UnitID=" + UnitID;

		this.sendInform(agent, call);

		if (!this.value.equals("Ok")) {
			logger.error("Leave Role " + this.value);
			throw new Exception("Leave Role " + this.value);
		} else
			return this.value;

	}

	/**
	 * Indicates roles adopted by an agent
	 * 
	 * @param agent
	 *            is a QueueAgent, this agent implemented the communication
	 *            protocol
	 * @param AgentID
	 *            entity,this agent is protocol://name@host:port
	 *            ej.qpid://clientagent2@localhost:8080 , we can extract this
	 *            inform with the method getAid().toString() for example.
	 * @return ArrayList RoleUnitList
	 * @throws Exception
	 */
	public ArrayList<String> informAgentRole(QueueAgent agent, String AgentID)
			throws Exception {

		this.list.clear();

		String call = configuration
				+ "InformAgentRoleProcess.owl RequestedAgentID=" + AgentID;
		this.sendInform(agent, call);

		if (!this.value.equals("Ok")) {
			throw new Exception("Inform Agent Role " + this.value);
		} else
			return this.list;

	}

	/**
	 * Indicates entities that are members of a specific unit
	 * 
	 * @param agent
	 *            is a QueueAgent, this agent implemented the communication
	 *            protocol
	 * @param RoleID
	 *            represent all required functionality needed in order to
	 *            achieve the unit goal.
	 * @param UnitID
	 *            organizational units (OUs), which represent groups of entities
	 *            (agents or other units)
	 * @return ArrayList EntityRoleList
	 * @throws Exception
	 */
	public ArrayList<String> informMembers(QueueAgent agent, String RoleID,
			String UnitID) throws Exception {

		this.list.clear();

		String call = configuration + "InformMembersProcess.owl RoleID="
				+ RoleID + " UnitID=" + UnitID;
		this.sendInform(agent, call);

		if (!this.value.equals("Ok")) {
			throw new Exception("Inform Members " + this.value);
		} else
			return this.list;

	}

	/**
	 * Provides all norms addressed to a specific role
	 * 
	 * @param agent
	 *            is a QueueAgent, this agent implemented the communication
	 *            protocol
	 * 
	 * @param RoleID
	 *            represent all required functionality needed in order to
	 *            achieve the unit goal.
	 * 
	 * @return ArrayList NormList
	 * @throws Exception
	 */
	public ArrayList<String> informRoleNorms(QueueAgent agent, String RoleID)
			throws Exception {

		this.list.clear();

		String call = configuration + "InformRoleNormsProcess.owl RoleID="
				+ RoleID;
		this.sendInform(agent, call);

		if (!this.value.equals("Ok")) {
			throw new Exception("Inform Role Norms " + this.value);
		} else
			return this.list;

	}

	/**
	 * Indicates all profiles associated to a specific role
	 * 
	 * @param agent
	 *            is a QueueAgent, this agent implemented the communication
	 *            protocol
	 * @param UnitID
	 *            organizational units (OUs), which represent groups of entities
	 *            (agents or other units)
	 * @return ArrayList ProfileList
	 * @throws Exception
	 */
	public ArrayList<String> informRoleProfiles(QueueAgent agent, String UnitID)
			throws Exception {

		this.list.clear();

		String call = configuration + "InformRoleProfilesProcess.owl UnitID="
				+ UnitID;
		this.sendInform(agent, call);

		if (!this.value.equals("Ok")) {
			throw new Exception("Inform Role Profiles " + this.value);
		} else
			return this.list;

	}

	/**
	 * Provides unit description
	 * 
	 * @param agent
	 *            is a QueueAgent, this agent implemented the communication
	 *            protocol
	 * @param UnitID
	 *            organizational units (OUs), which represent groups of entities
	 *            (agents or other units)
	 * @return ArrayList UnitType UnitGoal ParentID
	 * @throws Exception
	 */
	public ArrayList<String> informUnit(QueueAgent agent, String UnitID)
			throws Exception {

		this.list.clear();

		String call = configuration + "InformUnitProcess.owl UnitID=" + UnitID;
		this.sendInform(agent, call);

		if (!this.value.contains("Ok")) {
			throw new Exception("Inform Unit " + this.value);
		} else
			return this.list;

	}

	/**
	 * Indicates which roles are the ones defined within a specific unit
	 * 
	 * @param agent
	 *            is a QueueAgent, this agent implemented the communication
	 *            protocol
	 * 
	 * @param UnitID
	 *            organizational units (OUs), which represent groups of entities
	 *            (agents or other units)
	 * 
	 * @return ArrayList RoleList
	 * @throws Exception
	 */
	public ArrayList<String> informUnitRoles(QueueAgent agent, String UnitID)
			throws Exception {

		this.list.clear();

		String call = configuration + "InformUnitRolesProcess.owl UnitID="
				+ UnitID;
		this.sendInform(agent, call);
		if (!this.value.equals("Ok")) {
			throw new Exception("Inform Unit Roles " + this.value);
		} else
			return this.list;

	}

	/**
	 * Provides the number of current members of a specific unit
	 * 
	 * @param agent
	 *            is a QueueAgent, this agent implemented the communication
	 *            protocol
	 * 
	 * @param RoleID
	 *            represent all required functionality needed in order to
	 *            achieve the unit goal.
	 * @param UnitID
	 *            organizational units (OUs), which represent groups of entities
	 *            (agents or other units)
	 * 
	 * @return int Quantity
	 * @throws Exception
	 */
	public int quantityMembers(QueueAgent agent, String RoleID, String UnitID)
			throws Exception {

		String call = configuration + "QuantityMembersProcess.owl RoleID="
				+ RoleID + " UnitID=" + UnitID;
		this.sendInform(agent, call);
		if (!this.value.equals("Ok")) {
			throw new Exception("Quantity Members " + this.value);
		} else
			return this.Quantity;

	}

	/**
	 * Includes a new norm within a unit
	 * 
	 * @param agent
	 *            is a QueueAgent, this agent implemented the communication
	 *            protocol
	 * @param NormID
	 *            norm for controlling role actions
	 * @param NormContent
	 *            The syntax of the rules of incompatibility is the following:
	 *            FORBIDDEN role1 REQUEST AcquireRole MESSAGE (CONTENT (role
	 *            'role2ID')) Applications for registration of a rule is
	 *            necessary to replace the spaces between the different words
	 *            for "_".
	 * @return String Status ErrorValue
	 * @throws Exception
	 */
	public String registerNorm(QueueAgent agent, String NormID,
			String NormContent) throws Exception {

		String call = configuration + "RegisterNormProcess.owl NormID="
				+ NormID + " NormContent=" + NormContent;
		this.sendInform(agent, call);
		if (!this.value.equals("Ok")) {
			throw new Exception("Register Norm " + this.value);
		} else
			return this.value;

	}

	/**
	 * Creates a new role within a unit
	 * 
	 * @param agent
	 *            is a QueueAgent, this agent implemented the communication
	 *            protocol
	 * @param RoleID
	 *            is the identifier of the new role
	 * @param UnitID
	 *            is the identifier of the organizational unit in which the new
	 *            role is defined
	 * @param Accessibility
	 *            considers two types of roles: (a) internal roles, which are
	 *            assigned to internal agents of the system platform; and (b)
	 *            external roles, which can be enacted by any agent.
	 * @param Position
	 *            determines its structural position inside the unit, such as
	 *            supervisor or subordinate
	 * @param Visibility
	 *            indicates whether agents can obtain information of this role
	 *            from outside the unit in which this role is defined (public)
	 *            or from inside (private).
	 * @param Inheritance
	 *            is the identifier of the parent role in the role hierarchy
	 * @return String Status ErroValue
	 * @throws Exception
	 */
	public String registerRole(QueueAgent agent, String RoleID, String UnitID,
			String Accessibility, String Position, String Visibility,
			String Inheritance) throws Exception {

		String call = configuration + "RegisterRoleProcess.owl RoleID="
				+ RoleID + " UnitID=" + UnitID + " Accessibility="
				+ Accessibility + " Position=" + Position + " Visibility="
				+ Visibility + " Inheritance=" + Inheritance;
		this.sendInform(agent, call);
		if (!this.value.equals("Ok")) {
			throw new Exception("Register Role " + this.value);
		} else
			return this.value;

	}

	/**
	 * Creates a new unit within a specific organization
	 * 
	 * @param agent
	 *            is a QueueAgent, this agent implemented the communication
	 *            protocol
	 * @param UnitID
	 *            is the identifier of the new unit
	 * @param Type
	 *            indicates the topology of the new unit: (i) Hierarchy, in
	 *            which a supervisor agent has control over other members; (ii)
	 *            Team, which are groups of agents that share a common goal,
	 *            collaborating and cooperating between them; and (iii) Flat, in
	 *            which there is none agent with control over other members.
	 * @param Goal
	 *            describes goals pursued by the unit
	 * @param ParentUnitID
	 *            is the identifier of the parent unit which contains the new
	 *            unit
	 * @return String Status ErrorValue
	 * @throws Exception
	 */
	public String registerUnit(QueueAgent agent, String UnitID, String Type,
			String Goal, String ParentUnitID) throws Exception {

		String call = configuration + "RegisterUnitProcess.owl  UnitID="
				+ UnitID + " Type=" + Type + " Goal=" + Goal + " ParentUnitID="
				+ ParentUnitID;
		this.sendInform(agent, call);
		if (!this.value.equals("Ok")) {
			throw new Exception("Register Unit " + this.value);
		} else
			return this.value;

	}

	/**
	 * Removes a specific norm description
	 * 
	 * @param agent
	 *            is a QueueAgent, this agent implemented the communication
	 *            protocol
	 * @param NormID
	 *            norm for controlling role actions
	 * @return String Status ErrorValue
	 * @throws Exception
	 */
	public String deregisterNorm(QueueAgent agent, String NormID)
			throws Exception {

		String call = configuration + "DeregisterNormProcess.owl  NormID="
				+ NormID;
		this.sendInform(agent, call);
		if (!this.value.equals("Ok")) {
			throw new Exception("Deregister Norm " + this.value);
		} else
			return this.value;

	}

	/**
	 * Removes a specific role description from a unit
	 * 
	 * @param agent
	 *            is a QueueAgent, this agent implemented the communication
	 *            protocol
	 * @param RoleID
	 *            represent all required functionality needed in order to
	 *            achieve the unit goal.
	 * @param UnitID
	 *            organizational units (OUs), which represent groups of entities
	 *            (agents or other units)
	 * @return String Status ErrorValue
	 * @throws Exception
	 */
	public String deregisterRole(QueueAgent agent, String RoleID, String UnitID)
			throws Exception {

		String call = configuration + "DeregisterRoleProcess.owl  RoleID="
				+ RoleID + " UnitID=" + UnitID;
		this.sendInform(agent, call);
		if (!this.value.equals("Ok")) {
			throw new Exception("Deregister Role " + this.value);
		} else
			return this.value;

	}

	/**
	 * Removes a unit from an organization
	 * 
	 * @param agent
	 *            is a QueueAgent, this agent implemented the communication
	 *            protocol
	 * @param UnitID
	 *            organizational units (OUs), which represent groups of entities
	 *            (agents or other units)
	 * @return String Status ErrorValue
	 * @throws Exception
	 */
	public String deregisterUnit(QueueAgent agent, String UnitID)
			throws Exception {

		String call = configuration + "DeregisterNormProcess.owl  UnitID="
				+ UnitID;
		this.sendInform(agent, call);
		if (!this.value.equals("Ok")) {
			throw new Exception("Deregister Unit " + this.value);
		} else
			return this.value;

	}

	/**
	 * Forces an agent to leave a specific role
	 * 
	 * @param agent
	 *            is a QueueAgent, this agent implemented the communication
	 *            protocol
	 * @param AgentID
	 *            entity,this agent is protocol://name@host:port
	 *            ej.qpid://clientagent2@localhost:8080 , we can extract this
	 *            inform with the method getAid().toString() for example.
	 * @param RoleID
	 *            represent all required functionality needed in order to
	 *            achieve the unit goal.
	 * @param UnitID
	 *            organizational units (OUs), which represent groups of entities
	 *            (agents or other units)
	 * @return String Status ErrorValue
	 * @throws Exception
	 *             if
	 */
	public String expulse(QueueAgent agent, String AgentID, String RoleID,
			String UnitID) throws Exception {

		String call = configuration + "ExpulseProcess.owl AgentID=" + AgentID
				+ " RoleID=" + RoleID + " UnitID=" + UnitID;
		this.sendInform(agent, call);
		if (!this.value.equals("Ok")) {
			throw new Exception("Expulse  " + this.value);
		} else
			return this.value;

	}

	/**
	 * Requests the adoption of a specific role within a unit
	 * 
	 * @param agent
	 *            Agent that we will register in the organization, is a
	 *            QueueAgent
	 * @param roleID
	 *            Role that the agent acquires inside the organization
	 * @param unitID
	 *            Unit of which the agent was forming a part with the previous
	 *            role
	 * @return String Status ErrorValue
	 * @throws Exception
	 */
	public String acquireRole(QueueAgent agent, String RoleID, String UnitID)
			throws Exception {
		// montar string de conexion
		// Enviamos el mensaje

		String call = configuration + "AcquireRoleProcess.owl RoleID=" + RoleID
				+ " UnitID=" + UnitID;
		this.sendInform(agent, call);
		if (!this.value.equals("Ok")) {
			throw new Exception("Acquire Role " + this.value);
		} else
			return this.value;

	}

	/**
	 * Requests the adoption of a specific role within a unit
	 * 
	 * @param agent
	 *            Agent that we will register in the organization, is a
	 *            QueueAgent
	 * @param AgentID
	 *            entity,this agent is: protocol://name@host:port.
	 *            ej.qpid://clientagent2@localhost:8080 , we can extract this
	 *            inform with the method getAid().toString() for example.          
	 * @param roleID
	 *            Role that the agent acquires inside the organization
	 * @param unitID
	 *            Unit of which the agent was forming a part with the previous
	 *            role
	 * @return String Status ErrorValue
	 * @throws Exception
	 */
	public String acquireRole(QueueAgent agent, String AgentID, String RoleID, String UnitID)
			throws Exception {
		// montar string de conexion
		// Enviamos el mensaje

		String call = configuration + "AcquireRoleProcess.owl RoleID=" + RoleID
				+ " UnitID=" + UnitID+" AgentID="+ AgentID;
		this.sendInform(agent, call);
		if (!this.value.equals("Ok")) {
			throw new Exception("Acquire Role " + this.value);
		} else
			return this.value;

	}

	void extractInfo(ACLMessage msg) {
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
			this.addElementToList(arg);
			argAux = msg.getContent().substring(
					msg.getContent().indexOf("UnitGoal"),
					msg.getContent().length());
			arg = argAux.substring(argAux.indexOf("UnitGoal") + 9, argAux
					.indexOf(","));
			this.addElementToList(arg);
			argAux = argAux.substring(argAux.indexOf("UnitType"), argAux
					.length());
			arg = argAux.substring(argAux.indexOf("UnitType") + 9, argAux
					.indexOf("}"));
			this.addElementToList(arg);

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
					this.addElementToList("EMPTY");
				} else {
					String arg3 = msg.getContent().substring(
							msg.getContent().indexOf(",") + 1,
							msg.getContent().length());
					arg3 = arg3.substring(arg3.indexOf("("), arg3.indexOf("]"));

					elements = arg3.split(",");

					int paridad = 0;

					for (String e : elements) {
						if ((paridad % 2) == 0)// es par
						{
							argAux = e
									.substring(e.indexOf("(") + 1, e.length());

						} else {
							argAux = e.substring(0, e.indexOf(")"));
						}
						this.addElementToList(argAux);
						paridad++;
					}
				}
			} else {
				if (!status.equals("Ok")) {

					this.addElementToList("EMPTY");
				} else {
					String arg3 = msg.getContent().substring(
							msg.getContent().indexOf("[") + 1,
							msg.getContent().indexOf("]"));

					elements = arg3.split(",");

					for (String e : elements) {
						this.addElementToList(e);

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
				this.setQuantity(Integer.parseInt(quantity));
			}

			this.setValor(status);
		} else {
			// vemos que tipo de error
			this.setValor(status + " " + ErrorValue);
		}
	}

	/**
	 * TestAgentClient handles the messages received from the OMS
	 */
	static class TestAgentClient extends FIPARequestInitiator {

		OMSProxy oms;

		protected TestAgentClient(QueueAgent agent, ACLMessage msg, OMSProxy oms) {
			super(agent, msg);
			this.oms = oms;

		}

		protected void handleAgree(ACLMessage msg) {
			logger.info(myAgent.getName() + ": OOH! "
					+ msg.getSender().getLocalName()
					+ " Has agreed to excute the service!");

		}

		protected void handleRefuse(ACLMessage msg) {
			logger.error(myAgent.getName() + ": Oh no! "
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
			this.oms.extractInfo(msg);

		}

		protected void handleNotUnderstood(ACLMessage msg) {
			logger.error(myAgent.getName() + ":"
					+ msg.getSender().getLocalName()
					+ " has indicated that they didn't understand.");
			this.oms.setValor(myAgent.getName() + ":"
					+ msg.getSender().getLocalName()
					+ " has indicated that they didn't understand.");

		}

		protected void handleOutOfSequence(ACLMessage msg) {
			logger.error(myAgent.getName() + ":"
					+ msg.getSender().getLocalName()
					+ " has send me a message which i wasn't"
					+ " expecting in this conversation");
			this.oms.setValor(myAgent.getName() + ":"
					+ msg.getSender().getLocalName()
					+ " has send me a message which i wasn't"
					+ " expecting in this conversation");

		}

		protected void handleFailure(ACLMessage msg) {
			logger.error(myAgent.getName() + ":"
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
