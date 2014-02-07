package TestOMS;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import junit.framework.TestCase;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.SF;
import es.upv.dsic.gti_ia.organization.exception.EmptyParametersException;
import es.upv.dsic.gti_ia.organization.exception.MySQLException;
import es.upv.dsic.gti_ia.organization.exception.NotValidIdentifierException;
import es.upv.dsic.gti_ia.organization.exception.ParentUnitNotExistsException;


public class TestRegisterUnitInCorrectParam extends TestCase {

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
		qpidManager.UnixQpidManager.stopQpid(qpid_broker);
	}
	protected void setUp() throws Exception {
		super.setUp();

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

	public void testRegisterUnit1()
	{
		try
		{

			String result = omsProxy.registerUnit("Plana", "hierarchy", "inexistente", "creador");

			fail(result);

		}catch(ParentUnitNotExistsException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.registerUnit("Equipo", "hierarchy", "inexistente", "creador");

			fail(result);
		}catch(ParentUnitNotExistsException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.registerUnit("Jerarquia", "hierarchy", "inexistente", "creador");

			fail(result);
		}catch(ParentUnitNotExistsException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}


	public void testRegisterUnit2()
	{
		try
		{

			String result = omsProxy.registerUnit("Plana", "flat", "virtual", "");

			fail(result);

		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.registerUnit("Equipo", "team", "virtual", "");

			fail(result);
		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.registerUnit("Jerarquia", "hierarchy", "virtual", "");

			fail(result);
		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}



		try
		{

			String result = omsProxy.registerUnit("Plana", "flat", "virtual", null);

			fail(result);

		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.registerUnit("Equipo", "team", "virtual", null);

			fail(result);
		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.registerUnit("Jerarquia", "hierarchy", "virtual", null);

			fail(result);
		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testRegisterUnit3()
	{
		try
		{

			String result = omsProxy.registerUnit("Plana", "insexistente", "virtual","Creador");

			fail(result);

		}catch(MySQLException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.registerUnit("Equipo", "insexistente", "virtual","Creador");

			fail(result);
		}catch(MySQLException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.registerUnit("Jerarquia", "insexistente", "virtual","Creador");

			fail(result);
		}catch(MySQLException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}



		try
		{

			String result = omsProxy.registerUnit("Plana", null, "virtual","Creador");

			fail(result);

		}catch(MySQLException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.registerUnit("Equipo", null, "virtual","Creador");

			fail(result);
		}catch(MySQLException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.registerUnit("Equipo", null, "virtual","Creador");

			fail(result);
		}catch(MySQLException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testRegisterUnit4()
	{
		try
		{

			String result = omsProxy.registerUnit("", "flat", "virtual","Creador");

			fail(result);

		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.registerUnit("", "team", "virtual","Creador");

			fail(result);
		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.registerUnit("", "hierarchy", "virtual","Creador");

			fail(result);
		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}



		try
		{

			String result = omsProxy.registerUnit(null, "flat", "virtual","Creador");

			fail(result);

		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.registerUnit(null, "team", "virtual","Creador");

			fail(result);
		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.registerUnit(null, "hierarchy", "virtual","Creador");

			fail(result);
		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testRegisterUnit5()
	{
		try
		{

			String result = omsProxy.registerUnit("**Miunidad", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit6()
	{
		try
		{

			String result = omsProxy.registerUnit("team", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit7()
	{
		try
		{

			String result = omsProxy.registerUnit("flat", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit8()
	{
		try
		{

			String result = omsProxy.registerUnit("hierarchy", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit9()
	{
		try
		{

			String result = omsProxy.registerUnit("supervisor", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit10()
	{
		try
		{

			String result = omsProxy.registerUnit("subordinate", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit11()
	{
		try
		{

			String result = omsProxy.registerUnit("member", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit12()
	{
		try
		{

			String result = omsProxy.registerUnit("creator", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit13()
	{
		try
		{

			String result = omsProxy.registerUnit("private", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit14()
	{
		try
		{

			String result = omsProxy.registerUnit("public", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit15()
	{
		try
		{

			String result = omsProxy.registerUnit("external", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit16()
	{
		try
		{

			String result = omsProxy.registerUnit("internal", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit17()
	{
		try
		{

			String result = omsProxy.registerUnit("registerUnit", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit18()
	{
		try
		{

			String result = omsProxy.registerUnit("deregisterUnit", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit19()
	{
		try
		{

			String result = omsProxy.registerUnit("registerRole", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit20()
	{
		try
		{

			String result = omsProxy.registerUnit("deregisterRole", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}	
	
	public void testRegisterUnit21()
	{
		try
		{

			String result = omsProxy.registerUnit("registerNorm", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testRegisterUnit22()
	{
		try
		{

			String result = omsProxy.registerUnit("deregisterNorm", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}

	public void testRegisterUnit23()
	{
		try
		{

			String result = omsProxy.registerUnit("allocateRole", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit24()
	{
		try
		{

			String result = omsProxy.registerUnit("deallocateRole", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit25()
	{
		try
		{

			String result = omsProxy.registerUnit("joinUnit", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit26()
	{
		try
		{

			String result = omsProxy.registerUnit("informAgentRole", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testRegisterUnit27()
	{
		try
		{

			String result = omsProxy.registerUnit("informMembers", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit28()
	{
		try
		{

			String result = omsProxy.registerUnit("informQuantityMembers", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit29()
	{
		try
		{

			String result = omsProxy.registerUnit("informUnit", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit30()
	{
		try
		{

			String result = omsProxy.registerUnit("informUnitRoles", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit31()
	{
		try
		{

			String result = omsProxy.registerUnit("informTargetNorms", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit32()
	{
		try
		{

			String result = omsProxy.registerUnit("informRole", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit33()
	{
		try
		{

			String result = omsProxy.registerUnit("informNorm", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit34()
	{
		try
		{

			String result = omsProxy.registerUnit("acquireRole", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit35()
	{
		try
		{

			String result = omsProxy.registerUnit("leaveRole", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit36()
	{
		try
		{

			String result = omsProxy.registerUnit("isNorm", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit37()
	{
		try
		{

			String result = omsProxy.registerUnit("hasDeontic", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit38()
	{
		try
		{

			String result = omsProxy.registerUnit("hasTarget", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit39()
	{
		try
		{

			String result = omsProxy.registerUnit("hasAction", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testRegisterUnit40()
	{
		try
		{

			String result = omsProxy.registerUnit("isRole", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit41()
	{
		try
		{

			String result = omsProxy.registerUnit("hasAccessibility", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit42()
	{
		try
		{

			String result = omsProxy.registerUnit("hasVisibility", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit43()
	{
		try
		{

			String result = omsProxy.registerUnit("hasPosition", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit44()
	{
		try
		{

			String result = omsProxy.registerUnit("isUnit", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit45()
	{
		try
		{

			String result = omsProxy.registerUnit("hasType", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit46()
	{
		try
		{

			String result = omsProxy.registerUnit("hasParent", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit47()
	{
		try
		{

			String result = omsProxy.registerUnit("div", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit48()
	{
		try
		{

			String result = omsProxy.registerUnit("mod", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit49()
	{
		try
		{

			String result = omsProxy.registerUnit("not", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit50()
	{
		try
		{

			String result = omsProxy.registerUnit("_", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit51()
	{
		try
		{

			String result = omsProxy.registerUnit("agentName", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit52()
	{
		try
		{

			String result = omsProxy.registerUnit("roleName", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit53()
	{
		try
		{

			String result = omsProxy.registerUnit("positionName", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit54()
	{
		try
		{

			String result = omsProxy.registerUnit("o", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit55()
	{
		try
		{

			String result = omsProxy.registerUnit("f", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit56()
	{
		try
		{

			String result = omsProxy.registerUnit("p", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit57()
	{
		try
		{

			String result = omsProxy.registerUnit("*invalido", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit58()
	{
		try
		{

			String result = omsProxy.registerUnit("+invalido", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit59()
	{
		try
		{

			String result = omsProxy.registerUnit("?invalido", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit60()
	{
		try
		{

			String result = omsProxy.registerUnit("!invalido", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit61()
	{
		try
		{

			String result = omsProxy.registerUnit("invalido!", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit65()
	{
		try
		{

			String result = omsProxy.registerUnit("invalido?", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit66()
	{
		try
		{

			String result = omsProxy.registerUnit("invalido*", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit67()
	{
		try
		{

			String result = omsProxy.registerUnit("invalido+", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit68()
	{
		try
		{

			String result = omsProxy.registerUnit("!invalido", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	public void testRegisterUnit69()
	{
		try
		{

			String result = omsProxy.registerUnit("invalido-invalido", "flat", "virtual","Creador");

			fail(result);

		}catch(NotValidIdentifierException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}

}
