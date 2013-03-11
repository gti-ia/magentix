package es.upv.dsic.gti_ia.jason.conversationsFactory;

import es.upv.dsic.gti_ia.core.AgentID;

/**
 * This class stores the main elements to be tracked of a conversation under a 
 * Fipa Request When Protocol
 * @author Bexy Alfonso Espinosa
 */


public class FRWConversation extends Conversation {
	public String frMessage = "";
	public String initialMessage = "";
	public String Participant = "";
	public String finalResult = "";
	public FRWConversation(String jasonID, String internalID, int TO, String Partic, String iniMsg,
			AgentID initiator, String factName) {
		super(jasonID, internalID, initiator, factName);
		Participant = Partic;
		initialMessage = iniMsg;
	}
}