package es.upv.dsic.gti_ia.argAgents.knowledgeResources;

import java.util.ArrayList;


/**
 * Implementation of the concept <i>ArgumentJustification</i>
 * 
 */

public class ArgumentJustification extends Justification {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6674061752344111469L;
	private ArrayList<Long> domainCasesIDs;
	private ArrayList<Long> argumentCasesIDs;
	private ArrayList<ArgumentationScheme> schemes;
	private ArrayList<DialogueGraph> dialogueGraphs;

    public ArgumentJustification(ArrayList<Long> domainCasesIDs, ArrayList<Long> argumentCasesIDs, ArrayList<ArgumentationScheme> schemes, ArrayList<DialogueGraph> dialogueGraphs){
    	this.domainCasesIDs = domainCasesIDs;
    	this.argumentCasesIDs = argumentCasesIDs;
    	this.schemes = schemes;
    	this.dialogueGraphs = dialogueGraphs;
    }


    public ArgumentJustification() {
    	domainCasesIDs = new ArrayList<Long>();
    	argumentCasesIDs = new ArrayList<Long>();
    	schemes = new ArrayList<ArgumentationScheme>();
    	dialogueGraphs = new ArrayList<DialogueGraph>();
    }


    // Property hasArgumentationScheme

    public ArrayList<ArgumentationScheme> getArgumentationSchemes() {
        return schemes;
    }


    public void addArgumentationScheme(ArgumentationScheme newArgumentationScheme) {
        schemes.add(newArgumentationScheme);
    }


    public void removeArgumentationScheme(ArgumentationScheme oldArgumentationScheme) {
        schemes.remove(oldArgumentationScheme);
    }


    public void setArgumentationSchemes(ArrayList<ArgumentationScheme> newArgumentationScheme) {
        schemes = newArgumentationScheme;
    }

    // Property hasDomainCaseID

    public ArrayList<Long> getDomainCasesIDs() {
        return domainCasesIDs;
    }

    public void addDomainCase(Long newDomainCase) {
    	domainCasesIDs.add(newDomainCase);
    }


    public void removeDomainCase(Long oldDomainCase) {
    	domainCasesIDs.remove(oldDomainCase);
    }


    public void setDomainCases(ArrayList<Long> newDomainCases) {
    	domainCasesIDs = newDomainCases;
    }

    // Property hasArgumentCaseID

    public ArrayList<Long> getArgumentCasesIDs() {
        return argumentCasesIDs;
    }


    public void addArgumentCase(Long newArgumentCase) {
    	argumentCasesIDs.add(newArgumentCase);
    }


    public void removeArgumentCase(Long oldArgumentCase) {
    	argumentCasesIDs.remove(oldArgumentCase);
    }


    public void setArgumentCases(ArrayList<Long> newArgumentCases) {
    	argumentCasesIDs = newArgumentCases;
    }

    // Property hasDialogueGraph

    public ArrayList<DialogueGraph> getDialogueGraphs() {
        return dialogueGraphs;
    }




    public void addDialogueGraph(DialogueGraph newDialogueGraph) {
        dialogueGraphs.add(newDialogueGraph);
    }


    public void removeDialogueGraph(DialogueGraph oldDialogueGraph) {
        dialogueGraphs.remove(oldDialogueGraph);
    }


    public void setDialogueGraphs(ArrayList<DialogueGraph> newDialogueGraphs) {
        dialogueGraphs = newDialogueGraphs;
    }
}
