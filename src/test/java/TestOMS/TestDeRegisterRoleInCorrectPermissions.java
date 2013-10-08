package TestOMS;

import junit.framework.TestCase;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.SF;
import es.upv.dsic.gti_ia.organization.exception.AgentNotInUnitException;
import es.upv.dsic.gti_ia.organization.exception.NotInUnitAndNotCreatorException;
import es.upv.dsic.gti_ia.organization.exception.NotSupervisorOrCreatorInUnitException;
import es.upv.dsic.gti_ia.organization.exception.RoleInUseException;


public class TestDeRegisterRoleInCorrectPermissions extends TestCase {

	OMSProxy omsProxy = null;
	DatabaseAccess dbA = null;


	Agent agent = null;

	OMS oms = null;
	SF sf = null;

	public TestDeRegisterRoleInCorrectPermissions()
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
				"('miembro2',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
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
				"('miembro2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
				"(SELECT idposition FROM position WHERE positionName = 'member'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
				"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

		
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
				"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('subordinado2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
				"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
	




	}

	
	public void testDeRegisterRole1()
	{
		

		
		
		try
		{
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

			String result = omsProxy.deregisterRole("miembro2", "plana");

			fail(result);

		}catch(NotInUnitAndNotCreatorException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}		
	
	public void testDeRegisterRole2()
	{
		

		
		
		try
		{
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");


			String result = omsProxy.deregisterRole("miembro2", "equipo");

			fail(result);

		}catch(AgentNotInUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}	
	
	public void testDeRegisterRole3()
	{

		try
		{
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");


			String result = omsProxy.deregisterRole("subordinado2", "jerarquia");

			fail(result);

		}catch(NotSupervisorOrCreatorInUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}	
	
	public void testDeRegisterRole4()
	{

		try
		{
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");


			String result = omsProxy.deregisterRole("miembro", "plana");

			fail(result);

		}catch(RoleInUseException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
}
