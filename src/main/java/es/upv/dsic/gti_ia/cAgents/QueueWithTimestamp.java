package es.upv.dsic.gti_ia.cAgents;

import java.util.LinkedList;
import java.util.Queue;

import es.upv.dsic.gti_ia.core.ACLMessage;

public class QueueWithTimestamp {
	private long timestamp;
	private Queue<ACLMessage> theQueue;

	public QueueWithTimestamp() {
		theQueue = new LinkedList<ACLMessage>();
		timestamp = System.currentTimeMillis();
	}

	public Queue<ACLMessage> getQueue() {
		return theQueue;
	}

	public boolean checkTimestamp(long delta) {
		long nowMillis = System.currentTimeMillis();
		return nowMillis - timestamp <= delta;
	}

	public void addMessage(ACLMessage msg) {
		theQueue.add(msg);
		timestamp = System.currentTimeMillis();
	}
}