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
	 * Returns a list of the candidate cases with a similarity degree to the given ticket.
	 * The similarity is calculated using normalized Euclidean distance among the attributes. 
	 * @param attributes collection of attributes to calculate the similarity with the candidate cases
	 * @param candidateCases The cases that can be similar to the ticket
	 * @return A SimilarCase ArrayList with the candidate cases and its similarity degree to the ticket.
	 */	
	public static ArrayList<SimilarDomainCase> normalizedEuclideanSimilarity(HashMap<Integer,Premise> premises, ArrayList<DomainCase> candidateCases){
		

		int numCases=candidateCases.size();
		//System.out.println("numCasesCandidates="+numCases+" ticketId="+ticket.getID());
		
		
		float accumDist[] = new float[numCases]; // acumulaciÓn de la distancia por casos
		for(int i=0;i<numCases;i++){
			accumDist[i]=0;
		}
		
		Iterator<Premise> premIter=premises.values().iterator();
		
		while(premIter.hasNext()){ //para cada atributo del caso
			float maxDist=0;
			int index = 0;
			float auxDist[] = new float[numCases]; // vector temporal de distancias por atributo: key: el objeto caso, value: la distancia
			
			boolean maxDistVec[] = new boolean[numCases]; // vector de marcas de distancias maximas: key: el objeto caso, value: true/false
			for (int j = 0; j < maxDistVec.length; j++) {
				maxDistVec[j] = false;
			}
			
			Premise premise=premIter.next();
			
			Iterator<DomainCase> candIterator=candidateCases.iterator();
			
			while(candIterator.hasNext()){ //para cada caso candidato a ser similar
				DomainCase candidate=candIterator.next();
				float myDist = 1;
				
				Premise candPremise=candidate.getProblem().getDomainContext().getPremises().get(premise.getID());
				if(candPremise!=null){
					myDist=Metrics.doDist(premise.getContent(),candPremise.getContent());
				}
				else{//El atributo no existe en el caso recuperado
					myDist = 1;
					maxDistVec[index] = true;
				}
				
				auxDist[index] = myDist ;
				if(myDist>maxDist)
					maxDist=myDist;
				index++;
				
			} //fin para cada caso candidato a ser similar
			
			
			// hemos recorrido ya el atributo en todos los casos.
			// dividimos entre el mÁximo para normalizar las distancias
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

				// y se eleva al cuadrado
				auxDist[index] *= auxDist[index];
				accumDist[index] += auxDist[index];
			}
			
			
		} // fin para cada atributo del caso
		
		
		
		Iterator<DomainCase> candIterator=candidateCases.iterator();
		
		
		ArrayList<SimilarDomainCase> finalCandidates=new ArrayList<SimilarDomainCase>();
		int index=0;
		while(candIterator.hasNext()){ //para cada caso candidato a ser similar
			DomainCase candidate=candIterator.next();
			
			Iterator <Premise> candidatePremisesIter=candidate.getProblem().getDomainContext().getPremises().values().iterator();
			while(candidatePremisesIter.hasNext()){//para cada atributo del caso
				Premise candidatePremise=candidatePremisesIter.next();
				
				//buscar si existe el atributo en el caso objetivo
				if(premises.get(candidatePremise.getID())==null){// not found
					accumDist[index]++;
				}
				
			}
			
			float similarity=(float) (1/(Math.sqrt(accumDist[index])+1));
			finalCandidates.add(new SimilarDomainCase(candidate,similarity));
			index++;
		} //fin para cada caso candidato a ser similar
		
		Collections.sort(finalCandidates);
		
		
		return finalCandidates;
	}
	
	/**
	 * Returns a list of the candidate cases with a similarity degree to the given ticket.
	 * The similarity is calculated using weighted Euclidean distance among the attributes.
	 * @param attributes collection of attributes to calculate the similarity with the candidate cases 
	 * @param candidateCases The cases that can be similar to the ticket
	 * @return A SimilarCase ArrayList with the candidate cases and its similarity degree to the ticket.
	 */
	public static ArrayList<SimilarDomainCase> weightedEuclideanSimilarity(HashMap<Integer,Premise> premises, ArrayList<DomainCase> candidateCases){
		
		ArrayList<SimilarDomainCase> finalCandidates=new ArrayList<SimilarDomainCase>();
		
		//ArrayList<Attribute> ticketPremises=ticket.getAttributes().values().iterator();
		
		Iterator<DomainCase> candIterator=candidateCases.iterator();
		while(candIterator.hasNext()){ //para cada caso candidato a ser similar
			DomainCase candidate=candIterator.next();
			float distance = 0;
			float weight[] = new float[premises.size()];
			int attribute = 0;
			
			Iterator<Premise> casePremisesIter=premises.values().iterator();
			
			
			while(casePremisesIter.hasNext()){ // para cada atributo
				
				Premise casePremise=casePremisesIter.next();
				// entrenar los pesos
				weight[attribute] = 1; //por defecto todos los pesos valen 1
			
				float myDist=1;
				
				Premise candPremise=candidate.getProblem().getDomainContext().getPremises().get(casePremise.getID());
				
				if(candPremise!=null){
					myDist=Metrics.doDist(casePremise.getContent(),candPremise.getContent());
				}
				else{
					myDist = 1;
				}
				
				weight[attribute] *= weight[attribute]; //elevamos al cuadrado
				myDist *= myDist;
				distance = distance + weight[attribute] * myDist;
				attribute++;
				
			}
			
			Iterator<Premise> candPremisesIter=candidate.getProblem().getDomainContext().getPremises().values().iterator();
			
			while(candPremisesIter.hasNext()) {// para cada atributo del caso
				Premise candPremise=candPremisesIter.next();
				
				Premise domCasePremise=premises.get(candPremise.getID());
				
				if(domCasePremise==null)
					distance++;
				
			}
			
			float similarity=(float)(1/( Math.sqrt(distance) +1));
			finalCandidates.add(new SimilarDomainCase(candidate,similarity));
			//o bien
			//element.similarity = (float)(Math.exp(-distance));
		}
		
		Collections.sort(finalCandidates);
		
		return finalCandidates;
	}
	
	/**
	 * Returns a list of the candidate cases with a similarity degree to the given ticket.
	 * The similarity is calculated using normalized Tversky distance among the attributes.
	 * @param attributes collection of attributes to calculate the similarity with the candidate cases
	 * @param candidateCases The cases that can be similar to the ticket
	 * @return A SimilarCase ArrayList with the candidate cases and its similarity degree to the ticket.
	 */
	public static ArrayList<SimilarDomainCase> normalizedTverskySimilarity(HashMap<Integer,Premise> premises, ArrayList<DomainCase> candidateCases){
		int numCases=candidateCases.size();
		//System.out.println("numCasesCandidates="+numCases+" ticketId="+ticket.getID());
		
		
		float commonAt[] = new float[numCases]; // acumulador de atributos comunes por casos
		float differentAt[] = new float[numCases]; // acumulador de atributos diferentes por casos
		float distinctAt[] = new float[numCases]; // acumulador de atributos que no existen en ambos casos
		
		for (int i=0; i < numCases; i++) {
			commonAt[i] = 0;
			differentAt[i] = 0;
			distinctAt[i] = 0;
		}
		
		Iterator<Premise> premIter=premises.values().iterator();
		
		while(premIter.hasNext()){ //para cada atributo del caso
			
			float maxDist=0;
			int index = 0;
			float auxDist[] = new float[numCases]; // vector temporal de distancias por atributo: key: el objeto caso, value: la distancia
			
			boolean maxDistVec[] = new boolean[numCases]; // vector de marcas de distancias maximas: key: el objeto caso, value: true/false
			for (int j = 0; j < maxDistVec.length; j++) {
				maxDistVec[j] = false;
			}
			
			Premise premise=premIter.next();
			Iterator<DomainCase> candIterator=candidateCases.iterator();
			
			while(candIterator.hasNext()){ //para cada caso candidato a ser similar
				DomainCase candidate=candIterator.next();
				
				float myDist = 1;
				
				Premise candPremise=candidate.getProblem().getDomainContext().getPremises().get(premise.getID());
				
				if(candPremise!=null){
					myDist=Metrics.doDist(premise.getContent(),candPremise.getContent());
				}
				else{//El atributo no existe en el caso recuperado
					distinctAt[index]++;
					maxDistVec[index] = true;
				}
				
				auxDist[index] = myDist ;
				if(myDist>maxDist)
					maxDist=myDist;
				index++;
				
			} //fin para cada caso candidato a ser similar
			
			// hemos recorrido ya el atributo en todos los casos
			// normalizamos
			for(index=0;index<numCases;index++){ // para cada caso
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
			
			
			
		} // fin para cada atributo del caso
		
		
		Iterator<DomainCase> candIterator=candidateCases.iterator();
		
		ArrayList<SimilarDomainCase> finalCandidates=new ArrayList<SimilarDomainCase>();
		int index=0;
		while(candIterator.hasNext()){ //para cada caso candidato a ser similar
			DomainCase candidate=candIterator.next();
			
			Iterator <Premise> candidatePremisesIter=candidate.getProblem().getDomainContext().getPremises().values().iterator();
			while(candidatePremisesIter.hasNext()){//para cada atributo del caso
				Premise candidatePremise=candidatePremisesIter.next();
				
				//buscar si existe el atributo en el caso objetivo
				Premise domCasePremise=premises.get(candidatePremise.getID());
				if(domCasePremise==null)
					distinctAt[index]++;
				
			}
			
			
			float similarity=(commonAt[index] / (commonAt[index] + differentAt[index] + distinctAt[index]));
			finalCandidates.add(new SimilarDomainCase(candidate,similarity));
			index++;
		} //fin para cada caso candidato a ser similar
		
		Collections.sort(finalCandidates);
		
		
		return finalCandidates;
	}
}
