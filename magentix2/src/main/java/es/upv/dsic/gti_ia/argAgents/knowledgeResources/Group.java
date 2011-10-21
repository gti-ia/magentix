package es.upv.dsic.gti_ia.argAgents.knowledgeResources;

import java.io.Serializable;
import java.util.ArrayList;



/**
 * Implementation of the owl concept <i>Group</i>
 * 
 */
public class Group extends SocialEntity implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -200540749931771249L;
	private ArrayList<SocialEntity> agents;

    public Group(long id, String name, ValPref valpref, ArrayList<SocialEntity> agents) {
        super(id, name, "",null,valpref);
    	this.agents = agents;
    }


    public Group() {
    	agents = new ArrayList<SocialEntity>();
    }

    // Property hasMember

    public ArrayList<SocialEntity> getMembers() {
        return agents;
    }


    //public Iterator listHasMember() {
    //    return listPropertyValuesAs(getHasMemberProperty(), Agent.class);
    //}


    public void addMember(SocialEntity newMember) {
        agents.add(newMember);
    }


    public void removeMember(SocialEntity oldMember) {
        agents.remove(oldMember);
    }


    public void setMembers(ArrayList<SocialEntity> newMembers) {
        agents = newMembers;
    }
}
