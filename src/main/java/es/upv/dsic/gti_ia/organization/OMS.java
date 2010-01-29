package es.upv.dsic.gti_ia.organization;



/**
 * OMS.java
 * 
 * @version 2.0
 */





import java.net.URI;
import java.util.*;

import es.upv.dsic.gti_ia.architecture.FIPARequestResponder;
import es.upv.dsic.gti_ia.architecture.MessageTemplate;
import es.upv.dsic.gti_ia.architecture.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.cAgents.*;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;

import org.apache.log4j.Logger;
import org.mindswap.owl.EntityFactory;
import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owls.OWLSFactory;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.process.execution.ProcessExecutionEngine;
import org.mindswap.owls.service.Service;
import org.mindswap.query.ValueMap;


/**
 * OMS agent is responsible for managing all the request messages from other entities
 * OMS agent follows a FIPA-Request protocol
 */
public class OMS extends CAgent {

		//CAgents
		String msgContent = "";
	
		Configuration configuration = Configuration.getConfiguration();
		
		static private OMS oms = null;
		
	 	private String OMSServiceDesciptionLocation = configuration.getOMSServiceDesciptionLocation();
	 	private String SFServiceDesciptionLocation = configuration.getSFServiceDesciptionLocation();
	 	
		static Logger logger = Logger.getLogger(OMS.class);
		
	 	// create a kb
		OWLKnowledgeBase kb = OWLFactory.createKB();
		OWLKnowledgeBase kbaux = OWLFactory.createKB();
		
		// Debug
		private static final Boolean DEBUG = true;
		
		// URI where the SF service descriptions are located 

	    
	    private final URI OWL_S_OMS_SERVICES = URI.create(OMSServiceDesciptionLocation);    
	    private final URI OWL_S_SF_SERVICES = URI.create(SFServiceDesciptionLocation);    
	    
	    // URI of each SF services description parameters are located 
	    private final URI SF_REGISTERPROFILE_PROCESS = URI.create(OWL_S_SF_SERVICES + "RegisterProfileProcess.owl");
	    private final URI SF_REGISTERPROCESS_PROCESS = URI.create(OWL_S_SF_SERVICES + "RegisterProcessProcess.owl");
	    
	    //STRUCTURAL SERVICES
	    private  final URI OMS_REGISTERUNIT_PROFILE = URI.create(OWL_S_OMS_SERVICES + "RegisterUnitProfile.owl");
	    private  final URI OMS_REGISTERUNIT_PROCESS = URI.create(OWL_S_OMS_SERVICES + "RegisterUnitProcess.owl");
	    private  final URI OMS_REGISTERUNIT_ID = URI.create(OWL_S_OMS_SERVICES + "RegisterUnitProfile.owl");
	    private  final URI OMS_REGISTERUNIT_GOAL = URI.create("RegisterUnit");
	    
	    @SuppressWarnings("unused")
	    private  final URI OMS_REGISTERNORM_PROFILE = URI.create(OWL_S_OMS_SERVICES + "RegisterNormProfile.owl");
	    private  final URI OMS_REGISTERNORM_PROCESS = URI.create(OWL_S_OMS_SERVICES + "RegisterNormProcess.owl");
	    @SuppressWarnings("unused")
	    private  final URI OMS_REGISTERNORM_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "RegisterNormGrounding.owl");
	    @SuppressWarnings("unused")
	    private  final URI OMS_REGISTERNORM_ID = URI.create(OWL_S_OMS_SERVICES + "RegisterNormProfile.owl#RegisterNormProfile");
	    @SuppressWarnings("unused")
	    private  final URI OMS_REGISTERNORM_GOAL = URI.create("RegisterNorm");
	    @SuppressWarnings("unused")
	    private  final URI OMS_REGISTERNORM_PROVIDER = URI.create("Provider");    
	    
	    @SuppressWarnings("unused")
	    private  final URI OMS_REGISTERROLE_PROFILE = URI.create(OWL_S_OMS_SERVICES + "RegisterRoleProfile.owl");
	    private  final URI OMS_REGISTERROLE_PROCESS = URI.create(OWL_S_OMS_SERVICES + "RegisterRoleProcess.owl");
	    @SuppressWarnings("unused")
	    private  final URI OMS_REGISTERROLE_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "RegisterRoleGrounding.owl");
	    @SuppressWarnings("unused")
	    private  final URI OMS_REGISTERROLE_ID = URI.create(OWL_S_OMS_SERVICES + "RegisterRoleProfile.owl#RegisterRoleProfile");
	    @SuppressWarnings("unused")
	    private  final URI OMS_REGISTERROLE_GOAL = URI.create("RegisterRole");
	    @SuppressWarnings("unused")
	    private  final URI OMS_REGISTERROLE_PROVIDER = URI.create("Provider"); 
	    @SuppressWarnings("unused")
	    private  final URI OMS_DEREGISTERUNIT_PROFILE = URI.create(OWL_S_OMS_SERVICES + "DeregisterUnitProfile.owl");
	    private  final URI OMS_DEREGISTERUNIT_PROCESS = URI.create(OWL_S_OMS_SERVICES + "DeregisterUnitProcess.owl");
	    @SuppressWarnings("unused")
	    private  final URI OMS_DEREGISTERUNIT_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "DeregisterUnitGrounding.owl");
	    @SuppressWarnings("unused")
	    private  final URI OMS_DEREGISTERUNIT_ID = URI.create(OWL_S_OMS_SERVICES + "DeregisterUnitProfile.owl#DeregisterUnitProfile");
	    @SuppressWarnings("unused")
	    private  final URI OMS_DEREGISTERUNIT_GOAL = URI.create("DeregisterUnit");
	    @SuppressWarnings("unused")
	    private  final URI OMS_DEREGISTERUNIT_PROVIDER = URI.create("Provider");
	    @SuppressWarnings("unused")
	    private  final URI OMS_DEREGISTERNORM_PROFILE = URI.create(OWL_S_OMS_SERVICES + "DeregisterNormProfile.owl");
	    private  final URI OMS_DEREGISTERNORM_PROCESS = URI.create(OWL_S_OMS_SERVICES + "DeregisterNormProcess.owl");
	    @SuppressWarnings("unused")
	    private  final URI OMS_DEREGISTERNORM_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "DeregisterNormGrounding.owl");
	    @SuppressWarnings("unused")
	    private  final URI OMS_DEREGISTERNORM_ID = URI.create(OWL_S_OMS_SERVICES + "DeregisterNormProfile.owl#DeregisterNormProfile");
	    @SuppressWarnings("unused")
	    private  final URI OMS_DEREGISTERNORM_GOAL = URI.create("DeregisterNorm");
	    @SuppressWarnings("unused")
	    private  final URI OMS_DEREGISTERNORM_PROVIDER = URI.create("Provider");    
	    @SuppressWarnings("unused")
	    private  final URI OMS_DEREGISTERROLE_PROFILE = URI.create(OWL_S_OMS_SERVICES + "DeregisterRoleProfile.owl");
	    
	    private  final URI OMS_DEREGISTERROLE_PROCESS = URI.create(OWL_S_OMS_SERVICES + "DeregisterRoleProcess.owl");
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
	    private  final URI OMS_ACQUIREROLE_PROFILE = URI.create(OWL_S_OMS_SERVICES + "AcquireRoleProfile.owl");
	    private  final URI OMS_ACQUIREROLE_PROCESS = URI.create(OWL_S_OMS_SERVICES + "AcquireRoleProcess.owl");
	    @SuppressWarnings("unused")
	    private  final URI OMS_ACQUIREROLE_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "AcquireRoleGrounding.owl");
	    @SuppressWarnings("unused")
	    private  final URI OMS_ACQUIREROLE_ID = URI.create(OWL_S_OMS_SERVICES + "AcquireRoleProfile.owl#AcquireRoleProfile");
	    @SuppressWarnings("unused")
	    private  final URI OMS_ACQUIREROLE_GOAL = URI.create("AcquireRole");
	    @SuppressWarnings("unused")
	    private  final URI OMS_ACQUIREROLE_PROVIDER = URI.create("Provider");
	    @SuppressWarnings("unused")
	    private  final URI OMS_LEAVEROLE_PROFILE = URI.create(OWL_S_OMS_SERVICES + "LeaveRoleProfile.owl");
	    private  final URI OMS_LEAVEROLE_PROCESS = URI.create(OWL_S_OMS_SERVICES + "LeaveRoleProcess.owl");
	    @SuppressWarnings("unused")
	    private  final URI OMS_LEAVEROLE_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "LeaveRoleGrounding.owl");
	    @SuppressWarnings("unused")
	    private  final URI OMS_LEAVEROLE_ID = URI.create(OWL_S_OMS_SERVICES + "LeaveRoleProfile.owl#LeaveRoleProfile");
	    @SuppressWarnings("unused")
	    private  final URI OMS_LEAVEROLE_GOAL = URI.create("LeaveRole");
	    @SuppressWarnings("unused")
	    private  final URI OMS_LEAVEROLE_PROVIDER = URI.create("Provider");
	    @SuppressWarnings("unused")
	    private  final URI OMS_EXPULSE_PROFILE = URI.create(OWL_S_OMS_SERVICES + "ExpulseProfile.owl");
	    private  final URI OMS_EXPULSE_PROCESS = URI.create(OWL_S_OMS_SERVICES + "ExpulseProcess.owl");
	    @SuppressWarnings("unused")
	    private  final URI OMS_EXPULSE_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "ExpulseGrounding.owl");
	    @SuppressWarnings("unused")
	    private  final URI OMS_EXPULSE_ID = URI.create(OWL_S_OMS_SERVICES + "ExpulseProfile.owl#ExpulseProfile");
	    @SuppressWarnings("unused")
	    private  final URI OMS_EXPULSE_GOAL = URI.create("Expulse");
	    @SuppressWarnings("unused")
	    private  final URI OMS_EXPULSE_PROVIDER = URI.create("Provider");
	  //INFORMATIVE SERVICES
	    @SuppressWarnings("unused")
	    private  final URI OMS_INFORMUNIT_PROFILE = URI.create(OWL_S_OMS_SERVICES + "InformUnitProfile.owl");
	    private  final URI OMS_INFORMUNIT_PROCESS = URI.create(OWL_S_OMS_SERVICES + "InformUnitProcess.owl");
	    @SuppressWarnings("unused")
	    private  final URI OMS_INFORMUNIT_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "InformUnitGrounding.owl");
	    @SuppressWarnings("unused")
	    private  final URI OMS_INFORMUNIT_ID = URI.create(OWL_S_OMS_SERVICES + "InformUnitProfile.owl#InformUnitProfile");
	    @SuppressWarnings("unused")
	    private  final URI OMS_INFORMUNIT_GOAL = URI.create("InformUnit");
	    @SuppressWarnings("unused")
	    private  final URI OMS_INFORMUNIT_PROVIDER = URI.create("Provider");   
	    
	    @SuppressWarnings("unused")
	    private  final URI OMS_INFORMROLE_PROFILE = URI.create(OWL_S_OMS_SERVICES + "InformRoleProfile.owl");
	    private  final URI OMS_INFORMROLE_PROCESS = URI.create(OWL_S_OMS_SERVICES + "InformRoleProcess.owl");
	    @SuppressWarnings("unused")
	    private  final URI OMS_INFORMROLE_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "InformRoleGrounding.owl");
	    @SuppressWarnings("unused")
	    private  final URI OMS_INFORMROLE_ID = URI.create(OWL_S_OMS_SERVICES + "InformRoleProfile.owl#InformRoleProfile");
	    @SuppressWarnings("unused")
	    private  final URI OMS_INFORMROLE_GOAL = URI.create("InformRole");
	    @SuppressWarnings("unused")
	    private  final URI OMS_INFORMROLE_PROVIDER = URI.create("Provider");   
	    
	    @SuppressWarnings("unused")
	    private  final URI OMS_INFORMAGENTROLE_PROFILE = URI.create(OWL_S_OMS_SERVICES + "InformAgentRoleProfile.owl");
	    private  final URI OMS_INFORMAGENTROLE_PROCESS = URI.create(OWL_S_OMS_SERVICES + "InformAgentRoleProcess.owl");
	    @SuppressWarnings("unused")
	    private  final URI OMS_INFORMAGENTROLE_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "InformAgentRoleGrounding.owl");
	    @SuppressWarnings("unused")
	    private  final URI OMS_INFORMAGENTROLE_ID = URI.create(OWL_S_OMS_SERVICES + "InformAgentRoleProfile.owl#InformAgentRoleProfile");
	    @SuppressWarnings("unused")
	    private  final URI OMS_INFORMAGENTROLE_GOAL = URI.create("InformAgentRole");
	    @SuppressWarnings("unused")
	    private  final URI OMS_INFORMAGENTROLE_PROVIDER = URI.create("Provider");  
	    @SuppressWarnings("unused")
	    private  final URI OMS_INFORMMEMBERS_PROFILE = URI.create(OWL_S_OMS_SERVICES + "InformMembersProfile.owl");
	    private  final URI OMS_INFORMMEMBERS_PROCESS = URI.create(OWL_S_OMS_SERVICES + "InformMembersProcess.owl");
	    @SuppressWarnings("unused")
	    private  final URI OMS_INFORMMEMBERS_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "InformMembersGrounding.owl");
	    @SuppressWarnings("unused")
	    private  final URI OMS_INFORMMEMBERS_ID = URI.create(OWL_S_OMS_SERVICES + "InformMembersProfile.owl#InformMembersProfile");
	    @SuppressWarnings("unused")
	    private  final URI OMS_INFORMMEMBERS_GOAL = URI.create("InformMembers");
	    @SuppressWarnings("unused")
	    private  final URI OMS_INFORMMEMBERS_PROVIDER = URI.create("Provider"); 
	    
	    @SuppressWarnings("unused")
	    private  final URI OMS_INFORMROLENORMS_PROFILE = URI.create(OWL_S_OMS_SERVICES + "InformRoleNormsProfile.owl");
	    private  final URI OMS_INFORMROLENORMS_PROCESS = URI.create(OWL_S_OMS_SERVICES + "InformRoleNormsProcess.owl");
	    @SuppressWarnings("unused")
	    private  final URI OMS_INFORMROLENORMS_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "InformRoleNormsGrounding.owl");
	    @SuppressWarnings("unused")
	    private  final URI OMS_INFORMROLENORMS_ID = URI.create(OWL_S_OMS_SERVICES + "InformRoleNormsProfile.owl#InformRoleNormsProfile");
	    @SuppressWarnings("unused")
	    private  final URI OMS_INFORMROLENORMS_GOAL = URI.create("InformRoleNorms");
	    @SuppressWarnings("unused")
	    private  final URI OMS_INFORMROLENORMS_PROVIDER = URI.create("Provider"); 
	    @SuppressWarnings("unused")
	    private  final URI OMS_INFORMROLEPROFILES_PROFILE = URI.create(OWL_S_OMS_SERVICES + "InformRoleProfilesProfile.owl");
	    @SuppressWarnings("unused")
	    private  final URI OMS_INFORMROLEPROFILES_PROCESS = URI.create(OWL_S_OMS_SERVICES + "InformRoleProfilesProcess.owl");@SuppressWarnings("unused")
	    private  final URI OMS_INFORMROLEPROFILES_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "InformRoleProfilesGrounding.owl");@SuppressWarnings("unused")
	    private  final URI OMS_INFORMROLEPROFILES_ID = URI.create(OWL_S_OMS_SERVICES + "InformRoleProfilesProfile.owl#InformRoleProfilesProfile");@SuppressWarnings("unused")
	    private  final URI OMS_INFORMROLEPROFILES_GOAL = URI.create("InformRoleProfiles");@SuppressWarnings("unused")
	    private  final URI OMS_INFORMROLEPROFILES_PROVIDER = URI.create("Provider");@SuppressWarnings("unused")
	    
	    private  final URI OMS_INFORMUNITROLES_PROFILE = URI.create(OWL_S_OMS_SERVICES + "InformUnitRolesProfile.owl");
	    private  final URI OMS_INFORMUNITROLES_PROCESS = URI.create(OWL_S_OMS_SERVICES + "InformUnitRolesProcess.owl");@SuppressWarnings("unused")
	    private  final URI OMS_INFORMUNITROLES_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "InformUnitRolesGrounding.owl");@SuppressWarnings("unused")
	    private  final URI OMS_INFORMUNITROLES_ID = URI.create(OWL_S_OMS_SERVICES + "InformUnitRolesProfile.owl#InformUnitRolesProfile");@SuppressWarnings("unused")
	    private  final URI OMS_INFORMUNITROLES_GOAL = URI.create("InformUnitRoles");@SuppressWarnings("unused")
	    private  final URI OMS_INFORMUNITROLES_PROVIDER = URI.create("Provider"); @SuppressWarnings("unused")
	    
	    private  final URI OMS_QUANTITYMEMBERS_PROFILE = URI.create(OWL_S_OMS_SERVICES + "QuantityMembersProfile.owl");
	    private  final URI OMS_QUANTITYMEMBERS_PROCESS = URI.create(OWL_S_OMS_SERVICES + "QuantityMembersProcess.owl");@SuppressWarnings("unused")
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
	    URI[]OMSServicesProcess = {OMS_REGISTERUNIT_PROCESS,OMS_REGISTERROLE_PROCESS,OMS_REGISTERNORM_PROCESS,
					OMS_DEREGISTERUNIT_PROCESS,OMS_DEREGISTERROLE_PROCESS, OMS_DEREGISTERNORM_PROCESS,
						OMS_ACQUIREROLE_PROCESS,OMS_LEAVEROLE_PROCESS,OMS_EXPULSE_PROCESS,OMS_INFORMAGENTROLE_PROCESS,
						OMS_INFORMUNIT_PROCESS,OMS_INFORMUNITROLES_PROCESS,OMS_INFORMUNITROLES_PROCESS,
						OMS_INFORMROLE_PROCESS,OMS_INFORMROLENORMS_PROCESS,OMS_INFORMMEMBERS_PROCESS,
						OMS_INFORMROLE_PROCESS,OMS_INFORMUNITROLES_PROCESS,OMS_QUANTITYMEMBERS_PROCESS
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
    
   protected void setFactories() {
		ACLMessage template = new ACLMessage(ACLMessage.REQUEST);
		CProcessorFactory factory = new CProcessorFactory("Participant", template, 1);
		
		//B
		factory.getCProcessor().registerFirstState(new GenericBeginState("B"));
		
		//W
		factory.getCProcessor().registerState(new WaitState("W",1000000));
		factory.getCProcessor().addTransition("B", "W");
		
		//R
		ReceiveState1 R = new ReceiveState1("R");
		ACLMessage receiveFilter = new ACLMessage(ACLMessage.REQUEST);
		R.setAcceptFilter(receiveFilter);
		factory.getCProcessor().registerState(R);
		factory.getCProcessor().addTransition("W", "R");
		
		//RW
		GenericReceiveState RW = new GenericReceiveState("RW");
		receiveFilter = new ACLMessage(ACLMessage.INFORM);
		receiveFilter.setHeader("purpose", "waitMessage");
		RW.setAcceptFilter(receiveFilter);
		factory.getCProcessor().registerState(RW);
		factory.getCProcessor().addTransition("W", "RW");
		factory.getCProcessor().addTransition("RW", "W");
		
		//S1
		SendState1 S1 = new SendState1("S1");
		ACLMessage sendTemplate = new ACLMessage(ACLMessage.NOT_UNDERSTOOD);
		sendTemplate.setContent("Request message not understood");
		sendTemplate.setSender(getAid());
		sendTemplate.setProtocol("fipa-request");
		S1.setMessageTemplate(sendTemplate);
		factory.getCProcessor().registerState(S1);
		factory.getCProcessor().addTransition("R", "S1");
		
		//S2
		SendState1 S2 = new SendState1("S2");
		sendTemplate = new ACLMessage(ACLMessage.REFUSE);
		sendTemplate.setContent("Request message refused");
		sendTemplate.setSender(getAid());
		sendTemplate.setProtocol("fipa-request");
		S2.setMessageTemplate(sendTemplate);
		factory.getCProcessor().registerState(S2);
		factory.getCProcessor().addTransition("R", "S2");
		
		//S3
		SendState1 S3 = new SendState1("S3");
		sendTemplate = new ACLMessage(ACLMessage.AGREE);
		sendTemplate.setContent("=Agree");
		sendTemplate.setSender(getAid());
		sendTemplate.setProtocol("fipa-request");
		S3.setMessageTemplate(sendTemplate);
		factory.getCProcessor().registerState(S3);
		factory.getCProcessor().addTransition("R", "S3");
		
		//A
		factory.getCProcessor().registerState(new ActionState1("A"));
		factory.getCProcessor().addTransition("S3", "A");
		
		//S4
		SendState1 S4 = new SendState1("S4");
		sendTemplate = new ACLMessage(ACLMessage.FAILURE);
		sendTemplate.setContent("Failure performing the action");
		sendTemplate.setSender(getAid());
		sendTemplate.setProtocol("fipa-request");
		S4.setMessageTemplate(sendTemplate);
		factory.getCProcessor().registerState(S4);
		factory.getCProcessor().addTransition("A", "S4");
		
		//S5
		SendState2 S5 = new SendState2("S5");
		sendTemplate = new ACLMessage(ACLMessage.INFORM);
		sendTemplate.setHeader("inform", "done");
		sendTemplate.setSender(getAid());
		sendTemplate.setProtocol("fipa-request");
		S5.setMessageTemplate(sendTemplate);
		factory.getCProcessor().registerState(S5);
		factory.getCProcessor().addTransition("A", "S5");
		
		//S6
		SendState1 S6 = new SendState1("S6");
		sendTemplate = new ACLMessage(ACLMessage.INFORM);
		sendTemplate.setHeader("inform", "ref");
		sendTemplate.setContent("Action ref");
		sendTemplate.setSender(getAid());
		sendTemplate.setProtocol("fipa-request");
		S6.setMessageTemplate(sendTemplate);
		factory.getCProcessor().registerState(S6);
		factory.getCProcessor().addTransition("A", "S6");
		
		//final
		factory.getCProcessor().registerState(new GenericFinalState("F"));
		factory.getCProcessor().addTransition("S1", "F");
		factory.getCProcessor().addTransition("S2", "F");
		factory.getCProcessor().addTransition("S4", "F");
		factory.getCProcessor().addTransition("S5", "F");
		factory.getCProcessor().addTransition("S6", "F");
		
		//exception states
		factory.getCProcessor().registerState(new GenericCancelState());
		factory.getCProcessor().registerState(new GenericNotAcceptedMessagesState());
		factory.getCProcessor().registerState(new GenericSendingErrorsState());
		factory.getCProcessor().registerState(new GenericTerminatedFatherState());
		
		//attach factory to agent
		this.addFactory(factory);
	}
	
	public class ReceiveState1 extends ReceiveState{

		public ReceiveState1(String n) {
			super(n);
		}

		@Override
		protected String run(CProcessor myProcessor, ACLMessage msg) {
			String next = "";
			System.out.println("Protocol: "+msg.getProtocol());
			
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
						next = "S3";
						
					}else{
                       
						logger.info("REFUSE");
						next = "S2";
					}
					
				}catch(Exception e){
                   
					logger.info("EXCEPTION");
                   
					System.out.println(e);
					e.printStackTrace();
					throw new RuntimeException(e.getMessage());

				}
					
			}else{
               
				logger.info("NOTUNDERSTOOD");
				next = "S1";
			}  
						
			return next;
		}
	}
	
	public class ActionState1 extends ActionState{

		public ActionState1(String n) {
			super(n);
		}

		@Override
		protected String run(CProcessor myProcessor) {
			String next = "";
			
			// create an execution engine
			ProcessExecutionEngine exec = OWLSFactory.createExecutionEngine();
		
			//read msg content
			StringTokenizer Tok = new StringTokenizer(myProcessor.currentMessage.getContent());
			
			//read in the service description
			String token_process = Tok.nextElement().toString();
			if(DEBUG)
			{
				
				logger.info("[OMS]Doc OWL-S: " + token_process);
			}
			
			try{
				Service aService = kb.readService(token_process);		
			
				//get the process for the server
				Process aProcess = aService.getProcess();
				//initialize the input values to be empty
				ValueMap values = new ValueMap();
		
	
				for(int i=0;i<aProcess.getInputs().size();i++){
					if(aProcess.getInputs().inputAt(i).getLocalName().equalsIgnoreCase("AgentID"))
						values.setValue(aProcess.getInputs().inputAt(i),EntityFactory.createDataValue(myProcessor.currentMessage.getSender().toString()));
					else
						values.setValue(aProcess.getInputs().inputAt(i),EntityFactory.createDataValue(""));
				}
				while (Tok.hasMoreElements()) {
					String token = Tok.nextElement().toString();
					for(int i=0;i<aProcess.getInputs().size();i++){
						String paramName = aProcess.getInputs().inputAt(i).getLocalName().toLowerCase();
						if(paramName.equalsIgnoreCase(token.split("=")[0].toLowerCase())){
							if(token.split("=").length >= 2)
								values.setValue(aProcess.getInputs().inputAt(i),EntityFactory.createDataValue(token.split("=")[1]));
							else
								values.setValue(aProcess.getInputs().inputAt(i),EntityFactory.createDataValue(""));
							}
						
					}
				}//end while   
				
				//execute the service
				if(DEBUG)
				{
				
					logger.info("[OMS]Executing... "+values.getValues().toString());
				}
				values = exec.execute(aProcess, values);
				if(DEBUG)
				{
					
					logger.info("[OMS]Values obtained... "+values.toString());
				}
				if(DEBUG) 
				{
					
					logger.info("[OMS]Creating inform message to send...");
				}
			
				next = "S5";
				if(DEBUG)
				{
					
					logger.info("[OMS]Before set message content...");
					
				}
				myProcessor.currentMessage.setContent(aProcess.getLocalName()+"="+values.toString());
				                        
            }catch(Exception e){
            	if(DEBUG)
            	{
            		
            		logger.error(e.toString());
            	}
            	next = "S4";
            }		
			
			return next;
		}
	}
	
	public class SendState1 extends SendState{

		public SendState1(String n) {
			super(n);
		}

		@Override
		protected ACLMessage run(CProcessor myProcessor, ACLMessage lastReceivedMessage) {
			this.messageTemplate.setConversationId(myProcessor.getConversationID());
			this.messageTemplate.setReceiver(lastReceivedMessage.getSender());
			return this.messageTemplate;
		}

		@Override
		protected String getNext(CProcessor myProcessor,
				ACLMessage lastReceivedMessage) {
			String next = "";
			Set<String> transitions = new HashSet<String>();
			transitions = myProcessor.getTransitionTable().getTransitions(this.getName());
			Iterator<String> it = transitions.iterator();
			if (it.hasNext()) {
		        // Get element
		        next = it.next();
		    }
			return next;
		}
	}
	
	public class SendState2 extends SendState{

		public SendState2(String n) {
			super(n);
		}

		@Override
		protected ACLMessage run(CProcessor myProcessor, ACLMessage lastReceivedMessage) {
			this.messageTemplate.setConversationId(myProcessor.getConversationID());
			System.out.println("Soy OMS");
			System.out.println("ConID: "+myProcessor.getConversationID());
			System.out.println("Destino: "+lastReceivedMessage.getSender());
			System.out.println("Perf: "+messageTemplate.getPerformative());
			System.out.println("Content: "+lastReceivedMessage.getContent());
			this.messageTemplate.setReceiver(lastReceivedMessage.getSender());
			this.messageTemplate.setContent(lastReceivedMessage.getContent());
			return this.messageTemplate;
		}

		@Override
		protected String getNext(CProcessor myProcessor,
				ACLMessage lastReceivedMessage) {
			String next = "";
			Set<String> transitions = new HashSet<String>();
			transitions = myProcessor.getTransitionTable().getTransitions(this.getName());
			Iterator<String> it = transitions.iterator();
			if (it.hasNext()) {
		        // Get element
		        next = it.next();
		    }
			return next;
		}
	}

} //end OMS Agent
