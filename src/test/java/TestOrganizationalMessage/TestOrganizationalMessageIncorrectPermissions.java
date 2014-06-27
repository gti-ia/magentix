package TestOrganizationalMessage;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.SF;
import es.upv.dsic.gti_ia.organization.exception.AgentNotInUnitException;
import es.upv.dsic.gti_ia.organization.exception.EmptyParametersException;
import es.upv.dsic.gti_ia.organization.exception.NotInUnitOrParentUnitException;
import es.upv.dsic.gti_ia.organization.exception.NotPlaysAnyRoleException;
import es.upv.dsic.gti_ia.organization.exception.OnlyPlaysCreatorException;
import es.upv.dsic.gti_ia.organization.exception.UnitNotExistsException;

public class TestOrganizationalMessageIncorrectPermissions {

	OMSProxy omsProxy = null;
	DatabaseAccess dbA = null;

	Agent agent = null;
	OMS oms = null;
	SF sf = null;
	Process qpid_broker;
	
	@Before
	public void setUp() throws Exception {
		qpid_broker = qpidManager.UnixQpidManager.startQpid(
				Runtime.getRuntime(), qpid_broker);

		AgentsConnection.connect();

		oms = new OMS(new AgentID("OMS"));

		sf = new SF(new AgentID("SF"));

		oms.start();
		sf.start();

		agent = new Agent(new AgentID("pruebas"));

		omsProxy = new OMSProxy(agent);

		dbA = new DatabaseAccess();

		// ------------------Clean Data Base -----------//
		dbA.executeSQL("DELETE FROM agentPlayList");
		dbA.executeSQL("DELETE FROM agentList");
		dbA.executeSQL("DELETE FROM normList");
		dbA.executeSQL("DELETE FROM roleList WHERE idroleList != 1");
		dbA.executeSQL("DELETE FROM unitHierarchy WHERE idChildUnit != 1");
		dbA.executeSQL("DELETE FROM unitList WHERE idunitList != 1");

		// --------------------------------------------//

		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('Plana',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'Plana'))");

		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('Equipo',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'Equipo'))");

		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('Equipo2',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'Equipo'),(SELECT idunitList FROM unitList WHERE unitName = 'Equipo2'))");

		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('Jerarquia',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'Jerarquia'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"
				+ "('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"
				+ "(SELECT idposition FROM position WHERE positionName = 'member'), "
				+ "(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"
				+ "(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"
				+ "('creador',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"
				+ "(SELECT idposition FROM position WHERE positionName = 'creator'), "
				+ "(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"
				+ "(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"
				+ "('miembro',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"
				+ "(SELECT idposition FROM position WHERE positionName = 'member'), "
				+ "(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"
				+ "(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"
				+ "('creador',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"
				+ "(SELECT idposition FROM position WHERE positionName = 'creator'), "
				+ "(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"
				+ "(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"
				+ "('creador',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"
				+ "(SELECT idposition FROM position WHERE positionName = 'creator'), "
				+ "(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"
				+ "(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"
				+ "('supervisor',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"
				+ "(SELECT idposition FROM position WHERE positionName = 'supervisor'), "
				+ "(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"
				+ "(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"
				+ "('subordinado',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"
				+ "(SELECT idposition FROM position WHERE positionName = 'subordinate'), "
				+ "(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"
				+ "(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");

	}
	@After
	public void tearDown() throws Exception {

		// ------------------Clean Data Base -----------//
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
	@Test(timeout = 5 * 1000)
	public void test1() {

		String unit = "Equipo";

		try {
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"
					+ "((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

			omsProxy.buildOrganizationalMessage(unit);

			fail();

		} catch (NotInUnitOrParentUnitException e) {

			assertNotNull(e);

		} catch (Exception e) {
			fail(e.getMessage());
		}

	}
	@Test(timeout = 5 * 1000)
	public void test2() {

		String unit = "Equipo2";

		try {
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"
					+ "((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

			omsProxy.buildOrganizationalMessage(unit);

			fail();

		} catch (AgentNotInUnitException e) {

			assertNotNull(e);

		} catch (Exception e) {
			fail(e.getMessage());
		}

	}
	@Test(timeout = 5 * 1000)
	public void test4() {

		String unit = "Jerarquia";

		try {
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"
					+ "((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

			omsProxy.buildOrganizationalMessage(unit);

			fail();

		} catch (OnlyPlaysCreatorException e) {

			assertNotNull(e);

		} catch (Exception e) {
			fail(e.getMessage());
		}

	}
	@Test(timeout = 5 * 1000)
	public void test5() {

		String unit = "Inexistente";

		try {

			omsProxy.buildOrganizationalMessage(unit);

			fail();

		} catch (UnitNotExistsException e) {

			assertNotNull(e);

		} catch (Exception e) {
			fail(e.getMessage());
		}

	}
	@Test(timeout = 5 * 1000)
	public void test6() {

		String unit = "";

		try {

			omsProxy.buildOrganizationalMessage(unit);

			fail();

		} catch (EmptyParametersException e) {

			assertNotNull(e);

		} catch (Exception e) {
			fail(e.getMessage());
		}

	}
	@Test(timeout = 5 * 1000)
	public void test3() {

		String unit = "Plana";

		try {

			omsProxy.buildOrganizationalMessage(unit);

			fail();

		} catch (NotPlaysAnyRoleException e) {

			assertNotNull(e);

		} catch (Exception e) {
			fail(e.getMessage());
		}

	}

}
