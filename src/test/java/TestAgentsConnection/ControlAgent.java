package TestAgentsConnection;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;

public class ControlAgent extends BaseAgent {

	private CountDownLatch finished;

	private ACLMessage msg;

	private CountDownLatch received;

	CyclicBarrier agentsSt;

	ReentrantLock mutex;

	final Condition allFinished;

	private int maxAgents;

	public ControlAgent(AgentID aid, int maxAgents, Condition allFinished,
			CyclicBarrier agentsSt, ReentrantLock mutex, CountDownLatch finished)
			throws Exception {
		super(aid);
		this.maxAgents = maxAgents;
		this.agentsSt = agentsSt;
		this.allFinished = allFinished;
		this.mutex = mutex;
		this.received = new CountDownLatch(maxAgents);
		this.finished = finished;

	}

	public void execute() {
		// logger.info("Starting, I'm " + getName());

		// It awaits for the execution of agents
		try {
			agentsSt.await();
		} catch (InterruptedException e) {

			e.printStackTrace();
		} catch (BrokenBarrierException e) {

			e.printStackTrace();
		}

		// When all are ready, it sends the start message
		sendAll();

		try {
			logger.info("Controller awaits...");
			// It awaits in the barrier for the confirmation...
			received.await();
		} catch (InterruptedException e) {

			e.printStackTrace();
		}

		logger.info("Controller sending end to agents...");
		
		for (int i = 0; i < maxAgents; i++) {
			// logger.info("Sending end message");
			msg = new ACLMessage(ACLMessage.REQUEST);
			msg.setReceiver(new AgentID("SendAg" + i));
			msg.setSender(this.getAid());
			msg.setLanguage("ACL");
			msg.setContent("end");

			send(msg);

		}

		//Awaits the finish of all sender agents
		try {
			finished.await();
		} catch (InterruptedException e) {

			e.printStackTrace();
		}

		logger.info("Controller ending...");

		mutex.lock();

		allFinished.signal();

		mutex.unlock();

	}

	public void onMessage(ACLMessage msg) {
		if (msg.getContent().equals("agent end")) {

			logger.info("Mensaje received in " + this.getName()
					+ ", agent end = " + msg.getSender());

		} else {

			logger.info("Mensaje received in " + this.getName()
					+ ", task completed by : " + msg.getSender());
			received.countDown();
			logger.info("Quedan " + received.getCount() + "/" + maxAgents);

		}

	}

	public void sendAll() {

		for (int i = 0; i < maxAgents; i++) {

			msg = new ACLMessage(ACLMessage.REQUEST);
			msg.setReceiver(new AgentID("SendAg" + i));
			msg.setSender(this.getAid());
			msg.setLanguage("ACL");
			msg.setContent("Start");
			/**
			 * Sending a ACLMessage
			 */
			send(msg);

		}

	}

}
