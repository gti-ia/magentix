package jasonAgentsConversations.agent;


import es.upv.dsic.gti_ia.cAgents.BeginState;
import es.upv.dsic.gti_ia.cAgents.BeginStateMethod;
import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.CFactory;
import es.upv.dsic.gti_ia.cAgents.FinalState;
import es.upv.dsic.gti_ia.cAgents.FinalStateMethod;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;

/**
 * 
 * @author Ricard Lopez Fogues
 *
 */

public class ConvJasonAgent extends CAgent{
	
	private ConvMagentixAgArch agArch; //Bexy
	
	/**
	 * Creates a new Jason Agent
	 * @param aid Agent identifier
	 * @param filename File with the AgentSepak code
	 * @param arch Agent architecture
	 * @throws Exception
	 */
	public ConvJasonAgent(AgentID aid, String filename, ConvMagentixAgArch arch) throws Exception {
		super(aid);
		this.agArch = arch;
		this.agArch.init(filename, this);
	}

	@Override
	/**
	 * 
	 */
	protected void finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {}

	@Override
	/**
	 * Executes the architecture
	 */
	protected void execution(CProcessor firstProcessor,
			ACLMessage welcomeMessage) {
		agArch.run();		
	}
	
	/**
	 * returns the architecture
	 * @return
	 */
	public ConvMagentixAgArch getAgArch(){
		return agArch;
	}
	
	public void onMessage(ACLMessage msg) { //Redefinido por Bexy
		super.onMessage(msg);
		//if (msg.getConversationId().trim().compareTo("")==0)
		if (msg.getHeaderValue("jason").compareTo("true")==0)
				agArch.addMessage(msg);
	}
	
	/**
	 * Creates a different default factory adapted to the Jason's reasoning cycle
	 */
	protected final void createDefaultFactory(final CAgent me){
		this.defaultFactory = new CFactory("DefaultFactory",null, 1,this);

		// BEGIN STATE

		BeginState BEGIN = (BeginState) defaultFactory.cProcessorTemplate()
				.getState("BEGIN");
		class BEGIN_Method implements BeginStateMethod {
			public String run(CProcessor myProcessor, ACLMessage msg) {
				//agArch.addMessage(msg); //Bexy
				return "FINAL";
			}
		}
		BEGIN.setMethod(new BEGIN_Method());

		// FINAL STATE

		FinalState FINAL = new FinalState("FINAL");

		class F_Method implements FinalStateMethod {
			public void run(CProcessor myProcessor, ACLMessage msg) {
			}
		}
		FINAL.setMethod(new F_Method());
		defaultFactory.cProcessorTemplate().registerState(FINAL);
		defaultFactory.cProcessorTemplate().addTransition(BEGIN, FINAL);
	}
}

