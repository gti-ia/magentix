package jasonAgentsConversations.nconversationsFactory.initiator;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import jasonAgentsConversations.agentNConv.ConvCFactory;
import jasonAgentsConversations.agentNConv.ConvCProcessor;
import jasonAgentsConversations.agentNConv.ConvJasonAgent;
import jasonAgentsConversations.agentNConv.ConvMagentixAgArch;
import jasonAgentsConversations.agentNConv.FRCConversation;
import jasonAgentsConversations.agentNConv.Protocol_Template;
import jasonAgentsConversations.agentNConv.protocolInternalAction;
import jason.JasonException;
import jason.asSyntax.LiteralImpl;
import jason.asSyntax.Term;
import jason.asSemantics.Unifier;
import jason.asSemantics.TransitionSystem;


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

		checkArguments(args);
		
		agentConversationID = getAtomAsString(args[args.length-1]);
		
		ConvJasonAgent myag = ((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent();

		agName  = ts.getUserAgArch().getAgName();

		ts.getAg().getLogger().info("CALLING INTERNAL ACTION WITH STEEP: '"+protocolSteep+"'"+" CID: "+agentConversationID);

		
		//the first state in the conversation
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
			
			if (frci == null){


				/* The agent creates the CFactory that creates processors that initiate
        		 CONTRACT_NET protocol conversations. In this
        		 example the CFactory gets the name "TALK", we don't add any
        		 additional message acceptance criterion other than the required
        		 by the CONTRACT_NET protocol (null) and we do not limit the number of simultaneous
        		 processors (value 0)*/
				frci = new Jason_Fipa_Recruiting_Initiator( ts);

				Protocol_Factory = frci.newFactory("CNPFACTORY", null, 
						msg, 1, ((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent(),timeOut);
				/* The factory is setup to answer start conversation requests from the agent
       		 using the FIPA_REQUEST protocol.*/

				((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().addFactoryAsInitiator(Protocol_Factory);
				//ts.getAg().getLogger().info("@@@@@@@@@@@@@  Initiator de la factory al crear: "+Protocol_Factory.convinitiator);

			}
			
			/*			frci.initialMsg = initialInfo;
						frci.Participant=participant;
						frci.Condition  =  cond; 
						frci.ParticipantsNumber =  particnumber;*/
			
			/* finally the new conversation starts an asynchronous conversation.*/
			myag.lock();
			String ConvID = myag.newConvID();
			FRCConversation conv = new FRCConversation(agentConversationID,ConvID,initialInfo,timeOut,new AgentID(participant),
					((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().getAid()); //the internal id is unknown yet
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
			if(protocolSteep.compareTo(Protocol_Template.RECEIVE_INFORM_STEEP)==0){
				conversationsList.get(agentConversationID).release_semaphore();
				
			}

		return true;

	}

}
