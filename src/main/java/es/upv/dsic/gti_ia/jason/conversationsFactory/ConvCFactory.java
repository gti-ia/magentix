package es.upv.dsic.gti_ia.jason.conversationsFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import es.upv.dsic.gti_ia.cAgents.CFactory;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.MessageFilter;

/**
 * This class represents a CFactory for being used in the Conversations Factory from Jason agents.
 * @author Bexy Alfonso Espinosa
 */

public class ConvCFactory extends CFactory{

	HashMap<String, Conversation> participantNoIdConv =  new HashMap<String, Conversation>(); //Bexy
	ConvCProcessor myConvCProcessor; //template
	ConvJasonAgent convAgent; 
	public boolean convinitiator = false;

	public ConvCFactory(String name, MessageFilter filter,
			int conversationsLimit, ConvJasonAgent myAgent) {
		super(name, filter, conversationsLimit, myAgent);
		myConvCProcessor = new ConvCProcessor(myAgent);
		convAgent = myAgent;
		setCProcessor(myConvCProcessor);
	}

	public ConvCProcessor cProcessorTemplate() {
		return this.myConvCProcessor;
	}

	public ConvCProcessor startConversation(ACLMessage msg, CProcessor parent,  //Bexy: public
			Boolean isSync) {
		ConvCProcessor cloneProcessor = (ConvCProcessor) myConvCProcessor.clone();
		if (!convinitiator){
			Conversation conv = new Conversation("",msg.getConversationId(),null, this.getName());
			cloneProcessor.setConversation(conv);
			participantNoIdConv.put(msg.getConversationId(), conv);			
		}

		cloneProcessor.setConversationID(msg.getConversationId());
		cloneProcessor.addMessage(msg);
		cloneProcessor.setIdle(false);
		cloneProcessor.setFactory(this);
		cloneProcessor.setParent(parent);
		cloneProcessor.setIsSynchronized(isSync);
		cloneProcessor.setInitiator(convinitiator);
		// setParentChildren(cloneProcessor); // ???

		convAgent.addProcessor(msg.getConversationId(), cloneProcessor);
		convAgent.exec.execute(cloneProcessor);
		return (cloneProcessor);
	}

	protected boolean templateIsEqual(ACLMessage template) {
		return super.templateIsEqual(template);
	}

	public Conversation removeConversationByJasonID(String jasonID){
		Collection<Conversation> c  = participantNoIdConv.values();
		Iterator<Conversation> itr = c.iterator();
		boolean found = false;
		Conversation result = null;
		while((found==false)&&(itr.hasNext())){
			Conversation conv = itr.next();
			if (conv.jasonConvID.compareTo(jasonID)==0){
				found = true;
				result = participantNoIdConv.remove(conv.internalConvID);
			}
		}
		return result;
	}

	public Conversation removeConversationByInternalID(String internalID){
		return participantNoIdConv.remove(internalID);
	}

	public void UpdateConv(Conversation newConv, ConvCProcessor proc){
		Conversation tmpConv = null;
		tmpConv = removeConversationByInternalID(newConv.internalConvID);

		if (tmpConv!=null){
			participantNoIdConv.put(newConv.internalConvID, newConv);
		}
		proc.setConversation(newConv);
	}
}
