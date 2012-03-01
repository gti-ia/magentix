package es.upv.dsic.gti_ia.argAgents.knowledgeResources;

import java.util.ArrayList;


/**
 * Implementation of the concept <i>ArgumentSolution</i>
 * 
 */
public class ArgumentSolution extends Solution {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1756923394620376629L;
	public enum ArgumentType {INDUCTIVE, PRESUMTIVE, MIXED};
	private ArgumentType argumentType;
	private AcceptabilityStatus acceptabilityStatus;
	private ArrayList<Premise> distPremises;
	private ArrayList<Premise> presumptions;
	private ArrayList<Premise> exceptions;
	
	private ArrayList<Long> counterExamplesArgCaseIDList;
	private ArrayList<Long> counterExamplesDomCaseIDList;
	
    public ArgumentSolution(ArgumentType argumentType, AcceptabilityStatus acceptabilityStatus,
    		ArrayList<Premise> distPremises, ArrayList<Premise> presumptions, ArrayList<Premise> exceptions,
    		ArrayList<Long> counterExamplesDomCaseID, ArrayList<Long> counterExamplesArgCaseID) {
        this.argumentType = argumentType;
        this.acceptabilityStatus = acceptabilityStatus;
        this.distPremises = distPremises;
        this.presumptions = presumptions;
        this.exceptions = exceptions;
        this.counterExamplesArgCaseIDList = counterExamplesArgCaseID;
        this.counterExamplesDomCaseIDList = counterExamplesDomCaseID;
    }


    public ArgumentSolution() {
    	argumentType = null;
    	acceptabilityStatus = AcceptabilityStatus.UNDECIDED;
    	distPremises = new ArrayList<Premise>();
    	presumptions = new ArrayList<Premise>();
    	exceptions = new ArrayList<Premise>();
    	counterExamplesArgCaseIDList = new ArrayList<Long>();
    	counterExamplesDomCaseIDList = new ArrayList<Long>();
    }


    // Property hasAcceptabilityState

    public AcceptabilityStatus getAcceptabilityState() {
        return acceptabilityStatus;
    }


    public void setAcceptabilityState(AcceptabilityStatus newAcceptabilityState) {
        acceptabilityStatus = newAcceptabilityState;
    }


    // Property hasArgumentType

    public ArgumentType getArgumentType() {
        return argumentType;
    }

    public void setArgumentType(ArgumentType newArgumentType) {
        argumentType = newArgumentType;
    }
    
    public String getArgumentTypeString(){
    	return argumentType.toString();
    }
    
    public void setArgumentTypeString(String newArgumentType){
    	try {
			argumentType = ArgumentType.valueOf(newArgumentType);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    // Property hasCounterExampleArgCaseIDList

    public ArrayList<Long> getCounterExamplesArgCaseIDList() {
        return counterExamplesArgCaseIDList;
    }


    public void addCounterExampleArgCaseID(Long newCounterExampleArgCaseID) {
        counterExamplesArgCaseIDList.add(newCounterExampleArgCaseID);
    }


    public void removeCounterExampleArgCaseID(Long oldCounterExampleArgCaseID) {
        counterExamplesArgCaseIDList.remove(oldCounterExampleArgCaseID);
    }


    public void setCounterExamplesArgCaseIDList(ArrayList<Long> newCounterExamplesArgCaseIDList) {
        counterExamplesArgCaseIDList = newCounterExamplesArgCaseIDList;
    }
    
    // Property hasCounterExampleDomCaseIDList

    public ArrayList<Long> getCounterExamplesDomCaseIDList() {
        return counterExamplesDomCaseIDList;
    }


    public void addCounterExampleDomCaseID(Long newCounterExampleDomCaseID) {
        counterExamplesDomCaseIDList.add(newCounterExampleDomCaseID);
    }


    public void removeCounterExampleDomCaseID(Long oldCounterExampleDomCaseID) {
        counterExamplesDomCaseIDList.remove(oldCounterExampleDomCaseID);
    }


    public void setCounterExamplesDomCaseIDList(ArrayList<Long> newCounterExamplesDomCaseIDList) {
        counterExamplesDomCaseIDList = newCounterExamplesDomCaseIDList;
    }


    // Property hasDistinguishingPremise

    public ArrayList<Premise> getDistinguishingPremises() {
        return distPremises;
    }


    public void addDistinguishingPremise(Premise newDistinguishingPremise) {
        distPremises.add(newDistinguishingPremise);
    }


    public void removeDistinguishingPremise(Premise oldDistinguishingPremise) {
        distPremises.remove(oldDistinguishingPremise);
    }


    public void setDistinguishingPremises(ArrayList<Premise> newDistinguishingPremises) {
        distPremises = newDistinguishingPremises;
    }


    // Property hasException

    public ArrayList<Premise> getExceptions() {
        return exceptions;
    }


    public void addException(Premise newException) {
        exceptions.add(newException);
    }


    public void removeException(Premise oldException) {
        exceptions.remove(oldException);
    }


    public void setExceptions(ArrayList<Premise> newExceptions) {
        exceptions = newExceptions;
    }

    // Property hasPresumption

    public ArrayList<Premise> getPresumptions() {
        return presumptions;
    }


    public void addPresumption(Premise newPresumption) {
        presumptions.add(newPresumption);
    }


    public void removePresumption(Premise oldPresumption) {
       presumptions.remove(oldPresumption);
    }


    public void setPresumptions(ArrayList<Premise> newPresumptions) {
       presumptions = newPresumptions;
    }

}
