package LoadLauncher;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import es.upv.dsic.gti_ia.core.AgentID;

public class Load {

	public class Publication{
		private String channelName;
		private Long period;
		private Long nextPublication;
		private ArrayList<AgentID> publishers;
		private ArrayList<AgentID> subscribers;
		private long messages_to_send;
		
		public Publication(String channelName, long period){
			this.channelName=channelName;
			this.period=period;
			this.nextPublication = (long) -1;
			this.subscribers=new ArrayList<AgentID>();
			this.messages_to_send = 0;
		}
		
		public Publication(String channelName, long period, long n_messages){
			this.channelName=channelName;
			this.period=period;
			this.nextPublication = (long) -1;
			this.subscribers=new ArrayList<AgentID>();
			this.messages_to_send = n_messages;
		}
		
		public String getChannelName(){
			return this.channelName;
		}
		
		public Long getPeriod(){
			return this.period;
		}
		
		public Long getNextPublication(){
			return this.nextPublication;
		}
		
		public ArrayList<AgentID> getPublishers(){
			return this.publishers;
		}
		
		public ArrayList<AgentID> getSubscribers(){
			return this.subscribers;
		}
		
		public long getMessagesToSend(){
			return this.messages_to_send;
		}
		
		public void setNextPublication(Long nextPublication){
			this.nextPublication=nextPublication;
		}
		
		public void setMessagesToSend(long n_messages){
			this.messages_to_send = n_messages;
		}
	}
	
	public class PublisherSpec{
		private AgentID aid;
		private ArrayList<Publication> publications;
		
		public PublisherSpec(AgentID aid){
			this.aid=aid;
			this.publications=new ArrayList<Publication>();
		}
		
		public AgentID getAid(){
			return this.aid;
		}
		
		public ArrayList<Publication> getPublications(){
			return this.publications;
		}
	}
	
	public class SubscriberSpec{
		private AgentID aid;
		private ArrayList<String> subscriptions;
		private long messges_to_receive;
		private long max_period;
		
		public SubscriberSpec(AgentID aid){
			this.aid=aid;
			this.subscriptions = new ArrayList<String>();
			this.messges_to_receive = 0;
			this.max_period = 0;
		}
		
		public AgentID getAid(){
			return this.aid;
		}
		
		public ArrayList<String> getSubscriptions(){
			return this.subscriptions;
		}
		
		public long getMessagesToReceive(){
			return this.messges_to_receive;
		}
		
		public long getMaxPeriod(){
			return this.max_period;
		}
		
		public void setSubscritions(ArrayList<String> subscriptions){
			this.subscriptions = subscriptions;
		}
		
		public void setMessagesToReceive(long n_messages){
			this.messges_to_receive = n_messages;
		}
		
		public void setMaxPeriod(long period){
			this.max_period = period;
		}
	}
	
	public class Metric_spec{
		private String name;
		private int type;
		private int valueInt;
		private long valueLong;
		private String valueString;
		private float valueFloat;
		
		public static final int N_TYPES = 4;
		public static final int UNKNOWN_TYPE = -1;
		public static final int INT = 0;
		public static final int LONG = 1;
		public static final int STRING = 2;
		public static final int FLOAT = 3;
		
		public Metric_spec(String name, int type){
			if ((type < 0) || (type >= N_TYPES)){
				type = UNKNOWN_TYPE;
			}
			else{
				type = this.type;
			}
			this.name = name;
			
			this.valueFloat=0;
			this.valueInt=0;
			this.valueLong=0;
		}
		
		public int getType(){
			return this.type;
		}
		
		public String getName(){
			return this.name;
		}
		
		public int getValueInt(){
			return this.valueInt;
		}
		
		public long getValueLong(){
			return this.valueLong;
		}
		
		public String getValueString(){
			return this.valueString;
		}
		
		public double getValueFloat(){
			return this.valueFloat;
		}
		
		public boolean setValueInt(int value){
			if (this.type == INT){
				this.valueInt=value;
				return true;
			}
			else{
				return false;
			}
		}
		
		public boolean setValueLong(long value){
			if (this.type == LONG){
				this.valueLong = value;
				return true;
			}
			else{
				return false;
			}
		}
		
		public boolean setValueString(String value){
			if (this.type == STRING){
				this.valueString = value;
				return true;
			}
			else{
				return false;
			}
		}
		
		public boolean setValueFloat(float value){
			if (this.type == FLOAT){
				this.valueFloat = value;
				return true;
			}
			else{
				return false;
			}
		}
		
		public String valueToString(){
			switch (this.type){
				case INT:
					return String.valueOf(this.valueInt);

				case LONG:
					return String.valueOf(this.valueLong);
					
				case STRING:
					return this.valueString;
					
				case FLOAT:
					return String.valueOf(this.valueFloat);
					
				default:
					return "---";
			}
		}
	}

	public class Factor_spec{
		private String name;
		private int type;
		private int valueInt;
		private long valueLong;
		private String valueString;
		private float valueFloat;
		
		public static final int N_TYPES = 4;
		public static final int UNKNOWN_TYPE = -1;
		public static final int INT = 0;
		public static final int LONG = 1;
		public static final int STRING = 2;
		public static final int FLOAT = 3;
		
		public Factor_spec(String name, int type){
			if ((type < 0) || (type >= N_TYPES)){
				type = UNKNOWN_TYPE;
			}
			else{
				type = this.type;
			}
			this.name = name;
			
			this.valueFloat=0;
			this.valueInt=0;
			this.valueLong=0;
		}
		
		public int getType(){
			return this.type;
		}
		
		public String getName(){
			return this.name;
		}
		
		public int getValueInt(){
			return this.valueInt;
		}
		
		public long getValueLong(){
			return this.valueLong;
		}
		
		public String getValueString(){
			return this.valueString;
		}
		
		public double getValueFloat(){
			return this.valueFloat;
		}
		
		public boolean setValueInt(int value){
			if (this.type == INT){
				this.valueInt=value;
				return true;
			}
			else{
				return false;
			}
		}
		
		public boolean setValueLong(long value){
			if (this.type == LONG){
				this.valueLong = value;
				return true;
			}
			else{
				return false;
			}
		}
		
		public boolean setValueString(String value){
			if (this.type == STRING){
				this.valueString = value;
				return true;
			}
			else{
				return false;
			}
		}
		
		public boolean setValueFloat(float value){
			if (this.type == FLOAT){
				this.valueFloat = value;
				return true;
			}
			else{
				return false;
			}
		}
		
		public String valueToString(){
			switch (this.type){
				case INT:
					return String.valueOf(this.valueInt);

				case LONG:
					return String.valueOf(this.valueLong);
					
				case STRING:
					return this.valueString;
					
				case FLOAT:
					return String.valueOf(this.valueFloat);
					
				default:
					return "---";
			}
		}
	}
	
	public long TEST_TIME;
	public long MESSAGES_TO_SEND = 0;
	public long MESSAGES_TO_RECEIVE = 0;
	public long MAX_PERIOD = 0;
		
	static public final int N_STRATEGIES = 5;
	
	static public final int ALL = 0;
	static public final int BROADCAST = 1;
	static public final int MATCH_MAKER = 2;
	static public final int BROKER = 3;
	static public final int EVENT_TRACE = 4;
	
	
	static public final String[] prefixes = {"all", "bcast", "match", "brokr", "trace"};
	
	public int N_PUBLISHERS;
	public int N_SUBSCRIBERS;
	public int N_CHANNELS;
	
	private String out_path;
	private int STRATEGY;
	
	private ArrayList<PublisherSpec> publishers;
	private ArrayList<SubscriberSpec> subscribers;
	
	private AgentID middleAgentID;
	
	int N_METRICS = 12;
	
	int MESSAGES_SENT_SYS_METRIC = 0;
	int MESSAGES_RECV_SYS_METRIC = 1;
	int MESSAGES_SENT_COM_METRIC = 2;
	int MESSAGES_RECV_COM_METRIC = 3;
	int EXEC_TIME_METRIC = 4;
	int SND_TIME_METRIC = 5;
	int RECV_TIME_METRIC = 6;
	int ROUTING_TIME_METRIC = 7;
	int SND_FREQ_METRIC = 8;
	int RCV_FREQ_METRIC = 9;
	int SND_FREQ_MA_METRIC = 10;
	int RCV_FREQ_MA_METRIC = 11;
	
	Metric_spec[] metrics = new Metric_spec[]{
		new Metric_spec("MESSAGES SENT SYS", Metric_spec.INT),
		new Metric_spec("MESSAGES RECV SYS", Metric_spec.INT),
		new Metric_spec("MESSAGES SENT COM", Metric_spec.INT),
		new Metric_spec("MESSAGES RECV COM", Metric_spec.INT),
		new Metric_spec("EXEC TIME", Metric_spec.LONG),
		new Metric_spec("SND TIME", Metric_spec.LONG),
		new Metric_spec("RECV TIME", Metric_spec.LONG),
		new Metric_spec("ROUTING TIME", Metric_spec.LONG),
		new Metric_spec("SND FREQ", Metric_spec.FLOAT),
		new Metric_spec("RCV FREQ", Metric_spec.FLOAT),
		new Metric_spec("SND FREQ MA", Metric_spec.FLOAT),
		new Metric_spec("RCV FREQ MA", Metric_spec.FLOAT)
	};
	
	int N_FACTORS = 4;
	
	int N_PUBLISHERS_FACTOR = 0;
	int N_SUBSCRIBERS_FACTOR = 1;
	int N_CHANNELS_FACTOR = 2;
	int INTEREST_FACTOR = 3;
	
	Factor_spec[] factors = new Factor_spec[]{
		new Factor_spec("N PUBLISHERS", Factor_spec.INT),
		new Factor_spec("N SUBSCRIBERS", Factor_spec.INT),
		new Factor_spec("N CHANNELS", Factor_spec.INT),
		new Factor_spec("INTEREST", Factor_spec.INT)
	};
			
	public Load(String path, AgentID middleAgent, int strategy){
		
		if ((strategy >= 0) && (strategy < N_STRATEGIES)){
			this.STRATEGY=strategy;
			this.out_path=path;
			this.middleAgentID=middleAgent;
			
			initialize();
		}
		else{
			System.out.println("Error: Bad strategy specification");
		}
	}
	
	public AgentID getMiddleAgentID(){
		return this.middleAgentID;
	}
	
	public String getOutPath (){
		return this.out_path;
	}
	
	public int getStrategy(){
		return this.STRATEGY;
	}
	
	public ArrayList<PublisherSpec> getPublishers(){
		return this.publishers;
	}
	
	public ArrayList<SubscriberSpec> getSubscribers(){
		return this.subscribers;
	}
	
	private void initialize(){
		PublisherSpec auxPublisher;
		SubscriberSpec auxSubscriber;
		
		publishers=new ArrayList<PublisherSpec>();
		subscribers=new ArrayList<SubscriberSpec>();
		
		try {
			BufferedReader in = new BufferedReader(new FileReader(this.out_path + "/cnf"));
			
			String line, command, aid_str, channel_name;
			int index1, index2, nline;
			int n_channels;
			Long period;
			
			nline=0;
			while (((line=in.readLine()) != null) && (line.length() > 0)){
				nline++;
				if (line.startsWith("#") == false){ // Comments start with '#'
					index1=line.indexOf(' ');
					command=line.substring(0, index1);
					if (command.equals("TEST_TIME")){
						this.TEST_TIME=Long.parseLong(line.substring(index1+1));
					}
					else if (command.equals("N_PUBLISHERS")){
						this.N_PUBLISHERS=Integer.parseInt(line.substring(index1+1));
					}
					else if (command.equals("N_SUBSCRIBERS")){
						this.N_SUBSCRIBERS=Integer.parseInt(line.substring(index1+1));;
					}
					else if (command.equals("N_CHANNELS")){
						this.N_CHANNELS=Integer.parseInt(line.substring(index1+1));
					}
					else if (command.equals("PUBLISHER")){
						index1++;
						index2=line.indexOf(' ', index1);
						aid_str=line.substring(index1, index2);
						auxPublisher=new PublisherSpec(new AgentID(aid_str));
						index1=index2+1;
						index2=line.indexOf(' ', index1);
						n_channels=Integer.parseInt(line.substring(index1, index2));
						for (int i=0; i < (n_channels-1); i++){
							index1=index2+1;
							index2=line.indexOf(' ', index1);
							channel_name=line.substring(index1, index2);
							index1=index2+1;
							index2=line.indexOf(' ', index1);
							period=Long.parseLong(line.substring(index1, index2));
							if (this.MAX_PERIOD < period){
								this.MAX_PERIOD=period;
							}
							auxPublisher.getPublications().add(new Publication(channel_name, period, this.TEST_TIME/period));
							this.MESSAGES_TO_SEND = this.MESSAGES_TO_SEND + this.TEST_TIME/period;
						}
						index1=index2+1;
						index2=line.indexOf(' ', index1);
						channel_name=line.substring(index1, index2);
						period=Long.parseLong(line.substring(index2+1));
						if (this.MAX_PERIOD < period){
							this.MAX_PERIOD=period;
						}
						auxPublisher.getPublications().add(new Publication(channel_name, period, this.TEST_TIME/period));
						this.MESSAGES_TO_SEND = this.MESSAGES_TO_SEND + this.TEST_TIME/period;
						publishers.add(auxPublisher);
//						System.out.println("Loaded " + auxPublisher.getAid().toString());
					}
					else if (command.equals("SUBSCRIBER")){
						index1++;
						index2=line.indexOf(' ', index1);
						aid_str=line.substring(index1, index2);
						auxSubscriber=new SubscriberSpec(new AgentID(aid_str));
						index1=index2+1;
						index2=line.indexOf(' ', index1);
						n_channels=Integer.parseInt(line.substring(index1, index2));
						for (int i=0; i < (n_channels-1); i++){
							index1=index2+1;
							index2=line.indexOf(' ', index1);
							channel_name=line.substring(index1, index2);
							auxSubscriber.getSubscriptions().add(channel_name);
						}
						index1=index2+1;
						channel_name=line.substring(index1);
						auxSubscriber.getSubscriptions().add(channel_name);
						subscribers.add(auxSubscriber);
//						System.out.println("Loaded " + auxSubscriber.getAid().toString());
					}
					else if (command.equals("GRAF")){
						// Graphs to be generated...
						// GRAF METRIC FACTOR
					}
					else{
						System.out.println("Wrong command at line " + nline + ": " + line);
					}
				}
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/*********************** BEGIN LOAD CODE ******************************/
//		TEST_TIME = 10000;
//		
//		N_PUBLISHERS = 3;
//		N_SUBSCRIBERS = 3;
//		N_CHANNELS = 4;
//		
//		auxPublisher=new PublisherSpec(new AgentID("qpid://publisher0@localhost:8080"));
//		auxPublisher.getPublications().add(new Publication("C0", 1000));
//		this.MESSAGES_TO_SEND = this.MESSAGES_TO_SEND + this.TEST_TIME/1000;
//		auxPublisher.getPublications().add(new Publication("C1", 2000));
//		this.MESSAGES_TO_SEND = this.MESSAGES_TO_SEND + this.TEST_TIME/2000;
//		publishers.add(auxPublisher);
//				
//		auxPublisher=new PublisherSpec(new AgentID("qpid://publisher1@localhost:8080"));
//		auxPublisher.getPublications().add(new Publication("C2", 1000));
//		this.MESSAGES_TO_SEND = this.MESSAGES_TO_SEND + this.TEST_TIME/1000;
//		publishers.add(auxPublisher);
//		
//		auxPublisher=new PublisherSpec(new AgentID("qpid://publisher2@localhost:8080"));
//		auxPublisher.getPublications().add(new Publication("C3", 2000));
//		this.MESSAGES_TO_SEND = this.MESSAGES_TO_SEND + this.TEST_TIME/2000;
//		publishers.add(auxPublisher);
//		
//		auxSubscriber=new SubscriberSpec(new AgentID("qpid://subscriber0@localhost:8080"));
//		auxSubscriber.getSubscriptions().add("C0");
//		subscribers.add(auxSubscriber);
//		
//		auxSubscriber=new SubscriberSpec(new AgentID("qpid://subscriber1@localhost:8080"));
//		auxSubscriber.getSubscriptions().add("C1");
//		auxSubscriber.getSubscriptions().add("C2");
//		subscribers.add(auxSubscriber);
//		
//		auxSubscriber=new SubscriberSpec(new AgentID("qpid://subscriber2@localhost:8080"));
//		auxSubscriber.getSubscriptions().add("C3");
//		subscribers.add(auxSubscriber);
		/************************ END LOAD CODE *******************************/
		
		String channel_name;
		synchronized(subscribers){
			Iterator<SubscriberSpec> subIter = subscribers.iterator();
			while(subIter.hasNext()){
				long tmp_messages = 0, max_period=0;
				auxSubscriber=subIter.next();
				synchronized(auxSubscriber.getSubscriptions()){
					Iterator<String> stringIter=auxSubscriber.getSubscriptions().iterator();
					while(stringIter.hasNext()){
						channel_name=stringIter.next();
						// Locating Publisher and channel
						int pub_number, chan_number, index3;
						index3=channel_name.indexOf('_');
						pub_number=Integer.parseInt(channel_name.substring(1, index3));
						chan_number=Integer.parseInt(channel_name.substring(index3+1));
//						System.out.println(auxSubscriber.getAid().toString() + " " + channel_name + " " +
//							publishers.get(pub_number).getPublications().get(chan_number).getMessagesToSend() + " messages");
						tmp_messages=tmp_messages+publishers.get(pub_number).getPublications().get(chan_number).getMessagesToSend();
						if (max_period < publishers.get(pub_number).getPublications().get(chan_number).getPeriod()){
							max_period = publishers.get(pub_number).getPublications().get(chan_number).getPeriod();
						}
					}
				}
				auxSubscriber.setMessagesToReceive(tmp_messages);
				this.MESSAGES_TO_RECEIVE = this.MESSAGES_TO_RECEIVE + tmp_messages;
				auxSubscriber.setMaxPeriod(max_period);
			}
		}
	}
}
