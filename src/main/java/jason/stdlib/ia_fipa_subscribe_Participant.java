package jason.stdlib;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.MessageFilter;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvCFactory;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvJasonAgent;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvMagentixAgArch;
import es.upv.dsic.gti_ia.jason.conversationsFactory.Conversation;
import es.upv.dsic.gti_ia.jason.conversationsFactory.FSConversation;
import es.upv.dsic.gti_ia.jason.conversationsFactory.Protocol_Template;
import es.upv.dsic.gti_ia.jason.conversationsFactory.protocolInternalAction;
import es.upv.dsic.gti_ia.jason.conversationsFactory.participant.Jason_Fipa_Subscribe_Participant;

import jason.JasonException;
import jason.asSyntax.Term;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;

/**
 * This class represents the internal action to be used when adding a conversation to 
 * a Jason agent under the Fipa Subscribe Protocol as participant
 * @author Bexy Alfonso Espinosa
 */

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

		if (  (((Term)args[args.length-1]).isAtom())||
				(((Term)args[args.length-1]).isString())||
				(((Term)args[args.length-1]).isLiteral())||
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
		agentConversationID = getTermAsString(args[args.length-1]);
		if (((Term)args[args.length-1]).isString()){
			agentConversationID = "\""+agentConversationID+"\"";
		}
		ConvJasonAgent myag = ((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent();

		if (ts.getSettings().verbose()>1)
			ts.getAg().getLogger().info("CALLING INTERNAL ACTION WITH STEEP: '"+protocolSteep+"'"+" CID: "+agentConversationID); 
		if (protocolSteep.compareTo(Protocol_Template.JOIN_STEP)==0){
			if (fsp == null){
				fsp = new Jason_Fipa_Subscribe_Participant(agName, ts);
			}
			if (args.length >2)
			{					
				timeOut = getTermAsInt(args[1]);
			}
			String myfactName = getFactoryName(agentConversationID,"FS",false);
			String inifactName = getFactoryName(agentConversationID,"FS",true);
			MessageFilter filter = new MessageFilter("protocol = fipa-subscribe AND factoryname = "+inifactName);
			// The factory is setup to answer to incoming messages that
			// can start the participation of the agent in a new conversation
			ConvCFactory tmpFactory  = fsp.newFactory(myfactName, filter,1, 
					((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent());

			// Finally the factory is setup to answer to incoming messages that
			// can start the participation of the agent in a new conversation
			((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().addFactoryAsParticipant(tmpFactory);
			CFactories.put(agentConversationID, tmpFactory);
		}
		else
			if (protocolSteep.compareTo(Protocol_Template.AGREE_STEP)==0){
				ConvCFactory tmpFactory = CFactories.get(agentConversationID);
				//Removing conversation from pending conversations list and adding it to the 
				//list of conversations of the participant agent
				Conversation conv = tmpFactory.removeConversationByJasonID(agentConversationID);
				conversationsList.put(agentConversationID, conv);
				CFactories.put(agentConversationID, tmpFactory);
				FSConversation myConv = (FSConversation) conversationsList.get(agentConversationID);
				myConv.firstResult =ACLMessage.getPerformative(ACLMessage.AGREE);
				conv.release_semaphore();
			}
			else
				if (protocolSteep.compareTo(Protocol_Template.REFUSE_STEP)==0){
					ConvCFactory tmpFactory = CFactories.get(agentConversationID);
					Conversation conv = tmpFactory.removeConversationByJasonID(agentConversationID);
					conversationsList.put(agentConversationID, conv);
					CFactories.put(agentConversationID, tmpFactory);
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
