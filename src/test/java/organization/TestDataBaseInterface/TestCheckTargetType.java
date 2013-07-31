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

public class TestCheckTargetType extends TestCase {

	DataBaseInterface dbI = null;
	DatabaseAccess dbA = null;
	private Method m = null;
	
	
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		
		Class[] parameterTypes = new Class[1];
	    parameterTypes[0] = java.lang.String.class;
		
	    m = DataBaseInterface.class.getDeclaredMethod("checkTargetType", parameterTypes);
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
	public void testCheckTargetType1() {
		
		/**---------------------------------------------------------------------------------
		 * --			1.	
		 * --				- All parameters are correct
		 * --				- TargetName = 'agentName'
		 * --				- Check if targetName is valid
		 * ---------------------------------------------------------------------------------
		 */
		
		try {	
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[1];
		    parameters[0] = "agentName";
			
			boolean result = (Boolean) m.invoke(dbI, parameters);
			assertTrue("TargetType is invalid.", result);

		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
	
	@Test
	public void testCheckTargetType2() {
		
		/**---------------------------------------------------------------------------------
		 * --			2.	
		 * --				- All parameters are correct
		 * --				- TargetName = 'roleName'
		 * --				- Check if targetName is valid
		 * ---------------------------------------------------------------------------------
		 */
		
		try {	
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[1];
		    parameters[0] = "roleName";
			
			boolean result = (Boolean) m.invoke(dbI, parameters);
			assertTrue("TargetType is invalid.", result);

		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
	
	@Test
	public void testCheckTargetType3() {
		
		/**---------------------------------------------------------------------------------
		 * --			3.	
		 * --				- All parameters are correct
		 * --				- TargetName = 'positionName'
		 * --				- Check if targetName is valid
		 * ---------------------------------------------------------------------------------
		 */
		
		try {	
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[1];
		    parameters[0] = "positionName";
			
			boolean result = (Boolean) m.invoke(dbI, parameters);
			assertTrue("TargetType is invalid.", result);

		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
	
	@Test
	public void testCheckTargetType4() {
		
		/**---------------------------------------------------------------------------------
		 * --			4.	
		 * --				- All parameters are correct
		 * --				- TargetName is invalid
		 * --				- Check if targetName is valid
		 * ---------------------------------------------------------------------------------
		 */
		
		try {	
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[1];
		    parameters[0] = "NotExists";
			
			boolean result = (Boolean) m.invoke(dbI, parameters);
			assertFalse("TargetType is valid.", result);

		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
}
