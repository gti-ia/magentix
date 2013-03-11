package jason.stdlib;

import jason.JasonException;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import es.upv.dsic.gti_ia.core.MessageFilter;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvCFactory;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvMagentixAgArch;
import es.upv.dsic.gti_ia.jason.conversationsFactory.Conversation;
import es.upv.dsic.gti_ia.jason.conversationsFactory.FRConversation;
import es.upv.dsic.gti_ia.jason.conversationsFactory.Protocol_Template;
import es.upv.dsic.gti_ia.jason.conversationsFactory.protocolInternalAction;
import es.upv.dsic.gti_ia.jason.conversationsFactory.participant.Jason_Fipa_Request_Participant;

/**
 * This class represents the internal action to be used when adding a conversation to 
 * a Jason agent under the Fipa Request Protocol as participant
 * @author Bexy Alfonso Espinosa
 */

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

		if (  (((Term)args[args.length-1]).isAtom())||
				(((Term)args[args.length-1]).isString())||
				(((Term)args[args.length-1]).isLiteral())||
				(((Term)args[args.length-1]).isNumeric())){result=true;}

		result = (result && (((Term)args[0]).isString()) );

		if (protocolSteep.compareTo(Protocol_Template.JOIN_STEP)==0)
		{
			result = (result && ((Term)args[1]).isNumeric());
		}

		if (protocolSteep.compareTo(Protocol_Template.FAILURE_STEP)==0)
		{
			result = (result && ((Term)args[1]).isLiteral());
		}

		if (protocolSteep.compareTo(Protocol_Template.INFORM_STEP)==0){

			int cont = 0; result = true;
			for (Term t:args){
				switch (cont){
				case 1:result = (result&&t.isAtom());
				break;
				case 2:result = (result&& (t.isNumeric()||t.isLiteral())  );
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
		agentConversationID = getTermAsString(args[args.length-1]);
		if (((Term)args[args.length-1]).isString()){
			agentConversationID = "\""+agentConversationID+"\"";
		}
		if (ts.getSettings().verbose()>1)
			ts.getAg().getLogger().info("CALLING INTERNAL ACTION WITH STEEP: '"+protocolSteep+"'"+" CID: "+agentConversationID); 
		if (protocolSteep.compareTo(Protocol_Template.JOIN_STEP)==0){
			String myfactName = getFactoryName(agentConversationID,"FRQ",false);
			String inifactName = getFactoryName(agentConversationID,"FRQ",true);
			MessageFilter filter = new MessageFilter("performative = REQUEST AND protocol = fipa-request AND factoryname = "+inifactName);
			//The next would be better if cheking if the factory exists, and if not 
			//add the agent as participant
			//((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().removeFactory(factName);
			if (frp == null){
				frp = new Jason_Fipa_Request_Participant(ts);
			}
			if (args.length >2)
			{					
				timeOut = getTermAsInt(args[1]);
			}
			ConvCFactory tmpFactory = frp.newFactory(myfactName, filter,1, 
					((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent());

			// Finally the factory is setup to answer to incoming messages that
			// can start the participation of the agent in a new conversation
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
				((FRConversation)conversationsList.get(agentConversationID)).RequestResult = Protocol_Template.AGREE_STEP;
				conversationsList.get(agentConversationID).release_semaphore();
			}
			else
				if (protocolSteep.compareTo(Protocol_Template.REFUSE_STEP)==0){
					if (conversationsList.get(agentConversationID) == null)
					{	ConvCFactory tmpFactory = CFactories.get(agentConversationID);
					Conversation conv = tmpFactory.removeConversationByJasonID(agentConversationID);
					conversationsList.put(agentConversationID, conv);
					CFactories.remove(agentConversationID);
					}
					((FRConversation)conversationsList.get(agentConversationID)).RequestResult = Protocol_Template.REFUSE_STEP;	
					conversationsList.get(agentConversationID).release_semaphore();

				}
				else
					if (protocolSteep.compareTo(Protocol_Template.NOT_UNDERSTOOD_STEP)==0){
						if (conversationsList.get(agentConversationID) == null)
						{	ConvCFactory tmpFactory = CFactories.get(agentConversationID);
						Conversation conv = tmpFactory.removeConversationByJasonID(agentConversationID);
						conversationsList.put(agentConversationID, conv);
						CFactories.remove(agentConversationID);
						}
						((FRConversation)conversationsList.get(agentConversationID)).RequestResult = Protocol_Template.NOT_UNDERSTOOD_STEP;//Protocol_Template.NOT_UNDERSTOOD_STEP;
						conversationsList.get(agentConversationID).release_semaphore();
					}
					else
						if (protocolSteep.compareTo(Protocol_Template.INFORM_STEP)==0){
							((FRConversation)conversationsList.get(agentConversationID)).TaskResult = getTermAsString(args[2]);
							((FRConversation)conversationsList.get(agentConversationID)).TaskDecision = Protocol_Template.INFORM_STEP;						
							conversationsList.get(agentConversationID).release_semaphore();
						}
						else   
							if (protocolSteep.compareTo(Protocol_Template.FAILURE_STEP)==0){
								if ((FRConversation)conversationsList.get(agentConversationID)==null)
								{	ConvCFactory tmpFactory = CFactories.get(agentConversationID);
								Conversation conv = tmpFactory.removeConversationByJasonID(agentConversationID);
								conversationsList.put(agentConversationID, conv);
								CFactories.remove(agentConversationID);
								}
								((FRConversation)conversationsList.get(agentConversationID)).TaskDecision = Protocol_Template.FAILURE_STEP;						
								conversationsList.get(agentConversationID).release_semaphore();
							}

		return true;
	}



}
