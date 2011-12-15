package persistence;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DataBaseInterface
{
	private DataBase	db;
	public DataBaseInterface()
	{
		db = new DataBase();
	}
	
	public boolean acquireRole(String unitName, String roleName, String agentName){
		return true;
	}
	
	public String allocateRole(String roleName, String unitName, String targetAgentName, String agentName){
		return "";
	}
	
	public boolean checkAgent(String agentName){
		return true;
	}
	
	public boolean checkAgentInUnit(String agentName, String unit){
		return true;
	}
	
	public boolean checkAgentPlaysRole(String agentName, String role, String unit){
		return true;
	}
	
	public boolean checkPlayedRoleInUnit(String role, String unit){
		return true;
	}
	
	public boolean checkTargetRoleNorm(String role, String unit){
		return true;
	}
	
	public boolean checkAgentPlaysPosition(String agent, String position, String unit){
		return true;
	}
	
	public boolean checkRoleInUnit(String reolename, String unitName){
		return true;
	}
	
	public boolean checkSubUnits(String unit){
		return true;
	}
	
	public boolean checkUnit(String unitName){
		return true;
	}
	
	public boolean checkVirtualUnit(String unitName){
		return true;
	}
	
	public String createRole(String roleName, String uniName, String accessibility, String visibility, String position){
		return "";
	}
	
	public String createUnit(String unitName, String unitType, String parentUnitName, String agentName, String creatorAgentName){
		return "";
	}
	
	public String deallocateRole(String roleName, String unitName, String targetAgentName, String agentName){
		return "";
	}
	
	public String deleteRole(String roleName, String unitName, String agentName){
		return "";
	}
	
	public String deleteUnit(String unitName, String agentName){
		return "";
	}
	
	public String jointUnit(String unitName, String parentName, String agentName){
		return "";
	}
	
	public String leaveRole(String unitName, String roleName, String agentName){
		return "";
	}
	
	public String getUnitType(String unit){
		return "";
	}
	
	public boolean agentInUnit(String unit){
		return true;
	}
	
	/*public boolean CheckExistsRole(String RoleID)
	{
		boolean exists = false;
		try
		{
			Statement stmt = db.connection.createStatement();
			ResultSet rs =
					stmt.executeQuery("SELECT * FROM role WHERE roleid='"
							+ RoleID.toLowerCase() + "'");
			while (rs.next())
			{
				exists = true;
			}
		}
		catch (Exception e)
		{
		}
		return exists;
	}
	public boolean CheckExistsRoleInUnit(String RoleID, String UnitID)
	{
		boolean exists = false;
		try
		{
			Statement stmt = db.connection.createStatement();
			ResultSet rs =
					stmt.executeQuery("SELECT * FROM unit WHERE unitid='"
							+ UnitID.toLowerCase() + "'");
			if (rs.next())
			{
				stmt = db.connection.createStatement();
				ResultSet rs2 =
						stmt.executeQuery("SELECT * FROM role WHERE roleid='"
								+ RoleID.toLowerCase() + "' AND unit="
								+ rs.getString("id"));
				if (rs2.next())
					exists = true;
			}
		}
		catch (Exception e)
		{
		}
		return exists;
	}
	public boolean CheckExistsUnit(String UnitID)
	{
		boolean exists = false;
		try
		{
			Statement stmt = db.connection.createStatement();
			ResultSet rs =
					stmt.executeQuery("SELECT * FROM unit WHERE unitid='"
							+ UnitID.toLowerCase() + "'");
			while (rs.next())
			{
				exists = true;
			}
		}
		catch (Exception e)
		{
		}
		return exists;
	}
	public boolean AddNewRole(String RoleID, String UnitID, String Visibility,
			String Accessibility, String Inheritance, String Position)
	{
		try
		{
			String unit, parentRole;
			// search unitID
			Statement stmt = db.connection.createStatement();
			ResultSet rs =
					stmt.executeQuery("SELECT * FROM role WHERE roleid='"
							+ Inheritance.toLowerCase() + "'");
			if (!rs.next())
				return false;
			parentRole = rs.getString("id");
			// search unitID
			stmt = db.connection.createStatement();
			rs =
					stmt.executeQuery("SELECT * FROM unit WHERE unitid='"
							+ UnitID.toLowerCase() + "'");
			if (!rs.next())
				return false;
			unit = rs.getString("id");
			// Add new role
			stmt = db.connection.createStatement();
			String sql =
					"INSERT INTO role (roleid,accessibility,position,visibility,inheritance,unit)VALUES('"
							+ RoleID.toLowerCase()
							+ "','"
							+ Accessibility.toLowerCase()
							+ "','"
							+ Position.toLowerCase()
							+ "','"
							+ Visibility.toLowerCase()
							+ "',"
							+ parentRole.toLowerCase()
							+ ","
							+ unit.toLowerCase() + ")";
			sql = sql.toLowerCase();
			
			// Execute the insert statement
			stmt.executeUpdate(sql);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public boolean AddNewUnit(String UnitID, String Type, String Goal,
			String ParentUnitID, String AgentID)
	{
		try
		{
			String parentUnit;
			// search unitID
			Statement stmt = db.connection.createStatement();
			ResultSet rs =
					stmt.executeQuery("SELECT * FROM unit WHERE unitid='"
							+ ParentUnitID.toLowerCase() + "'");
			if (!rs.next())
				return false;
			parentUnit = rs.getString("id");
			// search unitID
			// Add new role
			stmt = db.connection.createStatement();
			String sql =
					"INSERT INTO unit (unitid,type,goal,parentunit)VALUES('"
							+ UnitID.toLowerCase() + "','" + Type.toLowerCase()
							+ "','" + Goal.toLowerCase() + "'," + parentUnit
							+ ")";
			sql = sql.toLowerCase();
			stmt.executeUpdate(sql);
			this.AddNewRole("creator", UnitID, "hidden", "private", "member",
					"supervisor");
			this.AddNewAgentPlaysRole("creator", UnitID, AgentID);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public boolean DeleteRole(String RoleID, String UnitID)
	{
		try
		{
			Statement stmt = db.connection.createStatement();
			ResultSet rs =
					stmt.executeQuery("SELECT * FROM unit WHERE unitid='"
							+ UnitID.toLowerCase() + "'");
			if (rs.next())
			{
				String unit = rs.getString("id");
				stmt = db.connection.createStatement();
				ResultSet rs1 =
						stmt.executeQuery("SELECT * FROM role WHERE roleid='"
								+ RoleID.toLowerCase() + "' AND unit=" + unit);
				if (rs1.next())
				{
					String ID = rs1.getString("id");
					stmt = db.connection.createStatement();
					stmt.executeUpdate("DELETE FROM role WHERE roleid='"
							+ RoleID.toLowerCase() + "' AND unit=" + unit);
//					stmt = db.connection.createStatement();
//					stmt
//							.executeUpdate("UPDATE norm SET issuerrole=NULL WHERE issuerrole="
//									+ ID);
//					stmt = db.connection.createStatement();
//					stmt
//							.executeUpdate("UPDATE norm SET defenderrole=NULL WHERE defenderrole="
//									+ ID);
//					stmt = db.connection.createStatement();
//					stmt
//							.executeUpdate("UPDATE norm SET promoterrole=NULL WHERE promoterrole="
//									+ ID);
					return true;
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("DataBaseInterface.DeleteRole exception. Type = "+e.getClass().toString()+". Message follows:");
			System.out.println(e.getMessage());
		}
		return false;
	}
	public boolean CheckRoleHasNorms(String RoleID)
	{
		boolean hasNorm = false;
		try
		{
			Statement stmt = db.connection.createStatement();
			ResultSet rs =
					stmt.executeQuery("SELECT * FROM role WHERE roleid='"
							+ RoleID.toLowerCase() + "'");
			if (rs.next())
			{
				stmt = db.connection.createStatement();
				ResultSet rs2 =
						stmt
								.executeQuery("SELECT * FROM norm WHERE addressedrole="
										+ rs.getString("id"));
				if (rs2.next())
					hasNorm = true;
			}
			
		}
		catch (Exception e)
		{
		}
		return hasNorm;
	}
	public boolean CheckRoleIsPlayed(String RoleID)
	{
		boolean isPlayed = false;
		try
		{
			Statement stmt = db.connection.createStatement();
			ResultSet rs =
					stmt.executeQuery("SELECT * FROM role WHERE roleid='"
							+ RoleID.toLowerCase() + "'");
			if (rs.next())
			{
				stmt = db.connection.createStatement();
				ResultSet rs2 =
						stmt
								.executeQuery("SELECT * FROM entityplaylist WHERE role="
										+ rs.getString("id"));
				if (rs2.next())
					isPlayed = true;
			}
			
		}
		catch (Exception e)
		{
		}
		return isPlayed;
	}
	public boolean CheckRoleIsPlayedInUnit(String RoleString, String UnitString)
	{
		try
		{
			System.out.println("CHECKROLE 1");
			Statement stmt = db.connection.createStatement();
			ResultSet unitId =
					stmt.executeQuery("SELECT * FROM unit WHERE unitid='"
							+ UnitString.toLowerCase() + "'");
			if (!unitId.next())
				return false;
			System.out.println("unitID="+unitId.getString("id"));
			System.out.println("CHECKROLE 2");
			stmt = db.connection.createStatement();
			ResultSet roleId =
				stmt.executeQuery("SELECT * FROM role WHERE roleid='"
						+ RoleString.toLowerCase() + "'");
			if(!roleId.next())
				return false;
			System.out.println("roleId="+roleId.getString("id"));
			System.out.println("CHECKROLE 3");
			stmt = db.connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM entityplaylist WHERE role='"
					+roleId.getString("id")+"' AND unit='"+unitId.getString("id")+"'");
			rs.first();
			System.out.println("COUNT = "+rs.getInt(1));
			if(rs.getInt(1)>0){
				return true;
			}
			else{
				return false;
			}
		}
		catch (Exception e)
		{
			System.out.println("Exception in DataBase. Message follows:");
			System.out.println(e.getMessage());
			return false; // TODO: what to return? it should throw again the exception
		}
	}
	public boolean CheckUnitHasRole(String unitID)
	{
		boolean hasRole = false;
		try
		{
			Statement stmt = db.connection.createStatement();
			ResultSet rs =
					stmt.executeQuery("SELECT * FROM unit WHERE unitid='"
							+ unitID.toLowerCase() + "'");
			if (rs.next())
			{
				stmt = db.connection.createStatement();
				ResultSet rs2 =
						stmt.executeQuery("SELECT * FROM role WHERE unit="
								+ rs.getString("id"));
				if (rs2.next())
					hasRole = true;
			}
			
		}
		catch (Exception e)
		{
		}
		return hasRole;
	}
	public boolean CheckUnitHasUnit(String unitID)
	{
		boolean hasUnit = false;
		try
		{
			Statement stmt = db.connection.createStatement();
			ResultSet rs =
					stmt.executeQuery("SELECT * FROM unit WHERE unitid='"
							+ unitID.toLowerCase() + "'");
			if (rs.next())
			{
				stmt = db.connection.createStatement();
				ResultSet rs2 =
						stmt
								.executeQuery("SELECT * FROM unit WHERE parentunit="
										+ rs.getString("id"));
				if (rs2.next())
					hasUnit = true;
			}
			
		}
		catch (Exception e)
		{
		}
		return hasUnit;
	}
	public boolean CheckUnitHasMember(String unitID)
	{
		boolean hasMember = false;
		try
		{
			Statement stmt = db.connection.createStatement();
			ResultSet rs =
					stmt.executeQuery("SELECT * FROM unit WHERE unitid='"
							+ unitID.toLowerCase() + "'");
			if (rs.next())
			{
				stmt = db.connection.createStatement();
				ResultSet rs2 =
						stmt
								.executeQuery("SELECT * FROM entityplaylist WHERE unit="
										+ rs.getString("id"));
				if (rs2.next())
					hasMember = true;
			}
			
		}
		catch (Exception e)
		{
		}
		return hasMember;
	}
	
	public boolean CheckUnitHasOnlyThisMemberWithOneRole(String unitID, String agentID)
	{
		try{
			
			Statement st = db.connection.createStatement();
			ResultSet rs = st.executeQuery("SELECT id FROM unit WHERE unitid='"+unitID.toLowerCase()+"'");
			if(!rs.next())
			{
				System.err.println("Unit not found");
				return false;
			}
			String unitCode = rs.getString("id");
			
			rs= st.executeQuery("SELECT id FROM entity WHERE entityid='"+agentID.toLowerCase()+"'");
			if(!rs.next())
			{
				System.err.println("Agent not found");
				return false;
			}
			String agentCode = rs.getString("id");
			
			String query = "SELECT count(*) FROM role r, entityplaylist pl WHERE r.unit=pl.unit AND "+
			"pl.unit='"+unitCode+"' AND pl.entity='"+agentCode+"' AND NOT EXISTS "+
			"(SELECT * FROM unit u WHERE u.parentunit='"+unitCode+"')";
			rs = st.executeQuery(query);
			//rs = st.executeQuery("SELECT COUNT(*) FROM role r, entityplaylist pl WHERE r.unit='"+
			//		unitID+"' AND pl.entity='"+agentID+"' AND pl.unit='"+unitID+"'");
			if(rs.next()==false)
			{
				// something went wrong, count(*) should always return a value
				return false; 
			}
			System.out.println("CheckUnitHasOnlyThisMemberWithOneRole returns "+rs.getInt(1));
			if(rs.getInt(1)==1)
			{
				return true; // un solo agente juega un rol, no hay más roles que éste
				// y las unidad no tiene unidades hijas
			}
			else
			{
				return false;
			}
		}
		catch(Exception exc)
		{
			System.err.println("Exception while in CheckUnitHasOnlyThisMemberWithOneRole");
			System.err.print(exc);
			return false;
		}
	}
	
	public boolean CheckUnitIsEmpty(String unitID)
	{
		if(CheckUnitHasMember(unitID) || CheckUnitHasRole(unitID) || CheckUnitHasUnit(unitID))
			return false;
		else
			return true;
	}
	
	
	public boolean DeleteUnit(String unitID)
	{
		try
		{
			Statement st = db.connection.createStatement();
			ResultSet rs = st.executeQuery("SELECT id FROM unit WHERE unitid='"+unitID+"'");
			if(!rs.next())
			{
				System.err.println("Error in Delete unit. Unit does not exist. UnitID="+unitID);
				return false;
			}
			String unitCode = rs.getString("id");
			
			// Tests whether there is no role, just one role or more than one role in the unit: 
			rs = st.executeQuery("SELECT roleid FROM role WHERE unit ='"+unitCode+"'");
			if(rs.next())
			{
				// here there is at least one role
				if(!rs.isLast())
				{
					// more than one role, cannot continue
					System.err.println("Error in Delete unit: unit contains more than 1 role");
					return false;
				}
				// here there is exactly one role in this unit, delete the relationships among
				// agent-role and role-unit
				String roleID = rs.getString("roleid");
				this.DeleteAgentPlaysRole(roleID, unitID);
				this.DeleteRole(roleID, unitID);
				// everything is now ready
			}
			
			// now is the right time to delete the entry for the unit in the "unit" table:
			Statement stmt = db.connection.createStatement();
			stmt.executeUpdate("DELETE FROM unit WHERE unitid='"
					+ unitID.toLowerCase() + "'");
			
//			this.DeleteAgentPlaysRole("creator", unitID);
//			this.DeleteRole("creator", unitID);
			
			return true;
		}
		catch (Exception e)
		{
			System.err.println("DBInterface.Delete Unit: Exception. Message follows:");
			System.err.println(e.toString());
			return false;
		}
	}
	public boolean CheckExistsNorm(String normID)
	{
		boolean exists = false;
		try
		{
			Statement stmt = db.connection.createStatement();
			ResultSet rs =
					stmt.executeQuery("SELECT * FROM norm WHERE normid='"
							+ normID.toLowerCase() + "'");
			if (rs.next())
				exists = true;
		}
		catch (Exception e)
		{
		}
		return exists;
	}
	public boolean DeleteNorm(String normID)
	{
		try
		{
			Statement stmt = db.connection.createStatement();
			stmt.executeUpdate("DELETE FROM norm WHERE normid='"
					+ normID.toLowerCase() + "'");
			return true;
		}
		catch (Exception e)
		{
		}
		return false;
	}
	public boolean CheckExistsAgent(String agentID)
	{
		boolean exists = false;
		try
		{
			Statement stmt = db.connection.createStatement();
			ResultSet rs =
					stmt.executeQuery("SELECT * FROM entity WHERE entityid='"
							+ agentID.toLowerCase() + "'");
			if (rs.next())
				exists = true;
		}
		catch (Exception e)
		{
		}
		return exists;
	}
	public List<String> GetRoleUnitList(String agentID)
	{
		List<String> roleUnitList = new ArrayList<String>();
		try
		{
			Statement stmt = db.connection.createStatement();
			ResultSet r =
					stmt.executeQuery("SELECT * FROM entity WHERE entityid='"
							+ agentID.toLowerCase() + "'");
			if (r.next())
			{
				ResultSet rs =
						stmt
								.executeQuery("SELECT * FROM entityplaylist WHERE entity="
										+ r.getString("id"));
				while (rs.next())
				{
					stmt = db.connection.createStatement();
					ResultSet rsRole =
							stmt.executeQuery("SELECT * FROM role WHERE id="
									+ rs.getString("role"));
					stmt = db.connection.createStatement();
					ResultSet rsUnit =
							stmt.executeQuery("SELECT * FROM unit WHERE id="
									+ rs.getString("unit"));
					if (rsRole.next() && rsUnit.next())
						roleUnitList.add("(" + rsRole.getString("roleid") + ","
								+ rsUnit.getString("UnitID") + ")");
				}
			}
		}
		catch (Exception e)
		{
		}
		return roleUnitList;
	}
	public List<String> GetEntityRoleList(String unitID, String roleID)
	{
		List<String> entityRoleList = new ArrayList<String>();
		try
		{
			Statement stmt = db.connection.createStatement();
			ResultSet r =
					stmt.executeQuery("SELECT * FROM unit WHERE unitid='"
							+ unitID.toLowerCase() + "'");
			if (r.next())
			{
				stmt = db.connection.createStatement();
				ResultSet rs =
						stmt
								.executeQuery("SELECT * FROM entityplaylist WHERE unit="
										+ r.getString("id"));
				while (rs.next())
				{
					stmt = db.connection.createStatement();
					ResultSet rsRole =
							stmt.executeQuery("SELECT * FROM role WHERE id="
									+ rs.getString("role"));
					stmt = db.connection.createStatement();
					ResultSet rsEntity =
							stmt.executeQuery("SELECT * FROM entity WHERE id="
									+ rs.getString("entity"));
					if (rsRole.next()
							&& rsEntity.next()
							&& (roleID == "" || roleID.equalsIgnoreCase(rsRole
									.getString("RoleID"))))
						entityRoleList.add("(" + rsEntity.getString("EntityID")
								+ "," + rsRole.getString("RoleID") + ")");
				}
			}
		}
		catch (Exception e)
		{
		}
		
		return entityRoleList;
	}
	public int GetQuantityMember(String unitID, String roleID)
	{
		int quantityMember = 0;
		try
		{
			Statement stmt = db.connection.createStatement();
			ResultSet r =
					stmt.executeQuery("SELECT * FROM unit WHERE unitid='"
							+ unitID.toLowerCase() + "'");
			if (r.next())
			{
				stmt = db.connection.createStatement();
				ResultSet rs =
						stmt
								.executeQuery("SELECT * FROM entityplaylist WHERE unit="
										+ r.getString("id"));
				while (rs.next())
				{
					stmt = db.connection.createStatement();
					ResultSet rsRole =
							stmt.executeQuery("SELECT * FROM role WHERE id="
									+ rs.getString("role"));
					if (rsRole.next())
						if (roleID == ""
								|| roleID.equalsIgnoreCase(rsRole
										.getString("roleid")))
							quantityMember++;
				}
			}
		}
		catch (Exception e)
		{
		}
		return quantityMember;
	}
	public String GetParentUnitID(String unitID)
	{
		String parentUnitID = "";
		try
		{
			Statement stmt = db.connection.createStatement();
			ResultSet r =
					stmt.executeQuery("SELECT * FROM unit WHERE unitid='"
							+ unitID.toLowerCase() + "'");
			if (r.next())
			{
				stmt = db.connection.createStatement();
				ResultSet rs =
						stmt.executeQuery("SELECT * FROM unit WHERE id="
								+ r.getString("parentUnit"));
				if (rs.next())
				{
					parentUnitID = rs.getString("unitid");
				}
			}
		}
		catch (Exception e)
		{
		}
		return parentUnitID;
	}
	public String GetUnitType(String unitID)
	{
		String type = "";
		try
		{
			Statement stmt = db.connection.createStatement();
			ResultSet r =
					stmt.executeQuery("SELECT * FROM unit WHERE unitid='"
							+ unitID.toLowerCase() + "'");
			if (r.next())
			{
				type = r.getString("Type");
			}
		}
		catch (Exception e)
		{
		}
		return type;
	}
	public String GetUnitGoal(String unitID)
	{
		String type = "";
		try
		{
			Statement stmt = db.connection.createStatement();
			ResultSet r =
					stmt.executeQuery("SELECT * FROM unit WHERE unitid='"
							+ unitID.toLowerCase() + "'");
			if (r.next())
			{
				type = r.getString("Goal");
			}
		}
		catch (Exception e)
		{
		}
		return type;
	}
	public List<String> GetRoleList(String unitID)
	{
		List<String> roleList = new ArrayList<String>();
		try
		{
			Statement stmt = db.connection.createStatement();
			ResultSet r =
					stmt.executeQuery("SELECT * FROM unit WHERE unitid='"
							+ unitID.toLowerCase() + "'");
			if (r.next())
			{
				stmt = db.connection.createStatement();
				ResultSet rs =
						stmt.executeQuery("SELECT * FROM role WHERE unit="
								+ r.getString("id"));
				while (rs.next())
				{
					roleList.add(rs.getString("roleid"));
				}
			}
		}
		catch (Exception e)
		{
		}
		
		return roleList;
	}
	public List<String> GetRoleNormsList(String roleID)
	{
		List<String> normList = new ArrayList<String>();
		try
		{
			Statement stmt = db.connection.createStatement();
			ResultSet r =
					stmt.executeQuery("SELECT * FROM role WHERE roleid='"
							+ roleID.toLowerCase() + "'");
			if (r.next())
			{
				stmt = db.connection.createStatement();
				ResultSet rs =
						stmt
								.executeQuery("SELECT * FROM (((norm N left join maxcardinalitynorm MC on N.id=MC.normid)"
										+ "left join incompatibilitynorm IC on N.id=IC.normid)"
										+ "left join simplerequestnorm SR on N.id=SR.normid)"
										+ "where MC.role1id="
										+ r.getString("ID")
										+ " OR IC.role1id="
										+ r.getString("ID")
										+ " OR SR.roleid="
										+ r.getString("ID"));
				while (rs.next())
				{
					normList.add(rs.getString("normid"));
				}
			}
		}
		catch (Exception e)
		{
		}
		
		return normList;
	}
	public boolean CheckAgentPlaysRole(String roleID, String unitID,
			String agentID)
	{
		boolean exists = false;
		try
		{
			Statement stmt = db.connection.createStatement();
			String sql =
					"SELECT * FROM (((EntityPlayList EPR  JOIN Role R ON R.ID=EPR.ROLE) JOIN Entity E ON E.ID=EPR.ENTITY) JOIN Unit U ON U.ID=EPR.UNIT) WHERE R.RoleID='"
							+ roleID.toLowerCase()
							+ "' AND U.UNitID='"
							+ unitID.toLowerCase()
							+ "' AND E.EntityID='"
							+ agentID.toLowerCase() + "'";
			ResultSet r = stmt.executeQuery(sql.toLowerCase());
			if (r.next())
			{
				exists = true;
			}
		}
		catch (Exception e)
		{
		}
		
		return exists;
	}
	public boolean AddNewAgentPlaysRole(String roleID, String unitID,
			String agentID)
	{
		try
		{
			Statement stmt = db.connection.createStatement();
			String sql =
					"SELECT * FROM UNIT WHERE UNITID='" + unitID.toLowerCase()
							+ "'";
			sql = sql.toLowerCase();
			ResultSet rs = stmt.executeQuery(sql);
			
			if (!rs.next())
				return false;
			String unit = rs.getString("id");
			stmt = db.connection.createStatement();
			sql =
					"SELECT * FROM Role WHERE RoleID='" + roleID.toLowerCase()
							+ "' AND unit='"+unit.toLowerCase()+"'";
			sql = sql.toLowerCase();
			rs = stmt.executeQuery(sql);
			if (!rs.next())
				return false;
			String role = rs.getString("id");
			if (roleID.equalsIgnoreCase("member")
					&& unitID.equalsIgnoreCase("virtual"))
			{
				stmt = db.connection.createStatement();
				sql =
						"INSERT INTO Entity (entityID) VALUES('"
								+ agentID.toLowerCase() + "')";
				sql = sql.toLowerCase();
				stmt.executeUpdate(sql);
			}
			stmt = db.connection.createStatement();
			sql =
					"SELECT * FROM Entity WHERE EntityID='"
							+ agentID.toLowerCase() + "'";
			sql = sql.toLowerCase();
			rs = stmt.executeQuery(sql);
			if (!rs.next())
				return false;
			String entity = rs.getString("id");
			// search unitID
			// Add new role
			stmt = db.connection.createStatement();
			sql =
					"INSERT INTO entityplaylist (unit,entity,role)VALUES("
							+ unit + "," + entity + "," + role + ")";
			sql = sql.toLowerCase();
			// Execute the insert statement
			stmt.executeUpdate(sql);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public boolean DeleteAgentPlaysRole(String roleID, String unitID,
			String agentID)
	{
		try
		{
			if (unitID.equalsIgnoreCase("virtual")
					&& roleID.equalsIgnoreCase("member"))
			{
				Statement stmt = db.connection.createStatement();
				String sql =
						"SELECT * FROM Entity WHERE EntityID='"
								+ agentID.toLowerCase() + "'";
				sql = sql.toLowerCase();
				ResultSet rs = stmt.executeQuery(sql);
				if (!rs.next())
					return false;
				String entity = rs.getString("id");
				stmt = db.connection.createStatement();
				sql = "DELETE FROM entityplaylist WHERE ENTITY=" + entity;
				sql = sql.toLowerCase();
				stmt.executeUpdate(sql);
				stmt.executeUpdate("DELETE FROM entity WHERE ID=" + entity);
			}
			else
			{
				Statement stmt = db.connection.createStatement();
				String sql =
						"SELECT * FROM UNIT WHERE UNITID='"
								+ unitID.toLowerCase() + "'";
				sql = sql.toLowerCase();
				ResultSet rs = stmt.executeQuery(sql);
				if (!rs.next())
					return false;
				String unit = rs.getString("id");
				stmt = db.connection.createStatement();
				sql =
					"SELECT * FROM Role WHERE RoleID='"
						+ roleID.toLowerCase() + "' and unit='"+unit+"'";
				sql = sql.toLowerCase();
				rs = stmt.executeQuery(sql);
				if (!rs.next())
					return false;
				String role = rs.getString("id");
				stmt = db.connection.createStatement();
				sql =
						"SELECT * FROM Entity WHERE EntityID='"
								+ agentID.toLowerCase() + "'";
				sql = sql.toLowerCase();
				rs = stmt.executeQuery(sql);
				if (!rs.next())
					return false;
				String entity = rs.getString("id");
				// search unitID
				// Add new role
				stmt = db.connection.createStatement();
				sql =
						"DELETE FROM entityplaylist WHERE UNIT=" + unit
								+ " AND ENTITY=" + entity + " AND Role=" + role;
				sql = sql.toLowerCase();
				stmt.executeUpdate(sql);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public boolean DeleteAgentPlaysRole(String roleID, String unitID)
	{
		try
		{
			
			Statement stmt = db.connection.createStatement();
			String sql = "SELECT * FROM UNIT WHERE UNITID='" + unitID.toLowerCase()
							+ "'";
			sql = sql.toLowerCase();
			ResultSet rs = stmt.executeQuery(sql);
			if (!rs.next())
				return false;
			String unit = rs.getString("id");
			stmt = db.connection.createStatement();
			sql = "SELECT * FROM Role WHERE RoleID='" + roleID.toLowerCase()
							+ "'";
			sql = sql.toLowerCase();
			rs = stmt.executeQuery(sql);
			if (!rs.next())
				return false;
			String role = rs.getString("id");
			stmt = db.connection.createStatement();
			sql = "DELETE FROM entityplaylist WHERE UNIT=" + unit
							+ " AND Role=" + role;
			sql = sql.toLowerCase();
			stmt.executeUpdate(sql);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	// Añadido
	public Integer GetNormID(String normID)
	{
		try
		{
			Statement stmt = db.connection.createStatement();
			String sql =
					"SELECT * FROM norm WHERE normid='" + normID.toLowerCase()
							+ "'";
			sql = sql.toLowerCase();
			ResultSet rs = stmt.executeQuery(sql);
			if (!rs.next())
				return -1;
			return rs.getInt("id");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -1;
		}
	}

	public boolean AddNewNorm(String normID, String normContent)
	{
		try
		{
			
			// Insert into database
			Statement stmt = db.connection.createStatement();
			normContent = normContent.replace("\'", "");
			String sql =
					"INSERT INTO norm (normid,normcontent) " + "VALUES ('"
							+ normID.toLowerCase() + "','"
							+ normContent.toLowerCase() + "')";
			sql = sql.toLowerCase();
			// System.out.println(sql);
			stmt.executeUpdate(sql);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	public String GetNormContent(String normID)
	{
		try
		{
			Statement stmt = db.connection.createStatement();
			ResultSet rs =
					stmt.executeQuery("SELECT * FROM norm WHERE normid='"
							+ normID.toLowerCase() + "'");
			if (rs.next())
			{
				return rs.getString("normcontent");
			}
			else
			{
				return null;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean CheckSimpleRequestNorm(String deonticConcept, String roleID,
			String serviceName)
	{
		try
		{
			Statement stmt = db.connection.createStatement();
			ResultSet rs =
					stmt.executeQuery("SELECT * FROM service WHERE serviceid='"
							+ serviceName.toLowerCase() + "'");
			if (!rs.next())
				return false;
			String serviceID = rs.getString("id");
			
			stmt = db.connection.createStatement();
			String sql =
					"SELECT * FROM simplerequestnorm WHERE roleid="
							+ roleID.toLowerCase() + " AND serviceid="
							+ serviceID.toLowerCase() + " AND deonticconcept='"
							+ deonticConcept.toLowerCase() + "'";
			// System.out.println(sql);
			rs = stmt.executeQuery(sql);
			if (rs.next())
				return true;
			return false;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	public List<String> GetAgentPosition(String AgentName, String UnitName)
			throws Exception
	{
		List<String> positionList = new ArrayList<String>();
		try
		{
			Statement stmt = db.connection.createStatement();
			String sql =
					"SELECT * FROM ((entityplaylist epl JOIN role r ON epl.role=r.id)JOIN entity e ON epl.entity=e.id) JOIN unit u ON u.id=epl.unit"
							+ " WHERE e.entityid='"
							+ AgentName.toLowerCase()
							+ "'";
			sql = sql.toLowerCase();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next())
			{
				if (UnitName.equalsIgnoreCase("")
						|| rs.getString("unitid").equalsIgnoreCase(UnitName))
					if (!positionList.contains(rs.getString("position")
							.toLowerCase()))
						positionList
								.add(rs.getString("position").toLowerCase());
			}
			return positionList;
		}
		catch (Exception e)
		{
			throw e;
		}
	}
	
	public boolean AddNewSimpleRequestNorm(String deonticConcept, String role,
			String service, Integer normID)
	{
		try
		{
			Statement stmt = db.connection.createStatement();
			ResultSet rs =
					stmt.executeQuery("SELECT * FROM role WHERE roleid='"
							+ role.toLowerCase() + "'");
			if (!rs.next())
				return false;
			String roleID = rs.getString("id");
			stmt = db.connection.createStatement();
			rs =
					stmt.executeQuery("SELECT * FROM service WHERE serviceid='"
							+ service.toLowerCase() + "'");
			if (!rs.next())
				return false;
			String serviceID = rs.getString("id");
			// AddNewIncompatiblityNorm
			stmt = db.connection.createStatement();
			String sql =
					"INSERT INTO simplerequestnorm (deonticconcept,roleid,serviceid,normid)VALUES('"
							+ deonticConcept
							+ "',"
							+ roleID
							+ ","
							+ serviceID
							+ "," + normID + ")";
			stmt.executeUpdate(sql);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public boolean AddNewIncompatiblityNorm(String role1, String role2,
			Integer normID)
	{
		try
		{
			Statement stmt = db.connection.createStatement();
			String sql =
					"SELECT * FROM role WHERE roleid='" + role1.toLowerCase()
							+ "'";
			sql = sql.toLowerCase();
			ResultSet rs = stmt.executeQuery(sql);
			if (!rs.next())
				return false;
			String role1ID = rs.getString("id");
			stmt = db.connection.createStatement();
			sql =
					"SELECT * FROM role WHERE roleid='" + role2.toLowerCase()
							+ "'";
			sql = sql.toLowerCase();
			rs = stmt.executeQuery(sql);
			if (!rs.next())
				return false;
			String role2ID = rs.getString("id");
			// AddNewIncompatiblityNorm
			stmt = db.connection.createStatement();
			sql =
					"INSERT INTO incompatibilitynorm (role1id,role2id,normid)VALUES("
							+ role1ID + "," + role2ID + "," + normID + ")";
			sql = sql.toLowerCase();
			stmt.executeUpdate(sql);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public boolean AddNewMaxCardinalityNorm(String role1, String role2,
			String card, Integer normID)
	{
		try
		{
			Statement stmt = db.connection.createStatement();
			String sql =
					"SELECT * FROM role WHERE roleid='" + role1.toLowerCase()
							+ "'";
			ResultSet rs = stmt.executeQuery(sql.toLowerCase());
			if (!rs.next())
				return false;
			String role1ID = rs.getString("id");
			
			stmt = db.connection.createStatement();
			sql =
					"SELECT * FROM role WHERE roleid='" + role2.toLowerCase()
							+ "'";
			rs = stmt.executeQuery(sql.toLowerCase());
			if (!rs.next())
				return false;
			String role2ID = rs.getString("id");
			int max;
			max = Integer.parseInt(card);
			// AddNewIncompatiblityNorm
			stmt = db.connection.createStatement();
			sql =
					"INSERT INTO maxcardinalitynorm (role1id,role2id,max,normid)VALUES("
							+ role1ID + "," + role2ID + "," + max + ","
							+ normID + ")";
			sql = sql.toLowerCase();
			stmt.executeUpdate(sql);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean CheckIncompatiblityNorm(String role1, String role2)
	{
		try
		{
			Statement stmt = db.connection.createStatement();
			String sql =
					"SELECT * FROM role WHERE roleid='" + role1.toLowerCase()
							+ "'";
			ResultSet rs = stmt.executeQuery(sql.toLowerCase());
			if (!rs.next())
				return false;
			String role1ID = rs.getString("ID");
			
			stmt = db.connection.createStatement();
			sql =
					"SELECT * FROM role WHERE roleid='" + role2.toLowerCase()
							+ "'";
			rs = stmt.executeQuery(sql.toLowerCase());
			if (!rs.next())
				return false;
			String role2ID = rs.getString("ID");
			
			stmt = db.connection.createStatement();
			sql =
					"SELECT * FROM incompatibilitynorm WHERE role2id="
							+ role2ID.toLowerCase() + " AND role1ID="
							+ role1ID.toLowerCase();
			rs = stmt.executeQuery(sql.toLowerCase());
			if (rs.next())
				return false;
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean CheckMaxCardinalityNorm(String role1, String role2)
	{
		try
		{
			Statement stmt = db.connection.createStatement();
			String sql =
					"SELECT * FROM role WHERE roleid='" + role1.toLowerCase()
							+ "'";
			ResultSet rs = stmt.executeQuery(sql.toLowerCase());
			if (!rs.next())
				return false;
			String Role1ID = rs.getString("ID");
			stmt = db.connection.createStatement();
			sql =
					"SELECT * FROM role WHERE roleid='" + role2.toLowerCase()
							+ "'";
			rs = stmt.executeQuery(sql.toLowerCase());
			if (!rs.next())
				return false;
			String Role2ID = rs.getString("ID");
			stmt = db.connection.createStatement();
			sql =
					"SELECT * FROM maxcardinalitynorm WHERE role2id="
							+ Role2ID.toLowerCase() + " AND role1ID="
							+ Role1ID.toLowerCase();
			rs = stmt.executeQuery(sql.toLowerCase());
			if (rs.next())
				return false;
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return true;
		}
	}
	public List<String[]> GetMaxCardinalityNorms(String roleID)
	{
		List<String[]> list = new ArrayList<String[]>();
		try
		{
			Statement stmt = db.connection.createStatement();
			String sql =
					"SELECT * FROM role WHERE roleid='" + roleID.toLowerCase()
							+ "'";
			ResultSet rs = stmt.executeQuery(sql.toLowerCase());
			if (!rs.next())
				return null;
			String ID = rs.getString("id");
			
			stmt = db.connection.createStatement();
			sql =
					"SELECT * FROM maxcardinalitynorm WHERE role2id="
							+ ID.toLowerCase() + "";
			rs = stmt.executeQuery(sql.toLowerCase());
			while (rs.next())
			{
				String[] norm =
						{ rs.getString("role2id"), rs.getString("max") };
				list.add(norm);
			}
			return list;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return list;
		}
	}
	
	public int GetRoleCardinality(String roleID)
	{
		int card = 0;
		try
		{
			Statement stmt = db.connection.createStatement();
			String sql =
					"SELECT * FROM entityplaylist WHERE role=" + roleID + "";
			ResultSet rs = stmt.executeQuery(sql.toLowerCase());
			while (rs.next())
			{
				card++;
			}
			return card;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return card;
		}
	}
	
	public List<Integer> GetIncompatibleRolesID(String roleID)
	{
		List<Integer> list = new ArrayList<Integer>();
		try
		{
			Statement stmt = db.connection.createStatement();
			String sql =
					"SELECT * FROM role WHERE roleid='" + roleID.toLowerCase()
							+ "'";
			ResultSet rs = stmt.executeQuery(sql.toLowerCase());
			if (!rs.next())
				return list;
			String ID = rs.getString("id");
			stmt = db.connection.createStatement();
			sql = "SELECT * FROM incompatibilitynorm WHERE role2id=" + ID;
			rs = stmt.executeQuery(sql.toLowerCase());
			while (rs.next())
			{
				list.add(rs.getInt("role1id"));
			}
			return list;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return list;
		}
	}
	public List<String> GetAgentRoles(String agentName)
	{
		List<String> list = new ArrayList<String>();
		try
		{
			Statement stmt = db.connection.createStatement();
			ResultSet rs =
					stmt.executeQuery("SELECT * FROM entity WHERE entityid='"
							+ agentName.toLowerCase() + "'");
			if (!rs.next())
				return list;
			String ID = rs.getString("id");
			
			stmt = db.connection.createStatement();
			rs =
					stmt
							.executeQuery("SELECT * FROM (entityplaylist E JOIN role R ON R.id=E.role) WHERE entity="
									+ ID);
			while (rs.next())
			{
				list.add(rs.getString("role"));
			}
			return list;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return list;
		}
	}
	
	public List<Integer> GetAgentRolesID(String agentID)
	{
		List<Integer> list = new ArrayList<Integer>();
		try
		{
			Statement stmt = db.connection.createStatement();
			ResultSet rs =
					stmt.executeQuery("SELECT * FROM entity WHERE entityid='"
							+ agentID.toLowerCase() + "'");
			if (!rs.next())
				return list;
			String ID = rs.getString("id");
			
			stmt = db.connection.createStatement();
			rs =
					stmt
							.executeQuery("SELECT * FROM entityplaylist WHERE entity="
									+ ID);
			while (rs.next())
			{
				list.add(rs.getInt("role"));
			}
			return list;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return list;
		}
	}
	public boolean CheckAgentPlaysRoleInUnit(String unitID, String agentID)
	{
		boolean exists = false;
		try
		{
			Statement stmt = db.connection.createStatement();
			String sql =
					"SELECT * FROM (((EntityPlayList EPR  JOIN Role R ON R.ID=EPR.ROLE) JOIN Entity E ON E.ID=EPR.ENTITY) JOIN Unit U ON U.ID=EPR.UNIT) WHERE  U.UNitID='"
							+ unitID.toLowerCase()
							+ "' AND E.EntityID='"
							+ agentID.toLowerCase() + "'";
			ResultSet r = stmt.executeQuery(sql.toLowerCase());
			if (r.next())
			{
				exists = true;
			}
		}
		catch (Exception e)
		{
		}
		
		return exists;
	}*/
	
}
