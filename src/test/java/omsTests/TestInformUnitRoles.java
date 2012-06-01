package omsTests;

import java.util.ArrayList;

import junit.framework.TestCase;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.SF;
import es.upv.dsic.gti_ia.organization.THOMASException;


public class TestInformUnitRoles extends TestCase {

	OMSProxy omsProxy = null;
	DatabaseAccess dbA = null;


	Agent agent = null;
	Agent agent2 = null;


	OMS oms = null;
	SF sf = null;
	
	public TestInformUnitRoles()
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

	public void testInformUnitRoles1()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro2',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro4',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro5',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

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




			ArrayList<ArrayList<String>> result = omsProxy.informUnitRoles("plana");

			assertEquals("El resultado debe ser 5", 5, result.size());
			
			assertEquals("miembro", result.get(0).get(0));
			assertEquals("member", result.get(0).get(1));
			assertEquals("public", result.get(0).get(2));//visibility
			assertEquals("external", result.get(0).get(3));//Accessibility
			
			assertEquals("miembro2", result.get(1).get(0));
			assertEquals("member", result.get(1).get(1));
			assertEquals("private", result.get(1).get(2));//visibility
			assertEquals("external", result.get(1).get(3));//Accessibility
			
			assertEquals("miembro4", result.get(2).get(0));
			assertEquals("member", result.get(2).get(1));
			assertEquals("public", result.get(2).get(2));//visibility
			assertEquals("internal", result.get(2).get(3));//Accessibility
			
			assertEquals("miembro5", result.get(3).get(0));
			assertEquals("member", result.get(3).get(1));
			assertEquals("private", result.get(3).get(2));//visibility
			assertEquals("internal", result.get(3).get(3));//Accessibility
			
			assertEquals("creador", result.get(4).get(0));
			assertEquals("creator", result.get(4).get(1));
			assertEquals("private", result.get(4).get(2));//visibility
			assertEquals("internal", result.get(4).get(3));//Accessibility

			//---------------------------------------------------------------------//

			

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
	
	public void testInformUnitRoles2()
	{
		try
		{
			String unit = "equipo";
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro4',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro5',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<ArrayList<String>> result = omsProxy.informUnitRoles(unit);

			assertEquals("El resultado debe ser 5", 5, result.size());
			
			assertEquals("miembro", result.get(0).get(0));
			assertEquals("member", result.get(0).get(1));
			assertEquals("public", result.get(0).get(2));//visibility
			assertEquals("external", result.get(0).get(3));//Accessibility
			
			assertEquals("miembro2", result.get(1).get(0));
			assertEquals("member", result.get(1).get(1));
			assertEquals("private", result.get(1).get(2));//visibility
			assertEquals("external", result.get(1).get(3));//Accessibility
			
			assertEquals("miembro4", result.get(2).get(0));
			assertEquals("member", result.get(2).get(1));
			assertEquals("public", result.get(2).get(2));//visibility
			assertEquals("internal", result.get(2).get(3));//Accessibility
			
			assertEquals("miembro5", result.get(3).get(0));
			assertEquals("member", result.get(3).get(1));
			assertEquals("private", result.get(3).get(2));//visibility
			assertEquals("internal", result.get(3).get(3));//Accessibility
			
			assertEquals("creador", result.get(4).get(0));
			assertEquals("creator", result.get(4).get(1));
			assertEquals("private", result.get(4).get(2));//visibility
			assertEquals("internal", result.get(4).get(3));//Accessibility

			//---------------------------------------------------------------------//

			

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
	
	public void testInformUnitRoles3()
	{
		try
		{
			String unit = "jerarquia";
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado3',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado4',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
	
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor3',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor4',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador4',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<ArrayList<String>> result = omsProxy.informUnitRoles(unit);

			assertEquals("El resultado debe ser 12", 12, result.size());
			
			assertEquals("subordinado", result.get(0).get(0));
			assertEquals("subordinate", result.get(0).get(1));
			assertEquals("public", result.get(0).get(2));//visibility
			assertEquals("external", result.get(0).get(3));//Accessibility
			
			assertEquals("subordinado2", result.get(1).get(0));
			assertEquals("subordinate", result.get(1).get(1));
			assertEquals("private", result.get(1).get(2));//visibility
			assertEquals("external", result.get(1).get(3));//Accessibility
			
			assertEquals("subordinado3", result.get(2).get(0));
			assertEquals("subordinate", result.get(2).get(1));
			assertEquals("public", result.get(2).get(2));//visibility
			assertEquals("internal", result.get(2).get(3));//Accessibility
			
			assertEquals("subordinado4", result.get(3).get(0));
			assertEquals("subordinate", result.get(3).get(1));
			assertEquals("private", result.get(3).get(2));//visibility
			assertEquals("internal", result.get(3).get(3));//Accessibility
			
			//---------------------------------------------------------------------//

			assertEquals("supervisor", result.get(4).get(0));
			assertEquals("supervisor", result.get(4).get(1));
			assertEquals("public", result.get(4).get(2));//visibility
			assertEquals("external", result.get(4).get(3));//Accessibility
			
			assertEquals("supervisor2", result.get(5).get(0));
			assertEquals("supervisor", result.get(5).get(1));
			assertEquals("private", result.get(5).get(2));//visibility
			assertEquals("external", result.get(5).get(3));//Accessibility
			
			assertEquals("supervisor3", result.get(6).get(0));
			assertEquals("supervisor", result.get(6).get(1));
			assertEquals("public", result.get(6).get(2));//visibility
			assertEquals("internal", result.get(6).get(3));//Accessibility
			
			assertEquals("supervisor4", result.get(7).get(0));
			assertEquals("supervisor", result.get(7).get(1));
			assertEquals("private", result.get(7).get(2));//visibility
			assertEquals("internal", result.get(7).get(3));//Accessibility
			

			//---------------------------------------------------------------------//
			
			assertEquals("creador", result.get(8).get(0));
			assertEquals("creator", result.get(8).get(1));
			assertEquals("public", result.get(8).get(2));//visibility
			assertEquals("external", result.get(8).get(3));//Accessibility
			
			assertEquals("creador2", result.get(9).get(0));
			assertEquals("creator", result.get(9).get(1));
			assertEquals("private", result.get(9).get(2));//visibility
			assertEquals("external", result.get(9).get(3));//Accessibility
			
			assertEquals("creador3", result.get(10).get(0));
			assertEquals("creator", result.get(10).get(1));
			assertEquals("public", result.get(10).get(2));//visibility
			assertEquals("internal", result.get(10).get(3));//Accessibility
			
			assertEquals("creador4", result.get(11).get(0));
			assertEquals("creator", result.get(11).get(1));
			assertEquals("private", result.get(11).get(2));//visibility
			assertEquals("internal", result.get(11).get(3));//Accessibility
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
	
	public void testInformUnitRoles4()
	{
		try
		{
			String unit = "jerarquia";
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado3',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado4',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
	
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor3',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor4',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador4',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<ArrayList<String>> result = omsProxy.informUnitRoles(unit);

			assertEquals("El resultado debe ser 12", 12, result.size());
			
			assertEquals("subordinado", result.get(0).get(0));
			assertEquals("subordinate", result.get(0).get(1));
			assertEquals("public", result.get(0).get(2));//visibility
			assertEquals("external", result.get(0).get(3));//Accessibility
			
			assertEquals("subordinado2", result.get(1).get(0));
			assertEquals("subordinate", result.get(1).get(1));
			assertEquals("private", result.get(1).get(2));//visibility
			assertEquals("external", result.get(1).get(3));//Accessibility
			
			assertEquals("subordinado3", result.get(2).get(0));
			assertEquals("subordinate", result.get(2).get(1));
			assertEquals("public", result.get(2).get(2));//visibility
			assertEquals("internal", result.get(2).get(3));//Accessibility
			
			assertEquals("subordinado4", result.get(3).get(0));
			assertEquals("subordinate", result.get(3).get(1));
			assertEquals("private", result.get(3).get(2));//visibility
			assertEquals("internal", result.get(3).get(3));//Accessibility
			
			//---------------------------------------------------------------------//

			assertEquals("supervisor", result.get(4).get(0));
			assertEquals("supervisor", result.get(4).get(1));
			assertEquals("public", result.get(4).get(2));//visibility
			assertEquals("external", result.get(4).get(3));//Accessibility
			
			assertEquals("supervisor2", result.get(5).get(0));
			assertEquals("supervisor", result.get(5).get(1));
			assertEquals("private", result.get(5).get(2));//visibility
			assertEquals("external", result.get(5).get(3));//Accessibility
			
			assertEquals("supervisor3", result.get(6).get(0));
			assertEquals("supervisor", result.get(6).get(1));
			assertEquals("public", result.get(6).get(2));//visibility
			assertEquals("internal", result.get(6).get(3));//Accessibility
			
			assertEquals("supervisor4", result.get(7).get(0));
			assertEquals("supervisor", result.get(7).get(1));
			assertEquals("private", result.get(7).get(2));//visibility
			assertEquals("internal", result.get(7).get(3));//Accessibility
			

			//---------------------------------------------------------------------//
			
			assertEquals("creador", result.get(8).get(0));
			assertEquals("creator", result.get(8).get(1));
			assertEquals("public", result.get(8).get(2));//visibility
			assertEquals("external", result.get(8).get(3));//Accessibility
			
			assertEquals("creador2", result.get(9).get(0));
			assertEquals("creator", result.get(9).get(1));
			assertEquals("private", result.get(9).get(2));//visibility
			assertEquals("external", result.get(9).get(3));//Accessibility
			
			assertEquals("creador3", result.get(10).get(0));
			assertEquals("creator", result.get(10).get(1));
			assertEquals("public", result.get(10).get(2));//visibility
			assertEquals("internal", result.get(10).get(3));//Accessibility
			
			assertEquals("creador4", result.get(11).get(0));
			assertEquals("creator", result.get(11).get(1));
			assertEquals("private", result.get(11).get(2));//visibility
			assertEquals("internal", result.get(11).get(3));//Accessibility
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
	
	public void testInformUnitRoles5()
	{
		try
		{
			String unit = "jerarquia";
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado3',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado4',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
	
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor3',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor4',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador4',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<ArrayList<String>> result = omsProxy.informUnitRoles(unit);

			assertEquals("El resultado debe ser 12", 12, result.size());
			
			assertEquals("subordinado", result.get(0).get(0));
			assertEquals("subordinate", result.get(0).get(1));
			assertEquals("public", result.get(0).get(2));//visibility
			assertEquals("external", result.get(0).get(3));//Accessibility
			
			assertEquals("subordinado2", result.get(1).get(0));
			assertEquals("subordinate", result.get(1).get(1));
			assertEquals("private", result.get(1).get(2));//visibility
			assertEquals("external", result.get(1).get(3));//Accessibility
			
			assertEquals("subordinado3", result.get(2).get(0));
			assertEquals("subordinate", result.get(2).get(1));
			assertEquals("public", result.get(2).get(2));//visibility
			assertEquals("internal", result.get(2).get(3));//Accessibility
			
			assertEquals("subordinado4", result.get(3).get(0));
			assertEquals("subordinate", result.get(3).get(1));
			assertEquals("private", result.get(3).get(2));//visibility
			assertEquals("internal", result.get(3).get(3));//Accessibility
			
			//---------------------------------------------------------------------//

			assertEquals("supervisor", result.get(4).get(0));
			assertEquals("supervisor", result.get(4).get(1));
			assertEquals("public", result.get(4).get(2));//visibility
			assertEquals("external", result.get(4).get(3));//Accessibility
			
			assertEquals("supervisor2", result.get(5).get(0));
			assertEquals("supervisor", result.get(5).get(1));
			assertEquals("private", result.get(5).get(2));//visibility
			assertEquals("external", result.get(5).get(3));//Accessibility
			
			assertEquals("supervisor3", result.get(6).get(0));
			assertEquals("supervisor", result.get(6).get(1));
			assertEquals("public", result.get(6).get(2));//visibility
			assertEquals("internal", result.get(6).get(3));//Accessibility
			
			assertEquals("supervisor4", result.get(7).get(0));
			assertEquals("supervisor", result.get(7).get(1));
			assertEquals("private", result.get(7).get(2));//visibility
			assertEquals("internal", result.get(7).get(3));//Accessibility
			

			//---------------------------------------------------------------------//
			
			assertEquals("creador", result.get(8).get(0));
			assertEquals("creator", result.get(8).get(1));
			assertEquals("public", result.get(8).get(2));//visibility
			assertEquals("external", result.get(8).get(3));//Accessibility
			
			assertEquals("creador2", result.get(9).get(0));
			assertEquals("creator", result.get(9).get(1));
			assertEquals("private", result.get(9).get(2));//visibility
			assertEquals("external", result.get(9).get(3));//Accessibility
			
			assertEquals("creador3", result.get(10).get(0));
			assertEquals("creator", result.get(10).get(1));
			assertEquals("public", result.get(10).get(2));//visibility
			assertEquals("internal", result.get(10).get(3));//Accessibility
			
			assertEquals("creador4", result.get(11).get(0));
			assertEquals("creator", result.get(11).get(1));
			assertEquals("private", result.get(11).get(2));//visibility
			assertEquals("internal", result.get(11).get(3));//Accessibility
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
	
	public void testInformUnitRoles6()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro5',(SELECT idunitList FROM unitList WHERE unitName = 'plana2'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
		
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro2',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro4',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro5',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro5' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana2'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<ArrayList<String>> result = omsProxy.informUnitRoles("plana");

			assertEquals("El resultado debe ser 2", 2, result.size());
			
			assertEquals("miembro", result.get(0).get(0));
			assertEquals("member", result.get(0).get(1));
			assertEquals("public", result.get(0).get(2));//visibility
			assertEquals("external", result.get(0).get(3));//Accessibility
			
			assertEquals("miembro4", result.get(1).get(0));
			assertEquals("member", result.get(1).get(1));
			assertEquals("public", result.get(1).get(2));//visibility
			assertEquals("internal", result.get(1).get(3));//Accessibility
			
	

			//---------------------------------------------------------------------//

			

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
	
	public void testInformUnitRoles7()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro5',(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
		
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro4',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('miembro5',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'member'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'miembro5' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo2'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<ArrayList<String>> result = omsProxy.informUnitRoles("equipo");

			assertEquals("El resultado debe ser 2", 2, result.size());
			
			assertEquals("miembro", result.get(0).get(0));
			assertEquals("member", result.get(0).get(1));
			assertEquals("public", result.get(0).get(2));//visibility
			assertEquals("external", result.get(0).get(3));//Accessibility
			
			assertEquals("miembro4", result.get(1).get(0));
			assertEquals("member", result.get(1).get(1));
			assertEquals("public", result.get(1).get(2));//visibility
			assertEquals("internal", result.get(1).get(3));//Accessibility
			
	

			//---------------------------------------------------------------------//

			

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
	
	public void testInformUnitRoles8()
	{
		try
		{
			String unit = "jerarquia";
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado5',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado3',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado4',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
	
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor3',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor4',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador4',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado5' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<ArrayList<String>> result = omsProxy.informUnitRoles(unit);

			assertEquals("El resultado debe ser 6", 6, result.size());
			
			assertEquals("subordinado", result.get(0).get(0));
			assertEquals("subordinate", result.get(0).get(1));
			assertEquals("public", result.get(0).get(2));//visibility
			assertEquals("external", result.get(0).get(3));//Accessibility
			
			assertEquals("subordinado3", result.get(1).get(0));
			assertEquals("subordinate", result.get(1).get(1));
			assertEquals("public", result.get(1).get(2));//visibility
			assertEquals("internal", result.get(1).get(3));//Accessibility
			

			
			//---------------------------------------------------------------------//

			assertEquals("supervisor", result.get(2).get(0));
			assertEquals("supervisor", result.get(2).get(1));
			assertEquals("public", result.get(2).get(2));//visibility
			assertEquals("external", result.get(2).get(3));//Accessibility
			
			assertEquals("supervisor3", result.get(3).get(0));
			assertEquals("supervisor", result.get(3).get(1));
			assertEquals("public", result.get(3).get(2));//visibility
			assertEquals("internal", result.get(3).get(3));//Accessibility
			
			

			//---------------------------------------------------------------------//
			
			assertEquals("creador2", result.get(4).get(0));
			assertEquals("creator", result.get(4).get(1));
			assertEquals("public", result.get(4).get(2));//visibility
			assertEquals("external", result.get(4).get(3));//Accessibility
			
			assertEquals("creador4", result.get(5).get(0));
			assertEquals("creator", result.get(5).get(1));
			assertEquals("public", result.get(5).get(2));//visibility
			assertEquals("internal", result.get(5).get(3));//Accessibility
			
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
	
	public void testInformUnitRoles9()
	{
		try
		{
			String unit = "jerarquia";
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor5',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado3',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado4',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
	
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor3',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor4',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador4',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor5' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<ArrayList<String>> result = omsProxy.informUnitRoles(unit);

			assertEquals("El resultado debe ser 6", 6, result.size());
			
			assertEquals("subordinado", result.get(0).get(0));
			assertEquals("subordinate", result.get(0).get(1));
			assertEquals("public", result.get(0).get(2));//visibility
			assertEquals("external", result.get(0).get(3));//Accessibility
			
			assertEquals("subordinado3", result.get(1).get(0));
			assertEquals("subordinate", result.get(1).get(1));
			assertEquals("public", result.get(1).get(2));//visibility
			assertEquals("internal", result.get(1).get(3));//Accessibility
			

			
			//---------------------------------------------------------------------//

			assertEquals("supervisor", result.get(2).get(0));
			assertEquals("supervisor", result.get(2).get(1));
			assertEquals("public", result.get(2).get(2));//visibility
			assertEquals("external", result.get(2).get(3));//Accessibility
			
			assertEquals("supervisor3", result.get(3).get(0));
			assertEquals("supervisor", result.get(3).get(1));
			assertEquals("public", result.get(3).get(2));//visibility
			assertEquals("internal", result.get(3).get(3));//Accessibility
			
			

			//---------------------------------------------------------------------//
			
			assertEquals("creador2", result.get(4).get(0));
			assertEquals("creator", result.get(4).get(1));
			assertEquals("public", result.get(4).get(2));//visibility
			assertEquals("external", result.get(4).get(3));//Accessibility
			
			assertEquals("creador4", result.get(5).get(0));
			assertEquals("creator", result.get(5).get(1));
			assertEquals("public", result.get(5).get(2));//visibility
			assertEquals("internal", result.get(5).get(3));//Accessibility
			
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
	
	public void testInformUnitRoles10()
	{
		try
		{
			String unit = "jerarquia";
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador5',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado3',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('subordinado4',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'subordinate'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
	
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor3',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('supervisor4',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'supervisor'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES"+ 
					"('creador4',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE position = 'creator'), "+
					"(SELECT idaccesibility FROM accesibility WHERE accesibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			


			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'creador5' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<ArrayList<String>> result = omsProxy.informUnitRoles(unit);

			assertEquals("El resultado debe ser 6", 6, result.size());
			
			assertEquals("subordinado", result.get(0).get(0));
			assertEquals("subordinate", result.get(0).get(1));
			assertEquals("public", result.get(0).get(2));//visibility
			assertEquals("external", result.get(0).get(3));//Accessibility
			
			assertEquals("subordinado3", result.get(1).get(0));
			assertEquals("subordinate", result.get(1).get(1));
			assertEquals("public", result.get(1).get(2));//visibility
			assertEquals("internal", result.get(1).get(3));//Accessibility
			

			
			//---------------------------------------------------------------------//

			assertEquals("supervisor", result.get(2).get(0));
			assertEquals("supervisor", result.get(2).get(1));
			assertEquals("public", result.get(2).get(2));//visibility
			assertEquals("external", result.get(2).get(3));//Accessibility
			
			assertEquals("supervisor3", result.get(3).get(0));
			assertEquals("supervisor", result.get(3).get(1));
			assertEquals("public", result.get(3).get(2));//visibility
			assertEquals("internal", result.get(3).get(3));//Accessibility
			
			

			//---------------------------------------------------------------------//
			
			assertEquals("creador2", result.get(4).get(0));
			assertEquals("creator", result.get(4).get(1));
			assertEquals("public", result.get(4).get(2));//visibility
			assertEquals("external", result.get(4).get(3));//Accessibility
			
			assertEquals("creador4", result.get(5).get(0));
			assertEquals("creator", result.get(5).get(1));
			assertEquals("public", result.get(5).get(2));//visibility
			assertEquals("internal", result.get(5).get(3));//Accessibility
			
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
