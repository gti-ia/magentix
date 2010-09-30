package TraceDaddy;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.TraceEvent;
import es.upv.dsic.gti_ia.trace.TraceInteract;

public class Daddy extends BaseAgent{
	private boolean finish=false;
	private boolean Bobby_agree=false;
	private boolean Timmy_agree=false;
	
	public Daddy(AgentID aid) throws Exception{
		super(aid);
		TraceInteract.requestTracingService(this, "NEW_AGENT");
		System.out.println("[Daddy " + this.getName() + "]: I want to read the newspaper...");
	}
	
	public void execute(){
		ACLMessage msg;
		while(!finish){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("[Daddy " + this.getName() + "]: Ok! I give up... Shall we go to the park?");
		TraceInteract.cancelTracingServiceSubscription(this, "MESSAGE_SENT_DETAIL",new AgentID("Timmy"));
		TraceInteract.cancelTracingServiceSubscription(this, "MESSAGE_SENT_DETAIL",new AgentID("Bobby"));
		
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
		
		System.out.println("[Daddy " + this.getName() + "]: Ok! Let's go, children!");
	}
	
	public void onTraceEvent(TraceEvent tEvent) {
		/**
		 * When a trace event arrives, it prints it on the screen
		 */
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(tEvent.getTimestamp());
		
		ACLMessage msg;
		
		if (tEvent.getTracingService().contentEquals("NEW_AGENT")){
			TraceInteract.requestTracingService(this, "MESSAGE_SENT_DETAIL", new AgentID(tEvent.getContent()));
			
		}
		else if (tEvent.getTracingService().contentEquals("MESSAGE_SENT_DETAIL")){
			msg = ACLMessage.fromString(tEvent.getContent());
			System.out.println("[" + this.getName() + " " + formatter.format(calendar.getTime()) + "]: " + msg.getSender().toString() + " said: " + msg.getPerformative() + ": " + msg.getContent());
			if (msg.getContent().contentEquals("GUAAAAAA..!")){
				finish=true;
			}
		}
	}
	
	public void onMessage(ACLMessage msg){
		if((msg.getPerformativeInt() == ACLMessage.AGREE) && (msg.getContent().contentEquals("GO TO THE PARK"))){
			System.out.println("[Daddy " + this.getName() + "]: " + msg.getSender().name + " says: " + msg.getPerformative() + " " + msg.getContent());
			if (msg.getSender().getLocalName().contentEquals("Bobby")){
				Bobby_agree=true;
			}
			if (msg.getSender().getLocalName().contentEquals("Timmy")){
				Timmy_agree=true;
			}
		}
	}
}
