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

public class TestGetNormContent extends TestCase {

	DataBaseInterface dbI = null;
	DatabaseAccess dbA = null;
	private Method m = null;
	
	
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		
		Class[] parameterTypes = new Class[2];
	    parameterTypes[0] = java.lang.String.class;
	    parameterTypes[1] = java.lang.String.class;
		
	    m = DataBaseInterface.class.getDeclaredMethod("getNormContent", parameterTypes);
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
	public void testGetNormContent1() {
		
		/**---------------------------------------------------------------------------------------------
		 * --			1.	
		 * --				- All parameters are correct
		 * --				- Unit has a parent unit
		 * --				- Show correctly the information
		 * ---------------------------------------------------------------------------------------------
		 */
		
		try {	
			
			//------------------------------------------- Test Initialization  -----------------------------------------------//
			//Test variables
			String unit = "unitTeam";
			String unit2 = "unitTeam2";
			String norm = "proofNorm";
			String eRoleCreator = "exampleRoleCreator";
			String eAgent = "exampleAgent";
			String eContent = "@proofNorm[f,agentName:exampleAgent,registerUnit(_,_,_,_,_),playsRole(exampleAgent,participant,virtual),_]";
			String eContent2 = "@proofNorm[p,positionName:creator,alocateRole(_,unitTeam,_,_),_,_]";
			//Data Base 
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+ unit +"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))");
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+ unit2 +"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+ unit2 +"'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ eRoleCreator +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('"+ eAgent +"')");
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList " +
					"WHERE agentName = '"+ eAgent +"'),(SELECT idroleList FROM roleList WHERE (roleName = '"+ eRoleCreator +"' AND " +
					"idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))))");
			
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
					"VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),'"+ norm +"', (SELECT iddeontic FROM deontic WHERE deonticdesc = 'f'), (SELECT idtargetType FROM " +
					"targetType WHERE targetName = 'agentName'),(SELECT idagentList FROM agentList WHERE agentName = '"+ eAgent +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'registerUnit' AND numParams = 5), '"+ eContent +"', 'registerUnit(_,_,_,_,_):-playsRole(pruebas,participant,virtual)')");
			
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
					"VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+ unit2 +"'),'"+ norm +"', (SELECT iddeontic FROM deontic WHERE deonticdesc = 'p'), (SELECT idtargetType FROM " +
					"targetType WHERE targetName = 'positionName'),(SELECT idposition FROM position WHERE positionName = 'creator'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'allocateRole' AND numParams = 4), '"+ eContent2 +"', 'alocateRole(_,Plana,_,_)')");
			//----------------------------------------------------------------------------------------------------------------//
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[2];
			parameters[0] = norm;
			parameters[1] = unit2;
		    
			String result = (String) m.invoke(dbI, parameters);
			assertEquals("The message should be:", eContent2, result);

		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
	
	@Test
	public void testGetNormContent2() {
		
		/**---------------------------------------------------------------------------------------------
		 * --			2.	
		 * --				- Any parameters are incorrect
		 * --				- Unit doesn't exist
		 * --				- Not return information
		 * ---------------------------------------------------------------------------------------------
		 */
		
		try {	
			
			//------------------------------------------- Test Initialization  -----------------------------------------------//
			//Test variables
			String norm = "proofNorm";
			String eUnit = "exampleUnitFlat";
			String eRoleCreator = "exampleRoleCreator";
			String eContent = "exampleContent";
			//Data Base 
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+ eUnit +"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+ eUnit +"'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ eRoleCreator +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ eUnit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
					"VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+ eUnit +"'),'"+ norm +"', (SELECT iddeontic FROM deontic WHERE deonticdesc = 'f'), (SELECT idtargetType FROM " +
					"targetType WHERE targetName = 'agentName'),(SELECT idroleList FROM roleList WHERE roleName = '"+ eRoleCreator +"'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
					"'registerUnit' AND numParams = 5), '"+ eContent +"', '')");
			//----------------------------------------------------------------------------------------------------------------//
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[2];
			parameters[0] = norm;
			parameters[1] = "NotExists";
		    
			String result = (String) m.invoke(dbI, parameters);
			assertEquals("The message should be:", "", result);

		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
	
	@Test
	public void testGetNormContent3() {
		
		/**---------------------------------------------------------------------------------------------
		 * --			3.	
		 * --				- Any parameters are incorrect
		 * --				- Norm doesn't exist
		 * --				- Not return information
		 * ---------------------------------------------------------------------------------------------
		 */
		
		try {	
			
			//------------------------------------------- Test Initialization  -----------------------------------------------//
			//Test variables
			String unit = "proofUnitFlat";
			String eRoleCreator = "exampleRoleCreator";
			//Data Base 
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+ unit +"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'))");
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('"+ eRoleCreator +"',(SELECT idunitList FROM unitList WHERE unitName = '"+ unit +"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
					"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			//----------------------------------------------------------------------------------------------------------------//
			
			dbI = new DataBaseInterface();
			
			Object[] parameters = new Object[2];
			parameters[0] = "NotExists";
			parameters[1] = unit;
		    
			String result = (String) m.invoke(dbI, parameters);
			assertEquals("The message should be:", "", result);

		} catch(InvocationTargetException e) {
			
			fail(e.getTargetException().getMessage());
			
		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}
}
