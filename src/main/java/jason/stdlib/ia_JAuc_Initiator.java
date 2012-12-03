package jason.stdlib;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jason.JasonException;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvCFactory;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvCProcessor;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvJasonAgent;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvMagentixAgArch;
import es.upv.dsic.gti_ia.jason.conversationsFactory.JAucIniConversation;
import es.upv.dsic.gti_ia.jason.conversationsFactory.Protocol_Template;
import es.upv.dsic.gti_ia.jason.conversationsFactory.protocolInternalAction;
import es.upv.dsic.gti_ia.jason.conversationsFactory.initiator.Jason_JAuc_Initiator;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;

/**
 * This class represents the internal action to be used when adding a conversation to 
 * a Jason agent under the Japanese Auction Protocol as initiator
 * @author Bexy Alfonso Espinosa
 */

public class ia_JAuc_Initiator extends protocolInternalAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Jason_JAuc_Initiator jauci;

	@Override public int getMinArgs() { return 9; };
	@Override public int getMaxArgs() { return 9; };

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

		if  (protocolSteep.compareTo(Protocol_Template.START_STEP)==0)
		{
			int cont = 0; 
			for (Term t:args){
				switch (cont){
				case 1:result = (result&&t.isNumeric());//join timeout
				break;
				case 2:result = (result&&t.isNumeric());//timeout
				break;
				case 3:result = (result&&t.isNumeric());//initial bid
				break;
				case 4:result = (result&&t.isNumeric());//increment
				break;
				case 5:result = (result&&t.isNumeric());//max iterations
				break;
				case 6:result = (result&&t.isString());//initial msg
				break;
				case 7:result = (result&&t.isList());//participants
				break;
				//case 7:result = (result&&t.isLiteral());//request
				//break;
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
		
		ConvJasonAgent myag = ((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent();

		agName  = ts.getUserAgArch().getAgName();
		ts.getAg().getLogger().fine("CALLING INTERNAL ACTION WITH STEEP: '"+protocolSteep+"'"+" CID: "+agentConversationID);

		
		//the first state in the conversation
		if (protocolSteep.compareTo(Protocol_Template.START_STEP)==0){
			
			joinTimeOut = getTermAsInt(args[1]); 
			
			timeOut = getTermAsInt(args[2]); 	
			
			double initialBid = getTermAsdouble(args[3]);
			
			double increment = getTermAsdouble(args[4]);
			
			int maxIterations = getTermAsInt(args[5]);
			
			String initialMsg = getTermAsString(args[6]);
			
			List<String> participants = getTermAsStringList(args[7]);
			List<AgentID> AIDparticipants =  new ArrayList<AgentID>();
			Iterator<String> it = participants.iterator();
			while (it.hasNext())
				AIDparticipants.add(new AgentID(it.next()));
			
			String request = getTermAsString(args[8]);
			
			int particnumber = participants.size();
			
			//building message template
			ACLMessage msg = new ACLMessage();
			msg.setProtocol("fipa-recruiting");

			//int participantsNumber = participants.size();
			
			msg.setContent(initialMsg);

			if (jauci == null){


				/* The agent creates the CFactory that creates processors that initiate
        		 the protocol conversations. */
				jauci = new Jason_JAuc_Initiator( ts);

				Protocol_Factory = jauci.newFactory("JAFACTORY", null, 
						msg, 1, ((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent(),particnumber,timeOut, joinTimeOut );

				((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().addFactoryAsInitiator(Protocol_Factory);
				

			}
			
			/* finally the new conversation starts an asynchronous conversation.*/
			myag.lock();
			String ConvID = myag.newConvID();
			JAucIniConversation conv = new JAucIniConversation(agentConversationID,ConvID,
					((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().getAid(),
					AIDparticipants,initialMsg,initialBid, increment,maxIterations); //the internal id is unknown yet
			ConvCProcessor processorTemplate = ((ConvCFactory)Protocol_Factory).cProcessorTemplate();
			processorTemplate.setConversation(conv);
			msg.setConversationId(ConvID);
			ConvCProcessor convPprocessor =  myag.newConversation(msg, processorTemplate, false, Protocol_Factory);
			convPprocessor.setConversation(conv);
			myag.unlock();
			conv.request =  request;
			conversationsList.put(agentConversationID, conv);

		}

		return true;

	}


}
