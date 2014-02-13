package TestTrace;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.trace.TracingEntity;
import es.upv.dsic.gti_ia.trace.TracingEntityList;

/** 
 * @author Jose Alemany Bordera  -  jalemany1@dsic.upv.es
 * 
 */

public class TestTracingEntityList {

	/* Constants */
	private static final AgentID[] AIDs = {new AgentID("Agent1"),new AgentID("Agent2"),new AgentID("Agent3")};
	private static final int[] TYPES = {TracingEntity.AGENT, TracingEntity.ARTIFACT, TracingEntity.AGGREGATION, 100};
	
	/* Attributes */
	private static TracingEntityList tEL = new TracingEntityList();
	
	@Before
	public void setUp() throws Exception {
		
		int i;
		TracingEntity tE;
		for(i = 0; i < 6; i++) {
			tE = new TracingEntity(TYPES[0], AIDs[i%AIDs.length]);
			tEL.add(tE);
			assertEquals("The number "+i+" TracingEntity instance is not created correctly.", true, tEL.contains(tE));
		}
		for(int j = i; j < 9; j++) {
			tE = new TracingEntity(TYPES[j-i+1], AIDs[j%AIDs.length]);
			tEL.add(tE);
			assertEquals("The number "+j+" TracingEntity instance is not created correctly.", true, tEL.contains(tE));
		}
	}
	
	@After
	public void tearDown() throws Exception {
		
		tEL.clear();
	}
	
	/* Test methods */
	@Test
	public void testGetTEByAid0(){ assertNotNull(tEL.getTEByAid(AIDs[0])); }
	@Test
	public void testGetTEByAid1(){ assertNotNull(tEL.getTEByAid(AIDs[1])); }
	@Test
	public void testGetTEByAid2(){ assertNotNull(tEL.getTEByAid(AIDs[2])); }
	@Test
	public void testGetTEByAid3(){ assertNull(tEL.getTEByAid(new AgentID("Agent4"))); }
	@Test
	public void testGetTEByAid4(){ assertNull(tEL.getTEByAid(null)); }
}
