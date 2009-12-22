/**
 * This class implements the Fipa-Contract-Net interaction protocol, Role Responder
 * 
 * @author  Joan Bellver Faus, GTI-IA, DSIC, UPV
 * @version 2009.9.07
 */

package es.upv.dsic.gti_ia.architecture;

import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.architecture.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.core.ACLMessage;

public class FIPAContractNetResponder {

	private final static int WAITING_MSG_STATE = 0;
	private final static int PREPARE_RESPONSE_STATE = 1;
	private final static int SEND_RESPONSE_STATE = 2;
	private final static int RECEIVE_MSG_STATE = 3;
	private final static int PREPARE_RES_NOT_STATE = 4;
	private final static int SEND_RESULT_NOTIFICATION_STATE = 5;
	private final static int RESET_STATE = 6;

	private MessageTemplate template;
	private int state = WAITING_MSG_STATE;
	private QueueAgent myAgent;
	private ACLMessage cfp;
	private ACLMessage propose;
	private ACLMessage accept;
	private ACLMessage reject;
	private ACLMessage resNofificationmsg;

	private Monitor monitor = null;

	/**
	 * Create a new FIPA-Contract-Net interaction protocol, rol responder.
	 * 
	 * @param agent
	 *             is the reference to the Agent Object
	 * @param template is a MessageTemplate, will serve as a filter for receiving the right message
	 */

	public FIPAContractNetResponder(QueueAgent _agent, MessageTemplate _template) {
		myAgent = _agent;
		template = _template;
		this.monitor = myAgent.addMonitor(this);

	}

	 int getState() {
		return this.state;
	}


	 /**
	  *  Run the state machine with the communication protocol
	  */
	public void action() {

		switch (state) {
		case WAITING_MSG_STATE: {
			ACLMessage request = myAgent.receiveACLMessage(template, 1);

			if (request != null) {

				this.cfp = request;
				state = PREPARE_RESPONSE_STATE;
			} else {

				monitor.waiting();// me espero a que llegue un mensaje.

			}
			break;
		}
		case PREPARE_RESPONSE_STATE: {
			ACLMessage request = this.cfp;
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

			this.propose = response;
			break;
		}
		case SEND_RESPONSE_STATE: {
			ACLMessage response = this.propose;

			if (response != null) {

				response = arrangeMessage(this.cfp, response);
				response.setSender(myAgent.getAid());

				myAgent.send(response);
				if (response.getPerformativeInt() == ACLMessage.PROPOSE)
					state = RECEIVE_MSG_STATE;
				else
					// si la performativa es refuse terminamos con el protocolo.
					state = RESET_STATE;
			}

			break;

		}

		case RECEIVE_MSG_STATE: {
			// configuramos un nuevo template para esperar solo al que le hemos
			// enviado la contrapuesta.
			MessageTemplate template2 = new MessageTemplate(
					InteractionProtocol.FIPA_CONTRACT_NET);
			template2.addConversation(this.propose.getConversationId());
			template2.add_receiver(this.propose.getReceiver());
			ACLMessage secondReply = myAgent.receiveACLMessage(template2, 0);

			// esperamos haber si acepta nuestra contrapropuesta
			if (secondReply != null) {

				switch (secondReply.getPerformativeInt()) {
				case ACLMessage.REJECT_PROPOSAL: {
					this.reject = secondReply;
					state = RESET_STATE;
					handleRejectProposal(this.cfp, this.propose, this.reject);
					break;

				}
				case ACLMessage.ACCEPT_PROPOSAL: {
					this.accept = secondReply;
					state = PREPARE_RES_NOT_STATE;
					handleAcceptProposal(this.cfp, this.propose, this.accept);
					break;

				}
				}
				break;
			} else {
				this.monitor.waiting();
				state = RECEIVE_MSG_STATE;
				break;
			}
		}

		case PREPARE_RES_NOT_STATE: {

			state = SEND_RESULT_NOTIFICATION_STATE;

			ACLMessage resNotification = null;

			try {
				resNotification = prepareResultNotification(this.cfp,
						this.propose, this.accept);
			} catch (FailureException fe) {

				resNotification = cfp.createReply();

				resNotification.setContent(fe.getMessage());
				resNotification.setPerformative(ACLMessage.FAILURE);
			}

			this.resNofificationmsg = resNotification;
			break;
		}
		case SEND_RESULT_NOTIFICATION_STATE: {
			state = RESET_STATE;
			ACLMessage resNotification = this.resNofificationmsg;
			if (resNotification != null) {

				ACLMessage receiveMsg = arrangeMessage(this.accept,
						resNotification);
				receiveMsg.setSender(myAgent.getAid());
				myAgent.send(receiveMsg);

			}

			break;

		}
		case RESET_STATE: {

			state = WAITING_MSG_STATE;
			this.cfp = null;
			this.accept = null;
			this.reject = null;
			this.propose = null;
			this.resNofificationmsg = null;
			break;
		}

		}

	}



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
	 * @param cfp
	 *            initial CFP message
	 * @return
	 * @throws NotUnderstoodException
	 * @throws RefuseException
	 */
	protected ACLMessage prepareResponse(ACLMessage cfp)
			throws NotUnderstoodException, RefuseException {
		return null;
	}

	/**
	 * This method is called after the response has been sent and only when one
	 * of the following two cases arise: the response was an agree message OR no
	 * response message was sent.
	 * 
	 * @param cfp
	 *            initial CFP message
	 * @param propose
	 *            propose message
	 * @param accept
	 *            accept message
	 * @return
	 * @throws FailureException
	 */
	protected ACLMessage prepareResultNotification(ACLMessage cfp,
			ACLMessage propose, ACLMessage accept) throws FailureException {
		return null;
	}

	/**
	 * This method is called when REJECT-PROPOSAL is received from the
	 * initiator.
	 * 
	 * @param cfp
	 *            initial CFP message
	 * @param propose
	 *            the PROPOSE message sent back as reply to the initial CFP
	 *            message
	 * @param reject
	 *            the received REJECT_PROPOSAL message
	 */
	protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose,
			ACLMessage reject) {

	}

	/**
	 * This method is called when ACCEPT-PROPOSAL is received from the
	 * initiator.
	 * 
	 * @param cfp
	 *            initial CFP message
	 * @param propose
	 *            the PROPOSE message sent back as reply to the initial CFP
	 *            message
	 * @param accept
	 *            the received ACCEPT_PROPOSAL message.
	 */
	protected void handleAcceptProposal(ACLMessage cfp, ACLMessage propose,
			ACLMessage accept) {

	}

}
