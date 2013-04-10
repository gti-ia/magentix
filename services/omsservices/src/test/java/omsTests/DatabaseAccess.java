package omsTests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import persistence.DataBase;



public class DatabaseAccess {


	
	Properties properties = new Properties();

	private Connection connection = null;


	/** 
	 * This method sets the connection with the THOMAS database  
	 * */
	public Connection connect() {
		try {

			

			properties.load(DataBase.class.getResourceAsStream("/"+"oms.properties"));

			Class.forName(properties.getProperty("driverName")).newInstance();
			
			String serverName = properties.getProperty("serverName");
			String mydatabase = properties.getProperty("dataBaseName");
			String url = "jdbc:mysql://" + serverName +  "/" + mydatabase; // a JDBC url
			String username = properties.getProperty("username");
			String password = properties.getProperty("password");
			connection = DriverManager.getConnection(url, username, password);
			connection.setAutoCommit(true);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);



			return connection;
		} catch (Exception e) {
			System.err.println("Exception: " + e.getMessage());
			return null;
		} 
	}


	public void removeJenaTables() throws SQLException{

		Statement st = null;
		Statement stUpdate = null;
		ResultSet res;
		



		Connection connection = null;

		try
		{
			connection = connect();



			st = connection.createStatement();
			res = st.executeQuery("SHOW TABLES FROM thomas LIKE 'jena%'");
			stUpdate = connection.createStatement();


			while(res.next())
			{
				String table = res.getString("Tables_in_thomas (jena%)");
				String sql = "DROP TABLE "+table+"";
				
				stUpdate.executeUpdate(sql);
				
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
			}
		}
	}

	public boolean executeQuery(String sql) throws SQLException{
		Statement st = null;	
		ResultSet res;
		boolean result = false;

		Connection connection = null;

		try
		{
			connection = connect();



			st = connection.createStatement();
			res = st.executeQuery(sql);

			if (res.next())
			{
				result = true;
			}

			return result;

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
			}
		}
	}

	public void executeSQL(String sql) throws SQLException{
		Statement st = null;	


		Connection connection = null;

		try
		{
			connection = connect();



			st = connection.createStatement();
			st.executeUpdate(sql);

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
				connection = null;
			}
			if (st != null)
			{
				st.close();
				st=null;
			}
		}
	}

}
