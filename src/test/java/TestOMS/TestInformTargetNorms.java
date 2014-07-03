package TestOMS;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.SF;
import es.upv.dsic.gti_ia.organization.exception.InvalidTargetTypeException;
import es.upv.dsic.gti_ia.organization.exception.UnitNotExistsException;


public class TestInformTargetNorms {

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

	@Test(timeout = 5 * 60 * 1000, expected = UnitNotExistsException.class)
	public void testInformTargetNorms1() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('jerarquia',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
			
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))");
		
		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");	

		dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionnorm, normContent, normRule )" +
				" VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'), 'accesRegisternotUnit', 3,3, 3, 1,'normContent' ,'registerUnit(_,_,_,_,_,) := null')");
			
		// Method call	
		omsProxy.informTargetNorms("roleName", "pruebas", "inexistente");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = InvalidTargetTypeException.class)
	public void testInformTargetNorms2() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('jerarquia',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
			
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionnorm, normContent, normRule )" +
				" VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'), 'accesRegisternotUnit', 3,3, 3, 1,'normContent' ,'registerUnit(_,_,_,_,_,) := null')");
			
		// Method call	
		omsProxy.informTargetNorms("invalidTargetValue", "targetValue", "jerarquia");
	}
	
	
	// Pruebas para el funcionamiento correcto.
	@Test(timeout = 5 * 60 * 1000)
	public void testInformTargetNormsCorrect1()
	{
		try
		{
			
			String unit = "plana";
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
		
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			

			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionnorm, normContent, normRule )" +
					" VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'), 'normaprueba', 3,1,(SELECT idagentList FROM agentList WHERE agentName = 'pruebas') , 3,'normContent' ,'registerRole(_,_,_,_,_,) := null')");
			
			
			ArrayList<ArrayList<String>> result = omsProxy.informTargetNorms("agentName", "pruebas", unit);
			
			assertEquals(1, result.size());
			assertEquals("normaprueba", result.get(0).get(0));
			assertEquals("plana", result.get(0).get(1));
			assertEquals("agentName", result.get(0).get(2));
			assertEquals("pruebas", result.get(0).get(3));
			
			


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test(timeout = 5 * 60 * 1000)
	public void testInformTargetNormsCorrect2()
	{
		try
		{
			
			String unit = "plana";
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
		
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			

			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionnorm, normContent, normRule )" +
					" VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'), 'normaprueba', 3,1,(SELECT idagentList FROM agentList WHERE agentName = 'pruebas') , 3,'normContent' ,'registerRole(_,_,_,_,_,) := null')");
			
			
			ArrayList<ArrayList<String>> result = omsProxy.informTargetNorms("agentName", "", unit);
			
			assertEquals(1, result.size());
			assertEquals("normaprueba", result.get(0).get(0));
			assertEquals("plana", result.get(0).get(1));
			assertEquals("agentName", result.get(0).get(2));
			assertEquals("pruebas", result.get(0).get(3));
			
			


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test(timeout = 5 * 60 * 1000)
	public void testInformTargetNormsCorrect3()
	{
		try
		{
			
			String unit = "plana";
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
		
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

			

			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionnorm, normContent, normRule )" +
					" VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'), 'normaprueba', 3,1,(SELECT idagentList FROM agentList WHERE agentName = 'pruebas') , 3,'normContent' ,'registerRole(_,_,_,_,_,) := null')");
			
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionnorm, normContent, normRule )" +
					" VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'), 'normaprueba2', 3,2,(SELECT idagentList FROM agentList WHERE agentName = 'pruebas') , 2,'normContent' ,'registerRole(_,_,_,_,_,) := null')");
		
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionnorm, normContent, normRule )" +
					" VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'), 'normaprueba3', 3,3,(SELECT idposition FROM position WHERE positionName = 'creator') , 3,'normContent' ,'registerRole(_,_,_,_,_,) := null')");
		
			ArrayList<ArrayList<String>> result = omsProxy.informTargetNorms("agentName", "", unit);
			
			assertEquals(1, result.size());
			assertEquals("normaprueba", result.get(0).get(0));
			assertEquals("plana", result.get(0).get(1));
			assertEquals("agentName", result.get(0).get(2));
			assertEquals("pruebas", result.get(0).get(3));
			
			


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test(timeout = 5 * 60 * 1000)
	public void testInformTargetNormsCorrect4()
	{
		try
		{
			
			String unit = "plana";
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
		
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('participant',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

			

			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionnorm, normContent, normRule )" +
					" VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'), 'normaprueba', 3,2,(SELECT idroleList FROM roleList WHERE roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"')) , 3,'normContent' ,'registerRole(_,_,_,_,_,) := null')");
			
		
			ArrayList<ArrayList<String>> result = omsProxy.informTargetNorms("roleName", "participant", unit);
			
			assertEquals(1, result.size());
			assertEquals("normaprueba", result.get(0).get(0));
			assertEquals("plana", result.get(0).get(1));
			assertEquals("roleName", result.get(0).get(2));
			assertEquals("participant", result.get(0).get(3));
			
			


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test(timeout = 5 * 60 * 1000)
	public void testInformTargetNormsCorrect5()
	{
		try
		{
			
			String unit = "plana";
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
		
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('participant',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

			

			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionnorm, normContent, normRule )" +
					" VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'), 'normaprueba', 3,2,-1 , 3,'normContent' ,'registerRole(_,_,_,_,_,) := null')");
			
		
			ArrayList<ArrayList<String>> result = omsProxy.informTargetNorms("roleName", "_", unit);
			
			assertEquals(1, result.size());
			assertEquals("normaprueba", result.get(0).get(0));
			assertEquals("plana", result.get(0).get(1));
			assertEquals("roleName", result.get(0).get(2));
			assertEquals("_", result.get(0).get(3));
			
			


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test(timeout = 5 * 60 * 1000)
	public void testInformTargetNormsCorrect6()
	{
		try
		{
			
			String unit = "plana";
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
		
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('participant',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

			

			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionnorm, normContent, normRule )" +
					" VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'), 'normaprueba', 3,2,-1 , 3,'normContent' ,'registerRole(_,_,_,_,_,) := null')");
			
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionnorm, normContent, normRule )" +
					" VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'), 'normaprueba2', 3,2,(SELECT idroleList FROM roleList WHERE roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"')) , 2,'normContent' ,'registerRole(_,_,_,_,_,) := null')");
		
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionnorm, normContent, normRule )" +
					" VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'), 'normaprueba3', 3,3,(SELECT idposition FROM position WHERE positionName = 'creator') , 3,'normContent' ,'registerRole(_,_,_,_,_,) := null')");
		
			ArrayList<ArrayList<String>> result = omsProxy.informTargetNorms("roleName", "_", unit);
			
			assertEquals(1, result.size());
			assertEquals("normaprueba", result.get(0).get(0));
			assertEquals("plana", result.get(0).get(1));
			assertEquals("roleName", result.get(0).get(2));
			assertEquals("_", result.get(0).get(3));
			
			


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test(timeout = 5 * 60 * 1000)
	public void testInformTargetNormsCorrect7()
	{
		try
		{
			
			String unit = "plana";
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
		
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('participant',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

			

			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionnorm, normContent, normRule )" +
					" VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'), 'normaprueba', 3,3, (SELECT idposition FROM position WHERE positionName = 'creator'), 3,'normContent' ,'registerRole(_,_,_,_,_,) := null')");
			
		
			ArrayList<ArrayList<String>> result = omsProxy.informTargetNorms("positionName", "creator", unit);
			
			assertEquals(1, result.size());
			assertEquals("normaprueba", result.get(0).get(0));
			assertEquals("plana", result.get(0).get(1));
			assertEquals("positionName", result.get(0).get(2));
			assertEquals("creator", result.get(0).get(3));
			
			


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test(timeout = 5 * 60 * 1000)
	public void testInformTargetNormsCorrect8()
	{
		try
		{
			
			String unit = "plana";
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
		
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('participant',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

			

			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionnorm, normContent, normRule )" +
					" VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'), 'normaprueba', 3,3, -1, 3,'normContent' ,'registerRole(_,_,_,_,_,) := null')");
			
		
			ArrayList<ArrayList<String>> result = omsProxy.informTargetNorms("positionName", "_", unit);
			
			assertEquals(1, result.size());
			assertEquals("normaprueba", result.get(0).get(0));
			assertEquals("plana", result.get(0).get(1));
			assertEquals("positionName", result.get(0).get(2));
			assertEquals("_", result.get(0).get(3));
			
			


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test(timeout = 5 * 60 * 1000)
	public void testInformTargetNormsCorrect9()
	{
		try
		{
			
			String unit = "plana";
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
		
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('participant',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

			

			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionnorm, normContent, normRule )" +
					" VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'), 'normaprueba', 3,3,-1 , 3,'normContent' ,'registerRole(_,_,_,_,_,) := null')");
			
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionnorm, normContent, normRule )" +
					" VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'), 'normaprueba2', 3,3,(SELECT idposition FROM position WHERE positionName = 'creator') , 2,'normContent' ,'registerRole(_,_,_,_,_,) := null')");
		
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionnorm, normContent, normRule )" +
					" VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'), 'normaprueba3', 3,1,(SELECT idagentList FROM agentList WHERE agentName = 'pruebas') , 3,'normContent' ,'registerRole(_,_,_,_,_,) := null')");
		
			ArrayList<ArrayList<String>> result = omsProxy.informTargetNorms("positionName", "_", unit);
			
			assertEquals(1, result.size());
			assertEquals("normaprueba", result.get(0).get(0));
			assertEquals("plana", result.get(0).get(1));
			assertEquals("positionName", result.get(0).get(2));
			assertEquals("_", result.get(0).get(3));
			
			


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test(timeout = 5 * 60 * 1000)
	public void testInformTargetNormsCorrect10()
	{
		try
		{
			
			String unit = "plana";
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
		
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('participant',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

			

			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionnorm, normContent, normRule )" +
					" VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'), 'normaprueba', 3,3,-1 , 3,'normContent' ,'registerRole(_,_,_,_,_,) := null')");
			
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionnorm, normContent, normRule )" +
					" VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'), 'normaprueba2', 3,3,(SELECT idposition FROM position WHERE positionName = 'creator') , 2,'normContent' ,'registerRole(_,_,_,_,_,) := null')");
		
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionnorm, normContent, normRule )" +
					" VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'), 'normaprueba3', 3,1,(SELECT idagentList FROM agentList WHERE agentName = 'pruebas') , 3,'normContent' ,'registerRole(_,_,_,_,_,) := null')");
		
			ArrayList<ArrayList<String>> result = omsProxy.informTargetNorms("positionName", "", unit);
			
			assertEquals(2, result.size());
			
			
			assertEquals("normaprueba2", result.get(0).get(0));
			assertEquals("plana", result.get(0).get(1));
			assertEquals("positionName", result.get(0).get(2));
			assertEquals("creator", result.get(0).get(3));
			
			assertEquals("normaprueba", result.get(1).get(0));
			assertEquals("plana", result.get(1).get(1));
			assertEquals("positionName", result.get(1).get(2));
			assertEquals("_", result.get(1).get(3));
			
			


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test(timeout = 5 * 60 * 1000)
	public void testInformTargetNormsCorrect11()
	{
		try
		{
			
			String unit = "plana";
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
		
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('Privado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('Publico',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

			

			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionnorm, normContent, normRule )" +
					" VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'), 'normaprueba', 3,2,(SELECT idroleList FROM roleList WHERE roleName = 'Publico') , 3,'normContent' ,'registerRole(_,_,_,_,_,) := null')");
			
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionnorm, normContent, normRule )" +
					" VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'), 'normaprueba2', 3,2,(SELECT idroleList FROM roleList WHERE roleName = 'Privado') , 2,'normContent' ,'registerRole(_,_,_,_,_,) := null')");
		
			
			ArrayList<ArrayList<String>> result = omsProxy.informTargetNorms("roleName", "", unit);
			
			assertEquals(1, result.size());
			
			
			assertEquals("normaprueba", result.get(0).get(0));
			assertEquals("plana", result.get(0).get(1));
			assertEquals("roleName", result.get(0).get(2));
			assertEquals("Publico", result.get(0).get(3));
			
			
			
			


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test(timeout = 5 * 60 * 1000)
	public void testInformTargetNormsCorrect12()
	{
		try
		{
			
			String unit = "plana";
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
		
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('Privado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('Publico',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('Creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'Creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");

			
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionnorm, normContent, normRule )" +
					" VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'), 'normaprueba', 3,2,(SELECT idroleList FROM roleList WHERE roleName = 'Publico') , 3,'normContent' ,'registerRole(_,_,_,_,_,) := null')");
			
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionnorm, normContent, normRule )" +
					" VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'), 'normaprueba2', 3,2,(SELECT idroleList FROM roleList WHERE roleName = 'Privado') , 2,'normContent' ,'registerRole(_,_,_,_,_,) := null')");
		
			
			ArrayList<ArrayList<String>> result = omsProxy.informTargetNorms("roleName", "", unit);
			
			assertEquals(2, result.size());
			
			
			assertEquals("normaprueba", result.get(0).get(0));
			assertEquals("plana", result.get(0).get(1));
			assertEquals("roleName", result.get(0).get(2));
			assertEquals("Publico", result.get(0).get(3));
			
			assertEquals("normaprueba2", result.get(1).get(0));
			assertEquals("plana", result.get(1).get(1));
			assertEquals("roleName", result.get(1).get(2));
			assertEquals("Privado", result.get(1).get(3));
			
			
			
			


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test(timeout = 5 * 60 * 1000)
	public void testInformTargetNormsCorrect13()
	{
		try
		{
			
			String unit = "plana";
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
		
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('Privado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('Publico',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('Creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'Creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");

			
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionnorm, normContent, normRule )" +
					" VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'), 'normaprueba', 3,3,-1, 3,'normContent' ,'registerRole(_,_,_,_,_,) := null')");
			
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionnorm, normContent, normRule )" +
					" VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'), 'normaprueba2', 3,3,(SELECT idposition FROM position WHERE positionName = 'creator') , 2,'normContent' ,'registerRole(_,_,_,_,_,) := null')");
		
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionnorm, normContent, normRule )" +
					" VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'), 'normaprueba3', 3,1,(SELECT idagentList FROM agentList WHERE agentName = 'pruebas') , 2,'normContent' ,'registerRole(_,_,_,_,_,) := null')");
		
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionnorm, normContent, normRule )" +
					" VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'), 'normaprueba4', 3,2,(SELECT idroleList FROM roleList WHERE roleName = 'Publico') , 2,'normContent' ,'registerRole(_,_,_,_,_,) := null')");
		
			dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionnorm, normContent, normRule )" +
					" VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'), 'normaprueba4', 3,2,(SELECT idroleList FROM roleList WHERE roleName = 'Privado') , 2,'normContent' ,'registerRole(_,_,_,_,_,) := null')");
	
			
			ArrayList<ArrayList<String>> result = omsProxy.informTargetNorms("agentName", "Creador", unit);
			
			assertEquals(0, result.size());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test(timeout = 5 * 60 * 1000)
	public void testInformTargetNormsCorrect14()
	{
		try
		{
			
			String unit = "plana";
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
		
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('Privado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('Publico',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('Creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'Creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");

			
		
			
			ArrayList<ArrayList<String>> result = omsProxy.informTargetNorms("positionName", "", unit);
			
			assertEquals(0, result.size());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	
}
