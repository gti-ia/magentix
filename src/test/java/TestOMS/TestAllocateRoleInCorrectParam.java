package TestOMS;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import junit.framework.TestCase;
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


public class TestAllocateRoleInCorrectParam extends TestCase {

	OMSProxy omsProxy = null;
	DatabaseAccess dbA = null;


	Agent agent = null;

	OMS oms = null;
	SF sf = null;

	Process qpid_broker;
	

	protected void tearDown() throws Exception {


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
	
	protected void setUp() throws Exception {
		super.setUp();

		qpid_broker = Runtime.getRuntime().exec("./installer/magentix2/bin/qpid-broker-0.20/bin/qpid-server");
		BufferedReader reader = new BufferedReader(new InputStreamReader(qpid_broker.getInputStream()));

		String line = reader.readLine();
		while (!line.contains("Qpid Broker Ready")) {
			line = reader.readLine();
		}

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
	
	public void testallocateRole5()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","**Miunidad");

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
	public void testallocateRole6()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","team");

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
	public void testallocateRole7()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","flat");

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
	public void testallocateRole8()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","hierarchy");

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
	public void testallocateRole9()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","supervisor");

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
	public void testallocateRole10()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","subordinate");

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
	public void testallocateRole11()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","member");

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
	public void testallocateRole12()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","creator");

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
	public void testallocateRole13()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","private");

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
	public void testallocateRole14()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","public");

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
	public void testallocateRole15()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","external");

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
	public void testallocateRole16()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","internal");

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
	public void testallocateRole17()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","allocateRole");

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
	public void testallocateRole18()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","deallocateRole");

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
	public void testallocateRole19()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","registerRole");

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
	public void testallocateRole20()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","deregisterRole");

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
	
	public void testallocateRole21()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","registerNorm");

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
	
	public void testallocateRole22()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","deregisterNorm");

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

	public void testallocateRole23()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","allocateRole");

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
	public void testallocateRole24()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","deallocateRole");

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
	public void testallocateRole25()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","joinUnit");

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
	public void testallocateRole26()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","informAgentRole");

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
	
	public void testallocateRole27()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","informMembers");

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
	public void testallocateRole28()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","informQuantityMembers");

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
	public void testallocateRole29()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","informUnit");

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
	public void testallocateRole30()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","informUnitRoles");

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
	public void testallocateRole31()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","informTargetNorms");

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
	public void testallocateRole32()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","informRole");

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
	public void testallocateRole33()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","informNorm");

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
	public void testallocateRole34()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","acquireRole");

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
	public void testallocateRole35()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","leaveRole");

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
	public void testallocateRole36()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","isNorm");

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
	public void testallocateRole37()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","hasDeontic");

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
	public void testallocateRole38()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","hasTarget");

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
	public void testallocateRole39()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","hasAction");

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
	
	public void testallocateRole40()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","isRole");

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
	public void testallocateRole41()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","hasAccessibility");

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
	public void testallocateRole42()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","hasVisibility");

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
	public void testallocateRole43()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","hasPosition");

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
	public void testallocateRole44()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","isUnit");

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
	public void testallocateRole45()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","hasType");

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
	public void testallocateRole46()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","hasParent");

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
	public void testallocateRole47()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","div");

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
	public void testallocateRole48()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","mod");

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
	public void testallocateRole49()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","not");

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
	public void testallocateRole50()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","_");

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
	public void testallocateRole51()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","agentName");

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
	public void testallocateRole52()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","roleName");

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
	public void testallocateRole53()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","positionName");

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
	public void testallocateRole54()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","o");

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
	public void testallocateRole55()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","f");

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
	public void testallocateRole56()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","p");

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
	public void testallocateRole57()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","*invalido");

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
	public void testallocateRole58()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","+invalido");

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
	public void testallocateRole59()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","?invalido");

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
	public void testallocateRole60()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","!invalido");

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
	public void testallocateRole61()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","invalido!");

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
	public void testallocateRole65()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","invalido?");

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
	public void testallocateRole66()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","invalido*");

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
	public void testallocateRole67()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","invalido+");

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
	public void testallocateRole68()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","!invalido");

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
	public void testallocateRole69()
	{
		try
		{

			String result = omsProxy.allocateRole("subordinado", "equipo","invalido-invalido");

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
