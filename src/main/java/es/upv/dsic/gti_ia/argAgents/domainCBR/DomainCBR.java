package es.upv.dsic.gti_ia.argAgents.domainCBR;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;


import es.upv.dsic.gti_ia.argAgents.Configuration;
import es.upv.dsic.gti_ia.argAgents.SimilarityAlgorithms;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.DomainCase;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.DomainContext;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Justification;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Position;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Premise;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Problem;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.SimilarDomainCase;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Solution;

/**
 * This class implements the domain CBR. This CBR stores domain knowledge of previously solved problems. 
 * It is used by the argumentative agent to generate and select 
 * the {@link Position} (solution) to defend in an argumentation dialogue.
 * @author Jaume Jordan
 *
 */
public class DomainCBR {

	private Hashtable<String, ArrayList<DomainCase>> domainCB;
	
	private String filePath;
	private String storingFilePath;
	
	private int index=-1;
	
	/**
	 * Constructor of the DomainCBR. Initializes the structures and loads the cases of the given data filePath.
	 * It also establishes the storingFilePath to the case-base.
	 * @param filePath path of the file to load the initial domain-cases
	 * @param storingFilePath path of the file to store the final domain-cases
	 * @param index identifier of the premise which value will be used as a hash index. If no indexation is used, just set this value to -1
	 */
	public DomainCBR(String filePath, String storingFilePath, int index) {
		
		this.filePath=filePath;
		this.storingFilePath = storingFilePath;
		
		this.index=index;
		loadCaseBase();
		
	}
	
	/**
	 * Loads the case-base stored in the initial file path
	 */
	private void loadCaseBase(){
		domainCB=new Hashtable<String, ArrayList<DomainCase>>();
		int introduced=0, notIntroduced=0;
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(this.filePath));
			
			// Read first object
			Object aux = ois.readObject();
			
			// While there are objects
			while (aux!=null){
			    if(aux instanceof DomainCase){
			    	DomainCase aCase=(DomainCase) aux;
			        boolean returnedValue=addCase(aCase);
					if(returnedValue)
						introduced++;
					else
						notIntroduced++;
			        
			    }
			    aux = ois.readObject();
			}
			ois.close();
		
		
		} catch (EOFException e) {
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	
		System.out.println("cases="+(introduced+notIntroduced)+" introduced="+introduced+" notIntroduced="+notIntroduced);
		
	}
	
	
	/** 
	 * Retrieves the most similar domain-cases to the given one with a similarity degree greater or equal than a given threshold. 
	 * Also, it retains a new domain-case if the premises of the domain-case are not exactly the same of any existent domain-case in the case-base.  
	 * @param domCase The domain-case (representing a problem to solve) that needs a solution from the CBR 
	 * @param threshold The threshold of minimum degree of similarity of the domain-cases to return
	 * @return an {@link ArrayList} of {@link SimilarDomainCase}
	 */	
	public ArrayList<SimilarDomainCase> retrieveAndRetain(DomainCase domCase,float threshold){
		

		 //The parameter timesUsed can be also increased depending of the application domain

		
		Configuration c= new Configuration();
		ArrayList<SimilarDomainCase> similarCases=getMostSimilar(domCase.getProblem().getDomainContext().getPremises(),threshold, c.domainCBRSimilarity);
		
		if(similarCases!=null){
			Iterator<SimilarDomainCase> iterCases=similarCases.iterator();
			while(iterCases.hasNext()){
				SimilarDomainCase similarCase=iterCases.next();
				
				if(similarCase.getSimilarity()<1.0f){
					//add case
					domCase.setSolutions(similarCase.getCaseb().getSolutions());//add the solutions of the most similar case to the new case
					boolean returnedValue=addCase(domCase);
					if(returnedValue)
						System.out.println("New case Introduced");
					else
						System.out.println("New case NOT Introduced");
				}
				else
					System.out.println("New case NOT Introduced. Similar 1.0");
				
			}
		}
		else System.out.println("No similar cases in CB");
		
		return similarCases;
		
		
	}
	
	/**
	 * Retrieves the most similar domain-cases to the given premises with a similarity degree greater or equal than a given threshold.
	 * @param premises {@link HashMap} of premises that describe the problem to solve
	 * @param threshold The threshold of minimum degree of similarity of the domain-cases to return
	 * @return an {@link ArrayList} of {@link SimilarDomainCase}
	 */
	public ArrayList<SimilarDomainCase> retrieve(HashMap<Integer, Premise> premises, float threshold){
		
		//The parameter timesUsed can be also increased depending of the application domain

		
		Configuration c= new Configuration();
		ArrayList<SimilarDomainCase> similarCases=getMostSimilar(premises,threshold, c.domainCBRSimilarity);
		
		return similarCases;
	}
	
	
	/**
	 * Adds a new domain-case to domain case-base.
	 * Otherwise, if the same domain-case exists in the case-base, adds the relevant data to the existing domain-case.
	 * @param newCase {@link DomainCase} that could be added.
	 * @return <code>true</code> if the domain-case is added, else <code>false</code>.
	 */
	public boolean addCase(DomainCase newCase){
		
		int mainPremiseID=-1;
		String mainPremiseValue=null;
		
		ArrayList<DomainCase> cases;
		if(index!=-1){
			mainPremiseValue=newCase.getProblem().getDomainContext().getPremises().get(index).getContent();
			cases=domainCB.get(mainPremiseValue);
		}
		else{
			Iterator<Premise> iterNewCasePremises= newCase.getProblem().getDomainContext().getPremises().values().iterator();
			ArrayList<Integer> newCasePremisesList=new ArrayList<Integer>();
			
			while(iterNewCasePremises.hasNext()){ //copy the premises to an ArrayList, ordered from lower to higher id
				Premise prem=iterNewCasePremises.next();
				newCasePremisesList.add(prem.getID());
			}
			Collections.sort(newCasePremisesList);
			mainPremiseID=newCasePremisesList.get(0);
			cases=domainCB.get(String.valueOf(mainPremiseID));
		}
		
		if(cases==null || cases.size()==0){
			cases=new ArrayList<DomainCase>();
			cases.add(newCase);
			
			if(mainPremiseValue!=null){
				domainCB.put(mainPremiseValue, cases);
				System.out.println("addcase mainPremiseValue: "+mainPremiseValue);
			}
			else{
				domainCB.put(String.valueOf(mainPremiseID), cases);
				System.out.println("addcase mainPremiseID: "+mainPremiseID);
			
			}
			return true;
		}
		
		boolean found=false;
		
		for(int i=0;i<cases.size();i++){
			DomainCase currentCase=cases.get(i);
			HashMap<Integer, Premise> currentPremises=currentCase.getProblem().getDomainContext().getPremises();
			if(currentPremises.size()!=newCase.getProblem().getDomainContext().getPremises().size())
				continue;//They do not have the same premises, we go to look the next one
			
			Iterator<Premise> casePremIter=newCase.getProblem().getDomainContext().getPremises().values().iterator();
			
			boolean equal=true;
			
			while(casePremIter.hasNext()){
				Premise casePrem=casePremIter.next();
				int premID=casePrem.getID();
				if(!currentPremises.containsKey(premID) ||
					!currentPremises.get(premID).getContent().equalsIgnoreCase(casePrem.getContent())){
					equal=false;
					break;
				}
			}
			
			if(equal){//same premises with same content
				
				//add the new solutions to the case if there are some
				Iterator<Solution> caseSolutions=newCase.getSolutions().iterator();
				while(caseSolutions.hasNext()){
					Solution aSolution=caseSolutions.next();
					Iterator<Solution> currentCaseSolsIter=currentCase.getSolutions().iterator();
					boolean solfound=false;
					while(currentCaseSolsIter.hasNext()){
						Solution bSolution=currentCaseSolsIter.next();
						if(bSolution.getConclusion().getID()==aSolution.getConclusion().getID()){
							//update times used
							bSolution.setTimesUsed(bSolution.getTimesUsed() + 1);
							solfound=true;
							break;
						}
					}
					if(!solfound){
						aSolution.setTimesUsed(1);
						currentCase.getSolutions().add(aSolution);
					}
					
				}
				
				
				found=true;
				return false; //We do not introduce it because it is already in the case-base
			}
			
			
		}
		if(!found){//It an equal case does not exist, we introduce it
			cases.add(newCase);
			return true;
		}
		
		return false;
		
	}
	
	
	/**
	 * Gets the most similar domain-cases to the given premises with a similarity degree greater or equal than a given threshold.
	 * The similarity algorithm is determined by an integer parameter.
	 * @param premises {@link HashMap} of premises for retrieving cases from case base.
	 * @param threshold The threshold of minimum similarity of the cases to return.
	 * @param similarityType A {@link String} to specify which similarity algorithm has to be used (i.e. normalizedEuclidean, weightedEuclidean or normalizedTversky).
	 * @return An {@link ArrayList} of {@link SimilarDomainCase} with similarity degree greater or equal to the threshold.
	 */
	private ArrayList<SimilarDomainCase> getMostSimilar(HashMap<Integer,Premise> premises, float threshold, String similarityType){
		
		ArrayList<DomainCase> candidateCases=getCandidateCases(premises);
		ArrayList<SimilarDomainCase> finalCandidates;
		ArrayList<SimilarDomainCase> moreSimilarCandidates=new ArrayList<SimilarDomainCase>();
		
		if(similarityType.equalsIgnoreCase("normalizedEuclidean")){
			finalCandidates=SimilarityAlgorithms.normalizedEuclideanSimilarity(premises, candidateCases);
		}
		else if(similarityType.equalsIgnoreCase("weightedEuclidean")){
			finalCandidates=SimilarityAlgorithms.weightedEuclideanSimilarity(premises, candidateCases);
		}
		else if(similarityType.equalsIgnoreCase("normalizedTversky")){
			finalCandidates=SimilarityAlgorithms.normalizedTverskySimilarity(premises, candidateCases);
		}
		else{
			finalCandidates=SimilarityAlgorithms.normalizedEuclideanSimilarity(premises, candidateCases);
		}
		
		Iterator<SimilarDomainCase> finCandIter=finalCandidates.iterator();
		while(finCandIter.hasNext()){
			SimilarDomainCase simCase=finCandIter.next();
			if(simCase.getSimilarity()>=threshold){
				moreSimilarCandidates.add(simCase);
			}
			else
				break;
			
		}
		
		return moreSimilarCandidates;
	}
	
	/**
	 * Obtains the similarity between two {@link HashMap} of premises using the similarity algorithm specified in the configuration of this class.
	 * @param premises1 {@link HashMap} of premises 1
	 * @param premises2 {@link HashMap} of premises 2
	 * @return float with the similarity
	 */
	public float getPremisesSimilarity(HashMap<Integer, Premise> premises1, HashMap<Integer, Premise> premises2){
		
		Configuration c = new Configuration();
		String similarityType = c.domainCBRSimilarity;
		
		
		DomainCase cas=new DomainCase(new Problem(new DomainContext(premises2)), new ArrayList<Solution>(), new Justification());
		ArrayList<DomainCase> caseList = new ArrayList<DomainCase>();
		caseList.add(cas);
		
		ArrayList<SimilarDomainCase> finalCandidates;
		
		if(similarityType.equalsIgnoreCase("normalizedEuclidean")){
			finalCandidates=SimilarityAlgorithms.normalizedEuclideanSimilarity(premises1, caseList);
		}
		else if(similarityType.equalsIgnoreCase("weightedEuclidean")){
			finalCandidates=SimilarityAlgorithms.weightedEuclideanSimilarity(premises1, caseList);
		}
		else if(similarityType.equalsIgnoreCase("normalizedTversky")){
			finalCandidates=SimilarityAlgorithms.normalizedTverskySimilarity(premises1, caseList);
		}
		else{
			finalCandidates=SimilarityAlgorithms.normalizedEuclideanSimilarity(premises1, caseList);
		}
		
		return finalCandidates.get(0).getSimilarity();
	}
	
	
	
	/**
	 * Stores the current domain-cases case-base to the storing file path
	 */
	public void doCache(){
		
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.storingFilePath));
		
			Iterator<DomainCase> iterCases=getAllCasesVector().iterator();
			while(iterCases.hasNext()){
				DomainCase aCase=iterCases.next();
				oos.writeObject(aCase);
			}
		
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	/**
	 * Stores the current domain-cases case-base to the storing file path, but keeping the contents of that file
	 */
	public void doCacheInc(){
		
		
		try {
			
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(this.storingFilePath));
			
			// Read first object
			Object aux = ois.readObject();
			
			// While there are objects
			while (aux!=null){
			    if(aux instanceof DomainCase){
			    	DomainCase acase=(DomainCase) aux;
			    	addCase(acase);
			        
			    }
			    aux = ois.readObject();
			}
			ois.close();
		
		
		} catch (EOFException e) {
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(storingFilePath));
		
			Iterator<DomainCase> iterCases=getAllCasesVector().iterator();
			while(iterCases.hasNext()){
				DomainCase aCase=iterCases.next();
				oos.writeObject(aCase);
			}
			
			oos.close();
		
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}

	}
	
	
	
	/**
	 * Returns all cases in a {@link Collection} of ArrayLists
	 * @return all cases in a {@link Collection} of ArrayLists
	 */
	public Collection<ArrayList<DomainCase>> getAllCases(){
		return domainCB.values();
	}
	
	/**
	 * Gets a {@link DomainCase} {@link ArrayList} with the domain-cases that fit the given premises
	 * @param premises {@link HashMap} of {@link Premise} that describe the problem
	 * @return A {@link DomainCase} {@link ArrayList} with the domain-cases that fit the given premises
	 */
	private ArrayList<DomainCase> getCandidateCases(HashMap<Integer,Premise> premises){
		
		ArrayList<DomainCase> candidateCases;
		
		int mainPremiseID=-1;
		String mainPremiseValue=null;
		
		if(index!=-1){
			mainPremiseValue=premises.get(index).getContent();
			System.out.println("mainPremiseValue: "+mainPremiseValue);
			
			candidateCases=domainCB.get(mainPremiseValue);
		}
		else{
			Iterator<Premise> iterNewCasePremises= premises.values().iterator();
			ArrayList<Integer> newCasePremisesList=new ArrayList<Integer>();
			
			while(iterNewCasePremises.hasNext()){ //copy the premises to an ArrayList, ordered from lower to higher id
				Premise prem=iterNewCasePremises.next();
				newCasePremisesList.add(prem.getID());
			}
			Collections.sort(newCasePremisesList);
			mainPremiseID=newCasePremisesList.get(0);
			
			candidateCases=domainCB.get(mainPremiseID);
			
		}
		if(candidateCases==null)
			candidateCases=new ArrayList<DomainCase>();
		
		System.out.println("Main Premise ID: "+mainPremiseID);
		System.out.println(" candidates: "+candidateCases.size());
		return candidateCases;
		
	}
	
	/**
	 * Returns all domain-cases in an {@link ArrayList}
	 * @return all domain-cases in an {@link ArrayList}
	 */
	public ArrayList<DomainCase> getAllCasesList(){
		ArrayList<DomainCase> cases=new ArrayList<DomainCase>();
		Iterator<ArrayList<DomainCase>> iterLists=getAllCases().iterator();
		while(iterLists.hasNext()){
			ArrayList<DomainCase> listCases=iterLists.next();
			cases.addAll(listCases);
		}
		
		return cases;
	}
	
	/**
	 * Returns all domain-cases in a {@link Vector}
	 * @return all domain-cases in a {@link Vector}
	 */
	public Vector<DomainCase> getAllCasesVector(){
		Vector<DomainCase> cases=new Vector<DomainCase>();
		Iterator<ArrayList<DomainCase>> iterLists=getAllCases().iterator();
		while(iterLists.hasNext()){
			ArrayList<DomainCase> listCases=iterLists.next();
			cases.addAll(listCases);
		}
		
		return cases;
	}
	
	
	
}
