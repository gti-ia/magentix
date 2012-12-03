package es.upv.dsic.gti_ia.jason.conversationsFactory;


import es.upv.dsic.gti_ia.core.AgentID;

/**
 * This class stores the main elements to be tracked of a conversation under a 
 * Japanese Auction Protocol from the participant perspective
 * @author Bexy Alfonso Espinosa
 */

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
