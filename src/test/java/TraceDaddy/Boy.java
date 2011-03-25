package TraceDaddy;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;

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

    BOYS (Bobby and Timmy):
       - Print on screen their name and age.
       
    Execution:
         
    BOYS (Bobby and Timmy):
       - Bobby, which is only 5, sends each second an ACL request message to Timmy (which
         is 7) to request him his toy (Give me your toy). After 5 denials, Bobby starts
         requesting it by crying (sending an ACL message with a loud GUAAAAAA!).
       - Both Boy agents reply NO! to any request which does not come from their father
         and only AGREE when their dad requestes them to GO TO THE PARK.
       - When dad requests them (via an ACL message) to go to the park, both sons agree
         and end their execution.
         
*****************************************************************************************/

public class Boy extends BaseAgent {
	private int age;
	private boolean finish=false;
	AgentID dad;
	
	public Boy (AgentID aid, int age, AgentID dad) throws Exception{
		super(aid);
		this.age=age;
		this.dad=dad;
		System.out.println("[" + this.getName() + "]: I'm " + this.getName() + " and I'm "+ this.age + " years old!");
	}
	
	public void execute(){
		ACLMessage msg;
		int counter=5;
		while(!finish){
			if (this.age <= 5) {
				msg = new ACLMessage(ACLMessage.REQUEST);
				msg.setSender(this.getAid());
				if (counter > 0){
					msg.setContent("Give me your toy...");
				}
				else{
					msg.setContent("GUAAAAAA..!");
				}
				counter--;
				
				msg.setReceiver(new AgentID("qpid://Timmy@localhost:8080"));
				send(msg);
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void onMessage(ACLMessage msg){
		if (msg.getSender().getLocalName().contentEquals(dad.getLocalName())){
			// Daddy!
			if(msg.getPerformativeInt() == ACLMessage.REQUEST){
				if (msg.getContent().contentEquals("GO TO THE PARK")){
					finish=true;
					ACLMessage response_msg = new ACLMessage(ACLMessage.AGREE);
					response_msg.setSender(this.getAid());
					response_msg.setContent("GO TO THE PARK");
					response_msg.setReceiver(msg.getSender());
					send(response_msg);
				}
			}
		}
		else{
			// You no daddy!
			if(msg.getPerformativeInt() == ACLMessage.REQUEST){
				ACLMessage response_msg = new ACLMessage(ACLMessage.REFUSE);
				response_msg.setSender(this.getAid());
				response_msg.setContent("NO!");
				response_msg.setReceiver(msg.getSender());
				send(response_msg);
			}
		}
	}
}
