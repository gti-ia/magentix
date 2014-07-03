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

/** 
 * @author Jose Alemany Bordera  -  jalemany1@dsic.upv.es
 * 
 */

public class TestAllocateRolePermissionsDomain {

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
		dbA.executeSQL("DELETE FROM actionNormParam");
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
		dbA.executeSQL("DELETE FROM actionNormParam");
		dbA.executeSQL("DELETE FROM normList");
		dbA.executeSQL("DELETE FROM roleList WHERE idroleList != 1");
		dbA.executeSQL("DELETE FROM unitHierarchy WHERE idChildUnit != 1");
		dbA.executeSQL("DELETE FROM unitList WHERE idunitList != 1");

		//--------------------------------------------//

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
		
	}
	
	@Test(timeout = 5 * 60 * 1000)
	public void testAllocateRolePermissionsDomain1() throws Exception {
		
		//------------------------------------------- Test Initialization  -----------------------------------------------//
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('plana',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'plana'))");
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

		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas2')");
		
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList WHERE "
				+ "agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT "
				+ "idunitList FROM unitList WHERE unitName = 'equipo'))))");
		
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList WHERE "
				+ "agentName = 'pruebas2'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT "
				+ "idunitList FROM unitList WHERE unitName = 'equipo'))))");

		dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
				"VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'plana'),'permisoAsignarRol', (SELECT iddeontic FROM deontic WHERE deonticdesc = 'p'), (SELECT idtargetType FROM " +
				"targetType WHERE targetName = 'agentName'),(SELECT idagentList FROM agentList WHERE agentName = 'pruebas'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'allocateRole' AND numParams = 4), '@permisoAsignarRol[p,<agentName:pruebas>,allocateRole(_,plana,_,pruebas),isAgent(pruebas) & playsRole(pruebas,miembro,equipo) & isRole(miembro,equipo) & hasPosition(miembro,equipo,member),_]',"
				+ "'allocateRole(_,plana,_,pruebas):-isAgent(pruebas) & playsRole(pruebas,miembro,equipo) & isRole(miembro,equipo) & hasPosition(miembro,equipo,member)')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'permisoAsignarRol'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'allocateRole' AND numParams = 4), '_')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'permisoAsignarRol'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'allocateRole' AND numParams = 4), 'plana')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'permisoAsignarRol'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'allocateRole' AND numParams = 4), '_')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'permisoAsignarRol'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'allocateRole' AND numParams = 4), 'pruebas')");
		//----------------------------------------------------------------------------------------------------------------//
			
		String result = omsProxy.allocateRole("miembro","plana","pruebas2");
		assertEquals("The message should be:", "miembro acquired", result);
			
		assertTrue(dbA.executeQuery("SELECT * FROM unitList ul INNER JOIN roleList rl ON (ul.idunitList=rl.idunitList) INNER JOIN agentPlayList apl ON (rl.idroleList=apl.idroleList) INNER JOIN agentList al ON (apl.idagentList=al.idagentList)"
				+ "WHERE ul.unitName = 'plana' AND rl.roleName = 'miembro' AND al.agentName = 'pruebas2'"));
		
	}
	
	@Test(timeout = 5 * 60 * 1000)
	public void testAllocateRolePermissionsDomain2() throws Exception {
		
		//------------------------------------------- Test Initialization  -----------------------------------------------//
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('plana',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'plana'))");
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
				+ "idunitList FROM unitList WHERE unitName = 'plana'))))");

		dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
				"VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'equipo'),'permisoAsignarRol', (SELECT iddeontic FROM deontic WHERE deonticdesc = 'p'), (SELECT idtargetType FROM " +
				"targetType WHERE targetName = 'agentName'),-1, (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'allocateRole' AND numParams = 4), '@permisoAsignarRol[p,<agentName:_>,allocateRole(_,equipo,_,pruebas),isAgent(pruebas) & ((playsRole(pruebas,miembro,plana) & isRole(miembro,plana) & hasPosition(miembro,plana,member)) | (playsRole(pruebas,miembro,equipo))),_]',"
				+ "'allocateRole(_,equipo,_,pruebas):-isAgent(pruebas) & ((playsRole(pruebas,miembro,plana) & isRole(miembro,plana) & hasPosition(miembro,plana,member)) | (playsRole(pruebas,miembro,equipo)))')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'permisoAsignarRol'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'allocateRole' AND numParams = 4), '_')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'permisoAsignarRol'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'allocateRole' AND numParams = 4), 'equipo')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'permisoAsignarRol'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'allocateRole' AND numParams = 4), '_')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'permisoAsignarRol'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'allocateRole' AND numParams = 4), 'pruebas')");
		//----------------------------------------------------------------------------------------------------------------//
			
		String result = omsProxy.allocateRole("miembro","equipo","pruebas2");
		assertEquals("The message should be:", "miembro acquired", result);
			
		assertTrue(dbA.executeQuery("SELECT * FROM unitList ul INNER JOIN roleList rl ON (ul.idunitList=rl.idunitList) INNER JOIN agentPlayList apl ON (rl.idroleList=apl.idroleList) INNER JOIN agentList al ON (apl.idagentList=al.idagentList)"
				+ "WHERE ul.unitName = 'equipo' AND rl.roleName = 'miembro' AND al.agentName = 'pruebas2'"));
		
	}
	
	@Test(timeout = 5 * 60 * 1000)
	public void testAllocateRolePermissionsDomain3() throws Exception {
		
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
		
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList WHERE "
				+ "agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT "
				+ "idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

		dbA.executeSQL("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionNorm, normContent, normRule) " +
				"VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'),'permisoAsignarRol', (SELECT iddeontic FROM deontic WHERE deonticdesc = 'p'), (SELECT idtargetType FROM " +
				"targetType WHERE targetName = 'positionName'), (SELECT idposition FROM position WHERE positionName = 'subordinate'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'allocateRole' AND numParams = 4), '@permisoAsignarRol[p,<positionName:subordinate>,allocateRole(_,jerarquia,_,Agent),isAgent(Agent) & playsRole(Agent,subordinado,jerarquia) & isRole(subordinado,jerarquia) & hasPosition(subordinado,jerarquia,subordinate),_]',"
				+ "'allocateRole(_,jerarquia,_,Agent):-isAgent(Agent) & playsRole(Agent,subordinado,jerarquia) & isRole(subordinado,jerarquia) & hasPosition(subordinado,jerarquia,subordinate)')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'permisoAsignarRol'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'allocateRole' AND numParams = 4), '_')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'permisoAsignarRol'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'allocateRole' AND numParams = 4), 'jerarquia')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'permisoAsignarRol'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'allocateRole' AND numParams = 4), '_')");
		dbA.executeSQL("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName = 'permisoAsignarRol'), (SELECT idactionNorm FROM actionNorm WHERE description = " +
				"'allocateRole' AND numParams = 4), 'Agent')");
		//----------------------------------------------------------------------------------------------------------------//
			
		String result = omsProxy.allocateRole("subordinado","jerarquia","pruebas2");
		assertEquals("The message should be:", "subordinado acquired", result);
			
		assertTrue(dbA.executeQuery("SELECT * FROM unitList ul INNER JOIN roleList rl ON (ul.idunitList=rl.idunitList) INNER JOIN agentPlayList apl ON (rl.idroleList=apl.idroleList) INNER JOIN agentList al ON (apl.idagentList=al.idagentList)"
				+ "WHERE ul.unitName = 'jerarquia' AND rl.roleName = 'subordinado' AND al.agentName = 'pruebas2'"));
		
	}
}
