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
import es.upv.dsic.gti_ia.organization.exception.EmptyParametersException;
import es.upv.dsic.gti_ia.organization.exception.InvalidPositionException;
import es.upv.dsic.gti_ia.organization.exception.MySQLException;
import es.upv.dsic.gti_ia.organization.exception.NotValidIdentifierException;
import es.upv.dsic.gti_ia.organization.exception.UnitNotExistsException;


public class TestRegisterRoleInCorrectParam {

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
				"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
				"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
		

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




		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
		"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
		"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
		"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");
	}

	@Test(timeout = 5 * 60 * 1000, expected = EmptyParametersException.class)
	public void testRegisterRole1() throws Exception {
		String result = omsProxy.registerRole("", "virtual", "external", "public", "member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = EmptyParametersException.class)
	public void testRegisterRole2() throws Exception {
		String result = omsProxy.registerRole("", "equipo", "external", "public", "member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = EmptyParametersException.class)
	public void testRegisterRole3() throws Exception {
		String result = omsProxy.registerRole("", "plana", "external", "public", "member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = EmptyParametersException.class)
	public void testRegisterRole4() throws Exception {
		String result = omsProxy.registerRole("", "jerarquia", "external", "public", "member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = EmptyParametersException.class)
	public void testRegisterRole5() throws Exception {
		String result = omsProxy.registerRole(null, "virtual", "external", "public", "member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = EmptyParametersException.class)
	public void testRegisterRole6() throws Exception {
		String result = omsProxy.registerRole(null, "equipo", "external", "public", "member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = EmptyParametersException.class)
	public void testRegisterRole7() throws Exception {
		String result = omsProxy.registerRole(null, "plana", "external", "public", "member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = EmptyParametersException.class)
	public void testRegisterRole8() throws Exception {
		String result = omsProxy.registerRole(null, "jerarquia", "external", "public", "member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000)
	public void testRegisterRole9()
	{
		try
		{

			String result = omsProxy.registerRole("miembro", "", "external", "public", "member");

			fail(result);

		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.registerRole("miembro", "", "external", "public", "member");
			fail(result);
		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.registerRole("miembro", "", "external", "public", "member");

			fail(result);
		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		
		try
		{
			String result = omsProxy.registerRole("subordinado", "", "external", "public", "member");

			fail(result);
		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole10() throws Exception {
		String result = omsProxy.registerRole("**Miunidad", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole11() throws Exception {
		String result = omsProxy.registerRole("team", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole12() throws Exception {
		String result = omsProxy.registerRole("flat", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole13() throws Exception {
		String result = omsProxy.registerRole("hierarchy", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole14() throws Exception {
		String result = omsProxy.registerRole("supervisor", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole15() throws Exception {
		String result = omsProxy.registerRole("subordinate", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole16() throws Exception {
		String result = omsProxy.registerRole("member", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole17() throws Exception {
		String result = omsProxy.registerRole("creator", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole18() throws Exception {
		String result = omsProxy.registerRole("private", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole19() throws Exception {
		String result = omsProxy.registerRole("public", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole20() throws Exception {
		String result = omsProxy.registerRole("external", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole21() throws Exception {
		String result = omsProxy.registerRole("internal", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole22() throws Exception {
		String result = omsProxy.registerRole("registerRole", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole23() throws Exception {
		String result = omsProxy.registerRole("deregisterRole", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole24() throws Exception {
		String result = omsProxy.registerRole("registerRole", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole25() throws Exception {
		String result = omsProxy.registerRole("deregisterRole", "plana", "external", "public","member");
		fail(result);
	}	
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole26() throws Exception {
		String result = omsProxy.registerRole("registerNorm", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole27() throws Exception {
		String result = omsProxy.registerRole("deregisterNorm", "plana", "external", "public","member");
		fail(result);
	}

	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole28() throws Exception {
		String result = omsProxy.registerRole("allocateRole", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole29() throws Exception {
		String result = omsProxy.registerRole("deallocateRole", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole30() throws Exception {
		String result = omsProxy.registerRole("joinUnit", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole31() throws Exception {
		String result = omsProxy.registerRole("informAgentRole", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole32() throws Exception {
		String result = omsProxy.registerRole("informMembers", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole33() throws Exception {
		String result = omsProxy.registerRole("informQuantityMembers", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole34() throws Exception {
		String result = omsProxy.registerRole("informUnit", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole35() throws Exception {
		String result = omsProxy.registerRole("informUnitRoles", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole36() throws Exception {
		String result = omsProxy.registerRole("informTargetNorms", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole37() throws Exception {
		String result = omsProxy.registerRole("informRole", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole38() throws Exception {
		String result = omsProxy.registerRole("informNorm", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole39() throws Exception {
		String result = omsProxy.registerRole("acquireRole", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole40() throws Exception {
		String result = omsProxy.registerRole("leaveRole", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole41() throws Exception {
		String result = omsProxy.registerRole("isNorm", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole42() throws Exception {
		String result = omsProxy.registerRole("hasDeontic", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole43() throws Exception {
		String result = omsProxy.registerRole("hasTarget", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole44() throws Exception {
		String result = omsProxy.registerRole("hasAction", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole45() throws Exception {
		String result = omsProxy.registerRole("isRole", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testregisterRole46() throws Exception {
		String result = omsProxy.registerRole("hasAccessibility", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole47() throws Exception {
		String result = omsProxy.registerRole("hasVisibility", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole48() throws Exception {
		String result = omsProxy.registerRole("hasPosition", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole49() throws Exception {
		String result = omsProxy.registerRole("isUnit", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole50() throws Exception {
		String result = omsProxy.registerRole("hasType", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole51() throws Exception {
		String result = omsProxy.registerRole("hasParent", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole52() throws Exception {
		String result = omsProxy.registerRole("div", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole53() throws Exception {
		String result = omsProxy.registerRole("mod", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testregisterRole54() throws Exception {
		String result = omsProxy.registerRole("not", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole55() throws Exception {
		String result = omsProxy.registerRole("_", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole56() throws Exception {
		String result = omsProxy.registerRole("agentName", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole57() throws Exception {
		String result = omsProxy.registerRole("roleName", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole58() throws Exception {
		String result = omsProxy.registerRole("positionName", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole59() throws Exception {
		String result = omsProxy.registerRole("o", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole60() throws Exception {
		String result = omsProxy.registerRole("f", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole61() throws Exception {
		String result = omsProxy.registerRole("p", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole62() throws Exception {
		String result = omsProxy.registerRole("*invalido", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole63() throws Exception {
		String result = omsProxy.registerRole("+invalido", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole64() throws Exception {
		String result = omsProxy.registerRole("?invalido", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole65() throws Exception {
		String result = omsProxy.registerRole("!invalido", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole66() throws Exception {
		String result = omsProxy.registerRole("invalido!", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole67() throws Exception {
		String result = omsProxy.registerRole("invalido?", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole68() throws Exception {
		String result = omsProxy.registerRole("invalido*", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole69() throws Exception {
		String result = omsProxy.registerRole("invalido+", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole70() throws Exception {
		String result = omsProxy.registerRole("!invalido", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterRole71() throws Exception {
		String result = omsProxy.registerRole("invalido-invalido", "plana", "external", "public","member");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000)
	public void testRegisterRole72()
	{
		try
		{

			String result = omsProxy.registerRole("miembro", null, "external", "public", "member");

			fail(result);

		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.registerRole("miembro", null, "external", "public", "member");
			fail(result);
		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.registerRole("miembro", null, "external", "public", "member");

			fail(result);
		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		
		try
		{
			String result = omsProxy.registerRole("subordinado", null, "external", "public", "member");

			fail(result);
		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	@Test(timeout = 5 * 60 * 1000)
	public void testRegisterRole73()
	{
		try
		{

			String result = omsProxy.registerRole("miembro", "inexistente", "external", "public", "member");

			fail(result);

		}catch(UnitNotExistsException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.registerRole("miembro", "inexistente", "external", "public", "member");
			fail(result);
		}catch(UnitNotExistsException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.registerRole("miembro", "inexistente", "external", "public", "member");

			fail(result);
		}catch(UnitNotExistsException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		
		try
		{
			String result = omsProxy.registerRole("subordinado", "inexistente", "external", "public", "member");

			fail(result);
		}catch(UnitNotExistsException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	@Test(timeout = 5 * 60 * 1000)
	public void testRegisterRole74()
	{
		try
		{

			String result = omsProxy.registerRole("miembro", "virtual", "", "public", "member");

			fail(result);

		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.registerRole("miembro", "equipo", "", "public", "member");
			fail(result);
		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.registerRole("miembro", "plana", "", "public", "member");

			fail(result);
		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		
		try
		{
			String result = omsProxy.registerRole("subordinado", "jerarquia", "", "public", "member");

			fail(result);
		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	@Test(timeout = 5 * 60 * 1000)
	public void testRegisterRole75()
	{
		try
		{

			String result = omsProxy.registerRole("miembro", "virtual", null, "public", "member");

			fail(result);

		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.registerRole("miembro", "equipo", null, "public", "member");
			fail(result);
		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.registerRole("miembro", "plana", null, "public", "member");

			fail(result);
		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		
		try
		{
			String result = omsProxy.registerRole("subordinado", "jerarquia", null, "public", "member");

			fail(result);
		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	@Test(timeout = 5 * 60 * 1000)
	public void testRegisterRole76()
	{
		try
		{

			String result = omsProxy.registerRole("miembro", "virtual", "inexistente", "public", "member");

			fail(result);

		}catch(MySQLException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.registerRole("miembro", "equipo", "inexistente", "public", "member");
			fail(result);
		}catch(MySQLException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.registerRole("miembro", "plana", "inexistente", "public", "member");

			fail(result);
		}catch(MySQLException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		
		try
		{
			String result = omsProxy.registerRole("subordinado", "jerarquia", "inexistente", "public", "subordinate");

			fail(result);
		}catch(MySQLException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	@Test(timeout = 5 * 60 * 1000)
	public void testRegisterRole77()
	{
		try
		{

			String result = omsProxy.registerRole("miembro", "virtual", "external", "", "member");

			fail(result);

		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.registerRole("miembro", "equipo", "external", "", "member");
			fail(result);
		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.registerRole("miembro", "plana", "external", "", "member");

			fail(result);
		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		
		try
		{
			String result = omsProxy.registerRole("subordinado", "jerarquia", "external", "", "member");

			fail(result);
		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	@Test(timeout = 5 * 60 * 1000)
	public void testRegisterRole78()
	{
		try
		{

			String result = omsProxy.registerRole("miembro", "virtual", "external", null, "member");

			fail(result);

		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.registerRole("miembro", "equipo", "external", null, "member");
			fail(result);
		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.registerRole("miembro", "plana", "external", null, "member");

			fail(result);
		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		
		try
		{
			String result = omsProxy.registerRole("subordinado", "jerarquia", "external", null, "member");

			fail(result);
		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	@Test(timeout = 5 * 60 * 1000)
	public void testRegisterRole79()
	{
		try
		{

			String result = omsProxy.registerRole("miembro", "virtual", "external", "inexistente", "member");

			fail(result);

		}catch(MySQLException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.registerRole("miembro", "equipo", "external", "inexistente", "member");
			fail(result);
		}catch(MySQLException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.registerRole("miembro", "plana", "external", "inexistente", "member");

			fail(result);
		}catch(MySQLException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		
		try
		{
			String result = omsProxy.registerRole("subordinado", "jerarquia", "external", "inexistente", "subordinate");

			fail(result);
		}catch(MySQLException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	@Test(timeout = 5 * 60 * 1000)
	public void testRegisterRole80()
	{
		try
		{

			String result = omsProxy.registerRole("miembro", "virtual", "external", "public", "");

			fail(result);

		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.registerRole("miembro", "equipo", "external", "public", "");
			fail(result);
		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.registerRole("miembro", "plana", "external", "public", "");

			fail(result);
		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		
		try
		{
			String result = omsProxy.registerRole("subordinado", "jerarquia", "external", "public", "");

			fail(result);
		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	@Test(timeout = 5 * 60 * 1000)
	public void testRegisterRole81()
	{
		try
		{

			String result = omsProxy.registerRole("miembro", "virtual", "external", "public", null);

			fail(result);

		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.registerRole("miembro", "equipo", "external", "public", null);
			fail(result);
		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.registerRole("miembro", "plana", "external", "public", null);

			fail(result);
		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		
		try
		{
			String result = omsProxy.registerRole("subordinado", "jerarquia", "external", "public", null);

			fail(result);
		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	@Test(timeout = 5 * 60 * 1000)
	public void testRegisterRole82()
	{
		try
		{

			String result = omsProxy.registerRole("miembro", "virtual", "external", "public", "inexistente");

			fail(result);

		}catch(InvalidPositionException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.registerRole("miembro", "equipo", "external", "public", "inexistente");
			fail(result);
		}catch(InvalidPositionException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.registerRole("miembro", "plana", "external", "public", "inexistente");

			fail(result);
		}catch(InvalidPositionException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		
		try
		{
			String result = omsProxy.registerRole("subordinado", "jerarquia", "external", "public", "inexistente");

			fail(result);
		}catch(InvalidPositionException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	@Test(timeout = 5 * 60 * 1000)
	public void testRegisterRole83()
	{
		try
		{

			String result = omsProxy.registerRole("miembro", "virtual", "external", "public", "supervisor");

			fail(result);

		}catch(InvalidPositionException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.registerRole("miembro", "equipo", "external", "public", "supervisor");
			fail(result);
		}catch(InvalidPositionException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.registerRole("miembro", "plana", "external", "public", "subordinate");

			fail(result);
		}catch(InvalidPositionException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		
		try
		{
			String result = omsProxy.registerRole("subordinado", "jerarquia", "external", "public", "member");

			fail(result);
		}catch(InvalidPositionException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	@Test(timeout = 5 * 60 * 1000)
	public void testRegisterRole84()
	{
		try
		{

			String result = omsProxy.registerRole("mi-embro", "virtual", "external", "public", "supervisor");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.registerRole("miembro", "equipo", "external", "public", "supervisor");
			fail(result);
		}catch(InvalidPositionException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.registerRole("miembro", "plana", "external", "public", "subordinate");

			fail(result);
		}catch(InvalidPositionException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		
		try
		{
			String result = omsProxy.registerRole("subordinado", "jerarquia", "external", "public", "member");

			fail(result);
		}catch(InvalidPositionException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}

}
