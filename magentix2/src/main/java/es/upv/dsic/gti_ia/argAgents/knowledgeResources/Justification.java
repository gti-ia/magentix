package es.upv.dsic.gti_ia.argAgents.knowledgeResources;

import java.io.Serializable;


/**
 * Implementation of the owl concept <i>Justification</i>
 */

public class Justification extends CaseComponent implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 452106433940723360L;
	private String description;

    public Justification(String description) {
        this.description = description;
    }


    public Justification() {
    	description = "";
    }

    // Property hasDescription

    public String getDescription() {
        return description;
    }

    public void setDescription(String newDescription) {
        description = newDescription;
    }
}
