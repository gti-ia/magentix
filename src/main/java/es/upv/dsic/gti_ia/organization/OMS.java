package es.upv.dsic.gti_ia.organization;



/**
 * OMS.java
 * 
 * @version 2.0
 */





import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
	private String SFServiceDesciptionLocation = configuration.getSFServiceDescriptionLocation();

	static Logger logger = Logger.getLogger(OMS.class);




	// Debug
	private static final Boolean DEBUG = true;

	// URI where the SF service descriptions are located 


	private final URI OWL_S_OMS_SERVICES = URI.create(OMSServiceDesciptionLocation);    


	//STRUCTURAL SERVICES

	private  final URI OMS_REGISTERUNIT_PROCESS = URI.create(OWL_S_OMS_SERVICES + "RegisterUnit.owl");
	private  final URI OMS_JOINTUNIT_PROCESS = URI.create(OWL_S_OMS_SERVICES + "JointUnit.owl");
	private  final URI OMS_REGISTERNORM_PROCESS = URI.create(OWL_S_OMS_SERVICES + "RegisterNorm.owl");
	private  final URI OMS_REGISTERROLE_PROCESS = URI.create(OWL_S_OMS_SERVICES + "RegisterRole.owl");
	private  final URI OMS_DEREGISTERUNIT_PROCESS = URI.create(OWL_S_OMS_SERVICES + "DeregisterUnit.owl");
	private  final URI OMS_DEREGISTERNORM_PROCESS = URI.create(OWL_S_OMS_SERVICES + "DeregisterNorm.owl");
	private  final URI OMS_DEREGISTERROLE_PROCESS = URI.create(OWL_S_OMS_SERVICES + "DeregisterRole.owl");
	//DYNAMIC SERVICES
	private  final URI OMS_ACQUIREROLE_PROCESS = URI.create(OWL_S_OMS_SERVICES + "AcquireRole.owl");
	private  final URI OMS_ALLOCATEROLE_PROCESS = URI.create(OWL_S_OMS_SERVICES + "AllocateRole.owl");
	private  final URI OMS_LEAVEROLE_PROCESS = URI.create(OWL_S_OMS_SERVICES + "LeaveRole.owl");
	private  final URI OMS_DEALLOCATEROLE_PROCESS = URI.create(OWL_S_OMS_SERVICES + "DeallocateRole.owl");
	//INFORMATIVE SERVICES
	private  final URI OMS_INFORMUNIT_PROCESS = URI.create(OWL_S_OMS_SERVICES + "InformUnit.owl");
	private  final URI OMS_INFORMNORM_PROCESS = URI.create(OWL_S_OMS_SERVICES + "InformNorm.owl");
	private  final URI OMS_INFORMROLE_PROCESS = URI.create(OWL_S_OMS_SERVICES + "InformRole.owl");
	private  final URI OMS_INFORMAGENTROLE_PROCESS = URI.create(OWL_S_OMS_SERVICES + "InformAgentRole.owl");
	private  final URI OMS_INFORMMEMBERS_PROCESS = URI.create(OWL_S_OMS_SERVICES + "InformMembers.owl");
	private  final URI OMS_INFORMTARGETNORMS_PROCESS = URI.create(OWL_S_OMS_SERVICES + "InformTargetNorms.owl");
	private  final URI OMS_INFORMUNITROLES_PROCESS = URI.create(OWL_S_OMS_SERVICES + "InformUnitRoles.owl");
	private  final URI OMS_QUANTITYMEMBERS_PROCESS = URI.create(OWL_S_OMS_SERVICES + "QuantityMembers.owl");


	URI[]OMSServicesProcess = {OMS_REGISTERUNIT_PROCESS,
			OMS_JOINTUNIT_PROCESS,
			OMS_REGISTERROLE_PROCESS,
			OMS_REGISTERNORM_PROCESS,
			OMS_DEREGISTERUNIT_PROCESS,
			OMS_DEREGISTERROLE_PROCESS, 
			OMS_DEREGISTERNORM_PROCESS,
			OMS_ACQUIREROLE_PROCESS,
			OMS_ALLOCATEROLE_PROCESS,
			OMS_LEAVEROLE_PROCESS,
			OMS_DEALLOCATEROLE_PROCESS,
			OMS_INFORMAGENTROLE_PROCESS,
			OMS_INFORMUNIT_PROCESS,
			OMS_INFORMTARGETNORMS_PROCESS,
			OMS_INFORMMEMBERS_PROCESS,
			OMS_INFORMROLE_PROCESS,
			OMS_INFORMNORM_PROCESS,
			OMS_INFORMUNITROLES_PROCESS,
			OMS_QUANTITYMEMBERS_PROCESS
	};



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
	 * Changes the URL where the owl's document is
	 * located.
	 * @param SFUrl
	 */
	public void setSFServiceDesciptionLocation(String SFUrl)
	{
		this.SFServiceDesciptionLocation = SFUrl;	
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

	/**
	 * Gets the URL where the owl's document is
	 * located.
	 * @param SFUrl
	 */
	public String getSFServiceDesciptionLocation()
	{
		return this.SFServiceDesciptionLocation;	
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


	private String executeWithJavaX(ACLMessage msg){

		//http://localhost:8080/omsservices/OMSservices/owl/owls/AcquireRole.owl RoleID=miembro2 UnitID=plana2
		//

		String inputParams = msg.getContent();
		StringTokenizer tokenInputParams = new StringTokenizer(inputParams, separatorToken);
		String serviceURL=tokenInputParams.nextToken().trim();
		//String serviceURL=sfServicesURLs.get(tokenInputParams.nextToken().trim());
		Oracle oracle = new Oracle();
		oracle.setURLProcess(serviceURL);

		ArrayList<String> processInputs=oracle.getWSDLInputs();

		HashMap<String,String> paramsComplete=new HashMap<String, String>();
		Iterator<String> iterProcessInputs=processInputs.iterator();
		while(iterProcessInputs.hasNext()){
			String in=iterProcessInputs.next().toLowerCase();
			//initialize the inputs
			paramsComplete.put(in, "");
		}


		while(tokenInputParams.hasMoreTokens()){
			String inputToken=tokenInputParams.nextToken().trim();
			StringTokenizer anInputToken=new StringTokenizer(inputToken, "=");
			String in=anInputToken.nextToken().toLowerCase().trim();
			String value="";
			if(anInputToken.hasMoreTokens())
				value=anInputToken.nextToken().trim();
			if(paramsComplete.get(in)!=null){
				paramsComplete.put(in, value);
			}
		}

		if (paramsComplete.get(("agentid")) == null || paramsComplete.get("agentid").equals(""))
		{
			paramsComplete.put("agentid", msg.getSender().name);
		}



		//construct params list with the value of the parameters ordered...
		ArrayList<String> params = new ArrayList<String>();
		Iterator<String> iterInputs=processInputs.iterator();
		while(iterInputs.hasNext()){
			String input=iterInputs.next().toLowerCase();
			params.add(paramsComplete.get(input));
			//System.out.println("inputParamValue: "+paramsComplete.get(input));
		}

		ServiceClient serviceClient = new ServiceClient();
		ArrayList<String> results = serviceClient.invoke(serviceURL, params);

		//String process_localName="SearchServiceProcess"; //TODO no estic segur si es això...
		//String resultStr=process_localName+ "=" + "{";
		String resultStr=serviceURL+"=" + "{";
		for(int i=0;i<results.size();i++){
			resultStr+=serviceURL+"#"+results.get(i);
			if(i!=results.size()-1){
				resultStr+=", ";
			}
			else{
				resultStr+=" }";
			}
		}


		return resultStr;
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

					StringTokenizer tokenInputParams = new StringTokenizer(myProcessor.getLastReceivedMessage().getContent(), separatorToken);
					String serviceURL=tokenInputParams.nextToken().trim();

					//Extract the parameters needed to create and delete binds
					if (serviceURL.contains("AcquireRole") || serviceURL.contains("AllocateRole"))
					{

						if (serviceURL.contains("AcquireRole"))
						{
							if (myProcessor.getLastReceivedMessage().getContent().contains("AgentID"))
							{

								while (tokenInputParams.hasMoreTokens())
								{
									String token = tokenInputParams.nextToken();
									if (token.contains("Agent"))
									{
										aidName =  token.split("=")[1];
									}
									else if (token.contains("Role"))
									{
										rol = token.split("=")[1];	
									}
									else if (token.contains("Unit"))
									{
										organizationID = token.split("=")[1];
									}
								}



							}
							else
							{
								while (tokenInputParams.hasMoreTokens())
								{
									String token = tokenInputParams.nextToken();


									if (token.contains("Role"))
									{
										rol = token.split("=")[1];	
									}
									else if (token.contains("Unit"))
									{
										organizationID = token.split("=")[1];
									}
								}
								aidName = myProcessor.getLastReceivedMessage().getSender().name;

							}
						}
						else if (serviceURL.contains("AllocateRole"))
						{

							while (tokenInputParams.hasMoreTokens())
							{
								String token = tokenInputParams.nextToken();
								if (token.contains("TargetAgentName"))
								{
									aidName =  token.split("=")[1];
								}
								else if (token.contains("Role"))
								{
									rol = token.split("=")[1];	
								}
								else if (token.contains("Unit"))
								{
									organizationID = token.split("=")[1];
								}
							}


						}



					}

					else if (serviceURL.toString().contains("LeaveRole") || serviceURL.contains("DeallocateRole"))
					{


						if (serviceURL.contains("LeaveRole"))
						{
							if (myProcessor.getLastReceivedMessage().getContent().contains("AgentID"))
							{

								while (tokenInputParams.hasMoreTokens())
								{
									String token = tokenInputParams.nextToken();
									if (token.contains("Agent"))
									{
										aidName =  token.split("=")[1];
									}
									else if (token.contains("Role"))
									{
										rol = token.split("=")[1];	
									}
									else if (token.contains("Unit"))
									{
										organizationID = token.split("=")[1];
									}
								}
							}
							else
							{
								while (tokenInputParams.hasMoreTokens())
								{
									String token = tokenInputParams.nextToken();


									if (token.contains("Role"))
									{
										rol = token.split("=")[1];	
									}
									else if (token.contains("Unit"))
									{
										organizationID = token.split("=")[1];
									}
								}
								aidName = myProcessor.getLastReceivedMessage().getSender().name;
							}
						}
						else if (serviceURL.contains("DeallocateRole"))
						{

							while (tokenInputParams.hasMoreTokens())
							{
								String token = tokenInputParams.nextToken();
								if (token.contains("TargetAgentName"))
								{
									aidName =  token.split("=")[1];
								}
								else if (token.contains("Role"))
								{
									rol = token.split("=")[1];	
								}
								else if (token.contains("Unit"))
								{
									organizationID = token.split("=")[1];
								}
							}

						}



						//-------------Inform Role-----------------


						String content = omsInterface.informRole(rol, organizationID, myProcessor.getLastReceivedMessage().getSender().name);

						responseParser.parseResponse(content);

						if (responseParser.getStatus().equals("Ok"))
							positionType = responseParser.getElementsList().get(0);

					}
					//Execute the service requested by the agent

					String resultStr=executeWithJavaX(myProcessor.getLastReceivedMessage());


					//Select result
					String result = resultStr.split("Result=")[1].substring(0, resultStr.split("Result=")[1].length()-2);

					responseParser.parseResponse(result);


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
					if(DEBUG)
					{						
						logger.info("[OMS]Before set message content...");						
					}
					myProcessor.getLastReceivedMessage().setContent(resultStr);

				}catch(Exception e){
					if(DEBUG)
					{	      
						StringTokenizer tokenInputParams = new StringTokenizer(myProcessor.getLastReceivedMessage().getContent(), separatorToken);
						String serviceURL=tokenInputParams.nextToken().trim();
						
						String resultXML="<response>\n<serviceName>"+serviceURL+"</serviceName>\n";
						resultXML+="<status>Error</status>\n";
						resultXML+="<result>\n<description>"+e.getMessage()+"</description>\n</result>\n";
						resultXML+="</response>";
					
						
						myProcessor.getLastReceivedMessage().setContent(resultXML);
						
					}
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
						//read msg content
						StringTokenizer Tok = new StringTokenizer(msg.getContent());						
						//read in the service description
						String token_process = Tok.nextElement().toString();
						Boolean exists=false;											
						for(int i=0;i<OMSServicesProcess.length;i++){		

							if(token_process.equals(OMSServicesProcess[i].toString())){
								exists=true;
							}
						}




						if(exists){							
							logger.info("AGREE");
							next = "AGREE";							
						}else{	                       
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