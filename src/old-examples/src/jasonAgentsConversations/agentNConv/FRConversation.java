package jasonAgentsConversations.agentNConv;

import es.upv.dsic.gti_ia.core.AgentID;

public class FRConversation extends Conversation {
	public String frMessage = "";
	public String initialMessage = "";
	public String Participant = "";
	public FRConversation(String jasonID, String internalID, int TO, String Partic, String iniMsg,
			AgentID initiator) {
		super(jasonID, internalID, initiator);
		Participant = Partic;
		initialMessage = iniMsg;

	}
}
