package es.upv.dsic.gti_ia.cAgents;

/**
 * 
 * @author Ricard Lopez Fogues
 * 
 */
// PENDIENTE
// Distinguir f�brica de iniciador y participante
public class CProcessorFactory {
	//private ACLMessage template;
	private MessageFilter filter;
	String name;
	private CProcessor myCProcessor;
	private CAgent myAgent;
	Semaphore availableConversations; // PENDIENTE buscar otra soluci�n
	int limit;
	private boolean initiator = false;

	// private ArrayList<String> children; // ??? Necesario?

	public CProcessorFactory(String name, MessageFilter filter,
			int conversationsLimit, CAgent myAgent) {
		this.name = name;
		this.filter = filter;
		this.availableConversations = new Semaphore(conversationsLimit, false);
		this.limit = conversationsLimit;
		this.myAgent = myAgent;
		this.myCProcessor = new CProcessor(this.myAgent);
		// children = new ArrayList<String>();
	}

	public int getLimit() {
		return limit;
	}

	public void setFilter(MessageFilter template) {
		this.myAgent.lock();
		this.filter = template;
		this.myAgent.unlock();
	}

	public MessageFilter getFilter() {
		return filter.clone();
	}

	public CProcessor cProcessorTemplate() {
		return this.myCProcessor;
	}

	CProcessor startConversation(ACLMessage msg, CProcessor parent,
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

	protected boolean templateIsEqual(ACLMessage template) {
		return this.filter.compareHeaders(template);
	}
	
	protected void setInitiator(boolean initiator){
		this.initiator = initiator;
	}
	
	protected boolean isInitiator(){
		return initiator;
	}
	
}
