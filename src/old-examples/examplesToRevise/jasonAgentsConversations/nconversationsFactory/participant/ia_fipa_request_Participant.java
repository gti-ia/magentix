package jasonAgentsConversations.nconversationsFactory.participant;

import es.upv.dsic.gti_ia.core.AgentID;
import jason.JasonException;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import jasonAgentsConversations.agentNConv.ConvMagentixAgArch;
import jasonAgentsConversations.agentNConv.Conversation;
import jasonAgentsConversations.agentNConv.Protocol_Template;
import jasonAgentsConversations.agentNConv.protocolInternalAction;

public class ia_fipa_request_Participant extends protocolInternalAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * It's necessary to define a new protocol Internal Action
	 */
	Jason_Fipa_Request_Participant frp = null;


	@Override public int getMinArgs() { return 2; };
	@Override public int getMaxArgs() { return 4; };

	@Override
	public void checkArguments(Term[] args) throws JasonException
	{
		super.checkArguments(args);
		boolean result = false;
		
		if (((Term)args[args.length-1]).isAtom()){result=true;}

		result = (result && (((Term)args[0]).isString()) );
		
		if ((protocolSteep.compareTo(Protocol_Template.JOIN_STEP)==0)||
				(protocolSteep.compareTo(Protocol_Template.FAILURE_STEP)==0))
		{
			result = (result && (((Term)args[1]).isNumeric()) );
		}

		if (protocolSteep.compareTo(Protocol_Template.INFORM_STEP)==0){
			
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
				
		checkArguments(args);

		agName  = ts.getUserAgArch().getAgName();

		agentConversationID = getAtomAsString(args[args.length-1]);
		
		//ConvJasonAgent myag = ((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent();
		
		ts.getAg().getLogger().info("CALLING INTERNAL ACTION WITH STEEP: '"+protocolSteep+"'"+" CID: "+agentConversationID); 
		
		if (protocolSteep.compareTo(Protocol_Template.JOIN_STEP)==0){
			
			if (frp == null){
				if (args.length >2)
				{					
					timeOut = getTermAsInt(args[1]);
				}

				AgentID tmpid = ((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().getAid();
				frp = new Jason_Fipa_Request_Participant(agName, ts, tmpid, timeOut);

				// The agent creates the CFactory that manages every message which its
				// performative is set to CFP and protocol set to CONTRACTNET. In this
				// example the CFactory gets the name "TALK", we don't add any
				// additional message acceptance criterion other than the required
				// by the CONTRACTNET protocol (null) and we limit the number of simultaneous
				// processors to 1, i.e. the requests will be attended one after another.

				Protocol_Factory = frp.newFactory("Protocol_Factory", null,1, 
						((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent());

				//Conversation conv = new Conversation(agentConversationID,""); //the internal id is unknown yet
				//((ConvCFactory)Protocol_Factory).cProcessorTemplate().setConversation(conv);

				
				
				// Finally the factory is setup to answer to incoming messages that
				// can start the participation of the agent in a new conversation

				((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().addFactoryAsParticipant(Protocol_Factory);
				//conversationsList.put(agentConversationID, conv);
				int i = 0;
				while (i>10000){i++;}
				//ts.getLogger().info("Factoria añadida "+agentConversationID);
				
			}
			
		
			

		}
		else
			if (protocolSteep.compareTo(Protocol_Template.AGREE_STEP)==0){
				
				frp.RequestResult = Protocol_Template.AGREE_STEP;
				//frp.Protocol_Semaphore.release();
				//myag.getConversationByJasonID(agentConversationID).release_semaphore();

				//Removing conversation from pending conversations list and adding it to the 
				//list of conversations of the participant agent
				Conversation conv = Protocol_Factory.removeConversationByJasonID(agentConversationID);
				conversationsList.put(agentConversationID, conv);
				
				conv.release_semaphore();

			}
			else
				if (protocolSteep.compareTo(Protocol_Template.REFUSE_STEP)==0){

					frp.RequestResult = Protocol_Template.REFUSE_STEP;					
					//frp.Protocol_Semaphore.release();
					//myag.getConversationByJasonID(agentConversationID).release_semaphore();
					
					Conversation conv = Protocol_Factory.removeConversationByJasonID(agentConversationID);
					conversationsList.put(agentConversationID, conv);
					
					conversationsList.get(agentConversationID).release_semaphore();
				}
				else
					if (protocolSteep.compareTo(Protocol_Template.NOT_UNDERSTOOD_STEP)==0){

						frp.RequestResult = Protocol_Template.NOT_UNDERSTOOD_STEP;						
						//frp.Protocol_Semaphore.release();
						//myag.getConversationByJasonID(agentConversationID).release_semaphore();

						Conversation conv = Protocol_Factory.removeConversationByJasonID(agentConversationID);
						conversationsList.put(agentConversationID, conv);
						
						conversationsList.get(agentConversationID).release_semaphore();
					}
					else
						if (protocolSteep.compareTo(Protocol_Template.INFORM_STEP)==0){
							//getTermAsString(args[2]): has the task done
							frp.TaskResult = getTermAsString(args[2]);
							
							frp.TaskDesition = Protocol_Template.INFORM_STEP;						
							//frp.Protocol_Semaphore.release();
							//myag.getConversationByJasonID(agentConversationID).release_semaphore();
							
							conversationsList.get(agentConversationID).release_semaphore();
							
						}
						else
							if (protocolSteep.compareTo(Protocol_Template.FAILURE_STEP)==0){
								//getTermAsString(args[2]): has the task done

								frp.TaskDesition = Protocol_Template.FAILURE_STEP;						
								//frp.Protocol_Semaphore.release();
								//myag.getConversationByJasonID(agentConversationID).release_semaphore();
								
								conversationsList.get(agentConversationID).release_semaphore();
							}

		return true;
	}



}
