package es.upv.dsic.gti_ia.organization;


import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

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


/**
 * This class is the parent class for the OMS and SF proxys.
 */
public class THOMASProxy {



	//-------------------------------------------------------------------
	//---------------------------VARIABLES-------------------------------
	//-------------------------------------------------------------------

	static Logger logger = Logger.getLogger(THOMASProxy.class);


	BaseAgent agent = null;
	Oracle oracle;

	Configuration c;

	String call,thomasAgent,clientProvider,serviceName,ErrorValue,status,ServiceDescriptionLocation;
	String value = ""; //Returned value

	int Quantity;
	String[] elements;

	Hashtable<AgentID, String> agents = new Hashtable<AgentID, String>();
	ArrayList<ArrayList<String>> listResults = new ArrayList<ArrayList<String>>();
	ResponseParser responseParser = new ResponseParser();

	ProcessDescription processDescripcion;
	ProfileDescription profileDescription;

	boolean isgenericSerice = false;

	private Hashtable<String, String> genericServiceList = new Hashtable<String, String>();
	private String[] agentGetProcess;
	private boolean Status = true;

	private ArrayList<String> serviceType1 = new ArrayList<String>();//This type returns a String
	private ArrayList<String> serviceType2 = new ArrayList<String>();//This type returns a Array of strings
	private ArrayList<String> serviceType3 = new ArrayList<String>();//This type returns a Integer
	private ArrayList<String> serviceType4 = new ArrayList<String>();//This type returns a Hastable<AgentID, String>






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

	/**
	 * Adds a new element to list
	 * @param element
	 */
	private void addElementToList(ArrayList<String> element) {
		this.listResults.add(element);
	}

	/**
	 * Sets quantity 
	 * @param Quantity
	 */
	private void setQuantity(int Quantity) {
		this.Quantity = Quantity;
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
		serviceType1.add("RegisterNorm");
		serviceType1.add("RegisterRole");
		serviceType1.add("RegisterUnit");
		serviceType1.add("JointUnit");
		serviceType1.add("DeregisterNorm");
		serviceType1.add("DeregisterRole");
		serviceType1.add("DeregisterUnit");
		serviceType1.add("DeallocateRole");
		serviceType1.add("RemoveProvider");
		serviceType1.add("ModifyProcess");
		serviceType1.add("ModifyProfile");
		serviceType1.add("DeregisterProfile");
		serviceType1.add("GetProfile");
		serviceType1.add("RegisterProfile");
		serviceType1.add("RegisterProcess");



		serviceType2.add("InformAgentRole");
		serviceType2.add("InformMembers");
		serviceType2.add("InformUnitRoles");
		serviceType2.add("InformRole");
		serviceType2.add("InformUnit");
		serviceType2.add("InformNorm");
		serviceType2.add("InformTargetNorms");
		serviceType2.add("SearchService");

		serviceType3.add("QuantityMembers");

		serviceType4.add("GetProcess");




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
		if (isgenericSerice)//If is a genericService, Receiver is the provider to service.
		{
			requestMsg.setReceiver(new AgentID(clientProvider));
		}
		else
		{
			requestMsg.setReceiver(new AgentID(thomasAgent));	
		}


		logger.info("[QueryAgent]Sms to send: " + requestMsg.getContent());
		logger.info("[QueryAgent]Sending... ");

		initProxyProtocol(requestMsg);

		return returnResult();
	}

	/**
	 * Clear values.
	 */
	private void reset()
	{
		this.setValue("");	
		this.Status = true;
		this.listResults.clear();
		this.agents.clear();
		ErrorValue = "";
		status = "";	
	}

	/**
	 * This function returns the result of service, adds a new object with the result 
	 * a new or showed an error message if the operation is incorrect.
	 * @return
	 */
	private Object returnResult() throws THOMASException
	{

		//Services that return a String.
		if (serviceType1.contains(serviceName))
		{
			if (!Status)
				throw new THOMASException(value);

			return  value;


		}//Services that return a ArrayList<String>()
		else if (serviceType2.contains(serviceName))
		{

			if (!Status) 
				throw new THOMASException(value);

			return new ArrayList<ArrayList<String>>(this.listResults);

		}//Services that return a Integer.
		else  if (serviceType3.contains(serviceName))
		{
			if (!Status)
				throw new THOMASException(value);

			return this.Quantity;
		}//Services that return a Hashtable
		else  if (serviceType4.contains(serviceName))
		{
			if (!Status)
				throw new THOMASException(value);
			return this.agents;

		}
		else //If types is a genericService.  
		{
			if (!Status)
				throw new THOMASException(value);

			return genericServiceList;

		}

	}


	/**
	 * This method initiates a new communication Protocol,If is an QueueAgent or is a CAgent. Each type runs protocol differently.
	 */
	private void initProxyProtocol(ACLMessage requestMsg)
	{
		if (agent instanceof QueueAgent)
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
		this.value = msg;

	}

	/**
	 * Adds the Id profile when the search service is calls
	 * @param id
	 */
	private void addIDSearchService(ArrayList<String> id) {

		this.listResults.add(id);

	}


	//	public Document string2DOM(String s)
	//
	//	{
	//
	//		Document tmpX=null;
	//
	//		DocumentBuilder builder = null;
	//
	//		try{
	//
	//			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	//
	//		}catch(javax.xml.parsers.ParserConfigurationException error){
	//
	//			return null;
	//		}
	//
	//		try{
	//
	//			tmpX=builder.parse(new ByteArrayInputStream(s.getBytes()));
	//
	//		}catch(org.xml.sax.SAXException error){
	//
	//
	//			return null;
	//
	//		}catch(IOException error){
	//
	//
	//			return null;
	//
	//		}
	//
	//		return tmpX;
	//
	//	}

	/**
	 * This function parses a result string for return a value. 
	 */
	private void extractInfo(ACLMessage msg)
	{


		//-------------------------------------------------------------------
		//---------------------------SF parsing------------------------------
		//-------------------------------------------------------------------


		if (thomasAgent.equals("SF"))
		{

			String arg1 ="";

			//if first argument is a DeregisterProfileProcess, not extract the arg1
			if (!serviceName.equals("DeregisterProfileProcess")
					&& !serviceName.equals("ModifyProfileProcess")
					&& !serviceName.equals("ModifyProcessProcess")
					&& !serviceName.equals("GetProcessProcess")
					&& !serviceName.equals("RemoveProviderProcess")) {
				arg1 = msg.getContent().substring(
						msg.getContent().indexOf("=") + 1,
						msg.getContent().length());
				arg1 = arg1.substring(arg1.indexOf("=") + 1, arg1.indexOf(","));
			}

			//Second argument
			String arg2 = msg.getContent();
			arg2 = arg2.substring((arg2.lastIndexOf("=")) + 1, arg2.length() - 1);



			if (serviceName.equals("RegisterProcessProcess")) {
				if (arg2.equals("1")) {
					this.processDescripcion.setImplementationID(arg1);
					this.setValue(arg1);

				} else {
					this.Status = false;
					this.setValue(arg1);
				}

			}


			if (serviceName.equals("GetProfileProcess")) {
				arg2 = msg.getContent().substring(
						msg.getContent().indexOf(",") + 1,
						msg.getContent().length());
				arg2 = arg2.substring(arg2.indexOf("=") + 1, arg2.indexOf(","));

				if (arg2.equals("1"))// Is correct
				{
					this.setValue(arg1);
				} else {
					this.Status = false;
					this.setValue(arg1);
				}

			}


			if (serviceName.equals("DeregisterProfileProcess")) {

				if (arg2.equals("1"))// Is correct
				{
					this.setValue(arg2);
				} else //Not right
				{
					this.Status = false;
					this.setValue("The error is caused for there are process associated with the profile or the id profile not exist.");
				}

			}


			if (serviceName.equals("GetProcessProcess")) {
				agentGetProcess = null;

				if (arg2.equals("0")) {
					this.Status = false;
					this.setValue(arg1);

				} else {
					//Extract the providers of the message content.
					String base = msg.getContent().substring(msg.getContent().indexOf("=")+1);
					for (String s : base.split(","))
					{
						if (s.contains("@") && s.contains("="))
						{
							arg1 = s.substring(
									s.indexOf("=") + 1,
									s.length());


							String arg_aux = arg1.substring(arg1.indexOf(" ") + 1, arg1
									.length());

							//arg1 = s.substring(0, arg1.indexOf(" "));
							arg1 = arg1.substring(arg1.indexOf("-") + 1, arg1.indexOf(" "));


							//We have control if exist zero, one o more providers.
							if (!arg1.equals("null"))//Exist any provider
							{

								this.agents.put(new AgentID(arg1), arg_aux);
							}

						}
						else if (s.contains("@"))
						{


							int index = s.indexOf(" ", 1);

							String arg_aux = s.substring(index, s
									.length());



							arg1 = s.substring(1, index);

							arg1 = arg1.substring(arg1.indexOf("-") + 1, arg1.length());


							//We have control if exist zero, one o more providers.
							if (!arg1.equals("null"))//Exist any provider
							{

								this.agents.put(new AgentID(arg1), arg_aux);
							}

						}
					}

				}

			}


			if (serviceName.equals("SearchServiceProcess")) {

				agentGetProcess = null;

				if (arg2.equals("1")) {
					this.agentGetProcess = arg1.split(",");

					for (String a : agentGetProcess) {
						a = a.substring(0,a.indexOf(" "));
						//TODO			this.addIDSearchService(a);
					}

				} else
				{
					this.Status = false;

					this.setValue(arg1);
				}

			}

			if (serviceName.equals("RegisterProfileProcess")) {
				if (arg1.equals("1")) {
					this.profileDescription.setServiceID(arg2);
					this.setValue(arg2);


				} else {
					this.Status = false;
					this.setValue(arg2);
				}

			}

			if (serviceName.equals("ModifyProfileProcess")) {
				if (arg2.equals("1"))
				{
					this.setValue(arg2);

				} else if (arg2.equals("0"))
				{
					this.Status = false;
					this.setValue(arg2);
				} else
				{

					this.setValue(arg1);
				}

			}


			if (serviceName.equals("ModifyProcessProcess")) {
				this.setValue(arg2);

			}

			if (serviceName.equals("RemoveProviderProcess")) {
				if (arg2.equals("1"))
					this.setValue(arg2);
				else
					this.setValue("Service process id does not exist");
			}


			if (this.isgenericSerice) {

				//If not is a OMS or SF services, according to outputs extract the results.
				String sub = msg.getContent().substring(
						msg.getContent().indexOf("=") + 1);
				String[] aux = sub.split(",");

				for (String output : oracle.getOutputs()) {
					for (int i = 0; i < aux.length; i++) {
						String a = aux[i];

						if (i != (aux.length - 1))
						{
							if (a.substring(a.indexOf("#") + 1, a.indexOf("="))
									.equals(output)) {
								this.genericServiceList.put(output, a
										.substring(a.indexOf("=") + 1));
							}
						} else {
							if (a.substring(a.indexOf("#") + 1, a.indexOf("="))
									.equals(output)) {
								this.genericServiceList.put(output, a.substring(
										a.indexOf("=") + 1, (a.length() - 1)));

							}

						}
					}
				}

				this.isgenericSerice = false;

			}
		}
		else
		{



			//-------------------------------------------------------------------
			//---------------------------OMS parsing------------------------------
			//-------------------------------------------------------------------


		
			String content = msg.getContent().substring(msg.getContent().indexOf("<response>"),msg.getContent().length()-1 );


			responseParser.parseResponse(content);


			//split by state
			if (responseParser.getStatus().equals("Ok"))
			{

				if (serviceType1.contains(responseParser.getServiceName()))
				{

					value = responseParser.getDescription();
				}
				else if (responseParser.getServiceName().equals("InformRole") || responseParser.getServiceName().equals("InformUnit")
						|| responseParser.getServiceName().equals("QuantityMembers"))
				{
					this.addElementToList(responseParser.getElementsList());
				}
				else if (responseParser.getServiceName().equals("InformUnitRoles") || responseParser.getServiceName().equals("InformMembers")
						|| responseParser.getServiceName().equals("InformAgentRole"))
				{
					
					for (ArrayList<String> al : responseParser.getItemsList())
					{
						this.addElementToList(al);
					}

				}
				
			}
			else
			{

				value = responseParser.getDescription();
				Status = false;
			}



			/*

			if (serviceName.equals("InformUnitProcess")) {

				String arg;
				String argAux;


				arg = msg.getContent().substring(
						msg.getContent().indexOf("ParentID") + 9,
						msg.getContent().indexOf(","));
				this.addElementToList(arg);

				argAux = msg.getContent().substring(msg.getContent().indexOf("UnitType"), msg.getContent()
						.length());
				arg = argAux.substring(argAux.indexOf("UnitType") + 9, argAux
						.indexOf("}"));
				this.addElementToList(arg);

			}

			//We extract status
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

			if (serviceName.equals("InformAgentRoleProcess")
					|| serviceName.equals("InformMembersProcess")
					|| serviceName.equals("InformTargetNormsProcess")
					|| serviceName.equals("InformNormProcess")
					|| serviceName.equals("InformRoleProcess")
					|| serviceName.equals("InformUnitRolesProcess")) {


				String argAux;

				if (serviceName.equals("InformAgentRoleProcess")
						|| serviceName.equals("InformMembersProcess")) {

					if (!status.equals("Ok")) {
						this.Status = false;
						this.addElementToList("EMPTY");
					} else {


						if (!msg.getContent().contains("<EntityRoleList/>"))
						{
							String arg3 = msg.getContent().substring(
									msg.getContent().indexOf("=< ")+1,
									msg.getContent().indexOf("-,"));

							StringTokenizer st = new StringTokenizer(arg3, "-");

							do{
								this.addElementToList(st.nextToken());
							}while(st.hasMoreTokens());
						}
						//						if (!arg3.contains("[]"))
						//						{
						//							arg3 = arg3.substring(arg3.indexOf("("), arg3.indexOf("]"));
						//
						//							elements = arg3.split(",");
						//
						//							int paridad = 0;
						//
						//							for (String e : elements) {
						//								if ((paridad % 2) == 0)// is pair
						//								{
						//									argAux = e
						//									.substring(e.indexOf("(") + 1, e.length());
						//
						//								} else {
						//									argAux = e.substring(0, e.indexOf(")"));
						//								}
						//								this.addElementToList(argAux);
						//								paridad++;
						//							}
						//						}

					}
				} else {
					if (!status.equals("Ok")) {
						this.Status = false;
						this.addElementToList("EMPTY");
					} else {
						if (serviceName.equals("InformUnitRolesProcess"))
						{
							if (!msg.getContent().contains("<RoleList/>"))
							{
								String arg = msg.getContent().substring(msg.getContent().indexOf("=<")+1, msg.getContent().indexOf("-,"));

								StringTokenizer st = new StringTokenizer(arg, "-");

								while(st.hasMoreTokens())
								{
									this.addElementToList(st.nextToken());

								}
							}


						}
						else
						{
							if (serviceName.equals("InformRoleProcess"))
							{
								String arg = msg.getContent().substring(msg.getContent().indexOf("=<")+1, msg.getContent().indexOf(">,")+1);

								StringTokenizer st = new StringTokenizer(arg, "<>, ");

								while(st.hasMoreTokens())
								{
									this.addElementToList(st.nextToken());

								}


							}
							else
							{
								String arg3 = msg.getContent().substring(
										msg.getContent().indexOf("[") + 1,
										msg.getContent().indexOf("]"));

								elements = arg3.split(",");

								for (String e : elements) {
									this.addElementToList(e.trim());

								}
							}
						}

					}

				}
			}

			ErrorValue = msg.getContent();

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

			if (status.contains("Ok")) {

				if (serviceName.equals("QuantityMembersProcess")) {

					String quantity = msg.getContent().substring(
							msg.getContent().indexOf("Quantity=") + 9,
							msg.getContent().indexOf("}"));
					this.setQuantity(Integer.parseInt(quantity));
				}


				this.setValue(status);
			} else {
				this.Status = false;
				this.setValue(status + " " + ErrorValue);
			}
			 */
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
