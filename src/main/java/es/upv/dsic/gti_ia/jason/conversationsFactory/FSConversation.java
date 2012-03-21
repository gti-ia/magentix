package es.upv.dsic.gti_ia.jason.conversationsFactory;


import java.util.HashMap;
import java.util.Map;

import es.upv.dsic.gti_ia.core.AgentID;

public class FSConversation extends Conversation{
	public String initialMessage = "";
	public Map<String, String> objects = new HashMap<String, String>();
	public AgentID Participant = null;
	public String firstResult = "none-actions";
	public String finalResult = "none-actions"; 
	public boolean conversationCanceled = false;
	public int performative ;
	public FSConversation(String jasonID, String internalID, AgentID Partic, String iniMsg,
			AgentID initiator) {
		super(jasonID, internalID, initiator);
		Participant = Partic;
		initialMessage = iniMsg;

	}
}
