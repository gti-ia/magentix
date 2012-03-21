package jason.stdlib;

import es.upv.dsic.gti_ia.core.ACLMessage;
import jason.JasonException;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvMagentixAgArch;
import es.upv.dsic.gti_ia.jason.conversationsFactory.Conversation;
import es.upv.dsic.gti_ia.jason.conversationsFactory.FQConversation;
import es.upv.dsic.gti_ia.jason.conversationsFactory.Protocol_Template;
import es.upv.dsic.gti_ia.jason.conversationsFactory.protocolInternalAction;

import es.upv.dsic.gti_ia.jason.conversationsFactory.participant.Jason_Fipa_Query_Participant;

public class ia_fipa_query_Participant extends protocolInternalAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*jasonAgentsConversations.nconversationsFactory.participant.ia_fipa_query_Participant("joinconversation",TO,ConvID).
	 * 		+query(Sender,Query,Kind,ConvID):not wantToAnswer(Sender,Protocol)
	 *jasonAgentsConversations.nconversationsFactory.participant.ia_fipa_query_Participant("refuse",ConvID)
	 *jasonAgentsConversations.nconversationsFactory.participant.ia_fipa_query_Participant("agree",Result,ConvID) 
	 *jasonAgentsConversations.nconversationsFactory.participant.ia_fipa_query_Participant("failure",ConvID) 
	 *jasonAgentsConversations.nconversationsFactory.participant.ia_fipa_query_Participant("notunderstood",ConvID) 
	 * 
	 * */
	
	
 	Jason_Fipa_Query_Participant fqp = null;


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
		
		if (protocolSteep.compareTo(Protocol_Template.JOIN_STEP)==0)
		{
			result = (result && (((Term)args[1]).isNumeric()) );
		}
		
		if (protocolSteep.compareTo(Protocol_Template.AGREE_STEP)==0)
		{
			result = (result && (((Term)args[1]).isLiteral()) );
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

		agentConversationID = getTermAsString(args[args.length-1]);
		if (ts.getSettings().verbose()>1)
			ts.getAg().getLogger().info("CALLING INTERNAL ACTION WITH STEEP: '"+protocolSteep+"'"+" CID: "+agentConversationID); 

		if (protocolSteep.compareTo(Protocol_Template.JOIN_STEP)==0){
			String factName = agentConversationID+"-FQFACTORY";
			((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().removeFactory(factName);
			if (fqp == null){
				if (args.length >2)
				{					
					timeOut = getTermAsInt(args[1]);
				}

				//AgentID tmpid = ((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().getAid();
				fqp = new Jason_Fipa_Query_Participant(agName, ts);

				// The agent creates the CFactory that manages every message which its
				// performative is set to CFP and protocol set to CONTRACTNET. In this
				// example the CFactory gets the name "TALK", we don't add any
				// additional message acceptance criterion other than the required
				// by the CONTRACTNET protocol (null) and we limit the number of simultaneous
				// processors to 1, i.e. the requests will be attended one after another.

				Protocol_Factory = fqp.newFactory(factName, null,1, 
						((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent());

				//Conversation conv = new Conversation(agentConversationID,""); //the internal id is unknown yet
				//((ConvCFactory)Protocol_Factory).cProcessorTemplate().setConversation(conv);

				// Finally the factory is setup to answer to incoming messages that
				// can start the participation of the agent in a new conversation

				((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().addFactoryAsParticipant(Protocol_Factory);
				//conversationsList.put(agentConversationID, conv);
				//int i = 0;
				//while (i>10000){i++;}

			}


			

		}
		else
			if (protocolSteep.compareTo(Protocol_Template.AGREE_STEP)==0){
				
				//Removing conversation from pending conversations list and adding it to the 
				//list of conversations of the participant agent
				Conversation conv = Protocol_Factory.removeConversationByJasonID(agentConversationID);
				conversationsList.put(agentConversationID, conv);
				
				FQConversation myConv = (FQConversation) conversationsList.get(agentConversationID);
				myConv.result=ACLMessage.getPerformative(ACLMessage.AGREE);
				myConv.evaluationResult = getTermAsString(args[1]);
				String res = getTermAsString(args[1]);
				if (res.compareTo("true")==0){
					myConv.evaluationResult = "1";
				}
				if (res.compareTo("false")==0){
					myConv.evaluationResult = "0";
				}
				
				conv.release_semaphore();
				//fqp = null;
			}
			else
				if (protocolSteep.compareTo(Protocol_Template.REFUSE_STEP)==0){
					
					Conversation conv = Protocol_Factory.removeConversationByJasonID(agentConversationID);
					conversationsList.put(agentConversationID, conv);
					FQConversation myConv = (FQConversation) conversationsList.get(agentConversationID);
					myConv.result=ACLMessage.getPerformative(ACLMessage.REFUSE);
					conversationsList.get(agentConversationID).release_semaphore();
					//fqp = null;
				}
				else
					if (protocolSteep.compareTo(Protocol_Template.NOT_UNDERSTOOD_STEP)==0){

						Conversation conv = Protocol_Factory.removeConversationByJasonID(agentConversationID);
						conversationsList.put(agentConversationID, conv);
						FQConversation myConv = (FQConversation) conversationsList.get(agentConversationID);
						myConv.result=ACLMessage.getPerformative(ACLMessage.NOT_UNDERSTOOD);
						conversationsList.get(agentConversationID).release_semaphore();
						//fqp = null;
					}
					else
						if (protocolSteep.compareTo(Protocol_Template.FAILURE_STEP)==0){
							Conversation conv = Protocol_Factory.removeConversationByJasonID(agentConversationID);
							conversationsList.put(agentConversationID, conv);
							FQConversation myConv = (FQConversation) conversationsList.get(agentConversationID);
							myConv.result=ACLMessage.getPerformative(ACLMessage.FAILURE);
							conversationsList.get(agentConversationID).release_semaphore();
							// fqp = null;
						}

		return true;
	}
}
