package es.upv.dsic.gti_ia.argAgents.knowledgeResources;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


/**
 * Implementation of the owl concept <i>ArgumentCase</i>
 * 
 */
public class ArgumentCase extends Case implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1136383472063190267L;
	private ArgumentProblem problem;
	private ArgumentSolution solution;
	private ArgumentJustification justification;
	private int timesUsed;
	

    public ArgumentCase(long id, String creationDate, ArgumentProblem problem,
			ArgumentSolution solution, ArgumentJustification justification,
			int timesUsed) {
		super(id, creationDate);
		this.problem = problem;
		this.solution = solution;
		this.justification = justification;
		this.timesUsed = timesUsed;
	}


    public ArgumentCase() {
    	super();
    	problem = new ArgumentProblem();
    	solution = new ArgumentSolution();
    	justification = new ArgumentJustification();
    	timesUsed=0;
    }

    // Property hasArgumentJustification

    public ArgumentJustification getArgumentJustification() {
        return (ArgumentJustification) justification;
    }


    public void setArgumentJustification(ArgumentJustification newArgumentJustification) {
        justification = newArgumentJustification;
    }

    // Property hasArgumentProblem

    public ArgumentProblem getArgumentProblem() {
        return (ArgumentProblem) problem;
    }


    public void setArgumentProblem(ArgumentProblem newArgumentProblem) {
        problem = newArgumentProblem;
    }

    // Property hasArgumentSolution

    public ArgumentSolution getArgumentSolution() {
        return (ArgumentSolution) solution;
    }


    public void setArgumentSolution(ArgumentSolution newArgumentSolution) {
        solution = newArgumentSolution;
    }


	public int getTimesUsed() {
		return timesUsed;
	}


	public void setTimesUsed(int timesUsed) {
		this.timesUsed = timesUsed;
	}
	
	public void printArgumentCase(ArgumentCase argCase){
		System.out.println(" ********************* ArgumentCase hasID: " + argCase.getID() + "************************");
		System.out.println("ArgumentCase creationDate: " + argCase.getCreationDate());
		System.out.println("ArgumentCase hasArgumentProblem: " + argCase.getArgumentProblem().hashCode());
			System.out.println("\t ArgumentProblem hasDomainContex: " + argCase.getArgumentProblem().getDomainContext().hashCode());
				HashMap<Integer, Premise> premises = argCase.getArgumentProblem().getDomainContext().getPremises();
				Iterator<Premise> it = premises.values().iterator();
				while(it.hasNext()){
					Premise p=it.next();
					
						System.out.println("\t\t Premise ID: " + p.getID());
						System.out.println("\t\t Premise Name: " + p.getName());
						System.out.println("\t\t Premise Content: " +p.getContent() );
					
				}
			System.out.println("\t ArgumentProblem hasSocialContext: " + argCase.getArgumentProblem().getSocialContext().hashCode());
				SocialEntity pro = argCase.getArgumentProblem().getSocialContext().getProponent();
				System.out.println("\t\t Proponent ID: " + pro.getID());
				System.out.println("\t\t Proponent Name: " + pro.getName());
				System.out.println("\t\t Proponent Role: " + pro.getRole());
					ArrayList<Norm> norms = pro.getNorms();
					if (norms != null)
						for (Norm n : norms){
							System.out.println("\t\t\t Norm ID: " + n.getID());
							System.out.println("\t\t\t Norm Description: " + n.getDescription());
						};
					ArrayList<String> vp = pro.getValPref().getValues();
					for (String v : vp){
						System.out.println("\t\t\t Value: " + v);
					}				
				
				SocialEntity op = argCase.getArgumentProblem().getSocialContext().getOpponent();
				System.out.println("\t\t Opponent ID: " + op.getID());
				System.out.println("\t\t Opponent Name: " + op.getName());
				System.out.println("\t\t Opponent Role: " + op.getRole());
					ArrayList<Norm> normsOP = op.getNorms();
					if (normsOP != null)
						for (Norm n : normsOP){
							System.out.println("\t\t\t Norm ID: " + n.getID());
							System.out.println("\t\t\t Norm Description: " + n.getDescription());
						};
					ArrayList<String> vpOP = op.getValPref().getValues();
					for (String v : vpOP){
						System.out.println("\t\t\t Value: " + v);
					}	
					
				Group gr = argCase.getArgumentProblem().getSocialContext().getGroup();
				System.out.println("\t\t Group ID: " + gr.getID());
				System.out.println("\t\t Group Name: " + gr.getName());
				System.out.println("\t\t Group Role: " + op.getRole());
					ArrayList<Norm> normsGR = gr.getNorms();
					if (normsGR != null)
						for (Norm n : normsGR){
							System.out.println("\t\t\t Norm ID: " + n.getID());
							System.out.println("\t\t\t Norm Description: " + n.getDescription());
						};
					ArrayList<String> vpGR = gr.getValPref().getValues();
					for (String v : vpGR){
						System.out.println("\t\t\t Value: " + v);
					}
					ArrayList<SocialEntity> mem = gr.getMembers();
					for (SocialEntity m : mem){
						System.out.println("\t\t\t Memmber ID: " + m.getID());
						System.out.println("\t\t\t Member Name: " + m.getName());
						System.out.println("\t\t Member Role: " + m.getRole());
							ArrayList<Norm> normsM = m.getNorms();
							if (normsM != null)
								for (Norm n : normsM){
									System.out.println("\t\t\t\t Norm ID: " + n.getID());
									System.out.println("\t\t\t\t Norm Description: " + n.getDescription());
								};
							ArrayList<String> vpM = m.getValPref().getValues();
							for (String v : vpM){
								System.out.println("\t\t\t\t Value: " + v);
							}
					}
				
				System.out.println("\t\t Dependency Relation: " + argCase.getArgumentProblem().getSocialContext().getDependencyRelation());
	}
	
	public String toString(){
		String str="id: "+this.getID()+" creationDate: "+this.getCreationDate()+"\n";
		
		str+="Domain context. Premises:\n";
		HashMap<Integer, Premise> premises = this.getArgumentProblem().getDomainContext().getPremises();
		Iterator<Premise> it = premises.values().iterator();
		while(it.hasNext()){
			Premise p=it.next();
			str+="\t ID: " + p.getID();
			str+=" Content: " +p.getContent();
		}
		
		str+="\nSocial context. \n";
		SocialEntity pro = this.getArgumentProblem().getSocialContext().getProponent();
		str+="Proponent ID: " + pro.getID()+" name: "+ pro.getName()+" role: "+pro.getRole()+"\n";
		
		SocialEntity op = this.getArgumentProblem().getSocialContext().getOpponent();
		str+="Oponent ID: " + op.getID()+" name: "+ op.getName()+" role: "+op.getRole()+"\n";
		
		str+="Dependency Relation: " + this.getArgumentProblem().getSocialContext().getDependencyRelation()+"\n";
		
		return str;
	}
	
}
