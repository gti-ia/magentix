package es.upv.dsic.gti_ia.jason.conversationsFactory;

import org.apache.log4j.Logger;

import jason.bb.BeliefBase;
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
 * @author Bexy Alfonso Espinosa
 *
 */

public class ConvJasonAgent extends CAgent{
	
	private ConvMagentixAgArch agArch; //Bexy
	private static Logger convlogger ;
	
	/**
	 * Sets an additional logger to the agent
	 * @param log
	 */
	public void setconvLogger(Logger log){
		convlogger = log;
	}
	
	/**
	 * Gets the additional logger to the agent
	 * @param log
	 */
	public Logger getconvlogger(){
		return convlogger;
	}
	
	/**
	 * Creates a new Jason Agent
	 * @param aid Agent identifier
	 * @param filename File with the AgentSepak code
	 * @param arch Agent architecture
	 * @throws Exception
	 */
	public ConvJasonAgent(AgentID aid, String filename, ConvMagentixAgArch arch, BeliefBase bb, String[] bbParameters) throws Exception {
		super(aid);
		this.agArch = arch;
		this.agArch.init(filename, this, bb, bbParameters);
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
	
/**
 * It has the same function than startConversation method but a factory must be provided and it 
 * is possible to recover the CProcessor object when creating a conversation.
 * */
	public ConvCProcessor newConversation(ACLMessage msg, CProcessor parent, Boolean sync, ConvCFactory fact) {
		this.lock();
		ConvCProcessor proc = null;
		if (fact.templateIsEqual(msg)) {
			proc = fact.startConversation(msg, parent, sync);
			this.unlock();
			return proc;
		}
		
		System.out.println("No hay factorias");
		this.unlock();
		return proc;
		// PENDIENTE: Lanzar excepci�n si no hay fabricas asociadas
	}
	
	public String newConvID(){
		return newConversationID();
	}
	
	public void addFactoryAsInitiator(ConvCFactory factory) {
		factory.convinitiator = true;
		super.addFactoryAsInitiator(factory);
	}
	
	protected void addProcessor(String conversationID, CProcessor processor) {
		super.addProcessor(conversationID, processor);
	}

}


