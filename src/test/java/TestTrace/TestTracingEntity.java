package TestTrace;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.TracingService;
import es.upv.dsic.gti_ia.trace.TracingEntity;
import es.upv.dsic.gti_ia.trace.TracingServiceSubscription;

/** 
 * @author Jose Alemany Bordera  -  jalemany1@dsic.upv.es
 * 
 */

public class TestTracingEntity {
	
	/* Constants */
	private static final int[] VALID_TYPES = {TracingEntity.AGENT, TracingEntity.ARTIFACT, TracingEntity.AGGREGATION, -1};
	private static final int[] INVALID_TYPES = {100,-100};
	private static final AgentID AID = new AgentID("ProofAgent");
	private static final TracingServiceSubscription TSS = new TracingServiceSubscription(new TracingEntity(TracingEntity.AGENT,AID),new TracingEntity(TracingEntity.AGENT,AID),new TracingService("ProofService","Its Description"));
	
	/* Attributes */
	private static TracingEntity[] validTEntities = {null, null, null, null};
	private static TracingEntity[] invalidTEntities = {null, null};
	
	@Before
	public void setUp() throws Exception {
		
		for(int i = 0; i < validTEntities.length; ++i) {
			if (i == validTEntities.length-1) validTEntities[i] = new TracingEntity();
			else validTEntities[i] = new TracingEntity(VALID_TYPES[i], AID);
			assertNotNull("The number "+i+" TracingEntity instance is not created correctly.", validTEntities[i]);
		}
		for(int i = 0; i < invalidTEntities.length; ++i) {
			invalidTEntities[i] = new TracingEntity(INVALID_TYPES[i], AID);
			assertNotNull("The number "+i+" TracingEntity instance is not created correctly.", invalidTEntities[i]);
		}
	}
	
	@After
	public void tearDown() throws Exception {
		
		for(int i = 0; i < validTEntities.length; ++i) {
			validTEntities[i] = null;
		}
		for(int i = 0; i < invalidTEntities.length; ++i) {
			invalidTEntities[i] = null;
		}
	}
	
	/* Test methods */
	@Test (timeout = 5000)
	public void testGetType0(){ theTestOfGetType(0); }
	@Test (timeout = 5000)
	public void testGetType1(){ theTestOfGetType(1); }
	@Test (timeout = 5000)
	public void testGetType2(){ theTestOfGetType(2); }
	@Test (timeout = 5000)
	public void testGetType3(){ theTestOfGetType(3); }
	@Test (timeout = 5000)
	public void testGetType4(){ theTestOfGetType(4); }
	@Test (timeout = 5000)
	public void testGetType5(){ theTestOfGetType(5); }
	public void theTestOfGetType(int d) {
		if (d < validTEntities.length)
			assertEquals(VALID_TYPES[d], validTEntities[d].getType());
		else
			assertEquals(-1, invalidTEntities[d-validTEntities.length].getType());
	}
	
	@Test (timeout = 5000)
	public void testGetAid0(){ theTestOfGetAid(0); }
	@Test (timeout = 5000)
	public void testGetAid1(){ theTestOfGetAid(1); }
	@Test (timeout = 5000)
	public void testGetAid2(){ theTestOfGetAid(2); }
	@Test (timeout = 5000)
	public void testGetAid3(){ theTestOfGetAid(3); }
	@Test (timeout = 5000)
	public void testGetAid4(){ theTestOfGetAid(4); }
	@Test (timeout = 5000)
	public void testGetAid5(){ theTestOfGetAid(5); }
	public void theTestOfGetAid(int d) {
		if (d < validTEntities.length)
			assertEquals(((VALID_TYPES[d]==0) ? AID : null), validTEntities[d].getAid());
		else
			assertEquals(((INVALID_TYPES[d-validTEntities.length]==0) ? AID : null), invalidTEntities[d-validTEntities.length].getAid());
	}
	
	@Test (timeout = 5000)
	public void testSetType0(){ theTestOfSetType(0,TracingEntity.AGGREGATION); }
	@Test (timeout = 5000)
	public void testSetType1(){ theTestOfSetType(1,TracingEntity.AGENT); }
	@Test (timeout = 5000)
	public void testSetType2(){ theTestOfSetType(2,100); }
	@Test (timeout = 5000)
	public void testSetType3(){ theTestOfSetType(3,TracingEntity.AGENT); }
	@Test (timeout = 5000)
	public void testSetType4(){ theTestOfSetType(4,-100); }
	@Test (timeout = 5000)
	public void testSetType5(){ theTestOfSetType(5,TracingEntity.AGENT); }
	public void theTestOfSetType(int d, int type) {
		int res = (type < 0 || type > 2) ? -1 : 0;
		if (d < validTEntities.length){
			assertEquals(res, validTEntities[d].setType(type));
			//if (res == 0 && type == 0) assertvalidTEntities[d].getAid()
		} else {
			assertEquals(res, invalidTEntities[d-validTEntities.length].setType(type));
		}
	}
	
	@Test (timeout = 5000)
	public void testGetPublishedTS0(){ theTestOfGetPublishedTS(0); }
	@Test (timeout = 5000)
	public void testGetPublishedTS1(){ theTestOfGetPublishedTS(1); }
	@Test (timeout = 5000)
	public void testGetPublishedTS2(){ theTestOfGetPublishedTS(2); }
	@Test (timeout = 5000)
	public void testGetPublishedTS3(){ theTestOfGetPublishedTS(3); }
	@Test (timeout = 5000)
	public void testGetPublishedTS4(){ theTestOfGetPublishedTS(4); }
	@Test (timeout = 5000)
	public void testGetPublishedTS5(){ theTestOfGetPublishedTS(5); }
	public void theTestOfGetPublishedTS(int d) {
		if (d < validTEntities.length)
			assertEquals("[]", validTEntities[d].getPublishedTS().toString());
		else
			assertEquals("[]", invalidTEntities[d-validTEntities.length].getPublishedTS().toString());
	}
	
	@Test (timeout = 5000)
	public void testGetSubscribedToTS0(){ theTestOfGetSubscribedToTS(0); }
	@Test (timeout = 5000)
	public void testGetSubscribedToTS1(){ theTestOfGetSubscribedToTS(1); }
	@Test (timeout = 5000)
	public void testGetSubscribedToTS2(){ theTestOfGetSubscribedToTS(2); }
	@Test (timeout = 5000)
	public void testGetSubscribedToTS3(){ theTestOfGetSubscribedToTS(3); }
	@Test (timeout = 5000)
	public void testGetSubscribedToTS4(){ theTestOfGetSubscribedToTS(4); }
	@Test (timeout = 5000)
	public void testGetSubscribedToTS5(){ theTestOfGetSubscribedToTS(5); }
	public void theTestOfGetSubscribedToTS(int d) {
		if (d < validTEntities.length)
			assertEquals("[]", validTEntities[d].getSubscribedToTS().toString());
		else
			assertEquals("[]", invalidTEntities[d-validTEntities.length].getSubscribedToTS().toString());
	}
	
	@Test (timeout = 5000)
	public void testAddSubscription0(){ theTestOfAddSubscription(0); }
	@Test (timeout = 5000)
	public void testAddSubscription1(){ theTestOfAddSubscription(1); }
	@Test (timeout = 5000)
	public void testAddSubscription2(){ theTestOfAddSubscription(2); }
	@Test (timeout = 5000)
	public void testAddSubscription3(){ theTestOfAddSubscription(3); }
	@Test (timeout = 5000)
	public void testAddSubscription4(){ theTestOfAddSubscription(4); }
	@Test (timeout = 5000)
	public void testAddSubscription5(){ theTestOfAddSubscription(5); }
	public void theTestOfAddSubscription(int d) {
		TracingServiceSubscription aux = new TracingServiceSubscription();
		if (d < validTEntities.length) {
			assertTrue(validTEntities[d].addSubscription(TSS));
			assertTrue(validTEntities[d].addSubscription(TSS));
			assertTrue(validTEntities[d].addSubscription(aux));
			assertEquals("["+TSS.toString()+", "+TSS.toString()+", "+aux.toString()+"]", validTEntities[d].getSubscribedToTS().toString());
		} else {
			assertTrue(invalidTEntities[d-validTEntities.length].addSubscription(TSS));
			assertTrue(invalidTEntities[d-validTEntities.length].addSubscription(TSS));
			assertTrue(invalidTEntities[d-validTEntities.length].addSubscription(aux));
			assertEquals("["+TSS.toString()+", "+TSS.toString()+", "+aux.toString()+"]", invalidTEntities[d-validTEntities.length].getSubscribedToTS().toString());
		}	
	}
	
	@Test (timeout = 5000)
	public void testRemoveSubscription0(){ theTestOfRemoveSubscription(0); }
	@Test (timeout = 5000)
	public void testRemoveSubscription1(){ theTestOfRemoveSubscription(1); }
	@Test (timeout = 5000)
	public void testRemoveSubscription2(){ theTestOfRemoveSubscription(2); }
	@Test (timeout = 5000)
	public void testRemoveSubscription3(){ theTestOfRemoveSubscription(3); }
	@Test (timeout = 5000)
	public void testRemoveSubscription4(){ theTestOfRemoveSubscription(4); }
	@Test (timeout = 5000)
	public void testRemoveSubscription5(){ theTestOfRemoveSubscription(5); }
	public void theTestOfRemoveSubscription(int d) {
		TracingServiceSubscription aux = new TracingServiceSubscription();
		if (d < validTEntities.length) {
			assertTrue(validTEntities[d].addSubscription(aux));
			assertEquals("["+aux.toString()+"]", validTEntities[d].getSubscribedToTS().toString());
			assertFalse(validTEntities[d].removeSubscription(TSS));
			assertEquals("["+aux.toString()+"]", validTEntities[d].getSubscribedToTS().toString());
		} else {
			assertTrue(invalidTEntities[d-validTEntities.length].addSubscription(TSS));
			assertTrue(invalidTEntities[d-validTEntities.length].addSubscription(TSS));
			assertTrue(invalidTEntities[d-validTEntities.length].addSubscription(aux));
			assertEquals("["+TSS.toString()+", "+TSS.toString()+", "+aux.toString()+"]", invalidTEntities[d-validTEntities.length].getSubscribedToTS().toString());
			assertTrue(invalidTEntities[d-validTEntities.length].removeSubscription(TSS));
			assertEquals("["+TSS.toString()+", "+aux.toString()+"]", invalidTEntities[d-validTEntities.length].getSubscribedToTS().toString());
			assertTrue(invalidTEntities[d-validTEntities.length].removeSubscription(TSS));
			assertEquals("["+aux.toString()+"]", invalidTEntities[d-validTEntities.length].getSubscribedToTS().toString());
		
		}	
	}
	
	@Test (timeout = 5000)
	public void testHasTheSameAidAs0(){ theTestOfHasTheSameAidAs(0, AID, true); }
	@Test (timeout = 5000) //(expected=NullPointerException.class)
	public void testHasTheSameAidAs1(){ theTestOfHasTheSameAidAs(0, null, false); }
	@Test (timeout = 5000)
	public void testHasTheSameAidAs2(){ theTestOfHasTheSameAidAs(0, new AgentID(), false); }
	@Test (timeout = 5000) //(expected=NullPointerException.class)
	public void testHasTheSameAidAs3(){ theTestOfHasTheSameAidAs(1, null, false); }
	@Test (timeout = 5000) //(expected=NullPointerException.class)
	public void testHasTheSameAidAs4(){ theTestOfHasTheSameAidAs(2, new AgentID(), false); }
	@Test (timeout = 5000) //(expected=NullPointerException.class)
	public void testHasTheSameAidAs5(){ theTestOfHasTheSameAidAs(4, AID, false); }
	public void theTestOfHasTheSameAidAs(int d, AgentID aid, boolean res) {
		if (d < validTEntities.length)
			assertEquals(res, validTEntities[d].hasTheSameAidAs(aid));
		else
			assertEquals(res, invalidTEntities[d-validTEntities.length].hasTheSameAidAs(aid));
	}
	
	@Test (timeout = 5000)
	public void testToReadableString0(){ theTestOfToReadableString(0); }
	@Test (timeout = 5000)
	public void testToReadableString1(){ theTestOfToReadableString(1); }
	@Test (timeout = 5000)
	public void testToReadableString2(){ theTestOfToReadableString(2); }
	@Test (timeout = 5000)
	public void testToReadableString3(){ theTestOfToReadableString(3); }
	@Test (timeout = 5000)
	public void testToReadableString4(){ theTestOfToReadableString(4); }
	@Test (timeout = 5000)
	public void testToReadableString5(){ theTestOfToReadableString(5); }
	public void theTestOfToReadableString(int d) {
		if (d < validTEntities.length)
			if (VALID_TYPES[d]==0) assertEquals(AID.toString(), validTEntities[d].toReadableString());
			else assertEquals(null, validTEntities[d].toReadableString());
		else
			assertEquals(null, invalidTEntities[d-validTEntities.length].toReadableString());
	}
	
	@Test (timeout = 5000)
	public void testToEquals0(){ theTestOfEquals(validTEntities[0],validTEntities[0], true); }
	@Test (timeout = 5000)
	public void testToEquals1(){ theTestOfEquals(validTEntities[0],new TracingEntity(TracingEntity.AGENT,null), false); }
	@Test (timeout = 5000)
	public void testToEquals2(){ TracingEntity t = new TracingEntity(TracingEntity.AGENT,AID); t.addSubscription(TSS); theTestOfEquals(validTEntities[0],t, false); }
	@Test (timeout = 5000)
	public void testToEquals3(){ theTestOfEquals(validTEntities[0],new TracingEntity(TracingEntity.AGENT,AID), true); }
	@Test (timeout = 5000)
	public void testToEquals4(){ theTestOfEquals(validTEntities[0],invalidTEntities[0], false); }
	@Test (timeout = 5000)
	public void testToEquals5(){ theTestOfEquals(validTEntities[0],validTEntities[3], false); }
	public void theTestOfEquals(TracingEntity t1, TracingEntity t2, boolean res) {
		assertEquals(res, t1.equals(t2));
	}
}
