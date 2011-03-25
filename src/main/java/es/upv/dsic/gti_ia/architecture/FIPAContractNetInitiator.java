package es.upv.dsic.gti_ia.architecture;



import java.util.*;


import es.upv.dsic.gti_ia.architecture.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;



/**
 * This class implements the FIPA-Contract-Net interaction protocol, Role Initiator
 * 
 * @author  Joan Bellver Faus, GTI-IA, DSIC, UPV
 * @version 2009.9.07
 */
public class FIPAContractNetInitiator {

	private Monitor monitor = null;

	private final static int PREPARE_MSG_STATE = 0;
	private final static int SEND_MSG_STATE = 1;
	private final static int RECEIVE_REPLY_STATE = 2;
	private final static int SEND_2ND_REPLY_STATE = 3;
	private final static int RECEIVE_2ND_REPLY_STATE = 4;
	private final static int ALL_REPLIES_RECEIVED_STATE = 5;
	private final static int ALL_RESULT_NOTIFICATION_RECEIVED_STATE = 6;

	private MessageTemplate template = null;
	private int state = PREPARE_MSG_STATE;
	public QueueAgent myAgent;
	private ACLMessage requestmsg;
	private ACLMessage requestsentmsg;

	private boolean finish = false;
	String conversationID = null;
	private long timeout = -1;
	private long endingtime = 0;

	ArrayList<ACLMessage> accepted = new ArrayList<ACLMessage>();
	ArrayList<ACLMessage> respuestas = new ArrayList<ACLMessage>();

	private int nSends = 0;
	private int nReads = 0;

	/**
	 * Creates a new FIPA-Contract-Net interaction protocol, initiator role.
	 * @param agent agent is the reference to the QueueAgent Object 
	 * @param msg initial message
	 */
	public FIPAContractNetInitiator(QueueAgent agent, ACLMessage msg) {
		myAgent = agent;
		requestmsg = msg;
		this.monitor = myAgent.addMonitor(this);

	}

	/**
	 * Returns the agent.
	 * @return QueueAgent 
	 */
	QueueAgent getQueueAgent()
	{
		return this.myAgent; 

	}

	/**
	 * This method reports if the protocol has been finished
	 * 
	 * @return value a boolean value is returned, true: the protocol has finished, false: the protocol even has not finished
	 */
	boolean finished() {
		return this.finish;
	}


	int getState()
	{
		return this.state;
	}


	/**
	 *  Runs the state machine with the communication protocol
	 */
	public void action() {

		switch (state) {
		case PREPARE_MSG_STATE: {

			ACLMessage msg = prepareRequest(this.requestmsg);
			this.requestsentmsg = msg;
			state = SEND_MSG_STATE;
			break;
		}
		case SEND_MSG_STATE: {

			ACLMessage request = this.requestsentmsg;
			if (request == null) {
				// End of protocol
				this.finish = true;
				break;
			} else {
				//Add the agent who sent him, we do transparent to the user
				
				//Add the agentID 

				//Sending agents are added temporarily
				@SuppressWarnings("unchecked")
				ArrayList<AgentID> agentes = (ArrayList<AgentID>)request.getReceiverList().clone();
				request.setSender(myAgent.getAid());
				
				
			
				template = new MessageTemplate(InteractionProtocol.FIPA_CONTRACT_NET);
				for (es.upv.dsic.gti_ia.core.AgentID agent : agentes) {


						
					//For each sending agent a new idconversation is added 
					conversationID = "C" + hashCode() + "_"
					+ System.currentTimeMillis();
					request.setConversationId(conversationID);
					template.add_receiver(agent);
					template.addConversation(conversationID);
					myAgent.setActiveConversation(conversationID);

					request.setReceiver(agent);
					myAgent.send(request);


				}

				this.nSends = agentes.size();


				//The timeout message is added
				Date d = request.getReplyByDate();
				if (d != null)
					timeout = d.getTime() - (new Date()).getTime();
				else
					timeout = -1;
				endingtime = System.currentTimeMillis() + timeout;

				state = RECEIVE_REPLY_STATE;

			}
			break;

		}
		case RECEIVE_REPLY_STATE: {

			// we will wait until a timeout or that all agents have finished


			//template matching with all messages that having an explicit conversationId, the protocol is a cfp, and the message
			//is an know agent
			ACLMessage firstReply = myAgent.receiveACLMessage(template,0);



			if (firstReply != null) {

				switch (firstReply.getPerformativeInt()) {
				case ACLMessage.PROPOSE: {

					respuestas.add(firstReply);
					// aceptados.add(firstReply);
					handlePropose(firstReply, accepted);
			
					//If all messages have arrived, out of this state
					if (this.nSends <= this.respuestas.size()) {
						state = SEND_2ND_REPLY_STATE;
						handleAllResponses(respuestas, accepted);
					}
					break;
				}
				case ACLMessage.REFUSE: {
					respuestas.add(firstReply);
					handleRefuse(firstReply);
					if (this.nSends <= this.respuestas.size()) {
						state = SEND_2ND_REPLY_STATE;
						handleAllResponses(respuestas, accepted);
					}
					break;
				}
				case ACLMessage.NOT_UNDERSTOOD:
					respuestas.add(firstReply);
					{
						handleNotUnderstood(firstReply);
						if (this.nSends <= this.respuestas.size()) {
							state = SEND_2ND_REPLY_STATE;
							handleAllResponses(respuestas, accepted);
						}
						break;

					}
				case ACLMessage.FAILURE: {
					respuestas.add(firstReply);
					handleFailure(firstReply);
					if (this.nSends <= this.respuestas.size()) {
						state = SEND_2ND_REPLY_STATE;
						handleAllResponses(respuestas, accepted);
					}
					break;
				}
				default: {
					respuestas.add(firstReply);
					handleOutOfSequence(firstReply);
					if (this.nSends <= this.respuestas.size()) {
						state = SEND_2ND_REPLY_STATE;
						handleAllResponses(respuestas, accepted);
					}
					break;

				}
				}
				break;
			} else {

				
				//If a timeout is added
				if (timeout > 0) {
					long blocktime = endingtime - System.currentTimeMillis();

					if (blocktime <= 0)
						//we stopped reading messages, the timeout is over
					{


						handleAllResponses(respuestas, accepted);
						state = SEND_2ND_REPLY_STATE;
						break;
					} else {


						
						//check if there are messages to read  
						if (this.nSends > this.respuestas.size()) {

							this.monitor.waiting();
							state = RECEIVE_REPLY_STATE;

							break;
						} else
						{

							handleAllResponses(respuestas, accepted);
							state = SEND_2ND_REPLY_STATE;
							break;
						}

					}

				} else {
				
					//All messages
					if (this.nSends < respuestas.size())
					{//waiting if there are messages to read
						this.monitor.waiting();
						state = RECEIVE_REPLY_STATE;// state =
						// ALL_REPLIES_RECEIVED_STATE;
						break;
					} else
					{//If all messages have been read, change status
						handleAllResponses(respuestas, accepted);
						state = SEND_2ND_REPLY_STATE;// state =
						// ALL_REPLIES_RECEIVED_STATE;
						break;
					}
				}
			}

		}
		case SEND_2ND_REPLY_STATE: {

			// resetemaos el template quitando los receivers y los
			// conversationID
			
			//The template is reset, deleting the receivers and conversationID's
			template.deleteAllConversation();
			template.deleteAllReceiver();
			
			
			//remove the active conversations
			myAgent.deleteAllActiveConversation();

			//the message is searched in accepted arrays and then is sent
			for (ACLMessage mensaje : accepted) {

				if (mensaje.getConversationId().equals("")) {
					conversationID = "C" + hashCode() + "_"
					+ System.currentTimeMillis();
					mensaje.setConversationId(conversationID);
				} else {
					conversationID = mensaje.getConversationId();
				}

				template.add_receiver(mensaje.getReceiver());
				template.addConversation(conversationID);
				myAgent.setActiveConversation(conversationID);

				mensaje.setSender(myAgent.getAid());

				myAgent.send(mensaje);
				// send

			}

			//If response that we send is rejected, then we terminated

			state = RECEIVE_2ND_REPLY_STATE;
		}
		case RECEIVE_2ND_REPLY_STATE: {


			//waiting for a second message if exist any accepted message
			if (this.accepted.size() == 0)
				state = ALL_RESULT_NOTIFICATION_RECEIVED_STATE;
			else {
				ACLMessage secondReply = myAgent
				.receiveACLMessage(template,0);

				if (secondReply != null) {
					this.nReads++;
					switch (secondReply.getPerformativeInt()) {
					case ACLMessage.INFORM: {
						handleInform(secondReply);
						break;

					}
					case ACLMessage.FAILURE: {
						handleFailure(secondReply);
						break;

					}
					default: {
						handleOutOfSequence(secondReply);
						break;
					}
					}
					break;
				} else {
				
					//waiting the proposals
					if (this.nReads < this.accepted.size())
					{
						this.monitor.waiting();
						state = RECEIVE_2ND_REPLY_STATE;
						break;
					} else {
						state = ALL_RESULT_NOTIFICATION_RECEIVED_STATE;
					}
				}

			}
		}
		case ALL_REPLIES_RECEIVED_STATE: {
			state = ALL_RESULT_NOTIFICATION_RECEIVED_STATE;
			break;
		}
		case ALL_RESULT_NOTIFICATION_RECEIVED_STATE: {

			this.finish = true;
			this.requestmsg = null;
			this.myAgent.deleteMonitor(this);
			//this.myAgent.deleteAllActiveConversation();
			for (String conversation : this.template.getList_Conversation())
				this.myAgent.deleteActiveConversation(conversation);

			break;
		}
		}

	}

	/**
	 * This method must returns the ACLMessage to be sent. This default
	 * implementation just return the ACLMessage object passed in the
	 * constructor. Programmer might override the method in order to return a
	 * different ACLMessage. Note that for this simple version of protocol, the
	 * message will be just send to the first receiver set.
	 * 
	 * @param msg
	 *            the ACLMessage object passed in the constructor.
	 * @return a ACLMessage.
	 */
	protected ACLMessage prepareRequest(ACLMessage msg) {
		return msg;
	}

	/**
	 * This method is called when a propose message is received.
	 * @param msg the received propose message.
	 * @param accepted the list of ACCEPT/REJECT_PROPOSAL to be sent back.
	 */
	protected void handlePropose(ACLMessage msg, ArrayList<ACLMessage> accepted) {
	}

	/**
	 * This method is called when a refuse message is received.
	 * @param msg the received refuse message
	 */
	protected void handleRefuse(ACLMessage msg) {
	}
	/**
	 * This method is called when a NotUnderstood message is received.
	 * @param msg the received NotUnderstood message
	 */
	protected void handleNotUnderstood(ACLMessage msg) {

	}
	/**
	 * This method is called when a inform message is received.
	 * @param msg the received inform message
	 */
	protected void handleInform(ACLMessage msg) {

	}
	/**
	 * This method is called when a failure message is received.
	 * @param msg the received failure message
	 */
	protected void handleFailure(ACLMessage msg) {

	}
	/**
	 * This method is called when a unexpected message is received.
	 * @param msg the received message
	 */
	protected void handleOutOfSequence(ACLMessage msg) {

	}
	/**
	 * This method is called when all the responses have been collected or when the timeout is expired
	 * @param msg the received refuse message
	 */
	protected void handleAllResponses(ArrayList<ACLMessage> responses, ArrayList<ACLMessage> accepted) {

	}

}
