package TestTrace.TestTraceDaddy;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.Semaphore;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.TraceEvent;
import es.upv.dsic.gti_ia.trace.TraceInteract;
import es.upv.dsic.gti_ia.trace.exception.TraceServiceNotAllowedException;

/*****************************************************************************************
/*                                       TraceDaddy                                      *
/*****************************************************************************************
/*                     Author: Luis Burdalo (lburdalo@dsic.upv.es)                       *
/*****************************************************************************************
/*                                     DESCRIPTION                                       *
/*****************************************************************************************
    Simple example of how to use domain independent tracing services to follow other
    agents' activities and to make decisions according to this activity.
    
    In this case, a Daddy agent listens to his sons (Boy agents) while they are playing
    and when one of them starts crying, he proposes them to take them to the park. When
    both children agree, daddy and his sons leave the building and the application
    finishes.
    
    Initialization:
    
    DADDY:
       - Requests to the NEW_AGENT tracing service in order to know when
         children arrive.
       - Prints on screen that he intends to read the newspaper.
       
    Execution:
    
    DADDY:
       - Each time a NEW_AGENT event is received, Daddy requests the tracing
         service MESSAGE_SENT_DETAIL in order to 'listen' to what that agent says.
       - Each time a MESSAGE_SENT_DETAIL trace event is received, Daddy prints its
         content on screen and checks if the content of the message is equal
         to 'GUAAAAAA!'. If so, Daddy cancels the subscription to MESSAGE_SENT_DETAIL
         tracing services and sends ACL request messages to both children to propose
         the go to the park.
       - When both children have replied with an AGREE message, Daddy agent prints it on
         screen and ends its execution.
         
*****************************************************************************************/

/** 
 * @author Luis Burdalo - lburdalo@dsic.upv.es
 * @author Jose Alemany Bordera - jalemany1@dsic.upv.es
 * 
 */

public class Daddy extends BaseAgent {
	
	static Semaphore contExec;
	private boolean Bobby_agree = false;
	private boolean Timmy_agree = false;
	private ArrayList<TraceEvent> events;
	
	public Daddy(AgentID aid) throws Exception {
		super(aid);
		contExec = new Semaphore(0);
		this.events = new ArrayList<TraceEvent>();
		updateTraceMask();
		TraceInteract.requestTracingService(this, "NEW_AGENT");
		System.out.println("[Daddy " + this.getName() + "]: I want to read the newspaper...");
	}
	
	public void execute() {
		ACLMessage msg;
		
		try {
			contExec.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	
		System.out.println("[Daddy " + this.getName() + "]: Ok! I give up... Shall we go to the park?");
		
		msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setSender(this.getAid());
		msg.setContent("GO TO THE PARK");
		msg.setReceiver(new AgentID("Timmy"));
		send(msg);
		msg.setReceiver(new AgentID("Bobby"));
		send(msg);
		while(!Bobby_agree || !Timmy_agree){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("[Daddy " + this.getName() + "]: Ok! Let's go, children!");
		TestTraceDaddy.end.release();
	}
	
	public void onTraceEvent(TraceEvent tEvent) {
		/**
		 * When a trace event arrives, it prints it on the screen
		 */
		this.events.add(tEvent);
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(tEvent.getTimestamp());
		
		ACLMessage msg;
		
		try {
			
			if (tEvent.getTracingService().contentEquals("NEW_AGENT")) {
				
				TraceInteract.requestTracingService(this, "MESSAGE_SENT_DETAIL", new AgentID(tEvent.getContent()));
			
			} else if (tEvent.getTracingService().contentEquals("MESSAGE_SENT_DETAIL")) {
				
				msg = ACLMessage.fromString(tEvent.getContent());
				System.out.println("[" + this.getName() + " " + formatter.format(calendar.getTime()) + "]: " + msg.getSender().toString() + " said: " + msg.getPerformative() + ": " + msg.getContent());
				
				if (msg.getContent().contentEquals("GUAAAAAA..!")) {
		
					TraceInteract.cancelTracingServiceSubscription(this, "MESSAGE_SENT_DETAIL",new AgentID("Timmy"));
					TraceInteract.cancelTracingServiceSubscription(this, "MESSAGE_SENT_DETAIL",new AgentID("Bobby"));
					TestTraceDaddy.contExec.release();
				}
			}
		} catch (TraceServiceNotAllowedException e) {
			
			e.printStackTrace();
		}
	}
	
	public void onMessage(ACLMessage msg) {
		
		if((msg.getPerformativeInt() == ACLMessage.AGREE) && (msg.getContent().contentEquals("GO TO THE PARK"))) {
			
			System.out.println("[Daddy " + this.getName() + "]: " + msg.getSender().name + " says: " + msg.getPerformative() + " " + msg.getContent());
			
			if (msg.getSender().getLocalName().contentEquals("Bobby")) {
				Bobby_agree=true;
			}
			
			if (msg.getSender().getLocalName().contentEquals("Timmy")) {
				Timmy_agree=true;
			}
		}
	}
	
	public ArrayList<TraceEvent> getEvents() {
		return this.events;
	}
}
