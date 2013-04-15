package jasonAgentsConversations.agentNConv;


import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CFactory;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.core.ACLMessage;

public class ConvCProcessor extends CProcessor{

	Conversation JasonConversation = null;
	
	protected ConvCProcessor(CAgent myAgent) {
		super(myAgent);
	}
	
	public  void setConversation(Conversation conv){
		JasonConversation = conv;
	}
	
	public Conversation getConversation(){
		return JasonConversation;
	}

	protected void setConversationID(String id) {
		super.setConversationID(id);
	}
	
	protected void addMessage(ACLMessage msg) {
		super.addMessage(msg);
	}
	
	protected void setIdle(boolean idle) {
		super.setIdle(idle);
	}
	
	protected void setFactory(CFactory factory) {
		super.setFactory(factory);
	}
	
	protected void setParent(CProcessor parent) {
		super.setParent(parent);
	}
	
	protected void setIsSynchronized(boolean synchronization) {
		super.setIsSynchronized(synchronization);
	}
	
	protected void setInitiator(boolean initiator) {
		super.setInitiator(initiator);
	}
	

}
