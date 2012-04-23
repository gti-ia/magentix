package es.upv.dsic.gti_ia.argAgents.knowledgeResources;

import java.io.Serializable;
import java.util.*;


/**
 * Implementation of the concept <i>ValueNode</i>
 * 
 */
public class ValPref implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6686626334547834816L;
	private ArrayList<String> values;

    public ValPref(ArrayList<String> values) {
        this.values = values;
    }


    public ValPref() {
    	values = new ArrayList<String>();
    }


    public String getPreferred() {
    	try{
    		return (String) values.get(0);
    	}catch(Exception e){
    		
    	}
    	return null;
    }


    // Property hasValues

    public ArrayList<String> getValues() {
        return values;
    }
    
    public void setValues(ArrayList<String> values) {
    	this.values = values;
    }



    public void addValue(String newValue) {
        values.add(newValue);
    }


    public void removeValue(String oldValue) {
        values.remove(oldValue);
    }

}
