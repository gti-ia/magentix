package TestTrace;

import static org.junit.Assert.*;
import jason.mas2j.parser.mas2j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.internal.runners.statements.Fail;

import TestBaseAgent.ConsumerAgent;
import TestBaseAgent.SenderAgent;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.core.TraceEvent;
import es.upv.dsic.gti_ia.trace.TraceManager;
import junit.framework.TestCase;

/**
 * Unit tests of class TraceInteract. All methods are tested twice, first with
 * normal strings and second with strings composed of a wide variety of unusual
 * characters.
 * 
 * All the tests are performed in the same way, since the tested class is only
 * used to create messages.
 * 
 * - First, the expected content of the message is composed.
 * 
 * - Then, the agent that is going to send the message is set up properly with
 * the required data (alternative trace manager, origin agent, tracing service
 * name, etc.).
 * 
 * - Finally, the agent is commanded to use the TraceInteract method that is
 * being tested and the message composed by the method is obtained to compare
 * its information with the expected one.
 * 
 * @author José Vicente Ruiz Cepeda (jruiz1@dsic.upv.es)
 * 
 */
public class TestTraceInteract {

	/* Constants */
	// Dependent of other classes (TraceInteract and TraceManager).
	private static final char SEPARATION_CHAR = '#';
	private static final String PUBLISH_LABEL = "publish";
	private static final String UNPUBLISH_LABEL = "unpublish";
	private static final String ANY_LABEL = "any";
	private static final String LIST_LABEL = "list";
	private static final String SERVICES_LABEL = "services";
	private static final String ENTITIES_LABEL = "entities";
	private static final String ALL_LABEL = "all";

	// Independent, proper of this class.
	private static final String MESSAGES_LANGUAGE = "ACL";
	private static final String DEFAULT_TM_NAME = "qpid://TM@localhost:8080";
	private static final String[] ALTERNATIVE_TM_NAMES = {
			"qpid://ALT_TM@localhost:8080",
			"qpid://ßðŋ@/#~½ßð/.æłæĦß~@localhost:8080" };
	private static final String[] AGENT_NAMES = {
			"qpid://agent1@localhost:8080", "qpid://3àsö\\$%.@localhost:8080" };
	private static final String[] ORIGIN_AGENT_NAMES = {
			"qpid://agent2@localhost:8080",
			"qpid://»ł~|”æðđ#ßæ·æ~æ{ł][{}~@localhost:8080" };
	private static final String[] AGENT_SERVICE_NAMES = {
			"AGENT1_MAGIC_SERVICE", "@ĵ2~|¨$!3*\"$%Ç↓đŋħ»¢”«»¢”æ€¶@@#a" };
	private static final String[] AGENT_DESCRIPTIONS = {
			"And now for something completely different",
			"æßđð	ŋ@~¬€ŧħ]}@{~¬@#½}{←æħ@#~½]" };

	/* Attributes */
	private static CommandedAgent[] agents = { null, null };
	
	static Process qpid_broker;
	

	/* Set up class and tear down class */
	@BeforeClass
	public static void setUpClass() throws Exception {
		
		qpid_broker = Runtime.getRuntime().exec("./installer/magentix2/bin/qpid-broker-0.20/bin/qpid-server");
		BufferedReader reader = new BufferedReader(new InputStreamReader(qpid_broker.getInputStream()));

		String line = reader.readLine();
		while (!line.contains("Qpid Broker Ready")) {
			line = reader.readLine();
		}
		
		AgentsConnection.connect(); // Connecting to Qpid Broker.

		for (int i = 0; i < agents.length; ++i) {
			agents[i] = new CommandedAgent(new AgentID(AGENT_NAMES[i]));
			agents[i].setAllAvailableTraceMask();
			agents[i].start();
		}
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		for (int i = 0; i < agents.length; ++i) {
			agents[i].addCommand(CommandedAgent.END);
		}
		
		AgentsConnection.disconnect();
		qpid_broker.destroy();
	}

	/* Test methods */
	@Test
	public void testPublishTracingServiceDefault0() {
		theTestOfPublishTracingServiceDefault(0);
	}

	@Test
	public void testPublishTracingServiceDefault1() {
		theTestOfPublishTracingServiceDefault(1);
	}

	public void theTestOfPublishTracingServiceDefault(int d) {
		ACLMessage msg;
		ArrayList<ACLMessage> messages;
		String content;

		// Expected message content.
		content = PUBLISH_LABEL + SEPARATION_CHAR
				+ AGENT_SERVICE_NAMES[d].length() + SEPARATION_CHAR
				+ AGENT_SERVICE_NAMES[d] + AGENT_DESCRIPTIONS[d];

		// Clear list of sent messages.
		agents[d].clearSentMessages();

		// Agent publishes a tracing service.
		agents[d].setDefaultTm();
		agents[d].setServiceName(AGENT_SERVICE_NAMES[d]);
		agents[d].setDescription(AGENT_DESCRIPTIONS[d]);
		agents[d].addCommand(CommandedAgent.PUBLISH);

		do {
			messages = agents[d].getSentMessages();
		} while (messages.isEmpty());

		// Get last message.
		msg = messages.get(messages.size() - 1);

		assertEquals(ACLMessage.REQUEST, msg.getPerformativeInt());
		assertEquals(DEFAULT_TM_NAME, msg.getReceiver().toString());
		assertEquals(AGENT_NAMES[d], msg.getSender().toString());
		assertEquals(MESSAGES_LANGUAGE, msg.getLanguage());
		assertEquals(content, msg.getContent());
	}

	@Test
	public void testPublishTracingServiceAlternative0() {
		theTestOfPublishTracingServiceAlternative(0);
	}

	@Test
	public void testPublishTracingServiceAlternative1() {
		theTestOfPublishTracingServiceAlternative(1);
	}

	public void theTestOfPublishTracingServiceAlternative(int d) {
		ACLMessage msg;
		ArrayList<ACLMessage> messages;
		String content;

		// Expected message content.
		content = PUBLISH_LABEL + SEPARATION_CHAR
				+ AGENT_SERVICE_NAMES[d].length() + SEPARATION_CHAR
				+ AGENT_SERVICE_NAMES[d] + AGENT_DESCRIPTIONS[d];

		// Clear list of sent messages.
		agents[d].clearSentMessages();

		// Agent publishes a tracing service.
		agents[d].setTm(new AgentID(ALTERNATIVE_TM_NAMES[d]));
		agents[d].setServiceName(AGENT_SERVICE_NAMES[d]);
		agents[d].setDescription(AGENT_DESCRIPTIONS[d]);
		agents[d].addCommand(CommandedAgent.PUBLISH);

		do {
			messages = agents[d].getSentMessages();
		} while (messages.isEmpty());

		// Get last message.
		msg = messages.get(messages.size() - 1);

		assertEquals(ACLMessage.REQUEST, msg.getPerformativeInt());
		assertEquals(ALTERNATIVE_TM_NAMES[d], msg.getReceiver().toString());
		assertEquals(AGENT_NAMES[d], msg.getSender().toString());
		assertEquals(MESSAGES_LANGUAGE, msg.getLanguage());
		assertEquals(content, msg.getContent());
	}

	@Test
	public void testUnpublishTracingServiceDefault0() {
		theTestOfUnpublishTracingServiceDefault(0);
	}

	@Test
	public void testUnpublishTracingServiceDefault1() {
		theTestOfUnpublishTracingServiceDefault(1);
	}

	public void theTestOfUnpublishTracingServiceDefault(int d) {
		ACLMessage msg;
		ArrayList<ACLMessage> messages;
		String content;

		// Expected message content.
		content = UNPUBLISH_LABEL + SEPARATION_CHAR + AGENT_SERVICE_NAMES[d];

		// Clear list of sent messages.
		agents[d].clearSentMessages();

		// Agent unpublishes a tracing service.
		agents[d].setDefaultTm();
		agents[d].setServiceName(AGENT_SERVICE_NAMES[d]);
		agents[d].addCommand(CommandedAgent.UNPUBLISH);

		do {
			messages = agents[d].getSentMessages();
		} while (messages.isEmpty());

		// Get last message.
		msg = messages.get(messages.size() - 1);

		assertEquals(ACLMessage.REQUEST, msg.getPerformativeInt());
		assertEquals(DEFAULT_TM_NAME, msg.getReceiver().toString());
		assertEquals(AGENT_NAMES[d], msg.getSender().toString());
		assertEquals(MESSAGES_LANGUAGE, msg.getLanguage());
		assertEquals(content, msg.getContent());
	}

	@Test
	public void testUnpublishTracingServiceAlternative0() {
		theTestOfUnpublishTracingServiceAlternative(0);
	}

	@Test
	public void testUnpublishTracingServiceAlternative1() {
		theTestOfUnpublishTracingServiceAlternative(1);
	}

	public void theTestOfUnpublishTracingServiceAlternative(int d) {
		ACLMessage msg;
		ArrayList<ACLMessage> messages;
		String content;

		// Expected message content.
		content = UNPUBLISH_LABEL + SEPARATION_CHAR + AGENT_SERVICE_NAMES[d];

		// Clear list of sent messages.
		agents[d].clearSentMessages();

		// Agent unpublishes a tracing service.
		agents[d].setTm(new AgentID(ALTERNATIVE_TM_NAMES[d]));
		agents[d].setServiceName(AGENT_SERVICE_NAMES[d]);
		agents[d].addCommand(CommandedAgent.UNPUBLISH);

		do {
			messages = agents[d].getSentMessages();
		} while (messages.isEmpty());

		// Get last message.
		msg = messages.get(messages.size() - 1);

		assertEquals(ACLMessage.REQUEST, msg.getPerformativeInt());
		assertEquals(ALTERNATIVE_TM_NAMES[d], msg.getReceiver().toString());
		assertEquals(AGENT_NAMES[d], msg.getSender().toString());
		assertEquals(MESSAGES_LANGUAGE, msg.getLanguage());
		assertEquals(content, msg.getContent());
	}

	@Test
	public void testRequestTracingServiceDefaultAny0() {
		theTestOfRequestTracingServiceDefaultAny(0);
	}

	@Test
	public void testRequestTracingServiceDefaultAny1() {
		theTestOfRequestTracingServiceDefaultAny(1);
	}

	public void theTestOfRequestTracingServiceDefaultAny(int d) {
		ACLMessage msg;
		ArrayList<ACLMessage> messages;
		String content;

		// Expected message content.
		content = AGENT_SERVICE_NAMES[d] + SEPARATION_CHAR + ANY_LABEL;

		// Clear list of sent messages.
		agents[d].clearSentMessages();

		// Agent requests a tracing service.
		agents[d].setRequestAll(false);
		agents[d].setDefaultTm();
		agents[d].setAnyOriginAgent();
		agents[d].setServiceName(AGENT_SERVICE_NAMES[d]);
		agents[d].addCommand(CommandedAgent.SUBSCRIBE);

		do {
			messages = agents[d].getSentMessages();
		} while (messages.isEmpty());

		// Get last message.
		msg = messages.get(messages.size() - 1);

		assertEquals(ACLMessage.SUBSCRIBE, msg.getPerformativeInt());
		assertEquals(DEFAULT_TM_NAME, msg.getReceiver().toString());
		assertEquals(AGENT_NAMES[d], msg.getSender().toString());
		assertEquals(MESSAGES_LANGUAGE, msg.getLanguage());
		assertEquals(content, msg.getContent());
	}

	@Test
	public void testRequestTracingServiceAlternativeAny0() {
		theTestOfRequestTracingServiceAlternativeAny(0);
	}

	@Test
	public void testRequestTracingServiceAlternativeAny1() {
		theTestOfRequestTracingServiceAlternativeAny(1);
	}

	public void theTestOfRequestTracingServiceAlternativeAny(int d) {
		ACLMessage msg;
		ArrayList<ACLMessage> messages;
		String content;

		// Expected message content.
		content = AGENT_SERVICE_NAMES[d] + SEPARATION_CHAR + ANY_LABEL;

		// Clear list of sent messages.
		agents[d].clearSentMessages();

		// Agent requests a tracing service.
		agents[d].setRequestAll(false);
		agents[d].setTm(new AgentID(ALTERNATIVE_TM_NAMES[d]));
		agents[d].setAnyOriginAgent();
		agents[d].setServiceName(AGENT_SERVICE_NAMES[d]);
		agents[d].addCommand(CommandedAgent.SUBSCRIBE);

		do {
			messages = agents[d].getSentMessages();
		} while (messages.isEmpty());

		// Get last message.
		msg = messages.get(messages.size() - 1);

		assertEquals(ACLMessage.SUBSCRIBE, msg.getPerformativeInt());
		assertEquals(ALTERNATIVE_TM_NAMES[d], msg.getReceiver().toString());
		assertEquals(AGENT_NAMES[d], msg.getSender().toString());
		assertEquals(MESSAGES_LANGUAGE, msg.getLanguage());
		assertEquals(content, msg.getContent());
	}

	@Test
	public void testRequestTracingServiceDefaultWithOrigin0() {
		theTestOfRequestTracingServiceDefaultWithOrigin(0);
	}

	@Test
	public void testRequestTracingServiceDefaultWithOrigin1() {
		theTestOfRequestTracingServiceDefaultWithOrigin(1);
	}

	public void theTestOfRequestTracingServiceDefaultWithOrigin(int d) {
		ACLMessage msg;
		ArrayList<ACLMessage> messages;
		String content;

		// Expected message content.
		content = AGENT_SERVICE_NAMES[d] + SEPARATION_CHAR
				+ ORIGIN_AGENT_NAMES[d];

		// Clear list of sent messages.
		agents[d].clearSentMessages();

		// Agent requests a tracing service.
		agents[d].setRequestAll(false);
		agents[d].setDefaultTm();
		agents[d].setOriginAgent(new AgentID(ORIGIN_AGENT_NAMES[d]));
		agents[d].setServiceName(AGENT_SERVICE_NAMES[d]);
		agents[d].addCommand(CommandedAgent.SUBSCRIBE);

		do {
			messages = agents[d].getSentMessages();
		} while (messages.isEmpty());

		// Get last message.
		msg = messages.get(messages.size() - 1);

		assertEquals(ACLMessage.SUBSCRIBE, msg.getPerformativeInt());
		assertEquals(DEFAULT_TM_NAME, msg.getReceiver().toString());
		assertEquals(AGENT_NAMES[d], msg.getSender().toString());
		assertEquals(MESSAGES_LANGUAGE, msg.getLanguage());
		assertEquals(content, msg.getContent());
	}

	@Test
	public void testRequestTracingServiceAlternativeWithOrigin0() {
		theTestOfRequestTracingServiceAlternativeWithOrigin(0);
	}

	@Test
	public void testRequestTracingServiceAlternativeWithOrigin1() {
		theTestOfRequestTracingServiceAlternativeWithOrigin(1);
	}

	public void theTestOfRequestTracingServiceAlternativeWithOrigin(int d) {
		ACLMessage msg;
		ArrayList<ACLMessage> messages;
		String content;

		// Expected message content.
		content = AGENT_SERVICE_NAMES[d] + SEPARATION_CHAR
				+ ORIGIN_AGENT_NAMES[d];

		// Clear list of sent messages.
		agents[d].clearSentMessages();

		// Agent requests a tracing service.
		agents[d].setRequestAll(false);
		agents[d].setTm(new AgentID(ALTERNATIVE_TM_NAMES[d]));
		agents[d].setOriginAgent(new AgentID(ORIGIN_AGENT_NAMES[d]));
		agents[d].setServiceName(AGENT_SERVICE_NAMES[d]);
		agents[d].addCommand(CommandedAgent.SUBSCRIBE);

		do {
			messages = agents[d].getSentMessages();
		} while (messages.isEmpty());

		// Get last message.
		msg = messages.get(messages.size() - 1);

		assertEquals(ACLMessage.SUBSCRIBE, msg.getPerformativeInt());
		assertEquals(ALTERNATIVE_TM_NAMES[d], msg.getReceiver().toString());
		assertEquals(AGENT_NAMES[d], msg.getSender().toString());
		assertEquals(MESSAGES_LANGUAGE, msg.getLanguage());
		assertEquals(content, msg.getContent());
	}

	@Test
	public void testRequestAllTracingServicesDefault0() {
		theTestOfRequestAllTracingServicesDefault(0);
	}

	@Test
	public void testRequestAllTracingServicesDefault1() {
		theTestOfRequestAllTracingServicesDefault(1);
	}

	public void theTestOfRequestAllTracingServicesDefault(int d) {
		ACLMessage msg;
		ArrayList<ACLMessage> messages;
		String content;

		// Expected message content.
		content = ALL_LABEL;

		// Clear list of sent messages.
		agents[d].clearSentMessages();

		// Agent requests all tracing services.
		agents[d].setRequestAll(true);
		agents[d].setDefaultTm();
		agents[d].addCommand(CommandedAgent.SUBSCRIBE);

		do {
			messages = agents[d].getSentMessages();
		} while (messages.isEmpty());

		// Get last message.
		msg = messages.get(messages.size() - 1);

		assertEquals(ACLMessage.SUBSCRIBE, msg.getPerformativeInt());
		assertEquals(DEFAULT_TM_NAME, msg.getReceiver().toString());
		assertEquals(AGENT_NAMES[d], msg.getSender().toString());
		assertEquals(MESSAGES_LANGUAGE, msg.getLanguage());
		assertEquals(content, msg.getContent());
	}

	@Test
	public void testRequestAllTracingServicesAlternative0() {
		theTestOfRequestAllTracingServicesAlternative(0);
	}

	@Test
	public void testRequestAllTracingServicesAlternative1() {
		theTestOfRequestAllTracingServicesAlternative(1);
	}

	public void theTestOfRequestAllTracingServicesAlternative(int d) {
		ACLMessage msg;
		ArrayList<ACLMessage> messages;
		String content;

		// Expected message content.
		content = ALL_LABEL;

		// Clear list of sent messages.
		agents[d].clearSentMessages();

		// Agent requests all tracing services.
		agents[d].setRequestAll(true);
		agents[d].setTm(new AgentID(ALTERNATIVE_TM_NAMES[d]));
		agents[d].addCommand(CommandedAgent.SUBSCRIBE);

		do {
			messages = agents[d].getSentMessages();
		} while (messages.isEmpty());

		// Get last message.
		msg = messages.get(messages.size() - 1);

		assertEquals(ACLMessage.SUBSCRIBE, msg.getPerformativeInt());
		assertEquals(ALTERNATIVE_TM_NAMES[d], msg.getReceiver().toString());
		assertEquals(AGENT_NAMES[d], msg.getSender().toString());
		assertEquals(MESSAGES_LANGUAGE, msg.getLanguage());
		assertEquals(content, msg.getContent());
	}

	@Test
	public void testCancelTracingServiceSubscriptionDefaultAny0() {
		theTestOfCancelTracingServiceSubscriptionDefaultAny(0);
	}

	@Test
	public void testCancelTracingServiceSubscriptionDefaultAny1() {
		theTestOfCancelTracingServiceSubscriptionDefaultAny(1);
	}

	public void theTestOfCancelTracingServiceSubscriptionDefaultAny(int d) {
		ACLMessage msg;
		ArrayList<ACLMessage> messages;
		String content;

		// Expected message content.
		content = AGENT_SERVICE_NAMES[d] + SEPARATION_CHAR + ANY_LABEL;

		// Clear list of sent messages.
		agents[d].clearSentMessages();

		// Agent requests a tracing service.
		agents[d].setDefaultTm();
		agents[d].setAnyOriginAgent();
		agents[d].setServiceName(AGENT_SERVICE_NAMES[d]);
		agents[d].addCommand(CommandedAgent.UNSUBSCRIBE);

		do {
			messages = agents[d].getSentMessages();
		} while (messages.isEmpty());

		// Get last message.
		msg = messages.get(messages.size() - 1);

		assertEquals(ACLMessage.CANCEL, msg.getPerformativeInt());
		assertEquals(DEFAULT_TM_NAME, msg.getReceiver().toString());
		assertEquals(AGENT_NAMES[d], msg.getSender().toString());
		assertEquals(MESSAGES_LANGUAGE, msg.getLanguage());
		assertEquals(content, msg.getContent());
	}

	@Test
	public void testCancelTracingServiceSubscriptionAlternativeAny0() {
		theTestOfCancelTracingServiceSubscriptionAlternativeAny(0);
	}

	@Test
	public void testCancelTracingServiceSubscriptionAlternativeAny1() {
		theTestOfCancelTracingServiceSubscriptionAlternativeAny(1);
	}

	public void theTestOfCancelTracingServiceSubscriptionAlternativeAny(int d) {
		ACLMessage msg;
		ArrayList<ACLMessage> messages;
		String content;

		// Expected message content.
		content = AGENT_SERVICE_NAMES[d] + SEPARATION_CHAR + ANY_LABEL;

		// Clear list of sent messages.
		agents[d].clearSentMessages();

		// Agent requests a tracing service.
		agents[d].setTm(new AgentID(ALTERNATIVE_TM_NAMES[d]));
		agents[d].setAnyOriginAgent();
		agents[d].setServiceName(AGENT_SERVICE_NAMES[d]);
		agents[d].addCommand(CommandedAgent.UNSUBSCRIBE);

		do {
			messages = agents[d].getSentMessages();
		} while (messages.isEmpty());

		// Get last message.
		msg = messages.get(messages.size() - 1);

		assertEquals(ACLMessage.CANCEL, msg.getPerformativeInt());
		assertEquals(ALTERNATIVE_TM_NAMES[d], msg.getReceiver().toString());
		assertEquals(AGENT_NAMES[d], msg.getSender().toString());
		assertEquals(MESSAGES_LANGUAGE, msg.getLanguage());
		assertEquals(content, msg.getContent());
	}

	@Test
	public void testCancelTracingServiceSubscriptionDefaultWithOrigin0() {
		theTestOfCancelTracingServiceSubscriptionDefaultWithOrigin(0);
	}

	@Test
	public void testCancelTracingServiceSubscriptionDefaultWithOrigin1() {
		theTestOfCancelTracingServiceSubscriptionDefaultWithOrigin(1);
	}

	public void theTestOfCancelTracingServiceSubscriptionDefaultWithOrigin(int d) {
		ACLMessage msg;
		ArrayList<ACLMessage> messages;
		String content;

		// Expected message content.
		content = AGENT_SERVICE_NAMES[d] + SEPARATION_CHAR
				+ ORIGIN_AGENT_NAMES[d];

		// Clear list of sent messages.
		agents[d].clearSentMessages();

		// Agent requests a tracing service.
		agents[d].setDefaultTm();
		agents[d].setOriginAgent(new AgentID(ORIGIN_AGENT_NAMES[d]));
		agents[d].setServiceName(AGENT_SERVICE_NAMES[d]);
		agents[d].addCommand(CommandedAgent.UNSUBSCRIBE);

		do {
			messages = agents[d].getSentMessages();
		} while (messages.isEmpty());

		// Get last message.
		msg = messages.get(messages.size() - 1);

		assertEquals(ACLMessage.CANCEL, msg.getPerformativeInt());
		assertEquals(DEFAULT_TM_NAME, msg.getReceiver().toString());
		assertEquals(AGENT_NAMES[d], msg.getSender().toString());
		assertEquals(MESSAGES_LANGUAGE, msg.getLanguage());
		assertEquals(content, msg.getContent());
	}

	@Test
	public void testCancelTracingServiceSubscriptionAlternativeWithOrigin0() {
		theTestOfCancelTracingServiceSubscriptionAlternativeWithOrigin(0);
	}

	@Test
	public void testCancelTracingServiceSubscriptionAlternativeWithOrigin1() {
		theTestOfCancelTracingServiceSubscriptionAlternativeWithOrigin(1);
	}

	public void theTestOfCancelTracingServiceSubscriptionAlternativeWithOrigin(
			int d) {
		ACLMessage msg;
		ArrayList<ACLMessage> messages;
		String content;

		// Expected message content.
		content = AGENT_SERVICE_NAMES[d] + SEPARATION_CHAR
				+ ORIGIN_AGENT_NAMES[d];

		// Clear list of sent messages.
		agents[d].clearSentMessages();

		// Agent requests a tracing service.
		agents[d].setTm(new AgentID(ALTERNATIVE_TM_NAMES[d]));
		agents[d].setOriginAgent(new AgentID(ORIGIN_AGENT_NAMES[d]));
		agents[d].setServiceName(AGENT_SERVICE_NAMES[d]);
		agents[d].addCommand(CommandedAgent.UNSUBSCRIBE);

		do {
			messages = agents[d].getSentMessages();
		} while (messages.isEmpty());

		// Get last message.
		msg = messages.get(messages.size() - 1);

		assertEquals(ACLMessage.CANCEL, msg.getPerformativeInt());
		assertEquals(ALTERNATIVE_TM_NAMES[d], msg.getReceiver().toString());
		assertEquals(AGENT_NAMES[d], msg.getSender().toString());
		assertEquals(MESSAGES_LANGUAGE, msg.getLanguage());
		assertEquals(content, msg.getContent());
	}

	@Test
	public void testListTracingEntitiesDefault0() {
		theTestOfListTracingEntitiesDefault(0);
	}

	@Test
	public void testListTracingEntitiesDefault1() {
		theTestOfListTracingEntitiesDefault(1);
	}

	public void theTestOfListTracingEntitiesDefault(int d) {
		ACLMessage msg;
		ArrayList<ACLMessage> messages;
		String content;

		// Expected message content.
		content = LIST_LABEL + SEPARATION_CHAR + ENTITIES_LABEL;

		// Clear list of sent messages.
		agents[d].clearSentMessages();

		// Agent list all tracing services.
		agents[d].setDefaultTm();
		agents[d].addCommand(CommandedAgent.LIST_ENTITIES);

		do {
			messages = agents[d].getSentMessages();
		} while (messages.isEmpty());

		// Get last message.
		msg = messages.get(messages.size() - 1);

		assertEquals(ACLMessage.REQUEST, msg.getPerformativeInt());
		assertEquals(DEFAULT_TM_NAME, msg.getReceiver().toString());
		assertEquals(AGENT_NAMES[d], msg.getSender().toString());
		assertEquals(MESSAGES_LANGUAGE, msg.getLanguage());
		assertEquals(content, msg.getContent());
	}

	@Test
	public void testListTracingEntitiesAlternative0() {
		theTestOfListTracingEntitiesAlternative(0);
	}

	@Test
	public void testListTracingEntitiesAlternative1() {
		theTestOfListTracingEntitiesAlternative(1);
	}

	public void theTestOfListTracingEntitiesAlternative(int d) {
		ACLMessage msg;
		ArrayList<ACLMessage> messages;
		String content;

		// Expected message content.
		content = LIST_LABEL + SEPARATION_CHAR + ENTITIES_LABEL;

		// Clear list of sent messages.
		agents[d].clearSentMessages();

		// Agent requests all tracing services.
		agents[d].setRequestAll(true);
		agents[d].setTm(new AgentID(ALTERNATIVE_TM_NAMES[d]));
		agents[d].addCommand(CommandedAgent.LIST_ENTITIES);

		do {
			messages = agents[d].getSentMessages();
		} while (messages.isEmpty());

		// Get last message.
		msg = messages.get(messages.size() - 1);

		assertEquals(ACLMessage.REQUEST, msg.getPerformativeInt());
		assertEquals(ALTERNATIVE_TM_NAMES[d], msg.getReceiver().toString());
		assertEquals(AGENT_NAMES[d], msg.getSender().toString());
		assertEquals(MESSAGES_LANGUAGE, msg.getLanguage());
		assertEquals(content, msg.getContent());
	}

	@Test
	public void testListTracingServicesDefault0() {
		theTestOfListTracingServicesDefault(0);
	}

	@Test
	public void testListTracingServicesDefault1() {
		theTestOfListTracingServicesDefault(1);
	}

	public void theTestOfListTracingServicesDefault(int d) {
		ACLMessage msg;
		ArrayList<ACLMessage> messages;
		String content;

		// Expected message content.
		content = LIST_LABEL + SEPARATION_CHAR + SERVICES_LABEL;

		// Clear list of sent messages.
		agents[d].clearSentMessages();

		// Agent list all tracing services.
		agents[d].setDefaultTm();
		agents[d].addCommand(CommandedAgent.LIST_SERVICES);

		do {
			messages = agents[d].getSentMessages();
		} while (messages.isEmpty());

		// Get last message.
		msg = messages.get(messages.size() - 1);

		assertEquals(ACLMessage.REQUEST, msg.getPerformativeInt());
		assertEquals(DEFAULT_TM_NAME, msg.getReceiver().toString());
		assertEquals(AGENT_NAMES[d], msg.getSender().toString());
		assertEquals(MESSAGES_LANGUAGE, msg.getLanguage());
		assertEquals(content, msg.getContent());
	}

	@Test
	public void testListTracingServicesAlternative0() {
		theTestOfListTracingServicesAlternative(0);
	}

	@Test
	public void testListTracingServicesAlternative1() {
		theTestOfListTracingServicesAlternative(1);
	}

	public void theTestOfListTracingServicesAlternative(int d) {
		ACLMessage msg;
		ArrayList<ACLMessage> messages;
		String content;

		// Expected message content.
		content = LIST_LABEL + SEPARATION_CHAR + SERVICES_LABEL;

		// Clear list of sent messages.
		agents[d].clearSentMessages();

		// Agent requests all tracing services.
		agents[d].setRequestAll(true);
		agents[d].setTm(new AgentID(ALTERNATIVE_TM_NAMES[d]));
		agents[d].addCommand(CommandedAgent.LIST_SERVICES);

		do {
			messages = agents[d].getSentMessages();
		} while (messages.isEmpty());

		// Get last message.
		msg = messages.get(messages.size() - 1);

		assertEquals(ACLMessage.REQUEST, msg.getPerformativeInt());
		assertEquals(ALTERNATIVE_TM_NAMES[d], msg.getReceiver().toString());
		assertEquals(AGENT_NAMES[d], msg.getSender().toString());
		assertEquals(MESSAGES_LANGUAGE, msg.getLanguage());
		assertEquals(content, msg.getContent());
	}
}
