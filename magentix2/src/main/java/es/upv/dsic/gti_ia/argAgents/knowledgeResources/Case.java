package es.upv.dsic.gti_ia.argAgents.knowledgeResources;

import java.io.Serializable;


/**
 * Implementation of the owl concept <i>Case</i>
 * 
 */
public class Case implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2931675447194795996L;
	private long id;
	private String creationDate;

    public Case(long id, String creationDate) {
        this.id = id;
        this.creationDate = creationDate;
    }


    public Case() {
    	id = -1;
    	creationDate = "";
    }

    // Property creationDate

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String newCreationDate) {
        creationDate = newCreationDate;
    }

    // Property hasID

    public long getID() {
        return id;
    }

    public void setID(long newID) {
        id = newID;
    }
}
