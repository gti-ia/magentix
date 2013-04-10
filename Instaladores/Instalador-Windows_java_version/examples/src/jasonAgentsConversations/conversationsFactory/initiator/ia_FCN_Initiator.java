package jasonAgentsConversations.conversationsFactory.initiator;


//Internal action code for project CNP


import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;
import jasonAgentsConversations.agent.ConvMagentixAgArch;
import jasonAgentsConversations.agent.Protocol_Template;
import jasonAgentsConversations.agent.protocolInternalAction;


public class ia_FCN_Initiator extends protocolInternalAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Jason_FCN_Initiator fcnp;
	private int contador = 0;


	@Override public int getMinArgs() { return 2; };
	@Override public int getMaxArgs() { return 7; };

	@Override
	public void checkArguments(Term[] args) throws JasonException{
		
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
				case 2:result = (result&&t.isNumeric());
				break;
				case 3:result = (result&&t.isList());
				break;
				}
				cont++;
			}
		}
		if (protocolSteep.compareTo(Protocol_Template.PROPOSALS_EVALUATED_STEP)==0)
			{
				int cont = 0; 
				for (Term t:args){
					switch (cont){
					case 1:result = (result&&t.isList());
					break;
					case 2:result = (result&&t.isList());
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
		// execute the internal action ".loadProtocol(P,callForProposal,"cnp",TO,CT,C1)" 
		contador++;
		//ts.getAg().getLogger().info("Contador -> "+contador);

		agName  = ts.getUserAgArch().getAgName();
		Term protSteep = args[0];
		protocolSteep = ((StringTerm)protSteep).getString();
		agentConversationID = ((Atom)args[args.length-1]).toString();
		ts.getAg().getLogger().info("CALLING INTERNAL ACTION WITH STEEP: ....... '"+protocolSteep+"' agentConversationID: "+agentConversationID);
		checkArguments(args);

		//the first state in the conversation
		if (protocolSteep.compareTo(Protocol_Template.START_STEEP)==0){
			timeOut = (int) ((NumberTerm)args[1]).solve();
			conversationTime = (long) ((NumberTerm)args[2]).solve();
			Term participants = args[3];
			Term proposal = args[4];
			//This time allows participants to join the conversation
			int wait = 0;
            while(wait<=100000){
            	wait++;
            }
			
			//building message template
			ACLMessage msg = new ACLMessage(ACLMessage.CFP);

			int participantsNumber = 0;

			for (Term t: (ListTerm)participants) {
				String rec = ((Atom)t).toString();
				msg.addReceiver(new AgentID(rec));
				participantsNumber++;
			}

			msg.setContent(getTermAsString(proposal));


			/* The agent creates the CFactory that creates processors that initiate
        		 CONTRACT_NET protocol conversations. In this
        		 example the CFactory gets the name "TALK", we don't add any
        		 additional message acceptance criterion other than the required
        		 by the CONTRACT_NET protocol (null) and we do not limit the number of simultaneous
        		 processors (value 0)*/
			fcnp = new Jason_FCN_Initiator(agName, agentConversationID, ts, 
					((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().getAid(),timeOut );

			Protocol_Factory = fcnp.newFactory("CNPFACTORY", null, 
					msg, 1, ((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent() ,
					participantsNumber, conversationTime,timeOut);

			/* The factory is setup to answer start conversation requests from the agent
        		 using the CONTRACT_NET protocol.*/

			((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().addFactoryAsInitiator(Protocol_Factory);

			/* finally the new conversation starts. Because it is synchronous, 
        		 the current interaction halts until the new conversation ends.*/
			Protocol_Processor = Protocol_Factory.cProcessorTemplate();

			Protocol_Processor.createAsyncConversation(msg);

		}
		else
			if (protocolSteep.compareTo(Protocol_Template.PROPOSALS_EVALUATED_STEP)==0){
				//the agent finished the evaluations of proposals
				Term PropAccepted = args[1];
				Term PropRejected = args[2];
				fcnp.myAcceptances = ((ListTerm)PropAccepted).getAsList().toString();
				fcnp.myRejections = ((ListTerm)PropRejected).getAsList().toString();
				/*
				 * Releases the semaphore, and if there is a process waiting, it will go on
				 */

				fcnp.mySem.release();

			}
			else
				if (protocolSteep.compareTo(Protocol_Template.RESULTS_PROCESSED_STEP)==0){
					//the agent finished to process the results

					/*
					 * Releases the semaphore, and if there is a process waiting, it will take 
					 * it again
					 */
					
					fcnp.mySem.release();
				}

		// everything ok, so returns true
		return true;
	}
}