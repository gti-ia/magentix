package benchmark2;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;

public class Emisor extends SingleAgent{

	String receptor,path;
	ACLMessage msg, msgcont;
	int nmsg,nmsgtot,nmsgpad,tmsg, receptorPorEmisor, nemisor;
	int ntotal=0,completat=0,nresults=0;
	long t1,t2;
	static int nagents=0,nacabats=0;
	static final Lock lock = new ReentrantLock();
	static final Condition notReady  = lock.newCondition();
	long results[];

	public Emisor(AgentID aid, int ntotal, int nmsg, int nmsgpad, int tmsg, int nemisor, int receptorPorEmisor) throws Exception {
		super(aid);
		this.ntotal = ntotal;     // total agent number
		this.nmsg = nmsg;       // messages taken into account in statistics
		this.nmsgpad = nmsgpad;    // padding messages
		this.tmsg = tmsg;       // message size
		this.receptorPorEmisor = receptorPorEmisor;
		this.nemisor = nemisor;

		nmsgtot=nmsgpad*2+nmsg;                          // total number of messages to be sent
		results = new long[nmsg];                        // result array
		
		if(receptorPorEmisor == 1) // agent receptor correspon amb emisor
			receptor = "receptor" + nemisor;  
		else // sols un agent receptor
			receptor = "receptor" + 0;
		
		/* message creation */
		msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setSender(this.getAid());
		msg.addReceiver(new AgentID(receptor));
		String content="";
		for(int i=0;i<tmsg;i++)
			content=content+"a";
		msg.setContent(content);

		msgcont = new ACLMessage(ACLMessage.UNKNOWN);
		msgcont.addReceiver(new AgentID("controlador"));
		msgcont.setContent("Ready");
		msgcont.setSender(getAid());
		send(msgcont);

		/* result file path */
		path = "results" + File.separator + "res_" + ntotal + "_" + tmsg  + "_" + nemisor;
	}

	public void execute(){
		try {
			msgcont = receiveACLMessage(); //wait for controller msg
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(!done())
			action();
		onEnd();
	}

	public void action(){
		if(receptorPorEmisor == 2){ // agent receptor aleatori
			Random rand = new Random();
			int x = rand.nextInt(ntotal);
			receptor = "receptor"+x;
			msg.setReceiver(new AgentID(receptor));
		}
				
		t1 = System.currentTimeMillis();
		send(msg);
		try {
			ACLMessage msg2 = this.receiveACLMessage();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		t2 = System.currentTimeMillis();

		/* Statistics */
		if(completat>nmsgpad && completat<=nmsgpad+nmsg) 
		{
			results[nresults] = t2 - t1;
			nresults++;
		}

		completat++;

	}

	public boolean done(){
		return nmsgtot==completat; // when all the messages are sent this agent finalize
	}

	public int onEnd(){
		/* writing results obtained */
		try{
			/* opening result file */
			FileOutputStream file = new FileOutputStream(path);
			DataOutputStream out = new DataOutputStream(file);

			/* writing RTT times collected in milliseconds */
			for(int i=0;i<nmsg;i++)
			{
				out.writeBytes(results[i]+"\n");
			}

			file.close();

		} catch (IOException e) {
			System.out.println(e.getMessage());

		}

		/* catching lock and increasing number of agents already finished */
		msgcont = new ACLMessage(ACLMessage.UNKNOWN);
		msgcont.addReceiver(new AgentID("controlador"));
		msgcont.setContent("Ended");
		send(msgcont);

		return 0;
	}
}
