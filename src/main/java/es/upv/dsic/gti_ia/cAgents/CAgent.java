package es.upv.dsic.gti_ia.cAgents;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;

public abstract class CAgent extends BaseAgent {

	private LinkedBlockingQueue<ACLMessage> messageList;
	private Map<String, CProcessor> processors = new HashMap<String, CProcessor>();
	protected ArrayList<CProcessorFactory> factories = new ArrayList<CProcessorFactory>();
	protected ExecutorService exec;
	protected final Semaphore availableSends = new Semaphore(1, true);
	private int startingFactoryIndex = -1;
	private String startingFactoryConversationId = "";
	private Map<String, Timer> timers = new HashMap<String, Timer>();
	
	public CAgent(AgentID aid) throws Exception {
		super(aid);
		exec =  Executors.newCachedThreadPool();
		messageList = new LinkedBlockingQueue<ACLMessage>();
	}
	
	protected CProcessorFactory createWelcomeFactory(){
		ACLMessage welcomeMessage = new ACLMessage(ACLMessage.INFORM);
		welcomeMessage.setHeader("Purpose", "Welcome");
		CProcessorFactory welcomeFactory = new CProcessorFactory("WelcomeFactory", welcomeMessage, 1);
		welcomeFactory = new CProcessorFactory("welcomeFactory", welcomeMessage , 1);
		welcomeFactory.getCProcessor().registerFirstState(new DefaultBeginState("defaultBeginState"));
		welcomeFactory.getCProcessor().registerState(new DefaultFinalState("defaultFinalState"));
		welcomeFactory.getCProcessor().addTransition("defaultBeginState", "defaultFinalState");
		return welcomeFactory;
	}
	
	private void createDefaultFactory(){
		CProcessorFactory defaultFactory = new CProcessorFactory("DefaultFactory", new ACLMessage(ACLMessage.UNKNOWN), 1000);
		defaultFactory.getCProcessor().registerFirstState(new DefaultBeginState("defaultBeginState"));
		defaultFactory.getCProcessor().registerState(new DefaultFinalState("defaultFinalState"));
		defaultFactory.getCProcessor().addTransition("defaultBeginState", "defaultFinalState");
		this.addFactory(defaultFactory);
	}
	
	class DefaultBeginState extends BeginState{

		public DefaultBeginState(String n) {
			super(n);
		}

		@Override
		protected String run(CProcessor myProcessor, ACLMessage msg) {
			System.out.println("Default factory tratando mensaje "+msg.getContent()+ " origen: "+msg.getSender()
					+" ConversationID: "+msg.getConversationId());
			return "defaultFinalState";
		}
		
	}
	
	class DefaultFinalState extends FinalState{

		public DefaultFinalState(String n) {
			super(n);
		}

		@Override
		protected String run(CProcessor myProcessor) {
			return null;
		}
		
	}
	
	public void send(ACLMessage msg){
		super.send(msg);
	}
	
	public synchronized void addFactory(CProcessorFactory factory){
		factory.setAgent(this);
		factories.add(factory);
	}
	
	public synchronized void addStartingFactory(CProcessorFactory factory, String conversationId){
		factory.setAgent(this);
		factories.add(factory);
		this.startingFactoryIndex = factories.size() - 1;
		this.startingFactoryConversationId = conversationId;
	}
	
	public synchronized void removeFactory(String name){
		for(int i=0; i<factories.size();i++){
			if(factories.get(i).name.equals(name)){
				factories.remove(i);
				break;
			}
		}
	}
	
	protected synchronized void addProcessor(String conversationID, CProcessor processor){
		processors.put(conversationID, processor);
	}
	
	protected synchronized void removeProcessor(String conversationID){
		processors.remove(conversationID);
	}	
	
	private synchronized void processMessage(ACLMessage msg){
		CProcessor auxProcessor = processors.get(msg.getConversationId());
		boolean accepted = false;
		if(auxProcessor != null){
			processors.get(msg.getConversationId()).addMessage(msg);
			if(auxProcessor.isIdle()){
				auxProcessor.setIdle(false);
				exec.execute(auxProcessor);
			}
		}			
		else{
			for(int i=1; i<factories.size();i++){
				if(factories.get(i).templateIsEqual(msg)){					
					factories.get(i).startConversation(msg,i);
					accepted = true;
					break;				
				}
			}
			if(!accepted){
				System.out.println("Agente: "+this.getName()+" Mensaje a tratar por la DefaultFactory");
				factories.get(0).startConversation(msg,0);
			}
		}
	}
	
	protected int addTimer(final String conversationId, int milliseconds){
		if(this.timers.get(conversationId) == null){
			Date timeToRun = new Date(System.currentTimeMillis()+milliseconds);
			Timer timer = new Timer();
	    
			timer.schedule(new TimerTask() {
	            public void run() {
	               ACLMessage waitMessage = new ACLMessage(ACLMessage.INFORM);
	               waitMessage.setHeader("Purpose", "WaitMessage");
	               waitMessage.setConversationId(conversationId);
	               processMessage(waitMessage);
	            }
			}, timeToRun);
			this.timers.put(conversationId, timer);
			return 1;
		}
		else
			return 0;
	}
	
	protected int removeTimer(String conversationId){
		if(this.timers.get(conversationId) == null)
			return 0;
		else{
			this.timers.get(conversationId).cancel();
			this.timers.remove(conversationId);
			return 1;
		}
	}
	
	protected void endConversation(int factoryArrayIndex){
		factories.get(factoryArrayIndex).availableConversations.release();
	}
	
	public final ACLMessage receiveACLMessage() throws InterruptedException {
		return messageList.take();
	}

	protected synchronized void addMessage(ACLMessage msg) {
		messageList.add(msg);
	}

	public void onMessage(ACLMessage msg) {
		addMessage(msg);
	}
	
	protected abstract void setFactories();
	
	protected final void execute(){
		this.createDefaultFactory();
		setFactories();
		//if a starting factory is set, then we force it to start
		ACLMessage startingMessage = new ACLMessage(ACLMessage.INFORM);
		startingMessage.setConversationId(startingFactoryConversationId);
		if(this.startingFactoryIndex >= 0){
			factories.get(this.startingFactoryIndex).forcedStartConversation(startingMessage, this.startingFactoryIndex);
		}
		
		System.out.println("Soy "+this.getName()+". Arranco");
		
		while(true){
			try {
				this.processMessage(this.receiveACLMessage());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}			
		}		
	}
}