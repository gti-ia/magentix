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
import es.upv.dsic.gti_ia.organization.exception.NotValidIdentifierException;
import es.upv.dsic.gti_ia.organization.exception.PlayingRoleException;
import es.upv.dsic.gti_ia.organization.exception.RoleNotExistsException;
import es.upv.dsic.gti_ia.organization.exception.SameAgentNameException;
import es.upv.dsic.gti_ia.organization.exception.UnitNotExistsException;


public class TestAllocateRoleInCorrectParam {

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
		dbA.executeSQL("DELETE FROM normList");
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

	@Test(timeout = 5 * 60 * 1000)
	public void testAllocateRole1()
	{
		try
		{

			String result = omsProxy.allocateRole("", "virtual", "pruebas2");

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
			String result = omsProxy.allocateRole("", "equipo", "pruebas2");
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
			String result = omsProxy.allocateRole("", "plana", "pruebas2");

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
			String result = omsProxy.allocateRole("", "jerarquia", "pruebas2");

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
	public void testAllocateRole2()
	{
		try
		{

			String result = omsProxy.allocateRole(null, "virtual", "pruebas2");

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
			String result = omsProxy.allocateRole(null, "equipo", "pruebas2");
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
			String result = omsProxy.allocateRole(null, "plana", "pruebas2");

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
			String result = omsProxy.allocateRole(null, "jerarquia", "pruebas2");

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
	public void testAllocateRole3()
	{
		try
		{

			String result = omsProxy.allocateRole("inexistente", "virtual", "pruebas2");

			fail(result);

		}catch(RoleNotExistsException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.allocateRole("inexistente", "equipo", "pruebas2");
			fail(result);
		}catch(RoleNotExistsException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.allocateRole("inexistente", "plana", "pruebas2");

			fail(result);
		}catch(RoleNotExistsException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		
		try
		{
			String result = omsProxy.allocateRole("inexistente", "jerarquia", "pruebas2");

			fail(result);
		}catch(RoleNotExistsException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	@Test(timeout = 5 * 60 * 1000)
	public void testAllocateRole4()
	{
		try
		{

			String result = omsProxy.allocateRole("miembro", "", "pruebas2");

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
			String result = omsProxy.allocateRole("miembro", "", "pruebas2");
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
			String result = omsProxy.allocateRole("miembro", "", "pruebas2");

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
			String result = omsProxy.allocateRole("subordinado", "", "pruebas2");

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
	public void testallocateRole5() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","**Miunidad");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole6() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","team");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole7() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","flat");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole8() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","hierarchy");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole9() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","supervisor");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole10() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","subordinate");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole11() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","member");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole12() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","creator");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole13() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","private");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole14() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","public");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole15() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","external");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole16() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","internal");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole17() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","allocateRole");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole18() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","deallocateRole");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole19() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","registerRole");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole20() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","deregisterRole");
	}	
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole21() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","registerNorm");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole22() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","deregisterNorm");
	}

	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole23() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","allocateRole");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole24() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","deallocateRole");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole25() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","joinUnit");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole26() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","informAgentRole");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole27() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","informMembers");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole28() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","informQuantityMembers");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole29() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","informUnit");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole30() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","informUnitRoles");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole31() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","informTargetNorms");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole32() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","informRole");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole33() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","informNorm");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole34() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","acquireRole");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole35() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","leaveRole");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole36() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","isNorm");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole37() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","hasDeontic");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole38() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","hasTarget");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole39() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","hasAction");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole40() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","isRole");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole41() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","hasAccessibility");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole42() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","hasVisibility");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole43() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","hasPosition");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole44() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","isUnit");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole45() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","hasType");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole46() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","hasParent");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole47() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","div");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole48() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","mod");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole49() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","not");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole50() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","_");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole51() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","agentName");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole52() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","roleName");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole53() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","positionName");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole54() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","o");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole55() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","f");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole56() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","p");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole57() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","*invalido");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole58() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","+invalido");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole59() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","?invalido");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole60() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","!invalido");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole61() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","invalido!");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole65() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","invalido?");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole66() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","invalido*");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole67() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","invalido+");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole68() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","!invalido");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testallocateRole69() throws Exception {
		omsProxy.allocateRole("subordinado", "equipo","invalido-invalido");
	}

	@Test(timeout = 5 * 60 * 1000)
	public void testAllocateRole70()
	{
		try
		{

			String result = omsProxy.allocateRole("miembro", null, "pruebas2");

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
			String result = omsProxy.allocateRole("miembro", null, "pruebas2");
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
			String result = omsProxy.allocateRole("miembro", null, "pruebas2");

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
			String result = omsProxy.allocateRole("subordinado", null, "pruebas2");

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
	public void testAllocateRole71()
	{
		try
		{

			String result = omsProxy.allocateRole("miembro", "inexistente", "pruebas2");

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
			String result = omsProxy.allocateRole("miembro", "inexistente", "pruebas2");
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
			String result = omsProxy.allocateRole("miembro", "inexistente", "pruebas2");

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
			String result = omsProxy.allocateRole("subordinado", "inexistente", "pruebas2");

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
	public void testAllocateRole72()
	{
		try
		{

			String result = omsProxy.allocateRole("miembro", "virtual", "");

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
			String result = omsProxy.allocateRole("miembro", "equipo", "");
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
			String result = omsProxy.allocateRole("miembro", "plana", "");

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
			String result = omsProxy.allocateRole("subordinado", "jerarquia", "");

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
	public void testAllocateRole73()
	{
		try
		{

			String result = omsProxy.allocateRole("miembro", "virtual", null);

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
			String result = omsProxy.allocateRole("miembro", "equipo", null);
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
			String result = omsProxy.allocateRole("miembro", "plana", null);

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
			String result = omsProxy.allocateRole("subordinado", "jerarquia", null);

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
	public void testAllocateRole74()
	{
		try
		{
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas2')");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas2'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas2'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas2'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas2'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

			String result = omsProxy.allocateRole("participant", "virtual", "pruebas2");

			fail(result);

		}catch(PlayingRoleException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.allocateRole("creador", "equipo", "pruebas2");
			fail(result);
		}catch(PlayingRoleException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.allocateRole("creador", "plana", "pruebas2");

			fail(result);
		}catch(PlayingRoleException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		
		try
		{
			String result = omsProxy.allocateRole("creador", "jerarquia", "pruebas2");

			fail(result);
		}catch(PlayingRoleException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	@Test(timeout = 5 * 60 * 1000)
	public void testAllocateRole75()
	{
		try
		{
			String result = omsProxy.allocateRole("participant", "virtual", "pruebas");

			fail(result);

		}catch(SameAgentNameException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.allocateRole("creador", "equipo", "pruebas");
			fail(result);
		}catch(SameAgentNameException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.allocateRole("creador", "plana", "pruebas");

			fail(result);
		}catch(SameAgentNameException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		
		try
		{
			String result = omsProxy.allocateRole("creador", "jerarquia", "pruebas");

			fail(result);
		}catch(SameAgentNameException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	@Test(timeout = 5 * 60 * 1000)
	public void testAllocateRole76()
	{
		try
		{
			String result = omsProxy.allocateRole("creador", "jerarquia", "mi-pruebas");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
}
