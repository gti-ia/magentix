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
