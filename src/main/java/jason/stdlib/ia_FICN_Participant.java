package jason.stdlib;

//Internal action code for project CNP

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvMagentixAgArch;
import es.upv.dsic.gti_ia.jason.conversationsFactory.Conversation;
import es.upv.dsic.gti_ia.jason.conversationsFactory.FICNConversation;
import es.upv.dsic.gti_ia.jason.conversationsFactory.Protocol_Template;
import es.upv.dsic.gti_ia.jason.conversationsFactory.protocolInternalAction;
import es.upv.dsic.gti_ia.jason.conversationsFactory.participant.Jason_FICN_Participant;


public class ia_FICN_Participant extends protocolInternalAction {
	//this name of loadProtocol must be changed by another one like "InitiatorProtocolManager"


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Jason_FICN_Participant fcnp;

	//her there must be more factory fields

	@Override public int getMinArgs() { return 2; };
	@Override public int getMaxArgs() { return 5; };

	@Override
	public void checkArguments(Term[] args) throws JasonException{

		super.checkArguments(args);
		boolean result = false;

		if (((Term)args[args.length-1]).isAtom()){result=true;}

		result = (result && (((Term)args[0]).isString()) );
		
		if ((protocolSteep.compareTo(Protocol_Template.TASK_DONE_STEP)==0)||
				(protocolSteep.compareTo(Protocol_Template.TASK_NOT_DONE_STEP)==0))
		{
			result = (result && (((Term)args[1]).isString()) );
		}
		if (protocolSteep.compareTo(Protocol_Template.MAKE_PROPOSAL_STEP)==0)
		{
			result = (result && (((Term)args[1]).isNumeric()||((Term)args[1]).isNumeric()) );
		}		
		/*if (protocolSteep.compareTo(Protocol_Template.JOIN_STEEP)==0)
		{
			result = (result && (((Term)args[1]).isNumeric()) );
		}*/
		
		if (!result)
		{
			throw JasonException.createWrongArgument(this,"Parameters must be in correct format.");
		}
	}

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		// execute the internal action ".loadProtocol(P,callForProposal,"cnp",TO,CT,C1)" 

		/*ts: contiene toda la informaci�n del estado actual del agente
		 un: funcion de unificacion determinada actualmente por:
		   1. La ejecuci�n del plan donde la accion interna aparece 
		   2. El chequeo de si el plan es aplicable. Esto depende de si 
		   la accion interna que esta siendo ejecutada aparece en el cuerpo 
		   o el contexto del plan.
		 args: Terminos de la funcion		    */
		
		agName  = ts.getUserAgArch().getAgName();

		Term protSteep = args[0];

		agentConversationID = ((Term)args[args.length-1]).toString();

		protocolSteep = ((StringTerm)protSteep).getString();

		checkArguments(args);

		ts.getAg().getLogger().info("CALLING INTERNAL ACTION WITH STEEP: '"+protocolSteep+"'"+" CID: "+agentConversationID); 
		
		//the first state in the conversation

		if (protocolSteep.compareTo(Protocol_Template.JOIN_STEP)==0){
			//TODO: It is necessary to document that 80000 is the max time for the waiting states by default so 
			//in the Jason code of the participant this data is eliminated. Update examples in document. This value 
			//must be greater than the wait_for_proposals deadline.
			int timeOut = 80000;
			
			if (fcnp == null){

				if (args.length >2)
				{					
					timeOut = (int)((NumberTerm)args[1]).solve();
				}
				
				//AgentID tmpid = ((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().getAid();
				
				fcnp = new Jason_FICN_Participant(agName, ts, timeOut);

				// The agent creates the CFactory that manages every message which its
				// performative is set to CFP and protocol set to CONTRACTNET. In this
				// example the CFactory gets the name "TALK", we don't add any
				// additional message acceptance criterion other than the required
				// by the CONTRACTNET protocol (null) and we limit the number of simultaneous
				// processors to 1, i.e. the requests will be attended one after another.

				Protocol_Factory = fcnp.newFactory("Protocol_Factory", null,null,1, 
						((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent(),timeOut);

				//Conversation conv = new Conversation(agentConversationID,""); //the internal id is unknown yet
				//((ConvCFactory)Protocol_Factory).cProcessorTemplate().setConversation(conv);

				
				
				// Finally the factory is setup to answer to incoming messages that
				// can start the participation of the agent in a new conversation

				((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().addFactoryAsParticipant(Protocol_Factory);
				
				//This helps participant to finish joining in Magentix conversation thread.
				int i = 0;
				while (i>900000000){i++;}
				
			}
		}
		else
			if (protocolSteep.compareTo(Protocol_Template.MAKE_PROPOSAL_STEP)==0){
				//the agent finished the evaluations of proposals
				Conversation conv = conversationsList.get(agentConversationID);
				
				if (conv==null)
					{conv = Protocol_Factory.removeConversationByJasonID(agentConversationID);
					conversationsList.put(agentConversationID, conv);}
				
				Term proposal = args[1];
				
				((FICNConversation)conversationsList.get(agentConversationID)).kindOfAnswer = "propose";
				((FICNConversation)conversationsList.get(agentConversationID)).proposal = getTermAsString(proposal);

				conversationsList.get(agentConversationID).release_semaphore();
			}
			else
				if (protocolSteep.compareTo(Protocol_Template.REFUSE_STEP)==0){
					Conversation conv = conversationsList.get(agentConversationID);
					
					if (conv==null)
						{ conv = Protocol_Factory.removeConversationByJasonID(agentConversationID);
						conversationsList.put(agentConversationID, conv);}
					
					((FICNConversation)conversationsList.get(agentConversationID)).kindOfAnswer = "refuse";

					conversationsList.get(agentConversationID).release_semaphore();

				}
				else
					if (protocolSteep.compareTo(Protocol_Template.NOT_UNDERSTOOD_STEP)==0){
						Conversation conv = conversationsList.get(agentConversationID);
						
						if (conv==null)
							{ conv = Protocol_Factory.removeConversationByJasonID(agentConversationID);
							conversationsList.put(agentConversationID, conv);}
						
						((FICNConversation)conversationsList.get(agentConversationID)).kindOfAnswer = "notUnderstood";

						conversationsList.get(agentConversationID).release_semaphore();
					}
					else
						if (protocolSteep.compareTo(Protocol_Template.TASK_DONE_STEP)==0){
							
							Term finalInfo = args[1];

							((FICNConversation)conversationsList.get(agentConversationID)).infoToSend = ((StringTerm)finalInfo).getString();
							((FICNConversation)conversationsList.get(agentConversationID)).taskDone = true;

							conversationsList.get(agentConversationID).release_semaphore();

						}
						else
							if (protocolSteep.compareTo(Protocol_Template.TASK_NOT_DONE_STEP)==0){

								Term finalInfo = args[1];
								((FICNConversation)conversationsList.get(agentConversationID)).infoToSend = ((StringTerm)finalInfo).getString();
								((FICNConversation)conversationsList.get(agentConversationID)).taskDone = false;

								conversationsList.get(agentConversationID).release_semaphore();

							}

		/*if (true) { // just to show how to throw another kind of exception
			throw new JasonException("not implemented!");
		}*/

		// everything ok, so returns true
		return true;
	}
}
