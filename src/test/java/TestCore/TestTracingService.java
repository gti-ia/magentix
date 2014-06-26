package TestCore;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.TracingService;
import es.upv.dsic.gti_ia.trace.TraceMask;
import es.upv.dsic.gti_ia.trace.TracingEntity;
import es.upv.dsic.gti_ia.trace.TracingEntityList;
import es.upv.dsic.gti_ia.trace.TracingServiceSubscription;
import es.upv.dsic.gti_ia.trace.TracingServiceSubscriptionList;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
/**
 * Tests for the TracingService class
 * 
 * @author David Fernández - dfernandez@dsic.upv.es
 */

public class TestTracingService {
	
	TracingService tracingService;
	private Process qpid_broker;
	
	@Before
	public void setUp() throws Exception {
		tracingService = new TracingService();
	} 
	
	/**
	 * Testing empty constructor()
	 * 
	 */
	@Test(timeout = 5000)
	public void testEmptyConstructor(){
		assertEquals(tracingService.getName(), null);
		assertEquals(tracingService.getMandatory(), false);
		assertEquals(tracingService.getRequestable(), true);
		assertEquals(tracingService.getMaskBitIndex(), null);
		assertEquals(tracingService.getDescription(), null);
		assertEquals(tracingService.getProviders(), new TracingEntityList());
		assertEquals(tracingService.getSubscriptions(), new TracingServiceSubscriptionList());
	}
	
	/**
	 * Testing constructor(String, String)
	 * 
	 */
	@Test(timeout = 5000)
	public void test2ArgumentConstructor(){
		String serviceName = "Food info";
		String description = "Information about available food";
		
		tracingService = new TracingService(serviceName, description);
		
		assertEquals(tracingService.getName(), serviceName);
		assertEquals(tracingService.getMandatory(), false);
		assertEquals(tracingService.getRequestable(), true);
		assertEquals(tracingService.getMaskBitIndex().intValue(), TraceMask.CUSTOM);
		assertEquals(tracingService.getDescription(), description);
		assertEquals(tracingService.getProviders(), new TracingEntityList());
		assertEquals(tracingService.getSubscriptions(), new TracingServiceSubscriptionList());
	}
	
	/**
	 * Testing method setName(String) and getName()
	 * 
	 */
	@Test(timeout = 5000)
	public void testSetGetName(){
		String serviceName = "Food info";
		
		//Before set name should be null
		assertEquals(tracingService.getName(), null);
		
		tracingService.setName(serviceName);
		
		//After set
		assertEquals(tracingService.getName(), serviceName);
		assertEquals(tracingService.getMandatory(), false);
		assertEquals(tracingService.getRequestable(), true);
		assertEquals(tracingService.getMaskBitIndex(), null);
		assertEquals(tracingService.getDescription(), null);
		assertEquals(tracingService.getProviders(), new TracingEntityList());
		assertEquals(tracingService.getSubscriptions(), new TracingServiceSubscriptionList());
	}
	
	
	/**
	 * Testing method setDescription(String)
	 * 
	 */
	@Test(timeout = 5000)
	public void testSetGetDescription(){
		String description = "Information about available food";
		
		//Before set name should be null
		assertEquals(tracingService.getDescription(), null);
		
		tracingService.setDescription(description);
		
		//After set
		assertEquals(tracingService.getName(), null);
		assertEquals(tracingService.getMandatory(), false);
		assertEquals(tracingService.getRequestable(), true);
		assertEquals(tracingService.getMaskBitIndex(), null);
		assertEquals(tracingService.getDescription(), description);
		assertEquals(tracingService.getProviders(), new TracingEntityList());
		assertEquals(tracingService.getSubscriptions(), new TracingServiceSubscriptionList());
	}
	
	/**
	 * Testing method getMandatory()
	 * 
	 */
	@Test(timeout = 5000)
	public void testGetMandatory(){
		assertEquals(tracingService.getMandatory(), false);
	}	
	
	/**
	 * Testing method getRequestable()
	 * 
	 */
	@Test(timeout = 5000)
	public void testGetRequestable(){
		assertEquals(tracingService.getRequestable(), true);
	}
	
	/**
	 * Testing method getMaskBitIndex()
	 * 
	 */
	@Test(timeout = 5000)
	public void testGetMaskBitIndex(){
		assertEquals(tracingService.getMaskBitIndex(), null);
	}
	
	/**
	 * Testing method getProviders()
	 * 
	 * Tested when no providers has been added or removed
	 * 
	 */
	@Test(timeout = 5000)
	public void testGetProviders(){
		assertEquals(tracingService.getProviders(), new TracingEntityList());
	}
	
	/**
	 * Testing method getDITracingServiceByName(String)
	 * 
	 * Tested when service is and is not found
	 * 
	 */
	@Test(timeout = 5000)
	public void testGetDITracingServiceByName(){			
		//Test when tracing service is found
		String tracingServiceName = "MESSAGE_SENT_DETAIL";
		tracingService = TracingService.getDITracingServiceByName(tracingServiceName);		
		assertEquals(tracingService.getName(), tracingServiceName);
		
		//Test when tracing service is not found
		tracingServiceName = "NOT_A_SERVICE";
		tracingService = TracingService.getDITracingServiceByName(tracingServiceName);
		assertEquals(tracingService, null);
		
	}
	
	/**
	 * Testing method addServiceProvider(TracingEntity)
	 * 
	 * Also getProviders is tested when a provider is in the list
	 * 
	 */
	@Test(timeout = 5000)
	public void testAddServiceProvider(){		
		TracingEntity provider = new TracingEntity();
		TracingEntityList expectedProviders = new TracingEntityList();
		expectedProviders.add(provider);
		
		//Now provider should NOT be in tracingService list of providers
		assertEquals(tracingService.getProviders(),new TracingEntityList()); 
		
		//Add the provider
		tracingService.addServiceProvider(provider);
		
		//Now provider should be in tracingService list of providers
		assertEquals(tracingService.getProviders(), expectedProviders);
		
	}
	
	/**
	 * Testing method removeProvider(AgentID)
	 * 
	 * Tested when the provider does and does not exist
	 * 
	 */
	@Test(timeout = 5000)
	public void testRemoveProvider(){		
		AgentID agent = new AgentID("TestAgent");
		TracingEntity provider = new TracingEntity(TracingEntity.AGENT, agent);
		TracingEntityList expectedProviders = new TracingEntityList();
		expectedProviders.add(provider);
		
		//Now provider should NOT be in tracingService list of providers
		//False should be returned with the remove method
		assertEquals(tracingService.getProviders(),new TracingEntityList());
		assertEquals(tracingService.removeProvider(agent), false);
		
		//Add the provider
		tracingService.addServiceProvider(provider);
		
		//Now provider should be in tracingService list of providers
		assertEquals(tracingService.getProviders(), expectedProviders);
		assertEquals(tracingService.removeProvider(agent), true);
		
		//Now provider should NOT be in tracingService list of providers
		assertEquals(tracingService.getProviders(),new TracingEntityList());
	}
	
	/**
	 * Testing method getSubscriptions()
	 * 
	 * Tested when no subscriptions have been added or removed
	 * 
	 */
	@Test(timeout = 5000)
	public void testGetSubscriptions(){
		assertEquals(tracingService.getSubscriptions(), new TracingServiceSubscriptionList());
	}
	
	/**
	 * Testing method addSubscription(TracingServiceSubscription)
	 * 
	 * Also getSubcriptions is tested when a subscription is in the list
	 * 
	 * 
	 */
	@Test(timeout = 5000)
	public void testAddSubscription(){		
		TracingServiceSubscription subscription = new TracingServiceSubscription();
		TracingServiceSubscriptionList expectedSubscriptions = new TracingServiceSubscriptionList();
		expectedSubscriptions.add(subscription);
		
		//Now subscription should NOT be in tracingService list of subscriptions
		assertEquals(tracingService.getSubscriptions(),new TracingServiceSubscriptionList()); 
		
		tracingService.addSubscription(subscription);
		
		//Now subscription should be in tracingService list of subscriptions
		assertEquals(tracingService.getSubscriptions(), expectedSubscriptions);
		
	}
	
}