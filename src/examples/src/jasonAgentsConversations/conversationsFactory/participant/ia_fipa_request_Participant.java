package jasonAgentsConversations.conversationsFactory.participant;

import es.upv.dsic.gti_ia.core.AgentID;
import jason.JasonException;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import jasonAgentsConversations.agent.ConvMagentixAgArch;
import jasonAgentsConversations.agent.Protocol_Template;
import jasonAgentsConversations.agent.protocolInternalAction;

public class ia_fipa_request_Participant extends protocolInternalAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * It's necessary to define a new protocol Internal Action
	 */
	Jason_Fipa_Request_Participant frp;

	@Override public int getMinArgs() { return 2; };
	@Override public int getMaxArgs() { return 4; };

	@Override
	public void checkArguments(Term[] args) throws JasonException
	{
		super.checkArguments(args);
		boolean result = false;
		
		if (((Term)args[args.length-1]).isAtom()){result=true;}

		result = (result && (((Term)args[0]).isString()) );
		
		if ((protocolSteep.compareTo(Protocol_Template.JOIN_STEEP)==0)||
				(protocolSteep.compareTo(Protocol_Template.FAILURE_STEEP)==0))
		{
			result = (result && (((Term)args[1]).isNumeric()) );
		}

		if (protocolSteep.compareTo(Protocol_Template.INFORM_STEEP)==0){
			
			int cont = 0; result = true;
			for (Term t:args){
				switch (cont){
				case 1:result = (result&&t.isAtom());
				break;
				case 2:result = (result&&t.isNumeric());
				break;
				}
				cont++;
			}
		}

		if (!result)
		{
			throw JasonException.createWrongArgument(this,"Parameters must be in correct format.");
		}
	}


	
	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {

		protocolSteep = getTermAsString(args[0]);
		ts.getAg().getLogger().info("CALLING INTERNAL ACTION WITH STEEP: '"+protocolSteep+"'"); 
		
		checkArguments(args);

		agName  = ts.getUserAgArch().getAgName();

		agentConversationID = getAtomAsString(args[args.length-1]);
		
		if (protocolSteep.compareTo(Protocol_Template.JOIN_STEEP)==0){
			
			if (args.length >2)
			{					
				timeOut = getTermAsInt(args[1]);
			}
			
			AgentID tmpid = ((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().getAid();
			
			frp = new Jason_Fipa_Request_Participant(agName, agentConversationID , ts, tmpid, timeOut);

			// The agent creates the CFactory that manages every message which its
			// performative is set to CFP and protocol set to CONTRACTNET. In this
			// example the CFactory gets the name "TALK", we don't add any
			// additional message acceptance criterion other than the required
			// by the CONTRACTNET protocol (null) and we limit the number of simultaneous
			// processors to 1, i.e. the requests will be attended one after another.

			Protocol_Factory = frp.newFactory("Protocol_Factory", null,1, 
			((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent());
			

			// Finally the factory is setup to answer to incoming messages that
			// can start the participation of the agent in a new conversation
			
			((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().addFactoryAsParticipant(Protocol_Factory);
			
		}
		else
			if (protocolSteep.compareTo(Protocol_Template.AGREE_STEEP)==0){
				System.out.println("************************ "+agentConversationID);
				
				frp.RequestResult = Protocol_Template.AGREE_STEEP;
				frp.Protocol_Semaphore.release();

			}
			else
				if (protocolSteep.compareTo(Protocol_Template.REFUSE_STEEP)==0){

					frp.RequestResult = Protocol_Template.REFUSE_STEEP;					
					frp.Protocol_Semaphore.release();
				}
				else
					if (protocolSteep.compareTo(Protocol_Template.NOT_UNDERSTOOD_STEEP)==0){

						frp.RequestResult = Protocol_Template.NOT_UNDERSTOOD_STEEP;						
						frp.Protocol_Semaphore.release();
					}
					else
						if (protocolSteep.compareTo(Protocol_Template.INFORM_STEEP)==0){
							//getTermAsString(args[2]): has the task done
							frp.TaskResult = getTermAsString(args[2]);
							
							frp.TaskDesition = Protocol_Template.INFORM_STEEP;						
							frp.Protocol_Semaphore.release();
							
						}
						else
							if (protocolSteep.compareTo(Protocol_Template.FAILURE_STEEP)==0){
								//getTermAsString(args[2]): has the task done

								frp.TaskDesition = Protocol_Template.FAILURE_STEEP;						
								frp.Protocol_Semaphore.release();
							}

		return true;
	}



}
