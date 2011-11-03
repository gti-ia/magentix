package ArgumentationCBRUnitTest;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import es.upv.dsic.gti_ia.argAgents.argCBR.ArgCBR;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.ArgumentCase;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.ArgumentProblem;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.ArgumentSolution;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.DomainContext;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Premise;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.SimilarArgumentCase;


public class ArgCBRTests {

	private ArgCBR cbr;
	public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ArgCBRTests.class);
    }

	@org.junit.Before 
	public void setUp(){
		//Initialize argumentation CBR
		cbr = new ArgCBR("partArgInc/partArgOperator0.dat", "/tmp/null");
	}
	
	@org.junit.Test 
	public void retrievalAccuracy(){
		// For each ticket in arguments case-base
		// Query the argCBR for the most similarCase
		Iterator<ArrayList<ArgumentCase>> iterCasesList=cbr.getAllCases().iterator();
		while(iterCasesList.hasNext()){
			ArrayList<ArgumentCase> casesList=iterCasesList.next();
			Iterator<ArgumentCase> iterCase=casesList.iterator();
			while(iterCase.hasNext()){
				ArgumentCase aCase=iterCase.next();
				ArrayList<SimilarArgumentCase> retrievedCases=cbr.getMostSimilarArgCases(aCase.getArgumentProblem());
				Iterator<SimilarArgumentCase> iterSimCase=retrievedCases.iterator();
				if(iterSimCase.hasNext()){
					SimilarArgumentCase simCase=iterSimCase.next();
					assertTrue(simCase.getArgumentCase().getID()==aCase.getID());
				}
				
			}
		}
	}
	
	@org.junit.Test 
	public void retrievalConsistency(){
		// For each ticket in argument case-base
		Collection<ArrayList<ArgumentCase>> allCases= cbr.getAllCases();
		ArrayList <ArrayList<ArgumentCase>> allCases2=new ArrayList<ArrayList<ArgumentCase>>();
		Iterator<ArrayList<ArgumentCase>> iterCasesList2=allCases.iterator();
		while(iterCasesList2.hasNext()){
			ArrayList<ArgumentCase> casesList=iterCasesList2.next();
			allCases2.add(casesList);
		}
		
		Iterator<ArrayList<ArgumentCase>> iterCasesList=allCases2.iterator();
		while(iterCasesList.hasNext()){
			ArrayList<ArgumentCase> casesList=iterCasesList.next();
			Iterator<ArgumentCase> iterCase=casesList.iterator();
			while(iterCase.hasNext()){
				ArgumentCase aCase=iterCase.next();
				// Query the argCBR for the list similarCases1
				ArrayList<SimilarArgumentCase> similarCases1=cbr.getMostSimilarArgCases(aCase.getArgumentProblem());
				// Query again the argCBR for the list of similarCases2
				ArrayList<SimilarArgumentCase> similarCases2=cbr.getMostSimilarArgCases(aCase.getArgumentProblem());
				// For each case1 in similarCases1
				Iterator<SimilarArgumentCase> iterSimCase1=similarCases1.iterator();
				while(iterSimCase1.hasNext()){
					SimilarArgumentCase case1=iterSimCase1.next();
					boolean found=false;
					// Retrieve case2 from similarCases2 such that case1 == case2
					for(int i=0;i<similarCases2.size();i++){
						SimilarArgumentCase case2=similarCases2.get(i);
						
						if(Float.compare(case1.getSuitability(), case2.getSuitability())==0 && 
								case1.getArgumentCase().getID()==case2.getArgumentCase().getID()){
							assertTrue(true);
							found=true;
							break;
						}
					}
					if(!found)
						assertTrue(false);
					
				}
			}
		}
	}
	
	
	@org.junit.Test 
	public void caseDuplication(){
		// For each ticket in argument case-base
		Collection<ArrayList<ArgumentCase>> allCases= cbr.getAllCases();
		ArrayList <ArrayList<ArgumentCase>> allCases2=new ArrayList<ArrayList<ArgumentCase>>();
		Iterator<ArrayList<ArgumentCase>> iterCasesList2=allCases.iterator();
		while(iterCasesList2.hasNext()){
			ArrayList<ArgumentCase> casesList=iterCasesList2.next();
			allCases2.add(casesList);
		}
		
		Iterator<ArrayList<ArgumentCase>> iterCasesList=allCases2.iterator();
		while(iterCasesList.hasNext()){
			ArrayList<ArgumentCase> casesList=iterCasesList.next();
			Iterator<ArgumentCase> iterCase=casesList.iterator();
			while(iterCase.hasNext()){
				ArgumentCase aCase=iterCase.next();
				// Query the argCBR for the list similarCases
				ArrayList<SimilarArgumentCase> similarCases=cbr.getMostSimilarArgCases(aCase.getArgumentProblem());
				// For each case in similarCases
				
				for(int i=0;i<similarCases.size();i++){
					SimilarArgumentCase case1=similarCases.get(i);
					similarCases.remove(case1);
					i--;
					assertTrue(!similarCases.contains(case1));
				}
				
			}
		}
	}
	
	@org.junit.Test 
	public void operability(){
		
		ArgumentCase firstCase=cbr.getAllCasesVector().get(0);
		ArrayList<SimilarArgumentCase> similar2firstCase=cbr.getMostSimilarArgCases(firstCase.getArgumentProblem());
		assertTrue(similar2firstCase.get(0).getArgumentCase().equals(firstCase));
		
		
		HashMap<Integer, Premise> premises=new HashMap<Integer, Premise>();
		Iterator<Premise> iterPremises= firstCase.getArgumentProblem().getDomainContext().getPremises().values().iterator();
		while(iterPremises.hasNext()){
			Premise prem=iterPremises.next();
			premises.put(prem.getID(), prem);
		}
		Premise firstPremise=premises.values().iterator().next();
		firstPremise.setContent(firstPremise.getContent()+"aa");
		premises.put(firstPremise.getID(), firstPremise);
		Premise prem=new Premise(100, "", "100");
		premises.put(prem.getID(), prem);
		ArgumentSolution solution=firstCase.getArgumentSolution();
		ArgumentCase argCase=new ArgumentCase(System.currentTimeMillis(), String.valueOf(System.currentTimeMillis()), new ArgumentProblem(new DomainContext(premises), firstCase.getArgumentProblem().getSocialContext()), solution, firstCase.getArgumentJustification(), firstCase.getTimesUsed());
		
		boolean added=cbr.addCase(argCase);
		if(added)
			System.out.println("Operability: case added");
		else System.out.println("Operability: case NOT added");
			
		
		ArrayList<SimilarArgumentCase> similarCases=cbr.getMostSimilarArgCases(argCase.getArgumentProblem());
		assertTrue(similarCases.get(0).getArgumentCase().equals(argCase));
		similarCases=cbr.getMostSimilarArgCases(firstCase.getArgumentProblem());
		assertTrue(similarCases.get(0).getArgumentCase().equals(firstCase));
	}

}
