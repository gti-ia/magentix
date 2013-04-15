package jasonAgentsConversations.conversationsFactory.participant;

import java.util.ArrayList;
import java.util.List;


import jason.JasonException;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.LiteralImpl;
import jason.asSyntax.Term;
import jasonAgentsConversations.agent.ConvMagentixAgArch;
import jasonAgentsConversations.agent.Protocol_Template;
import jasonAgentsConversations.agent.protocolInternalAction;
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
		
		if ( protocolSteep.compareTo(Protocol_Template.LOCATE_AGENTS_STEEP)==0)
		{
			result = (result && (((Term)args[1]).isList()) );
		}
		if ( protocolSteep.compareTo(Protocol_Template.INFORM_STEEP)==0)
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
		
		protocolSteep = getTermAsString(args[0]);
		ts.getAg().getLogger().info("CALLING INTERNAL ACTION WITH STEEP: ....... '"+protocolSteep+"'"); 
		
		checkArguments(args);
		
		agName  = ts.getUserAgArch().getAgName();

		if (protocolSteep.compareTo(Protocol_Template.JOIN_STEEP)==0){
			agentConversationID = getAtomAsString(args[1]);
			
			if (args.length >2)
			{					
				timeOut = getTermAsInt(args[2]);
			}
			
			AgentID tmpid = ((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().getAid();
			
			frcp = new Jason_Fipa_Recruiting_Participant(agName, agentConversationID , ts, tmpid, timeOut);

			// The agent creates the CFactory that manages every message which its
			// performative is set to CFP and protocol set to CONTRACTNET. In this
			// example the CFactory gets the name "TALK", we don't add any
			// additional message acceptance criterion other than the required
			// by the CONTRACTNET protocol (null) and we limit the number of simultaneous
			// processors to 1, i.e. the requests will be attended one after another.

			ACLMessage msg = new ACLMessage();
			msg.setProtocol("fipa-recruiting");
			msg.setContent("Joining fipa-recruiting conversation "+agentConversationID);
			
			Protocol_Factory = frcp.newFactory("Protocol_Factory", null, msg, 1, 
					((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent());
			
			// Finally the factory is setup to answer to incoming messages that
			// can start the participation of the agent in a new conversation
			
			((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().addFactoryAsParticipant(Protocol_Factory);
			
		}
		else
			if (protocolSteep.compareTo(Protocol_Template.AGREE_STEEP)==0){
				frcp.MsgProxyContent = (LiteralImpl)args[1];
				frcp.ProxyAcceptance = true;
				frcp.Protocol_Semaphore.release();
			}
			else
				if (protocolSteep.compareTo(Protocol_Template.REFUSE_STEEP)==0){
					frcp.MsgProxyContent = (LiteralImpl)args[1];
					frcp.ProxyAcceptance = false;
					frcp.Protocol_Semaphore.release();
				}
				else
					if (protocolSteep.compareTo(Protocol_Template.LOCATE_AGENTS_STEEP)==0){
						List<String> tagents = new ArrayList<String>();
						tagents = getTermAsStringList(args[1]);
						for (String ag:tagents){
							frcp.TargetAgents.add(new AgentID(ag));
						}
						frcp.Protocol_Semaphore.release();
					}
					else
						if (protocolSteep.compareTo(Protocol_Template.INFORM_STEEP)==0){
							//getTermAsString(args[2]): has the task done
							frcp.InfoToSend = getTermAsString(args[1]);
						
							frcp.Protocol_Semaphore.release();
							
						}


		return true;
		
	}
	
}
