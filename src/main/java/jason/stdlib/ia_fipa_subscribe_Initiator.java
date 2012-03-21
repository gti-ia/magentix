package jason.stdlib;

import java.util.Iterator;
import java.util.List;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvCFactory;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvCProcessor;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvJasonAgent;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvMagentixAgArch;
import es.upv.dsic.gti_ia.jason.conversationsFactory.FSConversation;
import es.upv.dsic.gti_ia.jason.conversationsFactory.Protocol_Template;
import es.upv.dsic.gti_ia.jason.conversationsFactory.protocolInternalAction;
import es.upv.dsic.gti_ia.jason.conversationsFactory.initiator.Jason_Fipa_Subscribe_Initiator;

import jason.JasonException;
import jason.asSyntax.Term;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
public class ia_fipa_subscribe_Initiator extends protocolInternalAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	Jason_Fipa_Subscribe_Initiator fsi = null;

	@Override public int getMinArgs() { return 2; };
	@Override public int getMaxArgs() { return 5; };

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
				case 1:result = ((result&&t.isAtom())||(result&&t.isString()));	
				break;
				case 2:result = (result&&t.isNumeric());
				break;
				case 3:result = (result&&t.isString());
				break;
				}
				cont++;
			}
		}


		if (protocolSteep.compareTo(Protocol_Template.SUBSCRIBE_STEP)==0)
		{
			int cont = 0; 
			for (Term t:args){
				switch (cont){
				case 1:result = (result&&t.isList());
				break;
				case 2:result = ((result&&t.isAtom())||(result&&t.isString()));	
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
		 * When a FS Protocol is taking place*/
		if (protocolSteep.compareTo(Protocol_Template.START_STEP)==0){

			String participant = getAtomAsString(args[1]);

			timeOut = getTermAsInt(args[2]); 			

			String initialInfo = getTermAsString(args[3]);

			//building message template
			ACLMessage msg = new ACLMessage();
			msg.setProtocol("fipa-subscribe");

			msg.setContent(initialInfo);
			if (fsi == null){
				/* The agent creates the CFactory that creates processors that initiate
        		 CONTRACT_NET protocol conversations. In this
        		 example the CFactory gets the name "TALK", we don't add any
        		 additional message acceptance criterion other than the required
        		 by the CONTRACT_NET protocol (null) and we do not limit the number of simultaneous
        		 processors (value 0)*/
				fsi = new Jason_Fipa_Subscribe_Initiator(agName, ts);

				
				Protocol_Factory = fsi.newFactory("FSFACTORY", null, 
						1, ((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent(),timeOut);

				/* The factory is setup to answer start conversation requests from the agent
       		 using the FIPA_REQUEST protocol.*/
				((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().addFactoryAsInitiator(Protocol_Factory);

				
			}

			/* finally the new conversation starts an asynchronous conversation.*/

			myag.lock();
			String ConvID = myag.newConvID();

			FSConversation conv = new FSConversation(agentConversationID,ConvID,new AgentID(participant),initialInfo,
					((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().getAid()); 
			ConvCProcessor processorTemplate = ((ConvCFactory)Protocol_Factory).cProcessorTemplate();
			processorTemplate.setConversation(conv);
			
			msg.setConversationId(ConvID);
			
			ConvCProcessor convPprocessor =  myag.newConversation(msg, processorTemplate, false, Protocol_Factory);

			convPprocessor.setConversation(conv);
			
			myag.unlock();

			conversationsList.put(agentConversationID, conv);

			
		}else
			if (protocolSteep.compareTo(Protocol_Template.SUBSCRIBE_STEP)==0){
				List<String> objects = getTermAsStringList(args[1]);
				Iterator<String> itr = objects.iterator();
				while (itr.hasNext())
				{
					((FSConversation)conversationsList.get(agentConversationID)).objects.put(itr.next(), "");
				}
				conversationsList.get(agentConversationID).release_semaphore();	

			}else
				if(protocolSteep.compareTo(Protocol_Template.CANCEL_STEP)==0){
					ACLMessage cancelmsg = new ACLMessage();
					cancelmsg.setSender(myag.getAid());
					cancelmsg.setReceiver(myag.getAid());
					cancelmsg.setProtocol("fipa-subscribe");
					cancelmsg.setPerformative(ACLMessage.CANCEL);
					cancelmsg.setContent("Canceling conversation");
					cancelmsg.setConversationId(((FSConversation)conversationsList.get(agentConversationID)).internalConvID);
					//Message for getting out of the WAIT_FOR_INFORM state
					myag.send(cancelmsg);
				}

		return true;

	}




}
