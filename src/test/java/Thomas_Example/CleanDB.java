package Thomas_Example;


import java.sql.Statement;

import com.hp.hpl.jena.db.DBConnection;
import com.hp.hpl.jena.db.IDBConnection;

import es.upv.dsic.gti_ia.organization.Configuration;
import es.upv.dsic.gti_ia.organization.DataBaseAcces;

import java.sql.ResultSet;

/**
 * Clean DataBase thomas and remove jena tables.
 */
public class CleanDB {


	
    private Configuration c;

  /**
   * This class clean the entire contents of the tables in the database thomas, including tables that are created within jena, it is important that we make use of this method at the beginning or end of the initialization of our projects, we must be careful configure the settings.xml file with data from the database.
   */
	public void initialize_db() {
		try {
			DataBaseAcces bd=new DataBaseAcces();
	        bd.connect(); 
	        //Borramos 
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
            s.executeUpdate("Insert into unit (unitID,type,goal,parentunit) values ('travelagency','team','reservetravel',1)");
            s = bd.conection.createStatement();
            ResultSet rs=s.executeQuery("Select * from unit where unitid='travelagency'");
            rs.next();
            String travelAgencyID=rs.getString("id");
            s.executeUpdate("Insert into role (roleid,accessibility,position,visibility,inheritance,unit) values ('oms','internal','supervisor','private',1,1)");
            s.executeUpdate("Insert into role (roleid,accessibility,position,visibility,inheritance,unit) values ('sf','internal','supervisor','private',1,1)");
            rs = s.executeQuery("Select * from role where roleid='oms'");
            rs.next();
            String omsroleID=rs.getString("id");
            rs = s.executeQuery("Select * from role where roleid='sf'");
            rs.next();
            String sfroleID=rs.getString("id");
            s.executeUpdate("Insert into entity (entityid) values ('oms')");
            s.executeUpdate("Insert into entity (entityid) values ('sf')");
            rs=s.executeQuery("Select * from entity where entityid='oms'");
            rs.next();
            String omsID=rs.getString("id");
            rs=s.executeQuery("Select * from entity where entityid='sf'");
            rs.next();
            String sfID=rs.getString("id");
            s.executeUpdate("Insert into entityplaylist (role,unit,entity) values ("+omsroleID+",1,"+omsID+")");
            s.executeUpdate("Insert into entityplaylist (role,unit,entity) values ("+sfroleID+",1,"+sfID+")"); 
            
            s.executeUpdate("Insert into role (roleid,accessibility,position,visibility,inheritance,unit) values ('provider','external','member','public',1,"+travelAgencyID+")");
            s.executeUpdate("Insert into role (roleid,accessibility,position,visibility,inheritance,unit) values ('customer','external','member','public',1,"+travelAgencyID+")");
            s.executeUpdate("Insert into role (roleid,accessibility,position,visibility,inheritance,unit) values ('payee','external','member','public',1,"+travelAgencyID+")");
            s.executeUpdate("Insert into role (roleid,accessibility,position,visibility,inheritance,unit) values ('otro','external','member','public',1,"+travelAgencyID+")");
            
            s = bd.conection.createStatement();
            s.executeUpdate("Delete from serviceprofileid");
            s = bd.conection.createStatement();
            s.executeUpdate("Delete from serviceprocessid");
            /*s = bd.conection.createStatement();
            s.executeUpdate("DROP TABLE jena_g1t0_reif");
            s = bd.conection.createStatement();
            s.executeUpdate("DROP TABLE jena_g1t1_stmt");
            s = bd.conection.createStatement();
            s.executeUpdate("DROP TABLE jena_g2t0_reif");
            s = bd.conection.createStatement();
            s.executeUpdate("DROP TABLE jena_g2t1_stmt");
            s = bd.conection.createStatement();
            s.executeUpdate("DROP TABLE jena_graph");
            s = bd.conection.createStatement();
            s.executeUpdate("DROP TABLE jena_long_lit");
            s = bd.conection.createStatement();
            s.executeUpdate("DROP TABLE jena_long_uri");
            s = bd.conection.createStatement();
            s.executeUpdate("DROP TABLE jena_prefix");
            s = bd.conection.createStatement();
            s.executeUpdate("DROP TABLE jena_sys_stmt");*/
       
            

            
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
