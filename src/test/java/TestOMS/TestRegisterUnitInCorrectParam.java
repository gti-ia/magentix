package TestOMS;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.SF;
import es.upv.dsic.gti_ia.organization.exception.EmptyParametersException;
import es.upv.dsic.gti_ia.organization.exception.MySQLException;
import es.upv.dsic.gti_ia.organization.exception.NotValidIdentifierException;
import es.upv.dsic.gti_ia.organization.exception.ParentUnitNotExistsException;


public class TestRegisterUnitInCorrectParam {

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

	@Test(timeout = 5 * 60 * 1000, expected = ParentUnitNotExistsException.class)
	public void testRegisterUnit1() throws Exception {
		String result = omsProxy.registerUnit("Plana", "hierarchy", "inexistente", "creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = ParentUnitNotExistsException.class)
	public void testRegisterUnit2() throws Exception {
		String result = omsProxy.registerUnit("Equipo", "hierarchy", "inexistente", "creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = ParentUnitNotExistsException.class)
	public void testRegisterUnit3() throws Exception {
		String result = omsProxy.registerUnit("Jerarquia", "hierarchy", "inexistente", "creador");
		fail(result);
	}

	@Test(timeout = 5 * 60 * 1000, expected = EmptyParametersException.class)
	public void testRegisterUnit4() throws Exception {
		String result = omsProxy.registerUnit("Plana", "flat", "virtual", "");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = EmptyParametersException.class)
	public void testRegisterUnit5() throws Exception {
		String result = omsProxy.registerUnit("Equipo", "team", "virtual", "");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = EmptyParametersException.class)
	public void testRegisterUnit6() throws Exception {
		String result = omsProxy.registerUnit("Jerarquia", "hierarchy", "virtual", "");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = EmptyParametersException.class)
	public void testRegisterUnit7() throws Exception {
		String result = omsProxy.registerUnit("Plana", "flat", "virtual", null);
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = EmptyParametersException.class)
	public void testRegisterUnit8() throws Exception {
		String result = omsProxy.registerUnit("Equipo", "team", "virtual", null);
		fail(result);
	}
		
	@Test(timeout = 5 * 60 * 1000, expected = EmptyParametersException.class)
	public void testRegisterUnit9() throws Exception {
		String result = omsProxy.registerUnit("Jerarquia", "hierarchy", "virtual", null);
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = MySQLException.class)
	public void testRegisterUnit10() throws Exception {
		String result = omsProxy.registerUnit("Plana", "insexistente", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = MySQLException.class)
	public void testRegisterUnit11() throws Exception {
		String result = omsProxy.registerUnit("Equipo", "insexistente", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = MySQLException.class)
	public void testRegisterUnit12() throws Exception {
		String result = omsProxy.registerUnit("Jerarquia", "insexistente", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = MySQLException.class)
	public void testRegisterUnit13() throws Exception {
		String result = omsProxy.registerUnit("Plana", null, "virtual","Creador");
		fail(result);
	}

	@Test(timeout = 5 * 60 * 1000, expected = MySQLException.class)
	public void testRegisterUnit14() throws Exception {
		String result = omsProxy.registerUnit("Equipo", null, "virtual","Creador");
		fail(result);
	}	
	
	@Test(timeout = 5 * 60 * 1000, expected = MySQLException.class)
	public void testRegisterUnit15() throws Exception {
		String result = omsProxy.registerUnit("Jerarquia", null, "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = EmptyParametersException.class)
	public void testRegisterUnit16() throws Exception {
		String result = omsProxy.registerUnit("", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = EmptyParametersException.class)
	public void testRegisterUnit17() throws Exception {
		String result = omsProxy.registerUnit("", "team", "virtual","Creador");
		fail(result);
	}

	@Test(timeout = 5 * 60 * 1000, expected = EmptyParametersException.class)
	public void testRegisterUnit18() throws Exception {
		String result = omsProxy.registerUnit("", "hierarchy", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = EmptyParametersException.class)
	public void testRegisterUnit19() throws Exception {
		String result = omsProxy.registerUnit(null, "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = EmptyParametersException.class)
	public void testRegisterUnit20() throws Exception {
		String result = omsProxy.registerUnit(null, "team", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = EmptyParametersException.class)
	public void testRegisterUnit21() throws Exception {
		String result = omsProxy.registerUnit(null, "hierarchy", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit22() throws Exception {
		String result = omsProxy.registerUnit("**Miunidad", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit23() throws Exception {
		String result = omsProxy.registerUnit("team", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit24() throws Exception {
		String result = omsProxy.registerUnit("flat", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit25() throws Exception {
		String result = omsProxy.registerUnit("hierarchy", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit26() throws Exception {
		String result = omsProxy.registerUnit("supervisor", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit27() throws Exception {
		String result = omsProxy.registerUnit("subordinate", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit28() throws Exception {
		String result = omsProxy.registerUnit("member", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit29() throws Exception {
		String result = omsProxy.registerUnit("creator", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit30() throws Exception {
		String result = omsProxy.registerUnit("private", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit31() throws Exception {
		String result = omsProxy.registerUnit("public", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit32() throws Exception {
		String result = omsProxy.registerUnit("external", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit33() throws Exception {
		String result = omsProxy.registerUnit("internal", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit34() throws Exception {
		String result = omsProxy.registerUnit("registerUnit", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit35() throws Exception {
		String result = omsProxy.registerUnit("deregisterUnit", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit36() throws Exception {
		String result = omsProxy.registerUnit("registerRole", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit37() throws Exception {
		String result = omsProxy.registerUnit("deregisterRole", "flat", "virtual","Creador");
		fail(result);
	}	
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit38() throws Exception {
		String result = omsProxy.registerUnit("registerNorm", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit39() throws Exception {
		String result = omsProxy.registerUnit("deregisterNorm", "flat", "virtual","Creador");
		fail(result);
	}

	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit40() throws Exception {
		String result = omsProxy.registerUnit("allocateRole", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit41() throws Exception {
		String result = omsProxy.registerUnit("deallocateRole", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit42() throws Exception {
		String result = omsProxy.registerUnit("joinUnit", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit43() throws Exception {
		String result = omsProxy.registerUnit("informAgentRole", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit44() throws Exception {
		String result = omsProxy.registerUnit("informMembers", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit45() throws Exception {
		String result = omsProxy.registerUnit("informQuantityMembers", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit46() throws Exception {
		String result = omsProxy.registerUnit("informUnit", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit47() throws Exception {
		String result = omsProxy.registerUnit("informUnitRoles", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit48() throws Exception {
		String result = omsProxy.registerUnit("informTargetNorms", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit49() throws Exception {
		String result = omsProxy.registerUnit("informRole", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit50() throws Exception {
		String result = omsProxy.registerUnit("informNorm", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit51() throws Exception {
		String result = omsProxy.registerUnit("acquireRole", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit52() throws Exception {
		String result = omsProxy.registerUnit("leaveRole", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit53() throws Exception {
		String result = omsProxy.registerUnit("isNorm", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit54() throws Exception {
		String result = omsProxy.registerUnit("hasDeontic", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit55() throws Exception {
		String result = omsProxy.registerUnit("hasTarget", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit56() throws Exception {
		String result = omsProxy.registerUnit("hasAction", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit57() throws Exception {
		String result = omsProxy.registerUnit("isRole", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit58() throws Exception {
		String result = omsProxy.registerUnit("hasAccessibility", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit59() throws Exception {
		String result = omsProxy.registerUnit("hasVisibility", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit60() throws Exception {
		String result = omsProxy.registerUnit("hasPosition", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit61() throws Exception {
		String result = omsProxy.registerUnit("isUnit", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit62() throws Exception {
		String result = omsProxy.registerUnit("hasType", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit63() throws Exception {
		String result = omsProxy.registerUnit("hasParent", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit64() throws Exception {
		String result = omsProxy.registerUnit("div", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit65() throws Exception {
		String result = omsProxy.registerUnit("mod", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit66() throws Exception {
		String result = omsProxy.registerUnit("not", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit67() throws Exception {
		String result = omsProxy.registerUnit("_", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit68() throws Exception {
		String result = omsProxy.registerUnit("agentName", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit69() throws Exception {
		String result = omsProxy.registerUnit("roleName", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit70() throws Exception {
		String result = omsProxy.registerUnit("positionName", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit71() throws Exception {
		String result = omsProxy.registerUnit("o", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit72() throws Exception {
		String result = omsProxy.registerUnit("f", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit73() throws Exception {
		String result = omsProxy.registerUnit("p", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit74() throws Exception {
		String result = omsProxy.registerUnit("*invalido", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit75() throws Exception {
		String result = omsProxy.registerUnit("+invalido", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit76() throws Exception {
		String result = omsProxy.registerUnit("?invalido", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit77() throws Exception {
		String result = omsProxy.registerUnit("!invalido", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit78() throws Exception {
		String result = omsProxy.registerUnit("invalido!", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit79() throws Exception {
		String result = omsProxy.registerUnit("invalido?", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit80() throws Exception {
		String result = omsProxy.registerUnit("invalido*", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit81() throws Exception {
		String result = omsProxy.registerUnit("invalido+", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit82() throws Exception {
		String result = omsProxy.registerUnit("!invalido", "flat", "virtual","Creador");
		fail(result);
	}
	
	@Test(timeout = 5 * 60 * 1000, expected = NotValidIdentifierException.class)
	public void testRegisterUnit83() throws Exception {
		String result = omsProxy.registerUnit("invalido-invalido", "flat", "virtual","Creador");
		fail(result);
	}
}
