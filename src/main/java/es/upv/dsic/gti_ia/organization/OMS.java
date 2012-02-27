package es.upv.dsic.gti_ia.organization;



/**
 * OMS.java
 * 
 * @version 2.0
 */





import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CFactory;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_REQUEST_Participant;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;


/**
 * OMS agent is responsible for managing all the request messages from other entities
 * OMS agent follows a FIPA-Request protocol
 */
public class OMS extends CAgent {

	//CAgents
	String msgContent = "";

	Configuration configuration = Configuration.getConfiguration();

	static private OMS oms = null;

	OMSInterface omsInterface = new OMSInterface();

	ResponseParser responseParser = new ResponseParser();

	String separatorToken=" ";
	private String OMSServiceDesciptionLocation = configuration.getOMSServiceDesciptionLocation();
	private static HashMap<String, String> omsServicesURLs=new HashMap<String, String>();
	ServiceTools st=new ServiceTools();
	static Logger logger = Logger.getLogger(OMS.class);


	// URI where the SF service descriptions are located 



	/**
	 * Returns an instance of the agents OMS
	 * @param agent a new Agent ID
	 * @return oms
	 */ 
	static public OMS getOMS(AgentID agent)
	{
		if (oms == null)
		{
			try{
				oms = new OMS(agent);
			}catch(Exception e){logger.error(e);}
		}
		return oms;
	}
	/**
	 * Returns an instance of the agents OMS
	 * @return oms
	 */
	static public OMS getOMS()
	{
		if (oms == null)
		{
			try{
				oms = new OMS(new AgentID("OMS"));
			}catch(Exception e){logger.error(e);}
		}
		return oms;
	}

	/**
	 * Returns an instance of the agents OMS
	 * @param aid new AgentID
	 * @throws Exception
	 */
	private OMS(AgentID aid)throws Exception{
		super(aid);


		omsServicesURLs.put("RegisterUnit", OMSServiceDesciptionLocation+"RegisterUnit?wsdl");
		omsServicesURLs.put("JointUnit", OMSServiceDesciptionLocation+"JointUnit?wsdl");
		omsServicesURLs.put("RegisterRole", OMSServiceDesciptionLocation+"RegisterRole?wsdl");
		omsServicesURLs.put("DeregisterUnit", OMSServiceDesciptionLocation+"DeregisterUnit?wsdl");
		omsServicesURLs.put("DeregisterRole", OMSServiceDesciptionLocation+"DeregisterRole?wsdl");
		omsServicesURLs.put("AcquireRole", OMSServiceDesciptionLocation+"AcquireRole?wsdl");
		omsServicesURLs.put("AllocateRole", OMSServiceDesciptionLocation+"AllocateRole?wsdl");
		omsServicesURLs.put("DeallocateRole", OMSServiceDesciptionLocation+"DeallocateRole?wsdl");
		omsServicesURLs.put("LeaveRole", OMSServiceDesciptionLocation+"LeaveRole?wsdl");
		omsServicesURLs.put("InformUnit", OMSServiceDesciptionLocation+"InformUnit?wsdl");
		omsServicesURLs.put("InformRole", OMSServiceDesciptionLocation+"InformRole?wsdl");
		omsServicesURLs.put("InformAgentRole", OMSServiceDesciptionLocation+"InformAgentRole?wsdl");
		omsServicesURLs.put("InformMembers", OMSServiceDesciptionLocation+"InformMembers?wsdl");
		omsServicesURLs.put("InformUnitRoles", OMSServiceDesciptionLocation+"InformUnitRoles?wsdl");
		omsServicesURLs.put("QuantityMembers", OMSServiceDesciptionLocation+"QuantityMembers?wsdl");

	}

	/**
	 * Changes the URL where the owl's document is
	 * located.
	 * @param OMSUrl
	 */
	public void setOMSServiceDesciptionLocation(String OMSUrl)
	{

		this.OMSServiceDesciptionLocation = OMSUrl; 
	}


	/**
	 * Gets the URL where the owl's document is
	 * located.
	 * @param OMSUrl
	 */
	public String getOMSServiceDesciptionLocation()
	{

		return OMSServiceDesciptionLocation; 
	}


	@Override
	protected void finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {		
	}


	/**
	 * Creates a new binding according to type of playing position into organization
	 * @param The Agent ID represents the name of the agent queue 
	 * @param The Organization ID is part of the binging key
	 * @param The Position Type, this position may be supervisor, subordinate, participant(member) or creator 
	 * @throws THOMASException in order to show the cause of exception uses getContent
	 */
	private void createBinding(String aid, String OrganizationID, String positionType) throws THOMASException
	{


		Map<String, Object> arguments = new HashMap<String, Object>();

		if (positionType.equals("member") || positionType.equals("subordinate"))
		{
			arguments.put("x-match", "all");
			arguments.put("participant", OrganizationID);
		}
		else if (positionType.equals("supervisor"))
		{
			arguments.put("x-match", "any");
			arguments.put("supervisor", OrganizationID);
			arguments.put("participant", OrganizationID);

		}
		else //any other
		{
			throw new THOMASException(positionType +" position does not match with participant, subordinate or supervisor");
		}

		try{
			this.session.exchangeBind(aid, "amq.match",aid + "." + OrganizationID+"."+positionType, arguments);
			this.session.sync();
		}catch(Exception e)
		{
			throw new THOMASException("Exchange bind error: "+ e);
		}
	}

	/**
	 * Deletes binding with binding key represented by the aid, organizationID and positionType 
	 * @param The Agent ID represents the name of the agent queue 
	 * @param The Organization ID is part of the binging key
	 * @param The Position Type, this position may be supervisor, subordinate, participant(member) or creator 
	 * @throws THOMASException in order to show the cause of exception uses getContent

	 */
	private void deleteBinding(String aid, String OrganizationID, String positionType) throws THOMASException
	{
		try
		{
			this.session.exchangeUnbind(aid, "amq.match", aid + "."+OrganizationID+"."+positionType);
			this.session.sync();
		}catch(Exception e)
		{
			throw new THOMASException("Exchange unbind error: "+ e);
		}

	}

	@Override
	protected void execution(CProcessor firstProcessor,
			ACLMessage welcomeMessage) {


		class myFIPA_REQUEST extends FIPA_REQUEST_Participant {

			@Override
			protected String doAction(CProcessor myProcessor) {
				String next = "";	
				String organizationID = "";
				String aidName = "";
				String rol = "";
				String positionType = "";


				try 
				{
					//execute the service


					responseParser.parseResponse(myProcessor.getLastReceivedMessage().getContent());
					String serviceName= responseParser.getServiceName();

					HashMap<String, String> inputs = responseParser.getKeyAndValueList();

					//Extract the parameters needed to create and delete binds
					if (serviceName.equals("AcquireRole") || serviceName.equals("LeaveRole"))
					{


						if (inputs.containsKey("AgentID"))
						{
							aidName = inputs.get("AgentID");
							rol = inputs.get("RoleID");
							organizationID = inputs.get("UnitID");
						}
						else
						{
							rol = inputs.get("RoleID");
							organizationID = inputs.get("UnitID");					
							aidName = myProcessor.getLastReceivedMessage().getSender().name;

						}
						//-------------Inform Role-----------------


						String content = omsInterface.informRole(rol, organizationID, myProcessor.getLastReceivedMessage().getSender().name);

						responseParser.parseResponse(content);

						if (responseParser.getStatus().equals("Ok"))
							positionType = responseParser.getElementsList().get(0);
					}
					else if (serviceName.equals("AllocateRole") || serviceName.equals("DeallocateRole"))
					{

						aidName = inputs.get("TargetAgentID");
						rol = inputs.get("RoleID");
						organizationID = inputs.get("UnitID");	
						//-------------Inform Role-----------------


						String content = omsInterface.informRole(rol, organizationID, myProcessor.getLastReceivedMessage().getSender().name);

						responseParser.parseResponse(content);

						if (responseParser.getStatus().equals("Ok"))
							positionType = responseParser.getElementsList().get(0);

					}

					//Execute the service requested by the agent

					String serviceWSDLURL=omsServicesURLs.get(serviceName);
					HashMap<String,Object> result=st.executeWebService(serviceWSDLURL, inputs);

					String resultContent=(String)result.get("Result");

					responseParser.parseResponse(resultContent);


					//If acquire role is ok. If organization is virtual the agent position is considered creator
					if (responseParser.getStatus().equals("Ok") 
							&& (responseParser.getServiceName().equals("AcquireRole") ||
									responseParser.getServiceName().equals("AllocateRole"))
									&&!organizationID.equals("virtual"))
					{
						//Gets position for the unit

						//< Accessibility - Visibility - Position >

						//-------------Inform Role-----------------



						String content = omsInterface.informRole(rol, organizationID, myProcessor.getLastReceivedMessage().getSender().name);

						responseParser.parseResponse(content);

						positionType = responseParser.getElementsList().get(0);


						//positionType = omsProxy.getAgentPosition(aidName,organizationID, rol, unitType);

						//If position type is member then creates binding for participant
						if (positionType.equals("member"))
						{
							createBinding(aidName, organizationID, "member");
						}//If position type is subordinate then creates binding for subordinate
						else if (positionType.equals("subordinate"))
						{
							createBinding(aidName, organizationID, "subordinate");

						}//If position type is supervisor then creates binding for supervisor
						else if (positionType.equals("supervisor"))
						{
							createBinding(aidName, organizationID, "supervisor");
						}//If not this one in any of the previous positions and it is not creator either
						else if (!positionType.equals("creator"))
						{
							throw new THOMASException("Unknown position "+ positionType);
						}

					}

					//If leave role is ok. If organization is virtual the agent position is considered creator
					if (responseParser.getStatus().equals("Ok")
							&&  (responseParser.getServiceName().equals("LeaveRole") ||  responseParser.getServiceName().equals("DeallocateRole"))
							&& !organizationID.equals("virtual"))
					{




						String content = omsInterface.informAgentRole(aidName, aidName);

						responseParser.parseResponse(content);

						ArrayList<ArrayList<String>> agentsRole = responseParser.getItemsList();


						String unit_aux;
						String role_aux;
						boolean exists_in_unit = false;

						for(ArrayList<String> agentRole : agentsRole)
						{

							role_aux = agentRole.get(0);
							unit_aux = agentRole.get(1);

							//If agent is inside the organization and the rol played is not creator
							if (unit_aux.equals(organizationID) && !role_aux.equals("creator"))
							{

								String contentRole = omsInterface.informRole(rol, organizationID, myProcessor.getLastReceivedMessage().getSender().name);
								responseParser.parseResponse(contentRole);

								String pos = responseParser.getElementsList().get(0);


								if (positionType.equals(pos))//;omsProxy.getAgentPosition(aidName,organizationID, role_aux, unitType)))
									exists_in_unit = true;
							}
						}

						if (!exists_in_unit)
						{

							deleteBinding(aidName, organizationID, positionType);
						} 

					}


					next = "INFORM";
											
					logger.info("[OMS]Before set message content...");						
					
					myProcessor.getLastReceivedMessage().setContent(resultContent);

				}catch(Exception e){
						StringTokenizer tokenInputParams = new StringTokenizer(myProcessor.getLastReceivedMessage().getContent(), separatorToken);
						String serviceURL=tokenInputParams.nextToken().trim();

						String resultXML="<response>\n<serviceName>"+serviceURL+"</serviceName>\n";
						resultXML+="<status>Error</status>\n";
						resultXML+="<result>\n<description>"+e.getMessage()+"</description>\n</result>\n";
						resultXML+="</response>";


						myProcessor.getLastReceivedMessage().setContent(resultXML);

					
					//next = "FAILURE";
				}				
				return next;
			}//

			@Override
			protected void doInform(CProcessor myProcessor, ACLMessage response) {
				ACLMessage lastReceivedMessage = myProcessor.getLastReceivedMessage();
				response.setContent(lastReceivedMessage.getContent());		
			}

			@Override
			protected String doReceiveRequest(CProcessor myProcessor,
					ACLMessage request) {				
				ACLMessage msg = request;
				String next = "";

				if (msg != null) {

					try{					
						HashMap<String,String> inputs=new HashMap<String, String>();
						String serviceName = st.extractServiceContent(msg.getContent(), inputs);

						logger.info("[SF]Service Name: " + serviceName);


						if (omsServicesURLs.containsKey(serviceName)) //if (sfServicesURLs.containsKey(serviceName))
						{

							logger.info("AGREE");
							next = "AGREE";

						} else {

							logger.info("REFUSE");
							next = "REFUSE";
						}

					}catch(Exception e){	                   
						logger.info("EXCEPTION");	                   
						System.out.println(e);
						e.printStackTrace();
						throw new RuntimeException(e.getMessage());
					}						
				}else{	               
					logger.info("NOTUNDERSTOOD");
					next = "NOT_UNDERSTOOD";
				}  

				return next;
			}
		}

		CFactory talk = new myFIPA_REQUEST().newFactory("TALK", null,
				1, firstProcessor.getMyAgent());

		// Finally the factory is setup to answer to incoming messages that
		// can start the participation of the agent in a new conversation
		this.addFactoryAsParticipant(talk);		
	}

} //end OMS Agent