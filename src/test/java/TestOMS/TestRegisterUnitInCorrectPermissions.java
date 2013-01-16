package TestOMS;

import junit.framework.TestCase;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.SF;
import es.upv.dsic.gti_ia.organization.exception.NotCreatorInParentUnitException;
import es.upv.dsic.gti_ia.organization.exception.UnitExistsException;


public class TestRegisterUnitInCorrectPermissions extends TestCase {

	OMSProxy omsProxy = null;
	DatabaseAccess dbA = null;


	Agent agent = null;

	OMS oms = null;
	SF sf = null;
	public TestRegisterUnitInCorrectPermissions()
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
		dbA.executeSQL("DELETE FROM agentList");
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
				"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
				"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
				"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
		
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
				"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


	




	}

	public void testRegisterUnit1a()
	{
		
		String unit = "jerarquia2";
		String parentUnit = "jerarquia";
		String unitType = "hierarchy";
		
		try
		{
			

			String result = omsProxy.registerUnit(unit, unitType, parentUnit, "creador");

			fail(result);

		}catch(NotCreatorInParentUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testRegisterUnit1b()
	{
		
		String unit = "jerarquia2";
		String parentUnit = "jerarquia";
		String unitType = "hierarchy";
		
	
		
		try
		{
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

			

			String result = omsProxy.registerUnit(unit, unitType, parentUnit, "creador");

			fail(result);

		}catch(NotCreatorInParentUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testRegisterUnit1c()
	{
		
		String unit = "jerarquia2";
		String parentUnit = "jerarquia";
		String unitType = "hierarchy";
		
		try
		{
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

			String result = omsProxy.registerUnit(unit, unitType, parentUnit, "creador");

			fail(result);

		}catch(NotCreatorInParentUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}

	public void testRegisterUnit2a()
	{
		
		String unit = "equipo2";
		String parentUnit = "jerarquia";
		String unitType = "team";
		
		try
		{
			

			String result = omsProxy.registerUnit(unit, unitType, parentUnit, "creador");

			fail(result);

		}catch(NotCreatorInParentUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testRegisterUnit2b()
	{
		String unit = "equipo2";
		String parentUnit = "jerarquia";
		String unitType = "team";
	
		
		try
		{
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

			

			String result = omsProxy.registerUnit(unit, unitType, parentUnit, "creador");

			fail(result);

		}catch(NotCreatorInParentUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testRegisterUnit2c()
	{
		
		String unit = "equipo2";
		String parentUnit = "jerarquia";
		String unitType = "team";
		
		try
		{
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

			String result = omsProxy.registerUnit(unit, unitType, parentUnit, "creador");

			fail(result);

		}catch(NotCreatorInParentUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testRegisterUnit3a()
	{
		
		String unit = "plana2";
		String parentUnit = "jerarquia";
		String unitType = "flat";
		
		try
		{
			

			String result = omsProxy.registerUnit(unit, unitType, parentUnit, "creador");

			fail(result);

		}catch(NotCreatorInParentUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testRegisterUnit3b()
	{
		String unit = "plana2";
		String parentUnit = "jerarquia";
		String unitType = "flat";
	
		
		try
		{
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

			

			String result = omsProxy.registerUnit(unit, unitType, parentUnit, "creador");

			fail(result);

		}catch(NotCreatorInParentUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testRegisterUnit3c()
	{
		
		String unit = "plana2";
		String parentUnit = "jerarquia";
		String unitType = "flat";
		
		try
		{
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

			String result = omsProxy.registerUnit(unit, unitType, parentUnit, "creador");

			fail(result);

		}catch(NotCreatorInParentUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testRegisterUnit4a()
	{
		
		String unit = "equipo2";
		String parentUnit = "equipo";
		String unitType = "team";
		
		try
		{
			

			String result = omsProxy.registerUnit(unit, unitType, parentUnit, "creador");

			fail(result);

		}catch(NotCreatorInParentUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testRegisterUnit4b()
	{
		String unit = "equipo2";
		String parentUnit = "equipo";
		String unitType = "team";
	
		
		try
		{
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

			

			String result = omsProxy.registerUnit(unit, unitType, parentUnit, "creador");

			fail(result);

		}catch(NotCreatorInParentUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testRegisterUnit5a()
	{
		
		String unit = "jerarquia2";
		String parentUnit = "equipo";
		String unitType = "hierarchy";
		
		try
		{
			

			String result = omsProxy.registerUnit(unit, unitType, parentUnit, "creador");

			fail(result);

		}catch(NotCreatorInParentUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testRegisterUnit5b()
	{
		String unit = "jerarquia2";
		String parentUnit = "equipo";
		String unitType = "hierarchy";
		
		try
		{
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

			

			String result = omsProxy.registerUnit(unit, unitType, parentUnit, "creador");

			fail(result);

		}catch(NotCreatorInParentUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testRegisterUnit6a()
	{
		
		String unit = "plana2";
		String parentUnit = "equipo";
		String unitType = "flat";
		
		try
		{
			

			String result = omsProxy.registerUnit(unit, unitType, parentUnit, "creador");

			fail(result);

		}catch(NotCreatorInParentUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testRegisterUnit6b()
	{
		String unit = "plana2";
		String parentUnit = "equipo";
		String unitType = "flat";
		
		try
		{
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

			

			String result = omsProxy.registerUnit(unit, unitType, parentUnit, "creador");

			fail(result);

		}catch(NotCreatorInParentUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testRegisterUnit7a()
	{
		
		String unit = "plana2";
		String parentUnit = "plana";
		String unitType = "flat";
		
		try
		{
			

			String result = omsProxy.registerUnit(unit, unitType, parentUnit, "creador");

			fail(result);

		}catch(NotCreatorInParentUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testRegisterUnit7b()
	{
		String unit = "plana2";
		String parentUnit = "plana";
		String unitType = "flat";
		
		try
		{
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

			

			String result = omsProxy.registerUnit(unit, unitType, parentUnit, "creador");

			fail(result);

		}catch(NotCreatorInParentUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testRegisterUnit8a()
	{
		
		String unit = "jerarquia2";
		String parentUnit = "plana";
		String unitType = "hierarchy";
		
		try
		{
			

			String result = omsProxy.registerUnit(unit, unitType, parentUnit, "creador");

			fail(result);

		}catch(NotCreatorInParentUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testRegisterUnit8b()
	{
		String unit = "jerarquia2";
		String parentUnit = "plana";
		String unitType = "hierarchy";
		
		try
		{
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

			

			String result = omsProxy.registerUnit(unit, unitType, parentUnit, "creador");

			fail(result);

		}catch(NotCreatorInParentUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testRegisterUnit9a()
	{
		
		String unit = "equipo2";
		String parentUnit = "plana";
		String unitType = "team";
		
		try
		{
			

			String result = omsProxy.registerUnit(unit, unitType, parentUnit, "creador");

			fail(result);

		}catch(NotCreatorInParentUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testRegisterUnit9b()
	{
		String unit = "equipo2";
		String parentUnit = "plana";
		String unitType = "team";
		
		try
		{
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

			

			String result = omsProxy.registerUnit(unit, unitType, parentUnit, "creador");

			fail(result);

		}catch(NotCreatorInParentUnitException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testRegisterUnit10()
	{
		String unit = "Plana";
		String parentUnit = "virtual";
		String unitType = "flat";
		
		try
		{
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

			

			String result = omsProxy.registerUnit(unit, unitType, parentUnit, "creador");

			fail(result);

		}catch(UnitExistsException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
}
