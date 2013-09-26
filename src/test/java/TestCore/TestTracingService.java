package TestCore;

import java.awt.List;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.TracingService;
import es.upv.dsic.gti_ia.trace.TraceMask;
import es.upv.dsic.gti_ia.trace.TracingEntity;
import es.upv.dsic.gti_ia.trace.TracingEntityList;
import es.upv.dsic.gti_ia.trace.TracingServiceSubscription;
import es.upv.dsic.gti_ia.trace.TracingServiceSubscriptionList;
import junit.framework.TestCase;

/**
 * Tests for the TracingService class
 * 
 * @author David Fernández - dfernandez@dsic.upv.es
 */

public class TestTracingService extends TestCase {
	
	TracingService tracingService;
	
	protected void setUp() throws Exception {
		super.setUp();
		
		tracingService = new TracingService();
	}
	
	public TestTracingService(String name) {
		super(name);
	}
	
	
	/**
	 * Testing empty constructor()
	 * 
	 */
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
	public void testGetMandatory(){
		assertEquals(tracingService.getMandatory(), false);
	}	
	
	/**
	 * Testing method getRequestable()
	 * 
	 */
	public void testGetRequestable(){
		assertEquals(tracingService.getRequestable(), true);
	}
	
	/**
	 * Testing method getMaskBitIndex()
	 * 
	 */
	public void testGetMaskBitIndex(){
		assertEquals(tracingService.getMaskBitIndex(), null);
	}
	
	/**
	 * Testing method getProviders()
	 * 
	 * Tested when no providers has been added or removed
	 * 
	 */
	public void testGetProviders(){
		assertEquals(tracingService.getProviders(), new TracingEntityList());
	}
	
	/**
	 * Testing method getDITracingServiceByName(String)
	 * 
	 * Tested when service is and is not found
	 * 
	 */
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
	public void testGetSubscriptions(){
		assertEquals(tracingService.getSubscriptions(), new TracingServiceSubscriptionList());
	}
	
	/**
	 * Testing method addSubscription(TracingServiceSubscription)
	 * 
	 * Also getSubcriptions is tested when a subscription is in the list
	 * 
	 */
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