package organization.TestDataBaseInterface;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.*;

import organization.TestDataBaseInterface.DatabaseAccess;
import es.upv.dsic.gti_ia.organization.DataBaseInterface;
import junit.framework.TestCase;


/** 
 * @author Jose Alemany Bordera  -  jalemany1@dsic.upv.es
 * 
 */

public class TestCheckValidIdentifier extends TestCase {

	DataBaseInterface dbI = null;
	DatabaseAccess dbA = null;
	private Method m = null;
	
	
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		
		Class[] parameterTypes = new Class[1];
	    parameterTypes[0] = java.lang.String.class;
		
	    m = DataBaseInterface.class.getDeclaredMethod("checkValidIdentifier", parameterTypes);
		m.setAccessible(true);
		
		dbA = new DatabaseAccess();

		//-------------  Clean Data Base  ------------//
		dbA.executeSQL("DELETE FROM agentPlayList");
		dbA.executeSQL("DELETE FROM agentList");
		dbA.executeSQL("DELETE FROM normList");
		dbA.executeSQL("DELETE FROM roleList WHERE idroleList != 1");
		dbA.executeSQL("DELETE FROM unitHierarchy WHERE idChildUnit != 1");
		dbA.executeSQL("DELETE FROM unitList WHERE idunitList != 1");

		//--------------------------------------------//
	}

	@After
	protected void tearDown() throws Exception {

		//-------------  Clean Data Base  ------------//
		dbA.executeSQL("DELETE FROM agentPlayList");
		dbA.executeSQL("DELETE FROM agentList");
		dbA.executeSQL("DELETE FROM normList");
		dbA.executeSQL("DELETE FROM roleList WHERE idroleList != 1");
		dbA.executeSQL("DELETE FROM unitHierarchy WHERE idChildUnit != 1");
		dbA.executeSQL("DELETE FROM unitList WHERE idunitList != 1");

		//--------------------------------------------//

		dbA = null;
		
		dbI = null;
		
		m = null;
	}
	
	@Test
	public void testCheckValidIdentifier1() {
		
		/**---------------------------------------------------------------------------------
		 * --			1.	
		 * --				- Identifier is valid
		 * --				- Check if identifier is a valid parameter
		 * ---------------------------------------------------------------------------------
		 */
		
		try {	
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[1];
			parameters[0] = "proofValidIdentifier55";
		    
			boolean result = (Boolean) m.invoke(dbI, parameters);
			assertTrue("Identifier isn't a valid parameter.", result);

		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
	
	@Test
	public void testCheckValidIdentifier2() {
		
		/**---------------------------------------------------------------------------------
		 * --			2.	
		 * --				- Identifier is valid
		 * --				- Check if identifier is a valid parameter
		 * ---------------------------------------------------------------------------------
		 */
		
		try {	
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[1];
			parameters[0] = "55";
		    
			boolean result = (Boolean) m.invoke(dbI, parameters);
			assertTrue("Identifier isn't a valid parameter.", result);

		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
	
	@Test
	public void testCheckValidIdentifier3() {
		
		/**---------------------------------------------------------------------------------
		 * --			3.	
		 * --				- Identifier is invalid
		 * --				- Check if identifier is a valid parameter
		 * ---------------------------------------------------------------------------------
		 */
		
		try {	
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[1];
			parameters[0] = "55proofValidIdentifier";
		    
			boolean result = (Boolean) m.invoke(dbI, parameters);
			assertFalse("Identifier is a valid parameter.", result);

		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
	
	@Test
	public void testCheckValidIdentifier4() {
		
		/**---------------------------------------------------------------------------------
		 * --			4.	
		 * --				- Identifier has special characters
		 * --				- Check if identifier is a valid parameter
		 * ---------------------------------------------------------------------------------
		 */
		
		try {	
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[1];
			parameters[0] = "example-Invalid-Identifier";
		    
			boolean result = (Boolean) m.invoke(dbI, parameters);
			assertFalse("Identifier is a valid parameter.", result);

		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
	
	@Test
	public void testCheckValidIdentifier5() {
		
		/**---------------------------------------------------------------------------------
		 * --			5.	
		 * --				- Identifier is a reserved word
		 * --				- Check if identifier is a valid parameter
		 * ---------------------------------------------------------------------------------
		 */
		
		try {	
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[1];
			parameters[0] = "FLAT";
		    
			boolean result = (Boolean) m.invoke(dbI, parameters);
			assertFalse("Identifier is a valid parameter.", result);

		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
}
