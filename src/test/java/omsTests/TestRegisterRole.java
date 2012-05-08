package omsTests;

import junit.framework.TestCase;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.THOMASException;


public class TestRegisterRole extends TestCase {

	OMSProxy omsProxy = null;
	DatabaseAccess dbA = null;


	Agent agent = null;


	public TestRegisterRole()
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




	}

	public void testRegisterRole1()
	{
		try
		{
			
			String role = "miembro";
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

			

			String result = omsProxy.registerRole(role, "virtual", "external", "public", "member");
			assertEquals("El mensaje debe ser el siguiente:", role+ " created", result);
			
			role = "creador";
			
			result = omsProxy.registerRole(role, "virtual", "internal", "private", "creator");
			assertEquals("El mensaje debe ser el siguiente:", role+ " created", result);
		

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
	
	public void testRegisterRole2a()
	{
		try
		{
			
			String role = "miembro";
		
			String unit = "plana";
			String unit2 = "equipo";
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");


			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('participante',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

			

			String result = omsProxy.registerRole(role, "plana", "external", "public", "member");
			assertEquals("El mensaje debe ser el siguiente:", role+ " created", result);
			
			role = "creador2";
			
			result = omsProxy.registerRole(role, "plana", "internal", "private", "creator");
			assertEquals("El mensaje debe ser el siguiente:", role+ " created", result);
		

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
	
	public void testRegisterRole2b()
	{
		try
		{
			
			String role = "miembro";
		
			String unit = "plana";
			String unit2 = "equipo";
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");


			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('participante',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

			

			String result = omsProxy.registerRole(role, "plana", "external", "public", "member");
			assertEquals("El mensaje debe ser el siguiente:", role+ " created", result);
			
			role = "creador2";
			
			result = omsProxy.registerRole(role, "plana", "internal", "private", "creator");
			assertEquals("El mensaje debe ser el siguiente:", role+ " created", result);
		

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
	
	public void testRegisterRole2c()
	{
		try
		{
			
			String role = "miembro";
		
			String unit = "plana";
			String unit2 = "equipo";
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");


			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('participante',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'participante' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

			

			String result = omsProxy.registerRole(role, "plana", "external", "public", "member");
			assertEquals("El mensaje debe ser el siguiente:", role+ " created", result);
			
			role = "creador2";
			
			result = omsProxy.registerRole(role, "plana", "internal", "private", "creator");
			assertEquals("El mensaje debe ser el siguiente:", role+ " created", result);
		

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
	
	public void testRegisterRole2d()
	{
		try
		{
			
			String role = "miembro";
		
			String unit = "plana";
			String unit2 = "equipo";
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");


			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('participante',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

			

			String result = omsProxy.registerRole(role, "plana", "external", "public", "member");
			assertEquals("El mensaje debe ser el siguiente:", role+ " created", result);
			
			role = "creador2";
			
			result = omsProxy.registerRole(role, "plana", "internal", "private", "creator");
			assertEquals("El mensaje debe ser el siguiente:", role+ " created", result);
		

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
	
	
	public void testRegisterRole3a()
	{
		try
		{
			
			String role = "miembro";
		
			
			String unit2 = "equipo";
			
			
			
			
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");


			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('participante',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'participante' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

			

			String result = omsProxy.registerRole(role, "equipo", "external", "public", "member");
			assertEquals("El mensaje debe ser el siguiente:", role+ " created", result);
			
			role = "creador2";
			
			result = omsProxy.registerRole(role, "equipo", "internal", "private", "creator");
			assertEquals("El mensaje debe ser el siguiente:", role+ " created", result);
		

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
	
	public void testRegisterRole3b()
	{
		try
		{
			
			String role = "miembro";
		
			
			String unit2 = "equipo";
			
			
			
			
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");


			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('participante',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

			

			String result = omsProxy.registerRole(role, "equipo", "external", "public", "member");
			assertEquals("El mensaje debe ser el siguiente:", role+ " created", result);
			
			role = "creador2";
			
			result = omsProxy.registerRole(role, "equipo", "internal", "private", "creator");
			assertEquals("El mensaje debe ser el siguiente:", role+ " created", result);
		

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
	
	
	public void testRegisterRole4a()
	{
		try
		{
			
			String role = "miembro";
		
			
			String unit2 = "jerarquia";
			
			
			
			
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");


			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

			

			String result = omsProxy.registerRole(role, unit2, "external", "public", "supervisor");
			assertEquals("El mensaje debe ser el siguiente:", role+ " created", result);
			
			role = "creador2";
			
			result = omsProxy.registerRole(role, unit2, "internal", "private", "creator");
			assertEquals("El mensaje debe ser el siguiente:", role+ " created", result);
		

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
	
	public void testRegisterRole4b()
	{
		try
		{
			
			String role = "miembro";
		
			
			String unit2 = "jerarquia";
			
			
			
			
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");


			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

			

			String result = omsProxy.registerRole(role, unit2, "external", "public", "supervisor");
			assertEquals("El mensaje debe ser el siguiente:", role+ " created", result);
			
			role = "creador2";
			
			result = omsProxy.registerRole(role, unit2, "internal", "private", "creator");
			assertEquals("El mensaje debe ser el siguiente:", role+ " created", result);
		

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
}
