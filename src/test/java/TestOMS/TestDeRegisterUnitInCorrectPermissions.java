package TestOMS;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.SF;
import es.upv.dsic.gti_ia.organization.exception.EmptyParametersException;
import es.upv.dsic.gti_ia.organization.exception.NotCreatorAgentInUnitException;
import es.upv.dsic.gti_ia.organization.exception.NotCreatorInUnitOrParentUnitException;
import es.upv.dsic.gti_ia.organization.exception.SubunitsInUnitException;
import es.upv.dsic.gti_ia.organization.exception.UnitNotExistsException;
import es.upv.dsic.gti_ia.organization.exception.VirtualUnitException;


public class TestDeRegisterUnitInCorrectPermissions {

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
				"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
				"(SELECT idposition FROM position WHERE positionName = 'member'), "+
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
		
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
				"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
	}

	@Test(timeout = 5 * 60 * 1000, expected = UnitNotExistsException.class)
	public void testDeRegisterUnitIncorrectParam() throws Exception {
		omsProxy.deregisterUnit("inexistente");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotCreatorInUnitOrParentUnitException.class)
	public void testDeRegisterUnit1a1() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('jerarquia2',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'Jerarquia'),(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");

		// Method call	
		omsProxy.deregisterUnit("jerarquia2");
	}	
	
	@Test(timeout = 5 * 60 * 1000, expected = NotCreatorInUnitOrParentUnitException.class)
	public void testDeRegisterUnit1a2() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('jerarquia2',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'equipo'),(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");

		// Method call	
		omsProxy.deregisterUnit("jerarquia2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotCreatorInUnitOrParentUnitException.class)
	public void testDeRegisterUnit1a3() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('jerarquia2',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'plana'),(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");

		// Method call	
		omsProxy.deregisterUnit("jerarquia2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotCreatorInUnitOrParentUnitException.class)
	public void testDeRegisterUnit1b1() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('equipo2',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'))");

		// Method call	
		omsProxy.deregisterUnit("equipo2");
	}	
	
	@Test(timeout = 5 * 60 * 1000, expected = NotCreatorInUnitOrParentUnitException.class)
	public void testDeRegisterUnit1b2() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('equipo2',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'equipo'),(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'))");

		// Method call	
		omsProxy.deregisterUnit("equipo2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotCreatorInUnitOrParentUnitException.class)
	public void testDeRegisterUnit1b3() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('equipo2',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'plana'),(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'))");

		// Method call	
		omsProxy.deregisterUnit("equipo2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotCreatorInUnitOrParentUnitException.class)
	public void testDeRegisterUnit1c1() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('plana2',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),(SELECT idunitList FROM unitList WHERE unitName = 'plana2'))");

		// Method call	
		omsProxy.deregisterUnit("plana2");
	}	
	
	@Test(timeout = 5 * 60 * 1000, expected = NotCreatorInUnitOrParentUnitException.class)
	public void testDeRegisterUnit1c2() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('plana2',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'equipo'),(SELECT idunitList FROM unitList WHERE unitName = 'plana2'))");

		// Method call	
		omsProxy.deregisterUnit("plana2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotCreatorInUnitOrParentUnitException.class)
	public void testDeRegisterUnit1c3() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('plana2',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'plana'),(SELECT idunitList FROM unitList WHERE unitName = 'plana2'))");

		// Method call	
		omsProxy.deregisterUnit("plana2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = VirtualUnitException.class)
	public void testDeRegisterUnit2()throws Exception {
		omsProxy.deregisterUnit("virtual");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotCreatorAgentInUnitException.class)
	public void testDeRegisterUnit3a1() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('jerarquia2',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
		
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('jugador')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'jugador'),(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))))");

		// Method call	
		omsProxy.deregisterUnit("jerarquia2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotCreatorAgentInUnitException.class)
	public void testDeRegisterUnit3a2() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('jerarquia2',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'equipo'),(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
	
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('jugador')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'jugador'),(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))))");

		// Method call	
		omsProxy.deregisterUnit("jerarquia2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotCreatorAgentInUnitException.class)
	public void testDeRegisterUnit3a3() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('jerarquia2',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'plana'),(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
	
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('jugador')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'jugador'),(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))))");

		// Method call	
		omsProxy.deregisterUnit("jerarquia2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotCreatorAgentInUnitException.class)
	public void testDeRegisterUnit3b1() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('equipo2',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'member'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('jugador')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'jugador'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo2'))))");

		// Method call	
		omsProxy.deregisterUnit("equipo2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotCreatorAgentInUnitException.class)
	public void testDeRegisterUnit3b2() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('equipo2',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'equipo'),(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'member'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('jugador')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'jugador'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo2'))))");

		// Method call	
		omsProxy.deregisterUnit("equipo2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotCreatorAgentInUnitException.class)
	public void testDeRegisterUnit3b3() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('equipo2',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'plana'),(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'member'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('jugador')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'jugador'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo2'))))");

		// Method call	
		omsProxy.deregisterUnit("equipo2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotCreatorAgentInUnitException.class)
	public void testDeRegisterUnit3c1() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('plan2',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),(SELECT idunitList FROM unitList WHERE unitName = 'plan2'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'plan2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'member'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
		
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('jugador')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'jugador'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plan2'))))");

		// Method call	
		omsProxy.deregisterUnit("plan2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotCreatorAgentInUnitException.class)
	public void testDeRegisterUnit3c2() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('plan2',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'equipo'),(SELECT idunitList FROM unitList WHERE unitName = 'plan2'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'plan2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'member'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('jugador')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'jugador'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plan2'))))");

		// Method call	
		omsProxy.deregisterUnit("plan2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotCreatorAgentInUnitException.class)
	public void testDeRegisterUnit3c3() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('plan2',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'plana'),(SELECT idunitList FROM unitList WHERE unitName = 'plan2'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'plan2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'member'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('jugador')");
			
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'jugador'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plan2'))))");

		// Method call	
		omsProxy.deregisterUnit("plan2");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = SubunitsInUnitException.class)
	public void testDeRegisterUnit4a() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('jerarquia2',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");

		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");
			
		// Method call	
		omsProxy.deregisterUnit("jerarquia");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = SubunitsInUnitException.class)
	public void testDeRegisterUnit4b() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('equipo2',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'equipo'),(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'))");

		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");
			
		// Method call	
		omsProxy.deregisterUnit("equipo");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = SubunitsInUnitException.class)
	public void testDeRegisterUnit4c() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('plana2',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'plana'),(SELECT idunitList FROM unitList WHERE unitName = 'plana2'))");

		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");
			
		// Method call	
		omsProxy.deregisterUnit("plana");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = EmptyParametersException.class)
	public void testDeRegisterUnit5a() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('plana2',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'plana'),(SELECT idunitList FROM unitList WHERE unitName = 'plana2'))");

		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");
			
		// Method call	
		omsProxy.deregisterUnit(null);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = EmptyParametersException.class)
	public void testDeRegisterUnit5b() throws Exception {
		// Database Initialization
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('plana2',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'plana'),(SELECT idunitList FROM unitList WHERE unitName = 'plana2'))");

		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
				"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");
			
		// Method call	
		omsProxy.deregisterUnit("");
	}
}
