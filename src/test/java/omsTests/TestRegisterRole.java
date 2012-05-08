package omsTests;

import junit.framework.TestCase;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.THOMASException;


public class TestRegisterRole extends TestCase {

	OMSProxy omsProxy = null;
	DatabaseAccess dbA = null;


	Agent agent = null;


	public TestRegisterRole()
	{

	}

	protected void tearDown() throws Exception {




		dbA = null;
		omsProxy = null;

		agent.terminate();
		agent = null;





	}
	protected void setUp() throws Exception {
		super.setUp();


		AgentsConnection.connect();


		agent = new Agent(new AgentID("pruebas"));



		omsProxy = new OMSProxy(agent);

		dbA = new DatabaseAccess();

		//------------------Clean Data Base -----------//
		dbA.executeSQL("DELETE FROM agentPlayList");
		dbA.executeSQL("DELETE FROM roleList WHERE idroleList != 1");
		dbA.executeSQL("DELETE FROM unitHierarchy WHERE idChildUnit != 1");
		dbA.executeSQL("DELETE FROM unitList WHERE idunitList != 1");

		//--------------------------------------------//




	}

	public void testRegisterRole1()
	{
		try
		{
			
			String role = "miembro";
		
			dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
			"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

			

			String result = omsProxy.registerRole(role, "virtual", "external", "public", "member");
			assertEquals("El mensaje debe ser el siguiente:", role+ " created", result);
			
			role = "creador";
			
			result = omsProxy.registerRole(role, "virtual", "internal", "private", "creator");
			assertEquals("El mensaje debe ser el siguiente:", role+ " created", result);
		

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
