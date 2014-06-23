package es.upv.dsic.gti_ia.organization;


import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.log4j.Logger;

import es.upv.dsic.gti_ia.architecture.FIPARequestInitiator;
import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.architecture.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CFactory;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_REQUEST_Initiator;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.organization.exception.AgentNotExistsException;
import es.upv.dsic.gti_ia.organization.exception.AgentNotInUnitException;
import es.upv.dsic.gti_ia.organization.exception.AlreadyRegisteredException;
import es.upv.dsic.gti_ia.organization.exception.DBConnectionException;
import es.upv.dsic.gti_ia.organization.exception.DeletingTableException;
import es.upv.dsic.gti_ia.organization.exception.EmptyParametersException;
import es.upv.dsic.gti_ia.organization.exception.ExchangeBindException;
import es.upv.dsic.gti_ia.organization.exception.ExchangeUnbindException;
import es.upv.dsic.gti_ia.organization.exception.ForbiddenNormException;
import es.upv.dsic.gti_ia.organization.exception.IDUnitTypeNotFoundException;
import es.upv.dsic.gti_ia.organization.exception.InsertingTableException;
import es.upv.dsic.gti_ia.organization.exception.InvalidAccessibilityException;
import es.upv.dsic.gti_ia.organization.exception.InvalidDataTypeException;
import es.upv.dsic.gti_ia.organization.exception.InvalidDeonticException;
import es.upv.dsic.gti_ia.organization.exception.InvalidExpressionException;
import es.upv.dsic.gti_ia.organization.exception.InvalidIDException;
import es.upv.dsic.gti_ia.organization.exception.InvalidOMSActionException;
import es.upv.dsic.gti_ia.organization.exception.InvalidParametersException;
import es.upv.dsic.gti_ia.organization.exception.InvalidPositionException;
import es.upv.dsic.gti_ia.organization.exception.InvalidRolePositionException;
import es.upv.dsic.gti_ia.organization.exception.InvalidServiceURLException;
import es.upv.dsic.gti_ia.organization.exception.InvalidTargetTypeException;
import es.upv.dsic.gti_ia.organization.exception.InvalidTargetValueException;
import es.upv.dsic.gti_ia.organization.exception.InvalidUnitTypeException;
import es.upv.dsic.gti_ia.organization.exception.InvalidVisibilityException;
import es.upv.dsic.gti_ia.organization.exception.MySQLException;
import es.upv.dsic.gti_ia.organization.exception.NormExistsInUnitException;
import es.upv.dsic.gti_ia.organization.exception.NormNotExistsException;
import es.upv.dsic.gti_ia.organization.exception.NotCreatorAgentInUnitException;
import es.upv.dsic.gti_ia.organization.exception.NotCreatorException;
import es.upv.dsic.gti_ia.organization.exception.NotCreatorInParentUnitException;
import es.upv.dsic.gti_ia.organization.exception.NotCreatorInUnitException;
import es.upv.dsic.gti_ia.organization.exception.NotCreatorInUnitOrParentUnitException;
import es.upv.dsic.gti_ia.organization.exception.NotInUnitAndNotCreatorException;
import es.upv.dsic.gti_ia.organization.exception.NotInUnitOrParentUnitException;
import es.upv.dsic.gti_ia.organization.exception.NotMemberOrCreatorInUnitException;
import es.upv.dsic.gti_ia.organization.exception.NotPlaysAnyRoleException;
import es.upv.dsic.gti_ia.organization.exception.NotPlaysRoleException;
import es.upv.dsic.gti_ia.organization.exception.NotSupervisorOrCreatorInUnitException;
import es.upv.dsic.gti_ia.organization.exception.NotValidIdentifierException;
import es.upv.dsic.gti_ia.organization.exception.OnlyPlaysCreatorException;
import es.upv.dsic.gti_ia.organization.exception.ParentUnitNotExistsException;
import es.upv.dsic.gti_ia.organization.exception.PlayingRoleException;
import es.upv.dsic.gti_ia.organization.exception.RoleContainsNormsException;
import es.upv.dsic.gti_ia.organization.exception.RoleExistsInUnitException;
import es.upv.dsic.gti_ia.organization.exception.RoleInUseException;
import es.upv.dsic.gti_ia.organization.exception.RoleNotExistsException;
import es.upv.dsic.gti_ia.organization.exception.SameAgentNameException;
import es.upv.dsic.gti_ia.organization.exception.SameUnitException;
import es.upv.dsic.gti_ia.organization.exception.ServiceProfileNotFoundException;
import es.upv.dsic.gti_ia.organization.exception.ServiceURINotFoundException;
import es.upv.dsic.gti_ia.organization.exception.ServicesNotFoundException;
import es.upv.dsic.gti_ia.organization.exception.SubunitsInUnitException;
import es.upv.dsic.gti_ia.organization.exception.THOMASException;
import es.upv.dsic.gti_ia.organization.exception.THOMASMessages;
import es.upv.dsic.gti_ia.organization.exception.UnitExistsException;
import es.upv.dsic.gti_ia.organization.exception.UnitNotExistsException;
import es.upv.dsic.gti_ia.organization.exception.VirtualParentException;
import es.upv.dsic.gti_ia.organization.exception.VirtualUnitException;
import es.upv.dsic.gti_ia.organization.exception.VisibilityRoleException;
import es.upv.dsic.gti_ia.organization.exception.THOMASMessages.MessageID;


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
	private THOMASMessages    l10n;

	
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
	THOMASProxy(BaseAgent agent, String thomasAgent,String ServiceDescriptionLocation) throws Exception {
		if (!(agent instanceof CAgent) && !(agent instanceof QueueAgent))
			throw new Exception("Not allowed action: you should change BaseAgent by CAgent or QueueAgent.");
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
	THOMASProxy(BaseAgent agent, String thomasAgent) throws Exception {
		if (!(agent instanceof CAgent) && !(agent instanceof QueueAgent))
			throw new Exception("Not allowed action: you should change BaseAgent by CAgent or QueueAgent.");
		this.agent = agent;
		this.thomasAgent = thomasAgent;
		c = Configuration.getConfiguration();
		l10n = new THOMASMessages();
		this.initialize();
	}
	
	THOMASProxy(CProcessor firstProcessor, String thomasAgent) {
		this.agent = firstProcessor.getMyAgent();
		this.myProcessor = firstProcessor;
		this.thomasAgent = thomasAgent;
		c = Configuration.getConfiguration();
		l10n = new THOMASMessages();
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
		serviceType1.add("RegisterNorm");
		serviceType1.add("DeregisterNorm");
		serviceType1.add("InformNorm");
		
		
		
		serviceType2.add("InformRole");
		serviceType2.add("InformUnit");
		serviceType2.add("QuantityMembers");
		
		
		serviceType3.add("InformAgentRole");
		serviceType3.add("InformMembers");
		serviceType3.add("InformUnitRoles");
		serviceType3.add("SearchService");
		serviceType3.add("InformTargetNorms");

		
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
	private Object returnResult() throws THOMASException
	{
 
		if (!Status)
		{
			//En caso de que el OMS nos devuelva un error, tenemos que extraer ese mensaje de error y convertirlo a la excepción correspondiente.
			String valueAux = value;
			
			
			
			if (valueAux.contains("'"))
				valueAux = valueAux.replace(valueAux.subSequence(valueAux.indexOf("'"), valueAux.indexOf("'", valueAux.indexOf("'")+1)+1), "{0}");
			if (valueAux.contains("'"))
				valueAux = valueAux.replace(valueAux.subSequence(valueAux.indexOf("'"), valueAux.lastIndexOf("'")+1), "{1}");
//			if (valueAux.contains("'"))
//				valueAux = valueAux.replace(valueAux.subSequence(valueAux.indexOf("'"), valueAux.lastIndexOf("'")+1), "{2}");
//			
			
			valueAux = valueAux.replace("{", "'{");
			valueAux = valueAux.replace("}", "}'");
			if (valueAux.equals(l10n.getMessage(MessageID.AGENT_NOT_EXISTS)))
				throw new AgentNotExistsException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.AGENT_NOT_IN_UNIT)))
				throw new AgentNotInUnitException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.ALREADY_REGISTERED)))
                throw new AlreadyRegisteredException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.DB_CONNECTION)))
                throw new DBConnectionException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.DELETING_TABLE)))
				throw new DeletingTableException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.EMPTY_PARAMETERS)))
				throw new EmptyParametersException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.EXCHANGE_BIND)))
				throw new ExchangeBindException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.EXCHANGE_UNBIND)))
				throw new ExchangeUnbindException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.ID_UNIT_TYPE_NOT_FOUND)))
				throw new IDUnitTypeNotFoundException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.INSERTING_TABLE)))
				throw new InsertingTableException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.INVALID_ACCESSIBILITY)))
				throw new InvalidAccessibilityException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.INVALID_DATA_TYPE)))
                throw new InvalidDataTypeException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.INVALID_PARAMETERS)))
				throw new InvalidParametersException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.INVALID_POSITION)))
				throw new InvalidPositionException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.INVALID_ROLE_POSITION)))
				throw new InvalidRolePositionException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.INVALID_SERVICE_URL)))
                throw new InvalidServiceURLException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.INVALID_UNIT_TYPE)))
				throw new InvalidUnitTypeException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.INVALID_VISIBILITY)))
				throw new InvalidVisibilityException(value);
			
			if (valueAux.contains(l10n.getMessage(MessageID.MYSQL).split(" ")[0]))
				throw new MySQLException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.NORM_EXISTS_IN_UNIT)))
				throw new NormExistsInUnitException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.NORM_NOT_EXISTS)))
				throw new NormNotExistsException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.NOT_CREATOR)))
				throw new NotCreatorException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.NOT_CREATOR_AGENT_IN_UNIT)))//
				throw new NotCreatorAgentInUnitException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.NOT_CREATOR_IN_PARENT_UNIT)))
				throw new NotCreatorInParentUnitException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.NOT_CREATOR_IN_UNIT)))
				throw new NotCreatorInUnitException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.NOT_CREATOR_IN_UNIT_OR_PARENT_UNIT)))
				throw new NotCreatorInUnitOrParentUnitException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.NOT_IN_UNIT_AND_NOT_CREATOR)))
				throw new NotInUnitAndNotCreatorException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.NOT_IN_UNIT_OR_PARENT_UNIT)))
				throw new NotInUnitOrParentUnitException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.NOT_MEMBER_OR_CREATOR_IN_UNIT)))
				throw new NotMemberOrCreatorInUnitException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.NOT_PLAYS_ANY_ROLE)))
				throw new NotPlaysAnyRoleException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.NOT_PLAYS_ROLE)))
				throw new NotPlaysRoleException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.NOT_SUPERVISOR_OR_CREATOR_IN_UNIT)))
				throw new NotSupervisorOrCreatorInUnitException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.ONLY_PLAYS_CREATOR)))
				throw new OnlyPlaysCreatorException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.PARENT_UNIT_NOT_EXISTS)))
				throw new ParentUnitNotExistsException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.PLAYING_ROLE)))
				throw new PlayingRoleException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.ROLE_CONTAINS_NORMS)))
				throw new RoleContainsNormsException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.ROLE_EXISTS_IN_UNIT)))
				throw new RoleExistsInUnitException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.ROLE_IN_USE)))
				throw new RoleInUseException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.ROLE_NOT_EXISTS)))
				throw new RoleNotExistsException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.SAME_AGENT_NAME)))
				throw new SameAgentNameException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.SAME_UNIT)))
				throw new SameUnitException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.SERVICE_PROFILE_NOT_FOUND)))
                throw new ServiceProfileNotFoundException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.SERVICE_URI_NOT_FOUND)))
                throw new ServiceURINotFoundException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.SERVICES_NOT_FOUND)))
                throw new ServicesNotFoundException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.SUBUNITS_IN_UNIT)))
				throw new SubunitsInUnitException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.UNIT_EXISTS)))
				throw new UnitExistsException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.UNIT_NOT_EXISTS)))
				throw new UnitNotExistsException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.VIRTUAL_PARENT)))
				throw new VirtualParentException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.VIRTUAL_UNIT)))
				throw new VirtualUnitException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.VISIBILITY_ROLE)))
				throw new VisibilityRoleException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.NOT_VALID_IDENTIFIER)))
				throw new NotValidIdentifierException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.FORBIDDEN_NORM)))
				throw new ForbiddenNormException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.INVALID_DEONTIC)))
				throw new InvalidDeonticException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.INVALID_ID)))
				throw new InvalidIDException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.INVALID_TARGET_TYPE)))
				throw new InvalidTargetTypeException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.INVALID_TARGET_VALUE)))
				throw new InvalidTargetValueException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.INVALID_OMS_ACTION)))
				throw new InvalidOMSActionException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.INVALID_EXPRESSION)))
				throw new InvalidExpressionException(value);
			
			if (valueAux.equals(l10n.getMessage(MessageID.FORBIDDEN_NORM)))
				throw new ForbiddenNormException(value);
			
			
			
			
			
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
