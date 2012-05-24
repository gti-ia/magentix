package es.upv.dsic.gti_ia.organization;


import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.log4j.Logger;

import es.upv.dsic.gti_ia.architecture.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.architecture.FIPARequestInitiator;
import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CFactory;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_REQUEST_Initiator;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.organization.THOMASMessages.MessageID;


/**
 * This class is the parent class for the OMS and SF proxys.
 */
public class THOMASProxy {



	//-------------------------------------------------------------------
	//---------------------------VARIABLES-------------------------------
	//-------------------------------------------------------------------

	static Logger logger = Logger.getLogger(THOMASProxy.class);


	BaseAgent agent = null;
	CProcessor myProcessor = null;
	Oracle oracle;

	Configuration c;

	String call;
	String thomasAgent;
	String serviceName;
	String ServiceDescriptionLocation;
	String value = ""; //Returned value

	int Quantity;
	String[] elements;

	Hashtable<AgentID, String> agents = new Hashtable<AgentID, String>();
	ArrayList<String> serviceTypeResult2;
	ArrayList<ArrayList<String>> serviceTypeResult3;
	String serviceTypeResult4;
	ArrayList<String> serviceTypeResult5;
	ResponseParser responseParser = new ResponseParser();

	
	Object result = null;

	private boolean Status = true;

	private ArrayList<String> serviceType1 = new ArrayList<String>();//This type returns a String
	private ArrayList<String> serviceType2 = new ArrayList<String>();//This type returns a Array of String
	private ArrayList<String> serviceType3 = new ArrayList<String>();//This type returns a Array of Array of strings
	private ArrayList<String> serviceType4 = new ArrayList<String>();//This type returns a String with specification
	private ArrayList<String> serviceType5 = new ArrayList<String>();//This type returns a Array of String with description and specification





	//-------------------------------------------------------------------
	//---------------------------CONSTRUCTORS----------------------------
	//-------------------------------------------------------------------
	/**
	 * This class gives us the support to accede to the services of the OMS and SF
	 * @param agent, is a BaseAgent, this agent implemented the communication protocol          
	 * @param thomasAgent, is a OMS or SF.
	 * @param ServiceDesciptionLocation The URL where the owl's document is located.
	 *            
	 */
	THOMASProxy(BaseAgent agent, String thomasAgent,String ServiceDescriptionLocation) {
		this.agent = agent;
		this.ServiceDescriptionLocation = ServiceDescriptionLocation;
	}

	THOMASProxy(CProcessor firstProcessor, String thomasAgent,String ServiceDescriptionLocation) {
		this.agent = firstProcessor.getMyAgent();
		this.myProcessor = firstProcessor;
		this.ServiceDescriptionLocation = ServiceDescriptionLocation;
	}
	/**
	 * This class gives us the support to access to the services of the OMS and SF.
	 * Checked that the data contained in the file configuration/Settings.xml the URL
	 * ServiceDescriptionLocation is not empty and is the correct path.
	 * 
	 * @param agent,
	 *            is a BaseAgent, this agent implemented the communication
	 *            protocol
	 * @param thomasAgent, is a OMS or SF.
	 * 
	 */
	THOMASProxy(BaseAgent agent, String thomasAgent) {

		this.agent = agent;
		this.thomasAgent = thomasAgent;
		c = Configuration.getConfiguration();
		this.initialize();

	}
	
	THOMASProxy(CProcessor firstProcessor, String thomasAgent) {
		this.agent = firstProcessor.getMyAgent();
		this.myProcessor = firstProcessor;
		this.thomasAgent = thomasAgent;
		c = Configuration.getConfiguration();
		this.initialize();

	}

	/**
	 * Initializes the structures with the types of services
	 */
	private void initialize()
	{

		//Add type for each service 
		serviceType1.add("LeaveRole");
		serviceType1.add("AcquireRole");
		serviceType1.add("AllocateRole");
		serviceType1.add("RegisterRole");
		serviceType1.add("RegisterUnit");
		serviceType1.add("JointUnit");
		serviceType1.add("DeregisterRole");
		serviceType1.add("DeregisterUnit");
		serviceType1.add("DeallocateRole");
		serviceType1.add("RemoveProvider");
		serviceType1.add("DeregisterService");
		
		
		serviceType2.add("InformRole");
		serviceType2.add("InformUnit");
		serviceType2.add("QuantityMembers");
		
		
		serviceType3.add("InformAgentRole");
		serviceType3.add("InformMembers");
		serviceType3.add("InformUnitRoles");
		serviceType3.add("SearchService");

		
		serviceType4.add("GetService");

		
		serviceType5.add("RegisterService");

	}


	//-------------------------------------------------------------------
	//---------------------------Common methods--------------------------
	//-------------------------------------------------------------------

	/**
	 * This method builds the ACLMessage with the sender, content, protocol and receivers.
	 */
	Object sendInform() throws THOMASException{

		this.reset();

		ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
		requestMsg.setSender(agent.getAid());
		requestMsg.setContent(call);
		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
		requestMsg.setReceiver(new AgentID(thomasAgent));	
	


		logger.info("[QueryAgent]Sms to send: " + requestMsg.getContent());
		logger.info("[QueryAgent]Sending... ");

		initProxyProtocol(requestMsg);

		return returnResult();
	}

	/**
	 * Reset values.
	 */
	private void reset()
	{
		value = new String();
		this.Status = true;

		serviceTypeResult2 = new ArrayList<String>();
		serviceTypeResult3 = new ArrayList<ArrayList<String>>();
		serviceTypeResult4 = "";
		serviceTypeResult5 = new ArrayList<String>();
	
	}

	/**
	 * This function returns the result of service, adds a new object with the result 
	 * a new or showed an error message if the operation is incorrect.
	 * @return
	 */
	private Object returnResult() throws InvalidVisibilityException, THOMASException
	{
		if (!Status)
		{
			//TODO Hacer un swithc, caputurar el error y lanzar la excepción que toque.
			if (value.equals(MessageID.NOT_IN_UNIT_OR_PARENT_UNIT))
				throw new NotInUnitOrParentUnitException(value);
			
			if (value.equals(MessageID.AGENT_NOT_EXISTS))
				throw new AgentNotExistsException(value);
			
			if (value.equals(MessageID.AGENT_NOT_IN_UNIT))
				throw new AgentNotInUnitException(value);
			
			if (value.equals(MessageID.DELETING_TABLE))
				throw new DeletingTableException(value);
			
			if (value.equals(MessageID.EMPTY_PARAMETERS))
				throw new EmptyParametersException(value);
			
			if (value.equals(MessageID.EXCHANGE_BIND))
				throw new ExchangeBindException(value);
			
			if (value.equals(MessageID.EXCHANGE_UNBIND))
				throw new ExchangeUnbindException(value);
			
			if (value.equals(MessageID.ID_UNIT_TYPE_NOT_FOUND))
				throw new IDUnitTypeNotFoundException(value);
			
			if (value.equals(MessageID.INSERTING_TABLE))
				throw new InsertingTableException(value);
			
			if (value.equals(MessageID.INVALID_ACCESSIBILITY))
				throw new InvalidAccessibilityException(value);
			
			if (value.equals(MessageID.INVALID_POSITION))
				throw new InvalidPositionException(value);
			
			if (value.equals(MessageID.INVALID_ROLE_POSITION))
				throw new InvalidRolePositionException(value);
			
			if (value.equals(MessageID.INVALID_UNIT_TYPE))
				throw new InvalidUnitTypeException(value);
			
			if (value.equals(MessageID.INVALID_VISIBILITY))
				throw new InvalidVisibilityException(value);
			
			if (value.equals(MessageID.MYSQL))
				throw new MySQLException(value);
			
			if (value.equals(MessageID.NOT_CREATOR))
				throw new NotCreatorException(value);
			
			if (value.equals(MessageID.NOT_CREATOR_AGENT_IN_UNIT))
				throw new NotCreatorAgentInUnitException(value);
			
			if (value.equals(MessageID.NOT_CREATOR_IN_PARENT_UNIT))
				throw new NotCreatorInParentUnitException(value);
			
			if (value.equals(MessageID.NOT_CREATOR_IN_UNIT))
				throw new NotCreatorInUnitException(value);
			
			if (value.equals(MessageID.NOT_CREATOR_IN_UNIT_OR_PARENT_UNIT))
				throw new NotCreatorInUnitOrParentUnitException(value);
			
			if (value.equals(MessageID.NOT_IN_UNIT_AND_NOT_CREATOR))
				throw new NotInUnitAndNotCreatorException(value);
			
			if (value.equals(MessageID.NOT_IN_UNIT_OR_PARENT_UNIT))
				throw new NotInUnitOrParentUnitException(value);
			
			if (value.equals(MessageID.NOT_MEMBER_OR_CREATOR_IN_UNIT))
				throw new NotMemberOrCreatorInUnitException(value);
			
			if (value.equals(MessageID.NOT_PLAYS_ANY_ROLE))
				throw new NotPlaysAnyRoleException(value);
			
			if (value.equals(MessageID.NOT_PLAYS_ROLE))
				throw new NotPlaysRoleException(value);
			
			if (value.equals(MessageID.NOT_SUPERVISOR_OR_CREATOR_IN_UNIT))
				throw new NotSupervisorOrCreatorInUnitException(value);
			
			if (value.equals(MessageID.ONLY_PLAYS_CREATOR))
				throw new OnlyPlaysCreatorException(value);
			
			if (value.equals(MessageID.PARENT_UNIT_NOT_EXISTS))
				throw new ParentUnitNotExistsException(value);
			
			if (value.equals(MessageID.PLAYING_ROLE))
				throw new PlayingRoleException(value);
			
			if (value.equals(MessageID.ROLE_CONTAINS_NORMS))
				throw new RoleContainsNormsException(value);
			
			if (value.equals(MessageID.ROLE_EXISTS_IN_UNIT))
				throw new RoleExistsInUnitException(value);
			
			if (value.equals(MessageID.ROLE_IN_USE))
				throw new RoleInUseException(value);
			
			if (value.equals(MessageID.ROLE_NOT_EXISTS))
				throw new RoleNotExistsException(value);
			
			if (value.equals(MessageID.SAME_AGENT_NAME))
				throw new SameAgentNameException(value);
			
			if (value.equals(MessageID.SAME_UNIT))
				throw new SameUnitException(value);
			
			if (value.equals(MessageID.SUBUNITS_IN_UNIT))
				throw new SubunitsInUnitException(value);
			
			if (value.equals(MessageID.UNIT_EXISTS))
				throw new UnitExistsException(value);
			
			if (value.equals(MessageID.UNIT_NOT_EXISTS))
				throw new UnitNotExistsException(value);
			
			if (value.equals(MessageID.VIRTUAL_PARENT))
				throw new VirtualParentException(value);
			
			if (value.equals(MessageID.VIRTUAL_UNIT))
				throw new VirtualUnitException(value);
			
			if (value.equals(MessageID.VISIBILITY_ROLE))
				throw new VisibilityRoleException(value);
			
			
		}
			//throw new InvalidVisibilityException(value);/*THOMASException(value);*/
		return result;

	}


	/**
	 * This method initiates a new communication Protocol,If is an QueueAgent or is a CAgent. Each type runs protocol differently.
	 */
	private void initProxyProtocol(ACLMessage requestMsg)
	{
		if (this.myProcessor != null)
		{			
			//Initialization protocol  / conversation request.
			CAgent myAgent = (CAgent)agent;
			THOMASCAgentRequest protocol = new THOMASCAgentRequest(this);
			CFactory talk = protocol.newFactory("THOMASRequest", null, requestMsg, 1, myAgent, 0);
			myAgent.addFactoryAsInitiator(talk);
			myProcessor.createSyncConversation(talk, myAgent.newConversationID());
			myAgent.removeFactory(talk.getName());
		}
		else if (agent instanceof QueueAgent)
		{
			THOMASQAgentRequest test = new THOMASQAgentRequest((QueueAgent)agent, requestMsg, this);

			do {
				test.action();
			} while (!test.finished());

		}
		else if (agent instanceof CAgent)
		{

			//Initialization protocol  / conversation request.
			CAgent myAgent = (CAgent)agent;
			THOMASCAgentRequest protocol = new THOMASCAgentRequest(this);
			CFactory talk = protocol.newFactory("THOMASRequest", null, requestMsg, 1, myAgent, 0);
			myAgent.addFactoryAsInitiator(talk);
			myAgent.startSyncConversation(talk.getName());
			myAgent.removeFactory(talk.getName());
		}
	


	}

	/**
	 * Sets returned value 
	 * @param msg
	 */
	private void setValue(String msg)
	{
		this.result = msg;
		
	}




	/**
	 * This function parses a result string for return a value. 
	 */
	private void extractInfo(ACLMessage msg)
	{

		responseParser.parseResponse(msg.getContent());
		
		serviceName = responseParser.getServiceName();


		//split by state
		if (responseParser.getStatus().equals("Ok"))
		{

			if (serviceType1.contains(serviceName))
			{
				
				this.setValue(responseParser.getDescription());
			
			}
			else if (serviceType2.contains(serviceName))
			{


				serviceTypeResult2 = responseParser.getElementsList();
				
				result = serviceTypeResult2;
			}
			else if (serviceType3.contains(serviceName))
			{

				for (ArrayList<String> al : responseParser.getItemsList())
				{

					serviceTypeResult3.add(al);
				}
				
				result = serviceTypeResult3;

			}
			else if (serviceType4.contains(serviceName))
			{
				
				serviceTypeResult4=responseParser.getSpecification();
				
				result = serviceTypeResult4;
			
			}
			else if (serviceType5.contains(serviceName))
			{
				serviceTypeResult5=new ArrayList<String>();
				serviceTypeResult5.add(responseParser.getDescription());
				serviceTypeResult5.add(responseParser.getSpecification());
				
				result = serviceTypeResult5;
			}

		}
		else
		{

			value = responseParser.getDescription();
			Status = false;
		}


	}


	//***************************************Protocol implementation*******************************************

	/**
	 * This class handles the messages received from the OMS or SF. 
	 */
	static class THOMASQAgentRequest extends FIPARequestInitiator {

		THOMASProxy thomasProxy;

		protected THOMASQAgentRequest(QueueAgent agent, ACLMessage msg, THOMASProxy thomasProxy) {
			super(agent, msg);
			this.thomasProxy = thomasProxy;
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
			this.thomasProxy.setValue("");

		}

		protected void handleInform(ACLMessage msg) {
			logger.info(myAgent.getName() + ":"
					+ msg.getSender().getLocalName()
					+ " has informed me of the status of my request."
					+ " They said : " + msg.getContent());
			this.thomasProxy.extractInfo(msg);

		}

		protected void handleNotUnderstood(ACLMessage msg) {
			logger.error(myAgent.getName() + ":"
					+ msg.getSender().getLocalName()
					+ " has indicated that they didn't understand.");
			this.thomasProxy.setValue("");

		}

		protected void handleOutOfSequence(ACLMessage msg) {
			logger.error(myAgent.getName() + ":"
					+ msg.getSender().getLocalName()
					+ " has send me a message which i wasn't"
					+ " expecting in this conversation");
			this.thomasProxy.setValue("");

		}

		protected void handleFailure(ACLMessage msg) {
			logger.error(myAgent.getName() + ":"
					+ msg.getSender().getLocalName()
					+ " has send me a message which i wasn't"
					+ " expecting in this conversation");
			this.thomasProxy.setValue("");

		}
	}

	/**
	 * This class handles the messages received from the OMS or SF. 
	 */
	class THOMASCAgentRequest extends FIPA_REQUEST_Initiator {

		THOMASProxy thomasProxy;

		public THOMASCAgentRequest(THOMASProxy thomasProxy){
			this.thomasProxy = thomasProxy;
			thomasProxy.setValue("");
		}

		protected void doInform(CProcessor myProcessor, ACLMessage msg) {
			logger.info(myProcessor.getMyAgent().getName() + ":"
					+ msg.getSender().getLocalName()
					+ " has informed me of the status of my request."
					+ " They said : " + msg.getContent());
			this.thomasProxy.extractInfo(msg);
		}

		protected void doAgree(CProcessor myProcessor, ACLMessage msg){
			logger.info(myProcessor.getMyAgent().getName() + ": OOH! "
					+ msg.getSender().getLocalName()
					+ " Has agreed to excute the service!");
		}

		protected void doRefuse(CProcessor myProcessor, ACLMessage msg){
			logger.error(myProcessor.getMyAgent().getName() + ": Oh no! "
					+ msg.getSender().getLocalName()
					+ " has rejected my proposal.");
			this.thomasProxy.setValue("");
		}

		protected void doNotUnderstood(CProcessor myProcessor, ACLMessage msg){
			logger.error(myProcessor.getMyAgent().getName() + ":"
					+ msg.getSender().getLocalName()
					+ " has indicated that they didn't understand.");
			this.thomasProxy.setValue("");
		}

		protected void doFailure(CProcessor myProcessor, ACLMessage msg){
			logger.error(myProcessor.getMyAgent().getName() + ":"
					+ msg.getSender().getLocalName()
					+ " has send me a message which i wasn't"
					+ " expecting in this conversation");
			this.thomasProxy.setValue("");
		}
	}


}
