package omsTests;



import persistence.OMSInterface;

import junit.framework.TestCase;



public class TestInformUnitRoles extends TestCase {

	
	DatabaseAccess dbA = null;
	OMSInterface omsInterface = null;
	ResponseParser responseParser = null;

		
	public TestInformUnitRoles()
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

		



		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");





	}

	public void testInformUnitRoles1()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro2',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro4',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro5',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			String result = omsInterface.informUnitRoles("plana", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 5", 5, responseParser.getItemsList().size());
			
			assertEquals("miembro", responseParser.getItemsList().get(0).get(0));
			assertEquals("member", responseParser.getItemsList().get(0).get(1));
			assertEquals("public", responseParser.getItemsList().get(0).get(2));//visibility
			assertEquals("external", responseParser.getItemsList().get(0).get(3));//Accessibility
			
			assertEquals("miembro2", responseParser.getItemsList().get(1).get(0));
			assertEquals("member", responseParser.getItemsList().get(1).get(1));
			assertEquals("private", responseParser.getItemsList().get(1).get(2));//visibility
			assertEquals("external", responseParser.getItemsList().get(1).get(3));//Accessibility
			
			assertEquals("miembro4", responseParser.getItemsList().get(2).get(0));
			assertEquals("member", responseParser.getItemsList().get(2).get(1));
			assertEquals("public", responseParser.getItemsList().get(2).get(2));//visibility
			assertEquals("internal", responseParser.getItemsList().get(2).get(3));//Accessibility
			
			assertEquals("miembro5", responseParser.getItemsList().get(3).get(0));
			assertEquals("member", responseParser.getItemsList().get(3).get(1));
			assertEquals("private", responseParser.getItemsList().get(3).get(2));//visibility
			assertEquals("internal", responseParser.getItemsList().get(3).get(3));//Accessibility
			
			assertEquals("creador", responseParser.getItemsList().get(4).get(0));
			assertEquals("creator", responseParser.getItemsList().get(4).get(1));
			assertEquals("private", responseParser.getItemsList().get(4).get(2));//visibility
			assertEquals("internal", responseParser.getItemsList().get(4).get(3));//Accessibility

			//---------------------------------------------------------------------//

			

			//---------------------------------------------------------------------//


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformUnitRoles2()
	{
		try
		{
			String unit = "equipo";
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro4',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro5',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			String result = omsInterface.informUnitRoles(unit, "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 5", 5, responseParser.getItemsList().size());
			
			assertEquals("miembro", responseParser.getItemsList().get(0).get(0));
			assertEquals("member", responseParser.getItemsList().get(0).get(1));
			assertEquals("public", responseParser.getItemsList().get(0).get(2));//visibility
			assertEquals("external", responseParser.getItemsList().get(0).get(3));//Accessibility
			
			assertEquals("miembro2", responseParser.getItemsList().get(1).get(0));
			assertEquals("member", responseParser.getItemsList().get(1).get(1));
			assertEquals("private", responseParser.getItemsList().get(1).get(2));//visibility
			assertEquals("external", responseParser.getItemsList().get(1).get(3));//Accessibility
			
			assertEquals("miembro4", responseParser.getItemsList().get(2).get(0));
			assertEquals("member", responseParser.getItemsList().get(2).get(1));
			assertEquals("public", responseParser.getItemsList().get(2).get(2));//visibility
			assertEquals("internal", responseParser.getItemsList().get(2).get(3));//Accessibility
			
			assertEquals("miembro5", responseParser.getItemsList().get(3).get(0));
			assertEquals("member", responseParser.getItemsList().get(3).get(1));
			assertEquals("private", responseParser.getItemsList().get(3).get(2));//visibility
			assertEquals("internal", responseParser.getItemsList().get(3).get(3));//Accessibility
			
			assertEquals("creador", responseParser.getItemsList().get(4).get(0));
			assertEquals("creator", responseParser.getItemsList().get(4).get(1));
			assertEquals("private", responseParser.getItemsList().get(4).get(2));//visibility
			assertEquals("internal", responseParser.getItemsList().get(4).get(3));//Accessibility

			//---------------------------------------------------------------------//

			

			//---------------------------------------------------------------------//


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformUnitRoles3()
	{
		try
		{
			String unit = "jerarquia";
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado3',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado4',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
	
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor3',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor4',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador4',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			String result = omsInterface.informUnitRoles(unit, "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 12", 12, responseParser.getItemsList().size());
			
			assertEquals("subordinado", responseParser.getItemsList().get(0).get(0));
			assertEquals("subordinate", responseParser.getItemsList().get(0).get(1));
			assertEquals("public", responseParser.getItemsList().get(0).get(2));//visibility
			assertEquals("external", responseParser.getItemsList().get(0).get(3));//Accessibility
			
			assertEquals("subordinado2", responseParser.getItemsList().get(1).get(0));
			assertEquals("subordinate", responseParser.getItemsList().get(1).get(1));
			assertEquals("private", responseParser.getItemsList().get(1).get(2));//visibility
			assertEquals("external", responseParser.getItemsList().get(1).get(3));//Accessibility
			
			assertEquals("subordinado3", responseParser.getItemsList().get(2).get(0));
			assertEquals("subordinate", responseParser.getItemsList().get(2).get(1));
			assertEquals("public", responseParser.getItemsList().get(2).get(2));//visibility
			assertEquals("internal", responseParser.getItemsList().get(2).get(3));//Accessibility
			
			assertEquals("subordinado4", responseParser.getItemsList().get(3).get(0));
			assertEquals("subordinate", responseParser.getItemsList().get(3).get(1));
			assertEquals("private", responseParser.getItemsList().get(3).get(2));//visibility
			assertEquals("internal", responseParser.getItemsList().get(3).get(3));//Accessibility
			
			//---------------------------------------------------------------------//

			assertEquals("supervisor", responseParser.getItemsList().get(4).get(0));
			assertEquals("supervisor", responseParser.getItemsList().get(4).get(1));
			assertEquals("public", responseParser.getItemsList().get(4).get(2));//visibility
			assertEquals("external", responseParser.getItemsList().get(4).get(3));//Accessibility
			
			assertEquals("supervisor2", responseParser.getItemsList().get(5).get(0));
			assertEquals("supervisor", responseParser.getItemsList().get(5).get(1));
			assertEquals("private", responseParser.getItemsList().get(5).get(2));//visibility
			assertEquals("external", responseParser.getItemsList().get(5).get(3));//Accessibility
			
			assertEquals("supervisor3", responseParser.getItemsList().get(6).get(0));
			assertEquals("supervisor", responseParser.getItemsList().get(6).get(1));
			assertEquals("public", responseParser.getItemsList().get(6).get(2));//visibility
			assertEquals("internal", responseParser.getItemsList().get(6).get(3));//Accessibility
			
			assertEquals("supervisor4", responseParser.getItemsList().get(7).get(0));
			assertEquals("supervisor", responseParser.getItemsList().get(7).get(1));
			assertEquals("private", responseParser.getItemsList().get(7).get(2));//visibility
			assertEquals("internal", responseParser.getItemsList().get(7).get(3));//Accessibility
			

			//---------------------------------------------------------------------//
			
			assertEquals("creador", responseParser.getItemsList().get(8).get(0));
			assertEquals("creator", responseParser.getItemsList().get(8).get(1));
			assertEquals("public", responseParser.getItemsList().get(8).get(2));//visibility
			assertEquals("external", responseParser.getItemsList().get(8).get(3));//Accessibility
			
			assertEquals("creador2", responseParser.getItemsList().get(9).get(0));
			assertEquals("creator", responseParser.getItemsList().get(9).get(1));
			assertEquals("private", responseParser.getItemsList().get(9).get(2));//visibility
			assertEquals("external", responseParser.getItemsList().get(9).get(3));//Accessibility
			
			assertEquals("creador3", responseParser.getItemsList().get(10).get(0));
			assertEquals("creator", responseParser.getItemsList().get(10).get(1));
			assertEquals("public", responseParser.getItemsList().get(10).get(2));//visibility
			assertEquals("internal", responseParser.getItemsList().get(10).get(3));//Accessibility
			
			assertEquals("creador4", responseParser.getItemsList().get(11).get(0));
			assertEquals("creator", responseParser.getItemsList().get(11).get(1));
			assertEquals("private", responseParser.getItemsList().get(11).get(2));//visibility
			assertEquals("internal", responseParser.getItemsList().get(11).get(3));//Accessibility
			//---------------------------------------------------------------------//

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformUnitRoles4()
	{
		try
		{
			String unit = "jerarquia";
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado3',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado4',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
	
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor3',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor4',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador4',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			String result = omsInterface.informUnitRoles(unit, "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 12", 12, responseParser.getItemsList().size());
			
			assertEquals("subordinado", responseParser.getItemsList().get(0).get(0));
			assertEquals("subordinate", responseParser.getItemsList().get(0).get(1));
			assertEquals("public", responseParser.getItemsList().get(0).get(2));//visibility
			assertEquals("external", responseParser.getItemsList().get(0).get(3));//Accessibility
			
			assertEquals("subordinado2", responseParser.getItemsList().get(1).get(0));
			assertEquals("subordinate", responseParser.getItemsList().get(1).get(1));
			assertEquals("private", responseParser.getItemsList().get(1).get(2));//visibility
			assertEquals("external", responseParser.getItemsList().get(1).get(3));//Accessibility
			
			assertEquals("subordinado3", responseParser.getItemsList().get(2).get(0));
			assertEquals("subordinate", responseParser.getItemsList().get(2).get(1));
			assertEquals("public", responseParser.getItemsList().get(2).get(2));//visibility
			assertEquals("internal", responseParser.getItemsList().get(2).get(3));//Accessibility
			
			assertEquals("subordinado4", responseParser.getItemsList().get(3).get(0));
			assertEquals("subordinate", responseParser.getItemsList().get(3).get(1));
			assertEquals("private", responseParser.getItemsList().get(3).get(2));//visibility
			assertEquals("internal", responseParser.getItemsList().get(3).get(3));//Accessibility
			
			//---------------------------------------------------------------------//

			assertEquals("supervisor", responseParser.getItemsList().get(4).get(0));
			assertEquals("supervisor", responseParser.getItemsList().get(4).get(1));
			assertEquals("public", responseParser.getItemsList().get(4).get(2));//visibility
			assertEquals("external", responseParser.getItemsList().get(4).get(3));//Accessibility
			
			assertEquals("supervisor2", responseParser.getItemsList().get(5).get(0));
			assertEquals("supervisor", responseParser.getItemsList().get(5).get(1));
			assertEquals("private", responseParser.getItemsList().get(5).get(2));//visibility
			assertEquals("external", responseParser.getItemsList().get(5).get(3));//Accessibility
			
			assertEquals("supervisor3", responseParser.getItemsList().get(6).get(0));
			assertEquals("supervisor", responseParser.getItemsList().get(6).get(1));
			assertEquals("public", responseParser.getItemsList().get(6).get(2));//visibility
			assertEquals("internal", responseParser.getItemsList().get(6).get(3));//Accessibility
			
			assertEquals("supervisor4", responseParser.getItemsList().get(7).get(0));
			assertEquals("supervisor", responseParser.getItemsList().get(7).get(1));
			assertEquals("private", responseParser.getItemsList().get(7).get(2));//visibility
			assertEquals("internal", responseParser.getItemsList().get(7).get(3));//Accessibility
			

			//---------------------------------------------------------------------//
			
			assertEquals("creador", responseParser.getItemsList().get(8).get(0));
			assertEquals("creator", responseParser.getItemsList().get(8).get(1));
			assertEquals("public", responseParser.getItemsList().get(8).get(2));//visibility
			assertEquals("external", responseParser.getItemsList().get(8).get(3));//Accessibility
			
			assertEquals("creador2", responseParser.getItemsList().get(9).get(0));
			assertEquals("creator", responseParser.getItemsList().get(9).get(1));
			assertEquals("private", responseParser.getItemsList().get(9).get(2));//visibility
			assertEquals("external", responseParser.getItemsList().get(9).get(3));//Accessibility
			
			assertEquals("creador3", responseParser.getItemsList().get(10).get(0));
			assertEquals("creator", responseParser.getItemsList().get(10).get(1));
			assertEquals("public", responseParser.getItemsList().get(10).get(2));//visibility
			assertEquals("internal", responseParser.getItemsList().get(10).get(3));//Accessibility
			
			assertEquals("creador4", responseParser.getItemsList().get(11).get(0));
			assertEquals("creator", responseParser.getItemsList().get(11).get(1));
			assertEquals("private", responseParser.getItemsList().get(11).get(2));//visibility
			assertEquals("internal", responseParser.getItemsList().get(11).get(3));//Accessibility
			//---------------------------------------------------------------------//

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformUnitRoles5()
	{
		try
		{
			String unit = "jerarquia";
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado3',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado4',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
	
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor3',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor4',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador4',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			String result = omsInterface.informUnitRoles(unit, "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 12", 12, responseParser.getItemsList().size());
			
			assertEquals("subordinado", responseParser.getItemsList().get(0).get(0));
			assertEquals("subordinate", responseParser.getItemsList().get(0).get(1));
			assertEquals("public", responseParser.getItemsList().get(0).get(2));//visibility
			assertEquals("external", responseParser.getItemsList().get(0).get(3));//Accessibility
			
			assertEquals("subordinado2", responseParser.getItemsList().get(1).get(0));
			assertEquals("subordinate", responseParser.getItemsList().get(1).get(1));
			assertEquals("private", responseParser.getItemsList().get(1).get(2));//visibility
			assertEquals("external", responseParser.getItemsList().get(1).get(3));//Accessibility
			
			assertEquals("subordinado3", responseParser.getItemsList().get(2).get(0));
			assertEquals("subordinate", responseParser.getItemsList().get(2).get(1));
			assertEquals("public", responseParser.getItemsList().get(2).get(2));//visibility
			assertEquals("internal", responseParser.getItemsList().get(2).get(3));//Accessibility
			
			assertEquals("subordinado4", responseParser.getItemsList().get(3).get(0));
			assertEquals("subordinate", responseParser.getItemsList().get(3).get(1));
			assertEquals("private", responseParser.getItemsList().get(3).get(2));//visibility
			assertEquals("internal", responseParser.getItemsList().get(3).get(3));//Accessibility
			
			//---------------------------------------------------------------------//

			assertEquals("supervisor", responseParser.getItemsList().get(4).get(0));
			assertEquals("supervisor", responseParser.getItemsList().get(4).get(1));
			assertEquals("public", responseParser.getItemsList().get(4).get(2));//visibility
			assertEquals("external", responseParser.getItemsList().get(4).get(3));//Accessibility
			
			assertEquals("supervisor2", responseParser.getItemsList().get(5).get(0));
			assertEquals("supervisor", responseParser.getItemsList().get(5).get(1));
			assertEquals("private", responseParser.getItemsList().get(5).get(2));//visibility
			assertEquals("external", responseParser.getItemsList().get(5).get(3));//Accessibility
			
			assertEquals("supervisor3", responseParser.getItemsList().get(6).get(0));
			assertEquals("supervisor", responseParser.getItemsList().get(6).get(1));
			assertEquals("public", responseParser.getItemsList().get(6).get(2));//visibility
			assertEquals("internal", responseParser.getItemsList().get(6).get(3));//Accessibility
			
			assertEquals("supervisor4", responseParser.getItemsList().get(7).get(0));
			assertEquals("supervisor", responseParser.getItemsList().get(7).get(1));
			assertEquals("private", responseParser.getItemsList().get(7).get(2));//visibility
			assertEquals("internal", responseParser.getItemsList().get(7).get(3));//Accessibility
			

			//---------------------------------------------------------------------//
			
			assertEquals("creador", responseParser.getItemsList().get(8).get(0));
			assertEquals("creator", responseParser.getItemsList().get(8).get(1));
			assertEquals("public", responseParser.getItemsList().get(8).get(2));//visibility
			assertEquals("external", responseParser.getItemsList().get(8).get(3));//Accessibility
			
			assertEquals("creador2", responseParser.getItemsList().get(9).get(0));
			assertEquals("creator", responseParser.getItemsList().get(9).get(1));
			assertEquals("private", responseParser.getItemsList().get(9).get(2));//visibility
			assertEquals("external", responseParser.getItemsList().get(9).get(3));//Accessibility
			
			assertEquals("creador3", responseParser.getItemsList().get(10).get(0));
			assertEquals("creator", responseParser.getItemsList().get(10).get(1));
			assertEquals("public", responseParser.getItemsList().get(10).get(2));//visibility
			assertEquals("internal", responseParser.getItemsList().get(10).get(3));//Accessibility
			
			assertEquals("creador4", responseParser.getItemsList().get(11).get(0));
			assertEquals("creator", responseParser.getItemsList().get(11).get(1));
			assertEquals("private", responseParser.getItemsList().get(11).get(2));//visibility
			assertEquals("internal", responseParser.getItemsList().get(11).get(3));//Accessibility
			//---------------------------------------------------------------------//

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformUnitRoles6()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro5',(SELECT idunitList FROM unitList WHERE unitName = 'plana2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
		
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro2',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro4',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro5',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro5' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana2'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			String result = omsInterface.informUnitRoles("plana", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", 2, responseParser.getItemsList().size());
			
			assertEquals("miembro", responseParser.getItemsList().get(0).get(0));
			assertEquals("member", responseParser.getItemsList().get(0).get(1));
			assertEquals("public", responseParser.getItemsList().get(0).get(2));//visibility
			assertEquals("external", responseParser.getItemsList().get(0).get(3));//Accessibility
			
			assertEquals("miembro4", responseParser.getItemsList().get(1).get(0));
			assertEquals("member", responseParser.getItemsList().get(1).get(1));
			assertEquals("public", responseParser.getItemsList().get(1).get(2));//visibility
			assertEquals("internal", responseParser.getItemsList().get(1).get(3));//Accessibility
			
	

			//---------------------------------------------------------------------//

			

			//---------------------------------------------------------------------//


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformUnitRoles7()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro5',(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
		
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro4',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro5',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro5' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo2'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			String result = omsInterface.informUnitRoles("equipo", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 2", 2, responseParser.getItemsList().size());
			
			assertEquals("miembro", responseParser.getItemsList().get(0).get(0));
			assertEquals("member", responseParser.getItemsList().get(0).get(1));
			assertEquals("public", responseParser.getItemsList().get(0).get(2));//visibility
			assertEquals("external", responseParser.getItemsList().get(0).get(3));//Accessibility
			
			assertEquals("miembro4", responseParser.getItemsList().get(1).get(0));
			assertEquals("member", responseParser.getItemsList().get(1).get(1));
			assertEquals("public", responseParser.getItemsList().get(1).get(2));//visibility
			assertEquals("internal", responseParser.getItemsList().get(1).get(3));//Accessibility
			
	

			//---------------------------------------------------------------------//

			

			//---------------------------------------------------------------------//


		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformUnitRoles8()
	{
		try
		{
			String unit = "jerarquia";
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado5',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado3',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado4',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
	
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor3',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor4',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador4',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado5' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			String result = omsInterface.informUnitRoles(unit, "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 6", 6, responseParser.getItemsList().size());
			
			assertEquals("subordinado", responseParser.getItemsList().get(0).get(0));
			assertEquals("subordinate", responseParser.getItemsList().get(0).get(1));
			assertEquals("public", responseParser.getItemsList().get(0).get(2));//visibility
			assertEquals("external", responseParser.getItemsList().get(0).get(3));//Accessibility
			
			assertEquals("subordinado3", responseParser.getItemsList().get(1).get(0));
			assertEquals("subordinate", responseParser.getItemsList().get(1).get(1));
			assertEquals("public", responseParser.getItemsList().get(1).get(2));//visibility
			assertEquals("internal", responseParser.getItemsList().get(1).get(3));//Accessibility
			

			
			//---------------------------------------------------------------------//

			assertEquals("supervisor", responseParser.getItemsList().get(2).get(0));
			assertEquals("supervisor", responseParser.getItemsList().get(2).get(1));
			assertEquals("public", responseParser.getItemsList().get(2).get(2));//visibility
			assertEquals("external", responseParser.getItemsList().get(2).get(3));//Accessibility
			
			assertEquals("supervisor3", responseParser.getItemsList().get(3).get(0));
			assertEquals("supervisor", responseParser.getItemsList().get(3).get(1));
			assertEquals("public", responseParser.getItemsList().get(3).get(2));//visibility
			assertEquals("internal", responseParser.getItemsList().get(3).get(3));//Accessibility
			
			

			//---------------------------------------------------------------------//
			
			assertEquals("creador2", responseParser.getItemsList().get(4).get(0));
			assertEquals("creator", responseParser.getItemsList().get(4).get(1));
			assertEquals("public", responseParser.getItemsList().get(4).get(2));//visibility
			assertEquals("external", responseParser.getItemsList().get(4).get(3));//Accessibility
			
			assertEquals("creador4", responseParser.getItemsList().get(5).get(0));
			assertEquals("creator", responseParser.getItemsList().get(5).get(1));
			assertEquals("public", responseParser.getItemsList().get(5).get(2));//visibility
			assertEquals("internal", responseParser.getItemsList().get(5).get(3));//Accessibility
			
			//---------------------------------------------------------------------//

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformUnitRoles9()
	{
		try
		{
			String unit = "jerarquia";
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor5',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado3',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado4',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
	
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor3',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor4',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador4',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor5' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			String result = omsInterface.informUnitRoles(unit, "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 6", 6, responseParser.getItemsList().size());
			
			assertEquals("subordinado", responseParser.getItemsList().get(0).get(0));
			assertEquals("subordinate", responseParser.getItemsList().get(0).get(1));
			assertEquals("public", responseParser.getItemsList().get(0).get(2));//visibility
			assertEquals("external", responseParser.getItemsList().get(0).get(3));//Accessibility
			
			assertEquals("subordinado3", responseParser.getItemsList().get(1).get(0));
			assertEquals("subordinate", responseParser.getItemsList().get(1).get(1));
			assertEquals("public", responseParser.getItemsList().get(1).get(2));//visibility
			assertEquals("internal", responseParser.getItemsList().get(1).get(3));//Accessibility
			

			
			//---------------------------------------------------------------------//

			assertEquals("supervisor", responseParser.getItemsList().get(2).get(0));
			assertEquals("supervisor", responseParser.getItemsList().get(2).get(1));
			assertEquals("public", responseParser.getItemsList().get(2).get(2));//visibility
			assertEquals("external", responseParser.getItemsList().get(2).get(3));//Accessibility
			
			assertEquals("supervisor3", responseParser.getItemsList().get(3).get(0));
			assertEquals("supervisor", responseParser.getItemsList().get(3).get(1));
			assertEquals("public", responseParser.getItemsList().get(3).get(2));//visibility
			assertEquals("internal", responseParser.getItemsList().get(3).get(3));//Accessibility
			
			

			//---------------------------------------------------------------------//
			
			assertEquals("creador2", responseParser.getItemsList().get(4).get(0));
			assertEquals("creator", responseParser.getItemsList().get(4).get(1));
			assertEquals("public", responseParser.getItemsList().get(4).get(2));//visibility
			assertEquals("external", responseParser.getItemsList().get(4).get(3));//Accessibility
			
			assertEquals("creador4", responseParser.getItemsList().get(5).get(0));
			assertEquals("creator", responseParser.getItemsList().get(5).get(1));
			assertEquals("public", responseParser.getItemsList().get(5).get(2));//visibility
			assertEquals("internal", responseParser.getItemsList().get(5).get(3));//Accessibility
			
			//---------------------------------------------------------------------//

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testInformUnitRoles10()
	{
		try
		{
			String unit = "jerarquia";
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador5',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado3',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado4',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
	
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor3',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor4',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador4',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador5' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			String result = omsInterface.informUnitRoles(unit, "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El resultado debe ser 6", 6, responseParser.getItemsList().size());
			
			assertEquals("subordinado", responseParser.getItemsList().get(0).get(0));
			assertEquals("subordinate", responseParser.getItemsList().get(0).get(1));
			assertEquals("public", responseParser.getItemsList().get(0).get(2));//visibility
			assertEquals("external", responseParser.getItemsList().get(0).get(3));//Accessibility
			
			assertEquals("subordinado3", responseParser.getItemsList().get(1).get(0));
			assertEquals("subordinate", responseParser.getItemsList().get(1).get(1));
			assertEquals("public", responseParser.getItemsList().get(1).get(2));//visibility
			assertEquals("internal", responseParser.getItemsList().get(1).get(3));//Accessibility
			

			
			//---------------------------------------------------------------------//

			assertEquals("supervisor", responseParser.getItemsList().get(2).get(0));
			assertEquals("supervisor", responseParser.getItemsList().get(2).get(1));
			assertEquals("public", responseParser.getItemsList().get(2).get(2));//visibility
			assertEquals("external", responseParser.getItemsList().get(2).get(3));//Accessibility
			
			assertEquals("supervisor3", responseParser.getItemsList().get(3).get(0));
			assertEquals("supervisor", responseParser.getItemsList().get(3).get(1));
			assertEquals("public", responseParser.getItemsList().get(3).get(2));//visibility
			assertEquals("internal", responseParser.getItemsList().get(3).get(3));//Accessibility
			
			

			//---------------------------------------------------------------------//
			
			assertEquals("creador2", responseParser.getItemsList().get(4).get(0));
			assertEquals("creator", responseParser.getItemsList().get(4).get(1));
			assertEquals("public", responseParser.getItemsList().get(4).get(2));//visibility
			assertEquals("external", responseParser.getItemsList().get(4).get(3));//Accessibility
			
			assertEquals("creador4", responseParser.getItemsList().get(5).get(0));
			assertEquals("creator", responseParser.getItemsList().get(5).get(1));
			assertEquals("public", responseParser.getItemsList().get(5).get(2));//visibility
			assertEquals("internal", responseParser.getItemsList().get(5).get(3));//Accessibility
			
			//---------------------------------------------------------------------//

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
}
