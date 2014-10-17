package es.upv.dsic.gti_ia.cAgents;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Queue;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.MessageFilter;

public class PendingQueueRepository {

	private HashMap<String, QueueWithTimestamp> queueMap;
	private long deltaToExpire;
	private long intervalToClean;
	private Date referenceDate;

	public PendingQueueRepository(long deltaToExpire, long intervalToClean) {
		this.deltaToExpire = deltaToExpire;
		this.intervalToClean = intervalToClean;
		queueMap = new HashMap<String, QueueWithTimestamp>();
		referenceDate = new Date();
	}

	public void addMessage(ACLMessage msg) {
		if (!hasQueueWithCid(msg)) {
			queueMap.put(msg.getConversationId(), new QueueWithTimestamp());
			queueMap.get(msg.getConversationId()).addMessage(msg);
		} else {
			queueMap.get(msg.getConversationId()).addMessage(msg);
		}
		if(new Date().getTime()- referenceDate.getTime() > intervalToClean){
			cleanRepository(getDeltaToExpire());
			referenceDate = new Date();
		}
	}

	private boolean hasQueueWithCid(ACLMessage msg) {
		return hasQueueWithCid(msg.getConversationId());
	}

	private boolean hasQueueWithCid(String conversationId) {
		return queueMap.containsKey(conversationId);
	}

	private boolean hasQueue(MessageFilter template) {
		ArrayList<QueueWithTimestamp> queuesWithTimestamp = new ArrayList<QueueWithTimestamp>(
				queueMap.values());
		for (QueueWithTimestamp theQueue : queuesWithTimestamp) {
			if (template.compareHeaders(theQueue.getQueue().peek())) {
				return true;
			}
		}
		return false;
	}

	public ArrayList<QueueWithTimestamp> popQueues(MessageFilter template) {
		ArrayList<QueueWithTimestamp> queuesWithTimestamp = new ArrayList<QueueWithTimestamp>(
				queueMap.values());
		ArrayList<QueueWithTimestamp> matchingQueues = new ArrayList<QueueWithTimestamp>();

		for (QueueWithTimestamp theQueue : queuesWithTimestamp) {
			if (template.compareHeaders(theQueue.getQueue().peek())) {
				matchingQueues.add(theQueue);
			}
		}
		return matchingQueues;
	}

	public Queue<ACLMessage> popQueue(String cid) {
		if (hasQueueWithCid(cid)) {
			return getQueueMap().get(cid).getQueue();
		} else {
			return null;
		}
	}

	public synchronized void cleanRepository(long delta) {
		ArrayList<String> cidsToClean = new ArrayList<String>();
		for (String cId : queueMap.keySet()) {
			if (!queueMap.get(cId).checkTimestamp(delta)) {
				cidsToClean.add(cId);
			}
		}
		if (!cidsToClean.isEmpty()) {
			for (String cid : cidsToClean) {
				queueMap.remove(cid);
			}
		}
	}

	public synchronized HashMap<String, QueueWithTimestamp> getQueueMap() {
		return queueMap;
	}

	public long getDeltaToExpire() {
		return deltaToExpire;
	}

	public void setDeltaToExpire(long deltaToExpire) {
		this.deltaToExpire = deltaToExpire;
	}

	public long getIntervalToClean() {
		return intervalToClean;
	}

	public void setIntervalToClean(long intervalToClean) {
		this.intervalToClean = intervalToClean;
	}
}
