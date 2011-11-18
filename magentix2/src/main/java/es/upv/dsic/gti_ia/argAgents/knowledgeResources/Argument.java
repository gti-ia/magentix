package es.upv.dsic.gti_ia.argAgents.knowledgeResources;

import java.io.Serializable;
import java.util.ArrayList;

import es.upv.dsic.gti_ia.argAgents.knowledgeResources.SocialContext.DependencyRelation;



/**
 * Implementation of the owl concept <i>Argument</i>
 *
 */
public class Argument implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -496726263059322323L;
	private long id;
	private Conclusion conclusion;
	private int timesUsedConclusion;
	private String value;
	private SupportSet supportSet;
	private AcceptabilityStatus acceptabilityState;
	private DependencyRelation proponentDepenRelation;
	private long attackingToArgID;
	private ArrayList<Argument> receivedAttacksCounterExamples;
	private ArrayList<Argument> receivedAttacksDistPremises;

    public Argument(long id, Conclusion conclusion, int timesUsedConclusion, 
    		String promotedValue, SupportSet supportSet, DependencyRelation proponentDepenRelation) {
    	this.id = id;
    	this.conclusion = conclusion;
    	this.timesUsedConclusion = timesUsedConclusion;
    	this.value = promotedValue;
    	this.supportSet = supportSet;
    	this.proponentDepenRelation = proponentDepenRelation;
    	this.acceptabilityState = AcceptabilityStatus.UNDECIDED;
    	this.attackingToArgID = -1;
    	this.receivedAttacksCounterExamples = new ArrayList<Argument>();
    	this.receivedAttacksDistPremises = new ArrayList<Argument>();

    }

    public Argument() {
    	id = -1;
    	conclusion = new Conclusion();
    	this.timesUsedConclusion = 0;
    	value = "";
    	supportSet = new SupportSet();
    	this.acceptabilityState = AcceptabilityStatus.UNDECIDED;
    	this.attackingToArgID = -1;
    	this.receivedAttacksCounterExamples = new ArrayList<Argument>();
    	this.receivedAttacksDistPremises = new ArrayList<Argument>();
    	
    }

    // Property hasConclusion

    public Conclusion getHasConclusion() {
        return conclusion;
    }

    public void setConclusion(Conclusion newConclusion) {
        conclusion = (Conclusion) newConclusion;
    }


    // Property hasID

    public int getTimesUsedConclusion() {
		return timesUsedConclusion;
	}

	public void setTimesUsedConclusion(int timesUsedConclusion) {
		this.timesUsedConclusion = timesUsedConclusion;
	}

	public long getID() {
        return id;
    }

    public void setID(long newID) {
        id = newID;
    }


    // Property hasSupportSet

    public SupportSet getSupportSet() {
        return supportSet;
    }


    public void setSupportSet(SupportSet newSupportSet) {
        supportSet = (SupportSet) newSupportSet;
    }


    // Property promotesValue

    public String getPromotesValue() {
        return value;
    }

    public void setPromotesValue(String newPromotesValue) {
        value = (String) newPromotesValue;
    }

	public AcceptabilityStatus getAcceptabilityState() {
		return acceptabilityState;
	}

	public void setAcceptabilityState(AcceptabilityStatus acceptabilityState) {
		this.acceptabilityState = acceptabilityState;
	}

	public DependencyRelation getProponentDepenRelation() {
		return proponentDepenRelation;
	}

	public void setProponentDepenRelation(DependencyRelation proponentDepenRelation) {
		this.proponentDepenRelation = proponentDepenRelation;
	}

	public long getAttackingToArgID() {
		return attackingToArgID;
	}

	public void setAttackingToArgID(long attackingToArgID) {
		this.attackingToArgID = attackingToArgID;
	}

	public void addReceivedAttacksCounterExample(Argument arg){
		this.receivedAttacksCounterExamples.add(arg);
	}
	
	public ArrayList<Argument> getReceivedAttacksCounterExamples() {
		return receivedAttacksCounterExamples;
	}

	public void setReceivedAttacksCounterExamples(
			ArrayList<Argument> receivedAttacksCounterExamples) {
		this.receivedAttacksCounterExamples = receivedAttacksCounterExamples;
	}
	
	public void addReceivedAttacksDistPremises(Argument arg){
		this.receivedAttacksDistPremises.add(arg);
	}

	public ArrayList<Argument> getReceivedAttacksDistPremises() {
		return receivedAttacksDistPremises;
	}

	public void setReceivedAttacksDistPremises(
			ArrayList<Argument> receivedAttacksDistPremises) {
		this.receivedAttacksDistPremises = receivedAttacksDistPremises;
	}
	
	public String toString(){
		
		return "id="+this.id+" solID="+this.conclusion.getID()+" promotedValue="+this.value+
				" attackingToArgID="+this.attackingToArgID+"\nSupportSet: "+this.supportSet.toString();
		
	}
}
