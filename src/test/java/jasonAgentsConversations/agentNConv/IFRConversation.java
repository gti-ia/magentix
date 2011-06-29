package jasonAgentsConversations.agentNConv;

public class IFRConversation extends Conversation {
	
	public String frMessage = "";
	public String initialMessage = "";
	public String Participant = "";
	public IFRConversation(String jasonID, String internalID, int TO, String Partic, String iniMsg) {
		super(jasonID, internalID);
		Participant = Partic;
		initialMessage = iniMsg;

	}

}

