package persistence;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
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
	
	public String acquireRole(String unitName, String roleName, String agentName) throws SQLException{
		Statement st;		
		st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unitName+"'");
		if(res.next()){
			int idUnit = res.getInt("idunitList");
			Statement st2 = db.connection.createStatement();
			ResultSet res2 = st2.executeQuery("SELECT idroleListt FROM roleList WHERE roleName ='"+roleName+"' AND idunitList = "+idUnit);
			if(res2.next()){
				int idRole = res.getInt("idroleListt");
				Statement st3 = db.connection.createStatement();
				int res3 = st3.executeUpdate("INSERT INTO agentPlayList (agentName, idroleListt) VALUES ('"+agentName+"', "+idRole+")");
				if(res3 != 0){
					db.connection.commit();
					return "<"+roleName+" + \"acquired\">";
				}
			}
			return "Error: role "+roleName+" not found in unit "+unitName;
		}
		return "Error: unit "+unitName+" not found in database";		
	}
	
	public String allocateRole(String roleName, String unitName, String targetAgentName, String agentName){
		// TODO no té molt de trellat la especificació, fa el mateix q l'anterior funció
		return "";
	}
	
	public boolean checkAgent(String agentName) throws SQLException{
		boolean exists = false;
		
		Statement stmt = db.connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM agentPlayList WHERE agentName='"+ agentName + "'");
		while (rs.next())
		{
			exists = true;
		}
		
		return exists;
	}
	
	public boolean checkAgentInUnit(String agentName, String unit) throws SQLException{
		boolean exists = false;
		
		Statement stmt = db.connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+unit+"'");
		if(rs.next()){
			int unitId = rs.getInt("idunitList");
			ResultSet rs2 = stmt.executeQuery("SELECT * FROM roleList WHERE agentName ='"+ agentName +"' AND idunitList ="+unitId);
			if (rs2.next())
			{
				exists = true;
			}
		}
		
		return exists;
	}
	
	public boolean checkAgentPlaysRole(String agentName, String role, String unit) throws SQLException{
		boolean exists = false;		
		Statement stmt = db.connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+unit+"'");
		if(rs.next()){
			int unitId = rs.getInt("idunitList");
			ResultSet rs2 = stmt.executeQuery("SELECT * FROM roleList WHERE agentName ='"+ agentName +"' AND idunitList ="+unitId+" AND roleName ='"+role+"'");
			if (rs2.next())
			{
				exists = true;
			}
		}		
		return exists;
	}
	
	public boolean checkNoCreatorAgentsInUnit(String unit) throws SQLException{		
		Statement stmt = db.connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT idposition FROM position WHERE position ='creator'");
		if(rs.next()){
			int positionId = rs.getInt("idposition");
			ResultSet rs2 = stmt.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+unit+"'");
			if (rs2.next())
			{
				int unitId = rs2.getInt("idunitList");
				ResultSet rs3 = stmt.executeQuery("SELECT * FROM roleList WHERE idunitlist ="+unitId+" AND idposition !="+positionId);
				if(rs3.next())
					return true;
			}
		}		
		return false;
	}
	
	public boolean checkPlayedRoleInUnit(String role, String unit) throws SQLException{
		
		Statement stmt = db.connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+unit+"'");
		if(rs.next()){
			int unitId = rs.getInt("idunitList");
			ResultSet rs2 = stmt.executeQuery("SELECT * FROM roleList WHERE idunitList ="+unitId+" AND roleName='"+role+"'");
			if (rs2.next())
			{
				return true;
			}
		}		
		return false;
	}
	
	public boolean checkTargetRoleNorm(String role, String unit){
		// TODO on estan les normes
		return true;
	}
	
	public boolean checkPosition(String agent, String position) throws SQLException{
		Statement st;		
		st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idroleListt FROM agentPlayList WHERE agentName ='"+ agent +"'");
		while(res.next()){
			int idRole = res.getInt("idroleListt");
			Statement st2 = db.connection.createStatement();
			ResultSet res2 = st2.executeQuery("SELECT idposition FROM roleList WHERE idroleListt ="+idRole);
			if(res2.next()){
				int idPosition = res2.getInt("idposition");
				Statement st3 = db.connection.createStatement();
				ResultSet res3 = st3.executeQuery("SELECT * FROM position WHERE idposition ="+idPosition);
				if(res3.next() && res3.getString("position").equalsIgnoreCase(position)){
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean checkPositionInUnit(String agent, String position, String unit) throws SQLException{
		Statement st;
		int idUnit = -1;
		st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idroleListt FROM agentPlayList WHERE agentName ='"+ agent +"'");
		Statement st4 = db.connection.createStatement();
		ResultSet res4 = st4.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unit +"'");
		if(res4.next())
			idUnit = res4.getInt("idunitList");
		while(res.next() && idUnit > -1){
			int idRole = res.getInt("idroleListt");
			Statement st2 = db.connection.createStatement();
			ResultSet res2 = st2.executeQuery("SELECT idposition FROM roleList WHERE idroleListt ="+idRole+" AND idunitList ="+idUnit);
			if(res2.next()){
				int idPosition = res2.getInt("idposition");
				Statement st3 = db.connection.createStatement();
				ResultSet res3 = st3.executeQuery("SELECT * FROM position WHERE idposition ="+idPosition);
				if(res3.next() && res3.getString("position").equalsIgnoreCase(position)){
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean checkRole(String role, String unit) throws SQLException{
		Statement st;
		st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unit +"'");
		if(res.next()){
			int idUnit = res.getInt("idunitList");
			Statement st2 = db.connection.createStatement();
			ResultSet res2 = st2.executeQuery("SELECT * FROM roleList WHERE idunitList ="+idUnit+" AND roleName ='"+role+"'");
			if(res2.next()){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkSubUnits(String unit) throws SQLException{
		Statement st;
		st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unit +"'");
		if(res.next()){
			int idUnit = res.getInt("idunitList");
			Statement st2 = db.connection.createStatement();
			ResultSet res2 = st2.executeQuery("SELECT * FROM unitHierarchy WHERE idParentUnit ="+idUnit);
			if(res2.next()){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkUnit(String unit) throws SQLException{
		Statement st;
		st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT * FROM unitList WHERE unitName ='"+ unit +"'");
		if(res.next()){
			return true;
		}
		return false;
	}
	
	public boolean checkVirtualUnit(String unit) throws SQLException{
		Statement st;
		st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idunitType FROM unitType WHERE unitTypeName ='virtual'");
		if(res.next()){
			int idUnitType = res.getInt("idunitType");
			Statement st2 = db.connection.createStatement();
			ResultSet res2 = st2.executeQuery("SELECT * FROM unitList WHERE idunitType ="+idUnitType+" AND unitName ='"+unit+"'");
			if(res2.next()){
				return true;
			}
		}
		return false;
	}
	
	public String createRole(String roleName, String unitName, String accessibility, String visibility, String position) throws SQLException{
		Statement st;		
		st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unitName+"'");
		if(res.next()){
			int idunit = res.getInt("idunitList");
			Statement st4 = db.connection.createStatement();
			ResultSet res4 = st4.executeQuery("SELECT idposition FROM position WHERE position ='"+ position+"'");
			if(res4.next()){
				int idposition = res.getInt("idposition");
				Statement st5 = db.connection.createStatement();
				ResultSet res5 = st5.executeQuery("SELECT idaccesibility FROM accesibility WHERE accesiblity ='"+ accessibility+"'");
				if(res5.next()){
					int idaccesibility = res.getInt("idaccesibility");
					Statement st6 = db.connection.createStatement();
					ResultSet res6 = st6.executeQuery("SELECT idvisibility FROM visibility WHERE visibility ='"+ visibility+"'");
					if(res6.next()){
						int idvisibility = res.getInt("idvisibility");
						Statement st3 = db.connection.createStatement();
						int res3 = st3.executeUpdate("INSERT INTO roleList (roleName, idunitList, idposition, idaccesibility, idvisibility) VALUES ('"+roleName+"', "+idunit+","+idposition+","+idaccesibility+","+idvisibility+")");
						if(res3 != 0){
							db.connection.commit();
							return "<"+roleName+" + \"created\">";
						}
					}
					return "Error: visibility "+position+" not found in database";
				}
				return "Error: accesibility "+position+" not found in database";
			}			
			return "Error: position "+position+" not found in database";
		}
		return "Error: unit "+unitName+" not found in database";	
	}
	
	public String createUnit(String unitName, String unitType, String parentUnitName, String agentName, String creatorAgentName) throws SQLException{
		Statement st;		
		st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idunitType FROM unitType WHERE unitTypeName ='"+ unitType+"'");
		if(res.next()){
			int idunitType = res.getInt("idunitType");
			Statement st4 = db.connection.createStatement();
			int res4 = st4.executeUpdate("INSERT INTO unitList (unitName, idunitType) VALUES ('"+unitName+"', "+idunitType+")");
			if(res4 != 0){
				Statement st5 = db.connection.createStatement();
				ResultSet res5 = st5.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ parentUnitName+"'");
				if(res5.next()){
					int idunitParent = res.getInt("idunitList");
					Statement st6 = db.connection.createStatement();
					int res6 = st6.executeUpdate("INSERT INTO unitHierarchy (idParentUnit, idChildUnit) VALUES ("+idunitParent+", "+res4+")");
					if(res6 != 0){
						Statement st7 = db.connection.createStatement();
						ResultSet res7 = st7.executeQuery("SELECT idposition FROM position WHERE position ='creator'");
						if(res7.next()){
							int idposition = res7.getInt("idposition");
							Statement st8 = db.connection.createStatement();
							ResultSet res8 = st8.executeQuery("SELECT idaccesibility FROM accesibility WHERE accesiblity ='internal'");
							if(res8.next()){
								int idaccesibility = res7.getInt("idaccesibility");
								Statement st9 = db.connection.createStatement();
								ResultSet res9 = st9.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='private'");
								if(res9.next()){
									int idVisibility = res9.getInt("idVisibility");
									Statement st10 = db.connection.createStatement();
									int res10 = st10.executeUpdate("INSERT INTO roleList (roleName, idunitList, idposition, idaccesibility, idvisibility) VALUES ('CreatorName', "+res4+", "+idposition+","+idaccesibility+","+idVisibility+")");
									if(res10 != 0){
										Statement st11 = db.connection.createStatement();
										int res11 = st11.executeUpdate("INSERT INTO agentPlayList (agentName, idroleListt) VALUES ('"+agentName+"', "+res10+")");
										if(res11 != 0){
											db.connection.commit();
											return "<"+unitName+" + \"created\">";
										}
										return "Error: inserting new play list";
									}
									return "Error: inserting new role";
								}
								return "Error: visibility private not found in database";
							}
							return "Error: accesibility internal not found in database";
						}
						return "Error: position creator not found in database";
					}
					return "Error: inserting hierarchy";
				}
				return "Error: parent unit "+parentUnitName+" not found in database";
			}			
			return "Error: inserting new unit";
		}
		return "Error: unitType "+unitType+" not found in database";	
	}
	
	public String deallocateRole(String roleName, String unitName, String targetAgentName, String agentName) throws SQLException{
		Statement st;		
		st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unitName+"'");
		if(res.next()){
			int idunitList = res.getInt("idunitList");
			Statement st2 = db.connection.createStatement();
			ResultSet res2 = st2.executeQuery("SELECT idroleListt FROM roleList WHERE roleName ='"+ roleName+"' AND idunitList ="+idunitList);
			if(res2.next()){
				int idroleListt = res2.getInt("idroleListt");
				Statement st3 = db.connection.createStatement();
				int res3 = st3.executeUpdate("DELETE FROM agentPlayList WHERE agentName = '"+targetAgentName+"' AND idroleListt = "+idroleListt);
				if(res3 != 0){
					db.connection.commit();
					return "<"+roleName+" + \"deallocated\">";
				}
				return "Error: mysql error "+res3;
			}
			return "Error: rolename "+roleName+" not found in database";
		}
		return "Error: unit "+unitName+" not found in database";
	}
	
	public String deleteRole(String roleName, String unitName, String agentName) throws SQLException{
		Statement st;		
		st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unitName+"'");
		if(res.next()){
			int idunitList = res.getInt("idunitList");
			Statement st2 = db.connection.createStatement();
			int res2 = st2.executeUpdate("DELETE FROM roleList WHERE roleName ='"+roleName+"' AND idunitList ="+idunitList);
			if(res2 != 0){				
				db.connection.commit();
				return "<"+roleName+" + \"deleted\">";
			}
			return "Error: mysql error "+res2;
		}
		return "Error: unit "+unitName+" not found in database";
	}
	
	public String deleteUnit(String unitName, String agentName) throws SQLException{
		Statement st;		
		st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unitName+"'");
		if(res.next()){
			int idunitList = res.getInt("idunitList");
			Statement st2 = db.connection.createStatement();
			ResultSet res2 = st2.executeQuery("SELECT idposition FROM position WHERE position ='creator'");
			if(res2.next()){
				int idposition = res.getInt("idposition");
				Statement st3 = db.connection.createStatement();
				ResultSet res3 = st3.executeQuery("SELECT idroleListt FROM roleList WHERE idposition ="+idposition+" AND idunit ="+idunitList);
				while(res3.next()){
					int idroleListt = res.getInt("idroleListt");
					Statement st4 = db.connection.createStatement();
					int res4 = st4.executeUpdate("DELETE FROM agentPlayList WHERE idroleListt ="+idroleListt);
					if(res4 == 0)
						return "Error: mysql error "+res4;
				}
				Statement st5 = db.connection.createStatement();
				int res5 = st5.executeUpdate("DELETE FROM roleList WHERE idunitList ="+idunitList);
				if(res5 != 0){
					Statement st6 = db.connection.createStatement();
					int res6 = st6.executeUpdate("DELETE FROM unitList WHERE idunitList ="+idunitList);
					if(res6 != 0){
						db.connection.commit();
						return "<"+unitName+" + \"deleted\">";
					}
					return "Error: mysql error "+res6;
				}
				return "Error: mysql error "+res5;
			}
			return "Error: position creator not found in database";
		}
		return "Error: unit "+unitName+" not found in database";
	}
	
	public String jointUnit(String unitName, String parentName, String agentName) throws SQLException{
		// TODO per a que vols l'agentName?
		Statement st;		
		st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unitName+"'");
		if(res.next()){
			int idunitList = res.getInt("idunitList");
			Statement st2 = db.connection.createStatement();
			ResultSet res2 = st2.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+parentName+"'");
			if(res2.next()){
				int idParentUnit = res.getInt("idunitList");
				Statement st3 = db.connection.createStatement();
				int res3 = st3.executeUpdate("DELETE FROM unitHierarchy WHERE idChildUnit ="+idunitList);
				if(res3 != 0){
					Statement st4 = db.connection.createStatement();
					int res4 = st4.executeUpdate("INSERT INTO unitHierarchy (idParentUnit, idChildUnit) VALUES ("+idParentUnit+","+idunitList+")");
					if(res4 != 0){
						db.connection.commit();
						return "<"+unitName+" + \"jointed to +\" "+parentName+">";
					}
				}
				return "Error: mysql error "+res3;
			}
			return "Error: unit "+parentName+" not found in database";
		}
		return "Error: unit "+unitName+" not found in database";
	}
	
	public String leaveRole(String unitName, String roleName, String agentName) throws SQLException{
		Statement st;		
		st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unitName+"'");
		if(res.next()){
			int idunitList = res.getInt("idunitList");
			Statement st2 = db.connection.createStatement();
			ResultSet res2 = st2.executeQuery("SELECT idroleListt FROM roleList WHERE idunitList ="+idunitList+" AND roleName ='"+roleName+"'");
			if(res2.next()){
				int idroleListt = res.getInt("idroleListt");
				Statement st3 = db.connection.createStatement();
				int res3 = st3.executeUpdate("DELETE FROM agentPlayList WHERE idroleListt ="+idroleListt);
				if(res3 != 0){					
					db.connection.commit();
					return "<"+roleName+" + \"left\">";			
				}
				return "Error: mysql error "+res3;
			}
			return "Error: unit "+roleName+" not found in unit "+unitName;
		}
		return "Error: unit "+unitName+" not found in database";
	}
	
	public String getUnitType(String unitName) throws SQLException{
		Statement st;
		st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idunitType FROM unitList WHERE unitName ='"+ unitName+"'");
		if(res.next()){
			int idunitType = res.getInt("idunitType");
			Statement st2 = db.connection.createStatement();
			ResultSet res2 = st2.executeQuery("SELECT unitTypeName FROM unitType WHERE idunitType ="+idunitType);
			if(res2.next()){
				return res2.getString("unitTypeName");
			}
			return "Error: idunitType "+idunitType+" not found in database";
		}
		return "Error: unit "+unitName+" not found in database";
	}
	
	public String getAgentsInUnit(String unitName) throws SQLException{
		// TODO quin format de cadena volen?
		return "";
	}
	
	public String getParentsUnit(String unitName) throws SQLException{
		Statement st;		
		st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unitName+"'");
		if(res.next()){
			int idunitList = res.getInt("idunitList");
			Statement st2 = db.connection.createStatement();
			ResultSet res2 = st2.executeQuery("SELECT idParentUnit FROM unitHierarchy WHERE idChildUnit ="+idunitList);
			if(res2.next()){
				int idParentUnit = res.getInt("idParentUnit");
				Statement st3 = db.connection.createStatement();
				ResultSet res3 = st3.executeQuery("SELECT unitName FROM unitList WHERE idunitList ="+idParentUnit);
				if(res3.next()){
					return res3.getString("unitName");
				}
			}
			return "virtual";
		}
		return "Error: unit "+unitName+" not found in database";
	}
	
	public String getInformAgentRole(String requestedAgentName, String agentName) throws SQLException{
		return "";
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
