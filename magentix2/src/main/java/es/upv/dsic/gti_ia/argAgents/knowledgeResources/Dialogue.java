package es.upv.dsic.gti_ia.argAgents.knowledgeResources;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Implementation of the concept <i>Dialogue</i>
 *
 */
public class Dialogue implements Serializable{
	
	private static final long serialVersionUID = 1225273417907906956L;
	private String dialogueID;
	private ArrayList<String> agentIDs;
	private Problem problem;
	
	public Dialogue(String dialogueID, ArrayList<String> agentIDs,
			Problem problem) {
		this.dialogueID = dialogueID;
		this.agentIDs = agentIDs;
		this.problem = problem;
	}

	public String getDialogueID() {
		return dialogueID;
	}

	public void setDialogueID(String dialogueID) {
		this.dialogueID = dialogueID;
	}

	/**
	 * Returns true if the agentID is added. If the agentID is in the list, returns false.
	 * @param agentID
	 * @return true if the agentID is added, otherwise, returns false.
	 */
	public boolean addAgentID(String agentID){
		if(this.agentIDs.contains(agentID))
			return false;
		
		this.agentIDs.add(agentID);
		return true;
	}
	
	/**
	 * Returns true if the agentID is removed, otherwise returns false.
	 * @param agentID
	 * @return true if the agentID is removed, otherwise returns false.
	 */
	public boolean removeAgentID(String agentID){
		if(this.agentIDs.contains(agentID)){
			this.agentIDs.remove(agentID);
			return true;
		}
		else
			return false;
		
	}
	
	public ArrayList<String> getAgentIDs() {
		return agentIDs;
	}

	public void setAgentIDs(ArrayList<String> agentIDs) {
		this.agentIDs = agentIDs;
	}

	public Problem getProblem() {
		return problem;
	}

	public void setProblem(Problem problem) {
		this.problem = problem;
	}
	
	
	
	
	
}
