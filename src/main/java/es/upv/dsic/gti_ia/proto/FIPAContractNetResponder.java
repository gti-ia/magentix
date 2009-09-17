
/**
 * La clase FIPAContractNetResponder permite ejecutar el protocolo FIPA-CONTRACT-NET por la parte del responder.
 * 
 * @author  Joan Bellver Faus, GTI-IA, DSIC, UPV
 * @version 2009.9.07
 */

package es.upv.dsic.gti_ia.proto;


import es.upv.dsic.gti_ia.magentix2.QueueAgent;
import es.upv.dsic.gti_ia.proto.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.proto.Adviser;
import es.upv.dsic.gti_ia.fipa.ACLMessage;


public class FIPAContractNetResponder{
	
	
	private final static int WAITING_MSG_STATE = 0;
	private final static int PREPARE_RESPONSE_STATE = 1;
	private final static int SEND_RESPONSE_STATE = 2;
	private final static int RECEIVE_MSG_STATE=3;
	private final static int PREPARE_RES_NOT_STATE = 4;
	private final static int SEND_RESULT_NOTIFICATION_STATE= 5;
	private final static int RESET_STATE=6;
	
	
	private MessageTemplate template;
	private int state = WAITING_MSG_STATE;
	private QueueAgent myAgent;
	private ACLMessage  cfp;
	private ACLMessage propose;
	private ACLMessage accept;
	private ACLMessage reject;
	private ACLMessage resNofificationmsg;

	
	private Adviser sin=null;
	

    /**
     * Create a FIPARequestInitiator.
     * @param agent    agente que crear el inicio del protocolo
     * @param template    plantilla para en la que el agente comparara los mensajes.
     */
	
	public FIPAContractNetResponder(QueueAgent _agent, MessageTemplate _template)
	{
		myAgent = _agent;
		template = _template;
		_agent.setAdviserRes(new Adviser());
		this.sin = _agent.getAdviserRes();
		
	
		
	}
	
	public  void action()
	{
		
		
		switch(state)
		{
		case WAITING_MSG_STATE:{	
			ACLMessage request = myAgent.receiveACLMessage(template);
			
			if(request != null)
			{

					this.cfp = request;
					state = PREPARE_RESPONSE_STATE;	
			}
			else
			{

				sin.esperar();//me espero a que llegue un mensaje.

			}
			break;
		}
		case PREPARE_RESPONSE_STATE:{
			ACLMessage request = this.cfp;
			ACLMessage response = null;
			state = SEND_RESPONSE_STATE;
			try
			{
				response = prepareResponse(request);
			}
			catch(NotUnderstoodException nue){
				response = request.createReply();
				response.setContent(nue.getMessage());
				response.setPerformative(ACLMessage.NOT_UNDERSTOOD);
				
			}
			catch(RefuseException re){
				
				
				response = request.createReply();
				response.setContent(re.getMessage());
				response.setPerformative(ACLMessage.REFUSE);
				
			}
		
			this.propose = response;
			break;
		}
		case SEND_RESPONSE_STATE:{
			ACLMessage response  = this.propose;
			
			if (response != null)
			{
		
				ACLMessage receivedMsg = this.cfp;
				response = arrangeMessage(receivedMsg, response);
				
				myAgent.send(response);
				if (response.getPerformativeInt() == ACLMessage.PROPOSE)
					state = RECEIVE_MSG_STATE;
				else//si la performativa es refuse terminamos con el protocolo.
					state = RESET_STATE;
			}

			break;
			
		}
		
		case RECEIVE_MSG_STATE:
		{
			//configuramos un nuevo template para esperar solo al que le hemos enviado la contrapuesta.
			MessageTemplate template2 = new MessageTemplate(InteractionProtocol.FIPA_CONTRACT_NET);
			template2.addConversacion(this.propose.getConversationId());
			template2.add_receiver(this.propose.getReceiver());
			ACLMessage secondReply = myAgent.receiveACLMessageI(template2);
			

			
			//esperamos haber si acepta nuestra contrapropuesta
			if (secondReply!=null){

				switch(secondReply.getPerformativeInt()){
				case ACLMessage.REJECT_PROPOSAL:{
					this.reject = secondReply;
					state = RESET_STATE;
					handleRejectProposal(this.cfp,this.propose,this.reject);
					break;
					
				}
				case ACLMessage.ACCEPT_PROPOSAL:{
					this.accept = secondReply;
					state = PREPARE_RES_NOT_STATE;
					break;
					
					
				}
				}
				break;
			}
			else
			{
				this.sin.esperar();
				state = RECEIVE_MSG_STATE;
				break;
			}
		}
		
		case PREPARE_RES_NOT_STATE:{
			
			state = SEND_RESULT_NOTIFICATION_STATE;

			ACLMessage resNotification = null;
			
			try{
				resNotification = prepareResultNotification(this.cfp,this.propose,this.accept);				
			}
			catch(FailureException fe){
				
				resNotification = cfp.createReply();
				
				resNotification.setContent(fe.getMessage());
				resNotification.setPerformative(ACLMessage.FAILURE);
			}
			
			this.resNofificationmsg = resNotification;
			break;
		}
		case SEND_RESULT_NOTIFICATION_STATE:{
			state =RESET_STATE;
			ACLMessage resNotification = this.resNofificationmsg;
			if (resNotification != null)
			{
				
				ACLMessage receiveMsg = this.accept;
				myAgent.send(arrangeMessage(receiveMsg,resNotification));
			}
			
			break;
			
		}
		case RESET_STATE:{
			
			state = WAITING_MSG_STATE;
			this.cfp = null;
			this.accept = null;
			this.reject= null;
			this.propose = null;
			this.resNofificationmsg = null;
			break;
		}
		
		}
		
	}
	
	private ACLMessage arrangeMessage(ACLMessage request, ACLMessage reply)
	{
		
		reply.setConversationId(request.getConversationId());
		reply.setInReplyTo(request.getReplyWith());
		reply.setProtocol(request.getProtocol());
		reply.setReceiver(request.getSender());
		reply.setSender(request.getReceiver());
		
		//set the receivers
		
			reply.add_receiver(request.getSender());
		
		
		
		
		return reply;
	}
	
	
	protected ACLMessage prepareResponse(ACLMessage cfp) throws NotUnderstoodException, RefuseException
	{
		return null;
	}
	
	protected ACLMessage prepareResultNotification(ACLMessage cfp, ACLMessage propose, ACLMessage accept) throws FailureException
	{
		return null;
	}
	
	protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject){
		
	}
	

}

