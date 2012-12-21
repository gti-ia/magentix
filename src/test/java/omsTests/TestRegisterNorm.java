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
import es.upv.dsic.gti_ia.organization.exception.EmptyParametersException;
import es.upv.dsic.gti_ia.organization.exception.InvalidDeonticException;
import es.upv.dsic.gti_ia.organization.exception.InvalidExpressionException;
import es.upv.dsic.gti_ia.organization.exception.InvalidIDException;
import es.upv.dsic.gti_ia.organization.exception.InvalidOMSActionException;
import es.upv.dsic.gti_ia.organization.exception.InvalidPositionException;
import es.upv.dsic.gti_ia.organization.exception.InvalidTargetTypeException;
import es.upv.dsic.gti_ia.organization.exception.InvalidTargetValueException;
import es.upv.dsic.gti_ia.organization.exception.NormExistsInUnitException;
import es.upv.dsic.gti_ia.organization.exception.NotInUnitAndNotCreatorException;
import es.upv.dsic.gti_ia.organization.exception.NotValidIdentifierException;
import es.upv.dsic.gti_ia.organization.exception.RoleNotExistsException;
import es.upv.dsic.gti_ia.organization.exception.THOMASException;
import es.upv.dsic.gti_ia.organization.exception.UnitNotExistsException;


public class TestRegisterNorm extends TestCase {

	OMSProxy omsProxy = null;
	DatabaseAccess dbA = null;


	Agent agent = null;

	
	OMS oms = null;
	SF sf = null;
	
	private Connection connection = null;

	public TestRegisterNorm()
	{

	}

	protected void tearDown() throws Exception {




		dbA = null;
		omsProxy = null;

		agent.terminate();
		agent = null;


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
	
	public void testRegisterNorm1()
	{
		try
		{
			/*
			String unit = "jerarquia";
		
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

*/



			String NormaPrueba = "@normaprueba[f,agentName:_,registerUnit(_,_,_,_,_),_,_]";


			String result = omsProxy.registerNorm("Invalida", NormaPrueba);




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
	
	public void testRegisterNorm2()
	{
		Statement st = null;	
		ResultSet res = null;
		
		try
		{

			
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

		
			String NormaPrueba = "@normaprueba[f,<agentName:_>,registerUnit(_,_,_,_,_),_,_]";


			String result = omsProxy.registerNorm("virtual", NormaPrueba);
			
			
			assertEquals("normaprueba created", result);
			
			
			try
			{
				connection = connect();



				st = connection.createStatement();
				res = st.executeQuery("SELECT * FROM normList WHERE normName = 'normaprueba'");

				if (res.next())
				{
					assertEquals("normaprueba", res.getString("normName"));
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
			
						

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testRegisterNorm3()
	{
			
		try
		{

			
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

		
			String NormaPrueba = "@normaprueba[f,<agentName:_>,registerUnit(_,_,_,_,_),_,_]";
			String result = omsProxy.registerNorm("virtual", NormaPrueba);
			
			assertEquals("normaprueba created", result);
			
			result = omsProxy.registerNorm("virtual", NormaPrueba);
			
			fail(result);
			
						
						

		}catch(NormExistsInUnitException e)
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
	
	public void testRegisterNorm4()
	{
			
		Statement st = null;	
		ResultSet res = null;
		
		
		try
		{

			
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

		
			String NormaPrueba = "@normaprueba[f,<agentName:_>,registerUnit(_,_,_,_,_),_,_]";
			String result = omsProxy.registerNorm("virtual", NormaPrueba);
			
			assertEquals("normaprueba created", result);
			
			String NormaPruebaRepitada = "@normapruebaRepetida[f,<agentName:_>,registerUnit(_,_,_,_,_),_,_]";
			result = omsProxy.registerNorm("virtual", NormaPruebaRepitada);
			
			assertEquals("normapruebaRepetida created", result);
			
			
			try
			{
				connection = connect();



				st = connection.createStatement();
				res = st.executeQuery("SELECT * FROM normList");

				if (res.next())
					assertEquals("normaprueba", res.getString("normName"));
				if (res.next())
					assertEquals("normapruebaRepetida", res.getString("normName"));
					
				
				
				

				

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
	
	
	public void testRegisterNorm5()
	{
			
		Statement st = null;	
		ResultSet res = null;
		
		
		try
		{

			
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

		
			String NormaPrueba = "@normaprueba[f,<agentName:_>,registerUnit(_,_,_,_,_),_,_]";
			String result = omsProxy.registerNorm("virtual", NormaPrueba);
			
			assertEquals("normaprueba created", result);
			
			String NormaPruebaRepitada = "@normapruebaRepetida[f,<agentName:_>,registerUnit(_,_,_,_,_),_,_]";
			result = omsProxy.registerNorm("virtual", NormaPruebaRepitada);
			
			assertEquals("normapruebaRepetida created", result);
			
			
			try
			{
				connection = connect();



				st = connection.createStatement();
				res = st.executeQuery("SELECT * FROM normList");

				if (res.next())
					assertEquals("normaprueba", res.getString("normName"));
				if (res.next())
					assertEquals("normapruebaRepetida", res.getString("normName"));
					
				
				
				

				

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
	
	public void testRegisterNorm5a()
	{
			
		try
		{

			
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

		
			String NormaPrueba = "@++normaprueba[f,<agentName:_>,registerUnit(_,_,_,_,_),_,_]";
			String result = omsProxy.registerNorm("virtual", NormaPrueba);
				
			fail(result);
			
						
						

		}catch(InvalidIDException e)
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
	
	public void testRegisterNorm5b()
	{
			
		try
		{

			
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

		
			String NormaPrueba = "@normaprueba[f,<agentName:_>,registerUnit(_,_,_,_,_) = 5,_,_]";
			String result = omsProxy.registerNorm("virtual", NormaPrueba);
				
			fail(result);
			
						
						

		}catch(InvalidExpressionException e)
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
	public void testRegisterNorm5c()
	{
			
		try
		{

			
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

		
			String NormaPrueba = "@normaprueba[a,<agentName:_>,registerUnit(_,_,_,_,_),_,_]";
			String result = omsProxy.registerNorm("virtual", NormaPrueba);
				
			fail(result);
			
						
						

		}catch(InvalidDeonticException e)
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
	
	public void testRegisterNorm5d()
	{
			
		try
		{

			
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

		
			String NormaPrueba = "@normaprueba[f,<InvalidTargetType:_>,registerUnit(_,_,_,_,_),_,_]";
			String result = omsProxy.registerNorm("virtual", NormaPrueba);
				
			fail(result);
			
						
						

		}catch(InvalidTargetTypeException e)
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
	
	public void testRegisterNorm5e()
	{
			
		try
		{

			
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

		
			String NormaPrueba = "@normaprueba[f,<agentName:*>,registerUnit(_,_,_,_,_),_,_]";
			String result = omsProxy.registerNorm("virtual", NormaPrueba);
				
			fail(result);
			
						
						

		}catch(InvalidTargetValueException e)
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
	
	//------------------------------------------------------------
	//-------Restricciones de consistencia o integridad-----------
	
	public void testRegisterNorm6()
	{
			
		try
		{

			
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

		
			String NormaPrueba = "@normaprueba[f,<roleName:rolInvalido>,registerUnit(_,_,_,_,_),_,_]";
			String result = omsProxy.registerNorm("virtual", NormaPrueba);
				
			fail(result);
			
						
						

		}catch(RoleNotExistsException e)
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
	
	public void testRegisterNorm7a()
	{
			
		try
		{

			String unit = "jerarquia";
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

		
			String NormaPrueba = "@normaprueba[f,<positionName:member>,registerUnit(_,_,_,_,_),_,_]";
			String result = omsProxy.registerNorm("jerarquia", NormaPrueba);
				
			fail(result);
			
						
						

		}catch(InvalidPositionException e)
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
	
	public void testRegisterNorm7b()
	{
			
		try
		{

			String unit = "jerarquia";
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
	
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");

			
		
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

		
			
			String NormaPrueba = "@normaprueba[f,<positionName:supervisor>,registerUnit(_,_,_,_,_),_,_]";
			String result = omsProxy.registerNorm("jerarquia", NormaPrueba);
				
			assertEquals("normaprueba created", result);
			
						
						

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testRegisterNorm7c()
	{
			
		try
		{

			String unit = "jerarquia";
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
	
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");

			
		
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

		
			
			String NormaPrueba = "@normaprueba[f,<positionName:creator>,registerUnit(_,_,_,_,_),_,_]";
			String result = omsProxy.registerNorm("jerarquia", NormaPrueba);
				
			assertEquals("normaprueba created", result);
			
						
						

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testRegisterNorm7d()
	{
			
		try
		{

			String unit = "jerarquia";
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
	
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");

			
		
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

		
			
			String NormaPrueba = "@normaprueba[f,<positionName:subordinate>,registerUnit(_,_,_,_,_),_,_]";
			String result = omsProxy.registerNorm("jerarquia", NormaPrueba);
				
			assertEquals("normaprueba created", result);
			
						
						

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testRegisterNorm8a()
	{
			
		try
		{

			String unit = "equipo";
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
	
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");

			
		
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

		
			
			String NormaPrueba = "@normaprueba[f,<positionName:member>,registerUnit(_,_,_,_,_),_,_]";
			String result = omsProxy.registerNorm(unit, NormaPrueba);
				
			assertEquals("normaprueba created", result);
			
						
						

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testRegisterNorm8b()
	{
			
		try
		{

			String unit = "equipo";
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
	
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");

			
		
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

		
			
			String NormaPrueba = "@normaprueba[f,<positionName:creator>,registerUnit(_,_,_,_,_),_,_]";
			String result = omsProxy.registerNorm(unit, NormaPrueba);
				
			assertEquals("normaprueba created", result);
			
						
						

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testRegisterNorm8c()
	{
			
		try
		{

			String unit = "equipo";
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
	
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");

			
		
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

		
		
			String NormaPrueba = "@normaprueba[f,<positionName:supervisor>,registerUnit(_,_,_,_,_),_,_]";
			String result = omsProxy.registerNorm(unit, NormaPrueba);
				
			fail(result);
			
						
						

		}catch(InvalidPositionException e)
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
	
	public void testRegisterNorm8d()
	{
			
		try
		{

			String unit = "equipo";
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
	
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");

			
		
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

		
		
			String NormaPrueba = "@normaprueba[f,<positionName:subordinate>,registerUnit(_,_,_,_,_),_,_]";
			String result = omsProxy.registerNorm(unit, NormaPrueba);
				
			fail(result);
			
						
						

		}catch(InvalidPositionException e)
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
	
	public void testRegisterNorm9a()
	{
			
		try
		{

			String unit = "plana";
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
	
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");

			
		
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

		
			
			String NormaPrueba = "@normaprueba[f,<positionName:member>,registerUnit(_,_,_,_,_),_,_]";
			String result = omsProxy.registerNorm(unit, NormaPrueba);
				
			assertEquals("normaprueba created", result);
			
						
						

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testRegisterNorm9b()
	{
			
		try
		{

			String unit = "plana";
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
	
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");

			
		
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

		
			
			String NormaPrueba = "@normaprueba[f,<positionName:creator>,registerUnit(_,_,_,_,_),_,_]";
			String result = omsProxy.registerNorm(unit, NormaPrueba);
				
			assertEquals("normaprueba created", result);
			
						
						

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testRegisterNorm9c()
	{
			
		try
		{

			String unit = "plana";
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
	
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");

			
		
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

		
		
			String NormaPrueba = "@normaprueba[f,<positionName:supervisor>,registerUnit(_,_,_,_,_),_,_]";
			String result = omsProxy.registerNorm(unit, NormaPrueba);
				
			fail(result);
			
						
						

		}catch(InvalidPositionException e)
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
	
	public void testRegisterNorm9d()
	{
			
		try
		{

			String unit = "plana";
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
	
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");

			
		
			
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

		
		
			String NormaPrueba = "@normaprueba[f,<positionName:subordinate>,registerUnit(_,_,_,_,_),_,_]";
			String result = omsProxy.registerNorm(unit, NormaPrueba);
				
			fail(result);
			
						
						

		}catch(InvalidPositionException e)
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
	
	public void testRegisterNorm10()
	{
	
		Statement st = null;	
		ResultSet res = null;
		
		try
		{

			String unit = "virtual";
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
				
					
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

		
		
			
			String NormaPrueba = "@normaprueba[f,<agentName:inexistente>,registerUnit(_,_,_,_,_),_,_]";
			String result = omsProxy.registerNorm(unit, NormaPrueba);
				
			assertEquals("normaprueba created", result);
			
			
			try
			{
				connection = connect();



				st = connection.createStatement();
				res = st.executeQuery("SELECT * FROM agentList WHERE agentName = 'inexistente'");

				if (res.next())
				{
					assertEquals("inexistente", res.getString("agentName"));
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
			
						
						

		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	
	public void testRegisterNorm11()
	{
	
		
		try
		{

			String unit = "virtual";
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
				
					
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

		
		
			
			String NormaPrueba = "@normaprueba[f,<agentName:inexistente>,registerService(_,_,_,_,_),_,_]";
			String result = omsProxy.registerNorm(unit, NormaPrueba);
				
			fail(result);
			
			
		}				
		catch(InvalidOMSActionException e)
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
	
	
	// Comprobación de normas estructurales
	
	public void testRegisterNorm12()
	{
	
		
		try
		{

			String unit = "plana";
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
	
//			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
//			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))))");

			
			
			String NormaPrueba = "@normaprueba[f,<agentName:_>,registerRole(_,_,_,_,_,_),_,_]";
			String result = omsProxy.registerNorm(unit, NormaPrueba);
				
			fail(result);
			
			
		}				
		catch(NotInUnitAndNotCreatorException e)
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
	
	
	
	
	public void testRegisterNorm13()
	{
	
		
		try
		{

			String unit = "plana";
			
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('pruebas')");
			
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('"+unit+"',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'))");

			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('equipo',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'equipo'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = '"+unit+"'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
			
			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('creador',(SELECT idunitList FROM unitList WHERE unitName = 'equipo'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
	
	
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'pruebas'),(SELECT idroleList FROM roleList WHERE (roleName = 'creador' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'equipo'))))");

			
			
			String NormaPrueba = "@normaprueba[f,<agentName:_>,registerRole(_,_,_,_,_,_),_,_]";
			String result = omsProxy.registerNorm(unit, NormaPrueba);
				
			
			assertNotNull("normaprueba registered",result);
			
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
	
	public void testRegisterNorm14()
	{
	
		
		try
		{

			
			
			
			String NormaPrueba = "@normaprueba[f,<agentName:_>,registerRole(_,_,_,_,_,_),_,_]";
			String result = omsProxy.registerNorm("registerUnit", NormaPrueba);
				
			
			assertNotNull("normaprueba registered",result);
			
		}				
		catch(UnitNotExistsException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	
}
