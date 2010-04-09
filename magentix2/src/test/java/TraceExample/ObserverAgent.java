package TraceExample;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.TraceEvent;

public class ObserverAgent extends BaseAgent {
	
	ObserverAgent(AgentID aid) throws Exception {
		super(aid);
		//requestTracingService("NEW_AGENT");
		//requestTracingService("MESSAGE_SENT");
		
		Map<String, Object> arguments = new HashMap<String, Object>();
		
		arguments.put("x-match", "any");
    	arguments.put("event_type", "MESSAGE_SENT");
    	    			    	
    	this.session.exchangeBind(this.getAid().toString()+".trace", "amq.match", "MESSAGE_SENT" + "#" + "any", arguments);
    	logger.info("[OBSERVER]: binding " + this.getAid().toString()+".trace");
	}
	
	public void execute() {
		Random rnd_generator = new Random(System.currentTimeMillis());
		
		logger.info("[OBSERVER]: Executing, I'm " + getName());
		
		
		
//		AgentID receiver = new AgentID("tm");
		
		while (true) {
//			for (int i=0; i < 10; i++){
//				/**
//				 * Building a ACLMessage
//				 */
//				ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
//				msg.setReceiver(receiver);
//				msg.setSender(this.getAid());
//				msg.setLanguage("ACL");
//				msg.setContent("Hello, I'm " + getName());
//				/**
//				 * Sending a ACLMessage
//				 */
//				send(msg);
//			}
		}
		
//		AgentID receiver = new AgentID("consumer");

		/**
		 * Building a ACLMessage
		 */
//		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
//		msg.setReceiver(receiver);
//		msg.setSender(this.getAid());
//		msg.setLanguage("ACL");
//		msg.setContent("Hello, I'm " + getName());
		/**
		 * Sending a ACLMessage
		 */
//		send(msg);
		
	}
	
	public void onMessage(ACLMessage msg) {
		/**
		 * When a message arrives, its shows on screen
		 */
		logger.info("[OBSERVER]: Mensaje received in " + this.getName()
				+ " agent, by onMessage: " + msg.getContent());
	}
	
	public void onTraceEvent(TraceEvent tEvent) {
		/**
		 * When a trace event arrives, its shows on screen
		 */
		logger.info("[OBSERVER]: Trace event received in " + this.getName()
				+ " agent, by onTraceEvent: " + tEvent.toReadableString());
	}
}
