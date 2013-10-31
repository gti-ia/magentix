package TestAgentsConnection;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;

/**

 */
public class SenderAgentLoop extends BaseAgent {

	private String receiver, controller;
	private ACLMessage msg;
	private AtomicBoolean end = new AtomicBoolean(false);
	private CyclicBarrier barrier;
	private CountDownLatch finished;

	public SenderAgentLoop(AgentID aid, String receiver, String controller,
			CyclicBarrier barrier, CountDownLatch finished) throws Exception {
		super(aid);
		this.receiver = receiver;
		this.controller = controller;
		this.barrier = barrier;
		this.finished = finished;

	}

	public void execute() {
		// logger.info("Executing, I'm " + getName());

		// It informs the controller that the agent is running
		try {
			barrier.await();
		} catch (InterruptedException e) {

			e.printStackTrace();
		} catch (BrokenBarrierException e) {

			e.printStackTrace();
		}

		while (!end.get()) {
		}

		logger.info("acaba " + getName());

		// It notifies that the agent cycle is finished
		msg = new ACLMessage(ACLMessage.INFORM);
		msg.setReceiver(new AgentID(controller));
		msg.setSender(this.getAid());
		msg.setLanguage("ACL");
		msg.setContent("agent end");

		send(msg);

		finished.countDown();

		logger.info("Remaining " + finished.getCount());
	}

	public void onMessage(ACLMessage msg) {

		if (msg.getContent().equals("end")) {
			logger.info("Receiving end from controller");
			// It ends the agent cycle
			end.set(true);
		} else {

			// It receives the message of the controller to start the task
			if (msg.getContent().equals("Start")) {

				// logger.info("Start finished, sending to... " + receiver);

				// The agent sends a message to the next agent
				msg = new ACLMessage(ACLMessage.REQUEST);
				msg.setReceiver(new AgentID(receiver));
				msg.setSender(this.getAid());
				msg.setLanguage("ACL");
				msg.setContent(getName());

				send(msg);

			} else {
				// The agent receives from the previous agent
				this.msg = msg;

				logger.info("Received from: " + msg.getSender());

				// It notifies the controller about the reception of the message
				msg = new ACLMessage(ACLMessage.INFORM);
				msg.setReceiver(new AgentID(controller));
				msg.setSender(this.getAid());
				msg.setLanguage("ACL");
				msg.setContent("Received from " + msg.getReceiver());

				send(msg);

			}
		}

	}

}
