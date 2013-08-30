package TestTrace;

import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import es.upv.dsic.gti_ia.trace.TraceError;

/**
 * Unit tests of class TraceError. All methods are tested twice, first with normal strings and second
 * with strings composed of a wide variety of unusual characters.
 * 
 * @author José Vicente Ruiz Cepeda (jruiz1@dsic.upv.es)
 *
 */
public class TestTraceError {

	/* Constants */
	private static final String[] NAMES = {"FAKE_TRACE_ERROR", "æßðŋ¶ħæððŋħøþßđłß€¶ßł#æ«»"};
	private static final String[] DESCRIPTIONS = {"Fake trace content goes here", "/ħæß}]«»ðłħææ=/(·Wæ¢ðıł¢$®º§ð’łæ§ðþıø£⅜™¢ŋ&łðħ æ¢↑øŋ"};
	
	/* Attributes */
	private static TraceError[] errors = {null, null};
	
	/* Set up class */
	@BeforeClass
	public static void setUpClass() throws Exception {
		for(int i = 0; i < errors.length; ++i) {
			errors[i] = new TraceError(NAMES[i], DESCRIPTIONS[i]);
		}
	}
	
	/* Test methods */
	@Test
	public void testTraceError0() { theTestOfTraceError(0); }
	@Test
	public void testTraceError1() { theTestOfTraceError(1); }
	public void theTestOfTraceError(int d) {
		assertNotNull(errors[d]);
	}
	
	@Test
	public void testGetName0(){ theTestOfGetName(0); }
	@Test
	public void testGetName1(){ theTestOfGetName(1); }
	public void theTestOfGetName(int d) {
		assertEquals(NAMES[d], errors[d].getName());
	}

	@Test
	public void testGetDescription0(){ theTestOfGetDescription(0); }
	@Test
	public void testGetDescription1(){ theTestOfGetDescription(1); }
	public void theTestOfGetDescription(int d){
		assertEquals(DESCRIPTIONS[d], errors[d].getDescription());
	}
}