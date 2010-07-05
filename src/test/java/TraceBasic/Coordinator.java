package TraceBasic;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
//import es.upv.dsic.gti_ia.trace.TraceInteract;

public class Coordinator extends BaseAgent {
	private final int MAX_SERVICES = 10;
	private AgentID publisherAid = new AgentID("qpid://publisher@localhost:8080");
	private AgentID subscriberAid = new AgentID("qpid://subscriber@localhost:8080");
	private Integer[] EventsSent = new Integer[MAX_SERVICES];
	private Integer[] EventsReceived = new Integer[MAX_SERVICES];
	private int nSent=MAX_SERVICES, nRecv=MAX_SERVICES;
	private int error, not_recv, not_sent, i;
	private ACLMessage coordination_msg;
	
	public Coordinator(AgentID aid) throws Exception {
		super(aid);
	}

	public void execute() {
//		try {
//			Thread.sleep(10000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		coordination_msg = new ACLMessage(ACLMessage.REQUEST);
		coordination_msg.setReceiver(publisherAid);
		coordination_msg.setContent("STOP");
		send(coordination_msg);
		coordination_msg = new ACLMessage(ACLMessage.REQUEST);
		coordination_msg.setReceiver(subscriberAid);
		coordination_msg.setContent("STOP");
		send(coordination_msg);
		
		while ((nSent > 0) || (nRecv > 0)){
			try {
				// Wait 1 seconds for Subscriber to subscribe to the services
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		for (i=0, error=0, not_sent=0, not_recv=0; i < MAX_SERVICES; i++){
			if (EventsSent[i] > EventsReceived[i]){
				error++;
				not_recv=not_recv+EventsSent[i]-EventsReceived[i];
				System.out.println("Error in SRV_" + i);
			}
			else if (EventsSent[i] < EventsReceived[i]){
				error++;
				not_sent=not_sent+EventsReceived[i]-EventsSent[i];
				System.out.println("Error in SRV_" + i);
			}
		}
		
		if (not_recv > 0){
			System.out.println(not_recv + " trace events sent but not received");
		}
		if (not_sent > 0){
			System.out.println(not_sent + " trace events received but not sent (¿?¿?¿?)");
		}
		System.out.println("TOTAL: " + error + " errors");
	}
	
	public void onMessage(ACLMessage msg){
		int i, index, index2;
		int service, nTimes;
//		
//		logger.info("[COORDINATOR " + getName() +"]: Message: " + msg.getPerformative() + " " + msg.getContent());
//		
		switch (msg.getPerformativeInt()){
			case ACLMessage.INFORM:
				index=msg.getContent().indexOf(":");				
				if(msg.getContent().substring(0, index).contentEquals("SENT")){
					index2=msg.getContent().indexOf(":", index);
					service=Integer.parseInt(msg.getContent().substring(index, index2));
					nTimes=Integer.parseInt(msg.getContent().substring(index2));
					EventsSent[service]=nTimes;
					nSent--;
				}
				else if(msg.getContent().substring(0, index).contentEquals("RECV")){
					index2=msg.getContent().indexOf(":", index);
					service=Integer.parseInt(msg.getContent().substring(index, index2));
					nTimes=Integer.parseInt(msg.getContent().substring(index2));
					EventsReceived[service]=nTimes;
					nRecv--;
				} 
				break;
		}
	}
}
