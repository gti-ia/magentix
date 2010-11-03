package es.upv.dsic.gti_ia.cAgents;

import java.util.concurrent.Semaphore;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.MessageFilter;

/**
 * 
 * @author Ricard Lopez Fogues
 * 
 */
// PENDIENTE
// Distinguir f�brica de iniciador y participante
public class CFactory {
	//private ACLMessage template;
	private MessageFilter filter;
	String name;
	private CProcessor myCProcessor;
	private CAgent myAgent;
	Semaphore availableConversations; // PENDIENTE buscar otra soluci�n
	int limit;
	private boolean initiator = false;

	// private ArrayList<String> children; // ??? Necesario?

	/**
	 * Constructor of the class
	 * @param name
	 * @param filter
	 * @param conversationLimit
	 * @param myAgent
	 */
	public CFactory(String name, MessageFilter filter,
			int conversationsLimit, CAgent myAgent) {
		this.name = name;
		this.filter = filter;
		this.availableConversations = new Semaphore(conversationsLimit, false);
		this.limit = conversationsLimit;
		this.myAgent = myAgent;
		this.myCProcessor = new CProcessor(this.myAgent);
		// children = new ArrayList<String>();
	}

	/**
	 * Returns the conversation limit
	 * @return
	 */
	public int getLimit() {
		return limit;
	}

	/**
	 * Sets the message filter that will make this CFactory to start new CProcessors
	 * @param template
	 */
	public void setFilter(MessageFilter template) {
		this.myAgent.lock();
		this.filter = template;
		this.myAgent.unlock();
	}

	/**
	 * Returns this CFactory's message filter
	 * @return
	 */
	public MessageFilter getFilter() {
		return (MessageFilter) filter.clone();
	}

	/**
	 * Returns the CProcessor that acts as template and will be cloned 
	 * in order to create new CProcessors
	 * @return
	 */
	public CProcessor cProcessorTemplate() {
		return this.myCProcessor;
	}

	/**
	 * Creates a new CProcessor that will manage the new conversation
	 * @param msg Initial message
	 * @param parent Parent CProcessor
	 * @param isSync True if it is synchronous, false otherwise
	 * @return the new CProcessor
	 */
	protected CProcessor startConversation(ACLMessage msg, CProcessor parent,
			Boolean isSync) {
		CProcessor cloneProcessor = (CProcessor) myCProcessor.clone();

		cloneProcessor.setConversationID(msg.getConversationId());
		cloneProcessor.addMessage(msg);
		cloneProcessor.setIdle(false);
		cloneProcessor.setFactory(this);
		cloneProcessor.setParent(parent);
		cloneProcessor.setIsSynchronized(isSync);
		cloneProcessor.setInitiator(this.initiator);
		// setParentChildren(cloneProcessor); // ???

		myAgent.addProcessor(msg.getConversationId(), cloneProcessor);
		myAgent.exec.execute(cloneProcessor);
		return (cloneProcessor);
	}
	
	/**
	 * Creates a new CProcessor that will manage the new conversation
	 * @param id The conversation identifier of the new conversation
	 * @param parent Parent CProcessor
	 * @param isSync True if it is synchronous, false otherwise
	 * @return
	 */
	protected CProcessor startConversationWithID(String id, CProcessor parent, Boolean isSync) {
		CProcessor cloneProcessor = (CProcessor) myCProcessor.clone();

		cloneProcessor.setConversationID(id);
		//cloneProcessor.addMessage(msg);
		cloneProcessor.setIdle(false);
		cloneProcessor.setFactory(this);
		cloneProcessor.setParent(parent);
		cloneProcessor.setIsSynchronized(isSync);
		cloneProcessor.setInitiator(this.initiator);
		// setParentChildren(cloneProcessor); // ???

		myAgent.addProcessor(id, cloneProcessor);
		myAgent.exec.execute(cloneProcessor);
		return (cloneProcessor);
	}

	// private void setParentChildren(CProcessor parent) { // ??? Necesario?
	// for (int i = 0; i < children.size(); i++) {
	// String factoryName = children.get(i);
	// for (int j = 0; j < myAgent.factories.size(); j++)
	// if (myAgent.factories.get(j).name.equals(factoryName))
	// myAgent.factories.get(j).cProcessorTemplate().setParent(
	// parent);
	// }
	// }
	//
	// public synchronized void addChild(CProcessorFactory child) { // ???
	// // Necesario?
	// children.add(child.name);
	// }

	// PENDIENTE
	// Hacer una comparaci�n de mensaje con template completa.
	// Probablemente mejor en ACLMessage

	/**
	 * Returns true if the message matches with the message filter
	 */
	protected boolean templateIsEqual(ACLMessage template) {
		if(this.filter == null)
			return true;
		return this.filter.compareHeaders(template);
	}
	
	/**
	 * Sets the type of the CFactory
	 * @param initiator True if this is initiator, false if this is participant
	 */
	protected void setInitiator(boolean initiator){
		this.initiator = initiator;
	}
	
	/**
	 * Returns true if this is initiator, false if this is participant
	 * @return
	 */
	protected boolean isInitiator(){
		return initiator;
	}
	
	/**
	 * Returns this CFactory's name
	 * @return
	 */
	public String getName(){
		return this.name;
	}
	
}
