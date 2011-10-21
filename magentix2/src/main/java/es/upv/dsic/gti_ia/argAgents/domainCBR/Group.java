package es.upv.dsic.gti_ia.argAgents.domainCBR;

import java.io.Serializable;
import java.util.ArrayList;

public class Group implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8446626668318342703L;
	private String groupID;
	private ArrayList<Operator> members;
	
	public Group(String groupID, ArrayList<Operator> members) {
		this.groupID = groupID;
		this.members = members;
	}
	
	public Group(){
		
	}

	public String getGroupID() {
		return groupID;
	}

	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}

	public ArrayList<Operator> getMembers() {
		return members;
	}

	public void setMembers(ArrayList<Operator> members) {
		this.members = members;
	}
	
	
}
