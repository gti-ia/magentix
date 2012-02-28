package es.upv.dsic.gti_ia.argAgents.knowledgeResources;

/**
 * Implementation of the concept <i>Norm</i>
 * In this version of the API, Norms are not considered, 
 * but this class can be used to include normative knowledge in further implementations.
 */

public class Norm {
	
	
	private long id;
	private String description;

    public Norm(long id, String description) {
        this.id = id;
        this.description = description;
    }

    public Norm() {
    	id = -1;
    	description = "";
    }

    // Property hasDescription

    public String getDescription() {
        return description;
    }


    public void setDescription(String newDescription) {
        description = newDescription;
    }


    // Property hasID

    public long getID() {
        return id;
    }


    public void setID(long newID) {
        id = newID;
    }
}
