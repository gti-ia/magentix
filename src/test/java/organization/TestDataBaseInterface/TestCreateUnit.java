package organization.TestDataBaseInterface;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.*;

import organization.TestDataBaseInterface.DatabaseAccess;
import es.upv.dsic.gti_ia.organization.DataBaseInterface;
import es.upv.dsic.gti_ia.organization.exception.MySQLException;
import junit.framework.TestCase;


/** 
 * @author Jose Alemany Bordera  -  jalemany1@dsic.upv.es
 * 
 */

public class TestCreateUnit extends TestCase {

	DataBaseInterface dbI = null;
	DatabaseAccess dbA = null;
	private Method m = null;
	
	
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		
		Class[] parameterTypes = new Class[5];
	    parameterTypes[0] = java.lang.String.class;
	    parameterTypes[1] = java.lang.String.class;
	    parameterTypes[2] = java.lang.String.class;
	    parameterTypes[3] = java.lang.String.class;
	    parameterTypes[4] = java.lang.String.class;
		
	    m = DataBaseInterface.class.getDeclaredMethod("createUnit", parameterTypes);
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
	public void testCreateUnit1() {
		
		/**---------------------------------------------------------------------------------
		 * --			1.	
		 * --				- All parameters are correct
		 * --				- Unit will be created
		 * ---------------------------------------------------------------------------------
		 */
		
		try {	
			
			//------------------------------------------- Test Initialization  -----------------------------------------------//
			//Test variables
			String unit = "proofUnit";
			String unitType = "team";
			String parentUnit = "virtual";
			String agentName = "proofAgent";
			String creatorRole = "proofRoleCreator";
			int count1, count2, count3, count4;
			//Data Base 
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('"+ agentName +"')");
			//----------------------------------------------------------------------------------------------------------------//
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[5];
			parameters[0] = unit;
		    parameters[1] = unitType;
		    parameters[2] = parentUnit;
		    parameters[3] = agentName;
		    parameters[4] = creatorRole;
		    
		    String result = (String) m.invoke(dbI, parameters);
		    
		    count1 = dbA.countQuery("SELECT * FROM unitList");
		    count2 = dbA.countQuery("SELECT * FROM unitHierarchy");
		    count3 = dbA.countQuery("SELECT * FROM roleList");
		    count4 = dbA.countQuery("SELECT * FROM agentPlayList");
		    
			assertTrue(result.equals(unit+ " created") && count1==2 && count2==2 && count3==2 && count4==1);
			
		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
	
	@Test
	public void testCreateUnit2() {
		
		/**---------------------------------------------------------------------------------
		 * --			2.	
		 * --				- Any parameters are incorrect, less unitType field
		 * --				- UnitType field is invalid
		 * --				- Unit won't be created
		 * ---------------------------------------------------------------------------------
		 */
		
		try {	
			
			//------------------------------------------- Test Initialization  -----------------------------------------------//
			//Test variables
			String unit = "proofUnit";
			String parentUnit = "virtual";
			String agentName = "proofAgent";
			String creatorRole = "proofRoleCreator";
			//Data Base 
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('"+ agentName +"')");
			//----------------------------------------------------------------------------------------------------------------//
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[5];
			parameters[0] = unit;
		    parameters[1] = "NotExists";
		    parameters[2] = parentUnit;
		    parameters[3] = agentName;
		    parameters[4] = creatorRole;
		    
		    String result = (String) m.invoke(dbI, parameters);
			fail(result);

		} catch(InvocationTargetException e) {
			
			assertTrue(e.getTargetException().getMessage(), e.getTargetException() instanceof MySQLException);
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
	
	@Test
	public void testCreateUnit3() {
		
		/**---------------------------------------------------------------------------------
		 * --			3.	
		 * --				- Any parameters are incorrect
		 * --				- Parent unit doesn't exist
		 * --				- Unit won't be created
		 * ---------------------------------------------------------------------------------
		 */
		
		try {	
			
			//------------------------------------------- Test Initialization  -----------------------------------------------//
			//Test variables
			String unit = "proofUnit";
			String unitType = "team";
			String agentName = "proofAgent";
			String creatorRole = "proofRoleCreator";
			//Data Base 
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('"+ agentName +"')");
			//----------------------------------------------------------------------------------------------------------------//
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[5];
			parameters[0] = unit;
		    parameters[1] = unitType;
		    parameters[2] = "NotExists";
		    parameters[3] = agentName;
		    parameters[4] = creatorRole;
		    
		    String result = (String) m.invoke(dbI, parameters);
			fail(result);

		} catch(InvocationTargetException e) {
			
			assertTrue(e.getTargetException().getMessage(), e.getTargetException() instanceof MySQLException);
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
	
	@Test
	public void testCreateUnit4() {
		
		/**---------------------------------------------------------------------------------
		 * --			4.	
		 * --				- Any parameters are incorrect
		 * --				- Agent doesn't exist
		 * --				- Unit won't be created
		 * ---------------------------------------------------------------------------------
		 */
		
		try {	
			
			//------------------------------------------- Test Initialization  -----------------------------------------------//
			//Test variables
			String unit = "proofUnit";
			String unitType = "team";
			String parentUnit = "virtual";
			String creatorRole = "proofRoleCreator";
			//----------------------------------------------------------------------------------------------------------------//
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[5];
			parameters[0] = unit;
		    parameters[1] = unitType;
		    parameters[2] = parentUnit;
		    parameters[3] = "NotExists";
		    parameters[4] = creatorRole;
		    
		    String result = (String) m.invoke(dbI, parameters);
			fail(result);

		} catch(InvocationTargetException e) {
			
			assertTrue(e.getTargetException().getMessage(), e.getTargetException() instanceof MySQLException);
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
}
