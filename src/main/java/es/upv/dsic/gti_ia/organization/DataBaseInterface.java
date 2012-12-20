package es.upv.dsic.gti_ia.organization;

import jason.asSyntax.Rule;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.upv.dsic.gti_ia.norms.Norm;
import es.upv.dsic.gti_ia.organization.exception.MySQLException;
import es.upv.dsic.gti_ia.organization.exception.ParentUnitNotExistsException;
import es.upv.dsic.gti_ia.organization.exception.RoleNotExistsException;
import es.upv.dsic.gti_ia.organization.exception.THOMASMessages;
import es.upv.dsic.gti_ia.organization.exception.THOMASMessages.MessageID;
import es.upv.dsic.gti_ia.organization.exception.UnitNotExistsException;

public class DataBaseInterface {
	private DataBaseAccess db;


	/**
	 * Used for retrieve local messages.
	 */
	private THOMASMessages l10n;

	public DataBaseInterface() {
		db = new DataBaseAccess();
		l10n = new THOMASMessages();

	}

	String acquireRole(String unitName, String roleName, String agentName) throws MySQLException {
		Statement st = null;
		Statement st2 = null;
		Statement st3 = null;

		ResultSet res = null;

		Connection connection = null;

		try {
			connection = db.connect();



			//TODO Si aún no esta en la tabla agentList hay que añadirlo.
			st2 = connection.createStatement();
			res = st2.executeQuery("SELECT * FROM agentList WHERE agentName = '"+agentName+"'");

			if (!res.next())
			{
				st3 = connection.createStatement();
				st3.executeUpdate("INSERT INTO agentList (agentName) VALUES ('"+agentName+"')"); 
			}

			st = connection.createStatement();

			st.executeUpdate("INSERT INTO agentPlayList (idagentList, idroleList)" +
					" VALUES ((SELECT idagentList FROM agentList WHERE agentName = '"+agentName+"'), (SELECT idroleList FROM roleList WHERE roleName ='" + roleName + "' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "')))");





			connection.commit();
			return roleName + " acquired";



		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {

			try
			{
				if (connection != null)
					connection.close();
				if (st != null)
					st.close();
				if (st2 != null)
					st2.close();
				if (st3 != null)
					st3.close();

				if (res != null)
					res.close();
			}catch(SQLException e)
			{
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	/*
	 * String allocateRole(String roleName, String unitName, String
	 * targetAgentName, String agentName) throws SQLException{ // TODO no té
	 * molt de trellat la especificació, fa el mateix q l'anterior funció
	 * Statement st; st = db.connection.createStatement(); ResultSet res =
	 * st.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+
	 * unitName+"'"); if(res.next()){ int idUnit = res.getInt("idunitList");
	 * Statement st2 = db.connection.createStatement(); ResultSet res2 =
	 * st2.executeQuery
	 * ("SELECT idroleList FROM roleList WHERE roleName ='"+roleName
	 * +"' AND idunitList = "+idUnit); if(res2.next()){ int idRole =
	 * res.getInt("idroleList"); Statement st3 =
	 * db.connection.createStatement(); int res3 =st3.executeUpdate(
	 * "INSERT INTO agentPlayList (agentName, idroleList) VALUES ('"
	 * +agentName+"', "+idRole+")"); if(res3 != 0){ db.connection.commit();
	 * return "<"+roleName+" + \"acquired\">"; } } return
	 * "Error: role "+roleName+" not found in unit "+unitName; } return
	 * "Error: unit "+unitName+" not found in database"; }
	 */

	boolean checkAgent(String agentName) throws MySQLException {
		boolean exists = false;
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			connection = db.connect();

			stmt = connection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM agentList WHERE agentName='"+agentName+"'");
			if (rs.next()) 
				exists = true;

			connection.commit();
			return exists;
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {

			try
			{
				if (connection != null)
					connection.close();
				if (stmt != null)
					stmt.close();

				if (rs != null)
					rs.close();

			}catch(SQLException e)
			{
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	boolean checkAgentInUnit(String agentName, String unit) throws MySQLException {
		boolean exists = false;
		Connection connection = null;

		Statement stmt = null;
		Statement stmt2 = null;

		ResultSet rs2 = null;
		ResultSet rs3 = null;

		try {
			connection = db.connect();

			stmt = connection.createStatement();
			rs2 = stmt.executeQuery("SELECT idroleList FROM roleList WHERE idunitList = (SELECT idunitList FROM unitList WHERE unitName ='" + unit + "')");
			while (rs2.next()) {
				stmt2 = connection.createStatement();
				int idRole = rs2.getInt("idroleList");

				rs3 = stmt2.executeQuery("SELECT * FROM agentPlayList WHERE idroleList=" + idRole + " AND idagentList = (SELECT idagentList FROM agentList WHERE agentName ='"+agentName+"')");

				if (rs3.next()) {
					exists = true;
				}
			}


			connection.commit();
			return exists;
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {

			try
			{
				if (connection != null)
					connection.close();
				if (stmt != null)
					stmt.close();
				if (stmt2 != null)
					stmt2.close();

				if (rs2 != null)
					rs2.close();
				if (rs3 != null)
					rs3.close();

			}catch(SQLException e)
			{
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	boolean checkAgentPlaysRole(String agentName, String role, String unit) throws MySQLException {
		boolean exists = false;
		Connection connection = null;
		Statement stmt = null;
		Statement stmt2 = null;

		ResultSet rs2 = null;
		ResultSet rs3 = null;

		try {
			connection = db.connect();

			stmt = connection.createStatement();

			rs2 = stmt.executeQuery("SELECT idroleList FROM roleList WHERE idunitList = (SELECT idunitList FROM unitList WHERE unitName ='" + unit + "') AND roleName ='" + role + "'");
			while (rs2.next()) {
				int roleId = rs2.getInt("idroleList");
				stmt2 = connection.createStatement();
				rs3 = stmt2.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = " + roleId + " AND idagentList = (SELECT idagentList FROM agentList WHERE agentName='" + agentName + "')");
				if (rs3.next()) {

					exists = true;
				}
			}


			connection.commit();
			return exists;
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {

			try
			{

				if (connection != null)
					connection.close();
				if (stmt != null)
					stmt.close();
				if (stmt2 != null)
					stmt2.close();

				if (rs2 != null)
					rs2.close();
				if (rs3 != null)
					rs3.close();

			}catch(SQLException e)
			{
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	boolean checkNoCreatorAgentsInUnit(String unit) throws MySQLException {

		Connection connection = null;
		Statement stmt = null;
		Statement stmt2 = null;

		ResultSet rs3 = null;
		ResultSet rs4 = null;

		try {
			connection = db.connect();

			stmt = connection.createStatement();
			rs3 = stmt.executeQuery("SELECT idroleList FROM roleList WHERE idunitlist = (SELECT idunitList FROM unitList WHERE unitName ='" + unit + "') AND idposition != (SELECT idposition FROM position WHERE positionName ='creator')");
			while (rs3.next()) {

				int roleId = rs3.getInt("idroleList");

				stmt2 = connection.createStatement();
				rs4 = stmt2.executeQuery("SELECT * FROM agentPlayList WHERE idroleList =" + roleId);

				if (rs4.next()) {
					connection.commit();
					return true;
				}
			}


			connection.commit();
			return false;
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {

			try
			{
				if (connection != null)
					connection.close();
				if (stmt != null)
					stmt.close();
				if (stmt2 != null)
					stmt2.close();

				if (rs3 != null)
					rs3.close();
				if (rs4 != null)
					rs4.close();
			}catch(SQLException e)
			{
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	boolean checkPlayedRoleInUnit(String role, String unit) throws MySQLException {

		Connection connection = null;
		Statement stmt2 = null;
		ResultSet rs3 = null;

		try {
			connection = db.connect();

			stmt2 = connection.createStatement();
			rs3 = stmt2.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE idunitList =(SELECT idunitList FROM unitList WHERE unitName ='" + unit + "') AND roleName='" + role + "')");
			if (rs3.next()) {
				connection.commit();
				return true;
			}
			connection.commit();
			return false;
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {

			try
			{
				if (connection != null)
					connection.close();
				if (stmt2 != null)
					stmt2.close();

				if (rs3 != null)
					rs3.close();
			}catch(SQLException e)
			{
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	boolean checkTargetRoleNorm(String role, String unit) {
		// TODO on estan les normes
		return false;
	}

	boolean checkPosition(String agent, String position) throws MySQLException {

		Connection connection = null;
		Statement st = null;

		Statement st3 = null;

		ResultSet res = null;

		ResultSet res3 = null;

		try {
			connection = db.connect();

			st = connection.createStatement();
			res = st.executeQuery("SELECT idroleList FROM agentPlayList WHERE idagentList = (SELECT idagentList FROM agentList WHERE agentName ='" + agent + "')");
			while (res.next()) {
				int idRole = res.getInt("idroleList");

				st3 = connection.createStatement();
				res3 = st3.executeQuery("SELECT * FROM position WHERE idposition = (SELECT idposition FROM roleList WHERE idroleList =" + idRole+")");
				if (res3.next() && res3.getString("positionName").equalsIgnoreCase(position)) {
					connection.commit();
					return true;
				}

			}
			connection.commit();
			return false;
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {

			try
			{
				if (connection != null)
					connection.close();
				if (st != null)
					st.close();
				if (st3 != null)
					st3.close();

				if (res != null)
					res.close();
				if (res3 != null)
					res3.close();
			}catch(SQLException e)
			{
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	boolean checkPositionInUnit(String agent, String position, String unit) throws MySQLException {

		Connection connection = null;
		Statement st = null;
		Statement st3 = null;


		ResultSet res = null;
		ResultSet res3 = null;


		try {
			connection = db.connect();

			st = connection.createStatement();
			res = st.executeQuery("SELECT idroleList FROM agentPlayList WHERE idagentList = (SELECT idagentList FROM agentList WHERE agentName ='" + agent + "')");
			while (res.next()) {
				int idRole = res.getInt("idroleList");

				st3 = connection.createStatement();
				res3 = st3.executeQuery("SELECT * FROM position WHERE idposition = (SELECT idposition FROM roleList WHERE idroleList =" + idRole + " AND idunitList = (SELECT idunitList FROM unitList WHERE unitName ='" + unit + "'))");
				if (res3.next() && res3.getString("positionName").equalsIgnoreCase(position)) {
					connection.commit();
					return true;
				}

			}
			connection.commit();
			return false;
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {

			try
			{
				if (connection != null)
					connection.close();
				if (st != null)
					st.close();
				if (st3 != null)
					st3.close();

				if (res != null)
					res.close();
				if (res3 != null)
					res3.close();
			}catch(SQLException e)
			{
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	boolean checkRole(String role, String unit) throws MySQLException {
		Connection connection = null;

		Statement st2 = null;


		ResultSet res2 = null;

		try {
			connection = db.connect();
			st2 = connection.createStatement();
			res2 = st2.executeQuery("SELECT * FROM roleList WHERE idunitList = (SELECT idunitList FROM unitList WHERE unitName ='" + unit + "') AND roleName ='" + role + "'");
			if (res2.next()) {
				connection.commit();
				return true;
			}

			connection.commit();
			return false;
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try
			{
				if (connection != null)
					connection.close();

				if (st2 != null)
					st2.close();


				if (res2 != null)
					res2.close();
			}catch(SQLException e)
			{
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}

		}
	}

	boolean checkSubUnits(String unit) throws MySQLException {
		Connection connection = null;

		Statement st2 = null;


		ResultSet res2 = null;

		try {
			connection = db.connect();

			st2 = connection.createStatement();
			res2 = st2.executeQuery("SELECT * FROM unitHierarchy WHERE idParentUnit = (SELECT idunitList FROM unitList WHERE unitName ='" + unit + "')");
			if (res2.next()) {
				connection.commit();
				return true;
			}

			connection.commit();
			return false;
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try
			{
				if (connection != null)
					connection.close();
				if (st2 != null)
					st2.close();

				if (res2 != null)
					res2.close();
			}catch(SQLException e)
			{
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}

		}
	}

	boolean checkUnit(String unit) throws MySQLException {
		Connection connection = null;
		Statement st = null;
		Statement st2 = null;

		ResultSet res = null;
		ResultSet res2 = null;

		try {
			connection = db.connect();

			st = connection.createStatement();
			res = st.executeQuery("SELECT * FROM unitList WHERE unitName ='" + unit + "'");
			if (res.next()) {
				connection.commit();
				return true;
			}
			connection.commit();
			return false;
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try
			{
				if (connection != null)
					connection.close();
				if (st != null)
					st.close();
				if (st2 != null)
					st2.close();

				if (res != null)
					res.close();
				if (res2 != null)
					res2.close();
			}catch(SQLException e)
			{
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}

		}
	}

	boolean checkVirtualUnit(String unit) throws MySQLException {
		Connection connection = null;

		Statement st2 = null;


		ResultSet res2 = null;

		try {
			connection = db.connect();

			st2 = connection.createStatement();
			res2 = st2.executeQuery("SELECT * FROM unitList WHERE idunitType = (SELECT idunitType FROM unitType WHERE unitTypeName ='virtual') AND unitName ='" + unit + "'");
			if (res2.next()) {
				connection.commit();
				return true;
			}

			connection.commit();
			return false;
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try
			{
				if (connection != null)
					connection.close();

				if (st2 != null)
					st2.close();


				if (res2 != null)
					res2.close();
			}catch(SQLException e)
			{
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}

		}
	}

	String createRole(String roleName, String unitName, String accessibility, String visibility, String position) throws MySQLException {

		Connection connection = null;
		Statement st = null;



		try {
			connection = db.connect();


			st = connection.createStatement();
			st.executeUpdate("INSERT INTO roleList (roleName, idunitList, idposition, idaccessibility, idvisibility) VALUES ('" + roleName + "', (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "'),(SELECT idposition FROM position WHERE positionName ='" + position + "'),(SELECT idaccessibility FROM accessibility WHERE accessibility ='" + accessibility + "'),(SELECT idvisibility FROM visibility WHERE visibility ='" + visibility + "'))");

			connection.commit();
			return roleName + " created";


		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);

		} finally {
			try
			{
				if (connection != null)
					connection.close();
				if (st != null)
					st.close();
			}catch(SQLException e)
			{
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}

		}
	}

	String createUnit(String unitName, String unitType, String parentUnitName, String agentName, String creatorAgentName) throws MySQLException {
		Connection connection = null;
		Statement st = null;
		Statement st2 = null;
		Statement st3 = null;
		Statement st4 = null;
		Statement st5 = null;

		ResultSet res = null;
		ResultSet res2 = null;
		ResultSet res5 = null;

		try {
			connection = db.connect();



			st = connection.createStatement();
			st.executeUpdate("INSERT INTO unitList (unitName, idunitType) VALUES ('" + unitName + "', (SELECT idunitType FROM unitType WHERE unitTypeName ='" + unitType + "'))");

			st2 = connection.createStatement();
			res2 = st2.executeQuery("SELECT LAST_INSERT_ID()");

			if (res2.next())
			{
				int insertedUnitId = res2.getInt(1);

				st3 = connection.createStatement();
				st3.executeUpdate("INSERT INTO unitHierarchy (idParentUnit, idChildUnit) VALUES ((SELECT idunitList FROM unitList WHERE unitName ='" + parentUnitName + "'), " + insertedUnitId + ")");

				st4 = connection.createStatement();
				st4.executeUpdate("INSERT INTO roleList (roleName, idunitList, idposition, idaccessibility, idvisibility) VALUES ('" + creatorAgentName + "', " + insertedUnitId + ", (SELECT idposition FROM position WHERE positionName ='creator'),(SELECT idaccessibility FROM accessibility WHERE accessibility ='internal'), (SELECT idVisibility FROM visibility WHERE visibility ='private'))");

				st5 = connection.createStatement();
				st5.executeUpdate("INSERT INTO agentPlayList (idagentList, idroleList) VALUES ((SELECT idagentList FROM agentList WHERE agentName='" + agentName + "'), LAST_INSERT_ID())");
			}
			connection.commit();
			return unitName + " created";


		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);

		} finally {
			try
			{
				if (connection != null)
					connection.close();
				if (st != null)
					st.close();
				if (st2 != null)
					st2.close();
				if (st3 != null)
					st3.close();
				if (st4 != null)
					st4.close();
				if (st5 != null)
					st5.close();

				if (res != null)
					res.close();
				if (res2 != null)
					res2.close();
				if (res5 != null)
					res5.close();

			}catch(SQLException e)
			{
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}

		}
	}

	String deallocateRole(String roleName, String unitName, String targetAgentName, String agentName) throws MySQLException {
		Connection connection = null;
		Statement st = null;
		Statement st2 = null;
		Statement st3 = null;

		ResultSet rs = null;

		try {
			connection = db.connect();

			st = connection.createStatement();
			st.executeUpdate("DELETE FROM agentPlayList WHERE idagentList = (SELECT idagentList FROM agentList WHERE agentName = '" + targetAgentName + "') AND idroleList = (SELECT idroleList FROM roleList WHERE roleName ='" + roleName + "' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "'))");


			//TODO Comprobar si es el último rol jugado, en ese caso eliminarlo de la tabla agentList
			st2 = connection.createStatement();
			rs = st2.executeQuery("SELECT * FROM agentPlayList WHERE idagentList = (SELECT idagentList FROM agentList WHERE agentName = '"+targetAgentName+"')");

			if (!rs.next())
			{
				st3 = connection.createStatement();
				st3.executeUpdate("DELETE FROM agentList WHERE agentName = '"+targetAgentName+"'");
			}

			connection.commit();
			return roleName + " deallocated";




		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);

		} finally {

			try
			{
				if (connection != null)
					connection.close();
				if (st != null)
					st.close();
				if (st2 != null)
					st2.close();
				if (st3 != null)
					st3.close();

				if (rs != null)
					rs.close();
			}catch(SQLException e)
			{
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}

		}
	}

	String deleteRole(String roleName, String unitName, String agentName) throws MySQLException{
		Connection connection = null;
		Statement st = null;

		try {
			connection = db.connect();



			st = connection.createStatement();
			st.executeUpdate("DELETE FROM roleList WHERE roleName ='" + roleName + "' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "')");
			connection.commit();
			return roleName + " deleted";


		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);

		} finally {
			try
			{
				if (connection != null)
					connection.close();
				if (st != null)
					st.close();

			}catch(SQLException e)
			{
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}

		}
	}

	String deleteUnit(String unitName, String agentName) throws MySQLException{
		Connection connection = null;
		Statement st = null;
		Statement st2 = null;
		Statement st3 = null;
		Statement st4 = null;
		Statement st5 = null;
		Statement st6 = null;

		ResultSet res = null;
		ResultSet res2 = null;

		try {
			connection = db.connect();

			st = connection.createStatement();
			res = st.executeQuery("SELECT idroleList FROM roleList WHERE idposition = (SELECT idposition FROM position WHERE positionName ='creator') AND idunitList = (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "')");
			while (res.next()) {
				int idroleList = res.getInt("idroleList");
				st2 = connection.createStatement();
				st2.executeUpdate("DELETE FROM agentPlayList WHERE idroleList =" + idroleList);
				// if(res4 == 0)//Puede que no haya nadie jugando ese
				// rol, no tiene por que dar un error.
				// throw new
				// THOMASException("Error: mysql error in agentPlayList "+res4);
			}
			st3 = connection.createStatement();
			st3.executeUpdate("DELETE FROM roleList WHERE idunitList = (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "')");
			st4 = connection.createStatement();
			st4.executeUpdate("DELETE FROM unitHierarchy WHERE idChildUnit = (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "')");

			st6 = connection.createStatement();
			res2 = st6.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "'");

			int unitId = -1;
			if (res2.next())
				unitId = res2.getInt("idunitList");
			st5 = connection.createStatement();
			st5.executeUpdate("DELETE FROM unitList WHERE idunitList = '"+unitId+"'");

			connection.commit();
			return unitName + " deleted";



		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);

		}  finally {
			try
			{
				if (connection != null)
					connection.close();
				if (st != null)
					st.close();
				if (st2 != null)
					st2.close();
				if (st3 != null)
					st3.close();
				if (st4 != null)
					st4.close();
				if (st5 != null)
					st5.close();
				if (st6 != null)
					st6.close();

				if (res != null)
					res.close();
				if (res2 != null)
					res2.close();

			}catch(SQLException e)
			{
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}

		}
	}

	String deleteNorm(String NormName, String UnitName) throws MySQLException
	{
		Connection connection = null;
		Statement st = null;

		
		try {
			connection = db.connect();

			st = connection.createStatement();
			
			st.executeUpdate("DELETE FROM actionNormParam USING actionNormParam INNER JOIN normList INNER JOIN unitList " +
					"ON (actionNormParam.idnormList = normList.idnormList AND normList.idunitList=unitList.idunitList)" +
					"WHERE unitName = '"+UnitName+"' AND normName = '"+NormName+"'");

			st.executeUpdate("DELETE FROM normList USING normList INNER JOIN unitList ON (normList.idunitList=unitList.idunitList)" +
					"WHERE unitName='"+UnitName+"' AND normName='"+NormName+"'");

			
			connection.commit();
			return NormName + " deleted";



		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);

		}  finally {
			try
			{
				if (connection != null)
					connection.close();
				if (st != null)
					st.close();
			
			}catch(SQLException e)
			{
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}

		}

	}
	String joinUnit(String unitName, String parentName) throws MySQLException, ParentUnitNotExistsException, UnitNotExistsException {

		Connection connection = null;
		Statement st = null;
		Statement st2 = null;

		try {
			connection = db.connect();

			st = connection.createStatement();
			st.executeUpdate("DELETE FROM unitHierarchy WHERE idChildUnit = (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "')");

			st2 = connection.createStatement();
			st2.executeUpdate("INSERT INTO unitHierarchy (idParentUnit, idChildUnit) VALUES ((SELECT idunitList FROM unitList WHERE unitName ='" + parentName + "'),(SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "'))");

			connection.commit();
			return unitName + " + jointed to " + parentName;

		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);

		}  finally {
			try
			{
				if (connection != null)
					connection.close();
				if (st != null)
					st.close();
				if (st2 != null)
					st2.close();


			}catch(SQLException e)
			{
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}

		}
	}

	String leaveRole(String unitName, String roleName, String agentName) throws MySQLException {

		Connection connection = null;
		Statement st = null;
		Statement st2 = null;
		Statement st3 = null;
		Statement st4 = null;
		Statement st5 = null;

		ResultSet res = null;
		ResultSet res2 = null;
		ResultSet res3 = null;

		try {
			connection = db.connect();

			st3 = connection.createStatement();
			st3.executeUpdate("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE idunitList = (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "') AND roleName ='" + roleName + "') AND idagentList = (SELECT idagentList FROM agentList WHERE agentName='" + agentName + "')");




			//TODO Comprobar si es el último rol jugado, en ese caso eliminarlo de la tabla agentList
			st4 = connection.createStatement();
			res3 = st4.executeQuery("SELECT * FROM agentPlayList WHERE idagentList = (SELECT idagentList FROM agentList WHERE agentName = '"+agentName+"')");

			if (!res3.next())
			{
				st5 = connection.createStatement();
				st5.executeUpdate("DELETE FROM agentList WHERE agentName = '"+agentName+"'");
			}

			connection.commit();
			return roleName + " left";

		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);

		} finally {
			try
			{
				if (connection != null)
					connection.close();
				if (st != null)
					st.close();
				if (st2 != null)
					st2.close();
				if (st3 != null)
					st3.close();
				if (st4 != null)
					st4.close();
				if (st5 != null)
					st5.close();

				if (res != null)
					res.close();
				if (res2 != null)
					res2.close();
				if (res3 != null)
					res3.close();
			}catch(SQLException e)
			{
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}

		}
	}

	String getUnitType(String unitName) throws MySQLException{
		Connection connection = null;
		String unitType = "";

		Statement st = null;


		ResultSet res = null;


		try {
			connection = db.connect();

			st = connection.createStatement();
			res = st.executeQuery("SELECT unitTypeName FROM unitType WHERE idunitType = (SELECT idunitType FROM unitList WHERE unitName ='" + unitName + "')");

			if (res.next())
			{
				unitType = res.getString("unitTypeName");
				connection.commit();
				return unitType;
			}
			return unitType;

		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);

		} finally {
			try
			{
				if (connection != null)
					connection.close();
				if (st != null)
					st.close();

				if (res != null)
					res.close();

			}catch(SQLException e)
			{
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}

		}
	}

	/*	ArrayList<ArrayList<String>> getAgentsInUnit(String unitName) throws MySQLException{
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();

		Connection connection = null;

		Statement st2 = null;
		Statement st3 = null;
		Statement st4 = null;


		ResultSet res2 = null;
		ResultSet res3 = null;
		ResultSet res4 = null;

		try {
			connection = db.connect();

			st2 = connection.createStatement();
			res2 = st2.executeQuery("SELECT * FROM roleList WHERE idunitList =(SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "')");
			while (res2.next()) {
				ArrayList<String> aux = new ArrayList<String>();
				String roleName = res2.getString("roleName");
				int idposition = res2.getInt("idposition");
				int idroleList = res2.getInt("idroleList");
				st3 = connection.createStatement();
				res3 = st3.executeQuery("SELECT agentName FROM agentList WHERE idagentList = (SELECT idagentList FROM agentPlayList WHERE idroleList =" + idroleList+")");
				if (res3.next())
					aux.add(res3.getString("agentName"));
				aux.add(roleName);
				st4 = connection.createStatement();
				res4 = st4.executeQuery("SELECT position FROM position WHERE idposition =" + idposition);
				if (res4.next())
					aux.add(res4.getString("position"));
				result.add(aux);
			}
			connection.commit();
			return result;


		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try
			{
				if (connection != null)
					connection.close();
				if (st2 != null)
					st2.close();
				if (st3 != null)
					st3.close();
				if (st4 != null)
					st4.close();

				if (res2 != null)
					res2.close();
				if (res3 != null)
					res3.close();
				if (res4 != null)
					res4.close();
			}catch(SQLException e)
			{
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}

		}
	}
	 */
	ArrayList<String> getParentsUnit(String unitName) throws MySQLException{

		ArrayList<String> result = new ArrayList<String>();

		Connection connection = null;

		Statement st2 = null;
		Statement st3 = null;


		ResultSet res2 = null;
		ResultSet res3 = null;

		try {
			connection = db.connect();



			st2 = connection.createStatement();
			res2 = st2.executeQuery("SELECT idParentUnit FROM unitHierarchy WHERE idChildUnit =(SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "')");
			if (res2.next()) {
				int idParentUnit = res2.getInt("idParentUnit");
				st3 = connection.createStatement();
				res3 = st3.executeQuery("SELECT unitName FROM unitList WHERE idunitList =" + idParentUnit);
				if (res3.next()) {
					result.add(res3.getString("unitName"));
					connection.commit();
					return result;
				}
			}
			result.add("virtual");
			connection.commit();
			return result;


		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);

		} finally {
			try
			{
				if (connection != null)
					connection.close();

				if (st2 != null)
					st2.close();
				if (st3 != null)
					st3.close();


				if (res2 != null)
					res2.close();
				if (res3 != null)
					res3.close();
			}catch(SQLException e)
			{
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}

		}
	}

	ArrayList<ArrayList<String>> getInformAgentRole(String requestedAgentName, String agentName) throws MySQLException{
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();

		Connection connection = null;
		Statement st = null;
		Statement st2 = null;
		Statement st3 = null;
		Statement st4 = null;
		Statement st5 = null;
		Statement st6 = null;
		Statement st7 = null;
		Statement st8 = null;
		Statement st9 = null;
		Statement st10 = null;
		Statement st11 = null;
		Statement st12 = null;
		Statement st13 = null;

		ResultSet res = null;
		ResultSet res2 = null;
		ResultSet res3 = null;
		ResultSet res4 = null;
		ResultSet res5 = null;
		ResultSet res6 = null;
		ResultSet res7 = null;
		ResultSet res8 = null;
		ResultSet res9 = null;
		ResultSet res10 = null;
		ResultSet res11 = null;
		ResultSet res12 = null;
		ResultSet res13 = null;

		try {
			connection = db.connect();



			st = connection.createStatement();
			res = st.executeQuery("SELECT idroleList FROM agentPlayList WHERE idagentList = (SELECT idagentList FROM agentList WHERE agentName ='" + requestedAgentName + "')");
			while (res.next()) {
				int idroleList = res.getInt("idroleList");
				st3 = connection.createStatement();
				res3 = st3.executeQuery("SELECT idunitList, roleName FROM roleList WHERE idroleList =" + idroleList + " AND idvisibility = (SELECT idVisibility FROM visibility WHERE visibility ='public')");
				if (res3.next()) {
					ArrayList<String> aux = new ArrayList<String>();
					int idunitList = res3.getInt("idunitList");
					String roleName = res3.getString("roleName");
					st4 = connection.createStatement();
					res4 = st4.executeQuery("SELECT unitName FROM unitList WHERE idunitList =" + idunitList);
					if (res4.next()) {
						String unitName = res4.getString("unitName");
						aux.add(roleName);
						aux.add(unitName);
						result.add(aux);
					}
				}
			}

			ArrayList<Integer> idunits1 = new ArrayList<Integer>();
			ArrayList<Integer> idunits2 = new ArrayList<Integer>();


			st7 = connection.createStatement();
			res7 = st7.executeQuery("SELECT idroleList FROM agentPlayList WHERE idagentList = (SELECT idagentList FROM agentList WHERE agentName='" + requestedAgentName + "')");
			while (res7.next()) {
				int idroleList = res7.getInt("idroleList");
				st8 = connection.createStatement();
				res8 = st8.executeQuery("SELECT idunitList FROM roleList WHERE idroleList =" + idroleList + " AND idvisibility = (SELECT idVisibility FROM visibility WHERE visibility ='private')");
				if (res8.next()) {
					idunits1.add(res8.getInt("idunitList"));
				}
			}

			st9 = connection.createStatement();
			res9 = st9.executeQuery("SELECT idroleList FROM agentPlayList WHERE idagentList = (SELECT idagentList FROM agentList WHERE agentName ='" + agentName + "')");
			while (res9.next()) {
				int idroleList = res9.getInt("idroleList");
				st10 = connection.createStatement();
				res10 = st10.executeQuery("SELECT idunitList FROM roleList WHERE idroleList =" + idroleList);// AND
				// idvisibility
				// ="+idVisibility);
				if (res10.next()) {
					idunits2.add(res10.getInt("idunitList"));
				}
			}

			for (int unitid : idunits1) {

				if (idunits2.contains(unitid)) {
					st11 = connection.createStatement();
					res11 = st11.executeQuery("SELECT idroleList FROM agentPlayList WHERE idagentList = (SELECT idagentList FROM agentList WHERE agentName ='" + requestedAgentName + "')");
					while (res11.next()) {
						ArrayList<String> aux = new ArrayList<String>();
						int idroleList = res11.getInt("idroleList");
						st12 = connection.createStatement();

						res12 = st12.executeQuery("SELECT roleName FROM roleList WHERE idroleList =" + idroleList + " AND idvisibility = (SELECT idVisibility FROM visibility WHERE visibility ='private') AND idunitList=" + unitid);
						if (res12.next()) {
							st13 = connection.createStatement();
							res13 = st13.executeQuery("SELECT unitName FROM unitList WHERE idunitList =" + unitid);
							if (res13.next()) {
								aux.add(res12.getString("roleName"));
								aux.add(res13.getString("unitName"));
								result.add(aux);
							}
						}
					}
				}
			}
			connection.commit();
			return result;

		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);

		} finally {
			try
			{
				if (connection != null)
					connection.close();
				if (st != null)
					st.close();
				if (st2 != null)
					st2.close();
				if (st3 != null)
					st3.close();
				if (st4 != null)
					st4.close();
				if (st5 != null)
					st5.close();
				if (st6 != null)
					st6.close();
				if (st7 != null)
					st7.close();
				if (st8 != null)
					st8.close();
				if (st9 != null)
					st9.close();
				if (st10 != null)
					st10.close();
				if (st11 != null)
					st11.close();
				if (st12 != null)
					st12.close();
				if (st13 != null)
					st13.close();

				if (res != null)
					res.close();
				if (res2 != null)
					res2.close();
				if (res3 != null)
					res3.close();
				if (res4 != null)
					res4.close();
				if (res5 != null)
					res5.close();
				if (res6 != null)
					res6.close();
				if (res7 != null)
					res7.close();
				if (res8 != null)
					res8.close();
				if (res9 != null)
					res9.close();
				if (res10 != null)
					res10.close();
				if (res11 != null)
					res11.close();
				if (res12 != null)
					res12.close();
				if (res13 != null)
					res13.close();
			}catch(SQLException e)
			{
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}

		}
	}

	ArrayList<ArrayList<String>> getInformAgentRolesPlayedInUnit(String unitName, String targetAgentName) throws MySQLException{
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();


		Connection connection = null;


		Statement st10 = null;
		Statement st11 = null;
		Statement st12 = null;
		Statement st13 = null;
		Statement st14 = null;


		ResultSet res10 = null;
		ResultSet res11 = null;
		ResultSet res12 = null;
		ResultSet res13 = null;
		ResultSet res14 = null;

		try {
			connection = db.connect();



			st10 = connection.createStatement();
			res10 = st10.executeQuery("SELECT idroleList FROM agentPlayList WHERE idagentList = (SELECT idagentList FROM agentList WHERE agentName ='" + targetAgentName + "')");
			while (res10.next()) {
				int idroleList = res10.getInt("idroleList");
				st11 = connection.createStatement();
				res11 = st11.executeQuery("SELECT * FROM roleList WHERE idroleList =" + idroleList + " AND idunitList=(SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "')");
				if (res11.next()) {
					int idvisibility = res11.getInt("idvisibility");
					int idaccessibility = res11.getInt("idaccessibility");
					int idposition = res11.getInt("idposition");
					String roleName = res11.getString("roleName");
					String position = "";
					String visibility = "";
					String accessibility = "";

					st12 = connection.createStatement();
					res12 = st12.executeQuery("SELECT * FROM position WHERE idposition =" + idposition);
					if (res12.next())
						position = res12.getString("positionName");

					st13 = connection.createStatement();
					res13 = st13.executeQuery("SELECT * FROM accessibility WHERE idaccessibility =" + idaccessibility);
					if (res13.next())
						accessibility = res13.getString("accessibility");

					st14 = connection.createStatement();
					res14 = st14.executeQuery("SELECT * FROM visibility WHERE idvisibility =" + idvisibility);
					if (res14.next())
						visibility = res14.getString("visibility");

					ArrayList<String> aux = new ArrayList<String>();
					aux.add(roleName);
					aux.add(visibility);
					aux.add(accessibility);
					aux.add(position);
					result.add(aux);
				}
			}
			connection.commit();
			return result;

		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);

		} finally {
			try
			{
				if (connection != null)
					connection.close();

				if (st10 != null)
					st10.close();
				if (st11 != null)
					st11.close();
				if (st12 != null)
					st12.close();
				if (st13 != null)
					st13.close();

				if (res10 != null)
					res10.close();
				if (res11 != null)
					res11.close();
				if (res12 != null)
					res12.close();
				if (res13 != null)
					res13.close();
			}catch(SQLException e)
			{
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}

		}
	}

	ArrayList<ArrayList<String>> getAgentsRolesInUnit(String unitName, String agentName) throws MySQLException {
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();


		Connection connection = null;
		Statement st12 = null;
		Statement st13 = null;
		Statement st14 = null;

		ResultSet res12 = null;
		ResultSet res13 = null;
		ResultSet res14 = null;

		try {
			connection = db.connect();

			st12 = connection.createStatement();

			res12 = st12.executeQuery("SELECT idroleList, roleName FROM roleList WHERE idunitList = (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "')");
			while (res12.next()) {

				String roleName = res12.getString("roleName");
				int idroleList = res12.getInt("idroleList");


				st14 = connection.createStatement();

				res14 = st14.executeQuery("(SELECT idagentList FROM agentPlayList WHERE idroleList =" + idroleList+")");

				while (res14.next())

				{
					st13 = connection.createStatement();
					int idagentList = res14.getInt("idagentList");
					res13 = st13.executeQuery("SELECT agentName FROM agentList WHERE idagentList = "+idagentList);

					if (res13.next()) {
						ArrayList<String> aux = new ArrayList<String>();
						aux.add(res13.getString("agentName"));
						aux.add(roleName);
						result.add(aux);
					}
				}

			}
			connection.commit();
			return result;
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);

		}  finally {
			try
			{
				if (connection != null)
					connection.close();
				if (st12 != null)
					st12.close();
				if (st13 != null)
					st13.close();
				if (st14 != null)
					st14.close();

				if (res12 != null)
					res12.close();
				if (res13 != null)
					res13.close();
				if (res14 != null)
					res14.close();

			}catch(SQLException e)
			{
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}

		}
	}

	ArrayList<ArrayList<String>> getAgentsVisibilityRolesInUnit(String unitName, String agentName) throws MySQLException {
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();

		Connection connection = null;
		Statement st12 = null;
		Statement st13 = null;
		Statement st14 = null;

		ResultSet res12 = null;
		ResultSet res13 = null;
		ResultSet res14 = null;

		try {
			connection = db.connect();







			st12 = connection.createStatement();

			res12 = st12.executeQuery("SELECT idroleList, roleName FROM roleList WHERE idunitList = (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "') AND idVisibility = (SELECT idVisibility FROM visibility WHERE visibility ='public')");
			while (res12.next()) {

				String roleName = res12.getString("roleName");
				int idroleList = res12.getInt("idroleList");


				st14 = connection.createStatement();

				res14 = st14.executeQuery("(SELECT idagentList FROM agentPlayList WHERE idroleList =" + idroleList+")");

				while (res14.next())

				{
					st13 = connection.createStatement();
					int idagentList = res14.getInt("idagentList");
					res13 = st13.executeQuery("SELECT agentName FROM agentList WHERE idagentList = "+idagentList);

					if (res13.next()) {
						ArrayList<String> aux = new ArrayList<String>();
						aux.add(res13.getString("agentName"));
						aux.add(roleName);
						result.add(aux);
					}
				}

			}
			connection.commit();
			return result;
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);

		}  finally {
			try
			{
				if (connection != null)
					connection.close();
				if (st12 != null)
					st12.close();
				if (st13 != null)
					st13.close();
				if (st14 != null)
					st14.close();

				if (res12 != null)
					res12.close();
				if (res13 != null)
					res13.close();
				if (res14 != null)
					res14.close();

			}catch(SQLException e)
			{
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}

		}
	}

	ArrayList<String> getAgentsPlayingRoleInUnit(String unitName, String roleName, String agentName) throws MySQLException {
		ArrayList<String> result = new ArrayList<String>();



		Connection connection = null;
		Statement st12 = null;
		Statement st13 = null;
		Statement st14 = null;

		ResultSet res12 = null;
		ResultSet res13 = null;
		ResultSet res14 = null;

		try {
			connection = db.connect();




			st12 = connection.createStatement();

			res12 = st12.executeQuery("SELECT idroleList FROM roleList WHERE idunitList = (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "') AND roleName ='" + roleName + "'");
			while (res12.next()) {
				int idroleList = res12.getInt("idroleList");

				st14 = connection.createStatement();
				res14 = st14.executeQuery("(SELECT idagentList FROM agentPlayList WHERE idroleList =" + idroleList+")");

				while (res14.next())
				{
					int idagentList = res14.getInt("idagentList");
					st13 = connection.createStatement();
					res13 = st13.executeQuery("SELECT agentName FROM agentList WHERE idagentList = "+idagentList);
					if(res13.next()) {
						result.add(res13.getString("agentName"));
					}
				}
			}
			connection.commit();
			return result;
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);

		} finally {
			try
			{
				if (connection != null)
					connection.close();
				if (st12 != null)
					st12.close();
				if (st13 != null)
					st13.close();
				if (st14 != null)
					st14.close();

				if (res12 != null)
					res12.close();
				if (res13 != null)
					res13.close();
				if (res14 != null)
					res14.close();

			}catch(SQLException e)
			{
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}

		}

	}

	ArrayList<ArrayList<String>> getAgentsPlayingPositionInUnit(String unitName, String positionValue, String agentName) throws MySQLException{
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();




		Connection connection = null;
		Statement st3 = null;
		Statement st4 = null;
		Statement st5 = null;

		ResultSet res3 = null;
		ResultSet res4 = null;
		ResultSet res5 = null;

		try {
			connection = db.connect();



			st3 = connection.createStatement();

			res3 = st3.executeQuery("SELECT idroleList, roleName FROM roleList WHERE idunitList = (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "') AND idposition = (SELECT idposition FROM position WHERE positionName ='" + positionValue + "')");

			while (res3.next()) {
				int idroleList = res3.getInt("idroleList");
				String roleName = res3.getString("roleName");

				st5 = connection.createStatement();
				res5 = st5.executeQuery("(SELECT idagentList FROM agentPlayList WHERE idroleList =" + idroleList+")");

				while(res5.next())
				{
					int idagentList = res5.getInt("idagentList");

					st4 = connection.createStatement();
					res4 = st4.executeQuery("SELECT agentName FROM agentList WHERE idagentList = " + idagentList);
					if (res4.next()) {
						ArrayList<String> aux = new ArrayList<String>();
						aux.add(res4.getString("agentName"));
						aux.add(roleName);
						result.add(aux);
					}
				}
			}
			connection.commit();
			return result;
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);

		} finally {

			try
			{
				if (connection != null)
					connection.close();
				if (st3 != null)
					st3.close();
				if (st4 != null)
					st4.close();
				if (st5 != null)
					st5.close();

				if (res3 != null)
					res3.close();
				if (res4 != null)
					res4.close();
				if (res5 != null)
					res5.close();

			}catch(SQLException e)
			{
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}

		}
	}

	ArrayList<String> getAgentsPlayingRolePositionInUnit(String unitName, String roleName, String positionValue, String agentName) throws MySQLException {
		// TODO deurien tornarse els agents q juguen el role roleName, amb la
		// posicio positionValue en la unitat unitName?
		ArrayList<String> result = new ArrayList<String>();




		Connection connection = null;
		Statement st10 = null;
		Statement st11 = null;

		ResultSet res10 = null;
		ResultSet res11 = null;

		try {
			connection = db.connect();




			st11 = connection.createStatement();

			st10 = connection.createStatement();

			res11 = st11.executeQuery("SELECT idagentList FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE idUnitList= (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "') AND roleName ='" + roleName + "' AND idposition = (SELECT idposition FROM position WHERE positionName ='" + positionValue + "'))");

			while(res11.next())
			{

				int idagentList = res11.getInt("idagentList");
				res10 = st10.executeQuery("SELECT agentName FROM agentList WHERE idagentList = "+ idagentList);
				if (res10.next()) {

					result.add(res10.getString("agentName"));
				}
			}


			connection.commit();
			return result;
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);

		}  finally {
			try
			{
				if (connection != null)
					connection.close();
				if (st10 != null)
					st10.close();
				if (st11 != null)
					st11.close();

				if (res10 != null)
					res10.close();
				if (res11 != null)
					res11.close();


			}catch(SQLException e)
			{
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}

		}
	}

	ArrayList<ArrayList<String>> getAgentsPlayingVisibilityPositionInUnit(String unitName, String positionValue, String agentName) throws MySQLException{
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();




		Connection connection = null;
		Statement st3 = null;
		Statement st4 = null;
		Statement st5 = null;

		ResultSet res3 = null;
		ResultSet res4 = null;
		ResultSet res5 = null;

		try {
			connection = db.connect();



			st3 = connection.createStatement();

			res3 = st3.executeQuery("SELECT idroleList, roleName FROM roleList WHERE idunitList = (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "') AND idposition = (SELECT idposition FROM position WHERE positionName ='" + positionValue + "') AND idVisibility = (SELECT idVisibility FROM visibility WHERE visibility ='public')");


			while (res3.next()) {
				int idroleList = res3.getInt("idroleList");
				String roleName = res3.getString("roleName");

				st5 = connection.createStatement();
				res5 = st5.executeQuery("(SELECT idagentList FROM agentPlayList WHERE idroleList =" + idroleList+")");

				while(res5.next())
				{
					int idagentList = res5.getInt("idagentList");

					st4 = connection.createStatement();
					res4 = st4.executeQuery("SELECT agentName FROM agentList WHERE idagentList = " + idagentList);
					if (res4.next()) {
						ArrayList<String> aux = new ArrayList<String>();
						aux.add(res4.getString("agentName"));
						aux.add(roleName);
						result.add(aux);
					}
				}
			}
			connection.commit();
			return result;
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);

		} finally {

			try
			{
				if (connection != null)
					connection.close();
				if (st3 != null)
					st3.close();
				if (st4 != null)
					st4.close();
				if (st5 != null)
					st5.close();

				if (res3 != null)
					res3.close();
				if (res4 != null)
					res4.close();
				if (res5 != null)
					res5.close();

			}catch(SQLException e)
			{
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}

		}
	}
	int getInformQuantityAgentsRolesInUnit(String unitName, String agentName) throws MySQLException{

		int idroleList;

		Connection connection = null;

		Statement st5 = null;
		Statement st10 = null;
		Statement st11 = null;

		ResultSet res5 = null;
		ResultSet res10 = null;
		ResultSet res11 = null;

		try {
			connection = db.connect();





			Set<String> agentNames = new HashSet<String>();
			st5 = connection.createStatement();

			res5 = st5.executeQuery("SELECT idroleList FROM roleList WHERE idunitList= (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "')");
			while (res5.next()) {
				idroleList = res5.getInt("idroleList");

				st11 = connection.createStatement();
				res11 = st11.executeQuery("SELECT idagentList FROM agentPlayList WHERE idroleList =" + idroleList);

				while(res11.next())
				{
					int idagentList = res11.getInt("idagentList");
					st10 = connection.createStatement();
					res10 = st10.executeQuery("SELECT agentName FROM agentList WHERE idagentList = " + idagentList);
					if (res10.next()) {
						agentNames.add(res10.getString("agentName"));
					}
				}
			}
			connection.commit();
			return agentNames.size();
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);

		} finally {

			try
			{
				if (connection != null)
					connection.close();

				if (st10 != null)
					st10.close();
				if (st11 != null)
					st11.close();

				if (res5 != null)
					res5.close();

				if (res10 != null)
					res10.close();
				if (res11 != null)
					res11.close();

			}catch(SQLException e)
			{
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	int getInformQuantityAgentsVisibilityRolesInUnit(String unitName, String agentName) throws MySQLException{

		int idroleList;


		Connection connection = null;

		Statement st5 = null;

		Statement st10 = null;
		Statement st11 = null;

		ResultSet res5 = null;

		ResultSet res10 = null;
		ResultSet res11 = null;

		try {
			connection = db.connect();





			Set<String> agentNames = new HashSet<String>();
			st5 = connection.createStatement();

			res5 = st5.executeQuery("SELECT idroleList FROM roleList WHERE idunitList= (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "') AND idVisibility =(SELECT idVisibility FROM visibility WHERE visibility ='public')");
			while (res5.next()) {
				idroleList = res5.getInt("idroleList");

				st11 = connection.createStatement();
				res11 = st11.executeQuery("SELECT idagentList FROM agentPlayList WHERE idroleList =" + idroleList);

				while(res11.next())
				{
					int idagentList = res11.getInt("idagentList");
					st10 = connection.createStatement();
					res10 = st10.executeQuery("SELECT agentName FROM agentList WHERE idagentList = " + idagentList);
					if (res10.next()) {
						agentNames.add(res10.getString("agentName"));
					}
				}
			}
			connection.commit();
			return agentNames.size();
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);

		} finally {

			try
			{
				if (connection != null)
					connection.close();
				if (st10 != null)
					st10.close();
				if (st11 != null)
					st11.close();

				if (res5 != null)
					res5.close();
				if (res10 != null)
					res10.close();
				if (res11 != null)
					res11.close();

			}catch(SQLException e)
			{
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	int getInformQuantityAgentsPlayingRoleInUnit(String unitName, String roleName, String agentName) throws MySQLException {
		int cont = 0;
		int idroleList;


		Connection connection = null;
		Statement st5 = null;
		Statement st10 = null;
		Statement st11 = null;

		ResultSet res5 = null;
		ResultSet res10 = null;
		ResultSet res11 = null;

		try {
			connection = db.connect();





			st5 = connection.createStatement();

			res5 = st5.executeQuery("SELECT idroleList FROM roleList WHERE idunitList = (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "') AND roleName ='" + roleName + "'");
			if (res5.next())
				idroleList = res5.getInt("idroleList");
			else
				return 0;

			st11 = connection.createStatement();
			res11 = st11.executeQuery("SELECT idagentList FROM agentPlayList WHERE idroleList =" + idroleList);

			while(res11.next())
			{
				int idagentList = res11.getInt("idagentList");

				st10 = connection.createStatement();
				res10 = st10.executeQuery("SELECT DISTINCT agentName FROM agentList WHERE idagentList = " + idagentList);
				if (res10.next()) {
					cont++;
				}
			}
			connection.commit();
			return cont;
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);

		}  finally {

			try
			{
				if (connection != null)
					connection.close();
				if (st5 != null)
					st5.close();

				if (st10 != null)
					st10.close();
				if (st11 != null)
					st11.close();

				if (res5 != null)
					res5.close();

				if (res10 != null)
					res10.close();
				if (res11 != null)
					res11.close();

			}catch(SQLException e)
			{
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	int getInformQuantityAgentsPlayingPositionInUnit(String unitName, String positionValue, String agentName) throws MySQLException{
		int idroleList;

		Connection connection = null;
		Statement st5 = null;
		Statement st10 = null;
		Statement st11 = null;

		ResultSet res5 = null;
		ResultSet res10 = null;
		ResultSet res11 = null;

		try {
			connection = db.connect();





			Set<String> agentNames = new HashSet<String>();
			st5 = connection.createStatement();


			res5 = st5.executeQuery("SELECT idroleList FROM roleList WHERE idunitList= (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "') AND idposition = (SELECT idposition FROM position WHERE positionName ='" + positionValue + "')");

			while (res5.next()) {
				idroleList = res5.getInt("idroleList");

				st11 = connection.createStatement();
				res11 = st11.executeQuery("SELECT idagentList FROM agentPlayList WHERE idroleList =" + idroleList);

				while(res11.next())
				{
					int idagentList = res11.getInt("idagentList");
					st10 = connection.createStatement();
					res10 = st10.executeQuery("SELECT agentName FROM agentList WHERE idagentList = " + idagentList);
					if (res10.next()) {

						agentNames.add(res10.getString("agentName"));
					}
				}
			}
			connection.commit();
			return agentNames.size();
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);

		} finally {
			try
			{
				if (connection != null)
					connection.close();
				if (st5 != null)
					st5.close();

				if (st10 != null)
					st10.close();
				if (st11 != null)
					st11.close();

				if (res5 != null)
					res5.close();

				if (res10 != null)
					res10.close();
				if (res11 != null)
					res11.close();

			}catch(SQLException e)
			{
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	int getInformQuantityAgentsPlayingRolePositionInUnit(String unitName, String roleName, String positionValue, String agentName) throws MySQLException {
		int cont = 0;

		int idroleList;

		Connection connection = null;

		Statement st5 = null;

		Statement st10 = null;
		Statement st11 = null;


		ResultSet res5 = null;

		ResultSet res10 = null;
		ResultSet res11 = null;

		try {
			connection = db.connect();






			st5 = connection.createStatement();


			res5 = st5.executeQuery("SELECT idroleList FROM roleList WHERE idunitList = (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "') AND roleName ='" + roleName + "' AND idposition = (SELECT idposition FROM position WHERE positionName ='" + positionValue + "')");
			if (res5.next())
				idroleList = res5.getInt("idroleList");
			else
				return 0;

			st11 = connection.createStatement();
			res11 = st11.executeQuery("SELECT idagentList FROM agentPlayList WHERE idroleList =" + idroleList);

			while(res11.next())
			{
				int idagentList = res11.getInt("idagentList");
				st10 = connection.createStatement();
				res10 = st10.executeQuery("SELECT agentName FROM agentList WHERE idagentList = "+idagentList);
				if (res10.next()) {
					cont++;
				}
			}
			connection.commit();
			return cont;
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);

		}  finally {
			try
			{
				if (connection != null)
					connection.close();

				if (st5 != null)
					st5.close();

				if (st10 != null)
					st10.close();
				if (st11 != null)
					st11.close();


				if (res5 != null)
					res5.close();

				if (res10 != null)
					res10.close();
				if (res11 != null)
					res11.close();

			}catch(SQLException e)
			{
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	int getInformQuantityAgentsPlayingVisibilityPositionInUnit(String unitName, String positionValue, String agentName) throws MySQLException{
		int idroleList;

		Connection connection = null;
		Statement st5 = null;
		Statement st10 = null;
		Statement st11 = null;

		ResultSet res5 = null;
		ResultSet res10 = null;
		ResultSet res11 = null;

		try {
			connection = db.connect();





			Set<String> agentNames = new HashSet<String>();
			st5 = connection.createStatement();

			res5 = st5.executeQuery("SELECT idroleList FROM roleList WHERE idunitList= (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "') AND idposition = (SELECT idposition FROM position WHERE positionName ='" + positionValue + "') AND idvisibility = (SELECT idVisibility FROM visibility WHERE visibility ='public')");


			while (res5.next()) {
				idroleList = res5.getInt("idroleList");

				st11 = connection.createStatement();
				res11 = st11.executeQuery("SELECT idagentList FROM agentPlayList WHERE idroleList =" + idroleList);

				while(res11.next())
				{
					int idagentList = res11.getInt("idagentList");
					st10 = connection.createStatement();
					res10 = st10.executeQuery("SELECT agentName FROM agentList WHERE idagentList = " + idagentList);
					if (res10.next()) {

						agentNames.add(res10.getString("agentName"));
					}
				}
			}
			connection.commit();
			return agentNames.size();
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);

		} finally {
			try
			{
				if (connection != null)
					connection.close();
				if (st5 != null)
					st5.close();

				if (st10 != null)
					st10.close();
				if (st11 != null)
					st11.close();

				if (res5 != null)
					res5.close();

				if (res10 != null)
					res10.close();
				if (res11 != null)
					res11.close();

			}catch(SQLException e)
			{
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	ArrayList<String> getInformUnit(String unitName) throws MySQLException {
		ArrayList<String> result = new ArrayList<String>();


		Connection connection = null;

		Statement st3 = null;
		Statement st4 = null;
		Statement st5 = null;

		ResultSet res3 = null;
		ResultSet res4 = null;
		ResultSet res5 = null;

		try {
			connection = db.connect();




			st3 = connection.createStatement();
			res3 = st3.executeQuery("SELECT unitTypeName FROM unitType WHERE idunitType = (SELECT idunitType FROM unitList WHERE unitName ='" + unitName + "')");
			res3.next();

			result.add(res3.getString("unitTypeName"));
			st4 = connection.createStatement();
			res4 = st4.executeQuery("SELECT idParentUnit FROM unitHierarchy WHERE idChildUnit = (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "')");
			if (res4.next()) {
				st5 = connection.createStatement();
				res5 = st5.executeQuery("SELECT unitName FROM unitList WHERE idunitList =" + res4.getInt("idParentUnit"));
				res5.next();
				result.add(res5.getString("unitName"));
			} else
				result.add("");
			connection.commit();
			return result;
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);

		} finally {
			try
			{
				if (connection != null)
					connection.close();

				if (st3 != null)
					st3.close();
				if (st4 != null)
					st4.close();
				if (st5 != null)
					st5.close();

				if (res3 != null)
					res3.close();
				if (res4 != null)
					res4.close();
				if (res5 != null)
					res5.close();
			}catch(SQLException e)
			{
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	ArrayList<ArrayList<String>> getInformUnitRoles(String unitName, String agentName, boolean permitted) throws MySQLException {
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		boolean playsRole = false;

		Connection connection = null;

		Statement st6 = null;
		Statement st7 = null;
		Statement st8 = null;
		Statement st9 = null;
		Statement st10 = null;
		Statement st11 = null;

		ResultSet res6 = null;
		ResultSet res7 = null;
		ResultSet res8 = null;
		ResultSet res9 = null;
		ResultSet res10 = null;
		ResultSet res11 = null;

		try {
			connection = db.connect();

			if (permitted)
			{

				st8 = connection.createStatement();


				res8 = st8.executeQuery("SELECT roleName, idaccessibility, idvisibility, idposition FROM roleList WHERE idunitList = (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "')");

				while (res8.next()) {
					ArrayList<String> aux = new ArrayList<String>();
					int idposition = res8.getInt("idposition");
					int idaccessibility = res8.getInt("idaccessibility");
					int idvisibility = res8.getInt("idvisibility");
					st9 = connection.createStatement();
					res9 = st9.executeQuery("SELECT positionName FROM position WHERE idposition =" + idposition);
					res9.next();

					st10 = connection.createStatement();
					res10 = st10.executeQuery("SELECT accessibility FROM accessibility WHERE idaccessibility =" + idaccessibility);
					res10.next();

					st11 = connection.createStatement();
					res11 = st11.executeQuery("SELECT visibility FROM visibility WHERE idvisibility =" + idvisibility);
					res11.next();
					aux.add(res8.getString("roleName"));
					aux.add(res10.getString("accessibility"));
					aux.add(res11.getString("visibility"));
					aux.add(res9.getString("positionName"));
					result.add(aux);
				}
				connection.commit();
				return result;
			}
			else
			{

				st6 = connection.createStatement();
				res6 = st6.executeQuery("SELECT idroleList FROM agentPlayList WHERE idagentList = (SELECT idagentList FROM agentList WHERE agentName ='" + agentName + "')");
				while (res6.next()) {
					int idroleList2 = res6.getInt("idroleList");
					st7 = connection.createStatement();
					res7 = st7.executeQuery("SELECT * FROM roleList WHERE idroleList =" + idroleList2 + " AND idunitList= (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "')");
					if (res7.next()) {
						playsRole = true;
						break;
					}
				}

				st8 = connection.createStatement();

				if (playsRole)
					res8 = st8.executeQuery("SELECT roleName, idaccessibility, idvisibility, idposition FROM roleList WHERE idunitList = (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "') AND (idVisibility = (SELECT idVisibility FROM visibility WHERE visibility ='private') OR idVisibility = (SELECT idVisibility FROM visibility WHERE visibility ='public'))");
				else
					res8 = st8.executeQuery("SELECT roleName, idaccessibility, idvisibility, idposition FROM roleList WHERE idunitList = (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "') AND idVisibility = (SELECT idVisibility FROM visibility WHERE visibility ='public')");
				while (res8.next()) {
					ArrayList<String> aux = new ArrayList<String>();
					int idposition = res8.getInt("idposition");
					int idaccessibility = res8.getInt("idaccessibility");
					int idvisibility = res8.getInt("idvisibility");
					st9 = connection.createStatement();
					res9 = st9.executeQuery("SELECT positionName FROM position WHERE idposition =" + idposition);
					res9.next();

					st10 = connection.createStatement();
					res10 = st10.executeQuery("SELECT accessibility FROM accessibility WHERE idaccessibility =" + idaccessibility);
					res10.next();

					st11 = connection.createStatement();
					res11 = st11.executeQuery("SELECT visibility FROM visibility WHERE idvisibility =" + idvisibility);
					res11.next();
					aux.add(res8.getString("roleName"));
					aux.add(res10.getString("accessibility"));
					aux.add(res11.getString("visibility"));
					aux.add(res9.getString("positionName"));
					result.add(aux);
				}
				connection.commit();
				return result;
			}
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);

		} finally {
			try
			{
				if (connection != null)
					connection.close();

				if (st6 != null)
					st6.close();
				if (st7 != null)
					st7.close();
				if (st8 != null)
					st8.close();
				if (st9 != null)
					st9.close();
				if (st10 != null)
					st10.close();
				if (st11 != null)
					st11.close();


				if (res6 != null)
					res6.close();
				if (res7 != null)
					res7.close();
				if (res8 != null)
					res8.close();
				if (res9 != null)
					res9.close();
				if (res10 != null)
					res10.close();
				if (res11 != null)
					res11.close();
			}catch(SQLException e)
			{
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	ArrayList<String> getInformRole(String roleName, String unitName) throws MySQLException, UnitNotExistsException, RoleNotExistsException {
		ArrayList<String> result = new ArrayList<String>();
		int idunitList;

		Connection connection = null;

		Statement st2 = null;
		Statement st3 = null;

		Statement st6 = null;

		Statement st9 = null;
		Statement st10 = null;
		Statement st11 = null;

		ResultSet res2 = null;
		ResultSet res3 = null;

		ResultSet res6 = null;

		ResultSet res9 = null;
		ResultSet res10 = null;
		ResultSet res11 = null;

		try {
			connection = db.connect();

			st2 = connection.createStatement();
			res2 = st2.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "'");
			if (res2.next())
				idunitList = res2.getInt("idunitList");
			else {
				String message = l10n.getMessage(MessageID.UNIT_NOT_EXISTS, unitName);
				throw new UnitNotExistsException(message);
			}

			st6 = connection.createStatement();
			res6 = st6.executeQuery("SELECT idaccessibility, idposition, idvisibility FROM roleList WHERE roleName ='" + roleName + "' AND idunitList =" + idunitList);
			if (res6.next()) {
				int idposition = res6.getInt("idposition");
				int idaccessibility = res6.getInt("idaccessibility");
				int idvisibility = res6.getInt("idvisibility");
				st9 = connection.createStatement();
				res9 = st9.executeQuery("SELECT positionName FROM position WHERE idposition =" + idposition);
				res9.next();

				st10 = connection.createStatement();
				res10 = st10.executeQuery("SELECT accessibility FROM accessibility WHERE idaccessibility =" + idaccessibility);
				res10.next();

				st11 = connection.createStatement();
				res11 = st11.executeQuery("SELECT visibility FROM visibility WHERE idvisibility =" + idvisibility);
				res11.next();
				result.add(res10.getString("accessibility"));
				result.add(res11.getString("visibility"));
				result.add(res9.getString("positionName"));
				connection.commit();
				return result;
			}
			String message = l10n.getMessage(MessageID.ROLE_NOT_EXISTS, roleName, unitName);
			throw new RoleNotExistsException(message);

		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);

		} catch (UnitNotExistsException e) {
			throw e;

		} catch (RoleNotExistsException e) {
			throw e;

		} finally {
			try
			{
				if (connection != null)
					connection.close();

				if (st2 != null)
					st2.close();
				if (st3 != null)
					st3.close();
				if (st6 != null)
					st6.close();
				if (st9 != null)
					st9.close();
				if (st10 != null)
					st10.close();
				if (st11 != null)
					st11.close();

				if (res2 != null)
					res2.close();
				if (res3 != null)
					res3.close();
				if (res6 != null)
					res6.close();
				if (res9 != null)
					res9.close();
				if (res10 != null)
					res10.close();
				if (res11 != null)
					res11.close();
			}catch(SQLException e)
			{
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	/**
	 * Checks the compatibility with the Jason languaje. 
	 * @param identifier
	 * @return boolean true if is a valid parameter and false if not
	 * @throws MySQLException
	 */
	boolean checkValidIdentifier(String identifier) throws MySQLException
	{
		boolean result= true;

		Connection connection = null;
		Statement st = null;
		ResultSet res = null;

		Pattern p = Pattern.compile("[0-9]+|[a-zA-Z][a-zA-Z_0-9]*");
		Matcher ma = p.matcher(identifier);
		boolean b = ma.matches();

		if(b)
		{
			result = true;
		}
		else
		{
			result = false;
		}

		try {

			connection = db.connect();
			st = connection.createStatement();
			res = st.executeQuery("select * from reservedWordList where reservedWord = '"+identifier+"'");

			if (res.next())
			{
				result = false;
			}


		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {

			try
			{
				if (connection != null)
					connection.close();
				if (st != null)
					st.close();


				if (res != null)
					res.close();




			}catch(SQLException e)
			{
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}

		return result;
	}
	
	boolean checkNormInUnit(String NormName, String UnitName) throws MySQLException
	{
		boolean result = false;
		Connection connection = null;
		Statement st = null;
		ResultSet res = null;

		
		try
		{
			connection = db.connect();
			st = connection.createStatement();
			res = st.executeQuery("select * from normList where normName = '"+NormName+"' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+UnitName+"')");

			if (res.next())
			{
				result = true;
			}

			
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {

			try
			{
				if (connection != null)
					connection.close();
				if (st != null)
					st.close();


				if (res != null)
					res.close();




			}catch(SQLException e)
			{
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
		
		return result;
	}
	
	boolean checkPermitNorms(String AgentName, String UnitName, String ServiceName)
	{
		//TODO por implementar.
		boolean result = true;
		
		return result;
	}
	
	boolean checkFordibbenNorms(String AgentName, String UnitName, String ServiceName)
	{
		//TODO por implementar.
		boolean result = true;
		
		return result;
	}
	
	String createNorm(String UnitName, String NormContent, Norm parsedNorm, Rule normRule) throws MySQLException
	{
		
		String idUnit = "";
		String idDeontic = "";
		String idTarget = "";
		String typeTableName = "";
		String typerowName = "";
		String idTargetValue = "";
		String idAction = "";
		String idNorm = "";
		
		
		Connection connection = null;
		Statement st = null;


		ResultSet res = null;
		ResultSet res2 = null;
		ResultSet res3 = null;
		ResultSet res4 = null;
		ResultSet res5 = null;
		ResultSet res6 = null;
		ResultSet res7 = null;

		try {
			connection = db.connect();
			st = connection.createStatement();
			res = st.executeQuery("SELECT idunitList FROM unitList WHERE unitName = '"+UnitName+"'");
			
			if (res.next())
			{
				idUnit = res.getString("idunitList");

			}

			res2 = st.executeQuery("SELECT iddeontic FROM deontic WHERE deonticdesc = '"+parsedNorm.getDeontic()+"'");
			
			if (res2.next())
			{
				idDeontic = res2.getString("iddeontic");

			}
			
			res3 = st.executeQuery("SELECT idtargetType, targetTable FROM targetType WHERE targetName = '"+parsedNorm.getTargetType()+"'");
			
			if (res3.next())
			{
				idTarget = res3.getString("idtargetType");
				typeTableName = res3.getString("targetTable");

			}
			
			typerowName = typeTableName.replace("List", "");
			typerowName = typeTableName + "Name";
			
			res4 = st.executeQuery("SELECT id"+typeTableName+" FROM "+typeTableName+" WHERE "+typerowName +"='"+parsedNorm.getTargetValue()+"'");
			
			if (res4.next())
			{
				idTargetValue = res4.getString("id"+typeTableName);
			}
			else if (parsedNorm.getTargetType().equals("agentName"))
			{				
				st.executeUpdate("INSERT INTO agentList (agentName) VALUES ('" + parsedNorm.getTargetValue()+"')");
				
				res5 = st.executeQuery("SELECT id"+typeTableName+" FROM "+typeTableName+" WHERE "+typerowName +"='"+parsedNorm.getTargetValue()+"'");
				
				if (res5.next())
				{
					idTargetValue = res5.getString("id"+typeTableName);
				}
			}
			

			res6 = st.executeQuery("SELECT idactionNorm FROM actionNorm WHERE description ='"+parsedNorm.getActionName()+"'");
			
			if (res6.next())
			{
				idAction = res6.getString("idactionNorm");
			}
			
			
			String sql = "INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionnorm, normContent, normRule) " +
			"VALUES (" + idUnit + ",'"+parsedNorm.getId()+"',"+idDeontic+","+idTarget+","+idTargetValue+","+idAction+",'"+NormContent+"','"+normRule.toString()+"')";
			st.executeUpdate(sql);

			
			res7 = st.executeQuery("SELECT idnormList FROM normList WHERE normName ='"+parsedNorm.getId()+"'");
			
			if (res7.next())
			{
				idNorm= res7.getString("idnormList");
			}
			
			for(String actionParam : parsedNorm.getActionParams())
			{	
				st.executeUpdate("INSERT INTO actionNormParam (idnormList, idactionNorm, value) " +
						"VALUES (" + idNorm + ","+idAction+",'"+actionParam+"')");				
			}
				
			
			connection.commit();
			return parsedNorm.getId() + " created";

		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);

		} finally {
			try
			{
				if (connection != null)
					connection.close();
				if (st != null)
					st.close();


				if (res != null)
					res.close();
				if (res2 != null)
					res2.close();
				if (res3 != null)
					res3.close();
				if (res4 != null)
					res4.close();
				if (res5 != null)
					res5.close();
				if (res6 != null)
					res6.close();
				if (res7 != null)
					res7.close();

			}catch(SQLException e)
			{
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}

		}
	}
	
}
