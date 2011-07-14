package benchmark1_rev;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;

public class Emisor extends SingleAgent{

	String receptor,path;
	ACLMessage msg;
	int nmsg,nmsgtot,nmsgpad,tmsg;
	int ntotal=0,completat=0,nresults=0;
	long t1,t2;
	static int nagents=0,nacabats=0;
	static final Lock lock = new ReentrantLock();
	static final Condition notReady  = lock.newCondition();
	long results[];

	public Emisor(AgentID aid, int ntotal, int nmsg, int nmsgpad, int tmsg, int nemisor) throws Exception {
		super(aid);
		//String nemisor = this.getName().substring(6);    // agent number
		receptor = "receptor" + nemisor;  // destination agent
		//Object[] args = getArguments();
		this.ntotal = ntotal;     // total agent number
		this.nmsg = nmsg;       // messages taken into account in statistics
		this.nmsgpad = nmsgpad;    // padding messages
		this.tmsg = tmsg;       // message size

		nmsgtot=nmsgpad*2+nmsg;                          // total number of messages to be sent
		results = new long[nmsg];                        // result array

		/* message creation */
		msg = new ACLMessage(ACLMessage.REQUEST);
		msg.addReceiver(new AgentID(receptor));
		msg.setSender(this.getAid());
		String content="";
		for(int i=0;i<tmsg;i++)
			content=content+"a";
		msg.setContent(content);

		/* result file path */
		path = "results" + File.separator + "res_" + ntotal + "_" + tmsg  + "_" + nemisor;
	}

	public void execute(){
		/* Waiting until all the agents are ready to start */
		lock.lock();
		nagents ++;
		if(ntotal!=nagents){			
			notReady.awaitUninterruptibly();			
		}
		else{
			notReady.signalAll();
		}

		lock.unlock();
				
		while(!done())
			action();
		onEnd();
	}
	
	public void action(){
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
		lock.lock();
		nacabats ++;
		if(nacabats == ntotal) // the last agent
		{
			try{
				Runtime r = Runtime.getRuntime();
				r.exec("killall -9 java");
			}catch(java.io.IOException e){System.out.println("no puidoooooooo");}
		}

		lock.unlock();

		return 0;
	}
}
