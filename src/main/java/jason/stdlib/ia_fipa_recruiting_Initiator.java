package jason.stdlib;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvCFactory;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvCProcessor;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvJasonAgent;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvMagentixAgArch;
import es.upv.dsic.gti_ia.jason.conversationsFactory.FRCConversation;
import es.upv.dsic.gti_ia.jason.conversationsFactory.Protocol_Template;
import es.upv.dsic.gti_ia.jason.conversationsFactory.protocolInternalAction;
import es.upv.dsic.gti_ia.jason.conversationsFactory.initiator.Jason_Fipa_Recruiting_Initiator;
import jason.JasonException;
import jason.asSyntax.LiteralImpl;
import jason.asSyntax.Term;
import jason.asSemantics.Unifier;
import jason.asSemantics.TransitionSystem;

/**
 * This class represents the internal action to be used when adding a conversation to 
 * a Jason agent under the Fipa Recruiting Protocol as initiator
 * @author Bexy Alfonso Espinosa
 */

public class ia_fipa_recruiting_Initiator extends protocolInternalAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	Jason_Fipa_Recruiting_Initiator frci;

	@Override public int getMinArgs() { return 2; };
	@Override public int getMaxArgs() { return 7; };

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
				case 1:result = (result&&t.isNumeric());
				break;
				case 2:result = (result&&t.isLiteral());
				break;
				case 3:result = (result&&t.isNumeric());
				break;
				case 4:result = (result&&t.isAtom());
				break;
				case 5:result = (result&&t.isString());
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

		ConvJasonAgent myag = ((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent();

		agName  = ts.getUserAgArch().getAgName();
		if (ts.getSettings().verbose()>1)
			ts.getAg().getLogger().info("CALLING INTERNAL ACTION WITH STEEP: '"+protocolSteep+"'"+" CID: "+agentConversationID);


		//the first state in the conversation
		if (protocolSteep.compareTo(Protocol_Template.START_STEP)==0){
			timeOut = getTermAsInt(args[1]); 	
			LiteralImpl cond = new LiteralImpl(getTermAsLiteral(args[2]));
			int particnumber = getTermAsInt(args[3]);
			String participant = getAtomAsString(args[4]);
			String initialInfo = getTermAsString(args[5]);

			//building message template
			ACLMessage msg = new ACLMessage();
			msg.setProtocol("fipa-recruiting");
			msg.setContent(initialInfo);
			String factName = getFactoryName(agentConversationID,"FRC",true);
			frci = new Jason_Fipa_Recruiting_Initiator( ts);
			String prevFactory = "";
			if (Protocol_Factory!=null)
				prevFactory = Protocol_Factory.getName();
			if (prevFactory.compareTo(factName)!=0) // if it is a new conversation a create a new one. This verification is not strictly 
				//necessary because it supposed that this condition will be always truth. This must be improved but, 
				//as the participants can not distinguish between conversation of the same factory also one factory per conversation 
				//is created with the initiator factory name as a filter. This implies one factory per conversation in the initiator too
			{Protocol_Factory = frci.newFactory(factName, null, 
					msg, 1, ((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent(),timeOut);
			/* The factory is setup to answer start conversation requests from the agent
       		 using the FIPA_REQUEST protocol.*/

			((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().addFactoryAsInitiator(Protocol_Factory);
			}

			myag.lock();
			String ConvID = myag.newConvID();
			FRCConversation conv = new FRCConversation(agentConversationID,ConvID,initialInfo,timeOut,new AgentID(participant),
					((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().getAid(),factName); //the internal id is unknown yet
			conv.Condition = cond;
			conv.participantsNumber = particnumber;
			ConvCProcessor processorTemplate = ((ConvCFactory)Protocol_Factory).cProcessorTemplate();
			processorTemplate.setConversation(conv);
			msg.setConversationId(ConvID);
			ConvCProcessor convPprocessor =  myag.newConversation(msg, processorTemplate, false, Protocol_Factory);
			convPprocessor.setConversation(conv);
			myag.unlock();
			conversationsList.put(agentConversationID, conv);

		}else
			if(protocolSteep.compareTo(Protocol_Template.RECEIVE_INFORM_STEP)==0){
				conversationsList.get(agentConversationID).release_semaphore();
			}

		return true;

	}

}
