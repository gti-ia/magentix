package TestTrace;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.core.TraceEvent;
import es.upv.dsic.gti_ia.organization.Configuration;
import es.upv.dsic.gti_ia.trace.TraceMask;
import es.upv.dsic.gti_ia.trace.TracingEntityList;

/** 
 * @author Jose Alemany Bordera  -  jalemany1@dsic.upv.es
 * 
 */

public class TestTraceManager {

	/* Constants */
	// Dependent of other classes (TraceManager).
	private static final String TRACING_ENTITIES_VARIABLE_NAME = "TracingEntities";
	private static final String TRACING_SERVICE_PROVIDER_ENTITIES_VARIABLE_NAME = "TSProviderEntities";
	private static final String TRACING_SERVICE_SUBSCRIBER_ENTITIES_VARIABLE_NAME = "TSSubscriberEntities";
	private static final String TRACING_SERVICES_VARIABLE_NAME = "TracingServices";
	private static final String MONITORIZABLE_VARIABLE_NAME = "monitorizable";
	private static final Configuration conf = Configuration.getConfiguration();
	
	// Independent, proper of this class.
	private static final String[] COMMANDED_TM_NOT_MONITORIZABLES = {"qpid://TM@localhost:8080", "qpid://\\”ł¶ħß¢ħł½»·¶Ħ[ßðđħł@localhost:8080"};
	private static final String[] COMMANDED_TM_MONITORIZABLES = {"qpid://TM_Alt@localhost:8080", "qpid://\\”ł¶ħß¢ħł½»·¶Ħ[ßðđħł_ALt@localhost:8080"};
	private static final String[] ENCODED_MASCKS = {"0000000100","1111111100","1111100100","1001100100"};
	
	/* Attributes */
	private static CommandedTraceManager[] commTraceManagers = {null, null, null, null};
	static Process qpid_broker;
	
	
	/* Set up class and tear down class */
	@Before
	public void setUp() throws Exception {
		
		qpid_broker = qpidManager.UnixQpidManager.startQpid(Runtime.getRuntime(), qpid_broker);
		
		AgentsConnection.connect();		// Connecting to Qpid Broker.
		
		for(int i = 0; i < commTraceManagers.length; i+=2) {
			commTraceManagers[i] = new CommandedTraceManager(new AgentID(COMMANDED_TM_NOT_MONITORIZABLES[i/2]), false);
			commTraceManagers[i].start();
			
			commTraceManagers[i+1] = new CommandedTraceManager(new AgentID(COMMANDED_TM_MONITORIZABLES[i/2]), true);
			commTraceManagers[i+1].start();
		}
	}

	@After
	public void tearDown() throws Exception {
		
		for(int i = 0; i < commTraceManagers.length; ++i) {
			commTraceManagers[i].addCommand(CommandedTraceManager.END);
		}
		
		AgentsConnection.disconnect();
		qpidManager.UnixQpidManager.stopQpid(qpid_broker);
	}

	/* Test methods */
	@Test
	public void testTraceManagerMonitorizable0() throws Exception { theTestOfTraceManagerMonitorizable(1); }
	@Test
	public void testTraceManagerMonitorizable1() throws Exception { theTestOfTraceManagerMonitorizable(3); }
	public void theTestOfTraceManagerMonitorizable (int d) throws Exception {
		
		Field fields[] = CommandedTraceManager.class.getSuperclass().getDeclaredFields();
	    for (int i = 0; i < fields.length; i++){ 
	        fields[i].setAccessible(true); 
	        
	        if(fields[i].getName().equals(TRACING_ENTITIES_VARIABLE_NAME)) {
	        	TracingEntityList tEntities = (TracingEntityList) fields[i].get(commTraceManagers[d]);
		       	assertEquals("The list of tracing entities must contain one element.", 1, tEntities.size());
		    }
		    else if(fields[i].getName().equals(TRACING_SERVICE_PROVIDER_ENTITIES_VARIABLE_NAME)) {
		        	
		    }
			else if(fields[i].getName().equals(TRACING_SERVICE_SUBSCRIBER_ENTITIES_VARIABLE_NAME)) {
					        	
			}
			else if(fields[i].getName().equals(TRACING_SERVICES_VARIABLE_NAME)) {
								
			}
			else if(fields[i].getName().equals(MONITORIZABLE_VARIABLE_NAME)) {
					assertTrue("Monitorizable flag must be set to true.", fields[i].getBoolean(commTraceManagers[d]));
			} 
	    }
	}
	
	@Test
	public void testTraceManagerNotMonitorizable0() throws Exception { theTestOfTraceManagerNotMonitorizable(0); }
	@Test
	public void testTraceManagerNotMonitorizable1() throws Exception { theTestOfTraceManagerNotMonitorizable(2); }
	public void theTestOfTraceManagerNotMonitorizable (int d) throws Exception {
		
		Field fields[] = CommandedTraceManager.class.getSuperclass().getDeclaredFields();
	    for (int i = 0; i < fields.length; i++){ 
	        fields[i].setAccessible(true); 
	        
	        if(fields[i].getName().equals(TRACING_ENTITIES_VARIABLE_NAME)) {
	        	TracingEntityList tEntities = (TracingEntityList) fields[i].get(commTraceManagers[d]);
		       	assertEquals("The list of tracing entities must contain one element.", 1, tEntities.size());
		    }
		    else if(fields[i].getName().equals(TRACING_SERVICE_PROVIDER_ENTITIES_VARIABLE_NAME)) {
		        	
		    }
			else if(fields[i].getName().equals(TRACING_SERVICE_SUBSCRIBER_ENTITIES_VARIABLE_NAME)) {
					        	
			}
			else if(fields[i].getName().equals(TRACING_SERVICES_VARIABLE_NAME)) {
								
			}
			else if(fields[i].getName().equals(MONITORIZABLE_VARIABLE_NAME)) {
					assertFalse("Monitorizable flag must be set to false.", fields[i].getBoolean(commTraceManagers[d]));
			} 
	    }
	}
	
	@Test
	public void testGetTraceMask() throws Exception { 
		
		for(int i = 0; i < commTraceManagers.length; i++)
			assertEquals("The TraceManager mask "+(i+1)+" should match with the specified in the configuration file.", conf.getTraceMask(), commTraceManagers[i].getTraceMask().toString());
	}
	
	//@Test(timeout=20000)
	public void testSetTraceMask() throws Exception { 
		
		ArrayList<TraceEvent> listTE;
		for(int i = 0; i < commTraceManagers.length; i++) {
			commTraceManagers[i].setTraceMask(new TraceMask(ENCODED_MASCKS[i]));
			
			for(int j = 0; j < commTraceManagers.length; j++) {
				assertEquals("The TraceManager mask "+(j+1)+" should match with the current mask.", ENCODED_MASCKS[i], commTraceManagers[j].getTraceMask().toString());
			}
		}
	}
}
