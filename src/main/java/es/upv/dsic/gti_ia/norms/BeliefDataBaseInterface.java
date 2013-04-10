package es.upv.dsic.gti_ia.norms;

import jason.asSyntax.ListTermImpl;
import jason.asSyntax.Literal;
import jason.asSyntax.LogicalFormula;
import jason.asSyntax.Rule;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import es.upv.dsic.gti_ia.organization.DataBaseAccess;
import es.upv.dsic.gti_ia.organization.exception.MySQLException;
import es.upv.dsic.gti_ia.organization.exception.THOMASMessages;
import es.upv.dsic.gti_ia.organization.exception.THOMASMessages.MessageID;
/**
 * This class gives us the interface for transform the Database in Jason predicates. 
 * 
 * @author root
 *
 */
public class BeliefDataBaseInterface {

	private DataBaseAccess db;

	/**
	 * Used for retrieve local messages.
	 */
	private THOMASMessages l10n;


	public BeliefDataBaseInterface()
	{
		db = new DataBaseAccess();
		l10n = new THOMASMessages();
	}

	/**
	 * Creates a new arrayList with the predicate "isUnit(UnitName)". This predicate is formed by a jason.asSyntax.Literal type. 
	 * @return ArrayList<Literal>
	 * @throws MySQLException
	 */
	public ArrayList<Literal> getIsUnit() throws MySQLException
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


	/**
	 * Creates a new arrayList with the predicate "hastype(UnitName,Type)". This predicate is formed by a jason.asSyntax.Literal type.
	 * @return ArrayList<Literal>
	 * @throws MySQLException
	 */
	public ArrayList<Literal> getHasType() throws MySQLException
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

	/**
	 * Creates a new arrayList with the predicate "hasParent(UnitName,ParentName)". This predicate is formed by a jason.asSyntax.Literal type.
	 * @return ArrayList<Literal>
	 * @throws MySQLException
	 */

	public ArrayList<Literal> getHasParent() throws MySQLException
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

	/**
	 * Creates a new arrayList with the predicate "isRole(RoleName, UnitName)". This predicate is formed by a jason.asSyntax.Literal type.
	 * @return ArrayList<Literal>
	 * @throws MySQLException
	 */
	public ArrayList<Literal> getIsRole() throws MySQLException
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


	/**
	 * Creates a new arrayList with the predicate "asAccessibility(RoleName,UnitName,Accessibility)". This predicate is formed by a jason.asSyntax.Literal type.
	 * @return ArrayList<Literal>
	 * @throws MySQLException
	 */
	public ArrayList<Literal> getHasAccessibility() throws MySQLException
	{
		ArrayList<Literal> percepts = new ArrayList<Literal>();
		Connection connection = null;
		Statement st = null;
		ResultSet res = null;

		try {

			connection = db.connect();


			st = connection.createStatement();
			res = st.executeQuery("select r1.roleName, u1.unitName, a.accessibility from roleList r1 inner join unitList u1 on r1.idunitList=u1.idunitList inner join accessibility a on a.idaccessibility=r1.idaccessibility");

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


	/**
	 * Creates a new arrayList with the predicate "hasVisibility(RoleName,UnitName,visibility)". This predicate is formed by a jason.asSyntax.Literal type.
	 * @return ArrayList<Literal>
	 * @throws MySQLException
	 */
	public ArrayList<Literal> getHasVisibility() throws MySQLException
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


	/**
	 * Creates a new arrayList with the predicate "hasPosition(Rolename,UnitName,Posicion)". This predicate is formed by a jason.asSyntax.Literal type.
	 * @return ArrayList<Literal>
	 * @throws MySQLException
	 */
	public ArrayList<Literal> getHasPosition() throws MySQLException
	{
		ArrayList<Literal> percepts = new ArrayList<Literal>();
		Connection connection = null;
		Statement st = null;
		ResultSet res = null;

		try {

			connection = db.connect();
			st = connection.createStatement();
			res = st.executeQuery("select r1.roleName, u1.unitName, p.positionName from roleList r1 inner join unitList u1 on r1.idunitList=u1.idunitList " +
			" inner join position p on p.idposition=r1.idposition");

			while (res.next())
			{

				String roleName = res.getString("roleName");
				String unitName = res.getString("unitName");
				String position = res.getString("positionName");



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


	/**
	 * Creates a new arrayList with the predicate "isNorm(NormName,UnitName)". This predicate is formed by a jason.asSyntax.Literal type.
	 * @return ArrayList<Literal>
	 * @throws MySQLException
	 */
	public ArrayList<Literal> getIsNorm() throws MySQLException
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


	/**
	 * Creates a new arrayList with the predicate "hasDeontic(NormName,UnitName,Deontic)". This predicate is formed by a jason.asSyntax.Literal type.
	 * @return ArrayList<Literal>
	 * @throws MySQLException
	 */
	public ArrayList<Literal> getHasDeontic() throws MySQLException
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


	/**
	 * Creates a new arrayList with the predicate "hasTarget(NormName,UnitName,TargetType,TargetValue)". This predicate is formed by a jason.asSyntax.Literal type.
	 * @return ArrayList<Literal>
	 * @throws MySQLException
	 */
	public ArrayList<Literal> getHasTarget() throws MySQLException
	{
		ArrayList<Literal> percepts = new ArrayList<Literal>();
		Connection connection = null;
		Statement st = null;
		Statement st2 = null;

		ResultSet res = null;
		ResultSet res2 = null;

		String valorTarget = "";


		try {

			connection = db.connect();

			st = connection.createStatement();
			res = st.executeQuery("select nl.normName,ul.unitname,t.targetName,nl.idnormList,nl.targetValue from normList nl inner join unitList ul on nl.idunitList=ul.idunitList inner join targetType t on nl.idtargetType=t.idtargetType ");

			while (res.next())
			{

				String targetValue = res.getString("targetValue");
				String targetName = res.getString("targetName");
				String normName = res.getString("normName");
				String unitName = res.getString("unitName");
				int idnorma = res.getInt("idnormList");
				if (targetValue.equals("-1"))
				{
					valorTarget = "_";
				}
				else if (targetName.equals("agentName"))
				{
					st2 = connection.createStatement();
					res2 = st2.executeQuery("select al.agentname from agentList al, normList nl where nl.idnormList=" +idnorma+" and al.idagentList=nl.targetValue");

					if (res2.next())
					{
						valorTarget = res2.getString("agentName");
					}

				}else if (targetName.equals("roleName"))
				{
					st2 = connection.createStatement();
					res2 = st2.executeQuery("select rl.roleName from normList nl, roleList rl where nl.idnormList="+idnorma+" and rl.idroleList=nl.targetValue");

					if (res2.next())
					{
						valorTarget = res2.getString("roleName");
					}
				}else if (targetName.equals("positionName"))
				{
					st2 = connection.createStatement();
					res2 = st2.executeQuery("select p.position from normList nl, position where nl.idnormList="+idnorma+" and p.idposition=nl.targetValue ");

					if (res2.next())
					{
						valorTarget = res2.getString("positionName");
					}
				} 


				percepts.add(Literal.parseLiteral("hasTarget("+normName.toLowerCase()+","+unitName.toLowerCase()+","+targetName.toLowerCase()+","+valorTarget.toLowerCase()+")"));

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

		return percepts;
	}


	/**
	 * Creates a new arrayList with the predicate "getHasAction(NormName,UnitName,Action)". This predicate is formed by a jason.asSyntax.Literal type.
	 * @return ArrayList<Literal>
	 * @throws MySQLException
	 */
	public ArrayList<Literal> getHasAction() throws MySQLException
	{
		ArrayList<Literal> percepts = new ArrayList<Literal>();
		Connection connection = null;
		Statement st = null;


		ResultSet res = null;





		try {

			connection = db.connect();

			st = connection.createStatement();
			res = st.executeQuery("select nl.normName,ul.unitname,an.description from normList nl inner join unitList ul on nl.idunitList=ul.idunitList inner join actionNorm an on nl.idactionnorm=an.idactionnorm ");

			while (res.next())
			{



				String normName = res.getString("normName");
				String unitName = res.getString("unitName");
				String action = res.getString("description");


				percepts.add(Literal.parseLiteral("hasAction("+normName.toLowerCase()+","+unitName.toLowerCase()+","+action.toLowerCase()+")"));

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
	 * Returns a norm rule.
	 * @return String
	 * @throws MySQLException
	 */
	public String getNormRule(String NormName, String UnitName) throws MySQLException
	{

		Connection connection = null;
		Statement st = null;
		ResultSet res = null;
		String normRule = "";

		try {

			connection = db.connect();
			st = connection.createStatement();
			res = st.executeQuery("select nl.normRule from normList nl inner join unitList ul on ul.idunitList=nl.idunitList where nl.normName='"+NormName+"' and ul.unitName='"+UnitName+"'");

			
			while (res.next())
			{

				normRule = res.getString("normRule");


			}

			return normRule;
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


	/**
	 * Builds a new Jason Rule with action := activation & not(expiration).
	 * @param norm
	 * @return
	 */
	public Rule buildNormRule(Norm norm)
	{
		Rule normRule = null;
		String normLiteral = "";

		if (!norm.getActivation().equals("") && !norm.getExpiration().equals(""))
		{
			normLiteral = norm.getActivation() + "&"+" not("+norm.getExpiration()+")";
		}else if (norm.getActivation().equals("") && !norm.getExpiration().equals(""))
		{
			normLiteral = " not("+norm.getExpiration()+")";
		}else if (!norm.getActivation().equals("") && norm.getExpiration().equals(""))
		{
			normLiteral = norm.getActivation();
		}
		
		if (normLiteral.equals("")) 
			normRule = new Rule(Literal.parseLiteral(norm.getAction()),null);
		else
			normRule = new Rule(Literal.parseLiteral(norm.getAction()),(LogicalFormula) ListTermImpl.parse(normLiteral));

		return normRule;
	}

	/**
	 * Creates a new arrayList with the predicate "isAgent(AgentName)". This predicate is formed by a jason.asSyntax.Literal type.
	 * @return ArrayList<Literal>
	 * @throws MySQLException
	 */
	public ArrayList<Literal> getIsAgent() throws MySQLException
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

	/**
	 * Creates a new arrayList with the predicate "playsRole(AgentName,RoleName,UnitName)". This predicate is formed by a jason.asSyntax.Literal type.
	 * @return ArrayList<Literal>
	 * @throws MySQLException
	 */
	public ArrayList<Literal> getPlaysRole() throws MySQLException
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

	/**
	 * Creates a new arrayList with the predicate "roleCardinality(RoleName,UnitName,NormContent)". This predicate is formed by a jason.asSyntax.Literal type.
	 * @return ArrayList<Literal>
	 * @throws MySQLException
	 */
	public ArrayList<Literal> getRoleCardinality() throws MySQLException
	{
		ArrayList<Literal> percepts = new ArrayList<Literal>();
		ArrayList<String> pe = new ArrayList<String>();
		Connection connection = null;
		Statement st = null;
		Statement st2 = null;

		ResultSet res = null;
		ResultSet res2 = null;

		try {



			connection = db.connect();
			st = connection.createStatement();
			res = st.executeQuery("select rl.roleName,ul.unitName,count(*) from roleList rl inner join agentPlayList apl on rl.idroleList=apl.idroleList inner join unitList ul on rl.idunitList=ul.idunitList group by rl.idroleList");


			st2 = connection.createStatement();
			res2 = st2.executeQuery("select rl.roleName,ul.unitName from roleList rl inner join unitList ul on rl.idunitList=ul.idunitList");


			while (res.next())
			{

				String cardinalidad = res.getString("count(*)");
				String roleName = res.getString("roleName");
				String unitName = res.getString("unitName");

				pe.add(roleName+","+unitName);

				percepts.add(Literal.parseLiteral("roleCardinality("+roleName.toLowerCase()+","+unitName.toLowerCase()+","+cardinalidad+")"));

			}


			while(res2.next())
			{
				String roleName = res2.getString("roleName");
				String unitName = res2.getString("unitName");

				if (!pe.contains(roleName+","+unitName))
					percepts.add(Literal.parseLiteral("roleCardinality("+roleName.toLowerCase()+","+unitName.toLowerCase()+","+0+")"));


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

		return percepts;
	}

	/**
	 * Creates a new arrayList with the predicate "positionCardinality(positionValue,unitName,cardinality)". This predicate is formed by a jason.asSyntax.Literal type.
	 * @return ArrayList<Literal>
	 * @throws MySQLException
	 */
	public ArrayList<Literal> getPositionCardinality() throws MySQLException
	{
		ArrayList<Literal> percepts = new ArrayList<Literal>();
		ArrayList<String> pe = new ArrayList<String>();

		Connection connection = null;
		Statement st = null;
		ResultSet res = null;

		Statement st2 = null;
		ResultSet res2 = null;

		try {

			connection = db.connect();
			st = connection.createStatement();
			res = st.executeQuery("select p.positionName,ul.unitName, count(*) from position p inner join roleList rl on p.idposition=rl.idposition inner join agentPlayList apl on rl.idroleList=apl.idroleList inner join unitList ul on rl.idunitList=ul.idunitList group by ul.idunitList, p.idposition");


		
			st2 = connection.createStatement();
			res2 = st2.executeQuery("select p.positionName,ul.unitName from position p inner join roleList rl on p.idposition=rl.idposition inner join unitList ul on rl.idunitList=ul.idunitList");


			while (res.next())
			{

				String cardinalidad = res.getString("count(*)");
				String positionName = res.getString("positionName");
				String unitName = res.getString("unitName");

				pe.add(positionName+","+unitName);

				percepts.add(Literal.parseLiteral("positionCardinality("+positionName.toLowerCase()+","+unitName.toLowerCase()+","+cardinalidad+")"));

			}

			while(res2.next())
			{
				String positionName = res2.getString("positionName");
				String unitName = res2.getString("unitName");

				if (!pe.contains(positionName+","+unitName))
					percepts.add(Literal.parseLiteral("positionCardinality("+positionName.toLowerCase()+","+unitName.toLowerCase()+","+0+")"));



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

		return percepts;
	}




}
