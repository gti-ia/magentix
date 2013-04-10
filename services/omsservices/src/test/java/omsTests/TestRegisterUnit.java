package omsTests;

import persistence.OMSInterface;
import junit.framework.TestCase;


public class TestRegisterUnit extends TestCase {


	DatabaseAccess dbA = null;
	OMSInterface omsInterface = null;
	ResponseParser responseParser = null;

	public TestRegisterUnit()
	{

	}

	protected void tearDown() throws Exception {




		dbA = null;
		


	}
	protected void setUp() throws Exception {
		super.setUp();


		responseParser = new ResponseParser();
		
		omsInterface = new OMSInterface();
		

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

			String result = omsInterface.registerUnit(unit, "hierarchy", "virtual", "pruebas", "creador");
			responseParser.parseResponse(result);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", responseParser.getDescription());
			
			boolean res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
		

			//---------------------------------------------------------------------//
			
			/**-----------
			 * --1b							
			 * -----------
			 */
			
			unit = "equipo";

			result = omsInterface.registerUnit(unit, "team", "virtual", "pruebas", "creador");
			responseParser.parseResponse(result);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", responseParser.getDescription());
			
			res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
			
			/**-----------
			 * --1c						
			 * -----------
			 */
			
			unit = "plana";

			result = omsInterface.registerUnit(unit, "flat", "virtual", "pruebas", "creador" );
			responseParser.parseResponse(result);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", responseParser.getDescription());
			
			res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);



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

			
			String result = omsInterface.registerUnit(unit, "hierarchy", "", "pruebas", "creador");
			responseParser.parseResponse(result);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", responseParser.getDescription());
			
			boolean res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
		

			//---------------------------------------------------------------------//
			
			/**-----------
			 * --2b							
			 * -----------
			 */
			
			unit = "equipo";

			result = omsInterface.registerUnit(unit, "team", "","pruebas", "creador");
			responseParser.parseResponse(result);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", responseParser.getDescription());
			
			res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
			
			/**-----------
			 * --2c							
			 * -----------
			 */
			
			unit = "plana";

			result = omsInterface.registerUnit(unit, "flat", "", "pruebas","creador");
			responseParser.parseResponse(result);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", responseParser.getDescription());
			
			res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);



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

			
			String result = omsInterface.registerUnit(unit, "hierarchy", null, "pruebas","creador");
			responseParser.parseResponse(result);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", responseParser.getDescription());
			
			boolean res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
		

			//---------------------------------------------------------------------//
			
			/**-----------
			 * --2b							
			 * -----------
			 */
			
			unit = "equipo";

			result = omsInterface.registerUnit(unit, "team", null, "pruebas","creador");
			responseParser.parseResponse(result);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", responseParser.getDescription());
			
			res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
			
			/**-----------
			 * --2c							
			 * -----------
			 */
			
			unit = "plana";

			result = omsInterface.registerUnit(unit, "flat", null, "pruebas","creador");
			responseParser.parseResponse(result);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", responseParser.getDescription());
			
			res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);



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

			
			String result = omsInterface.registerUnit(unit, "hierarchy", "virtual", "pruebas","creador");
			responseParser.parseResponse(result);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", responseParser.getDescription());
			
			boolean res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
		

			//---------------------------------------------------------------------//
			
			/**-----------
			 * --3a							
			 * -----------
			 */


			unit = "jerarquia2";

			
			result = omsInterface.registerUnit(unit, "hierarchy", "jerarquia", "pruebas","creador");
			responseParser.parseResponse(result);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", responseParser.getDescription());
			
			res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
		

			//---------------------------------------------------------------------//




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

			
			String result = omsInterface.registerUnit(unit, "hierarchy", "virtual", "pruebas","creador");
			responseParser.parseResponse(result);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", responseParser.getDescription());
			
			boolean res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
		

			//---------------------------------------------------------------------//
			
			/**-----------
			 * --3b							
			 * -----------
			 */


			unit = "equipo";

			
			result = omsInterface.registerUnit(unit, "team", "jerarquia", "pruebas","creador");
			responseParser.parseResponse(result);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", responseParser.getDescription());
			
			res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
		

			//---------------------------------------------------------------------//




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

			
			String result = omsInterface.registerUnit(unit, "hierarchy", "virtual", "pruebas","creador");
			responseParser.parseResponse(result);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", responseParser.getDescription());
			
			boolean res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
		

			//---------------------------------------------------------------------//
			
			/**-----------
			 * --3a							
			 * -----------
			 */


			unit = "plana";

			
			result = omsInterface.registerUnit(unit, "flat", "jerarquia","pruebas", "creador");
			responseParser.parseResponse(result);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", responseParser.getDescription());
			
			res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
		

			//---------------------------------------------------------------------//




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

			
			String result = omsInterface.registerUnit(unit, "team", "virtual", "pruebas","creador");
			responseParser.parseResponse(result);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", responseParser.getDescription());
			
			boolean res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
		

			//---------------------------------------------------------------------//
			
			/**-----------
			 * --3a							
			 * -----------
			 */


			unit = "equipo2";

			
			result = omsInterface.registerUnit(unit, "team", "equipo", "pruebas","creador");
			responseParser.parseResponse(result);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", responseParser.getDescription());
			
			res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
		

			//---------------------------------------------------------------------//




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

			
			String result = omsInterface.registerUnit(unit, "team", "virtual", "pruebas","creador");
			responseParser.parseResponse(result);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", responseParser.getDescription());
			
			boolean res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
		

			//---------------------------------------------------------------------//
			
			/**-----------
			 * --3a							
			 * -----------
			 */


			unit = "jerarquia";

			
			result = omsInterface.registerUnit(unit, "hierarchy", "equipo", "pruebas","creador");
			responseParser.parseResponse(result);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", responseParser.getDescription());
			
			res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
		

			//---------------------------------------------------------------------//




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

			
			String result = omsInterface.registerUnit(unit, "team", "virtual", "pruebas","creador");
			responseParser.parseResponse(result);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", responseParser.getDescription());
			
			boolean res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
		

			//---------------------------------------------------------------------//
			
			/**-----------
			 * --3a							
			 * -----------
			 */


			unit = "plana";

			
			result = omsInterface.registerUnit(unit, "flat", "equipo", "pruebas","creador");
			responseParser.parseResponse(result);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", responseParser.getDescription());
			
			res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
		

			//---------------------------------------------------------------------//




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

			
			String result = omsInterface.registerUnit(unit, "flat", "virtual", "pruebas","creador");
			responseParser.parseResponse(result);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", responseParser.getDescription());
			
			boolean res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
		

			//---------------------------------------------------------------------//
			
			/**-----------
			 * --3a							
			 * -----------
			 */


			unit = "plana2";

			
			result = omsInterface.registerUnit(unit, "flat", "plana", "pruebas","creador");
			responseParser.parseResponse(result);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", responseParser.getDescription());
			
			res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
		

			//---------------------------------------------------------------------//




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

			
			String result = omsInterface.registerUnit(unit, "flat", "virtual", "pruebas","creador");
			responseParser.parseResponse(result);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", responseParser.getDescription());
			
			boolean res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
		

			//---------------------------------------------------------------------//
			
			/**-----------
			 * --3a							
			 * -----------
			 */


			unit = "equipo";

			
			result = omsInterface.registerUnit(unit, "team", "plana","pruebas", "creador");
			responseParser.parseResponse(result);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", responseParser.getDescription());
			
			res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
		

			//---------------------------------------------------------------------//




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

			
			String result = omsInterface.registerUnit(unit, "flat", "virtual", "pruebas","creador");
			responseParser.parseResponse(result);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", responseParser.getDescription());
			
			boolean res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
		

			//---------------------------------------------------------------------//
			
			/**-----------
			 * --3a							
			 * -----------
			 */


			unit = "jerarquia";

			
			result = omsInterface.registerUnit(unit, "hierarchy", "plana", "pruebas","creador");
			responseParser.parseResponse(result);
			assertEquals("El mensaje debe ser el siguiente:", unit+ " created", responseParser.getDescription());
			
			res = dbA.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");
			assertEquals(true,res);
		

			//---------------------------------------------------------------------//




		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
}
