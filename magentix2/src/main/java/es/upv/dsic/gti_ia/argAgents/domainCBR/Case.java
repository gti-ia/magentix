package es.upv.dsic.gti_ia.argAgents.domainCBR;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Case implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4063185537520728139L;
	private Category categoryNode;
	private HashMap<Integer,Attribute> attributes;
	private String problemDesc;
	private String project;
	private HashMap<String,Group> solvingGroups;
	private HashMap<String,Operator> solvingOperators;
	private HashMap<Integer,Solution> solutions;
	
	
	public Case(Category categoryNode, HashMap<Integer, Attribute> attributes,
			String problemDesc, String project,
			HashMap<String, Group> solvingGroups,
			HashMap<String, Operator> solvingOperators,
			HashMap<Integer, Solution> solutions) {
		this.categoryNode = categoryNode;
		this.attributes = attributes;
		this.problemDesc = problemDesc;
		this.project = project;
		this.solvingGroups = solvingGroups;
		this.solvingOperators = solvingOperators;
		this.solutions = solutions;
	}
	
	public Case(){
		this.attributes=new HashMap<Integer, Attribute>();
		this.solutions=new HashMap<Integer, Solution>();
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
	public HashMap<String, Operator> getSolvingOperators() {
		return solvingOperators;
	}
	public void setSolvingOperators(HashMap<String, Operator> solvingOperators) {
		this.solvingOperators = solvingOperators;
	}
	public HashMap<Integer, Solution> getSolutions() {
		return solutions;
	}
	public void setSolutions(HashMap<Integer, Solution> solutions) {
		this.solutions = solutions;
	}
	
	public void printCase(String destFile){
		System.out.println("****************************************************************************************");
		System.out.println(destFile + " Category Node: " + this.getCategoryNode().getIdTipi());
			System.out.println("\t parentID: " + this.getCategoryNode().getParentID());
			System.out.println("\t tipification: " + this.getCategoryNode().getTipification());
		Iterator<Attribute> itAt = attributes.values().iterator();
 		while (itAt.hasNext()){
 			Attribute at = itAt.next();
 			System.out.println(destFile + " Attribute [QuestionID]: " + at.getAskedQuestion().getQuestionID());
 				System.out.println("\t Question Description: " + at.getAskedQuestion().getQuestionDesc());
 				System.out.println("\t Answer: " + at.getAnswer());
 		}
 		System.out.println(destFile + " Problem Description: " + this.getProblemDesc());
 		System.out.println(destFile + " Project: " + this.getProject());
 		Iterator<Group> itGr = solvingGroups.values().iterator();
 		while (itGr.hasNext()){
 			Group gr = itGr.next();
 			System.out.println(destFile + " Group: " + gr.getGroupID());
 			ArrayList<Operator> opGr = gr.getMembers();
 			for (Operator m : opGr){
 				System.out.println("\t Member: " + m.getOperatorID());
 			}
 		}
 		Iterator<Operator> itOp = solvingOperators.values().iterator();
 		while (itOp.hasNext()){
 			Operator op = itOp.next();
 			System.out.println(destFile + " Operator: " + op.getOperatorID());
 		}
 		Iterator<Solution> itSol = solutions.values().iterator();
 		while (itSol.hasNext()){
 			Solution sol = itSol.next();
 			System.out.println(destFile + " Solution: " + sol.getSolutionID());
 				System.out.println("\t Solution Description: " + sol.getSolutionDesc());
 				System.out.println("\t Promoted Value: " + sol.getPromotedValue());
 				System.out.println("\t Times Used: " + sol.getTimesUsed());
 		}
 		System.out.println("****************************************************************************************");
	}
	
	
}
