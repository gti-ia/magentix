package es.upv.dsic.gti_ia.argAgents.domainCBR;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import testMAS.CreatePartitions;


import domainResources.Attribute;
import domainResources.Case;
import domainResources.Category;
import domainResources.Ticket;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Conclusion;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.DomainCase;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.DomainContext;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Justification;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Premise;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Problem;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Solution;

public class Convert2Generic {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		convertTestCases2TestDomCases();
//		int nOperators=9;
//		ArrayList<String> domainFiles=new ArrayList<String>();
//		for(int cases=5;cases<=45;cases+=5){
//			
//				domainFiles=new ArrayList<String>();
//				for(int i=0;i<nOperators;i++){
//					domainFiles.add("partitionsInc/part"+cases+"cas"+i+"op.dat");
//					
//					convertCasesTickets2DomCasesGeneric("partitionsInc/part"+cases+"cas"+i+"op.dat","partitionsGeneric/part"+cases+"cas"+i+"op.dat");
//				}
//		}
		
		
		
	}
	
	private static void convertCasesTickets2DomCasesGeneric(String originalFile, String destFile){
		
		ArrayList<Case> cases=readCases(originalFile);
		ArrayList<DomainCase> domCases=new ArrayList<DomainCase>();
		
		Iterator<Case> iterCases=cases.iterator();
		while(iterCases.hasNext()){
			Case aCase=iterCases.next();
			DomainCase domCase=new DomainCase(new Problem(new DomainContext(convertAttributesToPremises(aCase.getAttributes(), aCase.getCategoryNode()))),
					convertSolutions(aCase.getSolutions()), new Justification());
			domCases.add(domCase);
			
		}
		
		writeDomainCases(domCases, destFile);
		
	}
	
	private static void convertTestCases2TestDomCases(){
		Vector<Case> casesTest = CreatePartitions.readCasesFile("Helpdesk-Cases.dat");
		System.out.println("cases test= "+casesTest.size());
		ArrayList<DomainCase> domCases=new ArrayList<DomainCase>();
		Iterator<Case> ite = casesTest.iterator();
		while(ite.hasNext()){
			Case aCase = ite.next();
			
			DomainCase domCase=new DomainCase(new Problem(new DomainContext(convertAttributesToPremises(aCase.getAttributes(), aCase.getCategoryNode()))),
					convertSolutions(aCase.getSolutions()), new Justification());
			domCases.add(domCase);
		}
		System.out.println("domcases test= "+domCases.size());
		writeDomainCases(domCases, "Helpdesk-DomainCases.dat");
	}
	
	private static ArrayList<Case> readCases(String filePath){
		ArrayList<Case> cases=new ArrayList<Case>();
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath));
			
			// Read first object
			Object aux = ois.readObject();
			
			// While there are objects
			while (aux!=null){
			    if(aux instanceof Case){
			        Case aCase=(Case) aux;
			        cases.add(aCase);
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
		
		return cases;
	}
	
	
	private static void writeDomainCases(ArrayList<DomainCase> domCases, String filePath){
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath));
		
			Iterator<DomainCase> iterCases=domCases.iterator();
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
	 * Converts a Map of {@link Attribute} in a HashMap of {@link Premise}. With the category as a premise with id 0.
	 * @param attributes Map of {@link Attribute} to convert to a HashMap of {@link Premise}
	 * @param category tipification node
	 * @return a HashMap of {@link Premise} with the category as a premise with id 0
	 */
	private static HashMap<Integer, Premise> convertAttributesToPremises(Map<Integer,Attribute> attributes, Category category){
		HashMap<Integer, Premise> premises=new HashMap<Integer, Premise>();
		
		//convert Attributes to Premises
		Iterator<Attribute> iterAttribute= attributes.values().iterator();
		while(iterAttribute.hasNext()){
			Attribute attr=iterAttribute.next();
			Premise premise=new Premise(attr.getAskedQuestion().getQuestionID(), attr.getAskedQuestion().getQuestionDesc(), attr.getAnswer());
			premises.put(premise.getID(), premise);
		}
		//put the tipification node as a Premise with identifier 0
		Premise tipificationPremise=
			new Premise(0,category.getTipification(), String.valueOf(category.getIdTipi()));
		premises.put(0, tipificationPremise);
		
		return premises;
	}
	
	private static ArrayList<Solution> convertSolutions(HashMap<Integer,domainResources.Solution> iniSolutions){
		ArrayList<Solution> finalSolutions=new ArrayList<Solution>();
		Iterator<domainResources.Solution> iterIniSols=iniSolutions.values().iterator();
		while(iterIniSols.hasNext()){
			domainResources.Solution iniSol=iterIniSols.next();
			Solution sol=new Solution(new Conclusion(iniSol.getSolutionID(), iniSol.getSolutionDesc()), iniSol.getPromotedValue(), iniSol.getTimesUsed());
			finalSolutions.add(sol);
		}
		
		return finalSolutions;
	}

}
