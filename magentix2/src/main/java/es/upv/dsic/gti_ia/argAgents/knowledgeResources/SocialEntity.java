package es.upv.dsic.gti_ia.argAgents.knowledgeResources;

import java.io.Serializable;
import java.util.*;


/**
 * Implementation of the concept <i>SocialEntity</i>
 * 
 */

public class SocialEntity implements Serializable{
	

	private static final long serialVersionUID = -5802164428293153519L;
	private long id;
	private String name;
	private String role;
	private ArrayList<Norm> norms;
	private ValPref valpref;

    public SocialEntity(long id, String name,String role, ArrayList<Norm> norms, ValPref valpref) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.norms = norms;
        this.valpref = valpref;
    }


    public SocialEntity() {
    	id = -1;
    	name = "";
    	role = "";
    	norms = new ArrayList<Norm>();
    	valpref = new ValPref();
    	
    }

    // Property hasID

    public long getID() {
        return id;
    }

    public void setID(long newID) {
        id = newID;
    }


    // Property hasNorm

    public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public ArrayList<Norm> getNorms() {
        return norms;
    }


    public void addNorm(Norm newNorm) {
        norms.add(newNorm);
    }


    public void removeNorm(Norm oldNorm) {
        norms.remove((Norm) oldNorm);
    }


    public void setNorms(ArrayList<Norm> newNorms) {
        norms = newNorms;
    }

    // Property hasRole

    public String getRole() {
        return (String) role;
    }

    public void setRole(String newRole) {
        role = newRole;
    }

    // Property hasValPref

    public ValPref getValPref() {
        return (ValPref) valpref;
    }


    public void setValPref(ValPref newValPref) {
        valpref = newValPref;
    }
}
