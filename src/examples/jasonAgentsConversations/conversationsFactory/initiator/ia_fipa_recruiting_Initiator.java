package jasonAgentsConversations.conversationsFactory.initiator;


import es.upv.dsic.gti_ia.core.ACLMessage;
import jason.JasonException;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.LiteralImpl;
import jason.asSyntax.Term;
import jasonAgentsConversations.agent.ConvMagentixAgArch;
import jasonAgentsConversations.agent.Protocol_Template;
import jasonAgentsConversations.agent.protocolInternalAction;

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

		if (((Term)args[args.length-1]).isAtom()){result=true;}
		result = (result && (((Term)args[0]).isString()) );

		if  (protocolSteep.compareTo(Protocol_Template.START_STEEP)==0)
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

		agentConversationID = getAtomAsString(args[args.length-1]);

		checkArguments(args);

		agName  = ts.getUserAgArch().getAgName();

		ts.getAg().getLogger().info("CALLING INTERNAL ACTION WITH STEEP: ....... '"+protocolSteep+"'");

		/*
		 * When a FRC Protocol is taking place*/
		if (protocolSteep.compareTo(Protocol_Template.START_STEEP)==0){

			timeOut = getTermAsInt(args[1]); 	
			LiteralImpl cond = new LiteralImpl(getTermAsLiteral(args[2]));
						
			int particnumber = getTermAsInt(args[3]);
			
			String participant = getAtomAsString(args[4]);
			
			String initialInfo = getTermAsString(args[5]);
			
			//building message template
			ACLMessage msg = new ACLMessage();
			msg.setProtocol("fipa-recruiting");

			//int participantsNumber = participants.size();
			
			msg.setContent(initialInfo);

			//This time allows participants to join the conversation
			int wait = 0;
            while(wait<=100000){
            	wait++;
            }

			/* The agent creates the CFactory that creates processors that initiate
        		 CONTRACT_NET protocol conversations. In this
        		 example the CFactory gets the name "TALK", we don't add any
        		 additional message acceptance criterion other than the required
        		 by the CONTRACT_NET protocol (null) and we do not limit the number of simultaneous
        		 processors (value 0)*/
			frci = new Jason_Fipa_Recruiting_Initiator(agName, agentConversationID, ts, 
					((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().getAid(), timeOut);
			
			frci.initialMsg = initialInfo;
			frci.Participant=participant;
			frci.Condition  =  cond; 
			frci.ParticipantsNumber =  particnumber;

			Protocol_Factory = frci.newFactory("Protocol_Factory", null, 
					msg, 1, ((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent(),timeOut);

			/* The factory is setup to answer start conversation requests from the agent
        		 using the FIPA_REQUEST protocol.*/

			((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().addFactoryAsInitiator(Protocol_Factory);

			/* finally the new conversation starts an asynchronous conversation.*/
			Protocol_Processor = Protocol_Factory.cProcessorTemplate();

			Protocol_Processor.createAsyncConversation(msg);



		}else
			if(protocolSteep.compareTo(Protocol_Template.RECEIVE_INFORM_STEEP)==0){

				frci.Protocol_Semaphore.release();
			}

		return true;

	}

}
