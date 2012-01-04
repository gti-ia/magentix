package persistence;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import configuration.Configuration;


/**
 * Clase encargada de conexion y consultas a base de datos.
 * 
 * @author Chuidiang
 * 
 */
public class DataBase {
	/** La conexion con la base de datos */
	public Connection connection = null;
	Properties properties = new Properties();

	/** Se establece la conexion con la base de datos */
	public DataBase() {
		if (connection != null)
			return;
		try {
		
			Class.forName("com.mysql.jdbc.Driver").newInstance();

			properties.load(Configuration.class.getResourceAsStream("/"+"oms.properties"));


			String serverName = properties.getProperty("serverName");
			String mydatabase = properties.getProperty("dataBaseName");
			String url = "jdbc:mysql://" + serverName +  "/" + mydatabase; // a JDBC url
			String username = properties.getProperty("username");
			String password = properties.getProperty("password");
			connection = DriverManager.getConnection(url, username, password);
			connection.setAutoCommit(false);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

			if (!connection.isClosed())
				System.out.println("Successfully connected to "
						+ "MySQL server using TCP/IP...");

		} catch (Exception e) {
			System.err.println("Exception: " + e.getMessage());
		} 
	}
}