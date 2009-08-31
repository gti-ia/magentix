package es.upv.dsic.gti_ia.proto;


import es.upv.dsic.gti_ia.magentix2.QueueAgent;
import es.upv.dsic.gti_ia.proto.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.proto.Sincro;
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
	
	private Sincro sin=null;
	

	
	
	public FIPARequestResponder(QueueAgent _agent, MessageTemplate _template, Sincro _sin)
	{
		myAgent = _agent;
		template = _template;
		this.sin = _sin;
		
	}
	
	public  void action()
	{
		
		
		switch(state)
		{
		case WAITING_MSG_STATE:{
			boolean puerta = true;
			
			do{
			ACLMessage request = myAgent.receiveACLMessage(template);
			
				if(request != null)
				{
					this.requestmsg = request;
					state = PREPARE_RESPONSE_STATE;
					puerta=false;
				
			}
			
			else
			{
				
				
				
				sin.esperar();//me espero a que llegue un mensaje.
				
				
			}
		
				
				
			}while(puerta);
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
			System.out.println("Sale con estado: "+state);
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
				
				ACLMessage receiveMsg = this.requestmsg;
				myAgent.send(arrangeMessage(receiveMsg,resNotification));
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
		reply.setSender(request.getReceiver());
		
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

