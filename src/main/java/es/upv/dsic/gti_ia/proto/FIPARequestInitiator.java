package es.upv.dsic.gti_ia.proto;


import java.util.Vector;
import java.util.Date;
import java.util.logging.*;

import es.upv.dsic.gti_ia.proto.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.magentix2.QueueAgent;

import es.upv.dsic.gti_ia.fipa.ACLMessage;

public class FIPARequestInitiator {
	private final static int PREPARE_MSG_STATE=0;
	private final static int SEND_MSG_STATE=1;
	private final static int RECEIVE_REPLY_STATE=2;
	private final static int RECEIVE_2ND_REPLY_STATE=3;
	private final static int ALL_REPLIES_RECEIVED_STATE=4;
	private final static int ALL_RESULT_NOTIFICATION_RECEIVED_STATE=5;
	
	
	private MessageTemplate template = null;
	private int state = PREPARE_MSG_STATE;
	protected QueueAgent myAgent;
	private ACLMessage  requestmsg;
	private ACLMessage requestsentmsg;
	private ACLMessage resNofificationmsg;
	
	private Adviser sin=null;

	
	
	
	
	private  Logger logger = Logger.getLogger(this.getClass().getName());
	
	
	
	
	public FIPARequestInitiator(QueueAgent agent, ACLMessage msg)//, Sincro _sin)
	{
		myAgent = agent;
		requestmsg = msg;
		//this.sin = _sin;
		agent.setAdviserIni(new Adviser());
		this.sin = agent.getAdviserIni();
		
	}
	
	public int getEstado()
	{
	return this.state;	
	}
	
	public  void action()
	{
		switch(state)
		{
		case PREPARE_MSG_STATE:{
			
			ACLMessage msg = prepareRequest(this.requestmsg);
			this.requestsentmsg = msg;
			state=SEND_MSG_STATE;
			break;
		}
		case SEND_MSG_STATE:{
			
			String conversationID = null;
			ACLMessage request = this.requestsentmsg;
			if (request==null)
			{
				//finalización del protocolo
				break;
			}
			else
			{
				if(request.getConversationId().equals("")){
					conversationID = "C"+hashCode()+"_"+System.currentTimeMillis();
					request.setConversationId(conversationID);
				}else
				{
					conversationID = request.getConversationId();
				}
				
				
				//configuramos el template
				template = new MessageTemplate(InteractionProtocol.FIPA_REQUEST);
				
				template.setConversationId(conversationID);
				template.setSender(request.getReceiver());
				


				myAgent.send(request);
				state = RECEIVE_REPLY_STATE;
						
			}
			break;
			
		}
		case RECEIVE_REPLY_STATE:{
			
	
			ACLMessage firstReply = myAgent.receiveACLMessageI(template);
			
			
			if (firstReply!=null){
				
				switch(firstReply.getPerformativeInt()){
				case ACLMessage.AGREE:{
					state = RECEIVE_2ND_REPLY_STATE;
					handleAgree(firstReply);
					break;
				}
				case ACLMessage.REFUSE:{
					state = ALL_REPLIES_RECEIVED_STATE;
					handleRefuse(firstReply);
					break;
				}
				case ACLMessage.NOT_UNDERSTOOD:{
					state = ALL_REPLIES_RECEIVED_STATE;
					handleNotUnderstood(firstReply);
					break;
					
				}
				case ACLMessage.FAILURE:{
					state = ALL_RESULT_NOTIFICATION_RECEIVED_STATE;
					handleFailure(firstReply);
					break;
					
				}
				case ACLMessage.INFORM:{
					state = ALL_RESULT_NOTIFICATION_RECEIVED_STATE;
					handleInform(firstReply);
					break;
					
				}
				default:{
					//nos llega el segundo mensaje, habido problemas con el primer mensaje
					state = RECEIVE_REPLY_STATE;
					handleOutOfSequence(firstReply);
					break;
					
				}
				}
				break;
			}
			else
			{
				this.sin.esperar();
				 state = RECEIVE_REPLY_STATE;// state = ALL_REPLIES_RECEIVED_STATE;
				 break;
			}
		}
		case RECEIVE_2ND_REPLY_STATE:{
			ACLMessage secondReply = myAgent.receiveACLMessageI(template);
			
			
			if (secondReply!=null){
				switch(secondReply.getPerformativeInt()){
				case ACLMessage.INFORM:{
					state = ALL_RESULT_NOTIFICATION_RECEIVED_STATE;
					handleInform(secondReply);
					break;
					
				}
				case ACLMessage.FAILURE:{
					state = ALL_RESULT_NOTIFICATION_RECEIVED_STATE;
					handleFailure(secondReply);
					break;
					
					
				}
				default:{
					
					//state = RECEIVE_REPLY_STATE;
					state = ALL_RESULT_NOTIFICATION_RECEIVED_STATE;
					handleOutOfSequence(secondReply);
					break;
				}
				}
				break;
			}
			else
			{
				this.sin.esperar();
				state = RECEIVE_2ND_REPLY_STATE;
				break;
			}
		}
		case ALL_REPLIES_RECEIVED_STATE:{
			myAgent.setAdviserIni(null);
			state = PREPARE_MSG_STATE;
			break;
		}
		case ALL_RESULT_NOTIFICATION_RECEIVED_STATE:{

			myAgent.setAdviserIni(null);
            state = PREPARE_MSG_STATE;
			this.requestmsg = null;
			this.resNofificationmsg = null;
			this.resNofificationmsg = null;
			break;
		}
		
		}
		
		
		
	}
	
    /**
     * This method must return the ACLMessage to be sent.
     * This default implementation just return the ACLMessage object passed in the constructor.
     * Programmer might override the method in order to return a different ACLMessage.
     * Note that for this simple version of protocol, the message will be just send to the first receiver set.
     * @param msg the ACLMessage object passed in the constructor.
     * @return a ACLMessage.
     **/
    protected ACLMessage prepareRequest(ACLMessage msg){
	return msg;
    }
    
    protected void handleAgree(ACLMessage msg)
    {
    	if(logger.isLoggable(Level.FINE))
			logger.log(Level.FINE,"in HandleAgree: " + msg.toString());
    }
    
    protected void handleRefuse(ACLMessage msg){
		if(logger.isLoggable(Level.FINE))
			logger.log(Level.FINE,"in HandleRefuse: " + msg.toString());
    }


    protected void handleNotUnderstood(ACLMessage msg){
		if(logger.isLoggable(Level.FINE))
			logger.log(Level.FINE,"in HandleNotUnderstood: " + msg.toString());
    }


    protected void handleInform(ACLMessage msg){
	if(logger.isLoggable(Level.FINE))
		logger.log(Level.FINE,"in HandleInform: " + msg.toString());
    }


    protected void handleFailure(ACLMessage msg){
	if(logger.isLoggable(Level.FINEST))
		logger.log(Level.FINEST,"in HandleFailure: " + msg.toString());
    }


    protected void handleOutOfSequence(ACLMessage msg){
	if(logger.isLoggable(Level.FINEST))
		logger.log(Level.FINEST,"in HandleOutOfSequence: " + msg.toString());
    }




    
	
}
