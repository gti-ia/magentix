package jason.stdlib;
import java.util.ArrayList;
import java.util.List;

import jason.JasonException;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.LiteralImpl;
import jason.asSyntax.Term;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvCFactory;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvMagentixAgArch;
import es.upv.dsic.gti_ia.jason.conversationsFactory.Conversation;
import es.upv.dsic.gti_ia.jason.conversationsFactory.FRCConversation;
import es.upv.dsic.gti_ia.jason.conversationsFactory.Protocol_Template;
import es.upv.dsic.gti_ia.jason.conversationsFactory.protocolInternalAction;
import es.upv.dsic.gti_ia.jason.conversationsFactory.participant.Jason_Fipa_Recruiting_Participant;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.MessageFilter;

/**
 * This class represents the internal action to be used when adding a conversation to 
 * a Jason agent under the Fipa Recruiting Protocol as participant
 * @author Bexy Alfonso Espinosa
 */

public class ia_fipa_recruiting_Participant extends protocolInternalAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * It's necessary to define a new protocol Internal Action
	 */
	Jason_Fipa_Recruiting_Participant frcp;

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

		if ( protocolSteep.compareTo(Protocol_Template.LOCATE_AGENTS_STEP)==0)
		{
			result = (result && (((Term)args[1]).isList()) );
		}
		if ( protocolSteep.compareTo(Protocol_Template.INFORM_STEP)==0)
		{
			result = (result && (((Term)args[1]).isString()) );
		}

		if (!result)
		{
			throw JasonException.createWrongArgument(this,"Parameters must be in correct format.");
		}
	}


	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		agentConversationID = getTermAsString( args[args.length-1]);
		protocolSteep = getTermAsString(args[0]);
		if (ts.getSettings().verbose()>1)
			ts.getAg().getLogger().info("CALLING INTERNAL ACTION WITH STEEP: '"+protocolSteep+"'"+" CID: "+agentConversationID); 
		checkArguments(args);
		agName  = ts.getUserAgArch().getAgName();
		if (protocolSteep.compareTo(Protocol_Template.JOIN_STEP)==0){

			if (args.length >2)
			{					
				timeOut = getTermAsInt(args[2]);
			}
			if (frcp == null){

				frcp = new Jason_Fipa_Recruiting_Participant( ts);
			}
			ACLMessage msg = new ACLMessage();
			msg.setProtocol("fipa-recruiting");
			msg.setContent("Joining fipa-recruiting conversation "+agentConversationID);
			String myfactName = getFactoryName(agentConversationID,"FRC",false);
			String inifactName = getFactoryName(agentConversationID,"FRC",true);
			MessageFilter filter = new MessageFilter("protocol = fipa-recruiting AND factoryname = "+inifactName);
			ConvCFactory tmpFactory = frcp.newFactory(myfactName, filter,msg,1, 
					((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent());
			// Finally the factory is setup to answer to incoming messages that
			// can start the participation of the agent in a new conversation
			((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().addFactoryAsParticipant(tmpFactory);
			CFactories.put(agentConversationID, tmpFactory);
		}
		else
			if (protocolSteep.compareTo(Protocol_Template.AGREE_STEP)==0){
				ConvCFactory tmpFactory = CFactories.get(agentConversationID);
				Conversation conv = tmpFactory.removeConversationByJasonID(agentConversationID);
				conversationsList.put(agentConversationID, conv);
				CFactories.remove(agentConversationID);

				((FRCConversation)conversationsList.get(agentConversationID)).MsgProxyContent = (LiteralImpl)args[1];
				((FRCConversation)conversationsList.get(agentConversationID)).ProxyAcceptance = true;

				conversationsList.get(agentConversationID).release_semaphore();

			}
			else
				if (protocolSteep.compareTo(Protocol_Template.REFUSE_STEP)==0){
					ConvCFactory tmpFactory = CFactories.get(agentConversationID);
					Conversation conv = tmpFactory.removeConversationByJasonID(agentConversationID);
					conversationsList.put(agentConversationID, conv);
					CFactories.remove(agentConversationID);

					((FRCConversation)conversationsList.get(agentConversationID)).MsgProxyContent = (LiteralImpl)args[1];
					((FRCConversation)conversationsList.get(agentConversationID)).ProxyAcceptance = false;

					conversationsList.get(agentConversationID).release_semaphore();
				}
				else
					if (protocolSteep.compareTo(Protocol_Template.LOCATE_AGENTS_STEP)==0){

						List<String> tagents = new ArrayList<String>();
						tagents = getTermAsStringList(args[1]);
						for (String ag:tagents){
							((FRCConversation)conversationsList.get(agentConversationID)).TargetAgents.add(new AgentID(ag));
						}

						conversationsList.get(agentConversationID).release_semaphore();
					}
					else
						if (protocolSteep.compareTo(Protocol_Template.INFORM_STEP)==0){
							//getTermAsString(args[2]): has the task done
							((FRCConversation)conversationsList.get(agentConversationID)).InfoToSend = getTermAsString(args[1]);

							conversationsList.get(agentConversationID).release_semaphore();

						}


		return true;

	}

}
