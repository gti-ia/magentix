package TraceTest_2;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;

/*****************************************************************************************
/*                                      Trace_Basic                                      *
/*****************************************************************************************
/*                     Author: Luis Burdalo (lburdalo@dsic.upv.es)                       *
/*****************************************************************************************
/*                                     DESCRIPTION                                       *
/*****************************************************************************************
    Simple test with three agents: a PUBLISHER agent, a SUBSCRIBER agent and a
    COORDINATOR agent.
        
    The COORDINATOR agent waits for 12 seconds for messages to arrive. Each time a
    message arrives, it prints in the screen the content of the message and the AgentID
    of the agent which sent it.
*****************************************************************************************/

public class Coordinator extends BaseAgent {
	final int N_PUBLISHERS = 10;
	final int N_SUBSCRIBERS = 10;
	final int N_EVENTS = 10;
	
	public Coordinator(AgentID aid) throws Exception {
		super(aid);
	}

	public void execute() {
		// Wait 12 seconds 
		try {
			Thread.sleep(12000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("[COORDINATOR]: Test done!");
	}
	
	public void onMessage(ACLMessage msg){
		switch (msg.getPerformativeInt()){
			case ACLMessage.INFORM:
				System.out.println("[COORDINATOR]: Received from " + msg.getSender().toString() + ": " + msg.getContent());
				break;
		}
	}
}
