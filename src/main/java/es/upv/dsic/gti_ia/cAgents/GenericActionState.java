package es.upv.dsic.gti_ia.cAgents;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class GenericActionState extends ActionState{

	public GenericActionState(String n) {
		super(n);
	}

	@Override
	protected String run(CProcessor myProcessor) {
		System.out.println("Performing an action...");		
		String next = "";
		Set<String> transitions = new HashSet<String>();
		transitions = myProcessor.getTransitionTable().getTransitions(this.getName());
		Iterator<String> it = transitions.iterator();
		if (it.hasNext()) {
	        // Get element
	        next = it.next();
	    }
		return next;
	}
}