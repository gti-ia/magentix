package LoadLauncher;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import LoadLauncher.Load;
import LoadLauncher.Transmission;

import es.upv.dsic.gti_ia.core.ACLMessage;
//import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.TraceEvent;

public class SubscriberEventTrace extends BaseAgent{
	private String LOG_FILE_NAME;
	
	private ArrayList<String> subscriptions;
	private ArrayList<Transmission> transmissions;
	private Long messages_to_receive, timeout;
		
	public SubscriberEventTrace(Load load_spec, int index) throws Exception {
		super(load_spec.getSubscribers().get(index).getAid());
		
		this.LOG_FILE_NAME = load_spec.getOutPath() + "/" +
			Load.prefixes[load_spec.getStrategy()] + "_" + this.getName() + "_result_log.txt";
		File LOG_FILE = new File(LOG_FILE_NAME);
		LOG_FILE.delete();
		LOG_FILE.createNewFile();
		this.subscriptions=load_spec.getSubscribers().get(index).getSubscriptions();
		this.transmissions = new ArrayList<Transmission>();
		this.messages_to_receive = Long.valueOf(0);
		this.timeout = load_spec.getSubscribers().get(index).getMaxPeriod();
		
		synchronized(this){
			wait(500);
		}
		
		String auxSubscription;
		Iterator<String> subIterator=subscriptions.iterator();
		
		while(subIterator.hasNext()){
			// Subscribe to the specified channels
			auxSubscription=subIterator.next();
			TraceInteract.requestTracingService(this, auxSubscription);
			
			// Locating Publisher and channel
			int pub_number, chan_number, index2;
			index2=auxSubscription.indexOf('_');
			pub_number=Integer.parseInt(auxSubscription.substring(1, index2));
			chan_number=Integer.parseInt(auxSubscription.substring(index2+1));
			this.messages_to_receive=this.messages_to_receive+load_spec.getPublishers().get(pub_number).getPublications().get(chan_number).getMessagesToSend();
		}
	}

	public void execute() {
		while(messages_to_receive > 0){
//			System.out.println(this.getAid().toString() + ": " + messages_to_receive + " messages to receive...");
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
	
	public void onTraceEvent(TraceEvent tEvent){
		Transmission trans;
		Date auxDate =  new Date(System.currentTimeMillis());
		
//		System.out.println("Event " + tEvent.toReadableString());
		
		if (subscriptions.contains(tEvent.getTracingService())){
			trans = new Transmission(tEvent.getOriginEntity().getAid(), this.getAid(),
					new Date(tEvent.getTimestamp()),
					auxDate, Transmission.COMMUNICATION, tEvent.getContent());
			synchronized(transmissions){
				transmissions.add(trans);
			}
			synchronized(messages_to_receive){
				messages_to_receive--;
			}
		}
		else{
			trans = new Transmission(tEvent.getOriginEntity().getAid(), this.getAid(),
					new Date(tEvent.getTimestamp()),
					auxDate, Transmission.SYSTEM, tEvent.getContent());
			synchronized(transmissions){
				transmissions.add(trans);
			}
		}
	}
	
	public void onMessage(ACLMessage msg){
		Date auxDate = new Date(System.currentTimeMillis());
		int index = msg.getContent().indexOf('#');
		Transmission trans;
		
//		System.out.println("Message " + msg.toString());
		
		if (msg.getPerformativeInt() != ACLMessage.AGREE){
			System.out.println(this.getAid() + " NOT AGREE! Message " + msg.toString());
		}
		
		trans = new Transmission(msg.getSender(), this.getAid(),
				new Date(Long.parseLong(msg.getContent().substring(0, index))),
				auxDate, Transmission.SYSTEM, msg.getContent().substring(index+1));
		synchronized(transmissions){
			transmissions.add(trans);
		}
	}
}
