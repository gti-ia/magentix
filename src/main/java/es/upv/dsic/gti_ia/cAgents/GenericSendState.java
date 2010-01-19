package es.upv.dsic.gti_ia.cAgents;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import es.upv.dsic.gti_ia.core.ACLMessage;

public class GenericSendState extends SendState{

	public GenericSendState(String n) {
		super(n);
	}

	@Override
	protected ACLMessage run(CProcessor myProcessor, ACLMessage lastReceivedMessage) {
		this.messageTemplate.setSender(myProcessor.getMyAgent().getAid());
		this.messageTemplate.setConversationId(myProcessor.getConversationID());
		return this.messageTemplate;
	}

	@Override
	protected String getNext(CProcessor myProcessor,
			ACLMessage lastReceivedMessage) {
		String next = "";
		Set<String> transitions = new HashSet<String>();
		transitions = myProcessor.getTransitionTable().getTransitions(this.getName());
		Iterator<String> it = transitions.iterator();
		if (it.hasNext()) {
	        // Get element
	        next = it.next();
	    }
		return next;
	}
}