package es.upv.dsic.gti_ia.cAgents;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

public class TransitionTable implements Cloneable{
	private Hashtable<String,Set<String>> transitions = new Hashtable<String,Set<String>>();
	
	protected void clear(){
		transitions.clear();
	}
	
	protected void addTransition(String from, String destination){
		transitions.get(from).add(destination);
	}
	
	protected void removeTransition(String from, String destination){
		transitions.get(from).remove(destination);
	}
	
	protected void addState(String name){
		transitions.put(name, new HashSet<String>());
	}
	
	protected void removeState(String name){
		transitions.remove(name);
					
		Enumeration<String> e = transitions.keys();
					
		//iterate through Hashtable keys Enumeration and remove any transition to the removed state
		while(e.hasMoreElements())
			transitions.get(e.nextElement()).remove(name);
	}
	
	protected boolean existsTransation(String from, String destination){
		return transitions.get(from).contains(destination);
	}
	
	public Set<String> getTransitions(String from){
		return transitions.get(from);
	}
}