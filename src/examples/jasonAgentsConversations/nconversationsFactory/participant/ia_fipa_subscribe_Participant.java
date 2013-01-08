package jasonAgentsConversations.nconversationsFactory.participant;

import es.upv.dsic.gti_ia.core.ACLMessage;
import jasonAgentsConversations.agentNConv.ConvJasonAgent;
import jasonAgentsConversations.agentNConv.ConvMagentixAgArch;
import jasonAgentsConversations.agentNConv.Conversation;
import jasonAgentsConversations.agentNConv.FSConversation;
import jasonAgentsConversations.agentNConv.Protocol_Template;
import jasonAgentsConversations.agentNConv.protocolInternalAction;

import jason.JasonException;
import jason.asSyntax.Term;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;

public class ia_fipa_subscribe_Participant extends protocolInternalAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

 	Jason_Fipa_Subscribe_Participant fsp = null;


	@Override public int getMinArgs() { return 2; };
	@Override public int getMaxArgs() { return 4; };

	@Override
	public void checkArguments(Term[] args) throws JasonException
	{
		super.checkArguments(args);
		boolean result = false;

		if ((((Term)args[args.length-1]).isAtom())||
				(((Term)args[args.length-1]).isString())||
				(((Term)args[args.length-1]).isNumeric())){result=true;}

		result = (result && (((Term)args[0]).isString()) );

		if (protocolSteep.compareTo(Protocol_Template.INFORM_STEP)==0)
		{
			int cont = 0; 
			for (Term t:args){
				switch (cont){
				case 1:result = (result&&t.isLiteral()||result&&t.isString());
				break;
				case 2:result = (result&&t.isLiteral());
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
		
		ConvJasonAgent myag = ((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent();
		
		ts.getAg().getLogger().info("CALLING INTERNAL ACTION WITH STEEP: '"+protocolSteep+"'"+" CID: "+agentConversationID); 
		
		if (protocolSteep.compareTo(Protocol_Template.JOIN_STEP)==0){
			
			if (fsp == null){
				if (args.length >2)
				{					
					timeOut = getTermAsInt(args[1]);
				}

				//AgentID tmpid = ((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().getAid();
				fsp = new Jason_Fipa_Subscribe_Participant(agName, ts);

				// The agent creates the CFactory that manages every message which its
				// performative is set to CFP and protocol set to CONTRACTNET. In this
				// example the CFactory gets the name "TALK", we don't add any
				// additional message acceptance criterion other than the required
				// by the CONTRACTNET protocol (null) and we limit the number of simultaneous
				// processors to 1, i.e. the requests will be attended one after another.
				
				Protocol_Factory = fsp.newFactory("Protocol_Factory", null,1, 
						((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent());

				// Finally the factory is setup to answer to incoming messages that
				// can start the participation of the agent in a new conversation
				
				((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().addFactoryAsParticipant(Protocol_Factory);
			
			}
			
		}
		else
			if (protocolSteep.compareTo(Protocol_Template.AGREE_STEP)==0){
				
				//Removing conversation from pending conversations list and adding it to the 
				//list of conversations of the participant agent
				Conversation conv = Protocol_Factory.removeConversationByJasonID(agentConversationID);
				conversationsList.put(agentConversationID, conv);
				
				FSConversation myConv = (FSConversation) conversationsList.get(agentConversationID);
				myConv.firstResult =ACLMessage.getPerformative(ACLMessage.AGREE);
				
				conv.release_semaphore();

			}
			else
				if (protocolSteep.compareTo(Protocol_Template.REFUSE_STEP)==0){
					
					Conversation conv = Protocol_Factory.removeConversationByJasonID(agentConversationID);
					conversationsList.put(agentConversationID, conv);
					
					FSConversation myConv = (FSConversation) conversationsList.get(agentConversationID);
					myConv.firstResult =ACLMessage.getPerformative(ACLMessage.REFUSE);
					
					myConv.release_semaphore();
				}
		
				else
					if (protocolSteep.compareTo(Protocol_Template.INFORM_STEP)==0){
						String key = getTermAsString(args[1]);
						String value = getTermAsString(args[2]);
						ACLMessage informmsg = new ACLMessage();
						informmsg.setSender(myag.getAid());
						informmsg.setReceiver(myag.getAid());
						informmsg.setProtocol("fipa-subscribe");
						informmsg.setPerformative(ACLMessage.INFORM);
						informmsg.setContent("A change has been produced.");
						informmsg.setConversationId(((FSConversation)conversationsList.get(agentConversationID)).internalConvID);
						informmsg.setHeader(key, value);
						myag.send(informmsg);
					}
		
				else
					if (protocolSteep.compareTo(Protocol_Template.FAILURE_STEP)==0){

						//Conversation conv = Protocol_Factory.removeConversationByJasonID(agentConversationID);
						ACLMessage failmsg = new ACLMessage();
						failmsg.setSender(myag.getAid());
						failmsg.setReceiver(myag.getAid());
						failmsg.setProtocol("fipa-subscribe");
						failmsg.setPerformative(ACLMessage.FAILURE);
						failmsg.setContent("Conversation failed.");
						failmsg.setConversationId(((FSConversation)conversationsList.get(agentConversationID)).internalConvID);
						//Message for getting out of the WAIT_FOR_CANCEL state
						myag.send(failmsg);
					}
					else
						if (protocolSteep.compareTo(Protocol_Template.FAILURE_CANCEL_STEP)==0){
							FSConversation conv = (FSConversation) conversationsList.get(agentConversationID);
							conv.finalResult =ACLMessage.getPerformative(ACLMessage.FAILURE)+"_CANCEL";
							conversationsList.get(agentConversationID).release_semaphore();
						}
						else
							if (protocolSteep.compareTo(Protocol_Template.INFORM_CANCEL_STEP)==0){
								FSConversation conv = (FSConversation) conversationsList.get(agentConversationID);
								conv.finalResult =ACLMessage.getPerformative(ACLMessage.INFORM)+"_CANCEL";
								conv.conversationCanceled = true;
								conversationsList.get(agentConversationID).release_semaphore();
							}

		return true;
	}
}
