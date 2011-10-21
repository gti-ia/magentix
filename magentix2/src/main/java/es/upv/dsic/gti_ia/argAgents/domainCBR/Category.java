package es.upv.dsic.gti_ia.argAgents.domainCBR;

import java.io.Serializable;
import java.util.ArrayList;

public class Category implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6700752582115715275L;
	private int idTipi;
	private int parentID;
	private ArrayList<Question> questions;
	private String tipification;
	
	public Category(int idTipi, int parentID, ArrayList<Question> questions,
			String tipification) {
		this.idTipi = idTipi;
		this.parentID = parentID;
		this.questions = questions;
		this.tipification = tipification;
	}
	
	public Category(){
		this.idTipi = -1;
	}

	public int getIdTipi() {
		return idTipi;
	}

	public void setIdTipi(int idTipi) {
		this.idTipi = idTipi;
	}

	public int getParentID() {
		return parentID;
	}

	public void setParentID(int parentID) {
		this.parentID = parentID;
	}

	public ArrayList<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(ArrayList<Question> questions) {
		this.questions = questions;
	}
	
	public void addQuestion(Question newQuestion){
		this.questions.add(newQuestion);
	}
	
	public void removeQuestion(Question oldQuestion){
		this.questions.remove(oldQuestion);
	}

	public String getTipification() {
		return tipification;
	}

	public void setTipification(String tipification) {
		this.tipification = tipification;
	}
	
	
}
