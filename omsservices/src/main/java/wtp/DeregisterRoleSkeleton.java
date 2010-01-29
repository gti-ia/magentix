
    /**
     * DeregisterRoleSkeleton.java
     *
     * This file was auto-generated from WSDL
     * by the Apache Axis2 version: 1.2 Apr 27, 2007 (04:35:37 IST)
     */
    package wtp;

import java.util.List;

import persistence.DataBaseInterface;
    /**
     *  DeregisterRoleSkeleton java skeleton for the axisService
     */
    public class DeregisterRoleSkeleton{
    	public static final Boolean DEBUG=true;
    	persistence.DataBaseInterface thomasBD=new DataBaseInterface();
        
        /**
         * Auto generated method signature
         
         
                                     * @param deregisterRole
         */
        

                 public wtp.DeregisterRoleResponse DeregisterRole
                  (
                  wtp.DeregisterRole deregisterRole
                  )
            {
                	 wtp.DeregisterRoleResponse res=new DeregisterRoleResponse();
                	 if (DEBUG) {
                			System.out.println("DeregisterRole :");
                			System.out.println("***AgentID..."+ deregisterRole.getAgentID());
                			System.out.println("*** RoleID()..."+ deregisterRole.getRoleID());
                			System.out.println("*** UnitID()..."+ deregisterRole.getUnitID());
                	 }
                	 
                	 
                	 res.setErrorValue("");
                     res.setStatus("Ok");

                     if(deregisterRole.getRoleID()=="" || deregisterRole.getRoleID().equalsIgnoreCase("member") || deregisterRole.getUnitID()==""){
                     	res.setErrorValue("Invalid");
                         res.setStatus("Error");
                         return res;
                     }
                     
                     if(!thomasBD.CheckExistsRoleInUnit(deregisterRole.getRoleID(),deregisterRole.getUnitID())){
                     	res.setErrorValue("NotFound");
                         res.setStatus("Error"); 
                         return res;                	
                     }
              
                     if(thomasBD.CheckRoleHasNorms(deregisterRole.getRoleID())){
                    	 res.setErrorValue("Invalid");
                         res.setStatus("ErrorNorm"); 
                         return res;
                     }
                     if(thomasBD.CheckRoleIsPlayed(deregisterRole.getRoleID())){
                    	 res.setErrorValue("Invalid");
                         res.setStatus("ErrorPlayed"); 
                         return res;
                     }
                     //role based control
                     if(!roleBasedControl(deregisterRole.getAgentID(),deregisterRole.getUnitID()))	
                     {	res.setErrorValue("Not-Allowed");
                  		res.setStatus("Error"); 
                  		return res;
                  	}
                     if(!thomasBD.DeleteRole(deregisterRole.getRoleID(),deregisterRole.getUnitID())){
                     	res.setErrorValue("Invalid");
                         res.setStatus("Error"); 
                         return res; 
                     }
                     return res;
             }
         		private boolean roleBasedControl(String agentID, String unitID) {
        			if(unitID.equalsIgnoreCase("virtual")) return false;
        			if(!thomasBD.CheckExistsAgent(agentID)) return false;
        			String unitType=thomasBD.GetUnitType(unitID);
        			if(unitType.equalsIgnoreCase("flat")) return true;
        			if(unitType.equalsIgnoreCase("team")) {
        				if(thomasBD.CheckAgentPlaysRoleInUnit(unitID, agentID)) return true;
        				else return false;
        			}
        			List<String> positions;
        			try {
        				positions = thomasBD.GetAgentPosition(agentID, unitID);
        				for(int i=0;i<positions.size();i++)
        					if(positions.get(i).equalsIgnoreCase("supervisor"))
        						return true;
        			} catch (Exception e) {
        			}
        			return false;
        		}
    }
    