package es.upv.dsic.gti_ia.argAgents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import es.upv.dsic.gti_ia.argAgents.knowledgeResources.DomainCase;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Premise;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.SimilarDomainCase;


/**
 * This class contains algorithms to calculate cases similarity.
 * @author Jaume Jordan 
 */
public class SimilarityAlgorithms {
	
	/**
	 * Returns a list of the candidate domain-cases with a similarity degree to the given domain-cases.
	 * The similarity is calculated using normalized Euclidean distance among the premises. 
	 * @param premises {@link HashMap} of {@link Premise} to calculate the similarity with the candidate domain-cases
	 * @param candidateCases The domain-cases that can be similar to the domain-case to solve
	 * @return A {@link SimilarDomainCase} {@link ArrayList} with the candidate domain-cases and its similarity degree to the domain-case to solve
	 */	
	public static ArrayList<SimilarDomainCase> normalizedEuclideanSimilarity(HashMap<Integer,Premise> premises, ArrayList<DomainCase> candidateCases){
		

		int numCases=candidateCases.size();
		
		float accumDist[] = new float[numCases];
		for(int i=0;i<numCases;i++){
			accumDist[i]=0;
		}
		
		Iterator<Premise> premIter=premises.values().iterator();
		
		while(premIter.hasNext()){ 
			float maxDist=0;
			int index = 0;
			float auxDist[] = new float[numCases]; //  temporal vector of distances per attribute: key: case object, value: distance
			
			boolean maxDistVec[] = new boolean[numCases]; // marks vector of max distances: key: case object, value: true/false
			for (int j = 0; j < maxDistVec.length; j++) {
				maxDistVec[j] = false;
			}
			
			Premise premise=premIter.next();
			
			Iterator<DomainCase> candIterator=candidateCases.iterator();
			
			while(candIterator.hasNext()){ 
				DomainCase candidate=candIterator.next();
				float myDist = 1;
				
				Premise candPremise=candidate.getProblem().getDomainContext().getPremises().get(premise.getID());
				if(candPremise!=null){
					myDist=Metrics.doDist(premise.getContent(),candPremise.getContent());
				}
				else{//The attribute does not exist in the retrieved case
					myDist = 1;
					maxDistVec[index] = true;
				}
				
				auxDist[index] = myDist ;
				if(myDist>maxDist)
					maxDist=myDist;
				index++;
				
			} 
			
			//divide by the maximum to normalize the distances
			for(index=0;index<numCases;index++){
				if (maxDist==0) 
					auxDist[index]=0;
				else {
					if (maxDistVec[index] == true) {
						auxDist[index] = 1;
					} else {
						auxDist[index] /= maxDist;
					}
				}

				auxDist[index] *= auxDist[index];
				accumDist[index] += auxDist[index];
			}
			
			
		}
		
		Iterator<DomainCase> candIterator=candidateCases.iterator();
		
		
		ArrayList<SimilarDomainCase> finalCandidates=new ArrayList<SimilarDomainCase>();
		int index=0;
		while(candIterator.hasNext()){
			DomainCase candidate=candIterator.next();
			
			Iterator <Premise> candidatePremisesIter=candidate.getProblem().getDomainContext().getPremises().values().iterator();
			while(candidatePremisesIter.hasNext()){
				Premise candidatePremise=candidatePremisesIter.next();
				
				if(premises.get(candidatePremise.getID())==null){// not found
					accumDist[index]++;
				}
				
			}
			
			float similarity=(float) (1/(Math.sqrt(accumDist[index])+1));
			finalCandidates.add(new SimilarDomainCase(candidate,similarity));
			index++;
		}
		
		Collections.sort(finalCandidates);
		
		
		return finalCandidates;
	}
	
	/**
	 * Returns a list of the candidate domain-cases with a similarity degree to the given domain-cases.
	 * The similarity is calculated using weighted Euclidean distance among the premises.
	 * @param premises {@link HashMap} of {@link Premise} to calculate the similarity with the candidate domain-cases
	 * @param candidateCases The domain-cases that can be similar to the domain-case to solve
	 * @return A {@link SimilarDomainCase} {@link ArrayList} with the candidate domain-cases and its similarity degree to the domain-case to solve
	 */
	public static ArrayList<SimilarDomainCase> weightedEuclideanSimilarity(HashMap<Integer,Premise> premises, ArrayList<DomainCase> candidateCases){
		
		ArrayList<SimilarDomainCase> finalCandidates=new ArrayList<SimilarDomainCase>();
		
		Iterator<DomainCase> candIterator=candidateCases.iterator();
		while(candIterator.hasNext()){ 
			DomainCase candidate=candIterator.next();
			float distance = 0;
			float weight[] = new float[premises.size()];
			int attribute = 0;
			
			Iterator<Premise> casePremisesIter=premises.values().iterator();
			
			
			while(casePremisesIter.hasNext()){ 
				
				Premise casePremise=casePremisesIter.next();
				//train weights
				weight[attribute] = 1; //by default 1
			
				float myDist=1;
				
				Premise candPremise=candidate.getProblem().getDomainContext().getPremises().get(casePremise.getID());
				
				if(candPremise!=null){
					myDist=Metrics.doDist(casePremise.getContent(),candPremise.getContent());
				}
				else{
					myDist = 1;
				}
				
				weight[attribute] *= weight[attribute]; 
				myDist *= myDist;
				distance = distance + weight[attribute] * myDist;
				attribute++;
				
			}
			
			Iterator<Premise> candPremisesIter=candidate.getProblem().getDomainContext().getPremises().values().iterator();
			
			while(candPremisesIter.hasNext()) {
				Premise candPremise=candPremisesIter.next();
				
				Premise domCasePremise=premises.get(candPremise.getID());
				
				if(domCasePremise==null)
					distance++;
				
			}
			
			float similarity=(float)(1/( Math.sqrt(distance) +1));
			finalCandidates.add(new SimilarDomainCase(candidate,similarity));

		}
		
		Collections.sort(finalCandidates);
		
		return finalCandidates;
	}
	
	/**
	 * Returns a list of the candidate domain-cases with a similarity degree to the given domain-cases.
	 * The similarity is calculated using normalized Tversky distance among the premises.
	 * @param premises {@link HashMap} of {@link Premise} to calculate the similarity with the candidate domain-cases
	 * @param candidateCases The domain-cases that can be similar to the domain-case to solve
	 * @return A {@link SimilarDomainCase} {@link ArrayList} with the candidate domain-cases and its similarity degree to the domain-case to solve
	 */
	public static ArrayList<SimilarDomainCase> normalizedTverskySimilarity(HashMap<Integer,Premise> premises, ArrayList<DomainCase> candidateCases){
		int numCases=candidateCases.size();
		
		float commonAt[] = new float[numCases]; 
		float differentAt[] = new float[numCases];
		float distinctAt[] = new float[numCases];
		
		for (int i=0; i < numCases; i++) {
			commonAt[i] = 0;
			differentAt[i] = 0;
			distinctAt[i] = 0;
		}
		
		Iterator<Premise> premIter=premises.values().iterator();
		
		while(premIter.hasNext()){ 
			
			float maxDist=0;
			int index = 0;
			float auxDist[] = new float[numCases]; 
			
			boolean maxDistVec[] = new boolean[numCases];
			for (int j = 0; j < maxDistVec.length; j++) {
				maxDistVec[j] = false;
			}
			
			Premise premise=premIter.next();
			Iterator<DomainCase> candIterator=candidateCases.iterator();
			
			while(candIterator.hasNext()){ 
				DomainCase candidate=candIterator.next();
				
				float myDist = 1;
				
				Premise candPremise=candidate.getProblem().getDomainContext().getPremises().get(premise.getID());
				
				if(candPremise!=null){
					myDist=Metrics.doDist(premise.getContent(),candPremise.getContent());
				}
				else{
					distinctAt[index]++;
					maxDistVec[index] = true;
				}
				
				auxDist[index] = myDist ;
				if(myDist>maxDist)
					maxDist=myDist;
				index++;
				
			} 
			
			// normalize
			for(index=0;index<numCases;index++){ 
				if (maxDist==0)
					auxDist[index]=0;
				else {
					if (maxDistVec[index] == true) {
						auxDist[index] = 1;
					} else {
						auxDist[index] /= maxDist;
					}
				}			
				
				if (auxDist[index] < 0.05)
					commonAt[index]++;
				else					
					differentAt[index]++;
			}
			
			
			
		} 
		
		Iterator<DomainCase> candIterator=candidateCases.iterator();
		
		ArrayList<SimilarDomainCase> finalCandidates=new ArrayList<SimilarDomainCase>();
		int index=0;
		while(candIterator.hasNext()){ 
			DomainCase candidate=candIterator.next();
			
			Iterator <Premise> candidatePremisesIter=candidate.getProblem().getDomainContext().getPremises().values().iterator();
			while(candidatePremisesIter.hasNext()){
				Premise candidatePremise=candidatePremisesIter.next();
				
				Premise domCasePremise=premises.get(candidatePremise.getID());
				if(domCasePremise==null)
					distinctAt[index]++;
				
			}
			
			
			float similarity=(commonAt[index] / (commonAt[index] + differentAt[index] + distinctAt[index]));
			finalCandidates.add(new SimilarDomainCase(candidate,similarity));
			index++;
		}
		
		Collections.sort(finalCandidates);
		
		
		return finalCandidates;
	}
}
