package Argumentation_Example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;


import es.upv.dsic.gti_ia.argAgents.domainCBR.Attribute;
import es.upv.dsic.gti_ia.argAgents.domainCBR.Category;
import es.upv.dsic.gti_ia.argAgents.domainCBR.DomainCBR;
import es.upv.dsic.gti_ia.argAgents.domainCBR.OWLDomainParser;
import es.upv.dsic.gti_ia.argAgents.domainCBR.Ticket;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.ArgumentCase;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Conclusion;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.DomainCase;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.DomainContext;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Justification;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Premise;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Problem;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Solution;



public class CreatePartitions {

//	public static void createDomCasesOWLPartitions(int nCases, int nRepetition){
//		try {
//		OWLDomainParser owlDomParser=new OWLDomainParser();
//		Vector<Case> allCases=owlDomParser.parseDomainOntologyInCases("Helpdesk-Cases.owl");
//		
//		
//		//ArrayList<ArrayList<Case>> partitionsCases=new ArrayList<ArrayList<Case>>();
//		
//		for(int op=0;op<10;op++){
//			int inc=2;
//			for(int cases=2;cases<=nCases;cases+=inc){
//				
//				for(int repetition=0;repetition<nRepetition;repetition++){
//					Vector<Case> currentPartition=new Vector<Case>();
//					ArrayList<Integer> usedIndex=new ArrayList<Integer>();
//					for(int i=0;i<cases;i++){
//						int index=(int)(Math.random()*allCases.size());
//						while(usedIndex.contains(index)){
//							index=(int)(Math.random()*allCases.size());
//						}
//						Case aCase=allCases.get(index);
//						currentPartition.add(aCase);
//						usedIndex.add(index);
//					}
//					//partitionsCases.add(currentPartition);
//					
//					//save the list currentPartition in the given file, using the function of domain onto parser
//					
//					
//						owlDomParser.saveCasesInDomainOntology(currentPartition, "HelpdeskOnto.owl", 
//								"partitions/part"+cases+"cas"+repetition+"rep"+op+"op.owl");
////						owlDomParser.saveCasesInDomainOntology(currentPartition, "partitions/part"+cases+"cas"+repetition+"rep"+op+"op.owl", 
////							"partitions/part"+cases+"cas"+repetition+"rep"+op+"op.owl");
//					
//					
//				}
//				
//				
//				if(inc==3) inc=2;
//				else inc=3;
//			}
//		}
//		
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//	}
	
//	public static void createDomCasesPartitions(int nCases, int nRepetition, int nOperators){
//		try {
//		
//		Vector<Case> allCases=readCasesFile("Helpdesk-Cases.dat");
//		
//		//ArrayList<ArrayList<Case>> partitionsCases=new ArrayList<ArrayList<Case>>();
//		
//		for(int op=0;op<nOperators;op++){
////			int inc=2;
//			for(int cases=5;cases<=nCases;cases+=5){
//				
//				for(int repetition=0;repetition<nRepetition;repetition++){
//					Vector<Case> currentPartition=new Vector<Case>();
//					ArrayList<Integer> usedIndex=new ArrayList<Integer>();
//					for(int i=0;i<cases;i++){
//						int index=(int)(Math.random()*allCases.size());
//						while(usedIndex.contains(index)){
//							index=(int)(Math.random()*allCases.size());
//						}
//						Case aCase=allCases.get(index);
//						currentPartition.add(aCase);
//						usedIndex.add(index);
//					}
//					//partitionsCases.add(currentPartition);
//					
//					//save the list currentPartition in the given file, using the function of domain onto parser
//					
//					
//						writeCasesFile(currentPartition, 
//								"partitions/part"+cases+"cas"+repetition+"rep"+op+"op.dat");
//					
//				}
//				
////				if(inc==3) inc=2;
////				else inc=3;
//			}
//		}
//		
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//	}
//	
	public static void createDomCasesPartitionsIncremental(int nCases, int nOperators){
		try {
		
		Vector<DomainCase> allCases=readDomainCasesFile("Helpdesk-DomainCases.dat");
		
		//ArrayList<ArrayList<Case>> partitionsCases=new ArrayList<ArrayList<Case>>();
		
		for(int op=0;op<nOperators;op++){
			ArrayList<DomainCase> currentPartition=new ArrayList<DomainCase>();
			ArrayList<Integer> usedIndex=new ArrayList<Integer>();
			for(int cases=5;cases<=nCases;cases+=5){
				
				for(int i=0;i<5;i++){//5 cases per incremental iteration
					int index=(int)(Math.random()*allCases.size());
					while(usedIndex.contains(index)){
						index=(int)(Math.random()*allCases.size());
					}
					DomainCase aCase=allCases.get(index);
					currentPartition.add(aCase);
					usedIndex.add(index);
				}
				
				
				//save the list currentPartition in the given file, using the function of domain onto parser
				
				
					writeDomainCases(currentPartition, 
							"partitionsInc/part"+cases+"cas"+op+"op.dat");
				
				
			}
		}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
//	
//	public static void createDomCasesPartitionsIncOrdered(int nCases, int nOperators){
//		try {
//		
//		Vector<Case> allCases=readCasesFile("Helpdesk-CasesCatOrder.dat");
//		
//		//ArrayList<ArrayList<Case>> partitionsCases=new ArrayList<ArrayList<Case>>();
//		
//		for(int op=0;op<nOperators;op++){
//			Vector<Case> currentPartition=new Vector<Case>();
//			for(int cases=5;cases<=nCases;cases+=5){
//				
//				for(int i=0;i<cases;i++){//5 cases per incremental iteration
//					Case aCase=allCases.get(i);
//					currentPartition.add(aCase);
//				}
//				
//				
//				//save the list currentPartition in the given file, using the function of domain onto parser
//				
//				
//					writeCasesFile(currentPartition, 
//							"partitionsInc/partExpert"+cases+"cas"+op+"op.dat");
//				
//				
//			}
//		}
//		
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//	}
//	
	public static void createArgCasesPartitionsIncremental(String file, int inc, int operator, int nCases){
		try {
		
		Vector<ArgumentCase> allCases=readArgCasesFile(file);
		
		if (allCases.size() < nCases)
			nCases = allCases.size();
		
		//ArrayList<ArrayList<Case>> partitionsCases=new ArrayList<ArrayList<Case>>();
		
		Vector<ArgumentCase> currentPartition=new Vector<ArgumentCase>();
		writeArgCasesFile(currentPartition, 
				"partArgInc/partArg"+0+"cas"+operator+"op.dat");
		ArrayList<Integer> usedIndex=new ArrayList<Integer>();
		for(int cases=2;cases<=nCases;cases+=inc){
				
			for(int i=0;i<inc;i++){//inc cases per incremental iteration
				int index=(int)(Math.random()*allCases.size());
				while(usedIndex.contains(index)){
					index=(int)(Math.random()*allCases.size());
				}
				ArgumentCase aCase=allCases.get(index);
				currentPartition.add(aCase);
				usedIndex.add(index);
			}
				
				
			//save the list currentPartition in the given file, using the function of domain onto parser
				
			writeArgCasesFile(currentPartition, 
						"partArgInc/partArg"+cases+"cas"+operator+"op.dat");
				
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void createEmptyArgCasesPartitions(ArrayList<String> destFileNames){
		
		for(int i=0;i<destFileNames.size();i++){
			try {
				FileWriter fstream = new FileWriter(destFileNames.get(i),false);
				BufferedWriter outFile = new BufferedWriter(fstream);
				//Close the output stream
				outFile.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void createDomCases(String resultFileName){
		try {
			OWLDomainParser owlDomParser=new OWLDomainParser();
			Vector<es.upv.dsic.gti_ia.argAgents.domainCBR.Case> allCases=owlDomParser.parseDomainOntologyInCases("Helpdesk-Cases.owl");
			ArrayList<DomainCase> domCases=new ArrayList<DomainCase>();
			
			Iterator<es.upv.dsic.gti_ia.argAgents.domainCBR.Case> iterCases=allCases.iterator();
			while(iterCases.hasNext()){
				es.upv.dsic.gti_ia.argAgents.domainCBR.Case cas=iterCases.next();
				DomainCase domCase=new DomainCase(new Problem(new DomainContext(convertAttributesToPremises(cas.getAttributes(), cas.getCategoryNode()))),
						convertSolutions(cas.getSolutions()), new Justification());
				domCases.add(domCase);
				
			}
			
			writeDomainCases(domCases, resultFileName);
			
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	private static ArrayList<Solution> convertSolutions(HashMap<Integer,es.upv.dsic.gti_ia.argAgents.domainCBR.Solution> iniSolutions){
		ArrayList<Solution> finalSolutions=new ArrayList<Solution>();
		Iterator<es.upv.dsic.gti_ia.argAgents.domainCBR.Solution> iterIniSols=iniSolutions.values().iterator();
		while(iterIniSols.hasNext()){
			es.upv.dsic.gti_ia.argAgents.domainCBR.Solution iniSol=iterIniSols.next();
			Solution sol=new Solution(new Conclusion(iniSol.getSolutionID(), iniSol.getSolutionDesc()), iniSol.getPromotedValue(), iniSol.getTimesUsed());
			finalSolutions.add(sol);
		}
		
		return finalSolutions;
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
	
	public static void createTestTickets(String resultFileName,int nTickets){
		
		try{
			OWLDomainParser owlDomainParser= new OWLDomainParser();
			Vector<Ticket> allTickets=new Vector<Ticket>();
			allTickets = owlDomainParser.parseDomainOntology("Helpdesk-Full.owl");
			
			// Create file 
			FileWriter fstream = new FileWriter(resultFileName,false);
			BufferedWriter outFile = new BufferedWriter(fstream);
			
			if(nTickets >= allTickets.size())
			{
				for (int i=0; i<allTickets.size();i++){
					outFile.write(Integer.toString(i));
					outFile.newLine();
				}
			
				for(int i=allTickets.size();i<nTickets;i++){
					int index=(int)(Math.random()*allTickets.size());
					outFile.write(Integer.toString(index));
					outFile.newLine();
				}
			}
			else
			{
				for (int i=0; i<nTickets;i++){
					outFile.write(Integer.toString(i));
					outFile.newLine();
				}
			}
			
			//Close the output stream
			outFile.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		
	}
	
//	public static void createExpertsOWLPartitions(){
//		OWLDomainParser owlDomainParser=new OWLDomainParser();
//		try {
//			
//			ArrayList<Integer> categoriesAdded=new ArrayList<Integer>();
//			Vector<Case> casesPartition=new Vector<Case>();
//			Vector<Case> aditionalCases=new Vector<Case>();
//			Vector<Case> cases=owlDomainParser.parseDomainOntologyInCases("Helpdesk-Cases.owl");
//			
//			Iterator<Case> iterCases=cases.iterator();
//			while(iterCases.hasNext()){
//				Case acase=iterCases.next();
//				int categoryID=acase.getCategoryNode().getIdTipi();
//				if(!categoriesAdded.contains(categoryID)){
//					categoriesAdded.add(categoryID);
//					casesPartition.add(acase);
//					System.out.println("category="+categoryID+" sol="+acase.getSolutions().values().iterator().next().getSolutionID());
//				}
//				else
//					aditionalCases.add(acase);
//				
//			}
//			System.out.println(casesPartition.size()+" cases in partition");
//			owlDomainParser.saveCasesInDomainOntology(casesPartition, "HelpdeskOnto.owl", "partitions/expert26.owl");
//			
//			int nCases=40;
////			int inc=2;
//			for(int icases=30;icases<=nCases;icases+=5){
//				
//				
//				Vector<Case> currentPartition=new Vector<Case>();
//				Iterator<Case> iterCasesPart=casesPartition.iterator();
//				while(iterCasesPart.hasNext()){
//					currentPartition.add(iterCasesPart.next());
//				}
//				ArrayList<Integer> usedIndex=new ArrayList<Integer>();
//				for(int i=0;i<icases-casesPartition.size();i++){
//					int index=(int)(Math.random()*aditionalCases.size());
//					while(usedIndex.contains(index)){
//						index=(int)(Math.random()*aditionalCases.size());
//					}
//					Case aCase=aditionalCases.get(index);
//					currentPartition.add(aCase);
//					usedIndex.add(index);
//				}
//				//partitionsCases.add(currentPartition);
//				
//				//save the list currentPartition in the given file, using the function of domain onto parser
//				
//				
//				owlDomainParser.saveCasesInDomainOntology(currentPartition, "HelpdeskOnto.owl", 
//							"partitions/expert"+icases+".owl");
//				
////				if(inc==3) inc=2;
////				else inc=3;
//			}
//		
//		
//		
//		} catch (Exception e) {
//			
//			e.printStackTrace();
//		}
//	}
//	
//	public static void createExpertsPartitions(){
//		
//		try {
//			
//			ArrayList<Integer> categoriesAdded=new ArrayList<Integer>();
//			Vector<Case> casesPartition=new Vector<Case>();
//			Vector<Case> aditionalCases=new Vector<Case>();
//			Vector<Case> cases=readCasesFile("Helpdesk-Cases.dat");
//			
//			Iterator<Case> iterCases=cases.iterator();
//			while(iterCases.hasNext()){
//				Case acase=iterCases.next();
//				int categoryID=acase.getCategoryNode().getIdTipi();
//				if(!categoriesAdded.contains(categoryID)){
//					categoriesAdded.add(categoryID);
//					casesPartition.add(acase);
//					System.out.println("category="+categoryID+" sol="+acase.getSolutions().values().iterator().next().getSolutionID());
//				}
//				else
//					aditionalCases.add(acase);
//				
//			}
//			System.out.println(casesPartition.size()+" cases in partition");
//			writeCasesFile(casesPartition, "partitions/expert26.dat");
//			
//			int nCases=40;
////			int inc=2;
//			for(int icases=30;icases<=nCases;icases+=5){
//				
//				
//				Vector<Case> currentPartition=new Vector<Case>();
//				Iterator<Case> iterCasesPart=casesPartition.iterator();
//				while(iterCasesPart.hasNext()){
//					currentPartition.add(iterCasesPart.next());
//				}
//				ArrayList<Integer> usedIndex=new ArrayList<Integer>();
//				for(int i=0;i<icases-casesPartition.size();i++){
//					int index=(int)(Math.random()*aditionalCases.size());
//					while(usedIndex.contains(index)){
//						index=(int)(Math.random()*aditionalCases.size());
//					}
//					Case aCase=aditionalCases.get(index);
//					currentPartition.add(aCase);
//					usedIndex.add(index);
//				}
//				//partitionsCases.add(currentPartition);
//				
//				//save the list currentPartition in the given file, using the function of domain onto parser
//				
//				
//				writeCasesFile(currentPartition,"partitions/expert"+icases+".dat");
//				
////				if(inc==3) inc=2;
////				else inc=3;
//			}
//		
//		
//		
//		} catch (Exception e) {
//			
//			e.printStackTrace();
//		}
//	}
//	
//	public static Vector<Case> readCasesFile(String fileName){
//		int ncases=0;
//		Vector<Case> cases=new Vector<Case>();
//		try {
//			
//			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));
//			
//			// Read first object
//			Object aux = ois.readObject();
//			
//			// While there are objects
//			while (aux!=null){
//			    if(aux instanceof Case){
//			    	ncases++;
//			    	Case acase=(Case) aux;
//			    	cases.add(acase);
//			        
//			    }
//			    aux = ois.readObject();
//			}
//			ois.close();
//		
//		
//		} catch (EOFException e) {
//			
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		}
//		
//		System.out.println("cases="+ncases);
//		
//		return cases;
//		
//	}
//	
	public static Vector<DomainCase> readDomainCasesFile(String fileName){
		int ncases=0;
		Vector<DomainCase> cases=new Vector<DomainCase>();
		try {
			
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));
			
			// Read first object
			Object aux = ois.readObject();
			
			// While there are objects
			while (aux!=null){
			    if(aux instanceof DomainCase){
			    	ncases++;
			    	DomainCase acase=(DomainCase) aux;
			    	cases.add(acase);
			        
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
		
		System.out.println("cases="+ncases);
		
		return cases;
		
	}
	
	public static Vector<ArgumentCase> readArgCasesFile(String fileName){
		int ncases=0;
		Vector<ArgumentCase> cases=new Vector<ArgumentCase>();
		try {
			
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));
			
			// Read first object
			Object aux = ois.readObject();
			
			// While there are objects
			while (aux!=null){
			    if(aux instanceof ArgumentCase){
			    	ncases++;
			    	ArgumentCase acase=(ArgumentCase) aux;
			    	cases.add(acase);
			        
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
		
		System.out.println("Argument-cases="+ncases);
		
		return cases;
		
	}
	
//	public static Vector<Ticket> readTicketsFile(String fileName){
//		int ntickets=0;
//		Vector<Ticket> tickets=new Vector<Ticket>();
//		try {
//			
//			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));
//			
//			// Read first object
//			Object aux = ois.readObject();
//			
//			// While there are objects
//			while (aux!=null){
//			    if(aux instanceof Ticket){
//			    	ntickets++;
//			    	Ticket ticket=(Ticket) aux;
//			    	tickets.add(ticket);
//			        
//			    }
//			    aux = ois.readObject();
//			}
//			ois.close();
//		
//		
//		} catch (EOFException e) {
//			
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		}
//		
//		System.out.println("tickets="+ntickets);
//		
//		return tickets;
//		
//	}
//	
//	public static void writeCasesFile(Vector<Case> cases, String fileName){
//		try {
//			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
//			Iterator<Case> iterCases=cases.iterator();
//			while(iterCases.hasNext()){
//				Case aCase=iterCases.next();
//				oos.writeObject(aCase);
//			}
//		
//		} catch (FileNotFoundException e) {
//			
//			e.printStackTrace();
//		} catch (IOException e) {
//			
//			e.printStackTrace();
//		}
//	}
	
	public static void writeArgCasesFile(Vector<ArgumentCase> cases, String fileName){
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
			Iterator<ArgumentCase> iterCases=cases.iterator();
			while(iterCases.hasNext()){
				ArgumentCase aCase=iterCases.next();
				oos.writeObject(aCase);
			}
		
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
//	public static void writeTicketsFile(Vector<Ticket> tickets, String fileName){
//		try {
//			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
//			Iterator<Ticket> iterTickets=tickets.iterator();
//			while(iterTickets.hasNext()){
//				Ticket ticket=iterTickets.next();
//				oos.writeObject(ticket);
//			}
//		
//		} catch (FileNotFoundException e) {
//			
//			e.printStackTrace();
//		} catch (IOException e) {
//			
//			e.printStackTrace();
//		}
//	}
//	
	
//	public static Vector<Case> createOrderedCaseBase(){
//		Vector<Case> cases=readCasesFile("Helpdesk-Cases.dat");
//		HashMap<Integer,ArrayList<Case>> casesByCategory=new HashMap<Integer, ArrayList<Case>>();
//		
//		Iterator<Case> iterCases=cases.iterator();
//		while(iterCases.hasNext()){
//			Case acase=iterCases.next();
//			int categoryID=acase.getCategoryNode().getIdTipi();
//			ArrayList<Case> casesList=casesByCategory.get(categoryID);
//			if(casesList!=null && !casesList.isEmpty()){
//				casesList.add(acase);
//			}
//			else{
//				casesList=new ArrayList<Case>();
//				casesList.add(acase);
//				casesByCategory.put(categoryID, casesList);
//			}
//		}
//		HashMap<Integer,ArrayList<Integer>> categoryQuantity= new HashMap<Integer, ArrayList<Integer>>();
//		Iterator<ArrayList<Case>> iterCasesLists=casesByCategory.values().iterator();
//		while(iterCasesLists.hasNext()){
//			ArrayList<Case> list= iterCasesLists.next();
//			ArrayList<Integer> categoriesList=categoryQuantity.get(list.size());
//			if(categoriesList!=null && !categoriesList.isEmpty())
//				categoriesList.add(list.get(0).getCategoryNode().getIdTipi());
//			else{
//				categoriesList=new ArrayList<Integer>();
//				categoriesList.add(list.get(0).getCategoryNode().getIdTipi());
//				categoryQuantity.put(list.size(), categoriesList);
//			}
//			
//		}
//		
//		ArrayList<Integer> categoriesOrdered=new ArrayList<Integer>();
//		Iterator<ArrayList<Integer>> iterCategoryQuantity= categoryQuantity.values().iterator();
//		while(iterCategoryQuantity.hasNext()){
//			ArrayList<Integer> categories= iterCategoryQuantity.next();
//			Iterator<Integer> iterCategories=categories.iterator();
//			while(iterCategories.hasNext()){
//				int category=iterCategories.next();
//				if(categoriesOrdered.isEmpty())
//					categoriesOrdered.add(category);
//				else 
//					categoriesOrdered.add(0, category);
//			
//			}
//		}
//		
//		Vector<Case> casesByCategoryOrdered=new Vector<Case>();
//		Iterator<Integer> iterCategoriesOrdered=categoriesOrdered.iterator();
//		while(iterCategoriesOrdered.hasNext()){
//			int category=iterCategoriesOrdered.next();
//			casesByCategoryOrdered.addAll(casesByCategory.get(category));
//		}
//		
//		Iterator<Case> iterCasesByCategoryOrdered=casesByCategoryOrdered.iterator();
//		while(iterCasesByCategoryOrdered.hasNext()){
//			Case acase=iterCasesByCategoryOrdered.next();
//			System.out.println(acase.getCategoryNode().getIdTipi());
//		}
//		
//		return casesByCategoryOrdered;
//	
//	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
//		createDomCasesPartitions(40, 48, 9);
//		createExpertsPartitions();
//		
//		createTestData();
		
//		createDomCasesPartitionsIncremental(45, 9);
		
//		Vector<Case> cases=readCasesFile("partitionsInc/part10cas8op.dat");
//		Iterator<Case> iterCases=cases.iterator();
//		while(iterCases.hasNext()){
//			Case caseA=iterCases.next();
//			System.out.println(caseA.getCategoryNode().getIdTipi()+" "+caseA.getSolutions().values().iterator().next().getSolutionID());
//		}
		
//		createOrderedCaseBase();
//		writeCasesFile(createOrderedCaseBase(), "Helpdesk-CasesCatOrder.dat");
	
//		Vector<Vector<ArgumentCase>> argCases = new Vector<Vector<ArgumentCase>>();
//		Vector<ArgumentCase> argCasesCB = new Vector<ArgumentCase>();
//		Vector<Integer> argCasesSize = new Vector<Integer>();
//
//
//		for (int op = 0; op < 9; op++){
////			Vector<ArgumentCase> argCasesOp = new Vector<ArgumentCase>();
////			Vector<ArgumentCase> argCasesAux= new Vector<ArgumentCase>();
////			for (int pref = 0; pref <= 5; pref++){
////				if (pref == 1 || pref == 3 || pref == 4)
////					argCasesAux = readArgCasesFile("partArgInc/partArg"+18+"cas"+pref+"ValPref"+op+"op.dat");
////				else
////					argCasesAux = readArgCasesFile("partArgInc/partArg"+19+"cas"+pref+"ValPref"+op+"op.dat");
////				argCasesOp.addAll(argCasesAux);
////			}
////			System.out.println(argCasesOp.size());
////			writeArgCasesFile(argCasesOp, "partArgInc/argCasesFull" + op + "op.dat");
//			
//			ArgCBR argCBR = new ArgCBR("partArgInc/argCasesFull" + op + "op.dat", "partArgInc/argCases" + op + "op.dat");
//			
//			argCBR.doCache();
//		
//			
////			createArgCasesPartitionsIncremental("partArgInc/argCases" + op + "op.dat",2,op,20);
//			
//		}
		
//		createDomCasesPartitionsIncOrdered(45, 9);
		
//		createDomCases("Helpdesk-DomainCases.dat");
		
//		createDomCasesPartitionsIncremental(45, 15);
		
		ArrayList<String> argFileNames=new ArrayList<String>();
		for(int i=0;i<15;i++){
			argFileNames.add("partArgInc/partArg"+"Operator"+i+".dat");
		}
		createEmptyArgCasesPartitions(argFileNames);
	}

//	public static void createTestData(){
//		Vector<Ticket> ticketsTest = new Vector<Ticket>();
//		Vector<Case> casesTest = readCasesFile("Helpdesk-Cases.dat");
//		
//		Iterator<Case> ite = casesTest.iterator();
//		while(ite.hasNext()){
//			Case c = ite.next();
//			
//			int id = (int) System.currentTimeMillis();
//			if (id < 0) id = id * -1;
//		    
//			// WARNING: it is correct since Helpdesk-Cases has ONLY 1 SOLUTION per case
//			Ticket t = new Ticket(id, c.getCategoryNode(), c.getAttributes(), c.getProblemDesc(), 
//					c.getProject(), c.getSolvingGroups(), c.getSolvingOperators(), c.getSolutions());
//			ticketsTest.add(t);	
//		}
//		
//		writeTicketsFile(ticketsTest, "Helpdesk-Test.dat");
//	}
//	
//	public static Vector<Ticket> getTestTickets(){
//		Vector<Ticket> tickets=new Vector<Ticket>();
//		try {
//			tickets = readTicketsFile("Helpdesk-Test.dat");	
//		}catch (Exception e){//Catch exception if any
//			System.err.println("Error reading file: " + e.getMessage());
//			e.printStackTrace();
//		}
//		return tickets;
//	}
//	
	public static Vector<DomainCase> getTestDomainCases(){
		Vector<DomainCase> tickets=new Vector<DomainCase>();
		try {
			tickets = readDomainCasesFile("Helpdesk-DomainCases.dat");
			System.out.println("domain cases= "+tickets.size());
		}catch (Exception e){//Catch exception if any
			System.err.println("Error reading file: " + e.getMessage());
			e.printStackTrace();
		}
		return tickets;
	}

}
