package es.upv.dsic.gti_ia.argAgents.knowledgeResources;

import java.io.Serializable;


/**
 * Implementation of the concept <i>Solution</i>
 * 
 */

public class Solution extends CaseComponent implements Serializable{
	
	private static final long serialVersionUID = -1151493950021017493L;
	private Conclusion conclusion;
	private String value;
	private int timesUsed;
	

	public Solution(Conclusion conclusion, String value, int timesUsed) {
		super();
		this.conclusion = conclusion;
		this.value = value;
		this.timesUsed = timesUsed;
	}


    public Solution() {
    	conclusion = new Conclusion();
    	value = "";
    	timesUsed = 0;
    }


    // Property hasConclusion

    public Conclusion getConclusion() {
        return (Conclusion) conclusion;
    }


    public void setConclusion(Conclusion newConclusion) {
        conclusion = newConclusion;
    }
    
    // Property promotesValue

    public String getPromotesValue(){
    	return value;
    }

    public void setPromotesValue(String newPromotesValue){
    	value = newPromotesValue;
    }
    
    public int getTimesUsed() {
		return timesUsed;
	}

	public void setTimesUsed(int timesUsed) {
		this.timesUsed = timesUsed;
	}
    
}
