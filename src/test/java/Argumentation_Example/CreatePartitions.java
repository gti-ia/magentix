package Argumentation_Example;

import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import es.upv.dsic.gti_ia.argAgents.knowledgeResources.ArgumentCase;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.DomainCase;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.SocialEntity;

/**
 * This class creates different partitions of domain-cases and argument-cases to
 * make tests
 * 
 * @author Jaume Jordan
 * 
 */
public class CreatePartitions {

	/**
	 * Creates the data files with the domain-cases serialized as Java Objects
	 * 
	 * @param nCases
	 *            Creates different files, from 0 to nCases
	 * @param nOperators
	 *            number of operators
	 */
	public static void createDomCasesPartitionsIncremental(int nCases, int nOperators) {
		try {

			Vector<DomainCase> allCases = readDomainCasesFile("testArgumentation/Helpdesk-DomainCases.dat");

			for (int op = 0; op < nOperators; op++) {
				Vector<DomainCase> currentPartition = new Vector<DomainCase>();
				ArrayList<Integer> usedIndex = new ArrayList<Integer>();
				for (int cases = 5; cases <= nCases; cases += 5) {

					for (int i = 0; i < 5; i++) {// 5 cases per incremental
													// iteration
						int index = (int) (Math.random() * allCases.size());
						while (usedIndex.contains(index)) {
							index = (int) (Math.random() * allCases.size());
						}
						DomainCase aCase = allCases.get(index);
						currentPartition.add(aCase);
						usedIndex.add(index);
					}

					// save the list currentPartition in the given file
					writeDomainCases(currentPartition, "testArgumentation/partitionsInc/part" + cases + "cas" + op + "op.dat");

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Creates the data files with the domain-cases, serialized as Java Objects.
	 * It takes a concrete order to create continuous and incremental
	 * partitions.
	 * 
	 * @param nCases
	 *            Creates different files, from 0 to nCases
	 * @param nOperators
	 *            number of operators
	 */
	public static void createDomCasesPartitionsContinued(int nCases, int nOperators) {
		try {

			Vector<DomainCase> allCases = readDomainCasesFile("testArgumentation/Helpdesk-DomainCases.dat");

			for (int op = 0; op < nOperators; op++) {
				System.out.println("Operator " + op);
				ArrayList<Integer> casesList = new ArrayList<Integer>();
				Vector<DomainCase> currentPartition = new Vector<DomainCase>();
				for (int cases = 0; cases < nCases; cases += 5) {
					System.out.println("Partition cases = " + (cases + 5));
					for (int i = cases; i < cases + 5; i++) {// 5 cases per incremental iteration
						int index = (op + i) % allCases.size();
						casesList.add(index);
						currentPartition.add(allCases.get(index));
						System.out.println(index);
					}
					System.out.println("current partition size = " + currentPartition.size());
					// save the list currentPartition in the given file
					writeDomainCases(currentPartition, "testArgumentation/partitionsInc/domCases" + (cases + 5) + "cas" + op + "op.dat");

				}
				if (op == 0) {
					Iterator<DomainCase> iterCases = currentPartition.iterator();
					while (iterCases.hasNext()) {
						DomainCase c = iterCases.next();
						System.out.println("tipiNode=" + c.getProblem().getDomainContext().getPremises().get(0).getContent() + " solID=" + c.getSolutions().get(0).getConclusion().getID());
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Creates the data files with the argument-cases of each
	 * {@link SocialEntity}, serialized as Java Objects
	 * 
	 * @param file
	 *            The original file with argument-cases to read (if any)
	 * @param inc
	 *            The number of cases that is added to each new file
	 * @param operator
	 *            number of operator
	 * @param nCases
	 *            Creates different files, from 0 to nCases
	 */
	public static void createArgCasesPartitionsIncremental(String file, int inc, int operator, int nCases) {
		try {

			Vector<ArgumentCase> allCases = readArgCasesFile(file);

			if (allCases.size() < nCases)
				nCases = allCases.size();

			Vector<ArgumentCase> currentPartition = new Vector<ArgumentCase>();
			writeArgCasesFile(currentPartition, "testArgumentation/partArgInc/partArg" + 0 + "cas" + operator + "op.dat");
			ArrayList<Integer> usedIndex = new ArrayList<Integer>();
			for (int cases = 2; cases <= nCases; cases += inc) {

				for (int i = 0; i < inc; i++) {// inc number of cases per incremental
												// iteration
					int index = (int) (Math.random() * allCases.size());
					while (usedIndex.contains(index)) {
						index = (int) (Math.random() * allCases.size());
					}
					ArgumentCase aCase = allCases.get(index);
					currentPartition.add(aCase);
					usedIndex.add(index);
				}

				// save the list currentPartition in the given file
				writeArgCasesFile(currentPartition, "testArgumentation/partArgInc/partArg" + cases + "cas" + operator + "op.dat");

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Create empty partitions of argument-cases with the given file names
	 * 
	 * @param destFileNames
	 *            file names to create empty partitions
	 */
	public static void createEmptyArgCasesPartitions(ArrayList<String> destFileNames) {

		for (int i = 0; i < destFileNames.size(); i++) {
			try {
				FileWriter fstream = new FileWriter(destFileNames.get(i), false);
				BufferedWriter outFile = new BufferedWriter(fstream);
				// Close the output stream
				outFile.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Write the given domain-cases as a {@link Serializable} Java Objects in
	 * the given file path
	 * 
	 * @param domCases
	 *            list of domain-cases to write
	 * @param filePath
	 *            file path to write the domain-cases
	 */
	public static void writeDomainCases(Vector<DomainCase> domCases, String filePath) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath));

			Iterator<DomainCase> iterCases = domCases.iterator();
			while (iterCases.hasNext()) {
				DomainCase aCase = iterCases.next();
				oos.writeObject(aCase);
			}

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	/**
	 * Reads in a {@link Vector} of {@link DomainCase} the contents of a file
	 * 
	 * @param fileName
	 *            The file to read
	 * @return {@link Vector} of {@link DomainCase}
	 */
	public static Vector<DomainCase> readDomainCasesFile(String fileName) {
		Vector<DomainCase> cases = new Vector<DomainCase>();
		try {

			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));

			// Read first object
			Object aux = ois.readObject();

			// While there are objects
			while (aux != null) {
				if (aux instanceof DomainCase) {
					DomainCase acase = (DomainCase) aux;
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

		return cases;

	}

	/**
	 * Reads in a {@link Vector} of {@link ArgumentCase} the contents of a file
	 * 
	 * @param fileName
	 *            The file to read
	 * @return {@link Vector} of {@link ArgumentCase}
	 */
	public static Vector<ArgumentCase> readArgCasesFile(String fileName) {
		Vector<ArgumentCase> cases = new Vector<ArgumentCase>();
		try {

			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));

			// Read first object
			Object aux = ois.readObject();

			// While there are objects
			while (aux != null) {
				if (aux instanceof ArgumentCase) {
					ArgumentCase acase = (ArgumentCase) aux;
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


		return cases;

	}

	/**
	 * Writes a {@link Vector} of {@link ArgumentCase} in a file
	 * 
	 * @param cases
	 *            {@link Vector} of {@link ArgumentCase}s to write
	 * @param fileName
	 *            File to write
	 */
	public static void writeArgCasesFile(Vector<ArgumentCase> cases, String fileName) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
			Iterator<ArgumentCase> iterCases = cases.iterator();
			while (iterCases.hasNext()) {
				ArgumentCase aCase = iterCases.next();
				oos.writeObject(aCase);
			}

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	/**
	 * Returns a {@link Vector} of all the domain-cases of the file
	 * Helpdesk-DomainCases.dat
	 * 
	 * @return a {@link Vector} of all the domain-cases of the file
	 *         Helpdesk-DomainCases.dat
	 */
	public static Vector<DomainCase> getTestDomainCases() {
		Vector<DomainCase> domCases = new Vector<DomainCase>();
		try {
			domCases = readDomainCasesFile("testArgumentation/Helpdesk-DomainCases.dat");
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error reading file: " + e.getMessage());
			e.printStackTrace();
		}
		return domCases;
	}

	public static void main(String[] args) {

		createDomCasesPartitionsContinued(45,9);
		
//		Vector<DomainCase> domCases=readDomainCasesFile("testArgumentation/Helpdesk-DomainCases2.dat");
//		Iterator<DomainCase> iter=domCases.iterator();
//		while(iter.hasNext()){
//			DomainCase d=iter.next();
//			System.out.println("case "+d.getID());
//			for (Entry<Integer, Premise> e : d.getProblem().getDomainContext().getPremises().entrySet()) {
//				int key=e.getKey();
//				Premise p=e.getValue();
//				System.out.print(key+" id="+p.getID()+"="+p.getContent()+" || ");
//			}
//			System.out.println("\npromValues:");
//			Iterator<Solution> iterSols=d.getSolutions().iterator();
//			while(iterSols.hasNext()){
//				Solution s=iterSols.next();
//				System.out.print(s.getPromotesValue()+" ");
//				String newPromotesValue;
////				if(s.getPromotesValue().equals("ahorro"))
////					newPromotesValue="savings";
////				else if(s.getPromotesValue().equals("calidad"))
////					newPromotesValue="quality";
////				else newPromotesValue="speed";
////				s.setPromotesValue(newPromotesValue);
////				System.out.print(" new="+s.getPromotesValue()+" ");
//						
//			}
//			System.out.println();
//		}
		
		//writeDomainCases(domCases, "testArgumentation/Helpdesk-DomainCases2.dat");
	}

}
