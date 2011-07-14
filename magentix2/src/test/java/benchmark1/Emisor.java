package benchmark1;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;

public class Emisor extends SingleAgent{
	String hap;
	ACLMessage msg,msg2,msgcont;
	int nmsg,nmsgtot,completat=0,tmsg;
	long t1,t2,tot=0;
	int ntotal=0,nemisor,nreceptor;
	String content="";

	public Emisor(AgentID aid, int ntotal, int nmsg, int nmsgtot, int tmsg, int nemisor, int nreceptor) throws Exception {
		super(aid);
		//ntotal = ntotal;           //nombre total d'agents
		this.nmsg = nmsg;             //nombre de missatges a tindre per a les estadístiques
		this.nmsgtot = nmsgtot;          //nombre total de missatges a enviar
		this.tmsg = tmsg;             //tamany del missatge
		this.nemisor = nemisor; //nombre d'agent
		nreceptor = (nemisor % ntotal) + 1;                      //nombre del primer destinatari
		//hap = getHap();                                          //HAP

		for(int i=0;i<tmsg;i++)
			content=content+"a";


		msgcont = new ACLMessage(ACLMessage.UNKNOWN);
		msgcont.addReceiver(new AgentID("controlador"));
		msgcont.setContent("Ready");
		send(msgcont);
	}
	
	public void execute() {
		while(!done()){
			try {
				action();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		onEnd();
	}

	public void action() throws InterruptedException{
		msg = new ACLMessage(ACLMessage.REQUEST);
		msg.addReceiver(new AgentID("receptor"+nreceptor));
		//msg.addReceiver(new AID("receptor"+nreceptor+"@"+hap,AID.ISGUID));
		msg.setContent(content);

		t1 = System.currentTimeMillis();
		send(msg);

		msg2 = this.receiveACLMessage();
		t2 = System.currentTimeMillis();

		if(completat < nmsg) tot = tot + t2 - t1; //només agafarem estadístiques dels primers nmsg missatges

		completat++;

		nreceptor = (nreceptor % ntotal) + 1;

		if(nreceptor == nemisor)
			nreceptor = (nreceptor % ntotal) + 1;
	}

	public boolean done(){
		return nmsgtot == completat; //quan ja s'han enviat tots els missatges s'acaba
	}

	public int onEnd(){
		System.out.println("Mitjana RTT: "+ (float) tot/nmsg+" ms");
		//System.out.println("Total: "+ tot+" ms");

		send(msgcont);

		return 0;
	}

}
