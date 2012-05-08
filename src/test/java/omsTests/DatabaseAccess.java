package omsTests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import es.upv.dsic.gti_ia.organization.Configuration;

public class DatabaseAccess {

	
	private Connection connection = null;



	/** 
	 * This method sets the connection with the THOMAS database  
	 * */
	public Connection connect() {
		try {


			Configuration c = Configuration.getConfiguration();
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
				connection.close();
			if (st != null)
				st.close();
		}
	}
	
	public void executeSQL(String sql) throws SQLException{
		Statement st = null;	
		int res;
		
		Connection connection = null;

		try
		{
			connection = connect();



			st = connection.createStatement();
			res = st.executeUpdate(sql);
				
		}
		catch(SQLException e)
		{
			throw e;
		}
		finally
		{
			if (connection != null)
				connection.close();
			if (st != null)
				st.close();
		}
	}
	
}
