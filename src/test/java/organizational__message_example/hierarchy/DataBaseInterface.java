package organizational__message_example.hierarchy;


import java.sql.ResultSet;
import java.sql.Statement;

import es.upv.dsic.gti_ia.organization.DataBaseAcces;

/**
 * Clean DataBase thomas and remove jena tables.
 */
public class DataBaseInterface {


	/**
	 * This class clean the entire contents of the tables in the database thomas, including tables that are created within jena, it is important that we make use of this method at the beginning or end of the initialization of our projects, we must be careful configure the settings.xml file with data from the database.
	 */
	public void initialize_db() {
		try {
			DataBaseAcces bd=new DataBaseAcces();
			bd.connect(); 


			//Miramos si ya existen los roles del oms y el sf, si existen no hacemos nada, sino los creamos.


			Statement s = bd.connection.createStatement();

			ResultSet rs = s.executeQuery("Select * from role where roleid='oms'");

			
			if (!rs.first())
			{

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
			}

			bd.closeConnection();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

}
