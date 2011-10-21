package es.upv.dsic.gti_ia.argAgents.knowledgeResources;

public class SimilarArgumentCase implements Comparable<Object>{
	private ArgumentCase argumentCase;
	private float suitability;
	public SimilarArgumentCase(ArgumentCase argumentCase, float suitability) {
		this.argumentCase = argumentCase;
		this.suitability = suitability;
	}
	
	public ArgumentCase getArgumentCase() {
		return argumentCase;
	}
	public void setArgumentCase(ArgumentCase argumentCase) {
		this.argumentCase = argumentCase;
	}
	public float getSuitability() {
		return suitability;
	}
	public void setSuitability(float suitability) {
		this.suitability = suitability;
	}
	
	public int compareTo(Object obj) {
		SimilarArgumentCase otherSimilarArgumentCase = (SimilarArgumentCase) obj;
	    return Math.round(otherSimilarArgumentCase.getSuitability()*100000 - this.suitability*100000);
	}
	
}
