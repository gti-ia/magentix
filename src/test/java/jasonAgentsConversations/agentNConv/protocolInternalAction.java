package jasonAgentsConversations.agentNConv;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.upv.dsic.gti_ia.cAgents.CFactory;
//import es.upv.dsic.gti_ia.cAgents.CProcessor;
import jason.asSemantics.DefaultInternalAction;
import jason.asSyntax.*;

public abstract class protocolInternalAction extends DefaultInternalAction {


	//MyFIPA_CONTRACTNET_Initiator fcnp;

	/**
	 * 
	 */
	public HashMap<String, Conversation> conversationsList =  new HashMap<String, Conversation>(); 

	private static final long serialVersionUID = 1L;
	protected String protocolSteep;
	protected ConvCFactory Protocol_Factory;
	//protected CProcessor Protocol_Processor;
	protected int timeOut = 0;
	protected long conversationTime;
	
	protected String agName;
	protected String agentConversationID;
	
	
    public boolean suspendIntention()   { return false;  }
    public boolean canBeUsedInContext() { return false;  }

	public String getAtomAsString(Term term){
		
		return ((Atom)term).toString();
		
	}
	
	public String getTermAsString(Term term){
		String result = "";
		if (term.isAtom()){
			
			result = ((Atom)term).toString();
		}else
		if (term.isString()){
			result = ((StringTerm)term).getString();
		}else
		if (term.isNumeric()){
			double  tmp =  ((NumberTerm)term).solve();
			result = Double.toString(tmp);
		}else
		if (term.isLiteral()){
			result = ((LiteralImpl)term).toString();
		}else	{
			result = term.toString();
		}
		return result;
		
	}
	
	public int getTermAsInt(Term term){
		
		return (int) ((NumberTerm)term).solve();
		
	}
	
	public long getTermAslong(Term term){
		
		return (long) ((NumberTerm)term).solve();
		
	}

	public double getTermAsdouble(Term term){
		
		return (double) ((NumberTerm)term).solve();
		
	}
	
	public List<String> getTermAsStringList(Term term){
		String selem;
		List<String> result = new ArrayList<String>();
		for (Term t: (ListTerm)term) {
			selem = ((Atom)t).toString();
			result.add(selem);
		}
		return result;
		
	}
	
	public List<Integer> getTermAsintList(Term term){
		int selem;
		List<Integer> result = new ArrayList<Integer>();
		for (Term t: (ListTerm)term) {
			selem = (int) ((NumberTerm)t).solve();
			result.add(selem);
		}
		return result;
		
	}
	
	public List<Double> getTermAsdoubleList(Term term){
		double selem;
		List<Double> result = new ArrayList<Double>();
		for (Term t: (ListTerm)term) {
			selem = (double) ((NumberTerm)t).solve();
			result.add(selem);
		}
		return result;
		
	}
	
	public Literal getTermAsLiteral(Term term){
		
		return   LiteralImpl.parseLiteral( (term).toString()  );
		
	}
	


}