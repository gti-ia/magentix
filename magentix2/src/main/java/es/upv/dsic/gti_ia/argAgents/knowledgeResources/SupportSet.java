package es.upv.dsic.gti_ia.argAgents.knowledgeResources;

import java.io.Serializable;
import java.util.*;


/**
 * Implementation of the owl concept <i>SupportSet</i>
 *
 */

public class SupportSet implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7765849287008916359L;
	private ArrayList<Premise> premises;
	private ArrayList<DomainCase> domainCases;
	private ArrayList<ArgumentCase> argumentCases;
	private ArrayList<ArgumentationScheme> schemes;
	private ArrayList<Premise> distPremises;
	private ArrayList<Premise> presumptions;
	private ArrayList<Premise> exceptions;
	//TODO not sure of doing this like that
	private ArrayList<DomainCase> counterExamplesDomCases;
	private ArrayList<ArgumentCase> counterExamplesArgCases;

    public SupportSet(ArrayList<Premise> premises, ArrayList<DomainCase> domainCases,
    		ArrayList<ArgumentCase> argumentCases, ArrayList<ArgumentationScheme> schemes,
    		ArrayList<Premise> distPremises, ArrayList<Premise> presumptions, ArrayList<Premise> exceptions,
    		ArrayList<DomainCase> counterExamplesDomCases, ArrayList<ArgumentCase> counterExamplesArgCases) {
        this.premises = premises;
        this.domainCases = domainCases;
        this.argumentCases = argumentCases;
        this.schemes = schemes;
        this.distPremises =  distPremises;
        this.presumptions = presumptions;
        this.exceptions = exceptions;
        this.counterExamplesDomCases = counterExamplesDomCases;
        this.counterExamplesArgCases = counterExamplesArgCases;
    }


    public SupportSet() {
    	premises = new ArrayList<Premise>();
    	domainCases = new ArrayList<DomainCase>();
    	argumentCases = new ArrayList<ArgumentCase>();
    	schemes = new ArrayList<ArgumentationScheme>();
    	distPremises = new ArrayList<Premise>();
    	presumptions = new ArrayList<Premise>();
    	exceptions = new ArrayList<Premise>();
    	counterExamplesDomCases = new ArrayList<DomainCase>();
    	counterExamplesArgCases = new ArrayList<ArgumentCase>();
    }

    // Property hasArgumentCase

    public ArrayList<ArgumentCase> getArgumentCases() {
        return argumentCases;
    }

    //public Iterator listHasArgumentCase() {
    //    return listPropertyValuesAs(getHasArgumentCaseProperty(), ArgumentCase.class);
    //}


    public void addArgumentCase(ArgumentCase newArgumentCase) {
        argumentCases.add(newArgumentCase);
    }


    public void removeArgumentCase(ArgumentCase oldArgumentCase) {
        argumentCases.remove(oldArgumentCase);
    }


    public void setArgumentCases(ArrayList<ArgumentCase> newArgumentCase) {
        argumentCases = newArgumentCase;
    }


    // Property hasArgumentationScheme

    public ArrayList<ArgumentationScheme> getArgumentationSchemes() {
        return schemes;
    }


    //public Iterator listHasArgumentationScheme() {
    //    return listPropertyValuesAs(getHasArgumentationSchemeProperty(), ArgumentationScheme.class);
    //}


    public void addArgumentationScheme(ArgumentationScheme newArgumentationScheme) {
        schemes.add(newArgumentationScheme);
    }


    public void removeArgumentationScheme(ArgumentationScheme oldArgumentationScheme) {
        schemes.remove(oldArgumentationScheme);
    }


    public void setArgumentationSchemes(ArrayList<ArgumentationScheme> newArgumentationSchemes) {
       schemes = newArgumentationSchemes;
    }



    // Property hasDistinguishingPremise

    public ArrayList<Premise> getDistinguishingPremises() {
        return distPremises;
    }


    //public Iterator listHasDistinguishingPremise() {
    //    return listPropertyValuesAs(getHasDistinguishingPremiseProperty(), Premise.class);
    //}


    public void addDistinguishingPremise(Premise newDistinguishingPremise) {
        distPremises.add(newDistinguishingPremise);
    }


    public void removeDistinguishingPremise(Premise oldDistinguishingPremise) {
        distPremises.remove(oldDistinguishingPremise);
    }


    public void setDistinguishingPremises(ArrayList<Premise> newDistinguishingPremises) {
        distPremises = newDistinguishingPremises;
    }


    // Property hasDomainCase

    public ArrayList<DomainCase> getDomainCases() {
        return domainCases;
    }


    //public Iterator listHasDomainCase() {
    //    return listPropertyValuesAs(getHasDomainCaseProperty(), DomainCase.class);
    //}


    public void addDomainCase(DomainCase newDomainCase) {
        domainCases.add(newDomainCase);
    }


    public void removeDomainCase(DomainCase oldDomainCase) {
        domainCases.remove(oldDomainCase);
    }


    public void setDomainCases(ArrayList<DomainCase> newDomainCases) {
        domainCases = newDomainCases;
    }


    // Property hasPremise

    public ArrayList<Premise> getPremises() {
        return premises;
    }

    //public Iterator listHasPremise() {
    //    return listPropertyValuesAs(getHasPremiseProperty(), Premise.class);
    //}


    public void addPremise(Premise newPremise) {
        premises.add(newPremise);
    }


    public void removePremise(Premise oldPremise) {
        premises.remove(oldPremise);
    }


    public void setPremises(ArrayList<Premise> newPremises) {
        premises = newPremises;
    }
    
    
    // Property hasPresumption

    public ArrayList<Premise> getPresumptions(){
    	return presumptions;
    }

    //Iterator listHasPresumption();

    public void addPresumption(Premise newPresumption){
    	presumptions.add(newPresumption);
    }

    public void removePresumption(Premise oldPresumption){
    	presumptions.remove(oldPresumption);
    }

    public void setPresumptions(ArrayList<Premise> newPresumptions){
    	presumptions = newPresumptions;
    }
    
    
    // Property hasException

    public ArrayList<Premise> getExceptions(){
    	return exceptions;
    }

    //Iterator listHasException();

    public void addException(Premise newException){
    	exceptions.add(newException);
    }

    public void removeException(Premise oldException){
    	exceptions.remove(oldException);
    }

    public void setExceptions(ArrayList<Premise> newExceptions){
    	exceptions = newExceptions;
    }


	public ArrayList<DomainCase> getCounterExamplesDomCases() {
		return counterExamplesDomCases;
	}

	public void addcounterExamplesDomCases(DomainCase newArgumentCase) {
		counterExamplesDomCases.add(newArgumentCase);
    }

    public void removecounterExamplesDomCases(DomainCase oldArgumentCase) {
    	counterExamplesDomCases.remove(oldArgumentCase);
    }
	
	public void setCounterExamplesDomCases(
			ArrayList<DomainCase> counterExamplesDomCases) {
		this.counterExamplesDomCases = counterExamplesDomCases;
	}


	public ArrayList<ArgumentCase> getCounterExamplesArgCases() {
		return counterExamplesArgCases;
	}

	public void addcounterExampleArgCase(ArgumentCase newArgumentCase) {
		counterExamplesArgCases.add(newArgumentCase);
    }


    public void removecounterExampleArgCase(ArgumentCase oldArgumentCase) {
    	counterExamplesArgCases.remove(oldArgumentCase);
    }

	public void setCounterExamplesArgCases(
			ArrayList<ArgumentCase> counterExamplesArgCases) {
		this.counterExamplesArgCases = counterExamplesArgCases;
	}
    
    public String toString(){
    	String str="";
    	if(this.premises!=null && !this.premises.isEmpty()){
    		str+="premises: ";
    		Iterator<Premise> iterPremises=this.premises.iterator();
    		while(iterPremises.hasNext()){
    			Premise p=iterPremises.next();
    			str+=p.getID()+"="+p.getContent()+" ";
    		}
    	}
    	if(this.distPremises!=null && !this.distPremises.isEmpty()){
    		str+="\ndistPremises: ";
    		Iterator<Premise> iterPremises=this.distPremises.iterator();
    		while(iterPremises.hasNext()){
    			Premise p=iterPremises.next();
    			str+=p.getID()+"="+p.getContent()+" ";
    		}
    	}
    	
    	if(this.counterExamplesArgCases!=null && !this.counterExamplesArgCases.isEmpty()){
    		str+="\nArgCases: ";
    		Iterator<ArgumentCase> iterArgCases=this.counterExamplesArgCases.iterator();
    		while(iterArgCases.hasNext()){
    			ArgumentCase argCase=iterArgCases.next();
    			str+="argCaseID="+argCase.getID()+" ";
    		}
    	}
    	return str;
    }

//  public void addCounterExample(Case newCounterExample) {
//      counterExamples.add(newCounterExample);
//  }
//
//
//  public void removeCounterExample(Case oldCounterExample) {
//      counterExamples.remove(oldCounterExample);
//  }

    
}
