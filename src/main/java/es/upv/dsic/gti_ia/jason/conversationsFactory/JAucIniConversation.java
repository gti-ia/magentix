package es.upv.dsic.gti_ia.jason.conversationsFactory;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import es.upv.dsic.gti_ia.core.AgentID;

/**
 * This class stores the main elements to be tracked of a conversation under a 
 * Japanese Auction Protocol from the initiator perspective
 * @author Bexy Alfonso Espinosa
 */

public class JAucIniConversation extends Conversation{

	public String initialMessage = "";
	public Hashtable<AgentID, Integer> PartParticipations = new Hashtable<AgentID, Integer>();
	public List<AgentID> AcceptancesReceivedInCurrLevel = new ArrayList<AgentID>();
	public List<AgentID> ActiveParticipants = new ArrayList<AgentID>();

	public int AuctionLevel = 0;
	public double initialBid = 0;
	public double NextBid = 0;
	public double Increment = 1;
	public String finalResult = "none-actions"; 
	public AgentID winner = null;
	public int MaxIterations ;
	public String request = "";

	public JAucIniConversation(String jasonID, String internalID,
			AgentID initiatorAg, List<AgentID> participants, String iniMsg, 
			double iniBid, double inc, int maxIterNumber, String factName) {
		super(jasonID, internalID, initiatorAg, factName);
		Iterator<AgentID> it = participants.iterator();
		while (it.hasNext())
		{	
			AgentID current = it.next();
			PartParticipations.put(current, 0);
			ActiveParticipants.add(current);
		}
		initialMessage = iniMsg;
		initialBid = iniBid;
		NextBid = iniBid;
		Increment = inc;
		MaxIterations = maxIterNumber;
	}

	public void updateActiveParticipants() {
		Iterator<AgentID> it = PartParticipations.keySet().iterator();
		ActiveParticipants.clear();
		while (it.hasNext()){
			AgentID current = it.next();
			if (PartParticipations.get(current)==AuctionLevel)
				ActiveParticipants.add(current);
		}

	}

	public AgentID getParticipant(String Name){
		Iterator<AgentID> it = PartParticipations.keySet().iterator();
		AgentID result = null;
		while (it.hasNext()&&(result==null)){
			AgentID current = it.next();
			if (current.name.compareTo(Name)==0)
				result = current;
		}
		return result;
	}

	public int getParticipation(String Name){
		Iterator<AgentID> it = PartParticipations.keySet().iterator();
		int result = -1;
		while (it.hasNext()&&(result==-1)){
			AgentID current = it.next();
			if (current.name.compareTo(Name)==0)
				result = PartParticipations.get(current);
		}
		return result;
	}

	public AgentID setParticipations(String Name, int partic){
		Iterator<AgentID> it = PartParticipations.keySet().iterator();
		AgentID result = null;
		while (it.hasNext()&&(result==null)){
			AgentID current = it.next();
			if (current.name.compareTo(Name)==0)
				PartParticipations.put(current, partic);
		}
		return result;
	}

	public boolean hasAcceptedInCurrLevel(String Name){
		Iterator<AgentID> it = AcceptancesReceivedInCurrLevel.iterator();
		boolean result = false;
		while (it.hasNext()&&(!result)){
			AgentID currentAcc = it.next();
			if (currentAcc.name.compareTo(Name)==0)
				result = true;
		}
		return result;
	}

	public boolean allActiveAcceptancesReceived(){
		boolean result = true;
		Iterator<AgentID> it = ActiveParticipants.iterator();
		while (it.hasNext()){
			AgentID current = it.next();
			result = result && (hasAcceptedInCurrLevel(current.name));
		}
		return result;
	}
}
