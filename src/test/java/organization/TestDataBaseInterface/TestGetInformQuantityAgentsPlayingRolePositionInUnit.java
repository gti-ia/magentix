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

public class TestGetInformQuantityAgentsPlayingRolePositionInUnit extends TestCase {

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
		
	    m = DataBaseInterface.class.getDeclaredMethod("getInformQuantityAgentsPlayingRolePositionInUnit", parameterTypes);
		m.setAccessible(true);
		
		dbA = new DatabaseAccess();

		//-------------  Clean Data Base  ------------//
		dbA.executeSQL("DELETE FROM agentPlayList");
		dbA.executeSQL("DELETE FROM agentList");
		dbA.executeSQL("DELETE FROM actionNormParam");
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
		dbA.executeSQL("DELETE FROM actionNormParam");
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
	public void testGetInformQuantityAgentsPlayingRolePositionInUnit1() {
		
		/**---------------------------------------------------------------------------------------------
		 * --			1.	
		 * --				- All parameters are correct
		 * --				- Unit has several roles and 2 agents playing role with
		 * --				  position field equal to member
		 * --				- Show correctly the information
		 * ---------------------------------------------------------------------------------------------
		 */
		
		try {	
			
			//------------------------------------------- Test Initialization  -----------------------------------------------//
			//Test variables
			String unit = "proofUnitTeam";
			String role = "proofRoleMember";
			String position = "member";
			String eAgent = "exampleAgent";
			String eAgent2 = "exampleAgent2";
			String eRoleCreator = "exampleRoleCreator";
			//Data Base 
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+ unit +"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ role +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ eRoleCreator +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('"+ eAgent +"')");
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList " +
					"WHERE agentName = '"+ eAgent +"'),(SELECT idroleList FROM roleList WHERE (roleName = '"+ role +"' AND " +
					"idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))))");
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList " +
					"WHERE agentName = '"+ eAgent +"'),(SELECT idroleList FROM roleList WHERE (roleName = '"+ eRoleCreator +"' AND " +
					"idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))))");
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('"+ eAgent2 +"')");
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList " +
					"WHERE agentName = '"+ eAgent2 +"'),(SELECT idroleList FROM roleList WHERE (roleName = '"+ role +"' AND " +
					"idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))))");
			//----------------------------------------------------------------------------------------------------------------//
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[3];
			parameters[0] = unit;
			parameters[1] = role;
			parameters[2] = position;
		    
			int result = (Integer) m.invoke(dbI, parameters);
			assertEquals("The message should be:", 2, result);

		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
	
	@Test
	public void testGetInformQuantityAgentsPlayingRolePositionInUnit2() {
		
		/**---------------------------------------------------------------------------------------------
		 * --			2.	
		 * --				- All parameters are correct
		 * --				- Unit has several roles and 2 agents playing role with
		 * --				  position field equal to creator
		 * --				- Show correctly the information
		 * ---------------------------------------------------------------------------------------------
		 */
		
		try {	
			
			//------------------------------------------- Test Initialization  -----------------------------------------------//
			//Test variables
			String unit = "proofUnitTeam";
			String role = "proofRoleMember";
			String position = "creator";
			String eAgent = "exampleAgent";
			String eAgent2 = "exampleAgent2";
			String eRoleCreator = "exampleRoleCreator";
			//Data Base 
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+ unit +"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ role +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ eRoleCreator +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('"+ eAgent +"')");
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList " +
					"WHERE agentName = '"+ eAgent +"'),(SELECT idroleList FROM roleList WHERE (roleName = '"+ role +"' AND " +
					"idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))))");
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('"+ eAgent2 +"')");
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList " +
					"WHERE agentName = '"+ eAgent2 +"'),(SELECT idroleList FROM roleList WHERE (roleName = '"+ role +"' AND " +
					"idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))))");
			//----------------------------------------------------------------------------------------------------------------//
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[3];
			parameters[0] = unit;
			parameters[1] = role;
			parameters[2] = position;
		    
			int result = (Integer) m.invoke(dbI, parameters);
			assertEquals("The message should be:", 0, result);

		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
	
	@Test
	public void testGetInformQuantityAgentsPlayingRolePositionInUnit3() {
		
		/**---------------------------------------------------------------------------------------------
		 * --			3.	
		 * --				- Any parameters are incorrect
		 * --				- Unit doesn't exist
		 * --				- Not return information
		 * ---------------------------------------------------------------------------------------------
		 */
		
		try {	
			
			//------------------------------------------- Test Initialization  -----------------------------------------------//
			//Test variables
			String role = "proofRoleMember";
			String position = "creator";
			String eUnit = "exampleUnitTeam";
			String eAgent = "exampleAgent";
			String eAgent2 = "exampleAgent2";
			String eRoleCreator = "exampleRoleCreator";
			//Data Base 
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+ eUnit +"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+ eUnit +"'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ role +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ eUnit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ eRoleCreator +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ eUnit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('"+ eAgent +"')");
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList " +
					"WHERE agentName = '"+ eAgent +"'),(SELECT idroleList FROM roleList WHERE (roleName = '"+ role +"' AND " +
					"idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+ eUnit +"'))))");
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('"+ eAgent2 +"')");
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList " +
					"WHERE agentName = '"+ eAgent2 +"'),(SELECT idroleList FROM roleList WHERE (roleName = '"+ role +"' AND " +
					"idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+ eUnit +"'))))");
			//----------------------------------------------------------------------------------------------------------------//
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[3];
			parameters[0] = "NotExists";
			parameters[1] = role;
			parameters[2] = position;
		    
			int result = (Integer) m.invoke(dbI, parameters);
			assertEquals("The message should be:", 0, result);

		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
	
	@Test
	public void testGetInformQuantityAgentsPlayingRolePositionInUnit4() {
		
		/**---------------------------------------------------------------------------------------------
		 * --			4.	
		 * --				- Any parameters are incorrect
		 * --				- Role doesn't exist
		 * --				- Not return information
		 * ---------------------------------------------------------------------------------------------
		 */
		
		try {	
			
			//------------------------------------------- Test Initialization  -----------------------------------------------//
			//Test variables
			String unit = "proofUnitTeam";
			String position = "creator";
			String eRole = "exampleRoleMember";
			String eAgent = "exampleAgent";
			String eAgent2 = "exampleAgent2";
			String eRoleCreator = "exampleRoleCreator";
			//Data Base 
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+ unit +"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ eRole +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ eRoleCreator +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('"+ eAgent +"')");
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList " +
					"WHERE agentName = '"+ eAgent +"'),(SELECT idroleList FROM roleList WHERE (roleName = '"+ eRole +"' AND " +
					"idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))))");
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('"+ eAgent2 +"')");
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList " +
					"WHERE agentName = '"+ eAgent2 +"'),(SELECT idroleList FROM roleList WHERE (roleName = '"+ eRole +"' AND " +
					"idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))))");
			//----------------------------------------------------------------------------------------------------------------//
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[3];
			parameters[0] = unit;
			parameters[1] = "NotExists";
			parameters[2] = position;
		    
			int result = (Integer) m.invoke(dbI, parameters);
			assertEquals("The message should be:", 0, result);

		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
	
	@Test
	public void testGetInformQuantityAgentsPlayingRolePositionInUnit5() {
		
		/**---------------------------------------------------------------------------------------------
		 * --			5.	
		 * --				- Any parameters are incorrect
		 * --				- Position is invalid
		 * --				- Not return information
		 * ---------------------------------------------------------------------------------------------
		 */
		
		try {	
			
			//------------------------------------------- Test Initialization  -----------------------------------------------//
			//Test variables
			String unit = "proofUnitTeam";
			String role = "proofRoleMember";
			String eAgent = "exampleAgent";
			String eAgent2 = "exampleAgent2";
			String eRoleCreator = "exampleRoleCreator";
			//Data Base 
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+ unit +"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ role +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ eRoleCreator +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('"+ eAgent +"')");
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList " +
					"WHERE agentName = '"+ eAgent +"'),(SELECT idroleList FROM roleList WHERE (roleName = '"+ role +"' AND " +
					"idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))))");
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('"+ eAgent2 +"')");
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList " +
					"WHERE agentName = '"+ eAgent2 +"'),(SELECT idroleList FROM roleList WHERE (roleName = '"+ role +"' AND " +
					"idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))))");
			//----------------------------------------------------------------------------------------------------------------//
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[3];
			parameters[0] = unit;
			parameters[1] = role;
			parameters[2] = "NotExists";
		    
			int result = (Integer) m.invoke(dbI, parameters);
			assertEquals("The message should be:", 0, result);

		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
}
