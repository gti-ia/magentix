//CAMBIOS

// Le afecta los cambios en la forma en que ahora se lanzan las subconversaciones
// Elimino autostartfactory

package es.upv.dsic.gti_ia.cAgents;

import java.util.concurrent.Semaphore;

import es.upv.dsic.gti_ia.core.ACLMessage;

/**
 * 
 * @author Ricard Lopez Fogues
 * 
 */
// PENDIENTE
// Distinguir fábrica de iniciador y participante

public class CProcessorFactory {
	private ACLMessage template;
	String name;
	private CProcessor myCProcessor;
	private CAgent myAgent;
	Semaphore availableConversations; // PENDIENTE buscar otra solución
//	private ArrayList<String> children; // ??? Necesario?



	
	public CProcessorFactory(String name, ACLMessage template,
			int availableConversations) {
		this.name = name;
		this.template = template;
		this.availableConversations = new Semaphore(availableConversations,
				false);
		this.myCProcessor = new CProcessor();
//		children = new ArrayList<String>();
	}

	public void setTemplate(ACLMessage template) {
		this.template = template;
	}

	void setAgent(CAgent myAgent) {
		this.myAgent = myAgent;
		myCProcessor.setMyAgent(myAgent);
	}

	public ACLMessage getTemplate() {
		return template.clone();
	}

	public CProcessor cProcessorTemplate() {
		return this.myCProcessor;
	}

	// PENDIENTE: cambiar índice por objeto

	synchronized CProcessor startConversation(ACLMessage msg, 
			CProcessor parent, Boolean isSync) {
		CProcessor cloneProcessor = (CProcessor) myCProcessor.clone();

		cloneProcessor.setConversationID(msg.getConversationId());
		cloneProcessor.addMessage(msg);
		cloneProcessor.setIdle(false);
		cloneProcessor.setFactory(this);
		cloneProcessor.setParent(parent);
		cloneProcessor.setIsSynchronized(isSync);
//		setParentChildren(cloneProcessor); // ???

		myAgent.addProcessor(msg.getConversationId(), cloneProcessor);
		myAgent.exec.execute(cloneProcessor);
		return (cloneProcessor);
	}

//	private void setParentChildren(CProcessor parent) { // ??? Necesario?
//		for (int i = 0; i < children.size(); i++) {
//			String factoryName = children.get(i);
//			for (int j = 0; j < myAgent.factories.size(); j++)
//				if (myAgent.factories.get(j).name.equals(factoryName))
//					myAgent.factories.get(j).cProcessorTemplate().setParent(
//							parent);
//		}
//	}
//
//	public synchronized void addChild(CProcessorFactory child) { // ???
//																	// Necesario?
//		children.add(child.name);
//	}

	// PENDIENTE
	// Hacer una comparación de mensaje con template completa.
	// Probablemente mejor en ACLMessage

	boolean templateIsEqual(ACLMessage template) {
		return (this.template.headersAreEqual(template))
				&& (this.template.getPerformativeInt() == template
						.getPerformativeInt());
	}
}
