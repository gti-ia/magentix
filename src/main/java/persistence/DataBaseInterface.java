package persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

class DataBaseInterface
{
	private DataBase	db;
	DataBaseInterface()
	{
		db = new DataBase();
	}

	String acquireRole(String unitName, String roleName, String agentName) throws SQLException{
		Statement st;		
		st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unitName+"'");
		if(res.next()){
			int idUnit = res.getInt("idunitList");
			Statement st2 = db.connection.createStatement();
			ResultSet res2 = st2.executeQuery("SELECT idroleList FROM roleList WHERE roleName ='"+roleName+"' AND idunitList = "+idUnit);
			if(res2.next()){
				int idRole = res2.getInt("idroleList");
				Statement st3 = db.connection.createStatement();
				int res3 = st3.executeUpdate("INSERT INTO agentPlayList (agentName, idroleList) VALUES ('"+agentName+"', "+idRole+")");
				if(res3 != 0){
					db.connection.commit();
					return "<"+roleName+" + \"acquired\">";
				}
			}
			return "Error: role "+roleName+" not found in unit "+unitName;
		}
		return "Error: unit "+unitName+" not found in database";		
	}

	/*String allocateRole(String roleName, String unitName, String targetAgentName, String agentName) throws SQLException{
		// TODO no té molt de trellat la especificació, fa el mateix q l'anterior funció
		Statement st;		
		st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unitName+"'");
		if(res.next()){
			int idUnit = res.getInt("idunitList");
			Statement st2 = db.connection.createStatement();
			ResultSet res2 = st2.executeQuery("SELECT idroleList FROM roleList WHERE roleName ='"+roleName+"' AND idunitList = "+idUnit);
			if(res2.next()){
				int idRole = res.getInt("idroleList");
				Statement st3 = db.connection.createStatement();
				int res3 = st3.executeUpdate("INSERT INTO agentPlayList (agentName, idroleList) VALUES ('"+agentName+"', "+idRole+")");
				if(res3 != 0){
					db.connection.commit();
					return "<"+roleName+" + \"acquired\">";
				}
			}
			return "Error: role "+roleName+" not found in unit "+unitName;
		}
		return "Error: unit "+unitName+" not found in database";		
	}*/

	boolean checkAgent(String agentName) throws SQLException{
		boolean exists = false;

		Statement stmt = db.connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM agentPlayList WHERE agentName='"+ agentName + "'");
		while (rs.next())
		{
			db.connection.commit();
			exists = true;
		}

		return exists;
	}

	boolean checkAgentInUnit(String agentName, String unit) throws SQLException{
		boolean exists = false;

		Statement stmt = db.connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+unit+"'");
		if(rs.next()){
			int unitId = rs.getInt("idunitList");
			ResultSet rs2 = stmt.executeQuery("SELECT idroleList FROM roleList WHERE idunitList ="+unitId);
			while(rs2.next())
			{
				Statement stmt2 = db.connection.createStatement();
				int idRole = rs2.getInt("idroleList");
				
				ResultSet rs3 = stmt2.executeQuery("SELECT * FROM agentPlayList WHERE idroleList="+idRole+" AND agentName='"+agentName+"'");

				if (rs3.next())
				{
					db.connection.commit();
					exists = true;
				}
			}
		}

		return exists;
	}

	boolean checkAgentPlaysRole(String agentName, String role, String unit) throws SQLException{
		boolean exists = false;		
		Statement stmt = db.connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+unit+"'");
		if(rs.next()){
			int unitId = rs.getInt("idunitList");
			ResultSet rs2 = stmt.executeQuery("SELECT idroleList FROM roleList WHERE idunitList ="+unitId+" AND roleName ='"+role+"'");
			while(rs2.next())
			{
				int roleId = rs2.getInt("idroleList");
				Statement stmt2 = db.connection.createStatement();
				ResultSet rs3 = stmt2.executeQuery("SELECT * FROM agentPlayList WHERE idroleList = "+roleId+" AND agentName='"+agentName+"'");
				if (rs3.next())
				{
					db.connection.commit();
					exists = true;
				}
			}
			db.connection.commit();
		}		
		return exists;
	}

	boolean checkNoCreatorAgentsInUnit(String unit) throws SQLException{		
		Statement stmt = db.connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT idposition FROM position WHERE position ='creator'");
		if(rs.next()){
			int positionId = rs.getInt("idposition");
			ResultSet rs2 = stmt.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+unit+"'");
			if (rs2.next())
			{
				int unitId = rs2.getInt("idunitList");
				ResultSet rs3 = stmt.executeQuery("SELECT idroleList FROM roleList WHERE idunitlist ="+unitId+" AND idposition !="+positionId);
				while(rs3.next())
				{

					int roleId = rs3.getInt("idroleList");

					Statement stmt2 = db.connection.createStatement();
					ResultSet rs4 = stmt2.executeQuery("SELECT * FROM agentPlayList WHERE idroleList ="+roleId);

					if (rs4.next())
					{
						db.connection.commit();
						return true;
					}
				}
			}
		}	
		db.connection.commit();
		return false;
	}

	boolean checkPlayedRoleInUnit(String role, String unit) throws SQLException{

		Statement stmt = db.connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+unit+"'");
		if(rs.next()){
			int unitId = rs.getInt("idunitList");
			ResultSet rs2 = stmt.executeQuery("SELECT idroleList FROM roleList WHERE idunitList ="+unitId+" AND roleName='"+role+"'");
			if (rs2.next())
			{
				int roleId = rs2.getInt("idroleList");
				Statement stmt2 = db.connection.createStatement();
				ResultSet rs3 = stmt2.executeQuery("SELECT * FROM agentPlayList WHERE idroleList ="+roleId);
				if (rs3.next())
				{
					db.connection.commit();
					return true;
				}
			}
		}
		db.connection.commit();
		return false;
	}

	boolean checkTargetRoleNorm(String role, String unit){
		// TODO on estan les normes
		return false;
	}

	boolean checkPosition(String agent, String position) throws SQLException{
		Statement st;		
		st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idroleList FROM agentPlayList WHERE agentName ='"+ agent +"'");
		while(res.next()){
			int idRole = res.getInt("idroleList");
			Statement st2 = db.connection.createStatement();
			ResultSet res2 = st2.executeQuery("SELECT idposition FROM roleList WHERE idroleList ="+idRole);
			if(res2.next()){
				int idPosition = res2.getInt("idposition");
				Statement st3 = db.connection.createStatement();
				ResultSet res3 = st3.executeQuery("SELECT * FROM position WHERE idposition ="+idPosition);
				if(res3.next() && res3.getString("position").equalsIgnoreCase(position)){
					db.connection.commit();
					return true;
				}
			}
		}
		return false;
	}

	boolean checkPositionInUnit(String agent, String position, String unit) throws SQLException{
		Statement st;
		int idUnit = -1;
		st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idroleList FROM agentPlayList WHERE agentName ='"+ agent +"'");
		Statement st4 = db.connection.createStatement();
		ResultSet res4 = st4.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unit +"'");
		if(res4.next())
			idUnit = res4.getInt("idunitList");
		while(res.next() && idUnit > -1){
			int idRole = res.getInt("idroleList");
			Statement st2 = db.connection.createStatement();
			ResultSet res2 = st2.executeQuery("SELECT idposition FROM roleList WHERE idroleList ="+idRole+" AND idunitList ="+idUnit);
			if(res2.next()){
				int idPosition = res2.getInt("idposition");
				Statement st3 = db.connection.createStatement();
				ResultSet res3 = st3.executeQuery("SELECT * FROM position WHERE idposition ="+idPosition);
				if(res3.next() && res3.getString("position").equalsIgnoreCase(position)){
					db.connection.commit();
					return true;
				}
			}
		}
		db.connection.commit();
		return false;
	}

	boolean checkRole(String role, String unit) throws SQLException{
		Statement st;
		st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unit +"'");
		if(res.next()){
			int idUnit = res.getInt("idunitList");
			Statement st2 = db.connection.createStatement();
			ResultSet res2 = st2.executeQuery("SELECT * FROM roleList WHERE idunitList ="+idUnit+" AND roleName ='"+role+"'");
			if(res2.next()){
				db.connection.commit();
				return true;
			}
		}
		db.connection.commit();
		return false;
	}

	boolean checkSubUnits(String unit) throws SQLException{
		Statement st;
		st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unit +"'");
		if(res.next()){
			int idUnit = res.getInt("idunitList");
			Statement st2 = db.connection.createStatement();
			ResultSet res2 = st2.executeQuery("SELECT * FROM unitHierarchy WHERE idParentUnit ="+idUnit);
			if(res2.next()){
				db.connection.commit();
				return true;
			}
		}
		db.connection.commit();
		return false;
	}

	boolean checkUnit(String unit) throws SQLException{
		Statement st;
		st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT * FROM unitList WHERE unitName ='"+ unit +"'");
		if(res.next()){
			db.connection.commit();
			return true;
		}
		db.connection.commit();
		return false;
	}

	boolean checkVirtualUnit(String unit) throws SQLException{
		Statement st;
		st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idunitType FROM unitType WHERE unitTypeName ='virtual'");
		if(res.next()){
			int idUnitType = res.getInt("idunitType");
			Statement st2 = db.connection.createStatement();
			ResultSet res2 = st2.executeQuery("SELECT * FROM unitList WHERE idunitType ="+idUnitType+" AND unitName ='"+unit+"'");
			if(res2.next()){
				db.connection.commit();
				return true;
			}
		}
		db.connection.commit();
		return false;
	}

	String createRole(String roleName, String unitName, String accessibility, String visibility, String position) throws SQLException, THOMASException{
		Statement st;		
		st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unitName+"'");
		if(res.next()){
			int idunit = res.getInt("idunitList");
			Statement st4 = db.connection.createStatement();
			ResultSet res4 = st4.executeQuery("SELECT idposition FROM position WHERE position ='"+ position+"'");
			if(res4.next()){
				int idposition = res4.getInt("idposition");
				Statement st5 = db.connection.createStatement();
				ResultSet res5 = st5.executeQuery("SELECT idaccesibility FROM accesibility WHERE accesibility ='"+ accessibility+"'");
				if(res5.next()){
					int idaccesibility = res5.getInt("idaccesibility");
					Statement st6 = db.connection.createStatement();
					ResultSet res6 = st6.executeQuery("SELECT idvisibility FROM visibility WHERE visibility ='"+ visibility+"'");
					if(res6.next()){
						int idvisibility = res6.getInt("idvisibility");
						Statement st3 = db.connection.createStatement();
						int res3 = st3.executeUpdate("INSERT INTO roleList (roleName, idunitList, idposition, idaccesibility, idvisibility) VALUES ('"+roleName+"', "+idunit+","+idposition+","+idaccesibility+","+idvisibility+")");
						if(res3 != 0){
							db.connection.commit();
							return "<"+roleName+" + \"created\">";
						}
					}
					throw new THOMASException("Error: visibility "+visibility+" not found in database");
				}
				throw new THOMASException("Error: accesibility "+accessibility+" not found in database");
			}
			throw new THOMASException("Error: position "+position+" not found in database");
		}
		throw new THOMASException("Error: unit "+unitName+" not found in database");
	}

	String createUnit(String unitName, String unitType, String parentUnitName, String agentName, String creatorAgentName) throws SQLException, THOMASException{
		Statement st;		
		st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idunitType FROM unitType WHERE unitTypeName ='"+ unitType+"'");
		if(res.next()){
			int idunitType = res.getInt("idunitType");
			Statement st4 = db.connection.createStatement();
			int res4 = st4.executeUpdate("INSERT INTO unitList (unitName, idunitType) VALUES ('"+unitName+"', "+idunitType+")");
			if(res4 != 0){
				Statement st12 = db.connection.createStatement();
				ResultSet res12 = st12.executeQuery("SELECT LAST_INSERT_ID()");
				if(res12.next()){
					int insertedUnitId = res12.getInt(1);
					Statement st5 = db.connection.createStatement();
					ResultSet res5 = st5.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ parentUnitName+"'");
					if(res5.next()){
						int idunitParent = res5.getInt("idunitList");
						Statement st6 = db.connection.createStatement();
						int res6 = st6.executeUpdate("INSERT INTO unitHierarchy (idParentUnit, idChildUnit) VALUES ("+idunitParent+", "+insertedUnitId+")");
						if(res6 != 0){
							Statement st7 = db.connection.createStatement();
							ResultSet res7 = st7.executeQuery("SELECT idposition FROM position WHERE position ='creator'");
							if(res7.next()){
								int idposition = res7.getInt("idposition");
								Statement st8 = db.connection.createStatement();
								ResultSet res8 = st8.executeQuery("SELECT idaccesibility FROM accesibility WHERE accesibility ='internal'");
								if(res8.next()){
									int idaccesibility = res8.getInt("idaccesibility");
									Statement st9 = db.connection.createStatement();
									ResultSet res9 = st9.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='private'");
									if(res9.next()){
										int idVisibility = res9.getInt("idVisibility");
										Statement st10 = db.connection.createStatement();
										int res10 = st10.executeUpdate("INSERT INTO roleList (roleName, idunitList, idposition, idaccesibility, idvisibility) VALUES ('"+creatorAgentName+"', "+insertedUnitId+", "+idposition+","+idaccesibility+","+idVisibility+")");
										if(res10 != 0){
											Statement st11 = db.connection.createStatement();
											int res11 = st11.executeUpdate("INSERT INTO agentPlayList (agentName, idroleList) VALUES ('"+agentName+"', LAST_INSERT_ID())");
											if(res11 != 0){
												db.connection.commit();
												return "<"+unitName+" + \"created\">";
											}
											throw new THOMASException("Error: inserting new play list");
										}
										throw new THOMASException("Error: inserting new role");
									}
									throw new THOMASException("Error: visibility private not found in database");
								}
								throw new THOMASException("Error: accesibility internal not found in database");
							}
							throw new THOMASException("Error: position creator not found in database");
						}
						throw new THOMASException("Error: inserting hierarchy");
					}
					throw new THOMASException("Error: parent unit "+parentUnitName+" not found in database");
				}
			}			
			throw new THOMASException("Error: inserting new unit");
		}
		throw new THOMASException("Error: unitType "+unitType+" not found in database");
	}

	String deallocateRole(String roleName, String unitName, String targetAgentName, String agentName) throws SQLException, THOMASException{
		Statement st;		
		st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unitName+"'");
		if(res.next()){
			int idunitList = res.getInt("idunitList");
			Statement st2 = db.connection.createStatement();
			ResultSet res2 = st2.executeQuery("SELECT idroleList FROM roleList WHERE roleName ='"+ roleName+"' AND idunitList ="+idunitList);
			if(res2.next()){
				int idroleList = res2.getInt("idroleList");
				Statement st3 = db.connection.createStatement();
				int res3 = st3.executeUpdate("DELETE FROM agentPlayList WHERE agentName = '"+targetAgentName+"' AND idroleList = "+idroleList);
				if(res3 != 0){
					db.connection.commit();
					return "<"+roleName+" + \"deallocated\">";
				}
				throw new THOMASException("Error: mysql error "+res3);
			}
			throw new THOMASException("Error: rolename "+roleName+" not found in database");
		}
		throw new THOMASException("Error: unit "+unitName+" not found in database");
	}

	String deleteRole(String roleName, String unitName, String agentName) throws SQLException, THOMASException{
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
			throw new THOMASException("Error: mysql error "+res2);
		}
		throw new THOMASException("Error: unit "+unitName+" not found in database");
	}

	String deleteUnit(String unitName, String agentName) throws SQLException, THOMASException{
		Statement st;		
		st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unitName+"'");
		if(res.next()){
			int idunitList = res.getInt("idunitList");
			Statement st2 = db.connection.createStatement();
			ResultSet res2 = st2.executeQuery("SELECT idposition FROM position WHERE position ='creator'");
			if(res2.next()){
				int idposition = res2.getInt("idposition");
				Statement st3 = db.connection.createStatement();
				ResultSet res3 = st3.executeQuery("SELECT idroleList FROM roleList WHERE idposition ="+idposition+" AND idunitList ="+idunitList);
				while(res3.next()){
					int idroleList = res3.getInt("idroleList");
					Statement st4 = db.connection.createStatement();
					st4.executeUpdate("DELETE FROM agentPlayList WHERE idroleList ="+idroleList);
					//if(res4 == 0)//Puede que no haya nadie jugando ese rol, no tiene por que dar un error.
					//throw new THOMASException("Error: mysql error in agentPlayList "+res4);
				}
				Statement st5 = db.connection.createStatement();
				st5.executeUpdate("DELETE FROM roleList WHERE idunitList ="+idunitList);
				Statement st7 = db.connection.createStatement();
				int res7 = st7.executeUpdate("DELETE FROM unitHierarchy WHERE idChildUnit ="+idunitList);
				if (res7 != 0)
				{
					Statement st6 = db.connection.createStatement();
					int res6 = st6.executeUpdate("DELETE FROM unitList WHERE idunitList ="+idunitList);
					if(res6 != 0){
						db.connection.commit();
						return "<"+unitName+" + \"deleted\">";
					}
					throw new THOMASException("Error: mysql error in delete from unitList "+res6);
				}
				throw new THOMASException("Error: mysql error in delete from unitHierarchy "+res7);

			}
			throw new THOMASException("Error: position creator not found in database");
		}
		throw new THOMASException("Error: unit "+unitName+" not found in database");
	}

	String jointUnit(String unitName, String parentName) throws SQLException, THOMASException{
		Statement st;		
		st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unitName+"'");
		if(res.next()){
			int idunitList = res.getInt("idunitList");
			Statement st2 = db.connection.createStatement();
			ResultSet res2 = st2.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+parentName+"'");
			if(res2.next()){
				int idParentUnit = res2.getInt("idunitList");
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
				throw new THOMASException("Error: mysql error "+res3);
			}
			throw new THOMASException("Error: unit "+parentName+" not found in database");
		}
		throw new THOMASException("Error: unit "+unitName+" not found in database");
	}

	String leaveRole(String unitName, String roleName, String agentName) throws SQLException, THOMASException{
		Statement st;
		st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unitName+"'");
		if(res.next()){
			int idunitList = res.getInt("idunitList");
			Statement st2 = db.connection.createStatement();
			ResultSet res2 = st2.executeQuery("SELECT idroleList FROM roleList WHERE idunitList ="+idunitList+" AND roleName ='"+roleName+"'");
			if(res2.next()){
				int idroleList = res2.getInt("idroleList");
				Statement st3 = db.connection.createStatement();
				int res3 = st3.executeUpdate("DELETE FROM agentPlayList WHERE idroleList ="+idroleList +" AND agentName='"+agentName+"'");
				if(res3 != 0){					
					db.connection.commit();
					return "<"+roleName+" + \"left\">";			
				}
				throw new THOMASException("Error: mysql error "+res3);
			}
			throw new THOMASException("Error: unit "+roleName+" not found in unit "+unitName);
		}
		throw new THOMASException("Error: unit "+unitName+" not found in database");
	}

	String getUnitType(String unitName) throws SQLException, THOMASException{
		Statement st;
		st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idunitType FROM unitList WHERE unitName ='"+ unitName+"'");
		if(res.next()){
			int idunitType = res.getInt("idunitType");
			Statement st2 = db.connection.createStatement();
			ResultSet res2 = st2.executeQuery("SELECT unitTypeName FROM unitType WHERE idunitType ="+idunitType);
			if(res2.next()){
				db.connection.commit();
				return res2.getString("unitTypeName");
			}
			throw new THOMASException("Error: idunitType "+idunitType+" not found in database");
		}
		throw new THOMASException("Error: unit "+unitName+" not found in database");
	}

	ArrayList<ArrayList<String>> getAgentsInUnit(String unitName) throws SQLException, THOMASException{
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		Statement st;
		st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unitName+"'");
		if(res.next()){
			int idunitList = res.getInt("idunitList");
			Statement st2 = db.connection.createStatement();
			ResultSet res2 = st2.executeQuery("SELECT * FROM roleList WHERE idunitList ="+idunitList);
			while(res2.next()){
				ArrayList<String> aux = new ArrayList<String>();
				String roleName = res2.getString("roleName");
				int idposition = res2.getInt("idposition");
				int idroleList = res2.getInt("idroleList");
				Statement st3 = db.connection.createStatement();
				ResultSet res3 = st3.executeQuery("SELECT agentName FROM agentPlayList WHERE idroleList ="+idroleList);
				if(res3.next())
					aux.add(res3.getString("agentName"));
				aux.add(roleName);
				Statement st4 = db.connection.createStatement();
				ResultSet res4 = st4.executeQuery("SELECT position FROM position WHERE idposition ="+idposition);
				if(res4.next())
					aux.add(res4.getString("position"));
				result.add(aux);
			}
			db.connection.commit();
			return result;
		}
		throw new THOMASException("Error: unit "+unitName+" not found in database");
	}

	ArrayList<String> getParentsUnit(String unitName) throws SQLException, THOMASException{
		Statement st;	
		ArrayList<String> result = new ArrayList<String>();
		st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unitName+"'");
		if(res.next()){
			int idunitList = res.getInt("idunitList");
			Statement st2 = db.connection.createStatement();
			ResultSet res2 = st2.executeQuery("SELECT idParentUnit FROM unitHierarchy WHERE idChildUnit ="+idunitList);
			if(res2.next()){
				int idParentUnit = res2.getInt("idParentUnit");
				Statement st3 = db.connection.createStatement();
				ResultSet res3 = st3.executeQuery("SELECT unitName FROM unitList WHERE idunitList ="+idParentUnit);
				if(res3.next()){
					result.add(res3.getString("unitName"));
					db.connection.commit();
					return result;
				}
			}
			result.add("virtual");
			db.connection.commit();
			return result;
		}
		throw new THOMASException("Error: unit "+unitName+" not found in database");
	}

	ArrayList<ArrayList<String>> getInformAgentRole(String requestedAgentName, String agentName) throws SQLException, THOMASException{
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		Statement st;
		Statement st2 = db.connection.createStatement();
		int idVisibility;
		ResultSet res2 = st2.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='public'");
		if(res2.next())
			idVisibility = res2.getInt("idVisibility");
		else
			throw new THOMASException("Error: visibility public not found in database");

		st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idroleList FROM agentPlayList WHERE agentName ='"+ requestedAgentName+"'");
		while(res.next()){
			int idroleList = res.getInt("idroleList");
			Statement st3 = db.connection.createStatement();
			ResultSet res3 = st3.executeQuery("SELECT idunitList, roleName FROM roleList WHERE idroleList ="+idroleList+" AND idvisibility ="+idVisibility);
			if(res3.next()){
				ArrayList<String> aux = new ArrayList<String>();
				int idunitList = res3.getInt("idunitList");
				String roleName = res3.getString("roleName");		
				Statement st4 = db.connection.createStatement();
				ResultSet res4 = st4.executeQuery("SELECT unitName FROM unitList WHERE idunitList ="+idunitList);
				if(res4.next()){
					String unitName = res4.getString("unitName");
					aux.add(roleName);
					aux.add(unitName);
					result.add(aux);
				}
			}
		}

		ArrayList<Integer> idunits1 = new ArrayList<Integer>();
		ArrayList<Integer> idunits2 = new ArrayList<Integer>();
		Statement st6 = db.connection.createStatement();
		ResultSet res6 = st6.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='private'");
		if(res6.next())
			idVisibility = res6.getInt("idVisibility");
		else
			throw new THOMASException("Error: visibility private not found in database");

		Statement st7 = db.connection.createStatement();
		ResultSet res7 = st7.executeQuery("SELECT idroleList FROM agentPlayList WHERE agentName ='"+ requestedAgentName+"'");
		while(res7.next()){
			int idroleList = res7.getInt("idroleList");
			Statement st8 = db.connection.createStatement();
			ResultSet res8 = st8.executeQuery("SELECT idunitList FROM roleList WHERE idroleList ="+idroleList+" AND idvisibility ="+idVisibility);
			if(res8.next()){
				idunits1.add(res8.getInt("idunitList"));
			}
		}

		Statement st9 = db.connection.createStatement();
		ResultSet res9 = st9.executeQuery("SELECT idroleList FROM agentPlayList WHERE agentName ='"+ agentName+"'");
		while(res9.next()){
			int idroleList = res9.getInt("idroleList");
			Statement st10 = db.connection.createStatement();
			ResultSet res10 = st10.executeQuery("SELECT idunitList FROM roleList WHERE idroleList ="+idroleList);// AND idvisibility ="+idVisibility);
			if(res10.next()){
				idunits2.add(res10.getInt("idunitList"));
			}
		}

	
		for(int unitid : idunits1){
			
			if(idunits2.contains(unitid)){
				Statement st11 = db.connection.createStatement();
				ResultSet res11 = st11.executeQuery("SELECT idroleList FROM agentPlayList WHERE agentName ='"+ requestedAgentName+"'");
				while(res11.next()){
					ArrayList<String> aux = new ArrayList<String>();
					int idroleList = res11.getInt("idroleList");
					Statement st12 = db.connection.createStatement();
					
					ResultSet res12 = st12.executeQuery("SELECT roleName FROM roleList WHERE idroleList ="+idroleList+" AND idvisibility ="+idVisibility+" AND idunitList="+unitid);
					if(res12.next()){
						Statement st13 = db.connection.createStatement();
						ResultSet res13 = st13.executeQuery("SELECT unitName FROM unitList WHERE idunitList ="+unitid);
						if(res13.next()){
							aux.add(res12.getString("roleName"));
							aux.add(res13.getString("unitName"));
							result.add(aux);
						}
					}
				}
			}
		}
		db.connection.commit();
		return result;
	}

	ArrayList<ArrayList<String>> getInformAgentRolesPlayedInUnit(String unitName, String targetAgentName) throws SQLException, THOMASException{
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		int idunitList;

		Statement st2 = db.connection.createStatement();
		ResultSet res2 = st2.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unitName+"'");
		if(res2.next())
			idunitList = res2.getInt("idunitList");
		else
			throw new THOMASException("Error : unit "+unitName+" not found in database");

		Statement st10 = db.connection.createStatement();
		ResultSet res10 = st10.executeQuery("SELECT idroleList FROM agentPlayList WHERE agentName ='"+targetAgentName+"'");
		while(res10.next()){
			int idroleList = res10.getInt("idroleList");
			Statement st11 = db.connection.createStatement();
			ResultSet res11 = st11.executeQuery("SELECT * FROM roleList WHERE idroleList ="+idroleList+" AND idunitList="+idunitList);
			if(res11.next()){
				int idvisibility = res11.getInt("idvisibility");
				int idaccesibility = res11.getInt("idaccesibility");
				int idposition = res11.getInt("idposition");
				String roleName = res11.getString("roleName");
				String position = "";
				String visibility = "";
				String accesibility = "";
				
				Statement st12 = db.connection.createStatement();
				ResultSet res12 = st12.executeQuery("SELECT * FROM position WHERE idposition ="+idposition);				
				if(res12.next())
					position = res12.getString("position");
				
				Statement st13 = db.connection.createStatement();
				ResultSet res13 = st13.executeQuery("SELECT * FROM accesibility WHERE idaccesibility ="+idaccesibility);
				if(res13.next())
					accesibility = res13.getString("accesibility");
				
				Statement st14 = db.connection.createStatement();
				ResultSet res14 = st14.executeQuery("SELECT * FROM visibility WHERE idvisibility ="+idvisibility);
				if(res14.next())
					visibility = res14.getString("visibility");
				
				ArrayList<String>aux = new ArrayList<String>();
				aux.add(roleName);
				aux.add(visibility);
				aux.add(accesibility);
				aux.add(position);
				result.add(aux);
			}
		}
		db.connection.commit();
		return result;		
	}
	
	ArrayList<ArrayList<String>> getAgentsRolesInUnit(String unitName, String agentName) throws SQLException, THOMASException{
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		int idPublicVisibility;
		int idPrivateVisbility;
		boolean playsRole = false;
		int idunitList;

		Statement st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='public'");
		if(res.next())
			idPublicVisibility = res.getInt("idVisibility");
		else
			throw new THOMASException("Error: visibility public not found in database");

		Statement st3 = db.connection.createStatement();
		ResultSet res3 = st3.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='private'");
		if(res3.next())
			idPrivateVisbility = res3.getInt("idVisibility");
		else
			throw new THOMASException("Error: visibility private not found in database");

		Statement st2 = db.connection.createStatement();
		ResultSet res2 = st2.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unitName+"'");
		if(res2.next())
			idunitList = res2.getInt("idunitList");
		else
			throw new THOMASException("Error : unit "+unitName+" not found in database");


		Statement st10 = db.connection.createStatement();
		ResultSet res10 = st10.executeQuery("SELECT idroleList FROM agentPlayList WHERE agentName ='"+agentName+"'");
		while(res10.next()){
			int idroleList = res10.getInt("idroleList");
			Statement st11 = db.connection.createStatement();
			ResultSet res11 = st11.executeQuery("SELECT * FROM roleList WHERE idroleList ="+idroleList+" AND idunitList="+idunitList);
			if(res11.next()){
				playsRole = true;
				break;
			}
		}

		Statement st12 = db.connection.createStatement();
		ResultSet res12;
		if(playsRole)
			res12 = st12.executeQuery("SELECT idroleList, roleName FROM roleList WHERE idunitList ="+ idunitList+" AND (idVisibility ="+idPrivateVisbility+" OR idVisibility ="+idPublicVisibility+")");
		else
			res12 = st12.executeQuery("SELECT idroleList, roleName FROM roleList WHERE idunitList ="+ idunitList+" AND idVisibility ="+idPublicVisibility);
		while(res12.next()){
			
			String roleName = res12.getString("roleName");
			int idroleList = res12.getInt("idroleList");
			Statement st13 = db.connection.createStatement();
			
			ResultSet res13 = st13.executeQuery("SELECT agentName FROM agentPlayList WHERE idroleList ="+idroleList);
			
			while(res13.next()){
				ArrayList<String> aux = new ArrayList<String>();
				aux.add(res13.getString("agentName"));
				aux.add(roleName);
				result.add(aux);
			}
	
			
		}
		db.connection.commit();
		return result;
	}

	ArrayList<String> getAgentsPlayingRoleInUnit(String unitName, String roleName, String agentName) throws SQLException, THOMASException{
		ArrayList<String> result = new ArrayList<String>();
		int idPublicVisibility;
		int idPrivateVisbility;
		boolean playsRole = false;
		int idunitList;

		Statement st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='public'");
		if(res.next())
			idPublicVisibility = res.getInt("idVisibility");
		else
			throw new THOMASException("Error: visibility public not found in database");

		Statement st3 = db.connection.createStatement();
		ResultSet res3 = st3.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='private'");
		if(res3.next())
			idPrivateVisbility = res3.getInt("idVisibility");
		else
			throw new THOMASException("Error: visibility private not found in database");

		Statement st2 = db.connection.createStatement();
		ResultSet res2 = st2.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unitName+"'");
		if(res2.next())
			idunitList = res2.getInt("idunitList");
		else
			throw new THOMASException("Error : unit "+unitName+" not found in database");


		Statement st10 = db.connection.createStatement();
		ResultSet res10 = st10.executeQuery("SELECT idroleList FROM agentPlayList WHERE agentName ='"+agentName+"'");
		while(res10.next()){
			int idroleList = res10.getInt("idroleList");
			Statement st11 = db.connection.createStatement();
			ResultSet res11 = st11.executeQuery("SELECT * FROM roleList WHERE idroleList ="+idroleList+" AND idunitList="+idunitList);
			if(res11.next()){
				playsRole = true;
				break;
			}
		}

		Statement st12 = db.connection.createStatement();
		ResultSet res12;
		if(playsRole)
			res12 = st12.executeQuery("SELECT idroleList FROM roleList WHERE idunitList ="+ idunitList+" AND roleName ='"+roleName+"' AND (idVisibility ="+idPrivateVisbility+" OR idVisibility ="+idPublicVisibility+")");
		else
			res12 = st12.executeQuery("SELECT idroleList FROM roleList WHERE idunitList ="+ idunitList+" AND roleName ='"+roleName+"' AND idVisibility ="+idPublicVisibility);
		while(res12.next()){
			int idroleList = res12.getInt("idroleList");
			Statement st13 = db.connection.createStatement();
			ResultSet res13 = st13.executeQuery("SELECT agentName FROM agentPlayList WHERE idroleList ="+idroleList);
			while(res13.next()){
				result.add(res13.getString("agentName"));
			}
		}
		db.connection.commit();
		return result;
	}

	ArrayList<ArrayList<String>> getAgentsPlayingPositionInUnit(String unitName,String positionValue, String agentName) throws SQLException, THOMASException{
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();		
		int idposition;
		int idPublicVisibility;
		int idPrivateVisbility;
		boolean playsRole = false;
		int idunit;

		Statement st10 = db.connection.createStatement();
		ResultSet res10 = st10.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='public'");
		if(res10.next())
			idPublicVisibility = res10.getInt("idVisibility");
		else
			throw new THOMASException("Error: visibility public not found in database");

		Statement st11 = db.connection.createStatement();
		ResultSet res11 = st11.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='private'");
		if(res11.next())
			idPrivateVisbility = res11.getInt("idVisibility");
		else
			throw new THOMASException("Error: visibility private not found in database");
		
		Statement st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idposition FROM position WHERE position ='"+positionValue+"'");
		if(res.next())
			idposition = res.getInt("idposition");
		else
			throw new THOMASException("Error: position "+positionValue+" not found in database");

		Statement st2 = db.connection.createStatement();
		ResultSet res2 = st2.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+unitName+"'");
		if(res2.next())
			idunit = res2.getInt("idunitList");
		else
			throw new THOMASException("Error: unit "+unitName+" not found in database");
		
		Statement st6 = db.connection.createStatement();
		ResultSet res6 = st6.executeQuery("SELECT idroleList FROM agentPlayList WHERE agentName ='"+agentName+"'");
		while(res6.next()){
			int idroleList2 = res6.getInt("idroleList");
			Statement st7 = db.connection.createStatement();
			ResultSet res7 = st7.executeQuery("SELECT * FROM roleList WHERE idroleList ="+idroleList2+" AND idunitList="+idunit);
			if(res7.next()){
				playsRole = true;
				break;
			}
		}

		Statement st3 = db.connection.createStatement();
		ResultSet res3;
		if(playsRole){
			res3 = st3.executeQuery("SELECT idroleList, roleName FROM roleList WHERE idunitList ="+idunit+" AND idposition ="+idposition+" AND (idvisibility ="+idPrivateVisbility+" OR idvisibility ="+idPublicVisibility+")");
		}
		else{
			res3 = st3.executeQuery("SELECT idroleList, roleName FROM roleList WHERE idunitList ="+idunit+" AND idposition ="+idposition+" AND idVisibility ="+idPublicVisibility);
		}
		while(res3.next()){
			int idroleList = res3.getInt("idroleList");
			String roleName = res3.getString("roleName");
			Statement st4 = db.connection.createStatement();
			ResultSet res4 = st4.executeQuery("SELECT agentName FROM agentPlayList WHERE idroleList ="+idroleList);
			while(res4.next()){
				ArrayList<String>aux = new ArrayList<String>();
				aux.add(res4.getString("agentName"));
				aux.add(roleName);
				result.add(aux);
			}
		}
		db.connection.commit();
		return result;
	}

	ArrayList<String> getAgentsPlayingRolePositionInUnit(String unitName, String roleName, String positionValue, String agentName) throws SQLException, THOMASException{
		// TODO deurien tornarse els agents q juguen el role roleName, amb la posicio positionValue en la unitat unitName?
		ArrayList<String> result = new ArrayList<String>();		
		int idPublicVisibility;
		int idPrivateVisbility;
		int idposition;
		int idroleList;
		boolean playsRole = false;
		int idunitList;

		Statement st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='public'");
		if(res.next())
			idPublicVisibility = res.getInt("idVisibility");
		else
			throw new THOMASException("Error: visibility public not found in database");

		Statement st3 = db.connection.createStatement();
		ResultSet res3 = st3.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='private'");
		if(res3.next())
			idPrivateVisbility = res3.getInt("idVisibility");
		else
			throw new THOMASException("Error: visibility private not found in database");

		Statement st2 = db.connection.createStatement();
		ResultSet res2 = st2.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unitName+"'");
		if(res2.next())
			idunitList = res2.getInt("idunitList");
		else
			throw new THOMASException("Error : unit "+unitName+" not found in database");

		Statement st4 = db.connection.createStatement();
		ResultSet res4 = st4.executeQuery("SELECT idposition FROM position WHERE position ='"+ positionValue+"'");
		if(res4.next())
			idposition = res4.getInt("idposition");
		else
			throw new THOMASException("Error : position "+positionValue+" not found in database");

		Statement st6 = db.connection.createStatement();
		ResultSet res6 = st6.executeQuery("SELECT idroleList FROM agentPlayList WHERE agentName ='"+agentName+"'");
		while(res6.next()){
			int idroleList2 = res6.getInt("idroleList");
			Statement st7 = db.connection.createStatement();
			ResultSet res7 = st7.executeQuery("SELECT * FROM roleList WHERE idroleList ="+idroleList2+" AND idunitList="+idunitList);
			if(res7.next()){
				playsRole = true;
				break;
			}
		}

		Statement st5 = db.connection.createStatement();
		ResultSet res5;
		if(playsRole)
		{
			
			res5 = st5.executeQuery("SELECT idroleList FROM roleList WHERE idUnitList="+idunitList+" AND roleName ='"+ roleName+"' AND idposition ="+idposition+" AND (idvisibility ="+idPrivateVisbility+" OR idvisibility ="+idPublicVisibility+")");
		}
		else
		{
			
			res5 = st5.executeQuery("SELECT idroleList FROM roleList WHERE idUnitList="+idunitList+" AND roleName ='"+ roleName+"' AND idposition ="+idposition+" AND idVisibility ="+idPublicVisibility);
		}
		if(res5.next())
			idroleList = res5.getInt("idroleList");
		else
			throw new THOMASException("Error : role "+roleName+" not found in database");

		Statement st10 = db.connection.createStatement();
		System.out.println("SELECT agentName FROM agentPlayList WHERE idroleList ="+idroleList);
		ResultSet res10 = st10.executeQuery("SELECT agentName FROM agentPlayList WHERE idroleList ="+idroleList);
		while(res10.next()){
			System.out.println("Agent name: "+ res10.getString("agentName"));
			result.add(res10.getString("agentName"));
		}
		db.connection.commit();
		return result;
	}

	int getQuantityAgentsRolesInUnit(String unitName, String agentName)  throws SQLException, THOMASException{
		int idPublicVisibility;
		int idroleList;
		boolean playsRole = false;
		int idunitList;

		Statement st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='public'");
		if(res.next())
			idPublicVisibility = res.getInt("idVisibility");
		else
			throw new THOMASException("Error: visibility public not found in database");

		Statement st2 = db.connection.createStatement();
		ResultSet res2 = st2.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unitName+"'");
		if(res2.next())
			idunitList = res2.getInt("idunitList");
		else
			throw new THOMASException("Error : unit "+unitName+" not found in database");

		Statement st6 = db.connection.createStatement();
		ResultSet res6 = st6.executeQuery("SELECT idroleList FROM agentPlayList WHERE agentName ='"+agentName+"'");
		while(res6.next()){
			int idroleList2 = res6.getInt("idroleList");
			Statement st7 = db.connection.createStatement();
			ResultSet res7 = st7.executeQuery("SELECT * FROM roleList WHERE idroleList ="+idroleList2+" AND idunitList="+idunitList);
			if(res7.next()){
				playsRole = true;
				break;
			}
		}

		Set<String> agentNames = new HashSet<String>();
		Statement st5 = db.connection.createStatement();
		ResultSet res5;
		if(playsRole)
			res5 = st5.executeQuery("SELECT idroleList FROM roleList WHERE idunitList="+idunitList);
		else
			res5 = st5.executeQuery("SELECT idroleList FROM roleList WHERE idunitList="+idunitList+" AND idVisibility ="+idPublicVisibility);
		while(res5.next()){
			idroleList = res5.getInt("idroleList");	
			Statement st10 = db.connection.createStatement();
			ResultSet res10 = st10.executeQuery("SELECT agentName FROM agentPlayList WHERE idroleList ="+idroleList);
			while(res10.next()){
				agentNames.add(res10.getString("agentName"));
			}
		}
		db.connection.commit();
		return agentNames.size();
	}

	int getQuantityAgentsPlayingRoleInUnit(String unitName, String roleName, String agentName) throws SQLException, THOMASException{
		int cont = 0;
		int idPublicVisibility;
		int idPrivateVisbility;
		int idroleList;
		boolean playsRole = false;
		int idunitList;

		Statement st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='public'");
		if(res.next())
			idPublicVisibility = res.getInt("idVisibility");
		else
			throw new THOMASException("Error: visibility public not found in database");

		Statement st3 = db.connection.createStatement();
		ResultSet res3 = st3.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='private'");
		if(res3.next())
			idPrivateVisbility = res3.getInt("idVisibility");
		else
			throw new THOMASException("Error: visibility private not found in database");

		Statement st2 = db.connection.createStatement();
		ResultSet res2 = st2.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unitName+"'");
		if(res2.next())
			idunitList = res2.getInt("idunitList");
		else
			throw new THOMASException("Error : unit "+unitName+" not found in database");

		Statement st6 = db.connection.createStatement();
		ResultSet res6 = st6.executeQuery("SELECT idroleList FROM agentPlayList WHERE agentName ='"+agentName+"'");
		while(res6.next()){
			int idroleList2 = res6.getInt("idroleList");
			Statement st7 = db.connection.createStatement();
			ResultSet res7 = st7.executeQuery("SELECT * FROM roleList WHERE idroleList ="+idroleList2+" AND idunitList="+idunitList);
			if(res7.next()){
				playsRole = true;
				break;
			}
		}

		Statement st5 = db.connection.createStatement();
		ResultSet res5;
		if(playsRole)
			res5 = st5.executeQuery("SELECT idroleList FROM roleList WHERE idunitList ="+ idunitList+" AND roleName ='"+ roleName+"' AND (idVisibility ="+idPrivateVisbility+" OR idvisibility ="+idPublicVisibility+")");
		else
			res5 = st5.executeQuery("SELECT idroleList FROM roleList WHERE idunitList ="+ idunitList+" AND roleName ='"+ roleName+"' AND idvisibility ="+idPublicVisibility);
		if(res5.next())
			idroleList = res5.getInt("idroleList");
		else
			return 0;

		Statement st10 = db.connection.createStatement();
		ResultSet res10 = st10.executeQuery("SELECT DISTINCT agentName FROM agentPlayList WHERE idroleList ="+idroleList);
		while(res10.next()){
			cont++;
		}
		db.connection.commit();
		return cont;
	}

	int getQuantityAgentsPlayingPositionInUnit(String unitName, String positionValue, String agentName)  throws SQLException, THOMASException{
		int idPublicVisibility;
		int idposition;
		int idroleList;
		boolean playsRole = false;
		int idunitList;

		Statement st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='public'");
		if(res.next())
			idPublicVisibility = res.getInt("idVisibility");
		else
			throw new THOMASException("Error: visibility public not found in database");

		Statement st2 = db.connection.createStatement();
		ResultSet res2 = st2.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unitName+"'");
		if(res2.next())
			idunitList = res2.getInt("idunitList");
		else
			throw new THOMASException("Error : unit "+unitName+" not found in database");

		Statement st4 = db.connection.createStatement();
		ResultSet res4 = st4.executeQuery("SELECT idposition FROM position WHERE position ='"+ positionValue+"'");
		if(res4.next())
			idposition = res4.getInt("idposition");
		else
			throw new THOMASException("Error : position "+positionValue+" not found in database");

		Statement st6 = db.connection.createStatement();
		ResultSet res6 = st6.executeQuery("SELECT idroleList FROM agentPlayList WHERE agentName ='"+agentName+"'");
		while(res6.next()){
			int idroleList2 = res6.getInt("idroleList");
			Statement st7 = db.connection.createStatement();
			ResultSet res7 = st7.executeQuery("SELECT * FROM roleList WHERE idroleList ="+idroleList2+" AND idunitList="+idunitList);
			if(res7.next()){
				playsRole = true;
				break;
			}
		}

		Set<String> agentNames = new HashSet<String>();
		Statement st5 = db.connection.createStatement();
		ResultSet res5;
		if(playsRole)
		{
			
			res5 = st5.executeQuery("SELECT idroleList FROM roleList WHERE idunitList="+idunitList+" AND idposition ="+idposition);
		}
		else
		{
			
			res5 = st5.executeQuery("SELECT idroleList FROM roleList WHERE idunitList="+idunitList+" AND idposition ="+idposition+" AND idvisibility ="+idPublicVisibility);
		}
		while(res5.next()){
			idroleList = res5.getInt("idroleList");
			Statement st10 = db.connection.createStatement();
			System.out.println("SELECT agentName FROM agentPlayList WHERE idroleList ="+idroleList);
			ResultSet res10 = st10.executeQuery("SELECT agentName FROM agentPlayList WHERE idroleList ="+idroleList);
			while(res10.next()){
				System.out.println("AgentName: "+res10.getString("agentName"));
				agentNames.add(res10.getString("agentName"));
			}
		}
		db.connection.commit();
		return agentNames.size();
	}

	int getQuantityAgentsPlayingRolePositionInUnit(String unitName, String roleName, String positionValue, String agentName) throws SQLException, THOMASException{
		int cont = 0;
		int idPublicVisibility;
		int idPrivateVisbility;
		int idposition;
		int idroleList;
		boolean playsRole = false;
		int idunitList;

		Statement st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='public'");
		if(res.next())
			idPublicVisibility = res.getInt("idVisibility");
		else
			throw new THOMASException("Error: visibility public not found in database");

		Statement st3 = db.connection.createStatement();
		ResultSet res3 = st3.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='private'");
		if(res3.next())
			idPrivateVisbility = res3.getInt("idVisibility");
		else
			throw new THOMASException("Error: visibility private not found in database");

		Statement st2 = db.connection.createStatement();
		ResultSet res2 = st2.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unitName+"'");
		if(res2.next())
			idunitList = res2.getInt("idunitList");
		else
			throw new THOMASException("Error : unit "+unitName+" not found in database");

		Statement st4 = db.connection.createStatement();
		ResultSet res4 = st4.executeQuery("SELECT idposition FROM position WHERE position ='"+ positionValue+"'");
		if(res4.next())
			idposition = res4.getInt("idposition");
		else
			throw new THOMASException("Error : position "+positionValue+" not found in database");

		Statement st6 = db.connection.createStatement();
		ResultSet res6 = st6.executeQuery("SELECT idroleList FROM agentPlayList WHERE agentName ='"+agentName+"'");
		while(res6.next()){
			int idroleList2 = res6.getInt("idroleList");
			Statement st7 = db.connection.createStatement();
			ResultSet res7 = st7.executeQuery("SELECT * FROM roleList WHERE idroleList ="+idroleList2+" AND idunitList="+idunitList);
			if(res7.next()){
				playsRole = true;
				break;
			}
		}

		Statement st5 = db.connection.createStatement();
		ResultSet res5;
		if(playsRole)
			res5 = st5.executeQuery("SELECT idroleList FROM roleList WHERE idunitList ="+ idunitList+" AND roleName ='"+ roleName+"' AND idposition ="+idposition+" AND (idVisibility ="+idPrivateVisbility+" OR idvisibility ="+idPublicVisibility+")");
		else
			res5 = st5.executeQuery("SELECT idroleList FROM roleList WHERE idunitList ="+ idunitList+" AND roleName ='"+ roleName+"' AND idposition ="+idposition+" AND idvisibility ="+idPublicVisibility);
		if(res5.next())
			idroleList = res5.getInt("idroleList");
		else
			return 0;

		Statement st10 = db.connection.createStatement();
		ResultSet res10 = st10.executeQuery("SELECT agentName FROM agentPlayList WHERE idroleList ="+idroleList);
		while(res10.next()){
			cont++;
		}	
		db.connection.commit();
		return cont;
	}

	ArrayList<String> getInformUnit(String unitName) throws SQLException, THOMASException{
		ArrayList<String> result = new ArrayList<String>();
		int idunitType;
		int idunitList;
		Statement st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+unitName+"'");
		if (res.next())
			idunitList = res.getInt("idunitList");
		else
			throw new THOMASException("Error : unit "+unitName+" not found in database");
		Statement st2 = db.connection.createStatement();
		ResultSet res2 = st2.executeQuery("SELECT idunitType FROM unitList WHERE unitName ='"+ unitName+"'");
		if(res2.next())
			idunitType = res2.getInt("idunitType");
		else
			throw new THOMASException("Error : unit "+unitName+" not found in database");

		Statement st3 = db.connection.createStatement();
		ResultSet res3 = st3.executeQuery("SELECT unitTypeName FROM unitType WHERE idunitType ="+ idunitType);
		res3.next();

		result.add(res3.getString("unitTypeName"));
		Statement st4 = db.connection.createStatement();
		ResultSet res4 = st4.executeQuery("SELECT idParentUnit FROM unitHierarchy WHERE idChildUnit ="+ idunitList);
		if(res4.next()){
			Statement st5 = db.connection.createStatement();
			ResultSet res5 = st5.executeQuery("SELECT unitName FROM unitList WHERE idunitList ="+ res4.getInt("idParentUnit"));
			res5.next();
			result.add(res5.getString("unitName"));
		}
		else
			result.add("");
		db.connection.commit();
		return result;
	}

	ArrayList<ArrayList<String>> getInformUnitRoles(String unitName, String agentName) throws SQLException, THOMASException{
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		int idPublicVisibility;
		int idPrivateVisbility;
		boolean playsRole = false;
		int idunitList;

		Statement st = db.connection.createStatement();
		ResultSet res = st.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='public'");
		if(res.next())
			idPublicVisibility = res.getInt("idVisibility");
		else
			throw new THOMASException("Error: visibility public not found in database");

		Statement st3 = db.connection.createStatement();
		ResultSet res3 = st3.executeQuery("SELECT idVisibility FROM visibility WHERE visibility ='private'");
		if(res3.next())
			idPrivateVisbility = res3.getInt("idVisibility");
		else
			throw new THOMASException("Error: visibility private not found in database");

		Statement st2 = db.connection.createStatement();
		ResultSet res2 = st2.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unitName+"'");
		if(res2.next())
			idunitList = res2.getInt("idunitList");
		else
			throw new THOMASException("Error : unit "+unitName+" not found in database");


		Statement st6 = db.connection.createStatement();
		ResultSet res6 = st6.executeQuery("SELECT idroleList FROM agentPlayList WHERE agentName ='"+agentName+"'");
		while(res6.next()){
			int idroleList2 = res6.getInt("idroleList");
			Statement st7 = db.connection.createStatement();
			ResultSet res7 = st7.executeQuery("SELECT * FROM roleList WHERE idroleList ="+idroleList2+" AND idunitList="+idunitList);
			if(res7.next()){
				playsRole = true;
				break;
			}
		}

		Statement st8 = db.connection.createStatement();
		ResultSet res8;
		if(playsRole)
			res8 = st8.executeQuery("SELECT roleName, idaccesibility, idvisibility, idposition FROM roleList WHERE idunitList ="+idunitList+" AND (idVisibility ="+idPrivateVisbility+" OR idVisibility ="+idPublicVisibility+")");
		else
			res8 = st8.executeQuery("SELECT roleName, idaccesibility, idvisibility, idposition FROM roleList WHERE idunitList ="+idunitList+" AND idVisibility ="+idPublicVisibility);
		while(res8.next()){
			ArrayList<String>aux = new ArrayList<String>();
			int idposition = res8.getInt("idposition");
			int idaccesibility = res8.getInt("idaccesibility");
			int idvisibility = res8.getInt("idvisibility");
			Statement st9 = db.connection.createStatement();
			ResultSet res9 = st9.executeQuery("SELECT position FROM position WHERE idposition ="+idposition);
			res9.next();

			Statement st10 = db.connection.createStatement();
			ResultSet res10 = st10.executeQuery("SELECT accesibility FROM accesibility WHERE idaccesibility ="+idaccesibility);
			res10.next();

			Statement st11 = db.connection.createStatement();
			ResultSet res11 = st11.executeQuery("SELECT visibility FROM visibility WHERE idvisibility ="+idvisibility);
			res11.next();
			aux.add(res8.getString("roleName"));
			aux.add(res10.getString("accesibility"));
			aux.add(res11.getString("visibility"));
			aux.add(res9.getString("position"));
			result.add(aux);
		}
		db.connection.commit();
		return result;		
	}

	ArrayList<String> getInformRole(String roleName, String unitName) throws SQLException, THOMASException{
		ArrayList<String> result = new ArrayList<String>();
		int idunitList;

		Statement st2 = db.connection.createStatement();
		ResultSet res2 = st2.executeQuery("SELECT idunitList FROM unitList WHERE unitName ='"+ unitName+"'");
		if(res2.next())
			idunitList = res2.getInt("idunitList");
		else
			throw new THOMASException("Error : unit "+unitName+" not found in database");


		Statement st6 = db.connection.createStatement();
		ResultSet res6 = st6.executeQuery("SELECT idaccesibility, idposition, idvisibility FROM roleList WHERE roleName ='"+roleName+"' AND idunitList ="+idunitList);
		if(res6.next()){
			int idposition = res6.getInt("idposition");
			int idaccesibility = res6.getInt("idaccesibility");
			int idvisibility = res6.getInt("idvisibility");
			Statement st9 = db.connection.createStatement();
			ResultSet res9 = st9.executeQuery("SELECT position FROM position WHERE idposition ="+idposition);
			res9.next();

			Statement st10 = db.connection.createStatement();
			ResultSet res10 = st10.executeQuery("SELECT accesibility FROM accesibility WHERE idaccesibility ="+idaccesibility);
			res10.next();

			Statement st11 = db.connection.createStatement();
			ResultSet res11 = st11.executeQuery("SELECT visibility FROM visibility WHERE idvisibility ="+idvisibility);
			res11.next();
			result.add(res10.getString("accesibility"));
			result.add(res11.getString("visibility"));
			result.add(res9.getString("position"));
			db.connection.commit();
			return result;
		}
		throw new THOMASException("Error : role "+roleName+" not found in database");
	}
}
