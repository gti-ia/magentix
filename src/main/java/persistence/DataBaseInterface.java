package persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
		String result = "";
		st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unitName+"'");
		if(res.next()){
			int idunitList = res.getInt("idunitList");
			Statement st2 = db.connection.createStatement();
			ResultSet res2 = st2.executeQuery("SELECT * FROM roleList WHERE idunitList ="+idunitList);
			while(res2.next()){
				result = "<";
				String roleName = res2.getString("roleName");
				int idposition = res2.getInt("idposition");
				int idroleListt = res2.getInt("idroleListt");
				Statement st3 = db.connection.createStatement();
				ResultSet res3 = st3.executeQuery("SELECT agentName FROM agentPlayList WHERE idroleListt ="+idroleListt);
				if(res3.next())
					result += res3.getString("agentName");
				result += ","+roleName+",";
				Statement st4 = db.connection.createStatement();
				ResultSet res4 = st4.executeQuery("SELECT position FROM position WHERE idposition ="+idposition);
				if(res4.next())
					result += res4.getString("position");
				result += ">";

			}
			return result;
		}
		return "Error: unit "+unitName+" not found in database";
	}

	public String getAgentsInUnit(String unitName) throws SQLException{
		// TODO quin format de cadena volen?
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
		String result = "";
		Statement st;
		Statement st2 = db.connection.createStatement();
		int idVisibility;
		ResultSet res2 = st2.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='public'");
		if(res2.next())
			idVisibility = res2.getInt("idVisibility");
		else
			return "Error: visibility public not found in database";

		st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idroleListt FROM agentPlayList WHERE agentName ='"+ requestedAgentName+"'");
		while(res.next()){
			int idroleListt = res.getInt("idroleListt");
			Statement st3 = db.connection.createStatement();
			ResultSet res3 = st3.executeQuery("SELECT idunitList, roleName FROM roleList WHERE idroleListt ="+idroleListt+" AND idvisibility ="+idVisibility);
			if(res3.next()){
				result += "<";
				int idunitList = res3.getInt("idunitList");
				String roleName = res3.getString("roleName");		
				Statement st4 = db.connection.createStatement();
				ResultSet res4 = st4.executeQuery("SELECT unitName FROM unitList WHERE idunitList ="+idunitList);
				if(res4.next()){
					String unitName = res4.getString("unitName");
					result += roleName+","+unitName;
				}
				result += ">";
			}
		}

		ArrayList<Integer> idunits1 = new ArrayList<Integer>();
		ArrayList<Integer> idunits2 = new ArrayList<Integer>();
		Statement st6 = db.connection.createStatement();
		ResultSet res6 = st6.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='private'");
		if(res6.next())
			idVisibility = res6.getInt("idVisibility");
		else
			return "Error: visibility private not found in database";

		Statement st7 = db.connection.createStatement();
		ResultSet res7 = st7.executeQuery("SELECT idroleListt FROM agentPlayList WHERE agentName ='"+ requestedAgentName+"'");
		while(res7.next()){
			int idroleListt = res7.getInt("idroleListt");
			Statement st8 = db.connection.createStatement();
			ResultSet res8 = st8.executeQuery("SELECT idunitList FROM roleList WHERE idroleListt ="+idroleListt+" AND idvisibility ="+idVisibility);
			if(res8.next()){
				idunits1.add(res8.getInt("idunitList"));
			}
		}

		Statement st9 = db.connection.createStatement();
		ResultSet res9 = st9.executeQuery("SELECT idroleListt FROM agentPlayList WHERE agentName ='"+ agentName+"'");
		while(res9.next()){
			int idroleListt = res9.getInt("idroleListt");
			Statement st10 = db.connection.createStatement();
			ResultSet res10 = st10.executeQuery("SELECT idunitList FROM roleList WHERE idroleListt ="+idroleListt+" AND idvisibility ="+idVisibility);
			if(res10.next()){
				idunits2.add(res10.getInt("idunitList"));
			}
		}

		for(int unitid : idunits1){
			if(idunits2.contains(unitid)){
				Statement st11 = db.connection.createStatement();
				ResultSet res11 = st11.executeQuery("SELECT idroleListt FROM agentPlayList WHERE agentName ='"+ requestedAgentName+"'");
				while(res11.next()){
					int idroleListt = res11.getInt("idroleListt");
					Statement st12 = db.connection.createStatement();
					ResultSet res12 = st12.executeQuery("SELECT roleName FROM roleList WHERE idroleListt ="+idroleListt+" AND idvisibility ="+idVisibility+" AND idunitList="+unitid);
					if(res12.next()){
						Statement st13 = db.connection.createStatement();
						ResultSet res13 = st13.executeQuery("SELECT unitName FROM unitList WHERE idunitList ="+unitid);
						if(res13.next()){
							result+= "<"+res12.getString("roleName")+","+res13.getString("unitName")+">";
						}
					}
				}
			}
		}
		return result;
	}

	public String getAgentsRolesInUnit(String unitName, String agentName) throws SQLException{
		String result = "";		
		int idPublicVisibility;
		int idPrivateVisbility;
		boolean playsRole = false;
		int idunitList;

		Statement st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='public'");
		if(res.next())
			idPublicVisibility = res.getInt("idVisibility");
		else
			return "Error: visibility public not found in database";

		Statement st3 = db.connection.createStatement();
		ResultSet res3 = st3.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='private'");
		if(res3.next())
			idPrivateVisbility = res.getInt("idVisibility");
		else
			return "Error: visibility private not found in database";

		Statement st2 = db.connection.createStatement();
		ResultSet res2 = st2.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unitName+"'");
		if(res2.next())
			idunitList = res2.getInt("idunitList");
		else
			return "Error : unit "+unitName+" not found in database";


		Statement st10 = db.connection.createStatement();
		ResultSet res10 = st10.executeQuery("SELECT idroleListt FROM agentPlayList WHERE agentName ='"+agentName+"'");
		while(res10.next()){
			int idroleListt = res10.getInt("idroleListt");
			Statement st11 = db.connection.createStatement();
			ResultSet res11 = st11.executeQuery("SELECT * FROM roleList WHERE idroleListt ="+idroleListt+" AND idunitList="+idunitList);
			if(res11.next()){
				playsRole = true;
				break;
			}
		}

		Statement st12 = db.connection.createStatement();
		ResultSet res12;
		if(playsRole)
			res12 = st12.executeQuery("SELECT idroleListt, roleName FROM roleList WHERE idunitList ="+ idunitList+" AND (idVisibility ="+idPrivateVisbility+" OR idVisibility ="+idPublicVisibility+")");
		else
			res12 = st12.executeQuery("SELECT idroleListt, roleName FROM roleList WHERE idunitList ="+ idunitList+" AND idVisibility ="+idPublicVisibility);
		while(res12.next()){
			String roleName = res12.getString("roleName");
			int idroleListt = res12.getInt("idroleListt");
			Statement st13 = db.connection.createStatement();
			ResultSet res13 = st13.executeQuery("SELECT agentName FROM agentPlayList WHERE idroleListt ="+idroleListt);
			while(res13.next()){
				result += "<"+res13.getString("agentName")+","+roleName+">";
			}
		}
		return result;
	}

	public String getAgentsPlayingRoleInUnit(String unitName, String roleName, String agentName) throws SQLException{
		String result = "";		
		int idPublicVisibility;
		int idPrivateVisbility;
		boolean playsRole = false;
		int idunitList;

		Statement st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='public'");
		if(res.next())
			idPublicVisibility = res.getInt("idVisibility");
		else
			return "Error: visibility public not found in database";

		Statement st3 = db.connection.createStatement();
		ResultSet res3 = st3.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='private'");
		if(res3.next())
			idPrivateVisbility = res.getInt("idVisibility");
		else
			return "Error: visibility private not found in database";

		Statement st2 = db.connection.createStatement();
		ResultSet res2 = st2.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unitName+"'");
		if(res2.next())
			idunitList = res2.getInt("idunitList");
		else
			return "Error : unit "+unitName+" not found in database";


		Statement st10 = db.connection.createStatement();
		ResultSet res10 = st10.executeQuery("SELECT idroleListt FROM agentPlayList WHERE agentName ='"+agentName+"'");
		while(res10.next()){
			int idroleListt = res10.getInt("idroleListt");
			Statement st11 = db.connection.createStatement();
			ResultSet res11 = st11.executeQuery("SELECT * FROM roleList WHERE idroleListt ="+idroleListt+" AND idunitList="+idunitList);
			if(res11.next()){
				playsRole = true;
				break;
			}
		}

		Statement st12 = db.connection.createStatement();
		ResultSet res12;
		if(playsRole)
			res12 = st12.executeQuery("SELECT idroleListt FROM roleList WHERE idunitList ="+ idunitList+" AND roleName ='"+roleName+"' AND (idVisibility ="+idPrivateVisbility+" OR idVisibility ="+idPublicVisibility+")");
		else
			res12 = st12.executeQuery("SELECT idroleListt FROM roleList WHERE idunitList ="+ idunitList+" AND roleName ='"+roleName+"' AND idVisibility ="+idPublicVisibility);
		while(res12.next()){
			int idroleListt = res12.getInt("idroleListt");
			Statement st13 = db.connection.createStatement();
			ResultSet res13 = st13.executeQuery("SELECT agentName FROM agentPlayList WHERE idroleListt ="+idroleListt);
			while(res13.next()){
				result += "<"+res13.getString("agentName")+">";
			}
		}
		return result;
	}

	public String getAgentsPlayingPositionInUnit(String unitName,String positionValue, String agentName) throws SQLException{
		// TODO per a que vols AgentName com a parametre?
		String result = "";		
		int idposition;
		int idunit;

		Statement st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idposition FROM position WHERE position ='"+positionValue+"'");
		if(res.next())
			idposition = res.getInt("idposition");
		else
			return "Error: position "+positionValue+" not found in database";

		Statement st2 = db.connection.createStatement();
		ResultSet res2 = st2.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+unitName+"'");
		if(res2.next())
			idunit = res.getInt("idunitList");
		else
			return "Error: unit "+unitName+" not found in database";

		Statement st3 = db.connection.createStatement();
		ResultSet res3 = st3.executeQuery("SELECT idroleListt, roleName FROM roleList WHERE idunitList ="+idunit+" AND idposition ="+idposition);
		while(res3.next()){
			int idroleListt = res3.getInt("idroleListt");
			String roleName = res3.getString("roleName");
			Statement st4 = db.connection.createStatement();
			ResultSet res4 = st4.executeQuery("SELECT agentName FROM agentPlayList WHERE idroleListt ="+idroleListt);
			while(res4.next())
				result += "<"+res4.getString("agentName")+","+roleName+">";
		}
		return result;
	}

	public String getAgentsPlayingRolePositionInUnit(String unitName, String roleName, String positionValue, String agentName) throws SQLException{
		// TODO deurien tornarse els agents q juguen el role roleName, amb la posicio positionValue en la unitat unitName?
		String result = "";		
		int idPublicVisibility;
		int idPrivateVisbility;
		int idposition;
		int idroleListt;
		boolean playsRole = false;
		int idunitList;

		Statement st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='public'");
		if(res.next())
			idPublicVisibility = res.getInt("idVisibility");
		else
			return "Error: visibility public not found in database";

		Statement st3 = db.connection.createStatement();
		ResultSet res3 = st3.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='private'");
		if(res3.next())
			idPrivateVisbility = res.getInt("idVisibility");
		else
			return "Error: visibility private not found in database";

		Statement st2 = db.connection.createStatement();
		ResultSet res2 = st2.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unitName+"'");
		if(res2.next())
			idunitList = res2.getInt("idunitList");
		else
			return "Error : unit "+unitName+" not found in database";

		Statement st4 = db.connection.createStatement();
		ResultSet res4 = st4.executeQuery("SELECT idposition FROM position WHERE position ='"+ positionValue+"'");
		if(res4.next())
			idposition = res4.getInt("idposition");
		else
			return "Error : position "+positionValue+" not found in database";

		Statement st6 = db.connection.createStatement();
		ResultSet res6 = st6.executeQuery("SELECT idroleListt FROM agentPlayList WHERE agentName ='"+agentName+"'");
		while(res6.next()){
			int idroleListt2 = res6.getInt("idroleListt");
			Statement st7 = db.connection.createStatement();
			ResultSet res7 = st7.executeQuery("SELECT * FROM roleList WHERE idroleListt ="+idroleListt2+" AND idunitList="+idunitList);
			if(res7.next()){
				playsRole = true;
				break;
			}
		}

		Statement st5 = db.connection.createStatement();
		ResultSet res5;
		if(playsRole)
			res5 = st5.executeQuery("SELECT idroleListt FROM roleList WHERE roleName ='"+ roleName+"' AND idposition ="+idposition+" AND (idVisibility ="+idPrivateVisbility+" OR idVisiblity ="+idPublicVisibility+")");
		else
			res5 = st5.executeQuery("SELECT idroleListt FROM roleList WHERE roleName ='"+ roleName+"' AND idposition ="+idposition+" AND idVisiblity ="+idPublicVisibility);
		if(res5.next())
			idroleListt = res5.getInt("idroleListt");
		else
			return "Error : role "+roleName+" not found in database";

		Statement st10 = db.connection.createStatement();
		ResultSet res10 = st10.executeQuery("SELECT agentName FROM agentPlayList WHERE idroleListt ="+idroleListt);
		while(res10.next()){
			result += "<"+res10.getString("agentName")+">";
		}		
		return result;
	}
	
	public String getQuantityAgentsRolesInUnit(String unitName, String agentName)  throws SQLException{
		// TODO retorne un String? Lo normal no seria un int?
		int idPublicVisibility;
		int idroleListt;
		boolean playsRole = false;
		int idunitList;

		Statement st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='public'");
		if(res.next())
			idPublicVisibility = res.getInt("idVisibility");
		else
			return "Error: visibility public not found in database";

		Statement st2 = db.connection.createStatement();
		ResultSet res2 = st2.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unitName+"'");
		if(res2.next())
			idunitList = res2.getInt("idunitList");
		else
			return "Error : unit "+unitName+" not found in database";

		Statement st6 = db.connection.createStatement();
		ResultSet res6 = st6.executeQuery("SELECT idroleListt FROM agentPlayList WHERE agentName ='"+agentName+"'");
		while(res6.next()){
			int idroleListt2 = res6.getInt("idroleListt");
			Statement st7 = db.connection.createStatement();
			ResultSet res7 = st7.executeQuery("SELECT * FROM roleList WHERE idroleListt ="+idroleListt2+" AND idunitList="+idunitList);
			if(res7.next()){
				playsRole = true;
				break;
			}
		}

		Set<String> agentNames = new HashSet<String>();
		Statement st5 = db.connection.createStatement();
		ResultSet res5;
		if(playsRole)
			res5 = st5.executeQuery("SELECT idroleListt FROM roleList WHERE idunitList="+idunitList);
		else
			res5 = st5.executeQuery("SELECT idroleListt FROM roleList WHERE idunitList="+idunitList+" AND idVisiblity ="+idPublicVisibility);
		while(res5.next()){
			idroleListt = res5.getInt("idroleListt");	
			Statement st10 = db.connection.createStatement();
			ResultSet res10 = st10.executeQuery("SELECT agentName FROM agentPlayList WHERE idroleListt ="+idroleListt);
			while(res10.next()){
				agentNames.add(res10.getString("agentName"));
			}
		}
		return String.valueOf(agentNames.size());
	}
	
	public String getQuantityAgentsPlayingRoleInUnit(String unitName, String roleName, String agentName) throws SQLException{
		// TODO retorne un String? Lo normal no seria un int?
		int cont = 0;
		int idPublicVisibility;
		int idPrivateVisbility;
		int idroleListt;
		boolean playsRole = false;
		int idunitList;

		Statement st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='public'");
		if(res.next())
			idPublicVisibility = res.getInt("idVisibility");
		else
			return "Error: visibility public not found in database";

		Statement st3 = db.connection.createStatement();
		ResultSet res3 = st3.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='private'");
		if(res3.next())
			idPrivateVisbility = res.getInt("idVisibility");
		else
			return "Error: visibility private not found in database";

		Statement st2 = db.connection.createStatement();
		ResultSet res2 = st2.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unitName+"'");
		if(res2.next())
			idunitList = res2.getInt("idunitList");
		else
			return "Error : unit "+unitName+" not found in database";

		Statement st6 = db.connection.createStatement();
		ResultSet res6 = st6.executeQuery("SELECT idroleListt FROM agentPlayList WHERE agentName ='"+agentName+"'");
		while(res6.next()){
			int idroleListt2 = res6.getInt("idroleListt");
			Statement st7 = db.connection.createStatement();
			ResultSet res7 = st7.executeQuery("SELECT * FROM roleList WHERE idroleListt ="+idroleListt2+" AND idunitList="+idunitList);
			if(res7.next()){
				playsRole = true;
				break;
			}
		}

		Statement st5 = db.connection.createStatement();
		ResultSet res5;
		if(playsRole)
			res5 = st5.executeQuery("SELECT idroleListt FROM roleList WHERE roleName ='"+ roleName+"' AND (idVisibility ="+idPrivateVisbility+" OR idVisiblity ="+idPublicVisibility+")");
		else
			res5 = st5.executeQuery("SELECT idroleListt FROM roleList WHERE roleName ='"+ roleName+"' AND idVisiblity ="+idPublicVisibility);
		if(res5.next())
			idroleListt = res5.getInt("idroleListt");
		else
			return "0";

		Statement st10 = db.connection.createStatement();
		ResultSet res10 = st10.executeQuery("SELECT DISTINCT agentName FROM agentPlayList WHERE idroleListt ="+idroleListt);
		while(res10.next()){
			cont++;
		}
		return String.valueOf(cont);
	}
	
	public String getQuantityAgentsPlayingPositionInUnit(String unitName, String positionValue, String agentName)  throws SQLException{
		// TODO retorne un String? Lo normal no seria un int?
		int idPublicVisibility;
		int idposition;
		int idroleListt;
		boolean playsRole = false;
		int idunitList;

		Statement st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='public'");
		if(res.next())
			idPublicVisibility = res.getInt("idVisibility");
		else
			return "Error: visibility public not found in database";

		Statement st2 = db.connection.createStatement();
		ResultSet res2 = st2.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unitName+"'");
		if(res2.next())
			idunitList = res2.getInt("idunitList");
		else
			return "Error : unit "+unitName+" not found in database";

		Statement st4 = db.connection.createStatement();
		ResultSet res4 = st4.executeQuery("SELECT idposition FROM position WHERE position ='"+ positionValue+"'");
		if(res4.next())
			idposition = res4.getInt("idposition");
		else
			return "Error : position "+positionValue+" not found in database";

		Statement st6 = db.connection.createStatement();
		ResultSet res6 = st6.executeQuery("SELECT idroleListt FROM agentPlayList WHERE agentName ='"+agentName+"'");
		while(res6.next()){
			int idroleListt2 = res6.getInt("idroleListt");
			Statement st7 = db.connection.createStatement();
			ResultSet res7 = st7.executeQuery("SELECT * FROM roleList WHERE idroleListt ="+idroleListt2+" AND idunitList="+idunitList);
			if(res7.next()){
				playsRole = true;
				break;
			}
		}

		Set<String> agentNames = new HashSet<String>();
		Statement st5 = db.connection.createStatement();
		ResultSet res5;
		if(playsRole)
			res5 = st5.executeQuery("SELECT idroleListt FROM roleList WHERE idposition ="+idposition);
		else
			res5 = st5.executeQuery("SELECT idroleListt FROM roleList WHERE idposition ="+idposition+" AND idVisiblity ="+idPublicVisibility);
		while(res5.next()){
			idroleListt = res5.getInt("idroleListt");
			Statement st10 = db.connection.createStatement();
			ResultSet res10 = st10.executeQuery("SELECT agentName FROM agentPlayList WHERE idroleListt ="+idroleListt);
			while(res10.next()){
				agentNames.add(res10.getString("agentName"));
			}
		}
		return String.valueOf(agentNames.size());
	}

	public String getQuantityAgentsPlayingRolePositionInUnit(String unitName, String roleName, String positionValue, String agentName) throws SQLException{
		// TODO retorne un String? Lo normal no seria un int?
		int cont = 0;
		int idPublicVisibility;
		int idPrivateVisbility;
		int idposition;
		int idroleListt;
		boolean playsRole = false;
		int idunitList;

		Statement st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='public'");
		if(res.next())
			idPublicVisibility = res.getInt("idVisibility");
		else
			return "Error: visibility public not found in database";

		Statement st3 = db.connection.createStatement();
		ResultSet res3 = st3.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='private'");
		if(res3.next())
			idPrivateVisbility = res.getInt("idVisibility");
		else
			return "Error: visibility private not found in database";

		Statement st2 = db.connection.createStatement();
		ResultSet res2 = st2.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unitName+"'");
		if(res2.next())
			idunitList = res2.getInt("idunitList");
		else
			return "Error : unit "+unitName+" not found in database";

		Statement st4 = db.connection.createStatement();
		ResultSet res4 = st4.executeQuery("SELECT idposition FROM position WHERE position ='"+ positionValue+"'");
		if(res4.next())
			idposition = res4.getInt("idposition");
		else
			return "Error : position "+positionValue+" not found in database";

		Statement st6 = db.connection.createStatement();
		ResultSet res6 = st6.executeQuery("SELECT idroleListt FROM agentPlayList WHERE agentName ='"+agentName+"'");
		while(res6.next()){
			int idroleListt2 = res6.getInt("idroleListt");
			Statement st7 = db.connection.createStatement();
			ResultSet res7 = st7.executeQuery("SELECT * FROM roleList WHERE idroleListt ="+idroleListt2+" AND idunitList="+idunitList);
			if(res7.next()){
				playsRole = true;
				break;
			}
		}

		Statement st5 = db.connection.createStatement();
		ResultSet res5;
		if(playsRole)
			res5 = st5.executeQuery("SELECT idroleListt FROM roleList WHERE roleName ='"+ roleName+"' AND idposition ="+idposition+" AND (idVisibility ="+idPrivateVisbility+" OR idVisiblity ="+idPublicVisibility+")");
		else
			res5 = st5.executeQuery("SELECT idroleListt FROM roleList WHERE roleName ='"+ roleName+"' AND idposition ="+idposition+" AND idVisiblity ="+idPublicVisibility);
		if(res5.next())
			idroleListt = res5.getInt("idroleListt");
		else
			return "0";

		Statement st10 = db.connection.createStatement();
		ResultSet res10 = st10.executeQuery("SELECT agentName FROM agentPlayList WHERE idroleListt ="+idroleListt);
		while(res10.next()){
			cont++;
		}		
		return String.valueOf(cont);
	}
	
	public String getInformUnit(String unitName) throws SQLException{
		int idunitList;
		Statement st2 = db.connection.createStatement();
		ResultSet res2 = st2.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unitName+"'");
		if(res2.next())
			idunitList = res2.getInt("idunitList");
		else
			return "Error : unit "+unitName+" not found in database";
		
		Statement st3 = db.connection.createStatement();
		ResultSet res3 = st3.executeQuery("SELECT unitTypeName FROM unitType WHERE idunitType ="+ idunitList);
		res3.next();
		
		String result = "<"+res3.getString("unitTypeName")+",";
		Statement st4 = db.connection.createStatement();
		ResultSet res4 = st4.executeQuery("SELECT idParentUnit FROM unitHierarchy WHERE idChildUnit ="+ idunitList);
		if(res4.next()){
			Statement st5 = db.connection.createStatement();
			ResultSet res5 = st5.executeQuery("SELECT unitName FROM unitList WHERE idunitList ="+ res4.getInt("idParentUnit"));
			res5.next();
			result += res5.getString("unitName");
		}
		result += ">";
		return result;
	}
	
	public String getInformUnitRoles(String unitName, String agentName) throws SQLException{
		String result = "";
		int idPublicVisibility;
		int idPrivateVisbility;
		boolean playsRole = false;
		int idunitList;

		Statement st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='public'");
		if(res.next())
			idPublicVisibility = res.getInt("idVisibility");
		else
			return "Error: visibility public not found in database";

		Statement st3 = db.connection.createStatement();
		ResultSet res3 = st3.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='private'");
		if(res3.next())
			idPrivateVisbility = res.getInt("idVisibility");
		else
			return "Error: visibility private not found in database";

		Statement st2 = db.connection.createStatement();
		ResultSet res2 = st2.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unitName+"'");
		if(res2.next())
			idunitList = res2.getInt("idunitList");
		else
			return "Error : unit "+unitName+" not found in database";


		Statement st6 = db.connection.createStatement();
		ResultSet res6 = st6.executeQuery("SELECT idroleListt FROM agentPlayList WHERE agentName ='"+agentName+"'");
		while(res6.next()){
			int idroleListt2 = res6.getInt("idroleListt");
			Statement st7 = db.connection.createStatement();
			ResultSet res7 = st7.executeQuery("SELECT * FROM roleList WHERE idroleListt ="+idroleListt2+" AND idunitList="+idunitList);
			if(res7.next()){
				playsRole = true;
				break;
			}
		}
		
		Statement st8 = db.connection.createStatement();
		ResultSet res8;
		if(playsRole)
			res8 = st8.executeQuery("SELECT roleName, idaccesibility, idvisiblity, idposition FROM roleList WHERE idunitList ="+idunitList+" AND (idVisibility ="+idPrivateVisbility+" OR idVisibility ="+idPublicVisibility+")");
		else
			res8 = st8.executeQuery("SELECT roleName, idaccesibility, idvisiblity, idposition FROM roleList WHERE idunitList ="+idunitList+" AND idVisibility ="+idPublicVisibility);
		while(res8.next()){
			int idposition = res8.getInt("idposition");
			int idaccesibility = res8.getInt("idaccesibility");
			int idvisiblity = res8.getInt("idvisiblity");
			Statement st9 = db.connection.createStatement();
			ResultSet res9 = st9.executeQuery("SELECT position FROM position WHERE idposition ="+idposition);
			res9.next();
			
			Statement st10 = db.connection.createStatement();
			ResultSet res10 = st10.executeQuery("SELECT accesiblity FROM accesibility WHERE idaccesibility ="+idaccesibility);
			res10.next();
			
			Statement st11 = db.connection.createStatement();
			ResultSet res11 = st11.executeQuery("SELECT visibility FROM visibility WHERE idvisiblity ="+idvisiblity);
			res11.next();
			
			result += "<"+res8.getString("roleName")+","+res10.getString("accesiblity")+","+res11.getString("visibility")+","+res9.getString("position")+">";
		}
		return result;		
	}
	
	public String getInformRole(String roleName, String unitName, String agentName) throws SQLException{
		// TODO per a que vull agentName?
		int idunitList;

		Statement st2 = db.connection.createStatement();
		ResultSet res2 = st2.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unitName+"'");
		if(res2.next())
			idunitList = res2.getInt("idunitList");
		else
			return "Error : unit "+unitName+" not found in database";


		Statement st6 = db.connection.createStatement();
		ResultSet res6 = st6.executeQuery("SELECT idaccesibility, idposition, idvisibility FROM roleList WHERE roleName ='"+roleName+"' AND idunitList ="+idunitList);
		if(res6.next()){
			int idposition = res6.getInt("idposition");
			int idaccesibility = res6.getInt("idaccesibility");
			int idvisiblity = res6.getInt("idvisiblity");
			Statement st9 = db.connection.createStatement();
			ResultSet res9 = st9.executeQuery("SELECT position FROM position WHERE idposition ="+idposition);
			res9.next();
			
			Statement st10 = db.connection.createStatement();
			ResultSet res10 = st10.executeQuery("SELECT accesiblity FROM accesibility WHERE idaccesibility ="+idaccesibility);
			res10.next();
			
			Statement st11 = db.connection.createStatement();
			ResultSet res11 = st11.executeQuery("SELECT visibility FROM visibility WHERE idvisiblity ="+idvisiblity);
			res11.next();
			
			return "<"+res10.getString("accesiblity")+","+res11.getString("visibility")+","+res9.getString("position")+">";
		}
		return "Error : role "+roleName+" not found in database";
	}
}