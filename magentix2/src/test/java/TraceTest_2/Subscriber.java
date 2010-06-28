package TraceTest_2;

import java.util.Random;

//import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.TraceEvent;

import es.upv.dsic.gti_ia.trace.*;

/*****************************************************************************************
/*                                      Trace_Basic                                      *
/*****************************************************************************************
/*                     Author: Luis Burdalo (lburdalo@dsic.upv.es)                       *
/*****************************************************************************************
/*                                     DESCRIPTION                                       *
/*****************************************************************************************
    Simple test with three agents: a PUBLISHER agent, a SUBSCRIBER agent and a
    COORDINATOR agent.
    
    The SUBSCRIBER agent subscribes to the tracing service 'DD_Test_TS' and waits for 10
    seconds for trace events to arrive. After this time, the SUBSCRIBER agent
    unsubscribes from the tracing service and says 'Bye!'. Each time a trace event is
    received, the SUBSCRIBER sends a message to the coordinator with the content of the
    trace event.
*****************************************************************************************/
public class Subscriber extends BaseAgent{
	final int N_PUBLISHERS = 1;
//	AgentID coordinatorAid;
	private Random generator;
	private int publisher_number1=0, publisher_number2=0;
	private int service1=0, service2=0;

	public Subscriber(AgentID aid) throws Exception {
		
		super(aid);
		/**
		 * Initializing tracing services and stuff
		 */
//		System.out.println("[SUBSCRIBER "+ this.getName() + "]: Basic test start...");

//		coordinatorAid = new AgentID("qpid://coordinator@localhost:8080");
		generator = new Random(System.currentTimeMillis());
		//System.out.println("[SUBSCRIBER "+ this.getName() + "]: Subscribing to tracing services...");
		while ((publisher_number1 == publisher_number2) && (service1 == service2)){
			publisher_number1=generator.nextInt(N_PUBLISHERS)+1;
			publisher_number2=generator.nextInt(N_PUBLISHERS)+1;
			service1=generator.nextInt(2)+1;
			service2=generator.nextInt(2)+1;
		}
		System.out.println("[SUBSCRIBER "+ this.getName() + "]: Subscribing to publisher"+publisher_number1+"<DD_Test_TS_"+service1+">");
		TraceInteract.requestTracingService(this, "publisher"+publisher_number1+"<DD_Test_TS_"+service1+">");
		System.out.println("[SUBSCRIBER "+ this.getName() + "]: Subscribing to publisher"+publisher_number2+"<DD_Test_TS_"+service2+">");
		TraceInteract.requestTracingService(this, "publisher"+publisher_number2+"<DD_Test_TS_"+service2+">");
		//System.out.println("[SUBSCRIBER "+ this.getName() + "]: Done!");
	}

	public void execute() {
		int i;
		
		System.out.println("[SUBSCRIBER "+ this.getName() + "]: Executing...");
		/**
		 * This agent has no definite work. Wait infinitely the arrival of new
		 * messages.
		 */
    	for (i=0; i < 10; i++) {
			try {
				//System.out.println("[SUBSCRIBER "+ this.getName() + "]: Waiting (" + i + ")...");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    	
    	System.out.println("[SUBSCRIBER "+ this.getName() + "]: Now unsubscribing from tracing services publisher"+publisher_number1+"<DD_Test_TS_"+service1+"> and publisher"+publisher_number2+"<DD_Test_TS_"+service2+">...");
		TraceInteract.cancelTracingServiceSubscription(this, "publisher"+publisher_number1+"<DD_Test_TS_"+service1+">");
		TraceInteract.cancelTracingServiceSubscription(this, "publisher"+publisher_number2+"<DD_Test_TS_"+service2+">");
    	//System.out.println("[SUBSCRIBER "+ this.getName() + "]: Done!");
		
		System.out.println("[SUBSCRIBER "+ this.getName() + "]: Bye!");
	}

	public void onTraceEvent(TraceEvent tEvent) {
		/**
		 * When a trace event arrives, it sends it to the coordinator
		 */
		System.out.println("[SUBSCRIBER "+ this.getName() + "] RECV:" + tEvent.getContent());
//		ACLMessage coordination_msg = new ACLMessage(ACLMessage.INFORM);
//		coordination_msg.setSender(this.getAid());
//		coordination_msg.setReceiver(coordinatorAid);
//		coordination_msg.setContent("RECV:" + tEvent.getContent());
//		send(coordination_msg);
	}
}
