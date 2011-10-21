package es.upv.dsic.gti_ia.argAgents.domainCBR;


public class SimilarCase implements Comparable<Object>{
	private Case caseb;
	private Float similarity;
	
	public SimilarCase(Case caseb, Float similarity) {
		this.caseb = caseb;
		this.similarity = similarity;
	}

	public Case getCase() {
		return caseb;
	}

	public void setCase(Case caseb) {
		this.caseb = caseb;
	}

	public Float getSimilarity() {
		return similarity;
	}

	public void setSimilarity(Float similarity) {
		this.similarity = similarity;
	}

	
	public int compareTo(Object obj) {
		SimilarCase otherSimilarCase = (SimilarCase) obj;
	    return Math.round(otherSimilarCase.getSimilarity()*100000 - this.similarity*100000);
	}
	
	
}
