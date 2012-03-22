// Internal action code for project MWaterWeb

package jason.stdlib;

import java.util.Hashtable;


import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvJasonAgent;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvMagentixAgArch;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class ia_manage_web_agent extends DefaultInternalAction {
	
	private Hashtable<String,ConvJasonAgent> agents = new Hashtable<String,ConvJasonAgent>();

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	@Override public int getMinArgs() { return 2; };
	@Override public int getMaxArgs() { return 2; };

	@Override
	public void checkArguments(Term[] args) throws JasonException
	{
		super.checkArguments(args);
		boolean result = false;

		if ((((Term)args[0]).isAtom())&&( 
				(((Term)args[0]).toString().compareTo("create")==0 )||
				(((Term)args[0]).toString().compareTo("kill")==0 ))) {result = true;};

		result = (result&&((Term)args[1]).isString());
		
		if (!result)
		{
			throw JasonException.createWrongArgument(this,"Parameters must be in correct format.");
		}
	}
	
	/**
	 * Creates or kills an agent
	 * @param args args[0]: create/kill , args[1]: agentName
	 */
	@Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        // execute the internal action
		boolean result = false;
		
        ts.getAg().getLogger().fine("executing internal action 'jason.stdlib.ia_manage_web_agent'");
        checkArguments(args);
        ConvMagentixAgArch arch = null;
        ConvJasonAgent cag = null;
        
        try{
        Term action = args[0];
        String agentName = ((StringTerm)args[1]).getString();
        if (action.toString().compareTo("create")==0){
        	if (agents.get(agentName)==null){
        		arch = new ConvMagentixAgArch();
        		cag = new ConvJasonAgent(new AgentID(agentName), "./src/test/java/mWaterWeb/webInterface/webCommParticipant.asl", arch,null,null);
        		agents.put(agentName, cag);
        		cag.start();
        	}
    		result = true;

        }else{
        	 if (action.toString().compareTo("kill")==0){
        		 cag = agents.get(agentName);
        		 if (cag!=null)
        		 	{
        			 cag.Shutdown();
        			 agents.remove(agentName);
        			 
        			 result = true;
        		 	}else{
        		 		throw new NullPointerException();
        		 	}
        	 }
        	 
        }
        }catch( NullPointerException npe){
        	System.out.println("Agent to kill not found!");
        }catch( Exception e ){
        	e.printStackTrace();
        	throw new JasonException("Not possible to create or kill agent in the internal action! ");
        }

        return result;
    }
}
