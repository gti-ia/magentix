package es.upv.dsic.gti_ia.organization;

import java.sql.ResultSet;
import java.sql.Statement;

public class CleanBD {

	/**
	 * @param args
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
			s
					.executeUpdate("Insert into unit (unitID,type,goal,parentunit) values ('travelagency','team','reservetravel',1)");
			s = bd.conection.createStatement();
			ResultSet rs = s
					.executeQuery("Select * from unit where unitid='travelagency'");
			rs.next();
			String travelAgencyID = rs.getString("id");
			s
					.executeUpdate("Insert into role (roleid,accessibility,position,visibility,inheritance,unit) values ('provider','external','member','public',1,"
							+ travelAgencyID + ")");
			s
					.executeUpdate("Insert into role (roleid,accessibility,position,visibility,inheritance,unit) values ('customer','external','member','public',1,"
							+ travelAgencyID + ")");
			s
					.executeUpdate("Insert into role (roleid,accessibility,position,visibility,inheritance,unit) values ('payee','external','member','public',1,"
							+ travelAgencyID + ")");
			s
					.executeUpdate("Insert into role (roleid,accessibility,position,visibility,inheritance,unit) values ('otro','external','member','public',1,"
							+ travelAgencyID + ")");

			s = bd.conection.createStatement();
			s.executeUpdate("Delete from serviceprofileid");
			s = bd.conection.createStatement();
			s.executeUpdate("Delete from serviceprocessid");
			s = bd.conection.createStatement();


			bd.closeConnection();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

}
