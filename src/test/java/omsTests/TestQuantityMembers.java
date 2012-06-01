package omsTests;

import junit.framework.TestCase;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.SF;
import es.upv.dsic.gti_ia.organization.THOMASException;


public class TestQuantityMembers extends TestCase {

	OMSProxy omsProxy = null;
	DatabaseAccess dbA = null;


	Agent agent = null;
	Agent agent2 = null;


	OMS oms = null;
	SF sf = null;
	public TestQuantityMembers()
	{

	}

	protected void tearDown() throws Exception {




		dbA = null;
		omsProxy = null;

		agent.terminate();
		agent = null;

		agent2.terminate();
		agent2 = null;

		oms.terminate();
		sf.terminate();
		
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

		agent2 = new Agent(new AgentID("pruebas2"));


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

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
				"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
				"(SELECT idposition FROM position WHERE position = 'member'), "+
				"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
				"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
				"(SELECT idposition FROM position WHERE position = 'creator'), "+
				"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
				"('manager',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
				"(SELECT idposition FROM position WHERE position = 'member'), "+
				"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


		//--------------------------------------------------------------------------------------------------//



		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('Equipo',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'Equipo'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
				"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
				"(SELECT idposition FROM position WHERE position = 'member'), "+
				"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
				"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
				"(SELECT idposition FROM position WHERE position = 'creator'), "+
				"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
				"('manager',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
				"(SELECT idposition FROM position WHERE position = 'member'), "+
				"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
		//------------------------------------------------------------------------------------------//

		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('Jerarquia',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'Jerarquia'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
				"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
				"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
				"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
				"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
				"(SELECT idposition FROM position WHERE position = 'creator'), "+
				"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
				"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
				"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
				"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


		dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
		"('pruebas2',(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


		dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
		"('pruebas2',(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

		dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
		"('pruebas2',(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

		dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
		"('pruebas2',(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

		dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
		"('pruebas2',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

		dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
		"('pruebas2',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

		dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
		"('pruebas2',(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

		dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
		"('pruebas2',(SELECT idroleList FROM roleList WHERE (roleName = 'manager' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

		dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
		"('pruebas2',(SELECT idroleList FROM roleList WHERE (roleName = 'manager' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

		dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
		"('pruebas2',(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");










	}

	public void testQuantityMembers1a()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.quantityMembers("jerarquia", "", "");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("equipo", "", "");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("plana", "", "");

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

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.quantityMembers("jerarquia", "supervisor", "");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("equipo", "manager", "");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("plana", "miembro", "");

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

			int result = omsProxy.quantityMembers("jerarquia", "creador", "");



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

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.quantityMembers("jerarquia", "", "subordinate");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("equipo", "", "member");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("plana", "", "member");

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

			int result = omsProxy.quantityMembers("jerarquia", "", "creator");



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

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.quantityMembers("jerarquia", "subordinado", "subordinate");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("equipo", "manager", "member");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("plana", "miembro", "member");

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

			int result = omsProxy.quantityMembers("jerarquia", "creador", "creator");



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

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");



			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.quantityMembers("jerarquia", "", "");

			assertEquals("El resultado debe ser 2", 2, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("equipo", "", "");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("plana", "", "");

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

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");


			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.quantityMembers("jerarquia", "supervisor", "");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("equipo", "manager", "");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("plana", "miembro", "");

			assertEquals("El resultado debe ser 1", 1, result);


			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("jerarquia", "creador", "");

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

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");


			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.quantityMembers("jerarquia", "", "subordinate");

			assertEquals("El resultado debe ser 2", 2, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("equipo", "", "member");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("plana", "", "member");

			assertEquals("El resultado debe ser 1", 1, result);


			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("jerarquia", "", "creator");

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

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");


			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.quantityMembers("jerarquia", "subordinado", "subordinate");

			assertEquals("El resultado debe ser 2", 2, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("equipo", "manager", "member");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("plana", "miembro", "member");

			assertEquals("El resultado debe ser 1", 1, result);


			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("jerarquia", "creador", "creator");

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

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");



			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.quantityMembers("jerarquia", "", "");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("equipo", "", "");

			assertEquals("El resultado debe ser 2", 2, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("plana", "", "");

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

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");


			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.quantityMembers("jerarquia", "supervisor", "");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("equipo", "manager", "");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("plana", "miembro", "");

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

			omsProxy.quantityMembers("jerarquia", "creador", "");



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

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");


			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.quantityMembers("jerarquia", "", "subordinate");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("equipo", "", "member");

			assertEquals("El resultado debe ser 2", 2, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("plana", "", "member");

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

			omsProxy.quantityMembers("jerarquia", "", "creator");



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

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");


			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.quantityMembers("jerarquia", "subordinado", "subordinate");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("equipo", "manager", "member");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("plana", "miembro", "member");

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

			omsProxy.quantityMembers("jerarquia", "creador", "creator");



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

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");



			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.quantityMembers("jerarquia", "", "");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("equipo", "", "");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("plana", "", "");

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

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");


			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.quantityMembers("jerarquia", "supervisor", "");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("equipo", "manager", "");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("plana", "miembro", "");

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

			omsProxy.quantityMembers("jerarquia", "creador", "");



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

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");


			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.quantityMembers("jerarquia", "", "subordinate");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("equipo", "", "member");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("plana", "", "member");

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

			omsProxy.quantityMembers("jerarquia", "", "creator");



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

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");


			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.quantityMembers("jerarquia", "subordinado", "subordinate");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("equipo", "manager", "member");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("plana", "miembro", "member");

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

			omsProxy.quantityMembers("jerarquia", "creador", "creator");



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

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");


			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.quantityMembers("jerarquia", "", "");

			assertEquals("El resultado debe ser 2", 2, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("equipo", "", "");

			assertEquals("El resultado debe ser 2", 2, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("plana", "", "");

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

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");


			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.quantityMembers("jerarquia", "supervisor", "");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("equipo", "manager", "");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("plana", "miembro", "");

			assertEquals("El resultado debe ser 1", 1, result);


			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("jerarquia", "creador", "");

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

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

			
			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.quantityMembers("jerarquia", "", "subordinate");

			assertEquals("El resultado debe ser 2", 2, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("equipo", "", "member");

			assertEquals("El resultado debe ser 2", 2, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("plana", "", "member");

			assertEquals("El resultado debe ser 1", 1, result);


			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("jerarquia", "", "creator");

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

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.quantityMembers("jerarquia", "subordinado", "subordinate");

			assertEquals("El resultado debe ser 2", 2, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("equipo", "manager", "member");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("plana", "miembro", "member");

			assertEquals("El resultado debe ser 1", 1, result);


			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("jerarquia", "creador", "creator");

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

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");



			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.quantityMembers("jerarquia", "", "");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("equipo", "", "");

			assertEquals("El resultado debe ser 2", 2, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("plana", "", "");

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

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");


			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.quantityMembers("jerarquia", "supervisor", "");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("equipo", "manager", "");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("plana", "miembro", "");

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

			omsProxy.quantityMembers("jerarquia", "creador", "");



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

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");


			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.quantityMembers("jerarquia", "", "subordinate");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("equipo", "", "member");

			assertEquals("El resultado debe ser 2", 2, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("plana", "", "member");

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

			omsProxy.quantityMembers("jerarquia", "", "creator");



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

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");



			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.quantityMembers("jerarquia", "subordinado", "subordinate");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("equipo", "manager", "member");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("plana", "miembro", "member");

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

			omsProxy.quantityMembers("jerarquia", "creador", "creator");



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

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.quantityMembers("jerarquia", "", "");

			assertEquals("El resultado debe ser 2", 2, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("equipo", "", "");

			assertEquals("El resultado debe ser 2", 2, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("plana", "", "");

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

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.quantityMembers("jerarquia", "supervisor", "");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("equipo", "manager", "");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("plana", "miembro", "");

			assertEquals("El resultado debe ser 2", 2, result);


			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("jerarquia", "creador", "");

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

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.quantityMembers("jerarquia", "", "subordinate");

			assertEquals("El resultado debe ser 2", 2, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("equipo", "", "member");

			assertEquals("El resultado debe ser 2", 2, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("plana", "", "member");

			assertEquals("El resultado debe ser 2", 2, result);


			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("jerarquia", "", "creator");

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

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

			/**-----------
			 * --1a							
			 * -----------
			 */




			int result = omsProxy.quantityMembers("jerarquia", "subordinado", "subordinate");

			assertEquals("El resultado debe ser 2", 2, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("equipo", "manager", "member");

			assertEquals("El resultado debe ser 1", 1, result);

			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("plana", "miembro", "member");

			assertEquals("El resultado debe ser 2", 2, result);


			//---------------------------------------------------------------------//

			result = omsProxy.quantityMembers("jerarquia", "creador", "creator");

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
