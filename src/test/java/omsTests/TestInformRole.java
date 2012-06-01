package omsTests;

import java.util.ArrayList;

import junit.framework.TestCase;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.SF;
import es.upv.dsic.gti_ia.organization.THOMASException;


public class TestInformRole extends TestCase {

	OMSProxy omsProxy = null;
	DatabaseAccess dbA = null;


	Agent agent = null;
	Agent agent2 = null;


	OMS oms = null;
	SF sf = null;
	public TestInformRole()
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


		//--------------------------------------------------------------------------------------------------//


		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('Plana2',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'Plana2'))");


		//--------------------------------------------------------------------------------------------------//


		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('Equipo',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'Equipo'))");


		//------------------------------------------------------------------------------------------//


		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('Equipo2',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'equipo'),(SELECT idunitList FROM unitList WHERE unitName = 'Equipo2'))");


		//------------------------------------------------------------------------------------------//

		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('Jerarquia',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'Jerarquia'))");



		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('Jerarquia2',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'Jerarquia2'))");

		









	}

	public void testInformRole1a()
	{
		try
		{

			String unit = "plana";
			String position = "member";
			String accesibility = "external";
			String visibility = "public";
			String roleName = "miembro2";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole("miembro2", "plana");

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole1b()
	{
		try
		{
			String visibility = "private";
			String accesibility = "external";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro2',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole("miembro2", "plana");

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals("member", result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}	
	public void testInformRole1c()
	{
		try
		{
			String visibility = "public";
			String accesibility = "internal";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro2',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole("miembro2", "plana");

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals("member", result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}	
	
	public void testInformRole1d()
	{
		try
		{
			String visibility = "private";
			String accesibility = "internal";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro2',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole("miembro2", "plana");

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals("member", result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}	
	
	public void testInformRole2a()
	{
		try
		{
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole("creador2", "plana");

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals("creator", result.get(0));
			assertEquals("public", result.get(1));
			assertEquals("external", result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole2b()
	{
		try
		{
			String visibility = "private";
			String accesibility = "external";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole("creador2", "plana");

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals("creator", result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}	
	public void testInformRole2c()
	{
		try
		{
			String visibility = "public";
			String accesibility = "internal";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole("creador2", "plana");

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals("creator", result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}	
	
	public void testInformRole2d()
	{
		try
		{
			String visibility = "private";
			String accesibility = "internal";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole("creador2", "plana");

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals("creator", result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}	
	
	public void testInformRole3a()
	{
		try
		{
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole("miembro", "plana");

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals("member", result.get(0));
			assertEquals("public", result.get(1));
			assertEquals("external", result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole3b()
	{
		try
		{
			String visibility = "private";
			String accesibility = "external";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole("miembro", "plana");

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals("member", result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}	
	public void testInformRole3c()
	{
		try
		{
			String visibility = "public";
			String accesibility = "internal";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole("miembro", "plana");

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals("member", result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}	
	
	public void testInformRole3d()
	{
		try
		{
			String visibility = "private";
			String accesibility = "internal";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole("miembro", "plana");

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals("member", result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole4a()
	{
		try
		{
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole("creador3", "plana");

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals("creator", result.get(0));
			assertEquals("public", result.get(1));
			assertEquals("external", result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole4b()
	{
		try
		{
			String visibility = "private";
			String accesibility = "external";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole("creador3", "plana");

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals("creator", result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}	
	public void testInformRole4c()
	{
		try
		{
			String visibility = "public";
			String accesibility = "internal";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole("creador3", "plana");

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals("creator", result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}	
	
	public void testInformRole4d()
	{
		try
		{
			String visibility = "private";
			String accesibility = "internal";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole("creador3", "plana");

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals("creator", result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole5a()
	{
		try
		{

			String unit = "equipo";
			String position = "member";
			String accesibility = "external";
			String visibility = "public";
			String roleName = "miembro2";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole("miembro2", "equipo");

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole5b()
	{
		try
		{
			String visibility = "private";
			String accesibility = "external";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole("miembro2", "equipo");

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals("member", result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}	
	public void testInformRole5c()
	{
		try
		{
			String visibility = "public";
			String accesibility = "internal";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole("miembro2", "equipo");

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals("member", result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}	
	
	public void testInformRole5d()
	{
		try
		{
			String visibility = "private";
			String accesibility = "internal";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole("miembro2", "equipo");

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals("member", result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}	
	
	public void testInformRole6a()
	{
		try
		{
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole("creador2", "equipo");

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals("creator", result.get(0));
			assertEquals("public", result.get(1));
			assertEquals("external", result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole6b()
	{
		try
		{
			String visibility = "private";
			String accesibility = "external";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole("creador2", "equipo");

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals("creator", result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}	
	public void testInformRole6c()
	{
		try
		{
			String visibility = "public";
			String accesibility = "internal";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole("creador2", "equipo");

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals("creator", result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}	
	
	public void testInformRole6d()
	{
		try
		{
			String visibility = "private";
			String accesibility = "internal";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole("creador2", "equipo");

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals("creator", result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}	
	
	public void testInformRole7a()
	{
		try
		{
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole("miembro", "equipo");

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals("member", result.get(0));
			assertEquals("public", result.get(1));
			assertEquals("external", result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole7b()
	{
		try
		{
			String visibility = "private";
			String accesibility = "external";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole("miembro", "equipo");

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals("member", result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}	
	public void testInformRole7c()
	{
		try
		{
			String visibility = "public";
			String accesibility = "internal";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole("miembro", "equipo");

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals("member", result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}	
	
	public void testInformRole7d()
	{
		try
		{
			String visibility = "private";
			String accesibility = "internal";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole("miembro", "equipo");

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals("member", result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole8a()
	{
		try
		{
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole("creador3", "equipo");

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals("creator", result.get(0));
			assertEquals("public", result.get(1));
			assertEquals("external", result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole8b()
	{
		try
		{
			String visibility = "private";
			String accesibility = "external";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole("creador3", "equipo");

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals("creator", result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}	
	public void testInformRole8c()
	{
		try
		{
			String visibility = "public";
			String accesibility = "internal";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole("creador3", "equipo");

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals("creator", result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}	
	
	public void testInformRole8d()
	{
		try
		{
			String visibility = "private";
			String accesibility = "internal";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole("creador3", "equipo");

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals("creator", result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	
	public void testInformRole9a()
	{
		try
		{

			String unit = "jerarquia";
			String position = "subordinate";
			String accesibility = "external";
			String visibility = "public";
			String roleName = "subordinado2";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	public void testInformRole9b()
	{
		try
		{

			String unit = "jerarquia";
			String position = "subordinate";
			String accesibility = "external";
			String visibility = "private";
			String roleName = "subordinado2";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	public void testInformRole9c()
	{
		try
		{

			String unit = "jerarquia";
			String position = "subordinate";
			String accesibility = "internal";
			String visibility = "public";
			String roleName = "subordinado2";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	public void testInformRole9d()
	{
		try
		{

			String unit = "jerarquia";
			String position = "subordinate";
			String accesibility = "internal";
			String visibility = "private";
			String roleName = "subordinado2";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole10a()
	{
		try
		{

			String unit = "jerarquia";
			String position = "subordinate";
			String accesibility = "external";
			String visibility = "public";
			String roleName = "subordinado";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	public void testInformRole10b()
	{
		try
		{

			String unit = "jerarquia";
			String position = "subordinate";
			String accesibility = "external";
			String visibility = "private";
			String roleName = "subordinado";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	public void testInformRole10c()
	{
		try
		{

			String unit = "jerarquia";
			String position = "subordinate";
			String accesibility = "internal";
			String visibility = "public";
			String roleName = "subordinado";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	public void testInformRole10d()
	{
		try
		{

			String unit = "jerarquia";
			String position = "subordinate";
			String accesibility = "internal";
			String visibility = "private";
			String roleName = "subordinado";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole11a()
	{
		try
		{

			String unit = "jerarquia";
			String position = "subordinate";
			String accesibility = "external";
			String visibility = "public";
			String roleName = "subordinado";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	public void testInformRole11b()
	{
		try
		{

			String unit = "jerarquia";
			String position = "subordinate";
			String accesibility = "external";
			String visibility = "private";
			String roleName = "subordinado";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	public void testInformRole11c()
	{
		try
		{

			String unit = "jerarquia";
			String position = "subordinate";
			String accesibility = "internal";
			String visibility = "public";
			String roleName = "subordinado";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	public void testInformRole11d()
	{
		try
		{

			String unit = "jerarquia";
			String position = "subordinate";
			String accesibility = "internal";
			String visibility = "private";
			String roleName = "subordinado";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole12a()
	{
		try
		{

			String unit = "jerarquia";
			String position = "supervisor";
			String accesibility = "external";
			String visibility = "public";
			String roleName = "supervisor";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	public void testInformRole12b()
	{
		try
		{

			String unit = "jerarquia";
			String position = "supervisor";
			String accesibility = "external";
			String visibility = "private";
			String roleName = "supervisor";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	public void testInformRole12c()
	{
		try
		{

			String unit = "jerarquia";
			String position = "supervisor";
			String accesibility = "internal";
			String visibility = "public";
			String roleName = "supervisor";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	public void testInformRole12d()
	{
		try
		{

			String unit = "jerarquia";
			String position = "supervisor";
			String accesibility = "internal";
			String visibility = "private";
			String roleName = "supervisor";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole13a()
	{
		try
		{

			String unit = "jerarquia";
			String position = "supervisor";
			String accesibility = "external";
			String visibility = "public";
			String roleName = "supervisor2";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	public void testInformRole13b()
	{
		try
		{

			String unit = "jerarquia";
			String position = "supervisor";
			String accesibility = "external";
			String visibility = "private";
			String roleName = "supervisor2";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	public void testInformRole13c()
	{
		try
		{

			String unit = "jerarquia";
			String position = "supervisor";
			String accesibility = "internal";
			String visibility = "public";
			String roleName = "supervisor2";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	public void testInformRole13d()
	{
		try
		{

			String unit = "jerarquia";
			String position = "supervisor";
			String accesibility = "internal";
			String visibility = "private";
			String roleName = "supervisor2";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole14a()
	{
		try
		{

			String unit = "jerarquia";
			String position = "supervisor";
			String accesibility = "external";
			String visibility = "public";
			String roleName = "supervisor";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	public void testInformRole14b()
	{
		try
		{

			String unit = "jerarquia";
			String position = "supervisor";
			String accesibility = "external";
			String visibility = "private";
			String roleName = "supervisor";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	public void testInformRole14c()
	{
		try
		{

			String unit = "jerarquia";
			String position = "supervisor";
			String accesibility = "internal";
			String visibility = "public";
			String roleName = "supervisor";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	public void testInformRole14d()
	{
		try
		{

			String unit = "jerarquia";
			String position = "supervisor";
			String accesibility = "internal";
			String visibility = "private";
			String roleName = "supervisor";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole15a()
	{
		try
		{

			String unit = "jerarquia";
			String position = "creator";
			String accesibility = "external";
			String visibility = "public";
			String roleName = "creador2";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	public void testInformRole15b()
	{
		try
		{

			String unit = "jerarquia";
			String position = "creator";
			String accesibility = "external";
			String visibility = "private";
			String roleName = "creador2";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	public void testInformRole15c()
	{
		try
		{

			String unit = "jerarquia";
			String position = "creator";
			String accesibility = "internal";
			String visibility = "public";
			String roleName = "creador2";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	public void testInformRole15d()
	{
		try
		{

			String unit = "jerarquia";
			String position = "creator";
			String accesibility = "internal";
			String visibility = "private";
			String roleName = "creador2";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole16a()
	{
		try
		{

			String unit = "jerarquia";
			String position = "creator";
			String accesibility = "external";
			String visibility = "public";
			String roleName = "creador2";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	public void testInformRole16b()
	{
		try
		{

			String unit = "jerarquia";
			String position = "creator";
			String accesibility = "external";
			String visibility = "private";
			String roleName = "creador2";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	public void testInformRole16c()
	{
		try
		{

			String unit = "jerarquia";
			String position = "creator";
			String accesibility = "internal";
			String visibility = "public";
			String roleName = "creador2";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	public void testInformRole16d()
	{
		try
		{

			String unit = "jerarquia";
			String position = "creator";
			String accesibility = "internal";
			String visibility = "private";
			String roleName = "creador2";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	public void testInformRole17a()
	{
		try
		{

			String unit = "jerarquia";
			String position = "creator";
			String accesibility = "external";
			String visibility = "public";
			String roleName = "creador3";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	public void testInformRole17b()
	{
		try
		{

			String unit = "jerarquia";
			String position = "creator";
			String accesibility = "external";
			String visibility = "private";
			String roleName = "creador3";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	public void testInformRole17c()
	{
		try
		{

			String unit = "jerarquia";
			String position = "creator";
			String accesibility = "internal";
			String visibility = "public";
			String roleName = "creador3";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	public void testInformRole17d()
	{
		try
		{

			String unit = "jerarquia";
			String position = "creator";
			String accesibility = "internal";
			String visibility = "private";
			String roleName = "creador3";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole18a()
	{
		try
		{

			String unit = "plana";
			String unit2 = "plana2";
			String position = "member";
			String accesibility = "external";
			String visibility = "public";
			String roleName = "miembro2";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole18b()
	{
		try
		{

			String unit = "plana";
			String unit2 = "plana2";
			String position = "member";
			String accesibility = "internal";
			String visibility = "public";
			String roleName = "miembro2";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole19a()
	{
		try
		{

			String unit = "plana";
			String unit2 = "plana2";
			String position = "creator";
			String accesibility = "external";
			String visibility = "public";
			String roleName = "creador2";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole19b()
	{
		try
		{

			String unit = "plana";
			String unit2 = "plana2";
			String position = "creator";
			String accesibility = "internal";
			String visibility = "public";
			String roleName = "creador2";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole20a()
	{
		try
		{

			String unit = "plana";
			String unit2 = "plana2";
			String position = "member";
			String accesibility = "external";
			String visibility = "public";
			String roleName = "miembro";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole20b()
	{
		try
		{

			String unit = "plana";
			String unit2 = "plana2";
			String position = "member";
			String accesibility = "internal";
			String visibility = "public";
			String roleName = "miembro";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole21a()
	{
		try
		{

			String unit = "plana";
			String unit2 = "plana2";
			String position = "creator";
			String accesibility = "external";
			String visibility = "public";
			String roleName = "creador3";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole21b()
	{
		try
		{

			String unit = "plana";
			String unit2 = "plana2";
			String position = "creator";
			String accesibility = "internal";
			String visibility = "public";
			String roleName = "creador3";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole22a()
	{
		try
		{

			String unit = "equipo";
			String unit2 = "equipo2";
			String position = "member";
			String accesibility = "external";
			String visibility = "public";
			String roleName = "miembro2";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole22b()
	{
		try
		{

			String unit = "equipo";
			String unit2 = "equipo2";
			String position = "member";
			String accesibility = "internal";
			String visibility = "public";
			String roleName = "miembro2";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole23a()
	{
		try
		{

			String unit = "equipo";
			String unit2 = "equipo2";
			String position = "creator";
			String accesibility = "external";
			String visibility = "public";
			String roleName = "creador2";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole23b()
	{
		try
		{

			String unit = "equipo";
			String unit2 = "equipo2";
			String position = "creator";
			String accesibility = "internal";
			String visibility = "public";
			String roleName = "creador2";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole24a()
	{
		try
		{

			String unit = "equipo";
			String unit2 = "equipo2";
			String position = "member";
			String accesibility = "external";
			String visibility = "public";
			String roleName = "miembro";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole24b()
	{
		try
		{

			String unit = "equipo";
			String unit2 = "equipo2";
			String position = "member";
			String accesibility = "internal";
			String visibility = "public";
			String roleName = "miembro";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole25a()
	{
		try
		{

			String unit = "equipo";
			String unit2 = "equipo2";
			String position = "creator";
			String accesibility = "external";
			String visibility = "public";
			String roleName = "creador3";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole25b()
	{
		try
		{

			String unit = "equipo";
			String unit2 = "equipo2";
			String position = "creator";
			String accesibility = "internal";
			String visibility = "public";
			String roleName = "creador3";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole26a()
	{
		try
		{

			String unit = "jerarquia";
			String unit2 = "jerarquia2";
			String position = "subordinate";
			String accesibility = "external";
			String visibility = "public";
			String roleName = "subordinado2";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole26b()
	{
		try
		{

			String unit = "jerarquia";
			String unit2 = "jerarquia2";
			String position = "subordinate";
			String accesibility = "internal";
			String visibility = "public";
			String roleName = "subordinado2";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole27a()
	{
		try
		{

			String unit = "jerarquia";
			String unit2 = "jerarquia2";
			String position = "subordinate";
			String accesibility = "external";
			String visibility = "public";
			String roleName = "subordinado";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole27b()
	{
		try
		{

			String unit = "jerarquia";
			String unit2 = "jerarquia2";
			String position = "subordinate";
			String accesibility = "internal";
			String visibility = "public";
			String roleName = "subordinado";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole28a()
	{
		try
		{

			String unit = "jerarquia";
			String unit2 = "jerarquia2";
			String position = "subordinate";
			String accesibility = "external";
			String visibility = "public";
			String roleName = "subordinado";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole28b()
	{
		try
		{

			String unit = "jerarquia";
			String unit2 = "jerarquia2";
			String position = "subordinate";
			String accesibility = "internal";
			String visibility = "public";
			String roleName = "subordinado";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole29a()
	{
		try
		{

			String unit = "jerarquia";
			String unit2 = "jerarquia2";
			String position = "supervisor";
			String accesibility = "external";
			String visibility = "public";
			String roleName = "supervisor";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole29b()
	{
		try
		{

			String unit = "jerarquia";
			String unit2 = "jerarquia2";
			String position = "supervisor";
			String accesibility = "internal";
			String visibility = "public";
			String roleName = "supervisor";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole30a()
	{
		try
		{

			String unit = "jerarquia";
			String unit2 = "jerarquia2";
			String position = "supervisor";
			String accesibility = "external";
			String visibility = "public";
			String roleName = "supervisor2";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole30b()
	{
		try
		{

			String unit = "jerarquia";
			String unit2 = "jerarquia2";
			String position = "supervisor";
			String accesibility = "internal";
			String visibility = "public";
			String roleName = "supervisor2";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole31a()
	{
		try
		{

			String unit = "jerarquia";
			String unit2 = "jerarquia2";
			String position = "supervisor";
			String accesibility = "external";
			String visibility = "public";
			String roleName = "supervisor";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole31b()
	{
		try
		{

			String unit = "jerarquia";
			String unit2 = "jerarquia2";
			String position = "supervisor";
			String accesibility = "internal";
			String visibility = "public";
			String roleName = "supervisor";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole32a()
	{
		try
		{

			String unit = "jerarquia";
			String unit2 = "jerarquia2";
			String position = "creator";
			String accesibility = "external";
			String visibility = "public";
			String roleName = "creador2";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole32b()
	{
		try
		{

			String unit = "jerarquia";
			String unit2 = "jerarquia2";
			String position = "creator";
			String accesibility = "internal";
			String visibility = "public";
			String roleName = "creador2";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole33a()
	{
		try
		{

			String unit = "jerarquia";
			String unit2 = "jerarquia2";
			String position = "creator";
			String accesibility = "external";
			String visibility = "public";
			String roleName = "creador2";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole33b()
	{
		try
		{

			String unit = "jerarquia";
			String unit2 = "jerarquia2";
			String position = "creator";
			String accesibility = "internal";
			String visibility = "public";
			String roleName = "creador2";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole34a()
	{
		try
		{

			String unit = "jerarquia";
			String unit2 = "jerarquia2";
			String position = "creator";
			String accesibility = "external";
			String visibility = "public";
			String roleName = "creador3";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformRole34b()
	{
		try
		{

			String unit = "jerarquia";
			String unit2 = "jerarquia2";
			String position = "creator";
			String accesibility = "internal";
			String visibility = "public";
			String roleName = "creador3";
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('"+roleName+"',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = '"+position+"'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = '"+accesibility+"'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = '"+visibility+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<String> result = omsProxy.informRole(roleName, unit);

			assertEquals("El resultado debe ser 3", 3, result.size());
			
			assertEquals(position, result.get(0));
			assertEquals(visibility, result.get(1));
			assertEquals(accesibility, result.get(2));//visibility
			
			

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
