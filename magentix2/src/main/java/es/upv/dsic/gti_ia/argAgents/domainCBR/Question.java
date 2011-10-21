package es.upv.dsic.gti_ia.argAgents.domainCBR;

import java.io.Serializable;

public class Question implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3239079673257789101L;
	private int questionID;
	private String questionDesc;
	
	public Question(int questionID, String questionDesc) {
		this.questionID = questionID;
		this.questionDesc = questionDesc;
	}

	public Question(){
		
	}
	
	public int getQuestionID() {
		return questionID;
	}

	public void setQuestionID(int questionID) {
		this.questionID = questionID;
	}

	public String getQuestionDesc() {
		return questionDesc;
	}

	public void setQuestionDesc(String questionDesc) {
		this.questionDesc = questionDesc;
	}
	
	
	
}
