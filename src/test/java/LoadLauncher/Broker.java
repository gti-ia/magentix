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

public class Broker extends BaseAgent{
	private class Channel{
		private String channelName;
		private ArrayList<AgentID> publishers;
		private ArrayList<AgentID> subscriptors;
		
		public Channel (String channelName){
			this.channelName=channelName;
			this.publishers = new ArrayList<AgentID>();
			this.subscriptors = new ArrayList<AgentID>();
		}
		
		public String getChannelName(){
			return this.channelName;
		}
		
		public ArrayList<AgentID> getSubscriptors(){
			return this.subscriptors;
		}
		
		public ArrayList<AgentID> getPublishers(){
			return this.publishers;
		}
	}

	private String LOG_FILE_NAME;
	
	private ArrayList<Channel> channels;
	private ArrayList<Transmission> transmissions;
	private Long messages_to_receive, timeout, received=Long.valueOf(0);
	
	public Broker(Load load_spec) throws Exception {
		super(load_spec.getMiddleAgentID());
		
		this.LOG_FILE_NAME = load_spec.getOutPath() + "/" +
			Load.prefixes[load_spec.getStrategy()] + "_" + this.getName() + "_result_log.txt";
		File LOG_FILE = new File(LOG_FILE_NAME);
		LOG_FILE.delete();
		LOG_FILE.createNewFile();
		this.channels=new ArrayList<Channel>();
		this.transmissions = new ArrayList<Transmission>();
		this.timeout = load_spec.MAX_PERIOD;
		this.messages_to_receive = load_spec.MESSAGES_TO_SEND;
	}
	
	private Channel getChannel(String channelName){
		Iterator<Channel> chanIter = channels.iterator();
		Channel auxChan;
		
		while(chanIter.hasNext()){
			auxChan = chanIter.next();
			
			if (auxChan.getChannelName().equals(channelName)){
				return auxChan;
			}
		}
		
		return null;
	}
	
	public void execute() {
		while(messages_to_receive > 0){
			System.out.println(this.getAid().toString() + ": " + messages_to_receive + " messages to receive (received " + received + ")...");
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
		String command;
		String channelName="";
		Channel auxChan;
		AgentID auxAid;
		ACLMessage auxMsg;
		int index1, index2;
		String minicontent;
		Transmission trans;
		Date auxDate = new Date(System.currentTimeMillis());
		
		index1=msg.getContent().indexOf('#');
		minicontent=msg.getContent().substring(index1+1);
		
//		System.out.println(this.getAid().toString() + ": Msg " + msg.toString());
		
		if (msg.getPerformativeInt() == ACLMessage.INFORM) {
			trans = new Transmission(msg.getSender(), this.getAid(),
					new Date(Long.parseLong(msg.getContent().substring(0, index1))),
					auxDate, Transmission.COMMUNICATION, minicontent);
			synchronized(transmissions){
				transmissions.add(trans);
			}
			synchronized(messages_to_receive){
				messages_to_receive--;
			}
			received++;
				
			index1=index1+1;
				
			index2=msg.getContent().indexOf('#', index1);
			channelName=msg.getContent().substring(index2+1);
				
			synchronized(this.getChannel(channelName).getSubscriptors()){
				Iterator<AgentID> subsIterator = this.getChannel(channelName).getSubscriptors().iterator();
				
				while(subsIterator.hasNext()){
					auxAid=subsIterator.next();
					
					auxMsg = msg;
					auxMsg.setReceiver(auxAid);
					auxMsg.setSender(this.getAid());
					auxMsg.setContent(String.valueOf(System.currentTimeMillis()) + "#" + minicontent + "#" + msg.getContent().substring(0, msg.getContent().indexOf('#')));
					send(auxMsg);
				}
			}
		}
		else{
			trans = new Transmission(msg.getSender(), this.getAid(),
					new Date(Long.parseLong(msg.getContent().substring(0, index1))),
					auxDate, Transmission.SYSTEM, minicontent);
			synchronized(transmissions){
				transmissions.add(trans);
			}
			
			if (msg.getPerformativeInt() == ACLMessage.REQUEST){
				index1=index1+1;
				index2=msg.getContent().indexOf('#', index1);
				command=msg.getContent().substring(index1, index2);

				if (command.equals("PUBLISH")){
					channelName=msg.getContent().substring(index2+1);
					if ((auxChan=getChannel(channelName)) != null){
						auxChan.getPublishers().add(msg.getSender());
					}
					else{
						auxChan=new Channel(channelName);
						auxChan.getPublishers().add(msg.getSender());
						channels.add(auxChan);
					}
				}
			}
			else if (msg.getPerformativeInt() == ACLMessage.SUBSCRIBE){
				channelName=msg.getContent().substring(index1+1);
				if ((auxChan=getChannel(channelName)) != null){
					auxChan.getSubscriptors().add(msg.getSender());
				}
				else{
					auxChan=new Channel(channelName);
					auxChan.getSubscriptors().add(msg.getSender());
					channels.add(auxChan);
				}
			}
				
			ACLMessage replyMsg = new ACLMessage();
			replyMsg.setLanguage("ACL");
			replyMsg.setSender(this.getAid());
			replyMsg.setReceiver(msg.getSender());
			replyMsg.setPerformative(ACLMessage.AGREE);
			replyMsg.setContent(String.valueOf(System.currentTimeMillis()) + "#" + channelName);
			send(replyMsg);
		}
	}
}
