package jason.stdlib;

import jason.JasonException;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvCFactory;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvMagentixAgArch;
import es.upv.dsic.gti_ia.jason.conversationsFactory.Conversation;
import es.upv.dsic.gti_ia.jason.conversationsFactory.JAucPartConversation;

import es.upv.dsic.gti_ia.jason.conversationsFactory.Protocol_Template;
import es.upv.dsic.gti_ia.jason.conversationsFactory.protocolInternalAction;
import es.upv.dsic.gti_ia.jason.conversationsFactory.participant.Jason_JAuc_Participant;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.MessageFilter;

/**
 * This class represents the internal action to be used when adding a conversation to 
 * a Jason agent under the Japanese Auction Protocol as participant
 * @author Bexy Alfonso Espinosa
 */

public class ia_JAuc_Participant extends protocolInternalAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * It's necessary to define a new protocol Internal Action
	 */
	Jason_JAuc_Participant jaucp;

	@Override public int getMinArgs() { return 2; };
	@Override public int getMaxArgs() { return 3; };

	@Override
	public void checkArguments(Term[] args) throws JasonException
	{
		super.checkArguments(args);
		boolean result = false;

		if (  (((Term)args[args.length-1]).isAtom())||
				(((Term)args[args.length-1]).isString())||
				(((Term)args[args.length-1]).isLiteral())||
				(((Term)args[args.length-1]).isNumeric())){result=true;}

		result = (result && (((Term)args[0]).isString()) );
		if ( protocolSteep.compareTo(Protocol_Template.JOIN_STEP)==0)
		{
			result = (result && (((Term)args[1]).isNumeric()) );
		}
	}


	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		agentConversationID = getTermAsString( args[args.length-1]);
		protocolSteep = getTermAsString(args[0]);
		ts.getAg().getLogger().fine("CALLING INTERNAL ACTION WITH STEEP: '"+protocolSteep+"'"+" CID: "+agentConversationID); 
		checkArguments(args);
		agName  = ts.getUserAgArch().getAgName();
		if (protocolSteep.compareTo(Protocol_Template.JOIN_STEP)==0){
			if (args.length >2)
			{					
				timeOut = getTermAsInt(args[1]);
			}
			if (jaucp == null){
				jaucp = new Jason_JAuc_Participant( ts);
			}
			ACLMessage msg = new ACLMessage();
			msg.setProtocol("japanese-auction");
			msg.setContent("Joining japanese-auction conversation "+agentConversationID);
			String myfactName = getFactoryName(agentConversationID,"JAUC",false);
			String inifactName = getFactoryName(agentConversationID,"JAUC",true);
			MessageFilter filter = new MessageFilter("protocol = japanese-auction AND purpose = startdata AND factoryname = "+inifactName);
			// The factory is setup to answer to incoming messages that
			// can start the participation of the agent in a new conversation
			ConvCFactory tmpFactory = jaucp.newFactory(myfactName, filter,msg,1, 
					((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent(),timeOut);
			((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().addFactoryAsParticipant(tmpFactory);
			CFactories.put(agentConversationID, tmpFactory);
		}
		else
			if (protocolSteep.compareTo(Protocol_Template.AGREE_STEP)==0){

				if (conversationsList.get(agentConversationID) == null)
				{	ConvCFactory tmpFactory = CFactories.get(agentConversationID);
				Conversation conv = tmpFactory.removeConversationByJasonID(agentConversationID);
				conversationsList.put(agentConversationID, conv);
				CFactories.remove(agentConversationID);
				}

				((JAucPartConversation)conversationsList.get(agentConversationID)).Accept = true;
				conversationsList.get(agentConversationID).release_semaphore();
			}
			else
				if (protocolSteep.compareTo(Protocol_Template.WITHDRAWAL_STEP)==0){
					if (conversationsList.get(agentConversationID) == null)
					{   ConvCFactory tmpFactory = CFactories.get(agentConversationID);
					Conversation conv = tmpFactory.removeConversationByJasonID(agentConversationID);
					conversationsList.put(agentConversationID, conv);
					CFactories.remove(agentConversationID);

					}
					((JAucPartConversation)conversationsList.get(agentConversationID)).Accept = false;
					conversationsList.get(agentConversationID).release_semaphore();
				}
		return true;
	}
}
