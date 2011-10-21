package es.upv.dsic.gti_ia.argAgents.domainCBR;

import java.io.Serializable;

public class Solution implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8031394783648586637L;
	private int solutionID;
	private String solutionDesc;
	private int timesUsed;
	private String promotedValue;
	
	

	public Solution(int solutionID, String solutionDesc, int timesUsed,
			String promotedValue) {
		this.solutionID = solutionID;
		this.solutionDesc = solutionDesc;
		this.timesUsed = timesUsed;
		this.promotedValue = promotedValue;
	}

	public Solution(){
		this.solutionID = -1;
		this.timesUsed = 0;
	}
	
	public int getSolutionID() {
		return solutionID;
	}

	public void setSolutionID(int solutionID) {
		this.solutionID = solutionID;
	}

	public String getSolutionDesc() {
		return solutionDesc;
	}

	public void setSolutionDesc(String solutionDesc) {
		this.solutionDesc = solutionDesc;
	}

	public int getTimesUsed() {
		return timesUsed;
	}

	public void setTimesUsed(int timesUsed) {
		this.timesUsed = timesUsed;
	}

	public String getPromotedValue() {
		return promotedValue;
	}

	public void setPromotedValue(String promotedValue) {
		this.promotedValue = promotedValue;
	}
	
	

	
	
}
