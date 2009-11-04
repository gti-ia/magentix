package es.upv.dsic.gti_ia.organization;

/**
 * Javier Abell�n, 31 Mayo 2006
 * 
 * Ejemplo para meter, de forma autom�tica, un ResultSet en un JTable
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Clase encargada de conexi�n y consultas a base de datos.
 * 
 * @author Chuidiang
 * 
 */
public class BaseDeDatos {
	/** La conexion con la base de datos */
	public Connection conexion = null;

	/** Se establece la conexion con la base de datos */
	public void estableceConexion() {
		if (conexion != null)
			return;

		try {
			// Se registra el Driver de MySQL
			String driverName = "com.mysql.jdbc.Driver"; // MySQL MM JDBC
															// driver
			Class.forName(driverName);
			// Se obtiene una conexi�n con la base de datos. Hay que
			// cambiar el usuario "usuario" y la clave "" por las
			// adecuadas a la base de datos que estemos usando.
			Configuration c = new Configuration();
			String serverName = c.serverName;
			String mydatabase = c.databaseName;
			String url = "jdbc:mysql://" + serverName + "/" + mydatabase; // a
																			// JDBC
																			// url
			String username = c.userName;
			String password = c.password;
			conexion = DriverManager.getConnection(url, username, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Realiza la consulta de personas en la tabla y devuelve el ResultSet
	 * correspondiente.
	 * 
	 * @return El resultado de la consulta
	 */
	public ResultSet dameListaUnidades() {
		ResultSet rs = null;
		try {
			// Se crea un Statement, para realizar la consulta
			Statement s = conexion.createStatement();

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

	public ResultSet dameListaRoles() {
		ResultSet rs = null;
		try {
			// Se crea un Statement, para realizar la consulta
			Statement s = conexion.createStatement();

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

	/** Cierra la conexi�n con la base de datos */
	public void cierraConexion() {
		try {
			conexion.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ResultSet dameListaNormas() {
		ResultSet rs = null;
		try {
			// Se crea un Statement, para realizar la consulta
			Statement s = conexion.createStatement();

			// Se realiza la consulta. Los resultados se guardan en el
			// ResultSet rs
			rs = s.executeQuery("select * from norm");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}

	public ResultSet dameListaEntityPlay() {
		ResultSet rs = null;
		try {
			// Se crea un Statement, para realizar la consulta
			Statement s = conexion.createStatement();

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
	 * Realiza la consulta de personas en la tabla y devuelve el ResultSet
	 * correspondiente.
	 * 
	 * @return El resultado de la consulta
	 */
	public ResultSet dameListaProfileId() {
		ResultSet rs = null;
		try {
			// Se crea un Statement, para realizar la consulta
			Statement s = conexion.createStatement();

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
