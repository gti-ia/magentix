package _thomas_Example;




import es.upv.dsic.gti_ia.core.*;
import es.upv.dsic.gti_ia.architecture.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.architecture.*;





import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLKnowledgeBase;



public class providerAgent extends QueueAgent {
	
	// create a kb
	OWLKnowledgeBase kb = OWLFactory.createKB();
	OWLKnowledgeBase kbaux = OWLFactory.createKB();

	
	
	public providerAgent(AgentID aid) throws Exception{
		super(aid);
	}
	
	protected void execute(){

		
	 		Responder responder = new Responder(this);
		
		do{
			
			responder.action();
		
		}while(true);

	};
	/**
	 * Manages the messages for the SF services 
	 */
	public class Responder extends FIPARequestResponder {

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
						
						
						
						//aceptar cualquier tipo de mensaje
					
						//read msg content
						
						
						
						
						//read in the service description
						
		
						
								
					
						//get the process for the server
						
						
				
							System.out.println("AGREE");
							response.setPerformative(ACLMessage.AGREE);
							response.setContent("ENTIENDO LO QUE ME DICES, hola cara de bola");
							
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
				
			
				//read msg content
				
				
				//read in the service description
				
				
				
			 
					
					//execute the service

			 	try{
				
					msg.setPerformative(ACLMessage.INFORM);

					System.out.println("[Provider]Before set message content...");
					msg.setContent("HOLA CARA DE BOLA");
					
	                        
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
    