package _thomas_Example;





import es.upv.dsic.gti_ia.core.AgentID;


import es.upv.dsic.gti_ia.architecture.*;




public class clientAgent extends QueueAgent {
	
	
	private Monitor ad = new Monitor();
	public clientAgent(AgentID aid) throws Exception{
		super(aid);
	}
	
	
	protected void execute(){

	    //lo hacemos para que no acabe el proceso
	    ad.waiting();
	    
	};
							 
	}
    