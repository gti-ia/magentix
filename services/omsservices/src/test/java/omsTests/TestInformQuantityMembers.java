package omsTests;

import persistence.OMSInterface;
import junit.framework.TestCase;



public class TestInformQuantityMembers extends TestCase {

	
	DatabaseAccess dbA = null;
	OMSInterface omsInterface = null;
	ResponseParser responseParser = null;

	public TestInformQuantityMembers()
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

	public void testinformQuantityMembers1a()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			String result = omsInterface.informQuantityMembers("jerarquia", "", "" ,"pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("equipo", "", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("plana", "", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));


			//---------------------------------------------------------------------//


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	public void testinformQuantityMembers1b()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			String result = omsInterface.informQuantityMembers("jerarquia", "supervisor", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("equipo", "manager", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("plana", "miembro", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));


			//---------------------------------------------------------------------//


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{

			String result = omsInterface.informQuantityMembers("jerarquia", "creador", "","pruebas");



		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}


	public void testinformQuantityMembers1c()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			String result = omsInterface.informQuantityMembers("jerarquia", "", "subordinate","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("equipo", "", "member","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("plana", "", "member","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));


			//---------------------------------------------------------------------//


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{

			String result = omsInterface.informQuantityMembers("jerarquia", "", "creator","pruebas");



		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	public void testinformQuantityMembers1d()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			String result = omsInterface.informQuantityMembers("jerarquia", "subordinado", "subordinate","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("equipo", "manager", "member","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("plana", "miembro", "member","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));


			//---------------------------------------------------------------------//


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{

			String result = omsInterface.informQuantityMembers("jerarquia", "creador", "creator","pruebas");



		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	public void testinformQuantityMembers2a()
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




			String result = omsInterface.informQuantityMembers("jerarquia", "", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", "2", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("equipo", "", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("plana", "", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));


			//---------------------------------------------------------------------//


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	public void testinformQuantityMembers2b()
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




			String result = omsInterface.informQuantityMembers("jerarquia", "supervisor", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("equipo", "manager", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("plana", "miembro", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));


			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("jerarquia", "creador", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}


	}


	public void testinformQuantityMembers2c()
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




			String result = omsInterface.informQuantityMembers("jerarquia", "", "subordinate","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", "2", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("equipo", "", "member","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("plana", "", "member","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));


			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("jerarquia", "", "creator","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}

	public void testinformQuantityMembers2d()
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




			String result = omsInterface.informQuantityMembers("jerarquia", "subordinado", "subordinate","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", "2", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("equipo", "manager", "member","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("plana", "miembro", "member","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));


			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("jerarquia", "creador", "creator","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}

	public void testinformQuantityMembers3a()
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




			String result = omsInterface.informQuantityMembers("jerarquia", "", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("equipo", "", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", "2", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("plana", "", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));


			//---------------------------------------------------------------------//


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	public void testinformQuantityMembers3b()
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




			String result = omsInterface.informQuantityMembers("jerarquia", "supervisor", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("equipo", "manager", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("plana", "miembro", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));


			//---------------------------------------------------------------------//




		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}


		try
		{

			omsInterface.informQuantityMembers("jerarquia", "creador", "","pruebas");



		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}


	}


	public void testinformQuantityMembers3c()
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




			String result = omsInterface.informQuantityMembers("jerarquia", "", "subordinate","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("equipo", "", "member","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", "2", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("plana", "", "member","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));


			//---------------------------------------------------------------------//

	


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		
		try
		{

			omsInterface.informQuantityMembers("jerarquia", "", "creator","pruebas");



		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}

	public void testinformQuantityMembers3d()
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




			String result = omsInterface.informQuantityMembers("jerarquia", "subordinado", "subordinate","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("equipo", "manager", "member","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("plana", "miembro", "member","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));


			//---------------------------------------------------------------------//




		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		
		try
		{

			omsInterface.informQuantityMembers("jerarquia", "creador", "creator","pruebas");



		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testinformQuantityMembers4a()
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




			String result = omsInterface.informQuantityMembers("jerarquia", "", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("equipo", "", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("plana", "", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", "2", responseParser.getElementsList().get(0));


			//---------------------------------------------------------------------//


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	public void testinformQuantityMembers4b()
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




			String result = omsInterface.informQuantityMembers("jerarquia", "supervisor", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("equipo", "manager", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("plana", "miembro", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", "2", responseParser.getElementsList().get(0));


			//---------------------------------------------------------------------//




		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}


		try
		{

			omsInterface.informQuantityMembers("jerarquia", "creador", "","pruebas");



		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}


	}


	public void testinformQuantityMembers4c()
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




			String result = omsInterface.informQuantityMembers("jerarquia", "", "subordinate","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("equipo", "", "member","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("plana", "", "member","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", "2", responseParser.getElementsList().get(0));


			//---------------------------------------------------------------------//

	


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		
		try
		{

			omsInterface.informQuantityMembers("jerarquia", "", "creator","pruebas");



		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}

	public void testinformQuantityMembers4d()
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




			String result = omsInterface.informQuantityMembers("jerarquia", "subordinado", "subordinate","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("equipo", "manager", "member","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("plana", "miembro", "member","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", "2", responseParser.getElementsList().get(0));


			//---------------------------------------------------------------------//




		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		
		try
		{

			omsInterface.informQuantityMembers("jerarquia", "creador", "creator","pruebas");



		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testinformQuantityMembers5a()
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




			String result = omsInterface.informQuantityMembers("jerarquia", "", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", "2", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("equipo", "", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", "2", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("plana", "", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));


			//---------------------------------------------------------------------//


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	public void testinformQuantityMembers5b()
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




			String result = omsInterface.informQuantityMembers("jerarquia", "supervisor", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("equipo", "manager", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("plana", "miembro", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));


			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("jerarquia", "creador", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}


	}


	public void testinformQuantityMembers5c()
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




			String result = omsInterface.informQuantityMembers("jerarquia", "", "subordinate","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", "2", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("equipo", "", "member","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", "2", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("plana", "", "member","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));


			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("jerarquia", "", "creator","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}

	public void testinformQuantityMembers5d()
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




			String result = omsInterface.informQuantityMembers("jerarquia", "subordinado", "subordinate","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", "2", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("equipo", "manager", "member","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("plana", "miembro", "member","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));


			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("jerarquia", "creador", "creator","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	
	public void testinformQuantityMembers6a()
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




			String result = omsInterface.informQuantityMembers("jerarquia", "", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("equipo", "", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", "2", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("plana", "", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", "2", responseParser.getElementsList().get(0));


			//---------------------------------------------------------------------//


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	public void testinformQuantityMembers6b()
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




			String result = omsInterface.informQuantityMembers("jerarquia", "supervisor", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("equipo", "manager", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("plana", "miembro", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", "2", responseParser.getElementsList().get(0));


			//---------------------------------------------------------------------//




		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}


		try
		{

			omsInterface.informQuantityMembers("jerarquia", "creador", "","pruebas");



		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}


	}


	public void testinformQuantityMembers6c()
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




			String result = omsInterface.informQuantityMembers("jerarquia", "", "subordinate","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("equipo", "", "member","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", "2", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("plana", "", "member","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", "2", responseParser.getElementsList().get(0));


			//---------------------------------------------------------------------//

	


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		
		try
		{

			omsInterface.informQuantityMembers("jerarquia", "", "creator","pruebas");



		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}

	public void testinformQuantityMembers6d()
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




			String result = omsInterface.informQuantityMembers("jerarquia", "subordinado", "subordinate","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("equipo", "manager", "member","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("plana", "miembro", "member","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", "2", responseParser.getElementsList().get(0));


			//---------------------------------------------------------------------//




		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		
		try
		{

			omsInterface.informQuantityMembers("jerarquia", "creador", "creator","pruebas");



		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	
	public void testinformQuantityMembers7a()
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




			String result = omsInterface.informQuantityMembers("jerarquia", "", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", "2", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("equipo", "", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", "2", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("plana", "", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", "2", responseParser.getElementsList().get(0));


			//---------------------------------------------------------------------//


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	public void testinformQuantityMembers7b()
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




			String result = omsInterface.informQuantityMembers("jerarquia", "supervisor", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("equipo", "manager", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("plana", "miembro", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", "2", responseParser.getElementsList().get(0));


			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("jerarquia", "creador", "","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}


	}


	public void testinformQuantityMembers7c()
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




			String result = omsInterface.informQuantityMembers("jerarquia", "", "subordinate","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", "2", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("equipo", "", "member","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", "2", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("plana", "", "member","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", "2", responseParser.getElementsList().get(0));


			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("jerarquia", "", "creator","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}

	public void testinformQuantityMembers7d()
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




			String result = omsInterface.informQuantityMembers("jerarquia", "subordinado", "subordinate","pruebas");
			responseParser.parseResponse(result);
			result = omsInterface.informQuantityMembers("equipo", "", "","pruebas");
			assertEquals("El resultado debe ser 2", "2", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("equipo", "manager", "member","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));

			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("plana", "miembro", "member","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", "2", responseParser.getElementsList().get(0));


			//---------------------------------------------------------------------//

			result = omsInterface.informQuantityMembers("jerarquia", "creador", "creator","pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", "1", responseParser.getElementsList().get(0));


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
}
