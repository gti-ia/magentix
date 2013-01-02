package Argumentation_Example;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Conclusion;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.DomainCase;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.DomainContext;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Justification;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Premise;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Problem;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Solution;

public class CreateDemoPartitions {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Premise p0=new Premise(0, "", "3002");
		Premise p100=new Premise(100, "", "si");
		Premise p100b=new Premise(100, "", "no");
		Premise p101=new Premise(101, "", "si");
		Premise p101b=new Premise(101, "", "no");
		Premise p102=new Premise(102, "", "si");
		Premise p102b=new Premise(102, "", "no");
		Premise p103=new Premise(103, "", "si");
		Premise p103b=new Premise(103, "", "no");
		
		HashMap<Integer, Premise> premises1=new HashMap<Integer, Premise>();
		premises1.put(0, p0);
		premises1.put(100, p100b);
		Solution sol1=new Solution(new Conclusion(3001, "General network problem, contact your administrator"), 
				"Speed", 1);		
		DomainCase domCase1=createDomCase(premises1, sol1);
		
		HashMap<Integer, Premise> premises2=new HashMap<Integer, Premise>();
		premises2.put(0, p0);
		premises2.put(101, p101);
		premises2.put(102, p102);
		premises2.put(103, p103);
		Solution sol2=new Solution(new Conclusion(3002, "Reconect to your network"), 
				"Savings", 1);		
		DomainCase domCase2=createDomCase(premises2, sol2);
		
		HashMap<Integer, Premise> premises3=new HashMap<Integer, Premise>();
		premises3.put(0, p0);
		premises3.put(101, p101b);
		premises3.put(102, p102b);
		premises3.put(103, p103);
		Solution sol3=new Solution(new Conclusion(3003, "Activate your wifi"), 
				"Savings", 1);		
		DomainCase domCase3=createDomCase(premises3, sol3);
		
		HashMap<Integer, Premise> premises4=new HashMap<Integer, Premise>();
		premises4.put(0, p0);
		premises4.put(100, p100);
		premises4.put(101, p101);
		premises4.put(102, p102b);
		premises4.put(103, p103);
		Solution sol4=new Solution(new Conclusion(3004, "Reset your computer and if it doesn't work, reconfigure your wifi card"), 
				"Quality", 1);		
		DomainCase domCase4=createDomCase(premises4, sol4);
		
		Vector<DomainCase> domCases0=new Vector<DomainCase>();
		Vector<DomainCase> domCases1=new Vector<DomainCase>();
		Vector<DomainCase> domCases2=new Vector<DomainCase>();
		Vector<DomainCase> domCases3=new Vector<DomainCase>();
		Vector<DomainCase> domCases4=new Vector<DomainCase>();
		Vector<DomainCase> domCases5=new Vector<DomainCase>();
		Vector<DomainCase> domCases6=new Vector<DomainCase>();
		Vector<DomainCase> domCases7=new Vector<DomainCase>();
		Vector<DomainCase> domCases8=new Vector<DomainCase>();
		Vector<DomainCase> domCases9=new Vector<DomainCase>();
		
		
		domCases0.add(domCase1);
		domCases0.add(domCase2);
		
		domCases1.add(domCase1);
		domCases1.add(domCase2);
		
		domCases2.add(domCase1);
		domCases2.add(domCase3);
		
		domCases3.add(domCase2);
		domCases3.add(domCase3);
		
		domCases4.add(domCase2);
		
		domCases5.add(domCase2);
		
		domCases6.add(domCase3);
		domCases6.add(domCase4);
		
		domCases7.add(domCase3);
		domCases7.add(domCase4);
		
		domCases8.add(domCase3);
		domCases8.add(domCase4);
		
		domCases9.add(domCase1);
		domCases9.add(domCase4);
		
		CreatePartitions.writeDomainCases(domCases0, "testArgumentation/partitionsInc/demoCases0.dat");
		CreatePartitions.writeDomainCases(domCases1, "testArgumentation/partitionsInc/demoCases1.dat");
		CreatePartitions.writeDomainCases(domCases2, "testArgumentation/partitionsInc/demoCases2.dat");
		CreatePartitions.writeDomainCases(domCases3, "testArgumentation/partitionsInc/demoCases3.dat");
		CreatePartitions.writeDomainCases(domCases4, "testArgumentation/partitionsInc/demoCases4.dat");
		CreatePartitions.writeDomainCases(domCases5, "testArgumentation/partitionsInc/demoCases5.dat");
		CreatePartitions.writeDomainCases(domCases6, "testArgumentation/partitionsInc/demoCases6.dat");
		CreatePartitions.writeDomainCases(domCases7, "testArgumentation/partitionsInc/demoCases7.dat");
		CreatePartitions.writeDomainCases(domCases8, "testArgumentation/partitionsInc/demoCases8.dat");
		CreatePartitions.writeDomainCases(domCases9, "testArgumentation/partitionsInc/demoCases9.dat");
		
		
		
	}

	
	private static DomainCase createDomCase(HashMap<Integer, Premise> premises, Solution sol){
		
		
		Problem problem=new Problem(new DomainContext(premises));
		
		ArrayList<Solution> solutions=new ArrayList<Solution>();
		solutions.add(sol);
		
		Justification justification=new Justification("");
		
		DomainCase domCase=new DomainCase(problem, solutions, justification);
		
		return domCase;
	}
	
	private static ArrayList<DomainCase> getDomCases(){
		ArrayList<DomainCase> domCases=new ArrayList<DomainCase>();
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream("testArgumentation/Helpdesk-DomainCases.dat"));

			// Read first object
			Object aux = ois.readObject();

			// While there are objects
			while (aux != null) {
				if (aux instanceof DomainCase) {
					DomainCase aCase = (DomainCase) aux;
					domCases.add(aCase);
					
					Iterator<Premise> iterPrems=aCase.getProblem().getDomainContext().getPremises().values().iterator();
					String strPrems="";
					while(iterPrems.hasNext()){
						Premise p=iterPrems.next();
						strPrems+=p.getID()+":"+p.getContent()+" || ";
					}
					System.out.println(strPrems);
					System.out.println(aCase.getSolutions().get(0).getConclusion().getID()+": "+
							aCase.getSolutions().get(0).getConclusion().getDescription());
					System.out.println();
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

		//System.out.println("domain-cases=" + (domCases.size()));
		
		return domCases;

	}
	
	
}
