package es.upv.dsic.gti_ia.organization;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 * This class is responsible for connection and database queries.
 * 
 */
public class DataBaseAccess {

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
			connection.setAutoCommit(false);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

			return connection;



		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	protected void finalize() {
		try {
			if (connection != null || !connection.isClosed())
				connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
