package LoadLauncher;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
//import es.upv.dsic.gti_ia.core.BaseAgent;

public class SubscriberBroadcast extends BaseAgent {
	private String LOG_FILE_NAME;
	
	private AgentID broadcastManagerAid;
	private ArrayList<Transmission> transmissions;
	private Long messages_to_receive, timeout;
		
	public SubscriberBroadcast(Load load_spec, int index) throws Exception {
		super(load_spec.getSubscribers().get(index).getAid());
		
		this.LOG_FILE_NAME = load_spec.getOutPath() + "/" +
			Load.prefixes[load_spec.getStrategy()] + "_" + this.getName() + "_result_log.txt";
		File LOG_FILE = new File(LOG_FILE_NAME);
		LOG_FILE.delete();
		LOG_FILE.createNewFile();
		this.broadcastManagerAid=load_spec.getMiddleAgentID();
		this.transmissions = new ArrayList<Transmission>();
		this.messages_to_receive = load_spec.MESSAGES_TO_SEND;
		this.timeout = load_spec.getSubscribers().get(index).getMaxPeriod();
		
		ACLMessage msg;
		
		msg=new ACLMessage();
		msg.setLanguage("ACL");
		msg.setPerformative(ACLMessage.INFORM);
		msg.setReceiver(broadcastManagerAid);
		msg.setSender(this.getAid());
		msg.setContent(String.valueOf(System.currentTimeMillis()));
		send(msg);
	}
	
	public void execute() {
		while(messages_to_receive > 0){
//			if (this.getName().contentEquals("subscriber10")){
//				System.out.println(this.getAid().toString() + ": " + messages_to_receive + " messages to receive...");
//			}
			try {
				synchronized(this){
					wait(timeout);
				}
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		System.out.println(this.getAid().toString() + ": " + messages_to_receive + " messages to receive... DONE!");
		
		System.out.println(this.getAid().toString() + " writing data...");
		
		// Append to file
		BufferedWriter log_file;
		try {
			log_file = new BufferedWriter(new FileWriter(LOG_FILE_NAME, false));
			Transmission auxTrans;
			synchronized(transmissions){
				Iterator<Transmission> transIter;
				transIter=transmissions.iterator();
				while (transIter.hasNext()){
					auxTrans=transIter.next();
					log_file.write(auxTrans.toString()+"\n");
				}
			}
			log_file.close();

			System.out.println(this.getAid().toString() + " done writing data!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void onMessage(ACLMessage msg){
		int index;
		Transmission trans;
		Date auxDate = new Date(System.currentTimeMillis());

		index=msg.getContent().indexOf('#');
		if (msg.getPerformativeInt() == ACLMessage.INFORM){
			trans = new Transmission(msg.getSender(), this.getAid(),
					new Date(Long.parseLong(msg.getContent().substring(0, index))),
					auxDate, Transmission.COMMUNICATION, msg.getContent().substring(index+1));
			synchronized(transmissions){
				transmissions.add(trans);
			}
			synchronized(messages_to_receive){
				messages_to_receive--;
			}
//			System.out.println(this.getAid().toString() + ": " + messages_to_receive + " messages to receive...");
		}
		else{
			trans = new Transmission(msg.getSender(), this.getAid(),
					new Date(Long.parseLong(msg.getContent().substring(0, index))),
					auxDate, Transmission.SYSTEM, msg.getContent().substring(index+1));
			synchronized(transmissions){
				transmissions.add(trans);
			}
		}
	}
}
