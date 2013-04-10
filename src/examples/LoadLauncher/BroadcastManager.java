package LoadLauncher;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Semaphore;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
//import es.upv.dsic.gti_ia.core.BaseAgent;

public class BroadcastManager extends BaseAgent {
	private String LOG_FILE_NAME;
	
	private ArrayList<AgentID> subscribers;
	
	private Semaphore write_semaphore;
	
	public BroadcastManager(Load load_spec) throws Exception{
		super(load_spec.getMiddleAgentID());

		this.LOG_FILE_NAME = load_spec.getOutPath() + "/" +
			Load.prefixes[load_spec.getStrategy()] + "_" + this.getName() + "_result_log.txt";
		File LOG_FILE = new File(LOG_FILE_NAME);
		LOG_FILE.createNewFile();
		
		this.write_semaphore = new Semaphore(1, true);
		
		this.subscribers=new ArrayList<AgentID>();
		
		System.out.println(this.getName() + " launched.");
	}
	
	public void onMessage(ACLMessage msg){
		String auxString;

		auxString = Long.toString(System.currentTimeMillis()) + "\t" +
			msg.getContent() + "\t" +
			msg.getSender().toString() + "\t" +
			this.getAid().toString()  + "\t" +
			String.valueOf(Transmission.SYSTEM) + "\t---\n";
				
		try {
			write_semaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
				
		try {
			BufferedWriter log_file = new BufferedWriter(new FileWriter(LOG_FILE_NAME, true));
			log_file.write(auxString);
			log_file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		write_semaphore.release();
				
		if (msg.getPerformativeInt() == ACLMessage.QUERY_REF){
			ACLMessage replyMsg = new ACLMessage();
			replyMsg.setLanguage("ACL");
			replyMsg.setSender(this.getAid());
			replyMsg.setReceiver(msg.getSender());
			replyMsg.setPerformative(ACLMessage.INFORM);
					
			String content;
			content = String.valueOf(this.subscribers.size());
					
			Iterator<AgentID> subIter=this.subscribers.iterator();
			AgentID auxSub;
			while(subIter.hasNext()){
				auxSub=subIter.next();
				content=content + "#" + auxSub.toString().length() + "#" + auxSub.toString();
			}
					
			replyMsg.setContent(String.valueOf(System.currentTimeMillis()) + "#" + content);
			send(replyMsg);
		}
		else if (msg.getPerformativeInt() == ACLMessage.INFORM){
			subscribers.add(msg.getSender());
		}
	}
}
