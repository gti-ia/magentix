package TestTrace;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import es.upv.dsic.gti_ia.core.TracingService;
import es.upv.dsic.gti_ia.trace.TracingServiceList;

/** 
 * @author Jose Alemany Bordera  -  jalemany1@dsic.upv.es
 * 
 */

public class TestTracingServiceList {

	/* Constants */
	private static final String[] NOT_VALID_SERVICE_NAME = {"trace_error","FAKE_SERVICE_NAME","æßðŋ¶ħæððŋħøþßđłß€¶ßł#æ«»"};
	
	/* Attributes */
	private static TracingServiceList tSL = new TracingServiceList();
	private static final String[] VALID_SERVICE_NAME = new String[TracingService.MAX_DI_TS];
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		
		for (int i = 0; i < TracingService.MAX_DI_TS; i++)
			VALID_SERVICE_NAME[i] = TracingService.DI_TracingServices[i].getName();
		assertTrue(tSL.initializeWithDITracingServices());
	}
	
	@AfterClass
	public static void tearDownClass() throws Exception {
		
		tSL.clear();
	}
	
	/* Test methods */
	@Test (timeout = 5000)
	public void testGetTS0(){ theTestOfGetTS(0); }
	@Test (timeout = 5000)
	public void testGetTS1(){ theTestOfGetTS(1); }
	public void theTestOfGetTS(int d) {
		
		TracingService tS;
		if (d==0)
			for (int i = 0; i < VALID_SERVICE_NAME.length; i++){
				tS = tSL.getTS(VALID_SERVICE_NAME[i]);
				assertEquals(true, tS instanceof TracingService);
				assertEquals(true, tSL.contains(tS));
			}
		else
			for (int i = 0; i < NOT_VALID_SERVICE_NAME.length; i++)
				assertEquals(null, tSL.getTS(NOT_VALID_SERVICE_NAME[i]));
	}
}
