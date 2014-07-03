package TestOMS;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.SF;

/** 
 * @author Jose Alemany Bordera  -  jalemany1@dsic.upv.es
 * 
 */

public class TestAcquireRolePermissionsDomain {

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
	
	@Test(timeout = 5 * 60 * 1000)
	public void testAcquireRolePermissionsDomain1() throws Exception {
		
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
				"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
				"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
				"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList WHERE "
				+ "agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT "
				+ "idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

		dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
				"VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'plana'),'permisoAdquirirRol', (SELECT iddeontic FROM deontic WHERE deonticdesc = 'p'), (SELECT idtargetType FROM " +
				"targetType WHERE targetName = 'roleName'),-1, (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'acquireRole' AND numParams = 3), '@permisoAdquirirRol[p,<roleName:_>,acquireRole(creador,plana,_),_,_]','acquireRole(creador,plana,_)')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'permisoAdquirirRol'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'acquireRole' AND numParams = 3), 'creador')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'permisoAdquirirRol'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'acquireRole' AND numParams = 3), 'plana')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'permisoAdquirirRol'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'acquireRole' AND numParams = 3), '_')");
		//----------------------------------------------------------------------------------------------------------------//
			
		String result = omsProxy.acquireRole("creador","plana");
		assertEquals("The message should be:", "creador acquired", result);
			
		assertTrue(dbA.executeQuery("SELECT * FROM unitList ul INNER JOIN roleList rl ON (ul.idunitList=rl.idunitList) INNER JOIN agentPlayList apl ON (rl.idroleList=apl.idroleList) INNER JOIN agentList al ON (apl.idagentList=al.idagentList)"
				+ "WHERE ul.unitName = 'plana' AND rl.roleName = 'creador' AND al.agentName = 'pruebas'"));
		
	}
	
	@Test(timeout = 5 * 60 * 1000)
	public void testAcquireRolePermissionsDomain2() throws Exception {
		
		//------------------------------------------- Test Initialization  -----------------------------------------------//
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('equipo',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'equipo'))");
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
				"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
		
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('jerarquia',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))");
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
		
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList WHERE "
				+ "agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT "
				+ "idunitList FROM unitList WHERE unitName = 'jerarquia'))))");
		
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList WHERE "
				+ "agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT "
				+ "idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

		dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
				"VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'equipo'),'permisoAdquirirRol', (SELECT iddeontic FROM deontic WHERE deonticdesc = 'p'), (SELECT idtargetType FROM " +
				"targetType WHERE targetName = 'roleName'),(SELECT rl.idroleList FROM unitList ul INNER JOIN roleList rl ON (ul.idunitList=rl.idunitList) WHERE ul.unitName='jerarquia' AND rl.roleName ='subordinado'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'acquireRole' AND numParams = 3), '@permisoAdquirirRol[p,<roleName:subordinado>,acquireRole(creador,equipo,Agent),isAgent(Agent) & playsRole(Agent,subordinado,jerarquia) & isUnit(jerarquia) & hasType(jerarquia,hierarchy) & isRole(subordinado,jerarquia),playsRole(Agent,creador,equipo)]',"
				+ "'acquireRole(creador,equipo,Agent):-isAgent(Agent) & playsRole(Agent,subordinado,jerarquia) & isUnit(jerarquia) & hasType(jerarquia,hierarchy) & isRole(subordinado,jerarquia) & not(playsRole(Agent,creador,equipo))')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'permisoAdquirirRol'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'acquireRole' AND numParams = 3), 'creador')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'permisoAdquirirRol'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'acquireRole' AND numParams = 3), 'equipo')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'permisoAdquirirRol'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'acquireRole' AND numParams = 3), 'Agent')");
		//----------------------------------------------------------------------------------------------------------------//
			
		String result = omsProxy.acquireRole("creador","equipo");
		assertEquals("The message should be:", "creador acquired", result);
			
		assertTrue(dbA.executeQuery("SELECT * FROM unitList ul INNER JOIN roleList rl ON (ul.idunitList=rl.idunitList) INNER JOIN agentPlayList apl ON (rl.idroleList=apl.idroleList) INNER JOIN agentList al ON (apl.idagentList=al.idagentList)"
				+ "WHERE ul.unitName = 'equipo' AND rl.roleName = 'creador' AND al.agentName = 'pruebas'"));
		
	}
	
	@Test(timeout = 5 * 60 * 1000)
	public void testAcquireRolePermissionsDomain3() throws Exception {
		
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
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
				"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
		
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('jerarquia2',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('jefe',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
		
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList WHERE "
				+ "agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT "
				+ "idunitList FROM unitList WHERE unitName = 'jerarquia'))))");
		
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList WHERE "
				+ "agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT "
				+ "idunitList FROM unitList WHERE unitName = 'jerarquia'))))");
		
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList WHERE "
				+ "agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT "
				+ "idunitList FROM unitList WHERE unitName = 'jerarquia2'))))");

		dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
				"VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),'permisoAdquirirRol', (SELECT iddeontic FROM deontic WHERE deonticdesc = 'p'), (SELECT idtargetType FROM " +
				"targetType WHERE targetName = 'positionName'), (SELECT idposition FROM position WHERE positionName = 'subordinate'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'acquireRole' AND numParams = 3), '@permisoAdquirirRol[p,<positionName:subordinate>,acquireRole(Rol,jerarquia2,Agent),isRole(Rol,jerarquia2) & hasPosition(Rol, jerarquia2,supervisor) & isAgent(Agent) & playsRole(Agent,Rolplayed,jerarquia2) & isUnit(jerarquia2) & " +
				"hasType(jerarquia2,hierarchy) & isRole(Rolplayed,jerarquia) & hasPosition(Rolplayed,jerarquia2,subordinate),playsRole(Agent,Rol,jerarquia2)]'," +
				"'acquireRole(Rol,jerarquia2,Agent):-isRole(Rol,jerarquia2) & hasPosition(Rol,jerarquia2,supervisor) & isAgent(Agent) & playsRole(Agent,Rolplayed,jerarquia2) & isUnit(jerarquia2) & hasType(jerarquia2,hierarchy) & isRole(Rolplayed,jerarquia) & hasPosition(Rolplayed, jerarquia2,subordinate) & not(playsRole(Agent,Rol,jerarquia2))')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'permisoAdquirirRol'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'acquireRole' AND numParams = 3), 'Rol')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'permisoAdquirirRol'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'acquireRole' AND numParams = 3), 'jerarquia2')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'permisoAdquirirRol'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'acquireRole' AND numParams = 3), 'Agent')");
		//----------------------------------------------------------------------------------------------------------------//
			
		String result = omsProxy.acquireRole("jefe","jerarquia2");
		assertEquals("The message should be:", "jefe acquired", result);
			
		assertTrue(dbA.executeQuery("SELECT * FROM unitList ul INNER JOIN roleList rl ON (ul.idunitList=rl.idunitList) INNER JOIN agentPlayList apl ON (rl.idroleList=apl.idroleList) INNER JOIN agentList al ON (apl.idagentList=al.idagentList)"
				+ "WHERE ul.unitName = 'jerarquia2' AND rl.roleName = 'jefe' AND al.agentName = 'pruebas'"));
		
	}
	
	@Test(timeout = 5 * 60 * 1000)
	public void testAcquireRolePermissionsDomain4() throws Exception {
		
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
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),"+
				"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
		
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('jerarquia2',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'))");
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('jefe',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),"+
				"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
		
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList WHERE "
				+ "agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT "
				+ "idunitList FROM unitList WHERE unitName = 'jerarquia'))))");
		
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList WHERE "
				+ "agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT "
				+ "idunitList FROM unitList WHERE unitName = 'jerarquia'))))");
		
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList WHERE "
				+ "agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT "
				+ "idunitList FROM unitList WHERE unitName = 'jerarquia2'))))");

		dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
				"VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'jerarquia2'),'permisoAdquirirRol', (SELECT iddeontic FROM deontic WHERE deonticdesc = 'p'), (SELECT idtargetType FROM " +
				"targetType WHERE targetName = 'positionName'), (SELECT idposition FROM position WHERE positionName = 'subordinate'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'acquireRole' AND numParams = 3), '@permisoAdquirirRol[p,<positionName:subordinate>,acquireRole(Rol,jerarquia2,Agent),isRole(Rol,jerarquia2) & hasPosition(Rol, jerarquia2,creator) & " +
				"isAgent(Agent) & playsRole(Agent,Rolplayed,jerarquia2) & isUnit(jerarquia2) & hasType(jerarquia2,hierarchy) & isRole(Rolplayed,jerarquia) & hasPosition(Rolplayed, jerarquia2,subordinate),playsRole(Agent,Rol,jerarquia2)]'," +
				"'acquireRole(Rol,jerarquia2,Agent):-isRole(Rol,jerarquia2) & hasPosition(Rol, jerarquia2,creator) & isAgent(Agent) & playsRole(Agent,Rolplayed,jerarquia2) & isUnit(jerarquia2) & hasType(jerarquia2,hierarchy) & isRole(Rolplayed,jerarquia) & hasPosition(Rolplayed, jerarquia2,subordinate) & not(playsRole(Agent,Rol,jerarquia2))')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'permisoAdquirirRol'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'acquireRole' AND numParams = 3), 'Rol')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'permisoAdquirirRol'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'acquireRole' AND numParams = 3), 'jerarquia2')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'permisoAdquirirRol'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'acquireRole' AND numParams = 3), 'Agent')");
		//----------------------------------------------------------------------------------------------------------------//
			
		String result = omsProxy.acquireRole("creador","jerarquia2");
		assertEquals("The message should be:", "creador acquired", result);
			
		assertTrue(dbA.executeQuery("SELECT * FROM unitList ul INNER JOIN roleList rl ON (ul.idunitList=rl.idunitList) INNER JOIN agentPlayList apl ON (rl.idroleList=apl.idroleList) INNER JOIN agentList al ON (apl.idagentList=al.idagentList)"
				+ "WHERE ul.unitName = 'jerarquia2' AND rl.roleName = 'creador' AND al.agentName = 'pruebas'"));
		
	}
}
