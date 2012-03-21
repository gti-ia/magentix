package es.upv.dsic.gti_ia.jason.conversationsFactory;


import es.upv.dsic.gti_ia.core.AgentID;

public class JAucPartConversation extends Conversation{

	public String AuctionLevel = "0";
	public String initialMessage = "";
	public boolean Accept = false;

	public JAucPartConversation(String jasonID, String internalID,
			AgentID initiatorAg, String iniMsg) {
		super(jasonID, internalID, initiatorAg);

		initialMessage = iniMsg;

	}

}
