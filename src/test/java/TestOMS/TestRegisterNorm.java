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

public class TestRegisterNorm {

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
	
	@Test
	public void testRegisterNorm1() throws Exception {
		
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
		//----------------------------------------------------------------------------------------------------------------//

		String NormaPrueba = "@permitidoAdquirirRol[p,<roleName:_>,acquireRole(creador,plana,_),_,_]";
		
		String result = omsProxy.registerNorm("plana", NormaPrueba);
		assertEquals("permitidoAdquirirRol created", result);
		
		assertTrue(dbA.executeQuery("SELECT * FROM normList nl INNER JOIN unitList ul ON (nl.idunitList=ul.idunitList) "
				+ "INNER JOIN deontic d ON (nl.iddeontic=d.iddeontic) INNER JOIN targetType tt ON (nl.idtargetType=tt.idtargetType) "
				+ "INNER JOIN actionNorm an ON (nl.idactionNorm=an.idactionNorm) "
				+ "WHERE nl.normName = 'permitidoAdquirirRol' AND ul.unitName = 'plana' AND d.deonticdesc = 'p' AND tt.targetName = 'roleName' AND an.description = 'acquireRole'"));
		
		assertTrue(dbA.executeQuery("SELECT * FROM normList WHERE normName = 'permitidoAdquirirRol' AND targetValue = -1"));
		
		assertTrue(dbA.executeQuery("SELECT * FROM normList WHERE normName = 'permitidoAdquirirRol' AND normContent = '"+ NormaPrueba +"'"));
		
		assertTrue(dbA.executeQuery("SELECT * FROM normList WHERE normName = 'permitidoAdquirirRol' AND normRule = 'acquireRole(creador,plana,_)'"));

		assertTrue(dbA.executeQuery("SELECT * FROM normList nl INNER JOIN actionNorm an ON (nl.idactionNorm=an.idactionNorm) INNER JOIN actionNormParam anp ON (an.idactionNorm=anp.idactionNorm) "
				+ "WHERE nl.normName = 'permitidoAdquirirRol' AND an.description = 'acquireRole' AND anp.value = 'creador'"));
		assertTrue(dbA.executeQuery("SELECT * FROM normList nl INNER JOIN actionNorm an ON (nl.idactionNorm=an.idactionNorm) INNER JOIN actionNormParam anp ON (an.idactionNorm=anp.idactionNorm) "
				+ "WHERE nl.normName = 'permitidoAdquirirRol' AND an.description = 'acquireRole' AND anp.value = 'plana'"));
		assertTrue(dbA.executeQuery("SELECT * FROM normList nl INNER JOIN actionNorm an ON (nl.idactionNorm=an.idactionNorm) INNER JOIN actionNormParam anp ON (an.idactionNorm=anp.idactionNorm) "
				+ "WHERE nl.normName = 'permitidoAdquirirRol' AND an.description = 'acquireRole' AND anp.value = '_'"));
		
	}
	
	@Test
	public void testRegisterNorm2() throws Exception {
		
		//------------------------------------------- Test Initialization  -----------------------------------------------//
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('plana',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'plana'))");
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'plana'),"+
				"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
		
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList WHERE "
				+ "agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT "
				+ "idunitList FROM unitList WHERE unitName = 'plana'))))");
		//----------------------------------------------------------------------------------------------------------------//

		String NormaPrueba = "@prohibidoRegistrarRol[f,<agentName:pruebas>,registerRole(_,plana,_,_,_,pruebas),isAgent(pruebas) & playsRole(pruebas,_,plana),_]";
		
		String result = omsProxy.registerNorm("plana", NormaPrueba);
		assertEquals("prohibidoRegistrarRol created", result);
		
		assertTrue(dbA.executeQuery("SELECT * FROM normList nl INNER JOIN unitList ul ON (nl.idunitList=ul.idunitList) "
				+ "INNER JOIN deontic d ON (nl.iddeontic=d.iddeontic) INNER JOIN targetType tt ON (nl.idtargetType=tt.idtargetType) "
				+ "INNER JOIN actionNorm an ON (nl.idactionNorm=an.idactionNorm) "
				+ "WHERE nl.normName = 'prohibidoRegistrarRol' AND ul.unitName = 'plana' AND d.deonticdesc = 'f' AND tt.targetName = 'agentName' AND an.description = 'registerRole'"));
		
		assertTrue(dbA.executeQuery("SELECT * FROM normList rl INNER JOIN agentList al ON (rl.targetValue=al.idagentList) WHERE rl.normName = 'prohibidoRegistrarRol' AND al.agentName = 'pruebas'"));
		
		assertTrue(dbA.executeQuery("SELECT * FROM normList WHERE normName = 'prohibidoRegistrarRol' AND normContent = '"+ NormaPrueba +"'"));
		
		assertTrue(dbA.executeQuery("SELECT * FROM normList WHERE normName = 'prohibidoRegistrarRol' AND normRule = 'registerRole(_,plana,_,_,_,pruebas):-isAgent(pruebas) & playsRole(pruebas,_,plana)'"));

		assertTrue(dbA.executeQuery("SELECT * FROM normList nl INNER JOIN actionNorm an ON (nl.idactionNorm=an.idactionNorm) INNER JOIN actionNormParam anp ON (an.idactionNorm=anp.idactionNorm) "
				+ "WHERE nl.normName = 'prohibidoRegistrarRol' AND an.description = 'registerRole' AND anp.value = '_'"));
		assertTrue(dbA.executeQuery("SELECT * FROM normList nl INNER JOIN actionNorm an ON (nl.idactionNorm=an.idactionNorm) INNER JOIN actionNormParam anp ON (an.idactionNorm=anp.idactionNorm) "
				+ "WHERE nl.normName = 'prohibidoRegistrarRol' AND an.description = 'registerRole' AND anp.value = 'plana'"));
		assertTrue(dbA.executeQuery("SELECT * FROM normList nl INNER JOIN actionNorm an ON (nl.idactionNorm=an.idactionNorm) INNER JOIN actionNormParam anp ON (an.idactionNorm=anp.idactionNorm) "
				+ "WHERE nl.normName = 'prohibidoRegistrarRol' AND an.description = 'registerRole' AND anp.value = '_'"));
		assertTrue(dbA.executeQuery("SELECT * FROM normList nl INNER JOIN actionNorm an ON (nl.idactionNorm=an.idactionNorm) INNER JOIN actionNormParam anp ON (an.idactionNorm=anp.idactionNorm) "
				+ "WHERE nl.normName = 'prohibidoRegistrarRol' AND an.description = 'registerRole' AND anp.value = '_'"));
		assertTrue(dbA.executeQuery("SELECT * FROM normList nl INNER JOIN actionNorm an ON (nl.idactionNorm=an.idactionNorm) INNER JOIN actionNormParam anp ON (an.idactionNorm=anp.idactionNorm) "
				+ "WHERE nl.normName = 'prohibidoRegistrarRol' AND an.description = 'registerRole' AND anp.value = '_'"));
		assertTrue(dbA.executeQuery("SELECT * FROM normList nl INNER JOIN actionNorm an ON (nl.idactionNorm=an.idactionNorm) INNER JOIN actionNormParam anp ON (an.idactionNorm=anp.idactionNorm) "
				+ "WHERE nl.normName = 'prohibidoRegistrarRol' AND an.description = 'registerRole' AND anp.value = 'pruebas'"));
		
	}
		
	@Test
	public void testRegisterNorm3() throws Exception {
		
		//------------------------------------------- Test Initialization  -----------------------------------------------//
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('equipo',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'equipo'))");
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
				"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
				"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");
		
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES ((SELECT idagentList FROM agentList WHERE "
				+ "agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT "
				+ "idunitList FROM unitList WHERE unitName = 'equipo'))))");
		//----------------------------------------------------------------------------------------------------------------//

		String NormaPrueba = "@permitidoAdquirirRol[p,<roleName:creador>,acquireRole(creador,equipo,Agent),isAgent(Agent) & playsRole(Agent,subordinado,jerarquia) & isUnit(jerarquia) & hasType(jerarquia,hierarchy) & isRole(subordinado,jerarquia),playsRole(Agent,creador,equipo)]";
		
		String result = omsProxy.registerNorm("equipo", NormaPrueba);
		assertEquals("permitidoAdquirirRol created", result);
		
		assertTrue(dbA.executeQuery("SELECT * FROM normList nl INNER JOIN unitList ul ON (nl.idunitList=ul.idunitList) "
				+ "INNER JOIN deontic d ON (nl.iddeontic=d.iddeontic) INNER JOIN targetType tt ON (nl.idtargetType=tt.idtargetType) "
				+ "INNER JOIN actionNorm an ON (nl.idactionNorm=an.idactionNorm) "
				+ "WHERE nl.normName = 'permitidoAdquirirRol' AND ul.unitName = 'equipo' AND d.deonticdesc = 'p' AND tt.targetName = 'roleName' AND an.description = 'acquireRole'"));
		
		assertTrue(dbA.executeQuery("SELECT * FROM normList nl INNER JOIN roleList rl ON (nl.targetValue=rl.idroleList) WHERE nl.normName = 'permitidoAdquirirRol' AND rl.roleName = 'creador'"));
		
		assertTrue(dbA.executeQuery("SELECT * FROM normList WHERE normName = 'permitidoAdquirirRol' AND normContent = '"+ NormaPrueba +"'"));
		
		assertTrue(dbA.executeQuery("SELECT * FROM normList WHERE normName = 'permitidoAdquirirRol' AND normRule = 'acquireRole(creador,equipo,Agent):-isAgent(Agent) & playsRole(Agent,subordinado,jerarquia) & isUnit(jerarquia) & hasType(jerarquia,hierarchy) & isRole(subordinado,jerarquia) & not(playsRole(Agent,creador,equipo))'"));

		assertTrue(dbA.executeQuery("SELECT * FROM normList nl INNER JOIN actionNorm an ON (nl.idactionNorm=an.idactionNorm) INNER JOIN actionNormParam anp ON (an.idactionNorm=anp.idactionNorm) "
				+ "WHERE nl.normName = 'permitidoAdquirirRol' AND an.description = 'acquireRole' AND anp.value = 'creador'"));
		assertTrue(dbA.executeQuery("SELECT * FROM normList nl INNER JOIN actionNorm an ON (nl.idactionNorm=an.idactionNorm) INNER JOIN actionNormParam anp ON (an.idactionNorm=anp.idactionNorm) "
				+ "WHERE nl.normName = 'permitidoAdquirirRol' AND an.description = 'acquireRole' AND anp.value = 'equipo'"));
		assertTrue(dbA.executeQuery("SELECT * FROM normList nl INNER JOIN actionNorm an ON (nl.idactionNorm=an.idactionNorm) INNER JOIN actionNormParam anp ON (an.idactionNorm=anp.idactionNorm) "
				+ "WHERE nl.normName = 'permitidoAdquirirRol' AND an.description = 'acquireRole' AND anp.value = 'Agent'"));
		
	}
	
	@Test
	public void testRegisterNorm4() throws Exception {
		
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
				+ "agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'jefe' AND idunitList = (SELECT "
				+ "idunitList FROM unitList WHERE unitName = 'jerarquia'))))");
		//----------------------------------------------------------------------------------------------------------------//

		String NormaPrueba = "@prohibidoAdquirirRol[f,<roleName:creador>,acquireRole(_,jerarquia,Agent),isAgent(Agent) & playsRole(Agent,creador,jerarquia),_]";
		
		String result = omsProxy.registerNorm("jerarquia", NormaPrueba);
		assertEquals("prohibidoAdquirirRol created", result);
		
		assertTrue(dbA.executeQuery("SELECT * FROM normList nl INNER JOIN unitList ul ON (nl.idunitList=ul.idunitList) "
				+ "INNER JOIN deontic d ON (nl.iddeontic=d.iddeontic) INNER JOIN targetType tt ON (nl.idtargetType=tt.idtargetType) "
				+ "INNER JOIN actionNorm an ON (nl.idactionNorm=an.idactionNorm) "
				+ "WHERE nl.normName = 'prohibidoAdquirirRol' AND ul.unitName = 'jerarquia' AND d.deonticdesc = 'f' AND tt.targetName = 'roleName' AND an.description = 'acquireRole'"));
		
		assertTrue(dbA.executeQuery("SELECT * FROM normList nl INNER JOIN roleList rl ON (nl.targetValue=rl.idroleList) WHERE nl.normName = 'prohibidoAdquirirRol' AND rl.roleName = 'creador'"));
		
		assertTrue(dbA.executeQuery("SELECT * FROM normList WHERE normName = 'prohibidoAdquirirRol' AND normContent = '"+ NormaPrueba +"'"));
		
		assertTrue(dbA.executeQuery("SELECT * FROM normList WHERE normName = 'prohibidoAdquirirRol' AND normRule = 'acquireRole(_,jerarquia,Agent):-isAgent(Agent) & playsRole(Agent,creador,jerarquia)'"));

		assertTrue(dbA.executeQuery("SELECT * FROM normList nl INNER JOIN actionNorm an ON (nl.idactionNorm=an.idactionNorm) INNER JOIN actionNormParam anp ON (an.idactionNorm=anp.idactionNorm) "
				+ "WHERE nl.normName = 'prohibidoAdquirirRol' AND an.description = 'acquireRole' AND anp.value = '_'"));
		assertTrue(dbA.executeQuery("SELECT * FROM normList nl INNER JOIN actionNorm an ON (nl.idactionNorm=an.idactionNorm) INNER JOIN actionNormParam anp ON (an.idactionNorm=anp.idactionNorm) "
				+ "WHERE nl.normName = 'prohibidoAdquirirRol' AND an.description = 'acquireRole' AND anp.value = 'jerarquia'"));
		assertTrue(dbA.executeQuery("SELECT * FROM normList nl INNER JOIN actionNorm an ON (nl.idactionNorm=an.idactionNorm) INNER JOIN actionNormParam anp ON (an.idactionNorm=anp.idactionNorm) "
				+ "WHERE nl.normName = 'prohibidoAdquirirRol' AND an.description = 'acquireRole' AND anp.value = 'Agent'"));
		
	}
}
