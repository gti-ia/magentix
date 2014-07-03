package TestOMS;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.SF;
import es.upv.dsic.gti_ia.organization.exception.EmptyParametersException;
import es.upv.dsic.gti_ia.organization.exception.InvalidRolePositionException;
import es.upv.dsic.gti_ia.organization.exception.RoleNotExistsException;
import es.upv.dsic.gti_ia.organization.exception.UnitNotExistsException;


public class TestInformQuantityMembersInCorrectParam {

	OMSProxy omsProxy = null;
	DatabaseAccess dbA = null;


	Agent agent = null;

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
		
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
		"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

	}

	@Test(timeout = 5 * 60 * 1000, expected = UnitNotExistsException.class)
	public void testInformMembers1() throws Exception {
		omsProxy.informQuantityMembers("noexiste", "subordinado", "subordinate");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = EmptyParametersException.class)
	public void testInformMembers2() throws Exception {
		omsProxy.informQuantityMembers("", "subordinado", "subordinate");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = EmptyParametersException.class)
	public void testInformMembers3() throws Exception {
		omsProxy.informQuantityMembers(null, "subordinado", "subordinate");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = RoleNotExistsException.class)
	public void testInformMembers4() throws Exception {
		omsProxy.informQuantityMembers("virtual", "noexiste", "subordinate");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = RoleNotExistsException.class)
	public void testInformMembers5() throws Exception {
		omsProxy.informQuantityMembers("virtual", null, "subordinate");
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = InvalidRolePositionException.class)
	public void testInformMembers6() throws Exception {
		omsProxy.informQuantityMembers("virtual", "participant", "subordinate");
	}
}
