package testSF;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Properties;

public class DatabaseAccess {

	private Connection connection = null;

	/**
	 * This method sets the connection with the THOMAS database
	 * */
	public Connection connect() {

		Properties properties = new Properties();
		String s_dbURL = "";
		String s_dbUser = "";
		String s_dbPw = "";
		String s_dbType = "";
		String s_dbDriver = "";

		try {

			properties.loadFromXML(DatabaseAccess.class.getResourceAsStream("/"
					+ "THOMASDemoConfiguration.xml"));

			for (Enumeration<Object> e = properties.keys(); e.hasMoreElements();) {
				// Obtain the object
				Object obj = e.nextElement();
				if (obj.toString().equalsIgnoreCase("DB_URL")) {
					s_dbURL = properties.getProperty(obj.toString());
				} else if (obj.toString().equalsIgnoreCase("DB_USER")) {
					s_dbUser = properties.getProperty(obj.toString());
				} else if (obj.toString().equalsIgnoreCase("DB_PASSWD")) {
					s_dbPw = properties.getProperty(obj.toString());
				} else if (obj.toString().equalsIgnoreCase("DB")) {
					s_dbType = properties.getProperty(obj.toString());
				} else if (obj.toString().equalsIgnoreCase("DB_DRIVER")) {
					s_dbDriver = properties.getProperty(obj.toString());
				}
			}

			// driver
			Class.forName(s_dbDriver).newInstance();

			connection = DriverManager.getConnection(s_dbURL, s_dbUser, s_dbPw);
			connection.setAutoCommit(true);
			connection
					.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

			return connection;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public void removeJenaTables() throws SQLException {

		Statement st = null;
		Statement stUpdate = null;
		ResultSet res;

		Connection connection = null;

		try {
			connection = connect();

			st = connection.createStatement();
			res = st.executeQuery("SHOW TABLES FROM thomas LIKE 'jena%'");
			stUpdate = connection.createStatement();

			while (res.next()) {
				String table = res.getString("Tables_in_thomas (jena%)");
				String sql = "DROP TABLE " + table + "";

				stUpdate.executeUpdate(sql);

			}
		} catch (SQLException e) {
			throw e;

		} finally {
			if (connection != null) {
				connection.close();
				connection = null;
			}
			if (st != null) {
				st.close();
				st = null;
			}
		}
	}

	public boolean executeQuery(String sql) throws SQLException {
		Statement st = null;
		ResultSet res;
		boolean result = false;

		Connection connection = null;

		try {
			connection = connect();

			st = connection.createStatement();
			res = st.executeQuery(sql);

			if (res.next()) {
				result = true;
			}

			return result;

		} catch (SQLException e) {
			throw e;

		} finally {
			if (connection != null) {
				connection.close();
				connection = null;
			}
			if (st != null) {
				st.close();
				st = null;
			}
		}
	}

	public void executeSQL(String sql) throws SQLException {
		Statement st = null;

		Connection connection = null;

		try {
			connection = connect();

			st = connection.createStatement();
			st.executeUpdate(sql);

		} catch (SQLException e) {
			throw e;
		} finally {
			if (connection != null) {
				connection.close();
				connection = null;
			}
			if (st != null) {
				st.close();
				st = null;
			}
		}
	}

}
