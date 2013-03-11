package jason.stdlib;

//Internal action code for project CNP

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;
import es.upv.dsic.gti_ia.core.MessageFilter;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvCFactory;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvMagentixAgArch;
import es.upv.dsic.gti_ia.jason.conversationsFactory.Conversation;
import es.upv.dsic.gti_ia.jason.conversationsFactory.FICNConversation;
import es.upv.dsic.gti_ia.jason.conversationsFactory.Protocol_Template;
import es.upv.dsic.gti_ia.jason.conversationsFactory.protocolInternalAction;
import es.upv.dsic.gti_ia.jason.conversationsFactory.participant.Jason_FICN_Participant;

/**
 * This class represents the internal action to be used when adding a conversation to 
 * a Jason agent under the Fipa Iterated Contract Net Protocol as participant
 * @author Bexy Alfonso Espinosa
 */

public class ia_FICN_Participant extends protocolInternalAction {
	//this name of loadProtocol must be changed by another one like "InitiatorProtocolManager"


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Jason_FICN_Participant ficnp;

	//her there must be more factory fields

	@Override public int getMinArgs() { return 2; };
	@Override public int getMaxArgs() { return 5; };

	@Override
	public void checkArguments(Term[] args) throws JasonException{

		super.checkArguments(args);
		boolean result = false;

		if (((Term)args[args.length-1]).isAtom()){result=true;}

		result = (result && (((Term)args[0]).isString()) );

		if ((protocolSteep.compareTo(Protocol_Template.TASK_DONE_STEP)==0)||
				(protocolSteep.compareTo(Protocol_Template.TASK_NOT_DONE_STEP)==0))
		{
			result = (result && (((Term)args[1]).isString()) );
		}
		if (protocolSteep.compareTo(Protocol_Template.MAKE_PROPOSAL_STEP)==0)
		{
			result = (result && (((Term)args[1]).isNumeric()||((Term)args[1]).isNumeric()) );
		}		
		if (!result)
		{
			throw JasonException.createWrongArgument(this,"Parameters must be in correct format.");
		}
	}

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {

		agName  = ts.getUserAgArch().getAgName();

		Term protSteep = args[0];

		agentConversationID = ((Term)args[args.length-1]).toString();

		protocolSteep = ((StringTerm)protSteep).getString();

		checkArguments(args);

		ts.getAg().getLogger().fine("CALLING INTERNAL ACTION WITH STEEP: '"+protocolSteep+"'"+" CID: "+agentConversationID); 

		//the first state in the conversation

		if (protocolSteep.compareTo(Protocol_Template.JOIN_STEP)==0){
			//TODO: It is necessary to document that 80000 is the max time for the waiting states by default so 
			//in the Jason code of the participant this data is eliminated. Update examples in document. This value 
			//must be greater than the wait_for_proposals deadline.
			int timeOut = 80000;

			if (ficnp == null){
				ficnp = new Jason_FICN_Participant(agName, ts);
			}
			if (args.length >2)
			{					
				timeOut = (int)((NumberTerm)args[1]).solve();
			}

			String myfactName = getFactoryName(agentConversationID,"FICN",false);
			String inifactName = getFactoryName(agentConversationID,"FICN",true);
			MessageFilter filter = new MessageFilter("performative = CFP AND protocol = fipa-iterated-contract-net AND factoryname = "+inifactName);

			ConvCFactory tmpFactory = ficnp.newFactory(myfactName, filter,null,1, 
					((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent(),timeOut);

			((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().addFactoryAsParticipant(tmpFactory);
			CFactories.put(agentConversationID, tmpFactory);
		}
		else
			if (protocolSteep.compareTo(Protocol_Template.MAKE_PROPOSAL_STEP)==0){
				if (conversationsList.get(agentConversationID) == null)
				{	ConvCFactory tmpFactory = CFactories.get(agentConversationID);
				{Conversation conv = tmpFactory.removeConversationByJasonID(agentConversationID);
				conversationsList.put(agentConversationID, conv);}
				CFactories.remove(agentConversationID);
				}
				Term proposal = args[1];

				((FICNConversation)conversationsList.get(agentConversationID)).kindOfAnswer = "propose";
				((FICNConversation)conversationsList.get(agentConversationID)).proposal = getTermAsString(proposal);

				conversationsList.get(agentConversationID).release_semaphore();
			}
			else
				if (protocolSteep.compareTo(Protocol_Template.REFUSE_STEP)==0){
					if (conversationsList.get(agentConversationID) == null)
					{	ConvCFactory tmpFactory = CFactories.get(agentConversationID);
					{Conversation conv = tmpFactory.removeConversationByJasonID(agentConversationID);
					conversationsList.put(agentConversationID, conv);}
					CFactories.remove(agentConversationID);
					}

					((FICNConversation)conversationsList.get(agentConversationID)).kindOfAnswer = "refuse";
					conversationsList.get(agentConversationID).release_semaphore();

				}
				else
					if (protocolSteep.compareTo(Protocol_Template.NOT_UNDERSTOOD_STEP)==0){
						if (conversationsList.get(agentConversationID) == null)
						{	ConvCFactory tmpFactory = CFactories.get(agentConversationID);
						{ Conversation conv = tmpFactory.removeConversationByJasonID(agentConversationID);
						conversationsList.put(agentConversationID, conv);}
						CFactories.remove(agentConversationID);
						}

						((FICNConversation)conversationsList.get(agentConversationID)).kindOfAnswer = "notUnderstood";
						conversationsList.get(agentConversationID).release_semaphore();
					}
					else
						if (protocolSteep.compareTo(Protocol_Template.TASK_DONE_STEP)==0){

							Term finalInfo = args[1];

							((FICNConversation)conversationsList.get(agentConversationID)).infoToSend = ((StringTerm)finalInfo).getString();
							((FICNConversation)conversationsList.get(agentConversationID)).taskDone = true;

							conversationsList.get(agentConversationID).release_semaphore();

						}
						else
							if (protocolSteep.compareTo(Protocol_Template.TASK_NOT_DONE_STEP)==0){

								Term finalInfo = args[1];
								((FICNConversation)conversationsList.get(agentConversationID)).infoToSend = ((StringTerm)finalInfo).getString();
								((FICNConversation)conversationsList.get(agentConversationID)).taskDone = false;

								conversationsList.get(agentConversationID).release_semaphore();

							}

		// everything ok, so returns true
		return true;
	}
}
