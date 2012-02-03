package es.upv.dsic.gti_ia.organization;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class is responsible for connection and database queries.
 * 
 */
public class DataBaseAcces {

	
	
	
	/** 
	 * 
	 * The connection to the database 
	 * */
	public Connection connection = null;

	/** 
	 * This method sets the connection with the THOMAS database  
	 * */
	public void connect() {
		if (connection != null)
			return;

		try {
			Configuration c = Configuration.getConfiguration();
			//Register a MySQL driver. 
			String driverName = c.getjenadbDriver(); // MySQL MM JDBC
			// driver
			Class.forName(driverName);
			String serverName = c.getdatabaseServer();
			String mydatabase = c.getdatabaseName();
			String url = "jdbc:mysql://" + serverName + "/" + mydatabase; // a
			String username = c.getdatabaseUser();
			String password = c.getdatabasePassword();
			connection = DriverManager.getConnection(url, username, password);
			
			connection.setAutoCommit(false);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

			if (!connection.isClosed())
				System.out.println("Successfully connected to "
						+ "MySQL server using TCP/IP...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method makes the query of the units in the table and returns the corresponding ResultSet.
	 * 
	 * @return The result of the consultation 
	 */
	public ResultSet getUnitList() {
		ResultSet rs = null;
		try {
			// Creates a Statement, to query
			Statement s = connection.createStatement();

			// Creates a query.The results are stored in ResultSet rs
			String sql = "SELECT u1.unitid As UnitID, u1.type as type, u1.goal AS goal,u2.unitid As parentUnit FROM (thomas.unit u1 LEFT joIN thomas.unit u2 ON u1.parentunit=u2.id)";
			sql.toLowerCase();
			rs = s.executeQuery(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}

	/**
	 * This method makes the query of the subunits in the table and returns the corresponding ResultSet.
	 * 
	 * @param idparentunit Id of parent unit.
	 * @return The result of the consultation.
	 */
	public ResultSet getListSubUnits(String idparentunit)
	{
		ResultSet rs = null;
		try
		{
			//Create a Statement, to query
			Statement s = connection.createStatement();

			// Creates a query.The results are stored in ResultSet rs
			String sql="SELECT u.unitid FROM unit u WHERE u.parentunit='"+idparentunit+"'  ";
			sql.toLowerCase();
			rs = s.executeQuery(sql);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return rs;
	}

	/**
	 * This method makes the query of the roles in the table and returns the corresponding ResultSet.
	 * 
	 * @return The result of the consultation
	 */
	public ResultSet getRoleList() {
		ResultSet rs = null;
		try {
			// Se crea un Statement, para realizar la consulta
			Statement s = connection.createStatement();

			// Se realiza la consulta. Los resultados se guardan en el
			// ResultSet rs
			String sql="select r1.RoleID, r1.Position, r1.Accessibility, r1.Visibility, r2.RoleID as Inheritance, un.UnitID from (role r1 left join role r2 on r1.inheritance=r2.id) left join unit un on r1.unit=un.ID";
			sql.toLowerCase();
			rs = s.executeQuery(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}


	/** This method closes the connection with the database  */
	public void closeConnection() {
		try {
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method returns a list of norms in the THOMAS organization
	 * @return The result of the consultation
	 */
	public ResultSet getListNorms() {
		ResultSet rs = null;
		try {
	
			Statement s = connection.createStatement();

			rs = s.executeQuery("select * from norm");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}

	/**
	 * This method returns a list of entity play in the THOMAS organization
	 * @return The result of the consultation
	 */
	public ResultSet getListEntityPlay() {
		ResultSet rs = null;
		try {
			// Se crea un Statement, para realizar la consulta
			Statement s = connection.createStatement();

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
	 * This method returns a list of profile id in the THOMAS organization
	 * @return The result of the consultation
	 */
	public ResultSet getListProfileId() {
		ResultSet rs = null;
		try {
			// Se crea un Statement, para realizar la consulta
			Statement s = connection.createStatement();

			// Se realiza la consulta. Los resultados se guardan en el
			// ResultSet rs

			String sql="SELECT s.serviceprofileid AS profileID,s.profilename AS profile, s.urlprofile AS URL FROM serviceprofileid s";
			sql.toLowerCase();
			rs = s.executeQuery(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}

	/**
	 * This method returns a list of process id in the THOMAS organization with an specific id.
	 * 
	 * @param profileid is a ID profile 
	 * @return The result of the consultation
	 */
	public ResultSet getListProcess(String profileid)
	{
		ResultSet rs = null;
		try
		{

			Statement s = connection.createStatement();


			String sql="SELECT s.serviceprocessid AS processID, s.processname AS process FROM serviceprocessid s WHERE s.serviceprofileid='"+profileid+"'  ";
			sql.toLowerCase();
			rs = s.executeQuery(sql);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return rs;
	}
	/**
	 * This method returns a list of process id in the THOMAS organization
	 *  
	 * @return The result of the consultation
	 */
	public ResultSet getListProcess()
	{
		ResultSet rs = null;
		try
		{
	
			Statement s = connection.createStatement();

		
			String sql="SELECT s.serviceprocessid AS processID, s.processname AS process FROM serviceprocessid s ";
			sql.toLowerCase();
			rs = s.executeQuery(sql);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return rs;
	}

	/**
	 * This method returns a list of specific entity units in the THOMAS organization 
	 * @param unit
	 * @return
	 */
	public ResultSet getUnitEntities(String unit)
	{
		ResultSet rs = null;
		try
		{
			// Se crea un Statement, para realizar la consulta
			Statement s = connection.createStatement();

			// Se realiza la consulta. Los resultados se guardan en el
			// ResultSet rs
			String sql="SELECT e.entityId FROM entityplaylist u join entity e on u.entity = e.id WHERE u.unit='"+unit+"'  ";
			sql.toLowerCase();
			rs = s.executeQuery(sql);

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return rs;
	}

	/**
	 * This method returns a unit id in the THOMAS organization
	 * @param unit name of unit
	 */
	public ResultSet getUnitID(String unit)
	{
		ResultSet rs = null;
		try
		{
			// Se crea un Statement, para realizar la consulta
			Statement s = connection.createStatement();

			// Se realiza la consulta. Los resultados se guardan en el
			// ResultSet rs
			String sql="SELECT u.id FROM unit u WHERE u.unitid='"+unit+"'  ";
			sql.toLowerCase();
			rs = s.executeQuery(sql);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return rs;
	}

	/**
	 * This method returns a ServiceId in the THOMAS organization
	 * @param url url process
	 * @return
	 */
	public ResultSet getServiceID(String url)
	{
		ResultSet rs = null;
		try
		{
			// Se crea un Statement, para realizar la consulta
			Statement s = connection.createStatement();

			// Se realiza la consulta. Los resultados se guardan en el
			// ResultSet rs
			String sql="SELECT u.serviceprofileid FROM serviceprofileid u WHERE u.urlprofile='"+url+"'  ";
			sql.toLowerCase();
			rs = s.executeQuery(sql);

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return rs;
	}


	/**
	 * This method returns Agent Services in the THOMAS organization
	 * 
	 * @param agentID 
	 * @return
	 */
	public String getAgentServices(String agentID) {

		String result = "[";
		try {
			Statement s = connection.createStatement();
			ResultSet rs = s
			.executeQuery("Select * from serviceprocessid where providername='"
					+ agentID.toLowerCase() + "'");
			System.out
			.println("Select * from serviceprocessid where providername='"
					+ agentID.toLowerCase() + "'");
			while (rs.next()) {
				result = result + "(" + rs.getString("processname") + "), ";
			}
			if (!result.equalsIgnoreCase("["))
				result = result.substring(0, result.length() - 2);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return result + "]";

	}

}
