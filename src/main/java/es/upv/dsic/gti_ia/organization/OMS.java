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
import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.architecture.FIPANames.InteractionProtocol;
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
public class OMS extends QueueAgent {

	
		Configuration configuration = new Configuration();
		
	 	private String OMSServiceDesciptionLocation = configuration.OMSServiceDesciptionLocation;
	 	private String SFServiceDesciptionLocation = configuration.SFServiceDesciptionLocation;
	 	
		static Logger logger = Logger.getLogger(OMS.class);
		
	 	private Monitor mon  = new Monitor();
	// create a kb
		OWLKnowledgeBase kb = OWLFactory.createKB();
		OWLKnowledgeBase kbaux = OWLFactory.createKB();
		
		// Debug
		private static final Boolean DEBUG = true;
		
		// URI where the SF service descriptions are located 

	    
	    public final URI OWL_S_OMS_SERVICES = URI.create(OMSServiceDesciptionLocation);    
	    public final URI OWL_S_SF_SERVICES = URI.create(SFServiceDesciptionLocation);    
	    
	    // URI of each SF services description parameters are located 
	    public final URI SF_REGISTERPROFILE_PROCESS = URI.create(OWL_S_SF_SERVICES + "RegisterProfileProcess.owl");
	    public final URI SF_REGISTERPROCESS_PROCESS = URI.create(OWL_S_SF_SERVICES + "RegisterProcessProcess.owl");
	    
	    //STRUCTURAL SERVICES
	    public  final URI OMS_REGISTERUNIT_PROFILE = URI.create(OWL_S_OMS_SERVICES + "RegisterUnitProfile.owl");
	    public  final URI OMS_REGISTERUNIT_PROCESS = URI.create(OWL_S_OMS_SERVICES + "RegisterUnitProcess.owl");
	    public  final URI OMS_REGISTERUNIT_ID = URI.create(OWL_S_OMS_SERVICES + "RegisterUnitProfile.owl");
	    public  final URI OMS_REGISTERUNIT_GOAL = URI.create("RegisterUnit");
	    
	    public  final URI OMS_REGISTERNORM_PROFILE = URI.create(OWL_S_OMS_SERVICES + "RegisterNormProfile.owl");
	    public  final URI OMS_REGISTERNORM_PROCESS = URI.create(OWL_S_OMS_SERVICES + "RegisterNormProcess.owl");
	    public  final URI OMS_REGISTERNORM_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "RegisterNormGrounding.owl");
	    public  final URI OMS_REGISTERNORM_ID = URI.create(OWL_S_OMS_SERVICES + "RegisterNormProfile.owl#RegisterNormProfile");
	    public  final URI OMS_REGISTERNORM_GOAL = URI.create("RegisterNorm");
	    public  final URI OMS_REGISTERNORM_PROVIDER = URI.create("Provider");    
	    
	    public  final URI OMS_REGISTERROLE_PROFILE = URI.create(OWL_S_OMS_SERVICES + "RegisterRoleProfile.owl");
	    public  final URI OMS_REGISTERROLE_PROCESS = URI.create(OWL_S_OMS_SERVICES + "RegisterRoleProcess.owl");
	    public  final URI OMS_REGISTERROLE_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "RegisterRoleGrounding.owl");
	    public  final URI OMS_REGISTERROLE_ID = URI.create(OWL_S_OMS_SERVICES + "RegisterRoleProfile.owl#RegisterRoleProfile");
	    public  final URI OMS_REGISTERROLE_GOAL = URI.create("RegisterRole");
	    public  final URI OMS_REGISTERROLE_PROVIDER = URI.create("Provider"); 

	    public  final URI OMS_DEREGISTERUNIT_PROFILE = URI.create(OWL_S_OMS_SERVICES + "DeregisterUnitProfile.owl");
	    public  final URI OMS_DEREGISTERUNIT_PROCESS = URI.create(OWL_S_OMS_SERVICES + "DeregisterUnitProcess.owl");
	    public  final URI OMS_DEREGISTERUNIT_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "DeregisterUnitGrounding.owl");
	    public  final URI OMS_DEREGISTERUNIT_ID = URI.create(OWL_S_OMS_SERVICES + "DeregisterUnitProfile.owl#DeregisterUnitProfile");
	    public  final URI OMS_DEREGISTERUNIT_GOAL = URI.create("DeregisterUnit");
	    public  final URI OMS_DEREGISTERUNIT_PROVIDER = URI.create("Provider");
	    
	    public  final URI OMS_DEREGISTERNORM_PROFILE = URI.create(OWL_S_OMS_SERVICES + "DeregisterNormProfile.owl");
	    public  final URI OMS_DEREGISTERNORM_PROCESS = URI.create(OWL_S_OMS_SERVICES + "DeregisterNormProcess.owl");
	    public  final URI OMS_DEREGISTERNORM_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "DeregisterNormGrounding.owl");
	    public  final URI OMS_DEREGISTERNORM_ID = URI.create(OWL_S_OMS_SERVICES + "DeregisterNormProfile.owl#DeregisterNormProfile");
	    public  final URI OMS_DEREGISTERNORM_GOAL = URI.create("DeregisterNorm");
	    public  final URI OMS_DEREGISTERNORM_PROVIDER = URI.create("Provider");    
	    
	    public  final URI OMS_DEREGISTERROLE_PROFILE = URI.create(OWL_S_OMS_SERVICES + "DeregisterRoleProfile.owl");
	    public  final URI OMS_DEREGISTERROLE_PROCESS = URI.create(OWL_S_OMS_SERVICES + "DeregisterRoleProcess.owl");
	    public  final URI OMS_DEREGISTERROLE_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "DeregisterRoleGrounding.owl");
	    public  final URI OMS_DEREGISTERROLE_ID = URI.create(OWL_S_OMS_SERVICES + "DeregisterRoleProfile.owl#DeregisterRoleProfile");
	    public  final URI OMS_DEREGISTERROLE_GOAL = URI.create("DeregisterRole");
	    public  final URI OMS_DEREGISTERROLE_PROVIDER = URI.create("Provider"); 
	    //DYNAMIC SERVICES
	    public  final URI OMS_ACQUIREROLE_PROFILE = URI.create(OWL_S_OMS_SERVICES + "AcquireRoleProfile.owl");
	    public  final URI OMS_ACQUIREROLE_PROCESS = URI.create(OWL_S_OMS_SERVICES + "AcquireRoleProcess.owl");
	    public  final URI OMS_ACQUIREROLE_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "AcquireRoleGrounding.owl");
	    public  final URI OMS_ACQUIREROLE_ID = URI.create(OWL_S_OMS_SERVICES + "AcquireRoleProfile.owl#AcquireRoleProfile");
	    public  final URI OMS_ACQUIREROLE_GOAL = URI.create("AcquireRole");
	    public  final URI OMS_ACQUIREROLE_PROVIDER = URI.create("Provider");
	    
	    public  final URI OMS_LEAVEROLE_PROFILE = URI.create(OWL_S_OMS_SERVICES + "LeaveRoleProfile.owl");
	    public  final URI OMS_LEAVEROLE_PROCESS = URI.create(OWL_S_OMS_SERVICES + "LeaveRoleProcess.owl");
	    public  final URI OMS_LEAVEROLE_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "LeaveRoleGrounding.owl");
	    public  final URI OMS_LEAVEROLE_ID = URI.create(OWL_S_OMS_SERVICES + "LeaveRoleProfile.owl#LeaveRoleProfile");
	    public  final URI OMS_LEAVEROLE_GOAL = URI.create("LeaveRole");
	    public  final URI OMS_LEAVEROLE_PROVIDER = URI.create("Provider");
	    
	    public  final URI OMS_EXPULSE_PROFILE = URI.create(OWL_S_OMS_SERVICES + "ExpulseProfile.owl");
	    public  final URI OMS_EXPULSE_PROCESS = URI.create(OWL_S_OMS_SERVICES + "ExpulseProcess.owl");
	    public  final URI OMS_EXPULSE_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "ExpulseGrounding.owl");
	    public  final URI OMS_EXPULSE_ID = URI.create(OWL_S_OMS_SERVICES + "ExpulseProfile.owl#ExpulseProfile");
	    public  final URI OMS_EXPULSE_GOAL = URI.create("Expulse");
	    public  final URI OMS_EXPULSE_PROVIDER = URI.create("Provider");
	  //INFORMATIVE SERVICES
	    public  final URI OMS_INFORMUNIT_PROFILE = URI.create(OWL_S_OMS_SERVICES + "InformUnitProfile.owl");
	    public  final URI OMS_INFORMUNIT_PROCESS = URI.create(OWL_S_OMS_SERVICES + "InformUnitProcess.owl");
	    public  final URI OMS_INFORMUNIT_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "InformUnitGrounding.owl");
	    public  final URI OMS_INFORMUNIT_ID = URI.create(OWL_S_OMS_SERVICES + "InformUnitProfile.owl#InformUnitProfile");
	    public  final URI OMS_INFORMUNIT_GOAL = URI.create("InformUnit");
	    public  final URI OMS_INFORMUNIT_PROVIDER = URI.create("Provider");   
	    
	    public  final URI OMS_INFORMROLE_PROFILE = URI.create(OWL_S_OMS_SERVICES + "InformRoleProfile.owl");
	    public  final URI OMS_INFORMROLE_PROCESS = URI.create(OWL_S_OMS_SERVICES + "InformRoleProcess.owl");
	    public  final URI OMS_INFORMROLE_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "InformRoleGrounding.owl");
	    public  final URI OMS_INFORMROLE_ID = URI.create(OWL_S_OMS_SERVICES + "InformRoleProfile.owl#InformRoleProfile");
	    public  final URI OMS_INFORMROLE_GOAL = URI.create("InformRole");
	    public  final URI OMS_INFORMROLE_PROVIDER = URI.create("Provider");   
	    
	    public  final URI OMS_INFORMAGENTROLE_PROFILE = URI.create(OWL_S_OMS_SERVICES + "InformAgentRoleProfile.owl");
	    public  final URI OMS_INFORMAGENTROLE_PROCESS = URI.create(OWL_S_OMS_SERVICES + "InformAgentRoleProcess.owl");
	    public  final URI OMS_INFORMAGENTROLE_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "InformAgentRoleGrounding.owl");
	    public  final URI OMS_INFORMAGENTROLE_ID = URI.create(OWL_S_OMS_SERVICES + "InformAgentRoleProfile.owl#InformAgentRoleProfile");
	    public  final URI OMS_INFORMAGENTROLE_GOAL = URI.create("InformAgentRole");
	    public  final URI OMS_INFORMAGENTROLE_PROVIDER = URI.create("Provider");  
	    
	    public  final URI OMS_INFORMMEMBERS_PROFILE = URI.create(OWL_S_OMS_SERVICES + "InformMembersProfile.owl");
	    public  final URI OMS_INFORMMEMBERS_PROCESS = URI.create(OWL_S_OMS_SERVICES + "InformMembersProcess.owl");
	    public  final URI OMS_INFORMMEMBERS_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "InformMembersGrounding.owl");
	    public  final URI OMS_INFORMMEMBERS_ID = URI.create(OWL_S_OMS_SERVICES + "InformMembersProfile.owl#InformMembersProfile");
	    public  final URI OMS_INFORMMEMBERS_GOAL = URI.create("InformMembers");
	    public  final URI OMS_INFORMMEMBERS_PROVIDER = URI.create("Provider"); 
	    
	    public  final URI OMS_INFORMROLENORMS_PROFILE = URI.create(OWL_S_OMS_SERVICES + "InformRoleNormsProfile.owl");
	    public  final URI OMS_INFORMROLENORMS_PROCESS = URI.create(OWL_S_OMS_SERVICES + "InformRoleNormsProcess.owl");
	    public  final URI OMS_INFORMROLENORMS_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "InformRoleNormsGrounding.owl");
	    public  final URI OMS_INFORMROLENORMS_ID = URI.create(OWL_S_OMS_SERVICES + "InformRoleNormsProfile.owl#InformRoleNormsProfile");
	    public  final URI OMS_INFORMROLENORMS_GOAL = URI.create("InformRoleNorms");
	    public  final URI OMS_INFORMROLENORMS_PROVIDER = URI.create("Provider"); 
	    
	    public  final URI OMS_INFORMROLEPROFILES_PROFILE = URI.create(OWL_S_OMS_SERVICES + "InformRoleProfilesProfile.owl");
	    public  final URI OMS_INFORMROLEPROFILES_PROCESS = URI.create(OWL_S_OMS_SERVICES + "InformRoleProfilesProcess.owl");
	    public  final URI OMS_INFORMROLEPROFILES_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "InformRoleProfilesGrounding.owl");
	    public  final URI OMS_INFORMROLEPROFILES_ID = URI.create(OWL_S_OMS_SERVICES + "InformRoleProfilesProfile.owl#InformRoleProfilesProfile");
	    public  final URI OMS_INFORMROLEPROFILES_GOAL = URI.create("InformRoleProfiles");
	    public  final URI OMS_INFORMROLEPROFILES_PROVIDER = URI.create("Provider"); 
	    
	    public  final URI OMS_INFORMUNITROLES_PROFILE = URI.create(OWL_S_OMS_SERVICES + "InformUnitRolesProfile.owl");
	    public  final URI OMS_INFORMUNITROLES_PROCESS = URI.create(OWL_S_OMS_SERVICES + "InformUnitRolesProcess.owl");
	    public  final URI OMS_INFORMUNITROLES_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "InformUnitRolesGrounding.owl");
	    public  final URI OMS_INFORMUNITROLES_ID = URI.create(OWL_S_OMS_SERVICES + "InformUnitRolesProfile.owl#InformUnitRolesProfile");
	    public  final URI OMS_INFORMUNITROLES_GOAL = URI.create("InformUnitRoles");
	    public  final URI OMS_INFORMUNITROLES_PROVIDER = URI.create("Provider"); 
	    
	    public  final URI OMS_QUANTITYMEMBERS_PROFILE = URI.create(OWL_S_OMS_SERVICES + "QuantityMembersProfile.owl");
	    public  final URI OMS_QUANTITYMEMBERS_PROCESS = URI.create(OWL_S_OMS_SERVICES + "QuantityMembersProcess.owl");
	    public  final URI OMS_QUANTITYMEMBERS_GROUNDING = URI.create(OWL_S_OMS_SERVICES + "QuantityMembersGrounding.owl");
	    public  final URI OMS_QUANTITYMEMBERS_ID = URI.create(OWL_S_OMS_SERVICES + "QuantityMembersProfile.owl#QuantityMembersProfile");
	    public  final URI OMS_QUANTITYMEMBERS_GOAL = URI.create("QuantityMembers");
	    public  final URI OMS_QUANTITYMEMBERS_PROVIDER = URI.create("Provider"); 
	    
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
     * 
     * @param aid
     * @param connection
     * @param OMSServiceDesciptionLocation
     * @param SFServiceDesciptionLocation
     * @throws Exception
     */
	public OMS(AgentID aid)throws Exception{
    	super(aid);
    }
   
	/**
	 * 
	 * @param OMSUrl
	 */
	public void setOMSServiceDesciptionLocation(String OMSUrl)
	{
	
		this.OMSServiceDesciptionLocation = OMSUrl; 
	}
	
	/**
	 * 
	 * @param SFUrl
	 */
	public void setSFServiceDesciptionLocation(String SFUrl)
	{
		this.SFServiceDesciptionLocation = SFUrl;	
    }

	/**
	 * 
	 * @param OMSUrl
	 */
	public String getOMSServiceDesciptionLocation()
	{
	
		return OMSServiceDesciptionLocation; 
	}
	
	/**
	 * 
	 * @param SFUrl
	 */
	public String getSFServiceDesciptionLocation()
	{
		return this.SFServiceDesciptionLocation;	
    }
    
    // array with OMS service grounding
    /**
	 * Initial registration of the OMS service profiles
	 *
	 * @param 
	 * @throws RuntimeException
	 */
	public void RegisterOMSServiceProfiles(){
		
		// create an execution engine
		ProcessExecutionEngine exec = OWLSFactory.createExecutionEngine();
			
		try {	
		  
			//REGISTER SOMSSERVICES PROFILES 
			Service RegisterProfileService = kb.readService(SF_REGISTERPROFILE_PROCESS);
			//get the process for the server
			Process RegisterProfileProcess = RegisterProfileService.getProcess();
			for(int k=0; k<OMSServicesProfiles.length;k++){
				// initialize the input values to be empty
	            ValueMap values = new ValueMap();           
				values.setDataValue(RegisterProfileProcess.getInput("RegisterProfileInputServiceProfile"),OMSServicesProfiles[k].toString());
				values.setDataValue(RegisterProfileProcess.getInput("RegisterProfileInputServiceGoal"),OMSServicesGoals[k].toString());
				values.setDataValue(RegisterProfileProcess.getInput("RegisterProfileInputAgentID"),"OMS");
				if(DEBUG) logger.info("[OMS]Executing... "+values.getValues().toString());
				values = exec.execute(RegisterProfileProcess, values);
				if(DEBUG) logger.info("[OMS]Values obtained... :"+values.toString());
				if(DEBUG) logger.info("[OMS]ServiceID... :"+values.getValue("RegisterProfileOutputServiceID").toString());
				OMSServicesIDs[k]=URI.create(values.getValue("RegisterProfileOutputServiceID").toString());
			}//for k
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}//end RegisterOMSServiceProfiles
		
	/**
	 * Initial registration of the OMS service process
	 *
	 * @param 
	 * @throws RuntimeException
	 */
	public void RegisterOMSServiceProcess(){
		// create an execution engine
		ProcessExecutionEngine exec = OWLSFactory.createExecutionEngine();
		
		try {	
			//REGISTER OMS SERVICES PROCESS 
			Service RegisterProcessService = kb.readService(SF_REGISTERPROCESS_PROCESS);
			//get the process 
			Process RegisterProcessProcess = RegisterProcessService.getProcess();
			
			for(int k=0; k<OMSServicesProcess.length;k++){
				
				// initialize the input values to be empty
	            ValueMap values = new ValueMap();
				values.setDataValue(RegisterProcessProcess.getInput("RegisterProcessInputServiceID"),OMSServicesIDs[k].toString() );
				values.setDataValue(RegisterProcessProcess.getInput("RegisterProcessInputServiceModel"),OMSServicesProcess[k].toString() );
				values.setDataValue(RegisterProcessProcess.getInput("RegisterProcessInputAgentID"),"OMS");
				if(DEBUG) logger.info("[OMS]Executing... "+values.getValues().toString());
				values = exec.execute(RegisterProcessProcess, values);
				if(DEBUG) logger.info("[OMS]Values obtained... :"+values.toString());
				
		
			
			}//for k
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}//end RegisterOMSServiceProcess
			
	
	/**
	 * Manages the messages for the OMS services 
	 */
	public class OMSResponder extends FIPARequestResponder{

		public OMSResponder(es.upv.dsic.gti_ia.architecture.QueueAgent agent) {
			super(agent, new MessageTemplate(InteractionProtocol.FIPA_REQUEST));
			
		}//OMSResponder

		/**
		 * Receives the messages and takes the message content. Analyzes the message content
		 * and gets the service process and input parameters to invoke the service. After the service
		 * invocation, the OMS gets the answer and sends it to the requester agent. 
		 *
		 * @param 
		 * @throws RuntimeException
		 */
		 protected  ACLMessage prepareResponse(ACLMessage msg) {
			 
			 	ACLMessage response = msg.createReply();
			 	
			 	
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
							response.setPerformative(es.upv.dsic.gti_ia.core.ACLMessage.AGREE);
							response.setContent("=Agree");
							
						}else{
                           
							logger.info("REFUSE");
							response.setPerformative(es.upv.dsic.gti_ia.core.ACLMessage.REFUSE);
							response.setContent("=Refuse");
						}
						
					}catch(Exception e){
                       
						logger.info("EXCEPTION");
                       
						System.out.println(e);
						e.printStackTrace();
						throw new RuntimeException(e.getMessage());

					}
						
				}else{
                   
					logger.info("NOTUNDERSTOOD");
					response.setPerformative(es.upv.dsic.gti_ia.core.ACLMessage.NOT_UNDERSTOOD);
					response.setContent("NotUnderstood");
				}

               
				logger.info("[OMS]Sending First message:"+ response);
				return(response);
		
		} //end prepareResponse
		 
	
		
         
		/**
		 * This callback happens if the OMS sent a positive reply to the original request (i.e. an AGREE) 
		 * if the OMS has agreed to supply the service, the OMS has to inform the other agent that 
		 * what they have asked is now complete (or if it failed)
		 *
		 * @param inmsg 
		 * @param outmsg
		 * @throws RuntimeException
		 */
		protected  ACLMessage   prepareResultNotification(ACLMessage inmsg,ACLMessage outmsg) {
	                        
		 	ACLMessage msg = inmsg.createReply();
			
		 	// create an execution engine
			ProcessExecutionEngine exec = OWLSFactory.createExecutionEngine();
		
			//read msg content
			StringTokenizer Tok = new StringTokenizer(inmsg.getContent());
			
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
						values.setValue(aProcess.getInputs().inputAt(i),EntityFactory.createDataValue(inmsg.getSender().toString()));
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
			
				msg.setPerformative(ACLMessage.INFORM);
				if(DEBUG)
				{
					
					logger.info("[OMS]Before set message content...");
					
				}
				msg.setContent(aProcess.getLocalName()+"="+values.toString());
				
                        
            }catch(Exception e){
            	if(DEBUG)
            	{
            		
            		logger.error(e.toString());
            	}
            	msg.setPerformative(ACLMessage.FAILURE);
            }
            return (msg);
		} //end prepareResultNotification
	
	}//end class OMSResponder

	
/**
* Starts the OMS agent and registers all the OMS services (process, profile, grounding)
*
* @param 
* @throws 
*/
protected void execute() {
	//RegisterOMSServiceProfiles();
	//RegisterOMSServiceProcess();
	logger.info("Agent OMS active");
	OMSResponder responder = new OMSResponder(this);
  
	this.setTask(responder);
	mon.waiting();
					
}// end setup

} //end OMS Agent
