package TestOMS;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.SF;
import es.upv.dsic.gti_ia.organization.exception.NotCreatorInParentUnitException;
import es.upv.dsic.gti_ia.organization.exception.UnitExistsException;


public class TestRegisterUnitInCorrectPermissions {

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
		
		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
		
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
		"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

		
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('Plana',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'Plana'))");

		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('Equipo',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'Equipo'))");

		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('Jerarquia',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'Jerarquia'))");

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
				"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
				"(SELECT idposition FROM position WHERE positionName = 'member'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
				"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
		
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
				"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
				"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
		
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
				"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
	}

	@Test(timeout = 5 * 60 * 1000, expected = NotCreatorInParentUnitException.class)
	public void testRegisterUnit1a() throws Exception {
		omsProxy.registerUnit("jerarquia2", "hierarchy", "jerarquia", "creador");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotCreatorInParentUnitException.class)
	public void testRegisterUnit1b() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

		// Method call	
		omsProxy.registerUnit("jerarquia2", "hierarchy", "jerarquia", "creador");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotCreatorInParentUnitException.class)
	public void testRegisterUnit1c() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

		// Method call	
		omsProxy.registerUnit("jerarquia2", "hierarchy", "jerarquia", "creador");
	}

	@Test(timeout = 5 * 60 * 1000, expected = NotCreatorInParentUnitException.class)
	public void testRegisterUnit2a() throws Exception {
		omsProxy.registerUnit("equipo2", "team", "jerarquia", "creador");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotCreatorInParentUnitException.class)
	public void testRegisterUnit2b() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

		// Method call	
		omsProxy.registerUnit("equipo2", "team", "jerarquia", "creador");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotCreatorInParentUnitException.class)
	public void testRegisterUnit2c() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

		// Method call	
		omsProxy.registerUnit("equipo2", "team", "jerarquia", "creador");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotCreatorInParentUnitException.class)
	public void testRegisterUnit3a() throws Exception {
		omsProxy.registerUnit("plana2", "flat", "jerarquia", "creador");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotCreatorInParentUnitException.class)
	public void testRegisterUnit3b() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

		// Method call	
		omsProxy.registerUnit("plana2", "flat", "jerarquia", "creador");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotCreatorInParentUnitException.class)
	public void testRegisterUnit3c() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

		// Method call	
		omsProxy.registerUnit("plana2", "flat", "jerarquia", "creador");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotCreatorInParentUnitException.class)
	public void testRegisterUnit4a() throws Exception {
		omsProxy.registerUnit("equipo2", "team", "equipo", "creador");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotCreatorInParentUnitException.class)
	public void testRegisterUnit4b() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

		// Method call	
		omsProxy.registerUnit("equipo2", "team", "equipo", "creador");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotCreatorInParentUnitException.class)
	public void testRegisterUnit5a() throws Exception {
		omsProxy.registerUnit("jerarquia2", "hierarchy", "equipo", "creador");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotCreatorInParentUnitException.class)
	public void testRegisterUnit5b() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

		// Method call	
		omsProxy.registerUnit("jerarquia2", "hierarchy", "equipo", "creador");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotCreatorInParentUnitException.class)
	public void testRegisterUnit6a() throws Exception {
		omsProxy.registerUnit("plana2", "flat", "equipo", "creador");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotCreatorInParentUnitException.class)
	public void testRegisterUnit6b() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

		// Method call	
		omsProxy.registerUnit("plana2", "flat", "equipo", "creador");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotCreatorInParentUnitException.class)
	public void testRegisterUnit7a() throws Exception {
		omsProxy.registerUnit("plana2", "flat", "plana", "creador");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotCreatorInParentUnitException.class)
	public void testRegisterUnit7b() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

		// Method call	
		omsProxy.registerUnit("plana2", "flat", "plana", "creador");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotCreatorInParentUnitException.class)
	public void testRegisterUnit8a() throws Exception {
		omsProxy.registerUnit("jerarquia2", "hierarchy", "plana", "creador");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotCreatorInParentUnitException.class)
	public void testRegisterUnit8b() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

		// Method call	
		omsProxy.registerUnit("jerarquia2", "hierarchy", "plana", "creador");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotCreatorInParentUnitException.class)
	public void testRegisterUnit9a() throws Exception {
		omsProxy.registerUnit("equipo2", "team", "plana", "creador");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotCreatorInParentUnitException.class)
	public void testRegisterUnit9b() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

		// Method call	
		omsProxy.registerUnit("equipo2", "team", "plana", "creador");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = UnitExistsException.class)
	public void testRegisterUnit10() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

		// Method call	
		omsProxy.registerUnit("Plana", "flat", "virtual", "creador");
	}
}
