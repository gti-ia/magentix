package es.upv.dsic.gti_ia.argAgents.knowledgeResources;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Implementation of the concept <i>ArgNode</i>
 * @author Stella Heras
 *
 */

public class ArgNode implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6054414917369003936L;
	private long argCaseID;
	private ArrayList<Long> childArgCaseIDList;
	private long parentArgCaseID;
	public enum NodeType {FIRST, LAST, NODE, AGREE};
	private NodeType nodeType;
	
	
	public ArgNode(long argCaseID, ArrayList<Long> childArgCaseIDList,
			long parentArgCaseID, NodeType nodeType) {
		super();
		this.argCaseID = argCaseID;
		this.childArgCaseIDList = childArgCaseIDList;
		this.parentArgCaseID = parentArgCaseID;
		this.nodeType = nodeType;
	}

	public ArgNode() {
	}
	
	public long getArgCaseID() {
		return argCaseID;
	}
	public void setArgCaseID(long argCaseID) {
		this.argCaseID = argCaseID;
	}
	public ArrayList<Long> getChildArgCaseIDList() {
		return childArgCaseIDList;
	}
	public void setChildArgCaseIDList(ArrayList<Long> childArgCaseIDList) {
		this.childArgCaseIDList = childArgCaseIDList;
	}
	public void addChildArgCaseID(Long id){
		childArgCaseIDList.add(id);
	}
	
	public void deleteChildArgCaseID(Long id){
		childArgCaseIDList.remove(id);
	}
	
	public long getParentArgCaseID() {
		return parentArgCaseID;
	}
	public void setParentArgCaseID(long parentArgCaseID) {
		this.parentArgCaseID = parentArgCaseID;
	}
	public NodeType getNodeType() {
		return nodeType;
	}
	public void setNodeType(NodeType nodeType) {
		this.nodeType = nodeType;
	}
	
}
