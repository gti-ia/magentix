package jasonAgentsConversations.agentNConv;

import jason.asSyntax.Literal;
import jason.asSyntax.LiteralImpl;
import es.upv.dsic.gti_ia.core.AgentID;

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
			AgentID initiator) {
		super(jasonID, internalID, initiator);
		Participant = Partic;
		initialMessage = iniMsg;

	}
}