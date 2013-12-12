package TestOMS;

import junit.framework.TestCase;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.SF;
import es.upv.dsic.gti_ia.organization.exception.THOMASException;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class TestAcquireRole extends TestCase {

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
		dbA.executeSQL("DELETE FROM normList");
		dbA.executeSQL("DELETE FROM roleList WHERE idroleList != 1");
		dbA.executeSQL("DELETE FROM unitHierarchy WHERE idChildUnit != 1");
		dbA.executeSQL("DELETE FROM unitList WHERE idunitList != 1");

		//--------------------------------------------//
	}

	public void testAcquireRoleFlat()
	{
		try
		{
			//-------------------------------------------------Inicialización---------------------------------------------------------------------------------
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

			//------------------------------------------------------------------------------------------------------------------------------------------------

			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");


			/**----------------------------------------------------------------------------------
			 * --1							acquire role miembro2
			 * ----------------------------------------------------------------------------------
			 */


			String roleName= "miembro2";


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro2',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			String result = omsProxy.acquireRole(roleName, "plana");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro2',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "plana");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro2',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "plana");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro2',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "plana");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//

			/**----------------------------------------------------------------------------------
			 * --2							acquire role creator2
			 * ----------------------------------------------------------------------------------
			 */


			roleName= "creador2";

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "plana");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "plana");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "plana");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "plana");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana')");
			//---------------------------------------------------------------------//


			/**----------------------------------------------------------------------------------
			 * --3							acquire role miembro
			 * ----------------------------------------------------------------------------------
			 */


			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = 'miembro'");

			roleName= "miembro";

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "plana");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "plana");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "plana");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "plana");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//


			/**----------------------------------------------------------------------------------
			 * --4							acquire role creador3
			 * ----------------------------------------------------------------------------------
			 */

			roleName= "creador3";


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "plana");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "plana");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "plana");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "plana");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);


		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	public void testAcquireRoleTeam()
	{

		try
		{

			//-------------------------------------------------Inicialización---------------------------------------------------------------------------------
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('equipo',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
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


			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");


			/**----------------------------------------------------------------------------------
			 * --5							acquire role miembro2
			 * ----------------------------------------------------------------------------------
			 */


			String roleName= "miembro2";


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			String result = omsProxy.acquireRole(roleName, "equipo");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "equipo");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "equipo");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "equipo");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//


			/**----------------------------------------------------------------------------------
			 * --6							acquire role creador2
			 * ----------------------------------------------------------------------------------
			 */


			roleName= "creador2";


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "equipo");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "equipo");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "equipo");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "equipo");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//


			/**----------------------------------------------------------------------------------
			 * --7							acquire role miembro
			 * ----------------------------------------------------------------------------------
			 */


			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = 'miembro'");

			roleName= "miembro";

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "equipo");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "equipo");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "equipo");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "equipo");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//


			/**----------------------------------------------------------------------------------
			 * --8							acquire role creador3
			 * ----------------------------------------------------------------------------------
			 */

			roleName= "creador3";


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "equipo");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "equipo");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "equipo");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "equipo");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);



		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}



	public void testAcquireRoleJerarquia()
	{


		try
		{

			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('jerarquia',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
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


		
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");


			/**----------------------------------------------------------------------------------
			 * --9							acquire role subordinado2
			 * ----------------------------------------------------------------------------------
			 */


			String roleName= "subordinado2";


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			String result = omsProxy.acquireRole(roleName, "jerarquia");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "jerarquia");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "jerarquia");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "jerarquia");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//----------------------------------------------------------------------//



			/**----------------------------------------------------------------------------------
			 * --10							acquire role subordinado
			 * ----------------------------------------------------------------------------------
			 */


			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = 'subordinado'");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");






			roleName= "subordinado";


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "jerarquia");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "jerarquia");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "jerarquia");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "jerarquia");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//----------------------------------------------------------------------//



			/**----------------------------------------------------------------------------------
			 * --11							acquire role subordinado
			 * ----------------------------------------------------------------------------------
			 */


			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'supervisor')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = 'supervisor'");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");






			roleName= "subordinado";


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "jerarquia");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "jerarquia");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "jerarquia");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "jerarquia");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//----------------------------------------------------------------------//



			/**----------------------------------------------------------------------------------
			 * --12							acquire role supervisor2
			 * ----------------------------------------------------------------------------------
			 */


			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador2')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = 'creador2'");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");






			roleName= "supervisor2";


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "jerarquia");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "jerarquia");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "jerarquia");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "jerarquia");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//----------------------------------------------------------------------//




			/**----------------------------------------------------------------------------------
			 * --13							acquire role supervisor2
			 * ----------------------------------------------------------------------------------
			 */


			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'supervisor')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = 'supervisor'");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");






			roleName= "supervisor";


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "jerarquia");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "jerarquia");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "jerarquia");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "jerarquia");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//----------------------------------------------------------------------//





			/**----------------------------------------------------------------------------------
			 * --14							acquire role creador2
			 * ----------------------------------------------------------------------------------
			 */


			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'creador2')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = 'creador2'");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");






			roleName= "creador2";


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "jerarquia");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "jerarquia");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "jerarquia");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "jerarquia");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//----------------------------------------------------------------------//


			/**----------------------------------------------------------------------------------
			 * --15							acquire role creador3
			 * ----------------------------------------------------------------------------------
			 */


			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'supervisor')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = 'supervisor'");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");






			roleName= "creador3";


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "jerarquia");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "jerarquia");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "jerarquia");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "jerarquia");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//----------------------------------------------------------------------//

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}


	public void testAcquireRolePlana2()
	{
		try
		{

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


			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('Plana2',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'Plana'),(SELECT idunitList FROM unitList WHERE unitName = 'Plana2'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'plana2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			/**----------------------------------------------------------------------------------
			 * --16							acquire role miembro2
			 * ----------------------------------------------------------------------------------
			 */




			


			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");


			String roleName= "miembro2";


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro2',(SELECT idunitList FROM unitList WHERE unitName = 'plana2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			String result = omsProxy.acquireRole(roleName, "plana2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro2',(SELECT idunitList FROM unitList WHERE unitName = 'plana2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "plana2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro2',(SELECT idunitList FROM unitList WHERE unitName = 'plana2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "plana2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro2',(SELECT idunitList FROM unitList WHERE unitName = 'plana2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "plana2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//----------------------------------------------------------------------//



			/**----------------------------------------------------------------------------------
			 * --17							acquire role creador2
			 * ----------------------------------------------------------------------------------
			 */









			roleName= "creador2";


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'plana2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "plana2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'plana2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "plana2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'plana2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "plana2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'plana2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "plana2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//----------------------------------------------------------------------//


			/**----------------------------------------------------------------------------------
			 * --18							acquire role miembro
			 * ----------------------------------------------------------------------------------
			 */




			dbA.executeSQL("DELETE FROM agentPlayList WHERE idagentList = (SELECT idagentList FROM agentList WHERE agentName = 'pruebas')");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");






			roleName= "miembro";


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'plana2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "plana2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'plana2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'plana2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'plana2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "plana2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'plana2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'plana2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'plana2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "plana2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'plana2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'plana2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'plana2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "plana2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'plana2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'plana2')");
			//----------------------------------------------------------------------//



			/**----------------------------------------------------------------------------------
			 * --19							acquire role creador3
			 * ----------------------------------------------------------------------------------
			 */












			roleName= "creador3";


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'plana2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "plana2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'plana2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'plana2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'plana2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "plana2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'plana2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'plana2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'plana2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "plana2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'plana2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'plana2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'plana2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "plana2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'plana2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'plana2')");
			//----------------------------------------------------------------------//

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}


	}

	public void testAcquireRoleEquipo2()
	{
		try
		{

			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('equipo',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
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


			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('equipo2',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'equipo'),(SELECT idunitList FROM unitList WHERE unitName = 'Equipo2'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");




			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");



			/**----------------------------------------------------------------------------------
			 * --20						acquire role miembro2
			 * ----------------------------------------------------------------------------------
			 */



			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");







			String roleName= "miembro2";


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			String result = omsProxy.acquireRole(roleName, "equipo2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "equipo2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "equipo2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "equipo2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2')");
			//----------------------------------------------------------------------//




			/**----------------------------------------------------------------------------------
			 * --21					acquire role creador2
			 * ----------------------------------------------------------------------------------
			 */









			roleName= "creador2";


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "equipo2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "equipo2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "equipo2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "equipo2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2')");
			//----------------------------------------------------------------------//


			/**----------------------------------------------------------------------------------
			 * --22				acquire role miembro
			 * ----------------------------------------------------------------------------------
			 */




			dbA.executeSQL("DELETE FROM agentPlayList WHERE idagentList = (SELECT idagentList FROM agentList WHERE agentName = 'pruebas')");


			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");




			dbA.executeSQL("DELETE FROM roleList WHERE roleName = 'miembro'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2')");




			roleName= "miembro";


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "equipo2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "equipo2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "equipo2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "equipo2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2')");
			//----------------------------------------------------------------------//


			/**----------------------------------------------------------------------------------
			 * --23				acquire role creador3
			 * ----------------------------------------------------------------------------------
			 */

			roleName= "creador3";


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "equipo2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "equipo2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "equipo2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "equipo2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2')");
			//----------------------------------------------------------------------//

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testAcquireRoleJerarquia2()
	{
		try
		{
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('jerarquia',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
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


			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('jerarquia2',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),(SELECT idunitList FROM unitList WHERE unitName = 'Jerarquia2'))");


			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");



			/**----------------------------------------------------------------------------------
			 * --24				acquire role subordinado2
			 * ----------------------------------------------------------------------------------
			 */



		


			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");



			String roleName= "subordinado2";


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			String result = omsProxy.acquireRole(roleName, "jerarquia2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "jerarquia2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "jerarquia2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "jerarquia2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");
			//----------------------------------------------------------------------//

			/**----------------------------------------------------------------------------------
			 * --25			acquire role subordinado2
			 * ----------------------------------------------------------------------------------
			 */



			dbA.executeSQL("DELETE FROM agentPlayList WHERE idagentList = (SELECT idagentList FROM agentList WHERE agentName = 'pruebas')");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

			//dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'subordinado' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))");


			roleName= "subordinado";


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "jerarquia2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "jerarquia2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "jerarquia2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "jerarquia2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");
			//----------------------------------------------------------------------//


			/**----------------------------------------------------------------------------------
			 * --26			acquire role creador2
			 * ----------------------------------------------------------------------------------
			 */



			dbA.executeSQL("DELETE FROM agentPlayList WHERE idagentList = (SELECT idagentList FROM agentList WHERE agentName = 'pruebas')");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

			//dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'subordinado' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))");


			roleName= "subordinado";


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "jerarquia2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "jerarquia2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "jerarquia2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "jerarquia2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");
			//----------------------------------------------------------------------//



			/**----------------------------------------------------------------------------------
			 * --27			acquire role supervisor2
			 * ----------------------------------------------------------------------------------
			 */



			dbA.executeSQL("DELETE FROM agentPlayList WHERE idagentList = (SELECT idagentList FROM agentList WHERE agentName = 'pruebas')");



			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

			//dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'subordinado' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))");


			roleName= "supervisor2";


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "jerarquia2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "jerarquia2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "jerarquia2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "jerarquia2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");
			//----------------------------------------------------------------------//





			/**----------------------------------------------------------------------------------
			 * --28			acquire role supervisor
			 * ----------------------------------------------------------------------------------
			 */




			dbA.executeSQL("DELETE FROM agentPlayList WHERE idagentList = (SELECT idagentList FROM agentList WHERE agentName = 'pruebas')");

			dbA.executeSQL("DELETE FROM roleList WHERE roleName != 'creador'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName != 'creador'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'supervisor' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))");


			roleName= "supervisor";


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "jerarquia2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "jerarquia2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "jerarquia2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "jerarquia2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");
			//----------------------------------------------------------------------//


			/**----------------------------------------------------------------------------------
			 * --29		acquire role supervisor
			 * ----------------------------------------------------------------------------------
			 */




			dbA.executeSQL("DELETE FROM agentPlayList WHERE idagentList = (SELECT idagentList FROM agentList WHERE agentName = 'pruebas')");

			dbA.executeSQL("DELETE FROM roleList WHERE roleName != 'creador'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName != 'creador'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");




			roleName= "creador2";


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "jerarquia2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "jerarquia2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "jerarquia2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "jerarquia2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");
			//----------------------------------------------------------------------//



			/**----------------------------------------------------------------------------------
			 * --30	acquire role creador3
			 * ----------------------------------------------------------------------------------
			 */




			dbA.executeSQL("DELETE FROM agentPlayList WHERE idagentList = (SELECT idagentList FROM agentList WHERE agentName = 'pruebas')");

			dbA.executeSQL("DELETE FROM roleList WHERE roleName != 'creador'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName != 'creador'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");




			roleName= "creador3";


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "jerarquia2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "jerarquia2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "jerarquia2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "jerarquia2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");
			//----------------------------------------------------------------------//





		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}


	//Test a partir de la 31
	public void testAcquireRolePlana()
	{

		try
		{

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


			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('Plana2',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'Plana2'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'plana2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			/**----------------------------------------------------------------------------------
			 * --31						acquire role miembro2
			 * ----------------------------------------------------------------------------------
			 */




		

			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");




			String roleName= "miembro2";


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro2',(SELECT idunitList FROM unitList WHERE unitName = 'plana2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			String result = omsProxy.acquireRole(roleName, "plana2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro2',(SELECT idunitList FROM unitList WHERE unitName = 'plana2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "plana2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//



			/**----------------------------------------------------------------------------------
			 * --32							acquire role creador2
			 * ----------------------------------------------------------------------------------
			 */









			roleName= "creador2";


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'plana2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "plana2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'plana2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "plana2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'");
			//---------------------------------------------------------------------//


			/**----------------------------------------------------------------------------------
			 * --33						acquire role miembro
			 * ----------------------------------------------------------------------------------
			 */




			dbA.executeSQL("DELETE FROM agentPlayList WHERE idagentList = (SELECT idagentList FROM agentList WHERE agentName = 'pruebas')");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'plana'))))");






			roleName= "miembro";


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'plana2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "plana2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'plana2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'plana2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'plana2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "plana2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'plana2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'plana2')");
			//---------------------------------------------------------------------//



			/**----------------------------------------------------------------------------------
			 * --33							acquire role creador3
			 * ----------------------------------------------------------------------------------
			 */












			roleName= "creador3";


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'plana2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "plana2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'plana2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'plana2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'plana2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "plana2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'plana2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'plana2')");
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


	public void testAcquireRoleEquipo()
	{
		try
		{


			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('equipo',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
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


			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('equipo2',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'Equipo2'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");




			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");



			/**----------------------------------------------------------------------------------
			 * --35					acquire role miembro2
			 * ----------------------------------------------------------------------------------
			 */





			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");







			String roleName= "miembro2";


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			String result = omsProxy.acquireRole(roleName, "equipo2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "equipo2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2')");
			//---------------------------------------------------------------------//



			/**----------------------------------------------------------------------------------
			 * --36					acquire role creador2
			 * ----------------------------------------------------------------------------------
			 */


			roleName= "creador2";


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "equipo2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "equipo2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2')");
			//---------------------------------------------------------------------//




			/**----------------------------------------------------------------------------------
			 * --37				acquire role miembro
			 * ----------------------------------------------------------------------------------
			 */




			dbA.executeSQL("DELETE FROM agentPlayList WHERE idagentList = (SELECT idagentList FROM agentList WHERE agentName = 'pruebas')");


			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");




			dbA.executeSQL("DELETE FROM roleList WHERE roleName = 'miembro'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2')");




			roleName= "miembro";


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "equipo2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "equipo2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2')");
			//---------------------------------------------------------------------//




			/**----------------------------------------------------------------------------------
			 * --38			acquire role creador3
			 * ----------------------------------------------------------------------------------
			 */


			roleName= "creador3";


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "equipo2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'equipo2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "equipo2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'equipo2')");
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
	
	public void testAcquireRoleJerarquia3()
	{
		try
		{
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('jerarquia',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
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


			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('jerarquia2',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'Jerarquia2'))");


			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");



			/**----------------------------------------------------------------------------------
			 * --39			acquire role subordinado2
			 * ----------------------------------------------------------------------------------
			 */



		

			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

			

			String roleName= "subordinado2";


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			String result = omsProxy.acquireRole(roleName, "jerarquia2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "jerarquia2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");
			//---------------------------------------------------------------------//

	

			/**----------------------------------------------------------------------------------
			 * --40			acquire role subordinado2
			 * ----------------------------------------------------------------------------------
			 */



			dbA.executeSQL("DELETE FROM agentPlayList WHERE idagentList = (SELECT idagentList FROM agentList WHERE agentName = 'pruebas')");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

			//dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'subordinado' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))");


			roleName= "subordinado";


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "jerarquia2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "jerarquia2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");
			//---------------------------------------------------------------------//


			/**----------------------------------------------------------------------------------
			 * --41		acquire role creador2
			 * ----------------------------------------------------------------------------------
			 */



			dbA.executeSQL("DELETE FROM agentPlayList WHERE idagentList = (SELECT idagentList FROM agentList WHERE agentName = 'pruebas')");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

			//dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'subordinado' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))");


			roleName= "subordinado";


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "jerarquia2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "jerarquia2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");
			//---------------------------------------------------------------------//

		



			/**----------------------------------------------------------------------------------
			 * --42			acquire role supervisor2
			 * ----------------------------------------------------------------------------------
			 */



			dbA.executeSQL("DELETE FROM agentPlayList WHERE idagentList = (SELECT idagentList FROM agentList WHERE agentName = 'pruebas')");



			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

			//dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'subordinado' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))");


			roleName= "supervisor2";


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "jerarquia2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "jerarquia2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");
			//---------------------------------------------------------------------//

		





			/**----------------------------------------------------------------------------------
			 * --43			acquire role supervisor
			 * ----------------------------------------------------------------------------------
			 */




			dbA.executeSQL("DELETE FROM agentPlayList WHERE idagentList = (SELECT idagentList FROM agentList WHERE agentName = 'pruebas')");

			dbA.executeSQL("DELETE FROM roleList WHERE roleName != 'creador'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName != 'creador'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = 'supervisor' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))");


			roleName= "supervisor";


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "jerarquia2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "jerarquia2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");
			//---------------------------------------------------------------------//




			/**----------------------------------------------------------------------------------
			 * --44		acquire role supervisor
			 * ----------------------------------------------------------------------------------
			 */




			dbA.executeSQL("DELETE FROM agentPlayList WHERE idagentList = (SELECT idagentList FROM agentList WHERE agentName = 'pruebas')");

			dbA.executeSQL("DELETE FROM roleList WHERE roleName != 'creador'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName != 'creador'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");




			roleName= "creador2";


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "jerarquia2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "jerarquia2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");
			//---------------------------------------------------------------------//

	



			/**----------------------------------------------------------------------------------
			 * --45	acquire role creador3
			 * ----------------------------------------------------------------------------------
			 */




			dbA.executeSQL("DELETE FROM agentPlayList WHERE idagentList = (SELECT idagentList FROM agentList WHERE agentName = 'pruebas')");

			dbA.executeSQL("DELETE FROM roleList WHERE roleName != 'creador'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia')");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName != 'creador'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador2',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador2' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");




			roleName= "creador3";


			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			result = omsProxy.acquireRole(roleName, "jerarquia2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");
			//---------------------------------------------------------------------//

			//---------------------------------------------------------------------//
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador3',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			result = omsProxy.acquireRole(roleName, "jerarquia2");
			assertEquals("El mensaje debe ser el siguiente:", roleName+ " acquired", result);

			dbA.executeSQL("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE roleName = '"+roleName+"' AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
			dbA.executeSQL("DELETE FROM roleList WHERE roleName = '"+ roleName+"'  AND idunitList =  (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2')");
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
