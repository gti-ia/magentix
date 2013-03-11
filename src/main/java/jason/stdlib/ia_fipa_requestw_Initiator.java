package jason.stdlib;

import jason.JasonException;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvCFactory;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvCProcessor;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvJasonAgent;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvMagentixAgArch;
import es.upv.dsic.gti_ia.jason.conversationsFactory.FRWConversation;
import es.upv.dsic.gti_ia.jason.conversationsFactory.Protocol_Template;
import es.upv.dsic.gti_ia.jason.conversationsFactory.protocolInternalAction;
import es.upv.dsic.gti_ia.jason.conversationsFactory.initiator.Jason_Fipa_RequestW_Initiator;

import es.upv.dsic.gti_ia.core.ACLMessage;

/**
 * This class represents the internal action to be used when adding a conversation to 
 * a Jason agent under the Fipa Request When Protocol as initiator
 * @author Bexy Alfonso Espinosa
 */

public class ia_fipa_requestw_Initiator extends protocolInternalAction {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	Jason_Fipa_RequestW_Initiator fri = null;

	@Override public int getMinArgs() { return 2; };
	@Override public int getMaxArgs() { return 6; };

	@Override
	public void checkArguments(Term[] args) throws JasonException
	{
		super.checkArguments(args);
		boolean result = false;

		if (((Term)args[args.length-1]).isLiteral()){result=true;}

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
		if ((protocolSteep.compareTo(Protocol_Template.REQUEST_STEP)==0)||
				(protocolSteep.compareTo(Protocol_Template.TASK_DONE_STEP)==0))
		{
			result = (result && (((Term)args[1]).isLiteral()) ); //Condition
		}
		if (protocolSteep.compareTo(Protocol_Template.REQUEST_STEP)==0)
		{
			result = (result && (((Term)args[2]).isAtom()) );
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
		
		agentConversationID = getAtomAsString(args[args.length-1]);

		agName  = ts.getUserAgArch().getAgName();

		ConvJasonAgent myag = ((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent();
		
		ts.getAg().getLogger().info("CALLING INTERNAL ACTION WITH STEEP: '"+protocolSteep+"'"+" CID: "+agentConversationID);

		/*
		 * When a FR Protocol is taking place*/
		if (protocolSteep.compareTo(Protocol_Template.START_STEP)==0){

			String participant = getAtomAsString(args[1]);

			timeOut = getTermAsInt(args[2]); 			

			String initialInfo = getTermAsString(args[4]);

			//This time allows participants to join the conversation
			int wait = 0;
            while(wait<=100000){
            	wait++;
            }
			
			//building message template
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			msg.setProtocol("request-when");

			msg.setContent(agName+","+initialInfo+","+agentConversationID);
			String factName = "Web_Protocol_Factory";
			if (fri == null){
				fri = new Jason_Fipa_RequestW_Initiator(agName, ts);


				Protocol_Factory = fri.newFactory(factName, null, 
						msg, 1, ((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent(),timeOut);

				((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().addFactoryAsInitiator(Protocol_Factory);
				
			}

			/* finally the new conversation starts an asynchronous conversation.*/
			
			myag.lock();
			String ConvID = myag.newConvID();
			FRWConversation conv = new FRWConversation(agentConversationID,ConvID,timeOut,participant,initialInfo,
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

				((FRWConversation)conversationsList.get(agentConversationID)).frMessage=getTermAsString(args[1]);
				
				ACLMessage requestmsg = new ACLMessage();
				requestmsg.setSender(myag.getAid());
				requestmsg.setReceiver(myag.getAid());
				requestmsg.setProtocol("request-when");
				requestmsg.setPerformative(ACLMessage.INFORM);
				requestmsg.setContent("Do the request");
				requestmsg.setConversationId(((FRWConversation)conversationsList.get(agentConversationID)).internalConvID);
				//Message for getting out of the WAIT_FOR_CONDITION state
				myag.send(requestmsg);

			}else
				if(protocolSteep.compareTo(Protocol_Template.TASK_DONE_STEP)==0){

					conversationsList.get(agentConversationID).release_semaphore();
					
					conversationsList.remove(agentConversationID);
				}

		return true;

	}



}

