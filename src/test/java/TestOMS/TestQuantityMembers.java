package TestOMS;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import junit.framework.TestCase;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.SF;
import es.upv.dsic.gti_ia.organization.exception.THOMASException;


public class TestQuantityMembers extends TestCase {

	OMSProxy omsProxy = null;
	DatabaseAccess dbA = null;


	Agent agent = null;
	Agent agent2 = null;


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

		agent2.terminate();
		agent2 = null;

		oms.Shutdown();
		sf.Shutdown();
		
		oms.await();
		sf.await();
		
		oms = null;
		sf = null;

		AgentsConnection.disconnect();
		qpid_broker.destroy();
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

		agent2 = new Agent(new AgentID("pruebas2"));


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
				"('manager',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
				"(SELECT idposition FROM position WHERE positionName = 'member'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


		//--------------------------------------------------------------------------------------------------//



		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('Equipo',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'Equipo'))");

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
				"('manager',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
				"(SELECT idposition FROM position WHERE positionName = 'member'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
		//------------------------------------------------------------------------------------------//

		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('Jerarquia',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'Jerarquia'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
				"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
				"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
				"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas2')");
		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
		
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
		"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas2'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

		
		

		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
		"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas2'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
		"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas2'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
		"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas2'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
		"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas2'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
		"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas2'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
		"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas2'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
		"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas2'),(SELECT idroleList FROM roleList WHERE (roleName = 'manager' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
		"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas2'),(SELECT idroleList FROM roleList WHERE (roleName = 'manager' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
		"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas2'),(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");










	}

	public void testQuantityMembers1a()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.informQuantityMembers("jerarquia", "", "");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("equipo", "", "");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("plana", "", "");

			assertEquals("El resultado debe ser 1", 1, result);


			//---------------------------------------------------------------------//


		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	public void testQuantityMembers1b()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.informQuantityMembers("jerarquia", "supervisor", "");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("equipo", "manager", "");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("plana", "miembro", "");

			assertEquals("El resultado debe ser 1", 1, result);


			//---------------------------------------------------------------------//


		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{

			int result = omsProxy.informQuantityMembers("jerarquia", "creador", "");



		}catch(THOMASException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}


	public void testQuantityMembers1c()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.informQuantityMembers("jerarquia", "", "subordinate");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("equipo", "", "member");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("plana", "", "member");

			assertEquals("El resultado debe ser 1", 1, result);


			//---------------------------------------------------------------------//


		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{

			int result = omsProxy.informQuantityMembers("jerarquia", "", "creator");



		}catch(THOMASException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	public void testQuantityMembers1d()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.informQuantityMembers("jerarquia", "subordinado", "subordinate");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("equipo", "manager", "member");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("plana", "miembro", "member");

			assertEquals("El resultado debe ser 1", 1, result);


			//---------------------------------------------------------------------//


		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{

			int result = omsProxy.informQuantityMembers("jerarquia", "creador", "creator");



		}catch(THOMASException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	public void testQuantityMembers2a()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");



			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.informQuantityMembers("jerarquia", "", "");

			assertEquals("El resultado debe ser 2", 2, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("equipo", "", "");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("plana", "", "");

			assertEquals("El resultado debe ser 1", 1, result);


			//---------------------------------------------------------------------//


		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	public void testQuantityMembers2b()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");


			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.informQuantityMembers("jerarquia", "supervisor", "");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("equipo", "manager", "");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("plana", "miembro", "");

			assertEquals("El resultado debe ser 1", 1, result);


			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("jerarquia", "creador", "");

			assertEquals("El resultado debe ser 1", 1, result);


		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}


	}


	public void testQuantityMembers2c()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");


			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.informQuantityMembers("jerarquia", "", "subordinate");

			assertEquals("El resultado debe ser 2", 2, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("equipo", "", "member");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("plana", "", "member");

			assertEquals("El resultado debe ser 1", 1, result);


			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("jerarquia", "", "creator");

			assertEquals("El resultado debe ser 1", 1, result);


		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}

	public void testQuantityMembers2d()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");


			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.informQuantityMembers("jerarquia", "subordinado", "subordinate");

			assertEquals("El resultado debe ser 2", 2, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("equipo", "manager", "member");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("plana", "miembro", "member");

			assertEquals("El resultado debe ser 1", 1, result);


			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("jerarquia", "creador", "creator");

			assertEquals("El resultado debe ser 1", 1, result);


		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}

	public void testQuantityMembers3a()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");



			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.informQuantityMembers("jerarquia", "", "");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("equipo", "", "");

			assertEquals("El resultado debe ser 2", 2, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("plana", "", "");

			assertEquals("El resultado debe ser 1", 1, result);


			//---------------------------------------------------------------------//


		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	public void testQuantityMembers3b()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");


			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.informQuantityMembers("jerarquia", "supervisor", "");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("equipo", "manager", "");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("plana", "miembro", "");

			assertEquals("El resultado debe ser 1", 1, result);


			//---------------------------------------------------------------------//




		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}


		try
		{

			omsProxy.informQuantityMembers("jerarquia", "creador", "");



		}catch(THOMASException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}


	}


	public void testQuantityMembers3c()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");


			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.informQuantityMembers("jerarquia", "", "subordinate");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("equipo", "", "member");

			assertEquals("El resultado debe ser 2", 2, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("plana", "", "member");

			assertEquals("El resultado debe ser 1", 1, result);


			//---------------------------------------------------------------------//

	


		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		
		try
		{

			omsProxy.informQuantityMembers("jerarquia", "", "creator");



		}catch(THOMASException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}

	public void testQuantityMembers3d()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");


			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.informQuantityMembers("jerarquia", "subordinado", "subordinate");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("equipo", "manager", "member");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("plana", "miembro", "member");

			assertEquals("El resultado debe ser 1", 1, result);


			//---------------------------------------------------------------------//




		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		
		try
		{

			omsProxy.informQuantityMembers("jerarquia", "creador", "creator");



		}catch(THOMASException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testQuantityMembers4a()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");



			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.informQuantityMembers("jerarquia", "", "");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("equipo", "", "");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("plana", "", "");

			assertEquals("El resultado debe ser 2", 2, result);


			//---------------------------------------------------------------------//


		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	public void testQuantityMembers4b()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");


			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.informQuantityMembers("jerarquia", "supervisor", "");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("equipo", "manager", "");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("plana", "miembro", "");

			assertEquals("El resultado debe ser 2", 2, result);


			//---------------------------------------------------------------------//




		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}


		try
		{

			omsProxy.informQuantityMembers("jerarquia", "creador", "");



		}catch(THOMASException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}


	}


	public void testQuantityMembers4c()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");


			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.informQuantityMembers("jerarquia", "", "subordinate");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("equipo", "", "member");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("plana", "", "member");

			assertEquals("El resultado debe ser 2", 2, result);


			//---------------------------------------------------------------------//

	


		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		
		try
		{

			omsProxy.informQuantityMembers("jerarquia", "", "creator");



		}catch(THOMASException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}

	public void testQuantityMembers4d()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");


			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.informQuantityMembers("jerarquia", "subordinado", "subordinate");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("equipo", "manager", "member");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("plana", "miembro", "member");

			assertEquals("El resultado debe ser 2", 2, result);


			//---------------------------------------------------------------------//




		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		
		try
		{

			omsProxy.informQuantityMembers("jerarquia", "creador", "creator");



		}catch(THOMASException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testQuantityMembers5a()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");


			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.informQuantityMembers("jerarquia", "", "");

			assertEquals("El resultado debe ser 2", 2, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("equipo", "", "");

			assertEquals("El resultado debe ser 2", 2, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("plana", "", "");

			assertEquals("El resultado debe ser 1", 1, result);


			//---------------------------------------------------------------------//


		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	public void testQuantityMembers5b()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");


			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.informQuantityMembers("jerarquia", "supervisor", "");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("equipo", "manager", "");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("plana", "miembro", "");

			assertEquals("El resultado debe ser 1", 1, result);


			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("jerarquia", "creador", "");

			assertEquals("El resultado debe ser 1", 1, result);


		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}


	}


	public void testQuantityMembers5c()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

			
			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.informQuantityMembers("jerarquia", "", "subordinate");

			assertEquals("El resultado debe ser 2", 2, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("equipo", "", "member");

			assertEquals("El resultado debe ser 2", 2, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("plana", "", "member");

			assertEquals("El resultado debe ser 1", 1, result);


			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("jerarquia", "", "creator");

			assertEquals("El resultado debe ser 1", 1, result);


		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}

	public void testQuantityMembers5d()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.informQuantityMembers("jerarquia", "subordinado", "subordinate");

			assertEquals("El resultado debe ser 2", 2, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("equipo", "manager", "member");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("plana", "miembro", "member");

			assertEquals("El resultado debe ser 1", 1, result);


			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("jerarquia", "creador", "creator");

			assertEquals("El resultado debe ser 1", 1, result);


		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	
	public void testQuantityMembers6a()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");



			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.informQuantityMembers("jerarquia", "", "");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("equipo", "", "");

			assertEquals("El resultado debe ser 2", 2, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("plana", "", "");

			assertEquals("El resultado debe ser 2", 2, result);


			//---------------------------------------------------------------------//


		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	public void testQuantityMembers6b()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");


			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.informQuantityMembers("jerarquia", "supervisor", "");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("equipo", "manager", "");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("plana", "miembro", "");

			assertEquals("El resultado debe ser 2", 2, result);


			//---------------------------------------------------------------------//




		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}


		try
		{

			omsProxy.informQuantityMembers("jerarquia", "creador", "");



		}catch(THOMASException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}


	}


	public void testQuantityMembers6c()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");


			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.informQuantityMembers("jerarquia", "", "subordinate");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("equipo", "", "member");

			assertEquals("El resultado debe ser 2", 2, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("plana", "", "member");

			assertEquals("El resultado debe ser 2", 2, result);


			//---------------------------------------------------------------------//

	


		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		
		try
		{

			omsProxy.informQuantityMembers("jerarquia", "", "creator");



		}catch(THOMASException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}

	public void testQuantityMembers6d()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");



			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.informQuantityMembers("jerarquia", "subordinado", "subordinate");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("equipo", "manager", "member");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("plana", "miembro", "member");

			assertEquals("El resultado debe ser 2", 2, result);


			//---------------------------------------------------------------------//




		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		
		try
		{

			omsProxy.informQuantityMembers("jerarquia", "creador", "creator");



		}catch(THOMASException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	
	public void testQuantityMembers7a()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.informQuantityMembers("jerarquia", "", "");

			assertEquals("El resultado debe ser 2", 2, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("equipo", "", "");

			assertEquals("El resultado debe ser 2", 2, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("plana", "", "");

			assertEquals("El resultado debe ser 2", 2, result);


			//---------------------------------------------------------------------//


		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	public void testQuantityMembers7b()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.informQuantityMembers("jerarquia", "supervisor", "");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("equipo", "manager", "");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("plana", "miembro", "");

			assertEquals("El resultado debe ser 2", 2, result);


			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("jerarquia", "creador", "");

			assertEquals("El resultado debe ser 1", 1, result);


		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}


	}


	public void testQuantityMembers7c()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.informQuantityMembers("jerarquia", "", "subordinate");

			assertEquals("El resultado debe ser 2", 2, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("equipo", "", "member");

			assertEquals("El resultado debe ser 2", 2, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("plana", "", "member");

			assertEquals("El resultado debe ser 2", 2, result);


			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("jerarquia", "", "creator");

			assertEquals("El resultado debe ser 1", 1, result);


		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}

	public void testQuantityMembers7d()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.informQuantityMembers("jerarquia", "subordinado", "subordinate");

			assertEquals("El resultado debe ser 2", 2, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("equipo", "manager", "member");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("plana", "miembro", "member");

			assertEquals("El resultado debe ser 2", 2, result);


			//---------------------------------------------------------------------//

			result = omsProxy.informQuantityMembers("jerarquia", "creador", "creator");

			assertEquals("El resultado debe ser 1", 1, result);


		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
}
