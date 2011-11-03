package ArgumentationCBRUnitTest;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import es.upv.dsic.gti_ia.argAgents.domainCBR.DomainCBR;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.DomainCase;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.DomainContext;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Justification;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Premise;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Problem;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.SimilarDomainCase;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Solution;




/**
* TestSuite that runs all the sample tests
*
*/
public class DomainCBRTests {

	private DomainCBR cbr = null;
	
//	public static void main(String[] args) {
//		//junit.textui.TestRunner.run (suite());
//		org.junit.runner.JUnitCore.main("test.AllTests");
//
//	}
	public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(DomainCBRTests.class);
    }

	@org.junit.Before 
	public void setUp(){
		//Initialize domain CBR
		cbr = new DomainCBR("Helpdesk-DomainCases.dat", "/tmp/null");
	}
	
	/**
	 * Code of the tests
	 */
	
	@org.junit.Test 
	public void retrievalAccuracy(){
		// For each ticket in domain case-base
		// Query the domainCBR for the most similarCase
		Iterator<ArrayList<DomainCase>> iterCasesList=cbr.getAllCases().iterator();
		while(iterCasesList.hasNext()){
			ArrayList<DomainCase> casesList=iterCasesList.next();
			Iterator<DomainCase> iterCase=casesList.iterator();
			while(iterCase.hasNext()){
				DomainCase aCase=iterCase.next();
				
				ArrayList<SimilarDomainCase> retrievedCases=cbr.retrieveAndRetain(aCase, 1f);
				Iterator<SimilarDomainCase> iterSimCase=retrievedCases.iterator();
				if(iterSimCase.hasNext()){
					SimilarDomainCase simCase=iterSimCase.next();
					assertTrue(simCase.getCaseb().getProblem().getDomainContext().getPremises().equals(aCase.getProblem().getDomainContext().getPremises()));
				}
				
			}
		}
	}
	
	@org.junit.Test 
	public void retrievalConsistency(){
		// For each ticket in domain case-base
		Collection<ArrayList<DomainCase>> allCases= cbr.getAllCases();
		ArrayList <ArrayList<DomainCase>> allCases2=new ArrayList<ArrayList<DomainCase>>();
		Iterator<ArrayList<DomainCase>> iterCasesList2=allCases.iterator();
		while(iterCasesList2.hasNext()){
			ArrayList<DomainCase> casesList=iterCasesList2.next();
			allCases2.add(casesList);
		}
		
		Iterator<ArrayList<DomainCase>> iterCasesList=allCases2.iterator();
		while(iterCasesList.hasNext()){
			ArrayList<DomainCase> casesList=iterCasesList.next();
			Iterator<DomainCase> iterCase=casesList.iterator();
			while(iterCase.hasNext()){
				DomainCase aCase=iterCase.next();
				
				// Query the domainCBR for the list similarCases1
				ArrayList<SimilarDomainCase> similarCases1=cbr.retrieveAndRetain(aCase, 0f);
				// Query again the domainCBR for the list of similarCases2
				ArrayList<SimilarDomainCase> similarCases2=cbr.retrieveAndRetain(aCase, 0f);
				// For each case1 in similarCases1
				Iterator<SimilarDomainCase> iterSimCase1=similarCases1.iterator();
				while(iterSimCase1.hasNext()){
					SimilarDomainCase case1=iterSimCase1.next();
					boolean found=false;
					// Retrieve case2 from similarCases2 such that case1 == case2
					for(int i=0;i<similarCases2.size();i++){
						SimilarDomainCase case2=similarCases2.get(i);
						
						if(Float.compare(case1.getSimilarity(), case2.getSimilarity())==0 && 
								case1.getCaseb().getJustification().getDescription().equals(case2.getCaseb().getJustification().getDescription()) &&
								case1.getCaseb().getProblem().getDomainContext().getPremises().equals(case2.getCaseb().getProblem().getDomainContext().getPremises()) &&
								case1.getCaseb().getSolutions().equals(case2.getCaseb().getSolutions())){
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
		// For each ticket in domain case-base
		Collection<ArrayList<DomainCase>> allCases= cbr.getAllCases();
		ArrayList <ArrayList<DomainCase>> allCases2=new ArrayList<ArrayList<DomainCase>>();
		Iterator<ArrayList<DomainCase>> iterCasesList2=allCases.iterator();
		while(iterCasesList2.hasNext()){
			ArrayList<DomainCase> casesList=iterCasesList2.next();
			allCases2.add(casesList);
		}
		
		Iterator<ArrayList<DomainCase>> iterCasesList=allCases2.iterator();
		while(iterCasesList.hasNext()){
			ArrayList<DomainCase> casesList=iterCasesList.next();
			Iterator<DomainCase> iterCase=casesList.iterator();
			while(iterCase.hasNext()){
				DomainCase aCase=iterCase.next();
				
				// Query the domainCBR for the list similarCases
				ArrayList<SimilarDomainCase> similarCases=cbr.retrieveAndRetain(aCase, 0f);
				// For each case in similarCases
				
				for(int i=0;i<similarCases.size();i++){
					SimilarDomainCase case1=similarCases.get(i);
					similarCases.remove(case1);
					i--;
					assertTrue(!similarCases.contains(case1));
				}
				
			}
		}
	}
	
	@org.junit.Test 
	public void operability(){
		
		DomainCase firstCase=cbr.getAllCasesList().get(0);
		ArrayList<SimilarDomainCase> similar2firstCase=cbr.retrieveAndRetain(firstCase, 0f);
		assertTrue(similar2firstCase.get(0).getCaseb().equals(firstCase));
		
		
		HashMap<Integer, Premise> premises=new HashMap<Integer, Premise>();
		Iterator<Premise> iterPremises= firstCase.getProblem().getDomainContext().getPremises().values().iterator();
		while(iterPremises.hasNext()){
			Premise prem=iterPremises.next();
			premises.put(prem.getID(), prem);
		}
		Premise firstPremise=premises.values().iterator().next();
		firstPremise.setContent(firstPremise.getContent()+"aa");
		premises.put(firstPremise.getID(), firstPremise);
		Premise prem=new Premise(100, "", "100");
		premises.put(prem.getID(), prem);
		ArrayList<Solution> solutions=firstCase.getSolutions();
		Justification justification=new Justification("justification");
		DomainCase domCase=new DomainCase(new Problem(new DomainContext(premises)), solutions, justification);
		
		boolean added=cbr.addCase(domCase);
		if(added)
			System.out.println("Operability: case added");
		else System.out.println("Operability: case NOT added");
			
		
		ArrayList<SimilarDomainCase> similarCases=cbr.retrieveAndRetain(domCase, 0f);
		assertTrue(similarCases.get(0).getCaseb().equals(domCase));
		similarCases=cbr.retrieveAndRetain(firstCase, 0f);
		assertTrue(similarCases.get(0).getCaseb().equals(firstCase));
	}
	
//	public static Test suite (){
//		TestSuite suite= new TestSuite("All Domain CBR Tests");
//		suite.addTest(DomainVerTest.suite());
//		//suite.addTest(junit.tests.AllTests.suite());
//		return suite;
//	}

}
