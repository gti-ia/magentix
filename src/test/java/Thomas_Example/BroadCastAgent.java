package Thomas_Example;



import es.upv.dsic.gti_ia.architecture.FIPARequestResponder;
import es.upv.dsic.gti_ia.architecture.MessageTemplate;
import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.architecture.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;

import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.SFServiceDescription;
import es.upv.dsic.gti_ia.organization.SFProxy;


import java.util.ArrayList;
import java.util.StringTokenizer;

import org.mindswap.owl.EntityFactory;
import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owls.OWLSFactory;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.process.execution.ProcessExecutionEngine;
import org.mindswap.owls.service.Service;
import org.mindswap.query.ValueMap;




public class BroadCastAgent extends QueueAgent {

	public BroadCastAgent(AgentID aid)throws Exception{
	
 	super(aid);
}
	
	
	public void execute()
    {
		//We create the class that will make us the agent proxy oms,facilitates access to the methods of the OMS
		OMSProxy OMSservices = new OMSProxy();
		
		//We create the class that will make us the agent proxy sf,facilitates access to the methods of the SF
		SFProxy SFservices = new SFProxy();
		
		//We create a SFServiceDescription, one for service that we have
		SFServiceDescription serviceOne = new SFServiceDescription("http://localhost:8080/broadcastservices/owl/owls/","http://localhost:8080/broadcastservices/owl/owls/");
		SFServiceDescription serviceTwo = new SFServiceDescription("http://localhost:8080/sfservices/THservices/owl/owls/","http://localhost:8080/sfservices/THservices/owl/owls/");
		
		String result;

        ArrayList<AgentID> agents;
        
        
        
        try
		{
        	
		result = OMSservices.acquireRole(this, "member","virtual");
		
		System.out.println("[BroadCastAgent]Acquire Role result: "+result+"\n");
		
	    //****************** RegisterUnit *************************
        result = OMSservices.registerUnit(this, "news", "congregation", "receivenews", "virtual");
        //*********************************************************
                
        System.out.println("[BroadCastAgent]Register Unit result: "+result+"\n");
        
		//****************** RegisterRole ***************
		result = OMSservices.registerRole(this, "broadcaster","news" , "external", "member", "public", "member");
		//*********************************************
   
		System.out.println("[BroadCastAgent]Register Role result: "+result+"\n");
		
		System.out.println("[BroadCastAgent] conencted with rol customer: "+ OMSservices.acquireRole(this, "broadcaster", "news"));
		
		
		
        //Initializing services
		
		//Service one
        serviceOne.setServiceGoal("BroadcastWS");
        //Service two
		serviceTwo.setServiceGoal("SearchCheapHotel");
		
		
			SFservices.registerProfile(this,serviceOne);
			System.out.println("[BroadCastAgent]The operation register Profile return: "+  serviceOne.getID()+"\n");
		

			
		    SFservices.registerProcess(this, serviceOne);
			System.out.println("[BroadCastAgent]The operation register Process return: "+  serviceOne.getImplementationID()+"\n");
		
		
			OMSservices.registerRole(this, "subscriptor","news" , "external", "member", "public", "member");
		
			OMSservices.acquireRole(this,"subscriptor", "news");
			
	        //************ SearchService *****************
	        ArrayList<String> serviceProfile = new ArrayList<String>();
	        
	        serviceProfile = SFservices.searchService(this, "BroadcastWS");
	           
	        
	        agents = SFservices.getProcess(this, serviceProfile.get(0));
	        
	        for (AgentID agent : agents)
				System.out
						.println("[BroadCastAgent]Agents who has the broadcast service: "
								+ agent.name+"\n");
	        
	        //************************************************


	        
	        //************ GetProfile *****************

	    	SFservices.registerProfile(this,serviceTwo);
				System.out.println("[BroadCastAgent]Register Profile return: "+  serviceTwo.getID()+"\n");
	    	
	    
	    	OMSservices.acquireRole(this,"provider", "travelagency");
	        
	        SFservices.registerProcess(this, serviceTwo);
	        
	        	System.out.println("[BroadCastAgent]Register Porcess return: "+ serviceTwo.getImplementationID()+"\n");
	        	
	        
	        //Rol responder
	    	Responder responder = new Responder(this);
			
			this.addTask(responder);
				
			//when we do not have to create more roles we await the expiration of the other roles
	
			
			es.upv.dsic.gti_ia.architecture.Monitor mon = new es.upv.dsic.gti_ia.architecture.Monitor();
		    mon.waiting();
		    
		}catch(Exception e){
			logger.error(e.getMessage());
			
		}
	       
	        
	        
	        
		
    }
	

	/**
	 * Manages the messages for the SF services 
	 */
	public class Responder extends FIPARequestResponder {

		OWLKnowledgeBase kb = OWLFactory.createKB();
		
		public Responder(QueueAgent agent) {
			super(agent, new MessageTemplate(InteractionProtocol.FIPA_REQUEST));
			
		}//SFResponder

		/**
		 * Receives the messages and takes the message content. Analyzes the message content
		 * and gets the service process and input parameters to invoke the service. After the service
		 * invocation, the SF gets the answer and sends it to the requester agent. 
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
						
						System.out.println("[Provider]Doc OWL-S: " + token_process);
						Service aService = kb.readService(token_process);		
					
						//get the process for the server
						Process aProcess = aService.getProcess();
						
						
							System.out.println("AGREE");
							response.setPerformative(ACLMessage.AGREE);
							response.setContent(aProcess.getLocalName()+"=Agree");
							
						/*}else{
							System.out.println("REFUSE");
							response.setPerformative(jade.lang.acl.ACLMessage.REFUSE);
							response.setContent(aProcess.getLocalName()+"=Refuse");
						}*/
						
					}catch(Exception e){
					
						System.out.println("EXCEPTION");
						System.out.println(e);
						e.printStackTrace();
						throw new RuntimeException(e.getMessage());

					}
						
				}else{
					
					System.out.println("NOTUNDERSTOOD");
					response.setPerformative(ACLMessage.NOT_UNDERSTOOD);
					response.setContent("NotUnderstood");
				}
			
			
				System.out.println("[Provider]Sending First message:"+ response);
			
				return(response);
		
		} //end prepareResponse
		 
	
		
         
		/**
		 * This callback happens if the SF sent a positive reply to the original request (i.e. an AGREE) 
		 * if the SF has agreed to supply the service, the SF has to inform the other agent that 
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
			
				System.out.println("[Provider]Doc OWL-S: " + token_process);
				
				try{
					Service aService = kb.readService(token_process);		
				
					//get the process for the server
					Process aProcess = aService.getProcess();
					//initialize the input values to be empty
					ValueMap values = new ValueMap();
			
					//get the input values
					//int n = 0;

					//int tokenCount = Tok.countTokens();
					for(int i=0;i<aProcess.getInputs().size();i++)
					values.setValue(aProcess.getInputs().inputAt(i),EntityFactory.createDataValue(""));
					while (Tok.hasMoreElements()) {
						String token = Tok.nextElement().toString();
						for(int i=0;i<aProcess.getInputs().size();i++){
							String paramName = aProcess.getInputs().inputAt(i).getLocalName().toLowerCase();
							if(paramName.equalsIgnoreCase(token.split("=")[0].toLowerCase())){
								if(token.split("=").length >= 2)
									values.setValue(aProcess.getInputs().inputAt(i),EntityFactory.createDataValue(token.split("=")[1]));
								else
									values.setValue(aProcess.getInputs().inputAt(i),EntityFactory.createDataValue(""));
								break;
								}
						}
					}//end while   
					
					//execute the service
					
					System.out.println("[Provider]Executing... "+values.getValues().toString());
					values = exec.execute(aProcess, values);
					
					System.out.println("[Provider]Values obtained... :"+values.toString());
					
					System.out.println("[Provider]Creating inform message to send...");
				
					msg.setPerformative(ACLMessage.INFORM);
					
					System.out.println("[Provider]Before set message content...");
					msg.setContent(aProcess.getLocalName()+"="+values.toString());
					
	                        
	            }catch(Exception e){
	            	
	            	System.out.println("EXCEPTION");
					System.out.println(e);
					e.printStackTrace();
					msg.setPerformative(ACLMessage.FAILURE);
	            }
	            return (msg);
		} //end prepareResultNotification
	
	}//end class SFResponder

	
}
