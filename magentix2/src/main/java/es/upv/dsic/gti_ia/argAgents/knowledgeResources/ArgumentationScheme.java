package es.upv.dsic.gti_ia.argAgents.knowledgeResources;

import java.util.ArrayList;


/**
 * Implementation of the interface <i>ArgumentationScheme</i>
 * @see {@link es.upv.dsic.gti_ia.argAgents.knowledgeResources.ArgumentationScheme}
 */
public class ArgumentationScheme {
	
	private long id;
	private String argTitle;
	private String creationDate;
	private Author author;
	private Conclusion conclusion;
	private ArrayList<Premise> premises;
	private ArrayList<Premise> presumptions;
	private ArrayList<Premise> exceptions;

    public ArgumentationScheme(long id, String argTitle, String creationDate, 
    		Author author, Conclusion conclusion, ArrayList<Premise> premises, 
    		ArrayList<Premise> presumptions, ArrayList<Premise> exceptions){
    	this.id = id;
    	this.argTitle = argTitle;
    	this.creationDate = creationDate;
    	this.author = author;
    	this.premises = premises;
    	this.presumptions = presumptions;
    	this.exceptions = exceptions;
    }


    public ArgumentationScheme() {
    	id = -1;
    	argTitle = "";
    	creationDate = "";
    	author = new Author();
    	premises = new ArrayList<Premise>();
    	presumptions = new ArrayList<Premise>();
    	exceptions = new ArrayList<Premise>();
    }


    // Property argTitle

    public String getArgTitle() {
        return (String) argTitle;
    }

    public void setArgTitle(String newArgTitle) {
        argTitle = newArgTitle;
    }

    // Property creationDate

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String newCreationDate) {
        this.creationDate = newCreationDate;
    }

    // Property hasAuthor

    public Author getAuthor() {
        return (Author) author;
    }


    public void setAuthor(Author newAuthor) {
        author = newAuthor;
    }

    // Property hasConclusion

    public Conclusion getConclusion() {
        return (Conclusion) conclusion;
    }

  
    public void setConclusion(Conclusion newConclusion) {
        conclusion = newConclusion;
    }


    // Property hasException

    public ArrayList<Premise> getExceptions() {
        return exceptions;
    }

    //public Iterator listHasException() {
    //    return listPropertyValuesAs(getHasExceptionProperty(), Premise.class);
    //}


    public void addException(Premise newException) {
        exceptions.add(newException);
    }


    public void removeException(Premise oldException) {
        exceptions.remove(oldException);
    }


    public void setExceptions(ArrayList<Premise> newExceptions) {
        exceptions = newExceptions;
    }

    // Property hasID

    public long getID() {
        return (long) id;
    }

    public void setID(long newID) {
        id = newID;
    }


    // Property hasPremise

    public ArrayList<Premise> getPremises() {
        return premises;
    }

   // public Iterator listHasPremise() {
   //     return listPropertyValuesAs(getHasPremiseProperty(), Premise.class);
   // }


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

    public ArrayList<Premise> getPresumptions() {
        return presumptions;
    }

    //public Iterator listHasPresumption() {
    //    return listPropertyValuesAs(getHasPresumptionProperty(), Premise.class);
    //}


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
