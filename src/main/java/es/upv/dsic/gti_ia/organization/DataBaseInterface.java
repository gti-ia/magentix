package es.upv.dsic.gti_ia.organization;

import jason.asSyntax.Rule;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.upv.dsic.gti_ia.norms.Norm;
import es.upv.dsic.gti_ia.organization.exception.MySQLException;
import es.upv.dsic.gti_ia.organization.exception.ParentUnitNotExistsException;
import es.upv.dsic.gti_ia.organization.exception.RoleNotExistsException;
import es.upv.dsic.gti_ia.organization.exception.THOMASMessages;
import es.upv.dsic.gti_ia.organization.exception.THOMASMessages.MessageID;
import es.upv.dsic.gti_ia.organization.exception.UnitNotExistsException;


/**
 * In this class are included the methods of database access that have arisen during the design of new OMS services
 * 
 * @author Joan Bello
 * 
 * @author Ricard Lopez Fogues
 * 
 * @author Jose Alemany Bordera  -  jalemany1@dsic.upv.es
 * 
 * @autor Soledad Valero - svalero@dsic.upv.es
 */

public class DataBaseInterface {
	private DataBaseAccess db;
	private Connection connection;

	/**
	 * Used for retrieve local messages.
	 */
	private THOMASMessages l10n;

	public DataBaseInterface() {
		db = new DataBaseAccess();
		l10n = new THOMASMessages();
		connection = null;
	}

	String acquireRole(String unitName, String roleName, String agentName) throws MySQLException {
		
		Statement st = null;
		ResultSet res = null;
		
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}	

			st = connection.createStatement();
			
			res = st.executeQuery("SELECT * FROM agentList WHERE agentName = '"+agentName+"'");

			//If the Agent is not yet on the table agentList have to add it
			if (!res.next()) {
				st.executeUpdate("INSERT INTO agentList (agentName) VALUES ('"+agentName+"')"); 
			}

			st.executeUpdate("INSERT INTO agentPlayList (idagentList, idroleList)" +
					" VALUES ((SELECT idagentList FROM agentList WHERE agentName = '"+agentName+"'), (SELECT idroleList FROM roleList WHERE roleName ='" + roleName + "' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "')))");

			connection.commit();
			return roleName + " acquired";
			
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {

			try {
				
				if (close)
					connection.close();
				if (st != null)
					st.close();

				if (res != null)
					res.close();
				
			} catch(SQLException e) {
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
		Statement st = null;
		ResultSet res = null;
		
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st = connection.createStatement();
			res = st.executeQuery("SELECT * FROM agentList WHERE agentName='"+ agentName +"'");
			if (res.next()) 
				return true;

			return false;
			
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try {
				
				if (close)
					connection.close();
				if (st != null)
					st.close();

				if (res != null)
					res.close();

			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}
	
	boolean checkAgentActive(String agentName) throws MySQLException {
		
		Statement st = null;
		ResultSet res = null;
		
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}
			
			st = connection.createStatement();
			
			res = st.executeQuery("SELECT * FROM agentList al INNER JOIN agentPlayList apl ON (apl.idagentList=al.idagentList) "
					+ "WHERE al.agentName='"+ agentName +"'");
			
			if (res.next()) {
				return true;
			} else {
				res = st.executeQuery("SELECT * FROM agentList al INNER JOIN normList nl ON (nl.targetValue=al.idagentlist) INNER"
						+ " JOIN targetType tt ON (nl.idtargettype=tt.idtargettype) WHERE al.agentname='"+ agentName +"' AND tt.targetName='agentName'");
				
				if (res.next()) {
					return true;
				} else {
					return false;
				}
			}
			
		} catch(SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try {
				
				if (close)
					connection.close();
				if (st != null)
					st.close();

				if (res != null)
					res.close();

			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	boolean checkAgentInUnit(String agentName, String unit) throws MySQLException {
		
		Statement st = null;
		Statement st2 = null;
		ResultSet res = null;
		ResultSet res2 = null;
		
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st = connection.createStatement();
			res = st.executeQuery("SELECT idroleList FROM roleList WHERE idunitList = (SELECT idunitList FROM unitList WHERE unitName ='" + unit + "')");
			
			while (res.next()) {
				
				int idRole = res.getInt("idroleList");
				st2 = connection.createStatement();
				res2 = st2.executeQuery("SELECT * FROM agentPlayList WHERE idroleList=" + idRole + " AND idagentList = (SELECT idagentList FROM agentList WHERE agentName ='"+agentName+"')");

				if (res2.next())
					return true;
			}

			return false;
			
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try {
				
				if (close)
					connection.close();
				if (st != null)
					st.close();
				if (st2 != null)
					st2.close();

				if (res != null)
					res.close();
				if (res2 != null)
					res2.close();

			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	boolean checkAgentPlaysRole(String agentName, String role, String unit) throws MySQLException {
		
		Statement st = null;
		Statement st2 = null;
		ResultSet res = null;
		ResultSet res2 = null;
		
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st = connection.createStatement();

			res = st.executeQuery("SELECT idroleList FROM roleList WHERE idunitList = (SELECT idunitList FROM unitList WHERE unitName ='" + unit + "') AND roleName ='" + role + "'");
			
			while (res.next()) {
				
				int roleId = res.getInt("idroleList");
				st2 = connection.createStatement();
				res2 = st2.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = " + roleId + " AND idagentList = (SELECT idagentList FROM agentList WHERE agentName='" + agentName + "')");
				
				if (res2.next())
					return true;
			}

			return false;
			
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try{

				if (close)
					connection.close();
				if (st != null)
					st.close();
				if (st2 != null)
					st2.close();

				if (res != null)
					res.close();
				if (res2 != null)
					res2.close();

			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	boolean checkNoCreatorAgentsInUnit(String unit) throws MySQLException {

		Statement st = null;
		Statement st2 = null;
		ResultSet res = null;
		ResultSet res2 = null;
		
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st = connection.createStatement();
			
			res = st.executeQuery("SELECT idroleList FROM roleList WHERE idunitList = (SELECT idunitList FROM unitList WHERE unitName ='" + unit + "') AND idposition != (SELECT idposition FROM position WHERE positionName ='creator')");
			
			while (res.next()) {

				int roleId = res.getInt("idroleList");
				st2 = connection.createStatement();
				res2 = st2.executeQuery("SELECT * FROM agentPlayList WHERE idroleList =" + roleId);

				if (res2.next())
					return true;
			}

			return false;

		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try {
				
				if (close)
					connection.close();
				if (st != null)
					st.close();
				if (st2 != null)
					st2.close();

				if (res != null)
					res.close();
				if (res2 != null)
					res2.close();
				
			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	boolean checkPlayedRoleInUnit(String role, String unit) throws MySQLException {

		Statement st = null;
		ResultSet res = null;
		
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st = connection.createStatement();
			
			res = st.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE idunitList =(SELECT idunitList FROM unitList WHERE unitName ='" + unit + "') AND roleName='" + role + "')");
			
			if (res.next())
				return true;
			
			return false;
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try {
				
				if (close)
					connection.close();
				if (st != null)
					st.close();

				if (res != null)
					res.close();
				
			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	boolean checkTargetRoleNorm(String role, String unit) throws MySQLException {
		
		Statement st = null;
		ResultSet res = null;

		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st = connection.createStatement();
			res = st.executeQuery("SELECT true FROM normList nl inner join unitList ul on nl.idunitList=ul.idunitList inner join targetType tt on tt.idtargetType=nl.idtargetType inner join roleList rl on rl.idroleList=nl.targetvalue where ul.unitName='"+unit+"' and tt.targetName='roleName' and rl.roleName='"+role+"'");
			
			if (res.next())
				return true;

			return false;
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try {
				
				if (close)
					connection.close();
				if (st != null)
					st.close();
			
				if (res != null)
					res.close();
			
			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	boolean checkPosition(String agent, String position) throws MySQLException {

		Statement st = null;
		Statement st2 = null;
		ResultSet res = null;
		ResultSet res2 = null;
		
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st = connection.createStatement();
			
			res = st.executeQuery("SELECT idroleList FROM agentPlayList WHERE idagentList = (SELECT idagentList FROM agentList WHERE agentName ='" + agent + "')");
			
			while (res.next()) {
				
				int idRole = res.getInt("idroleList");
				st2 = connection.createStatement();
				res2 = st2.executeQuery("SELECT * FROM position WHERE idposition = (SELECT idposition FROM roleList WHERE idroleList =" + idRole+")");
				
				if (res2.next() && res2.getString("positionName").equalsIgnoreCase(position))
					return true;
			}
			
			return false;
			
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try {
				
				if (close)
					connection.close();
				if (st != null)
					st.close();
				if (st2 != null)
					st2.close();

				if (res != null)
					res.close();
				if (res2 != null)
					res2.close();
				
			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	boolean checkTargetType(String targetType) throws MySQLException {

		Statement st = null;
		ResultSet res = null;
		
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}
			
			st = connection.createStatement();
			
			res = st.executeQuery("SELECT * FROM targetType WHERE targetName = '"+targetType+"'");

			if (res.next())
				return true;
			
			return false;

		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try {
				
				if (close)
					connection.close();
				if (st != null)
					st.close();

				if (res != null)
					res.close();

			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	boolean checkPositionInUnit(String agent, String position, String unit) throws MySQLException {

		Statement st = null;
		Statement st2 = null;
		ResultSet res = null;
		ResultSet res2 = null;

		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st = connection.createStatement();
			res = st.executeQuery("SELECT idroleList FROM agentPlayList WHERE idagentList = (SELECT idagentList FROM agentList WHERE agentName ='" + agent + "')");
			while (res.next()) {
				
				int idRole = res.getInt("idroleList");
				st2 = connection.createStatement();
				res2 = st2.executeQuery("SELECT * FROM position WHERE idposition = (SELECT idposition FROM roleList WHERE idroleList =" + idRole + " AND idunitList = (SELECT idunitList FROM unitList WHERE unitName ='" + unit + "'))");
				
				if (res2.next() && res2.getString("positionName").equalsIgnoreCase(position))
					return true;
			}
			
			return false;
			
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try {
				
				if (close)
					connection.close();
				if (st != null)
					st.close();
				if (st2 != null)
					st2.close();

				if (res != null)
					res.close();
				if (res2 != null)
					res2.close();
				
			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	boolean checkRole(String role, String unit) throws MySQLException {
		
		Statement st = null;
		ResultSet res = null;
		
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}
			
			st = connection.createStatement();
			
			res = st.executeQuery("SELECT * FROM roleList WHERE idunitList = (SELECT idunitList FROM unitList WHERE unitName ='" + unit + "') AND roleName ='" + role + "'");
			
			if (res.next())
				return true;

			return false;
			
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try {
				
				if (close)
					connection.close();
				if (st != null)
					st.close();

				if (res != null)
					res.close();
				
			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	boolean checkSubUnits(String unit) throws MySQLException {
		
		Statement st = null;
		ResultSet res = null;

		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st = connection.createStatement();
			
			res = st.executeQuery("SELECT * FROM unitHierarchy WHERE idParentUnit = (SELECT idunitList FROM unitList WHERE unitName ='" + unit + "')");
			
			if (res.next())
				return true;

			return false;
			
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try {
				
				if (close)
					connection.close();
				if (st != null)
					st.close();

				if (res != null)
					res.close();
				
			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	boolean checkUnit(String unit) throws MySQLException {
		
		Statement st = null;
		ResultSet res = null;

		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st = connection.createStatement();
			
			res = st.executeQuery("SELECT * FROM unitList WHERE unitName ='" + unit + "'");
			
			if (res.next())
				return true;
			
			return false;
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try {
				
				if (close)
					connection.close();
				if (st != null)
					st.close();
				
				if (res != null)
					res.close();
				
			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	boolean checkVirtualUnit(String unit) throws MySQLException {
		return unit.equalsIgnoreCase("virtual");
	}

	String createRole(String roleName, String unitName, String accessibility, String visibility, String position) throws MySQLException {

		Statement st = null;
		
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st = connection.createStatement();
			st.executeUpdate("INSERT INTO roleList (roleName, idunitList, idposition, idaccessibility, idvisibility) VALUES ('" + roleName + "', (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "'),(SELECT idposition FROM position WHERE positionName ='" + position + "'),(SELECT idaccessibility FROM accessibility WHERE accessibility ='" + accessibility + "'),(SELECT idvisibility FROM visibility WHERE visibility ='" + visibility + "'))");

			connection.commit();
			return roleName + " created";

		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);

		} finally {
			try {
				
				if (close)
					connection.close();
				if (st != null)
					st.close();
				
			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	String createUnit(String unitName, String unitType, String parentUnitName, String agentName, String creatorRoleName) throws MySQLException {
		
		Statement st = null;
		Statement st2 = null;
		Statement st3 = null;
		Statement st4 = null;
		Statement st5 = null;
		ResultSet res = null;
		ResultSet res2 = null;
		ResultSet res5 = null;

		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}



			st = connection.createStatement();
			st.executeUpdate("INSERT INTO unitList (unitName, idunitType) VALUES ('" + unitName + "', (SELECT idunitType FROM unitType WHERE unitTypeName ='" + unitType + "'))");

			st2 = connection.createStatement();
			res2 = st2.executeQuery("SELECT LAST_INSERT_ID()");

			if (res2.next()) {
				int insertedUnitId = res2.getInt(1);

				st3 = connection.createStatement();
				st3.executeUpdate("INSERT INTO unitHierarchy (idParentUnit, idChildUnit) VALUES ((SELECT idunitList FROM unitList WHERE unitName ='" + parentUnitName + "'), " + insertedUnitId + ")");

				st4 = connection.createStatement();
				st4.executeUpdate("INSERT INTO roleList (roleName, idunitList, idposition, idaccessibility, idvisibility) VALUES ('" + creatorRoleName + "', " + insertedUnitId + ", (SELECT idposition FROM position WHERE positionName ='creator'),(SELECT idaccessibility FROM accessibility WHERE accessibility ='internal'), (SELECT idVisibility FROM visibility WHERE visibility ='private'))");

				st5 = connection.createStatement();
				st5.executeUpdate("INSERT INTO agentPlayList (idagentList, idroleList) VALUES ((SELECT idagentList FROM agentList WHERE agentName='" + agentName + "'), LAST_INSERT_ID())");
			}
			
			connection.commit();
			return unitName + " created";

		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);

		} finally {
			try {
				
				if (close)
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

			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	String deallocateRole(String roleName, String unitName, String targetAgentName) throws MySQLException {
		
		Statement st = null;

		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}
			
			st = connection.createStatement();
			
			st.executeUpdate("DELETE FROM agentPlayList WHERE idagentList = (SELECT idagentList FROM agentList WHERE agentName = '" + targetAgentName + "') AND idroleList = (SELECT idroleList FROM roleList WHERE roleName ='" + roleName + "' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "'))");
			
			//Check if last role played and has no associated norm, then remove it from the table agentList
			if (!checkAgentActive(targetAgentName))
				deleteAgent(targetAgentName);

			connection.commit();
			return roleName + " deallocated";

		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);

		} finally {
			try {
				
				if (close)
					connection.close();
				if (st != null)
					st.close();
				
			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	String deleteRole(String roleName, String unitName) throws MySQLException{
		
		Statement st = null;
		
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}
			
			st = connection.createStatement();

			st.executeUpdate("DELETE FROM roleList WHERE roleName ='" + roleName + "' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "')");
			
			connection.commit();
			return roleName + " deleted";

		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try {
				
				if (close)
					connection.close();
				if (st != null)
					st.close();

			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	String deleteUnit(String unitName) throws MySQLException {
		
		Statement st = null;
		Statement st2 = null;
		Statement st3 = null;
		Statement st4 = null;
		Statement st5 = null;
		Statement st6 = null;
		Statement st7 = null;
		Statement st8 = null;
		ResultSet res = null;

		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}
			
			st = connection.createStatement();
			res = st.executeQuery("SELECT al.agentName FROM agentList al INNER JOIN agentPlayList apl ON (apl.idagentList=al.idagentList) "
					+ "INNER JOIN roleList rl ON (rl.idroleList=apl.idroleList) INNER JOIN unitList ul ON (rl.idunitList=ul.idunitList) "
					+ "WHERE ul.unitName='"+ unitName +"'");
			
			st2 = connection.createStatement();
			st2.executeUpdate("DELETE FROM agentPlayList WHERE idroleList IN (SELECT rl.idroleList FROM roleList rl INNER JOIN "
					+ "unitList ul ON (rl.idunitList=ul.idunitList) WHERE ul.unitName='"+ unitName +"')");
			
			while (res.next()) {
				String agent = res.getString("agentName");
				if (!checkAgentActive(agent))
					deleteAgent(agent);
			}
			
			st3 = connection.createStatement();
			st3.executeUpdate("DELETE FROM roleList WHERE idunitList=(SELECT ul.idunitList FROM unitList ul WHERE ul.unitName='"+ unitName +"')");
			
			st4 = connection.createStatement();
			res = st4.executeQuery("SELECT al.agentName FROM agentList al INNER JOIN normList nl ON (nl.targetValue=al.idagentList) INNER JOIN targetType tt ON (nl.idtargetType=tt.idtargetType) INNER JOIN unitList ul ON (ul.idunitList=nl.idunitList) WHERE tt.targetName='agentName' AND ul.unitName='"+ unitName +"'");
			
			st5 = connection.createStatement();
			st5.executeUpdate("DELETE FROM actionNormParam USING actionNormParam INNER JOIN normList INNER JOIN unitList " +
					"ON (actionNormParam.idnormList = normList.idnormList AND normList.idunitList=unitList.idunitList)" +
					"WHERE unitName ='"+ unitName +"'");
			
			st6 = connection.createStatement();
			st6.executeUpdate("DELETE FROM normList USING normList INNER JOIN unitList ON (normList.idunitList=unitList.idunitList)" +
					"WHERE unitName='"+ unitName +"'");
			
			while (res.next()) {
				String agent = res.getString("agentName");
				if (!checkAgentActive(agent))
					deleteAgent(agent);
			}
			
			st7 = connection.createStatement();
			st7.executeUpdate("DELETE FROM unitHierarchy WHERE idChildUnit = (SELECT ul.idunitList FROM unitList ul WHERE ul.unitName ='" + unitName + "')");

			st8 = connection.createStatement();
			st8.executeUpdate("DELETE FROM unitList WHERE unitName ='" + unitName + "'");

			connection.commit();
			return unitName + " deleted";

		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try {
				
				if (close)
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
				
				if (res != null)
					res.close();

			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	String deleteAgent(String agentName) throws MySQLException {
		
		Statement st = null;
		
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}
			
			st = connection.createStatement();
			
			st.executeUpdate("DELETE FROM agentList WHERE agentName='"+ agentName +"'");
			
			connection.commit();
			return agentName + " deleted";
			
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try {
				
				if (close)
					connection.close();
				if (st != null)
					st.close();

			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}
	
	String deleteNorm(String NormName, String UnitName) throws MySQLException {
		
		Statement st = null;
		Statement st2 = null;
		ResultSet res = null;

		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}
			
			st = connection.createStatement();
			
			res = st.executeQuery("SELECT al.agentName FROM normList nl INNER JOIN targetType tt ON (nl.idtargettype=tt.idtargettype) " +
					"INNER JOIN agentList al ON (al.idagentList=nl.targetValue) WHERE tt.targetName='agentName' and nl.normName='"+ NormName +"'");

			st2 = connection.createStatement();
			
			st2.executeUpdate("DELETE FROM actionNormParam USING actionNormParam INNER JOIN normList INNER JOIN unitList " +
					"ON (actionNormParam.idnormList = normList.idnormList AND normList.idunitList=unitList.idunitList)" +
					"WHERE unitName = '"+UnitName+"' AND normName = '"+NormName+"'");
			st2.executeUpdate("DELETE FROM normList USING normList INNER JOIN unitList ON (normList.idunitList=unitList.idunitList)" +
					"WHERE unitName='"+UnitName+"' AND normName='"+NormName+"'");

			if (res.next()) {
				
				String agentName = res.getString("agentName");
				
				//Check if last role played and has no associated norm, then remove it from the table agentList
				if (!checkAgentActive(agentName))
					deleteAgent(agentName);
			}
			
			connection.commit();
			return NormName + " deleted";

		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try {
				
				if (close)
					connection.close();
				if (st != null)
					st.close();
				if (st2 != null)
					st2.close();
				
				if (res != null)
					res.close();

			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}
	
	String joinUnit(String unitName, String parentName) throws MySQLException {

		Statement st = null;

		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}
			
			st = connection.createStatement();
			
			st.executeUpdate("DELETE FROM unitHierarchy WHERE idChildUnit = (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "')");

			st.executeUpdate("INSERT INTO unitHierarchy (idParentUnit, idChildUnit) VALUES ((SELECT idunitList FROM unitList WHERE unitName ='" + parentName + "'),(SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "'))");

			connection.commit();
			return unitName + " + jointed to " + parentName;

		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try {
				
				if (close)
					connection.close();
				if (st != null)
					st.close();

			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	String leaveRole(String unitName, String roleName, String agentName) throws MySQLException {

		Statement st = null;

		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}
			
			st = connection.createStatement();
			
			st.executeUpdate("DELETE FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE idunitList = (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "') AND roleName ='" + roleName + "') AND idagentList = (SELECT idagentList FROM agentList WHERE agentName='" + agentName + "')");

			//Check if last role played and has no associated norm or doesn't play any role, then remove it from the table agentList
			if (!checkAgentActive(agentName))
				deleteAgent(agentName);

			connection.commit();
			return roleName + " left";

		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try {
				
				if (close)
					connection.close();
				if (st != null)
					st.close();
				
			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	String getUnitType(String unitName) throws MySQLException {

		Statement st = null;
		ResultSet res = null;
		
		String unitType = "";
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}
			
			st = connection.createStatement();
			
			res = st.executeQuery("SELECT unitTypeName FROM unitType WHERE idunitType = (SELECT idunitType FROM unitList WHERE unitName ='" + unitName + "')");

			if (res.next())
				unitType = res.getString("unitTypeName");

			return unitType;

		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try {
				
				if (close)
					connection.close();
				if (st != null)
					st.close();

				if (res != null)
					res.close();

			} catch(SQLException e) {
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
	
	ArrayList<String> getParentsUnit(String unitName) throws MySQLException {

		Statement st = null;
		Statement st2 = null;
		ResultSet res = null;
		ResultSet res2 = null;

		ArrayList<String> result = new ArrayList<String>();
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st = connection.createStatement();
			
			res = st.executeQuery("SELECT idParentUnit FROM unitHierarchy WHERE idChildUnit =(SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "')");
			
			if (res.next()) {
				
				int idParentUnit = res.getInt("idParentUnit");
				st2 = connection.createStatement();
				res2 = st2.executeQuery("SELECT unitName FROM unitList WHERE idunitList =" + idParentUnit);
				
				if (res2.next())
					result.add(res2.getString("unitName"));
				else
					result.add("virtual");
			}
			
			return result;

		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try {
				
				if (close)
					connection.close();
				if (st != null)
					st.close();
				if (st2 != null)
					st2.close();

				if (res != null)
					res.close();
				if (res2 != null)
					res2.close();
				
			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	ArrayList<ArrayList<String>> getInformAgentRole(String requestedAgentName, String agentName) throws MySQLException {
		
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

		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st = connection.createStatement();

			res = st.executeQuery("SELECT idroleList FROM agentPlayList WHERE idagentList = (SELECT idagentList FROM agentList WHERE agentName ='" + requestedAgentName + "')");
			
			while (res.next()) {
			
				int idroleList = res.getInt("idroleList");
				st2 = connection.createStatement();
				res2 = st2.executeQuery("SELECT idunitList, roleName FROM roleList WHERE idroleList =" + idroleList + " AND idvisibility = (SELECT idVisibility FROM visibility WHERE visibility ='public')");
				
				if (res2.next()) {
					
					ArrayList<String> aux = new ArrayList<String>();
					int idunitList = res2.getInt("idunitList");
					String roleName = res2.getString("roleName");
					st3 = connection.createStatement();
					res3 = st3.executeQuery("SELECT unitName FROM unitList WHERE idunitList =" + idunitList);
					
					if (res3.next()) {
					
						aux.add(roleName);
						aux.add(res3.getString("unitName"));
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
			try {
				
				if (close)
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
				
			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	ArrayList<ArrayList<String>> getInformAgentRolesPlayedInUnit(String unitName, String targetAgentName) throws MySQLException {

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

		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

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
			try {
				
				if (close)
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
				
			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	ArrayList<ArrayList<String>> getAgentsRolesInUnit(String unitName) throws MySQLException {

		Statement st12 = null;
		Statement st13 = null;
		Statement st14 = null;
		ResultSet res12 = null;
		ResultSet res13 = null;
		ResultSet res14 = null;

		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st12 = connection.createStatement();

			res12 = st12.executeQuery("SELECT idroleList, roleName FROM roleList WHERE idunitList = (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "')");
			while (res12.next()) {

				String roleName = res12.getString("roleName");
				int idroleList = res12.getInt("idroleList");


				st14 = connection.createStatement();

				res14 = st14.executeQuery("(SELECT idagentList FROM agentPlayList WHERE idroleList =" + idroleList+")");

				while (res14.next()) {
					
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
			try {
				
				if (close)
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

			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	ArrayList<ArrayList<String>> getAgentsVisibilityRolesInUnit(String unitName) throws MySQLException {

		Statement st12 = null;
		Statement st13 = null;
		Statement st14 = null;
		ResultSet res12 = null;
		ResultSet res13 = null;
		ResultSet res14 = null;

		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st12 = connection.createStatement();

			res12 = st12.executeQuery("SELECT idroleList, roleName FROM roleList WHERE idunitList = (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "') AND idVisibility = (SELECT idVisibility FROM visibility WHERE visibility ='public')");
			while (res12.next()) {

				String roleName = res12.getString("roleName");
				int idroleList = res12.getInt("idroleList");


				st14 = connection.createStatement();

				res14 = st14.executeQuery("(SELECT idagentList FROM agentPlayList WHERE idroleList =" + idroleList+")");

				while (res14.next()) {
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
		} finally {
			try {
				if (close)
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

			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	ArrayList<String> getAgentsPlayingRoleInUnit(String unitName, String roleName) throws MySQLException {

		Statement st12 = null;
		Statement st13 = null;
		Statement st14 = null;
		ResultSet res12 = null;
		ResultSet res13 = null;
		ResultSet res14 = null;

		ArrayList<String> result = new ArrayList<String>();
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st12 = connection.createStatement();

			res12 = st12.executeQuery("SELECT idroleList FROM roleList WHERE idunitList = (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "') AND roleName ='" + roleName + "'");
			while (res12.next()) {
				int idroleList = res12.getInt("idroleList");

				st14 = connection.createStatement();
				res14 = st14.executeQuery("(SELECT idagentList FROM agentPlayList WHERE idroleList =" + idroleList+")");

				while (res14.next()) {
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
			try {
				
				if (close)
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

			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	ArrayList<ArrayList<String>> getAgentsPlayingPositionInUnit(String unitName, String positionValue) throws MySQLException{

		Statement st3 = null;
		Statement st4 = null;
		Statement st5 = null;
		ResultSet res3 = null;
		ResultSet res4 = null;
		ResultSet res5 = null;

		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st3 = connection.createStatement();

			res3 = st3.executeQuery("SELECT idroleList, roleName FROM roleList WHERE idunitList = (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "') AND idposition = (SELECT idposition FROM position WHERE positionName ='" + positionValue + "')");

			while (res3.next()) {
				int idroleList = res3.getInt("idroleList");
				String roleName = res3.getString("roleName");

				st5 = connection.createStatement();
				res5 = st5.executeQuery("(SELECT idagentList FROM agentPlayList WHERE idroleList =" + idroleList+")");

				while(res5.next()) {
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
			try {
				
				if (close)
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

			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	ArrayList<String> getAgentsPlayingRolePositionInUnit(String unitName, String roleName, String positionValue) throws MySQLException {
		// TODO deurien tornarse els agents q juguen el role roleName, amb la
		// posicio positionValue en la unitat unitName?
		
		Statement st10 = null;
		Statement st11 = null;
		ResultSet res10 = null;
		ResultSet res11 = null;

		ArrayList<String> result = new ArrayList<String>();
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st11 = connection.createStatement();

			res11 = st11.executeQuery("SELECT idagentList FROM agentPlayList WHERE idroleList = (SELECT idroleList FROM roleList WHERE idUnitList= (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "') AND roleName ='" + roleName + "' AND idposition = (SELECT idposition FROM position WHERE positionName ='" + positionValue + "'))");

			while(res11.next()) {

				int idagentList = res11.getInt("idagentList");
				st10 = connection.createStatement();
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
			try {
				
				if (close)
					connection.close();
				if (st10 != null)
					st10.close();
				if (st11 != null)
					st11.close();

				if (res10 != null)
					res10.close();
				if (res11 != null)
					res11.close();

			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	ArrayList<ArrayList<String>> getAgentsPlayingVisibilityPositionInUnit(String unitName, String positionValue) throws MySQLException {

		Statement st3 = null;
		Statement st4 = null;
		Statement st5 = null;
		ResultSet res3 = null;
		ResultSet res4 = null;
		ResultSet res5 = null;

		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st3 = connection.createStatement();

			res3 = st3.executeQuery("SELECT idroleList, roleName FROM roleList WHERE idunitList = (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "') AND idposition = (SELECT idposition FROM position WHERE positionName ='" + positionValue + "') AND idVisibility = (SELECT idVisibility FROM visibility WHERE visibility ='public')");

			while (res3.next()) {
				
				int idroleList = res3.getInt("idroleList");
				String roleName = res3.getString("roleName");

				st5 = connection.createStatement();
				res5 = st5.executeQuery("(SELECT idagentList FROM agentPlayList WHERE idroleList =" + idroleList+")");

				while(res5.next()) {
					
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
			try {
				
				if (close)
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

			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}
	
	int getInformQuantityAgentsRolesInUnit(String unitName) throws MySQLException {

		Statement st5 = null;
		Statement st10 = null;
		Statement st11 = null;
		ResultSet res5 = null;
		ResultSet res10 = null;
		ResultSet res11 = null;

		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			Set<String> agentNames = new HashSet<String>();
			st5 = connection.createStatement();

			res5 = st5.executeQuery("SELECT idroleList FROM roleList WHERE idunitList= (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "')");
			
			while(res5.next()) {
			
				int idroleList = res5.getInt("idroleList");
				st11 = connection.createStatement();
				res11 = st11.executeQuery("SELECT idagentList FROM agentPlayList WHERE idroleList =" + idroleList);

				while(res11.next()) {
					
					int idagentList = res11.getInt("idagentList");
					st10 = connection.createStatement();
					res10 = st10.executeQuery("SELECT agentName FROM agentList WHERE idagentList = " + idagentList);
					
					if (res10.next())
						agentNames.add(res10.getString("agentName"));
				}
			}
			connection.commit();
			return agentNames.size();
			
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try {
				
				if (close)
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

			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	int getInformQuantityAgentsVisibilityRolesInUnit(String unitName) throws MySQLException {

		Statement st5 = null;
		Statement st10 = null;
		Statement st11 = null;
		ResultSet res5 = null;
		ResultSet res10 = null;
		ResultSet res11 = null;

		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			Set<String> agentNames = new HashSet<String>();
			st5 = connection.createStatement();

			res5 = st5.executeQuery("SELECT idroleList FROM roleList WHERE idunitList= (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "') AND idVisibility =(SELECT idVisibility FROM visibility WHERE visibility ='public')");
			
			while(res5.next()) {
				
				int idroleList = res5.getInt("idroleList");
				st11 = connection.createStatement();
				res11 = st11.executeQuery("SELECT idagentList FROM agentPlayList WHERE idroleList =" + idroleList);

				while(res11.next()) {
					
					int idagentList = res11.getInt("idagentList");
					st10 = connection.createStatement();
					res10 = st10.executeQuery("SELECT agentName FROM agentList WHERE idagentList = " + idagentList);
					
					if (res10.next())
						agentNames.add(res10.getString("agentName"));
				}
			}
			connection.commit();
			return agentNames.size();
			
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);

		} finally {
			try {
				
				if (close)
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

			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	int getInformQuantityAgentsPlayingRoleInUnit(String unitName, String roleName) throws MySQLException {
		
		Statement st5 = null;
		Statement st10 = null;
		Statement st11 = null;
		ResultSet res5 = null;
		ResultSet res10 = null;
		ResultSet res11 = null;

		int idroleList, cont = 0;
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st5 = connection.createStatement();

			res5 = st5.executeQuery("SELECT idroleList FROM roleList WHERE idunitList = (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "') AND roleName ='" + roleName + "'");
			
			if (res5.next())
				idroleList = res5.getInt("idroleList");
			else
				return 0;

			st11 = connection.createStatement();
			res11 = st11.executeQuery("SELECT idagentList FROM agentPlayList WHERE idroleList =" + idroleList);

			while(res11.next()) {
				
				int idagentList = res11.getInt("idagentList");
				st10 = connection.createStatement();
				res10 = st10.executeQuery("SELECT DISTINCT agentName FROM agentList WHERE idagentList = " + idagentList);
				
				if (res10.next())
					cont++;
			}
			connection.commit();
			return cont;
			
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try {
				
				if (close)
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

			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	int getInformQuantityAgentsPlayingPositionInUnit(String unitName, String positionValue) throws MySQLException{
		
		Statement st5 = null;
		Statement st10 = null;
		Statement st11 = null;
		ResultSet res5 = null;
		ResultSet res10 = null;
		ResultSet res11 = null;

		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			Set<String> agentNames = new HashSet<String>();
			st5 = connection.createStatement();


			res5 = st5.executeQuery("SELECT idroleList FROM roleList WHERE idunitList= (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "') AND idposition = (SELECT idposition FROM position WHERE positionName ='" + positionValue + "')");

			while(res5.next()) {
				
				int idroleList = res5.getInt("idroleList");
				st11 = connection.createStatement();
				res11 = st11.executeQuery("SELECT idagentList FROM agentPlayList WHERE idroleList =" + idroleList);

				while(res11.next()) {
					
					int idagentList = res11.getInt("idagentList");
					st10 = connection.createStatement();
					res10 = st10.executeQuery("SELECT agentName FROM agentList WHERE idagentList = " + idagentList);
					
					if (res10.next())
						agentNames.add(res10.getString("agentName"));
				}
			}
			connection.commit();
			return agentNames.size();
			
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try {
				
				if (close)
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

			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	int getInformQuantityAgentsPlayingRolePositionInUnit(String unitName, String roleName, String positionValue) throws MySQLException {

		Statement st5 = null;
		Statement st10 = null;
		Statement st11 = null;
		ResultSet res5 = null;
		ResultSet res10 = null;
		ResultSet res11 = null;

		int idroleList, cont = 0;
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st5 = connection.createStatement();

			res5 = st5.executeQuery("SELECT idroleList FROM roleList WHERE idunitList = (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "') AND roleName ='" + roleName + "' AND idposition = (SELECT idposition FROM position WHERE positionName ='" + positionValue + "')");

			if (res5.next())
				idroleList = res5.getInt("idroleList");
			else
				return 0;

			st11 = connection.createStatement();
			res11 = st11.executeQuery("SELECT idagentList FROM agentPlayList WHERE idroleList =" + idroleList);

			while(res11.next()) {
				
				int idagentList = res11.getInt("idagentList");
				st10 = connection.createStatement();
				res10 = st10.executeQuery("SELECT agentName FROM agentList WHERE idagentList = "+idagentList);
				
				if (res10.next())
					cont++;
			}
			connection.commit();
			return cont;
			
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try {
				
				if (close)
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

			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	int getInformQuantityAgentsPlayingVisibilityPositionInUnit(String unitName, String positionValue) throws MySQLException {
		
		Statement st5 = null;
		Statement st10 = null;
		Statement st11 = null;
		ResultSet res5 = null;
		ResultSet res10 = null;
		ResultSet res11 = null;

		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}
			
			Set<String> agentNames = new HashSet<String>();
			st5 = connection.createStatement();

			res5 = st5.executeQuery("SELECT idroleList FROM roleList WHERE idunitList= (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "') AND idposition = (SELECT idposition FROM position WHERE positionName ='" + positionValue + "') AND idvisibility = (SELECT idVisibility FROM visibility WHERE visibility ='public')");


			while (res5.next()) {
				
				int idroleList = res5.getInt("idroleList");
				st11 = connection.createStatement();
				res11 = st11.executeQuery("SELECT idagentList FROM agentPlayList WHERE idroleList =" + idroleList);

				while(res11.next()) {
					
					int idagentList = res11.getInt("idagentList");
					st10 = connection.createStatement();
					res10 = st10.executeQuery("SELECT agentName FROM agentList WHERE idagentList = " + idagentList);
					if (res10.next())
						agentNames.add(res10.getString("agentName"));
				}
			}
			connection.commit();
			return agentNames.size();
			
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try {
				
				if (close)
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

			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	ArrayList<String> getInformUnit(String unitName) throws MySQLException {
		
		Statement st = null;
		ResultSet res = null;

		ArrayList<String> result = new ArrayList<String>();
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st = connection.createStatement();
			res = st.executeQuery("SELECT ulp.unitName, ut.unitTypeName FROM unitList ulh INNER JOIN unitType ut ON (ulh.idunitType=ut.idunitType) INNER JOIN "
					+ "unitHierarchy uh ON (uh.idChildUnit=ulh.idunitList) INNER JOIN unitList ulp ON (uh.idParentUnit=ulp.idunitList) WHERE ulh.unitName='"+ unitName +"'");
			
			if (res.next()) {
				result.add(res.getString("unitTypeName"));
				result.add(res.getString("unitName"));
			}
			
			connection.commit();
			return result;
			
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try {
				
				if (close)
					connection.close();
				if (st != null)
					st.close();

				if (res != null)
					res.close();
				
			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	ArrayList<ArrayList<String>> getInformUnitRoles(String unitName, boolean permitted) throws MySQLException {
		
		Statement st = null;
		ResultSet res = null;

		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			if (permitted) {

				st = connection.createStatement();

				res = st.executeQuery("SELECT rl.roleName, a.accessibility, v.visibility, p.positionName FROM unitList ul INNER JOIN roleList rl ON"
						+ " (ul.idunitList=rl.idunitList) INNER JOIN accessibility a ON (a.idaccessibility=rl.idaccessibility) INNER JOIN visibility"
						+ " v ON (rl.idvisibility=v.idvisibility) INNER JOIN position p ON (p.idposition=rl.idposition) WHERE ul.unitName='"
						+ unitName +"'");

				while (res.next()) {
					ArrayList<String> aux = new ArrayList<String>();
					aux.add(res.getString("roleName"));
					aux.add(res.getString("accessibility"));
					aux.add(res.getString("visibility"));
					aux.add(res.getString("positionName"));
					result.add(aux);
				}
			} else {

				st = connection.createStatement();

				res = st.executeQuery("SELECT rl.roleName, a.accessibility, v.visibility, p.positionName FROM unitList ul INNER JOIN roleList rl ON"
						+ " (ul.idunitList=rl.idunitList) INNER JOIN accessibility a ON (a.idaccessibility=rl.idaccessibility) INNER JOIN visibility"
						+ " v ON (rl.idvisibility=v.idvisibility) INNER JOIN position p ON (p.idposition=rl.idposition) WHERE ul.unitName='"
						+ unitName +"' AND v.visibility='public'");

				while (res.next()) {
					ArrayList<String> aux = new ArrayList<String>();
					aux.add(res.getString("roleName"));
					aux.add(res.getString("accessibility"));
					aux.add(res.getString("visibility"));
					aux.add(res.getString("positionName"));
					result.add(aux);
				}
			}
			connection.commit();
			return result;
			
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try {
				
				if (close)
					connection.close();
				if (st != null)
					st.close();

				if (res != null)
					res.close();
				
			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}


	ArrayList<String> getInformRole(String roleName, String unitName) throws MySQLException, UnitNotExistsException, RoleNotExistsException {

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

		ArrayList<String> result = new ArrayList<String>();
		int idunitList;
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

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
			try {
				
				if (close)
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
				
			} catch(SQLException e) {
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
	boolean checkValidIdentifier(String identifier) throws MySQLException {

		Statement st = null;
		ResultSet res = null;

		Pattern p = Pattern.compile("[0-9]+|[a-zA-Z][a-zA-Z_0-9]*");
		Matcher ma = p.matcher(identifier);
		boolean b = ma.matches();
		
		boolean result= true;
		boolean close = false;

		if(b)
			result = true;
		else
			result = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}
			
			st = connection.createStatement();
			res = st.executeQuery("select * from reservedWordList where reservedWord = '"+identifier+"'");

			if (res.next())
				result = false;

		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try {
				
				if (close)
					connection.close();
				if (st != null)
					st.close();

				if (res != null)
					res.close();
				
			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
		return result;
	}

	String getNormContent(String NormName, String UnitName) throws MySQLException {
		
		Statement st = null;
		ResultSet res = null;

		String result = "";
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}
			
			st = connection.createStatement();
			res = st.executeQuery("SELECT n1.normContent FROM normList n1, unitList u1 WHERE n1.normname='"+NormName+"' AND u1.unitName='"+UnitName+"'");

			if (res.next())
				result = res.getString("normContent");

		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try {
				
				if (close)
					connection.close();
				if (st != null)
					st.close();

				if (res != null)
					res.close();

			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
		return result;
	}
	
	boolean checkNormName(String NormName, String UnitName) throws MySQLException {
		
		Statement st = null;
		ResultSet res = null;

		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}
			
			st = connection.createStatement();
			res = st.executeQuery("select * from normList where normName = '"+NormName+"' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = '"+UnitName+"')");

			if (res.next())
				return true;
			
			return false;
			
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try {
				
				if (close)
					connection.close();
				if (st != null)
					st.close();

				if (res != null)
					res.close();

			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	ArrayList<String> getAgentNormRules(String UnitName, String AgentName, String Deontic, String Service) throws MySQLException {

		Statement st = null;
		ResultSet res = null;
		ResultSet res2 = null;

		ArrayList<String> result = new ArrayList<String>();
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}
			
			st = connection.createStatement();
			res = st.executeQuery("select distinct nl.normRule from normList nl inner join unitList ul on nl.idunitList=ul.idunitList inner join targetType tt on nl.idtargetType=tt.idtargetType inner join deontic d on d.iddeontic=nl.iddeontic inner join actionNorm an on an.idactionnorm=nl.idactionnorm" +
					" where ul.unitName='"+UnitName+"' and d.deonticdesc='"+Deontic+"' and an.description='"+Service+"' and tt.targetName='agentName' and nl.targetValue=-1 ");

			while(res.next()) {
				result.add(res.getString("normRule"));
			}

			res2 = st.executeQuery("select distinct nl.normRule from normList nl inner join unitList ul on nl.idunitList=ul.idunitList inner join targetType tt on nl.idtargetType=tt.idtargetType inner join deontic d on d.iddeontic=nl.iddeontic inner join actionNorm an on an.idactionnorm=nl.idactionnorm inner join agentList al on nl.targetValue=al.idagentList" +
					" where ul.unitName='"+UnitName+"' and d.deonticdesc='"+Deontic+"' and an.description='"+Service+"' and tt.targetName='agentName' and al.agentName='"+AgentName+"'");

			while(res2.next()) {
				result.add(res2.getString("normRule"));
			}

			return result;

		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try {
				
				if (close)
					connection.close();
				if (st != null)
					st.close();

				if (res != null)
					res.close();
				if (res2 != null)
					res2.close();

			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	ArrayList<String> getPositionNormRules(String UnitName, String AgentName, String Deontic, String Service) throws MySQLException {

		Statement st = null;
		ResultSet res = null;
		ResultSet res2 = null;

		ArrayList<String> result = new ArrayList<String>();
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}
			
			st = connection.createStatement();
			
			res = st.executeQuery("select distinct nl.normRule from normList nl inner join unitList ul on nl.idunitList=ul.idunitList inner join targetType tt on nl.idtargetType=tt.idtargetType inner join deontic d on d.iddeontic=nl.iddeontic inner join actionNorm an on an.idactionnorm=nl.idactionnorm" +
					" where ul.unitName='"+UnitName+"' and d.deonticdesc='"+Deontic+"' and an.description='"+Service+"' and tt.targetName='positionName' and nl.targetValue=-1");

			while (res.next()) {
				result.add(res.getString("normRule"));
			}

			res2 = st.executeQuery("select distinct nl.normRule from normList nl inner join unitList ul on nl.idunitList=ul.idunitList inner join deontic d on d.iddeontic=nl.iddeontic inner join actionNorm an on an.idactionnorm=nl.idactionnorm inner join targetType tt on nl.idtargetType=tt.idtargetType inner join position p on nl.targetValue=p.idposition inner join roleList rl on rl.idposition=p.idposition inner join agentPlayList apl on apl.idroleList=rl.idrolelist inner join agentList al on al.idagentList=apl.idagentList" +
					" where ul.unitName='"+UnitName+"' and d.deonticdesc='"+Deontic+"' and an.description='"+Service+"' and tt.targetName='positionName' and rl.idunitList=nl.idunitList and al.agentName='"+AgentName+"'");

			while (res2.next()) {
				result.add(res2.getString("normRule"));
			}

			return result;

		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try {
				
				if (close)
					connection.close();
				if (st != null)
					st.close();

				if (res != null)
					res.close();
				if (res2 != null)
					res2.close();

			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}


	ArrayList<String> getRoleNormRules(String UnitName, String AgentName, String Deontic, String Service) throws MySQLException {
		
		Statement st = null;
		ResultSet res = null;
		ResultSet res2 = null;

		ArrayList<String> result = new ArrayList<String>();
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}
			
			st = connection.createStatement();
			res = st.executeQuery("select distinct nl.normRule, nl.idnormlist from normList nl inner join unitList ul on nl.idunitList=ul.idunitList inner join targetType tt on nl.idtargetType=tt.idtargetType inner join deontic d on d.iddeontic=nl.iddeontic inner join actionNorm an on an.idactionnorm=nl.idactionnorm" +
					" where ul.unitName='"+UnitName+"' and d.deonticdesc='"+Deontic+"' and an.description='"+Service+"' and tt.targetName='roleName' and nl.targetValue=-1");

			while (res.next()) {
				result.add(res.getString("normRule"));
			}

			res2 = st.executeQuery("select distinct nl.normRule from normList nl inner join unitList ul on nl.idunitList=ul.idunitList inner join deontic d on d.iddeontic=nl.iddeontic inner join actionNorm an on an.idactionnorm=nl.idactionnorm inner join targetType tt on nl.idtargetType=tt.idtargetType inner join roleList rl on rl.idroleList=nl.targetValue inner join agentPlayList apl on apl.idroleList=rl.idrolelist inner join agentList al on al.idagentList=apl.idagentList" +
					" where ul.unitName='"+UnitName+"' and d.deonticdesc='"+Deontic+"' and an.description='"+Service+"' and tt.targetName='roleName' and rl.idunitList=nl.idunitList and al.agentName='"+AgentName+"'");

			while (res2.next()) {
				result.add(res2.getString("normRule"));
			}

			return result;

		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try {
				
				if (close)
					connection.close();
				if (st != null)
					st.close();

				if (res != null)
					res.close();
				if (res2 != null)
					res2.close();

			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}


	String createNorm(String UnitName, String NormContent, Norm parsedNorm, Rule normRule) throws MySQLException {

		Statement st = null;
		ResultSet res = null;
		ResultSet res2 = null;
		ResultSet res3 = null;
		ResultSet res4 = null;
		ResultSet res5 = null;
		ResultSet res6 = null;
		ResultSet res7 = null;

		String idUnit = "";
		String idDeontic = "";
		String idTarget = "";
		String typeTableName = "";
		String typerowName = "";
		String idTargetValue = "";
		String idAction = "";
		String idNorm = "";
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}
			
			st = connection.createStatement();
			res = st.executeQuery("SELECT idunitList FROM unitList WHERE unitName = '"+UnitName+"'");

			if (res.next())
				idUnit = res.getString("idunitList");

			res2 = st.executeQuery("SELECT iddeontic FROM deontic WHERE deonticdesc = '"+parsedNorm.getDeontic()+"'");

			if (res2.next())
				idDeontic = res2.getString("iddeontic");

			res3 = st.executeQuery("SELECT idtargetType, targetTable FROM targetType WHERE targetName = '"+parsedNorm.getTargetType()+"'");

			if (res3.next())
				idTarget = res3.getString("idtargetType");
				typeTableName = res3.getString("targetTable");

			if (parsedNorm.getTargetValue().equals("_")) {
				idTargetValue = "-1";
			} else {

				typerowName = typeTableName.replace("List", "");
				typerowName = typerowName + "Name";

				res4 = st.executeQuery("SELECT id"+typeTableName+" FROM "+typeTableName+" WHERE "+typerowName +"='"+parsedNorm.getTargetValue()+"'");

				if (res4.next()) {
					
					idTargetValue = res4.getString("id"+typeTableName);
					
				} else if (parsedNorm.getTargetType().equals("agentName")) {
					
					st.executeUpdate("INSERT INTO agentList (agentName) VALUES ('" + parsedNorm.getTargetValue()+"')");

					res5 = st.executeQuery("SELECT id"+typeTableName+" FROM "+typeTableName+" WHERE "+typerowName +"='"+parsedNorm.getTargetValue()+"'");

					if (res5.next()) {
						
						idTargetValue = res5.getString("id"+typeTableName);
					}
				}
			}

			res6 = st.executeQuery("SELECT idactionNorm FROM actionNorm WHERE description ='"+parsedNorm.getActionName()+"'");

			if (res6.next()) {
				idAction = res6.getString("idactionNorm");
			}

			String sql = "INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionnorm, normContent, normRule) " +
			"VALUES (" + idUnit + ",'"+parsedNorm.getId()+"',"+idDeontic+","+idTarget+","+idTargetValue+","+idAction+",'"+NormContent+"','"+normRule.toString()+"')";
			st.executeUpdate(sql);

			res7 = st.executeQuery("SELECT idnormList FROM normList WHERE normName ='"+parsedNorm.getId()+"'");

			if (res7.next()) {
				idNorm= res7.getString("idnormList");
			}

			for(String actionParam : parsedNorm.getActionParams()) {	
				st.executeUpdate("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES (" + idNorm + ","+idAction+",'"+actionParam+"')");				
			}

			connection.commit();
			return parsedNorm.getId() + " created";

		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try {
				
				if (close)
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

			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}


	ArrayList<ArrayList<String>> getAgentNorms(String TargetValueName, String UnitName) throws MySQLException {
		
		Statement st = null;
		ResultSet res = null;

		String normName = "";
		String unitName = "";
		String targetTypeName = "";
		String targetValueName = "";
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			if (TargetValueName.equals("_")) {

				st = connection.createStatement();

				res = st.executeQuery("SELECT n1.normName, u1.unitName, tt.targetName, '_' FROM normList n1 inner join unitList u1 on n1.idunitList = u1.idunitList inner join targetType tt on n1.idtargetType = tt.idtargetType WHERE tt.targetName='agentName' and n1.targetValue=-1 and u1.unitName='"+UnitName+"'");

				while (res.next()) {
					
					ArrayList<String> aux = new ArrayList<String>();
					
					normName = res.getString("normName");
					unitName = res.getString("unitName");
					targetTypeName = res.getString("targetName");
					targetValueName = res.getString("_");

					aux.add(normName);
					aux.add(unitName);
					aux.add(targetTypeName);
					aux.add(targetValueName);
					result.add(aux);
				}
				return result;
				
			} else {
				
				st = connection.createStatement();

				res = st.executeQuery("select nl.normName, ul.unitName, tt.targetName,al.agentName from normList nl inner join unitList ul on nl.idunitList=ul.idunitList inner join targetType tt on nl.idtargetType=tt.idtargetType inner join agentList al on nl.targetValue=al.idagentList where tt.targetName='agentName' and al.agentName='"+TargetValueName+"' and ul.unitName='"+ UnitName+"'");

				while (res.next()) {
					
					ArrayList<String> aux = new ArrayList<String>();
					normName = res.getString("normName");
					unitName = res.getString("unitName");
					targetTypeName = res.getString("targetName");
					targetValueName = res.getString("agentName");

					aux.add(normName);
					aux.add(unitName);
					aux.add(targetTypeName);
					aux.add(targetValueName);
					result.add(aux);
				}
				return result; 
			}

		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try {
				
				if (close)
					connection.close();
				if (st != null)
					st.close();

				if (res != null)
					res.close();

			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}


	ArrayList<ArrayList<String>> getAgentNorms(String UnitName) throws MySQLException {

		Statement st = null;
		ResultSet res = null;

		String normName = "";
		String unitName = "";
		String targetTypeName = "";
		String targetValueName = "";
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st = connection.createStatement();

			res = st.executeQuery("select nl.normName, ul.unitName,tt.targetName,al.agentName from normList nl inner join unitList ul on nl.idunitList=ul.idunitList inner join targetType tt on nl.idtargetType=tt.idtargetType inner join agentList al on nl.targetValue=al.idagentList where tt.targetName='agentName' and ul.unitName='"+UnitName+"'");

			while (res.next()) {
				
				ArrayList<String> aux = new ArrayList<String>();

				normName = res.getString("normName");
				unitName = res.getString("unitName");
				targetTypeName = res.getString("targetName");
				targetValueName = res.getString("agentName");

				aux.add(normName);
				aux.add(unitName);
				aux.add(targetTypeName);
				aux.add(targetValueName);
				result.add(aux);
			}

			res = st.executeQuery("select nl.normName, ul.unitName,tt.targetName,'_' from normList nl inner join unitList ul on nl.idunitList=ul.idunitList inner join targetType tt on nl.idtargetType=tt.idtargetType where tt.targetName='agentName' and nl.targetValue=-1 and ul.unitName='"+UnitName+"'");

			while (res.next()) {
				
				ArrayList<String> aux = new ArrayList<String>();
				
				normName = res.getString("normName");
				unitName = res.getString("unitName");
				targetTypeName = res.getString("targetName");
				targetValueName = res.getString("_");

				aux.add(normName);
				aux.add(unitName);
				aux.add(targetTypeName);
				aux.add(targetValueName);
				result.add(aux);
			}

			return result; 

		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try {
				
				if (close)
					connection.close();
				if (st != null)
					st.close();

				if (res != null)
					res.close();

			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}


	ArrayList<ArrayList<String>> getPositionNorms(String TargetValueName, String UnitName) throws MySQLException {
		
		Statement st = null;
		ResultSet res = null;

		String normName = "";
		String unitName = "";
		String targetTypeName = "";
		String targetValueName = "";
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			if (TargetValueName.equals("_")) {

				st = connection.createStatement();

				res = st.executeQuery("SELECT n1.normName, u1.unitName, tt.targetName, '_' FROM normList n1 inner join unitList u1 on n1.idunitList = u1.idunitList inner join targetType tt on n1.idtargetType = tt.idtargetType WHERE tt.targetName='positionName' and n1.targetValue=-1 and u1.unitName='"+UnitName+"'");

				while (res.next()) {
					
					ArrayList<String> aux = new ArrayList<String>();

					normName = res.getString("normName");
					unitName = res.getString("unitName");
					targetTypeName = res.getString("targetName");
					targetValueName = res.getString("_");

					aux.add(normName);
					aux.add(unitName);
					aux.add(targetTypeName);
					aux.add(targetValueName);
					result.add(aux);
				}
				return result; 
			
			} else {
				
				st = connection.createStatement();

				res = st.executeQuery("select nl.normName, ul.unitName, tt.targetName,p.positionName from normList nl inner join unitList ul on nl.idunitList=ul.idunitList inner join targetType tt on nl.idtargetType=tt.idtargetType inner join position p on nl.targetValue=p.idposition where tt.targetName='positionName' and p.positionName='"+TargetValueName+"' and ul.unitName='"+ UnitName+"'");

				while (res.next()) {
					
					ArrayList<String> aux = new ArrayList<String>();
					
					normName = res.getString("normName");
					unitName = res.getString("unitName");
					targetTypeName = res.getString("targetName");
					targetValueName = res.getString("positionName");

					aux.add(normName);
					aux.add(unitName);
					aux.add(targetTypeName);
					aux.add(targetValueName);
					result.add(aux);
				}
				return result; 
			}

		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try {
				
				if (close)
					connection.close();
				if (st != null)
					st.close();

				if (res != null)
					res.close();

			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	ArrayList<ArrayList<String>> getPositionNorms(String UnitName) throws MySQLException {
		
		Statement st = null;
		ResultSet res = null;
		
		String normName = "";
		String unitName = "";
		String targetTypeName = "";
		String targetValueName = "";
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st = connection.createStatement();

			res = st.executeQuery("select nl.normName, ul.unitName,tt.targetName,p.positionName from normList nl inner join unitList ul on nl.idunitList=ul.idunitList inner join targetType tt on nl.idtargetType=tt.idtargetType inner join position p on nl.targetValue=p.idposition where tt.targetName='positionName' and ul.unitName='"+UnitName +"'");

			while (res.next()) {
				
				ArrayList<String> aux = new ArrayList<String>();

				normName = res.getString("normName");
				unitName = res.getString("unitName");
				targetTypeName = res.getString("targetName");
				targetValueName = res.getString("positionName");

				aux.add(normName);
				aux.add(unitName);
				aux.add(targetTypeName);
				aux.add(targetValueName);
				result.add(aux);
			}

			res = st.executeQuery("select nl.normName, ul.unitName,tt.targetName,'_' from normList nl inner join unitList ul on nl.idunitList=ul.idunitList inner join targetType tt on nl.idtargetType=tt.idtargetType where tt.targetName='positionName' and nl.targetValue=-1 and ul.unitName='"+UnitName+"'");

			while (res.next()) {
				
				ArrayList<String> aux = new ArrayList<String>();
				
				normName = res.getString("normName");
				unitName = res.getString("unitName");
				targetTypeName = res.getString("targetName");
				targetValueName = res.getString("_");

				aux.add(normName);
				aux.add(unitName);
				aux.add(targetTypeName);
				aux.add(targetValueName);
				result.add(aux);
			}
			return result;
			
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try {
				
				if (close)
					connection.close();
				if (st != null)
					st.close();

				if (res != null)
					res.close();

			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	ArrayList<ArrayList<String>> getRoleNorms(String TargetValueName, String UnitName) throws MySQLException {
		
		Statement st = null;
		ResultSet res = null;
		
		String normName = "";
		String unitName = "";
		String targetTypeName = "";
		String targetValueName = "";
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			if (TargetValueName.equals("_")) {

				st = connection.createStatement();

				res = st.executeQuery("select nl.normName, ul.unitName,tt.targetName,'_' from normList nl inner join unitList ul on nl.idunitList=ul.idunitList inner join targetType tt on nl.idtargetType=tt.idtargetType where tt.targetName='roleName' and nl.targetValue=-1 and ul.unitName='"+UnitName+"'");

				while (res.next()) {
					
					ArrayList<String> aux = new ArrayList<String>();

					normName = res.getString("normName");
					unitName = res.getString("unitName");
					targetTypeName = res.getString("targetName");
					targetValueName = res.getString("_");

					aux.add(normName);
					aux.add(unitName);
					aux.add(targetTypeName);
					aux.add(targetValueName);
					result.add(aux);
				}

				return result; 
				
			} else {
				
				st = connection.createStatement();

				res = st.executeQuery("select nl.normName, ul.unitName,tt.targetName,rl.roleName from normList nl inner join unitList ul on nl.idunitList=ul.idunitList inner join targetType tt on nl.idtargetType=tt.idtargetType inner join roleList rl on nl.targetValue=rl.idroleList where tt.targetName='roleName' and rl.rolename='"+TargetValueName+"' and ul.unitName='"+UnitName+"'");

				while (res.next()) {
					
					ArrayList<String> aux = new ArrayList<String>();
					
					normName = res.getString("normName");
					unitName = res.getString("unitName");
					targetTypeName = res.getString("targetName");
					targetValueName = res.getString("roleName");

					aux.add(normName);
					aux.add(unitName);
					aux.add(targetTypeName);
					aux.add(targetValueName);
					result.add(aux);
				}

				return result; 
			}

		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try {
				
				if (close)
					connection.close();
				if (st != null)
					st.close();

				if (res != null)
					res.close();

			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	ArrayList<ArrayList<String>> getRoleNorms(String UnitName) throws MySQLException {
		
		Statement st = null;
		ResultSet res = null;
		
		String normName = "";
		String unitName = "";
		String targetTypeName = "";
		String targetValueName = "";
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}
			
			st = connection.createStatement();

			res = st.executeQuery("select nl.normName, ul.unitName,tt.targetName,rl.roleName from normList nl inner join unitList ul on nl.idunitList=ul.idunitList inner join targetType tt on nl.idtargetType=tt.idtargetType inner join roleList rl on nl. targetValue=rl.idroleList where tt.targetName='roleName' and ul.unitName='"+UnitName +"'");

			while (res.next()) {
				
				ArrayList<String> aux = new ArrayList<String>();

				normName = res.getString("normName");
				unitName = res.getString("unitName");
				targetTypeName = res.getString("targetName");
				targetValueName = res.getString("roleName");

				aux.add(normName);
				aux.add(unitName);
				aux.add(targetTypeName);
				aux.add(targetValueName);
				result.add(aux);
			}

			res = st.executeQuery("select nl.normName, ul.unitName,tt.targetName,'_' from normList nl inner join unitList ul on nl.idunitList=ul.idunitList inner join targetType tt on nl.idtargetType=tt.idtargetType where tt.targetName='roleName' and nl.targetValue=-1 and ul.unitName='"+UnitName+"'");

			while (res.next()) {
				
				ArrayList<String> aux = new ArrayList<String>();
				
				normName = res.getString("normName");
				unitName = res.getString("unitName");
				targetTypeName = res.getString("targetName");
				targetValueName = res.getString("_");

				aux.add(normName);
				aux.add(unitName);
				aux.add(targetTypeName);
				aux.add(targetValueName);
				result.add(aux);
			}

			return result; 

		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try {
				
				if (close)
					connection.close();
				if (st != null)
					st.close();

				if (res != null)
					res.close();

			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	ArrayList<ArrayList<String>> getPublicRoleNorms(String UnitName) throws MySQLException {
		
		Statement st = null;
		ResultSet res = null;
		
		String normName = "";
		String unitName = "";
		String targetTypeName = "";
		String targetValueName = "";
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}
			
			st = connection.createStatement();

			res = st.executeQuery("select nl.normName, ul.unitName,tt.targetName,rl.rolename from normList nl inner join unitList ul on nl.idunitList=ul.idunitList inner join targetType tt on nl.idtargetType=tt.idtargetType inner join roleList rl on nl. targetValue=rl.idroleList inner join visibility v on rl.idvisibility=v.idvisibility where tt.targetName='roleName' and v.visibility='public' and ul.unitName='"+UnitName+"'");

			while (res.next()) {
				
				ArrayList<String> aux = new ArrayList<String>();

				normName = res.getString("normName");
				unitName = res.getString("unitName");
				targetTypeName = res.getString("targetName");
				targetValueName = res.getString("roleName");

				aux.add(normName);
				aux.add(unitName);
				aux.add(targetTypeName);
				aux.add(targetValueName);
				result.add(aux);
			}

			res = st.executeQuery("select nl.normName, ul.unitName,tt.targetName,'_' from normList nl inner join unitList ul on nl.idunitList=ul.idunitList inner join targetType tt on nl.idtargetType=tt.idtargetType where tt.targetName='roleName' and nl.targetValue=-1 and ul.unitName='"+UnitName+"'");

			while (res.next()) {
				
				ArrayList<String> aux = new ArrayList<String>();
				
				normName = res.getString("normName");
				unitName = res.getString("unitName");
				targetTypeName = res.getString("targetName");
				targetValueName = res.getString("_");

				aux.add(normName);
				aux.add(unitName);
				aux.add(targetTypeName);
				aux.add(targetValueName);
				result.add(aux);
			}

			return result; 

		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		} finally {
			try {
				
				if (close)
					connection.close();
				if (st != null)
					st.close();

				if (res != null)
					res.close();

			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}
}
