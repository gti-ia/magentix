package omsTests;

import junit.framework.TestCase;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.SF;
import es.upv.dsic.gti_ia.organization.exception.THOMASException;


public class TestDeRegisterUnit extends TestCase {

	OMSProxy omsProxy = null;
	DatabaseAccess dbA = null;


	Agent agent = null;

	
	OMS oms = null;
	SF sf = null;

	public TestDeRegisterUnit()
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
		dbA.executeSQL("DELETE FROM agentList");
		dbA.executeSQL("DELETE FROM roleList WHERE idroleList != 1");
		dbA.executeSQL("DELETE FROM unitHierarchy WHERE idChildUnit != 1");
		dbA.executeSQL("DELETE FROM unitList WHERE idunitList != 1");

		//--------------------------------------------//




	}

	public void testDeregisterUnit1()
	{
		try
		{
			
			String unit = "jerarquia";
		
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			/**-----------
			 * --1a							
			 * -----------
			 */


			

			String result = omsProxy.deregisterUnit(unit);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " deleted", result);
		

			//---------------------------------------------------------------------//
			
		
			unit = "equipo";
			
		
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			/**-----------
			 * --1a							
			 * -----------
			 */


			

			result = omsProxy.deregisterUnit(unit);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " deleted", result);
		

			//---------------------------------------------------------------------//
			
			unit = "plana";
			
	
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			/**-----------
			 * --1a							
			 * -----------
			 */


			

			result = omsProxy.deregisterUnit(unit);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " deleted", result);
		

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
	
	public void testDeregisterUnit2()
	{
		try
		{
			
			String unit = "jerarquia";
		
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");
			
		

			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			/**-----------
			 * --1a							
			 * -----------
			 */


			

			String result = omsProxy.deregisterUnit(unit);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " deleted", result);
		

			//---------------------------------------------------------------------//
			
		
			unit = "equipo";
			
		
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			/**-----------
			 * --1a							
			 * -----------
			 */


			

			result = omsProxy.deregisterUnit(unit);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " deleted", result);
		

			//---------------------------------------------------------------------//
			
			unit = "plana";
			
	
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			/**-----------
			 * --1a							
			 * -----------
			 */


			

			result = omsProxy.deregisterUnit(unit);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " deleted", result);
		

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
	
	
	public void testDeregisterUnit3()
	{
		try
		{
			
			String unit = "jerarquia";
		
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");
			
		

			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			/**-----------
			 * --1a							
			 * -----------
			 */


			

			String result = omsProxy.deregisterUnit(unit);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " deleted", result);
		

			//---------------------------------------------------------------------//
			
		
			unit = "equipo";
			
		
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

			/**-----------
			 * --1a							
			 * -----------
			 */


			

			result = omsProxy.deregisterUnit(unit);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " deleted", result);
		

			//---------------------------------------------------------------------//
			
			unit = "plana";
			
	
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			/**-----------
			 * --1a							
			 * -----------
			 */


			

			result = omsProxy.deregisterUnit(unit);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " deleted", result);
		

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
	
	public void testDeregisterUnit4()
	{
		try
		{
			
			String unit = "jerarquia";
		
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");
			
		

			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");
			


			/**-----------
			 * --1a							
			 * -----------
			 */


			

			String result = omsProxy.deregisterUnit(unit);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " deleted", result);
		

			//---------------------------------------------------------------------//
			
		
			unit = "equipo";
			
		
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");
					
			/**-----------
			 * --1a							
			 * -----------
			 */


			

			result = omsProxy.deregisterUnit(unit);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " deleted", result);
		

			//---------------------------------------------------------------------//
			
			unit = "plana";
			
	
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");
					
			
			/**-----------
			 * --1a							
			 * -----------
			 */


			

			result = omsProxy.deregisterUnit(unit);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " deleted", result);
		

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
	
	public void testDeregisterUnit5()
	{
		try
		{
			
			String unit = "jerarquia";
		
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");
			
		

			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");
		


			/**-----------
			 * --1a							
			 * -----------
			 */


			

			String result = omsProxy.deregisterUnit(unit);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " deleted", result);
		

			//---------------------------------------------------------------------//
			
		
			unit = "equipo";
			
		
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");
		

			/**-----------
			 * --1a							
			 * -----------
			 */


			

			result = omsProxy.deregisterUnit(unit);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " deleted", result);
		

			//---------------------------------------------------------------------//
			
			unit = "plana";
			
	
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");
		
			
			/**-----------
			 * --1a							
			 * -----------
			 */


			

			result = omsProxy.deregisterUnit(unit);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " deleted", result);
		

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
	
	public void testDeregisterUnit6()
	{
		try
		{
			
			String unit = "jerarquia";
		
			
		

			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");
		


			/**-----------
			 * --1a							
			 * -----------
			 */


			

			String result = omsProxy.deregisterUnit(unit);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " deleted", result);
		

			//---------------------------------------------------------------------//
			
		
			unit = "equipo";
			
		
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");
		

			/**-----------
			 * --1a							
			 * -----------
			 */


			

			result = omsProxy.deregisterUnit(unit);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " deleted", result);
		

			//---------------------------------------------------------------------//
			
			unit = "plana";
			
	
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");
		
			
			/**-----------
			 * --1a							
			 * -----------
			 */


			

			result = omsProxy.deregisterUnit(unit);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " deleted", result);
		

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
	
	
	public void testDeregisterUnit7()
	{
		try
		{
			
			String unit = "jerarquia";
			
			String unit2 = "jerarquia2";
			
		
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");

			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");
			
			
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
			
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");
		

			

			String result = omsProxy.deregisterUnit(unit2);
			assertEquals("El mensaje debe ser el siguiente:", unit2+ " deleted", result);
			
			//---------------------------------------------------------------------//
			
			unit2 = "equipo2";
			
		
			
			

			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");
			
			
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			

			result = omsProxy.deregisterUnit(unit2);
			assertEquals("El mensaje debe ser el siguiente:", unit2+ " deleted", result);
	
			
			
		//---------------------------------------------------------------------//
			
			unit2 = "plana2";
			
		
			
			

			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");
			
			
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			

			result = omsProxy.deregisterUnit(unit2);
			assertEquals("El mensaje debe ser el siguiente:", unit2+ " deleted", result);
			
			
			//---------------------------------------------------------------------//
			
			unit = "equipo";
			
			unit2 = "equipo2";
			
		
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");

			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");
			
			
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
			
			
				
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");
	

			

			result = omsProxy.deregisterUnit(unit2);
			assertEquals("El mensaje debe ser el siguiente:", unit2+ " deleted", result);
		
			
			
		//---------------------------------------------------------------------//
			
			unit2 = "jerarquia2";
			
		
			
			

			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
			
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");
			
			
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			

			result = omsProxy.deregisterUnit(unit2);
			assertEquals("El mensaje debe ser el siguiente:", unit2+ " deleted", result);
			

		//---------------------------------------------------------------------//
			
			unit2 = "plana2";
			
		
			
			

			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");
			
			
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			

			result = omsProxy.deregisterUnit(unit2);
			assertEquals("El mensaje debe ser el siguiente:", unit2+ " deleted", result);
			
			
			//---------------------------------------------------------------------//
			
			
			
			
			
			unit = "plana";
			
			unit2 = "plana2";
			
		
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");

			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");
			
			
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
			
			
				
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");
		


			

			result = omsProxy.deregisterUnit(unit2);
			assertEquals("El mensaje debe ser el siguiente:", unit2+ " deleted", result);
		

		//---------------------------------------------------------------------//
			
			unit2 = "equipo2";
			
		
			
			

			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");
			
			
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			

			result = omsProxy.deregisterUnit(unit2);
			assertEquals("El mensaje debe ser el siguiente:", unit2+ " deleted", result);
			
			
		//---------------------------------------------------------------------//
			
			unit2 = "jerarquia2";
			
		
			
			

			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
			
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");
			
			
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			

			result = omsProxy.deregisterUnit(unit2);
			assertEquals("El mensaje debe ser el siguiente:", unit2+ " deleted", result);
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
	
	public void testDeregisterUnit8a()
	{
		try
		{
			
			
			/**
			 * 8a.1
			 */
			String unit = "jerarquia";
			
			String unit2 = "jerarquia2";
			
		
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");

			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");
			
			
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
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");
		
			

			String result = omsProxy.deregisterUnit(unit2);
			assertEquals("El mensaje debe ser el siguiente:", unit2+ " deleted", result);
			
			//---------------------------------------------------------------------//
			
			
			/**
			 * 8a.2
			 */
			
			
			unit2 = "equipo2";
			
		
			


			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			

			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");
			
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			

			
			
				
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");
		
			

			result = omsProxy.deregisterUnit(unit2);
			assertEquals("El mensaje debe ser el siguiente:", unit2+ " deleted", result);
			
			//---------------------------------------------------------------------//
			
			/**
			 * 8a.3
			 */
			
			
			unit2 = "plana2";
			
		
			


			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			

			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");
			
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			

			
			
				
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");
		
			

			result = omsProxy.deregisterUnit(unit2);
			assertEquals("El mensaje debe ser el siguiente:", unit2+ " deleted", result);
			
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
	
	public void testDeregisterUnit8b()
	{
		try
		{
			
			
			/**
			 * 8a.1
			 */
			String unit = "equipo";
			
			String unit2 = "jerarquia2";
			
		
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");

			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");
			
			
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
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
				
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");
		
			

			String result = omsProxy.deregisterUnit(unit2);
			assertEquals("El mensaje debe ser el siguiente:", unit2+ " deleted", result);
			
			//---------------------------------------------------------------------//
			
			
			/**
			 * 8a.2
			 */
			
			
			unit2 = "equipo2";
			
		
			


			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			

			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");
			
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			

			
			
				
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");
		
			

			result = omsProxy.deregisterUnit(unit2);
			assertEquals("El mensaje debe ser el siguiente:", unit2+ " deleted", result);
			
			//---------------------------------------------------------------------//
			
			/**
			 * 8a.3
			 */
			
			
			unit2 = "plana2";
			
		
			


			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			

			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");
			
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			

			
			
				
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");
		
			

			result = omsProxy.deregisterUnit(unit2);
			assertEquals("El mensaje debe ser el siguiente:", unit2+ " deleted", result);
			
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
	
	public void testDeregisterUnit8c()
	{
		try
		{
			
			
			/**
			 * 8a.1
			 */
			String unit = "plana";
			
			String unit2 = "jerarquia2";
			
		
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");

			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");
			
			
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
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
				
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");
		
			

			String result = omsProxy.deregisterUnit(unit2);
			assertEquals("El mensaje debe ser el siguiente:", unit2+ " deleted", result);
			
			//---------------------------------------------------------------------//
			
			
			/**
			 * 8a.2
			 */
			
			
			unit2 = "equipo2";
			
		
			


			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			

			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");
			
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			

			
			
				
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");
		
			

			result = omsProxy.deregisterUnit(unit2);
			assertEquals("El mensaje debe ser el siguiente:", unit2+ " deleted", result);
			
			//---------------------------------------------------------------------//
			
			/**
			 * 8a.3
			 */
			
			
			unit2 = "plana2";
			
		
			


			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			

			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");
			
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			

			
			
				
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");
		
			

			result = omsProxy.deregisterUnit(unit2);
			assertEquals("El mensaje debe ser el siguiente:", unit2+ " deleted", result);
			
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
	
	
	public void testDeregisterUnit9a()
	{
		try
		{
			
			
			/**
			 * 8a.1
			 */
			String unit = "jerarquia";
			
			String unit2 = "jerarquia2";
			
		
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");

			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");
			
			
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
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");	
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");
		
			

			String result = omsProxy.deregisterUnit(unit2);
			assertEquals("El mensaje debe ser el siguiente:", unit2+ " deleted", result);
			
			//---------------------------------------------------------------------//
			
			
			/**
			 * 8a.2
			 */
			
			
			unit2 = "equipo2";
			
		
			


			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			

			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");
			
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			

			
			
				
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");
		
			

			result = omsProxy.deregisterUnit(unit2);
			assertEquals("El mensaje debe ser el siguiente:", unit2+ " deleted", result);
			
			//---------------------------------------------------------------------//
			
			/**
			 * 8a.3
			 */
			
			
			unit2 = "plana2";
			
		
			


			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			

			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");
			
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			

			
			
				
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");
		
			

			result = omsProxy.deregisterUnit(unit2);
			assertEquals("El mensaje debe ser el siguiente:", unit2+ " deleted", result);
			
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
	
	public void testDeregisterUnit9b()
	{
		try
		{
			
			
			/**
			 * 8a.1
			 */
			String unit = "equipo";
			
			String unit2 = "jerarquia2";
			
		
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");

			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");
			
			
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
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
				
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");
		
			

			String result = omsProxy.deregisterUnit(unit2);
			assertEquals("El mensaje debe ser el siguiente:", unit2+ " deleted", result);
			
			//---------------------------------------------------------------------//
			
			
			/**
			 * 8a.2
			 */
			
			
			unit2 = "equipo2";
			
		
			


			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			

			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");
			
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			

			
			
				
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");
		
			

			result = omsProxy.deregisterUnit(unit2);
			assertEquals("El mensaje debe ser el siguiente:", unit2+ " deleted", result);
			
			//---------------------------------------------------------------------//
			
			/**
			 * 8a.3
			 */
			
			
			unit2 = "plana2";
			
		
			


			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			

			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");
			
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			

			
			
				
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");
		
			

			result = omsProxy.deregisterUnit(unit2);
			assertEquals("El mensaje debe ser el siguiente:", unit2+ " deleted", result);
			
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
	
	public void testDeregisterUnit9c()
	{
		try
		{
			
			
			/**
			 * 8a.1
			 */
			String unit = "plana";
			
			String unit2 = "jerarquia2";
			
		
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");

			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");
			
			
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
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");
		
			

			String result = omsProxy.deregisterUnit(unit2);
			assertEquals("El mensaje debe ser el siguiente:", unit2+ " deleted", result);
			
			//---------------------------------------------------------------------//
			
			
			/**
			 * 8a.2
			 */
			
			
			unit2 = "equipo2";
			
		
			


			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			

			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");
			
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			

			
			
				
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");
		
			

			result = omsProxy.deregisterUnit(unit2);
			assertEquals("El mensaje debe ser el siguiente:", unit2+ " deleted", result);
			
			//---------------------------------------------------------------------//
			
			/**
			 * 8a.3
			 */
			
			
			unit2 = "plana2";
			
		
			


			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			

			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");
			
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			

			
			
				
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");
		
			

			result = omsProxy.deregisterUnit(unit2);
			assertEquals("El mensaje debe ser el siguiente:", unit2+ " deleted", result);
			
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
	
	
	public void testDeregisterUnit10a()
	{
		try
		{
			
			
			/**
			 * 8a.1
			 */
			String unit = "jerarquia";
			
			String unit2 = "jerarquia2";
			
		
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");

			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");
			
			
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
			
	
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('jugador')");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'jugador'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");
			

			String result = omsProxy.deregisterUnit(unit2);
			assertEquals("El mensaje debe ser el siguiente:", unit2+ " deleted", result);
			
			//---------------------------------------------------------------------//
			
			
			/**
			 * 8a.2
			 */
			
			
			unit2 = "equipo2";
			
		
			


			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			

			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");
			
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
					
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'jugador'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");
		

			result = omsProxy.deregisterUnit(unit2);
			assertEquals("El mensaje debe ser el siguiente:", unit2+ " deleted", result);
			
			//---------------------------------------------------------------------//
			
			/**
			 * 8a.3
			 */
			
			
			unit2 = "plana2";
			
		
			


			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			

			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");
			
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'jugador'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");
		

			result = omsProxy.deregisterUnit(unit2);
			assertEquals("El mensaje debe ser el siguiente:", unit2+ " deleted", result);
			
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
	
	public void testDeregisterUnit10b()
	{
		try
		{
			
			
			/**
			 * 8a.1
			 */
			String unit = "equipo";
			
			String unit2 = "jerarquia2";
			
		
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");

			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");
			
			
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
			

			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('jugador')");
				
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'jugador'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");
		

			String result = omsProxy.deregisterUnit(unit2);
			assertEquals("El mensaje debe ser el siguiente:", unit2+ " deleted", result);
			
			//---------------------------------------------------------------------//
			
			
			/**
			 * 8a.2
			 */
			
			
			unit2 = "equipo2";
			
		
			


			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			

			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");
			
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
		
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'jugador'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");
		
		
			

			result = omsProxy.deregisterUnit(unit2);
			assertEquals("El mensaje debe ser el siguiente:", unit2+ " deleted", result);
			
			//---------------------------------------------------------------------//
			
			/**
			 * 8a.3
			 */
			
			
			unit2 = "plana2";
			
		
			


			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			

			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");
			
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
	
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'jugador'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");
			

			result = omsProxy.deregisterUnit(unit2);
			assertEquals("El mensaje debe ser el siguiente:", unit2+ " deleted", result);
			
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
	
	public void testDeregisterUnit10c()
	{
		try
		{
			
			
			/**
			 * 8a.1
			 */
			String unit = "plana";
			
			String unit2 = "jerarquia2";
			
		
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");

			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");
			
			
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
		
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('jugador')");
			
				
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'jugador'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");
		
			

			String result = omsProxy.deregisterUnit(unit2);
			assertEquals("El mensaje debe ser el siguiente:", unit2+ " deleted", result);
			
			//---------------------------------------------------------------------//
			
			
			/**
			 * 8a.2
			 */
			
			
			unit2 = "equipo2";
			
		
			


			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			

			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");
			
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
				
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'jugador'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");
		

			result = omsProxy.deregisterUnit(unit2);
			assertEquals("El mensaje debe ser el siguiente:", unit2+ " deleted", result);
			
			//---------------------------------------------------------------------//
			
			/**
			 * 8a.3
			 */
			
			
			unit2 = "plana2";
			
		
			


			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit2+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			

			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))");
			
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
					"((SELECT idagentList FROM agentList WHERE agentName = 'jugador'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit2+"'))))");
		

			result = omsProxy.deregisterUnit(unit2);
			assertEquals("El mensaje debe ser el siguiente:", unit2+ " deleted", result);
			
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
