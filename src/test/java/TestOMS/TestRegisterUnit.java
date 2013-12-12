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


public class TestRegisterUnit extends TestCase {

	OMSProxy omsProxy = null;
	DatabaseAccess dbA = null;


	Agent agent = null;

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


	}

	public void testRegisterUnitConUnidadPadre()
	{
		try
		{
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			/**-----------
			 * --1a							
			 * -----------
			 */


			String unit = "jerarquia";

			String result = omsProxy.registerUnit(unit, "hierarchy", "virtual", "creador");
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", result);
			
			boolean res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
		

			//---------------------------------------------------------------------//
			
			/**-----------
			 * --1b							
			 * -----------
			 */
			
			unit = "equipo";

			result = omsProxy.registerUnit(unit, "team", "virtual", "creador");
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", result);
			
			res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
			
			/**-----------
			 * --1c						
			 * -----------
			 */
			
			unit = "plana";

			result = omsProxy.registerUnit(unit, "flat", "virtual", "creador");
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", result);
			
			res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);



		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	
	public void testRegisterUnitSinUnidadPadre()
	{
		try
		{
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			/**-----------
			 * --2a							
			 * -----------
			 */


			String unit = "jerarquia";

			
			String result = omsProxy.registerUnit(unit, "hierarchy", "", "creador");
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", result);
			
			boolean res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
		

			//---------------------------------------------------------------------//
			
			/**-----------
			 * --2b							
			 * -----------
			 */
			
			unit = "equipo";

			result = omsProxy.registerUnit(unit, "team", "", "creador");
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", result);
			
			res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
			
			/**-----------
			 * --2c							
			 * -----------
			 */
			
			unit = "plana";

			result = omsProxy.registerUnit(unit, "flat", "", "creador");
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", result);
			
			res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);



		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testRegisterUnitSinUnidadPadre2()
	{
		try
		{
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			/**-----------
			 * --2a							
			 * -----------
			 */


			String unit = "jerarquia";

			
			String result = omsProxy.registerUnit(unit, "hierarchy", null, "creador");
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", result);
			
			boolean res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
		

			//---------------------------------------------------------------------//
			
			/**-----------
			 * --2b							
			 * -----------
			 */
			
			unit = "equipo";

			result = omsProxy.registerUnit(unit, "team", null, "creador");
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", result);
			
			res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
			
			/**-----------
			 * --2c							
			 * -----------
			 */
			
			unit = "plana";

			result = omsProxy.registerUnit(unit, "flat", null, "creador");
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", result);
			
			res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);



		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testRegisterUnit3a()
	{
		try
		{
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			/**-----------
			 * --3a							
			 * -----------
			 */


			String unit = "jerarquia";

			
			String result = omsProxy.registerUnit(unit, "hierarchy", "virtual", "creador");
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", result);
			
			boolean res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
		

			//---------------------------------------------------------------------//
			
			/**-----------
			 * --3a							
			 * -----------
			 */


			unit = "jerarquia2";

			
			result = omsProxy.registerUnit(unit, "hierarchy", "jerarquia", "creador");
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", result);
			
			res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
		

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
	
	
	public void testRegisterUnit3b()
	{
		try
		{
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			/**-----------
			 * --3b							
			 * -----------
			 */


			String unit = "jerarquia";

			
			String result = omsProxy.registerUnit(unit, "hierarchy", "virtual", "creador");
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", result);
			
			boolean res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
		

			//---------------------------------------------------------------------//
			
			/**-----------
			 * --3b							
			 * -----------
			 */


			unit = "equipo";

			
			result = omsProxy.registerUnit(unit, "team", "jerarquia", "creador");
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", result);
			
			res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
		

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
	
	public void testRegisterUnit3c()
	{
		try
		{
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			/**-----------
			 * --3a							
			 * -----------
			 */


			String unit = "jerarquia";

			
			String result = omsProxy.registerUnit(unit, "hierarchy", "virtual", "creador");
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", result);
			
			boolean res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
		

			//---------------------------------------------------------------------//
			
			/**-----------
			 * --3a							
			 * -----------
			 */


			unit = "plana";

			
			result = omsProxy.registerUnit(unit, "flat", "jerarquia", "creador");
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", result);
			
			res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
		

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
	
	
	public void testRegisterUnit3d()
	{
		try
		{
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			/**-----------
			 * --3a							
			 * -----------
			 */


			String unit = "equipo";

			
			String result = omsProxy.registerUnit(unit, "team", "virtual", "creador");
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", result);
			
			boolean res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
		

			//---------------------------------------------------------------------//
			
			/**-----------
			 * --3a							
			 * -----------
			 */


			unit = "equipo2";

			
			result = omsProxy.registerUnit(unit, "team", "equipo", "creador");
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", result);
			
			res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
		

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
	
	public void testRegisterUnit3e()
	{
		try
		{
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			/**-----------
			 * --3a							
			 * -----------
			 */


			String unit = "equipo";

			
			String result = omsProxy.registerUnit(unit, "team", "virtual", "creador");
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", result);
			
			boolean res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
		

			//---------------------------------------------------------------------//
			
			/**-----------
			 * --3a							
			 * -----------
			 */


			unit = "jerarquia";

			
			result = omsProxy.registerUnit(unit, "hierarchy", "equipo", "creador");
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", result);
			
			res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
		

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
	
	public void testRegisterUnit3f()
	{
		try
		{
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			/**-----------
			 * --3a							
			 * -----------
			 */


			String unit = "equipo";

			
			String result = omsProxy.registerUnit(unit, "team", "virtual", "creador");
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", result);
			
			boolean res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
		

			//---------------------------------------------------------------------//
			
			/**-----------
			 * --3a							
			 * -----------
			 */


			unit = "plana";

			
			result = omsProxy.registerUnit(unit, "flat", "equipo", "creador");
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", result);
			
			res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
		

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
	
	public void testRegisterUnit3g()
	{
		try
		{
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			/**-----------
			 * --3a							
			 * -----------
			 */


			String unit = "plana";

			
			String result = omsProxy.registerUnit(unit, "flat", "virtual", "creador");
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", result);
			
			boolean res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
		

			//---------------------------------------------------------------------//
			
			/**-----------
			 * --3a							
			 * -----------
			 */


			unit = "plana2";

			
			result = omsProxy.registerUnit(unit, "flat", "plana", "creador");
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", result);
			
			res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
		

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
	
	public void testRegisterUnit3h()
	{
		try
		{
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			/**-----------
			 * --3a							
			 * -----------
			 */


			String unit = "plana";

			
			String result = omsProxy.registerUnit(unit, "flat", "virtual", "creador");
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", result);
			
			boolean res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
		

			//---------------------------------------------------------------------//
			
			/**-----------
			 * --3a							
			 * -----------
			 */


			unit = "equipo";

			
			result = omsProxy.registerUnit(unit, "team", "plana", "creador");
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", result);
			
			res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
		

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
	
	public void testRegisterUnit3i()
	{
		try
		{
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			/**-----------
			 * --3a							
			 * -----------
			 */


			String unit = "plana";

			
			String result = omsProxy.registerUnit(unit, "flat", "virtual", "creador");
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", result);
			
			boolean res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
		

			//---------------------------------------------------------------------//
			
			/**-----------
			 * --3a							
			 * -----------
			 */


			unit = "jerarquia";

			
			result = omsProxy.registerUnit(unit, "hierarchy", "plana", "creador");
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", result);
			
			res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
		

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
