package TestTrace;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.trace.TraceManager;
import es.upv.dsic.gti_ia.trace.TracingEntityList;

public class TestTraceManager {

	/* Constants */
	// Dependent of other classes (TraceManager).
	private static final String TRACING_ENTITIES_VARIABLE_NAME = "TracingEntities";
	private static final String TRACING_SERVICE_PROVIDER_ENTITIES_VARIABLE_NAME = "TSProviderEntities";
	private static final String TRACING_SERVICE_SUBSCRIBER_ENTITIES_VARIABLE_NAME = "TSSubscriberEntities";
	private static final String TRACING_SERVICES_VARIABLE_NAME = "TracingServices";
	private static final String MONITORIZABLE_VARIABLE_NAME = "monitorizable";
	
	// Independent, proper of this class.
	private static final String[] COMMANDED_TM_NAMES = {"qpid://TM@localhost:8080", "qpid://\\”ł¶ħß¢ħł½»·¶Ħ[ßðđħł@localhost:8080"};

	/* Attributes */
	private static CommandedTraceManager[] commTraceManagers = {null, null};
	
	static Process qpid_broker;
	
	
	/* Set up class and tear down class */
	@BeforeClass
	public static void setUpClass() throws Exception {
		
		qpid_broker = qpidManager.UnixQpidManager.startQpid(Runtime.getRuntime(), qpid_broker);
		
		AgentsConnection.connect();		// Connecting to Qpid Broker.
		
		for(int i = 0; i < commTraceManagers.length; ++i) {
			commTraceManagers[i] = new CommandedTraceManager(new AgentID(COMMANDED_TM_NAMES[i]));
			commTraceManagers[i].start();
		}
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		for(int i = 0; i < commTraceManagers.length; ++i) {
			commTraceManagers[i].addCommand(CommandedTraceManager.END);
		}
		
		AgentsConnection.disconnect();
		qpidManager.UnixQpidManager.stopQpid(qpid_broker);
	}

	/* Test methods */
	@Test
	public void testTraceManagerMonitorizable0() { theTestOfTraceManagerMonitorizable(0); }
	@Test
	public void testTraceManagerMonitorizable1() { theTestOfTraceManagerMonitorizable(1); }
	public void theTestOfTraceManagerMonitorizable(int d) {
		Field fields[] = CommandedTraceManager.class.getSuperclass().getDeclaredFields();
	    for (int i = 0; i < fields.length; i++){ 
	        fields[i].setAccessible(true); 
	        
	        try {
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
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				fail();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				fail();
			} 
	    }
	}
	
	//@Test
	public void testTraceManagerNotMonitorizable() {
		Class[] argClasses = {AgentID.class};
		try {
			Method method = TraceManager.class.getDeclaredMethod("TraceManager", argClasses);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
