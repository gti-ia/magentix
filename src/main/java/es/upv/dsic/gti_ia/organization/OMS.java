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
	private String SFServiceDesciptionLocation = configuration.getSFServiceDesciptionLocation();

	static Logger logger = Logger.getLogger(OMS.class);




	// Debug
	private static final Boolean DEBUG = true;

	// URI where the SF service descriptions are located 


	private final URI OWL_S_OMS_SERVICES = URI.create(OMSServiceDesciptionLocation);    
	//private final URI OWL_S_SF_SERVICES = URI.create(SFServiceDesciptionLocation);    

	// URI of each SF services description parameters are located 
	//private final URI SF_REGISTERPROFILE_PROCESS = URI.create(OWL_S_SF_SERVICES + "RegisterProfileProcess.owl");
	//private final URI SF_REGISTERPROCESS_PROCESS = URI.create(OWL_S_SF_SERVICES + "RegisterProcessProcess.owl");

	//STRUCTURAL SERVICES
	private  final URI OMS_REGISTERUNIT_PROFILE = URI.create(OWL_S_OMS_SERVICES + "RegisterUnit.owl");
	private  final URI OMS_REGISTERUNIT_PROCESS = URI.create(OWL_S_OMS_SERVICES + "RegisterUnit.owl");
	private  final URI OMS_REGISTERUNIT_ID = URI.create(OWL_S_OMS_SERVICES + "RegisterUnit.owl");
	private  final URI OMS_REGISTERUNIT_GOAL = URI.create("RegisterUnit");

	private  final URI OMS_JOINTUNIT_PROFILE = URI.create(OWL_S_OMS_SERVICES + "JointUnit.owl");
	private  final URI OMS_JOINTUNIT_PROCESS = URI.create(OWL_S_OMS_SERVICES + "JointUnit.owl");
	private  final URI OMS_JOINTUNIT_ID = URI.create(OWL_S_OMS_SERVICES + "JointUnit.owl");
	private  final URI OMS_JOINTUNIT_GOAL = URI.create("JointUnit");


	@SuppressWarnings("unused")
	private  final URI OMS_REGISTERNORM_PROFILE = URI.create(OWL_S_OMS_SERVICES + "RegisterNorm.owl");
	private  final URI OMS_REGISTERNORM_PROCESS = URI.create(OWL_S_OMS_SERVICES + "RegisterNorm.owl");
	@SuppressWarnings("unused")
	private  final URI OMS_REGISTERNORM_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "RegisterNorm.owl");
	@SuppressWarnings("unused")
	private  final URI OMS_REGISTERNORM_ID = URI.create(OWL_S_OMS_SERVICES + "RegisterNormProfile.owl#RegisterNormProfile");
	@SuppressWarnings("unused")
	private  final URI OMS_REGISTERNORM_GOAL = URI.create("RegisterNorm");
	@SuppressWarnings("unused")
	private  final URI OMS_REGISTERNORM_PROVIDER = URI.create("Provider");    

	@SuppressWarnings("unused")
	private  final URI OMS_REGISTERROLE_PROFILE = URI.create(OWL_S_OMS_SERVICES + "RegisterRole.owl");
	private  final URI OMS_REGISTERROLE_PROCESS = URI.create(OWL_S_OMS_SERVICES + "RegisterRole.owl");
	@SuppressWarnings("unused")
	private  final URI OMS_REGISTERROLE_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "RegisterRoleGrounding.owl");
	@SuppressWarnings("unused")
	private  final URI OMS_REGISTERROLE_ID = URI.create(OWL_S_OMS_SERVICES + "RegisterRoleProfile.owl#RegisterRole");
	@SuppressWarnings("unused")
	private  final URI OMS_REGISTERROLE_GOAL = URI.create("RegisterRole");
	@SuppressWarnings("unused")
	private  final URI OMS_REGISTERROLE_PROVIDER = URI.create("Provider"); 
	@SuppressWarnings("unused")
	private  final URI OMS_DEREGISTERUNIT_PROFILE = URI.create(OWL_S_OMS_SERVICES + "DeregisterUnit.owl");
	private  final URI OMS_DEREGISTERUNIT_PROCESS = URI.create(OWL_S_OMS_SERVICES + "DeregisterUnit.owl");
	@SuppressWarnings("unused")
	private  final URI OMS_DEREGISTERUNIT_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "DeregisterUnitGrounding.owl");
	@SuppressWarnings("unused")
	private  final URI OMS_DEREGISTERUNIT_ID = URI.create(OWL_S_OMS_SERVICES + "DeregisterUnitProfile.owl#DeregisterUnitProfile");
	@SuppressWarnings("unused")
	private  final URI OMS_DEREGISTERUNIT_GOAL = URI.create("DeregisterUnit");
	@SuppressWarnings("unused")
	private  final URI OMS_DEREGISTERUNIT_PROVIDER = URI.create("Provider");
	@SuppressWarnings("unused")
	private  final URI OMS_DEREGISTERNORM_PROFILE = URI.create(OWL_S_OMS_SERVICES + "DeregisterNorm.owl");
	private  final URI OMS_DEREGISTERNORM_PROCESS = URI.create(OWL_S_OMS_SERVICES + "DeregisterNorm.owl");
	@SuppressWarnings("unused")
	private  final URI OMS_DEREGISTERNORM_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "DeregisterNormGrounding.owl");
	@SuppressWarnings("unused")
	private  final URI OMS_DEREGISTERNORM_ID = URI.create(OWL_S_OMS_SERVICES + "DeregisterNormProfile.owl#DeregisterNormProfile");
	@SuppressWarnings("unused")
	private  final URI OMS_DEREGISTERNORM_GOAL = URI.create("DeregisterNorm");
	@SuppressWarnings("unused")
	private  final URI OMS_DEREGISTERNORM_PROVIDER = URI.create("Provider");    
	@SuppressWarnings("unused")
	private  final URI OMS_DEREGISTERROLE_PROFILE = URI.create(OWL_S_OMS_SERVICES + "DeregisterRole.owl");

	private  final URI OMS_DEREGISTERROLE_PROCESS = URI.create(OWL_S_OMS_SERVICES + "DeregisterRole.owl");
	@SuppressWarnings("unused")
	private  final URI OMS_DEREGISTERROLE_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "DeregisterRoleGrounding.owl");
	@SuppressWarnings("unused")
	private  final URI OMS_DEREGISTERROLE_ID = URI.create(OWL_S_OMS_SERVICES + "DeregisterRoleProfile.owl#DeregisterRoleProfile");
	@SuppressWarnings("unused")
	private  final URI OMS_DEREGISTERROLE_GOAL = URI.create("DeregisterRole");
	@SuppressWarnings("unused")
	private  final URI OMS_DEREGISTERROLE_PROVIDER = URI.create("Provider"); 
	//DYNAMIC SERVICES
	@SuppressWarnings("unused")
	private  final URI OMS_ACQUIREROLE_PROFILE = URI.create(OWL_S_OMS_SERVICES + "AcquireRole.owl");
	private  final URI OMS_ACQUIREROLE_PROCESS = URI.create(OWL_S_OMS_SERVICES + "AcquireRole.owl");
	@SuppressWarnings("unused")
	private  final URI OMS_ACQUIREROLE_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "AcquireRoleGrounding.owl");
	@SuppressWarnings("unused")
	private  final URI OMS_ACQUIREROLE_ID = URI.create(OWL_S_OMS_SERVICES + "AcquireRoleProfile.owl#AcquireRoleProfile");
	@SuppressWarnings("unused")
	private  final URI OMS_ACQUIREROLE_GOAL = URI.create("AcquireRole");
	@SuppressWarnings("unused")
	private  final URI OMS_ACQUIREROLE_PROVIDER = URI.create("Provider");


	private  final URI OMS_ALLOCATEROLE_PROFILE = URI.create(OWL_S_OMS_SERVICES + "AllocateRole.owl");
	private  final URI OMS_ALLOCATEROLE_PROCESS = URI.create(OWL_S_OMS_SERVICES + "AllocateRole.owl");
	@SuppressWarnings("unused")
	private  final URI OMS_ALLOCATEROLE_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "AllocateRoleGrounding.owl");
	@SuppressWarnings("unused")
	private  final URI OMS_ALLOCATEROLE_ID = URI.create(OWL_S_OMS_SERVICES + "AllocateRoleProfile.owl#AllocateRoleProfile");
	@SuppressWarnings("unused")
	private  final URI OMS_ALLOCATEROLE_GOAL = URI.create("AllocateRole");
	@SuppressWarnings("unused")
	private  final URI OMS_ALLOCATEROLE_PROVIDER = URI.create("Provider");


	@SuppressWarnings("unused")
	private  final URI OMS_LEAVEROLE_PROFILE = URI.create(OWL_S_OMS_SERVICES + "LeaveRole.owl");
	private  final URI OMS_LEAVEROLE_PROCESS = URI.create(OWL_S_OMS_SERVICES + "LeaveRole.owl");
	@SuppressWarnings("unused")
	private  final URI OMS_LEAVEROLE_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "LeaveRoleGrounding.owl");
	@SuppressWarnings("unused")
	private  final URI OMS_LEAVEROLE_ID = URI.create(OWL_S_OMS_SERVICES + "LeaveRoleProfile.owl#LeaveRoleProfile");
	@SuppressWarnings("unused")
	private  final URI OMS_LEAVEROLE_GOAL = URI.create("LeaveRole");
	@SuppressWarnings("unused")
	private  final URI OMS_LEAVEROLE_PROVIDER = URI.create("Provider");
	@SuppressWarnings("unused")
	private  final URI OMS_DEALLOCATEROLE_PROFILE = URI.create(OWL_S_OMS_SERVICES + "DeallocateRole.owl");
	private  final URI OMS_DEALLOCATEROLE_PROCESS = URI.create(OWL_S_OMS_SERVICES + "DeallocateRole.owl");
	@SuppressWarnings("unused")
	private  final URI OMS_DEALLOCATEROLE_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "DeallocateRoleGrounding.owl");
	@SuppressWarnings("unused")
	private  final URI OMS_DEALLOCATEROLE_ID = URI.create(OWL_S_OMS_SERVICES + "DeallocateRoleProfile.owl#DeallocateRoleProfile");
	@SuppressWarnings("unused")
	private  final URI OMS_DEALLOCATEROLE_GOAL = URI.create("DeallocateRole");
	@SuppressWarnings("unused")
	private  final URI OMS_DEALLOCATEROLE_PROVIDER = URI.create("Provider");
	//INFORMATIVE SERVICES
	@SuppressWarnings("unused")
	private  final URI OMS_INFORMUNIT_PROFILE = URI.create(OWL_S_OMS_SERVICES + "InformUnit.owl");
	private  final URI OMS_INFORMUNIT_PROCESS = URI.create(OWL_S_OMS_SERVICES + "InformUnit.owl");
	@SuppressWarnings("unused")
	private  final URI OMS_INFORMUNIT_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "InformUnitGrounding.owl");
	@SuppressWarnings("unused")
	private  final URI OMS_INFORMUNIT_ID = URI.create(OWL_S_OMS_SERVICES + "InformUnitProfile.owl#InformUnitProfile");
	@SuppressWarnings("unused")
	private  final URI OMS_INFORMUNIT_GOAL = URI.create("InformUnit");
	@SuppressWarnings("unused")
	private  final URI OMS_INFORMUNIT_PROVIDER = URI.create("Provider");   

	private  final URI OMS_INFORMNORM_PROCESS = URI.create(OWL_S_OMS_SERVICES + "InformNorm.owl");

	private  final URI OMS_INFORMROLE_PROFILE = URI.create(OWL_S_OMS_SERVICES + "InformRole.owl");
	private  final URI OMS_INFORMROLE_PROCESS = URI.create(OWL_S_OMS_SERVICES + "InformRole.owl");
	@SuppressWarnings("unused")
	private  final URI OMS_INFORMROLE_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "InformRoleGrounding.owl");
	@SuppressWarnings("unused")
	private  final URI OMS_INFORMROLE_ID = URI.create(OWL_S_OMS_SERVICES + "InformRoleProfile.owl#InformRoleProfile");
	@SuppressWarnings("unused")
	private  final URI OMS_INFORMROLE_GOAL = URI.create("InformRole");
	@SuppressWarnings("unused")
	private  final URI OMS_INFORMROLE_PROVIDER = URI.create("Provider");   

	@SuppressWarnings("unused")
	private  final URI OMS_INFORMAGENTROLE_PROFILE = URI.create(OWL_S_OMS_SERVICES + "InformAgentRole.owl");
	private  final URI OMS_INFORMAGENTROLE_PROCESS = URI.create(OWL_S_OMS_SERVICES + "InformAgentRole.owl");
	@SuppressWarnings("unused")
	private  final URI OMS_INFORMAGENTROLE_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "InformAgentRoleGrounding.owl");
	@SuppressWarnings("unused")
	private  final URI OMS_INFORMAGENTROLE_ID = URI.create(OWL_S_OMS_SERVICES + "InformAgentRoleProfile.owl#InformAgentRoleProfile");
	@SuppressWarnings("unused")
	private  final URI OMS_INFORMAGENTROLE_GOAL = URI.create("InformAgentRole");
	@SuppressWarnings("unused")
	private  final URI OMS_INFORMAGENTROLE_PROVIDER = URI.create("Provider");  
	@SuppressWarnings("unused")
	private  final URI OMS_INFORMMEMBERS_PROFILE = URI.create(OWL_S_OMS_SERVICES + "InformMembers.owl");
	private  final URI OMS_INFORMMEMBERS_PROCESS = URI.create(OWL_S_OMS_SERVICES + "InformMembers.owl");
	@SuppressWarnings("unused")
	private  final URI OMS_INFORMMEMBERS_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "InformMembersGrounding.owl");
	@SuppressWarnings("unused")
	private  final URI OMS_INFORMMEMBERS_ID = URI.create(OWL_S_OMS_SERVICES + "InformMembersProfile.owl#InformMembersProfile");
	@SuppressWarnings("unused")
	private  final URI OMS_INFORMMEMBERS_GOAL = URI.create("InformMembers");
	@SuppressWarnings("unused")
	private  final URI OMS_INFORMMEMBERS_PROVIDER = URI.create("Provider"); 

	@SuppressWarnings("unused")
	private  final URI OMS_INFORTARGETNORMS_PROFILE = URI.create(OWL_S_OMS_SERVICES + "InformTargetNorms.owl");
	private  final URI OMS_INFORMTARGETNORMS_PROCESS = URI.create(OWL_S_OMS_SERVICES + "InformTargetNorms.owl");
	@SuppressWarnings("unused")
	private  final URI OMS_INFORMTARGETNORMS_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "InformTargetNormsGrounding.owl");
	@SuppressWarnings("unused")
	private  final URI OMS_INFORMTARGETNORMS_ID = URI.create(OWL_S_OMS_SERVICES + "InformTargetNormsProfile.owl#InformTargetNormsProfile");
	@SuppressWarnings("unused")
	private  final URI OMS_INFORMTARGETNORMS_GOAL = URI.create("InformTargetNorms");
	@SuppressWarnings("unused")
	private  final URI OMS_INFORMTARGETNORMS_PROVIDER = URI.create("Provider"); 
	@SuppressWarnings("unused")
	private  final URI OMS_INFORMROLEPROFILES_PROFILE = URI.create(OWL_S_OMS_SERVICES + "InformRoleProfilesProfile.owl");
	@SuppressWarnings("unused")
	private  final URI OMS_INFORMROLEPROFILES_PROCESS = URI.create(OWL_S_OMS_SERVICES + "InformRoleProfilesProcess.owl");@SuppressWarnings("unused")
	private  final URI OMS_INFORMROLEPROFILES_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "InformRoleProfilesGrounding.owl");@SuppressWarnings("unused")
	private  final URI OMS_INFORMROLEPROFILES_ID = URI.create(OWL_S_OMS_SERVICES + "InformRoleProfilesProfile.owl#InformRoleProfilesProfile");@SuppressWarnings("unused")
	private  final URI OMS_INFORMROLEPROFILES_GOAL = URI.create("InformRoleProfiles");@SuppressWarnings("unused")
	private  final URI OMS_INFORMROLEPROFILES_PROVIDER = URI.create("Provider");@SuppressWarnings("unused")

	private  final URI OMS_INFORMUNITROLES_PROFILE = URI.create(OWL_S_OMS_SERVICES + "InformUnitRoles.owl");
	private  final URI OMS_INFORMUNITROLES_PROCESS = URI.create(OWL_S_OMS_SERVICES + "InformUnitRoles.owl");@SuppressWarnings("unused")
	private  final URI OMS_INFORMUNITROLES_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "InformUnitRolesGrounding.owl");@SuppressWarnings("unused")
	private  final URI OMS_INFORMUNITROLES_ID = URI.create(OWL_S_OMS_SERVICES + "InformUnitRolesProfile.owl#InformUnitRolesProfile");@SuppressWarnings("unused")
	private  final URI OMS_INFORMUNITROLES_GOAL = URI.create("InformUnitRoles");@SuppressWarnings("unused")
	private  final URI OMS_INFORMUNITROLES_PROVIDER = URI.create("Provider"); @SuppressWarnings("unused")

	private  final URI OMS_QUANTITYMEMBERS_PROFILE = URI.create(OWL_S_OMS_SERVICES + "QuantityMembers.owl");
	private  final URI OMS_QUANTITYMEMBERS_PROCESS = URI.create(OWL_S_OMS_SERVICES + "QuantityMembers.owl");@SuppressWarnings("unused")
	private  final URI OMS_QUANTITYMEMBERS_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "QuantityMembersGrounding.owl");@SuppressWarnings("unused")
	private  final URI OMS_QUANTITYMEMBERS_ID = URI.create(OWL_S_OMS_SERVICES + "QuantityMembersProfile.owl#QuantityMembersProfile");@SuppressWarnings("unused")
	private  final URI OMS_QUANTITYMEMBERS_GOAL = URI.create("QuantityMembers");@SuppressWarnings("unused")
	private  final URI OMS_QUANTITYMEMBERS_PROVIDER = URI.create("Provider"); 

	// array with the OMS processes related with registration
	URI[] OMSServicesProfiles = {OMS_REGISTERUNIT_PROFILE/*,OMS_REGISTERROLE_PROFILE,OMS_REGISTERNORM_PROFILE,
			   						OMS_DEREGISTERUNIT_PROFILE,OMS_DEREGISTERROLE_PROFILE, OMS_DEREGISTERNORM_PROFILE,
			   						OMS_ACQUIREROLE_PROFILE,OMS_LEAVEROLE_PROFILE,OMS_EXPULSE_PROFILE,
			   						OMS_INFORMUNIT_PROFILE,OMS_INFORMUNITROLES_PROFILE,OMS_INFORMUNITROLES_PROFILE,
			   						OMS_INFORMROLEPROFILES_PROFILE,OMS_INFORMROLENORMS_PROFILE,OMS_INFORMMEMBERS_PROFILE,
			   						OMS_INFORMROLE_PROFILE,OMS_INFORMUNITROLES_PROFILE,OMS_QUANTITYMEMBERS_PROFILE
	 */ };
	// array with OMS service goals	
	URI[] OMSServicesGoals = {OMS_REGISTERUNIT_GOAL/*,OMS_REGISTERROLE_GOAL,OMS_REGISTERNORM_GOAL,
					OMS_DEREGISTERUNIT_GOAL,OMS_DEREGISTERROLE_GOAL, OMS_DEREGISTERNORM_GOAL,
						OMS_ACQUIREROLE_GOAL,OMS_LEAVEROLE_GOAL,OMS_EXPULSE_GOAL,
						OMS_INFORMUNIT_GOAL,OMS_INFORMUNITROLES_GOAL,OMS_INFORMUNITROLES_GOAL,
						OMS_INFORMROLE_GOAL,OMS_INFORMROLENORMS_GOAL,OMS_INFORMMEMBERS_GOAL,
						OMS_INFORMROLE_GOAL,OMS_INFORMUNITROLES_GOAL,OMS_QUANTITYMEMBERS_GOAL*/
	};
	// array with OMS service ID	
	URI[]OMSServicesIDs = {OMS_REGISTERUNIT_ID/*,OMS_REGISTERROLE_ID,OMS_REGISTERNORM_ID,
					OMS_DEREGISTERUNIT_ID,OMS_DEREGISTERROLE_ID, OMS_DEREGISTERNORM_ID,
						OMS_ACQUIREROLE_ID,OMS_LEAVEROLE_ID,OMS_EXPULSE_ID,OMS_INFORMAGENTROLE_ID,
						OMS_INFORMUNIT_ID,OMS_INFORMUNITROLES_ID,OMS_INFORMUNITROLES_ID,
						OMS_INFORMROLE_ID,OMS_INFORMROLENORMS_ID,OMS_INFORMMEMBERS_ID,
						OMS_INFORMROLE_ID,OMS_INFORMUNITROLES_ID,OMS_QUANTITYMEMBERS_ID*/};
	// array with OMS service processes
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
	 * Change the URL where the owl's document is
	 * located.
	 * @param OMSUrl
	 */
	public void setOMSServiceDesciptionLocation(String OMSUrl)
	{

		this.OMSServiceDesciptionLocation = OMSUrl; 
	}

	/**
	 * Change the URL where the owl's document is
	 * located.
	 * @param SFUrl
	 */
	public void setSFServiceDesciptionLocation(String SFUrl)
	{
		this.SFServiceDesciptionLocation = SFUrl;	
	}

	/**
	 * Get the URL where the owl's document is
	 * located.
	 * @param OMSUrl
	 */
	public String getOMSServiceDesciptionLocation()
	{

		return OMSServiceDesciptionLocation; 
	}

	/**
	 * Get the URL where the owl's document is
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


	
//	private Document stringToDocument(String msg)
//	{
//		SAXBuilder builder = new SAXBuilder();
//		
//		String content = msg.substring(msg.indexOf("<response>"));
//
//		try {
//			Document doc = builder.build(new StringReader(content));
//
//			return doc;
//		
//
//		} catch (JDOMException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//			return null;
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//			return null;
//		}
//
//	}

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
				System.out.println("inputParamName: "+in+" value: "+value);
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
				String unitType = "";
				
				try
				{
					//execute the service
			
				StringTokenizer tokenInputParams = new StringTokenizer(myProcessor.getLastReceivedMessage().getContent(), separatorToken);
				String serviceURL=tokenInputParams.nextToken().trim();

					
//TODO
				/*
					//Extract the parameters needed to create and delete binds
					if (serviceURL.contains("AcquireRole"))
					{


						rol = values.getValues().toString().replace("[", "").split(",")[0].trim();
						aidName = values.getValues().toString().split(",")[1].trim();
						organizationID = values.getValues().toString().replace("]", "").split(",")[2].trim();

						

						String content = omsInterface.informUnit(organizationID, aidName);
						
						responseParser.parseResponse(content);

						if (responseParser.getElementsList().size() != 0)
							unitType = responseParser.getElementsList().get(0);

					}

					else if (aProcess.toString().contains("LeaveRoleProcess"))
					{


						aidName = values.getValues().toString().replace("[", "").split(",")[0].trim();
						rol = values.getValues().toString().split(",")[1].trim();
						organizationID = values.getValues().toString().replace("]", "").split(",")[2].trim();

						
						String content = omsInterface.informUnit(organizationID, aidName);
						
						responseParser.parseResponse(content);
						
						if (responseParser.getElementsList().size() != 0)
							unitType = responseParser.getElementsList().get(0);

						//-------------Inform Role-----------------

						
						content = omsInterface.informRole(rol, organizationID, aidName);
						
						responseParser.parseResponse(content);
						
						
						positionType = responseParser.getElementsList().get(0);

					}
					//Execute the service requested by the agent

*/
					String resultStr=executeWithJavaX(myProcessor.getLastReceivedMessage());


//					if(DEBUG)
//					{						
//						logger.info("[OMS]Values obtained... "+values.toString());
//
//					}
//					if(DEBUG) 
//					{						
//						logger.info("[OMS]Creating inform message to send...");
//					}
/*
					//TODO Probar cuando los servicios informativos esten testeados

					//If acquire role is ok. If organization is virtual the agent position is considered creator
					if (values.toString().contains("acquired") && !organizationID.equals("virtual"))
					{
						//Gets position for the unit

						//< Accessibility - Visibility - Position >

						//-------------Inform Role-----------------

	
						
						String content = omsInterface.informRole(rol, organizationID, aidName);
						
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
					//TODO Comentar hasta que este revisado los servicios informativos.

					//If leave role is ok. If organization is virtual the agent position is considered creator
					if (values.toString().contains("left") && !organizationID.equals("virtual"))
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
					
								String contentRole = omsInterface.informRole(rol, organizationID, aidName);
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
					*/
					next = "INFORM";
					if(DEBUG)
					{						
						logger.info("[OMS]Before set message content...");						
					}
					myProcessor.getLastReceivedMessage().setContent(resultStr);

				}catch(Exception e){
					if(DEBUG)
					{	            		
						logger.error(e.toString());
					}
					next = "FAILURE";
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