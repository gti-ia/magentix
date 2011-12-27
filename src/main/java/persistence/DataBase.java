package persistence;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Clase encargada de conexion y consultas a base de datos.
 * 
 * @author Chuidiang
 * 
 */
public class DataBase {
	/** La conexion con la base de datos */
	public Connection connection = null;

	/** Se establece la conexion con la base de datos */
	public DataBase() {
		if (connection != null)
			return;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection = DriverManager.getConnection("jdbc:mysql://localhost/thomas2", "root", "potallos");
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