package omsTests;

import java.util.ArrayList;

import junit.framework.TestCase;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.EmptyParametersException;
import es.upv.dsic.gti_ia.organization.NotPlaysAnyRoleException;
import es.upv.dsic.gti_ia.organization.NotPlaysRoleException;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.PlayingRoleException;
import es.upv.dsic.gti_ia.organization.RoleNotExistsException;
import es.upv.dsic.gti_ia.organization.SameAgentNameException;
import es.upv.dsic.gti_ia.organization.UnitNotExistsException;


public class AcquireRoleInCorrectParamTest extends TestCase {

	OMSProxy omsProxy = null;
	DatabaseAccess dbA = null;


	Agent agent = null;


	public AcquireRoleInCorrectParamTest()
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
		
		
		dbA.executeSQL("INSERT INTO `agentPlayList` (`agentName`, `idroleList`) VALUES"+
		"('pruebas',(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

		

	}

	public void testAcquireRole1()
	{
		try
		{

			String result = omsProxy.acquireRole("participante", "noexiste");
			
			fail(result);

		}catch(UnitNotExistsException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testAcquireRole2()
	{
		try
		{

			String result = omsProxy.acquireRole("participante", "");

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
	
	public void testAcquireRole3()
	{
		try
		{

			String result = omsProxy.acquireRole("participante", null);

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
	
	public void testAcquireRole4()
	{
		try
		{

			String result = omsProxy.acquireRole("noexiste", "virtual");

			fail(result);

		}catch(RoleNotExistsException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testAcquireRole5()
	{
		try
		{

			String result = omsProxy.acquireRole("", "virtual");

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
	
	public void testAcquireRole6()
	{
		try
		{

			String result = omsProxy.acquireRole(null, "virtual");

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
}
