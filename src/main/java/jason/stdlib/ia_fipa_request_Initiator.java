package jason.stdlib;

import jason.JasonException;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvCFactory;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvCProcessor;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvJasonAgent;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvMagentixAgArch;
import es.upv.dsic.gti_ia.jason.conversationsFactory.FRConversation;
import es.upv.dsic.gti_ia.jason.conversationsFactory.Protocol_Template;
import es.upv.dsic.gti_ia.jason.conversationsFactory.protocolInternalAction;
import es.upv.dsic.gti_ia.jason.conversationsFactory.initiator.Jason_Fipa_Request_Initiator;

import es.upv.dsic.gti_ia.core.ACLMessage;

/**
 * This class represents the internal action to be used when adding a conversation to 
 * a Jason agent under the Fipa Request Protocol as initiator
 * @author Bexy Alfonso Espinosa
 */

public class ia_fipa_request_Initiator extends protocolInternalAction {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	Jason_Fipa_Request_Initiator fri = null;

	@Override public int getMinArgs() { return 2; };
	@Override public int getMaxArgs() { return 6; };

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

		if ( protocolSteep.compareTo(Protocol_Template.START_STEP)==0)
		{
			int cont = 0; 
			for (Term t:args){
				switch (cont){
				case 1:result = (result&&t.isAtom());	
				break;
				case 2:result = (result&&t.isNumeric());
				break;
				case 3:result = (result&&t.isString());
				break;
				}
				cont++;
			}

		}
		/*if (protocolSteep.compareTo(Protocol_Template.TASK_DONE_STEP)==0)
		{
			result = (result && (((Term)args[1]).isAtom()) );
		}*/

		if (protocolSteep.compareTo(Protocol_Template.REQUEST_STEP)==0)
		{
			int cont = 0; 
			for (Term t:args){
				switch (cont){
				case 1:result = ((result&&t.isLiteral())||(result&&t.isAtom())||(result&&t.isString())||(result&&t.isNumeric()));	
				break;
				case 2:result = (result&&t.isAtom());
				break;
				case 3:result = (result&&t.isLiteral());
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

		agentConversationID = getTermAsString(args[args.length-1]);
		if (((Term)args[args.length-1]).isString()){
			agentConversationID = "\""+agentConversationID+"\"";
		}
		agName  = ts.getUserAgArch().getAgName();

		ConvJasonAgent myag = ((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent();
		if (ts.getSettings().verbose()>1)
			ts.getAg().getLogger().info("CALLING INTERNAL ACTION WITH STEEP: '"+protocolSteep+"'"+" CID: "+agentConversationID);

		/*
		 * When a FR Protocol is taking place*/
		if (protocolSteep.compareTo(Protocol_Template.START_STEP)==0){
			String participant = getAtomAsString(args[1]);
			timeOut = getTermAsInt(args[2]); 			
			String initialInfo = getTermAsString(args[3]);

			//building message template
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			msg.setProtocol("fipa-request");
			msg.setContent(agName+","+initialInfo+","+agentConversationID);
			String factName = getFactoryName(agentConversationID,"FRQ",true);
			fri = new Jason_Fipa_Request_Initiator(agName, ts);
			String prevFactory = "";
			if (Protocol_Factory!=null)
				prevFactory = Protocol_Factory.getName();
			if (prevFactory.compareTo(factName)!=0) // if it is a new conversation a create a new one. This verification is not strictly 
				//necessary because it supposed that this condition will be always truth. This must be improved but, 
				//as the participants can not distinguish between conversation of the same factory also one factory per conversation 
				//is created with the initiator factory name as a filter. This implies one factory per conversation in the initiator too
			{
				Protocol_Factory = fri.newFactory(factName, null, 
						msg, 1, ((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent(),timeOut);
				((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().addFactoryAsInitiator(Protocol_Factory);
			}

			myag.lock();
			String ConvID = myag.newConvID();
			FRConversation conv = new FRConversation(agentConversationID,ConvID,timeOut,participant,initialInfo,
					((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().getAid(),factName); //the internal id is unknown yet
			ConvCProcessor processorTemplate = ((ConvCFactory)Protocol_Factory).cProcessorTemplate();
			processorTemplate.setConversation(conv);
			msg.setConversationId(ConvID);
			ConvCProcessor convPprocessor =  myag.newConversation(msg, processorTemplate, false, Protocol_Factory);
			convPprocessor.setConversation(conv);
			myag.unlock();
			conversationsList.put(agentConversationID, conv);
		}else
			if(protocolSteep.compareTo(Protocol_Template.REQUEST_STEP)==0){

				((FRConversation)conversationsList.get(agentConversationID)).frMessage=getTermAsString(args[1]);
				((FRConversation)conversationsList.get(agentConversationID)).frData=getTermAsString (args[3]);

				conversationsList.get(agentConversationID).release_semaphore();	
			}else
				if(protocolSteep.compareTo(Protocol_Template.TASK_DONE_STEP)==0){
					conversationsList.get(agentConversationID).release_semaphore();

					// It's deleted because this is the last step of the conversation
					conversationsList.remove(agentConversationID);
				}
		return true;
	}
}

