// Internal action code for project magentix2JasonConv

package jason.stdlib;

import java.util.logging.FileHandler;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;
import jason.runtime.MASConsoleLogFormatter;

/**
 * This class represents the internal action for adding a file handler with logging purposes 
 * @author Bexy Alfonso Espinosa
 */

public class setlogfile extends DefaultInternalAction {

   
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {

    	if ((args.length == 1)&&(args[0].isString())){
    		String filename = ((StringTermImpl)args[0]).getString();
    		
    		FileHandler fh = new FileHandler(filename,true);

    		fh.setFormatter(new MASConsoleLogFormatter());
    		ts.getLogger().getParent().addHandler(fh) ;

    		//ts.getLogger().setUseParentHandlers(false); //This must be modified in order to turn off the default console of Jason
    	}else {
    		throw new JasonException("Wrong arguments for setting the log file. It must be provided one string argument!");
    	}
    	
        
        return true;
    }


}
