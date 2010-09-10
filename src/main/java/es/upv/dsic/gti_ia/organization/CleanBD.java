package es.upv.dsic.gti_ia.organization;

import java.sql.ResultSet;
import java.sql.Statement;

import com.hp.hpl.jena.db.DBConnection;
import com.hp.hpl.jena.db.IDBConnection;

/**
 * Clean DataBase thomas and remove jena tables.
 */
public class CleanBD {


	
    private Configuration c;

  /**
   * This class clean the entire contents of the tables in the database thomas, including tables that are created within jena, it is important that we make use of this method at the beginning or end of the initialization of our projects, we must be careful configure the settings.xml file with data from the database.
   */
	public void clean_database() {
		try {
			DataBaseAcces bd = new DataBaseAcces();
			bd.connect();
			// Borramos
			Statement s = bd.conection.createStatement();
			s.executeUpdate("Delete from unit where id<>1");
			s = bd.conection.createStatement();
			s.executeUpdate("Delete from role where id<>1");
			s = bd.conection.createStatement();
			s.executeUpdate("Delete from norm");
			s = bd.conection.createStatement();
			s.executeUpdate("Delete from simplerequestnorm");
			s = bd.conection.createStatement();
			s.executeUpdate("Delete from maxcardinalitynorm");
			s = bd.conection.createStatement();
			s.executeUpdate("Delete from incompatibilitynorm");
			s = bd.conection.createStatement();
			s.executeUpdate("Delete from entity");
			s = bd.conection.createStatement();
			s.executeUpdate("Delete from entityplaylist");
			s = bd.conection.createStatement();
			s.executeUpdate("Delete from serviceprofileid");
			s = bd.conection.createStatement();
			s.executeUpdate("Delete from serviceprocessid");
		


			bd.closeConnection();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
/////////////
		////JENA/////
		/////////////
		IDBConnection conn = null;
		
		c = Configuration.getConfiguration();


		// ensure the JDBC driver class is loaded
		try {
			Class.forName(c.getjenadbDriver());
		} catch (Exception e) {
			System.err.println("Failed to load the driver for the database: "+ e.getMessage());
			System.err.println("Have you got the CLASSPATH set correctly?");
		}

		
	
		
		
		

		// Create database connection
		try {
			conn = new DBConnection(c.getjenadbURL(),c.getdatabaseUser(),c.getdatabasePassword(),c.getjenadbType());
			conn.cleanDB();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
