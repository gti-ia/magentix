package es.upv.dsic.gti_ia.argAgents.domainCBR;

import java.io.Serializable;

public class Operator implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2631753770349249675L;
	private String operatorID;

	public Operator(String operatorID) {
		this.operatorID = operatorID;
	}
	
	public Operator(){
		
	}

	public String getOperatorID() {
		return operatorID;
	}

	public void setOperatorID(String operatorID) {
		this.operatorID = operatorID;
	}
	
	
}
