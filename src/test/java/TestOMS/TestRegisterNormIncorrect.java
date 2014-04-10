package TestOMS;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.SF;
import es.upv.dsic.gti_ia.organization.exception.InvalidDeonticException;
import es.upv.dsic.gti_ia.organization.exception.InvalidExpressionException;
import es.upv.dsic.gti_ia.organization.exception.InvalidIDException;
import es.upv.dsic.gti_ia.organization.exception.InvalidOMSActionException;
import es.upv.dsic.gti_ia.organization.exception.InvalidPositionException;
import es.upv.dsic.gti_ia.organization.exception.InvalidTargetTypeException;
import es.upv.dsic.gti_ia.organization.exception.InvalidTargetValueException;
import es.upv.dsic.gti_ia.organization.exception.NormExistsInUnitException;
import es.upv.dsic.gti_ia.organization.exception.NotInUnitAndNotCreatorException;
import es.upv.dsic.gti_ia.organization.exception.RoleNotExistsException;
import es.upv.dsic.gti_ia.organization.exception.UnitNotExistsException;

/** 
 * @author Javier Palanca
 * @author Jose Alemany Bordera  -  jalemany1@dsic.upv.es
 * 
 */

public class TestRegisterNormIncorrect {

	OMSProxy omsProxy = null;
	DatabaseAccess dbA = null;


	Agent agent = null;

	
	OMS oms = null;
	SF sf = null;

	Process qpid_broker;
	
	@After
	public void tearDown() throws Exception {

		//------------------Clean Data Base -----------//
		dbA.executeSQL("DELETE FROM agentPlayList");
		dbA.executeSQL("DELETE FROM agentList");
		
		dbA.executeSQL("DELETE FROM actionNormParam");
		
		dbA.executeSQL("DELETE FROM normList");
		dbA.executeSQL("DELETE FROM roleList WHERE idroleList != 1");
		dbA.executeSQL("DELETE FROM unitHierarchy WHERE idChildUnit != 1");
		dbA.executeSQL("DELETE FROM unitList WHERE idunitList != 1");
		//--------------------------------------------//
		

		dbA = null;
		omsProxy = null;

		agent.terminate();
		agent = null;

		oms.Shutdown();
		sf.Shutdown();
		
		oms.await();
		sf.await();
		
		oms = null;
		sf = null;

		AgentsConnection.disconnect();
		qpidManager.UnixQpidManager.stopQpid(qpid_broker);
	}
	
	@Before
	public void setUp() throws Exception {

		qpid_broker = qpidManager.UnixQpidManager.startQpid(Runtime.getRuntime(), qpid_broker);
		AgentsConnection.connect();

		oms = new OMS(new AgentID("OMS"));
		sf =  new SF(new AgentID("SF"));

		oms.start();
		sf.start();


		agent = new Agent(new AgentID("pruebas"));

		omsProxy = new OMSProxy(agent);

		dbA = new DatabaseAccess();

		//------------------Clean Data Base -----------//
		dbA.executeSQL("DELETE FROM agentPlayList");
		dbA.executeSQL("DELETE FROM agentList");
		
		dbA.executeSQL("DELETE FROM actionNormParam");
		
		dbA.executeSQL("DELETE FROM normList");
		dbA.executeSQL("DELETE FROM roleList WHERE idroleList != 1");
		dbA.executeSQL("DELETE FROM unitHierarchy WHERE idChildUnit != 1");
		dbA.executeSQL("DELETE FROM unitList WHERE idunitList != 1");
		//--------------------------------------------//
	}
	
	@Test(expected=UnitNotExistsException.class)
	public void testRegisterNormIncorrectParam1() throws Exception {
		
		String NormaPrueba = "@normaprueba[f,agentName:_,registerUnit(_,_,_,_,_),_,_]";

		String result = omsProxy.registerNorm("Invalida", NormaPrueba);

		fail(result);
	}
	
	@Test
	public void testRegisterNormIncorrectParam2() throws Exception {
		
		//------------------------------------------- Test Initialization  -----------------------------------------------//
		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES" +
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");
		//----------------------------------------------------------------------------------------------------------------//
		
		String NormaPrueba = "@normaprueba[f,<agentName:_>,registerUnit(_,_,_,_,_),_,_]";

		String result = omsProxy.registerNorm("virtual", NormaPrueba);		
		assertEquals("normaprueba created", result);
			
		assertTrue(dbA.executeQuery("SELECT * FROM normList WHERE normName = 'normaprueba'"));
		
	}
	
	@Test(expected=NormExistsInUnitException.class)
	public void testRegisterNormIncorrectParam3() throws Exception {
			
		//------------------------------------------- Test Initialization  -----------------------------------------------//
		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");
		//----------------------------------------------------------------------------------------------------------------//
		
		String NormaPrueba = "@normaprueba[f,<agentName:_>,registerUnit(_,_,_,_,_),_,_]";
		String result = omsProxy.registerNorm("virtual", NormaPrueba);
			
		assertEquals("normaprueba created", result);
			
		result = omsProxy.registerNorm("virtual", NormaPrueba);
			
		fail(result);
		
	}
	
	@Test
	public void testRegisterNormIncorrectParam4() throws Exception {
			
		//------------------------------------------- Test Initialization  -----------------------------------------------//
		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");
		//----------------------------------------------------------------------------------------------------------------//
		
		String NormaPrueba = "@normaprueba[f,<agentName:_>,registerUnit(_,_,_,_,_),_,_]";
		String result = omsProxy.registerNorm("virtual", NormaPrueba);
			
		assertEquals("normaprueba created", result);
			
		String NormaPruebaRepitada = "@normapruebaRepetida[f,<agentName:_>,registerUnit(_,_,_,_,_),_,_]";
		result = omsProxy.registerNorm("virtual", NormaPruebaRepitada);
			
		assertEquals("normapruebaRepetida created", result);
			
		assertTrue(dbA.executeQuery("SELECT * FROM normList WHERE normName = 'normaprueba'"));
		assertTrue(dbA.executeQuery("SELECT * FROM normList WHERE normName = 'normapruebaRepetida'"));
		
	}
	
	@Test
	public void testRegisterNormIncorrectParam5() throws Exception {
		
		//------------------------------------------- Test Initialization  -----------------------------------------------//
		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");
		//----------------------------------------------------------------------------------------------------------------//
		
		String NormaPrueba = "@normaprueba[f,<agentName:_>,registerUnit(_,_,_,_,_),_,_]";
		String result = omsProxy.registerNorm("virtual", NormaPrueba);
			
		assertEquals("normaprueba created", result);
			
		String NormaPruebaRepitada = "@normapruebaRepetida[f,<agentName:_>,registerUnit(_,_,_,_,_),_,_]";
		result = omsProxy.registerNorm("virtual", NormaPruebaRepitada);
			
		assertEquals("normapruebaRepetida created", result);
			
		assertTrue(dbA.executeQuery("SELECT * FROM normList WHERE normName = 'normaprueba'"));
		assertTrue(dbA.executeQuery("SELECT * FROM normList WHERE normName = 'normapruebaRepetida'"));
	}
	
	@Test(expected=InvalidIDException.class)
	public void testRegisterNormIncorrectParam5a() throws Exception {
			
		//------------------------------------------- Test Initialization  -----------------------------------------------//
		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");
		//----------------------------------------------------------------------------------------------------------------//
		
		String NormaPrueba = "@++normaprueba[f,<agentName:_>,registerUnit(_,_,_,_,_),_,_]";
		String result = omsProxy.registerNorm("virtual", NormaPrueba);
				
		fail(result);
			
	}
	
	@Test(expected=InvalidExpressionException.class)
	public void testRegisterNormIncorrectParam5b() throws Exception {
			
		//------------------------------------------- Test Initialization  -----------------------------------------------//
		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");
		//----------------------------------------------------------------------------------------------------------------//
		
		String NormaPrueba = "@normaprueba[f,<agentName:_>,registerUnit(_,_,_,_,_) = 5,_,_]";
		String result = omsProxy.registerNorm("virtual", NormaPrueba);
				
		fail(result);
		
	}
	
	@Test(expected=InvalidDeonticException.class)
	public void testRegisterNormIncorrectParam5c() throws Exception {
	
		//------------------------------------------- Test Initialization  -----------------------------------------------//
		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");
		//----------------------------------------------------------------------------------------------------------------//

		String NormaPrueba = "@normaprueba[a,<agentName:_>,registerUnit(_,_,_,_,_),_,_]";
		String result = omsProxy.registerNorm("virtual", NormaPrueba);
				
		fail(result);
		
	}
	
	@Test(expected=InvalidTargetTypeException.class)
	public void testRegisterNormIncorrectParam5d() throws Exception {

		//------------------------------------------- Test Initialization  -----------------------------------------------//
		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");
		//----------------------------------------------------------------------------------------------------------------//
		
		String NormaPrueba = "@normaprueba[f,<InvalidTargetType:_>,registerUnit(_,_,_,_,_),_,_]";
		String result = omsProxy.registerNorm("virtual", NormaPrueba);
				
		fail(result);
			
	}
	
	@Test(expected=InvalidTargetValueException.class)
	public void testRegisterNormIncorrectParam5e() throws Exception {
	
		//------------------------------------------- Test Initialization  -----------------------------------------------//
		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");
		//----------------------------------------------------------------------------------------------------------------//
		
		String NormaPrueba = "@normaprueba[f,<agentName:*>,registerUnit(_,_,_,_,_),_,_]";
		String result = omsProxy.registerNorm("virtual", NormaPrueba);
				
		fail(result);
			
	}
	
	//------------------------------------------------------------
	//-------Restricciones de consistencia o integridad-----------
	
	@Test(expected=RoleNotExistsException.class)
	public void testRegisterNormIncorrect6() throws Exception {
			
		//------------------------------------------- Test Initialization  -----------------------------------------------//
		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");
		//----------------------------------------------------------------------------------------------------------------//
		
		String NormaPrueba = "@normaprueba[f,<roleName:rolInvalido>,registerUnit(_,_,_,_,_),_,_]";
		String result = omsProxy.registerNorm("virtual", NormaPrueba);
				
		fail(result);
		
	}
	
	@Test(expected=InvalidPositionException.class)
	public void testRegisterNormIncorrect7a() throws Exception {
			
		//------------------------------------------- Test Initialization  -----------------------------------------------//
		String unit = "jerarquia";
			
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
			
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
	
		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");
		//----------------------------------------------------------------------------------------------------------------//
		
		String NormaPrueba = "@normaprueba[f,<positionName:member>,registerUnit(_,_,_,_,_),_,_]";
		String result = omsProxy.registerNorm("jerarquia", NormaPrueba);
				
		fail(result);
		
	}
	
	@Test
	public void testRegisterNormIncorrect7b() throws Exception {
	
		//------------------------------------------- Test Initialization  -----------------------------------------------//
		String unit = "jerarquia";
			
		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
			
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
			"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
			"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
			"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
	
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");

		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");
		//----------------------------------------------------------------------------------------------------------------//
			
		String NormaPrueba = "@normaprueba[f,<positionName:supervisor>,registerUnit(_,_,_,_,_),_,_]";
		String result = omsProxy.registerNorm("jerarquia", NormaPrueba);
				
		assertEquals("normaprueba created", result);
		
	}
	
	@Test
	public void testRegisterNormIncorrect7c() throws Exception {
			
		//------------------------------------------- Test Initialization  -----------------------------------------------//
		String unit = "jerarquia";
			
		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
			
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
			"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
			"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
			"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
	
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");

		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");
		//----------------------------------------------------------------------------------------------------------------//
			
		String NormaPrueba = "@normaprueba[f,<positionName:creator>,registerUnit(_,_,_,_,_),_,_]";
		String result = omsProxy.registerNorm("jerarquia", NormaPrueba);
				
		assertEquals("normaprueba created", result);
		
	}
	
	@Test
	public void testRegisterNormIncorrect7d() throws Exception {
	
		//------------------------------------------- Test Initialization  -----------------------------------------------//
		String unit = "jerarquia";
			
		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
			
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
			"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
			"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
			"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
	
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");

		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");
		//----------------------------------------------------------------------------------------------------------------//
			
		String NormaPrueba = "@normaprueba[f,<positionName:subordinate>,registerUnit(_,_,_,_,_),_,_]";
		String result = omsProxy.registerNorm("jerarquia", NormaPrueba);
				
		assertEquals("normaprueba created", result);
		
	}
	
	@Test
	public void testRegisterNormIncorrect8a() throws Exception {
			
		//------------------------------------------- Test Initialization  -----------------------------------------------//
		String unit = "equipo";
			
		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
		
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
			"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
			"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
			"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
	
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");

		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");
		//----------------------------------------------------------------------------------------------------------------//
			
		String NormaPrueba = "@normaprueba[f,<positionName:member>,registerUnit(_,_,_,_,_),_,_]";
		String result = omsProxy.registerNorm(unit, NormaPrueba);
				
		assertEquals("normaprueba created", result);

	}
	
	@Test
	public void testRegisterNormIncorrect8b() throws Exception {
			
		//------------------------------------------- Test Initialization  -----------------------------------------------//
		String unit = "equipo";
			
		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
			"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
			"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
			"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
	
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");

		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");
		//----------------------------------------------------------------------------------------------------------------//
			
		String NormaPrueba = "@normaprueba[f,<positionName:creator>,registerUnit(_,_,_,_,_),_,_]";
		String result = omsProxy.registerNorm(unit, NormaPrueba);
				
		assertEquals("normaprueba created", result);
		
	}
	
	@Test(expected=InvalidPositionException.class)
	public void testRegisterNormIncorrect8c() throws Exception {
	
		//------------------------------------------- Test Initialization  -----------------------------------------------//
		String unit = "equipo";
			
		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
			"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
			"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
			"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
	
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");

		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");
		//----------------------------------------------------------------------------------------------------------------//
		
		String NormaPrueba = "@normaprueba[f,<positionName:supervisor>,registerUnit(_,_,_,_,_),_,_]";
		String result = omsProxy.registerNorm(unit, NormaPrueba);
				
		fail(result);
		
	}
	
	@Test(expected=InvalidPositionException.class)
	public void testRegisterNormIncorrect8d() throws Exception {
	
		//------------------------------------------- Test Initialization  -----------------------------------------------//
		String unit = "equipo";
			
		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
			"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
			"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
			"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
	
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");

		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");
		//----------------------------------------------------------------------------------------------------------------//

		String NormaPrueba = "@normaprueba[f,<positionName:subordinate>,registerUnit(_,_,_,_,_),_,_]";
		String result = omsProxy.registerNorm(unit, NormaPrueba);
				
		fail(result);
		
	}
	
	@Test
	public void testRegisterNormIncorrect9a() throws Exception {
			
		//------------------------------------------- Test Initialization  -----------------------------------------------//
		String unit = "plana";
			
		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
			"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
			"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
			"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
	
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");

		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");
		//----------------------------------------------------------------------------------------------------------------//
			
		String NormaPrueba = "@normaprueba[f,<positionName:member>,registerUnit(_,_,_,_,_),_,_]";
		String result = omsProxy.registerNorm(unit, NormaPrueba);
				
		assertEquals("normaprueba created", result);
		
	}
	
	@Test
	public void testRegisterNormIncorrect9b() throws Exception {
			
		//------------------------------------------- Test Initialization  -----------------------------------------------//
		String unit = "plana";
			
		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
			"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
			"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
			"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
	
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");

		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");
		//----------------------------------------------------------------------------------------------------------------//

		String NormaPrueba = "@normaprueba[f,<positionName:creator>,registerUnit(_,_,_,_,_),_,_]";
		String result = omsProxy.registerNorm(unit, NormaPrueba);
				
		assertEquals("normaprueba created", result);
		
	}
	
	@Test(expected=InvalidPositionException.class)
	public void testRegisterNormIncorrect9c() throws Exception {
	
		//------------------------------------------- Test Initialization  -----------------------------------------------//
		String unit = "plana";
			
		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
			"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
			"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
			"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
	
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");

		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");
		//----------------------------------------------------------------------------------------------------------------//
		
		String NormaPrueba = "@normaprueba[f,<positionName:supervisor>,registerUnit(_,_,_,_,_),_,_]";
		String result = omsProxy.registerNorm(unit, NormaPrueba);
				
		fail(result);
		
	}
	
	@Test(expected=InvalidPositionException.class)
	public void testRegisterNormIncorrect9d() throws Exception {
	
		//------------------------------------------- Test Initialization  -----------------------------------------------//
		String unit = "plana";
			
		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
			"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
			"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
			"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
	
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");

		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");
		//----------------------------------------------------------------------------------------------------------------//

		String NormaPrueba = "@normaprueba[f,<positionName:subordinate>,registerUnit(_,_,_,_,_),_,_]";
		String result = omsProxy.registerNorm(unit, NormaPrueba);
				
		fail(result);
		
	}
	
	@Test
	public void testRegisterNormIncorrect10() throws Exception {
	
		//------------------------------------------- Test Initialization  -----------------------------------------------//
		String unit = "virtual";
		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
				
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");
		//----------------------------------------------------------------------------------------------------------------//
		
		String NormaPrueba = "@normaprueba[f,<agentName:inexistente>,registerUnit(_,_,_,_,_),_,_]";
		String result = omsProxy.registerNorm(unit, NormaPrueba);
				
		assertEquals("normaprueba created", result);
			
		assertTrue(dbA.executeQuery("SELECT * FROM agentList WHERE agentName = 'inexistente'"));
		
	}
	
	@Test(expected=InvalidOMSActionException.class)
	public void testRegisterNormIncorrect11() throws Exception {
	
		//------------------------------------------- Test Initialization  -----------------------------------------------//
		String unit = "virtual";
		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
				
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");
		//----------------------------------------------------------------------------------------------------------------//

		String NormaPrueba = "@normaprueba[f,<agentName:inexistente>,registerService(_,_,_,_,_),_,_]";
		String result = omsProxy.registerNorm(unit, NormaPrueba);
				
		fail(result);
		
	}
	
	
	// Comprobación de normas estructurales
	@Test(expected=NotInUnitAndNotCreatorException.class)
	public void testRegisterNormIncorrectPermissions12() throws Exception {
	
		//------------------------------------------- Test Initialization  -----------------------------------------------//
		String unit = "plana";
			
		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
			"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
			"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
			"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
		//----------------------------------------------------------------------------------------------------------------//

		String NormaPrueba = "@normaprueba[f,<agentName:_>,registerRole(_,_,_,_,_,_),_,_]";
		String result = omsProxy.registerNorm(unit, NormaPrueba);
				
		fail(result);
		
	}
	
	
	
	@Test
	public void testRegisterNormIncorrectPermissions13() throws Exception {
	
		//------------------------------------------- Test Initialization  -----------------------------------------------//
		String unit = "plana";
			
		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('equipo',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'equipo'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
			"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
			"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
			"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
			"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
			"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
			"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
	
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");
		//----------------------------------------------------------------------------------------------------------------//

		String NormaPrueba = "@normaprueba[f,<agentName:_>,registerRole(_,_,_,_,_,_),_,_]";
		String result = omsProxy.registerNorm(unit, NormaPrueba);
					
		assertEquals("normaprueba created", result);
		
	}
	
	@Test(expected=UnitNotExistsException.class)
	public void testRegisterNormIncorrectPermissions14() throws Exception {
	
		String NormaPrueba = "@normaprueba[f,<agentName:_>,registerRole(_,_,_,_,_,_),_,_]";
		String result = omsProxy.registerNorm("registerUnit", NormaPrueba);
				
		fail(result);
		
	}
}
