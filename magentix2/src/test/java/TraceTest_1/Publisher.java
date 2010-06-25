package TraceTest_1;

import java.util.Random;
import java.lang.System;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.TraceEvent;
import es.upv.dsic.gti_ia.core.TracingService;
import es.upv.dsic.gti_ia.trace.TraceInteract;

/**
 * SenderAgent class defines the structure of a sender BaseAgent
 * 
 * Sends a trace event of the type "TRACE_TEST" each 1 second
 * 
 * @author Luis Burdalo - lburdalo@dsic.upv.es
 */
public class Publisher extends BaseAgent {
	private final int MAX_SERVICES = 10;
	private TracingService[] TracingServices=new TracingService[MAX_SERVICES];
	private Integer[] EventsSent = new Integer[MAX_SERVICES];
	private boolean finish;
	private Random generator;
	private AgentID coordinatorAid;
	
	public Publisher(AgentID aid) throws Exception {
		super(aid);
		
		int i;
		/**
		 * Initializing tracing services and stuff
		 */
		for (i=0; i < MAX_SERVICES; i++){
			TracingServices[i]=new TracingService("SRV_" + i, "Tracing Service " + i);
			EventsSent[i]=0;
		}
		finish=false;
		generator = new Random(System.currentTimeMillis());
		coordinatorAid = new AgentID("qpid://publisher@localhost:8080");
		
		System.out.println("[PUBLISHER]: Basic test start...");
		
		System.out.println("[PUBLISHER]: Publishing " + MAX_SERVICES + " tracing services:");
		for (i=0; i < MAX_SERVICES; i++){
			System.out.println("[PUBLISHER]: Publishing " + (i+1) + " of "+ MAX_SERVICES);
			TraceInteract.publishTracingService(this, TracingServices[i].getName(), TracingServices[i].getDescription());
		}
		System.out.println("[PUBLISHER]: Done!");
	}

	public void execute() {
		TraceEvent tEvent;
		String serviceName;
		int i;
		
//		try {
//			// Wait 2 seconds for Subscriber to subscribe to the services
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		while(!finish){
			try {
				// Create a trace event of a random type
				i=generator.nextInt(MAX_SERVICES);
				serviceName="SRV_" + i;
				tEvent = new TraceEvent(serviceName, this.getAid(), serviceName);

				// Generating the trace event
				sendTraceEvent(tEvent);
				System.out.println("[PUBLISHER " + getName() + "]: Event sent (" + serviceName + ")");
				EventsSent[i]++;
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		System.out.println("[PUBLISHER]: Now unpublishing tracing services...");
		for (i=0; i < MAX_SERVICES; i++){
			//logger.info("[PUBLISHER]: Unpublishing " + (i+1) + " of "+ MAX_SERVICES);
			TraceInteract.unpublishTracingService(this, TracingServices[i].getName());
		}
		System.out.println("[PUBLISHER]: Done!");
		
		for (i=0; i < MAX_SERVICES; i++){
			ACLMessage coordination_msg = new ACLMessage(ACLMessage.INFORM);
			coordination_msg.setReceiver(coordinatorAid);
			coordination_msg.setContent("SENT:" + i + ":" + EventsSent[i]);
			send(coordination_msg);
		}
		
		System.out.println("[PUBLISHER]: Bye!");
	}
	
	public void onTraceEvent(TraceEvent tEvent) {
		/**
		 * When a trace event arrives, its shows it on the screen
		 */
		//System.out.println("[PUBLISHER " + getName() +"]: Event: " + tEvent.toReadableString());
	}
	
	public void onMessage(ACLMessage msg){
		switch (msg.getPerformativeInt()){
			case ACLMessage.REQUEST:
				if (msg.getContent().contentEquals("STOP")){
					finish = true;
				}
				break;
		}
	}
}
