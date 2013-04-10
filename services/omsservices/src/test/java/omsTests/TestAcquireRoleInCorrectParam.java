package omsTests;

import persistence.OMSInterface;
import junit.framework.TestCase;



public class TestAcquireRoleInCorrectParam extends TestCase {

	DatabaseAccess dbA = null;
	OMSInterface omsInterface = null;
	ResponseParser responseParser = null;

	
	public TestAcquireRoleInCorrectParam()
	{

	}

	protected void tearDown() throws Exception {




		dbA = null;
	
		omsInterface = null;

	
	}
	protected void setUp() throws Exception {
		super.setUp();

		omsInterface = new OMSInterface();
	
		dbA = new DatabaseAccess();
		responseParser = new ResponseParser();

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

	public void testAcquireRole1()
	{
		try
		{

			String result = omsInterface.acquireRole("participante", "noexiste", "pruebas");
			responseParser.parseResponse(result);
			String description = responseParser.getDescription();
			assertEquals("El mensaje debe ser el siguiente:", "Unit noexiste does not exist.", description);

			

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

			String result = omsInterface.acquireRole("participante", "", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El mensaje debe ser el siguiente:", "Invalid. Empty parameters.", responseParser.getDescription());

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

			String result = omsInterface.acquireRole("participante", null, "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El mensaje debe ser el siguiente:", "Invalid. Empty parameters.", responseParser.getDescription());

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

			String result = omsInterface.acquireRole("noexiste", "virtual", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El mensaje debe ser el siguiente:", "noexiste does not exist in unit virtual", responseParser.getDescription());

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

			String result = omsInterface.acquireRole("", "virtual", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El mensaje debe ser el siguiente:", "Invalid. Empty parameters.", responseParser.getDescription());

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

			String result = omsInterface.acquireRole(null, "virtual", "pruebas");
			responseParser.parseResponse(result);
			assertEquals("El mensaje debe ser el siguiente:", "Invalid. Empty parameters.", responseParser.getDescription());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}
}
