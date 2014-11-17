package TestCore;

import java.util.Date;

import com.ibm.icu.util.Calendar;

import org.junit.Test;

import static org.junit.Assert.*;
import es.upv.dsic.gti_ia.core.ISO8601;

/**
 * Tests for the static methods of ISO8601 class
 * 
 * @author David Fernández - dfernandez@dsic.upv.es
 */

public class TestISO8601 {
	
	/**
	 * Testing method toDate(String)
	 * 
	 * With an UTC Date format
	 * 
	 */
	@Test(timeout=50000)
	public void testToDateUTC(){
		String utcStringDate = "20130730T191940000Z"; //000 = milliseconds
		

		com.ibm.icu.util.TimeZone timeZone = com.ibm.icu.util.TimeZone.getTimeZone("GMT");
		Calendar c = Calendar.getInstance(timeZone);
		c.set(2013, 6, 30, 19, 19, 40); //Months go from 0 to 11
		Date javaDate = c.getTime();
		
		Date parsedJavaDate;
		
		try {
			parsedJavaDate = ISO8601.toDate(utcStringDate);
			assertEquals(javaDate.toString(), parsedJavaDate.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}
	
	/**
	 * Testing method toDate(String)
	 * 
	 * With an local Date format
	 * 
	 */
	@Test(timeout=50000)
	public void testToDateLocal(){
		String localStringDate = "20130730T191940000"; //000 = milliseconds
		
		Calendar c = Calendar.getInstance();
		c.set(2013, 6, 30, 19, 19, 40); //Months go from 0 to 11
		Date javaDate = c.getTime();
		
		Date parsedJavaDate;
		
		try {
			parsedJavaDate = ISO8601.toDate(localStringDate);
			assertEquals(javaDate.toString(), parsedJavaDate.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}
	
	/**
	 * Testing method toDate(String)
	 * 
	 * With a NULL date
	 * 
	 */
	@Test(timeout=50000)
	public void testToDateNULL(){
		String utcStringDate = null;
		
		Date javaDate = new Date();		
		Date parsedJavaDate;
		
		try {
			parsedJavaDate = ISO8601.toDate(utcStringDate);
			assertTrue( Math.abs(javaDate.getTime() - parsedJavaDate.getTime()) < 10000 );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}
	
	/**
	 * Testing method toDate(String)
	 * 
	 * With a relative date
	 * Test with negative and positive relative date
	 * 
	 */
	@Test(timeout=50000)
	public void testToDateRelative(){
		//Test with positive relative date
		String relativeStringDate = "+00000000T010000000"; //1 hour from now
		

		Date javaDate = new Date(System.currentTimeMillis() + 3600000); //Current time in miliseconds plus 1 hour
		
		Date parsedJavaDate;
		
		try {
			parsedJavaDate = ISO8601.toDate(relativeStringDate);
			assertTrue( Math.abs(javaDate.getTime() - parsedJavaDate.getTime()) < 10000 );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
		
		//Test with negative relative date		
		relativeStringDate = "-00000000T010000000"; //1 hour before now
		

		javaDate = new Date(System.currentTimeMillis() - 3600000); //Current time in miliseconds plus 1 hour
		
		
		try {
			parsedJavaDate = ISO8601.toDate(relativeStringDate);
			assertTrue( Math.abs(javaDate.getTime() - parsedJavaDate.getTime()) < 10000 );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}
	
	/**
	 * Testing method toString(Date, bool)
	 * 
	 * Date to UTC String
	 * 
	 */
	@Test(timeout=50000)
	public void testUTCToString(){
		String utcStringDate = "20130730T191940000Z"; //000 = milliseconds
		

		com.ibm.icu.util.TimeZone timeZone = com.ibm.icu.util.TimeZone.getTimeZone("GMT");
		Calendar c = Calendar.getInstance(timeZone);
		c.set(2013, 6, 30, 19, 19, 40); //Months go from 0 to 11
		
		Date parsedJavaDate;
		
		try {
			parsedJavaDate = ISO8601.toDate(utcStringDate);
			String parsedUTCString = ISO8601.toString(parsedJavaDate, true);
			
			assertEquals(parsedUTCString, utcStringDate);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}
	
	/**
	 * Testing method toString(Date, bool)
	 * 
	 * Date to local String
	 * 
	 */
	@Test(timeout=50000)
	public void testLocalToString(){
		String localStringDate = "20130730T191940000"; //000 = milliseconds
		
		Date parsedJavaDate;
		
		try {
			parsedJavaDate = ISO8601.toDate(localStringDate);
			String parsedLocalString = ISO8601.toString(parsedJavaDate, false);
			
			assertEquals(parsedLocalString, localStringDate);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}
	
	/**
	 * Testing method toString(Date)
	 * 
	 * Same as toString(Date, bool) but in this case
	 * date format can not be chosen, is always UTC
	 * 
	 */
	@Test(timeout=50000)
	public void testToString(){
		String utcStringDate = "20130730T191940000Z"; //000 = milliseconds
		

		com.ibm.icu.util.TimeZone timeZone = com.ibm.icu.util.TimeZone.getTimeZone("GMT");
		Calendar c = Calendar.getInstance(timeZone);
		c.set(2013, 6, 30, 19, 19, 40); //Months go from 0 to 11
		
		Date parsedJavaDate;
		
		try {
			parsedJavaDate = ISO8601.toDate(utcStringDate);
			String parsedUTCString = ISO8601.toString(parsedJavaDate);
			
			assertEquals(parsedUTCString, utcStringDate);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}	
	
	/**
	 * Testing method toRelativeTimeString(long)
	 * 
	 * Test with negative and positive milliseconds
	 * 
	 */
	@Test(timeout=50000)
	public void testToRelativeTimeString(){
		//Test with positive milliseconds
		String relativeStringDate = "+00000000T010000000"; //1 hour from now
		
		String parsedJavaDate;
		
		try {
			//Plus 1 hour in milliseconds 
			parsedJavaDate = ISO8601.toRelativeTimeString(3600000);
			
			assertEquals(relativeStringDate, parsedJavaDate);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
		
		//Test with negative milliseconds
		relativeStringDate = "-00000000T010000000"; //1 hour from now
		
		try {
			//Plus 1 hour in milliseconds 
			parsedJavaDate = ISO8601.toRelativeTimeString(-3600000);
			
			assertEquals(relativeStringDate, parsedJavaDate);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}
	
}