package omsTests;

import junit.framework.TestCase;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.EmptyParametersException;
import es.upv.dsic.gti_ia.organization.InvalidUnitTypeException;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.ParentUnitNotExistsException;
import es.upv.dsic.gti_ia.organization.THOMASException;


public class RegisterUnitInCorrectParamTest extends TestCase {

	OMSProxy omsProxy = null;
	DatabaseAccess dbA = null;


	Agent agent = null;


	public RegisterUnitInCorrectParamTest()
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




	}

	public void testRegisterUnit1()
	{
		try
		{

			String result = omsProxy.registerUnit("Plana", "hierarchy", "inexistente", "creador");

			assertNull(result);

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

			assertNull(result);
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

			assertNull(result);
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

			String result = omsProxy.registerUnit("Plana", "hierarchy", "inexistente", "");

			assertNull(result);

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
			String result = omsProxy.registerUnit("Equipo", "hierarchy", "inexistente", "");

			assertNull(result);
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
			String result = omsProxy.registerUnit("Jerarquia", "hierarchy", "inexistente", "");

			assertNull(result);
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

			String result = omsProxy.registerUnit("Plana", "hierarchy", "inexistente", null);

			assertNull(result);

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
			String result = omsProxy.registerUnit("Equipo", "hierarchy", "inexistente", null);

			assertNull(result);
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
			String result = omsProxy.registerUnit("Jerarquia", "hierarchy", "inexistente", null);

			assertNull(result);
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

			assertNull(result);

		}catch(InvalidUnitTypeException e)
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

			assertNull(result);
		}catch(InvalidUnitTypeException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			String result = omsProxy.registerUnit("Jerarquía", "insexistente", "virtual","Creador");

			assertNull(result);
		}catch(InvalidUnitTypeException e)
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

			assertNull(result);

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
			String result = omsProxy.registerUnit("Equipo", null, "virtual","Creador");

			assertNull(result);
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
			String result = omsProxy.registerUnit("Equipo", null, "virtual","Creador");

			assertNull(result);
		}catch(EmptyParametersException e)
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

			assertNull(result);

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

			assertNull(result);
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

			assertNull(result);
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

			assertNull(result);

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

			assertNull(result);
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

			assertNull(result);
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
