package conversaciones;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import es.upv.dsic.gti_ia.cAgents.BeginState;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.core.ACLMessage;

public class GenericBeginState extends BeginState{

	public GenericBeginState(String n) {
		super(n);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String run(CProcessor myProcessor, ACLMessage msg) {
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
