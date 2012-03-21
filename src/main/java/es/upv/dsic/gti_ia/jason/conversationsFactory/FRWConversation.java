package es.upv.dsic.gti_ia.jason.conversationsFactory;

import es.upv.dsic.gti_ia.core.AgentID;

public class FRWConversation extends Conversation {
	public String frMessage = "";
	public String initialMessage = "";
	public String Participant = "";
	public String finalResult = "";
	public FRWConversation(String jasonID, String internalID, int TO, String Partic, String iniMsg,
			AgentID initiator) {
		super(jasonID, internalID, initiator);
		Participant = Partic;
		initialMessage = iniMsg;

	}
}