package es.upv.dsic.gti_ia.core;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Ricard Lopez Fogues
 * @author Sergio Pajares Ferrando
 * @author Joan Bellver Faus
 * 
 * This class defines an new agents template, extending of BaseAgent.
 */

public abstract class SingleAgent extends BaseAgent {

	//private ArrayList<ACLMessage> messageList;
	private LinkedBlockingQueue<ACLMessage> messageList;

	/**
	 * Creates a new SingleAgent
	 * 
	 * @param aid
	 *            Agent Id
	 * @param connection
	 *            Connection the agent will use
	 * @throws Exception
	 *             if agent id already exists on the platform
	 */
	public SingleAgent(AgentID aid) throws Exception {
		super(aid);
		//messageList = new ArrayList<ACLMessage>();
		messageList = new LinkedBlockingQueue<ACLMessage>();
	}

	/**
	 * Receives a AclMessage taking into account a blocking reception
	 * @return an ACLMessage
	 * @throws InterruptedException 
	 * @throws Exception
	 */
	public final ACLMessage receiveACLMessage() throws InterruptedException {

		/*boolean condition = false;
		ACLMessage msg = new ACLMessage(0);
		int i = 0;
		
		while(!condition) {
			
				msg = messageList.get(i);
	
				if (msg != null) {
						condition = true;
				}
			
		}
		messageList.remove(msg);
		return msg;*/
		return messageList.take();


	}

	private synchronized void writeQueue(ACLMessage msg) {
		messageList.add(msg);
	}

	@Override
	public void onMessage(ACLMessage msg) {
		this.writeQueue(msg);
	}
}