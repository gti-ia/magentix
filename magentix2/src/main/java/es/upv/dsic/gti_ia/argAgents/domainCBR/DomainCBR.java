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
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Premise;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Problem;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.SimilarDomainCase;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Solution;


public class DomainCBR {

	private Hashtable<String, ArrayList<DomainCase>> domainCB;
	
	private String filePath;
	private String storingFilePath;
	
	private int index=-1;
	
	public DomainCBR(String filePath, String storingFilePath) {
		
		this.filePath=filePath;
		this.storingFilePath = storingFilePath;
		
		//TODO select index
		
		loadCaseBase();
		
//		domainCB=new Hashtable<String, ArrayList<DomainCase>>();
//		
//		HashMap<Integer,Premise> premises=new HashMap<Integer, Premise>();
//		premises.put(1, new Premise(1, "", "blau"));
//		premises.put(2, new Premise(2, "", "verd"));
//		premises.put(3, new Premise(3, "", "roig"));
//		
//		ArrayList<Solution> solutions= new ArrayList<Solution>();
//		solutions.add(new Solution(new Conclusion(1, "sol1"), "sol1_value", 1));
//		
//		DomainCase dmCase=new DomainCase(new Problem(new DomainContext(premises)), solutions, new Justification("justification"));
//		
//		int introduced=0, notIntroduced=0;
//	    boolean returnedValue=addCase(dmCase);
//		if(returnedValue)
//			introduced++;
//		else
//			notIntroduced++;
//		System.out.println("cases="+(introduced+notIntroduced)+" introduced="+introduced+" notIntroduced="+notIntroduced);
	}
	
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
		
		
		
	
		//System.out.println("tickets="+vTickets.size()+" introduced="+introduced+" notIntroduced="+notIntroduced);
		System.out.println("cases="+(introduced+notIntroduced)+" introduced="+introduced+" notIntroduced="+notIntroduced);
		
	}
	
	
	/** 
	 * Retrieves the most similar cases of the ticket with a similarity greater or equal to the given threshold. 
	 * Also, it retains a new case if the attributes of the ticket are not exactly the same of any case.  
	 * @param ticket The ticket that needs a solution from the CBR 
	 * @param threshold The threshold of minimum similarity of the cases to return
	 * @return an {@link ArrayList} of {@link SimilarDomainCase}
	 */	
	public ArrayList<SimilarDomainCase> retrieveAndRetain(DomainCase domCase,float threshold){
		
		/**
		 * TODO
		 *  Increase timesUsed. If we return a list of possible cases and possible solutions for each case, what solution has to be increased?
		 *
		 */
		
		Configuration c= new Configuration();
		ArrayList<SimilarDomainCase> similarCases=getMostSimilar(domCase.getProblem().getDomainContext().getPremises(),threshold, c.domainCBRSimilarity);
		
		if(similarCases!=null){
			Iterator<SimilarDomainCase> iterCases=similarCases.iterator();
			while(iterCases.hasNext()){
				SimilarDomainCase similarCase=iterCases.next();
				//System.out.println("\nTipiNode:"+similarCase.getCase().getCategoryNode().getIdTipi()+" Project="+similarCase.getCase().getProject());
				if(similarCase.getSimilarity()<1.0f){
					//add case
					//Case caseb=new Case(categories.get(tipiNodeID),attributes,)
					
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
	
	public ArrayList<SimilarDomainCase> retrieve(HashMap<Integer, Premise> premises,float threshold){
		
		/**
		 * TODO
		 *  Increase timesUsed. If we return a list of possible cases and possible solutions for each case, what solution has to be increased?
		 *
		 */
		//Premise categoryPrem=premises.remove(0);
		//int categoryNode=Integer.parseInt(categoryPrem.getContent());
		//System.err.println("CATEGORYNODEid="+categoryNode);
		
		
		Configuration c= new Configuration();
		ArrayList<SimilarDomainCase> similarCases=getMostSimilar(premises,threshold, c.domainCBRSimilarity);
		
		return similarCases;
	}
	
	
	/**
	 * Adds a new case to CB
	 * Otherwise, if it exists the same case in case base, adds the group, the operator 
	 * and the solutions to the corresponding case.
	 * @param newCase Case that could be added.
	 * @return True if a case is added, else false.
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
			
			while(iterNewCasePremises.hasNext()){ //copy the premises to an arraylist, ordered from lower to higher id
				Premise prem=iterNewCasePremises.next();
				newCasePremisesList.add(prem.getID());
//				System.out.println("Prem: "+prem.getID());
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
			//System.err.println("Vacia, lo pongo");
			return true;
		}
		
		boolean found=false;
		
		for(int i=0;i<cases.size();i++){
			DomainCase currentCase=cases.get(i);
			HashMap<Integer, Premise> currentPremises=currentCase.getProblem().getDomainContext().getPremises();
			if(currentPremises.size()!=newCase.getProblem().getDomainContext().getPremises().size())
				continue;//no tienen los mismos atributos, se pasa a mirar el siguiente
			
			
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
			
//			for(int i=0;i<currentAttributes.size();i++){
//				if(currentAttributes.get(i).getID()!=ticketAttributes.get(i).getID() ||
//						!currentAttributes.get(i).getContent().equalsIgnoreCase(ticketAttributes.get(i).getContent())){
//					equal=false;
//					break;
//				}
//			}
			
			if(equal){//same premises with same content
				
				//add the new solutions to the case if there are
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
				return false; //no lo introducimos porque ya lo tenemos
			}
			
			
		}
		if(!found){//no existe igual, se introduce
			cases.add(newCase);
			//System.err.println("No existe ninguno igual");
			return true;
		}
		
		return false;
		
	}
	
	
	/**
	 * Gets the most similar cases of the given case with a similarity degree greater or equal to the given threshold.
	 * The similarity algorithm is determined by an integer parameter.
	 * @param categoryNode id of the tipification node
	 * @param attributes collection of attributes for retrieving cases from case base.
	 * @param threshold The threshold of minimum similarity of the cases to return.
	 * @param similarityType A string to specify which similarity algorithm has to be used.
	 * @return An ArrayList of SimilarCase with similarity degree greater or equal to the threshold.
	 */
	private ArrayList<SimilarDomainCase> getMostSimilar(HashMap<Integer,Premise> premises, float threshold, String similarityType){
		
		ArrayList<DomainCase> candidateCases=getCandidateCases(premises);
		ArrayList<SimilarDomainCase> finalCandidates;
		ArrayList<SimilarDomainCase> moreSimilarCandidates=new ArrayList<SimilarDomainCase>();
		
		if(similarityType.equalsIgnoreCase("normalizedEuclidean")){
			finalCandidates=SimilarityAlgorithms.normalizedEuclideanSimilarity(premises, candidateCases);
			//System.err.println("normalizedEuclidean");
		}
		else if(similarityType.equalsIgnoreCase("weightedEuclidean")){
			finalCandidates=SimilarityAlgorithms.weightedEuclideanSimilarity(premises, candidateCases);
			//System.err.println("weightedEuclideandd");
		}
		else if(similarityType.equalsIgnoreCase("normalizedTversky")){
			finalCandidates=SimilarityAlgorithms.normalizedTverskySimilarity(premises, candidateCases);
			//System.err.println("normalizedTversky");
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
	 * Gets the most similar case of the given ticket.
	 * The similarity algorithm is determined by an integer parameter.
	 * @param categoryNode id of the tipification node
	 * @param attributes collection of attributes for retrieving cases from case base.
	 * @param similarityType A string to specify which similarity algorithm has to be used.
	 * @return The most similar case to the ticket of case base.
	 */
	
	@SuppressWarnings("unused")
	private SimilarDomainCase getMostSimilar(HashMap<Integer,Premise> premises, String similarityType){
		
		//obtener los casos con tipiNode igual y "padres" del tipiNode para comparar
		ArrayList<DomainCase> candidateCases=getCandidateCases(premises);
		ArrayList<SimilarDomainCase> finalCandidates;
		
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
	
//		Iterator<SimilarCase> finCandIter=finalCandidates.iterator();
//		while(finCandIter.hasNext()){
//			SimilarCase simCase=finCandIter.next();
//			//System.out.println(simTicket.getTicket().getID()+" "+simTicket.getTicket().getIdTipiNode()+" "+simTicket.getSimilarity());
//			
//		}
		
		//obtener la soluci�n que se ha utilizado m�s veces de los casos m�s similares
		//para eso se mira el primer caso devuelto (que es el m�s similar)
		//y los siguientes al primero que tengan el mismo grado de similitud que �ste
		
		SimilarDomainCase bestSimilarTicket=null;
		if(finalCandidates!=null && finalCandidates.size()>0){
			bestSimilarTicket=finalCandidates.get(0);
			float bestSimilarity=finalCandidates.get(0).getSimilarity();
			Solution bestSolution=null;
			
			//primero obtener la mejor soluci�n del primer caso devuelto (m�s similar)
			Iterator<Solution> iterSolutions=finalCandidates.get(0).getCaseb().getSolutions().iterator();
			int mostTimesUsed=-1;
			while(iterSolutions.hasNext()){
				Solution sol= iterSolutions.next();
				if(sol.getTimesUsed()>mostTimesUsed){
					mostTimesUsed=sol.getTimesUsed();
					bestSolution=sol;
				}
			}
			
			//mirar los siguientes casos al primero mientras tengan el mismo grado de similitud
			Iterator<SimilarDomainCase> finalCandidatesIter=finalCandidates.iterator();
			while(finalCandidatesIter.hasNext()){
				SimilarDomainCase simTicket=finalCandidatesIter.next();
				if(simTicket.getSimilarity()==bestSimilarity){
					Iterator<Solution> iterSolutions2=simTicket.getCaseb().getSolutions().iterator();
					while(iterSolutions2.hasNext()){
						Solution sol= iterSolutions2.next();
						if(sol.getTimesUsed()>mostTimesUsed){
							mostTimesUsed=sol.getTimesUsed();
							bestSolution=sol;
							bestSimilarTicket=simTicket;
						}
					}
				}
				else
					break;
			}
			
			
			System.out.println("\nSolution="+bestSolution.getConclusion().getID()+" -> "+bestSolution.getConclusion().getDescription()+" TimesUsed="+bestSolution.getTimesUsed()+" similarity="+bestSimilarity);
			
		}
		
		return bestSimilarTicket;
	}
	
	

	
	public float getPremisesSimilarity(HashMap<Integer, Premise> premises1, HashMap<Integer, Premise> premises2){
		
		Configuration c = new Configuration();
		String similarityType = c.domainCBRSimilarity;
		
		
		DomainCase cas=new DomainCase(new Problem(new DomainContext(premises2)), new ArrayList<Solution>(), new Justification());
		ArrayList<DomainCase> caseList = new ArrayList<DomainCase>();
		caseList.add(cas);
		
		ArrayList<SimilarDomainCase> finalCandidates;
		
		if(similarityType.equalsIgnoreCase("normalizedEuclidean")){
			finalCandidates=SimilarityAlgorithms.normalizedEuclideanSimilarity(premises1, caseList);
			//System.err.println("normalizedEuclidean");
		}
		else if(similarityType.equalsIgnoreCase("weightedEuclidean")){
			finalCandidates=SimilarityAlgorithms.weightedEuclideanSimilarity(premises1, caseList);
			//System.err.println("weightedEuclideandd");
		}
		else if(similarityType.equalsIgnoreCase("normalizedTversky")){
			finalCandidates=SimilarityAlgorithms.normalizedTverskySimilarity(premises1, caseList);
			//System.err.println("normalizedTversky");
		}
		else{
			finalCandidates=SimilarityAlgorithms.normalizedEuclideanSimilarity(premises1, caseList);
		}
		
		return finalCandidates.get(0).getSimilarity();
	}
	
	
	
	
	public void doCache(){
		
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.filePath));
		
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
		//System.out.println("\n\n\n\n++++++++++++++++++++++\nWriting "+OWLFilePath+"\n++++++++++++++++++++++\n\n\n\n");
	}
	
	public void doCacheInc(){
//		try {
//			owlArgCBRparser.saveArgumentationOntology(getAllCasesVector(), initialOWLFilePath, storingOWLFilePath);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
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
	 * Returns all cases in a Collection of ArrayLists
	 * @return
	 */
	public Collection<ArrayList<DomainCase>> getAllCases(){
		return domainCB.values();
	}
	
	/**
	 * Gets the cases of a given tipiNode and the cases of its ancestor tipiNodes
	 * @param tipiNode An integer representing the identifier of a tipiNode
	 * @return A Case ArrayList with the cases of the tipiNode and its ancestors
	 */
	private ArrayList<DomainCase> getCandidateCases(HashMap<Integer,Premise> premises){
		
		ArrayList<DomainCase> candidateCases=new ArrayList<DomainCase>();
		
		int mainPremiseID=-1;
		String mainPremiseValue=null;
		
		if(index!=-1){
			mainPremiseValue=premises.get(index).getContent();
			candidateCases=domainCB.get(mainPremiseValue);
		}
		else{
			Iterator<Premise> iterNewCasePremises= premises.values().iterator();
			ArrayList<Integer> newCasePremisesList=new ArrayList<Integer>();
			
			while(iterNewCasePremises.hasNext()){ //copy the premises to an arraylist, ordered from lower to higher id
				Premise prem=iterNewCasePremises.next();
				newCasePremisesList.add(prem.getID());
//				System.out.println("Prem: "+prem.getID());
			}
			Collections.sort(newCasePremisesList);
			mainPremiseID=newCasePremisesList.get(0);
			
			candidateCases=domainCB.get(String.valueOf(mainPremiseID));
			
		}
		System.out.println("Main Premise ID: "+mainPremiseID);
		System.out.println(" candidates: "+candidateCases.size());
		return candidateCases;
		
	}
	
	/**
	 * Returns all cases in an ArrayList
	 * @return
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
	 * Returns all cases in a Vector
	 * @return
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
