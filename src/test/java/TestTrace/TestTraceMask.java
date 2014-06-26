package TestTrace;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import es.upv.dsic.gti_ia.trace.TraceMask;

/** 
 * @author Jose Alemany Bordera  -  jalemany1@dsic.upv.es
 * 
 */

public class TestTraceMask {
	
	/* Constants */
	private static final int[] VALID_INDEXES = {0,1,2,3,4,5,6,10,11,12};
	private static final boolean MASK_WITH_ANY_SERVICES_AVAILABLE = false;
	private static final boolean MASK_WITH_ALL_SERVICES_AVAILABLE = true;
	private static final String[] VALID_ENCODED_MASCKS = {"0000000100","1111111100","1111100100"};
	private static final String[] INVALID_ENCODED_MASCKS = {"FAKE_ENCODE_MASK", "æßðŋ¶ħæððŋ"};
	
	/* Attributes */
	private static TraceMask[] masks = {null, null, null};
	
	@Before
	public void setUp() throws Exception {
		
		for(int i = 0; i < masks.length; ++i) {
			switch(i){
			case 0:
				masks[i] = new TraceMask(MASK_WITH_ANY_SERVICES_AVAILABLE);
				break;
			case 1:
				masks[i] = new TraceMask(MASK_WITH_ALL_SERVICES_AVAILABLE);
				break;
			default:
				masks[i] = new TraceMask(VALID_ENCODED_MASCKS[i]);
			}
			assertNotNull("The number "+i+" TraceMask instance is not created correctly.", masks[i]);
		}
	}
	
	@After
	public void tearDown() throws Exception {
		
		for(int i = 0; i < masks.length; ++i) {
			masks[i] = null;
		}
	}
	
	/* Test methods */
	@Test (expected=ParseException.class, timeout = 5000)
	public void testTraceMaskError0() throws Exception { theTestOfTraceMaskError(0); }
	@Test(expected=ParseException.class, timeout = 5000)
	public void testTraceMaskError1() throws Exception { theTestOfTraceMaskError(1); }
	public void theTestOfTraceMaskError(int d) throws Exception {
		new TraceMask(INVALID_ENCODED_MASCKS[d]);
	}
	
	@Test (timeout = 5000)
	public void testGetBitIndex0(){ theTestOfGetName(0); }
	@Test (timeout = 5000)
	public void testGetBitIndex1(){ theTestOfGetName(1); }
	@Test (timeout = 5000)
	public void testGetBitIndex2(){ theTestOfGetName(2); }
	@Test(expected=IndexOutOfBoundsException.class, timeout = 5000)
	public void testGetBitIndex3(){ masks[2].get(-11); }
	@Test(expected=IndexOutOfBoundsException.class, timeout = 5000)
	public void testGetBitIndex4(){ masks[2].get(7); }
	public void theTestOfGetName(int d) {
		for(int i=0; i<VALID_INDEXES.length; i++){
			assertEquals(Character.getNumericValue(VALID_ENCODED_MASCKS[d].charAt(i))!=0, masks[d].get(VALID_INDEXES[i]));
		}
	}
	
	@Test (timeout = 5000)
	public void testSetBitIndex0() throws Exception { theTestOfSetBitIndex(0,3);  }
	@Test (timeout = 5000)
	public void testSetBitIndex1() throws Exception { theTestOfSetBitIndex(1,11); }
	@Test (timeout = 5000)
	public void testSetBitIndex2() throws Exception { theTestOfSetBitIndex(2,6); }
	@Test(expected=IndexOutOfBoundsException.class, timeout = 5000)
	public void testSetBitIndex3() throws Exception { theTestOfSetBitIndex(0,-11); }
	@Test(expected=IndexOutOfBoundsException.class, timeout = 5000)
	public void testSetBitIndex4() throws Exception { theTestOfSetBitIndex(1,7); }
	public void theTestOfSetBitIndex(int d, int n) throws Exception {
		Class[] parameterTypes = new Class[1];
	    parameterTypes[0] = int.class;
	    
	    Method m = TraceMask.class.getDeclaredMethod("set", parameterTypes);
		m.setAccessible(true);
				
		Object[] parameters = new Object[1];
		parameters[0] = n;
		
		try {
			m.invoke(masks[d], parameters);
		} catch(InvocationTargetException e) {
			throw (Exception) e.getCause();
	    }
		
		assertTrue(masks[d].get(n));
	}
	
	@Test (timeout = 5000)
	public void testSetBitIndexAndValue0() throws Exception { theTestOfSetBitIndexAndValue(0,3,false); assertFalse(masks[0].get(3)); }
	@Test (timeout = 5000)
	public void testSetBitIndexAndValue1() throws Exception { theTestOfSetBitIndexAndValue(1,11,false); assertFalse(masks[1].get(11)); }
	@Test (timeout = 5000)
	public void testSetBitIndexAndValue2() throws Exception { theTestOfSetBitIndexAndValue(2,6,false); assertFalse(masks[2].get(6)); }
	@Test(expected=IndexOutOfBoundsException.class, timeout = 5000)
	public void testSetBitIndexAndValue3() throws Exception { theTestOfSetBitIndexAndValue(0,-11,false); }
	@Test(expected=IndexOutOfBoundsException.class, timeout = 5000)
	public void testSetBitIndexAndValue4() throws Exception { theTestOfSetBitIndexAndValue(1,7,false); }
	public void theTestOfSetBitIndexAndValue(int d, int n1, boolean n2) throws Exception {
		Class[] parameterTypes = new Class[2];
	    parameterTypes[0] = int.class;
	    parameterTypes[1] = boolean.class;
	    
	    Method m = TraceMask.class.getDeclaredMethod("set", parameterTypes);
		m.setAccessible(true);
				
		Object[] parameters = new Object[2];
		parameters[0] = n1;
		parameters[1] = n2;
		
		try {
			m.invoke(masks[d], parameters);
		} catch(InvocationTargetException e) {
			throw (Exception) e.getCause();
	    }
	}
	
	@Test (timeout = 5000)
	public void testLength0() throws Exception { theTestOfLength(0); }
	@Test (timeout = 5000)
	public void testLength1() throws Exception { theTestOfLength(1); }
	@Test (timeout = 5000)
	public void testLength2() throws Exception { theTestOfLength(2); }
	public void theTestOfLength(int d) throws Exception {
		assertEquals(VALID_INDEXES.length, masks[d].length());
	}
	
	@Test (timeout = 5000)
	public void testIsTraceAvailable0() throws Exception { theTestOfIsTraceAvailable(0,false); }
	@Test (timeout = 5000)
	public void testIsTraceAvailable1() throws Exception { theTestOfIsTraceAvailable(1,true); }
	@Test (timeout = 5000)
	public void testIsTraceAvailable2() throws Exception { theTestOfIsTraceAvailable(2,true); }
	public void theTestOfIsTraceAvailable(int d, boolean l) throws Exception {
		assertEquals(l, masks[d].isTraceAvailable());
	}
	
	@Test (timeout = 5000)
	public void testToString0() throws Exception { theTestOfToString(0); }
	@Test (timeout = 5000)
	public void testToString1() throws Exception { theTestOfToString(1); }
	@Test (timeout = 5000)
	public void testToString2() throws Exception { theTestOfToString(2); }
	public void theTestOfToString(int d) throws Exception {
		assertEquals(VALID_ENCODED_MASCKS[d], masks[d].toString());
	}
	
	@Test (timeout = 5000)
	public void testEquals0() throws Exception { theTestOfEquals(0,new TraceMask("0000000100"),true); }
	@Test (timeout = 5000)
	public void testEquals1() throws Exception { theTestOfEquals(1,new TraceMask("0000000100"),false); }
	@Test (timeout = 5000)
	public void testEquals2() throws Exception { theTestOfEquals(2,new Object(),false); }
	public void theTestOfEquals(int d, Object o, boolean res) throws Exception {
		assertEquals(res, masks[d].equals(o));
	}
	
	@Test (timeout = 5000)
	public void testClone0() throws Exception { theTestOfClone(0); }
	@Test (timeout = 5000)
	public void testClone1() throws Exception { theTestOfClone(1); }
	@Test (timeout = 5000)
	public void testClone2() throws Exception { theTestOfClone(2); }
	public void theTestOfClone(int d) throws Exception {
		assertTrue(masks[d].equals(masks[d].clone()));
	}
}
