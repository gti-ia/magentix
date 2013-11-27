package es.upv.dsic.gti_ia.jason.conversationsFactory;

import java.util.ArrayList;

import jason.asSyntax.Literal;
import jason.asSyntax.LiteralImpl;
import es.upv.dsic.gti_ia.core.AgentID;

/**
 * This class stores the main elements to be tracked of a conversation under a 
 * Fipa Recruiting Protocol
 * @author Bexy Alfonso Espinosa
 */

public class FRCConversation extends Conversation{
	public String initialMsg = "";
	public AgentID participant;
	public int participantsNumber = 0;
	public String conversationResult = "";
	public LiteralImpl MsgProxyContent = new LiteralImpl("");
	public boolean ProxyAcceptance = false; 
	public ArrayList<AgentID> TargetAgents = new ArrayList<AgentID>();
	public Literal Condition = new LiteralImpl("");
	public int TargetAgentsMaxNumber = 0;
	public String InfoToSend = "";
	public String FinalResult = "";
	public int timeOut ;

	public FRCConversation(String jasonID, String internalID, String iniMsg, int TO, AgentID part,
			AgentID initiatorAg, String factName) {
		super(jasonID, internalID, initiatorAg, factName);
		initialMsg = iniMsg;
		timeOut = TO;
		participant = part;
	}

}
