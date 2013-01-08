package jasonAgentsConversations.agentNConv;

import java.util.ArrayList;

import jason.asSyntax.Literal;
import jason.asSyntax.LiteralImpl;
import es.upv.dsic.gti_ia.core.AgentID;


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
			AgentID initiatorAg) {
		super(jasonID, internalID, initiatorAg);
		initialMsg = iniMsg;
		timeOut = TO;
		participant = part;
	}

}
