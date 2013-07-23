package TestCAgents;

import junit.framework.TestCase;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.Ignore;
import org.junit.Test;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

/**
 * Test class for Request factory template (FIPA protocol)
 * based on the example requestFactory
 * 
 * @author David Fernández - dfernandez@dsic.upv.es
 */

public class TestRequestFactory extends TestCase{
	
	HarryClass2 Harry;
	SallyClass2 Sally;
	
	public TestRequestFactory(String name){
		super(name);
	}
	
	public void setUp() throws Exception{
		super.setUp();
		
		try {
		
			/**
			 * Setting the configuration
			 */
			DOMConfigurator.configure("configuration/loggin.xml");
				
			/**
			* Connecting to Qpid Broker, default localhost.
			*/	
		    AgentsConnection.connect();
		        
		    /**
			 * Instantiating the CAgents
			 */
		    Harry = new HarryClass2(new AgentID("Harry2"));
		    Sally = new SallyClass2(new AgentID("Sally2"));
		    
		
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	
	
	
	/**
	 * Testing inform message sent by the participant Factory to Harry
	 */
	@Test public void testInformMessage(){
		Sally.start();
		Harry.start();
		
		//If Agent has not received the inform message
		while(Harry.informMsg.equalsIgnoreCase(""))
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		assertEquals(Harry.getName()+": "+Sally.getName()+" informs me Yes, my number is 666 456 855"
			,Harry.informMsg);
	}
	
	/**
	 * Testing ReceiveRequest in participant Factory in Sally
	 */
	@Test public void testRefuseMessage(){
		Sally.start();
		Harry.start();
		
		
		//If Agent has not received the inform message
		while(Harry.informMsg.equalsIgnoreCase(""))
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		
		assertTrue(Sally.acceptRequests);
	}
	
}
