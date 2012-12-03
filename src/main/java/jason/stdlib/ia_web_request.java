package jason.stdlib;

import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvJasonAgent;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvMagentixAgArch;
import es.upv.dsic.gti_ia.jason.conversationsFactory.Conversation;
import es.upv.dsic.gti_ia.jason.conversationsFactory.Protocol_Template;
import es.upv.dsic.gti_ia.jason.conversationsFactory.protocolInternalAction;
import es.upv.dsic.gti_ia.core.MessageFilter;
import mWaterWeb.webInterface.WebRequestConversation;
import mWaterWeb.webInterface.webCommParticipantTemplate;

import jason.JasonException;
import jason.asSyntax.Term;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;

/**
 * This class represents the internal action to be used by the agent whose function
 * is to be intermediary between the web interface and the MAS in the
 * mWater prototype
 * @author Bexy Alfonso Espinosa
 */

public class ia_web_request extends protocolInternalAction{
	private static final long serialVersionUID = 1L;

 	webCommParticipantTemplate webcomm = null;


	@Override public int getMinArgs() { return 1; };
	@Override public int getMaxArgs() { return 4; };

	@Override
	public void checkArguments(Term[] args) throws JasonException
	{
		super.checkArguments(args);
		boolean result = false;

		if (((Term)args[0]).isString()) {result = true;};

		if (protocolSteep.compareTo(Protocol_Template.INFORM_STEP)==0)
		{
			int cont = 0; 
			for (Term t:args){
				switch (cont){
				case 1:result = (result&&t.isString()); //purpose
				break;
				case 2:result = (result&&t.isLiteral()||result&&t.isString()); //conversation result
				break;
				case 3:result =((result&&t.isAtom())||
								(result&&t.isString())||
								(result&&t.isLiteral())||
								(result&&t.isNumeric())); //conversation id
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

		agName  = ts.getUserAgArch().getAgName();

		ConvJasonAgent myag = ((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent();
		
		if (ts.getSettings().verbose()>1)
			ts.getAg().getLogger().info("CALLING INTERNAL ACTION WITH STEEP: '"+protocolSteep+"'"+" CID: "+agentConversationID); 
		
		if (protocolSteep.compareTo(Protocol_Template.JOIN_STEP)==0){
			
			if (webcomm == null){
				if (args.length >2)
				{					
					timeOut = getTermAsInt(args[1]); //default it 3000
				}

				webcomm = new webCommParticipantTemplate( ts);
				MessageFilter filter = new MessageFilter("performative = REQUEST AND protocol = web");
				String factName = "WEBFACTORY";
				Protocol_Factory = webcomm.newFactory(factName, filter,1, 
						((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent());
				System.out.println(agName+" Entrando...");
				((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent().addFactoryAsParticipant(Protocol_Factory);
			
			}
			
		}
		else
			if (protocolSteep.compareTo(Protocol_Template.INFORM_STEP)==0){
				//Removing conversation from pending conversations list and adding it to the 
				//list of conversations of the participant agent
				agentConversationID = getTermAsString(args[args.length-1]);
				Conversation conv = Protocol_Factory.removeConversationByJasonID(agentConversationID);
				conversationsList.put(agentConversationID, conv);

				WebRequestConversation myConv = (WebRequestConversation) conversationsList.get(agentConversationID);

				myConv.fillAccreditationResult(getTermAsString(args[1]), getTermAsLiteral(args[2]));
				
				myConv.release_semaphore();
			}

		return true;
	}
}