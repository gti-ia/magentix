package es.upv.dsic.gti_ia.argAgents.knowledgeResources;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Position implements Comparable<Object>, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3807582996756957849L;
	/**
	 * 
	 */
	
	private String agentID;
	private String dialogueID;
	private Solution solution;
	private HashMap<Integer, Premise> premises;
	private ArrayList<DomainCase> domainCases;
	private float domainCaseSimilarity;
	private float argSuitabilityFactor;
	private float finalSuitability;
	private int timesAccepted;
	
	public Position(String agentID, String dialogueID, Solution solution, HashMap<Integer, Premise> premises, ArrayList<DomainCase> domainCases, float domainCaseSimilarity) {
		this.agentID = agentID;
		this.dialogueID = dialogueID;
		this.solution = solution;
		this.premises = premises;
		this.domainCases = domainCases;
		this.domainCaseSimilarity = domainCaseSimilarity;
		this.timesAccepted=0;
	}

	public String getAgentID() {
		return agentID;
	}

	public void setAgentID(String agentID) {
		this.agentID = agentID;
	}

	public String getDialogueID() {
		return dialogueID;
	}

	public void setDialogueID(String dialogueID) {
		this.dialogueID = dialogueID;
	}

	public Solution getSolution() {
		return solution;
	}

	public void setSolution(Solution solution) {
		this.solution = solution;
	}

	public HashMap<Integer, Premise> getPremises() {
		return premises;
	}

	public void setPremises(HashMap<Integer, Premise> premises) {
		this.premises = premises;
	}

	public ArrayList<DomainCase> getDomainCases() {
		return domainCases;
	}

	public void setDomainCases(ArrayList<DomainCase> domainCases) {
		this.domainCases = domainCases;
	}
	
	public void addDomainCase(DomainCase newCase){
		this.domainCases.add(newCase);
	}
	
	public void removeDomainCase(DomainCase oldCase){
		this.domainCases.remove(oldCase);
	}

	public float getDomainCaseSimilarity() {
		return domainCaseSimilarity;
	}

	public void setDomainCaseSimilarity(float domainCaseSimilarity) {
		this.domainCaseSimilarity = domainCaseSimilarity;
	}

	public float getArgSuitabilityFactor() {
		return argSuitabilityFactor;
	}

	public void setArgSuitabilityFactor(float argSuitabilityFactor) {
		this.argSuitabilityFactor = argSuitabilityFactor;
	}

	public float getFinalSuitability() {
		return finalSuitability;
	}

	public void setFinalSuitability(float finalSuitability) {
		this.finalSuitability = finalSuitability;
	}

	public int getTimesAccepted() {
		return timesAccepted;
	}
	
	public void increaseTimesAccepted(){
		this.timesAccepted++;
	}

	public void setTimesAccepted(int timesAccepted) {
		this.timesAccepted = timesAccepted;
	}

	public int compareTo(Object obj) {
		Position otherPosition = (Position) obj;
	    return Math.round(otherPosition.getFinalSuitability()*100000 - this.finalSuitability*100000);
	}
	
}
