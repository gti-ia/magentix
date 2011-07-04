package jasonAgentsConversations.agentNConv;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CFactory;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.MessageFilter;

public class ConvCFactory extends CFactory{

	//HashMap<String, Conversation> jasonIDconversationsList =  new HashMap<String, Conversation>(); //Bexy
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
		//System.out.println(convAgent.getName()+" PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP: INITIATOR EN CONVCFACTORY -> "+convinitiator);
		ConvCProcessor cloneProcessor = (ConvCProcessor) myConvCProcessor.clone();
		if (!convinitiator){
		Conversation conv = new Conversation("",msg.getConversationId());
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
	
	/*public void insertConversation(Conversation conv){
		jasonIDconversationsList.put( conv.jasonConvID , conv);
		internalIDconversationsList.put( conv.internalConvID , conv);
	}
	
	public void insertJasonIDConversation(Conversation conv, String jasonID){
		jasonIDconversationsList.put( jasonID , conv);
	}
	
	public void insertintIDConversation(Conversation conv, String internalID){
		internalIDconversationsList.put( internalID , conv);
	}
	
	
	public void removeConversation(Conversation conv){
		jasonIDconversationsList.remove(conv.jasonConvID);
		internalIDconversationsList.remove(conv.internalConvID);
	}
	
	public Conversation getConversationByJasonID(String jasonID){
		return jasonIDconversationsList.get(jasonID);
	}
	
	public Conversation getConversationByintID(String internalID){
		return internalIDconversationsList.get(internalID);
		
	}*/

}
