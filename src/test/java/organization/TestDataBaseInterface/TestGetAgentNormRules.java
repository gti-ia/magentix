package organization.TestDataBaseInterface;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.junit.*;

import organization.TestDataBaseInterface.DatabaseAccess;
import es.upv.dsic.gti_ia.organization.DataBaseInterface;
import junit.framework.TestCase;


/** 
 * @author Jose Alemany Bordera  -  jalemany1@dsic.upv.es
 * 
 */

public class TestGetAgentNormRules extends TestCase {

	DataBaseInterface dbI = null;
	DatabaseAccess dbA = null;
	private Method m = null;
	
	
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		
		Class[] parameterTypes = new Class[4];
	    parameterTypes[0] = java.lang.String.class;
	    parameterTypes[1] = java.lang.String.class;
	    parameterTypes[2] = java.lang.String.class;
	    parameterTypes[3] = java.lang.String.class;
		
	    m = DataBaseInterface.class.getDeclaredMethod("getAgentNormRules", parameterTypes);
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
	public void testGetAgentNormRules1() {
		
		/**---------------------------------------------------------------------------------
		 * --			1.	
		 * --				- All parameters are correct
		 * --				- There are two norms, one associated to specific agent 
		 * --				  and other associated to any agent from unit
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
			String eRoleCreator = "exampleRoleCreator";
			String eRule = "exampleRule";
			String eRule2 = "exampleRule2";
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
					"targetType WHERE targetName = 'agentName'), -1, (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), '', '"+ eRule +"')");
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
					"VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),'"+ eNorm2 +"', (SELECT iddeontic FROM deontic WHERE deonticdesc = '"+ deontic +"'), (SELECT idtargetType FROM " +
					"targetType WHERE targetName = 'agentName'),(SELECT idagentList FROM agentList WHERE agentName = '"+ agentName +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), '', '"+ eRule2 +"')");
			//----------------------------------------------------------------------------------------------------------------//
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[4];
			parameters[0] = unit;
		    parameters[1] = agentName;
		    parameters[2] = deontic;
		    parameters[3] = service;
		    
		    ArrayList<String> result = (ArrayList<String>) m.invoke(dbI, parameters);
			assertEquals("The message should be:", "["+ eRule +", "+ eRule2 +"]", result.toString());

		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
	
	@Test
	public void testGetAgentNormRules2() {
		
		/**---------------------------------------------------------------------------------
		 * --			2.	
		 * --				- All parameters are correct
		 * --				- There are two norms, one associated to specific agent 
		 * --				  and other associated to any agent from different units
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
			String eUnit = "exampleUnitFlat";
			String eNorm = "exampleNorm";
			String eNorm2 = "exampleNorm2";
			String eRoleCreator = "exampleRoleCreator";
			String eRoleCreator2 = "exampleRoleCreator2";
			String eRule = "exampleRule";
			String eRule2 = "exampleRule2";
			//Data Base 
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+ unit +"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ eRoleCreator +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+ eUnit +"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+ eUnit +"'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ eRoleCreator2 +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ eUnit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('"+ agentName +"')");
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList " +
					"WHERE agentName = '"+ agentName +"'),(SELECT idroleList FROM roleList WHERE (roleName = '"+ eRoleCreator +"' AND " +
					"idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))))");
			
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
					"VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+ eUnit +"'),'"+ eNorm +"', (SELECT iddeontic FROM deontic WHERE deonticdesc = '"+ deontic +"'), (SELECT idtargetType FROM " +
					"targetType WHERE targetName = 'agentName'), -1, (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), '', '"+ eRule +"')");
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
					"VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),'"+ eNorm2 +"', (SELECT iddeontic FROM deontic WHERE deonticdesc = '"+ deontic +"'), (SELECT idtargetType FROM " +
					"targetType WHERE targetName = 'agentName'),(SELECT idagentList FROM agentList WHERE agentName = '"+ agentName +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), '', '"+ eRule2 +"')");
			//----------------------------------------------------------------------------------------------------------------//
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[4];
			parameters[0] = unit;
		    parameters[1] = agentName;
		    parameters[2] = deontic;
		    parameters[3] = service;
		    
		    ArrayList<String> result = (ArrayList<String>) m.invoke(dbI, parameters);
			assertEquals("The message should be:", "["+ eRule2 +"]", result.toString());

		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
	
	@Test
	public void testGetAgentNormRules3() {
		
		/**---------------------------------------------------------------------------------
		 * --			3.	
		 * --				- All parameters are correct
		 * --				- There are two norms, one associated to agent 
		 * --				  and other associated to agent2 from unit
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
			String eAgent = "exampleAgent";
			String eNorm = "exampleNorm";
			String eNorm2 = "exampleNorm2";
			String eRoleCreator = "exampleRoleCreator";
			String eRule = "exampleRule";
			String eRule2 = "exampleRule2";
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
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('"+ eAgent +"')");
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList " +
					"WHERE agentName = '"+ eAgent +"'),(SELECT idroleList FROM roleList WHERE (roleName = '"+ eRoleCreator +"' AND " +
					"idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))))");
			
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
					"VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),'"+ eNorm +"', (SELECT iddeontic FROM deontic WHERE deonticdesc = '"+ deontic +"'), (SELECT idtargetType FROM " +
					"targetType WHERE targetName = 'agentName'),(SELECT idagentList FROM agentList WHERE agentName = '"+ eAgent +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), '', '"+ eRule +"')");
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
					"VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),'"+ eNorm2 +"', (SELECT iddeontic FROM deontic WHERE deonticdesc = '"+ deontic +"'), (SELECT idtargetType FROM " +
					"targetType WHERE targetName = 'agentName'),(SELECT idagentList FROM agentList WHERE agentName = '"+ agentName +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), '', '"+ eRule2 +"')");
			//----------------------------------------------------------------------------------------------------------------//
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[4];
			parameters[0] = unit;
		    parameters[1] = agentName;
		    parameters[2] = deontic;
		    parameters[3] = service;
		    
		    ArrayList<String> result = (ArrayList<String>) m.invoke(dbI, parameters);
			assertEquals("The message should be:", "["+ eRule2 +"]", result.toString());

		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
	
	@Test
	public void testGetAgentNormRules4() {
		
		/**---------------------------------------------------------------------------------
		 * --			4.	
		 * --				- All parameters are correct
		 * --				- There are two norms, one associated to specific agent 
		 * --				  and other associated to any agent from unit
		 * --				- Deontic field is different
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
			String eRoleCreator = "exampleRoleCreator";
			String eRule = "exampleRule";
			String eRule2 = "exampleRule2";
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
					"targetType WHERE targetName = 'agentName'), -1, (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), '', '"+ eRule +"')");
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
					"VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),'"+ eNorm2 +"', (SELECT iddeontic FROM deontic WHERE deonticdesc = 'o'), (SELECT idtargetType FROM " +
					"targetType WHERE targetName = 'agentName'),(SELECT idagentList FROM agentList WHERE agentName = '"+ agentName +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), '', '"+ eRule2 +"')");
			//----------------------------------------------------------------------------------------------------------------//
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[4];
			parameters[0] = unit;
		    parameters[1] = agentName;
		    parameters[2] = deontic;
		    parameters[3] = service;
		    
		    ArrayList<String> result = (ArrayList<String>) m.invoke(dbI, parameters);
			assertEquals("The message should be:", "["+ eRule +"]", result.toString());

		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
	
	@Test
	public void testGetAgentNormRules5() {
		
		/**---------------------------------------------------------------------------------
		 * --			5.	
		 * --				- All parameters are correct
		 * --				- There are two norms, one associated to specific agent 
		 * --				  and other associated to any agent from unit
		 * --				- Service field is different
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
			String eRoleCreator = "exampleRoleCreator";
			String eRule = "exampleRule";
			String eRule2 = "exampleRule2";
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
					"targetType WHERE targetName = 'agentName'), -1, (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), '', '"+ eRule +"')");
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
					"VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),'"+ eNorm2 +"', (SELECT iddeontic FROM deontic WHERE deonticdesc = '"+ deontic +"'), (SELECT idtargetType FROM " +
					"targetType WHERE targetName = 'agentName'),(SELECT idagentList FROM agentList WHERE agentName = '"+ agentName +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'registerRole' AND numParams = 6), '', '"+ eRule2 +"')");
			//----------------------------------------------------------------------------------------------------------------//
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[4];
			parameters[0] = unit;
		    parameters[1] = agentName;
		    parameters[2] = deontic;
		    parameters[3] = service;
		    
		    ArrayList<String> result = (ArrayList<String>) m.invoke(dbI, parameters);
			assertEquals("The message should be:", "["+ eRule +"]", result.toString());

		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
	
	@Test
	public void testGetAgentNormRules6() {
		
		/**---------------------------------------------------------------------------------
		 * --			6.	
		 * --				- Any parameters are incorrect
		 * --				- Unit doesn't exist
		 * --				- Not return information
		 * ---------------------------------------------------------------------------------
		 */
		
		try {	
			
			//------------------------------------------- Test Initialization  -----------------------------------------------//
			//Test variables
			String agentName = "proofAgent";
			String deontic = "p";
			String service = "registerUnit";
			String eUnit = "exampleUnitFlat";
			String eNorm = "exampleNorm";
			String eNorm2 = "exampleNorm2";
			String eRoleCreator = "exampleRoleCreator";
			String eRule = "exampleRule";
			String eRule2 = "exampleRule2";
			//Data Base 
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+ eUnit +"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+ eUnit +"'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ eRoleCreator +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ eUnit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('"+ agentName +"')");
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList " +
					"WHERE agentName = '"+ agentName +"'),(SELECT idroleList FROM roleList WHERE (roleName = '"+ eRoleCreator +"' AND " +
					"idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+ eUnit +"'))))");
			
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
					"VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+ eUnit +"'),'"+ eNorm +"', (SELECT iddeontic FROM deontic WHERE deonticdesc = '"+ deontic +"'), (SELECT idtargetType FROM " +
					"targetType WHERE targetName = 'agentName'), -1, (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), '', '"+ eRule +"')");
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
					"VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+ eUnit +"'),'"+ eNorm2 +"', (SELECT iddeontic FROM deontic WHERE deonticdesc = '"+ deontic +"'), (SELECT idtargetType FROM " +
					"targetType WHERE targetName = 'agentName'),(SELECT idagentList FROM agentList WHERE agentName = '"+ agentName +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), '', '"+ eRule2 +"')");
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
	
	@Test
	public void testGetAgentNormRules7() {
		
		/**---------------------------------------------------------------------------------
		 * --			7.	
		 * --				- Any parameters are incorrect
		 * --				- Agent doesn't exist
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
			String eRoleCreator = "exampleRoleCreator";
			String eRule = "exampleRule";
			String eRule2 = "exampleRule2";
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
					"targetType WHERE targetName = 'agentName'),(SELECT idagentList FROM agentList WHERE agentName = '"+ agentName +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), '', '"+ eRule +"')");
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
					"VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),'"+ eNorm2 +"', (SELECT iddeontic FROM deontic WHERE deonticdesc = '"+ deontic +"'), (SELECT idtargetType FROM " +
					"targetType WHERE targetName = 'agentName'),(SELECT idagentList FROM agentList WHERE agentName = '"+ agentName +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), '', '"+ eRule2 +"')");
			//----------------------------------------------------------------------------------------------------------------//
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[4];
			parameters[0] = unit;
		    parameters[1] = "NotExists";
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
	
	@Test
	public void testGetAgentNormRules8() {
		
		/**---------------------------------------------------------------------------------
		 * --			8.	
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
			String eRoleCreator = "exampleRoleCreator";
			String eRule = "exampleRule";
			String eRule2 = "exampleRule2";
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
					"targetType WHERE targetName = 'agentName'), -1, (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), '', '"+ eRule +"')");
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
					"VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),'"+ eNorm2 +"', (SELECT iddeontic FROM deontic WHERE deonticdesc = '"+ deontic +"'), (SELECT idtargetType FROM " +
					"targetType WHERE targetName = 'agentName'),(SELECT idagentList FROM agentList WHERE agentName = '"+ agentName +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), '', '"+ eRule2 +"')");
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
	
	@Test
	public void testGetAgentNormRules9() {
		
		/**---------------------------------------------------------------------------------
		 * --			9.	
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
			String eRoleCreator = "exampleRoleCreator";
			String eRule = "exampleRule";
			String eRule2 = "exampleRule2";
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
					"targetType WHERE targetName = 'agentName'), -1, (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), '', '"+ eRule +"')");
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
					"VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),'"+ eNorm2 +"', (SELECT iddeontic FROM deontic WHERE deonticdesc = '"+ deontic +"'), (SELECT idtargetType FROM " +
					"targetType WHERE targetName = 'agentName'),(SELECT idagentList FROM agentList WHERE agentName = '"+ agentName +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'"+ service +"' AND numParams = 5), '', '"+ eRule2 +"')");
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
}
