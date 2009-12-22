package es.upv.dsic.gti_ia.organization;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Class responsible for connection and database queries.
 * 

 */
public class DataBaseAcces {
    
	/** The connection to the database */
	public Connection conection = null;

	/** Establishing the connection with the database  */
	public void connect() {
		if (conection != null)
			return;

		try {
		    	Configuration c = Configuration.getConfiguration();
			// Se registra el Driver de MySQL
			String driverName = c.getjenadbDriver(); // MySQL MM JDBC
															// driver
			Class.forName(driverName);
			// Se obtiene una conexi�n con la base de datos. Hay que
			// cambiar el usuario "usuario" y la clave "" por las
			// adecuadas a la base de datos que estemos usando.
			
			String serverName = c.getdatabaseServer();
			String mydatabase = c.getdatabaseName();
			String url = "jdbc:mysql://" + serverName + "/" + mydatabase; // a
																			// JDBC
																			// url
			String username = c.getdatabaseUser();
			String password = c.getdatabasePassword();
			conection = DriverManager.getConnection(url, username, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Make the query of the units in the table and returns the corresponding ResultSet.
	 * 
	 * @return The result of the consultation 
	 */
	public ResultSet getUnitList() {
		ResultSet rs = null;
		try {
			// Se crea un Statement, para realizar la consulta
			Statement s = conection.createStatement();

			// Se realiza la consulta. Los resultados se guardan en el
			// ResultSet rs
			String sql = "SELECT u1.unitid As UnitID, u1.type as type, u1.goal AS goal,u2.unitid As parentUnit FROM (thomas.unit u1 LEFT joIN thomas.unit u2 ON u1.parentunit=u2.id)";
			sql.toLowerCase();
			rs = s.executeQuery(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}

	/**
	 * Make the query of the roles in the table and returns the corresponding ResultSet.
	 * @return The result of the consultation
	 */
	public ResultSet getRoleList() {
		ResultSet rs = null;
		try {
			// Se crea un Statement, para realizar la consulta
			Statement s = conection.createStatement();

			// Se realiza la consulta. Los resultados se guardan en el
			// ResultSet rs
			String sql = "select r1.RoleID, r1.Position, r1.Accessibility, r1.Visibility, r2.RoleID as Inheritance, un.UnitID from (role r1 left join role r2 on r1.inheritance=r2.id) left join unit un on r1.unit=un.ID";
			sql.toLowerCase();
			rs = s.executeQuery(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}

	/** Closes the connection with the database  */
	public void closeConnection() {
		try {
			conection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Return a list of norms in the Thomas organization
	 * @return The result of the consultation
	 */
	public ResultSet getListNorms() {
		ResultSet rs = null;
		try {
			// Se crea un Statement, para realizar la consulta
			Statement s = conection.createStatement();

			// Se realiza la consulta. Los resultados se guardan en el
			// ResultSet rs
			rs = s.executeQuery("select * from norm");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}

	/**
	 * Return a list of entity play in the Thomas organization
	 * @return The result of the consultation
	 */
	public ResultSet getListEntityPlay() {
		ResultSet rs = null;
		try {
			// Se crea un Statement, para realizar la consulta
			Statement s = conection.createStatement();

			// Se realiza la consulta. Los resultados se guardan en el
			// ResultSet rs
			String sql = "select R.RoleID, U.UnitID, E.EntityID from EntityPLayList EPL, Role R, Unit U, Entity E Where EPL.Role=R.ID AND EPL.Unit=U.ID AND EPL.Entity=E.ID";
			sql = sql.toLowerCase();
			rs = s.executeQuery(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}

	/**
	 * Return a list of profile id in the Thomas organization
	 * @return The result of the consultation
	 */
	public ResultSet getListProfileId() {
		ResultSet rs = null;
		try {
			// Se crea un Statement, para realizar la consulta
			Statement s = conection.createStatement();

			// Se realiza la consulta. Los resultados se guardan en el
			// ResultSet rs
			String sql = "SELECT s.serviceprofileid AS profileID,s.profilename AS profile, p.serviceprocessid AS processID, p.processname AS process FROM (serviceprofileid s LEFT JOIN serviceprocessid p ON s.serviceprofileid=p.serviceprofileid)  ";
			sql.toLowerCase();
			rs = s.executeQuery(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}

}
