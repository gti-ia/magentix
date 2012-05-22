package omsTests;

import junit.framework.TestCase;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.NotCreatorInParentUnitException;
import es.upv.dsic.gti_ia.organization.NotCreatorInUnitException;
import es.upv.dsic.gti_ia.organization.NotInUnitAndNotCreatorException;
import es.upv.dsic.gti_ia.organization.NotPlaysAnyRoleException;
import es.upv.dsic.gti_ia.organization.NotSupervisorOrCreatorInUnitException;
import es.upv.dsic.gti_ia.organization.OMSProxy;


public class JointUnitInCorrectPermissionsTest extends TestCase {

	OMSProxy omsProxy = null;
	DatabaseAccess dbA = null;


	Agent agent = null;


	public JointUnitInCorrectPermissionsTest()
	{

	}

	protected void tearDown() throws Exception {




		dbA = null;
		omsProxy = null;

		agent.terminate();
		agent = null;





	}
	protected void setUp() throws Exception {
		super.setUp();


		AgentsConnection.connect();


		agent = new Agent(new AgentID("pruebas"));



		omsProxy = new OMSProxy(agent);

		dbA = new DatabaseAccess();

		//------------------Clean Data Base -----------//
		dbA.executeSQL("DELETE FROM agentPlayList");
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

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
				"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
				"(SELECT idposition FROM position WHERE position = 'member'), "+
				"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
		

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
				"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
				"(SELECT idpositmiembroion FROM position WHERE position = 'creator'), "+
				"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
		
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
				"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
				"(SELECT idposition FROM position WHERE position = 'member'), "+
				"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
		

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
				"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
				"(SELECT idpositmiembroion FROM position WHERE position = 'creator'), "+
				"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
		
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
				"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
				"(SELECT idpositmiembroion FROM position WHERE position = 'creator'), "+
				"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

		
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
				"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
				"(SELECT idpositmiembroion FROM position WHERE position = 'subordinate'), "+
				"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


		dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
		"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

		dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
		"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

		dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
		"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

	}

	
	public void testJointUnit1()
	{
		

		
		
		try
		{
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

			String result = omsProxy.jointUnit("equipo", "jerarquia");

			assertNull(result);

		}catch(NotCreatorInParentUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		
		try
		{
		
	
			String result = omsProxy.jointUnit("plana", "jerarquia");

			assertNull(result);

		}catch(NotCreatorInParentUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}		
	
	public void testJointUnit2()
	{
		

		
		
		try
		{
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");
			
		
			String result = omsProxy.jointUnit("equipo", "jerarquia");

			

			assertNull(result);

		}catch(NotCreatorInUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		
		try
		{
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");
			
		
			String result = omsProxy.jointUnit("plana", "jerarquia");

			

			assertNull(result);

		}catch(NotCreatorInUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}	
	
	//TODO Puede que salte la de que no es creador en la unidad hija o en la unidad padre, las dos son validas,
	//dependiendo de cual sea la primer comprobación.
	public void testJointUnit3()
	{
		

		
		
		try
		{
		
			
		
			String result = omsProxy.jointUnit("equipo", "jerarquia");

			

			assertNull(result);

		}catch(NotCreatorInUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		
		try
		{
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");
			
		
			String result = omsProxy.jointUnit("plana", "jerarquia");

			

			assertNull(result);

		}catch(NotCreatorInUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}	
}
