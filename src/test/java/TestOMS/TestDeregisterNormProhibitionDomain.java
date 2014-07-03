package TestOMS;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.SF;
import es.upv.dsic.gti_ia.organization.exception.ForbiddenNormException;

/** 
 * @author Jose Alemany Bordera  -  jalemany1@dsic.upv.es
 * 
 */

public class TestDeregisterNormProhibitionDomain {

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
		//--------------------------------------------//

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
	
	@Test(timeout = 5 * 60 * 1000, expected=ForbiddenNormException.class)
	public void testDeregisterNormProhibitionDomain1() throws Exception {
		
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
				"('jefe',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
				"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
				"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
		
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList WHERE "
				+ "agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT "
				+ "idunitList FROM unitList WHERE unitName = 'jerarquia'))))");
		
		dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
				"VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'plana'),'prohibidoDeregistrarNorma', (SELECT iddeontic FROM deontic WHERE deonticdesc = 'f'), (SELECT idtargetType FROM " +
				"targetType WHERE targetName = 'agentName'), -1, (SELECT idactionNorm FROM actionNorm WHERE description = 'deregisterNorm' AND numParams = 3), '@prohibidoDeregistrarNorma[f,<agentName:_>,deregisterNorm(_,plana,Agent),isAgent(Agent) & not(playsRole(Agent,_,plana)),_]',"
				+ "'deregisterNorm(_,plana,Agent):-isAgent(Agent) & not(playsRole(Agent,_,plana))')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'prohibidoDeregistrarNorma'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'deregisterNorm' AND numParams = 3), 'plana')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'prohibidoDeregistrarNorma'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'deregisterNorm' AND numParams = 3), '_')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'prohibidoDeregistrarNorma'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'deregisterNorm' AND numParams = 3), 'Agent')");
		
		dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
				"VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'plana'),'permisoAdquirirRole', (SELECT iddeontic FROM deontic WHERE deonticdesc = 'p'), (SELECT idtargetType FROM " +
				"targetType WHERE targetName = 'roleName'), -1, (SELECT idactionNorm FROM actionNorm WHERE description = 'acquireRole' AND numParams = 3), '@permisoAdquirirRole[p,<roleName:_>,acquireRole(creador,plana,_),_,_]',"
				+ "'acquireRole(creador,plana,_)')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'permisoAdquirirRole'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'acquireRole' AND numParams = 3), 'creador')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'permisoAdquirirRole'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'acquireRole' AND numParams = 3), 'plana')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'permisoAdquirirRole'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'acquireRole' AND numParams = 3), '_')");
		//----------------------------------------------------------------------------------------------------------------//

		omsProxy.deregisterNorm("permisoAdquirirRole","plana");
		
		fail("Should have return an exception, product of a deregisterNorm not allowed.");
		
	}
	
	@Test(timeout = 5 * 60 * 1000, expected=ForbiddenNormException.class)
	public void testDeregisterNormProhibitionDomain2() throws Exception {
		
		//------------------------------------------- Test Initialization  -----------------------------------------------//
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('equipo',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'equipo'))");
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
		
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList WHERE "
				+ "agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT "
				+ "idunitList FROM unitList WHERE unitName = 'equipo'))))");
		
		dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
				"VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'equipo'),'prohibidoDeregistrarNorma', (SELECT iddeontic FROM deontic WHERE deonticdesc = 'f'), (SELECT idtargetType FROM " +
				"targetType WHERE targetName = 'agentName'), -1, (SELECT idactionNorm FROM actionNorm WHERE description = 'deregisterNorm' AND numParams = 3), '@prohibidoDeregistrarNorma[f,<agentName:_>,deregisterNorm(_,equipo,Agent),isAgent(Agent) & playsRole(Agent,miembro,equipo),_]',"
				+ "'deregisterNorm(_,equipo,Agent):-isAgent(Agent) & playsRole(Agent,miembro,equipo)')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'prohibidoDeregistrarNorma'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'deregisterNorm' AND numParams = 3), '_')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'prohibidoDeregistrarNorma'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'deregisterNorm' AND numParams = 3), 'equipo')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'prohibidoDeregistrarNorma'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'deregisterNorm' AND numParams = 3), 'Agent')");
		
		dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
				"VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'equipo'),'permisoAdquirirRole', (SELECT iddeontic FROM deontic WHERE deonticdesc = 'p'), (SELECT idtargetType FROM " +
				"targetType WHERE targetName = 'roleName'), (SELECT rl.idroleList FROM roleList rl INNER JOIN unitList ul ON (rl.idunitList=ul.idunitList) WHERE rl.roleName = 'creador' AND ul.unitName = 'equipo'), "
				+ "(SELECT idactionNorm FROM actionNorm WHERE description = 'acquireRole' AND numParams = 3), '@permisoAdquirirRole[p,<roleName:creador>,acquireRole(creador,equipo,Agent),isAgent(Agent) & playsRole(Agent,subordinado,jerarquia) & isUnit(jerarquia) & hasType(jerarquia,hierarchy) & isRole(subordinado,jerarquia),playsRole(Agent,creador,equipo)]',"
				+ "'acquireRole(creador,equipo,Agent):-isAgent(Agent) & playsRole(Agent,subordinado,jerarquia) & isUnit(jerarquia) & hasType(jerarquia,hierarchy) & isRole(subordinado,jerarquia) & not(playsRole(Agent,creador,equipo))')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'permisoAdquirirRole'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'acquireRole' AND numParams = 3), 'creador')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'permisoAdquirirRole'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'acquireRole' AND numParams = 3), 'plana')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'permisoAdquirirRole'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'acquireRole' AND numParams = 3), '_')");
		//----------------------------------------------------------------------------------------------------------------//

		omsProxy.deregisterNorm("permisoAdquirirRole","equipo");
		
		fail("Should have return an exception, product of a registerNorm not allowed.");
		
	}
	
	@Test(timeout = 5 * 60 * 1000, expected=ForbiddenNormException.class)
	public void testDeregisterNormProhibitionDomain3() throws Exception {
		
		//------------------------------------------- Test Initialization  -----------------------------------------------//
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('jerarquia',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))");
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
				"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('jefe',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
				"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
				"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
		
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList WHERE "
				+ "agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'jefe' AND idunitList = (SELECT "
				+ "idunitList FROM unitList WHERE unitName = 'jerarquia'))))");
		
		dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
				"VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),'prohibidoDeregistrarNorma', (SELECT iddeontic FROM deontic WHERE deonticdesc = 'f'), (SELECT idtargetType FROM " +
				"targetType WHERE targetName = 'agentName'), -1, (SELECT idactionNorm FROM actionNorm WHERE description = 'deregisterNorm' AND numParams = 3), '@prohibidoDeregistrarNorma[f,<agentName:_>,deregisterNorm(_,jerarquia,Agent),isAgent(Agent) & playsRole(Agent,supervisor,jerarquia),_]',"
				+ "'deregisterNorm(_,jerarquia,Agent):-isAgent(Agent) & playsRole(Agent,jefe,jerarquia)')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'prohibidoDeregistrarNorma'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'deregisterNorm' AND numParams = 3), '_')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'prohibidoDeregistrarNorma'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'deregisterNorm' AND numParams = 3), 'jerarquia')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'prohibidoDeregistrarNorma'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'deregisterNorm' AND numParams = 3), 'Agent')");
		
		dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
				"VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),'prohibidoAdquirirRole', (SELECT iddeontic FROM deontic WHERE deonticdesc = 'f'), (SELECT idtargetType FROM " +
				"targetType WHERE targetName = 'roleName'), (SELECT rl.idroleList FROM roleList rl INNER JOIN unitList ul ON (rl.idunitList=ul.idunitList) WHERE rl.roleName = 'creador' AND ul.unitName = 'jerarquia'), "
				+ "(SELECT idactionNorm FROM actionNorm WHERE description = 'acquireRole' AND numParams = 3), '@prohibidoAdquirirRole[f,<roleName:creador>,acquireRole(_,jerarquia,Agent),isAgent(Agent) & playsRole(Agent,creador,jerarquia),_]',"
				+ "'acquireRole(_,jerarquia,Agent):-isAgent(Agent) & playsRole(Agent,creador,jerarquia)')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'prohibidoAdquirirRole'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'acquireRole' AND numParams = 3), '_')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'prohibidoAdquirirRole'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'acquireRole' AND numParams = 3), 'jerarquia')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'prohibidoAdquirirRole'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'acquireRole' AND numParams = 3), 'Agent')");
		//----------------------------------------------------------------------------------------------------------------//

		omsProxy.deregisterNorm("prohibidoAdquirirRole","jerarquia");
		
		fail("Should have return an exception, product of a registerNorm not allowed.");
		
	}
}
