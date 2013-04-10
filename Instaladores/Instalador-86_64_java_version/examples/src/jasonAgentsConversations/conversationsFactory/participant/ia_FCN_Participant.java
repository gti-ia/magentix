package jasonAgentsConversations.conversationsFactory.participant;

//Internal action code for project CNP

import es.upv.dsic.gti_ia.core.AgentID;
import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;
import jasonAgentsConversations.agent.ConvMagentixAgArch;
import jasonAgentsConversations.agent.Protocol_Template;
import jasonAgentsConversations.agent.protocolInternalAction;


public class ia_FCN_Participant extends protocolInternalAction {
	//this name of loadProtocol must be changed by another one like "InitiatorProtocolManager"


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Jason_FCN_Participant fcnp;

	//her there must be more factory fields

	@Override public int getMinArgs() { return 2; };
	@Override public int getMaxArgs() { return 5; };

	@Override
	public void checkArguments(Term[] args) throws JasonException{

		super.checkArguments(args);
		boolean result = false;

		if (((Term)args[args.length-1]).isAtom()){result=true;}

		result = (result && (((Term)args[0]).isString()) );
		
		if ((protocolSteep.compareTo(Protocol_Template.TASK_DONE_STEEP)==0)||
				(protocolSteep.compareTo(Protocol_Template.TASK_NOT_DONE_STEEP)==0))
		{
			result = (result && (((Term)args[1]).isString()) );
		}
		if (protocolSteep.compareTo(Protocol_Template.MAKE_PROPOSAL_STEP)==0)
		{
			result = (result && (((Term)args[1]).isNumeric()||((Term)args[1]).isNumeric()) );
		}		
		if (protocolSteep.compareTo(Protocol_Template.JOIN_STEEP)==0)
		{
			result = (result && (((Term)args[1]).isNumeric()) );
		}
		
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
		ts.getAg().getLogger().info("CALLING INTERNAL ACTION WITH STEEP: ....... '"+protocolSteep+"'");
		
		//the first state in the conversation

		if (protocolSteep.compareTo(Protocol_Template.JOIN_STEEP)==0){

			Term timeOut = args[1];

			int ntimeOut = (int) ((NumberTerm)timeOut).solve();
			
			AgentID tmpid = ((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().getAid();

			fcnp = new Jason_FCN_Participant(agName,"", ts,tmpid,	ntimeOut);

			// The agent creates the CFactory that manages every message which its
			// performative is set to CFP and protocol set to CONTRACTNET. In this
			// example the CFactory gets the name "TALK", we don't add any
			// additional message acceptance criterion other than the required
			// by the CONTRACTNET protocol (null) and we limit the number of simultaneous
			// processors to 1, i.e. the requests will be attended one after another.

			Protocol_Factory = fcnp.newFactory("CNPFACTORY", null,null, 1,
					((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent(), 0);

			

			// Finally the factory is setup to answer to incoming messages that
			// can start the participation of the agent in a new conversation
			fcnp.setConversationID(agentConversationID);
			((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().addFactoryAsParticipant(Protocol_Factory);

		}
		else
			if (protocolSteep.compareTo(Protocol_Template.MAKE_PROPOSAL_STEP)==0){
				//the agent finished the evaluations of proposals
				Term proposal = args[1];
				
				fcnp.answerToProposal = "propose";
				fcnp.myProposal = getTermAsString(proposal);

				fcnp.mySem.release();
			}
			else
				if (protocolSteep.compareTo(Protocol_Template.REFUSE_STEEP)==0){

					fcnp.answerToProposal = "refuse";

					fcnp.mySem.release();

				}
				else
					if (protocolSteep.compareTo(Protocol_Template.NOT_UNDERSTOOD_STEEP)==0){

						fcnp.answerToProposal = "notUnderstood";

						fcnp.mySem.release();
					}
					else
						if (protocolSteep.compareTo(Protocol_Template.TASK_DONE_STEEP)==0){

							Term finalInfo = args[1];

							fcnp.infoToSend = ((StringTerm)finalInfo).getString();
							fcnp.taskDone = true;

							fcnp.mySem.release();

						}
						else
							if (protocolSteep.compareTo(Protocol_Template.TASK_NOT_DONE_STEEP)==0){

								Term finalInfo = args[1];
								fcnp.infoToSend = ((StringTerm)finalInfo).getString();
								fcnp.taskDone = false;

								fcnp.mySem.release();

							}

		/*if (true) { // just to show how to throw another kind of exception
			throw new JasonException("not implemented!");
		}*/

		// everything ok, so returns true
		return true;
	}
}
