//CAMBIOS

// Le afecta los cambios en la forma en que ahora se lanzan las subconversaciones
// Elimino autostartfactory

package es.upv.dsic.gti_ia.cAgents;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.Semaphore;

import es.upv.dsic.gti_ia.core.ACLMessage;

/**
 * 
 * @author Ricard Lopez Fogues
 *
 */

public class CProcessorFactory{
	private ACLMessage template;
	public String name;
	protected CProcessor myCProcessor;
	private CAgent myAgent;
	protected Semaphore availableConversations;
	private ArrayList<String> children;
		
	public CProcessorFactory(String name, ACLMessage template, int availableConversations){
		this.name = name;
		this.template = template;
		this.availableConversations = new Semaphore(availableConversations, false);
		this.myCProcessor = new CProcessor();
		children = new ArrayList<String>();
	}
	
	public void setAvailableConversations(int availableConversations){
		this.availableConversations = new Semaphore(availableConversations);
	}
	
	public void setTemplate(ACLMessage template){
		this.template = template.clone();
	}
	
	protected void setAgent(CAgent myAgent){
		this.myAgent = myAgent;
		myCProcessor.myAgent = myAgent;
	}
	
	public int getAvailableConversations(){
		return availableConversations.availablePermits();
	}
	
	public ACLMessage getTemplate(){
		return template.clone();
	}
	
	public CProcessor cProcessorTemplate(){
		return this.myCProcessor;
	}
	

	// PENDIENTE: cambiar índice por objeto
	
	protected synchronized void startConversation(ACLMessage msg, int factoryArrayIndex, CProcessor parent, Boolean isSync){	
		CProcessor cloneProcessor = (CProcessor) myCProcessor.clone();
		
		cloneProcessor.setConversationID(msg.getConversationId());
		cloneProcessor.addMessage(msg);
		cloneProcessor.setIdle(false);
		cloneProcessor.setFactoryArrayIndex(factoryArrayIndex);
		cloneProcessor.setParent(parent);
		cloneProcessor.setIsSynchronized(isSync);
		setParentChildren(cloneProcessor); //???
		
		myAgent.addProcessor(msg.getConversationId(), cloneProcessor);
		myAgent.exec.execute(cloneProcessor);
	}

	
	private void setParentChildren(CProcessor parent){ //???
		for(int i=0; i< children.size(); i++){
			String factoryName = children.get(i);
			for(int j=0; j< myAgent.factories.size(); j++)
				if(myAgent.factories.get(j).name.equals(factoryName))
					myAgent.factories.get(j).cProcessorTemplate().setParent(parent);
		}		
	}
	
	public synchronized void addChild(CProcessorFactory child){
		children.add(child.name);
	}
	
	public boolean templateIsEqual(ACLMessage template){
		return (this.template.headersAreEqual(template)) && (this.template.getPerformativeInt() == template.getPerformativeInt());
	}
}
