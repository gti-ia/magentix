package TraceTest_1;

//import java.util.concurrent.LinkedBlockingQueue;
//import org.apache.qpid.transport.MessageTransfer;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.TraceEvent;
//import es.upv.dsic.gti_ia.core.TracingService;

import es.upv.dsic.gti_ia.trace.*;

/**
 * ConsumerAgent class defines the structure of a consumer BaseAgent
 * 
 * Subscribes to the event_type "TRACE_TEST" and waits for 10 seconds for events to arrive.
 * When a TRACE_TEST event arrives, ConsumerAgent prints its content on the screen.
 * After the 10 seconds, ConsumerAgent unsubscribes from the event type and then ends its execution
 * 
 * @author Luis Burdalo - lburdalo@dsic.upv.es
 */
public class Subscriber extends BaseAgent{
	private final int MAX_SERVICES = 10;
//	private TracingService[] TracingServices=new TracingService[MAX_SERVICES];
	private boolean finish;
	private Integer[] EventsReceived = new Integer[MAX_SERVICES];
	AgentID coordinatorAid;
//	LinkedBlockingQueue<MessageTransfer> internalQueue;

	public Subscriber(AgentID aid) throws Exception {
		super(aid);
		/**
		 * Initializing tracing services and stuff
		 */
		System.out.println("[SUBSCRIBER]: Basic test start...");
		
		System.out.println("[SUBSCRIBER]: Subscribing to tracing services:");
		for (int i=0; i < MAX_SERVICES; i++){
			//TracingServices[i]=new TracingService("SRV_" + i, "Tracing Service " + i);
			EventsReceived[i]=0;
			System.out.println("[SUBSCRIBER]: Requesting SRV_"+i);
			TraceInteract.requestTracingService(this, "SRV_" + i);
//			try {
//				Thread.sleep(500);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		finish=false;
		coordinatorAid = new AgentID("qpid://publisher@localhost:8080");
	}

	public void execute() {
		int i;
		logger.info("[SUBSCRIBER]: Executing...");
		/**
		 * This agent has no definite work. Wait infinitely the arrival of new
		 * messages.
		 */
    	while (!finish) {
//    		
//			try {
//				System.out.println("[CONSUMER " + getName() + "]: Waiting...");
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
    	
    	System.out.println("[SUBSCRIBER]: Now unsubscribing from tracing services...");
    	for (i=0; i < MAX_SERVICES; i++){
			TraceInteract.cancelTracingServiceSubscription(this, "SRV_" + i);
		}
    	System.out.println("[SUBSCRIBER]: Done!");
		
    	for (i=0; i < MAX_SERVICES; i++){
			ACLMessage coordination_msg = new ACLMessage(ACLMessage.INFORM);
			coordination_msg.setReceiver(coordinatorAid);
			coordination_msg.setContent("RECV:" + i + ":" + EventsReceived[i]);
			send(coordination_msg);
		}
		
		System.out.println("[PUBLISHER " + getName() + "]: Bye!");
    	
    	try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logger.info("[CONSUMER]: Bye!");
		
	}

	public void onTraceEvent(TraceEvent tEvent) {
		/**
		 * When a trace event arrives, its shows it on the screen
		 */
		//logger.info("[CONSUMER " + getName() +"]: Trace event received by onTraceEvent: " + tEvent.toReadableString());
		System.out.println("[SUBSCRIBER]: Event received (" + tEvent.getTracingService() + ")");
		EventsReceived[Integer.parseInt(tEvent.getTracingService().substring("SRV_".length()))]++;
	}
	
	public void onMessage(ACLMessage msg){
		System.out.println("[SUBSCRIBER]: Message: " + msg.getPerformative() + " " + msg.getContent());
		
		switch (msg.getPerformativeInt()){
			case ACLMessage.REQUEST:
				if (msg.getContent().contentEquals("STOP")){
					finish=true;
				}
				break;
		}
	}
}
