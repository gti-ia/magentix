package benchmark1;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;

public class Controlador extends SingleAgent{

	ACLMessage msg,msg2;
	int nagents=0,nacabats=0,ntotal=0,i;

	public Controlador(AgentID aid, int ntotal) throws Exception {
		super(aid);
		this.ntotal = ntotal; //nombre total d'agents emisors
		//hap = getHap();
	}

	public void execute(){
		while(!done1()){
			action1();
		}
		onEnd1();

		while(!done2()){
			action2();
		}
		onEnd2();
	}

	public void action1(){

		try {
			msg = this.receiveACLMessage();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		nagents ++;

	}

	public boolean done1(){
		return nagents == ntotal; 
	}


	public int onEnd1(){
		msg2 = new ACLMessage(ACLMessage.UNKNOWN);
		msg2.setContent("Start!");
		for(i=1;i<=nagents;i++) 
			msg2.addReceiver(new AgentID("emisor"+i));

		send(msg2);

		return 0;
	}

	public void action2(){

		try {
			msg = this.receiveACLMessage();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		nacabats ++;

	}

	public boolean done2(){
		return nacabats == ntotal; 
	}


	public int onEnd2(){
		System.out.println("Prova Acabada");
		return 0;
	}

}
