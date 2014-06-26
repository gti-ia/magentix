package organization.TestDataBaseInterface;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.junit.*;

import organization.TestDataBaseInterface.DatabaseAccess;
import es.upv.dsic.gti_ia.organization.DataBaseInterface;


/** 
 * @author Jose Alemany Bordera  -  jalemany1@dsic.upv.es
 * 
 */

public class TestGetRoleNormRules {

	DataBaseInterface dbI = null;
	DatabaseAccess dbA = null;
	private Method m = null;
	
	
	@Before
	public void setUp() throws Exception {
		
		Class[] parameterTypes = new Class[4];
	    parameterTypes[0] = java.lang.String.class;
	    parameterTypes[1] = java.lang.String.class;
	    parameterTypes[2] = java.lang.String.class;
	    parameterTypes[3] = java.lang.String.class;
		
	    m = DataBaseInterface.class.getDeclaredMethod("getRoleNormRules", parameterTypes);
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
	public void tearDown() throws Exception {

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
	
	@Test(timeout = 5 * 1000)
	public void testGetRoleNormRules1() {
		
		/**---------------------------------------------------------------------------------
		 * --			1.	
		 * --				- All parameters are correct
		 * --				- Norm is associated to any role with deontic and 
		 * --				  service field equal to method parameters
		 * --				- Show correctly the information
		 * ---------------------------------------------------------------------------------
		 */
		
		try {	
			
			//------------------------------------------- Test Initialization  -----------------------------------------------//
			//Test variables
			String unit = "proofUnitFlat";
			String agentName = "proofAgent";
			String deontic = "p";
			String service = "registerUnit";
			String eNorm = "exampleNorm";
			String eNormRule = "exampleNormRule";
			String eRoleCreator = "exampleRoleCreator";
			//Data Base 
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+ unit +"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ eRoleCreator +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('"+ agentName +"')");
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList " +
					"WHERE agentName = '"+ agentName +"'),(SELECT idroleList FROM roleList WHERE (roleName = '"+ eRoleCreator +"' AND " +
					"idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))))");
			
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
					"VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),'"+ eNorm +"', (SELECT iddeontic FROM deontic WHERE deonticdesc = '"+ deontic +"'), (SELECT idtargetType FROM " +
					"targetType WHERE targetName = 'roleName'), -1, (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), '', '"+ eNormRule +"')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleUnitFlat')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'flat')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'virtual')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleAgent')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleRoleCreator2')");
			//----------------------------------------------------------------------------------------------------------------//
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[4];
			parameters[0] = unit;
		    parameters[1] = agentName;
		    parameters[2] = deontic;
		    parameters[3] = service;
		    
		    ArrayList<String> result = (ArrayList<String>) m.invoke(dbI, parameters);
			assertEquals("The message should be:", "["+ eNormRule +"]", result.toString());

		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
	
	@Test(timeout = 5 * 1000)
	public void testGetRoleNormRules2() {
		
		/**---------------------------------------------------------------------------------
		 * --			2.	
		 * --				- All parameters are correct
		 * --				- Norm is associated to role played by agent that is passed 
		 * --				  as parameter
		 * --				- Show correctly the information
		 * ---------------------------------------------------------------------------------
		 */
		
		try {	
			
			//------------------------------------------- Test Initialization  -----------------------------------------------//
			//Test variables
			String unit = "proofUnitFlat";
			String agentName = "proofAgent";
			String deontic = "p";
			String service = "registerUnit";
			String eNorm = "exampleNorm";
			String eNormRule = "exampleNormRule";
			String eRoleCreator = "exampleRoleCreator";
			//Data Base 
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+ unit +"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ eRoleCreator +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('"+ agentName +"')");
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList " +
					"WHERE agentName = '"+ agentName +"'),(SELECT idroleList FROM roleList WHERE (roleName = '"+ eRoleCreator +"' AND " +
					"idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))))");
			
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
					"VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),'"+ eNorm +"', (SELECT iddeontic FROM deontic WHERE deonticdesc = '"+ deontic +"'), (SELECT idtargetType FROM " +
					"targetType WHERE targetName = 'roleName'),(SELECT idroleList FROM roleList WHERE roleName = '"+ eRoleCreator +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), '', '"+ eNormRule +"')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleUnitFlat')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'flat')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'virtual')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleAgent')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleRoleCreator2')");
			//----------------------------------------------------------------------------------------------------------------//
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[4];
			parameters[0] = unit;
		    parameters[1] = agentName;
		    parameters[2] = deontic;
		    parameters[3] = service;
		    
		    ArrayList<String> result = (ArrayList<String>) m.invoke(dbI, parameters);
			assertEquals("The message should be:", "["+ eNormRule +"]", result.toString());

		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
	
	@Test(timeout = 5 * 1000)
	public void testGetRoleNormRules3() {
		
		/**---------------------------------------------------------------------------------
		 * --			3.	
		 * --				- All parameters are correct
		 * --				- Norm is associated to any role with deontic
		 * --				  field equal to method parameters, but not service
		 * --				- Show correctly the information
		 * ---------------------------------------------------------------------------------
		 */
		
		try {	
			
			//------------------------------------------- Test Initialization  -----------------------------------------------//
			//Test variables
			String unit = "proofUnitFlat";
			String agentName = "proofAgent";
			String deontic = "p";
			String service = "registerUnit";
			String eNorm = "exampleNorm";
			String eNormRule = "exampleNormRule";
			String eRoleCreator = "exampleRoleCreator";
			//Data Base 
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+ unit +"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ eRoleCreator +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('"+ agentName +"')");
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList " +
					"WHERE agentName = '"+ agentName +"'),(SELECT idroleList FROM roleList WHERE (roleName = '"+ eRoleCreator +"' AND " +
					"idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))))");
			
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
					"VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),'"+ eNorm +"', (SELECT iddeontic FROM deontic WHERE deonticdesc = '"+ deontic +"'), (SELECT idtargetType FROM " +
					"targetType WHERE targetName = 'roleName'), -1, (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), '', '"+ eNormRule +"')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleUnitFlat')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'flat')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'virtual')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleAgent')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleRoleCreator2')");
			//----------------------------------------------------------------------------------------------------------------//
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[4];
			parameters[0] = unit;
		    parameters[1] = agentName;
		    parameters[2] = deontic;
		    parameters[3] = "registerRole";
		    
		    ArrayList<String> result = (ArrayList<String>) m.invoke(dbI, parameters);
			assertEquals("The message should be:", "[]", result.toString());

		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
	
	@Test(timeout = 5 * 1000)
	public void testGetRoleNormRules4() {
		
		/**---------------------------------------------------------------------------------
		 * --			4.	
		 * --				- All parameters are correct
		 * --				- Norm is associated to any role with service field 
		 * --				  equal to method parameters, but not deontic
		 * --				- Show correctly the information
		 * ---------------------------------------------------------------------------------
		 */
		
		try {	
			
			//------------------------------------------- Test Initialization  -----------------------------------------------//
			//Test variables
			String unit = "proofUnitFlat";
			String agentName = "proofAgent";
			String deontic = "p";
			String service = "registerUnit";
			String eNorm = "exampleNorm";
			String eNormRule = "exampleNormRule";
			String eRoleCreator = "exampleRoleCreator";
			//Data Base 
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+ unit +"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ eRoleCreator +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('"+ agentName +"')");
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList " +
					"WHERE agentName = '"+ agentName +"'),(SELECT idroleList FROM roleList WHERE (roleName = '"+ eRoleCreator +"' AND " +
					"idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))))");
			
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
					"VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),'"+ eNorm +"', (SELECT iddeontic FROM deontic WHERE deonticdesc = '"+ deontic +"'), (SELECT idtargetType FROM " +
					"targetType WHERE targetName = 'roleName'), -1, (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), '', '"+ eNormRule +"')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleUnitFlat')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'flat')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'virtual')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleAgent')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleRoleCreator2')");
			//----------------------------------------------------------------------------------------------------------------//
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[4];
			parameters[0] = unit;
		    parameters[1] = agentName;
		    parameters[2] = "f";
		    parameters[3] = service;
		    
		    ArrayList<String> result = (ArrayList<String>) m.invoke(dbI, parameters);
			assertEquals("The message should be:", "[]", result.toString());

		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
	
	@Test(timeout = 5 * 1000)
	public void testGetRoleNormRules5() {
		
		/**---------------------------------------------------------------------------------
		 * --			5.	
		 * --				- All parameters are correct
		 * --				- Norm is associated to role don't played by agent that 
		 * --				  is passed as parameter 
		 * --				- Show correctly the information
		 * ---------------------------------------------------------------------------------
		 */
		
		try {	
			
			//------------------------------------------- Test Initialization  -----------------------------------------------//
			//Test variables
			String unit = "proofUnitFlat";
			String agentName = "proofAgent";
			String deontic = "p";
			String service = "registerUnit";
			String eNorm = "exampleNorm";
			String eNormRule = "exampleNormRule";
			String eRoleMember = "exampleRoleMember";
			String eRoleCreator = "exampleRoleCreator";
			//Data Base 
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+ unit +"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ eRoleCreator +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ eRoleMember +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('"+ agentName +"')");
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList " +
					"WHERE agentName = '"+ agentName +"'),(SELECT idroleList FROM roleList WHERE (roleName = '"+ eRoleCreator +"' AND " +
					"idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))))");
			
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
					"VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),'"+ eNorm +"', (SELECT iddeontic FROM deontic WHERE deonticdesc = '"+ deontic +"'), (SELECT idtargetType FROM " +
					"targetType WHERE targetName = 'roleName'),(SELECT idroleList FROM roleList WHERE roleName = '"+ eRoleMember +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), '', '"+ eNormRule +"')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleUnitFlat')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'flat')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'virtual')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleAgent')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleRoleCreator2')");
			//----------------------------------------------------------------------------------------------------------------//
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[4];
			parameters[0] = unit;
		    parameters[1] = agentName;
		    parameters[2] = deontic;
		    parameters[3] = service;
		    
		    ArrayList<String> result = (ArrayList<String>) m.invoke(dbI, parameters);
			assertEquals("The message should be:", "[]", result.toString());

		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
	
	@Test(timeout = 5 * 1000)
	public void testGetRoleNormRules6() {
		
		/**---------------------------------------------------------------------------------
		 * --			6.	
		 * --				- All parameters are correct
		 * --				- Norm is associated to role played by agent that is passed
		 * --				  as parameter, with deontic field equal to method 
		 * --				  parameters, but not service 
		 * --				- Show correctly the information
		 * ---------------------------------------------------------------------------------
		 */
		
		try {	
			
			//------------------------------------------- Test Initialization  -----------------------------------------------//
			//Test variables
			String unit = "proofUnitFlat";
			String agentName = "proofAgent";
			String deontic = "p";
			String service = "registerUnit";
			String eNorm = "exampleNorm";
			String eNormRule = "exampleNormRule";
			String eRoleCreator = "exampleRoleCreator";
			//Data Base 
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+ unit +"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ eRoleCreator +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('"+ agentName +"')");
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList " +
					"WHERE agentName = '"+ agentName +"'),(SELECT idroleList FROM roleList WHERE (roleName = '"+ eRoleCreator +"' AND " +
					"idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))))");
			
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
					"VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),'"+ eNorm +"', (SELECT iddeontic FROM deontic WHERE deonticdesc = '"+ deontic +"'), (SELECT idtargetType FROM " +
					"targetType WHERE targetName = 'roleName'),(SELECT idroleList FROM roleList WHERE roleName = '"+ eRoleCreator +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), '', '"+ eNormRule +"')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleUnitFlat')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'flat')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'virtual')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleAgent')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleRoleCreator2')");
			//----------------------------------------------------------------------------------------------------------------//
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[4];
			parameters[0] = unit;
		    parameters[1] = agentName;
		    parameters[2] = deontic;
		    parameters[3] = "registerRole";
		    
		    ArrayList<String> result = (ArrayList<String>) m.invoke(dbI, parameters);
			assertEquals("The message should be:", "[]", result.toString());

		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
	
	@Test(timeout = 5 * 1000)
	public void testGetRoleNormRules7() {
		
		/**---------------------------------------------------------------------------------
		 * --			7.	
		 * --				- All parameters are correct
		 * --				- Norm is associated to role played by agent that is passed
		 * --				  as parameter, with service field equal to method 
		 * --				  parameters, but not deontic
		 * --				- Show correctly the information
		 * ---------------------------------------------------------------------------------
		 */
		
		try {	
			
			//------------------------------------------- Test Initialization  -----------------------------------------------//
			//Test variables
			String unit = "proofUnitFlat";
			String agentName = "proofAgent";
			String deontic = "p";
			String service = "registerUnit";
			String eNorm = "exampleNorm";
			String eNormRule = "exampleNormRule";
			String eRoleCreator = "exampleRoleCreator";
			//Data Base 
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+ unit +"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ eRoleCreator +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('"+ agentName +"')");
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList " +
					"WHERE agentName = '"+ agentName +"'),(SELECT idroleList FROM roleList WHERE (roleName = '"+ eRoleCreator +"' AND " +
					"idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))))");
			
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
					"VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),'"+ eNorm +"', (SELECT iddeontic FROM deontic WHERE deonticdesc = '"+ deontic +"'), (SELECT idtargetType FROM " +
					"targetType WHERE targetName = 'roleName'),(SELECT idroleList FROM roleList WHERE roleName = '"+ eRoleCreator +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), '', '"+ eNormRule +"')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleUnitFlat')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'flat')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'virtual')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleAgent')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleRoleCreator2')");
			//----------------------------------------------------------------------------------------------------------------//
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[4];
			parameters[0] = unit;
		    parameters[1] = agentName;
		    parameters[2] = "f";
		    parameters[3] = service;
		    
		    ArrayList<String> result = (ArrayList<String>) m.invoke(dbI, parameters);
			assertEquals("The message should be:", "[]", result.toString());

		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
	
	@Test(timeout = 5 * 1000)
	public void testGetRoleNormRules8() {
		
		/**---------------------------------------------------------------------------------
		 * --			8.	
		 * --				- Any parameters are incorrect
		 * --				- Service field is invalid
		 * --				- Not return information
		 * ---------------------------------------------------------------------------------
		 */
		
		try {	
			
			//------------------------------------------- Test Initialization  -----------------------------------------------//
			//Test variables
			String unit = "proofUnitFlat";
			String agentName = "proofAgent";
			String deontic = "p";
			String service = "registerUnit";
			String eNorm = "exampleNorm";
			String eNorm2 = "exampleNorm2";
			String eNormRule = "exampleNormRule";
			String eNormRule2 = "exampleNormRule2";
			String eRoleCreator = "exampleRoleCreator";
			//Data Base 
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+ unit +"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ eRoleCreator +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('"+ agentName +"')");
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList " +
					"WHERE agentName = '"+ agentName +"'),(SELECT idroleList FROM roleList WHERE (roleName = '"+ eRoleCreator +"' AND " +
					"idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))))");
			
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
					"VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),'"+ eNorm +"', (SELECT iddeontic FROM deontic WHERE deonticdesc = '"+ deontic +"'), (SELECT idtargetType FROM " +
					"targetType WHERE targetName = 'roleName'), -1, (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), '', '"+ eNormRule +"')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleUnitFlat')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'flat')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'virtual')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleAgent')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleRoleCreator2')");
			

			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
					"VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),'"+ eNorm2 +"', (SELECT iddeontic FROM deontic WHERE deonticdesc = '"+ deontic +"'), (SELECT idtargetType FROM " +
					"targetType WHERE targetName = 'positionName'),(SELECT idposition FROM position WHERE positionName = 'creator'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), '', '"+ eNormRule2 +"')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm2 +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleUnitFlat')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm2 +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'flat')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm2 +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'virtual')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm2 +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleAgent')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm2 +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleRoleCreator2')");
			//----------------------------------------------------------------------------------------------------------------//
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[4];
			parameters[0] = unit;
		    parameters[1] = agentName;
		    parameters[2] = deontic;
		    parameters[3] = "NotExists";
		    
		    ArrayList<String> result = (ArrayList<String>) m.invoke(dbI, parameters);
			assertEquals("The message should be:", "[]", result.toString());

		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
	
	@Test(timeout = 5 * 1000)
	public void testGetRoleNormRules9() {
		
		/**---------------------------------------------------------------------------------
		 * --			9.	
		 * --				- Any parameters are incorrect
		 * --				- Deontic field is invalid
		 * --				- Not return information
		 * ---------------------------------------------------------------------------------
		 */
		
		try {	
			
			//------------------------------------------- Test Initialization  -----------------------------------------------//
			//Test variables
			String unit = "proofUnitFlat";
			String agentName = "proofAgent";
			String deontic = "p";
			String service = "registerUnit";
			String eNorm = "exampleNorm";
			String eNorm2 = "exampleNorm2";
			String eNormRule = "exampleNormRule";
			String eNormRule2 = "exampleNormRule2";
			String eRoleCreator = "exampleRoleCreator";
			//Data Base 
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+ unit +"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ eRoleCreator +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('"+ agentName +"')");
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList " +
					"WHERE agentName = '"+ agentName +"'),(SELECT idroleList FROM roleList WHERE (roleName = '"+ eRoleCreator +"' AND " +
					"idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))))");
			
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
					"VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),'"+ eNorm +"', (SELECT iddeontic FROM deontic WHERE deonticdesc = '"+ deontic +"'), (SELECT idtargetType FROM " +
					"targetType WHERE targetName = 'roleName'), -1, (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), '', '"+ eNormRule +"')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleUnitFlat')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'flat')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'virtual')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleAgent')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleRoleCreator2')");
			

			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
					"VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),'"+ eNorm2 +"', (SELECT iddeontic FROM deontic WHERE deonticdesc = '"+ deontic +"'), (SELECT idtargetType FROM " +
					"targetType WHERE targetName = 'roleName'),(SELECT idroleList FROM roleList WHERE roleName = '"+ eRoleCreator +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), '', '"+ eNormRule2 +"')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm2 +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleUnitFlat')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm2 +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'flat')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm2 +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'virtual')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm2 +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleAgent')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm2 +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleRoleCreator2')");
			//----------------------------------------------------------------------------------------------------------------//
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[4];
			parameters[0] = unit;
		    parameters[1] = agentName;
		    parameters[2] = "NotExists";
		    parameters[3] = service;
		    
		    ArrayList<String> result = (ArrayList<String>) m.invoke(dbI, parameters);
			assertEquals("The message should be:", "[]", result.toString());

		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
	
	@Test(timeout = 5 * 1000)
	public void testGetRoleNormRules10() {
		
		/**---------------------------------------------------------------------------------
		 * --			10.	
		 * --				- All parameters are correct
		 * --				- Agent doesn't exists
		 * --				- Show correctly the information
		 * ---------------------------------------------------------------------------------
		 */
		
		try {	
			
			//------------------------------------------- Test Initialization  -----------------------------------------------//
			//Test variables
			String unit = "proofUnitFlat";
			String agentName = "proofAgent";
			String deontic = "p";
			String service = "registerUnit";
			String eNorm = "exampleNorm";
			String eNorm2 = "exampleNorm2";
			String eNormRule = "exampleNormRule";
			String eNormRule2 = "exampleNormRule2";
			String eRoleCreator = "exampleRoleCreator";
			//Data Base 
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+ unit +"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ eRoleCreator +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('"+ agentName +"')");
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList " +
					"WHERE agentName = '"+ agentName +"'),(SELECT idroleList FROM roleList WHERE (roleName = '"+ eRoleCreator +"' AND " +
					"idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))))");
			
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
					"VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),'"+ eNorm +"', (SELECT iddeontic FROM deontic WHERE deonticdesc = '"+ deontic +"'), (SELECT idtargetType FROM " +
					"targetType WHERE targetName = 'roleName'), -1, (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), '', '"+ eNormRule +"')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleUnitFlat')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'flat')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'virtual')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleAgent')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleRoleCreator2')");
			

			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
					"VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),'"+ eNorm2 +"', (SELECT iddeontic FROM deontic WHERE deonticdesc = '"+ deontic +"'), (SELECT idtargetType FROM " +
					"targetType WHERE targetName = 'roleName'),(SELECT idroleList FROM roleList WHERE roleName = '"+ eRoleCreator +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), '', '"+ eNormRule2 +"')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm2 +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleUnitFlat')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm2 +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'flat')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm2 +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'virtual')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm2 +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleAgent')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm2 +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleRoleCreator2')");
			//----------------------------------------------------------------------------------------------------------------//
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[4];
			parameters[0] = unit;
		    parameters[1] = "NotExists";
		    parameters[2] = deontic;
		    parameters[3] = service;
		    
		    ArrayList<String> result = (ArrayList<String>) m.invoke(dbI, parameters);
			assertEquals("The message should be:", "["+ eNormRule +"]", result.toString());

		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
	
	@Test(timeout = 5 * 1000)
	public void testGetRoleNormRules11() {
		
		/**---------------------------------------------------------------------------------
		 * --			11.	
		 * --				- Any parameters are incorrect
		 * --				- Unit field is invalid
		 * --				- Not return information
		 * ---------------------------------------------------------------------------------
		 */
		
		try {	
			
			//------------------------------------------- Test Initialization  -----------------------------------------------//
			//Test variables
			String unit = "proofUnitFlat";
			String agentName = "proofAgent";
			String deontic = "p";
			String service = "registerUnit";
			String eNorm = "exampleNorm";
			String eNorm2 = "exampleNorm2";
			String eNormRule = "exampleNormRule";
			String eNormRule2 = "exampleNormRule2";
			String eRoleCreator = "exampleRoleCreator";
			//Data Base 
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+ unit +"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ eRoleCreator +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('"+ agentName +"')");
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList " +
					"WHERE agentName = '"+ agentName +"'),(SELECT idroleList FROM roleList WHERE (roleName = '"+ eRoleCreator +"' AND " +
					"idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))))");
			
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
					"VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),'"+ eNorm +"', (SELECT iddeontic FROM deontic WHERE deonticdesc = '"+ deontic +"'), (SELECT idtargetType FROM " +
					"targetType WHERE targetName = 'roleName'), -1, (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), '', '"+ eNormRule +"')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleUnitFlat')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'flat')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'virtual')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleAgent')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleRoleCreator2')");
			

			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
					"VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),'"+ eNorm2 +"', (SELECT iddeontic FROM deontic WHERE deonticdesc = '"+ deontic +"'), (SELECT idtargetType FROM " +
					"targetType WHERE targetName = 'roleName'),(SELECT idroleList FROM roleList WHERE roleName = '"+ eRoleCreator +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), '', '"+ eNormRule2 +"')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm2 +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleUnitFlat')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm2 +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'flat')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm2 +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'virtual')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm2 +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleAgent')");
			dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = '"+ eNorm2 +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), 'exampleRoleCreator2')");
			//----------------------------------------------------------------------------------------------------------------//
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[4];
			parameters[0] = "NotExists";
		    parameters[1] = agentName;
		    parameters[2] = deontic;
		    parameters[3] = service;
		    
		    ArrayList<String> result = (ArrayList<String>) m.invoke(dbI, parameters);
			assertEquals("The message should be:", "[]", result.toString());

		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
}