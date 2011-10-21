package es.upv.dsic.gti_ia.argAgents.domainCBR;

import java.io.Serializable;
import java.util.HashMap;

public class Ticket implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1234924728986147965L;
	private int ticketID;
	private Category categoryNode;
	private HashMap<Integer,Attribute> attributes;
	private String problemDesc;
	private String project;
	private HashMap<String,Group> solvingGroups; 
	private HashMap<String,Operator> solvingOperators;
	private HashMap<Integer,Solution> solutions;
	
	public Ticket(int ticketID, Category categoryNode,
			HashMap<Integer, Attribute> attributes, String problemDesc,
			String project, HashMap<String, Group> solvingGroups, HashMap<String, Operator> solvingOperators,
			HashMap<Integer, Solution> solutions) {
		this.ticketID = ticketID;
		this.categoryNode = categoryNode;
		this.attributes = attributes;
		this.problemDesc = problemDesc;
		this.project = project;
		this.solvingGroups = solvingGroups;
		this.solvingOperators = solvingOperators;
		this.solutions = solutions;
	}
	
	public Ticket(){
		this.attributes=new HashMap<Integer, Attribute>();
		this.solutions=new HashMap<Integer, Solution>();
		this.solvingOperators=new HashMap<String, Operator>();
		this.solvingGroups=new HashMap<String, Group>();
	}

	public int getTicketID() {
		return ticketID;
	}

	public void setTicketID(int ticketID) {
		this.ticketID = ticketID;
	}

	public Category getCategoryNode() {
		return categoryNode;
	}

	public void setCategoryNode(Category categoryNode) {
		this.categoryNode = categoryNode;
	}

	public HashMap<Integer, Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(HashMap<Integer, Attribute> attributes) {
		this.attributes = attributes;
	}
	
	public void addAttribute(Attribute newAttribute){
		this.attributes.put(newAttribute.getAskedQuestion().getQuestionID(), newAttribute);
	}
	
	public void removeAttribute(Attribute oldAttribute){
		this.attributes.remove(oldAttribute.getAskedQuestion().getQuestionID());
	}

	public String getProblemDesc() {
		return problemDesc;
	}

	public void setProblemDesc(String problemDesc) {
		this.problemDesc = problemDesc;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public HashMap<String, Group> getSolvingGroups() {
		return solvingGroups;
	}

	public void setSolvingGroups(HashMap<String, Group> solvingGroups) {
		this.solvingGroups = solvingGroups;
	}
	
	public void addGroup(Group newGroup){
		if (this.solvingGroups.get(newGroup.getGroupID()) == null)
			this.solvingGroups.put(newGroup.getGroupID(), newGroup);
	}
	
	public void removeGroup(Group oldGroup){
		this.solvingGroups.remove(oldGroup.getGroupID());
	}

	public HashMap<String, Operator> getSolvingOperators() {
		return solvingOperators;
	}

	public void setSolvingOperators(HashMap<String, Operator> solvingOperators) {
		this.solvingOperators = solvingOperators;
	}
	
	public void addOperator(Operator newOperator){
		if (this.solvingOperators.get(newOperator.getOperatorID()) == null)
			this.solvingOperators.put(newOperator.getOperatorID(), newOperator);
	}
	
	public void removeOperator(Operator oldOperator){
		this.solvingOperators.remove(oldOperator.getOperatorID());
	}

	public HashMap<Integer, Solution> getSolutions() {
		return solutions;
	}

	public void setSolutions(HashMap<Integer, Solution> solutions) {
		this.solutions = solutions;
	}
	
	public void addSolution(Solution newSolution){
		if (this.solutions.get(newSolution.getSolutionID()) == null)
			this.solutions.put(newSolution.getSolutionID(), newSolution);
	}
	
	public void removeSolution(Solution oldSolution){
		this.solutions.remove(oldSolution.getSolutionID());
	}
	
	
}
