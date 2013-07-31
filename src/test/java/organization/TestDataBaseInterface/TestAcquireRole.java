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

public class TestAcquireRole extends TestCase {

	DataBaseInterface dbI = null;
	DatabaseAccess dbA = null;
	private Method m = null;
	
	
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		
		Class[] parameterTypes = new Class[3];
	    parameterTypes[0] = java.lang.String.class;
	    parameterTypes[1] = java.lang.String.class;
	    parameterTypes[2] = java.lang.String.class;
		
	    m = DataBaseInterface.class.getDeclaredMethod("acquireRole", parameterTypes);
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
	public void testAcquireRole1() {
		
		/**---------------------------------------------------------------------------------
		 * --			1.	
		 * --				- All parameters are correct
		 * --				- Agent exists in the system
		 * --				- Agent is acquired to role within unit
		 * ---------------------------------------------------------------------------------
		 */
		
		try {
			
			//------------------------------------------- Test Initialization  -----------------------------------------------//
			//Test variables
			String unit = "proofUnitFlat";
			String agentName = "proofAgent";
			String role = "proofRoleMember";
			String eRoleMember = "exampleRoleMember";
			String eRoleCreator = "exampleRoleCreator";
			int count1, count2;
			//Data Base 
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+ unit +"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ eRoleMember +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ eRoleCreator +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('"+ agentName +"')");
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList " +
					"WHERE agentName = '"+ agentName +"'),(SELECT idroleList FROM roleList WHERE (roleName = '"+ eRoleMember +"' AND " +
					"idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ role +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			//----------------------------------------------------------------------------------------------------------------//
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[3];
		    parameters[0] = unit;
		    parameters[1] = role;
		    parameters[2] = agentName;
			
		    String result = (String) m.invoke(dbI, parameters);
		    
		    count1 = dbA.countQuery("SELECT * FROM agentList");
		    count2 = dbA.countQuery("SELECT * FROM agentPlayList");
			
		    assertTrue(result.equals(role+ " acquired") && count1==1 && count2==2);

		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
	
	@Test
	public void testAcquireRole2() {
		
		/**---------------------------------------------------------------------------------
		 * --			2.	
		 * --				- All parameters are correct
		 * --				- Agent not exists in the system
		 * --				- Agent is acquired to role within unit
		 * ---------------------------------------------------------------------------------
		 */
		
		try {	
			
			//------------------------------------------- Test Initialization  -----------------------------------------------//
			//Test variables
			String unit = "proofUnitTeam";
			String agentName = "proofAgent";
			String role = "proofRoleMember";
			String eRoleMember = "exampleRoleMember";
			String eRoleCreator = "exampleRoleCreator";
			int count1, count2;
			//Data Base 
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+ unit +"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ eRoleMember +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ eRoleCreator +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ role +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			//----------------------------------------------------------------------------------------------------------------//
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[3];
		    parameters[0] = unit;
		    parameters[1] = role;
		    parameters[2] = agentName;
			
			String result = (String) m.invoke(dbI, parameters);
		    
		    count1 = dbA.countQuery("SELECT * FROM agentList");
		    count2 = dbA.countQuery("SELECT * FROM agentPlayList");
			
		    assertTrue(result.equals(role+ " acquired") && count1==1 && count2==1);

		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
	
	@Test
	public void testAcquireRole3() {
		
		/**---------------------------------------------------------------------------------
		 * --			3.	
		 * --				- Any parameters are incorrect
		 * --				- Agent exists in the system
		 * --				- Role for acquire not exists
		 * --				- Agent isn't acquired to role within unit
		 * ---------------------------------------------------------------------------------
		 */
		
		try {
			
			//------------------------------------------- Test Initialization  -----------------------------------------------//
			//Test variables
			String unit = "proofUnitFlat";
			String agentName = "proofAgent";
			String eRoleMember = "exampleRoleMember";
			String eRoleCreator = "exampleRoleCreator";
			//Data Base 
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+ unit +"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ eRoleMember +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ eRoleCreator +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('"+ agentName +"')");
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList " +
					"WHERE agentName = '"+ agentName +"'),(SELECT idroleList FROM roleList WHERE (roleName = '"+ eRoleMember +"' AND " +
					"idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))))");
			//----------------------------------------------------------------------------------------------------------------//
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[3];
		    parameters[0] = unit;
		    parameters[1] = "NotExits";
		    parameters[2] = agentName;
		    
		    String result = (String) m.invoke(dbI, parameters);
			fail(result);

		} catch(InvocationTargetException e) {
			
			assertTrue(e.getTargetException().getMessage(), e.getTargetException() instanceof MySQLException);
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
	
	@Test
	public void testAcquireRole4() {
		
		/**---------------------------------------------------------------------------------
		 * --			4.	
		 * --				- Any parameters are incorrect
		 * --				- Agent not exists in the system
		 * --				- Role for acquire not exists
		 * --				- Agent isn't acquired to role within unit
		 * ---------------------------------------------------------------------------------
		 */
		
		try {	
			
			//------------------------------------------- Test Initialization  -----------------------------------------------//
			//Test variables
			String unit = "proofUnitTeam";
			String agentName = "proofAgent";
			String eRoleMember = "exampleRoleMember";
			String eRoleCreator = "exampleRoleCreator";
			//Data Base 
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+ unit +"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ eRoleMember +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ eRoleCreator +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			//----------------------------------------------------------------------------------------------------------------//
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[3];
		    parameters[0] = unit;
		    parameters[1] = "NotExists";
		    parameters[2] = agentName;
			
		    String result = (String) m.invoke(dbI, parameters);
			fail(result);

		} catch(InvocationTargetException e) {
			
			assertTrue(e.getTargetException().getMessage(), e.getTargetException() instanceof MySQLException);
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
	
	@Test
	public void testAcquireRole5() {
		
		/**---------------------------------------------------------------------------------
		 * --			5.	
		 * --				- Any parameters are incorrect
		 * --				- Agent exists in the system
		 * --				- Unit for agent must acquire to role not exists
		 * --				- Agent isn't acquired to role within unit
		 * ---------------------------------------------------------------------------------
		 */
		
		try {
			
			//------------------------------------------- Test Initialization  -----------------------------------------------//
			//Test variables
			String agentName = "proofAgent";
			String role = "proofRoleMember";
			String eUnit = "exampleUnitFlat";
			String eRoleMember = "exampleRoleMember";
			String eRoleCreator = "exampleRoleCreator";
			//Data Base 
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+ eUnit +"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+ eUnit +"'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ eRoleMember +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ eUnit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ eRoleCreator +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ eUnit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('"+ agentName +"')");
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList " +
					"WHERE agentName = '"+ agentName +"'),(SELECT idroleList FROM roleList WHERE (roleName = '"+ eRoleMember +"' AND " +
					"idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+ eUnit +"'))))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ role +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ eUnit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			//----------------------------------------------------------------------------------------------------------------//
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[3];
		    parameters[0] = "NotExists";
		    parameters[1] = role;
		    parameters[2] = agentName;
			
		    String result = (String) m.invoke(dbI, parameters);
			fail(result);

		} catch(InvocationTargetException e) {
			
			assertTrue(e.getTargetException().getMessage(), e.getTargetException() instanceof MySQLException);
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
	
	@Test
	public void testAcquireRole6() {
		
		/**---------------------------------------------------------------------------------
		 * --			6.	
		 * --				- Any parameters are incorrect
		 * --				- Agent not exists in the system
		 * --				- Unit for agent must acquire to role not exists
		 * --				- Agent isn't acquired to role within unit
		 * ---------------------------------------------------------------------------------
		 */
		
		try {
			
			//------------------------------------------- Test Initialization  -----------------------------------------------//
			//Test variables
			String agentName = "proofAgent";
			String role = "proofRoleMember";
			String eUnit = "exampleUnitFlat";
			String eRoleMember = "exampleRoleMember";
			String eRoleCreator = "exampleRoleCreator";
			int count1, count2;
			//Data Base 
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+ eUnit +"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+ eUnit +"'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ eRoleMember +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ eUnit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ eRoleCreator +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ eUnit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			//----------------------------------------------------------------------------------------------------------------//
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[3];
		    parameters[0] = "NotExists";
		    parameters[1] = role;
		    parameters[2] = agentName;
			
		    String result = (String) m.invoke(dbI, parameters);
			fail(result);

		} catch(InvocationTargetException e) {
			
			assertTrue(e.getTargetException().getMessage(), e.getTargetException() instanceof MySQLException);
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
}	