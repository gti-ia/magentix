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

public class SubscriberBroker extends BaseAgent {
	private String LOG_FILE_NAME;
	
	private ArrayList<String> subscriptions;
	private AgentID brokerAid;
	private ArrayList<Transmission> transmissions;
	private Long messages_to_receive, timeout;
	private int index;
	
	public SubscriberBroker(Load load_spec, int index) throws Exception {
		super(load_spec.getSubscribers().get(index).getAid());
		
		this.index=index;
		
		this.LOG_FILE_NAME = load_spec.getOutPath() + "/" +
			Load.prefixes[load_spec.getStrategy()] + "_" + this.getName() + "_result_log.txt";
		File LOG_FILE = new File(LOG_FILE_NAME);
		LOG_FILE.delete();
		LOG_FILE.createNewFile();
		this.brokerAid=load_spec.getMiddleAgentID();
		this.subscriptions=load_spec.getSubscribers().get(index).getSubscriptions();
		this.transmissions = new ArrayList<Transmission>();
		this.messages_to_receive = Long.valueOf(0);
		this.timeout = load_spec.getSubscribers().get(index).getMaxPeriod();
		
		String auxSubscription;
		Iterator<String> subIterator=subscriptions.iterator();
		ACLMessage msg;
		
		while(subIterator.hasNext()){
			// Subscribe to the specified channels
			auxSubscription=subIterator.next();
			msg=new ACLMessage();
			msg.setLanguage("ACL");
			msg.setPerformative(ACLMessage.SUBSCRIBE);
			msg.setReceiver(brokerAid);
			msg.setSender(this.getAid());
			msg.setContent(String.valueOf(System.currentTimeMillis()) + "#" + auxSubscription);
			send(msg);
			
			// Locating Publisher and channel
			int pub_number, chan_number, index2;
			index2=auxSubscription.indexOf('_');
			pub_number=Integer.parseInt(auxSubscription.substring(1, index2));
			chan_number=Integer.parseInt(auxSubscription.substring(index2+1));
			synchronized(messages_to_receive){
				this.messages_to_receive=this.messages_to_receive+load_spec.getPublishers().get(pub_number).getPublications().get(chan_number).getMessagesToSend();
			}
		}
	}
	
	public void execute() {
		while(messages_to_receive > 0){
			
			if (this.index == 7){
				System.out.println(this.getAid().toString() + ": " + messages_to_receive + " messages to receive...");
			}
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
//					System.out.println(this.getName() + ": Writing " + auxTrans.getContent());
//					System.out.println(this.getName() + ": TO STRING " + auxTrans.toString());
					log_file.write(auxTrans.toString() + "\n");
				}
			}
			log_file.close();

			System.out.println(this.getAid().toString() + " done writing data!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void onMessage(ACLMessage msg){
		int index=msg.getContent().indexOf("#");
		Transmission trans;
		Date auxDate = new Date(System.currentTimeMillis());
		
		if (msg.getPerformativeInt() == ACLMessage.INFORM){
//			System.out.println(this.getAid().toString() + ": " + messages_to_receive + " messages to receive...");
//			System.out.println(this.getAid().toString() + ": " + "Msg: " + msg.getContent().substring(index+1));
			trans = new Transmission(msg.getSender(), this.getAid(),
					new Date(Long.parseLong(msg.getContent().substring(0, index))),
					auxDate, Transmission.COMMUNICATION, msg.getContent().substring(index+1));
			synchronized(transmissions){
				transmissions.add(trans);
			}
			synchronized(messages_to_receive){
				messages_to_receive--;
			}
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
