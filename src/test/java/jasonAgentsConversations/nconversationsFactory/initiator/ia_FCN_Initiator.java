package jasonAgentsConversations.nconversationsFactory.initiator;

//Internal action code for project CNP


import es.upv.dsic.gti_ia.cAgents.SendState;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import jason.JasonException;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Atom;
import jason.asSyntax.ListTerm;
import jason.asSyntax.Term;
import jasonAgentsConversations.agentNConv.ConvCFactory;
import jasonAgentsConversations.agentNConv.ConvCProcessor;
import jasonAgentsConversations.agentNConv.ConvJasonAgent;
import jasonAgentsConversations.agentNConv.ConvMagentixAgArch;
import jasonAgentsConversations.agentNConv.FCNConversation;
import jasonAgentsConversations.agentNConv.Protocol_Template;
import jasonAgentsConversations.agentNConv.protocolInternalAction;


public class ia_FCN_Initiator extends protocolInternalAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Jason_FCN_Initiator fcnp = null;
    ACLMessage msg = new ACLMessage(ACLMessage.CFP); //This message acst as template for all conversations under this protocol
	//private int contador = 0;


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
					case 1: result = (result&&t.isList());
					break;
					case 2: result = (result&&t.isList());
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
		// execute the internal action ".loadProtocol(P,callForProposal,"cnp",TO,CT,C1)" 
		//contador++;
		//ts.getAg().getLogger().info("Contador -> "+contador);

		agName  = ts.getUserAgArch().getAgName();
		
		ConvJasonAgent myag = ((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent();
		
		
		
		agentConversationID = getAtomAsString(args[args.length-1]);
		
		ts.getAg().getLogger().info("CALLING INTERNAL ACTION WITH STEEP: '"+protocolSteep+"'"+" CID: "+agentConversationID);

		//the first state in the conversation
		if (protocolSteep.compareTo(Protocol_Template.START_STEEP)==0){
			//TODO: It's necessary to document that this is the time for waiting for the inform
			timeOut = getTermAsInt(args[1]);
			
			//TODO: It's necessary to document that this is the deadLine for waiting for proposals. Also in the Jason code examples
			long deadLineTime = getTermAslong(args[2]);
			
			Term participants = args[3];
			
			Term proposal = args[4];
			
			//This time allows participants to join the conversation
			int wait = 0;
			while(wait<=100000){
				wait++;
			}
			
			if (fcnp == null){
				//TODO: Document: The initial participants are going to be the same in the rest of instantiations of the protocol
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
				fcnp = new Jason_FCN_Initiator(agName, ts) ;

//TODO: It's necessary to document that the maxium number of conversations with this CFactory is 30
				Protocol_Factory = fcnp.newFactory("CNPFACTORY", null, 
						msg, 1, ((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent() ,
						participantsNumber, deadLineTime,timeOut);
				/* The factory is setup to answer start conversation requests from the agent
       		 using the FIPA_REQUEST protocol.*/

				((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().addFactoryAsInitiator(Protocol_Factory);
				//ts.getAg().getLogger().info("@@@@@@@@@@@@@  Initiator de la factory al crear: "+Protocol_Factory.convinitiator);
			}
			
			/* finally the new conversation starts an asynchronous conversation.*/
			myag.lock();
			String ConvID = myag.newConvID();
			FCNConversation conv = new FCNConversation(agentConversationID,ConvID,"Starting new FCN conversation",
					((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().getAid()); //the internal id is unknown yet
			ConvCProcessor processorTemplate = ((ConvCFactory)Protocol_Factory).cProcessorTemplate();
			processorTemplate.setConversation(conv);
			//It is necessary in each new conversation with the same protocol to update the template
			/*if (processorTemplate.getState("SOLICIT_PROPOSALS")!=null){
				((SendState)processorTemplate.getState("SOLICIT_PROPOSALS")).setMessageTemplate(msg);
			}*/
			
			msg.setConversationId(ConvID);
			ConvCProcessor convPprocessor =  myag.newConversation(msg, processorTemplate, false, Protocol_Factory);
			convPprocessor.setConversation(conv);
			myag.unlock();
			conv.solicitude = agentConversationID;
			conversationsList.put(agentConversationID, conv);
			

		}
		else
			if (protocolSteep.compareTo(Protocol_Template.PROPOSALS_EVALUATED_STEP)==0){
				//the agent finished the evaluations of proposals
				
				Term PropAccepted = args[1];
				Term PropRejected = args[2];
				
				((FCNConversation)conversationsList.get(agentConversationID)).myAcceptances =getTermAsString(PropAccepted);
				((FCNConversation)conversationsList.get(agentConversationID)).myRejections =getTermAsString(PropRejected);
				/*
				 * Releases the semaphore, and if there is a process waiting, it will go on
				 */				
				conversationsList.get(agentConversationID).release_semaphore();	
				
			}
			else
				if (protocolSteep.compareTo(Protocol_Template.RESULTS_PROCESSED_STEP)==0){
					//the agent finished to process the results

					/*
					 * Releases the semaphore, and if there is a process waiting, it will take 
					 * it again
					 */
										
					conversationsList.get(agentConversationID).release_semaphore();
				}

		// everything ok, so returns true
		return true;
	}
}