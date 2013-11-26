// Internal action code for project magentix2JasonConv

package jason.stdlib;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

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

    		fh.setFormatter(new MyMASConsoleLogFormatter());
  		
    		Handler han = ts.getLogger().getParent().getHandlers()[0]; //comment in order to turn on the default console
    		
    		ts.getLogger().getParent().removeHandler(han);//comment in order to turn on the default console
    		ts.getLogger().getParent().addHandler(fh);
    		
    		
    		//ts.getLogger().getParent().setUseParentHandlers(false); //comment in order to turn off the default console

    	}else {
    		throw new JasonException("Wrong arguments for setting the log file. It must be provided one string argument!");
    	}
    	
        
        return true;
    }

	public class MyMASConsoleLogFormatter extends java.util.logging.Formatter {

	    public String format(LogRecord l) {
	    	Calendar cal = Calendar.getInstance();
	    	String time = ""+cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH)+"-"+
	    	cal.get(Calendar.DAY_OF_MONTH)+" "+cal.get(Calendar.HOUR)+":"+
	    	cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.SECOND)+","+cal.get(Calendar.MILLISECOND);
	        StringBuilder s = new StringBuilder("["+time+" Agent:");
	        s.append(getAgName(l));
	        s.append("] ");
	        s.append(l.getMessage());
	        if (l.getThrown() != null) {
	            StringWriter sw = new StringWriter();
	            PrintWriter pw = new PrintWriter(sw);
	            l.getThrown().printStackTrace(pw);
	            s.append('\n');
	            s.append(sw);
	        }
	        s.append('\n');
	        return s.toString();
	    }
	    
	    public String getAgName(LogRecord l) {
	        String lname = l.getLoggerName();
	        int posd = lname.lastIndexOf('.');
	        if (posd > 0) {
	            return lname.substring(posd+1);
	        }
	        return lname;
	    }
	}
	
}
