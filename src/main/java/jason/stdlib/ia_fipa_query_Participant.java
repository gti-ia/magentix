package jason.stdlib;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.MessageFilter;
import jason.JasonException;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvCFactory;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvMagentixAgArch;
import es.upv.dsic.gti_ia.jason.conversationsFactory.Conversation;
import es.upv.dsic.gti_ia.jason.conversationsFactory.FQConversation;
import es.upv.dsic.gti_ia.jason.conversationsFactory.Protocol_Template;
import es.upv.dsic.gti_ia.jason.conversationsFactory.protocolInternalAction;

import es.upv.dsic.gti_ia.jason.conversationsFactory.participant.Jason_Fipa_Query_Participant;

/**
 * This class represents the internal action to be used when adding a conversation to 
 * a Jason agent under the Fipa Query If/Ref Protocol as participant
 * @author Bexy Alfonso Espinosa
 */

public class ia_fipa_query_Participant extends protocolInternalAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
			//	String factName = agentConversationID+"-FQFACTORY";
			//	((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().removeFactory(factName);
			if (fqp == null){
				fqp = new Jason_Fipa_Query_Participant(agName, ts);
			}	

			if (args.length >2)
			{					
				timeOut = getTermAsInt(args[1]);
			}
			String myfactName = getFactoryName(agentConversationID,"FQ",false);
			String inifactName = getFactoryName(agentConversationID,"FQ",true);
			MessageFilter filter = new MessageFilter("protocol = fipa-query AND factoryname = "+inifactName);
			ConvCFactory tmpFactory = fqp.newFactory(myfactName, filter,1, 
					((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent());
			((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().addFactoryAsParticipant(tmpFactory);
			CFactories.put(agentConversationID, tmpFactory);
		}
		else
			if (protocolSteep.compareTo(Protocol_Template.AGREE_STEP)==0){

				//Removing conversation from pending conversations list and adding it to the 
				//list of conversations of the participant agent
				ConvCFactory tmpFactory = CFactories.get(agentConversationID);
				Conversation conv = tmpFactory.removeConversationByJasonID(agentConversationID);
				conversationsList.put(agentConversationID, conv);
				CFactories.remove(agentConversationID);
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
			}
			else
				if (protocolSteep.compareTo(Protocol_Template.REFUSE_STEP)==0){
					ConvCFactory tmpFactory = CFactories.get(agentConversationID);
					Conversation conv = tmpFactory.removeConversationByJasonID(agentConversationID);
					conversationsList.put(agentConversationID, conv);
					CFactories.remove(agentConversationID);

					FQConversation myConv = (FQConversation) conversationsList.get(agentConversationID);
					myConv.result=ACLMessage.getPerformative(ACLMessage.REFUSE);
					conversationsList.get(agentConversationID).release_semaphore();
				}
				else
					if (protocolSteep.compareTo(Protocol_Template.NOT_UNDERSTOOD_STEP)==0){
						ConvCFactory tmpFactory = CFactories.get(agentConversationID);
						Conversation conv = tmpFactory.removeConversationByJasonID(agentConversationID);
						conversationsList.put(agentConversationID, conv);
						CFactories.remove(agentConversationID);

						FQConversation myConv = (FQConversation) conversationsList.get(agentConversationID);
						myConv.result=ACLMessage.getPerformative(ACLMessage.NOT_UNDERSTOOD);
						conversationsList.get(agentConversationID).release_semaphore();
					}
					else
						if (protocolSteep.compareTo(Protocol_Template.FAILURE_STEP)==0){
							ConvCFactory tmpFactory = CFactories.get(agentConversationID);
							Conversation conv = tmpFactory.removeConversationByJasonID(agentConversationID);
							conversationsList.put(agentConversationID, conv);
							CFactories.remove(agentConversationID);

							FQConversation myConv = (FQConversation) conversationsList.get(agentConversationID);
							myConv.result=ACLMessage.getPerformative(ACLMessage.FAILURE);
							conversationsList.get(agentConversationID).release_semaphore();
						}

		return true;
	}
}
