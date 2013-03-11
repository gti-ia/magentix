package es.upv.dsic.gti_ia.jason.conversationsFactory;

import jason.asSyntax.Literal;
import jason.asSyntax.LiteralImpl;
import es.upv.dsic.gti_ia.core.AgentID;

/**
 * This class stores the main elements to be tracked of a conversation under a 
 * Fipa Query If/Ref Protocol
 * @author Bexy Alfonso Espinosa
 */

public class FQConversation extends Conversation {

	public String initialMessage = "";
	public Literal query = new LiteralImpl("");
	public String Participant = "";
	public String result = "";


	/**
	 * This field will have 0 or 1 if the performative 
	 * is IF or any result on the other hand
	 * */
	public String evaluationResult = ""; 

	public int performative ;
	public FQConversation(String jasonID, String internalID, String Partic, String iniMsg,
			AgentID initiator, String factName) {
		super(jasonID, internalID, initiator, factName);
		Participant = Partic;
		initialMessage = iniMsg;

	}
}