package persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class DataBase {
	Properties properties = new Properties();

	private Connection connection = null;
	
	
	/** Se establece la conexion con la base de datos */
	public DataBase() {
		
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
	
	public Connection connect()
	{
		try {

			

			properties.load(DataBase.class.getResourceAsStream("/"+"oms.properties"));

			Class.forName(properties.getProperty("driverName")).newInstance();
			
			String serverName = properties.getProperty("serverName");
			String mydatabase = properties.getProperty("dataBaseName");
			String url = "jdbc:mysql://" + serverName +  "/" + mydatabase; // a JDBC url
			String username = properties.getProperty("username");
			String password = properties.getProperty("password");
			connection = DriverManager.getConnection(url, username, password);
			connection.setAutoCommit(false);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

			return connection;
		} catch (Exception e) {
			System.err.println("Exception: " + e.getMessage());
			return null;
		} 
	}
}