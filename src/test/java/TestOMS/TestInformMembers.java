package TestOMS;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import junit.framework.TestCase;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.SF;
import es.upv.dsic.gti_ia.organization.exception.AgentNotInUnitException;
import es.upv.dsic.gti_ia.organization.exception.THOMASException;


public class TestInformMembers extends TestCase {

	OMSProxy omsProxy = null;
	DatabaseAccess dbA = null;


	Agent agent = null;
	Agent agent2 = null;

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

	public void testInformAgentRole1a() {
		
		try {

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<ArrayList<String>> result = omsProxy.informMembers("jerarquia", "", "");

			assertEquals("The result should be 2", 2, result.size());

			ArrayList<String> aux = new ArrayList<String>();
			aux.add("pruebas2");
			aux.add("subordinado");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("supervisor");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("equipo", "", "");

			assertEquals("The result should be 2", 2, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("manager");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("plana", "", "");

			assertEquals("The result should be 2", 2, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("manager");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//
		} catch(THOMASException e) {

			fail(e.getMessage());

		} catch(Exception e) {
			
			fail(e.getMessage());
		
		}
	}

	public void testInformAgentRole1b() {
		
		try {

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<ArrayList<String>> result = omsProxy.informMembers("jerarquia", "supervisor", "");

			assertEquals("The result should be 1", 1, result.size());

			ArrayList<String> aux = new ArrayList<String>();
			aux.add("pruebas2");
			aux.add("supervisor");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("equipo", "manager", "");

			assertEquals("The result should be 1", 1, result.size());
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("manager");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("plana", "miembro", "");

			assertEquals("The result should be 1", 1, result.size());
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			//---------------------------------------------------------------------//
		} catch(THOMASException e) {

			fail(e.getMessage());

		} catch(Exception e) {
			
			fail(e.getMessage());
		
		}

		try {
			
			omsProxy.informMembers("jerarquia", "creador", "");

		} catch(AgentNotInUnitException e) {

			assertNotNull(e);

		} catch(Exception e) {
			
			fail(e.getMessage());
		
		}
		//---------------------------------------------------------------------//
	}

	public void testInformAgentRole1c() {
		
		try {

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<ArrayList<String>> result = omsProxy.informMembers("jerarquia", "", "subordinate");

			assertEquals("The result should be 1", 1, result.size());

			ArrayList<String> aux = new ArrayList<String>();
			aux.add("pruebas2");
			aux.add("subordinado");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("equipo", "", "member");

			assertEquals("The result should be 2", 2, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("manager");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("plana", "", "member");

			assertEquals("The result should be 2", 2, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("manager");
			
			assertTrue(result.contains(aux));
			
			//---------------------------------------------------------------------//
		} catch(THOMASException e) {

			fail(e.getMessage());

		} catch(Exception e) {
			
			fail(e.getMessage());
		
		}
		
		try {
			
			omsProxy.informMembers("jerarquia", "", "creator");

		} catch(THOMASException e) {

			assertNotNull(e);

		} catch(Exception e) {
			
			fail(e.getMessage());
		
		}
		//---------------------------------------------------------------------//
	}

	public void testInformAgentRole1d() {
		
		try {

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");




			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<ArrayList<String>> result = omsProxy.informMembers("jerarquia", "subordinado", "subordinate");

			assertEquals("The result should be 1", 1, result.size());

			ArrayList<String> aux = new ArrayList<String>();
			aux.add("pruebas2");
			aux.add("subordinado");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("equipo", "manager", "member");

			assertEquals("The result should be 1", 1, result.size());
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("manager");
			
			assertTrue(result.contains(aux));
			
			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("plana", "miembro", "member");

			assertEquals("The result should be 1", 1, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//
		} catch(THOMASException e) {

			fail(e.getMessage());

		} catch(Exception e) {
			
			fail(e.getMessage());
		}

		try	{
			
			omsProxy.informMembers("jerarquia", "Creador", "creator");

		} catch(THOMASException e) {

			assertNotNull(e);

		} catch(Exception e) {
			
			fail(e.getMessage());
		}
		//---------------------------------------------------------------------//
	}


	public void testInformAgentRole2a() {
		
		try {

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");


			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<ArrayList<String>> result = omsProxy.informMembers("jerarquia", "", "");

			assertEquals("The result should be 4", 4, result.size());

			ArrayList<String> aux = new ArrayList<String>();
			aux.add("pruebas2");
			aux.add("subordinado");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas");
			aux.add("subordinado");
			
			assertTrue(result.contains(aux));

			aux.clear();
			aux.add("pruebas2");
			aux.add("creador");
			
			assertTrue(result.contains(aux));

			aux.clear();
			aux.add("pruebas2");
			aux.add("supervisor");
			
			assertTrue(result.contains(aux));


			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("equipo", "", "");

			assertEquals("The result should be 2", 2, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));

			aux.clear();
			aux.add("pruebas2");
			aux.add("manager");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("plana", "", "");

			assertEquals("The result should be 2", 2, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));

			aux.clear();
			aux.add("pruebas2");
			aux.add("manager");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//
		} catch(THOMASException e) {

			fail(e.getMessage());

		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
	}

	public void testInformAgentRole2b() {
		
		try {

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");



			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<ArrayList<String>> result = omsProxy.informMembers("jerarquia", "supervisor", "");

			assertEquals("The result should be 1", 1, result.size());

			ArrayList<String> aux = new ArrayList<String>();
			aux.add("pruebas2");
			aux.add("supervisor");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("equipo", "manager", "");

			assertEquals("The result should be 1", 1, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("manager");
			
			assertTrue(result.contains(aux));
			
			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("plana", "miembro", "");

			assertEquals("The result should be 1", 1, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			//---------------------------------------------------------------------//


			result =  omsProxy.informMembers("jerarquia", "creador", "");

			assertEquals("The result should be 1", 1, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("creador");
			
			assertTrue(result.contains(aux));

		} catch(THOMASException e) {

			fail(e.getMessage());

		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
		//---------------------------------------------------------------------//
	}

	public void testInformAgentRole2c() {
		
		try {

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");


			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<ArrayList<String>> result = omsProxy.informMembers("jerarquia", "", "subordinate");

			assertEquals("The result should be 2", 2, result.size());

			ArrayList<String> aux = new ArrayList<String>();
			aux.add("pruebas2");
			aux.add("subordinado");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas");
			aux.add("subordinado");
			
			assertTrue(result.contains(aux));
			
			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("equipo", "", "member");

			assertEquals("The result should be 2", 2, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("manager");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("plana", "", "member");

			assertEquals("The result should be 2", 2, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("manager");
			
			assertTrue(result.contains(aux));
			
			//---------------------------------------------------------------------//


			result = omsProxy.informMembers("jerarquia", "", "creator");

			assertEquals("The result should be 1", 1, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("creador");
			
			assertTrue(result.contains(aux));

		} catch(THOMASException e) {

			fail(e.getMessage());

		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
		//---------------------------------------------------------------------//
	}

	public void testInformAgentRole2d() {
		
		try {

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");


			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<ArrayList<String>> result = omsProxy.informMembers("jerarquia", "subordinado", "subordinate");

			assertEquals("The result should be 2", 2, result.size());

			ArrayList<String> aux = new ArrayList<String>();
			aux.add("pruebas2");
			aux.add("subordinado");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas");
			aux.add("subordinado");
			
			assertTrue(result.contains(aux));
			
			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("equipo", "manager", "member");

			assertEquals("The result should be 1", 1, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("manager");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("plana", "miembro", "member");

			assertEquals("The result should be 1", 1, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("jerarquia", "creador", "creator");

			assertEquals("The result should be 1", 1, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("creador");
			
			assertTrue(result.contains(aux));

		} catch(THOMASException e) {

			fail(e.getMessage());

		} catch(Exception e) {
			
			fail(e.getMessage());
			
		}
		//---------------------------------------------------------------------//
	}


	public void testInformAgentRole3a() {
		
		try {

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");


			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<ArrayList<String>> result = omsProxy.informMembers("jerarquia", "", "");

			assertEquals("The result should be 2", 2, result.size());

			ArrayList<String> aux = new ArrayList<String>();
			aux.add("pruebas2");
			aux.add("subordinado");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("supervisor");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("equipo", "", "");

			assertEquals("The result should be 4", 4, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));

			aux.clear();
			aux.add("pruebas2");
			aux.add("creador");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("manager");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("plana", "", "");

			assertEquals("The result should be 2", 2, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("manager");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//
		} catch(THOMASException e) {

			fail(e.getMessage());

		} catch(Exception e) {
			
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




			ArrayList<ArrayList<String>> result = omsProxy.informMembers("jerarquia", "supervisor", "");

			assertEquals("The result should be 1", 1, result.size());

			ArrayList<String> aux = new ArrayList<String>();
			aux.add("pruebas2");
			aux.add("supervisor");
			
			assertTrue(result.contains(aux));
			
			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("equipo", "manager", "");

			assertEquals("The result should be 1", 1, result.size());
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("manager");
			
			assertTrue(result.contains(aux));
			
			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("plana", "miembro", "");

			assertEquals("The result should be 1", 1, result.size());
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			//---------------------------------------------------------------------//
		} catch(THOMASException e) {

			fail(e.getMessage());

		} catch(Exception e) {
			
			fail(e.getMessage());
		
		}
		
		try {

			omsProxy.informMembers("jerarquia", "creador", "");
		
		} catch(THOMASException e) {

			assertNotNull(e);

		} catch(Exception e) {
			
			fail(e.getMessage());
		
		}
		//---------------------------------------------------------------------//
	}

	public void testInformAgentRole3c() {
		
		try {

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");


			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<ArrayList<String>> result = omsProxy.informMembers("jerarquia", "", "subordinate");

			assertEquals("The result should be 1", 1, result.size());

			ArrayList<String> aux = new ArrayList<String>();
			aux.add("pruebas2");
			aux.add("subordinado");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("equipo", "", "member");

			assertEquals("The result should be 3", 3, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));

			aux.clear();
			aux.add("pruebas2");
			aux.add("manager");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("plana", "", "member");

			assertEquals("The result should be 2", 2, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("manager");
			
			assertTrue(result.contains(aux));
			
			//---------------------------------------------------------------------//
		} catch(THOMASException e) {

			fail(e.getMessage());

		} catch(Exception e) {
			
			fail(e.getMessage());
		
		}

		try {
			
			omsProxy.informMembers("jerarquia", "", "creator");

		} catch(THOMASException e) {

			assertNotNull(e);

		} catch(Exception e) {
			
			fail(e.getMessage());
		
		}
		//---------------------------------------------------------------------//
	}

	public void testInformAgentRole3d() {
		
		try {

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");


			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<ArrayList<String>> result = omsProxy.informMembers("jerarquia", "subordinado", "subordinate");

			assertEquals("The result should be 1", 1, result.size());

			ArrayList<String> aux = new ArrayList<String>();
			aux.add("pruebas2");
			aux.add("subordinado");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("equipo", "manager", "member");

			assertEquals("The result should be 1", 1, result.size());
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("manager");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("plana", "miembro", "member");

			assertEquals("The result should be 1", 1, result.size());
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			//---------------------------------------------------------------------//
		} catch(THOMASException e) {

			fail(e.getMessage());

		} catch(Exception e) {
			
			fail(e.getMessage());
		
		}

		try {
			
			omsProxy.informMembers("jerarquia", "creador", "creator");

		} catch(THOMASException e) {

			assertNotNull(e);

		} catch(Exception e) {
			
			fail(e.getMessage());
		
		}
		//---------------------------------------------------------------------//
	}


	public void testInformAgentRole4a() {
		
		try {

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");


			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<ArrayList<String>> result = omsProxy.informMembers("jerarquia", "", "");

			assertEquals("The result should be 2", 2, result.size());

			ArrayList<String> aux = new ArrayList<String>();
			aux.add("pruebas2");
			aux.add("subordinado");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("supervisor");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("equipo", "", "");

			assertEquals("The result should be 2", 2, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("manager");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("plana", "", "");

			assertEquals("The result should be 4", 4, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
		
			aux.clear();
			aux.add("pruebas2");
			aux.add("creador");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("manager");
			
			assertTrue(result.contains(aux));

		} catch(THOMASException e) {

			fail(e.getMessage());

		} catch(Exception e) {
			
			fail(e.getMessage());
		
		}
	}

	public void testInformAgentRole4b() {
		
		try {

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");



			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<ArrayList<String>> result = omsProxy.informMembers("jerarquia", "supervisor", "");

			assertEquals("The result should be 1", 1, result.size());

			ArrayList<String> aux = new ArrayList<String>();
			aux.add("pruebas2");
			aux.add("supervisor");
			
			assertTrue(result.contains(aux));
			
			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("equipo", "manager", "");

			assertEquals("The result should be 1", 1, result.size());
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("manager");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("plana", "miembro", "");

			assertEquals("The result should be 2", 2, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//

		} catch(THOMASException e) {

			fail(e.getMessage());

		} catch(Exception e) {
			
			fail(e.getMessage());
		
		}
		
		try {

			omsProxy.informMembers("jerarquia", "creador", "");
		
		} catch(THOMASException e) {

			assertNotNull(e);

		} catch(Exception e) {
			
			fail(e.getMessage());
		
		}
		//---------------------------------------------------------------------//
	}

	public void testInformAgentRole4c() {
		
		try {

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");


			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<ArrayList<String>> result = omsProxy.informMembers("jerarquia", "", "subordinate");

			assertEquals("The result should be 1", 1, result.size());

			ArrayList<String> aux = new ArrayList<String>();
			aux.add("pruebas2");
			aux.add("subordinado");
			
			assertTrue(result.contains(aux));
			
			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("equipo", "", "member");

			assertEquals("The result should be 2", 2, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("manager");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("plana", "", "member");

			assertEquals("The result should be 3", 3, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("manager");
			
			assertTrue(result.contains(aux));
			
			//---------------------------------------------------------------------//

		} catch(THOMASException e) {

			fail(e.getMessage());

		} catch(Exception e) {
			
			fail(e.getMessage());
		
		}

		try {
			
			omsProxy.informMembers("jerarquia", "", "creator");

		} catch(THOMASException e) {

			assertNotNull(e);

		} catch(Exception e) {
			
			fail(e.getMessage());
		
		}
		//---------------------------------------------------------------------//
	}

	public void testInformAgentRole4d() {
		
		try {

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");


			/**-----------
			 * --1a							
			 * -----------
			 */




			ArrayList<ArrayList<String>> result = omsProxy.informMembers("jerarquia", "subordinado", "subordinate");

			assertEquals("The result should be 1", 1, result.size());

			ArrayList<String> aux = new ArrayList<String>();
			aux.add("pruebas2");
			aux.add("subordinado");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("equipo", "manager", "member");

			assertEquals("The result should be 1", 1, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("manager");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("plana", "miembro", "member");

			assertEquals("The result should be 2", 2, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//
		} catch(THOMASException e) {

			fail(e.getMessage());

		} catch(Exception e) {
			
			fail(e.getMessage());
		
		}

		try {
			
			omsProxy.informMembers("jerarquia", "creador", "creator");

		} catch(THOMASException e) {

			assertNotNull(e);

		} catch(Exception e) {
			
			fail(e.getMessage());
		
		}
		//---------------------------------------------------------------------//
	}


	public void testInformAgentRole5a() {
		
		try {

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




			ArrayList<ArrayList<String>> result = omsProxy.informMembers("jerarquia", "", "");

			assertEquals("The result should be 4", 4, result.size());

			ArrayList<String> aux = new ArrayList<String>();
			aux.add("pruebas2");
			aux.add("subordinado");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas");
			aux.add("subordinado");
			
			assertTrue(result.contains(aux));

			aux.clear();
			aux.add("pruebas2");
			aux.add("creador");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("supervisor");
			
			assertTrue(result.contains(aux));
			
			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("equipo", "", "");

			assertEquals("The result should be 4", 4, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));

			aux.clear();
			aux.add("pruebas2");
			aux.add("creador");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("manager");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("plana", "", "");

			assertEquals("The result should be 2", 2, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("manager");
			
			assertTrue(result.contains(aux));

		} catch(THOMASException e) {

			fail(e.getMessage());

		} catch(Exception e) {
			
			fail(e.getMessage());
		
		}
	}

	public void testInformAgentRole5b() {
		
		try {

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




			ArrayList<ArrayList<String>> result = omsProxy.informMembers("jerarquia", "supervisor", "");

			assertEquals("The result should be 1", 1, result.size());

			ArrayList<String> aux = new ArrayList<String>();
			aux.add("pruebas2");
			aux.add("supervisor");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("equipo", "manager", "");

			assertEquals("The result should be 1", 1, result.size());
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("manager");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("plana", "miembro", "");

			assertEquals("The result should be 1", 1, result.size());
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			//---------------------------------------------------------------------//

		} catch(THOMASException e) {

			fail(e.getMessage());

		} catch(Exception e) {
			
			fail(e.getMessage());
		
		}
		
		try {

			omsProxy.informMembers("jerarquia", "creador", "");
		
		} catch(THOMASException e) {

			assertNotNull(e);

		} catch(Exception e) {
			
			fail(e.getMessage());
		
		}
		//---------------------------------------------------------------------//
	}

	public void testInformAgentRole5c() {
		
		try {

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




			ArrayList<ArrayList<String>> result = omsProxy.informMembers("jerarquia", "", "subordinate");

			assertEquals("The result should be 2", 2, result.size());

			ArrayList<String> aux = new ArrayList<String>();
			aux.add("pruebas2");
			aux.add("subordinado");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas");
			aux.add("subordinado");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("equipo", "", "member");

			assertEquals("The result should be 3", 3, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));

			aux.clear();
			aux.add("pruebas2");
			aux.add("manager");
			
			assertTrue(result.contains(aux));
			
			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("plana", "", "member");

			assertEquals("The result should be 2", 2, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("manager");
			
			assertTrue(result.contains(aux));
			
			//---------------------------------------------------------------------//


			result = omsProxy.informMembers("jerarquia", "", "creator");

			assertEquals("The result should be 1", 1, result.size());
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("creador");
			
			assertTrue(result.contains(aux));

		} catch(THOMASException e) {

			fail(e.getMessage());

		} catch(Exception e) {
			
			fail(e.getMessage());
		
		}
		//---------------------------------------------------------------------//
	}

	public void testInformAgentRole5d() {
		
		try {

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




			ArrayList<ArrayList<String>> result = omsProxy.informMembers("jerarquia", "subordinado", "subordinate");

			assertEquals("The result should be 2", 2, result.size());

			ArrayList<String> aux = new ArrayList<String>();
			aux.add("pruebas2");
			aux.add("subordinado");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas");
			aux.add("subordinado");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("equipo", "manager", "member");

			assertEquals("The result should be 1", 1, result.size());
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("manager");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("plana", "miembro", "member");

			assertEquals("The result should be 1", 1, result.size());
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("jerarquia", "creador", "creator");

			assertEquals("The result should be 1", 1, result.size());
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("creador");
			
			assertTrue(result.contains(aux));
			
		} catch(THOMASException e) {

			fail(e.getMessage());

		} catch(Exception e) {
			
			fail(e.getMessage());
		
		}
		//---------------------------------------------------------------------//
	}

	public void testInformAgentRole6a() {
		
		try {

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




			ArrayList<ArrayList<String>> result = omsProxy.informMembers("jerarquia", "", "");

			assertEquals("The result should be 2", 2, result.size());

			ArrayList<String> aux = new ArrayList<String>();
			aux.add("pruebas2");
			aux.add("subordinado");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("supervisor");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("equipo", "", "");

			assertEquals("The result should be 4", 4, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));

			aux.clear();
			aux.add("pruebas2");
			aux.add("creador");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("manager");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("plana", "", "");

			assertEquals("The result should be 4", 4, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));

			aux.clear();
			aux.add("pruebas2");
			aux.add("creador");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("manager");
			
			assertTrue(result.contains(aux));

		} catch(THOMASException e){

			fail(e.getMessage());

		} catch(Exception e) {
			
			fail(e.getMessage());
		
		}
	}

	public void testInformAgentRole6b() {
		
		try {

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




			ArrayList<ArrayList<String>> result = omsProxy.informMembers("jerarquia", "supervisor", "");

			assertEquals("The result should be 1", 1, result.size());

			ArrayList<String> aux = new ArrayList<String>();
			aux.add("pruebas2");
			aux.add("supervisor");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("equipo", "manager", "");

			assertEquals("The result should be 1", 1, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("manager");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("plana", "miembro", "");

			assertEquals("The result should be 2", 2, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			//---------------------------------------------------------------------//
		} catch(THOMASException e) {

			fail(e.getMessage());

		} catch(Exception e) {
			
			fail(e.getMessage());
		
		}
		
		try {
			
			omsProxy.informMembers("jerarquia", "", "creator");
			
		} catch(THOMASException e) {

			assertNotNull(e);

		} catch(Exception e) {
			
			fail(e.getMessage());
		
		}
		//---------------------------------------------------------------------//
	}

	public void testInformAgentRole6c() {
		
		try {

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




			ArrayList<ArrayList<String>> result = omsProxy.informMembers("jerarquia", "", "subordinate");

			assertEquals("The result should be 1", 1, result.size());

			ArrayList<String> aux = new ArrayList<String>();
			aux.add("pruebas2");
			aux.add("subordinado");
			
			assertTrue(result.contains(aux));
			
			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("equipo", "", "member");

			assertEquals("The result should be 3", 3, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("manager");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("plana", "", "member");

			assertEquals("The result should be 3", 3, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));

			aux.clear();
			aux.add("pruebas2");
			aux.add("manager");
			
			assertTrue(result.contains(aux));
			
			//---------------------------------------------------------------------//
		} catch(THOMASException e) {

			fail(e.getMessage());

		} catch(Exception e) {
			
			fail(e.getMessage());
		
		}
		
		try {
			
			omsProxy.informMembers("jerarquia", "", "creator");
			
		} catch(THOMASException e) {

			assertNotNull(e);

		} catch(Exception e) {
			
			fail(e.getMessage());
		
		}
		//---------------------------------------------------------------------//
	}

	public void testInformAgentRole6d() {
		
		try {

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




			ArrayList<ArrayList<String>> result = omsProxy.informMembers("jerarquia", "subordinado", "subordinate");

			assertEquals("The result should be 1", 1, result.size());

			ArrayList<String> aux = new ArrayList<String>();
			aux.add("pruebas2");
			aux.add("subordinado");
			
			assertTrue(result.contains(aux));
			
			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("equipo", "manager", "member");

			assertEquals("The result should be 1", 1, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("manager");
			
			assertTrue(result.contains(aux));
			
			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("plana", "miembro", "member");

			assertEquals("The result should be 2", 2, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//
		} catch(THOMASException e) {

			fail(e.getMessage());

		} catch(Exception e) {
			
			fail(e.getMessage());
		
		}
		
		try {
			
			omsProxy.informMembers("jerarquia", "", "creator");
			
		} catch(THOMASException e) {

			assertNotNull(e);

		} catch(Exception e) {
			
			fail(e.getMessage());
		
		}
		//---------------------------------------------------------------------//
	}
	
	
	public void testInformAgentRole7a() {
		
		try {

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




			ArrayList<ArrayList<String>> result = omsProxy.informMembers("jerarquia", "", "");

			assertEquals("The result should be 4", 4, result.size());

			ArrayList<String> aux = new ArrayList<String>();
			aux.add("pruebas2");
			aux.add("subordinado");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas");
			aux.add("subordinado");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("creador");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("supervisor");
			
			assertTrue(result.contains(aux));
			
			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("equipo", "", "");

			assertEquals("The result should be 4", 4, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));

			aux.clear();
			aux.add("pruebas2");
			aux.add("creador");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("manager");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("plana", "", "");

			assertEquals("The result should be 4", 4, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("creador");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("manager");
			
			assertTrue(result.contains(aux));

		} catch(THOMASException e) {

			fail(e.getMessage());

		} catch(Exception e) {
			
			fail(e.getMessage());
		
		}
	}

	public void testInformAgentRole7b() {
		
		try {

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




			ArrayList<ArrayList<String>> result = omsProxy.informMembers("jerarquia", "supervisor", "");

			assertEquals("The result should be 1", 1, result.size());

			ArrayList<String> aux = new ArrayList<String>();
			aux.add("pruebas2");
			aux.add("supervisor");
			
			assertTrue(result.contains(aux));
			
			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("equipo", "manager", "");

			assertEquals("The result should be 1", 1, result.size());
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("manager");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("plana", "miembro", "");

			assertEquals("The result should be 2", 2, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			//---------------------------------------------------------------------//

			
			result = omsProxy.informMembers("jerarquia", "", "creator");
			
			assertEquals("The result should be 1", 1, result.size());
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("creador");
			
			assertTrue(result.contains(aux));
			
		} catch(THOMASException e) {

			fail(e.getMessage());

		} catch(Exception e) {
			
			fail(e.getMessage());
		
		}
		//---------------------------------------------------------------------//
	}

	public void testInformAgentRole7c() {
		
		try {

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




			ArrayList<ArrayList<String>> result = omsProxy.informMembers("jerarquia", "", "subordinate");

			assertEquals("The result should be 2", 2, result.size());

			ArrayList<String> aux = new ArrayList<String>();
			aux.add("pruebas2");
			aux.add("subordinado");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas");
			aux.add("subordinado");
			
			assertTrue(result.contains(aux));

			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("equipo", "", "member");

			assertEquals("The result should be 3", 3, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("manager");
			
			assertTrue(result.contains(aux));
			
			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("plana", "", "member");

			assertEquals("The result should be 3", 3, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("manager");
			
			assertTrue(result.contains(aux));
			
			//---------------------------------------------------------------------//


			result = omsProxy.informMembers("jerarquia", "", "creator");
			
			assertEquals("The result should be 1", 1, result.size());
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("creador");
			
			assertTrue(result.contains(aux));

		} catch(THOMASException e) {

			fail(e.getMessage());

		} catch(Exception e) {
			
			fail(e.getMessage());
		
		}
		//---------------------------------------------------------------------//
	}

	public void testInformAgentRole7d() {
		
		try {

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




			ArrayList<ArrayList<String>> result = omsProxy.informMembers("jerarquia", "subordinado", "subordinate");

			assertEquals("The result should be 2", 2, result.size());

			ArrayList<String> aux = new ArrayList<String>();
			aux.add("pruebas2");
			aux.add("subordinado");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas");
			aux.add("subordinado");
			
			assertTrue(result.contains(aux));
			
			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("equipo", "manager", "member");

			assertEquals("The result should be 1", 1, result.size());
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("manager");
			
			assertTrue(result.contains(aux));
			
			//---------------------------------------------------------------------//

			result = omsProxy.informMembers("plana", "miembro", "member");

			assertEquals("The result should be 2", 2, result.size());

			aux.clear();
			aux.add("pruebas2");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			aux.clear();
			aux.add("pruebas");
			aux.add("miembro");
			
			assertTrue(result.contains(aux));
			
			//---------------------------------------------------------------------//


			result = omsProxy.informMembers("jerarquia", "", "creator");
			
			assertEquals("The result should be 1", 1, result.size());
			
			aux.clear();
			aux.add("pruebas2");
			aux.add("creador");
			
			assertTrue(result.contains(aux));
			
		} catch(THOMASException e) {

			fail(e.getMessage());

		} catch(Exception e) {
			
			fail(e.getMessage());
		
		}
		//---------------------------------------------------------------------//
	}
}
