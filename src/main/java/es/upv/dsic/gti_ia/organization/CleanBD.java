package es.upv.dsic.gti_ia.organization;

import java.sql.ResultSet;
import java.sql.Statement;

public class CleanBD {

	/**
	 * @param args
	 */

	public void clean_database() {
		try {
			BaseDeDatos bd = new BaseDeDatos();
			bd.estableceConexion();
			// Borramos
			Statement s = bd.conexion.createStatement();
			s.executeUpdate("Delete from unit where id<>1");
			s = bd.conexion.createStatement();
			s.executeUpdate("Delete from role where id<>1");
			s = bd.conexion.createStatement();
			s.executeUpdate("Delete from norm");
			s = bd.conexion.createStatement();
			s.executeUpdate("Delete from simplerequestnorm");
			s = bd.conexion.createStatement();
			s.executeUpdate("Delete from maxcardinalitynorm");
			s = bd.conexion.createStatement();
			s.executeUpdate("Delete from incompatibilitynorm");
			s = bd.conexion.createStatement();
			s.executeUpdate("Delete from entity");
			s = bd.conexion.createStatement();
			s.executeUpdate("Delete from entityplaylist");
			s = bd.conexion.createStatement();
			s
					.executeUpdate("Insert into unit (unitID,type,goal,parentunit) values ('travelagency','team','reservetravel',1)");
			s = bd.conexion.createStatement();
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

			s = bd.conexion.createStatement();
			s.executeUpdate("Delete from serviceprofileid");
			s = bd.conexion.createStatement();
			s.executeUpdate("Delete from serviceprocessid");
			s = bd.conexion.createStatement();


			bd.cierraConexion();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

}
