package es.upv.dsic.gti_ia.trace;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;

public class TraceInteract {
	
	/**
	 * Publish a tracing service so that other agents can request it
	 * and receive the corresponding trace events 
	 */
	static public void publishTracingService(){
		/* Oops! This still has to be done :P */
	}
	
	/**
	 * Unpublish a previously published tracing service so that other agents cannot
	 * receive the corresponding trace events 
	 */
	static public void unpublishTracingService(){
		/* Oops! This still has to be done :P */
	}
	
	/**
	 * Request a tracing service
	 */
	static public void requestTracingService(BaseAgent requesterAgent, String eventType, AgentID originEntity) {
		/**
		 * Building a ACLMessage
		 */
		ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);
		AgentID tms_aid = new AgentID("qpid://tm@localhost:8080");
		
		msg.setReceiver(tms_aid);
		msg.setSender(requesterAgent.getAid());
		msg.setLanguage("ACL");
		msg.setContent(eventType + "#" + originEntity.toString());
		/**
		 * Sending a ACLMessage
		 */
		requesterAgent.send(msg);
	}
	
	/**
	 * Request a tracing service
	 */
	static public void requestTracingService(BaseAgent requesterAgent, String eventType) {
		/**
		 * Building a ACLMessage
		 */
		ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);
		AgentID tms_aid = new AgentID("qpid://tm@localhost:8080");
		
		msg.setReceiver(tms_aid);
		msg.setSender(requesterAgent.getAid());
		msg.setLanguage("ACL");
		msg.setContent(eventType + "#any");
		/**
		 * Sending a ACLMessage
		 */
		requesterAgent.send(msg);
	}
	
	/**
	 * Cancel subscription to a tracing service
	 */
	static public void cancelTracingServiceSubscription(BaseAgent requesterAgent, String eventType, AgentID originEntity) {
		/**
		 * Building a ACLMessage
		 */
		ACLMessage msg = new ACLMessage(ACLMessage.CANCEL);
		AgentID tms_aid = new AgentID("qpid://tm@localhost:8080");
		
		msg.setReceiver(tms_aid);
		msg.setSender(requesterAgent.getAid());
		msg.setLanguage("ACL");
		msg.setContent(eventType + "#" + originEntity.toString());
		/**
		 * Sending a ACLMessage
		 */
		requesterAgent.send(msg);
	}
	
	/**
	 * Cancel subscription to a tracing service
	 */
	static public void cancelTracingServiceSubscription(BaseAgent requesterAgent, String eventType) {
		/**
		 * Building a ACLMessage
		 */
		ACLMessage msg = new ACLMessage(ACLMessage.CANCEL);
		AgentID tms_aid = new AgentID("qpid://tm@localhost:8080");
		
		msg.setReceiver(tms_aid);
		msg.setSender(requesterAgent.getAid());
		msg.setLanguage("ACL");
		msg.setContent(eventType + "#any");
		/**
		 * Sending a ACLMessage
		 */
		requesterAgent.send(msg);
	}
}
