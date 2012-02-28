package es.upv.dsic.gti_ia.argAgents.knowledgeResources;

import java.io.Serializable;

/**
 * Implementation of the concept <i>SocialContext</i>
 *
 */
public class SocialContext extends Context implements Serializable{

	private static final long serialVersionUID = -7633680027995522766L;
	private SocialEntity proponent;
	private SocialEntity opponent;
	private Group group;
	public enum DependencyRelation {POWER, AUTHORISATION, CHARITY}
	private DependencyRelation relation;

    public SocialContext(SocialEntity proponent, SocialEntity opponent, Group group,
    		DependencyRelation relation) {
        this.proponent =  proponent;
        this.opponent = opponent;
        this.group = group;
        this.relation = relation;
    }


    public SocialContext() {
    	proponent = new SocialEntity();
    	opponent = new SocialEntity();
    	group = new Group();
    	relation = DependencyRelation.CHARITY;
    }

    // Property hasDependencyRelation

    public DependencyRelation getDependencyRelation() {
        return relation;
    }

    public void setDependencyRelation(DependencyRelation newDependencyRelation) {
        relation = newDependencyRelation;
    }

    // Property hasGroup

    public Group getGroup() {
        return (Group) group;
    }


    public void setGroup(Group newGroup) {
        group =  newGroup;
    }


    // Property hasOpponent

    public SocialEntity getOpponent() {
        return (SocialEntity) opponent;
    }

    public void setOpponent(SocialEntity newOpponent) {
        opponent = newOpponent;
    }

    // Property hasProponent

    public SocialEntity getProponent() {
        return (SocialEntity) proponent;
    }


    public void setProponent(SocialEntity newProponent) {
        proponent = newProponent;
    }
}
