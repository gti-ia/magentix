package es.upv.dsic.gti_ia.argAgents.domainCBR;

import java.io.Serializable;

public class Attribute implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7043827367408557865L;
	private Question askedQuestion;
	private String answer;
	
	public Attribute(Question askedQuestion, String answer) {
		this.askedQuestion = askedQuestion;
		this.answer = answer;
	}
	
	public Attribute(){
		
	}

	public Question getAskedQuestion() {
		return askedQuestion;
	}

	public void setAskedQuestion(Question askedQuestion) {
		this.askedQuestion = askedQuestion;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}
	
	
}
