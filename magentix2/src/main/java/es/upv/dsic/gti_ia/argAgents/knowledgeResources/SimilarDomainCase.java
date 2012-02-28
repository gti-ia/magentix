package es.upv.dsic.gti_ia.argAgents.knowledgeResources;

/**
 * Structure to store the degree of similarity of a domain-case
 *
 */
public class SimilarDomainCase implements Comparable<Object>{
	private DomainCase caseb;
	private Float similarity;
	
	public SimilarDomainCase(DomainCase caseb, Float similarity) {
		this.caseb = caseb;
		this.similarity = similarity;
	}

	public DomainCase getCaseb() {
		return caseb;
	}

	public void setCaseb(DomainCase caseb) {
		this.caseb = caseb;
	}

	public Float getSimilarity() {
		return similarity;
	}

	public void setSimilarity(Float similarity) {
		this.similarity = similarity;
	}
	
	public int compareTo(Object obj) {
		SimilarDomainCase otherSimilarDomainCase = (SimilarDomainCase) obj;
	    return Math.round(otherSimilarDomainCase.getSimilarity()*100000 - this.similarity*100000);
	}
	
	
}
