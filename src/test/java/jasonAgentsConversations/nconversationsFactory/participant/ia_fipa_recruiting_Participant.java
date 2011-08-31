package jasonAgentsConversations.nconversationsFactory.participant;

import java.util.ArrayList;
import java.util.List;


import jason.JasonException;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.LiteralImpl;
import jason.asSyntax.Term;
import jasonAgentsConversations.agentNConv.ConvMagentixAgArch;
import jasonAgentsConversations.agentNConv.Conversation;
import jasonAgentsConversations.agentNConv.FRCConversation;
import jasonAgentsConversations.agentNConv.Protocol_Template;
import jasonAgentsConversations.agentNConv.protocolInternalAction;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;


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

		if (((Term)args[args.length-1]).isAtom()){result=true;}

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
		
		agentConversationID = getAtomAsString( args[args.length-1]);
		protocolSteep = getTermAsString(args[0]);
		ts.getAg().getLogger().info("CALLING INTERNAL ACTION WITH STEEP: '"+protocolSteep+"'"+" CID: "+agentConversationID); 

		checkArguments(args);

		agName  = ts.getUserAgArch().getAgName();

		if (protocolSteep.compareTo(Protocol_Template.JOIN_STEP)==0){

			

			if (args.length >2)
			{					
				timeOut = getTermAsInt(args[2]);
			}

			//AgentID tmpid =  ((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().getAid();

			if (frcp == null){

				frcp = new Jason_Fipa_Recruiting_Participant( ts);

				ACLMessage msg = new ACLMessage();
				msg.setProtocol("fipa-recruiting");
				msg.setContent("Joining fipa-recruiting conversation "+agentConversationID);
				// The agent creates the CFactory that manages every message which its
				// performative is set to CFP and protocol set to CONTRACTNET. In this
				// example the CFactory gets the name "TALK", we don't add any
				// additional message acceptance criterion other than the required
				// by the CONTRACTNET protocol (null) and we limit the number of simultaneous
				// processors to 1, i.e. the requests will be attended one after another.

				Protocol_Factory = frcp.newFactory("Protocol_Factory", null,msg,1, 
						((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent());

				// Finally the factory is setup to answer to incoming messages that
				// can start the participation of the agent in a new conversation

				((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().addFactoryAsParticipant(Protocol_Factory);

				//This helps participant to finish joining in Magentix conversation thread.
				int i = 0;
				while (i>900000000){i++;}
			}

		}
		else
			if (protocolSteep.compareTo(Protocol_Template.AGREE_STEP)==0){
				
				Conversation conv = Protocol_Factory.removeConversationByJasonID(agentConversationID);

				conversationsList.put(agentConversationID, conv);
				
				((FRCConversation)conversationsList.get(agentConversationID)).MsgProxyContent = (LiteralImpl)args[1];
				((FRCConversation)conversationsList.get(agentConversationID)).ProxyAcceptance = true;
				
				conversationsList.get(agentConversationID).release_semaphore();
				
			}
			else
				if (protocolSteep.compareTo(Protocol_Template.REFUSE_STEP)==0){
					Conversation conv = Protocol_Factory.removeConversationByJasonID(agentConversationID);
					conversationsList.put(agentConversationID, conv);

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
