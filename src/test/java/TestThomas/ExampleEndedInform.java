package Thomas_example;

import es.upv.dsic.gti_ia.cAgents.BeginState;
import es.upv.dsic.gti_ia.cAgents.BeginStateMethod;
import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CFactory;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.FinalState;
import es.upv.dsic.gti_ia.cAgents.FinalStateMethod;
import es.upv.dsic.gti_ia.cAgents.ReceiveState;
import es.upv.dsic.gti_ia.cAgents.ReceiveStateMethod;
import es.upv.dsic.gti_ia.cAgents.WaitState;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.MessageFilter;

/**
 * Abstract class that defines the protocol that the Commitment Store follows to attend
 * the petitions of the agents.
 * @author Jaume Jordan
 *
 */
public abstract class ExampleEndedInform {
	
	
	/**
	 * Begin method executed to begin the conversation
	 * @param myProcessor {@link CProcessor} that manage the conversation
	 * @param msg initial message
	 */
	protected void doBegin(CProcessor myProcessor, ACLMessage msg) {
		myProcessor.getInternalData().put("InitialMessage", msg);
	}
	
	class Begin_Method implements BeginStateMethod {
		public String run(CProcessor myProcessor, ACLMessage msg) {
			doBegin(myProcessor, msg);
			return "WAIT";
		};
	}

	/**
	 * Creates a response depending on the petition received previously in the {@link ACLMessage} parameter
	 * @param myProcessor {@link CProcessor} that manage the conversation
	 * @param msgReceived {@link ACLMessage} received as a petition by other agent
	 * @return {@link ACLMessage} with the response to give or null if it is not necessary to respond
	 */
	protected abstract boolean doFinish(CProcessor myProcessor, ACLMessage msgReceived);
	
	class Receive_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			boolean finish=doFinish(myProcessor, messageReceived);
			if(finish)
				return "DIE";
			else 
				return "WAIT";
		};
	}
	
	/**
	 * Actions to perform when the DIE message is received.
	 * @param myProcessor {@link CProcessor} that manage the conversation
	 */
	protected abstract void doDie(CProcessor myProcessor);
	
	
	class Die_Method implements FinalStateMethod {
		public void run(CProcessor myProcessor, ACLMessage messageToSend) {
			doDie(myProcessor);
		}
	}
	
	
	/**
	 * Creates a new commitment store protocol {@link CFactory}
	 * @param name factory's name
	 * @param availableConversations maximum number of conversation this {@link CFactory} can manage simultaneously
	 * @param myAgent agent owner of this CFactory
	 * @return a new commitment store protocol factory
	 */
	public CFactory newFactory(String name, int availableConversations, CAgent myAgent) {

		// Create factory
		
		MessageFilter filter = new MessageFilter("performative = INFORM");
		CFactory theFactory = new CFactory(name, filter,
				availableConversations, myAgent);

		// Processor template setup

		CProcessor processor = theFactory.cProcessorTemplate();

		// BEGIN State

		BeginState BEGIN = (BeginState) processor.getState("BEGIN");
		BEGIN.setMethod(new Begin_Method());
		
		
		WaitState WAIT = new WaitState("WAIT", 0);
		processor.registerState(WAIT);
		processor.addTransition(BEGIN, WAIT);
		
		ReceiveState RECEIVE = new ReceiveState("RECEIVE");
		RECEIVE.setMethod(new Receive_Method());
		filter = new MessageFilter("performative = INFORM");
		RECEIVE.setAcceptFilter(filter);
		processor.registerState(RECEIVE);
		processor.addTransition(WAIT,RECEIVE);	
		processor.addTransition(RECEIVE, WAIT);	

		
		FinalState DIE = new FinalState("DIE");
		DIE.setMethod(new Die_Method());
		processor.registerState(DIE);
		processor.addTransition(RECEIVE, DIE);
		
		
		
		return theFactory;
		
	}
}