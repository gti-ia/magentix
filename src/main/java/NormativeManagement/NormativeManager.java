package NormativeManagement;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import persistence.DataBaseInterface;

public class NormativeManager {
	persistence.DataBaseInterface thomasDB=new DataBaseInterface();
	public boolean ManageNorm(String [] normInfo, Integer normID) throws Exception{
		Exception e = null;
		if(normInfo[0].equalsIgnoreCase("maxCardinalityNorm"))
			if(RegisterMaxCardinalityNorm(normInfo,normID))
				return true;
			else
				throw e;
		if(normInfo[0].equalsIgnoreCase("IncompatiblityNorm"))
			if(RegisterIncompatiblityNorm(normInfo,normID))
				return true;
			else	
				throw e;
		/*if(normInfo[0].equalsIgnoreCase("SimpleRequestNorm"))
			if(SimpleRequestNorm(normInfo,normID))
				return true;
			else
				throw e;*/
		return false;
		
	}
	private boolean SimpleRequestNorm(String[] normInfo, Integer normID) {
		if(thomasDB.CheckSimpleRequestNorm(normInfo[1],normInfo[2],normInfo[3]))
			return thomasDB.AddNewSimpleRequestNorm(normInfo[1],normInfo[2],normInfo[3],normID);
		else return false;
		
	}
	private boolean RegisterIncompatiblityNorm(String[] normInfo, Integer normID) {
		if(thomasDB.CheckIncompatiblityNorm(normInfo[1],normInfo[2]))
			return thomasDB.AddNewIncompatiblityNorm(normInfo[1],normInfo[2],normID);
		else return false;
	}
	private boolean RegisterMaxCardinalityNorm(String[] normInfo, Integer normID) {
		if(thomasDB.CheckMaxCardinalityNorm(normInfo[1],normInfo[2]))
			return thomasDB.AddNewMaxCardinalityNorm(normInfo[1],normInfo[2],normInfo[3],normID);
		else return false;
		
	}
	public boolean checkMaxCardinalityNorms(String roleID, String agentID) {
		List <String[]> norms=thomasDB.GetMaxCardinalityNorms(roleID);
		//Cuando se contemplen normas de cardinlidad generales hbra que comprobar si el agente juega el rol secundario
		/*List <String> roles=thomasDB.getAgentPlayRole(agentID);*/
		for(int i=0;i<norms.size();i++){int card=thomasDB.GetRoleCardinality(norms.get(i)[0]);
			if(card>=Integer.parseInt(norms.get(i)[1]))
					return false;
		}
		return true;
	}
	public boolean checkIncompatibilityNorms(String roleID, String agentID) {
		List <Integer> incompatibleRoles=thomasDB.GetIncompatibleRolesID(roleID);
		List <Integer> roles=thomasDB.GetAgentRolesID(agentID);
		//Cuando se contemplen normas de cardinlidad generales hbra que comprobar si el agente jeuga el rol secundario
		/*List <String> roles=thomasDB.getAgentPlayRole(agentID);*/
		for(int i=0;i<incompatibleRoles.size();i++){
			if(roles.contains(incompatibleRoles.get(i)))
				return false;
		}
		return true;
	}
/*	
	public boolean checkSimpleRequestNorms(String agentID,String serviceID) {
		List <String[]> norms=thomasDB.GetSimpleRequestNorms("fordibidden",roleID,serviceID);
		//Cuando se contemplen normas de cardinlidad generales hbra que comprobar si el agente juega el rol secundario
		for(int i=0;i<norms.size();i++){int card=thomasDB.GetRoleCardinality(norms.get(i)[0]);
			if(card>=Integer.parseInt(norms.get(i)[1]))
					return false;
		}
		return true;
	}
*/
}
