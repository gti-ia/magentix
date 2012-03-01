package es.upv.dsic.gti_ia.argAgents.knowledgeResources;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Implementation of the concept <i>DomainCase</i>
 * 
 */
public class DomainCase extends Case implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4846976548520731070L;
	private Problem problem;
	private ArrayList<Solution> solutions;
	private Justification justification;

    public DomainCase(Problem problem, ArrayList<Solution> solutions,
			Justification justification) {
		this.problem = problem;
		this.solutions = solutions;
		this.justification = justification;
	}


    public DomainCase() {
    	problem = new Problem();
    	solutions = new ArrayList<Solution>();
    	justification = new Justification();
    }


    // Property hasJustification

    public Justification getJustification() {
        return justification;
    }


    public void setJustification(Justification newJustification) {
        justification = newJustification;
    }


    // Property hasProblem

    public Problem getProblem() {
        return problem;
    }


    public void setProblem(Problem newProblem) {
        problem = newProblem;
    }

    // Property hasSolution

	public ArrayList<Solution> getSolutions() {
		return solutions;
	}
	
	public void removeSolution(Solution oldSolution){
		solutions.remove(oldSolution);
	}
	
	public void addSolution(Solution newSolution){
		solutions.add(newSolution);
	}


	public void setSolutions(ArrayList<Solution> solutions) {
		this.solutions = solutions;
	}


    
}
