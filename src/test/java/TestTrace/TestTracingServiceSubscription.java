package TestTrace;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.TracingService;
import es.upv.dsic.gti_ia.trace.TracingEntity;
import es.upv.dsic.gti_ia.trace.TracingServiceSubscription;

/** 
 * @author Jose Alemany Bordera  -  jalemany1@dsic.upv.es
 * 
 */

public class TestTracingServiceSubscription {

	/* Constants */
	private static final String[] SERVICE_NAME = {"SERVICE1", "æßðŋ¶ħæððŋħøþßđłß€¶ßł#æ«»"};
	private static final String[] SERVICE_DESCRIPTION = {"Service1 content goes here", "/ħæß}]«»ðłħææ=/(·Wæ¢ðıł¢$®º§ð’łæ§ðþıø£⅜™¢ŋ&łðħ æ¢↑øŋ"};
	private static final int[] VALID_TYPES = {TracingEntity.AGENT, TracingEntity.ARTIFACT, TracingEntity.AGGREGATION, -1};
	private static final AgentID[] AIDs = {new AgentID("ProofAgent1"), new AgentID("ProofAgent2"), null};
	
	/* Attributes */
	private static TracingEntity[] tEntities = new TracingEntity[VALID_TYPES.length*AIDs.length+1];
	private static TracingService[] tService = new TracingService[4];
	private static TracingServiceSubscription[] tSS = new TracingServiceSubscription[tEntities.length*tEntities.length*tService.length+1];
	
	@BeforeClass
	public static void setUp() throws Exception {
		
		int nTE = 0, nTS = 0, nTSS = 0;
		for(int i = 0; i < VALID_TYPES.length; i++)
			for(int j = 0; j < AIDs.length; j++)
				assertNotNull("The number "+nTE+" TracingEntity instance is not created correctly.", tEntities[nTE++] = new TracingEntity(VALID_TYPES[i], AIDs[j]));
		
		for(nTS = 0; nTS < tService.length-1; nTS++)
			if (nTS == tService.length-2) assertNotNull("The number "+nTS+" TracingService instance is not created correctly.", tService[nTS] = new TracingService());
			else assertNotNull("The number "+nTS+" TracingService instance is not created correctly.", tService[nTS] = new TracingService(SERVICE_NAME[nTS], SERVICE_DESCRIPTION[nTS]));
		
		for(TracingEntity tE1 : tEntities)
			for(TracingEntity tE2 : tEntities)
				for(TracingService tS : tService)
					assertNotNull("The number "+nTSS+" TracingServiceSubscription instance is not created correctly.", tSS[nTSS++] = new TracingServiceSubscription(tE1,tE2,tS));
		assertNotNull("The number "+nTSS+" TracingServiceSubscription instance is not created correctly.", tSS[nTSS] = new TracingServiceSubscription());
	}
	
	@AfterClass
	public static void tearDown() throws Exception {
		
		for(int i = 0; i < tEntities.length; i++)
			tEntities[i] = null;
		
		for(int i = 0; i < tService.length; i++)
			tService[i] = null;
		
		for(int i = 0; i < tSS.length; i++)
			tSS[i] = null;
	}
	
	/* Test methods */
	@Test
	public void testGetSubscriptorEntity(){
		
		int i, n = tEntities.length*tService.length;
		for(i = 0; i < tSS.length-1; i++)
			assertEquals("The Subscriptor Entity of TracingServiceSubscription "+(i+1)+" of "+tSS.length+" is not correct.", tEntities[(int) i/n], tSS[i].getSubscriptorEntity());
		assertEquals("The Subscriptor Entity of TracingServiceSubscription "+(i+1)+" of "+tSS.length+" is not correct.", null, tSS[i].getSubscriptorEntity());
	}
	
	@Test
	public void testGetAnyProvider(){
		
		int i, n = tService.length, m = tEntities.length;
		for(i = 0; i < tSS.length-1; i++)
			assertEquals("The TracingServiceSubscription "+(i+1)+" of "+tSS.length+" does not return the expected value.", ((tEntities[((int) i/n)%m] == null) ? true : false), tSS[i].getAnyProvider());
		assertEquals("The TracingServiceSubscription "+(i+1)+" of "+tSS.length+" does not return the expected value.", true, tSS[i].getAnyProvider());
	}
	
	@Test
	public void testGetOriginEntity(){
		
		int i, n = tService.length, m = tEntities.length;
		for(i = 0; i < tSS.length-1; i++)
			assertEquals("The Origin Entity of TracingServiceSubscription "+(i+1)+" of "+tSS.length+" is not correct.", tEntities[((int) i/n)%m], tSS[i].getOriginEntity());
		assertEquals("The Origin Entity of TracingServiceSubscription "+(i+1)+" of "+tSS.length+" is not correct.", null, tSS[i].getOriginEntity());
	}
	
	@Test
	public void testGetTracingService(){
		
		int i, n = tService.length;
		for(i = 0; i < tSS.length-1; i++)
			assertEquals("The TracingService of TracingServiceSubscription "+(i+1)+" of "+tSS.length+" is not correct.", tService[i%n], tSS[i].getTracingService());
		assertEquals("The TracingService of TracingServiceSubscription "+(i+1)+" of "+tSS.length+" is not correct.", null, tSS[i].getTracingService());
	}
	
	@Test
	public void testToEquals0(){ theTestOfEquals(tSS[0], tSS[0], true); }
	@Test
	public void testToEquals1(){ theTestOfEquals(tSS[0], new TracingServiceSubscription(tEntities[0], new TracingEntity(TracingEntity.AGENT, new AgentID("OtherAgent")), tService[0]), false); }
	@Test
	public void testToEquals2(){ TracingEntity t = new TracingEntity(TracingEntity.AGENT, new AgentID("OtherAgent")); t.addSubscription(tSS[0]); theTestOfEquals(tSS[0], new TracingServiceSubscription(t,t,tService[2]), false); }
	@Test
	public void testToEquals3(){ theTestOfEquals(tSS[1], new TracingServiceSubscription(tEntities[0], tEntities[0], tService[1]), true); }
	@Test
	public void testToEquals4(){ theTestOfEquals(tSS[1], null, false); }
	@Test
	public void testToEquals5(){ theTestOfEquals(tSS[0], tSS[3], false); }
	public void theTestOfEquals(TracingServiceSubscription t1, TracingServiceSubscription t2, boolean res) {
		assertEquals(res, t1.equals(t2));
	}
}
