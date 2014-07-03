package TestOMS;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.SF;
import es.upv.dsic.gti_ia.organization.exception.NotInUnitOrParentUnitException;
import es.upv.dsic.gti_ia.organization.exception.NotSupervisorOrCreatorInUnitException;
import es.upv.dsic.gti_ia.organization.exception.PlayingRoleException;


public class TestAcquireRoleInCorrectPermissions {

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
		dbA.executeSQL("DELETE FROM normList");
		dbA.executeSQL("DELETE FROM roleList WHERE idroleList != 1");
		dbA.executeSQL("DELETE FROM unitHierarchy WHERE idChildUnit != 1");
		dbA.executeSQL("DELETE FROM unitList WHERE idunitList != 1");

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
		dbA.executeSQL("DELETE FROM roleList WHERE idroleList != 1");
		dbA.executeSQL("DELETE FROM unitHierarchy WHERE idChildUnit != 1");
		dbA.executeSQL("DELETE FROM unitList WHERE idunitList != 1");

		//--------------------------------------------//
		
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('Plana',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'Plana'))");
		
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('Plana2',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'Plana2'))");

		
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('Equipo',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'Equipo'))");

		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('Jerarquia',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'Jerarquia'))");
		
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('Jerarquia2',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'Jerarquia2'))");
	
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('Equipo2',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'Equipo2'))");

		
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
				"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
				"(SELECT idposition FROM position WHERE positionName = 'member'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
		
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
				"(SELECT idposition FROM position WHERE positionName = 'member'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
				"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
		
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'plana2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
				"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
		
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
				"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
		
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
				"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
		
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
				"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
		
		
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('subordinado2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
				"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotInUnitOrParentUnitException.class)
	public void testAcquireRole1a() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('miembro2',(SELECT idunitList FROM unitList WHERE unitName = 'plana2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'member'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");
		
		// Method call	
		omsProxy.acquireRole("miembro2","plana2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotInUnitOrParentUnitException.class)
	public void testAcquireRole1b() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('miembro2',(SELECT idunitList FROM unitList WHERE unitName = 'plana2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'member'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

		// Method call
		omsProxy.acquireRole("miembro2","plana2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotInUnitOrParentUnitException.class)
	public void testAcquireRole2a() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'plana2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

		// Method call
		omsProxy.acquireRole("creador2","plana2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotInUnitOrParentUnitException.class)
	public void testAcquireRole2b() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'plana2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

		// Method call
		omsProxy.acquireRole("creador2","plana2");
	}

	@Test(timeout = 5 * 60 * 1000, expected = NotInUnitOrParentUnitException.class)
	public void testAcquireRole3a() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'plana2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'member'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

		// Method call
		omsProxy.acquireRole("miembro","plana2");
	}

	@Test(timeout = 5 * 60 * 1000, expected = NotInUnitOrParentUnitException.class)
	public void testAcquireRole3b() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'plana2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'member'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

		// Method call
		omsProxy.acquireRole("miembro","plana2");
	}

	@Test(timeout = 5 * 60 * 1000, expected = NotInUnitOrParentUnitException.class)
	public void testAcquireRole4a() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'plana2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

		// Method call
		omsProxy.acquireRole("creador3","plana2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotInUnitOrParentUnitException.class)
	public void testAcquireRole4b() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'plana2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

		// Method call
		omsProxy.acquireRole("creador3","plana2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotInUnitOrParentUnitException.class)
	public void testAcquireRole5a() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('miembro2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'member'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

		// Method call
		omsProxy.acquireRole("miembro2","equipo2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotInUnitOrParentUnitException.class)
	public void testAcquireRole5b() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('miembro2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'member'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

		// Method call
		omsProxy.acquireRole("miembro2","equipo2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotInUnitOrParentUnitException.class)
	public void testAcquireRole6a() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

		// Method call
		omsProxy.acquireRole("creador2","equipo2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotInUnitOrParentUnitException.class)
	public void testAcquireRole6b() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

		// Method call
		omsProxy.acquireRole("creador2","equipo2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotInUnitOrParentUnitException.class)
	public void testAcquireRole7a() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'member'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

		// Method call
		omsProxy.acquireRole("miembro","equipo2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotInUnitOrParentUnitException.class)
	public void testAcquireRole7b() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'member'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

		// Method call
		omsProxy.acquireRole("miembro","equipo2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotInUnitOrParentUnitException.class)
	public void testAcquireRole8a() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

		// Method call
		omsProxy.acquireRole("creador3","equipo2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotInUnitOrParentUnitException.class)
	public void testAcquireRole8b() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

		// Method call
		omsProxy.acquireRole("creador3","equipo2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotInUnitOrParentUnitException.class)
	public void testAcquireRole9a() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('subordinado2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

		// Method call
		omsProxy.acquireRole("subordinado2","jerarquia2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotInUnitOrParentUnitException.class)
	public void testAcquireRole9b() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('subordinado2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

		// Method call
		omsProxy.acquireRole("subordinado2","jerarquia2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotInUnitOrParentUnitException.class)
	public void testAcquireRole10a() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

		// Method call
		omsProxy.acquireRole("subordinado","jerarquia2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotInUnitOrParentUnitException.class)
	public void testAcquireRole10b() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

		// Method call
		omsProxy.acquireRole("subordinado","jerarquia2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotInUnitOrParentUnitException.class)
	public void testAcquireRole11a() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

		// Method call
		omsProxy.acquireRole("subordinado","jerarquia2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotInUnitOrParentUnitException.class)
	public void testAcquireRole11b() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

		// Method call
		omsProxy.acquireRole("subordinado","jerarquia2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotInUnitOrParentUnitException.class)
	public void testAcquireRole12a() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

		// Method call
		omsProxy.acquireRole("supervisor","jerarquia2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotInUnitOrParentUnitException.class)
	public void testAcquireRole12b() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

		// Method call
		omsProxy.acquireRole("supervisor","jerarquia2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotInUnitOrParentUnitException.class)
	public void testAcquireRole13a() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('supervisor2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

		// Method call
		omsProxy.acquireRole("supervisor2","jerarquia2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotInUnitOrParentUnitException.class)
	public void testAcquireRole13b() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('supervisor2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

		// Method call
		omsProxy.acquireRole("supervisor2","jerarquia2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotInUnitOrParentUnitException.class)
	public void testAcquireRole14a() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

		// Method call
		omsProxy.acquireRole("supervisor","jerarquia2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotInUnitOrParentUnitException.class)
	public void testAcquireRole14b() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

		// Method call
		omsProxy.acquireRole("supervisor","jerarquia2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotInUnitOrParentUnitException.class)
	public void testAcquireRole15a() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

		// Method call
		omsProxy.acquireRole("creador2","jerarquia2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotInUnitOrParentUnitException.class)
	public void testAcquireRole15b() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

		// Method call
		omsProxy.acquireRole("creador2","jerarquia2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotInUnitOrParentUnitException.class)
	public void testAcquireRole16a() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

		// Method call
		omsProxy.acquireRole("creador2","jerarquia2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotInUnitOrParentUnitException.class)
	public void testAcquireRole16b() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

		// Method call
		omsProxy.acquireRole("creador2","jerarquia2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotInUnitOrParentUnitException.class)
	public void testAcquireRole17a() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

		// Method call
		omsProxy.acquireRole("creador3","jerarquia2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotInUnitOrParentUnitException.class)
	public void testAcquireRole17b() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

		// Method call
		omsProxy.acquireRole("creador3","jerarquia2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotSupervisorOrCreatorInUnitException.class)
	public void testAcquireRole18() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

		// Method call
		omsProxy.acquireRole("creador2","jerarquia");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotSupervisorOrCreatorInUnitException.class)
	public void testAcquireRole19() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

		// Method call
		omsProxy.acquireRole("supervisor","jerarquia");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = PlayingRoleException.class)
	public void testAcquireRole20() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

		// Method call
		omsProxy.acquireRole("subordinado","jerarquia");
	}
}
