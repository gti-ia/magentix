package TestTrace;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

import org.apache.log4j.Level;
import org.apache.qpid.transport.MessageTransfer;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.TraceEvent;
import es.upv.dsic.gti_ia.trace.TraceInteract;
import es.upv.dsic.gti_ia.trace.TraceManager;
import es.upv.dsic.gti_ia.trace.TraceMask;
import es.upv.dsic.gti_ia.trace.TraceServiceNotAllowedException;

/**
 * Agent commanded externally.
 * 
 * The trace manager and the service name must be set with the corresponding
 * setters before giving any command.
 * 
 * @author Jose Vicente Ruiz (jruiz1@dsic.upv.es)
 * 
 */
public class CommandedAgent extends BaseAgent {
	/* Constants */
	public static final int END = 0;
	public static final int SUBSCRIBE = 1;
	public static final int UNSUBSCRIBE = 2;
	public static final int PUBLISH = 3;
	public static final int UNPUBLISH = 4;
	public static final int LIST_ENTITIES = 5;
	public static final int LIST_SERVICES = 6;

	public static final String[] COMMAND_NAMES = { "END", "SUBSCRIBE",
			"UNSUBSCRIBE", "PUBLISH", "UNPUBLISH", "LIST_ENTITIES",
			"LIST_SERVICES" };

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

	// Data related to the trace service.
	private AgentID tm;
	private String serviceName;
	private AgentID originAgent;
	private String description;
	private boolean requestAll;

	/* Constructor */
	public CommandedAgent(AgentID aid) throws Exception {
		super(aid);
		logger.setLevel(Level.DEBUG);

		commands = new ArrayList<Integer>();
		checkCommand = new Semaphore(0);
		messagesReceived = new ArrayList<ACLMessage>();
		messagesSent = new ArrayList<ACLMessage>();
		traceEvents = new ArrayList<TraceEvent>();
		tm = null;
		serviceName = null;
		originAgent = null;
		description = null;
		requestAll = false;
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
			logger.debug(this.getName() + " is going to execute command "
					+ COMMAND_NAMES[currentCommand] + " (" + currentCommand
					+ ")");

			// Check current command.
			switch (currentCommand) {
			case SUBSCRIBE:
				if (requestAll) {
					// All the services must be requested.
					if (tm == null) {
						// No trace manager specified. Use default.
						try {
							TraceInteract.requestAllTracingServices(this);
						} catch (TraceServiceNotAllowedException e) {
							e.printStackTrace();
						}
						logger.debug(this.getName()
								+ " has SUBSCRIBED to ALL tracing services of the default TM.\n");
					} else {
						// A trace manager has been specified. Use it.
						try {
							TraceInteract.requestAllTracingServices(tm, this);
						} catch (TraceServiceNotAllowedException e) {
							e.printStackTrace();
						}
						logger.debug(this.getName()
								+ " has SUBSCRIBED to ALL tracing services of an alternative TM ("
								+ tm.toString() + ").\n");
					}
				} else {
					// Only one service must be requested.
					if (tm == null) {
						// No trace manager specified. Use default.
						if (originAgent == null) {
							// No origin agent specified. Use any.
							try {
								TraceInteract.requestTracingService(this,
										serviceName);
							} catch (TraceServiceNotAllowedException e) {
								e.printStackTrace();
							}
							logger.debug(this.getName()
									+ " has SUBSCRIBED to \""
									+ serviceName
									+ "\" tracing service of any agent through the default TM.\n");
						} else {
							// An origin agent has been specified. Use it.
							try {
								TraceInteract.requestTracingService(this,
										serviceName, originAgent);
							} catch (TraceServiceNotAllowedException e) {
								e.printStackTrace();
							}
							logger.debug(this.getName()
									+ " has SUBSCRIBED to \"" + serviceName
									+ "\" tracing service of agent \""
									+ originAgent.toString()
									+ "\" through the default TM.\n");
						}
					} else {
						// A trace manager has been specified. Use it.
						if (originAgent == null) {
							// No origin agent specified. Use any.
							try {
								TraceInteract.requestTracingService(tm, this,
										serviceName);
							} catch (TraceServiceNotAllowedException e) {
								e.printStackTrace();
							}
							logger.debug(this.getName()
									+ " has SUBSCRIBED to \""
									+ serviceName
									+ "\" tracing service of any agent through an alternative TM ("
									+ tm.toString() + ").\n");
						} else {
							// An origin agent has been specified. Use it.
							try {
								TraceInteract.requestTracingService(tm, this,
										serviceName, originAgent);
							} catch (TraceServiceNotAllowedException e) {
								e.printStackTrace();
							}
							logger.debug(this.getName()
									+ " has SUBSCRIBED to \"" + serviceName
									+ "\" tracing service of agent \""
									+ originAgent.toString()
									+ "\" through an alternative TM ("
									+ tm.toString() + ").\n");
						}
					}
				}
				break;

			case UNSUBSCRIBE:
				if (tm == null) {
					// No trace manager specified. Use default.
					if (originAgent == null) {
						// No origin agent specified. Use any.
						try {
							TraceInteract.cancelTracingServiceSubscription(
									this, serviceName);
						} catch (TraceServiceNotAllowedException e) {
							e.printStackTrace();
						}
						logger.debug(this.getName()
								+ " has UNSUBSCRIBED of \""
								+ serviceName
								+ "\" tracing service of any agent through the default TM.\n");
					} else {
						// An origin agent has been specified. Use it.
						try {
							TraceInteract.cancelTracingServiceSubscription(
									this, serviceName, originAgent);
						} catch (TraceServiceNotAllowedException e) {
							e.printStackTrace();
						}
						logger.debug(this.getName() + " has UNSUBSCRIBED of \""
								+ serviceName
								+ "\" tracing service of agent \""
								+ originAgent.toString()
								+ "\" through the default TM.\n");
					}
				} else {
					// A trace manager has been specified. Use it.
					if (originAgent == null) {
						// No origin agent specified. Use any.
						try {
							TraceInteract.cancelTracingServiceSubscription(tm,
									this, serviceName);
						} catch (TraceServiceNotAllowedException e) {
							e.printStackTrace();
						}
						logger.debug(this.getName()
								+ " has UNSUBSCRIBED of \""
								+ serviceName
								+ "\" tracing service of any agent through an alternative TM ("
								+ tm.toString() + ").\n");
					} else {
						// An origin agent has been specified. Use it.
						try {
							TraceInteract.cancelTracingServiceSubscription(tm,
									this, serviceName, originAgent);
						} catch (TraceServiceNotAllowedException e) {
							e.printStackTrace();
						}
						logger.debug(this.getName() + " has UNSUBSCRIBED of \""
								+ serviceName
								+ "\" tracing service of agent \""
								+ originAgent.toString()
								+ "\" through an alternative TM ("
								+ tm.toString() + ").\n");
					}
				}
				break;

			case PUBLISH:
				if (tm == null) {
					// No trace manager specified. Use default.
					try {
						TraceInteract.publishTracingService(this, serviceName,
								description);
					} catch (TraceServiceNotAllowedException e) {
						e.printStackTrace();
					}
					logger.debug(this.getName()
							+ " has PUBLISHED a tracing service called \""
							+ serviceName + "\" described as \"" + description
							+ "\" through the default TM.\n");
				} else {
					// A trace manager has been specified. Use it.
					try {
						TraceInteract.publishTracingService(tm, this,
								serviceName, description);
					} catch (TraceServiceNotAllowedException e) {
						e.printStackTrace();
					}
					logger.debug(this.getName()
							+ " has PUBLISHED a tracing service called \""
							+ serviceName + "\" described as \"" + description
							+ "\" through an alternative TM (" + tm.toString()
							+ ").\n");
				}
				break;

			case UNPUBLISH:
				if (tm == null) {
					// No trace manager specified. Use default.
					try {
						TraceInteract
								.unpublishTracingService(this, serviceName);
					} catch (TraceServiceNotAllowedException e) {
						e.printStackTrace();
					}
					logger.debug(this.getName()
							+ " has UNPUBLISHED a tracing service called \""
							+ serviceName + "\" through the default TM.\n");
				} else {
					// A trace manager has been specified. Use it.
					try {
						TraceInteract.unpublishTracingService(tm, this,
								serviceName);
					} catch (TraceServiceNotAllowedException e) {
						e.printStackTrace();
					}
					logger.debug(this.getName()
							+ " has UNPUBLISHED a tracing service called \""
							+ serviceName + "\" through an alternative TM ("
							+ tm.toString() + ").\n");
				}
				break;

			case LIST_ENTITIES:
				if (tm == null) {
					// No trace manager specified. Use default.
					try {
						TraceInteract.listTracingEntities(this);
					} catch (TraceServiceNotAllowedException e) {
						e.printStackTrace();
					}
					logger.debug(this.getName()
							+ " has asked for a LIST of ENTITIES to the default TM.\n");
				} else {
					// A trace manager has been specified. Use it.
					try {
						TraceInteract.listTracingEntities(tm, this);
					} catch (TraceServiceNotAllowedException e) {
						e.printStackTrace();
					}
					logger.debug(this.getName()
							+ " has asked for a LIST of ENTITIES to an alternative TM ("
							+ tm.toString() + ").\n");
				}
				break;

			case LIST_SERVICES:
				if (tm == null) {
					// No trace manager specified. Use default.
					try {
						TraceInteract.listTracingServices(this);
					} catch (TraceServiceNotAllowedException e) {
						e.printStackTrace();
					}
					logger.debug(this.getName()
							+ " has asked for a LIST of SERVICES to the default TM.\n");
				} else {
					// A trace manager has been specified. Use it.
					try {
						TraceInteract.listTracingServices(tm, this);
					} catch (TraceServiceNotAllowedException e) {
						e.printStackTrace();
					}
					logger.debug(this.getName()
							+ " has asked for a LIST of SERVICES to an alternative TM ("
							+ tm.toString() + ").\n");
				}
				break;
			}
		} while (currentCommand != END);
	}

	@Override
	public synchronized void send(ACLMessage msg) {
		super.send(msg);
		logger.debug(this.getName()
				+ " has sent an ACL MESSAGE with this content: " + msg);
		messagesSent.add(msg);
	}

	public synchronized void onMessage(ACLMessage msg) {
		logger.debug(this.getName()
				+ " has received an ACL MESSAGE with this content: " + msg);
		messagesReceived.add(msg);
	}

	public synchronized void onTraceEvent(TraceEvent tEvent) {
		logger.debug(this.getName()
				+ " has received a TRACE EVENT with this content: " + tEvent);
		traceEvents.add(tEvent);
	}

	/* Getters and setters */
	public synchronized ArrayList<Integer> getCommands() {
		return commands;
	}

	/**
	 * Activate all the tracing services in the trace mask of this commanded
	 * agent.
	 */
	public void setAllAvailableTraceMask() {
		Field field = null;
		TraceMask tm = new TraceMask(true);

		try {
			field = BaseAgent.class.getDeclaredField("traceMask");
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}

		field.setAccessible(true);

		try {
			field.set(this, tm);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Set the given trace mask as the new mask of this commanded agent. 
	 * 
	 * @param the new trace mask.
	 */
	public void setTraceMask(TraceMask tm) {
		Field field = null;
		try {
			field = BaseAgent.class.getDeclaredField("traceMask");
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}

		field.setAccessible(true);

		try {
			field.set(this, tm);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
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

	public AgentID getTm() {
		return tm;
	}

	public void setTm(AgentID tm) {
		this.tm = tm;
	}

	public void setDefaultTm() {
		this.tm = null;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public AgentID getOriginAgent() {
		return originAgent;
	}

	public void setOriginAgent(AgentID originAgent) {
		this.originAgent = originAgent;
	}

	public void setAnyOriginAgent() {
		this.originAgent = null;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isRequestAll() {
		return requestAll;
	}

	public void setRequestAll(boolean requestAll) {
		this.requestAll = requestAll;
	}
}
