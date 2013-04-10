package omsTests;

import java.util.ArrayList;

import persistence.OMSInterface;

import junit.framework.TestCase;


public class TestInformMembers extends TestCase {


	DatabaseAccess dbA = null;


	OMSInterface omsInterface = null;
	ResponseParser responseParser = null;

	public TestInformMembers()
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

	public void testInformAgentRole1a()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			String result = omsInterface.informMembers("jerarquia", "", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", 2, responseParser.getItemsList().size());

		
			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("equipo", "", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", 2,  responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(0).get(1));


			assertEquals("pruebas2", responseParser.getItemsList().get(1).get(0));
			assertEquals("manager", responseParser.getItemsList().get(1).get(1));

			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("plana", "", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", 2, responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(0).get(1));


			assertEquals("pruebas2", responseParser.getItemsList().get(1).get(0));
			assertEquals("manager", responseParser.getItemsList().get(1).get(1));

			//---------------------------------------------------------------------//


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	public void testInformAgentRole1b()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			String result = omsInterface.informMembers("jerarquia", "supervisor", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("supervisor", responseParser.getItemsList().get(0).get(1));


			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("equipo", "manager", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("manager", responseParser.getItemsList().get(0).get(1));


			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("plana", "miembro", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(0).get(1));
			//---------------------------------------------------------------------//




		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}


		try
		{
			omsInterface.informMembers("jerarquia", "creador", "", "pruebas");


		}
		catch(Exception e)
		{
			assertEquals("", e.getMessage());
		}


		//---------------------------------------------------------------------//
	}

	public void testInformAgentRole1c()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			String result = omsInterface.informMembers("jerarquia", "", "subordinate", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("subordinado", responseParser.getItemsList().get(0).get(1));


			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("equipo", "", "member", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", 2, responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(0).get(1));

			assertEquals("pruebas2", responseParser.getItemsList().get(1).get(0));
			assertEquals("manager", responseParser.getItemsList().get(1).get(1));


			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("plana", "", "member", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", 2, responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(0).get(1));

			assertEquals("pruebas2", responseParser.getItemsList().get(1).get(0));
			assertEquals("manager", responseParser.getItemsList().get(1).get(1));
			//---------------------------------------------------------------------//




		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}


		try
		{
			omsInterface.informMembers("jerarquia", "", "creator", "pruebas");


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}


		//---------------------------------------------------------------------//
	}

	public void testInformAgentRole1d()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			String result = omsInterface.informMembers("jerarquia", "subordinado", "subordinate", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("subordinado", responseParser.getItemsList().get(0).get(1));


			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("equipo", "manager", "member", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());


			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("manager", responseParser.getItemsList().get(0).get(1));


			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("plana", "miembro", "member", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(0).get(1));


			//---------------------------------------------------------------------//




		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}


		try
		{
			omsInterface.informMembers("jerarquia", "Creador", "creator", "pruebas");


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}


		//---------------------------------------------------------------------//
	}


	public void testInformAgentRole2a()
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




			String result = omsInterface.informMembers("jerarquia", "", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 4", 4, responseParser.getItemsList().size());



			
			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("subordinado", responseParser.getItemsList().get(0).get(1));
			
			assertEquals("pruebas", responseParser.getItemsList().get(1).get(0));
			assertEquals("subordinado", responseParser.getItemsList().get(1).get(1));


			assertEquals("pruebas2", responseParser.getItemsList().get(2).get(0));
			assertEquals("creador", responseParser.getItemsList().get(2).get(1));



			assertEquals("pruebas2", responseParser.getItemsList().get(3).get(0));
			assertEquals("supervisor", responseParser.getItemsList().get(3).get(1));



			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("equipo", "", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", 2, responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(0).get(1));


			assertEquals("pruebas2", responseParser.getItemsList().get(1).get(0));
			assertEquals("manager", responseParser.getItemsList().get(1).get(1));

			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("plana", "", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", 2, responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(0).get(1));


			assertEquals("pruebas2", responseParser.getItemsList().get(1).get(0));
			assertEquals("manager", responseParser.getItemsList().get(1).get(1));

			//---------------------------------------------------------------------//


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	public void testInformAgentRole2b()
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




			String result = omsInterface.informMembers("jerarquia", "supervisor", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("supervisor", responseParser.getItemsList().get(0).get(1));


			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("equipo", "manager", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("manager", responseParser.getItemsList().get(0).get(1));


			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("plana", "miembro", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(0).get(1));
			//---------------------------------------------------------------------//


			result =  omsInterface.informMembers("jerarquia", "creador", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("creador", responseParser.getItemsList().get(0).get(1));

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		//---------------------------------------------------------------------//
	}

	public void testInformAgentRole2c()
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




			String result = omsInterface.informMembers("jerarquia", "", "subordinate", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", 2, responseParser.getItemsList().size());

			

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("subordinado", responseParser.getItemsList().get(0).get(1));
			
			assertEquals("pruebas", responseParser.getItemsList().get(1).get(0));
			assertEquals("subordinado", responseParser.getItemsList().get(1).get(1));
			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("equipo", "", "member", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", 2, responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(0).get(1));

			assertEquals("pruebas2", responseParser.getItemsList().get(1).get(0));
			assertEquals("manager", responseParser.getItemsList().get(1).get(1));


			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("plana", "", "member", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", 2, responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(0).get(1));

			assertEquals("pruebas2", responseParser.getItemsList().get(1).get(0));
			assertEquals("manager", responseParser.getItemsList().get(1).get(1));
			//---------------------------------------------------------------------//


			result = omsInterface.informMembers("jerarquia", "", "creator", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("creador", responseParser.getItemsList().get(0).get(1));


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		//---------------------------------------------------------------------//
	}

	public void testInformAgentRole2d()
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




			String result = omsInterface.informMembers("jerarquia", "subordinado", "subordinate", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", 2, responseParser.getItemsList().size());


			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("subordinado", responseParser.getItemsList().get(0).get(1));
			
			assertEquals("pruebas", responseParser.getItemsList().get(1).get(0));
			assertEquals("subordinado", responseParser.getItemsList().get(1).get(1));
			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("equipo", "manager", "member", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());


			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("manager", responseParser.getItemsList().get(0).get(1));


			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("plana", "miembro", "member", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(0).get(1));


			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("jerarquia", "creador", "creator", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("creador", responseParser.getItemsList().get(0).get(1));


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		//---------------------------------------------------------------------//
	}


	public void testInformAgentRole3a()
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




			String result = omsInterface.informMembers("jerarquia", "", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", 2, responseParser.getItemsList().size());



			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("subordinado", responseParser.getItemsList().get(0).get(1));

			assertEquals("pruebas2", responseParser.getItemsList().get(1).get(0));
			assertEquals("supervisor", responseParser.getItemsList().get(1).get(1));




			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("equipo", "", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 4", 4, responseParser.getItemsList().size());

		

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(0).get(1));
			
			assertEquals("pruebas", responseParser.getItemsList().get(1).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(1).get(1));

			assertEquals("pruebas2", responseParser.getItemsList().get(2).get(0));
			assertEquals("creador", responseParser.getItemsList().get(2).get(1));

			assertEquals("pruebas2", responseParser.getItemsList().get(3).get(0));
			assertEquals("manager", responseParser.getItemsList().get(3).get(1));

			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("plana", "", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", 2, responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(0).get(1));


			assertEquals("pruebas2", responseParser.getItemsList().get(1).get(0));
			assertEquals("manager", responseParser.getItemsList().get(1).get(1));

			//---------------------------------------------------------------------//


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	public void testInformAgentRole3b()
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




			String result = omsInterface.informMembers("jerarquia", "supervisor", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("supervisor", responseParser.getItemsList().get(0).get(1));


			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("equipo", "manager", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("manager", responseParser.getItemsList().get(0).get(1));


			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("plana", "miembro", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(0).get(1));
			//---------------------------------------------------------------------//





		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		try
		{

			omsInterface.informMembers("jerarquia", "creador", "", "pruebas");
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		//---------------------------------------------------------------------//
	}

	public void testInformAgentRole3c()
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




			String result = omsInterface.informMembers("jerarquia", "", "subordinate", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("subordinado", responseParser.getItemsList().get(0).get(1));


			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("equipo", "", "member", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 3", 3, responseParser.getItemsList().size());

	

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(0).get(1));
			
			assertEquals("pruebas", responseParser.getItemsList().get(1).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(1).get(1));

			assertEquals("pruebas2", responseParser.getItemsList().get(2).get(0));
			assertEquals("manager", responseParser.getItemsList().get(2).get(1));


			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("plana", "", "member", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", 2, responseParser.getItemsList().size());


			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(0).get(1));

			assertEquals("pruebas2", responseParser.getItemsList().get(1).get(0));
			assertEquals("manager", responseParser.getItemsList().get(1).get(1));
			//---------------------------------------------------------------------//





		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			omsInterface.informMembers("jerarquia", "", "creator", "pruebas");

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		//---------------------------------------------------------------------//
	}

	public void testInformAgentRole3d()
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




			String result = omsInterface.informMembers("jerarquia", "subordinado", "subordinate", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("subordinado", responseParser.getItemsList().get(0).get(1));

			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("equipo", "manager", "member", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());


			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("manager", responseParser.getItemsList().get(0).get(1));


			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("plana", "miembro", "member", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(0).get(1));


			//---------------------------------------------------------------------//




		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			omsInterface.informMembers("jerarquia", "creador", "creator", "pruebas");

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		//---------------------------------------------------------------------//
	}


	public void testInformAgentRole4a()
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




			String result = omsInterface.informMembers("jerarquia", "", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", 2, responseParser.getItemsList().size());



			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("subordinado", responseParser.getItemsList().get(0).get(1));

			assertEquals("pruebas2", responseParser.getItemsList().get(1).get(0));
			assertEquals("supervisor", responseParser.getItemsList().get(1).get(1));






			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("equipo", "", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", 2, responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(0).get(1));


			assertEquals("pruebas2", responseParser.getItemsList().get(1).get(0));
			assertEquals("manager", responseParser.getItemsList().get(1).get(1));
			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("plana", "", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 4", 4, responseParser.getItemsList().size());

		

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(0).get(1));
			
			assertEquals("pruebas", responseParser.getItemsList().get(1).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(1).get(1));

			assertEquals("pruebas2", responseParser.getItemsList().get(2).get(0));
			assertEquals("creador", responseParser.getItemsList().get(2).get(1));

			assertEquals("pruebas2", responseParser.getItemsList().get(3).get(0));
			assertEquals("manager", responseParser.getItemsList().get(3).get(1));

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	public void testInformAgentRole4b()
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




			String result = omsInterface.informMembers("jerarquia", "supervisor", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("supervisor", responseParser.getItemsList().get(0).get(1));


			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("equipo", "manager", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("manager", responseParser.getItemsList().get(0).get(1));


			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("plana", "miembro", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", 2, responseParser.getItemsList().size());

		

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(0).get(1));
			
			assertEquals("pruebas", responseParser.getItemsList().get(1).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(1).get(1));
			//---------------------------------------------------------------------//





		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		try
		{

			omsInterface.informMembers("jerarquia", "creador", "", "pruebas");
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		//---------------------------------------------------------------------//
	}

	public void testInformAgentRole4c()
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




			String result = omsInterface.informMembers("jerarquia", "", "subordinate", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("subordinado", responseParser.getItemsList().get(0).get(1));


			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("equipo", "", "member", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", 2, responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(0).get(1));

			assertEquals("pruebas2", responseParser.getItemsList().get(1).get(0));
			assertEquals("manager", responseParser.getItemsList().get(1).get(1));


			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("plana", "", "member", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 3", 3, responseParser.getItemsList().size());



			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(0).get(1));
			
			assertEquals("pruebas", responseParser.getItemsList().get(1).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(1).get(1));

			assertEquals("pruebas2", responseParser.getItemsList().get(2).get(0));
			assertEquals("manager", responseParser.getItemsList().get(2).get(1));
			//---------------------------------------------------------------------//





		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			omsInterface.informMembers("jerarquia", "", "creator", "pruebas");

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		//---------------------------------------------------------------------//
	}

	public void testInformAgentRole4d()
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




			String result = omsInterface.informMembers("jerarquia", "subordinado", "subordinate", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("subordinado", responseParser.getItemsList().get(0).get(1));

			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("equipo", "manager", "member", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());


			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("manager", responseParser.getItemsList().get(0).get(1));


			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("plana", "miembro", "member", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", 2, responseParser.getItemsList().size());



			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(0).get(1));
			
			assertEquals("pruebas", responseParser.getItemsList().get(1).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(1).get(1));


			//---------------------------------------------------------------------//




		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			omsInterface.informMembers("jerarquia", "creador", "creator", "pruebas");

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		//---------------------------------------------------------------------//
	}


	public void testInformAgentRole5a()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

			/**-----------
			 * --1a							
			 * -----------
			 */




			String result = omsInterface.informMembers("jerarquia", "", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 4", 4, responseParser.getItemsList().size());

		

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("subordinado", responseParser.getItemsList().get(0).get(1));
			
			assertEquals("pruebas", responseParser.getItemsList().get(1).get(0));
			assertEquals("subordinado", responseParser.getItemsList().get(1).get(1));

			assertEquals("pruebas2", responseParser.getItemsList().get(2).get(0));
			assertEquals("creador", responseParser.getItemsList().get(2).get(1));

			assertEquals("pruebas2", responseParser.getItemsList().get(3).get(0));
			assertEquals("supervisor", responseParser.getItemsList().get(3).get(1));






			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("equipo", "", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 4", 4, responseParser.getItemsList().size());

			

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(0).get(1));
			
			assertEquals("pruebas", responseParser.getItemsList().get(1).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(1).get(1));

			assertEquals("pruebas2", responseParser.getItemsList().get(2).get(0));
			assertEquals("creador", responseParser.getItemsList().get(2).get(1));

			assertEquals("pruebas2", responseParser.getItemsList().get(3).get(0));
			assertEquals("manager", responseParser.getItemsList().get(3).get(1));
			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("plana", "", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser ", 2, responseParser.getItemsList().size());


			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(0).get(1));

			assertEquals("pruebas2", responseParser.getItemsList().get(1).get(0));
			assertEquals("manager", responseParser.getItemsList().get(1).get(1));

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	public void testInformAgentRole5b()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");



			/**-----------
			 * --1a							
			 * -----------
			 */




			String result = omsInterface.informMembers("jerarquia", "supervisor", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("supervisor", responseParser.getItemsList().get(0).get(1));


			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("equipo", "manager", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("manager", responseParser.getItemsList().get(0).get(1));


			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("plana", "miembro", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());


			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(0).get(1));
			//---------------------------------------------------------------------//





		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		try
		{

			omsInterface.informMembers("jerarquia", "creador", "", "pruebas");
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		//---------------------------------------------------------------------//
	}

	public void testInformAgentRole5c()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");


			/**-----------
			 * --1a							
			 * -----------
			 */




			String result = omsInterface.informMembers("jerarquia", "", "subordinate", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", 2, responseParser.getItemsList().size());

			

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("subordinado", responseParser.getItemsList().get(0).get(1));
			
			assertEquals("pruebas", responseParser.getItemsList().get(1).get(0));
			assertEquals("subordinado", responseParser.getItemsList().get(1).get(1));


			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("equipo", "", "member", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 3", 3, responseParser.getItemsList().size());

	

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(0).get(1));
			
			assertEquals("pruebas", responseParser.getItemsList().get(1).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(1).get(1));

			assertEquals("pruebas2", responseParser.getItemsList().get(2).get(0));
			assertEquals("manager", responseParser.getItemsList().get(2).get(1));


			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("plana", "", "member", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", 2, responseParser.getItemsList().size());


			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(0).get(1));

			assertEquals("pruebas2", responseParser.getItemsList().get(1).get(0));
			assertEquals("manager", responseParser.getItemsList().get(1).get(1));
			//---------------------------------------------------------------------//


			result = omsInterface.informMembers("jerarquia", "", "creator", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());


			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("creador", responseParser.getItemsList().get(0).get(1));

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		//---------------------------------------------------------------------//
	}

	public void testInformAgentRole5d()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");


			/**-----------
			 * --1a							
			 * -----------
			 */




			String result = omsInterface.informMembers("jerarquia", "subordinado", "subordinate", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", 2, responseParser.getItemsList().size());

			

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("subordinado", responseParser.getItemsList().get(0).get(1));
			
			assertEquals("pruebas", responseParser.getItemsList().get(1).get(0));
			assertEquals("subordinado", responseParser.getItemsList().get(1).get(1));

			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("equipo", "manager", "member", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());


			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("manager", responseParser.getItemsList().get(0).get(1));


			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("plana", "miembro", "member", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(0).get(1));


			//---------------------------------------------------------------------//


			result = omsInterface.informMembers("jerarquia", "creador", "creator", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("creador", responseParser.getItemsList().get(0).get(1));


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}


		//---------------------------------------------------------------------//
	}

	public void testInformAgentRole6a()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

			/**-----------
			 * --1a							
			 * -----------
			 */




			String result = omsInterface.informMembers("jerarquia", "", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", 2, responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("subordinado", responseParser.getItemsList().get(0).get(1));

			assertEquals("pruebas2", responseParser.getItemsList().get(1).get(0));
			assertEquals("supervisor", responseParser.getItemsList().get(1).get(1));






			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("equipo", "", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 4", 4, responseParser.getItemsList().size());

	

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(0).get(1));
			
			assertEquals("pruebas", responseParser.getItemsList().get(1).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(1).get(1));

			assertEquals("pruebas2", responseParser.getItemsList().get(2).get(0));
			assertEquals("creador", responseParser.getItemsList().get(2).get(1));

			assertEquals("pruebas2", responseParser.getItemsList().get(3).get(0));
			assertEquals("manager", responseParser.getItemsList().get(3).get(1));
			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("plana", "", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 4", 4, responseParser.getItemsList().size());

	

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(0).get(1));
			
			assertEquals("pruebas", responseParser.getItemsList().get(1).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(1).get(1));

			assertEquals("pruebas2", responseParser.getItemsList().get(2).get(0));
			assertEquals("creador", responseParser.getItemsList().get(2).get(1));

			assertEquals("pruebas2", responseParser.getItemsList().get(3).get(0));
			assertEquals("manager", responseParser.getItemsList().get(3).get(1));


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	public void testInformAgentRole6b()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");


			/**-----------
			 * --1a							
			 * -----------
			 */




			String result = omsInterface.informMembers("jerarquia", "supervisor", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("supervisor", responseParser.getItemsList().get(0).get(1));


			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("equipo", "manager", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("manager", responseParser.getItemsList().get(0).get(1));


			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("plana", "miembro", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", 2, responseParser.getItemsList().size());


		

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(0).get(1));
			
			assertEquals("pruebas", responseParser.getItemsList().get(1).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(1).get(1));
			//---------------------------------------------------------------------//


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		
		try
		{
			omsInterface.informMembers("jerarquia", "", "creator", "pruebas");
			
			
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		//---------------------------------------------------------------------//
	}

	public void testInformAgentRole6c()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");


			/**-----------
			 * --1a							
			 * -----------
			 */




			String result = omsInterface.informMembers("jerarquia", "", "subordinate", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("subordinado", responseParser.getItemsList().get(0).get(1));


			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("equipo", "", "member", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 3", 3, responseParser.getItemsList().size());

	

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(0).get(1));
			
			assertEquals("pruebas", responseParser.getItemsList().get(1).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(1).get(1));

			assertEquals("pruebas2", responseParser.getItemsList().get(2).get(0));
			assertEquals("manager", responseParser.getItemsList().get(2).get(1));


			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("plana", "", "member", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 3", 3, responseParser.getItemsList().size());


		

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(0).get(1));
			
			assertEquals("pruebas", responseParser.getItemsList().get(1).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(1).get(1));

			assertEquals("pruebas2", responseParser.getItemsList().get(2).get(0));
			assertEquals("manager", responseParser.getItemsList().get(2).get(1));
			//---------------------------------------------------------------------//






		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		try
		{
			omsInterface.informMembers("jerarquia", "", "creator", "pruebas");
			
			
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		//---------------------------------------------------------------------//
	}

	public void testInformAgentRole6d()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");


			/**-----------
			 * --1a							
			 * -----------
			 */




			String result = omsInterface.informMembers("jerarquia", "subordinado", "subordinate", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());


			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("subordinado", responseParser.getItemsList().get(0).get(1));

			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("equipo", "manager", "member", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());


			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("manager", responseParser.getItemsList().get(0).get(1));


			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("plana", "miembro", "member", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", 2, responseParser.getItemsList().size());

			
			
			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(0).get(1));
			
			assertEquals("pruebas", responseParser.getItemsList().get(1).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(1).get(1));


			//---------------------------------------------------------------------//





		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		try
		{
			omsInterface.informMembers("jerarquia", "", "creator", "pruebas");
			
			
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		//---------------------------------------------------------------------//
	}
	
	
	public void testInformAgentRole7a()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

			/**-----------
			 * --1a							
			 * -----------
			 */




			String result = omsInterface.informMembers("jerarquia", "", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 4", 4, responseParser.getItemsList().size());

		

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("subordinado", responseParser.getItemsList().get(0).get(1));
			
			assertEquals("pruebas", responseParser.getItemsList().get(1).get(0));
			assertEquals("subordinado", responseParser.getItemsList().get(1).get(1));

			assertEquals("pruebas2", responseParser.getItemsList().get(2).get(0));
			assertEquals("creador", responseParser.getItemsList().get(2).get(1));

			assertEquals("pruebas2", responseParser.getItemsList().get(3).get(0));
			assertEquals("supervisor", responseParser.getItemsList().get(3).get(1));






			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("equipo", "", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 4", 4, responseParser.getItemsList().size());

	

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(0).get(1));
			
			assertEquals("pruebas", responseParser.getItemsList().get(1).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(1).get(1));

			assertEquals("pruebas2", responseParser.getItemsList().get(2).get(0));
			assertEquals("creador", responseParser.getItemsList().get(2).get(1));

			assertEquals("pruebas2", responseParser.getItemsList().get(3).get(0));
			assertEquals("manager", responseParser.getItemsList().get(3).get(1));
			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("plana", "", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 4", 4, responseParser.getItemsList().size());



			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(0).get(1));
			
			assertEquals("pruebas", responseParser.getItemsList().get(1).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(1).get(1));

			assertEquals("pruebas2", responseParser.getItemsList().get(2).get(0));
			assertEquals("creador", responseParser.getItemsList().get(2).get(1));

			assertEquals("pruebas2", responseParser.getItemsList().get(3).get(0));
			assertEquals("manager", responseParser.getItemsList().get(3).get(1));


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	public void testInformAgentRole7b()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");


			/**-----------
			 * --1a							
			 * -----------
			 */




			String result = omsInterface.informMembers("jerarquia", "supervisor", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("supervisor", responseParser.getItemsList().get(0).get(1));


			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("equipo", "manager", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("manager", responseParser.getItemsList().get(0).get(1));


			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("plana", "miembro", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", 2, responseParser.getItemsList().size());


		

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(0).get(1));
			
			assertEquals("pruebas", responseParser.getItemsList().get(1).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(1).get(1));
			//---------------------------------------------------------------------//

			
			result = omsInterface.informMembers("jerarquia", "", "creator", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());


			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("creador", responseParser.getItemsList().get(0).get(1));

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	

		//---------------------------------------------------------------------//
	}

	public void testInformAgentRole7c()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");


			/**-----------
			 * --1a							
			 * -----------
			 */




			String result = omsInterface.informMembers("jerarquia", "", "subordinate", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", 2, responseParser.getItemsList().size());

		
			
			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("subordinado", responseParser.getItemsList().get(0).get(1));
			
			assertEquals("pruebas", responseParser.getItemsList().get(1).get(0));
			assertEquals("subordinado", responseParser.getItemsList().get(1).get(1));


			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("equipo", "", "member", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 3", 3, responseParser.getItemsList().size());

			

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(0).get(1));
			
			assertEquals("pruebas", responseParser.getItemsList().get(1).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(1).get(1));

			assertEquals("pruebas2", responseParser.getItemsList().get(2).get(0));
			assertEquals("manager", responseParser.getItemsList().get(2).get(1));


			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("plana", "", "member", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 3", 3, responseParser.getItemsList().size());


	

			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(0).get(1));
			
			assertEquals("pruebas", responseParser.getItemsList().get(1).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(1).get(1));

			assertEquals("pruebas2", responseParser.getItemsList().get(2).get(0));
			assertEquals("manager", responseParser.getItemsList().get(2).get(1));
			//---------------------------------------------------------------------//


			result = omsInterface.informMembers("jerarquia", "", "creator", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());


			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("creador", responseParser.getItemsList().get(0).get(1));



		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		//---------------------------------------------------------------------//
	}

	public void testInformAgentRole7d()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

			/**-----------
			 * --1a							
			 * -----------
			 */




			String result = omsInterface.informMembers("jerarquia", "subordinado", "subordinate", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", 2, responseParser.getItemsList().size());


	
			
			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("subordinado", responseParser.getItemsList().get(0).get(1));
			
			assertEquals("pruebas", responseParser.getItemsList().get(1).get(0));
			assertEquals("subordinado", responseParser.getItemsList().get(1).get(1));

			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("equipo", "manager", "member", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());


			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("manager", responseParser.getItemsList().get(0).get(1));


			//---------------------------------------------------------------------//

			result = omsInterface.informMembers("plana", "miembro", "member", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", 2, responseParser.getItemsList().size());

			
			
			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(0).get(1));
			
			assertEquals("pruebas", responseParser.getItemsList().get(1).get(0));
			assertEquals("miembro", responseParser.getItemsList().get(1).get(1));


			//---------------------------------------------------------------------//


			result = omsInterface.informMembers("jerarquia", "", "creator", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 1", 1, responseParser.getItemsList().size());


			assertEquals("pruebas2", responseParser.getItemsList().get(0).get(0));
			assertEquals("creador", responseParser.getItemsList().get(0).get(1));


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		//---------------------------------------------------------------------//
	}
	
	
}
