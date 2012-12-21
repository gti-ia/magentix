package omsTests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.TestCase;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.Configuration;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.SF;
import es.upv.dsic.gti_ia.organization.exception.AgentNotInUnitException;
import es.upv.dsic.gti_ia.organization.exception.NormNotExistsException;
import es.upv.dsic.gti_ia.organization.exception.NotInUnitAndNotCreatorException;
import es.upv.dsic.gti_ia.organization.exception.NotSupervisorOrCreatorInUnitException;
import es.upv.dsic.gti_ia.organization.exception.THOMASException;
import es.upv.dsic.gti_ia.organization.exception.UnitNotExistsException;


public class TestDeregisterNorm extends TestCase {

	OMSProxy omsProxy = null;
	OMSProxy omsProxyCreator = null;
	DatabaseAccess dbA = null;


	Agent agent = null;
	Agent agentCreador = null;



	OMS oms = null;
	SF sf = null;

	private Connection connection = null;

	public TestDeregisterNorm()
	{

	}

	protected void tearDown() throws Exception {




		dbA = null;
		omsProxy = null;

		agent.terminate();
		agent = null;
		
		agentCreador.terminate();
		agentCreador = null;


		oms.terminate();
		sf.terminate();

		oms = null;
		sf = null;


	}
	protected void setUp() throws Exception {
		super.setUp();


		AgentsConnection.connect();


		oms = new OMS(new AgentID("OMS"));

		sf =  new SF(new AgentID("SF"));

		oms.start();
		sf.start();


		agent = new Agent(new AgentID("pruebas"));
		agentCreador = new Agent(new AgentID("creador"));


		omsProxyCreator = new OMSProxy(agentCreador);
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



	}

	/** 
	 * This method sets the connection with the THOMAS database  
	 * */
	public Connection connect() {
		Configuration c = null;
		try {


			c = Configuration.getConfiguration();
			//Register a MySQL driver. 
			String driverName = c.getjenadbDriver(); // MySQL MM JDBC
			// driver
			Class.forName(driverName).newInstance();

			String serverName = c.getdatabaseServer();
			String mydatabase = c.getdatabaseName();
			String url = "jdbc:mysql://" + serverName + "/" + mydatabase; // a
			String username = c.getdatabaseUser();
			String password = c.getdatabasePassword();

			connection = DriverManager.getConnection(url, username, password);
			connection.setAutoCommit(true);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);


			return connection;



		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		finally{
			c=null;
		}
	}

	public void testDeregisterNorm1()
	{
		try
		{


			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			String NormaPrueba = "@normaprueba[f,<agentName:_>,registerRole(_,_,_,_,_,_),_,_]";


			omsProxy.registerNorm("virtual", NormaPrueba);

			String result = omsProxy.deregisterNorm("normaPrueba","Invalida");

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

	public void testDeregisterNorm2()
	{
		try
		{


			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			String NormaPrueba = "@normaprueba[f,<agentName:_>,registerRole(_,_,_,_,_,_),_,_]";


			omsProxy.registerNorm("virtual", NormaPrueba);

			String result = omsProxy.deregisterNorm("normaPruebaInexistente","virtual");

			fail(result);



		}catch(NormNotExistsException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	public void testDeregisterNorm3()
	{
		try
		{


			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			String NormaPrueba = "@normaprueba[f,<agentName:_>,registerRole(_,_,_,_,_,_),_,_]";


			omsProxy.registerNorm("virtual", NormaPrueba);

			String result = omsProxy.deregisterNorm("normaPrueba","virtual");

			assertEquals("normaPrueba deleted", result);



		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	// Comprobación de normas estructurales.

	public void testDeregisterNorm4()
	{
		try
		{


			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			String NormaPrueba = "@normaprueba[f,<agentName:_>,registerRole(_,_,_,_,_,_),_,_]";


			omsProxy.registerNorm("virtual", NormaPrueba);

			String result = omsProxy.deregisterNorm("normaPrueba","virtual");

			assertEquals("normaPrueba deleted", result);



		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	public void testDeregisterNorm5()
	{

		try
		{

			String unit = "plana";

			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");

			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('creador')");

			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");

			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");




			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'creador'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");



			String NormaPrueba = "@normaprueba[f,<positionName:member>,registerRole(_,_,_,_,_,_),_,_]";
			String result = omsProxyCreator.registerNorm(unit, NormaPrueba);

			assertEquals("normaprueba created", result);

			result = omsProxy.deregisterNorm("normaprueba",unit);

			fail(result);


		}
		catch(NotInUnitAndNotCreatorException e)
		{

			assertNotNull(e);


		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testDeregisterNorm6()
	{
		Statement st = null;	
		ResultSet res = null;
		
		try
		{

			String unit = "plana";

			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");

			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('creador')");

			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");

			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('equipo',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");

			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'equipo'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

			

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'creador'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");



			String NormaPrueba = "@normaprueba[f,<positionName:member>,registerRole(_,_,_,_,_,_),_,_]";
			String result = omsProxyCreator.registerNorm(unit, NormaPrueba);

			assertEquals("normaprueba created", result);

			result = omsProxy.deregisterNorm("normaprueba",unit);

			assertEquals("normaprueba deleted", result);
			
			
			try
			{
				connection = connect();



				st = connection.createStatement();
				res = st.executeQuery("SELECT * FROM normList WHERE normName = 'normaprueba'");

				if (res.next())
				{
					fail();
				}
					
				

			}
			catch(SQLException e)
			{
				throw e;

			}
			finally
			{
				if (connection != null)
				{
					connection.close();
					connection=null;
				}
				if (st != null)
				{
					st.close();
					st=null;
					
					res.close();
					res = null;
				}
			}


		}
		catch(NotInUnitAndNotCreatorException e)
		{

			fail(e.getMessage());


		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	
	public void testDeregisterNorm7()
	{

		try
		{

			String unit = "equipo";

			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");

			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('creador')");

			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");

			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");




			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'creador'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");



			String NormaPrueba = "@normaprueba[f,<positionName:member>,registerRole(_,_,_,_,_,_),_,_]";
			String result = omsProxyCreator.registerNorm(unit, NormaPrueba);

			assertEquals("normaprueba created", result);

			result = omsProxy.deregisterNorm("normaprueba",unit);

			fail(result);


		}
		catch(AgentNotInUnitException e)
		{

			assertNotNull(e);


		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testDeregisterNorm8()
	{
		Statement st = null;	
		ResultSet res = null;

		try
		{

			String unit = "equipo";

			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");

			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('creador')");

			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");

			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('miembro',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'miembro' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'creador'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");



			String NormaPrueba = "@normaprueba[f,<positionName:member>,registerRole(_,_,_,_,_,_),_,_]";
			String result = omsProxyCreator.registerNorm(unit, NormaPrueba);

			assertEquals("normaprueba created", result);

			result = omsProxy.deregisterNorm("normaprueba",unit);

			
			assertEquals("normaprueba deleted", result);
			try
			{
				connection = connect();



				st = connection.createStatement();
				res = st.executeQuery("SELECT * FROM normList WHERE normName = 'normaprueba'");

				if (res.next())
				{
					fail();
				}
					
				

			}
			catch(SQLException e)
			{
				throw e;

			}
			finally
			{
				if (connection != null)
				{
					connection.close();
					connection=null;
				}
				if (st != null)
				{
					st.close();
					st=null;
					
					res.close();
					res = null;
				}
			}


		}
		catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testDeregisterNorm9()
	{

		Statement st = null;	
		ResultSet res = null;
		
		try
		{

			String unit = "jerarquia";

			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");

			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('creador')");

			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");

			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('supervisor',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'supervisor' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'creador'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");



			String NormaPrueba = "@normaprueba[f,<agentName:_>,registerRole(_,_,_,_,_,_),_,_]";
			String result = omsProxyCreator.registerNorm(unit, NormaPrueba);

			assertEquals("normaprueba created", result);

			result = omsProxy.deregisterNorm("normaprueba",unit);

			assertEquals("normaprueba deleted", result);
			try
			{
				connection = connect();



				st = connection.createStatement();
				res = st.executeQuery("SELECT * FROM normList WHERE normName = 'normaprueba'");

				if (res.next())
				{
					fail();
				}
					
				

			}
			catch(SQLException e)
			{
				throw e;

			}
			finally
			{
				if (connection != null)
				{
					connection.close();
					connection=null;
				}
				if (st != null)
				{
					st.close();
					st=null;
					
					res.close();
					res = null;
				}
			}



		}
		catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testDeregisterNorm10()
	{

		Statement st = null;	
		ResultSet res = null;
		
		try
		{

			String unit = "jerarquia";

			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");

			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('creador')");

			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");

			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");




			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'creador'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");



			String NormaPrueba = "@normaprueba[f,<agentName:_>,registerRole(_,_,_,_,_,_),_,_]";
			String result = omsProxyCreator.registerNorm(unit, NormaPrueba);

			assertEquals("normaprueba created", result);

			result = omsProxy.deregisterNorm("normaprueba",unit);

			assertEquals("normaprueba deleted", result);
			try
			{
				connection = connect();



				st = connection.createStatement();
				res = st.executeQuery("SELECT * FROM normList WHERE normName = 'normaprueba'");

				if (res.next())
				{
					fail();
				}
					
				

			}
			catch(SQLException e)
			{
				throw e;

			}
			finally
			{
				if (connection != null)
				{
					connection.close();
					connection=null;
				}
				if (st != null)
				{
					st.close();
					st=null;
					
					res.close();
					res = null;
				}
			}



		}
		catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testDeregisterNorm11()
	{

		
		try
		{

			String unit = "jerarquia";

			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");

			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('creador')");

			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");

			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('subordinado',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'subordinado' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");

			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'creador'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'jerarquia'))))");



			String NormaPrueba = "@normaprueba[f,<agentName:_>,registerRole(_,_,_,_,_,_),_,_]";
			String result = omsProxyCreator.registerNorm(unit, NormaPrueba);

			assertEquals("normaprueba created", result);

			result = omsProxy.deregisterNorm("normaprueba",unit);

			fail(result);
			



		}
		catch(NotSupervisorOrCreatorInUnitException e)
		{

			assertNotNull(e);

		}
		catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testDeregisterNorm14()
	{
	
		
		try
		{

			String result = omsProxy.deregisterNorm("normaprueba", "registerUnit");
				
			
			fail(result);
			
		}				
		catch(UnitNotExistsException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
}
