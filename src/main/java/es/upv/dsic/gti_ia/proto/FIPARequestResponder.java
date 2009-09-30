
/**
 * La clase FIPARequestResponder permite ejecutar el protocolo FIPA-REQUEST por la parte del responder.
 * 
 * @author  Joan Bellver Faus, GTI-IA, DSIC, UPV
 * @version 2009.9.07
 */

package es.upv.dsic.gti_ia.proto;


import es.upv.dsic.gti_ia.magentix2.QueueAgent;
import es.upv.dsic.gti_ia.proto.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.proto.Adviser;
import es.upv.dsic.gti_ia.fipa.ACLMessage;


public class FIPARequestResponder{
	
	
	private final static int WAITING_MSG_STATE = 0;
	private final static int PREPARE_RESPONSE_STATE = 1;
	private final static int SEND_RESPONSE_STATE = 2;
	private final static int PREPARE_RES_NOT_STATE = 3;
	private final static int SEND_RESULT_NOTIFICATION_STATE=4;
	private final static int RESET_STATE=5;
	
	
	private MessageTemplate template;
	private int state = WAITING_MSG_STATE;
	private QueueAgent myAgent;
	private ACLMessage  requestmsg;
	private ACLMessage responsemsg;
	private ACLMessage resNofificationmsg;
	
	private Adviser sin=null;
	

    /**
     * Create a FIPARequestInitiator.
     * @param agent    agente que crear el inicio del protocolo
     * @param template    plantilla para en la que el agente comparara los mensajes.
     */
	
	public FIPARequestResponder(QueueAgent _agent, MessageTemplate _template)//, Sincro _sin)
	{
		myAgent = _agent;
		template = _template;
		//this.sin = _sin;
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
					this.requestmsg = request;
					state = PREPARE_RESPONSE_STATE;	
			}
			else
			{
				sin.esperar();//me espero a que llegue un mensaje.
			}
			break;
		}
		case PREPARE_RESPONSE_STATE:{
			ACLMessage request = this.requestmsg;
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
			
			this.responsemsg = response;
			break;
		}
		case SEND_RESPONSE_STATE:{
			ACLMessage response  = this.responsemsg;
			
			if (response != null)
			{
			
		
					
				ACLMessage receivedMsg = this.requestmsg;
				
				
				
				response = arrangeMessage(receivedMsg, response);
				

				response.setSender(myAgent.getAid());
				
				
				myAgent.send(response);
		
				
				if (response.getPerformativeInt() == ACLMessage.AGREE)
					state = PREPARE_RES_NOT_STATE;
				else
				{
					
					state = RESET_STATE;
				}
				
				
			}
			else
			{
				
				state = PREPARE_RES_NOT_STATE;
			}
		
			break;
			
		}
		case PREPARE_RES_NOT_STATE:{
			
			state = SEND_RESULT_NOTIFICATION_STATE;
			ACLMessage request = this.requestmsg;
			ACLMessage response = this.responsemsg;
			ACLMessage resNotification = null;
			
			try{
				resNotification = prepareResultNotification(request, response);
				
			}
			catch(FailureException fe){
				
				resNotification = request.createReply();
				
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
				
				ACLMessage receiveMsg = arrangeMessage(this.requestmsg,resNotification);
				receiveMsg.setSender(myAgent.getAid());
				myAgent.send(receiveMsg);
			}
			
			break;
			
		}
		case RESET_STATE:{
			
			state = WAITING_MSG_STATE;
			this.requestmsg = null;
			this.resNofificationmsg = null;
			this.responsemsg = null;
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
		
		
		return reply;
	}
	
	
	protected ACLMessage prepareResponse(ACLMessage request) throws NotUnderstoodException, RefuseException
	{
		return null;
	}
	
	protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage responder) throws FailureException
	{
		return null;
	}
	

}

