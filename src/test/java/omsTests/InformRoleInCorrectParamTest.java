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


public class InformRoleInCorrectParamTest extends TestCase {

	OMSProxy omsProxy = null;
	DatabaseAccess dbA = null;


	Agent agent = null;


	public InformRoleInCorrectParamTest()
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

	public void testInformRole1()
	{
		try
		{

			ArrayList<String> result = omsProxy.informRole("participante","noexiste");

			fail();

		}catch(UnitNotExistsException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testInformRole2()
	{
		try
		{

			ArrayList<String> result = omsProxy.informRole("participante","");

			fail();

		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testInformRole3()
	{
		try
		{

			ArrayList<String> result = omsProxy.informRole("participante",null);

			fail();

		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testInformRole4()
	{
		try
		{

			ArrayList<String> result = omsProxy.informRole("noexiste","virtual");

			fail();

		}catch(RoleNotExistsException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testInformRole5()
	{
		try
		{

			ArrayList<String> result = omsProxy.informRole("","virtual");

			fail();

		}catch(EmptyParametersException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
	
	public void testInformRole6()
	{
		try
		{

			ArrayList<String> result = omsProxy.informRole(null,"virtual");

			fail();

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
