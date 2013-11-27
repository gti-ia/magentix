// Internal action code for project magentix2JasonConv

package jason.stdlib;

import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvJasonAgent;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvMagentixAgArch;
import jason.JasonException;
import jason.asSemantics.*;
import jason.asSyntax.*;

import java.net.*;
import java.util.Calendar;

/**
 * This class represents the internal action touse the Jason  conversations logger 
 * for logging a text with adding the current pc name, and the currrent time
 * mWater prototype
 * @author Bexy Alfonso Espinosa
 */

public class ia_save_log extends DefaultInternalAction {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	@Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {

			  Calendar cal = Calendar.getInstance();
		      InetAddress add = InetAddress.getLocalHost();

		      int temp  = add.toString().indexOf( "/" );
		      InetAddress address = InetAddress.getByName( add.toString().substring(temp+1) );
		      String hostname = address.getHostName();
		
		try{
    		String text  ;
    		if (args[0].isAtom()){
    			
    			text = ((Atom)args[0]).toString();
    		}else
    		if (args[0].isString()){
    			text = ((StringTerm)args[0]).getString();
    		}else
    		if (args[0].isNumeric()){
    			double  tmp =  ((NumberTerm)args[0]).solve();
    			text = Double.toString(tmp);
    		}else
    		if (args[0].isLiteral()){
    			text = ((LiteralImpl)args[0]).toString();
    		}else	{
    			text = args[0].toString();
    		}
    		
        	ConvJasonAgent myag = ((ConvMagentixAgArch)ts.getUserAgArch()).getJasonAgent();
        	myag.getconvlogger().info(text+" pc: "+hostname+
        			" time: "+cal.get(Calendar.HOUR)+":"+cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.SECOND)+":"+cal.get(Calendar.MILLISECOND));
    	}catch(Exception e){
    		throw new JasonException("Bad arguments or internal error.");
    	}

        
        // everything ok, so returns true
        return true;
    }
}
