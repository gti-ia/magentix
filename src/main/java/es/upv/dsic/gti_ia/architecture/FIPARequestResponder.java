/**
 * This class implements the FIPA-Request interaction protocol, Role Responder.
 * 
 * @author  Joan Bellver Faus, GTI-IA, DSIC, UPV
 * @version 2009.9.07
 */

package es.upv.dsic.gti_ia.architecture;

import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.architecture.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.core.ACLMessage;

public class FIPARequestResponder {

	private final static int WAITING_MSG_STATE = 0;
	private final static int PREPARE_RESPONSE_STATE = 1;
	private final static int SEND_RESPONSE_STATE = 2;
	private final static int PREPARE_RES_NOT_STATE = 3;
	private final static int SEND_RESULT_NOTIFICATION_STATE = 4;
	private final static int RESET_STATE = 5;
	private final static int FINISH_STATE = -1;

	private MessageTemplate template;
	private int state = WAITING_MSG_STATE;
	private QueueAgent myAgent;
	private ACLMessage requestmsg;
	private ACLMessage responsemsg;
	private ACLMessage resNofificationmsg;
	private MessageTemplate template_cancel;
	private ACLMessage  send_cancel;
	private Monitor monitor = null;

	/**
	 * Create a FIPARequestResponder.
	 * 
	 * @param agent
	 *            agent is the reference to the Agent Object.
	 * @param template
	 */

	public FIPARequestResponder(QueueAgent _agent, MessageTemplate _template) {
		myAgent = _agent;
		template = _template;
		this.monitor = myAgent.addMonitor(this);
	}

	
	 int getState() {
		return this.state;
	}

	 void finish()
	 {
		 //Send a CANCEL Message for  active conversation
		 
		 if (this.requestmsg!=null)
		 {
		 arrangeMessage(this.requestmsg,send_cancel);
		 this.send_cancel.setPerformative(ACLMessage.CANCEL);
		 this.myAgent.send(send_cancel);
		 }
		 this.state = FINISH_STATE;
		 
	 }
	
	// #APIDOC_EXCLUDE_BEGIN
	public void action() {
		switch (state) {
		case WAITING_MSG_STATE: {
			ACLMessage request = myAgent.receiveACLMessage(template, 1);
			if (request != null) {
				this.requestmsg = request;
				state = PREPARE_RESPONSE_STATE;
				//configuramos el template del cancel
				template_cancel = new MessageTemplate(InteractionProtocol.FIPA_REQUEST);
				template_cancel.addConversation(request.getConversationId());
				template_cancel.add_receiver(request.getReceiver());
				
			} else {
				monitor.waiting();// waiting a message.
			}
			
			//comprobar que no me llega un mensaje CANCEL de la conversacion activa
			
	
			break;
			
		}
		case PREPARE_RESPONSE_STATE: {
			
			send_cancel = myAgent.receiveACLMessage(template_cancel, 0);
			
		
			if (send_cancel != null)
			{
			if (send_cancel.getPerformativeInt() == ACLMessage.CANCEL)
				this.state = FINISH_STATE;
			}
			else
			{
			ACLMessage request = this.requestmsg;
			ACLMessage response = null;
			
			state = SEND_RESPONSE_STATE;
			try {
				response = prepareResponse(request);
			} catch (NotUnderstoodException nue) {
				response = request.createReply();
				response.setContent(nue.getMessage());
				response.setPerformative(ACLMessage.NOT_UNDERSTOOD);

			} catch (RefuseException re) {

				response = request.createReply();
				response.setContent(re.getMessage());
				response.setPerformative(ACLMessage.REFUSE);

			}

			this.responsemsg = response;
			}
			break;
		}
		case SEND_RESPONSE_STATE: {
			
			send_cancel = myAgent.receiveACLMessage(template_cancel, 0);
	
			if (send_cancel != null)
			{
			if (send_cancel.getPerformativeInt() == ACLMessage.CANCEL)
				this.state = FINISH_STATE;
			}
			else
			{
			ACLMessage response = this.responsemsg;

			if (response != null) {
				ACLMessage receivedMsg = this.requestmsg;

				response = arrangeMessage(receivedMsg, response);

				response.setSender(myAgent.getAid());

				myAgent.send(response);

				if (response.getPerformativeInt() == ACLMessage.AGREE)
					state = PREPARE_RES_NOT_STATE;
				else {
					state = RESET_STATE;
				}
			} else {
				state = PREPARE_RES_NOT_STATE;
			}
			}
			break;

		}
		case PREPARE_RES_NOT_STATE: {
			
			send_cancel = myAgent.receiveACLMessage(template_cancel, 0);
	
			if (send_cancel != null)
			{
			if (send_cancel.getPerformativeInt() == ACLMessage.CANCEL)
				this.state = FINISH_STATE;
			}
			else
			{

			state = SEND_RESULT_NOTIFICATION_STATE;
			ACLMessage request = this.requestmsg;
			ACLMessage response = this.responsemsg;
			ACLMessage resNotification = null;

			try {
				resNotification = prepareResultNotification(request, response);

			} catch (FailureException fe) {

				resNotification = request.createReply();

				resNotification.setContent(fe.getMessage());
				resNotification.setPerformative(ACLMessage.FAILURE);
			}

			this.resNofificationmsg = resNotification;
			}
			break;
		}
		case SEND_RESULT_NOTIFICATION_STATE: {
			send_cancel = myAgent.receiveACLMessage(template_cancel, 0);
	
			if (send_cancel != null)
			{
			if (send_cancel.getPerformativeInt() == ACLMessage.CANCEL)
				this.state = FINISH_STATE;
			}
			else
			{
			state = RESET_STATE;
			ACLMessage resNotification = this.resNofificationmsg;
			if (resNotification != null) {
				ACLMessage receiveMsg = arrangeMessage(this.requestmsg,
						resNotification);
				receiveMsg.setSender(myAgent.getAid());
				myAgent.send(receiveMsg);
			}

			break;
			}

		}
		case RESET_STATE: {

			state = WAITING_MSG_STATE;
			this.requestmsg = null;
			this.resNofificationmsg = null;
			this.responsemsg = null;
			this.template_cancel = null;
			break;
		}

		}

	}

	// #APIDOC_EXCLUDE_END

	private ACLMessage arrangeMessage(ACLMessage request, ACLMessage reply) {
		reply.setConversationId(request.getConversationId());
		reply.setInReplyTo(request.getReplyWith());
		reply.setProtocol(request.getProtocol());

		reply.setReceiver(request.getSender());

		return reply;
	}

	/**
	 * This method is called when the initiator's message is received that
	 * matches the message template passed in the constructor.
	 * 
	 * @param request
	 *            initial message
	 * @return
	 * @throws NotUnderstoodException
	 * @throws RefuseException
	 */
	protected ACLMessage prepareResponse(ACLMessage request)
			throws NotUnderstoodException, RefuseException {
		return null;
	}

	/**
	 * This method is called after the response has been sent and only when one
	 * of the following two cases arise: the response was an agree message OR no
	 * response message was sent.
	 * 
	 * @param request
	 * @param responder
	 * @return
	 * @throws FailureException
	 */
	protected ACLMessage prepareResultNotification(ACLMessage request,
			ACLMessage responder) throws FailureException {
		return null;
	}

}
