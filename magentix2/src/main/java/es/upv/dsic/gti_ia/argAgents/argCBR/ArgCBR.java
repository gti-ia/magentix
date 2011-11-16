package es.upv.dsic.gti_ia.argAgents.argCBR;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;


import es.upv.dsic.gti_ia.argAgents.Configuration;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.AcceptabilityState;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.ArgNode;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.ArgumentCase;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.ArgumentProblem;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.DialogueGraph;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Position;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Premise;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.SimilarArgumentCase;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.SocialContext;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Solution;

/**
 * This class implements the argumentation CBR. This CBR stores argument cases that
 * represent past argumentation experiences and their final outcome.
 * @author Jaume Jordan
 *
 */
public class ArgCBR {

	private HashMap<Integer,ArrayList<ArgumentCase>> argCB;
	
	private String initialFilePath;
	private String storingFilePath;
//	private OWLArgCBRParser owlArgCBRparser;
	
	/**
	 * Constructor that initializes the ArgCBR creating the HashMap structure and 
	 * loading the argument-cases stored in the indicated path.
	 * @param initialOWLFilePath Path of the OWL file with the argument-cases to load
	 * @param storingOWLFilePath Path of the OWL file to store the argument-cases
	 */
	public ArgCBR(String initialOWLFilePath, String storingOWLFilePath) {
		
		this.initialFilePath=initialOWLFilePath;
		this.storingFilePath=storingOWLFilePath;

		argCB=new HashMap<Integer, ArrayList<ArgumentCase>>();
/*		owlArgCBRparser=new OWLArgCBRParser();
		try {
			Vector<ArgumentCase> argCases=owlArgCBRparser.parseArgCBROnto(this.initialOWLFilePath);
			Iterator<ArgumentCase> iterArgCases=argCases.iterator();
			while(iterArgCases.hasNext()){
				addCase(iterArgCases.next());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
		int ncases=0;
		try {
			
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(this.initialFilePath));
			
			// Read first object
			Object aux = ois.readObject();
			
			// While there are objects
			while (aux!=null){
			    if(aux instanceof ArgumentCase){
			    	ncases++;
			    	ArgumentCase acase=(ArgumentCase) aux;
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
		
		System.out.println(ncases+" argument Cases in ArgCBR");
		
		
	}
	
	/**
	 * Adds a new argument case to the case base.
	 * If the argument case is in case base, the associated domain cases and the attacks received are added to that argument case.
	 * Two cases are considered equal if they have the same domain context, social context, conclusion and state of acceptability.
	 * @param newArgCase A new argument case to add to the case base.
	 * @return <code>true</code> if the argument case is added, otherwise <code>false</code>.
	 */
	public boolean addCase(ArgumentCase newArgCase){
		//two cases are equal if they have the same domain context, social context, conclusion and state of acceptability
		//if two cases are equal, also the domain cases associated and attacks received must be added to the corresponding argument case
		
		HashMap<Integer, Premise> newCasePremises=newArgCase.getArgumentProblem().getDomainContext().getPremises();
		
		Iterator<Premise> iterNewCasePremises= newCasePremises.values().iterator();
		ArrayList<Premise> newCasePremisesList=new ArrayList<Premise>();
		
		while(iterNewCasePremises.hasNext()){ //copy the premises to an arraylist, ordered from lower to higher id
			newCasePremisesList.add(iterNewCasePremises.next());
		}
		Premise firstCasePremise=newCasePremisesList.get(0);
		if(firstCasePremise!=null){
			int firstPremiseID=firstCasePremise.getID();
			ArrayList<ArgumentCase> candidateCases=argCB.get(firstPremiseID);
			if(candidateCases!=null && candidateCases.size()>0){
				Iterator<ArgumentCase> iterArgCases=candidateCases.iterator();
				while(iterArgCases.hasNext()){
					ArgumentCase argCase=iterArgCases.next();
					HashMap<Integer,Premise> argCasePremises=argCase.getArgumentProblem().getDomainContext().getPremises();
					
					//if the premises are the same with the same content, check if social context conclusion and state of acceptability are the same
					if(isSameDomainContextPrecise(newCasePremisesList, argCasePremises) &&
							isSameSocialContext(newArgCase.getArgumentProblem().getSocialContext(), argCase.getArgumentProblem().getSocialContext()) &&
							newArgCase.getArgumentSolution().getConclusion().getID()==argCase.getArgumentSolution().getConclusion().getID() &&
							newArgCase.getArgumentSolution().getAcceptabilityState().compareTo(argCase.getArgumentSolution().getAcceptabilityState())==0 ){
						
						//it is the same argument case, so it is not introduced
						//but we add associated cases and attacks received, and dialogue graphs and increase timesUsed
						
						// increase timesUsed
						int timesUsed = argCase.getTimesUsed() + newArgCase.getTimesUsed();
						argCase.setTimesUsed(timesUsed);
						
						//distinguishing premises
						ArrayList<Premise> distinguishingPremises=argCase.getArgumentSolution().getDistinguishingPremises();
						ArrayList<Premise> newDistinguishingPremises=newArgCase.getArgumentSolution().getDistinguishingPremises();
						if (newDistinguishingPremises == null)
							newDistinguishingPremises = new ArrayList<Premise>();
						if(distinguishingPremises==null){
							argCase.getArgumentSolution().setDistinguishingPremises(newDistinguishingPremises);
						}
						else if(newDistinguishingPremises!=null){
							for(int i=0;i<newDistinguishingPremises.size();i++){
								boolean addPremise=true;
								for(int j=0;j<distinguishingPremises.size();j++){
									//TODO check that distinguishingPremises are NEVER translated to HashMap (there are several dp with the same ID but different content in the ArrayList)
									if(newDistinguishingPremises.get(i).getID()==distinguishingPremises.get(j).getID() 
											&& newDistinguishingPremises.get(i).getContent().equalsIgnoreCase(distinguishingPremises.get(j).getContent())){
										addPremise=false;
										break;
									}
								}
								if(addPremise)
									distinguishingPremises.add(newDistinguishingPremises.get(i));
							}
							argCase.getArgumentSolution().setDistinguishingPremises(distinguishingPremises);
						}
						
						//exceptions
						ArrayList<Premise> exceptions=argCase.getArgumentSolution().getExceptions();
						ArrayList<Premise> newExceptions=newArgCase.getArgumentSolution().getExceptions();
						if (newExceptions == null)
							newExceptions = new ArrayList<Premise>();
						if(exceptions==null)
							argCase.getArgumentSolution().setExceptions(newExceptions);
						else if(newExceptions!=null){
							for(int i=0;i<newExceptions.size();i++){
								boolean addPremise=true;
								for(int j=0;j<exceptions.size();j++){
									if(newExceptions.get(i).getID()==exceptions.get(j).getID()
											&& newExceptions.get(i).getContent().equalsIgnoreCase(exceptions.get(j).getContent())){
										addPremise=false;
										break;
									}
								}
								if(addPremise)
									exceptions.add(newExceptions.get(i));
							}
							argCase.getArgumentSolution().setExceptions(exceptions);
						}
						
						//presumptions
						ArrayList<Premise> presumptions=argCase.getArgumentSolution().getPresumptions();
						ArrayList<Premise> newPresumptions=newArgCase.getArgumentSolution().getPresumptions();
						if (newPresumptions == null)
							newPresumptions = new ArrayList<Premise>();
						if(presumptions==null)
							argCase.getArgumentSolution().setPresumptions(newPresumptions);
						else if(newPresumptions!=null){
							for(int i=0;i<newPresumptions.size();i++){
								boolean addPremise=true;
								for(int j=0;j<presumptions.size();j++){
									if(newPresumptions.get(i).getID()==presumptions.get(j).getID()
											&& newPresumptions.get(i).getContent().equalsIgnoreCase(presumptions.get(j).getContent())){
										addPremise=false;
										break;
									}
								}
								if(addPremise)
									presumptions.add(newPresumptions.get(i));
							}
							argCase.getArgumentSolution().setDistinguishingPremises(presumptions);
						}
						
						//counter examples domain case ids
						ArrayList<Long> counterExamplesDCIds=argCase.getArgumentSolution().getCounterExamplesDomCaseIDList();
						ArrayList<Long> newCounterExamplesDCIds=newArgCase.getArgumentSolution().getCounterExamplesDomCaseIDList();
						if (newCounterExamplesDCIds == null)
							newCounterExamplesDCIds = new ArrayList<Long>();
						if(counterExamplesDCIds==null)
							argCase.getArgumentSolution().setCounterExamplesDomCaseIDList(newCounterExamplesDCIds);
						else if(newCounterExamplesDCIds!=null){
							for(int i=0;i<newCounterExamplesDCIds.size();i++){
								boolean addCounterExample=true;
								for(int j=0;j<counterExamplesDCIds.size();j++){
									if(newCounterExamplesDCIds.get(i)==counterExamplesDCIds.get(j)){
										addCounterExample=false;
										break;
									}
								}
								if(addCounterExample)
									counterExamplesDCIds.add(newCounterExamplesDCIds.get(i));
							}
							argCase.getArgumentSolution().setCounterExamplesDomCaseIDList(counterExamplesDCIds);
						}
						
						//counter examples arg case ids
						ArrayList<Long> counterExamplesArgCIds=argCase.getArgumentSolution().getCounterExamplesArgCaseIDList();
						ArrayList<Long> newCounterExamplesArgCIds=newArgCase.getArgumentSolution().getCounterExamplesArgCaseIDList();
						if (newCounterExamplesArgCIds == null)
							newCounterExamplesArgCIds = new ArrayList<Long>();
						if(counterExamplesArgCIds==null)
							argCase.getArgumentSolution().setCounterExamplesArgCaseIDList(newCounterExamplesArgCIds);
						else if(newCounterExamplesArgCIds!=null){
							for(int i=0;i<newCounterExamplesArgCIds.size();i++){
								boolean addCounterExample=true;
								for(int j=0;j<counterExamplesArgCIds.size();j++){
									if(newCounterExamplesArgCIds.get(i)==counterExamplesArgCIds.get(j)){
										addCounterExample=false;
										break;
									}
								}
								if(addCounterExample)
									counterExamplesArgCIds.add(newCounterExamplesArgCIds.get(i));
							}
							argCase.getArgumentSolution().setCounterExamplesArgCaseIDList(counterExamplesArgCIds);
						}
						
						
						//associated domain cases
						ArrayList<Long> domCases=argCase.getArgumentJustification().getDomainCasesIDs();
						ArrayList<Long> newDomCases=newArgCase.getArgumentJustification().getDomainCasesIDs();
						if (newDomCases == null)
							newDomCases = new ArrayList<Long>();
						if(domCases==null)
							argCase.getArgumentJustification().setDomainCases(newDomCases);
						else if(newDomCases!=null){
							for(int i=0;i<newDomCases.size();i++){
								boolean addCase=true;
								for(int j=0;j<domCases.size();j++){
									if(newDomCases.get(i)==domCases.get(j)){
										addCase=false;
										break;
									}
								}
								if(addCase)
									domCases.add(newDomCases.get(i));
							}
							argCase.getArgumentJustification().setDomainCases(domCases);
						}
						
						//associated arg cases
						ArrayList<Long> argCases=argCase.getArgumentJustification().getArgumentCasesIDs();
						ArrayList<Long> newArgCases=newArgCase.getArgumentJustification().getArgumentCasesIDs();
						if (newArgCases == null)
							newArgCases = new ArrayList<Long>();
						if(argCases==null)
							argCase.getArgumentJustification().setArgumentCases(newArgCases);
						else if(newArgCases!=null){
							for(int i=0;i<newArgCases.size();i++){
								boolean addCase=true;
								for(int j=0;j<argCases.size();j++){
									if(newArgCases.get(i)==argCases.get(j)){
										addCase=false;
										break;
									}
								}
								if(addCase)
									argCases.add(newArgCases.get(i));
							}
							argCase.getArgumentJustification().setArgumentCases(argCases);
						}
						
						//dialogue graphs
						//take the id of the adding argcase (newArgCase), 
						//search in to its dialogue graphs and change it by the id of the argcase (argCase) with which it is joining
						ArrayList<DialogueGraph> dialogueGraphs=newArgCase.getArgumentJustification().getDialogueGraphs();
						Iterator<DialogueGraph> iterDialogues=dialogueGraphs.iterator();
						ArrayList<DialogueGraph> graphs = argCase.getArgumentJustification().getDialogueGraphs();
						while(iterDialogues.hasNext()){
							DialogueGraph diag= iterDialogues.next();
							//change the ID of the new arg case to corresponding arg case in all nodes
							ArrayList<ArgNode> nodesToChange = diag.getNodes(newArgCase.getID());
							if (nodesToChange == null){
								System.err.println("ERROR updating argument-case case-base. No Argument-nodes matching in DialogueGraph");
								continue;
							}
							for (ArgNode node : nodesToChange)
								node.setArgCaseID(argCase.getID());
							//add dialogue graph of newArgCase to argCase
							graphs.add(diag);
						}
						
						return false;
					
					}
				}		
			}
			//the same case is not stored, so it is added
			if(candidateCases==null)
				candidateCases=new ArrayList<ArgumentCase>();
			candidateCases.add(newArgCase);
			argCB.put(firstPremiseID, candidateCases);
			return true;
			
		}
		
		return false;
	}
	
	/**
	 * Return a list with the degrees (attack, efficiency, explanatory power, 
	 * persuasiveness, support and risk) of an argument-case
	 * @param argProblem Problem to solve
	 * @param solution {@link Solution} that proposes the argument-case
	 * @param allPositions Positions to calculate the degrees of the argument-case
	 * @param index Index of the position that represents the argument-case which the degrees are being calculated
	 * @return list with the degrees
	 */
	public ArrayList<Float> getDegrees(ArgumentProblem argProblem, Solution solution, ArrayList<Position> allPositions, int index){
		
		ArrayList<Float> degrees=new ArrayList<Float>();
		
		ArrayList<SimilarArgumentCase> mostSimilarArgCases=getMostSimilarArgCases(argProblem);
		
		for(int i=0;i<mostSimilarArgCases.size();i++){
			SimilarArgumentCase simArg=mostSimilarArgCases.get(i);
			//if it has different promote value, remove it
			if(!simArg.getArgumentCase().getArgumentSolution().getPromotesValue().equalsIgnoreCase(solution.getPromotesValue())){
				mostSimilarArgCases.remove(i);
				i--;
			}
		}
		
		ArrayList<SimilarArgumentCase> sameProblemAcceptedArgCases=getSameProblemAcceptedArgCases(mostSimilarArgCases);
		ArrayList<SimilarArgumentCase> sameProblemConclusionArgCases=getSameProblemConclusionArgCases(mostSimilarArgCases,solution);
		ArrayList<SimilarArgumentCase> sameProblemConclusionAcceptedArgCases=getSameProblemConclusionAcceptedArgCases(sameProblemConclusionArgCases);
		ArrayList<SimilarArgumentCase> sameProblemConclusionAcceptedAttackedArgCases=getSameProblemConclusionAcceptedAttackedArgCases(sameProblemConclusionAcceptedArgCases);
		
		Iterator<SimilarArgumentCase> iteArgAccC = sameProblemConclusionAcceptedArgCases.iterator();
		int argAccC = 0;
		while (iteArgAccC.hasNext()){
			argAccC += iteArgAccC.next().getArgumentCase().getTimesUsed();
		}
		
		Iterator<SimilarArgumentCase> iteArgC = sameProblemConclusionArgCases.iterator();
		int argC = 0;
		while (iteArgC.hasNext()){
			argC += iteArgC.next().getArgumentCase().getTimesUsed();
		}
		
		float persuasivenessDegree=0f;
		if(argC > 0)
			persuasivenessDegree= argAccC / argC;
		
		Iterator<SimilarArgumentCase> iteArg = mostSimilarArgCases.iterator();
		int arg = 0;
		while (iteArg.hasNext()){
			arg += iteArg.next().getArgumentCase().getTimesUsed();
		}
		
		float supportDegree=0f;
		if(arg>0)
			supportDegree= argAccC / arg;
		
		Iterator<SimilarArgumentCase> iteArgAccCAtt = sameProblemConclusionAcceptedAttackedArgCases.iterator();
		int argAccCAtt = 0;
		while (iteArgAccCAtt.hasNext()){
			argAccCAtt += iteArgAccCAtt.next().getArgumentCase().getTimesUsed();
		}
		
		float riskDegree=0f;
		if(argAccC>0)
			riskDegree= argAccCAtt / argAccC;
		
		
		//TODO this is inefficient because for each position it is calculated the attack degrees of allPositions...
		float attackDegree=0f;
		ArrayList<Float> attackDegrees=getAttackDegree(sameProblemAcceptedArgCases, allPositions);
		attackDegree=attackDegrees.get(index);
		
		//TODO this is inefficient because for each position it is calculated the efficiency degree of allPositions...
		float efficiencyDegree=0f;
		ArrayList<Float> efficiencyDegrees=getEfficiencyDegree(sameProblemAcceptedArgCases, allPositions);
		efficiencyDegree=efficiencyDegrees.get(index);
		
		//TODO this is inefficient because for each position it is calculated the explanatory power of allPositions...
		float explanatoryPower=0f;
		ArrayList<Float> explanatoryPowers=getExplanatoryPower(sameProblemAcceptedArgCases, allPositions);
		explanatoryPower=explanatoryPowers.get(index);
		
		degrees.add(persuasivenessDegree);
		degrees.add(supportDegree);
		degrees.add(riskDegree);
		degrees.add(attackDegree);
		degrees.add(efficiencyDegree);
		degrees.add(explanatoryPower);
		
		return degrees;
		
	}
	
	/**
	 * Get similar argument cases to the given one with the same problem description and accepted
	 * @param sameProblemArgCases {@link ArrayList} with argument cases with the same problem description
	 * @return {@link ArrayList} of similar argument cases with the same problem description and accepted
	 */
	private ArrayList<SimilarArgumentCase> getSameProblemAcceptedArgCases(ArrayList<SimilarArgumentCase> sameProblemArgCases){
		
		ArrayList<SimilarArgumentCase> returnList = new ArrayList<SimilarArgumentCase>();
		
		for(int i=0;i<sameProblemArgCases.size();i++){
			// if case is accepted, add it to the list
			if(sameProblemArgCases.get(i).getArgumentCase().getArgumentSolution().getAcceptabilityState().compareTo(AcceptabilityState.ACCEPTABLE)==0){
				returnList.add(sameProblemArgCases.get(i));
			}
		}
		return returnList;
	}
	
	/**
	 * Get similar argument cases to the given one with the same problem description and conclusion
	 * @param sameProblemArgCases {@link ArrayList} with argument cases with the same problem description
	 * @param solution the conclusion that must have the argument cases returned 
	 * @return ArrayList of similar argument cases with the same problem description and conclusion
	 */
	private ArrayList<SimilarArgumentCase> getSameProblemConclusionArgCases(ArrayList<SimilarArgumentCase> sameProblemArgCases, Solution solution){
		
		ArrayList<SimilarArgumentCase> returnList = new ArrayList<SimilarArgumentCase>();
		
		for(int i=0;i<sameProblemArgCases.size();i++){
			if(sameProblemArgCases.get(i).getArgumentCase().getArgumentSolution().getConclusion().getID()==
				solution.getConclusion().getID()){
				returnList.add(sameProblemArgCases.get(i));
			}
		}
		return returnList;
	}
	
	
	/**
	 * Get similar argument cases to the given one with the same problem description, conclusion and accepted
	 * @param sameProblemConclusionArgCases {@link ArrayList} with argument cases with the same problem description and conclusion
	 * @return {@link ArrayList} of similar argument cases with the same problem description, conclusion and accepted
	 */
	private ArrayList<SimilarArgumentCase> getSameProblemConclusionAcceptedArgCases(ArrayList<SimilarArgumentCase> sameProblemConclusionArgCases){
		
		ArrayList<SimilarArgumentCase> returnList = new ArrayList<SimilarArgumentCase>();
		
		for(int i=0;i<sameProblemConclusionArgCases.size();i++){
			// if case is accepted, add it to the list
			if(sameProblemConclusionArgCases.get(i).getArgumentCase().getArgumentSolution().getAcceptabilityState().compareTo(AcceptabilityState.ACCEPTABLE)==0){
				returnList.add(sameProblemConclusionArgCases.get(i));
			}
		}
		return returnList;
	}
	
	/**
	 * Get similar argument cases to the given one with the same problem description, conclusion, accepted and with attacks
	 * @param sameProblemConclusionAcceptedArgCases {@link ArrayList} with argument cases with the same problem description, conclusion and accepted
	 * @return {@link ArrayList} of similar argument cases with the same problem description, conclusion, accepted and with attacks
	 */
	private ArrayList<SimilarArgumentCase> getSameProblemConclusionAcceptedAttackedArgCases(ArrayList<SimilarArgumentCase> sameProblemConclusionAcceptedArgCases){
		
		ArrayList<SimilarArgumentCase> returnList = new ArrayList<SimilarArgumentCase>();
		
		for(int i=0;i<sameProblemConclusionAcceptedArgCases.size();i++){
			// if case is attacked, add it to the list
			if(sameProblemConclusionAcceptedArgCases.get(i).getArgumentCase().getArgumentSolution().getCounterExamplesDomCaseIDList().size()>0 ||
					sameProblemConclusionAcceptedArgCases.get(i).getArgumentCase().getArgumentSolution().getCounterExamplesArgCaseIDList().size()>0 ||
					sameProblemConclusionAcceptedArgCases.get(i).getArgumentCase().getArgumentSolution().getDistinguishingPremises().size()>0 ||
					sameProblemConclusionAcceptedArgCases.get(i).getArgumentCase().getArgumentSolution().getExceptions().size()>0 ||
					sameProblemConclusionAcceptedArgCases.get(i).getArgumentCase().getArgumentSolution().getPresumptions().size()>0){
				returnList.add(sameProblemConclusionAcceptedArgCases.get(i));

			}
		}
		return returnList;
	}
	
	
	/**
	 * Return all the argument cases of the different given positions that have the same conclusion in each case
	 * @param sameProblemAcceptedArgCases argument cases with the same problem description and accepted
	 * @param initialPositions argument cases with different initial positions 
	 * @return an {@link ArrayList} of {@link ArrayList} of argument cases for each initial position
	 */
	
	private ArrayList<ArrayList<SimilarArgumentCase>> getAllPositionArgCases(ArrayList<SimilarArgumentCase> sameProblemAcceptedArgCases, ArrayList<Position> initialPositions){
		
		// classify the arg cases, with same problem description and accepted, by its conclusion
		HashMap<Long,ArrayList<SimilarArgumentCase>> conclusionSets=new HashMap<Long, ArrayList<SimilarArgumentCase>>();
		Iterator<SimilarArgumentCase> iterSimCases=sameProblemAcceptedArgCases.iterator();
		while(iterSimCases.hasNext()){
			SimilarArgumentCase simArgCase=iterSimCases.next();
			long conclusionID=simArgCase.getArgumentCase().getArgumentSolution().getConclusion().getID();
			ArrayList<SimilarArgumentCase> list=conclusionSets.get(conclusionID);
			if(list==null)
				list=new ArrayList<SimilarArgumentCase>();
			list.add(simArgCase);
			conclusionSets.put(conclusionID, list);
		}
		// put a list of argument case for each initial position
		ArrayList<ArrayList<SimilarArgumentCase>> allPositions= new ArrayList<ArrayList<SimilarArgumentCase>>();
		Iterator<Position> iterPositions=initialPositions.iterator();
		while(iterPositions.hasNext()){
			Position position=iterPositions.next();
			ArrayList<SimilarArgumentCase> samePosition=conclusionSets.get(position.getSolution().getConclusion().getID());
			if(samePosition==null){
				samePosition= new ArrayList<SimilarArgumentCase>();
				
			}
			allPositions.add(samePosition);
			
		}
		
		return allPositions;
		
	}
		
	
	/**
	 * Returns the attack degree of each given {@link Position}
	 * @param sameProblemAcceptedArgCases argument cases with the same problem description and accepted
	 * @param initialPositions {@link ArrayList} with different positions, but with the same problem description
	 * @return an {@link ArrayList} of {@link Float} with the attack degree of each initial position
	 */
	public ArrayList<Float> getAttackDegree(ArrayList<SimilarArgumentCase> sameProblemAcceptedArgCases, ArrayList<Position> initialPositions){
		//TODO it could be join attackDegree, efficiency degree and explanatory power in only one function
		ArrayList<ArrayList<SimilarArgumentCase>> allPositionsCases=getAllPositionArgCases(sameProblemAcceptedArgCases, initialPositions);
		
		
		ArrayList<Float> positionAttacksAverages=new ArrayList<Float>();
		
		// calculate min and max attacks
		int minAttacks=Integer.MAX_VALUE;
		int maxAttacks=Integer.MIN_VALUE;
		Iterator<ArrayList<SimilarArgumentCase>> iterListAllPositions= allPositionsCases.iterator();
		while(iterListAllPositions.hasNext()){
			ArrayList<SimilarArgumentCase> argCasesList=iterListAllPositions.next();
			Iterator<SimilarArgumentCase> iterCasesOfPosition= argCasesList.iterator();
			float positionAttacksAverage=0f;
			while(iterCasesOfPosition.hasNext()){
				SimilarArgumentCase simArgCasePosition=iterCasesOfPosition.next();
				int nAttacks=simArgCasePosition.getArgumentCase().getArgumentSolution().getCounterExamplesDomCaseIDList().size()+
				simArgCasePosition.getArgumentCase().getArgumentSolution().getCounterExamplesArgCaseIDList().size()+
				simArgCasePosition.getArgumentCase().getArgumentSolution().getDistinguishingPremises().size()+
				simArgCasePosition.getArgumentCase().getArgumentSolution().getExceptions().size()+
				simArgCasePosition.getArgumentCase().getArgumentSolution().getPresumptions().size();
				if(nAttacks<minAttacks)
					minAttacks=nAttacks;
				if(nAttacks>maxAttacks)
					maxAttacks=nAttacks;
				positionAttacksAverage+=nAttacks;//add attacks to obtain the average
			}
			//calculate attacks average of this position and store it in the list
			positionAttacksAverage=positionAttacksAverage/argCasesList.size();
			positionAttacksAverages.add(positionAttacksAverage);
		}
		
		
		
		
		ArrayList<Float> attackDegrees=new ArrayList<Float>();
		
		Iterator<Float> iterAverages=positionAttacksAverages.iterator();
		while(iterAverages.hasNext()){
			float nAttacks=iterAverages.next();
			float attackDegree=(nAttacks-minAttacks)/(maxAttacks-minAttacks);
			attackDegrees.add(attackDegree);
		}
		
		return attackDegrees;
	}
	
	
	/**
	 * Returns the efficiency degree of each initial position
	 * @param sameProblemAcceptedArgCases argument cases with the same problem description and accepted
	 * @param initialPositions {@link ArrayList} with different positions, but with the same problem description
	 * @return an {@link ArrayList} of {@link Float} with the efficiency degree of each initial position
	 */
	public ArrayList<Float> getEfficiencyDegree(ArrayList<SimilarArgumentCase> sameProblemAcceptedArgCases, ArrayList<Position> initialPositions){
		
		ArrayList<ArrayList<SimilarArgumentCase>> allPositionsCases=getAllPositionArgCases(sameProblemAcceptedArgCases, initialPositions);
		
		ArrayList<Float> positionStepsAverages=new ArrayList<Float>();
		
		// calculate min and max steps
		int minSteps=Integer.MAX_VALUE;
		int maxSteps=Integer.MIN_VALUE;
		Iterator<ArrayList<SimilarArgumentCase>> iterListAllPositions= allPositionsCases.iterator();
		while(iterListAllPositions.hasNext()){
			ArrayList<SimilarArgumentCase> argCasesList=iterListAllPositions.next();
			Iterator<SimilarArgumentCase> iterCasesOfPosition= argCasesList.iterator();
			float positionStepsAverage=0f;
			int positionTotalDialogueGraphs=0;
			while(iterCasesOfPosition.hasNext()){
				SimilarArgumentCase simArgCasePosition=iterCasesOfPosition.next();
				
				Iterator<DialogueGraph> iterDialogueGraphs=simArgCasePosition.getArgumentCase().getArgumentJustification().getDialogueGraphs().iterator();
				int nSteps=0;
				while(iterDialogueGraphs.hasNext()){
					DialogueGraph dialogueGraph=iterDialogueGraphs.next();
					int dialogueSteps;
					try {
						dialogueSteps = dialogueGraph.distanceToFinal(simArgCasePosition.getArgumentCase().getID());
					} catch (Exception e) {

						e.printStackTrace();						
						continue;
					}
					if(dialogueSteps<minSteps)
						minSteps=dialogueSteps;
					if(dialogueSteps>maxSteps)
						maxSteps=dialogueSteps;
					
					nSteps+=dialogueSteps;
					positionTotalDialogueGraphs++;
				}
				
				positionStepsAverage+=nSteps;//add steps to obtain the average
			}
			//calculate steps average of this position and store it in the list
			positionStepsAverage=positionStepsAverage/positionTotalDialogueGraphs;
			positionStepsAverages.add(positionStepsAverage);
		}
		
		
		
		
		ArrayList<Float> efficiencyDegrees=new ArrayList<Float>();
		
		Iterator<Float> iterAverages=positionStepsAverages.iterator();
		while(iterAverages.hasNext()){
			float nSteps=iterAverages.next();
			float efficiencyDegree=(nSteps-minSteps)/(maxSteps-minSteps);
			efficiencyDegrees.add(efficiencyDegree);
		}
		
		return efficiencyDegrees;
	}
	
	
	/**
	 * Returns the explanatory power of the given positions 
	 * @param sameProblemAcceptedArgCases argument cases with the same problem description and accepted
	 * @param initialPositions {@link ArrayList} with different positions, but with the same problem description
	 * @return an {@link ArrayList} of {@link Float} with the explanatory power of each initial position
	 */
	public ArrayList<Float> getExplanatoryPower(ArrayList<SimilarArgumentCase> sameProblemAcceptedArgCases, ArrayList<Position> initialPositions){
		
		ArrayList<ArrayList<SimilarArgumentCase>> allPositionsCases=getAllPositionArgCases(sameProblemAcceptedArgCases, initialPositions);
		
		ArrayList<Float> kRAverages=new ArrayList<Float>();
		
		// calculate min and max knowledge resources
		int minKR=Integer.MAX_VALUE;
		int maxKR=Integer.MIN_VALUE;
		Iterator<ArrayList<SimilarArgumentCase>> iterListAllPositions= allPositionsCases.iterator();
		while(iterListAllPositions.hasNext()){
			ArrayList<SimilarArgumentCase> argCasesList=iterListAllPositions.next();
			Iterator<SimilarArgumentCase> iterCasesOfPosition= argCasesList.iterator();
			float kRAverage=0f;
			while(iterCasesOfPosition.hasNext()){
				SimilarArgumentCase simArgCasePosition=iterCasesOfPosition.next();
				int nKR=simArgCasePosition.getArgumentCase().getArgumentJustification().getArgumentationSchemes().size()+
				simArgCasePosition.getArgumentCase().getArgumentJustification().getDomainCasesIDs().size()+
				simArgCasePosition.getArgumentCase().getArgumentJustification().getArgumentCasesIDs().size();
				if(nKR<minKR)
					minKR=nKR;
				if(nKR>maxKR)
					maxKR=nKR;
				kRAverage+=nKR;//add knowledge resources to obtain the average
			}
			//calculate knowledge resources average of this position and store it in the list
			kRAverage=kRAverage/argCasesList.size();
			kRAverages.add(kRAverage);
		}
		
		ArrayList<Float> explanatoryPowers=new ArrayList<Float>();
		
		Iterator<Float> iterAverages=kRAverages.iterator();
		while(iterAverages.hasNext()){
			float nKR=iterAverages.next();
			float explanatoryPower=(nKR-minKR)/(maxKR-minKR);
			explanatoryPowers.add(explanatoryPower);
		}
		
		return explanatoryPowers;
		
	}
	
	
	/**
	 * Returns the argument-cases with the same domain and social context that have been accepted
	 * @param premises {@link HashMap} of the premises that describe the domain context
	 * @param solution {@link Solution} of the problem
	 * @param socialContext {@link SocialContext} of the current situation
	 * @return argument-cases with the same domain and social context that have been accepted
	 */
	public ArrayList<SimilarArgumentCase> getSameDomainAndSocialContextAccepted(HashMap<Integer,Premise> premises, Solution solution, SocialContext socialContext){
		ArrayList<SimilarArgumentCase> finalArgCases=new ArrayList<SimilarArgumentCase>();
		Configuration c=new Configuration();
		
		ArrayList<ArgumentCase> domainSimilarArgCases=getDomainSimilarArgCases(premises);
		Iterator<ArgumentCase> iterDomainSimilarArgCases=domainSimilarArgCases.iterator();
		while(iterDomainSimilarArgCases.hasNext()){
			float suitability=0;
			ArgumentCase currentArgCase=iterDomainSimilarArgCases.next();
			if(socialContext.getDependencyRelation().compareTo(currentArgCase.getArgumentProblem().getSocialContext().getDependencyRelation())==0){
				float proponentIDcomp=0f, proponentPrefcomp=0f,opponentIDcomp=0f,opponentPrefcomp=0f,groupIDcomp=0f,groupPrefcomp=0f;
				if(socialContext.getProponent().getID()==
					currentArgCase.getArgumentProblem().getSocialContext().getProponent().getID())
					proponentIDcomp=c.argCBRproponentidweight;
				if(socialContext.getProponent().getValPref().getPreferred().equalsIgnoreCase(currentArgCase.getArgumentProblem().getSocialContext().getProponent().getValPref().getPreferred()))
					proponentPrefcomp=c.argCBRproponentprefweight;
				
				if(socialContext.getOpponent().getID()==
					currentArgCase.getArgumentProblem().getSocialContext().getOpponent().getID())
					opponentIDcomp=c.argCBRopponentidweight;
				if(socialContext.getOpponent().getValPref().getPreferred().equalsIgnoreCase(currentArgCase.getArgumentProblem().getSocialContext().getOpponent().getValPref().getPreferred()))
					opponentPrefcomp=c.argCBRopponentprefweight;
				
				if(socialContext.getGroup().getID()==
					currentArgCase.getArgumentProblem().getSocialContext().getGroup().getID())
					groupIDcomp=c.argCBRgroupidweight;
				if(socialContext.getGroup().getValPref().getPreferred().equalsIgnoreCase(currentArgCase.getArgumentProblem().getSocialContext().getGroup().getValPref().getPreferred()))
					groupPrefcomp=c.argCBRgroupprefweight;
				
				//System.err.println(proponentIDcomp+" "+proponentPrefcomp+" "+opponentIDcomp+" "+opponentPrefcomp+" "+groupIDcomp+" "+groupPrefcomp);
				suitability=(proponentIDcomp+proponentPrefcomp+opponentIDcomp+opponentPrefcomp+groupIDcomp+groupPrefcomp)/
				(c.argCBRproponentidweight+c.argCBRproponentprefweight+c.argCBRopponentidweight+c.argCBRopponentprefweight+c.argCBRgroupidweight+c.argCBRgroupprefweight);
			
				
				finalArgCases.add(new SimilarArgumentCase(currentArgCase, suitability));
				
			}
			
			
		}
		
		//only with the same solution id, and promoted value
		for(int i=0;i<finalArgCases.size();i++){
			if(finalArgCases.get(i).getArgumentCase().getArgumentSolution().getConclusion().getID() != solution.getConclusion().getID() || 
					!finalArgCases.get(i).getArgumentCase().getArgumentSolution().getPromotesValue().equalsIgnoreCase(solution.getPromotesValue())){
				finalArgCases.remove(i);
				i--;
			}
		}
		
		//only accepted cases
		for(int i=0;i<finalArgCases.size();i++){
			// if case is not accepted, remove it from list
			if(finalArgCases.get(i).getArgumentCase().getArgumentSolution().getAcceptabilityState().compareTo(AcceptabilityState.ACCEPTABLE)!=0){
				finalArgCases.remove(i);
				i--;
			}
		}
		
		
		
		return finalArgCases;
	}
	
	/**
	 * Get the argument cases with the same domain context as the given argument case and the same dependency relation.
	 * The returned argument cases are pondered with a degree of suitability depending on the coincidence of the social context.
	 * @param argProblem The argument problem with a determined domain context and social context that the returned argument cases have to be similar 
	 * @return an {@link ArrayList} of {@link SimilarArgumentCase} that have he same domain context as the given argument case and the same dependency relation, pondered with a suitability 
	 */
	public ArrayList<SimilarArgumentCase> getMostSimilarArgCases(ArgumentProblem argProblem){
		ArrayList<SimilarArgumentCase> mostSimilarArgCases=new ArrayList<SimilarArgumentCase>();
		Configuration c=new Configuration();
		
		ArrayList<ArgumentCase> domainSimilarArgCases=getDomainSimilarArgCases(argProblem.getDomainContext().getPremises());
		Iterator<ArgumentCase> iterDomainSimilarArgCases=domainSimilarArgCases.iterator();
		while(iterDomainSimilarArgCases.hasNext()){
			float suitability=0;
			ArgumentCase currentArgCase=iterDomainSimilarArgCases.next();
			
			SocialContext socialContext=argProblem.getSocialContext();
			//if there is a social context
			if(socialContext!=null){
				// if dependency relation is the same
				if(argProblem.getSocialContext().getDependencyRelation() == null || argProblem.getSocialContext().getDependencyRelation().compareTo(currentArgCase.getArgumentProblem().getSocialContext().getDependencyRelation())==0){
					float proponentIDcomp=0f, proponentPrefcomp=0f,opponentIDcomp=0f,opponentPrefcomp=0f,groupIDcomp=0f,groupPrefcomp=0f;
					try {
						if(argProblem.getSocialContext().getProponent().getID()==
							currentArgCase.getArgumentProblem().getSocialContext().getProponent().getID())
							proponentIDcomp=c.argCBRproponentidweight;
					} catch (Exception e) {
						
					}
					try {
						if(argProblem.getSocialContext().getProponent().getValPref().getPreferred().equalsIgnoreCase(currentArgCase.getArgumentProblem().getSocialContext().getProponent().getValPref().getPreferred()))
							proponentPrefcomp=c.argCBRproponentprefweight;
					} catch (Exception e) {
						
					}
					
					try {
						if(argProblem.getSocialContext().getOpponent().getID()==
							currentArgCase.getArgumentProblem().getSocialContext().getOpponent().getID())
							opponentIDcomp=c.argCBRopponentidweight;
					} catch (Exception e) {
						
					}
					
					try {
						if(argProblem.getSocialContext().getOpponent().getValPref().getPreferred().equalsIgnoreCase(currentArgCase.getArgumentProblem().getSocialContext().getOpponent().getValPref().getPreferred()))
							opponentPrefcomp=c.argCBRopponentprefweight;
					} catch (Exception e) {
						
					}
					
					try {
						if(argProblem.getSocialContext().getGroup().getID()==
							currentArgCase.getArgumentProblem().getSocialContext().getGroup().getID())
							groupIDcomp=c.argCBRgroupidweight;
					} catch (Exception e) {
						
					}
					try {
						if(argProblem.getSocialContext().getGroup().getValPref().getPreferred().equalsIgnoreCase(currentArgCase.getArgumentProblem().getSocialContext().getGroup().getValPref().getPreferred()))
							groupPrefcomp=c.argCBRgroupprefweight;
					} catch (Exception e) {
						
					}

					suitability=(proponentIDcomp+proponentPrefcomp+opponentIDcomp+opponentPrefcomp+groupIDcomp+groupPrefcomp)/
					(c.argCBRproponentidweight+c.argCBRproponentprefweight+c.argCBRopponentidweight+c.argCBRopponentprefweight+c.argCBRgroupidweight+c.argCBRgroupprefweight);
				}
			}
			
			mostSimilarArgCases.add(new SimilarArgumentCase(currentArgCase,suitability));
			
		}
		
		return mostSimilarArgCases;
		
	}
	
	/**
	 * Returns an {@link ArrayList} with argument cases with the same given premises (id and content) in the domain context
	 * @param desiredPremises {@link HashMap} with the desired premises  
	 * @return argument cases with the same given premises in the domain context
	 */
	private ArrayList<ArgumentCase> getDomainSimilarArgCases(HashMap<Integer,Premise> desiredPremises){
		ArrayList<ArgumentCase> domainSimilarCases= new ArrayList<ArgumentCase>();
		
		Iterator<Premise> iterCasePremises=desiredPremises.values().iterator();
		ArrayList<Premise> desiredPremisesList=new ArrayList<Premise>();
		
		while(iterCasePremises.hasNext()){ //copy the premises to an ArrayList, ordered from lower to higher id
			Premise premise=iterCasePremises.next();
			
			desiredPremisesList.add(premise);
		}
		
		Premise firstCasePremise=desiredPremisesList.get(0);
		if(firstCasePremise!=null){
			int firstPremiseID=firstCasePremise.getID();
			ArrayList<ArgumentCase> candidateCases=argCB.get(firstPremiseID);
			if(candidateCases!=null && candidateCases.size()>0){
				Iterator<ArgumentCase> iterArgCases=candidateCases.iterator();
				while(iterArgCases.hasNext()){
					ArgumentCase argCase=iterArgCases.next();
					HashMap<Integer,Premise> argCasePremises=argCase.getArgumentProblem().getDomainContext().getPremises();
					
					//if the premises are the same with the same content, it is a similar argument case, add it to final list
					if(isSameDomainContext(desiredPremisesList, argCasePremises))
						domainSimilarCases.add(argCase);
					
					
				}		
			}
		}
		
		
		return domainSimilarCases;
	}
	
	/**
	 * Returns <code>true</code> if all the premises in the given {@link ArrayList} are the same (id and content) in the {@link HashMap}
	 * @param premises1 {@link ArrayList} with premises
	 * @param premises2 {@link HashMap} with premises
	 * @return <code>true</code> if it is the same domain context, otherwise <code>false</code>
	 */
	private boolean isSameDomainContext(ArrayList<Premise> premises1, HashMap<Integer,Premise> premises2){
		Iterator<Premise> iterPremises1=premises1.iterator();
		
		while(iterPremises1.hasNext()){
			Premise currentPremise1=iterPremises1.next();
			Premise currentPremise2=premises2.get(currentPremise1.getID());
			//if premise not exists or the content is different, this case is not valid
			if( currentPremise2==null || !currentPremise2.getContent().equalsIgnoreCase(currentPremise1.getContent()) ){
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Returns <code>true</code> if all the premises in the given {@link ArrayList} are the same (id and content) in the {@link HashMap} and there are not anymore. 
	 * @param premises1 {@link ArrayList} with premises
	 * @param premises2 {@link HashMap} with premises
	 * @return <code>true</code> if it is the same domain context, otherwise <code>false</code>
	 */
	private boolean isSameDomainContextPrecise(ArrayList<Premise> premises1, HashMap<Integer,Premise> premises2){
		
		if(premises1.size()!=premises2.size())
			return false;
		
		Iterator<Premise> iterPremises1=premises1.iterator();
		
		while(iterPremises1.hasNext()){
			Premise currentPremise1=iterPremises1.next();
			Premise currentPremise2=premises2.get(currentPremise1.getID());
			//if premise not exists or the content is different, this case is not valid
			if( currentPremise2==null || !currentPremise2.getContent().equalsIgnoreCase(currentPremise1.getContent()) ){
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Returns <code>true</code> if the given social contexts are the same (dependency relation, group, proponent and opponent).
	 * @param socialContext1 a social context to compare
	 * @param socialContext2 a social context to compare
	 * @return <code>true</code> if the given social contexts are the same, otherwise <code>false</code>
	 */
	private boolean isSameSocialContext(SocialContext socialContext1, SocialContext socialContext2){
		
		//TODO add list of proponents, opponents and groups (checking that norms and valprefs are the same)
		if(socialContext1.getDependencyRelation().compareTo(socialContext2.getDependencyRelation())!=0)
			return false;
		if(socialContext1.getGroup().getID()!=socialContext2.getGroup().getID())
			return false;
		if(socialContext1.getOpponent().getID()!=socialContext2.getOpponent().getID())
			return false;
		if(socialContext1.getProponent().getID()!=socialContext2.getProponent().getID())
			return false;
		
		return true;
	}
	
	/**
	 * Returns all argument-cases in a {@link Collection}
	 * @return all argument-cases in a {@link Collection}
	 */
	public Collection<ArrayList<ArgumentCase>> getAllCases(){
		return argCB.values();
	}
	
	/**
	 * Returns all argument-cases in a {@link Vector}
	 * @return all argument-cases in a {@link Vector}
	 */
	public Vector<ArgumentCase> getAllCasesVector(){
		Vector<ArgumentCase> vector= new Vector<ArgumentCase>();
		
		Iterator<ArrayList<ArgumentCase>> iterLists=argCB.values().iterator();
		while(iterLists.hasNext()){
			ArrayList<ArgumentCase> list=iterLists.next();
			Iterator<ArgumentCase> iterArgCase=list.iterator();
			while(iterArgCase.hasNext()){
				vector.add(iterArgCase.next());
			}
		}
		
		return vector;
	}
	
	/**
	 * Stores the argument-case case-base in the path specified in the creation of the class
	 */
	public void doCache(){
//		try {
//			owlArgCBRparser.saveArgumentationOntology(getAllCasesVector(), initialOWLFilePath, storingOWLFilePath);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(storingFilePath));
		
			Iterator<ArgumentCase> iterCases=getAllCasesVector().iterator();
			int nCases = 0;
			while(iterCases.hasNext()){
				ArgumentCase argCase=iterCases.next();
				oos.writeObject(argCase);
				nCases++;
			}
			
			oos.close();
			
			System.out.println(nCases + " argCases introduced");
		
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}

	}
	
	/**
	 * Stores the argument-case case-base in the path specified as argument
	 * @param fileName Path to store the case-base
	 */
	public void doCache(String fileName){
//		try {
//			owlArgCBRparser.saveArgumentationOntology(getAllCasesVector(), initialOWLFilePath, storingOWLFilePath);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
		
			Iterator<ArgumentCase> iterCases=getAllCasesVector().iterator();
			while(iterCases.hasNext()){
				ArgumentCase argCase=iterCases.next();
				oos.writeObject(argCase);
			}
			
			oos.close();
		
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}

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
			    if(aux instanceof ArgumentCase){
			    	ArgumentCase acase=(ArgumentCase) aux;
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
		
			Iterator<ArgumentCase> iterCases=getAllCasesVector().iterator();
			while(iterCases.hasNext()){
				ArgumentCase argCase=iterCases.next();
				oos.writeObject(argCase);
			}
			
			oos.close();
		
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}

	}
}
