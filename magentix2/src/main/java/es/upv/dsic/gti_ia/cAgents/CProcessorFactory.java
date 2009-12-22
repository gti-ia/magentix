package es.upv.dsic.gti_ia.cAgents;

import java.util.concurrent.Semaphore;

import es.upv.dsic.gti_ia.core.ACLMessage;

public class CProcessorFactory{
	private ACLMessage template;
	public String name;
	private CProcessor myCProcessor;
	private CAgent myAgent;
	protected Semaphore availableConversations;
		
	public CProcessorFactory(String name, ACLMessage template, int availableConversations){
		this.name = name;
		this.template = template;
		this.availableConversations = new Semaphore(availableConversations, false);
		this.myCProcessor = new CProcessor();
	}
	
	public void setAvailableConversations(int availableConversations){
		this.availableConversations = new Semaphore(availableConversations);
	}
	
	public void setTemplate(ACLMessage template){
		this.template = template;
	}
	
	protected void setAgent(CAgent myAgent){
		this.myAgent = myAgent;
		myCProcessor.myAgent = myAgent;
	}
	
	public int getAvailableConversations(){
		return availableConversations.availablePermits();
	}
	
	public ACLMessage getTemplate(){
		return template;
	}
	
	/*public void setCProcessor(CProcessor cproc){
		myCProcessor = cproc;
		myCProcessor.myAgent = myAgent;
	}*/
	
	public CProcessor getCProcessor(){
		return this.myCProcessor;
	}
	
	protected synchronized void startConversation(ACLMessage msg, int factoryArrayIndex){	
		CProcessor cloneProcessor = (CProcessor) myCProcessor.clone();
		cloneProcessor.setConversationID(msg.getConversationId());
		cloneProcessor.addMessage(msg);
		cloneProcessor.setIdle(false);
		cloneProcessor.setFactoryArrayIndex(factoryArrayIndex);
		myAgent.exec.execute(cloneProcessor);
		myAgent.addProcessor(msg.getConversationId(), cloneProcessor);		
	}
	
	protected synchronized void forcedStartConversation(ACLMessage msg, int factoryArrayIndex){
		CProcessor cloneProcessor = (CProcessor) myCProcessor.clone();
		cloneProcessor.setConversationID(msg.getConversationId());
		cloneProcessor.setIdle(false);
		cloneProcessor.setFactoryArrayIndex(factoryArrayIndex);
		myAgent.addProcessor(msg.getConversationId(), cloneProcessor);
		myAgent.exec.execute(cloneProcessor);		
	}
	
	public boolean templateIsEqual(ACLMessage template){
		return (this.template.headersAreEqual(template)) && (this.template.getPerformativeInt() == template.getPerformativeInt());
	}
}
