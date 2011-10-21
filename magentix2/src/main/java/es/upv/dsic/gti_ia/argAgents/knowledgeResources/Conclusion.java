package es.upv.dsic.gti_ia.argAgents.knowledgeResources;

import java.io.Serializable;


/**
 * Implementation of the owl concept <i>Conclusion</i>
 * 
 */
public class Conclusion implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1766786263941771604L;
	private long id;
	private String description;

  

    public Conclusion(long id, String description) {
		this.id = id;
		this.description = description;
	}



	public Conclusion() {
    	id=-1;
    	description = "";
    }



    // Property hasDescription

    public String getDescription() {
        return description;
    }

    public void setDescription(String newDescription) {
        description = newDescription;
    }



	public long getID() {
		return id;
	}



	public void setID(long id) {
		this.id = id;
	}
    
    
}
