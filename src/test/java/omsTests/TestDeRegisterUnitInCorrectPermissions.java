package omsTests;

import junit.framework.TestCase;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.SF;
import es.upv.dsic.gti_ia.organization.exception.EmptyParametersException;
import es.upv.dsic.gti_ia.organization.exception.NotCreatorAgentInUnitException;
import es.upv.dsic.gti_ia.organization.exception.NotCreatorInUnitOrParentUnitException;
import es.upv.dsic.gti_ia.organization.exception.SubunitsInUnitException;
import es.upv.dsic.gti_ia.organization.exception.UnitNotExistsException;
import es.upv.dsic.gti_ia.organization.exception.VirtualUnitException;


public class TestDeRegisterUnitInCorrectPermissions extends TestCase {

	OMSProxy omsProxy = null;
	DatabaseAccess dbA = null;


	Agent agent = null;


	OMS oms = null;
	SF sf = null;
	
	public TestDeRegisterUnitInCorrectPermissions()
	{

	}

	protected void tearDown() throws Exception {




		dbA = null;
		omsProxy = null;

		agent.terminate();
		agent = null;



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



		omsProxy = new OMSProxy(agent);

		dbA = new DatabaseAccess();

		//------------------Clean Data Base -----------//
		dbA.executeSQL("DELETE FROM agentPlayList");
		dbA.executeSQL("DELETE FROM roleList WHERE idroleList != 1");
		dbA.executeSQL("DELETE FROM unitHierarchy WHERE idChildUnit != 1");
		dbA.executeSQL("DELETE FROM unitList WHERE idunitList != 1");

		//--------------------------------------------//
		
		
		dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
		"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

		
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
				"(SELECT idposition FROM position WHERE position = 'creator'), "+
				"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
		
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
				"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
				"(SELECT idposition FROM position WHERE position = 'member'), "+
				"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
				"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
				"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
				"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
		
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
				"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
				"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
				"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
		
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
				"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
				"(SELECT idposition FROM position WHERE position = 'creator'), "+
				"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


	




	}

	public void testDeRegisterUnitIncorrectParam()
	{
		
		
		try
		{
			String result = omsProxy.deregisterUnit("inexistente");

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
	
	public void testDeRegisterUnit1a1()
	{
		
		String unit = "jerarquia2";
		String unitType = "hierarchy";
		String parentUnit = "Jerarquia";
		
		
		try
		{
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = '"+unitType+"'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+parentUnit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");


			String result = omsProxy.deregisterUnit(unit);

			fail(result);

		}catch(NotCreatorInUnitOrParentUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}	
	public void testDeRegisterUnit1a2()
	{
		
		String unit = "jerarquia2";
		String unitType = "hierarchy";
		String parentUnit = "equipo";
		
		
		try
		{
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = '"+unitType+"'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+parentUnit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");


			String result = omsProxy.deregisterUnit(unit);

			fail(result);

		}catch(NotCreatorInUnitOrParentUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testDeRegisterUnit1a3()
	{
		
		String unit = "jerarquia2";
		String unitType = "hierarchy";
		String parentUnit = "plana";
		
		
		try
		{
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = '"+unitType+"'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+parentUnit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");


			String result = omsProxy.deregisterUnit(unit);

			fail(result);

		}catch(NotCreatorInUnitOrParentUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testDeRegisterUnit1b1()
	{
		
		String unit = "equipo2";
		String unitType = "team";
		String parentUnit = "jerarquia";
		
		
		try
		{
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = '"+unitType+"'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+parentUnit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");


			String result = omsProxy.deregisterUnit(unit);

			fail(result);

		}catch(NotCreatorInUnitOrParentUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}	
	public void testDeRegisterUnit1b2()
	{
		
		String unit = "equipo2";
		String unitType = "team";
		String parentUnit = "equipo";
		
		
		try
		{
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = '"+unitType+"'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+parentUnit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");


			String result = omsProxy.deregisterUnit(unit);

			fail(result);

		}catch(NotCreatorInUnitOrParentUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testDeRegisterUnit1b3()
	{
		
		String unit = "equipo2";
		String unitType = "team";
		String parentUnit = "plana";
		
		
		try
		{
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = '"+unitType+"'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+parentUnit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");


			String result = omsProxy.deregisterUnit(unit);

			fail(result);

		}catch(NotCreatorInUnitOrParentUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testDeRegisterUnit1c1()
	{
		
		String unit = "plana2";
		String unitType = "flat";
		String parentUnit = "jerarquia";
		
		
		try
		{
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = '"+unitType+"'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+parentUnit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");


			String result = omsProxy.deregisterUnit(unit);

			fail(result);

		}catch(NotCreatorInUnitOrParentUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}	
	public void testDeRegisterUnit1c2()
	{
		
		String unit = "plana2";
		String unitType = "flat";
		String parentUnit = "equipo";
		
		
		try
		{
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = '"+unitType+"'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+parentUnit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");


			String result = omsProxy.deregisterUnit(unit);

			fail(result);

		}catch(NotCreatorInUnitOrParentUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testDeRegisterUnit1c3()
	{
		
		String unit = "plana2";
		String unitType = "flat";
		String parentUnit = "plana";
		
		
		try
		{
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = '"+unitType+"'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+parentUnit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");


			String result = omsProxy.deregisterUnit(unit);

			fail(result);

		}catch(NotCreatorInUnitOrParentUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testDeRegisterUnit2()
	{
		
		
		try
		{
		
			String result = omsProxy.deregisterUnit("virtual");

			fail(result);

		}catch(VirtualUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testDeRegisterUnit3a1()
	{
		
		String unit = "jerarquia2";
		String unitType = "hierarchy";
		String parentUnit = "jerarquia";
		
		
		try
		{
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = '"+unitType+"'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+parentUnit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
		
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+parentUnit+"'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
					"('jugador',(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");

		

			String result = omsProxy.deregisterUnit(unit);

			fail(result);

		}catch(NotCreatorAgentInUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testDeRegisterUnit3a2()
	{
		
		String unit = "jerarquia2";
		String unitType = "hierarchy";
		String parentUnit = "equipo";
		
		
		try
		{
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = '"+unitType+"'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+parentUnit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
	
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+parentUnit+"'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
					"('jugador',(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");

		

			String result = omsProxy.deregisterUnit(unit);

			fail(result);

		}catch(NotCreatorAgentInUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testDeRegisterUnit3a3()
	{
		
		String unit = "jerarquia2";
		String unitType = "hierarchy";
		String parentUnit = "plana";
		
		
		try
		{
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = '"+unitType+"'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+parentUnit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
	
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+parentUnit+"'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('jugador',(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");

		

			String result = omsProxy.deregisterUnit(unit);

			fail(result);

		}catch(NotCreatorAgentInUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	
	public void testDeRegisterUnit3b1()
	{
		
		String unit = "equipo2";
		String unitType = "team";
		String parentUnit = "jerarquia";
		
		
		try
		{
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = '"+unitType+"'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+parentUnit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
	
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+parentUnit+"'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
					"('jugador',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");

		

			String result = omsProxy.deregisterUnit(unit);

			fail(result);

		}catch(NotCreatorAgentInUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testDeRegisterUnit3b2()
	{
		
		String unit = "equipo2";
		String unitType = "team";
		String parentUnit = "equipo";
		
		
		try
		{
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = '"+unitType+"'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+parentUnit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+parentUnit+"'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
					"('jugador',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");

		

			String result = omsProxy.deregisterUnit(unit);

			fail(result);

		}catch(NotCreatorAgentInUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testDeRegisterUnit3b3()
	{
		
		String unit = "equipo2";
		String unitType = "team";
		String parentUnit = "plana";
		
		
		try
		{
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = '"+unitType+"'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+parentUnit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+parentUnit+"'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('jugador',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");

		

			String result = omsProxy.deregisterUnit(unit);

			fail(result);

		}catch(NotCreatorAgentInUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testDeRegisterUnit3c1()
	{
		
		String unit = "plan2";
		String unitType = "flat";
		String parentUnit = "jerarquia";
		
		
		try
		{
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = '"+unitType+"'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+parentUnit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+parentUnit+"'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
					"('jugador',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");

		

			String result = omsProxy.deregisterUnit(unit);

			fail(result);

		}catch(NotCreatorAgentInUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testDeRegisterUnit3c2()
	{
		
		String unit = "plan2";
		String unitType = "flat";
		String parentUnit = "equipo";
		
		
		try
		{
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = '"+unitType+"'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+parentUnit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+parentUnit+"'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
					"('jugador',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");

		

			String result = omsProxy.deregisterUnit(unit);

			fail(result);

		}catch(NotCreatorAgentInUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testDeRegisterUnit3c3()
	{
		
		String unit = "plan2";
		String unitType = "flat";
		String parentUnit = "plana";
		
		
		try
		{
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = '"+unitType+"'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+parentUnit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+parentUnit+"'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('jugador',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");

		

			String result = omsProxy.deregisterUnit(unit);

			fail(result);

		}catch(NotCreatorAgentInUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testDeRegisterUnit4a()
	{
		
		String unit = "jerarquia2";
		String unitType = "hierarchy";
		String parentUnit = "jerarquia";
		
		
		try
		{
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = '"+unitType+"'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+parentUnit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+parentUnit+"'))))");
			
			
		

			String result = omsProxy.deregisterUnit(parentUnit);

			fail(result);

		}catch(SubunitsInUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testDeRegisterUnit4b()
	{
		
		String unit = "equipo2";
		String unitType = "team";
		String parentUnit = "equipo";
		
		
		try
		{
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = '"+unitType+"'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+parentUnit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+parentUnit+"'))))");
			
			
		

			String result = omsProxy.deregisterUnit(parentUnit);

			fail(result);

		}catch(SubunitsInUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testDeRegisterUnit4c()
	{
		
		String unit = "plana2";
		String unitType = "flat";
		String parentUnit = "plana";
		
		
		try
		{
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = '"+unitType+"'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+parentUnit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+parentUnit+"'))))");
			
			
		

			String result = omsProxy.deregisterUnit(parentUnit);

			fail(result);

		}catch(SubunitsInUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testDeRegisterUnit5a()
	{
		
		String unit = "plana2";
		String unitType = "flat";
		String parentUnit = "plana";
		
		
		try
		{
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = '"+unitType+"'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+parentUnit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+parentUnit+"'))))");
			
			
		

			String result = omsProxy.deregisterUnit(null);

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
	
	public void testDeRegisterUnit5b()
	{
		
		String unit = "plana2";
		String unitType = "flat";
		String parentUnit = "plana";
		
		
		try
		{
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = '"+unitType+"'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+parentUnit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+parentUnit+"'))))");
			
			
		

			String result = omsProxy.deregisterUnit("");

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
	
	
}
