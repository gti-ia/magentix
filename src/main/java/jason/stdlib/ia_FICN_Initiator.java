package jason.stdlib;

//Internal action code for project CNP


import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import jason.JasonException;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Atom;
import jason.asSyntax.ListTerm;
import jason.asSyntax.Term;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvCFactory;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvCProcessor;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvJasonAgent;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvMagentixAgArch;
import es.upv.dsic.gti_ia.jason.conversationsFactory.FICNConversation;
import es.upv.dsic.gti_ia.jason.conversationsFactory.Protocol_Template;
import es.upv.dsic.gti_ia.jason.conversationsFactory.protocolInternalAction;
import es.upv.dsic.gti_ia.jason.conversationsFactory.initiator.Jason_FICN_Initiator;

/**
 * This class represents the internal action to be used when adding a conversation to 
 * a Jason agent under the Fipa Iterated Contract Net Protocol as initiator
 * @author Bexy Alfonso Espinosa
 */

public class ia_FICN_Initiator extends protocolInternalAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Jason_FICN_Initiator fcnp = null;
	ACLMessage msg = new ACLMessage(ACLMessage.CFP); //This message acst as template for all conversations under this protocol


	@Override public int getMinArgs() { return 2; };
	@Override public int getMaxArgs() { return 7; };

	@Override
	public void checkArguments(Term[] args) throws JasonException{

		super.checkArguments(args);
		boolean result = false;

		String finalMsg = "";


		if (((Term)args[args.length-1]).isAtom()){result=true;}
		result = (result && (((Term)args[0]).isString()) );

		if  (protocolSteep.compareTo(Protocol_Template.START_STEP)==0)
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
			if (args.length>4){
				//the only value accepted is "end" if the argument is supplied
				result = (result&&(((Term)args[3]).isAtom()&&("end".compareTo(getTermAsString(args[3]))==0))   );
				if (!result){finalMsg = "If 4th argument is supplied it must have value: end";}

			}
		}

		if (!result)
		{
			finalMsg = finalMsg + " Parameters must be in correct format.";
			throw JasonException.createWrongArgument(this,finalMsg);
		}
	}

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {

		protocolSteep = getTermAsString(args[0]);

		checkArguments(args);

		agName  = ts.getUserAgArch().getAgName();

		ConvJasonAgent myag = ((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent();

		agentConversationID = getAtomAsString(args[args.length-1]);

		ts.getAg().getLogger().fine("CALLING INTERNAL ACTION WITH STEEP: '"+protocolSteep+"'"+" CID: "+agentConversationID);

		//the first state in the conversation
		if (protocolSteep.compareTo(Protocol_Template.START_STEP)==0){
			//TODO: It's necessary to document that this is the time for waiting for the inform
			timeOut = getTermAsInt(args[1]);

			//TODO: It's necessary to document that this is the deadLine for waiting for proposals. Also in the Jason code examples
			long deadLineTime = getTermAslong(args[2]);

			Term participants = args[3];

			Term proposal = args[4];

			int participantsNumber = 0;

			for (Term t: (ListTerm)participants) {
				String rec = ((Atom)t).toString();
				msg.addReceiver(new AgentID(rec));
				participantsNumber++;
			}

			msg.setContent(getTermAsString(proposal));
			msg.setProtocol("fipa-iterated-contract-net");
			String factName = getFactoryName(agentConversationID,"FICN",true);
			fcnp = new Jason_FICN_Initiator(agName, ts) ;

			String prevFactory = "";
			if (Protocol_Factory!=null)
				prevFactory = Protocol_Factory.getName();

			if (prevFactory.compareTo(factName)!=0) // if it is a new conversation a create a new one. This verification is not strictly 
				//necessary because it supposed that this condition will be always truth. This must be improved but, 
				//as the participants can not distinguish between conversation of the same factory also one factory per conversation 
				//is created with the initiator factory name as a filter. This implies one factory per conversation in the initiator too
			{
				Protocol_Factory = fcnp.newFactory(factName, null, 
						msg, 1, ((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent() ,
						participantsNumber, deadLineTime,timeOut);

				((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().addFactoryAsInitiator(Protocol_Factory);
			}

			/* finally the new conversation starts an asynchronous conversation.*/
			myag.lock();
			String ConvID = myag.newConvID();
			FICNConversation conv = new FICNConversation(agentConversationID,ConvID,"Starting new FCN conversation",
					((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().getAid(),factName); //the internal id is unknown yet
			ConvCProcessor processorTemplate = ((ConvCFactory)Protocol_Factory).cProcessorTemplate();
			processorTemplate.setConversation(conv);

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

				if (args.length>4){
					((FICNConversation)conversationsList.get(agentConversationID)).goOnIterating = false;
				}
				((FICNConversation)conversationsList.get(agentConversationID)).myAcceptances =getTermAsString(PropAccepted);
				((FICNConversation)conversationsList.get(agentConversationID)).myRejections =getTermAsString(PropRejected);
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