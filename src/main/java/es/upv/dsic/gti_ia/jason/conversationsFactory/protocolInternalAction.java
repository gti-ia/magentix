package es.upv.dsic.gti_ia.jason.conversationsFactory;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import jason.asSemantics.DefaultInternalAction;
import jason.asSyntax.*;

/**
 * This class represents an abstract internal action with the main elements 
 * of this type of action to be inherited when using conversations in Jason agents
 * @author Bexy Alfonso Espinosa
 */

public abstract class protocolInternalAction extends DefaultInternalAction {
	/**
	 * 
	 */
	public HashMap<String, Conversation> conversationsList =  new HashMap<String, Conversation>(); 

	private static final long serialVersionUID = 1L;
	protected String protocolSteep;
	protected ConvCFactory Protocol_Factory;
	protected Hashtable<String, ConvCFactory> CFactories = new Hashtable<String, ConvCFactory>();
	protected int timeOut = 3000;
	protected int joinTimeOut = 4000;
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
				//result = ((StringTerm)term).toString();
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
		String selem = "" ;
		List<String> result = new ArrayList<String>();
		for (Term t: (ListTerm)term) {
			if (t.isAtom())
			{selem = ((Atom)t).toString();}
			else if (t.isLiteral())
			{selem = t.toString();}
			else if (t.isString())
			{selem = ((StringTermImpl)t).getString();}
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

	/**
	 * @param convId: Conversation Jason identifier
	 * @param protocolstr: A small string for identifying the protocol
	 * @param initiator: true if it is the initiator agent or false if it isn't
	 * @return A formatted string for the name of the factory
	 */
	protected String getFactoryName(String convId, String protocolstr, boolean initiator) {
		String rol="INI";
		if (!initiator)
			rol="PART";
		String facName = convId;
		facName = facName.replaceAll("[\\W]|^_", "");
		facName = facName +"_"+rol+"_"+protocolstr+"FACTORY" ;
		return facName;
	}

}