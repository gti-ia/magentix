package es.upv.dsic.gti_ia.argAgents.knowledgeResources;

import java.io.Serializable;
import java.util.*;


/**
 * Implementation of the concept <i>DomainContext</i>
 * 
 */
public class DomainContext extends Context implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1942913126620854662L;
	private HashMap<Integer,Premise> premises;

    public DomainContext(HashMap<Integer, Premise> premises) {
		this.premises = premises;
	}

    public DomainContext() {
    	premises = new HashMap<Integer,Premise>();
    }

    // Property hasPremise

    public HashMap<Integer,Premise> getPremises() {
        return premises;
    }


    public void addPremise(Premise newPremise) {
        premises.put(newPremise.getID(), newPremise);
    }


    public void removePremise(Premise oldPremise) {
        premises.remove(oldPremise.getID());
    }


    public void setPremises(HashMap<Integer,Premise> newPremises) {
        premises = newPremises;
    }
    
    public void setPremisesFromList(ArrayList<Premise> newPremises) {
    	for (Premise pre : newPremises) {
    		premises.put(pre.getID(), pre);
    	}
    }
}
