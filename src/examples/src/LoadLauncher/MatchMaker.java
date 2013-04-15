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

public class MatchMaker extends BaseAgent{
	private class Channel{
		private String channelName;
		private ArrayList<AgentID> publishers;
		
		public Channel (String channelName){
			this.channelName=channelName;
			this.publishers = new ArrayList<AgentID>();
		}
		
		public String getChannelName(){
			return this.channelName;
		}
		
		public ArrayList<AgentID> getPublishers(){
			return this.publishers;
		}
	}
	
	private String LOG_FILE_NAME;
	
	private ArrayList<Channel> channels;
	private Semaphore write_semaphore;
	
	public MatchMaker(Load load_spec) throws Exception{
		super(load_spec.getMiddleAgentID());
		
		this.LOG_FILE_NAME = load_spec.getOutPath() + "/" +
			Load.prefixes[load_spec.getStrategy()] + "_" + this.getName() + "_result_log.txt";
		File LOG_FILE = new File(LOG_FILE_NAME);
		LOG_FILE.delete();
		LOG_FILE.createNewFile();
		
		this.write_semaphore = new Semaphore(1, true);
		
		this.channels=new ArrayList<Channel>();
		
		System.out.println(this.getName() + " launched.");
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
	
	public void onMessage(ACLMessage msg){
		String command;
		String channelName;
		Channel auxChan;
		int index1, index2;
		String auxString;
		String minicontent;
		
		index1=msg.getContent().indexOf('#');
		minicontent=msg.getContent().substring(index1+1);
		
		auxString = Long.toString(System.currentTimeMillis()) + "\t" +
			msg.getContent().substring(0, index1) + "\t" +
			msg.getSender().toString() + "\t" +
			this.getAid().toString()  + "\t" +
			String.valueOf(Transmission.SYSTEM) + "\t"+ minicontent + "\n";
		
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
		else if (msg.getPerformativeInt() == ACLMessage.QUERY_REF){
			channelName=minicontent;
					
			ACLMessage replyMsg = new ACLMessage();
			replyMsg.setLanguage("ACL");
			replyMsg.setSender(this.getAid());
			replyMsg.setReceiver(msg.getSender());
			replyMsg.setPerformative(ACLMessage.INFORM);
					
			String content;
			content = channelName.length() + "#" + channelName + "#" + String.valueOf(this.getChannel(channelName).getPublishers().size());
					
			Iterator<AgentID> pubIter=this.getChannel(channelName).getPublishers().iterator();
			AgentID auxPub;
			while(pubIter.hasNext()){
				auxPub=pubIter.next();
				content=content + "#" + auxPub.toString().length() + "#" + auxPub.toString();
			}
			replyMsg.setContent(String.valueOf(System.currentTimeMillis()) + "#" + content);
			send(replyMsg);
		}
	}
}
