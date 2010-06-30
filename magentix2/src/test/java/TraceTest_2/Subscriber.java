package TraceTest_2;

import java.util.Random;

//import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.TraceEvent;

import es.upv.dsic.gti_ia.trace.*;

/*****************************************************************************************/
/*                                      TraceTest_2                                      */
/*****************************************************************************************/
/*                     Author: Luis Burdalo (lburdalo@dsic.upv.es)                       */
/*****************************************************************************************/
/*                                     DESCRIPTION                                       */
/*****************************************************************************************

    Simple test with two types of agents: 100 PUBLISHER agents and 30 SUBSCRIBER agents.
    
    SUBSCRIBER agents subscribe randomly to two of the services offered by the PUBLISHER
    agents and wait during 12 seconds for events to arrive. Each time a trace event is
    received, the SUBSCRIBER agent updates the corresponding counter so that it is
    possible to verify after the execution that the number of received events of each
    tracing service is 10. Before finishing, each SUBSCRIBER agent displays the number
    of trace events of each tracing service which have been received.
    
    Messages to be displayed on the screen during the execution have been commented in
    order to make the execution more easily readable.

*****************************************************************************************/
public class Subscriber extends BaseAgent{
	final int N_PUBLISHERS = 100;
	private final int N_EVENTS = 10;
	private Random generator;
	private int publisher_number1=0, publisher_number2=0;
	private int service1=0, service2=0;
	private int n_received1=0;
	private int n_received2=0;
	
	public Subscriber(AgentID aid) throws Exception {
		super(aid);
		/**
		 * Initializing tracing services and stuff
		 */
		generator = new Random(System.currentTimeMillis());
		//System.out.println("[SUBSCRIBER "+ this.getName() + "]: Subscribing to tracing services...");
		while ((publisher_number1 == publisher_number2) && (service1 == service2)){
			publisher_number1=generator.nextInt(N_PUBLISHERS)+1;
			publisher_number2=generator.nextInt(N_PUBLISHERS)+1;
			service1=generator.nextInt(2)+1;
			service2=generator.nextInt(2)+1;
		}
//		System.out.println("[SUBSCRIBER " + this.getName() + "]: Subscribing to publisher"+publisher_number1+"<DD_Test_TS_"+service1+">");
		TraceInteract.requestTracingService(this, "publisher"+publisher_number1+"<DD_Test_TS_"+service1+">");
//		System.out.println("[SUBSCRIBER " + this.getName() + "]: Subscribing to publisher"+publisher_number2+"<DD_Test_TS_"+service2+">");
		TraceInteract.requestTracingService(this, "publisher"+publisher_number2+"<DD_Test_TS_"+service2+">");
	}

	public void execute() {
		/**
		 * This agent has no definite work. Wait infinitely the arrival of new
		 * messages.
		 */
		try {
			Thread.sleep(12000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//System.out.println("[SUBSCRIBER " + this.getName() + "]: Now unsubscribing from tracing services publisher"+publisher_number1+"<DD_Test_TS_"+service1+"> and publisher"+publisher_number2+"<DD_Test_TS_"+service2+">...");
		TraceInteract.cancelTracingServiceSubscription(this, "publisher"+publisher_number1+"<DD_Test_TS_"+service1+">");
		TraceInteract.cancelTracingServiceSubscription(this, "publisher"+publisher_number2+"<DD_Test_TS_"+service2+">");
    	
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if ((n_received1 == N_EVENTS) && (n_received2 == N_EVENTS)){
			System.out.println("[SUBSCRIBER " + this.getName() + "]: OK! Received " + n_received1 + " of " + N_EVENTS);
		}
		else{
			System.out.println("[SUBSCRIBER " + this.getName() + "]: FAIL! Missed events. Received " + n_received1 + " of " + N_EVENTS + " and " + n_received2 + " of " + N_EVENTS);
		}
		
		//System.out.println("[SUBSCRIBER " + this.getName() + "]: Bye!");
		
	}

	public void onTraceEvent(TraceEvent tEvent) {
		int index;
		/**
		 * When a trace event arrives, it updates counters and prints the content on the screen
		 */
		//System.out.println("[SUBSCRIBER " + this.getName() + "]: Received from " + tEvent.getOriginEntity().getAid().name + ": " + tEvent.getContent());
		index=tEvent.getContent().indexOf(" ");
		if (tEvent.getContent().substring(0, index).contentEquals("publisher"+publisher_number1+"<DD_Test_TS_"+service1+">")){
			n_received1++;
		}
		else if (tEvent.getContent().substring(0, index).contentEquals("publisher"+publisher_number2+"<DD_Test_TS_"+service2+">")){
			n_received2++;
		}
	}
	
	public void onMessage(ACLMessage msg){
//		System.out.println("[SUBSCRIBER " + this.getName() + "]: Received from " + msg.getSender().toString() + ": " + msg.getContent());
	}
}
