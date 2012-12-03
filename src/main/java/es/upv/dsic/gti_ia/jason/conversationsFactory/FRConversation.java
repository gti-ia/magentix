package es.upv.dsic.gti_ia.jason.conversationsFactory;

import es.upv.dsic.gti_ia.core.AgentID;

/**
 * This class stores the main elements to be tracked of a conversation under a 
 * Fipa Request Protocol
 * @author Bexy Alfonso Espinosa
 */


public class FRConversation extends Conversation {
	public String frMessage = "";
	public String frData = "";
	public String initialMessage = "";
	public String Participant = "";
	public String TaskResult = "" ;
	public String TaskDecision = "";
	public String RequestResult = "";
	public String FinalResult = "";
	public String Task = "" ;
	
	public FRConversation(String jasonID, String internalID, int TO, String Partic, String iniMsg,
			AgentID initiator) {
		super(jasonID, internalID, initiator);
		Participant = Partic;
		initialMessage = iniMsg;

	}
}
