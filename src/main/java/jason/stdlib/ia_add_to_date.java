// Internal action code for project magentix2JasonConv

package jason.stdlib;
import java.util.Calendar;

import es.upv.dsic.gti_ia.jason.conversationsFactory.protocolInternalAction;
import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

/**
 * This class represents the internal action for adding a number of years, months and days to a date defined bye the set
 * year, month, day
 * @author Bexy Alfonso Espinosa
 */

public class ia_add_to_date extends protocolInternalAction {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override public int getMinArgs() { return 9; };
	@Override public int getMaxArgs() { return 9; };

	@Override
	public void checkArguments(Term[] args) throws JasonException{
		
		super.checkArguments(args);
		boolean result = true;
		int cont=0;
		while (result && (cont<9)) {
			result = (((Term)args[cont]).isNumeric());
			cont++;
		}

		if (!result)
		{
			throw JasonException.createWrongArgument(this,"Parameters must be in correct format.");
		}
	}
	/*
	 * (non-Javadoc)
	 * @see jason.asSemantics.DefaultInternalAction#execute(jason.asSemantics.TransitionSystem, jason.asSemantics.Unifier, jason.asSyntax.Term[])
	 * The parameter args must have 9 elements: the first three are the current year, month and day, the second three are
	 * the number of years, months and days to add, and the third three are the resulting date as year, month and day
	 */
	@Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        // execute the internal action
        ts.getAg().getLogger().fine("executing internal action 'jason.stdlib.ia_add_to_date'");
        
        int year = getTermAsInt(args[0]);
        int month = getTermAsInt(args[1]);
        int day = getTermAsInt(args[2]);

        int addy = getTermAsInt(args[3]);
        int addm = getTermAsInt(args[4]);
        int addd = getTermAsInt(args[5]);
        
        //SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy"); 
        Calendar c = Calendar.getInstance();
        c.set(year, month-1, day);
        c.add(Calendar.YEAR, addy);
        c.add(Calendar.MONTH, addm);
        c.add(Calendar.DAY_OF_YEAR, addd);
        NumberTermImpl resulty  = new NumberTermImpl(c.get(Calendar.YEAR));
        NumberTermImpl resultm  = new NumberTermImpl(c.get(Calendar.MONTH)+1);
        NumberTermImpl resultd  = new NumberTermImpl(c.get(Calendar.DAY_OF_MONTH));
		
		boolean result = ((un.unifies(resulty, args[6]))&&(un.unifies(resultm, args[7]))&&(un.unifies(resultd, args[8])));
        
        /*if (true) { // just to show how to throw another kind of exception
            throw new JasonException("not implemented!");
        }*/
        
        // everything ok, so returns true
        return result;
    }
}
