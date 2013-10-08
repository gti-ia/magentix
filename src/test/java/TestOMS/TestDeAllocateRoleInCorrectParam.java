package TestOMS;

import junit.framework.TestCase;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.SF;
import es.upv.dsic.gti_ia.organization.exception.EmptyParametersException;
import es.upv.dsic.gti_ia.organization.exception.InvalidParametersException;
import es.upv.dsic.gti_ia.organization.exception.NotPlaysRoleException;
import es.upv.dsic.gti_ia.organization.exception.RoleNotExistsException;
import es.upv.dsic.gti_ia.organization.exception.SameAgentNameException;
import es.upv.dsic.gti_ia.organization.exception.UnitNotExistsException;


public class TestDeAllocateRoleInCorrectParam extends TestCase {

	OMSProxy omsProxy = null;
	DatabaseAccess dbA = null;


	Agent agent = null;


	OMS oms = null;
	SF sf = null;
	
	public TestDeAllocateRoleInCorrectParam()
	{

	}

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


	}
	protected void setUp() throws Exception {
		super.setUp();


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

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas2')");
		
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
		"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas2'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

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

		
		
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
		"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas2'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
		"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas2'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
		"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas2'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");



	}

	public void testDeAllocateRole1()
	{
		try
		{

			String result = omsProxy.deallocateRole("", "virtual", "pruebas2");

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
			String result = omsProxy.deallocateRole("", "equipo", "pruebas2");
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
			String result = omsProxy.deallocateRole("", "plana", "pruebas2");

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
			String result = omsProxy.deallocateRole("", "jerarquia", "pruebas2");

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
	
	public void testDeAllocateRole2()
	{
		try
		{

			String result = omsProxy.deallocateRole(null, "virtual", "pruebas2");

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
			String result = omsProxy.deallocateRole(null, "equipo", "pruebas2");
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
			String result = omsProxy.deallocateRole(null, "plana", "pruebas2");

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
			String result = omsProxy.deallocateRole(null, "jerarquia", "pruebas2");

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
	
	public void testDeAllocateRole3()
	{
		try
		{

			String result = omsProxy.deallocateRole("inexistente", "virtual", "pruebas2");

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
			String result = omsProxy.deallocateRole("inexistente", "equipo", "pruebas2");
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
			String result = omsProxy.deallocateRole("inexistente", "plana", "pruebas2");

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
			String result = omsProxy.deallocateRole("inexistente", "jerarquia", "pruebas2");

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
	public void testDeAllocateRole4()
	{
		try
		{

			String result = omsProxy.deallocateRole("miembro", "", "pruebas2");

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
			String result = omsProxy.deallocateRole("miembro", "", "pruebas2");
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
			String result = omsProxy.deallocateRole("miembro", "", "pruebas2");

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
			String result = omsProxy.deallocateRole("subordinado", "", "pruebas2");

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
	
	public void testDeAllocateRole5()
	{
		try
		{

			String result = omsProxy.deallocateRole("miembro", null, "pruebas2");

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
			String result = omsProxy.deallocateRole("miembro", null, "pruebas2");
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
			String result = omsProxy.deallocateRole("miembro", null, "pruebas2");

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
			String result = omsProxy.deallocateRole("subordinado", null, "pruebas2");

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
	
	public void testDeAllocateRole6()
	{
		try
		{

			String result = omsProxy.deallocateRole("miembro", "inexistente", "pruebas2");

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
			String result = omsProxy.deallocateRole("miembro", "inexistente", "pruebas2");
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
			String result = omsProxy.deallocateRole("miembro", "inexistente", "pruebas2");

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
			String result = omsProxy.deallocateRole("subordinado", "inexistente", "pruebas2");

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
	
	public void testDeAllocateRole7()
	{
		try
		{

			String result = omsProxy.deallocateRole("miembro", "virtual", "");

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
			String result = omsProxy.deallocateRole("miembro", "equipo", "");
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
			String result = omsProxy.deallocateRole("miembro", "plana", "");

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
			String result = omsProxy.deallocateRole("subordinado", "jerarquia", "");

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
	
	public void testDeAllocateRole8()
	{
		try
		{

			String result = omsProxy.deallocateRole("miembro", "virtual", null);

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
			String result = omsProxy.deallocateRole("miembro", "equipo", null);
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
			String result = omsProxy.deallocateRole("miembro", "plana", null);

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
			String result = omsProxy.deallocateRole("subordinado", "jerarquia", null);

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
	
	public void testDeAllocateRole9()
	{
		try
		{
			
			
			dbA.executeSQL("DELETE FROM `agentPlayList` WHERE idagentList = (SELECT idagentList FROM agentList WHERE agentName = 'pruebas2') AND idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))");

			dbA.executeSQL("DELETE FROM `agentPlayList` WHERE idagentList = (SELECT idagentList FROM agentList WHERE agentName = 'pruebas2') AND idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))");
			
			dbA.executeSQL("DELETE FROM `agentPlayList` WHERE idagentList = (SELECT idagentList FROM agentList WHERE agentName = 'pruebas2') AND idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))");
			
			dbA.executeSQL("DELETE FROM `agentPlayList` WHERE idagentList = (SELECT idagentList FROM agentList WHERE agentName = 'pruebas2') AND idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))");
			
			String result = omsProxy.deallocateRole("participant", "virtual", "pruebas2");

			fail(result);

		}catch(NotPlaysRoleException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.deallocateRole("creador", "equipo", "pruebas2");
			fail(result);
		}catch(NotPlaysRoleException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.deallocateRole("creador", "plana", "pruebas2");

			fail(result);
		}catch(NotPlaysRoleException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		
		try
		{
			String result = omsProxy.deallocateRole("creador", "jerarquia", "pruebas2");

			fail(result);
		}catch(NotPlaysRoleException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testAllocateRole10()
	{
		try
		{
			String result = omsProxy.deallocateRole("participant", "virtual", "pruebas");

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
			String result = omsProxy.deallocateRole("creador", "equipo", "pruebas");
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
			String result = omsProxy.deallocateRole("creador", "plana", "pruebas");

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
			String result = omsProxy.deallocateRole("creador", "jerarquia", "pruebas");

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
	
	
}
