package TestOMS;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.SF;
import es.upv.dsic.gti_ia.organization.exception.ForbiddenNormException;
import es.upv.dsic.gti_ia.organization.exception.NotPlaysRoleException;

/** 
 * @author Jose Alemany Bordera  -  jalemany1@dsic.upv.es
 * 
 */

public class TestLeaveRoleProhibitionDomain {

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
		dbA.executeSQL("DELETE FROM actionNormParam");
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
		dbA.executeSQL("DELETE FROM actionNormParam");
		dbA.executeSQL("DELETE FROM normList");
		dbA.executeSQL("DELETE FROM roleList WHERE idroleList != 1");
		dbA.executeSQL("DELETE FROM unitHierarchy WHERE idChildUnit != 1");
		dbA.executeSQL("DELETE FROM unitList WHERE idunitList != 1");

		//--------------------------------------------//

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
		
	}
	
	@Test(expected=ForbiddenNormException.class)
	public void testLeaveRoleProhibitionDomain1() throws Exception {
		
		//------------------------------------------- Test Initialization  -----------------------------------------------//
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('plana',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'plana'))");
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
				"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('jerarquia',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))");
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
				"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList WHERE "
				+ "agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT "
				+ "idunitList FROM unitList WHERE unitName = 'jerarquia'))))");
		
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList WHERE "
				+ "agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT "
				+ "idunitList FROM unitList WHERE unitName = 'plana'))))");

		dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
				"VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'plana'),'prohibidoAbandonarRol', (SELECT iddeontic FROM deontic WHERE deonticdesc = 'f'), (SELECT idtargetType FROM " +
				"targetType WHERE targetName = 'roleName'),-1, (SELECT idactionNorm FROM actionNorm WHERE description = 'leaveRole' AND numParams = 3), '@prohibidoAbandonarRol[f,<roleName:_>,leaveRole(Rol,plana,Agent),isAgent(Agent) & playsRole(Agent,Rol,plana),_]',"
				+ "'leaveRole(Rol,plana,Agent):-isAgent(Agent) & playsRole(Agent,Rol,plana)')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'prohibidoAbandonarRol'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'leaveRole' AND numParams = 3), 'Rol')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'prohibidoAbandonarRol'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'leaveRole' AND numParams = 3), 'plana')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'prohibidoAbandonarRol'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'leaveRole' AND numParams = 3), 'Agent')");
		//----------------------------------------------------------------------------------------------------------------//
			
		try {
			omsProxy.leaveRole("creador","plana");
		} catch(ForbiddenNormException e) {
			throw e;
		} finally {
			assertTrue(dbA.executeQuery("SELECT * FROM unitList ul INNER JOIN roleList rl ON (ul.idunitList=rl.idunitList) INNER JOIN agentPlayList apl ON (rl.idroleList=apl.idroleList) INNER JOIN agentList al ON (apl.idagentList=al.idagentList)"
					+ "WHERE ul.unitName = 'jerarquia' AND rl.roleName = 'creador' AND al.agentName = 'pruebas'"));
			assertTrue(dbA.executeQuery("SELECT * FROM unitList ul INNER JOIN roleList rl ON (ul.idunitList=rl.idunitList) INNER JOIN agentPlayList apl ON (rl.idroleList=apl.idroleList) INNER JOIN agentList al ON (apl.idagentList=al.idagentList)"
					+ "WHERE ul.unitName = 'plana' AND rl.roleName = 'creador' AND al.agentName = 'pruebas'"));
		}
		
	}
	
	@Test(expected=NotPlaysRoleException.class)
	public void testLeaveRoleProhibitionDomain2() throws Exception {
		
		//------------------------------------------- Test Initialization  -----------------------------------------------//
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('plana',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'plana'))");
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
				"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
			
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('jerarquia',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))");
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
				"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList WHERE "
				+ "agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT "
				+ "idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

		dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
				"VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'plana'),'prohibidoAbandonarRol', (SELECT iddeontic FROM deontic WHERE deonticdesc = 'f'), (SELECT idtargetType FROM " +
				"targetType WHERE targetName = 'roleName'),-1, (SELECT idactionNorm FROM actionNorm WHERE description = 'leaveRole' AND numParams = 3), '@prohibidoAbandonarRol[f,<roleName:_>,leaveRole(Rol,plana,Agent),isAgent(Agent) & playsRole(Agent,Rol,plana),_]',"
				+ "'leaveRole(Rol,plana,Agent):-isAgent(Agent) & playsRole(Agent,Rol,plana)')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'prohibidoAbandonarRol'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'leaveRole' AND numParams = 3), 'Rol')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'prohibidoAbandonarRol'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'leaveRole' AND numParams = 3), 'plana')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'prohibidoAbandonarRol'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'leaveRole' AND numParams = 3), 'Agent')");
		//----------------------------------------------------------------------------------------------------------------//
			
		try {
			omsProxy.leaveRole("creador","plana");
		} catch(NotPlaysRoleException e) {
			throw e;
		} finally {
			assertTrue(dbA.executeQuery("SELECT * FROM unitList ul INNER JOIN roleList rl ON (ul.idunitList=rl.idunitList) INNER JOIN agentPlayList apl ON (rl.idroleList=apl.idroleList) INNER JOIN agentList al ON (apl.idagentList=al.idagentList)"
					+ "WHERE ul.unitName = 'jerarquia' AND rl.roleName = 'creador' AND al.agentName = 'pruebas'"));
		}
		
	}
}
