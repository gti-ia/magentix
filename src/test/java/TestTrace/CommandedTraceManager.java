package TestTrace;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Level;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.TraceEvent;
import es.upv.dsic.gti_ia.trace.TraceManager;

/**
 * Trace manager commanded externally.
 * 
 * @author Jose Vicente Ruiz (jruiz1@dsic.upv.es)
 * @author Jose Alemany Bordera  -  jalemany1@dsic.upv.es
 */

public class CommandedTraceManager extends TraceManager {
	/* Constants */
	public static final int END = 0;
	
	public static final String[] COMMAND_NAMES = {"END"};
	
	/* Attributes */
	// List of commands.
	private ArrayList<Integer> commands;

	// Semaphore that will determine the amount of commands to process.
	private Semaphore checkCommand;
	
	// List of received messages.
	private ArrayList<ACLMessage> messagesReceived;
	
	// List of sent messages.
	private ArrayList<ACLMessage> messagesSent;

	// List of received trace events.
	private ArrayList<TraceEvent> traceEvents;
	
	/* Constructor */
	public CommandedTraceManager(AgentID aid, Boolean monitorizable) throws Exception{
		super(aid, monitorizable);
		logger.setLevel(Level.DEBUG);
		
		commands = new ArrayList<Integer>();
		checkCommand = new Semaphore(0); 
		messagesReceived = new ArrayList<ACLMessage>();
		messagesSent = new ArrayList<ACLMessage>();
		traceEvents = new ArrayList<TraceEvent>();
	}
	
	/* Methods */
	public void execute() {
		int currentCommand;
		do {
			// It will wait until there are commands to process.
			try {
				checkCommand.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}   
			
			// Obtain the first command in the list of commands and remove it.
			currentCommand = commands.get(0);
			commands.remove(0);
			logger.debug(this.getName() + " is going to execute command " + COMMAND_NAMES[currentCommand] + " ("+currentCommand+")");
			
			// Check current command.
			switch (currentCommand) {}
		} while(currentCommand != END);
	}
	
	@Override
	public synchronized void send(ACLMessage msg) {
		super.send(msg);
		logger.debug(this.getName() + " has sent an ACL MESSAGE with this content: " + msg);
		messagesSent.add(msg);
	}
	
	public synchronized void onMessage(ACLMessage msg) {
		logger.debug(this.getName() + " has received an ACL MESSAGE with this content: " + msg);
		messagesReceived.add(msg);
	}
	
	public synchronized void onTraceEvent(TraceEvent tEvent) {
		logger.debug(this.getName() + " has received a TRACE EVENT with this content: " + tEvent);
		traceEvents.add(tEvent);
	}
	
	/* Getters and setters */
	public synchronized ArrayList<Integer> getCommands() {
		return commands;
	}
	
	/**
	 * Give a command to the agent.
	 * 
	 * The required information must be set properly with the specific methods
	 * in order to allow the agent to complete the command properly.
	 * 
	 * @param newCommand
	 */
	public synchronized void addCommand(int newCommand) {
		this.commands.add(newCommand);
		checkCommand.release();
	}
	
	public synchronized ArrayList<ACLMessage> getReceivedMessages() {
		return messagesReceived;
	}
	
	public synchronized void clearReceivedMessages() {
		messagesReceived.clear();
	}
	
	public synchronized ArrayList<ACLMessage> getSentMessages() {
		return messagesSent;
	}
	
	public synchronized void clearSentMessages() {
		messagesSent.clear();
	}

	public synchronized ArrayList<TraceEvent> getTraceEvents() {
		return traceEvents;
	}
	
	public void clearTraceEvents() {
		traceEvents.clear();
	}
}