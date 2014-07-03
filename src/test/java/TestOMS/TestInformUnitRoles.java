package TestOMS;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.SF;
import es.upv.dsic.gti_ia.organization.exception.THOMASException;


public class TestInformUnitRoles {

	OMSProxy omsProxy = null;
	DatabaseAccess dbA = null;


	Agent agent = null;
	Agent agent2 = null;


	OMS oms = null;
	SF sf = null;
	
	Process qpid_broker;
	
	@After
	public void tearDown() throws Exception {

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

		agent2.terminate();
		agent2 = null;

		oms.Shutdown();
		sf.Shutdown();
		
		oms.await();
		sf.await();
		
		oms = null;
		sf = null;

		AgentsConnection.disconnect();
		qpidManager.UnixQpidManager.stopQpid(qpid_broker);
	}
	
	@Before
	public void setUp() throws Exception {

		qpid_broker = qpidManager.UnixQpidManager.startQpid(Runtime.getRuntime(), qpid_broker);
		

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

	@Test(timeout = 5 * 60 * 1000)
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




			ArrayList<ArrayList<String>> result = omsProxy.informUnitRoles("plana");

			assertEquals("El resultado debe ser 5", 5, result.size());
			
			ArrayList<String> aux = new ArrayList<String>();
			aux.add("miembro");
			aux.add("member");
			aux.add("public");//visibility
			aux.add("external");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("miembro2");
			aux.add("member");
			aux.add("private");//visibility
			aux.add("external");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("miembro4");
			aux.add("member");
			aux.add("public");//visibility
			aux.add("internal");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("miembro5");
			aux.add("member");
			aux.add("private");//visibility
			aux.add("internal");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("creador");
			aux.add("creator");
			aux.add("private");//visibility
			aux.add("internal");//Accessibility
			
			assertTrue(result.contains(aux));

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
	
	@Test(timeout = 5 * 60 * 1000)
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




			ArrayList<ArrayList<String>> result = omsProxy.informUnitRoles(unit);

			assertEquals("El resultado debe ser 5", 5, result.size());
			
			ArrayList<String> aux = new ArrayList<String>();
			aux.add("miembro");
			aux.add("member");
			aux.add("public");//visibility
			aux.add("external");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("miembro2");
			aux.add("member");
			aux.add("private");//visibility
			aux.add("external");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("miembro4");
			aux.add("member");
			aux.add("public");//visibility
			aux.add("internal");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("miembro5");
			aux.add("member");
			aux.add("private");//visibility
			aux.add("internal");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("creador");
			aux.add("creator");
			aux.add("private");//visibility
			aux.add("internal");//Accessibility
			
			assertTrue(result.contains(aux));
			
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
	
	@Test(timeout = 5 * 60 * 1000)
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




			ArrayList<ArrayList<String>> result = omsProxy.informUnitRoles(unit);

			assertEquals("El resultado debe ser 12", 12, result.size());
			
			ArrayList<String> aux = new ArrayList<String>();
			aux.add("subordinado");
			aux.add("subordinate");
			aux.add("public");//visibility
			aux.add("external");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("subordinado2");
			aux.add("subordinate");
			aux.add("private");//visibility
			aux.add("external");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("subordinado3");
			aux.add("subordinate");
			aux.add("public");//visibility
			aux.add("internal");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("subordinado4");
			aux.add("subordinate");
			aux.add("private");//visibility
			aux.add("internal");//Accessibility
			
			assertTrue(result.contains(aux));
			
			//---------------------------------------------------------------------//

			aux.clear();
			aux.add("supervisor");
			aux.add("supervisor");
			aux.add("public");//visibility
			aux.add("external");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("supervisor2");
			aux.add("supervisor");
			aux.add("private");//visibility
			aux.add("external");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("supervisor3");
			aux.add("supervisor");
			aux.add("public");//visibility
			aux.add("internal");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("supervisor4");
			aux.add("supervisor");
			aux.add("private");//visibility
			aux.add("internal");//Accessibility
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//
			
			aux.clear();
			aux.add("creador");
			aux.add("creator");
			aux.add("public");//visibility
			aux.add("external");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("creador2");
			aux.add("creator");
			aux.add("private");//visibility
			aux.add("external");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("creador3");
			aux.add("creator");
			aux.add("public");//visibility
			aux.add("internal");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("creador4");
			aux.add("creator");
			aux.add("private");//visibility
			aux.add("internal");//Accessibility
			
			assertTrue(result.contains(aux));
			
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
	
	@Test(timeout = 5 * 60 * 1000)
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




			ArrayList<ArrayList<String>> result = omsProxy.informUnitRoles(unit);

			assertEquals("El resultado debe ser 12", 12, result.size());
			
			ArrayList<String> aux = new ArrayList<String>();
			aux.add("subordinado");
			aux.add("subordinate");
			aux.add("public");//visibility
			aux.add("external");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("subordinado2");
			aux.add("subordinate");
			aux.add("private");//visibility
			aux.add("external");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("subordinado3");
			aux.add("subordinate");
			aux.add("public");//visibility
			aux.add("internal");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("subordinado4");
			aux.add("subordinate");
			aux.add("private");//visibility
			aux.add("internal");//Accessibility
			
			assertTrue(result.contains(aux));
			
			//---------------------------------------------------------------------//

			aux.clear();
			aux.add("supervisor");
			aux.add("supervisor");
			aux.add("public");//visibility
			aux.add("external");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("supervisor2");
			aux.add("supervisor");
			aux.add("private");//visibility
			aux.add("external");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("supervisor3");
			aux.add("supervisor");
			aux.add("public");//visibility
			aux.add("internal");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("supervisor4");
			aux.add("supervisor");
			aux.add("private");//visibility
			aux.add("internal");//Accessibility
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//
			
			aux.clear();
			aux.add("creador");
			aux.add("creator");
			aux.add("public");//visibility
			aux.add("external");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("creador2");
			aux.add("creator");
			aux.add("private");//visibility
			aux.add("external");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("creador3");
			aux.add("creator");
			aux.add("public");//visibility
			aux.add("internal");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("creador4");
			aux.add("creator");
			aux.add("private");//visibility
			aux.add("internal");//Accessibility
			
			assertTrue(result.contains(aux));
			
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
	
	@Test(timeout = 5 * 60 * 1000)
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




			ArrayList<ArrayList<String>> result = omsProxy.informUnitRoles(unit);

			assertEquals("El resultado debe ser 12", 12, result.size());
			
			ArrayList<String> aux = new ArrayList<String>();
			aux.add("subordinado");
			aux.add("subordinate");
			aux.add("public");//visibility
			aux.add("external");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("subordinado2");
			aux.add("subordinate");
			aux.add("private");//visibility
			aux.add("external");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("subordinado3");
			aux.add("subordinate");
			aux.add("public");//visibility
			aux.add("internal");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("subordinado4");
			aux.add("subordinate");
			aux.add("private");//visibility
			aux.add("internal");//Accessibility
			
			assertTrue(result.contains(aux));
			
			//---------------------------------------------------------------------//

			aux.clear();
			aux.add("supervisor");
			aux.add("supervisor");
			aux.add("public");//visibility
			aux.add("external");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("supervisor2");
			aux.add("supervisor");
			aux.add("private");//visibility
			aux.add("external");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("supervisor3");
			aux.add("supervisor");
			aux.add("public");//visibility
			aux.add("internal");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("supervisor4");
			aux.add("supervisor");
			aux.add("private");//visibility
			aux.add("internal");//Accessibility
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//
			
			aux.clear();
			aux.add("creador");
			aux.add("creator");
			aux.add("public");//visibility
			aux.add("external");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("creador2");
			aux.add("creator");
			aux.add("private");//visibility
			aux.add("external");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("creador3");
			aux.add("creator");
			aux.add("public");//visibility
			aux.add("internal");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("creador4");
			aux.add("creator");
			aux.add("private");//visibility
			aux.add("internal");//Accessibility
			
			assertTrue(result.contains(aux));
			
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
	
	@Test(timeout = 5 * 60 * 1000)
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




			ArrayList<ArrayList<String>> result = omsProxy.informUnitRoles("plana");

			assertEquals("El resultado debe ser 2", 2, result.size());
			
			ArrayList<String> aux = new ArrayList<String>();
			aux.add("miembro");
			aux.add("member");
			aux.add("public");//visibility
			aux.add("external");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("miembro4");
			aux.add("member");
			aux.add("public");//visibility
			aux.add("internal");//Accessibility
			
			assertTrue(result.contains(aux));
			
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
	
	@Test(timeout = 5 * 60 * 1000)
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




			ArrayList<ArrayList<String>> result = omsProxy.informUnitRoles("equipo");

			assertEquals("El resultado debe ser 2", 2, result.size());
			
			ArrayList<String> aux = new ArrayList<String>();
			aux.add("miembro");
			aux.add("member");
			aux.add("public");//visibility
			aux.add("external");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("miembro4");
			aux.add("member");
			aux.add("public");//visibility
			aux.add("internal");//Accessibility
			
			assertTrue(result.contains(aux));

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
	
	@Test(timeout = 5 * 60 * 1000)
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




			ArrayList<ArrayList<String>> result = omsProxy.informUnitRoles(unit);

			assertEquals("El resultado debe ser 6", 6, result.size());
			
			ArrayList<String> aux = new ArrayList<String>();
			aux.add("subordinado");
			aux.add("subordinate");
			aux.add("public");//visibility
			aux.add("external");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("subordinado3");
			aux.add("subordinate");
			aux.add("public");//visibility
			aux.add("internal");//Accessibility
			
			assertTrue(result.contains(aux));
			
			//---------------------------------------------------------------------//

			aux.clear();
			aux.add("supervisor");
			aux.add("supervisor");
			aux.add("public");//visibility
			aux.add("external");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("supervisor3");
			aux.add("supervisor");
			aux.add("public");//visibility
			aux.add("internal");//Accessibility
			
			assertTrue(result.contains(aux));
			
			//---------------------------------------------------------------------//
			
			aux.clear();
			aux.add("creador2");
			aux.add("creator");
			aux.add("public");//visibility
			aux.add("external");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("creador4");
			aux.add("creator");
			aux.add("public");//visibility
			aux.add("internal");//Accessibility
			
			assertTrue(result.contains(aux));
			
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
	
	@Test(timeout = 5 * 60 * 1000)
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




			ArrayList<ArrayList<String>> result = omsProxy.informUnitRoles(unit);

			assertEquals("El resultado debe ser 6", 6, result.size());
			
			ArrayList<String> aux = new ArrayList<String>();
			aux.add("subordinado");
			aux.add("subordinate");
			aux.add("public");//visibility
			aux.add("external");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("subordinado3");
			aux.add("subordinate");
			aux.add("public");//visibility
			aux.add("internal");//Accessibility
			
			assertTrue(result.contains(aux));
			
			//---------------------------------------------------------------------//

			aux.clear();
			aux.add("supervisor");
			aux.add("supervisor");
			aux.add("public");//visibility
			aux.add("external");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("supervisor3");
			aux.add("supervisor");
			aux.add("public");//visibility
			aux.add("internal");//Accessibility
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//
			
			aux.clear();
			aux.add("creador2");
			aux.add("creator");
			aux.add("public");//visibility
			aux.add("external");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("creador4");
			aux.add("creator");
			aux.add("public");//visibility
			aux.add("internal");//Accessibility
			
			assertTrue(result.contains(aux));
			
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
	
	@Test(timeout = 5 * 60 * 1000)
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




			ArrayList<ArrayList<String>> result = omsProxy.informUnitRoles(unit);

			assertEquals("El resultado debe ser 6", 6, result.size());
			
			ArrayList<String> aux = new ArrayList<String>();
			aux.add("subordinado");
			aux.add("subordinate");
			aux.add("public");//visibility
			aux.add("external");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("subordinado3");
			aux.add("subordinate");
			aux.add("public");//visibility
			aux.add("internal");//Accessibility
			
			assertTrue(result.contains(aux));
			
			//---------------------------------------------------------------------//

			aux.clear();
			aux.add("supervisor");
			aux.add("supervisor");
			aux.add("public");//visibility
			aux.add("external");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("supervisor3");
			aux.add("supervisor");
			aux.add("public");//visibility
			aux.add("internal");//Accessibility
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//
			
			aux.clear();
			aux.add("creador2");
			aux.add("creator");
			aux.add("public");//visibility
			aux.add("external");//Accessibility
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("creador4");
			aux.add("creator");
			aux.add("public");//visibility
			aux.add("internal");//Accessibility
			
			assertTrue(result.contains(aux));
			
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
