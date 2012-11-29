package es.upv.dsic.gti_ia.norms;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.upv.dsic.gti_ia.organization.DataBaseAccess;
import es.upv.dsic.gti_ia.organization.exception.MySQLException;
import es.upv.dsic.gti_ia.organization.exception.THOMASMessages;
import es.upv.dsic.gti_ia.organization.exception.THOMASMessages.MessageID;
import jason.asSyntax.Literal;

class BeliefDataBaseInterface {

	private DataBaseAccess db;

	/**
	 * Used for retrieve local messages.
	 */
	private THOMASMessages l10n;


	BeliefDataBaseInterface()
	{
		db = new DataBaseAccess();
		l10n = new THOMASMessages();
	}

	ArrayList<Literal> getIsUnit() throws MySQLException
	{
		ArrayList<Literal> percepts = new ArrayList<Literal>();
		Connection connection = null;
		Statement st = null;
		ResultSet res = null;

		try {

			connection = db.connect();


			//------ Predicados de Unidad -----
			st = connection.createStatement();
			res = st.executeQuery("SELECT * FROM unitList");
			
			while (res.next())
			{
				
				 String unitName = res.getString("unitName");
				 percepts.add(Literal.parseLiteral("isUnit("+unitName.toLowerCase()+")"));

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

		return percepts;
	}
	
	
	
	ArrayList<Literal> getHasType() throws MySQLException
	{
		ArrayList<Literal> percepts = new ArrayList<Literal>();
		Connection connection = null;
		Statement st = null;
		ResultSet res = null;

		try {

			connection = db.connect();

			//------ Predicados de Unidad -----
			st = connection.createStatement();
			res = st.executeQuery("SELECT ul.unitName, ut.unitTypeName FROM unitList ul inner join unitType ut on ul.idunitType=ut.idunitType");
			
			while (res.next())
			{
				
				 String unitName = res.getString("unitName");
				 String unitType = res.getString("unitTypeName");
				 
				 percepts.add(Literal.parseLiteral("hasType("+unitName.toLowerCase()+","+unitType+")"));

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

		return percepts;
	}
	
	
	ArrayList<Literal> getHasParent() throws MySQLException
	{
		ArrayList<Literal> percepts = new ArrayList<Literal>();
		Connection connection = null;
		Statement st = null;
		ResultSet res = null;

		try {

			connection = db.connect();

			//------ Predicados de Unidad -----
			st = connection.createStatement();
			res = st.executeQuery("select ul1.unitName,ul2.unitName from unitList ul1 inner join unitHierarchy uh on ul1.idunitList=uh.idChildUnit inner join unitList ul2 on uh.idParentUnit=ul2.idunitList");
			
			while (res.next())
			{
				
				 String unitChild = res.getString("ul1.unitName");
				 String unitParent = res.getString("ul2.unitName");
				 
				 
				 
				 percepts.add(Literal.parseLiteral("hasParent("+unitChild.toLowerCase()+","+unitParent.toLowerCase()+")"));

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

		return percepts;
	}
	
	ArrayList<Literal> getIsRole() throws MySQLException
	{
		ArrayList<Literal> percepts = new ArrayList<Literal>();
		Connection connection = null;
		Statement st = null;
		ResultSet res = null;

		try {

			connection = db.connect();
			
			
			st = connection.createStatement();
			res = st.executeQuery("select rl.roleName, ul.unitName from roleList rl inner join unitList ul on rl.idunitList=ul.idunitList");
			
			while (res.next())
			{
				
				 String roleName = res.getString("roleName");
				 String unitName = res.getString("unitName");
				 
				 
				 
				 percepts.add(Literal.parseLiteral("isRole("+roleName.toLowerCase()+","+unitName.toLowerCase()+")"));

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

		return percepts;
	}
	
	
	ArrayList<Literal> getHasAccessibility() throws MySQLException
	{
		ArrayList<Literal> percepts = new ArrayList<Literal>();
		Connection connection = null;
		Statement st = null;
		ResultSet res = null;

		try {

			connection = db.connect();
			

			st = connection.createStatement();
			res = st.executeQuery("select r1.roleName, u1.unitName, a.accessibility from roleList r1 inner join unitList u1 on r1.idunitList=u1.idunitList " +
					" inner join accessibility a on a.idaccessibility=r1.idaccessibility");
		
			while (res.next())
			{
				
				 String roleName = res.getString("roleName");
				 String unitName = res.getString("unitName");
				 String accessibility = res.getString("accessibility");
				 
				 
				 
				 percepts.add(Literal.parseLiteral("hasAccessibility("+roleName.toLowerCase()+","+unitName.toLowerCase()+","+accessibility.toLowerCase()+")"));

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

		return percepts;
	}
	
	ArrayList<Literal> getHasVisibility() throws MySQLException
	{
		ArrayList<Literal> percepts = new ArrayList<Literal>();
		Connection connection = null;
		Statement st = null;
		ResultSet res = null;

		try {

			connection = db.connect();
			st = connection.createStatement();
			res = st.executeQuery("select r1.roleName, u1.unitName, v.visibility from roleList r1 inner join unitList u1 on r1.idunitList=u1.idunitList " +
					" inner join visibility v on v.idvisibility=r1.idvisibility");
		
			while (res.next())
			{
				
				 String roleName = res.getString("roleName");
				 String unitName = res.getString("unitName");
				 String visibility = res.getString("visibility");
				 
				 
				 
				 percepts.add(Literal.parseLiteral("hasVisibility("+roleName.toLowerCase()+","+unitName.toLowerCase()+","+visibility.toLowerCase()+")"));

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

		return percepts;
	}
	
	ArrayList<Literal> getHasPosition() throws MySQLException
	{
		ArrayList<Literal> percepts = new ArrayList<Literal>();
		Connection connection = null;
		Statement st = null;
		ResultSet res = null;

		try {

			connection = db.connect();
			st = connection.createStatement();
			res = st.executeQuery("select r1.roleName, u1.unitName, p.position from roleList r1 inner join unitList u1 on r1.idunitList=u1.idunitList " +
					" inner join position p on p.idposition=r1.idposition");
		
			while (res.next())
			{
				
				 String roleName = res.getString("roleName");
				 String unitName = res.getString("unitName");
				 String position = res.getString("position");
				 
				 
				 
				 percepts.add(Literal.parseLiteral("hasPosition("+roleName.toLowerCase()+","+unitName.toLowerCase()+","+position.toLowerCase()+")"));

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

		return percepts;
	}
	
	ArrayList<Literal> getIsNorm() throws MySQLException
	{
		ArrayList<Literal> percepts = new ArrayList<Literal>();
		Connection connection = null;
		Statement st = null;
		ResultSet res = null;

		try {

			connection = db.connect();
			st = connection.createStatement();
			res = st.executeQuery("select nl.normName,ul.unitname from normList nl inner join unitList ul on nl.idunitList=ul.idunitList");
		
			while (res.next())
			{
				
				 String normName = res.getString("normName");
				 String unitName = res.getString("unitName");
				 
				 
				 
				 
				 percepts.add(Literal.parseLiteral("isNorm("+normName.toLowerCase()+","+unitName.toLowerCase()+")"));

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

		return percepts;
	}
	
	ArrayList<Literal> getHasDeontic() throws MySQLException
	{
		ArrayList<Literal> percepts = new ArrayList<Literal>();
		Connection connection = null;
		Statement st = null;
		ResultSet res = null;

		try {

			connection = db.connect();

			st = connection.createStatement();
			res = st.executeQuery("select nl.normName,ul.unitname, d.deonticdesc from normList nl inner join unitList ul on nl.idunitList=ul.idunitList inner join deontic d on nl.iddeontic=d.iddeontic");
		
			while (res.next())
			{
				
				 String normName = res.getString("normName");
				 String unitName = res.getString("unitName");
				 String deonticdesc = res.getString("deonticdesc");
				 
				 
				 
				 percepts.add(Literal.parseLiteral("hasDeontic("+normName.toLowerCase()+","+unitName.toLowerCase()+","+deonticdesc.toLowerCase()+")"));

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

		return percepts;
	}

	ArrayList<Literal> getIsAgent() throws MySQLException
	{
		ArrayList<Literal> percepts = new ArrayList<Literal>();
		Connection connection = null;
		Statement st = null;
		ResultSet res = null;

		try {

			connection = db.connect();
			st = connection.createStatement();
			res = st.executeQuery("select a1.agentName from agentList a1");
		
			while (res.next())
			{
				
				 String agentName = res.getString("agentName");
				 		 
				 percepts.add(Literal.parseLiteral("isAgent("+agentName.toLowerCase()+")"));

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

		return percepts;
	}
	
	ArrayList<Literal> getPlaysRole() throws MySQLException
	{
		ArrayList<Literal> percepts = new ArrayList<Literal>();
		Connection connection = null;
		Statement st = null;
		ResultSet res = null;

		try {

			connection = db.connect();
			st = connection.createStatement();
			res = st.executeQuery("select al.agentName, rl.roleName,ul.unitName from agentList al inner join agentPlayList apl on al.idagentList=apl.idagentList inner join roleList rl on apl.idroleList=rl.idroleList inner join unitList ul on ul.idunitList=rl.idunitList");
		
			while (res.next())
			{
				
				 String agentName = res.getString("agentName");
				 String roleName = res.getString("roleName");
				 String unitName = res.getString("unitName");
				 		 
				 percepts.add(Literal.parseLiteral("playsRole("+agentName.toLowerCase()+","+roleName.toLowerCase()+","+unitName.toLowerCase()+")"));

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

		return percepts;
	}
	
	ArrayList<Literal> getRoleCardinality() throws MySQLException
	{
		ArrayList<Literal> percepts = new ArrayList<Literal>();
		Connection connection = null;
		Statement st = null;
		ResultSet res = null;

		try {

			connection = db.connect();
			st = connection.createStatement();
			res = st.executeQuery("select rl.roleName,ul.unitName,count(*) from roleList rl inner join agentPlayList apl on rl.idroleList=apl.idroleList inner join unitList ul on rl.idunitList=ul.idunitList group by rl.idroleList");
		
			while (res.next())
			{
				
				 String cardinalidad = res.getString("count(*)");
				 String roleName = res.getString("roleName");
				 String unitName = res.getString("unitName");
				 		 
				 percepts.add(Literal.parseLiteral("roleCardinality("+roleName.toLowerCase()+","+unitName.toLowerCase()+","+cardinalidad+")"));

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

		return percepts;
	}
	
	ArrayList<Literal> getPositionCardinality() throws MySQLException
	{
		ArrayList<Literal> percepts = new ArrayList<Literal>();
		Connection connection = null;
		Statement st = null;
		ResultSet res = null;

		try {

			connection = db.connect();
			st = connection.createStatement();
			res = st.executeQuery("select p.position,ul.unitName, count(*) from position p inner join roleList rl on p.idposition=rl.idposition inner join agentPlayList apl on rl.idroleList=apl.idroleList inner join unitList ul on rl.idunitList=ul.idunitList group by ul.idunitList, p.idposition");
		
			while (res.next())
			{
				
				 String cardinalidad = res.getString("count(*)");
				 String positionName = res.getString("position");
				 String unitName = res.getString("unitName");
				 		 
				 percepts.add(Literal.parseLiteral("positionCardinality("+positionName.toLowerCase()+","+unitName.toLowerCase()+","+cardinalidad+")"));

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

		return percepts;
	}
	/**
	 * Returns true if parameter is valid and false if not 
	 * @param identifier
	 * @return boolean
	 * @throws MySQLException
	 */
	public boolean checkValidIdentifier(String identifier) throws MySQLException
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



}
