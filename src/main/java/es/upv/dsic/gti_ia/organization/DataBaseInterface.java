package es.upv.dsic.gti_ia.organization;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.upv.dsic.gti_ia.norms.Norm;
import es.upv.dsic.gti_ia.organization.exception.MySQLException;
import es.upv.dsic.gti_ia.organization.exception.THOMASMessages;
import es.upv.dsic.gti_ia.organization.exception.THOMASMessages.MessageID;


/**
 * In this class are included the methods of database access that have arisen during the design of new OMS services
 * 
 * @author Joan Bello
 * 
 * @author Ricard Lopez Fogues
 * 
 * @author Jose Alemany Bordera  -  jalemany1@dsic.upv.es
 * 
 * @author Soledad Valero  -  svalero@dsic.upv.es
 */

public class DataBaseInterface {
	private DataBaseAccess db;
	private Connection connection;

	/**
	 * Used for retrieve local messages.
	 */
	private THOMASMessages l10n;

	
	/**
	 * Constructor which initializes the class.
	 * 
	 */
	public DataBaseInterface() {
		db = new DataBaseAccess();
		l10n = new THOMASMessages();
		connection = null;
	}

	
	/**
	 * Accesses the database and assign a role R, previously created in the unit U, to an agent A.
	 * If the agent A doesn't exist this is created.
	 * 
	 * @param unitName			Identifier of the organization unit.
	 * @param roleName			Identifier of the role which will be assigned the agent.
	 * @param agentName			Identifier of the agent.
	 * 
	 * @return 					Returns <rolename + " acquired">
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	String acquireRole(String unitName, String roleName, String agentName) throws MySQLException {
		
		Statement st = null;
		
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}	

			st = connection.createStatement();
			
			//If the Agent is not yet on the table agentList have to add it
			if (!checkAgent(agentName)) {
				st.addBatch("INSERT INTO agentList (agentName) VALUES ('"+agentName+"')"); 
			}

			st.addBatch("INSERT INTO agentPlayList (idagentList, idroleList)" +
					" VALUES ((SELECT idagentList FROM agentList WHERE agentName = '"+agentName+"'), (SELECT idroleList FROM roleList WHERE roleName ='" + roleName + "' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "')))");
			st.executeBatch();
			
			if (close)
				connection.commit();
			
			return roleName + " acquired";
			
		} catch(BatchUpdateException b) {                

			//Extracts useful error information
			String message = l10n.getMessage(MessageID.MYSQL, b.getMessage());
			int [] bUpdateCounts = b.getUpdateCounts();
			
			for (int i = 0; i < bUpdateCounts.length; i++) {
				if (bUpdateCounts[i] < 0)
					message = message + "\nSQL statement number " + Integer.toString(bUpdateCounts.length - i) + " has failed.";
			}

			throw new MySQLException(message);
			
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

	
	/**
	 * Check if the agent A exists, returning true in this case. Otherwise, it will return false.
	 * 
	 * @param agentName			Identifier of the agent to check.
	 * 
	 * @return 					True if agent A exists, false otherwise.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
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
	
	
	/**
	 * Check if the agent A has activity on the platform, returning true in this case. Otherwise, it will return false.
	 * The agent A has activity on the platform if plays some role or has associated some norm.
	 * 
	 * @param agentName			Identifier of the agent to check.
	 * 
	 * @return 					True if agent A has activity on the platform, false otherwise.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
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

	
	/**
	 * Check if the agent A exists in the unit U, returning true in this case. Otherwise, it will return false.
	 * 
	 * @param agentName			Identifier of the agent to check.
	 * @param unitName			Identifier of the organization unit.
	 * 
	 * @return 					True if agent A exists in the unit U, false otherwise.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	boolean checkAgentInUnit(String agentName, String unitName) throws MySQLException {
		
		Statement st = null;
		ResultSet res = null;
		
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st = connection.createStatement();
			res = st.executeQuery("SELECT * FROM agentList al INNER JOIN agentPlayList apl ON (apl.idagentList=al.idagentList) INNER JOIN "
					+ "roleList rl ON (rl.idroleList=apl.idroleList) INNER JOIN unitList ul ON (ul.idunitList=rl.idunitList) WHERE "
					+ "al.agentName='" + agentName + "' AND unitName='" + unitName + "'");
			
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

	
	/**
	 * Check if the agent A play the role R within unit U, returning true in this case. Otherwise, it will return false.
	 * 
	 * @param agentName			Identifier of the agent to check.
	 * @param roleName			Identifier of the role.
	 * @param unitName			Identifier of the organization unit.
	 * 
	 * @return 					True if agent A play the role R within unit U, false otherwise.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	boolean checkAgentPlaysRole(String agentName, String roleName, String unitName) throws MySQLException {
		
		Statement st = null;
		ResultSet res = null;
		
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st = connection.createStatement();
			res = st.executeQuery("SELECT * FROM agentPlayList apl INNER JOIN agentList al ON (apl.idagentList=al.idagentList) INNER JOIN "
					+ "roleList rl ON (rl.idroleList=apl.idroleList) INNER JOIN unitList ul ON (ul.idunitList=rl.idunitList) WHERE "
					+ "al.agentName='" + agentName + "' AND rl.roleName='" + roleName + "' AND ul.unitName='" + unitName + "'");
			
			if (res.next())
				return true;

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

				if (res != null)
					res.close();

			} catch(SQLException e) {
				String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
				throw new MySQLException(message);
			}
		}
	}

	
	/**
	 * Check if exists some agent playing a creator role within unit U, returning true in this case. Otherwise, it will return false.
	 * 
	 * @param unitName			Identifier of the organization unit.
	 * 
	 * @return 					True if exists some agent playing a creator role within unit U, false otherwise.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	boolean checkNoCreatorAgentsInUnit(String unitName) throws MySQLException {

		Statement st = null;
		ResultSet res = null;
		
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st = connection.createStatement();
			res = st.executeQuery("SELECT * FROM agentPlayList apl INNER JOIN agentList al ON (apl.idagentList=al.idagentList) INNER JOIN "
					+ "roleList rl ON (rl.idroleList=apl.idroleList) INNER JOIN position p ON (p.idposition=rl.idposition) INNER JOIN "
					+ "unitList ul ON (ul.idunitList=rl.idunitList) WHERE ul.unitName='" + unitName + "' and p.positionName !='creator'");
			
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
	
	
	/**
	 * This method accesses the database and check if there is a rule whose ID/name is N in the unit U.
	 * 
	 * @param normName			Identifier of the norm N.
	 * @param unitName			Identifier of the organization unit U.
	 * 
	 * @return 					Returns true if exists, otherwise return false.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	boolean checkNormName(String normName, String unitName) throws MySQLException {
		
		Statement st = null;
		ResultSet res = null;

		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}
			
			st = connection.createStatement();
			res = st.executeQuery("SELECT * FROM normList nl INNER JOIN unitList ul ON (ul.idunitList=nl.idunitList) "
					+ "WHERE nl.normName='" + normName + "' AND ul.unitName='" + unitName + "'");
					
			if(res.next())
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

	
	/**
	 * Check if the role R within unit U is played by some agent, returning true in this case. Otherwise, it will return false.
	 * 
	 * @param roleName			Identifier of the role.
	 * @param unitName			Identifier of the organization unit.
	 * 
	 * @return 					True if the role R within unit U is played by some agent, false otherwise.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	boolean checkPlayedRoleInUnit(String roleName, String unitName) throws MySQLException {

		Statement st = null;
		ResultSet res = null;
		
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st = connection.createStatement();
			res = st.executeQuery("SELECT * FROM agentPlayList apl INNER JOIN agentList al ON (apl.idagentList=al.idagentList) INNER JOIN "
					+ "roleList rl on (rl.idroleList=apl.idroleList) INNER JOIN unitList ul ON (ul.idunitList=rl.idunitList) WHERE "
					+ "ul.unitName = '" + unitName + "' and rl.rolename = '" + roleName + "'");
			
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

	
	/**
	 * Check if the agent A play the position P, returning true in this case. Otherwise, it will return false.
	 * 
	 * @param agentName			Identifier of the agent to check.
	 * @param position			Identifier of the position.
	 * 
	 * @return 					True if the agent A play the position P, false otherwise.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	boolean checkPosition(String agentName, String position) throws MySQLException {

		Statement st = null;
		ResultSet res = null;
		
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st = connection.createStatement();
			res = st.executeQuery("SELECT * FROM agentPlayList apl INNER JOIN agentList al ON (apl.idagentList=al.idagentList) INNER JOIN "
					+ "roleList rl ON (rl.idroleList=apl.idroleList) INNER JOIN position p ON (p.idposition=rl.idposition) WHERE "
					+ "al.agentName='" + agentName + "' and p.positionName='" + position + "'");
			
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

	
	/**
	 * Check if the agent A play the position P within unit U, returning true in this case. Otherwise, it will return false.
	 * 
	 * @param agentName			Identifier of the agent to check.
	 * @param position			Identifier of the position.
	 * @param unitName			Identifier of the organization unit.
	 * 
	 * @return 					True if the agent A play the position P within unit U, false otherwise.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	boolean checkPositionInUnit(String agentName, String position, String unitName) throws MySQLException {

		Statement st = null;
		ResultSet res = null;

		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st = connection.createStatement();
			res = st.executeQuery("SELECT * FROM agentPlayList apl INNER JOIN agentList al ON (apl.idagentList=al.idagentList) INNER JOIN "
					+ "roleList rl ON (rl.idroleList=apl.idroleList) INNER JOIN position p ON (p.idposition=rl.idposition) INNER JOIN "
					+ "unitList ul ON (ul.idunitList=rl.idunitList) WHERE ul.unitName='" + unitName + "' AND "
					+ "p.positionName='" + position + "' AND al.agentName='" + agentName + "'");
			
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

	
	/**
	 * Check if exists the role R within unit U, returning true in this case. Otherwise, it will return false.
	 * 
	 * @param roleName			Identifier of the role.
	 * @param unitName			Identifier of the organization unit.
	 * 
	 * @return 					True if exists the role R within unit U, false otherwise.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	boolean checkRole(String roleName, String unitName) throws MySQLException {
		
		Statement st = null;
		ResultSet res = null;
		
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}
			
			st = connection.createStatement();
			res = st.executeQuery("SELECT * FROM roleList rl INNER JOIN unitList ul ON (ul.idunitList=rl.idunitList) WHERE "
					+ "ul.unitName='" + unitName + "' AND rl.rolename='" + roleName + "'");
			
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

	
	/**
	 * Check if exists descendants units from the unit U, returning true in this case. Otherwise, it will return false.
	 * 
	 * @param unitName			Identifier of the organization unit.
	 * 
	 * @return 					True if exists descendants units from the unit U, false otherwise.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	boolean checkSubUnits(String unitName) throws MySQLException {
		
		Statement st = null;
		ResultSet res = null;

		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st = connection.createStatement();
			res = st.executeQuery("SELECT * FROM unitHierarchy uh INNER JOIN unitList ul ON (ul.idunitList=uh.idParentUnit) WHERE ul.unitName='" + unitName + "'");
			
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
	
	
	/**
	 * Check if the role R within unit U has associated some norms, returning true in this case. Otherwise, it will return false.
	 * 
	 * @param roleName			Identifier of the role.
	 * @param unitName			Identifier of the organization unit.
	 * 
	 * @return 					True if the role R within unit U has associated some norms, false otherwise.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	boolean checkTargetRoleNorm(String roleName, String unitName) throws MySQLException {
		
		Statement st = null;
		ResultSet res = null;

		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st = connection.createStatement();
			res = st.executeQuery("SELECT true FROM normList nl INNER JOIN unitList ul ON nl.idunitList=ul.idunitList INNER JOIN "
					+ "targetType tt ON tt.idtargetType=nl.idtargetType INNER JOIN roleList rl ON rl.idroleList=nl.targetvalue WHERE "
					+ "ul.unitName='"+ unitName +"' AND tt.targetName='roleName' AND rl.roleName='"+ roleName +"'");
			
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

	
	/**
	 * Check if the parameter is a valid target type, returning true in this case. Otherwise, it will return false.
	 * There are three target types: 'agentName', 'roleNorm' and 'positionName'.
	 * 
	 * @param targetType		Identifier of the targetType.
	 * 
	 * @return 					True if the parameter is a valid target type, false otherwise.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
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

	
	/**
	 * Check if the unit U is already created, returning true in this case. Otherwise, it will return false.
	 * 
	 * @param unitName			Identifier of the organization unit.
	 * 
	 * @return 					True if the unit U is already created, false otherwise.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	boolean checkUnit(String unitName) throws MySQLException {
		
		Statement st = null;
		ResultSet res = null;

		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st = connection.createStatement();
			res = st.executeQuery("SELECT * FROM unitList WHERE unitName ='" + unitName + "'");
			
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

	
	/**
	 * Checks the compatibility with the Jason languaje. 
	 * 
	 * @param identifier		Identifier to check if is a valid parameter.
	 * 
	 * @return boolean 			Return true if is a valid parameter and false if not.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
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
			res = st.executeQuery("SELECT * FROM reservedWordList WHERE reservedWord='" + identifier + "'");

			if (res.next())
				return false;
			
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

	
	/**
	 * Check if the unit U is the virtual unit, returning true in this case. Otherwise, it will return false.
	 * 
	 * @param unitName			Identifier of the organization unit.
	 * 
	 * @return 					True if the unit U is the virtual unit, false otherwise.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	boolean checkVirtualUnit(String unitName) throws MySQLException {
		return unitName.equalsIgnoreCase("virtual");
	}


	/**
	 * This method inserts the necessary information in the database to register a norm.
	 * 
	 * @param unitName			Identifier of the organization unit U.
	 * @param normContent		Contents of the norm expressed as <Deontic, Target, Action, Activation, Expiration>.
	 * @param parsedNorm		Object type Norm with the parsed content <id, deontic, targetType, targetValue, actionName, actionParams, action, activation, expiration>.
	 * @param normRule			The belief or norm parsed in Jason language.
	 * 
	 * @return 					Returns <normName + " created">.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	String createNorm(String unitName, String normContent, Norm parsedNorm, String normRule) throws MySQLException {

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
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}
			
			st = connection.createStatement();
			res = st.executeQuery("SELECT idunitList FROM unitList WHERE unitName='" + unitName + "'");

			if (res.next())
				idUnit = res.getString("idunitList");

			res2 = st.executeQuery("SELECT iddeontic FROM deontic WHERE deonticdesc='" + parsedNorm.getDeontic() + "'");

			if (res2.next())
				idDeontic = res2.getString("iddeontic");

			res3 = st.executeQuery("SELECT idtargetType, targetTable FROM targetType WHERE targetName='" + parsedNorm.getTargetType() + "'");

			if (res3.next())
				idTarget = res3.getString("idtargetType");
				typeTableName = res3.getString("targetTable");

			if (parsedNorm.getTargetValue().equals("_")) {
				idTargetValue = "-1";
			} else {

				typerowName = typeTableName.replace("List", "") + "Name";

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
			
			st.addBatch("INSERT INTO normList (idunitList, normName, iddeontic, idtargetType, targetValue, idactionnorm, normContent, normRule) "
					+ "VALUES (" + idUnit + ",'"+parsedNorm.getId()+"',"+idDeontic+","+idTarget+","+idTargetValue+","+idAction+",'"+normContent+"','"+normRule+"')");

			for(String actionParam : parsedNorm.getActionParams()) {	
				st.addBatch("INSERT INTO actionNormParam (idnormList, idactionNorm, value) VALUES ((SELECT idnormList FROM normList WHERE normName ='"+parsedNorm.getId()+"'),"+idAction+",'"+actionParam+"')");				
			}

			st.executeBatch();
			
			if (close)
				connection.commit();

			return parsedNorm.getId() + " created";

		} catch(BatchUpdateException b) {                

			//Extracts useful error information
			String message = l10n.getMessage(MessageID.MYSQL, b.getMessage());
			int [] bUpdateCounts = b.getUpdateCounts();
			
			for (int i = 0; i < bUpdateCounts.length; i++) {
				if (bUpdateCounts[i] < 0)
					message = message + "\nSQL statement number " + Integer.toString(bUpdateCounts.length - i) + " has failed.";
			}

			throw new MySQLException(message);
			
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

	
	/**
	 * Accesses the database and create the role R within the unit U with the information that is passed as arguments.
	 * 
	 * @param roleName			Identifier of the role which will be created.
	 * @param unitName			Identifier of the organization unit.
	 * @param accessibility		This attribute allows controlling who can acquire role(internal, external).
	 * @param visibility		This attribute allows controlling what information about roles is provided(public, private).
	 * @param position			The possible values that can take are: member, supervisor, subordinate or creator.
	 * 
	 * @return 					Returns <roleName + " created">
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
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

			if (close)
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

	
	/**
	 * Accesses the database, create a creator type role R and unit U with the information that is passed as arguments.
	 * 
	 * @param unitName			Identifier of the organization unit.
	 * @param unitType			Parameter to identify the unit type.
	 * @param parentUnitName	Identifier of the parent organization unit.
	 * @param agentName			Identifier of the agent to acquire the role creatorRoleName.
	 * @param creatorRoleName	Identifier of the role creator which will be created and assigned to unit U.
	 * 
	 * @return 					Returns <unitName + " created">
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	String createUnit(String unitName, String unitType, String parentUnitName, String agentName, String creatorRoleName) throws MySQLException {
		
		Statement st = null;

		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st = connection.createStatement();
			
			st.addBatch("INSERT INTO unitList (unitName, idunitType) VALUES ('" + unitName + "', (SELECT idunitType FROM unitType "
					+ "WHERE unitTypeName ='" + unitType + "'))");
			st.addBatch("INSERT INTO unitHierarchy (idParentUnit, idChildUnit) VALUES ((SELECT idunitList FROM unitList "
					+ "WHERE unitName ='" + parentUnitName + "'), (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "'))");
			st.addBatch("INSERT INTO roleList (roleName, idunitList, idposition, idaccessibility, idvisibility) VALUES "
					+ "('" + creatorRoleName + "', (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "'), (SELECT "
					+ "idposition FROM position WHERE positionName ='creator'),(SELECT idaccessibility FROM accessibility WHERE "
					+ "accessibility ='internal'), (SELECT idVisibility FROM visibility WHERE visibility ='private'))");
			st.addBatch("INSERT INTO agentPlayList (idagentList, idroleList) VALUES ((SELECT idagentList FROM agentList "
					+ "WHERE agentName='" + agentName + "'), (SELECT idroleList FROM roleList WHERE roleName ='" + creatorRoleName + "' "
					+ "AND idunitList =(SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "')))");
			st.executeBatch();
			
			if (close)
				connection.commit();
			
			return unitName + " created";

		} catch(BatchUpdateException b) {                

			//Extracts useful error information
			String message = l10n.getMessage(MessageID.MYSQL, b.getMessage());
			int [] bUpdateCounts = b.getUpdateCounts();
			
			for (int i = 0; i < bUpdateCounts.length; i++) {
				if (bUpdateCounts[i] < 0)
					message = message + "\nSQL statement number " + Integer.toString(bUpdateCounts.length - i) + " has failed.";
			}

			throw new MySQLException(message);
			
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

	
	/**
	 * Accesses the database and deallocate the agent A from the role R within unit U.
	 * 
	 * @param roleName			Identifier of the role to deallocate.
	 * @param unitName			Identifier of the organization unit to which the role R belongs.
	 * @param targetAgentName	Identifier of the agent to deallocate.
	 * 
	 * @return 					Returns <roleName + " deallocated">
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
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

			if (close)
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

	
	/**
	 * Accesses the database and delete the agent A with the information that is passed as arguments.
	 * 
	 * @param agentName			Identifier of the agent to delete.
	 * 
	 * @return 					Returns <agentName + " deleted">
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
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
			
			if (close)
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
	
	
	/**
	 * Accesses the database and delete the norm N with the information that is passed as arguments.
	 * To correct and complete elimination of the norm are carried out the following actions before removing this: delete information from two tables(actionNormParams and normList) and remove inactive agents (those who do not play any role or have rules associated).
	 * 
	 * @param normName			Identifier of the norm to delete.
	 * @param unitName			Identifier of the organization unit to which the norm N belongs.
	 * 
	 * @return 					Returns <normName + " deleted">
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	String deleteNorm(String normName, String unitName) throws MySQLException {
		
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
					"INNER JOIN agentList al ON (al.idagentList=nl.targetValue) WHERE tt.targetName='agentName' and nl.normName='"+ normName +"'");

			st2 = connection.createStatement();
			st2.addBatch("DELETE FROM actionNormParam USING actionNormParam INNER JOIN normList INNER JOIN unitList " +
					"ON (actionNormParam.idnormList = normList.idnormList AND normList.idunitList=unitList.idunitList)" +
					"WHERE unitName = '" + unitName + "' AND normName = '" + normName + "'");
			st2.addBatch("DELETE FROM normList USING normList INNER JOIN unitList ON (normList.idunitList=unitList.idunitList)" +
					"WHERE unitName='" + unitName + "' AND normName='" + normName + "'");
			st2.executeBatch();
			
			if (res.next()) {
				
				String agentName = res.getString("agentName");
				
				//Check if last role played and has no associated norm, then remove it from the table agentList
				if (!checkAgentActive(agentName))
					deleteAgent(agentName);
			}
			
			if (close)
				connection.commit();
			
			return normName + " deleted";

		} catch(BatchUpdateException b) {                

			//Extracts useful error information
			String message = l10n.getMessage(MessageID.MYSQL, b.getMessage());
			int [] bUpdateCounts = b.getUpdateCounts();
			
			for (int i = 0; i < bUpdateCounts.length; i++) {
				if (bUpdateCounts[i] < 0)
					message = message + "\nSQL statement number " + Integer.toString(bUpdateCounts.length - i) + " has failed.";
			}

			throw new MySQLException(message);
			
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

	
	/**
	 * Accesses the database and delete the role R with the information that is passed as arguments.
	 * 
	 * @param roleName			Identifier of the role to delete.
	 * @param unitName			Identifier of the organization unit to which the role R belongs.
	 * 
	 * @return 					Returns <roleName + " deleted">
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
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
			
			if (close)
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

	
	/**
	 * Accesses the database and delete the unit U with the information that is passed as arguments.
	 * To correct and complete elimination of the unit are carried out the following actions before removing this: delete all relationships with agents playing a role within unit U, remove roles, delete the rules associated to the unit U, remove inactive agents (those who do not play any role or have rules associated), delete relations with other units and, finally, remove the unit.
	 * 
	 * @param unitName			Identifier of the organization unit to delete.
	 * 
	 * @return 					Returns <unitName + " deleted">
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
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
	
	
	/**
	 * Accesses the database and allow a unit U becomes part of another.
	 * 
	 * @param unitName			Identifier of the organization unit to join with parentUnitName.
	 * @param parentUnitName	Identifier of the parent unit.
	 * 
	 * @return 					Returns <unitName + " joined to" + parentUnitName>
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	String joinUnit(String unitName, String parentUnitName) throws MySQLException {

		Statement st = null;

		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}
			
			st = connection.createStatement();
			st.addBatch("DELETE FROM unitHierarchy WHERE idChildUnit = (SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "')");
			st.addBatch("INSERT INTO unitHierarchy (idParentUnit, idChildUnit) VALUES ((SELECT idunitList FROM unitList WHERE unitName ='" + parentUnitName + "'),(SELECT idunitList FROM unitList WHERE unitName ='" + unitName + "'))");
			st.executeBatch();
			
			if (close)
				connection.commit();
			
			return unitName + " joined to " + parentUnitName;

		} catch(BatchUpdateException b) {                

			//Extracts useful error information
			String message = l10n.getMessage(MessageID.MYSQL, b.getMessage());
			int [] bUpdateCounts = b.getUpdateCounts();
			
			for (int i = 0; i < bUpdateCounts.length; i++) {
				if (bUpdateCounts[i] < 0)
					message = message + "\nSQL statement number " + Integer.toString(bUpdateCounts.length - i) + " has failed.";
			}

			throw new MySQLException(message);
			
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

	
	/**
	 * Accesses the database and reflect the agent A abandons a role R.
	 * 
	 * @param unitName			Identifier of the organization unit to which the role R belongs.
	 * @param roleName			Identifier of the role to leave.
	 * @param agentName			Identifier of the agent.
	 * 
	 * @return 					Returns <roleName + " left">
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
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

			if (close)
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
	
	
	/**
	 * This method returns a list with Jason rules associated with the norms registered in unit U, deontic D type.
	 * Return the norms with...
	 * 	- targetValue = '_', and
	 * 	- targetValue = agentName, that can be activated for the agent A.
	 * 
	 * @param unitName			Identifier of the organization unit U.
	 * @param agentName			Identifier of the agent A.
	 * @param deontic			Type of the norm('d','p' o 'f').
	 * @param service			Service affected by the norm.
	 * 
	 * @return 					Returns an ArrayList with Jason rules.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	ArrayList<String> getAgentNormRules(String unitName, String agentName, String deontic, String service) throws MySQLException {

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
					" where ul.unitName='"+unitName+"' and d.deonticdesc='"+deontic+"' and an.description='"+service+"' and tt.targetName='agentName' and nl.targetValue=-1 ");

			while(res.next())
				result.add(res.getString("normRule"));

			res2 = st.executeQuery("select distinct nl.normRule from normList nl inner join unitList ul on nl.idunitList=ul.idunitList inner join targetType tt on nl.idtargetType=tt.idtargetType inner join deontic d on d.iddeontic=nl.iddeontic inner join actionNorm an on an.idactionnorm=nl.idactionnorm inner join agentList al on nl.targetValue=al.idagentList" +
					" where ul.unitName='"+unitName+"' and d.deonticdesc='"+deontic+"' and an.description='"+service+"' and tt.targetName='agentName' and al.agentName='"+agentName+"'");

			while(res2.next())
				result.add(res2.getString("normRule"));

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


	/**
	 * This method retrieves the norms associated with unit U whose target is AgentName type and matches with the provided in targetValueName.
	 * 
	 * @param targetValueName	Identifier of the agent T.
	 * @param unitName			Identifier of the organization unit U.
	 * 
	 * @return 					Returns the rules as <NormName, UnitName, TargetTypeName, TargetValueName>.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	ArrayList<ArrayList<String>> getAgentNorms(String targetValueName, String unitName) throws MySQLException {
		
		Statement st = null;
		ResultSet res = null;

		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st = connection.createStatement();
			
			if (targetValueName.equals("_"))
				res = st.executeQuery("SELECT n1.normName, u1.unitName, tt.targetName, '_' FROM normList n1 INNER JOIN unitList u1 "
						+ "ON n1.idunitList = u1.idunitList INNER JOIN targetType tt ON n1.idtargetType = tt.idtargetType WHERE "
						+ "tt.targetName='agentName' AND n1.targetValue=-1 AND u1.unitName='"+unitName+"'");
			else
				res = st.executeQuery("SELECT nl.normName, ul.unitName, tt.targetName, al.agentName FROM normList nl INNER JOIN unitList ul "
						+ "ON nl.idunitList=ul.idunitList INNER JOIN targetType tt ON nl.idtargetType=tt.idtargetType INNER JOIN agentList "
						+ "al ON nl.targetValue=al.idagentList WHERE tt.targetName='agentName' AND al.agentName='"+targetValueName+"' AND "
						+ "ul.unitName='"+ unitName+"'");

			while (res.next()) {
				
				ArrayList<String> aux = new ArrayList<String>();
				aux.add(res.getString(1));
				aux.add(res.getString(2));
				aux.add(res.getString(3));
				aux.add(res.getString(4));
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


	/**
	 * This method retrieves the norms associated with unit U whose target is AgentName type.
	 * First, get the set of tuples that are associated with some Agent and then recovers which are defined with value "_".
	 * 
	 * @param unitName			Identifier of the organization unit U.
	 * 
	 * @return 					Returns the rules as <NormName, UnitName, TargetTypeName, TargetValueName>.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	ArrayList<ArrayList<String>> getAgentNorms(String unitName) throws MySQLException {

		Statement st = null;
		ResultSet res = null;

		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st = connection.createStatement();

			res = st.executeQuery("SELECT nl.normName, ul.unitName, tt.targetName, al.agentName FROM normList nl INNER JOIN unitList ul ON "
					+ "nl.idunitList=ul.idunitList INNER JOIN targetType tt ON nl.idtargetType=tt.idtargetType INNER JOIN agentList al ON "
					+ "nl.targetValue=al.idagentList WHERE tt.targetName='agentName' AND ul.unitName='"+unitName+"'");

			while (res.next()) {
				
				ArrayList<String> aux = new ArrayList<String>();
				aux.add(res.getString(1));
				aux.add(res.getString(2));
				aux.add(res.getString(3));
				aux.add(res.getString(4));
				result.add(aux);
			}

			res = st.executeQuery("SELECT nl.normName, ul.unitName, tt.targetName, '_' FROM normList nl INNER JOIN unitList ul ON "
					+ "nl.idunitList=ul.idunitList INNER JOIN targetType tt ON nl.idtargetType=tt.idtargetType WHERE "
					+ "tt.targetName='agentName' AND nl.targetValue=-1 AND ul.unitName='"+unitName+"'");

			while (res.next()) {
				
				ArrayList<String> aux = new ArrayList<String>();
				aux.add(res.getString(1));
				aux.add(res.getString(2));
				aux.add(res.getString(3));
				aux.add(res.getString(4));
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

	
	/**
	 * This method accesses the database and returning the set of agents and their roles which play a role within unit U with position P.
	 * 
	 * @param unitName			Identifier of the organization unit U.
	 * @param positionValue		Identifier of the position P.
	 * 
	 * @return 					Returns a list (<requestedAgentName, roleName>) where agents play roles within unit U with position P.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	ArrayList<ArrayList<String>> getAgentsPlayingPositionInUnit(String unitName, String positionValue) throws MySQLException{

		Statement st = null;
		ResultSet res = null;

		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st = connection.createStatement();

			res = st.executeQuery("SELECT al.agentName, rl.roleName FROM agentList al INNER JOIN agentPlayList apl ON (apl.idagentList=al.idagentList) "
					+ "INNER JOIN roleList rl ON (rl.idroleList=apl.idroleList) INNER JOIN position p ON (rl.idposition=p.idposition) "
					+ "INNER JOIN unitList ul ON (ul.idunitList=rl.idunitList) WHERE ul.unitName='" + unitName + "' AND p.positionName='" + positionValue + "'");

			while (res.next()) {
				
				ArrayList<String> aux = new ArrayList<String>();
				aux.add(res.getString("agentName"));
				aux.add(res.getString("roleName"));
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

	
	/**
	 * This method accesses the database and returning the set of agents playing the role R within unit U.
	 * 
	 * @param unitName			Identifier of the organization unit U.
	 * @param roleName			Identifier of the role R.
	 * 
	 * @return 					Returns a list of agents playing the role R within unit U. 
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	ArrayList<String> getAgentsPlayingRoleInUnit(String unitName, String roleName) throws MySQLException {

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

			res = st.executeQuery("SELECT al.agentName FROM agentList al INNER JOIN agentPlayList apl ON (apl.idagentList=al.idagentList) "
					+ "INNER JOIN roleList rl ON (rl.idroleList=apl.idroleList) INNER JOIN unitList ul ON (ul.idunitList=rl.idunitList) "
					+ "WHERE ul.unitName='" + unitName + "' AND rl.roleName='" + roleName + "'");
			
			while (res.next())
				result.add(res.getString("agentName"));
			
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

	
	/**
	 * This method accesses the database and returning a list with the names of all agents playing the role R, whose position is P.
	 * 
	 * @param unitName			Identifier of the organization unit U.
	 * @param roleName			Identifier of the role R.
	 * @param positionValue		Identifier of the position P.
	 * 
	 * @return 					Returns a agent list whose they play role R within unit U with position P.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	ArrayList<String> getAgentsPlayingRolePositionInUnit(String unitName, String roleName, String positionValue) throws MySQLException {
		
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

			res = st.executeQuery("SELECT al.agentname FROM agentList al INNER JOIN agentPlayList apl ON (apl.idagentList=al.idagentList) "
					+ "INNER JOIN roleList rl ON (rl.idroleList=apl.idroleList) INNER JOIN position p ON (rl.idposition=p.idposition) "
					+ "INNER JOIN unitList ul ON (ul.idunitList=rl.idunitList) WHERE ul.unitName='" + unitName + "' AND rl.roleName='" + roleName + "' "
					+ "AND p.positionName='" + positionValue + "'");
					
			while(res.next()) {
				result.add(res.getString("agentName"));
			}

			return result;
		
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		}  finally {
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

	
	/**
	 * This method accesses the database and returning the set of agents and their roles which play a role within unit U with position P.
	 * In addition, we must consider that only return those roles whose visibility is public.
	 * 
	 * @param unitName			Identifier of the organization unit U.
	 * @param positionValue		Identifier of the position P.
	 * 
	 * @return 					Returns a list (<requestedAgentName, roleName>) where agents play roles whose visibility is public within unit U with position P.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	ArrayList<ArrayList<String>> getAgentsPlayingVisibilityPositionInUnit(String unitName, String positionValue) throws MySQLException {

		Statement st = null;
		ResultSet res = null;

		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st = connection.createStatement();

			res = st.executeQuery("SELECT al.agentName, rl.roleName FROM agentList al INNER JOIN agentPlayList apl ON "
					+ "(apl.idagentList=al.idagentList) INNER JOIN roleList rl ON (rl.idroleList=apl.idroleList) INNER JOIN "
					+ "position p ON (rl.idposition=p.idposition) INNER JOIN visibility v ON (rl.idvisibility=v.idvisibility) "
					+ "INNER JOIN unitList ul ON (ul.idunitList=rl.idunitList) WHERE ul.unitName='" + unitName + "' AND "
					+ "p.positionName='" + positionValue + "' AND v.visibility='public'");
					
			while (res.next()) {
				
				ArrayList<String> aux = new ArrayList<String>();
				aux.add(res.getString("agentName"));
				aux.add(res.getString("roleName"));
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

	
	/**
	 * This method accesses the database and returning the set of agents of unit U with their respective roles.
	 * 
	 * @param unitName			Identifier of the organization unit U.
	 * 
	 * @return 					For each agent within unit U, returns a tuple with the following information: agentName and roleName.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	ArrayList<ArrayList<String>> getAgentsRolesInUnit(String unitName) throws MySQLException {

		Statement st = null;
		ResultSet res = null;

		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st = connection.createStatement();

			res = st.executeQuery("SELECT rl.roleName, al.agentName FROM roleList rl INNER JOIN unitList ul ON (ul.idunitList=rl.idunitList) "
					+ "INNER JOIN agentPlayList apl ON (apl.idroleList=rl.idroleList) INNER JOIN agentList al ON (apl.idagentList=al.idagentList) "
					+ "WHERE ul.unitName='" + unitName + "'");
			
			while (res.next()) {

				ArrayList<String> aux = new ArrayList<String>();
				aux.add(res.getString("agentName"));
				aux.add(res.getString("roleName"));
				result.add(aux);
				
			}
			
			return result;
			
		} catch (SQLException e) {
			String message = l10n.getMessage(MessageID.MYSQL, e.getMessage());
			throw new MySQLException(message);
		}  finally {
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

	
	/**
	 * This method accesses the database and returning the set of agents playing roles of public visibility in unit U with their respective roles.
	 * 
	 * @param unitName			Identifier of the organization unit U.
	 * 
	 * @return 					For each agent playing roles of public visibility within unit U, returns a tuple with the following information: agentName and roleName.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	ArrayList<ArrayList<String>> getAgentsVisibilityRolesInUnit(String unitName) throws MySQLException {

		Statement st = null;
		ResultSet res = null;

		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st = connection.createStatement();

			res = st.executeQuery("SELECT al.agentName, rl.roleName FROM roleList rl INNER JOIN visibility v ON (v.idvisibility=rl.idvisibility) "
					+ "INNER JOIN unitList ul ON (ul.idunitList=rl.idunitList) INNER JOIN agentPlayList apl ON (apl.idroleList=rl.idroleList) "
					+ "INNER JOIN agentList al ON (apl.idagentList=al.idagentList) WHERE ul.unitName='" + unitName + "' AND v.visibility='public'");
			
			while (res.next()) {
				
				ArrayList<String> aux = new ArrayList<String>();
				aux.add(res.getString("agentName"));
				aux.add(res.getString("roleName"));
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

	
	/**
	 * This function retrieves information from the roles played by the agent A1, attending to:
	 *  - Returns all roles with public visibility played by agent A1 on any unit.
	 *  - In addition, also returns the roles with private visibility played by agent A1 in all units where both agents (A1 and A2) play some role as well.
	 * 
	 * @param requestedAgentName	Agent identifier which you want to know the information.
	 * @param agentName				Identifier of the agent requesting the service.
	 * 
	 * @return 						Returns a <RoleName, UnitName> list of the roles played by the agent A1.
	 * 
	 * @throws MySQLException		If any errors happens with the database connection.
	 * 
	 */
	ArrayList<ArrayList<String>> getInformAgentRole(String requestedAgentName, String agentName) throws MySQLException {
		
		Statement st = null;
		Statement st2 = null;
		ResultSet res = null;
		ResultSet res2 = null;

		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st = connection.createStatement();

			res = st.executeQuery("SELECT rl.roleName, ul.unitName FROM agentPlayList apl INNER JOIN agentList al ON "
					+ "(apl.idagentList=al.idagentList) INNER JOIN roleList rl ON (rl.idroleList=apl.idroleList) INNER JOIN "
					+ "visibility v ON (v.idvisibility=rl.idvisibility) INNER JOIN unitList ul ON (ul.idunitList=rl.idunitList) "
					+ "WHERE v.visibility='public' AND al.agentName='" + requestedAgentName + "'");
			
			while (res.next()) {
				
				ArrayList<String> aux = new ArrayList<String>();
				aux.add(res.getString("roleName"));
				aux.add(res.getString("unitName"));
				result.add(aux);

			}
			
			st2 = connection.createStatement();
			
			res2 = st2.executeQuery("SELECT rl.roleName, ul.unitName FROM agentPlayList apl INNER JOIN agentList al ON (apl.idagentList=al.idagentList) "
					+ "INNER JOIN roleList rl ON (rl.idroleList=apl.idroleList) INNER JOIN visibility v ON (v.idvisibility=rl.idvisibility) "
					+ "INNER JOIN unitList ul ON (ul.idunitList=rl.idunitList) INNER JOIN roleList rl2 ON (rl2.idunitList=rl.idunitList) "
					+ "INNER JOIN agentPlayList apl2 ON (apl2.idroleList=rl2.idroleList) INNER JOIN agentList al2 ON (al2.idagentList=apl2.idagentList) "
					+ "WHERE v.visibility='private' AND al.agentName='" + requestedAgentName + "' AND al2.agentName='" + agentName + "'");
			
			while (res2.next()) {
				
				ArrayList<String> aux = new ArrayList<String>();
				aux.add(res2.getString("roleName"));
				aux.add(res2.getString("unitName"));
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

	
	/**
	 * This method accesses the database and returning the set of roles played by the agent A in unit U.
	 * 
	 * @param unitName			Identifier of the organization unit U.
	 * @param agentName			Identifier of the agent A.
	 * 
	 * @return 					For each role, returns a tuple with the following information: roleName, visibility, accessibility and position.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	ArrayList<ArrayList<String>> getInformAgentRolesPlayedInUnit(String unitName, String agentName) throws MySQLException {

		Statement st = null;
		ResultSet res = null;

		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st = connection.createStatement();
			
			res = st.executeQuery("SELECT rl.roleName, v.visibility, a.accessibility, p.positionName FROM roleList rl INNER JOIN "
					+ "unitList ul ON (ul.idunitList=rl.idunitList) INNER JOIN visibility v ON (v.idvisibility=rl.idvisibility) INNER JOIN "
					+ "accessibility a ON (a.idaccessibility=rl.idaccessibility) INNER JOIN position p ON (p.idposition=rl.idposition) "
					+ "INNER JOIN agentPlayList apl ON (apl.idrolelist=rl.idrolelist) INNER JOIN agentList al ON (apl.idagentList=al.idagentList) "
					+ "WHERE al.agentName='" + agentName + "' AND ul.unitName='" + unitName + "'");
			
			while (res.next()) {
				
				ArrayList<String> aux = new ArrayList<String>();
				aux.add(res.getString("roleName"));
				aux.add(res.getString("visibility"));
				aux.add(res.getString("accessibility"));
				aux.add(res.getString("positionName"));
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
	

	/**
	 * This function returns the type of the unit U.
	 * The allowed values ​​are: hierarchy, team and flat.
	 * 
	 * @param unitName			Identifier of the organization unit.
	 * 
	 * @return 					Returns the type of the unit U.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	String getUnitType(String unitName) throws MySQLException {

		Statement st = null;
		ResultSet res = null;
		
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}
			
			st = connection.createStatement();
			
			res = st.executeQuery("SELECT unitTypeName FROM unitType WHERE idunitType = (SELECT idunitType FROM unitList WHERE unitName ='" + unitName + "')");

			if (res.next())
				return res.getString("unitTypeName");

			return "";

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
	
	
	/**
	 * This function is responsible for returning a list of the parents units of the unit U.
	 * 
	 * @param unitName			Identifier of the organization unit U.
	 * 
	 * @return 					Returns a list of the parent units of the unit U.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	ArrayList<String> getParentsUnit(String unitName) throws MySQLException {

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
			
			res = st.executeQuery("SELECT ul.unitName FROM unitList ul INNER JOIN unitHierarchy uh ON (ul.idunitList=uh.idParentUnit) "
					+ "INNER JOIN unitList ul2 ON (ul2.idunitList=uh.idChildUnit) WHERE ul2.unitName='" + unitName + "'");
			
			while (res.next())
				result.add(res.getString("unitName"));
			
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
	
	
	/**
	 * This method accesses the database and returning the amount of all the agents that play some role in unit U.
	 * 
	 * @param unitName			Identifier of the organization unit U.
	 * 
	 * @return 					Returns the amount of all the agents that play some role in unit U.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	int getInformQuantityAgentsRolesInUnit(String unitName) throws MySQLException {

		Statement st = null;
		ResultSet res = null;

		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st = connection.createStatement();

			res = st.executeQuery("SELECT COUNT(DISTINCT apl.idagentList) FROM agentPlayList apl INNER JOIN roleList rl ON "
					+ "(rl.idroleList=apl.idroleList) INNER JOIN unitList ul ON (ul.idunitList=rl.idunitList) WHERE ul.unitName='" + unitName + "'");
			
			res.next();
			
			return res.getInt(1);
			
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

	
	/**
	 * This method accesses the database and returning the amount of all the agents that play some role in unit U.
	 * The agents that play roles with private visibility will not be counted because they do not have sufficient permissions to know of its existence.
	 * 
	 * @param unitName			Identifier of the organization unit U.
	 * 
	 * @return 					Returns the amount of all the agents that play some role with public visibility in unit U.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	int getInformQuantityAgentsVisibilityRolesInUnit(String unitName) throws MySQLException {

		Statement st = null;
		ResultSet res = null;

		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st = connection.createStatement();

			res = st.executeQuery("SELECT COUNT(DISTINCT apl.idagentList) FROM agentPlayList apl INNER JOIN roleList rl ON "
					+ "(rl.idroleList=apl.idroleList) INNER JOIN visibility v ON (rl.idvisibility=v.idvisibility) INNER JOIN unitList ul ON "
					+ "(ul.idunitList=rl.idunitList) WHERE ul.unitName='" + unitName + "' AND v.visibility='public'");
					
			res.next();
			
			return res.getInt(1);
			
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

	
	/**
	 * This method accesses the database and returning the amount of all the agents that play role R in unit U.
	 * If the role is not registered in the unit provided, this method will return zero.
	 * 
	 * @param unitName			Identifier of the organization unit U.
	 * @param roleName			Identifier of the role R.
	 * 
	 * @return 					Returns the amount of all the agents that play role R in unit U.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	int getInformQuantityAgentsPlayingRoleInUnit(String unitName, String roleName) throws MySQLException {
		
		Statement st = null;
		ResultSet res = null;

		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st = connection.createStatement();

			res = st.executeQuery("SELECT COUNT(DISTINCT apl.idagentList) FROM agentPlayList apl INNER JOIN roleList rl ON "
					+ "(rl.idroleList=apl.idroleList) INNER JOIN unitList ul ON (ul.idunitList=rl.idunitList) "
					+ "WHERE ul.unitName='" + unitName + "' AND rl.roleName='" + roleName + "'");
				
			res.next();
			
			return res.getInt(1);
			
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

	
	/**
	 * This method accesses the database and returning the amount of all the agents that play some role within unit U in position P.
	 * If no roles registered with the given position, this method returns zero.
	 * 
	 * @param unitName			Identifier of the organization unit U.
	 * @param positionValue		Identifier of the position P.
	 * 
	 * @return 					Returns the amount of all the agents that play some role within unit U in position P.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	int getInformQuantityAgentsPlayingPositionInUnit(String unitName, String positionValue) throws MySQLException{
		
		Statement st = null;
		ResultSet res = null;

		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st = connection.createStatement();

			res = st.executeQuery("SELECT COUNT(DISTINCT apl.idagentList) from agentPlayList apl INNER JOIN roleList rl ON "
					+ "(rl.idroleList=apl.idroleList) INNER JOIN position p ON (rl.idposition=p.idposition) INNER JOIN unitList ul ON "
					+ "(ul.idunitList=rl.idunitList) WHERE ul.unitName='" + unitName + "' AND p.positionName='" + positionValue + "'");
			
			res.next();
			
			return res.getInt(1);
			
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

	
	/**
	 * This method accesses the database and returning the amount of all the agents that play role R, whose position is P in the unit U.
	 * If role provided is not registered in the unit or their position is not given, it will return a zero around me.
	 * 
	 * @param unitName			Identifier of the organization unit U.
	 * @param roleName			Identifier of the role R.
	 * @param positionValue		Identifier of the position P.
	 * 
	 * @return 					Returns the amount of all the agents that play role R, whose position is P in the unit U.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	int getInformQuantityAgentsPlayingRolePositionInUnit(String unitName, String roleName, String positionValue) throws MySQLException {

		Statement st = null;
		ResultSet res = null;

		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st = connection.createStatement();

			res = st.executeQuery("SELECT COUNT(DISTINCT apl.idagentList) FROM agentPlayList apl INNER JOIN roleList rl ON (rl.idroleList=apl.idroleList) "
					+ "INNER JOIN position p ON (rl.idposition=p.idposition) INNER JOIN unitList ul ON (ul.idunitList=rl.idunitList) "
					+ "WHERE  ul.unitName='" + unitName + "' AND rl.roleName='" + roleName + "' AND p.positionName='" + positionValue + "'");
				
			res.next();
			
			return res.getInt(1);
			
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

	
	/**
	 * This method accesses the database and returning the amount of all the agents that play some role within unit U in position P, considering that these roles will have a public visibility.
	 * If no roles registered with the given position, this method returns zero.
	 * 
	 * @param unitName			Identifier of the organization unit U.
	 * @param positionValue		Identifier of the position P.
	 * 
	 * @return 					Returns the amount of all the agents that play some role within unit U in position P, considering that these roles will have a public visibility.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	int getInformQuantityAgentsPlayingVisibilityPositionInUnit(String unitName, String positionValue) throws MySQLException {
		
		Statement st = null;
		ResultSet res = null;

		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}
			
			st = connection.createStatement();

			res = st.executeQuery("SELECT COUNT(DISTINCT apl.idagentList) FROM agentPlayList apl INNER JOIN roleList rl ON "
					+ "(rl.idroleList=apl.idroleList) INNER JOIN position p ON (rl.idposition=p.idposition) INNER JOIN visibility v ON "
					+ "(rl.idvisibility=v.idvisibility) INNER JOIN unitList ul ON (ul.idunitList=rl.idunitList) "
					+ "WHERE ul.unitName='" + unitName + "' AND p.positionName='" + positionValue + "' AND v.visibility='public'");
			
			res.next();
			
			return res.getInt(1);
			
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

	
	/**
	 * This method accesses the database and returning the next information associated with unit U: <UnitType, ParentName>.
	 * 
	 * @param unitName			Identifier of the organization unit U.
	 * 
	 * @return 					Returns the information <UnitType, ParentName> associated with unit U.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
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

	
	/**
	 * This method accesses the database and returning all the role's information belonging to the unit U.
	 * If permission is true, returns the information of all the roles, else returns the information from the roles with public visibility.
	 * 
	 * @param unitName			Identifier of the organization unit U.
	 * @param permitted			Variable indicating the behavior of the method.
	 * 
	 * @return 					Returns the information <RoleName, Accessibility, Visibility, Position> of all roles belonging to the unit U.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
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

			st = connection.createStatement();
			
			if (permitted)
				res = st.executeQuery("SELECT rl.roleName, a.accessibility, v.visibility, p.positionName FROM unitList ul INNER JOIN roleList rl ON"
						+ " (ul.idunitList=rl.idunitList) INNER JOIN accessibility a ON (a.idaccessibility=rl.idaccessibility) INNER JOIN visibility"
						+ " v ON (rl.idvisibility=v.idvisibility) INNER JOIN position p ON (p.idposition=rl.idposition) WHERE ul.unitName='"
						+ unitName +"'");

			else
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


	/**
	 * This method accesses the database and returning the next information associated with role R belonging to unit U: <Accessibility, Visibility, Position>.
	 * 
	 * @param roleName			Identifier of the role R.
	 * @param unitName			Identifier of the organization unit U.
	 * 
	 * @return 					Returns the information <Accessibility, Visibility, Position> associated with role R belonging to unit U.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	ArrayList<String> getInformRole(String roleName, String unitName) throws MySQLException {

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
			res = st.executeQuery("SELECT a.accessibility, v.visibility, p.positionName FROM roleList rl "
					+ "INNER JOIN visibility v ON (v.idvisibility=rl.idvisibility) INNER JOIN position p ON (p.idposition=rl.idposition) "
					+ "INNER JOIN accessibility a ON (a.idaccessibility=rl.idaccessibility) INNER JOIN unitList ul ON (ul.idunitList=rl.idunitList) "
					+ "WHERE ul.unitName='" + unitName + "' AND rl.roleName='" + roleName + "'");
					
			if (res.next()) {
				
				result.add(res.getString("accessibility"));
				result.add(res.getString("visibility"));
				result.add(res.getString("positionName"));
				
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

	
	/**
	 * This method accesses the database and returning the contents of the norm associated with the unit U whose name is N.
	 * 
	 * @param normName			Identifier of the norm N.
	 * @param unitName			Identifier of the organization unit U.
	 * 
	 * @return 					Returns the contents of the norm associated with the unit U whose name is N.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	String getNormContent(String normName, String unitName) throws MySQLException {
		
		Statement st = null;
		ResultSet res = null;

		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}
			
			st = connection.createStatement();
			
			res = st.executeQuery("SELECT nl.normContent FROM normList nl INNER JOIN unitList ul ON (ul.idunitList=nl.idunitList) "
					+ "WHERE nl.normName='" + normName + "' AND ul.unitName='" + unitName + "'");
					
			if (res.next())
				return res.getString("normContent");
			
			return "";

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

	
	/**
	 * This method returns a list with Jason rules associated with the norms registered in unit U, deontic D type.
	 * Return the norms with...
	 * 	- targetValue = '_', and
	 * 	- targetValue = positionName, that can be activated for the agent A, which plays that position in any unit.
	 * 
	 * @param unitName			Identifier of the organization unit U.
	 * @param agentName			Identifier of the agent A.
	 * @param deontic			Type of the norm('d','p' o 'f').
	 * @param service			Service affected by the norm.
	 * 
	 * @return 					Returns an ArrayList with Jason rules.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	ArrayList<String> getPositionNormRules(String unitName, String agentName, String deontic, String service) throws MySQLException {

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
					" where ul.unitName='"+unitName+"' and d.deonticdesc='"+deontic+"' and an.description='"+service+"' and tt.targetName='positionName' and nl.targetValue=-1");

			while (res.next())
				result.add(res.getString("normRule"));

			res2 = st.executeQuery("select distinct nl.normRule from normList nl inner join unitList ul on nl.idunitList=ul.idunitList inner join deontic d on d.iddeontic=nl.iddeontic inner join actionNorm an on an.idactionnorm=nl.idactionnorm inner join targetType tt on nl.idtargetType=tt.idtargetType inner join position p on nl.targetValue=p.idposition inner join roleList rl on rl.idposition=p.idposition inner join agentPlayList apl on apl.idroleList=rl.idrolelist inner join agentList al on al.idagentList=apl.idagentList" +
					" where ul.unitName='"+unitName+"' and d.deonticdesc='"+deontic+"' and an.description='"+service+"' and tt.targetName='positionName' and al.agentName='"+agentName+"'");

			while (res2.next())
				result.add(res2.getString("normRule"));

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


	/**
	 * This method returns a list with Jason rules associated with the norms registered in unit U, deontic D type.
	 * Return the norms with...
	 * 	- targetValue = '_', and
	 * 	- targetValue = roleName, that can be activated for the agent A, which plays that role in any unit.
	 * 
	 * @param unitName			Identifier of the organization unit U.
	 * @param agentName			Identifier of the agent A.
	 * @param deontic			Type of the norm('d','p' o 'f').
	 * @param service			Service affected by the norm.
	 * 
	 * @return 					Returns an ArrayList with Jason rules.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	ArrayList<String> getRoleNormRules(String unitName, String agentName, String deontic, String service) throws MySQLException {
		
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
					" where ul.unitName='"+unitName+"' and d.deonticdesc='"+deontic+"' and an.description='"+service+"' and tt.targetName='roleName' and nl.targetValue=-1");

			while (res.next())
				result.add(res.getString("normRule"));

			res2 = st.executeQuery("select distinct nl.normRule from normList nl inner join unitList ul on nl.idunitList=ul.idunitList inner join deontic d on d.iddeontic=nl.iddeontic inner join actionNorm an on an.idactionnorm=nl.idactionnorm inner join targetType tt on nl.idtargetType=tt.idtargetType inner join roleList rl on rl.idroleList=nl.targetValue inner join agentPlayList apl on apl.idroleList=rl.idrolelist inner join agentList al on al.idagentList=apl.idagentList" +
					" where ul.unitName='"+unitName+"' and d.deonticdesc='"+deontic+"' and an.description='"+service+"' and tt.targetName='roleName' and al.agentName='"+agentName+"'");

			while (res2.next())
				result.add(res2.getString("normRule"));

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


	/**
	 * This method retrieves the norms associated with unit U whose target is PositionName type and matches with the provided in targetValueName.
	 * 
	 * @param targetValueName	Identifier of the position T.
	 * @param unitName			Identifier of the organization unit U.
	 * 
	 * @return 					Returns the rules as <NormName, UnitName, TargetTypeName, TargetValueName>.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	ArrayList<ArrayList<String>> getPositionNorms(String targetValueName, String unitName) throws MySQLException {
		
		Statement st = null;
		ResultSet res = null;

		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st = connection.createStatement();
			
			if (targetValueName.equals("_"))
				res = st.executeQuery("SELECT n1.normName, u1.unitName, tt.targetName, '_' FROM normList n1 INNER JOIN unitList u1 ON "
						+ "n1.idunitList = u1.idunitList INNER JOIN targetType tt ON n1.idtargetType = tt.idtargetType WHERE "
						+ "tt.targetName='positionName' AND n1.targetValue=-1 AND u1.unitName='"+unitName+"'");
			else
				res = st.executeQuery("SELECT nl.normName, ul.unitName, tt.targetName, p.positionName FROM normList nl INNER JOIN unitList ul "
						+ "ON nl.idunitList=ul.idunitList INNER JOIN targetType tt ON nl.idtargetType=tt.idtargetType INNER JOIN position p "
						+ "ON nl.targetValue=p.idposition WHERE tt.targetName='positionName' AND p.positionName='"+targetValueName+"' "
						+ "AND ul.unitName='"+ unitName+"'");

			while (res.next()) {
				
				ArrayList<String> aux = new ArrayList<String>();
				aux.add(res.getString(1));
				aux.add(res.getString(2));
				aux.add(res.getString(3));
				aux.add(res.getString(4));
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

	
	/**
	 * This method retrieves the norms associated with unit U whose target is PositionName type.
	 * First, get the set of tuples that are associated with some Position and then recovers which are defined with value "_".
	 * 
	 * @param unitName			Identifier of the organization unit U.
	 * 
	 * @return 					Returns the rules as <NormName, UnitName, TargetTypeName, TargetValueName>.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	ArrayList<ArrayList<String>> getPositionNorms(String unitName) throws MySQLException {
		
		Statement st = null;
		ResultSet res = null;
		
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st = connection.createStatement();

			res = st.executeQuery("SELECT nl.normName, ul.unitName, tt.targetName, p.positionName FROM normList nl INNER JOIN unitList ul ON "
					+ "nl.idunitList=ul.idunitList INNER JOIN targetType tt ON nl.idtargetType=tt.idtargetType INNER JOIN position p ON "
					+ "nl.targetValue=p.idposition WHERE tt.targetName='positionName' AND ul.unitName='"+unitName +"'");

			while (res.next()) {
				
				ArrayList<String> aux = new ArrayList<String>();
				aux.add(res.getString(1));
				aux.add(res.getString(2));
				aux.add(res.getString(3));
				aux.add(res.getString(4));
				result.add(aux);
			}

			res = st.executeQuery("SELECT nl.normName, ul.unitName, tt.targetName, '_' FROM normList nl INNER JOIN unitList ul ON "
					+ "nl.idunitList=ul.idunitList INNER JOIN targetType tt ON nl.idtargetType=tt.idtargetType WHERE "
					+ "tt.targetName='positionName' AND nl.targetValue=-1 AND ul.unitName='"+unitName+"'");

			while (res.next()) {
				
				ArrayList<String> aux = new ArrayList<String>();
				aux.add(res.getString(1));
				aux.add(res.getString(2));
				aux.add(res.getString(3));
				aux.add(res.getString(4));
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

	
	/**
	 * This method retrieves the norms associated with unit U whose target is RoleName type and matches with the provided in targetValueName.
	 * 
	 * @param targetValueName	Identifier of the role T.
	 * @param unitName			Identifier of the organization unit U.
	 * 
	 * @return 					Returns the rules as <NormName, UnitName, TargetTypeName, TargetValueName>.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	ArrayList<ArrayList<String>> getRoleNorms(String targetValueName, String unitName) throws MySQLException {
		
		Statement st = null;
		ResultSet res = null;
		
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}

			st = connection.createStatement();
			
			if (targetValueName.equals("_"))
				res = st.executeQuery("SELECT nl.normName, ul.unitName, tt.targetName, '_' FROM normList nl INNER JOIN unitList ul ON "
						+ "nl.idunitList=ul.idunitList INNER JOIN targetType tt ON nl.idtargetType=tt.idtargetType WHERE tt.targetName='roleName' "
						+ "AND nl.targetValue=-1 AND ul.unitName='"+unitName+"'");
			else
				res = st.executeQuery("SELECT nl.normName, ul.unitName, tt.targetName, rl.roleName FROM normList nl INNER JOIN unitList ul ON "
						+ "nl.idunitList=ul.idunitList INNER JOIN targetType tt ON nl.idtargetType=tt.idtargetType INNER JOIN roleList rl ON "
						+ "nl.targetValue=rl.idroleList WHERE tt.targetName='roleName' AND rl.rolename='"+targetValueName+"' AND "
						+ "ul.unitName='"+unitName+"'");
			
			while (res.next()) {
				
				ArrayList<String> aux = new ArrayList<String>();
				aux.add(res.getString(1));
				aux.add(res.getString(2));
				aux.add(res.getString(3));
				aux.add(res.getString(4));
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

	
	/**
	 * This method retrieves the norms associated with unit U whose target is RoleName type.
	 * First, get the set of tuples that are associated with some Role and then recovers which are defined with value "_".
	 * 
	 * @param unitName			Identifier of the organization unit U.
	 * 
	 * @return 					Returns the rules as <NormName, UnitName, TargetTypeName, TargetValueName>.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	ArrayList<ArrayList<String>> getRoleNorms(String unitName) throws MySQLException {
		
		Statement st = null;
		ResultSet res = null;
		
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}
			
			st = connection.createStatement();

			res = st.executeQuery("SELECT nl.normName, ul.unitName, tt.targetName, rl.roleName FROM normList nl INNER JOIN unitList ul ON "
					+ "nl.idunitList=ul.idunitList INNER JOIN targetType tt ON nl.idtargetType=tt.idtargetType INNER JOIN roleList rl ON "
					+ "nl.targetValue=rl.idroleList WHERE tt.targetName='roleName' AND ul.unitName='"+unitName +"'");

			while (res.next()) {
				
				ArrayList<String> aux = new ArrayList<String>();
				aux.add(res.getString(1));
				aux.add(res.getString(2));
				aux.add(res.getString(3));
				aux.add(res.getString(4));
				result.add(aux);
			}

			res = st.executeQuery("SELECT nl.normName, ul.unitName, tt.targetName, '_' FROM normList nl INNER JOIN unitList ul ON "
					+ "nl.idunitList=ul.idunitList INNER JOIN targetType tt ON nl.idtargetType=tt.idtargetType WHERE "
					+ "tt.targetName='roleName' AND nl.targetValue=-1 AND ul.unitName='"+unitName+"'");

			while (res.next()) {
				
				ArrayList<String> aux = new ArrayList<String>();
				aux.add(res.getString(1));
				aux.add(res.getString(2));
				aux.add(res.getString(3));
				aux.add(res.getString(4));
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

	
	/**
	 * This method retrieves the norms associated with unit U whose target is RoleName type and refer to roles with public visibility.
	 * First, get the set of tuples that are associated with some Role and then recovers which are defined with value "_".
	 * 
	 * @param unitName			Identifier of the organization unit U.
	 * 
	 * @return 					Returns the rules as <NormName, UnitName, TargetTypeName, TargetValueName>.
	 * 
	 * @throws MySQLException	If any errors happens with the database connection.
	 * 
	 */
	ArrayList<ArrayList<String>> getPublicRoleNorms(String unitName) throws MySQLException {
		
		Statement st = null;
		ResultSet res = null;
		
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		boolean close = false;

		try {
			
			if (connection == null || connection.isClosed()) {
				connection = db.connect();
				close = true;
			}
			
			st = connection.createStatement();

			res = st.executeQuery("SELECT nl.normName, ul.unitName, tt.targetName, rl.rolename FROM normList nl INNER JOIN unitList ul ON "
					+ "nl.idunitList=ul.idunitList INNER JOIN targetType tt ON nl.idtargetType=tt.idtargetType INNER JOIN roleList rl ON "
					+ "nl. targetValue=rl.idroleList INNER JOIN visibility v ON rl.idvisibility=v.idvisibility WHERE "
					+ "tt.targetName='roleName' AND v.visibility='public' AND ul.unitName='"+unitName+"'");

			while (res.next()) {
				
				ArrayList<String> aux = new ArrayList<String>();
				aux.add(res.getString(1));
				aux.add(res.getString(2));
				aux.add(res.getString(3));
				aux.add(res.getString(4));
				result.add(aux);
			}

			res = st.executeQuery("SELECT nl.normName, ul.unitName, tt.targetName, '_' FROM normList nl INNER JOIN unitList ul ON "
					+ "nl.idunitList=ul.idunitList INNER JOIN targetType tt ON nl.idtargetType=tt.idtargetType WHERE "
					+ "tt.targetName='roleName' AND nl.targetValue=-1 AND ul.unitName='"+unitName+"'");

			while (res.next()) {
				
				ArrayList<String> aux = new ArrayList<String>();
				aux.add(res.getString(1));
				aux.add(res.getString(2));
				aux.add(res.getString(3));
				aux.add(res.getString(4));
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
