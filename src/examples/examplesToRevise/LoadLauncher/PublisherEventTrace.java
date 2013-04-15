package LoadLauncher;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;

import LoadLauncher.Load.*;

import es.upv.dsic.gti_ia.core.ACLMessage;
//import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.TraceEvent;

public class PublisherEventTrace extends BaseAgent {
	private String LOG_FILE_NAME;
	
	private ArrayList<Publication> publications;
	private ArrayList<Transmission> transmissions;
	private ArrayList<Long> sequences;
	private long messages_to_send;
	
	public PublisherEventTrace(Load load_spec, int index) throws Exception {
		super(load_spec.getPublishers().get(index).getAid());
		
		this.LOG_FILE_NAME = load_spec.getOutPath() + "/" +
			Load.prefixes[load_spec.getStrategy()] + "_" + this.getName() + "_result_log.txt";
		this.publications = load_spec.getPublishers().get(index).getPublications();
		this.transmissions = new ArrayList<Transmission>();
		this.sequences = new ArrayList<Long>();
		this.messages_to_send = 0;
		
		synchronized(this){
			wait(500);
		}
		
		Publication auxPub;
		
		synchronized (publications){
			Iterator<Publication> pubIterator = publications.iterator();
			
			while (pubIterator.hasNext()){
				// Publish things
				auxPub=pubIterator.next();
				sequences.add(auxPub.getMessagesToSend());
				messages_to_send = messages_to_send + sequences.get(publications.indexOf(auxPub));
				TraceInteract.publishTracingService(this, auxPub.getChannelName(), auxPub.getChannelName());
			}
		}
	}

	public void execute() {
		Iterator<Publication> pubIterator;
		Publication auxPub, nextPub=null;
		TraceEvent tEvent;
		Long currentTime = System.currentTimeMillis();
		long timeout, nextStop=Long.MAX_VALUE;
		
		Random generator = new Random(System.currentTimeMillis());
		
		// Initial transmissions
		pubIterator=publications.iterator();
		while(pubIterator.hasNext()){
			auxPub = pubIterator.next();
			
			try {
				timeout=generator.nextInt(5000);
				synchronized(this){
					wait(timeout);
				}
				currentTime = System.currentTimeMillis();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			Long auxLong=sequences.get(publications.indexOf(auxPub));
			tEvent = new TraceEvent(auxPub.getChannelName(), this.getAid(),
					currentTime.toString()
					+ "#" + String.valueOf(auxLong) 
					+ "#" + this.getAid().toString().length() + this.getAid()
					+ "#" + auxPub.getChannelName().length() + "#" + auxPub.getChannelName());
			// Generating trace events
			sendTraceEvent(tEvent);

			auxLong--;
			
			sequences.set(publications.indexOf(auxPub), auxLong);
			auxPub.setNextPublication(currentTime + auxPub.getPeriod());
			if (nextStop > auxPub.getNextPublication()){
				nextStop = auxPub.getNextPublication();
				nextPub=auxPub;
			}
			
			messages_to_send--;
		}
		
		while (messages_to_send > 0){
			try {
				if ((timeout=(nextStop-currentTime)) > 0){
					synchronized(this){
						wait(timeout);
					}
				}
//				else {
////					System.out.println("Que me cago, que no llego..!");
//				}
				currentTime = System.currentTimeMillis();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			Long auxLong=sequences.get(publications.indexOf(nextPub));
			tEvent = new TraceEvent(nextPub.getChannelName(), this.getAid(),
					currentTime.toString()
					+ "#" + String.valueOf(auxLong) 
					+ "#" + this.getAid().toString().length() + "#" + this.getAid()
					+ nextPub.getChannelName().length() + "#" + nextPub.getChannelName());
			// Generating trace events
			sendTraceEvent(tEvent);

			auxLong--;
			sequences.set(publications.indexOf(nextPub), auxLong);
			
			if (auxLong > 0){
				nextPub.setNextPublication(currentTime + nextPub.getPeriod());
			}
			else{
				nextPub.setNextPublication(Long.MAX_VALUE);	
			}
			nextStop=nextPub.getNextPublication();
			
			pubIterator=publications.iterator();
			while(pubIterator.hasNext()){
				auxPub = pubIterator.next();
				if (nextStop > auxPub.getNextPublication()){
					nextStop = auxPub.getNextPublication();
					nextPub=auxPub;
				}
			}

			messages_to_send--;
		}
		
		System.out.println(this.getName() + " writing data...");
		
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
			System.out.println(this.getName() + " done writing data!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void onTraceEvent(TraceEvent tEvent){
		Transmission trans;
		
		trans = new Transmission(tEvent.getOriginEntity().getAid(),
				this.getAid(), new Date(tEvent.getTimestamp()),
				new Date(System.currentTimeMillis()), Transmission.SYSTEM, tEvent.getContent());
		
		synchronized(transmissions){	
			transmissions.add(trans);
		}
	}
	
	public void onMessage(ACLMessage msg){
		Transmission trans;
				
		int index = msg.getContent().indexOf('#');
		trans = new Transmission(msg.getSender(), this.getAid(),
				new Date(Long.parseLong(msg.getContent().substring(0, index))),
				new Date(System.currentTimeMillis()), Transmission.SYSTEM, msg.getContent().substring(index+1));
		
		if (msg.getPerformativeInt() != ACLMessage.AGREE){
			System.out.println(this.getAid() + " NOT AGREE! Message " + msg.toString());
		}
		
		synchronized(transmissions){
			transmissions.add(trans);
		}
	}
}