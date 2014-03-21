package TestTrace;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.omg.CORBA.COMM_FAILURE;

import com.sun.mail.imap.ACL;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.core.TraceEvent;
import es.upv.dsic.gti_ia.core.TracingService;
import es.upv.dsic.gti_ia.organization.Configuration;
import es.upv.dsic.gti_ia.trace.TraceError;
import es.upv.dsic.gti_ia.trace.TraceManager;
import es.upv.dsic.gti_ia.trace.TraceMask;
import es.upv.dsic.gti_ia.trace.TracingEntity;
import es.upv.dsic.gti_ia.trace.TracingEntityList;
import es.upv.dsic.gti_ia.trace.TracingServiceList;

/** 
 * @author Jose Alemany Bordera  -  jalemany1@dsic.upv.es
 * 
 */

public class TestTraceManager {

	/* Constants */
	// Dependent of other classes (TraceManager).
	private static final String TRACING_ENTITIES_VARIABLE_NAME = "TracingEntities";
	private static final String MONITORIZABLE_VARIABLE_NAME = "monitorizable";
	private static final Configuration conf = Configuration.getConfiguration();
	
	// Independent, proper of this class.
	private static final int[] VALID_TYPES = {TracingEntity.AGENT, TracingEntity.ARTIFACT, TracingEntity.AGGREGATION};
	private static final String[] COMMANDED_TM_NOT_MONITORIZABLES = {"qpid://TM@localhost:8080", "qpid://\\”ł¶ħß¢ħł½»·¶Ħ[ßðđħł@localhost:8080"};
	private static final String[] COMMANDED_TM_MONITORIZABLES = {"qpid://TM_Alt@localhost:8080", "qpid://\\”ł¶ħß¢ħł½»·¶Ħ[ßðđħł_ALt@localhost:8080"};
	private static final String[] COMMANDED_AGENTS = {"Agent1","Agent2","Agent3"};
	private static final String[] ENCODED_MASCKS = {"0000000100","1111111100","1111100100","1001100100"};
	
	/* Attributes */
	private static CommandedTraceManager[] commTraceManagers = {null, null, null, null};
	private static CommandedAgent[] commAgents = {null, null, null};
	static Process qpid_broker;
	
	
	/* Set up class and tear down class */
	@Before
	public void setUp() throws Exception {
		
		qpid_broker = qpidManager.UnixQpidManager.startQpid(Runtime.getRuntime(), qpid_broker);
		
		AgentsConnection.connect();		// Connecting to Qpid Broker.
		
		// TraceManager creation.
		for(int i = 0; i < commTraceManagers.length; i+=2) {
			commTraceManagers[i] = new CommandedTraceManager(new AgentID(COMMANDED_TM_NOT_MONITORIZABLES[i/2]), false);
			commTraceManagers[i].start();
			
			commTraceManagers[i+1] = new CommandedTraceManager(new AgentID(COMMANDED_TM_MONITORIZABLES[i/2]), true);
			commTraceManagers[i+1].start();
		}
		
		// Agents creation.
		for(int i = 0; i < commAgents.length; i++)
			commAgents[i] = new CommandedAgent(new AgentID(COMMANDED_AGENTS[i]));
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			fail(e.getMessage());
		}
	}

	@After
	public void tearDown() throws Exception {
		
		for(int i = 0; i < commTraceManagers.length; ++i)
			commTraceManagers[i].addCommand(CommandedTraceManager.END);
		
		for(int i = 0; i < commAgents.length; i++)
			commAgents[i].addCommand(CommandedAgent.END);
		
		AgentsConnection.disconnect();
		qpidManager.UnixQpidManager.stopQpid(qpid_broker);
	}

	/* Test methods */
	
	/* CONSTRUCTOR ---------------------------------- */
	//@Test
	public void testTraceManagerMonitorizable0() throws Exception { theTestOfTraceManagerMonitorizable(1); }
	//@Test
	public void testTraceManagerMonitorizable1() throws Exception { theTestOfTraceManagerMonitorizable(3); }
	public void theTestOfTraceManagerMonitorizable (int d) throws Exception {
		
		Field fields[] = CommandedTraceManager.class.getSuperclass().getDeclaredFields();
	    for (int i = 0; i < fields.length; i++){ 
	        fields[i].setAccessible(true); 
	        
	        if(fields[i].getName().equals(TRACING_ENTITIES_VARIABLE_NAME)) {
	        	TracingEntityList tEntities = (TracingEntityList) fields[i].get(commTraceManagers[d]);
		       	assertEquals("The list of tracing entities must contain one element.", 1, tEntities.size());
		    }
			else if(fields[i].getName().equals(MONITORIZABLE_VARIABLE_NAME)) {
					assertTrue("Monitorizable flag must be set to true.", fields[i].getBoolean(commTraceManagers[d]));
			} 
	    }
	}
	
	//@Test
	public void testTraceManagerNotMonitorizable0() throws Exception { theTestOfTraceManagerNotMonitorizable(0); }
	//@Test
	public void testTraceManagerNotMonitorizable1() throws Exception { theTestOfTraceManagerNotMonitorizable(2); }
	public void theTestOfTraceManagerNotMonitorizable (int d) throws Exception {
		
		Field fields[] = CommandedTraceManager.class.getSuperclass().getDeclaredFields();
	    for (int i = 0; i < fields.length; i++){ 
	        fields[i].setAccessible(true); 
	        
	        if(fields[i].getName().equals(TRACING_ENTITIES_VARIABLE_NAME)) {
	        	TracingEntityList tEntities = (TracingEntityList) fields[i].get(commTraceManagers[d]);
		       	assertEquals("The list of tracing entities must contain one element.", 1, tEntities.size());
		    }
			else if(fields[i].getName().equals(MONITORIZABLE_VARIABLE_NAME)) {
					assertFalse("Monitorizable flag must be set to false.", fields[i].getBoolean(commTraceManagers[d]));
			} 
	    }
	}
	
	/* GET_TRACE_MASK ---------------------------------- */
	@Test
	public void testGetTraceMask() throws Exception { 
		
		for(int i = 0; i < commTraceManagers.length; i++)
			assertEquals("The TraceManager mask "+(i+1)+" should match with the specified in the configuration file.", conf.getTraceMask(), commTraceManagers[i].getTraceMask().toString());
	}
	
	/* SET_TRACE_MASK ---------------------------------- */
	@Test(timeout=20000)
	public void testSetTraceMask() throws Exception {
		
		for(int i = 0; i < commTraceManagers.length; i++) {
			commTraceManagers[i].setTraceMask(new TraceMask(ENCODED_MASCKS[i]));
			
			try{
				Thread.sleep(1000);
			}catch(Exception e){}
			
			for(int j = 0; j < commTraceManagers.length; j++) {
				assertEquals("The TraceManager mask "+(j+1)+" should match with the current mask.", ENCODED_MASCKS[i], commTraceManagers[j].getTraceMask().toString());
			}
		}
	}
	
	/* SEND_SYSTEM_TRACE_EVENT ---------------------------------- */
	@Test(timeout=50000)
	public void testSendSystemTraceEvent0() throws Exception {
		// Sent to one Agent.
		callSendSystemTraceEvent(0, 0, 1);
		checkSendSystemTraceEvent("DD_Test_TS: Test", 1);
	}
		
	@Test(timeout=50000)
	public void testSendSystemTraceEvent1() throws Exception {
		// Sent to all.
		callSendSystemTraceEvent(0, 0, -1);
		checkSendSystemTraceEvent("DD_Test_TS: Test", 0, 1, 2);
	}
	
	//@Test(timeout=50000)	(INCOMPLETE)
	public void testSendSystemTraceEvent2() throws Exception {
		//To Artifacts.
		callSendSystemTraceEvent(1, 0, 1);
		//checkSendSystemTraceEvent("DD_Test_TS: Test", 0, 1, 2);
	}
	
	//@Test(timeout=50000)	(INCOMPLETE)
	public void testSendSystemTraceEvent3() throws Exception {
		//To Aggregations.
		callSendSystemTraceEvent(2, 0, -1);
		//checkSendSystemTraceEvent("DD_Test_TS: Test", 0, 1, 2);
	}
		
	private void callSendSystemTraceEvent(int type, int origA1, int destA2) throws Exception {
		
		Class[] parameterTypes = new Class[2];
	    parameterTypes[0] = TraceEvent.class;
	    parameterTypes[1] = TracingEntity.class;
	    
	    Method m = TraceManager.class.getDeclaredMethod("sendSystemTraceEvent", parameterTypes);
		m.setAccessible(true);
				
		Object[] parameters = new Object[2];
		parameters[0] = new TraceEvent("DD_Test_TS", commAgents[origA1].getAid(), "Test");
		parameters[1] = (destA2 == -1) ? null : new TracingEntity(VALID_TYPES[type], commAgents[destA2].getAid());
		
		try {
			m.invoke(commTraceManagers[0], parameters);
		} catch(InvocationTargetException e) {
			throw (Exception) e.getCause();
	    }
	}
	
	private void checkSendSystemTraceEvent(String res, int... d) {
		for(int n : d) {
			while(commAgents[n].getTraceEvents().size() < 1) {
				try {
					Thread.sleep(1 * 50);
				} catch (InterruptedException e) {
					fail(e.getMessage());
				}
			}
			ArrayList<TraceEvent> tE = commAgents[n].getTraceEvents();
			ArrayList<String> controlTE = new ArrayList<String>();
			for(TraceEvent t : tE)
				controlTE.add(t.getTracingService() + ": " + t.getContent());
			assertTrue(controlTE.contains(res));
		}
	}
	
	/* ON_MESSAGE ---------------------------------- */
	@Test(timeout=20000)
	public void testOnMessageRequestPublish0() throws Exception {
		
		//Return REFUSE because in TraceMask CUSTOM is false
		commTraceManagers[1].setTraceMask(new TraceMask("1011110100"));
		
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setContent("publish#10#DD_Test_TS#Test");
		msg.setReceiver(commAgents[0].getAid());
		msg.setSender(commAgents[1].getAid());
		
		commTraceManagers[1].onMessage(msg);
		
		checkOnMessage("REFUSE: publish#10#DD_Test_TS" + TraceError.SERVICE_NOT_ALLOWED, 1);
		
	}
	
	@Test(timeout=20000)
	public void testOnMessageRequestPublish1() throws Exception {
		
		//Return REFUSE because destination Agent is not in the TracingEntity system.
		Field f = CommandedTraceManager.class.getSuperclass().getDeclaredField("TracingEntities");
		f.setAccessible(true);
		while(!((TracingEntityList) f.get(commTraceManagers[1])).remove(((TracingEntityList) f.get(commTraceManagers[1])).getTEByAid(commAgents[2].getAid()))){
			try {
				Thread.sleep(1 * 50);
			} catch (InterruptedException e) {
				fail(e.getMessage());
			}
		}
		
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setContent("publish#10#DD_Test_TS#Test");
		msg.setReceiver(commAgents[1].getAid());
		msg.setSender(commAgents[2].getAid());
		
		commTraceManagers[1].onMessage(msg);
		
		checkOnMessage("REFUSE: publish#10#DD_Test_TS" + TraceError.ENTITY_NOT_FOUND, 2);
		
	}
	
	@Test(timeout=20000)
	public void testOnMessageRequestPublish2() throws Exception {
		
		//Return AGREE
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setContent("publish#10#DD_Test_TS#Test");
		msg.setReceiver(commAgents[2].getAid());
		msg.setSender(commAgents[0].getAid());
		
		commTraceManagers[1].onMessage(msg);
		
		checkOnMessage("AGREE: publish#DD_Test_TS", 0);
		
		//Return REFUSE by Duplicate Service
		
		commTraceManagers[1].onMessage(msg);
		
		checkOnMessage("REFUSE: publish#10#DD_Test_TS" + TraceError.SERVICE_DUPLICATE, 0);
		
	}
	
	@Test(timeout=20000)
	public void testOnMessageRequestUnpublish0() throws Exception {
		
		//Return REFUSE because in TraceMask CUSTOM is false
		commTraceManagers[1].setTraceMask(new TraceMask("1011110100"));
				
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setContent("unpublish#DD_Test_TS");
		msg.setReceiver(commAgents[0].getAid());
		msg.setSender(commAgents[1].getAid());
				
		commTraceManagers[1].onMessage(msg);
				
		checkOnMessage("REFUSE: unpublish#10#DD_Test_TS" + TraceError.SERVICE_NOT_ALLOWED, 1);
		
	}
	
	@Test(timeout=20000)
	public void testOnMessageRequestUnpublish1() throws Exception {
		
		//Return REFUSE because TracingService does not exist.
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setContent("unpublish#DD_Test_TS");
		msg.setReceiver(commAgents[1].getAid());
		msg.setSender(commAgents[2].getAid());
		
		commTraceManagers[1].onMessage(msg);
		
		checkOnMessage("REFUSE: unpublish#10#DD_Test_TS" + TraceError.SERVICE_NOT_FOUND, 2);
		
	}
	
	@Test(timeout=20000)
	public void testOnMessageRequestUnpublish2() throws Exception {
		
		//Return REFUSE because TracingService cannot be unpublished.
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setContent("unpublish#NEW_AGENT");
		msg.setReceiver(commAgents[2].getAid());
		msg.setSender(commAgents[0].getAid());
		
		commTraceManagers[2].onMessage(msg);
		
		checkOnMessage("REFUSE: unpublish#9#NEW_AGENT" + TraceError.BAD_SERVICE, 0);
		
	}
	
	@Test(timeout=20000)
	public void testOnMessageRequestUnpublish3() throws Exception {
		
		//Return AGREE
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setContent("publish#10#DD_Test_TS#Test");
		msg.setReceiver(commAgents[2].getAid());
		msg.setSender(commAgents[0].getAid());
				
		commTraceManagers[1].onMessage(msg);
				
		checkOnMessage("AGREE: publish#DD_Test_TS", 0);
		
		//Return REFUSE because destination Agent is not in the TracingEntity system.
		msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setContent("unpublish#DD_Test_TS");
		msg.setReceiver(commAgents[2].getAid());
		msg.setSender(commAgents[1].getAid());
		
		commTraceManagers[1].onMessage(msg);
		
		checkOnMessage("REFUSE: unpublish#10#DD_Test_TS" + TraceError.BAD_SERVICE, 1);
		
	}
	
	@Test(timeout=20000)
	public void testOnMessageRequestUnpublish4() throws Exception {
		
		//Return REFUSE because Tracing service not published by the tracing entity.
		Field f = CommandedTraceManager.class.getSuperclass().getDeclaredField("TSProviderEntities");
		f.setAccessible(true);
		((ArrayList<TracingEntity>) f.get(commTraceManagers[1])).add(new TracingEntity(VALID_TYPES[0], commAgents[1].getAid()));
		
		TracingService tS = new TracingService("DD_Test_TS", "Test");
		tS.addServiceProvider(new TracingEntity(VALID_TYPES[0], commAgents[1].getAid()));
		tS.addServiceProvider(new TracingEntity(VALID_TYPES[0], commAgents[0].getAid()));
		
		f = CommandedTraceManager.class.getSuperclass().getDeclaredField("TracingServices");
		f.setAccessible(true);
		((ArrayList<TracingService>) f.get(commTraceManagers[1])).add(tS);
		
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setContent("unpublish#DD_Test_TS");
		msg.setReceiver(commAgents[2].getAid());
		msg.setSender(commAgents[1].getAid());
		
		commTraceManagers[1].onMessage(msg);
		
		checkOnMessage("REFUSE: unpublish#10#DD_Test_TS" + TraceError.BAD_SERVICE, 1);
		
	}
	
	@Test(timeout=100000)
	public void testOnMessageRequestUnpublish5() throws Exception {
		
		//Return AGREE
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setContent("publish#10#DD_Test_TS#Test");
		msg.setReceiver(commAgents[2].getAid());
		msg.setSender(commAgents[2].getAid());
					
		commTraceManagers[3].onMessage(msg);
					
		checkOnMessage("AGREE: publish#DD_Test_TS", 2);
		
		for(int i = 0; i < commAgents.length; i++) {
			msg = new ACLMessage(ACLMessage.SUBSCRIBE);
			msg.setContent("all");
			msg.setReceiver(commAgents[2].getAid());
			msg.setSender(commAgents[i].getAid());
						
			commTraceManagers[3].onMessage(msg);
						
			checkOnMessage("AGREE: subscribe#3#all", i);
		}
				
		msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setContent("unpublish#DD_Test_TS");
		msg.setReceiver(commAgents[2].getAid());
		msg.setSender(commAgents[2].getAid());
				
		commTraceManagers[3].onMessage(msg);
				
		checkOnMessage("AGREE: unpublish#DD_Test_TS", 2);
		
	}
	
	//@Test(timeout=100000) 807-854
	public void testOnMessageRequestUnpublish6() throws Exception {
		
		//Return AGREE
		for(int i = 0; i < commAgents.length; i++) {
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			msg.setContent("publish#10#DD_Test_TS#Test");
			msg.setReceiver(commAgents[2].getAid());
			msg.setSender(commAgents[2].getAid());
					
			commTraceManagers[3].onMessage(msg);
					
			checkOnMessage("AGREE: publish#DD_Test_TS", 2);
		}
		
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setContent("unpublish#DD_Test_TS");
		msg.setReceiver(commAgents[2].getAid());
		msg.setSender(commAgents[2].getAid());
				
		commTraceManagers[3].onMessage(msg);
				
		checkOnMessage("AGREE: unpublish#DD_Test_TS", 2);
		
	}
	
	@Test(timeout=20000)
	public void testOnMessageRequestList0() throws Exception {
		
		//Return REFUSE because in TraceMask LIST_ENTITIES is false
		commTraceManagers[1].setTraceMask(new TraceMask("1111010100"));
				
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setContent("list#entities");
		msg.setReceiver(commAgents[2].getAid());
		msg.setSender(commAgents[0].getAid());
		
		commTraceManagers[1].onMessage(msg);
		
		checkOnMessage("REFUSE: list#entities#" + TraceError.SERVICE_NOT_ALLOWED, 0);
		
	}
	
	@Test(timeout=20000)
	public void testOnMessageRequestList1() throws Exception {
		
		//Return AGREE LIST_ENTITIES
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setContent("list#entities");
		msg.setReceiver(commAgents[2].getAid());
		msg.setSender(commAgents[0].getAid());
		
		commTraceManagers[1].onMessage(msg);
		
		while(commAgents[0].getReceivedMessages().size() < 1) {
			try {
				Thread.sleep(1 * 50);
			} catch (InterruptedException e) {
				fail(e.getMessage());
			}
		}
		ArrayList<ACLMessage> tM = commAgents[0].getReceivedMessages();
		for(ACLMessage m : tM) {
			System.out.println(m.getPerformative() + ": " + m.getContent());
			assertEquals(m.getPerformative(),"AGREE");
			assertTrue(m.getContent().contains("list#entities"));
			for(int i = 0; i < commAgents.length; i++) assertTrue(m.getContent().contains(commAgents[i].getAid().toString()));
			for(int j = 0; j < commTraceManagers.length; j++) assertTrue(m.getContent().contains(commTraceManagers[j].getAid().toString()));
		}
	}
	
	@Test(timeout=20000)
	public void testOnMessageRequestList2() throws Exception {
		
		//Return REFUSE because in TraceMask LIST_SERVICES is false
		commTraceManagers[1].setTraceMask(new TraceMask("1111100100"));
				
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setContent("list#services");
		msg.setReceiver(commAgents[0].getAid());
		msg.setSender(commAgents[1].getAid());
		
		commTraceManagers[1].onMessage(msg);
		
		checkOnMessage("REFUSE: list#services#" + TraceError.SERVICE_NOT_ALLOWED, 1);
		
	}
	
	@Test(timeout=20000)
	public void testOnMessageRequestList3() throws Exception {
		
		//Return AGREE LIST_SERVICES
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setContent("list#services");
		msg.setReceiver(commAgents[2].getAid());
		msg.setSender(commAgents[0].getAid());
		
		commTraceManagers[3].onMessage(msg);
		
		while(commAgents[0].getReceivedMessages().size() < 1) {
			try {
				Thread.sleep(1 * 50);
			} catch (InterruptedException e) {
				fail(e.getMessage());
			}
		}
		ArrayList<ACLMessage> tM = commAgents[0].getReceivedMessages();
		for(ACLMessage m : tM) {
			assertEquals(m.getPerformative(),"AGREE");
			assertTrue(m.getContent().contains("list#services"));
			for(int i = 0; i < TracingService.MAX_DI_TS; i++) assertTrue(m.getContent().contains(TracingService.DI_TracingServices[i].getName() + "#" + TracingService.DI_TracingServices[i].getDescription()));
		}
	}
	
	@Test(timeout=20000)
	public void testOnMessageRequestList4() throws Exception {
		
		//Return REFUSE LIST_SERVICE because tracing service does not exist.
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setContent("list#service#DD_Test_TS");
		msg.setReceiver(commAgents[0].getAid());
		msg.setSender(commAgents[2].getAid());
		
		commTraceManagers[2].onMessage(msg);
		
		checkOnMessage("REFUSE: list#service#10#DD_Test_TS" + TraceError.SERVICE_NOT_ALLOWED, 2);
	}
	
	@Test(timeout=20000)
	public void testOnMessageRequestList5() throws Exception {
		
		//Return AGREE LIST_SERVICE NEW_AGENT.
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setContent("list#service#NEW_AGENT");
		msg.setReceiver(commAgents[2].getAid());
		msg.setSender(commAgents[0].getAid());
		
		commTraceManagers[3].onMessage(msg);
		
		checkOnMessage("AGREE: list#service#9#NEW_AGENTA new agent was registered in the system.", 0);
	}
	
	@Test(timeout=20000)
	public void testOnMessageRequestListUnknown0() throws Exception {
		
		//Return UNKNOWN listing.
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setContent("list#Unknown");
		msg.setReceiver(commAgents[1].getAid());
		msg.setSender(commAgents[1].getAid());
		
		commTraceManagers[3].onMessage(msg);
		
		checkOnMessage("UNKNOWN: list#Unknown", 1);
	}
	
	@Test(timeout=20000)
	public void testOnMessageRequestUpdateMask0() throws Exception {
		
		//Return AGREE
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setContent("UpdateMask#any");
		msg.setReceiver(commAgents[0].getAid());
		msg.setSender(commAgents[2].getAid());
		
		commTraceManagers[0].onMessage(msg);
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			fail(e.getMessage());
		}
		
		assertEquals(commAgents[2].getTraceMask().toString(), commTraceManagers[1].getTraceMask().toString());
		
	}
	
	@Test(timeout=20000)
	public void testOnMessageRequestUnknown0() throws Exception {
		
		//Return Unknown Request
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setContent("UnknownRequest#any");
		msg.setReceiver(commAgents[0].getAid());
		msg.setSender(commAgents[2].getAid());
		
		commTraceManagers[1].onMessage(msg);
		
		checkOnMessage("UNKNOWN: UnknownRequest#any", 2);
		
	}
	
	@Test(timeout=20000)
	public void testOnMessageSubscribeAll0() throws Exception {
		
		//Return REFUSE because destination Agent is not in the TracingEntity system.
		Field f = CommandedTraceManager.class.getSuperclass().getDeclaredField("TracingEntities");
		f.setAccessible(true);
		while(!((TracingEntityList) f.get(commTraceManagers[1])).remove(((TracingEntityList) f.get(commTraceManagers[1])).getTEByAid(commAgents[2].getAid()))){
			try {
				Thread.sleep(1 * 50);
			} catch (InterruptedException e) {
				fail(e.getMessage());
			}
		}
				
		ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);
		msg.setContent("all");
		msg.setReceiver(commAgents[0].getAid());
		msg.setSender(commAgents[2].getAid());
		
		commTraceManagers[1].onMessage(msg);
		
		checkOnMessage("REFUSE: subscribe#3#all" + TraceError.ENTITY_NOT_FOUND, 2);
		
	}
	
	@Test(timeout=20000)
	public void testOnMessageSubscribeAll1() throws Exception {
		
		//Return AGREE		
		ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);
		msg.setContent("all");
		msg.setReceiver(commAgents[0].getAid());
		msg.setSender(commAgents[2].getAid());
		
		commTraceManagers[1].onMessage(msg);
		
		checkOnMessage("AGREE: subscribe#3#all", 2);
		
	}
	
	@Test(timeout=20000)
	public void testOnMessageSubscribeAll2() throws Exception {
		
		//Return REFUSE because TM is not monitorizable.		
		ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);
		msg.setContent("all");
		msg.setReceiver(commAgents[0].getAid());
		msg.setSender(commAgents[2].getAid());
		
		commTraceManagers[2].onMessage(msg);
		
		checkOnMessage("REFUSE: subscribe#3#all" + TraceError.AUTHORIZATION_ERROR, 2);
		
	}
	
	@Test(timeout=20000)
	public void testOnMessageSubscribe0() throws Exception {
		
		//Return REFUSE because destination Agent is not in the TracingEntity system.
		Field f = CommandedTraceManager.class.getSuperclass().getDeclaredField("TracingEntities");
		f.setAccessible(true);
		while(!((TracingEntityList) f.get(commTraceManagers[2])).remove(((TracingEntityList) f.get(commTraceManagers[2])).getTEByAid(commAgents[2].getAid()))){
			try {
				Thread.sleep(1 * 50);
			} catch (InterruptedException e) {
				fail(e.getMessage());
			}
		}
		
		ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);
		msg.setContent("NEW_AGENT#" + commAgents[1].getAid().toString());
		msg.setReceiver(commAgents[0].getAid());
		msg.setSender(commAgents[2].getAid());
		
		commTraceManagers[2].onMessage(msg);
		
		checkOnMessage("REFUSE: subscribe#9#NEW_AGENT" + commAgents[1].getAid().toString().length() + "#" +
				commAgents[1].getAid().toString() + TraceError.ENTITY_NOT_FOUND, 2);
		
	}
	
	@Test(timeout=20000)
	public void testOnMessageSubscribe1() throws Exception {
		
		//Return REFUSE because Service does not exist.
		ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);
		msg.setContent("UnknownService#Agent");
		msg.setReceiver(commAgents[2].getAid());
		msg.setSender(commAgents[1].getAid());
		
		commTraceManagers[2].onMessage(msg);
		
		checkOnMessage("REFUSE: subscribe#14#UnknownService5#Agent" + TraceError.SERVICE_NOT_FOUND, 1);
		
	}
	
	@Test(timeout=20000)
	public void testOnMessageSubscribe2() throws Exception {
		
		//Return REFUSE because Service does not requestable.
		ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);
		msg.setContent("TRACE_ERROR#Agent");
		msg.setReceiver(commAgents[2].getAid());
		msg.setSender(commAgents[1].getAid());
		
		commTraceManagers[2].onMessage(msg);
		
		checkOnMessage("REFUSE: subscribe#11#TRACE_ERROR5#Agent" + TraceError.BAD_SERVICE, 1);
		
	}
	
	@Test(timeout=20000)
	public void testOnMessageSubscribe3() throws Exception {
		
		//Return REFUSE because Tracing service not published by the origin tracing entity.
		ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);
		msg.setContent("NEW_AGENT#Agent");
		msg.setReceiver(commAgents[2].getAid());
		msg.setSender(commAgents[1].getAid());
		
		commTraceManagers[2].onMessage(msg);
		
		checkOnMessage("REFUSE: subscribe#9#NEW_AGENT5#Agent" + TraceError.SERVICE_NOT_FOUND, 1);
		
	}
	
	@Test(timeout=20000)
	public void testOnMessageSubscribe4() throws Exception {
		
		//Return AGREE
		ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);
		msg.setContent("MESSAGE_SENT_DETAIL#" + commTraceManagers[0].getAid().toString());
		msg.setReceiver(commAgents[0].getAid());
		msg.setSender(commAgents[2].getAid());
		
		commTraceManagers[2].onMessage(msg);
		
		checkOnMessage("AGREE: subscribe#19#MESSAGE_SENT_DETAIL#" + commTraceManagers[0].getAid().toString(), 2);
		
		//Return REFUSE because the subscription already exists
		commTraceManagers[2].onMessage(msg);
				
		checkOnMessage("REFUSE: subscribe#19#MESSAGE_SENT_DETAIL" + commTraceManagers[0].getAid().toString().length() +
				"#" + commTraceManagers[0].getAid().toString() + TraceError.SUBSCRIPTION_DUPLICATE, 2);
		
	}
	
	@Test(timeout=20000)
	public void testOnMessageSubscribe5() throws Exception {
		
		//Return REFUSE because Tracing service not allowed by the mask.
		commTraceManagers[2].setTraceMask(new TraceMask("1110110100"));
		
		ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);
		msg.setContent("MESSAGE_SENT_DETAIL#" + commTraceManagers[2].getAid().toString());
		msg.setReceiver(commAgents[2].getAid());
		msg.setSender(commAgents[1].getAid());
		
		commTraceManagers[2].onMessage(msg);
		
		checkOnMessage("REFUSE: subscribe#19#MESSAGE_SENT_DETAIL" + commTraceManagers[2].getAid().toString().length() +
				"#" + commTraceManagers[2].getAid().toString() + TraceError.SERVICE_NOT_ALLOWED, 1);
		
	}
	
	@Test(timeout=20000)
	public void testOnMessageCancel0() throws Exception {
		
		//Return REFUSE because Service does not exist.
		ACLMessage msg = new ACLMessage(ACLMessage.CANCEL);
		msg.setContent("UnknownService#Agent");
		msg.setReceiver(commAgents[2].getAid());
		msg.setSender(commAgents[1].getAid());
		
		commTraceManagers[2].onMessage(msg);
		
		checkOnMessage("REFUSE: unsubscribe#14#UnknownService5#Agent" + TraceError.SERVICE_NOT_FOUND, 1);
		
	}
	
	@Test(timeout=20000)
	public void testOnMessageCancel1() throws Exception {
		
		//Return REFUSE because Subscription does not exist.
		ACLMessage msg = new ACLMessage(ACLMessage.CANCEL);
		msg.setContent("NEW_AGENT#Agent");
		msg.setReceiver(commAgents[1].getAid());
		msg.setSender(commAgents[2].getAid());
		
		commTraceManagers[2].onMessage(msg);
		
		checkOnMessage("REFUSE: unsubscribe#9#NEW_AGENT5#Agent" + TraceError.SUBSCRIPTION_NOT_FOUND, 2);
		
	}
	
	@Test(timeout=20000)
	public void testOnMessageCancel2() throws Exception {
		
		//Return AGREE
		ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);
		msg.setContent("MESSAGE_SENT_DETAIL#" + commTraceManagers[0].getAid().toString());
		msg.setReceiver(commAgents[1].getAid());
		msg.setSender(commAgents[2].getAid());
				
		commTraceManagers[0].onMessage(msg);
				
		checkOnMessage("AGREE: subscribe#19#MESSAGE_SENT_DETAIL#" + commTraceManagers[0].getAid().toString(), 2);
				
		//Return REFUSE because Tracing service not allowed by the mask.
		commTraceManagers[0].setTraceMask(new TraceMask("1110110100"));
				
		msg = new ACLMessage(ACLMessage.CANCEL);
		msg.setContent("MESSAGE_SENT_DETAIL#" + commTraceManagers[0].getAid().toString());
		msg.setReceiver(commAgents[1].getAid());
		msg.setSender(commAgents[2].getAid());
		
		commTraceManagers[0].onMessage(msg);
		
		checkOnMessage("REFUSE: unsubscribe#19#MESSAGE_SENT_DETAIL" + commTraceManagers[0].getAid().toString().length() +
				"#" + commTraceManagers[0].getAid().toString() + TraceError.SERVICE_NOT_ALLOWED, 2);
		
	}
	
	@Test(timeout=20000)
	public void testOnMessageUnknown0() throws Exception {
		
		//Return REFUSE because Subscription does not exist.
		ACLMessage msg = new ACLMessage(ACLMessage.UNKNOWN);
		msg.setContent("NEW_AGENT#Agent");
		msg.setReceiver(commAgents[0].getAid());
		msg.setSender(commAgents[1].getAid());
		
		commTraceManagers[3].onMessage(msg);
		
		checkOnMessage("UNKNOWN: NEW_AGENT#Agent", 1);
		
	}
	
	
	
	private void checkOnMessage(String res, int... d){
		for(int n : d) {
			while(commAgents[n].getReceivedMessages().size() < 1) {
				try {
					Thread.sleep(1 * 50);
				} catch (InterruptedException e) {
					fail(e.getMessage());
				}
			}
			ArrayList<ACLMessage> tM = commAgents[n].getReceivedMessages();
			ArrayList<String> controlTM = new ArrayList<String>();
			for(ACLMessage m : tM){
				System.out.println(m.getPerformative() + ": " + m.getContent());
				controlTM.add(m.getPerformative() + ": " + m.getContent());
			}
			assertTrue(controlTM.contains(res));
			commAgents[n].clearReceivedMessages();
		}
	}
	
	/* ON_TRACE_EVENT ---------------------------------- */
	
	@Test
	public void testOnTraceEventNew_Agent() throws Exception {
		
		// Check that all TraceManagers in the system receive NEW_AGENT Trace Event
		for(int i = 0; i < commTraceManagers.length; i++) {
			ArrayList<TraceEvent> tEs = commTraceManagers[i].getTraceEvents();
			ArrayList<String> contentTraceEvents = new ArrayList<String>();
			for(int j = 0; j < tEs.size(); j++) {
				TraceEvent tE = tEs.get(j);
				contentTraceEvents.add(tE.getTracingService() + ": " + tE.getContent());
			}
			for(int j = 0; j < commAgents.length; j++) {
				assertTrue("The TraceManager "+(i+1)+" should know that Agent"+(j+1)+" exists.", contentTraceEvents.contains("NEW_AGENT: " + commAgents[j].getAid()));
			}
		}
	}
	
	
	//@Test(timeout=20000)
	public void testOnTraceEventAgent_Destroy() throws Exception {
		
		// Check that all TraceManagers in the system receive NEW_AGENT Trace Event
		for(int i = 0; i < commTraceManagers.length; ++i)
			commTraceManagers[i].clearTraceEvents();
		
		for(int i = 0; i < commAgents.length; i++)
			commAgents[i].addCommand(CommandedAgent.END);
		
		for(int i = 0; i < commTraceManagers.length; i++) {
			while(commTraceManagers[i].getTraceEvents().size() < 1) {
				try {
					Thread.sleep(1 * 50);
				} catch (InterruptedException e) {
					fail(e.getMessage());
				}
			}
			ArrayList<TraceEvent> tE = commTraceManagers[i].getTraceEvents();
			ArrayList<String> controlTE = new ArrayList<String>();
			for(TraceEvent e : tE) {
				System.out.println(e.getTracingService() + ": " + e.getContent());
				controlTE.add(e.getTracingService() + ": " + e.getContent());
			}
			//assertTrue(controlTE.contains(res));
		}
		
	}
}
