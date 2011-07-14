package benchmark2_rev;

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
		return nagents == ntotal * 2; //han d'estar tots els agents preparats, emisors i receptors 
	}


	public int onEnd1(){
		System.out.println(this.getName()+": Todos los agentes preparados, envio mensaje Start!");
		msg2 = new ACLMessage(ACLMessage.UNKNOWN);
		msg2.setContent("Start!");
		for(i=0;i<=nagents;i++) 
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
		try{
			Runtime r = Runtime.getRuntime();
			r.exec("killall -9 java");
		}
		catch(java.io.IOException e){
			System.out.println("no puidoooooooo");
		}
		return 0;
	}

}
