package TestTrace;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.TracingService;
import es.upv.dsic.gti_ia.trace.TracingEntity;
import es.upv.dsic.gti_ia.trace.TracingServiceSubscription;
import es.upv.dsic.gti_ia.trace.TracingServiceSubscriptionList;

/** 
 * @author Jose Alemany Bordera  -  jalemany1@dsic.upv.es
 * 
 */

public class TestTracingServiceSubscriptionList {

	/* Constants */
	private static final String[] SERVICE_NAME = {"SERVICE1", "æßðŋ¶ħæððŋħøþßđłß€¶ßł#æ«»"};
	private static final String[] SERVICE_DESCRIPTION = {"Service1 content goes here", "/ħæß}]«»ðłħææ=/(·Wæ¢ðıł¢$®º§ð’łæ§ðþıø£⅜™¢ŋ&łðħ æ¢↑øŋ"};
	private static final int[] VALID_TYPES = {TracingEntity.AGENT, TracingEntity.ARTIFACT, TracingEntity.AGGREGATION, -1};
	private static final AgentID[] AIDs = {new AgentID("ProofAgent1"), new AgentID("ProofAgent2"), null};
	
	/* Attributes */
	private static TracingEntity[] tEntities = new TracingEntity[VALID_TYPES.length*AIDs.length+1];
	private static TracingService[] tService = new TracingService[4];
	private static TracingServiceSubscriptionList tSSL = new TracingServiceSubscriptionList();
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		
		int nTE = 0, nTS = 0, nTSS = 0;
		for(int i = 0; i < VALID_TYPES.length; i++)
			for(int j = 0; j < AIDs.length; j++)
				assertNotNull("The number "+(nTE+1)+" TracingEntity instance is not created correctly.", tEntities[nTE++] = new TracingEntity(VALID_TYPES[i], AIDs[j]));
		
		for(nTS = 0; nTS < tService.length-1; nTS++)
			if (nTS == tService.length-2) assertNotNull("The number "+(nTS+1)+" TracingService instance is not created correctly.", tService[nTS] = new TracingService());
			else assertNotNull("The number "+(nTS+1)+" TracingService instance is not created correctly.", tService[nTS] = new TracingService(SERVICE_NAME[nTS], SERVICE_DESCRIPTION[nTS]));
		
		TracingServiceSubscription tSS;
		for(TracingEntity tE1 : tEntities)
			for(TracingEntity tE2 : tEntities)
				for(TracingService tS : tService) {
					tSS = new TracingServiceSubscription(tE1,tE2,tS);
					tSSL.add(tSS);
					assertEquals("The number "+(++nTSS)+" TracingServiceSubscription instance is not created correctly.", true, tSSL.contains(tSS));
				}
		tSS = new TracingServiceSubscription();
		tSSL.add(tSS);
		assertEquals("The number "+(++nTSS)+" TracingServiceSubscription instance is not created correctly.", true, tSSL.contains(tSS));
	}
	
	@AfterClass
	public static void tearDownClass() throws Exception {
		
		for(int i = 0; i < tEntities.length; i++)
			tEntities[i] = null;
		
		for(int i = 0; i < tService.length; i++)
			tService[i] = null;
		
		tSSL.clear();
	}
	
	/* Test methods */
	@Test (timeout = 5000)
	public void testGetTSS0(){
		
		int n = 0;
		for(TracingEntity tE1 : tEntities)
			for(TracingEntity tE2 : tEntities)
				for(TracingService tS : tService)
					assertEquals("Not found the "+(++n)+" TracingServiceSubscription when should have found.", true, tSSL.contains(tSSL.getTSS(tE1, tE2, tS)));
	}
	@Test (timeout = 5000)
	public void testGetTSS1(){
		
		int n = 0;
		TracingEntity[] tEntitiesFake = {new TracingEntity(TracingEntity.AGENT, new AgentID("FakeAgent1")), new TracingEntity(TracingEntity.AGGREGATION, new AgentID("FakeAgent2"))};
		for(TracingEntity tE1 : tEntitiesFake)
			for(TracingEntity tE2 : tEntities)
				for(TracingService tS : tService)
					assertEquals("Found the "+(++n)+" TracingServiceSubscription when shouldn't have found.", false, tSSL.contains(tSSL.getTSS(tE1, tE2, tS)));
	}
	@Test (timeout = 5000)
	public void testGetTSS2(){
		
		int n = 0;
		TracingEntity[] tEntitiesFake = {new TracingEntity(TracingEntity.AGENT, new AgentID("FakeAgent1")), new TracingEntity(TracingEntity.AGGREGATION, new AgentID("FakeAgent2"))};
		for(TracingEntity tE1 : tEntities)
			for(TracingEntity tE2 : tEntitiesFake)
				for(TracingService tS : tService)
					assertEquals("Found the "+(++n)+" TracingServiceSubscription when shouldn't have found.", false, tSSL.contains(tSSL.getTSS(tE1, tE2, tS)));
	}
	@Test (timeout = 5000)
	public void testGetTSS3(){
		
		int n = 0;
		TracingService[] tServiceFake = {new TracingService("FAKE_SERVICE","Its fake description"), new TracingService("ħæððŋħøþßđłß","/ħæß}]«»ðłħææ=/(·Wæ¢ðıł¢$®º§ð’łæ§ðþ")};
		for(TracingEntity tE1 : tEntities)
			for(TracingEntity tE2 : tEntities)
				for(TracingService tS : tServiceFake)
					assertEquals("Found the "+(++n)+" TracingServiceSubscription when shouldn't have found.", false, tSSL.contains(tSSL.getTSS(tE1, tE2, tS)));
	}
}
