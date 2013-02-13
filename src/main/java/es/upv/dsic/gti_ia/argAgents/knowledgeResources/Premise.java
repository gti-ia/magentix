package es.upv.dsic.gti_ia.argAgents.knowledgeResources;

import java.io.Serializable;

/**
 * Implementation of the concept <i>Premise</i>
 * 
 */

public class Premise implements Serializable, Comparable<Object>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 657825132227150938L;
	private int id;
	private String name;
	private String content;

    public Premise(int id, String name, String content) {
        this.id = id;
        this.name = name;
        this.content = content;
    }


    public Premise() {
    	id = -1;
    	name = "";
    	content = "";
    }

    // Property hasID
    
    public int getID() {
    	return id;
    }
    
    public void setID(int newID){
    	id = newID;
    }

    // Property hasContent

    public String getContent() {
        return content;
    }


    public void setContent(String newContent) {
        content = newContent;
    }


    // Property hasName

    public String getName() {
        return (String) name;
    }


    public void setName(String newName) {
        name = newName;
    }


	@Override
	public int compareTo(Object obj) {
		Premise otherPremise= (Premise) obj;
		return this.getID()-otherPremise.getID();
	}
}
