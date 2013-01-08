package jasonAgentsConversations.agentNConv;

import es.upv.dsic.gti_ia.core.AgentID;

public class FCNConversation extends Conversation{
	public String solicitude = "";
	public String myAcceptances = "";
	public String initialMessage = "";
	public String myRejections = "";
	public String kindOfAnswer = "";
	public String proposal = "";
	public String infoToSend = "";
	public boolean taskDone = false;
	
	public FCNConversation(String jasonID, String internalID, String iniMsg, AgentID initiator) {
		super(jasonID, internalID, initiator);
		initialMessage = iniMsg;
	}
}

