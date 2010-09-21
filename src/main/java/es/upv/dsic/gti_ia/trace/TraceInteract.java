package es.upv.dsic.gti_ia.trace;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;

public class TraceInteract {
	/**
	 * Publish a tracing service so that other agents can request it
	 * and receive the corresponding trace events.
	 * 
	 * The applicant agent must specify a name for the tracing service,
	 * the corresponding type of the trace events which will be provided
	 * by the service and a description of the tracing service.
	 * 
	 * The description is now a human-readable/human-understandable string to describe the service,
	 * but in future versions, it should contain an entity-readable/entity-understandable
	 * description, in order to let tracing entities discover tracing services and decide which
	 * of them are interesting for them.
	 */
	static public void publishTracingService(BaseAgent applicantAgent, String serviceName, String description){
		/**
		 * Building a ACLMessage
		 */
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		AgentID tms_aid = new AgentID("qpid://tm@localhost:8080");
		String body;
		
		msg.setReceiver(tms_aid);
		msg.setSender(applicantAgent.getAid());
		msg.setLanguage("ACL");
		body = "publish" + "#" + serviceName.length() + "#" + serviceName + description;
		//System.out.println("Publication request: " + body);
		msg.setContent(body);
		/**
		 * Sending a ACLMessage
		 */
		applicantAgent.send(msg);
	}
	
	/**
	 * Unpublish a previously published tracing service so that other agents cannot
	 * subscribe nor receive the corresponding trace events 
	 */
	static public void unpublishTracingService(BaseAgent applicantAgent, String name){
		/**
		 * Building a ACLMessage
		 */
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		AgentID tms_aid = new AgentID("qpid://tm@localhost:8080");
		String body;
		
		msg.setReceiver(tms_aid);
		msg.setSender(applicantAgent.getAid());
		msg.setLanguage("ACL");
		body = "unpublish" + "#" + name;
		//System.out.println(body);
		msg.setContent(body);
		/**
		 * Sending a ACLMessage
		 */
		applicantAgent.send(msg);
	}
	
	/**
	 * Request a tracing service
	 */
	static public void requestTracingService(BaseAgent requesterAgent, String name, AgentID originEntity) {
		/**
		 * Building a ACLMessage
		 */
		ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);
		AgentID tms_aid = new AgentID("qpid://tm@localhost:8080");
		
		msg.setReceiver(tms_aid);
		msg.setSender(requesterAgent.getAid());
		msg.setLanguage("ACL");
		msg.setContent(name + "#" + originEntity.toString());
		/**
		 * Sending a ACLMessage
		 */
		requesterAgent.send(msg);
	}
	
	/**
	 * Request a tracing service
	 */
	static public void requestTracingService(BaseAgent requesterAgent, String name) {
		/**
		 * Building a ACLMessage
		 */
		ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);
		AgentID tms_aid = new AgentID("qpid://tm@localhost:8080");
		
		msg.setReceiver(tms_aid);
		msg.setSender(requesterAgent.getAid());
		msg.setLanguage("ACL");
		msg.setContent(name + "#any");
		/**
		 * Sending a ACLMessage
		 */
		requesterAgent.send(msg);
	}
	
	/**
	 * Request all tracing services available at this time
	 */
	static public void requestAllTracingServices(BaseAgent requesterAgent) {
		/**
		 * Building a ACLMessage
		 */
		ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);
		AgentID tms_aid = new AgentID("qpid://tm@localhost:8080");
		
		msg.setReceiver(tms_aid);
		msg.setSender(requesterAgent.getAid());
		msg.setLanguage("ACL");
		msg.setContent("all");
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
